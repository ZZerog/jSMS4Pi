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
import cz.zerog.jsms4pi.message.OutboundMessage;
import cz.zerog.jsms4pi.event.CallEvent;
import cz.zerog.jsms4pi.event.InboundCallEventListener;
import cz.zerog.jsms4pi.event.OutboundMessageEvent;
import cz.zerog.jsms4pi.event.OutboundMessageEventListener;
import cz.zerog.jsms4pi.at.*;
import cz.zerog.jsms4pi.notification.*;
import cz.zerog.jsms4pi.tool.TypeOfMemory;
import cz.zerog.jsms4pi.event.InboundMessageEvent;
import cz.zerog.jsms4pi.event.InboundMessageEventListener;
import cz.zerog.jsms4pi.exception.GatewayException;
import cz.zerog.jsms4pi.exception.GatewayRuntimeException;
import cz.zerog.jsms4pi.exception.ModemException;
import cz.zerog.jsms4pi.message.InboundMessage;
import cz.zerog.jsms4pi.tool.SPStatus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author zerog
 */
public class ATGateway implements Gateway {

    private final Logger log = LogManager.getLogger();

    private final Configurator config = new Configurator();

    /**
     * Modem
     */
    private final SerialModem modem = new SerialModem(this);

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

    /**
     * List of outgoing message
     */
    private final ArrayList<OutboundMessage> outgoingList = new ArrayList<>();

    /*
     Listeners
     */
    private OutboundMessageEventListener smsStatusListener;
    private InboundCallEventListener callListener;
    private InboundMessageEventListener inboundMessageLinstener;

    /*
     Serial port associate with gateway
     */
    private final String port;

    //Global gateway setting
    private boolean globalDeliveryReport;
    private boolean globalValidityPeriod;

    public ATGateway(String portname) {
        this.port = portname;
    }

    /*
     Set Listeners
     */
    public void setOutboundMessageEventListener(OutboundMessageEventListener listener) {
        this.smsStatusListener = listener;
    }

    public void setInboundCallListener(InboundCallEventListener callListener) {
        this.callListener = callListener;
    }

    public void setInboundMessageListener(InboundMessageEventListener listener) {
        this.inboundMessageLinstener = listener;
    }

    @Override
    public void setGlobalDeliveryReport(boolean b) {
        //TODO implemt me
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setGlobalValidityPeriod(boolean b) {
        //TODO implemt me
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getPortName() {
        return port;
    }

    /**
     * Open gateway
     *
     * @throws GatewayException
     */
    @Override
    public void open() throws GatewayException {
        try {
            modem.open(port);
            status = Status.OPENED;
            log.info("Gateway is ready, port '{}'", port);
        } catch (ModemException ex) {
            throw new GatewayException(ex, port);
        }
    }

    /**
     * Close gateway
     *
     * @throws GatewayException
     */
    @Override
    public void close() throws GatewayException {
        try {
            modem.close();
            status = Status.CLOSED;
            log.info("Gateway is closed, port '{}' ", port);
        } catch (ModemException e) {
            throw new GatewayException(e, port);
        }
    }

    public boolean isServiceAddressSet() throws GatewayException {
        try {
            CSCAquestion cscaq = modem.send(new CSCAquestion());
            if (!cscaq.isStatusOK()) {
                throw new GatewayException(GatewayException.SERVISE_READ_ERR, port);
            }
            return cscaq.getAddress().length() > 0;

        } catch (ModemException ex) {
            throw new GatewayException(ex, port);
        }
    }

    public <T extends AAT> T directSendAtCommand(T cmd) throws ModemException {
        return modem.send(cmd);
    }

    /**
     * Initialize gateway
     *
     * @return true if the initialization alright
     * @throws GatewayException
     */
    @Override
    public boolean init() throws GatewayException {

        try {
            if (status == Status.CLOSED) {
                throw new GatewayRuntimeException("Gateway is closed", port);
            }

            //only test
            modem.send(new AT());

            //select optimal modem configuration
            config.selectModem(this.getManufactures());

            //print all setting
            //config.printAll();

            //restart modem
            modem.send(new ATZ());
            //echo disable
            modem.send(new ATE0());

            //set sms service address
            if (smsServiceAddress == null) {
                if (!isServiceAddressSet()) {
                    log.info("Cannot send message before set Service Address!.  Use method setSmsServiceAddress(service_number);");
                } else {
                    //reset address
                    CSCAquestion cscaq = modem.send(new CSCAquestion());
                    smsServiceAddress = cscaq.getAddress();
                    modem.send(new CSCA(cscaq));
                }
            } else {
                modem.send(new CSCA(smsServiceAddress));
            }

            //set text mode
            if (!modem.send(new CMGF(CMGF.Mode.TEXT)).isStatusOK()) {
                log.error("Cannot set 'Text Mode' (AT Command CMGF). Initialization failed.");
                return false;
            }

            //use delivery report and time out expiration
            modem.send(new CSMP(CSMP.DELIVERY_REPORT | CSMP.VALIDITY_PERIOD));

            //test suppored stores
            CPMSsupport cpmss = new CPMSsupport();
            if (modem.send(cpmss).isStatusOK()) {
                mem1RW = getPreferedMemoryBySuppored(cpmss.getMemory1(), config.getMemory1RW());
                mem2Storege = getPreferedMemoryBySuppored(cpmss.getMemory2(), config.getMemory2Storage());
                mem3Rec = getPreferedMemoryBySuppored(cpmss.getMemory3(), config.getMemory3Rec());

                //set stores (for read/write, send, rec)
                if (!modem.send(new CPMS(mem1RW, mem2Storege, mem3Rec)).isStatusOK()) {
                    log.error("Cannot set storages (AT Command CPMS). Initialization failed.");
                    return false;
                }
            } else {
                log.error("CPMSsupport AT Command failed. Initialization failed.");
                return false;
            }

            //test for network
            //only for info
            if (isRegisteredIntoNetwork()) {
                if (!sufficientSignal()) {
                    log.warn("While modem initialization no signal");
                }
            } else {
                log.warn("While modem initialization, modem doesn't registered into network");
            }

            //set notification to PC
            if (!modem.send(new CNMI(
                    config.getCNMIMode(),
                    config.getCNMIMt(),
                    config.getCNMIBm(),
                    config.getCNMIDs())).isStatusOK()) {
                log.error("Cannot set notification policy (AT Command CNMI). Initialization failed.");
                return false;
            }
            
            //show caller ID when RING notify
            if (!modem.send(new CLIP(true)).isStatusOK()) {
                log.warn("Cannot set RING notification. Inbound Call Event  is out of service!");
            }

            status = Status.OPENED_INITIALIZED;
            log.info("Gateway initialized successfully");
            return true;
        } catch (ModemException ex) {
            throw new GatewayException(ex, port);
        }
    }

    /**
     * Send outbound SMS message.
     *
     * @param message
     * @throws GatewayException
     */
    public void sendMessage(OutboundMessage message) throws GatewayException {
        try {
            if (!status.equals(Status.OPENED_INITIALIZED)) {
                throw new GatewayRuntimeException("Modem is not OPENED and INITIALIZED", port);
            }

            if (smsServiceAddress == null) {
                throw new GatewayRuntimeException("Sms Service Address is empty. Set it first.", port);
            }

            if (!isRegisteredIntoNetwork() || !sufficientSignal()) {
                message.setStatus(OutboundMessage.Status.NOT_SEND_NO_SIGNAL);
                return;
            }

            if (message.isDeliveryReport()) {
                //TODO impl. me
            }

            CMGS cmgs = new CMGS(message.getDestination());
            if (!modem.send(cmgs).isStatusOK()) {
                //TODO
            }
            CMGSText cmgstext = modem.send(new CMGSText(message.getText()));
            if (!cmgstext.isStatusOK()) {
                //TODO
            }
            message.setIndex(cmgstext.getIndex());
            message.setStatus(OutboundMessage.Status.SENDED_NOT_ACK);
            outgoingList.add(message);
        } catch (ModemException ex) {
            throw new GatewayException(ex, port);
        }
    }

    /**
     * Set SMS service address.
     *
     * @param address
     * @throws GatewayException
     */
    public void setSmsServiceAddress(String address) throws GatewayException {
        Pattern pattern = Pattern.compile("^\\+?[1-9]\\d{1,14}$");
        Matcher matcher = pattern.matcher(address);

        try {
            if (matcher.matches()) {
                switch (status) {
                    case OPENED:
                    case OPENED_INITIALIZED: {

                        if (modem.send(new CSCA(address)).isStatusOK()) {
                            smsServiceAddress = address;
                        } else {
                            throw new GatewayRuntimeException("Modem cannot accept sms service address", port);
                        }

                    }
                    break;
                    case CLOSED:
                        smsServiceAddress = address;
                        break;
                }
            } else {
                throw new GatewayRuntimeException("The Message Service Address has invalid format", port);
            }
        } catch (ModemException ex) {
            throw new GatewayException(ex, port);
        }
    }

    @Override
    public void notify(Notification notification) {
        try {
            /*
             Delivery status
             */
            if (notification instanceof CDSI) {
                CDSI cdsi = (CDSI) notification;

                modem.send(new AT());
                //change to memory from CDSI notification
                modem.send(new CPMS(cdsi.getMemoryType()));
                //read status repord
                CMGR cmgr = modem.send(new CMGR(CMGR.Mode.SMS_STATUS_REPORT, cdsi.getSMSIndex()));
                if (!cmgr.isStatusOK()) {
                    int eCode = cmgr.getCmsErrorCode();
                    if (eCode > 0) {
                        cmgr.throwExceptionInMainThread(new RuntimeException("Cannot read sms status by index (" + eCode + "): " + cdsi.getSMSIndex()));
                        return;
                    }
                }
                //delete status repord
                modem.send(new CMGD(cdsi.getSMSIndex()));
                //change back to main memory
                modem.send(new CPMS(mem1RW));

                if(!findOutboudMessage(cmgr.getMr(), cmgr.getSp())) {
                    throw new RuntimeException("Cannot find  message with index " + cdsi.getSMSIndex());
                }
            }

            if (notification instanceof CDS) {
                CDS cds = (CDS) notification;
                if(!findOutboudMessage(cds.getMr(), cds.getStatus())) {
                    throw new RuntimeException("Cannot find  message with index " + cds.getMr());
                }
            }

            /*
             Incoming call
             */
            if (notification instanceof RING) {
                RING ring = (RING) notification;
                createInboundCallEvent(ring.getCallerId(), ring.getValidity());
                return;
            }
            /*
             Incoming call
             */
            if (notification instanceof CLIPN) {
                CLIPN ring = (CLIPN) notification;
                createInboundCallEvent(ring.getCallerId(), RING.Validity.VALID);
                return;
            }
            /*
             Incoming SMS
             */
            if (notification instanceof CMTI) {
                CMTI cmti = (CMTI) notification;
                //change to memory from CDSI notification
                modem.send(new CPMS(cmti.getMemoryType()));
                //read sms
                CMGR cmgr = modem.send(new CMGR(CMGR.Mode.SMS_DELIVERY, cmti.getSMSIndex()));
                if (!cmgr.isStatusOK()) {
                    int eCode = cmgr.getCmsErrorCode();
                    if (eCode > 0) {
                        cmgr.throwExceptionInMainThread(new RuntimeException("Cannot read sms by index (" + eCode + "): " + cmti.getSMSIndex()));
                        return;
                    }
                }
                //delete sms
                modem.send(new CMGD(cmti.getSMSIndex()));
                //change back to main memory
                modem.send(new CPMS(mem1RW));
                createInboundMessageEvent(new InboundMessage(cmgr.getText(), cmgr.getOa()));
                return;
            }
            
            if (notification instanceof CMT) {
                CMT cmt = (CMT) notification;
                createInboundMessageEvent(new InboundMessage(cmt.getData(), cmt.getOa()));
                return;
            }            
        } catch (ModemException ex) {
            log.warn("Exception while notification process", ex);
        }
    }

    private boolean findOutboudMessage(int messIndex, SPStatus messStatus) {
        for (OutboundMessage outMess : outgoingList) {
            if (outMess.getIndex() == messIndex) {

                switch (messStatus) {
                    case RECEIVED: //0
                        outMess.setStatus(OutboundMessage.Status.SENDED_ACK);
                        outgoingList.remove(outMess);
                        createOutboundEvent(outMess, outMess.getStatus());
                        break;
                    case SEVICE_REJECTED: //99
                        outMess.setStatus(OutboundMessage.Status.EXPIRED);
                        outgoingList.remove(outMess);
                        createOutboundEvent(outMess, outMess.getStatus());
                        break;
                    default:
                        log.warn("Unknown Outboud Message status: '{}'",messStatus);                        
                } 
                return true;
            }
        }
        return false;
    }

    private void createOutboundEvent(OutboundMessage mess, OutboundMessage.Status status) {
        if (smsStatusListener != null) {
            smsStatusListener.outboundMessageEvent(new OutboundMessageEvent(mess, status));
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
        }
    }

    /*
     Private methods
     */
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
    private boolean isRegisteredIntoNetwork() throws ModemException {
        //test if modem is registered into GSM network
        CREGquestion cregq = modem.send(new CREGquestion());
        if (!cregq.useSMS()) {
            return false;
        }
        return true;
    }

    /**
     * Return true if modem have any signal
     *
     * @return
     * @throws ModemException
     */
    //TODO co se stane kdyz zavolam totu metodu a pritom telefon nebude zaregistrovat ??
    private boolean sufficientSignal() throws ModemException {
        //network signal stranche
        CSQ csq = new CSQ();
        if (!modem.send(csq).isStatusOK()) {
            return false;
        }

        if (csq.getRawValue() <= 1) {
            return false;
        }
        return true;
    }

    private ModemInformation getManufactures() throws ModemException {
        ModemInformation info = new ModemInformation();

        info.setManufacturer(modem.send(new GMI()).getManufaturer());
        info.setModelAndCapabilities(modem.send(new CGMM()).getModel());
        info.setImei(modem.send(new CGSN()).getIMEI());

        return info;
    }

    public void printModemInfo() throws ModemException {
        System.out.print("Manufacturer name: ");
        System.out.println(modem.send(new GMI()).getManufaturer());

        System.out.print("Manufacturer OS version: ");
        System.out.println(modem.send(new CGMR()).getVersion());

        System.out.print("IMEI: ");
        System.out.println(modem.send(new CGSN()).getIMEI());

        System.out.print("Model description: ");
        System.out.println(modem.send(new CGMM()).getModel());
        
        //select optimal modem configuration
        config.selectModem(this.getManufactures());        
    }

    /**
     * Gateway status
     */
    public enum Status {

        OPENED, OPENED_INITIALIZED, CLOSED;
    }   
}
