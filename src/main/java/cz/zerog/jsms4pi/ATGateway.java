package cz.zerog.jsms4pi;

/*
 * #%L
 * jSMS4Pi
 * %%
 * Copyright (C) 2015 jSMS4Pi
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.zerog.jsms4pi.at.AAT;
import cz.zerog.jsms4pi.at.AT;
import cz.zerog.jsms4pi.at.ATE0;
import cz.zerog.jsms4pi.at.ATZ;
import cz.zerog.jsms4pi.at.CGMM;
import cz.zerog.jsms4pi.at.CGMR;
import cz.zerog.jsms4pi.at.CGSN;
import cz.zerog.jsms4pi.at.CLIP;
import cz.zerog.jsms4pi.at.CMEE;
import cz.zerog.jsms4pi.at.CMGD;
import cz.zerog.jsms4pi.at.CMGF;
import cz.zerog.jsms4pi.at.CMGR;
import cz.zerog.jsms4pi.at.CMGS;
import cz.zerog.jsms4pi.at.CMGSText;
import cz.zerog.jsms4pi.at.CNMI;
import cz.zerog.jsms4pi.at.CPINquestion;
import cz.zerog.jsms4pi.at.CPMS;
import cz.zerog.jsms4pi.at.CPMSsupport;
import cz.zerog.jsms4pi.at.CREG.Registration;
import cz.zerog.jsms4pi.at.CREGquestion;
import cz.zerog.jsms4pi.at.CREGquestion.NetworkStatus;
import cz.zerog.jsms4pi.at.CSCA;
import cz.zerog.jsms4pi.at.CSCAquestion;
import cz.zerog.jsms4pi.at.CSMP;
import cz.zerog.jsms4pi.at.GMI;
import cz.zerog.jsms4pi.exception.AtParseException;
import cz.zerog.jsms4pi.exception.GatewayException;
import cz.zerog.jsms4pi.listener.event.CallEvent;
import cz.zerog.jsms4pi.listener.event.GatewayStatusEvent;
import cz.zerog.jsms4pi.listener.event.InboundMessageEvent;
import cz.zerog.jsms4pi.listener.event.NetworkCellEvent;
import cz.zerog.jsms4pi.listener.event.NetworkStatusEvent;
import cz.zerog.jsms4pi.listener.event.OutboundMessageEvent;
import cz.zerog.jsms4pi.listener.gateway.GatewayStatusGatewayListener;
import cz.zerog.jsms4pi.listener.gateway.InboundCallGatewayListener;
import cz.zerog.jsms4pi.listener.gateway.InboundMessageGatewayListener;
import cz.zerog.jsms4pi.listener.gateway.NetworkCellGatewayListener;
import cz.zerog.jsms4pi.listener.gateway.NetworkStatusGatewayListener;
import cz.zerog.jsms4pi.listener.gateway.OutboundMessageGatewayListener;
import cz.zerog.jsms4pi.message.InboundMessage;
import cz.zerog.jsms4pi.message.OutboundMessage;
import cz.zerog.jsms4pi.notification.CDS;
import cz.zerog.jsms4pi.notification.CDSI;
import cz.zerog.jsms4pi.notification.CLIPN;
import cz.zerog.jsms4pi.notification.CMT;
import cz.zerog.jsms4pi.notification.CMTI;
import cz.zerog.jsms4pi.notification.CREG;
import cz.zerog.jsms4pi.notification.Notification;
import cz.zerog.jsms4pi.notification.OutboundMessageNotification;
import cz.zerog.jsms4pi.notification.RING;
import cz.zerog.jsms4pi.tool.SPStatus;
import cz.zerog.jsms4pi.tool.TypeOfMemory;

/**
 *
 * @author zerog
 */
public class ATGateway implements Gateway {

	private final Logger log = LogManager.getLogger();

	private final Configurator config = new Configurator();

	/*
	 * RS-232 default setting
	 */
	private final static int BOUDRATE = 57600;
	// private final static int AT_RESPONSE_TO = 5 * 1000;
	private final static int AT_RESPONSE_TO = 30 * 1000;
	private final static int DATA_BIT = 8;
	private final static int STOP_BIT = 1;
	private final static int PARITY = 0; // NONE

	/**
	 * Modem
	 */
	private final Modem modem;

	/**
	 * Address of SMS service
	 */
	private String smsServiceAddress = null;

	/**
	 * Memory setting in modem
	 */
	private TypeOfMemory mem1RW = TypeOfMemory.SM;
	private TypeOfMemory mem2Storege = TypeOfMemory.SM;
	private TypeOfMemory mem3Rec = TypeOfMemory.SM;

	/**
	 * Status of this gateway
	 */
	private Status status = Status.CLOSED;

	/*
	 * Saved value of last network status (CREG). If set CREG with <n>=2, is
	 * enable network registration and location information unsolicited
	 * notification. For separation only network status is saved its last state.
	 */
	private NetworkStatus cregLastNetworkStatus = null;

	/**
	 * List of outgoing message
	 */
	private final ArrayList<OutboundMessage> outgoingList = new ArrayList<>();

	/*
	 * Listeners
	 */
	private OutboundMessageGatewayListener smsStatusListener;
	private InboundCallGatewayListener callListener;
	private InboundMessageGatewayListener inboundMessageLinstener;
	private NetworkStatusGatewayListener networkStatusListener;
	private NetworkCellGatewayListener networkCellListener;
	private GatewayStatusGatewayListener gatewayStatusListener;

	public ATGateway(Modem modem) {
		this.modem = modem;
	}

	public static Gateway getDefaultFactory(String serialPortName) {
		return getDefaultFactory(serialPortName, BOUDRATE);
	}

	public static Gateway getDefaultFactory(String portname, int boudrate) {
		return getDefaultFactory(portname, boudrate, DATA_BIT, STOP_BIT, PARITY, AT_RESPONSE_TO);
	}

	public static Gateway getDefaultFactory(String portname, int serialSpeed, int databit, int stopbit, int parity,
			int atTimeOut) {
		Modem modem = new SerialModem(portname, serialSpeed, databit, stopbit, parity, atTimeOut);
		Gateway gateway = new ATGateway(modem);
		modem.setGatewayListener(gateway);
		return gateway;
	}

	/**
	 * Open gateway
	 *
	 * @throws GatewayException
	 */
	@Override
	public void open() throws GatewayException {
		modem.open();
		changeStatus(Status.OPENED);
		log.info("Gateway is ready, port '{}'", modem.getPortName());
	}

	/**
	 * Close gateway
	 *
	 * @throws GatewayException
	 */
	@Override
	public void close() throws GatewayException {
		modem.close();
		changeStatus(Status.CLOSED);
		log.info("Gateway is closed, port '{}' ", modem.getPortName());
	}

	/**
	 * Initialize gateway
	 *
	 * @return true if the initialization alright
	 * @throws GatewayException
	 */
	@Override
	public boolean init() throws GatewayException {

		if (status.getStatusCode() <= Status.CLOSED.getStatusCode()) {
			throw new GatewayException(GatewayException.GATEWAY_CLOSED, modem.getPortName());
		}

		// only test
		modem.send(new AT());

		// print all setting
		// config.printAll();
		// restart modem
		modem.send(new ATZ());
		// echo disable
		modem.send(new ATE0());

		// select optimal modem configuration
		config.selectModem(this.getManufactures());

		if (!isSimReady()) {
			return false;
		}

		// enable CMS error message
		modem.send(new CMEE(CMEE.Status.DISABLE));

		// set sms service address
		if (smsServiceAddress == null) {
			if (!isServiceAddressSet()) {
				log.warn("The SMS Service Center Address is not set");
			} else {
				// get and set Service Address (fix bug in modems)
				CSCAquestion cscaq = modem.send(new CSCAquestion());
				smsServiceAddress = cscaq.getAddress();
				modem.send(new CSCA(cscaq));
			}
		} else {
			// set new SMS Service Address
			modem.send(new CSCA(smsServiceAddress));
		}

		// set text mode
		if (!modem.send(new CMGF(CMGF.Mode.TEXT)).isStatusOK()) {
			log.error("Cannot set 'Text Mode' (AT Command CMGF). Initialization failed.");
			return false;
		}

		// use delivery report and time out expiration
		modem.send(new CSMP(CSMP.DELIVERY_REPORT | CSMP.VALIDITY_PERIOD));

		// test supported stores
		CPMSsupport cpmss = new CPMSsupport();
		if (modem.send(cpmss).isStatusOK()) {
			mem1RW = getPreferedMemoryBySuppored(cpmss.getMemory1(), config.getMemory1RW());
			mem2Storege = getPreferedMemoryBySuppored(cpmss.getMemory2(), config.getMemory2Storage());
			mem3Rec = getPreferedMemoryBySuppored(cpmss.getMemory3(), config.getMemory3Rec());

			// set stores (for read/write, send, rec)
			if (!modem.send(new CPMS(mem1RW, mem2Storege, mem3Rec)).isStatusOK()) {
				log.error("Cannot set storages (AT Command CPMS). Initialization failed.");
				return false;
			}
		} else {
			log.error("CPMSsupport AT Command failed. Initialization failed.");
			return false;
		}

		// set notification to PC
		if (!modem.send(new CNMI(config.getCNMIMode(), config.getCNMIMt(), config.getCNMIBm(), config.getCNMIDs()))
				.isStatusOK()) {
			log.error("Cannot set notification policy (AT Command CNMI). Initialization failed.");
			return false;
		}

		// show caller ID when RING notify
		if (!modem.send(new CLIP(true)).isStatusOK()) {
			log.warn("Cannot set RING notification. Inbound Call Event  is out of service!");
		}

		if (!modem.send(new cz.zerog.jsms4pi.at.CREG(Registration.ENABLE_EXTEND)).isStatusOK()) {
			log.warn("Cannot set Network Status notification. Network Status Event is out of service!");
		}

		changeStatus(Status.INITIALIZED);
		log.info("Gateway initialized successfully");

		// test for network
		isRegisteredIntoNetwork();

		if (status.equals(Status.NETWORK_OK)) {
			changeStatus(Status.READY);
		}

		return true;
	}

	@Override
	public void notify(Notification notification) {
		try {
			/*
			 * Delivery status saved into modem memory
			 */
			if (notification instanceof CDSI) {
				CDSI cdsi = (CDSI) notification;

				modem.send(new AT());
				// change to memory from CDSI notification
				modem.send(new CPMS(cdsi.getMemoryType()));
				// read status repord
				CMGR cmgr = modem.send(new CMGR(CMGR.Mode.SMS_STATUS_REPORT, cdsi.getSMSIndex()));
				if (!cmgr.isStatusOK()) {
					new GatewayException("Cannot read sms status by index(" + cdsi.getSMSIndex() + ")",
							cmgr.getCmsError(), modem.getPortName());
				}
				// delete status repord
				modem.send(new CMGD(cdsi.getSMSIndex()));
				// change back to main memory
				modem.send(new CPMS(mem1RW));

				if (!findOutboudMessage(cmgr.getMr(), cmgr.getSp())) {
					throw new GatewayException("Cannot find  message with index " + cdsi.getSMSIndex(),
							modem.getPortName());
				}
			}

			/*
			 * Delivery status Routed directly into TE
			 */
			if (notification instanceof CDS) {
				CDS cds = (CDS) notification;
				if (!findOutboudMessage(cds.getMr(), cds.getStatus())) {
					throw new GatewayException("Cannot find  message with index " + cds.getMr(), modem.getPortName());
				}
			}

			/*
			 * Incoming call
			 */
			if (notification instanceof RING) {
				RING ring = (RING) notification;
				createInboundCallEvent(ring.getCallerId(), ring.getValidity());
				return;
			}
			/*
			 * Incoming call
			 */
			if (notification instanceof CLIPN) {
				CLIPN ring = (CLIPN) notification;
				createInboundCallEvent(ring.getCallerId(), RING.Validity.VALID);
				return;
			}
			/*
			 * Incoming SMS
			 */
			if (notification instanceof CMTI) {
				CMTI cmti = (CMTI) notification;
				// change to memory from CMTI notification
				modem.send(new CPMS(cmti.getMemoryType()));
				// read sms
				CMGR cmgr = modem.send(new CMGR(CMGR.Mode.SMS_DELIVERY, cmti.getSMSIndex()));
				if (!cmgr.isStatusOK()) {
					new GatewayException("Cannot read sms by index: " + cmti.getSMSIndex(), cmgr.getCmsError(),
							modem.getPortName());

				}
				// delete sms
				modem.send(new CMGD(cmti.getSMSIndex()));
				// change back to main memory
				modem.send(new CPMS(mem1RW));
				createInboundMessageEvent(new InboundMessage(cmgr.getText(), cmgr.getOa()));
				return;
			}

			/*
			 * Incoming SMS Routed directly into TE
			 */
			if (notification instanceof CMT) {
				CMT cmt = (CMT) notification;
				createInboundMessageEvent(new InboundMessage(cmt.getData(), cmt.getOa()));
				return;
			}

			/*
			 * Network and Cell Status
			 */
			if (notification instanceof CREG) {
				CREG creg = (CREG) notification;
				if (!creg.useSMS()) {
					if (status.getStatusCode() > Status.INITIALIZED.getStatusCode()) {
						changeStatus(Status.INITIALIZED);
					}
				} else {
					// FIXME !!
				}

				// test if is change in CELL or NETWORK STATUS
				if (!creg.getNetworkStatus().equals(this.cregLastNetworkStatus)) {
					cregLastNetworkStatus = creg.getNetworkStatus();
					createNetworkStatusEvent(creg.getNetworkStatus());
					log.info("Network status notification");
				}

				createCellStatusEvent(creg.getLac(), creg.getCi());
				return;
			}

			/*
			 * Outbound message changed status
			 */
			if (notification instanceof OutboundMessageNotification) {
				createOutboundEvent(((OutboundMessageNotification) notification).getOutboundMessage());
			}

		} catch (GatewayException | AtParseException ex) {
			log.warn("The exception while notification process", ex);
		}
	}

	/*
	 * Set Listeners
	 */
	@Override
	public void setOutboundMessageListener(OutboundMessageGatewayListener listener) {
		this.smsStatusListener = listener;
	}

	@Override
	public void setInboundCallListener(InboundCallGatewayListener callListener) {
		this.callListener = callListener;
	}

	@Override
	public void setInboundMessageListener(InboundMessageGatewayListener listener) {
		this.inboundMessageLinstener = listener;
	}

	@Override
	public void setNetworkStatusListener(NetworkStatusGatewayListener networkStatusListener) {
		this.networkStatusListener = networkStatusListener;
	}

	@Override
	public void setGatewayStatusListener(GatewayStatusGatewayListener gatewayListener) {
		this.gatewayStatusListener = gatewayListener;
	}

	@Override
	public void setNetworkCellListener(NetworkCellGatewayListener networkCellListener) {
		this.networkCellListener = networkCellListener;
	}

	@Override
	public String getPortName() {
		return modem.getPortName();
	}

	public int getSerialSpeed() {
		return modem.getSpeed();
	}

	public int getAtTimeOut() {
		return modem.getAtTimeout();
	}

	@Override
	public boolean isServiceAddressSet() throws GatewayException {
		CSCAquestion cscaq = modem.send(new CSCAquestion());
		if (!cscaq.isStatusOK()) {
			throw new GatewayException(GatewayException.SERVISE_READ_ERR, modem.getPortName());
		}
		return cscaq.getAddress().length() > 0;

	}

	@Override
	public <T extends AAT> T directSendAtCommand(T cmd) throws GatewayException {
		return modem.send(cmd);
	}

	@Override
	public boolean isAlive() {
		try {
			if (modem.send(new AT()).isStatusOK()) {
				return true;
			}
		} catch (GatewayException ex) {

		}
		return false;
	}

	/**
	 * Send outbound SMS message.
	 *
	 * @param message
	 * @throws GatewayException
	 */
	@Override
	public void sendMessage(OutboundMessage message) throws GatewayException {
		if (!status.equals(Status.READY)) {
			throw new GatewayException(GatewayException.GATEWAY_NOT_READY, modem.getPortName());
		}

		changeStatus(Status.BUSY);

		if (smsServiceAddress == null) {
			log.warn("The SMS Service Center Address is not set. Set to 1234");
			smsServiceAddress = "1234";
		}

		if (!modem.send(new CSCA(smsServiceAddress)).isStatusOK()) {
			throw new GatewayException("Modem cannot accept sms service address", modem.getPortName());
		}

		if (message.isDeliveryReport()) {
			// TODO impl. me
		}

		modem.send(new CMGS(message.getDestination()));
		CMGSText cmgstext = modem.send(new CMGSText(message.getText()));
		if (!cmgstext.isStatusOK()) {
			throw new GatewayException(GatewayException.CANNOT_SEND, cmgstext.getCmsError(), modem.getPortName());
		}

		message.setIndex(cmgstext.getIndex());
		message.setStatus(OutboundMessage.Status.SENDED_NOT_ACK);
		modem.putNotification(new OutboundMessageNotification(message));
		outgoingList.add(message);

		changeStatus(Status.READY);
	}

	/**
	 * Set SMS service address.
	 *
	 * @param address
	 * @throws GatewayException
	 */
	@Override
	public void setSmsServiceAddress(String address) throws GatewayException {
		Pattern pattern = Pattern.compile("^\\+?[1-9]\\d{1,14}$");
		Matcher matcher = pattern.matcher(address);

		if (matcher.matches()) {
			smsServiceAddress = address;
			return;
		}

		throw new GatewayException("The Message Service Address has invalid format", modem.getPortName());
	}

	private boolean findOutboudMessage(int messIndex, SPStatus messStatus) {
		for (OutboundMessage outMess : outgoingList) {
			if (outMess.getIndex() == messIndex) {

				switch (messStatus) {
				case RECEIVED: // 0
					outMess.setStatus(OutboundMessage.Status.SENDED_ACK);
					outgoingList.remove(outMess);
					createOutboundEvent(outMess);
					break;
				case SEVICE_REJECTED: // 99
					outMess.setStatus(OutboundMessage.Status.EXPIRED);
					outgoingList.remove(outMess);
					createOutboundEvent(outMess);
					break;
				default:
					log.warn("Unknown Outboud Message status: '{}'", messStatus);
				}
				return true;
			}
		}
		return false;
	}

	/*
	 * Private methods
	 */

	private void createOutboundEvent(OutboundMessage mess) {
		if (smsStatusListener != null) {
			smsStatusListener.outboundMessageEvent(new OutboundMessageEvent(mess, mess.getStatus()));
		}
	}

	private void createInboundMessageEvent(InboundMessage mess) {
		if (inboundMessageLinstener != null) {
			inboundMessageLinstener.inboundMessageEvent(new InboundMessageEvent(mess));
		}
	}

	private void createInboundCallEvent(String callerId, RING.Validity validity) {
		if (callListener != null) {
			callListener.inboundCallEvent(new CallEvent(callerId, validity));
		} else {
			log.info("call listener is null");
		}
	}

	private void createNetworkStatusEvent(CREGquestion.NetworkStatus status) {
		if (networkStatusListener != null) {
			networkStatusListener.networkStatusEvent(new NetworkStatusEvent(status));
		}
	}

	private void createGatewayStatusEvent(Gateway.Status status) {
		if (gatewayStatusListener != null) {
			gatewayStatusListener.gatewayStatusEvent(new GatewayStatusEvent(status));
		}
	}

	private void createCellStatusEvent(int lac, int ci) {
		if (networkCellListener != null) {
			networkCellListener.networkStatusEvent(new NetworkCellEvent(lac, ci));
		}
	}

	private void changeStatus(Gateway.Status status) {
		this.status = status;
		createGatewayStatusEvent(status);
	}

	private TypeOfMemory getPreferedMemoryBySuppored(TypeOfMemory[] supportedMemoryList, TypeOfMemory defaulMemory) {
		List<TypeOfMemory> supportedMemory = Arrays.asList(supportedMemoryList);
		if (supportedMemory.contains(defaulMemory)) {
			return defaulMemory;
		} else {
			return TypeOfMemory.SM;
		}
	}

	/**
	 * Return true if modem is registered into GSM network
	 *
	 * @return
	 * @throws ModemException
	 */
	private boolean isRegisteredIntoNetwork() throws GatewayException {
		// test if modem is registered into GSM network
		CREGquestion cregq = modem.send(new CREGquestion());
		if (!cregq.useSMS()) {
			return false;
		}
		changeStatus(Status.NETWORK_OK);
		return true;
	}

	// /**
	// * Return true if modem have any signal
	// *
	// * @return
	// * @throws ModemException
	// */
	//
	// private boolean sufficientSignal() throws ModemException {
	// //network signal stranche
	// CSQ csq = new CSQ();
	// if (!modem.send(csq).isStatusOK()) {
	// return false;
	// }
	//
	// if (csq.getRawValue() <= 1) {
	// return false;
	// }
	// return true;
	// }
	private ModemInformation getManufactures() throws GatewayException {
		ModemInformation info = new ModemInformation();

		info.setManufacturer(modem.send(new GMI()).getManufaturer());
		info.setModelAndCapabilities(modem.send(new CGMM()).getModel());
		info.setImei(modem.send(new CGSN()).getIMEI());

		return info;
	}

	public void printModemInfo() throws GatewayException {
		System.out.print("Manufacturer name: ");
		System.out.println(modem.send(new GMI()).getManufaturer());

		System.out.print("Manufacturer OS version: ");
		System.out.println(modem.send(new CGMR()).getVersion());

		System.out.print("IMEI: ");
		System.out.println(modem.send(new CGSN()).getIMEI());

		System.out.print("Model description: ");
		System.out.println(modem.send(new CGMM()).getModel());

		// select optimal modem configuration
		config.selectModem(this.getManufactures());
	}

	private boolean isSimReady() throws GatewayException {
		CPINquestion cpinq = new CPINquestion();
		if (modem.send(cpinq).isStatusOK()) {
			if (cpinq.getPinStatus() == CPINquestion.PinStatus.READY) {
				this.status = Status.SIM_OK;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isReadyToSend() {
		return status.equals(Status.READY);
	}

}
