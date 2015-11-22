package cz.zerog.jsms4pi.tool;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.zerog.jsms4pi.Configurator;
import cz.zerog.jsms4pi.Gateway;
import cz.zerog.jsms4pi.Modem;
import cz.zerog.jsms4pi.ModemInformation;
import cz.zerog.jsms4pi.NullGateway;
import cz.zerog.jsms4pi.SerialModem;
import cz.zerog.jsms4pi.at.AT;
import cz.zerog.jsms4pi.at.ATE0;
import cz.zerog.jsms4pi.at.ATZ;
import cz.zerog.jsms4pi.at.CGMM;
import cz.zerog.jsms4pi.at.CGSN;
import cz.zerog.jsms4pi.at.CLIP;
import cz.zerog.jsms4pi.at.CMGD;
import cz.zerog.jsms4pi.at.CMGF;
import cz.zerog.jsms4pi.at.CMGR;
import cz.zerog.jsms4pi.at.CMGS;
import cz.zerog.jsms4pi.at.CMGSText;
import cz.zerog.jsms4pi.at.CNMI;
import cz.zerog.jsms4pi.at.CPMS;
import cz.zerog.jsms4pi.at.CPMSsupport;
import cz.zerog.jsms4pi.at.CSCA;
import cz.zerog.jsms4pi.at.CSMP;
import cz.zerog.jsms4pi.at.GMI;
import cz.zerog.jsms4pi.example.Tool;
import cz.zerog.jsms4pi.exception.AtParseException;
import cz.zerog.jsms4pi.exception.GatewayException;
import cz.zerog.jsms4pi.notification.CDS;
import cz.zerog.jsms4pi.notification.CDSI;
import cz.zerog.jsms4pi.notification.CLIPN;
import cz.zerog.jsms4pi.notification.CMT;
import cz.zerog.jsms4pi.notification.CMTI;
import cz.zerog.jsms4pi.notification.Notification;
import cz.zerog.jsms4pi.notification.RING;

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

/**
 *
 * @author zerog
 */
public class FunctionalTest extends NullGateway {

	private final Logger log = LogManager.getLogger();

	private Modem modem;

	private ModemInformation modemInfo;

	private String messageText = "jSMS4Pi, a simple Java library for sending SMS";

	/*
	 * Flags of test. Each test have own flag
	 */
	private volatile boolean inboundCall = false;
	private volatile boolean sendMessMethod1 = false;
	private volatile boolean sendMessMethod2 = false;
	private volatile boolean receivedMessMethod1 = false;
	private volatile boolean receivedMessMethod2 = false;
	private volatile boolean receivedStatusMethod1 = false;
	private volatile boolean receivedStatusMethod2 = false;

	/*
	 * Texts
	 */
	private final String manufacturerText = "Geting an information about manufacturer of the modem and imei";
	private final String inboundCallText = "Testing of inbound call detection";
	private final String sendM1Text = "Sending test text message (a first method)";
	private final String sendM2Text = "Sending test text message (a second method)";
	private final String inboundMessageM1Text = "Detecting text message (a first method)";
	private final String inboundMessageM2Text = "Detecting text message (a second method)";
	private final String deliveryStatusM1Text = "Testing delivery status detection (a first method)";
	private final String deliveryStatusM2Text = "Testing delivery status detection(a second method)";

	public FunctionalTest(String port, String destination, String service) {
		System.out.println("Modem testing start\n\n");

		/*
		 * Modem Info
		 */
		try {
			modemInfo = printModemInfo(port);
			if (modemInfo != null) {
				printResult(manufacturerText, true);
			} else {
				printResult(manufacturerText, false);
			}

			Thread.sleep(500);
		} catch (InterruptedException e) {
			log.error(e, e);
			printResult(manufacturerText, false);
		} catch (GatewayException e) {
			printResult(manufacturerText, "Port: " + e.getPortName() + ", Error: " + e.getErrorMessage());
		}

		/*
		 * Inbound call
		 */
		try {
			printResult(inboundCallText, inboundCall(port));
			Thread.sleep(500);
		} catch (IOException | InterruptedException e) {
			log.error(e, e);
		} catch (GatewayException e) {
			log.error(e, e);
			printResult(inboundCallText, "Port: " + e.getPortName() + ", Error: " + e.getErrorMessage());
		}

		/*
		 * Send, Received and Delivery message default way
		 */
		if (false) {
			try {
				if (sendMessageDefault(port, destination, service)) {

					printResult(sendM1Text, true);

					synchronized (this) {
						try {
							wait(30 * 1000);
						} catch (InterruptedException e) {
							System.out.println("Interupted");
						}
					}
				} else {
					printResult(sendM1Text, false);
					// Thread.sleep(5 * 1000);
				}

				Thread.sleep(500);
			} catch (IOException | InterruptedException e) {
				log.error(e, e);
			} catch (GatewayException e) {
				log.error(e, e);
				printResult(sendM1Text, "Port: " + e.getPortName() + ", Error: " + e.getErrorMessage());
			} finally {
				try {
					modem.close();

				} catch (GatewayException e) {
					log.error(e, e);
				}
			}

			/*
			 * Send, Received and Delivery message directly to TE
			 */
			try {
				if (sendMessageIntoTE(port, destination, service)) {
					printResult(sendM2Text, true);

					synchronized (this) {
						try {
							wait(30 * 1000);
						} catch (InterruptedException e) {
							System.out.println("Interupted");
						}
					}

				} else {
					printResult(sendM2Text, false);
				}
			} catch (IOException e) {
				log.error(e, e);
			} catch (GatewayException e) {
				log.error(e, e);
				printResult(sendM2Text, "Port: " + e.getPortName() + ", Error: " + e.getErrorMessage());
			} finally {
				try {
					modem.close();
				} catch (GatewayException e) {
					log.error(e, e);
				}
			}
		}

		if (!receivedMessMethod1) {
			printResult(inboundMessageM1Text, false);
		}

		if (!receivedMessMethod2) {
			printResult(inboundMessageM2Text, false);
		}

		if (!receivedStatusMethod1) {
			printResult(deliveryStatusM1Text, false);
		}

		if (!receivedStatusMethod2) {
			printResult(deliveryStatusM2Text, false);
		}

		/*
		 * If method2 work well (directly into TE) and method1 not work (saved
		 * message into storage) generate properties file with setting
		 */
		if (modemInfo != null && (receivedMessMethod2 && receivedStatusMethod2)
				&& (!receivedMessMethod1 || !receivedStatusMethod1)) {

			Map<String, String> settings = new HashMap<String, String>();

			// message directly to TE
			settings.put(Configurator.CNMI_MODE, "1");
			settings.put(Configurator.CNMI_MT, "2");
			settings.put(Configurator.CNMI_BM, "0");
			settings.put(Configurator.CNMI_DS, "1");

			Configurator.generateFile(modemInfo, settings);

		}

		System.out.println("\n\nTest finished");

	}

	private boolean inboundCall(String port) throws GatewayException, IOException {
		try {
			if (!modemInit(port, this)) {
				return false;
			}
			// show caller ID when RING notify
			if (!modem.send(new CLIP(true)).isStatusOK()) {
				return false;
			}

			System.out.println("Now call the modem.  After dial tone hang up.");

			synchronized (this) {
				try {
					wait(30 * 1000);
				} catch (InterruptedException e) {
					System.out.println("Interupted");
				}
			}

			if (inboundCall) {
				System.out.println("Is this information correct? [y/n]");
				char c = (char) System.in.read();
				if (c == 'y') {
					return true;
				}
			}

			return false;
		} finally {
			modem.close();
		}
	}

	private void supportedMemory(String port) throws GatewayException {

		try {
			System.out.println("Supported storages: ");
			modemInit(port, new NullGateway());
			CPMSsupport cpmss = new CPMSsupport();
			if (modem.send(cpmss).isStatusOK()) {
				TypeOfMemory[] mem1 = cpmss.getMemory1();
				TypeOfMemory[] mem2 = cpmss.getMemory2();
				TypeOfMemory[] mem3 = cpmss.getMemory3();

				System.out.print("Memory1: ");
				for (int i = 0; i < mem1.length; i++) {
					System.out.print(mem1[i] + ", ");
				}
				System.out.println();

				System.out.print("Memory2: ");
				for (int i = 0; i < mem2.length; i++) {
					System.out.print(mem2[i] + ", ");
				}
				System.out.println();

				System.out.print("Memory3: ");
				for (int i = 0; i < mem3.length; i++) {
					System.out.print(mem3[i] + ", ");
				}
				System.out.println();
			}

		} finally {
			modem.close();
		}

	}

	public ModemInformation printModemInfo(String port) throws GatewayException {

		try {

			// Manufacturer name
			if (!modemInit(port, new NullGateway())) {
				return null;
			}

			ModemInformation info = new ModemInformation();

			GMI gmi = new GMI();
			if (!modem.send(gmi).isStatusOK()) {
				return null;
			}
			info.setManufacturer(gmi.getManufaturer());

			// Model description
			CGMM cgmm = new CGMM();
			if (!modem.send(cgmm).isStatusOK()) {
				return null;
			}
			info.setModelAndCapabilities(cgmm.getModel());

			// IMEI
			CGSN cgsn = new CGSN();
			if (!modem.send(cgsn).isStatusOK()) {
				return null;
			}
			info.setImei(cgsn.getIMEI());

			return info;

		} finally {
			modem.close();
		}
	}

	/**
	 * Send message and delivery save into modem storage
	 * 
	 * @param port
	 * @param modemNumber
	 * @param service
	 * @throws GatewayException
	 * @throws IOException
	 */
	private boolean sendMessageDefault(String port, String modemNumber, String service)
			throws GatewayException, IOException {

		sendMessMethod1 = true;

		// save delivery into memory
		return send(port, modemNumber, service, new CNMI(CNMI.Mode._2, CNMI.Mt.NOTIFI_1, CNMI.Bm.NO_CBM_NOTIFI_0,
				CNMI.Ds.STATUS_REPORT_NOTIFI_IF_STORED_2));
	}

	/**
	 * Send message and notify into TE
	 * 
	 * 
	 * CNMI.mode=1 CNMI.mt=2 CNMI.bm=0 CNMI.ds=1
	 * 
	 * @param port
	 * @param modemNumber
	 * @param service
	 * @throws GatewayException
	 * @throws IOException
	 */
	private boolean sendMessageIntoTE(String port, String modemNumber, String service)
			throws GatewayException, IOException {

		sendMessMethod2 = true;

		// save delivery into memory
		return send(port, modemNumber, service, new CNMI(CNMI.Mode._1, CNMI.Mt.DIRECT_NOTIFI_BESIDES_CLASS2_2,
				CNMI.Bm.NO_CBM_NOTIFI_0, CNMI.Ds.STATUS_REPORT_NOTIFI_1));
	}

	private boolean send(String port, String modemNumber, String service, CNMI cnmi)
			throws GatewayException, IOException {

		if (!modemInit(port, this)) {
			return false;
		}

		// set text mode
		if (!modem.send(new CMGF(CMGF.Mode.TEXT)).isStatusOK()) {
			return false;
		}
		// use delivery report and time out expiration
		if (!modem.send(new CSMP(CSMP.DELIVERY_REPORT | CSMP.VALIDITY_PERIOD)).isStatusOK()) {
			return false;
		}
		// set stores
		if (!modem.send(new CPMS(TypeOfMemory.ME, TypeOfMemory.ME, TypeOfMemory.ME)).isStatusOK()) {
			return false;
		}
		// notifications
		if (!modem.send(cnmi).isStatusOK()) {
			return false;
		}
		// set sms service
		if (!modem.send(new CSCA(service)).isStatusOK()) {
			return false;
		}
		// destination number
		if (!modem.send(new CMGS(modemNumber)).isStatusOK()) {
			return false;
		}
		// text
		if (!modem.send(new CMGSText(messageText)).isStatusOK()) {
			return false;
		}
		return true;
	}

	private boolean modemInit(String port, Gateway gateway) throws GatewayException {

		modem = new SerialModem(port);
		modem.setGatewayListener(gateway);
		modem.open();

		if (!modem.send(new AT()).isStatusOK()) {
			return false;
		}
		if (!modem.send(new ATZ()).isStatusOK()) {
			return false;
		}
		if (!modem.send(new ATE0()).isStatusOK()) {
			return false;
		}
		return true;
	}

	@Override
	public void notify(Notification notification) {
		// System.out.println("notification: " +
		// notification.getClass().getName());

		try {
			/*
			 * Incoming call
			 */
			if (notification instanceof RING) {
				RING ring = (RING) notification;
				printCallInfo(ring.getCallerId());
				return;
			}
			/*
			 * Incoming call
			 */
			if (notification instanceof CLIPN) {
				CLIPN ring = (CLIPN) notification;
				printCallInfo(ring.getCallerId());
				return;
			}
			/*
			 * Delivery status saved into modem memory
			 */
			if (notification instanceof CDSI) {
				CDSI cdsi = (CDSI) notification;

				receivedStatusMethod1 = true;

				boolean fail = false;

				if (!modem.send(new AT()).isStatusOK()) {
					fail = true;
				}
				// change to memory from CDSI notification
				if (fail || !modem.send(new CPMS(cdsi.getMemoryType())).isStatusOK()) {
					fail = true;
				}
				// read status repord
				if (fail || !modem.send(new CMGR(CMGR.Mode.SMS_STATUS_REPORT, cdsi.getSMSIndex())).isStatusOK()) {
					fail = true;
				}
				// delete status repord
				if (fail || !modem.send(new CMGD(cdsi.getSMSIndex())).isStatusOK()) {
					fail = true;
				}
				// change back to main memory
				if (fail || !modem.send(new CPMS(TypeOfMemory.SM)).isStatusOK()) {
					fail = true;
				}

				synchronized (this) {
					notifyAll();
				}

				if (fail) {
					printResult(deliveryStatusM1Text, false);
					return;
				}
				printResult(deliveryStatusM1Text, true);

				return;
			}

			/*
			 * Delivery status Routed directly into TE
			 */
			if (notification instanceof CDS) {
				CDS cds = (CDS) notification;

				receivedStatusMethod2 = true;

				synchronized (this) {
					notifyAll();
				}

				printResult(deliveryStatusM2Text, true);

				return;
			}

			/*
			 * Incoming SMS
			 */
			if (notification instanceof CMTI) {
				CMTI cmti = (CMTI) notification;

				receivedMessMethod1 = true;

				boolean fail = false;

				// change to memory from CMTI notification
				if (!modem.send(new CPMS(cmti.getMemoryType())).isStatusOK()) {
					fail = true;
				}
				// read sms
				CMGR cmgr = new CMGR(CMGR.Mode.SMS_DELIVERY, cmti.getSMSIndex());
				if (fail || !modem.send(cmgr).isStatusOK()) {
					fail = true;
				}
				// delete sms
				if (fail || !modem.send(new CMGD(cmti.getSMSIndex())).isStatusOK()) {
					fail = true;
				}
				// change back to main memory
				if (fail || !modem.send(new CPMS(TypeOfMemory.SM)).isStatusOK()) {
					fail = true;
				}

				if (fail) {
					printResult(inboundMessageM1Text, false);
				}

				if (messageText.equals(cmgr.getText())) {
					printResult(inboundMessageM1Text, true);
					return;
				}

				System.out.println("A malformed or unexpected inbound message ");
				System.out.println("Text: '" + cmgr.getText() + "', Source tel. number: '" + cmgr.getOa() + "'");

				return;
			}

			/*
			 * Incoming SMS Routed directly into TE
			 */
			if (notification instanceof CMT) {
				CMT cmt = (CMT) notification;

				receivedMessMethod2 = true;

				if (messageText.equals(cmt.getData())) {
					printResult(inboundMessageM2Text, true);
					return;
				}

				System.out.println("A malformed or unexception inbound message");
				System.out.println("Text: '" + cmt.getData() + "', Source tel. number: '" + cmt.getOa() + "'");

				return;
			}

		} catch (GatewayException | AtParseException e) {
			log.error(e, e);
		}

	}

	private void printCallInfo(String tel) throws GatewayException {

		if (inboundCall) {
			return;
		}

		System.out.println("Detected an inbound call. Caller ID: " + tel);

		inboundCall = true;

		synchronized (this) {
			notifyAll();
		}

		throw new AtParseException("", Pattern.compile(""));
	}

	private void printResult(String text, String errorText) {
		System.out.println(text);
		System.out.println("  - FAIL, " + errorText);
	}

	private void printResult(String text, boolean result) {
		System.out.println(text);
		if (result) {
			System.out.println("  - OK");
		} else {
			System.out.println("  - FAIL");
		}
	}

	public static void main(String[] args) throws IOException, GatewayException {
		System.out.println(String.format("The program will test if the modem supports required functions%n"
				+ "   - manufacturing of information%n" + "   - inbound calls detections%n"
				+ "    - inbound / outbound text messages%n"
				+ "%nIf the library is missing configuration file for the modem, it will generate it.%n%n" +

		"Warning: This test will try  to send two text messages to itself to  test inbount / outbound messages."));

		Tool.showHelp(args,
				String.format("Parameters:%n" + "   - p <port name> Name of a serial port%n" + "   - h To show this%n"
						+ "   - d <number> Modem / Destination phone number%n"
						+ "   - s <number> Number of Message Service Center%n" +

		"%nInteractive mode is activated when program is started without parameters%n"));

		String port = Tool.selectionPort(args);
		String dest = Tool.destNumber(args);
		String service = Tool.serviceNumer(args);

		System.out.println(String.format("%n--- Summary ---"));
		System.out.println("Serial port: " + port);
		System.out.println("Destination (Modem) phone number: " + dest);
		System.out.println("Number of Message Service Center: " + service);

		Tool.pressEnter();

		Tool.activeLoggingToFile();
		new FunctionalTest(port, dest, service);
	}
}
