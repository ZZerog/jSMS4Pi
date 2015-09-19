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
import cz.zerog.jsms4pi.at.CPMS.TypeOfMemory;
import cz.zerog.jsms4pi.exception.GatewayException;
import cz.zerog.jsms4pi.exception.GatewayRuntimeException;
import cz.zerog.jsms4pi.exception.ModemException;
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

    public void setOutboundMessageEventListener(OutboundMessageEventListener listener) {
        this.smsStatusListener = listener;
    }

    public void setInboundCallListener(InboundCallEventListener callListener) {
        this.callListener = callListener;
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
            log.info("Gateway on port '{}' is ready", port);
        } catch (ModemException ex) {
            throw new GatewayException(ex);
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
            log.info("Gateway on port '{}' is closed", port);
        } catch (ModemException e) {
            throw new GatewayException(e);
        }
    }

    public boolean isServiceAddressSet() throws GatewayException {
        try {
            CSCAquestion cscaq = modem.send(new CSCAquestion());
            if (!cscaq.isStatusOK()) {
                throw new GatewayException("Cannot test if Service Address is set");
            }
            return cscaq.getAddress().length() > 0;

        } catch (ModemException ex) {
            throw new GatewayException(ex);
        }
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
                throw new GatewayRuntimeException("Gateway on port '" + port + "' is closed");
            }

            //only test
            modem.send(new AT());
            //restart modem
            modem.send(new ATZ());
            //echo disable
            modem.send(new ATE0());

            //set sms service address
            if (smsServiceAddress == null) {
                if (!isServiceAddressSet()) {
                    log.info("Cannot send message before set Service Address!.  User method setSmsServiceAddress(service_number);");
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
                mem1RW = getPreferedMemoryBySuppored(cpmss.getMemory1());
                mem2Storege = getPreferedMemoryBySuppored(cpmss.getMemory1());
                mem3Rec = getPreferedMemoryBySuppored(cpmss.getMemory1());

                //set stores (for read/write, send, rec)
                if(!modem.send(new CPMS(mem1RW, mem2Storege, mem3Rec)).isStatusOK()) {
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
                log.warn("While modem initialization modem isn't registered into network");
            }

            //set notification to PC
            if (!modem.send(new CNMI(
                    CNMI.Mode._2,
                    CNMI.Mt.NOTIFI_1,
                    CNMI.Bm.NO_CBM_NOTIFI_0,
                    CNMI.Ds.STATUS_REPORT_NOTIFI_IF_STORED_2)).isStatusOK()) {
                log.error("Cannot set notification policy (AT Command CNMI). Initialization failed.");
                return false;
            }

            //show caller ID when RING notify
            if(modem.send(new CLIP(true)).isStatusOK()) {
                log.warn("Cannot set RING notification. Inbound Call Event  is out of service!");
            }

            status = Status.OPENED_INITIALIZED;
            log.info("Gateway inicialized succesful");
            return true;
        } catch (ModemException ex) {
            throw new GatewayException(ex);
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
                throw new GatewayRuntimeException("Modem is not OPENED and INITIALIZED");
            }

            if (smsServiceAddress == null) {
                throw new GatewayRuntimeException("Sms Service Address is empty. Set it first.");
            }

            if (!isRegisteredIntoNetwork() || !sufficientSignal()) {
                message.setStatus(OutboundMessage.Status.NOT_SEND_NO_SIGNAL);
                return;
            }

            //TODO v SMS musi byt delivery report 3 stavy, active, deactive a nenastaveno. Protoze pokud je aktivni nebo je aktivni globalni
            //tak se nastavi, pokud je aktivni globalni a v sms nenastaveno tak se taky nastavi
            //ale pokud je v sms nastaveno neaktivni a globalni je nastaveno na aktivni tak se NEODESLE!
            //SMS nastaveni ma vyssi prioritu od globalniho
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
            throw new GatewayException(ex);
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
                            throw new GatewayRuntimeException("Modem cannot accept sms service address");
                        }

                    }
                    break;
                    case CLOSED:
                        smsServiceAddress = address;
                        break;
                }
            } else {
                throw new GatewayRuntimeException("Address number have bad format.");
            }
        } catch (ModemException ex) {
            throw new GatewayException(ex);
        }
    }

    @Override
    public void notify(Notification notification) {
        try {
            if (notification instanceof CDSI) {
                CDSI cdsi = (CDSI) notification;

                modem.send(new AT());
                //change to memory from CDSI notification
                modem.send(new CPMS(cdsi.getMemoryType()));
                //read status repord
                CMGR cmgr = modem.send(new CMGR(cdsi.getSMSIndex()));
                if (!cmgr.isStatusOK()) {
                    int eCode = cmgr.getCmsErrorCode();
                    if(eCode>0) {
                        cmgr.throwExceptionInMainThread(new RuntimeException("Cannot read sms status by index ("+eCode+"): " + cdsi.getSMSIndex()));
                        return;
                    }                    
                }
                //delete status repord
                modem.send(new CMGD(cdsi.getSMSIndex()));
                //change back to main memory
                modem.send(new CPMS(mem1RW));

                for (OutboundMessage outMess : outgoingList) {
                    if (outMess.getIndex() == cmgr.getMr()) {

                        switch (cmgr.getCode()) {
                            case 0:
                                outMess.setStatus(OutboundMessage.Status.SENDED_ACK);
                                outgoingList.remove(outMess);
                                createOutboundEvent(outMess, outMess.getStatus());
                                break;
                            case 99:
                                outMess.setStatus(OutboundMessage.Status.EXPIRED);
                                outgoingList.remove(outMess);
                                createOutboundEvent(outMess, outMess.getStatus());
                                break;
                            default:
                                return;

                        }
                        return;
                    }
                }

                throw new RuntimeException("Cannot find  message with index " + cdsi.getSMSIndex());
            }
            if (notification instanceof RING) {
                RING ring = (RING) notification;
                createInboundCallEvent(ring.getCallerId(), ring.getValidity());
                return;
            }
            if (notification instanceof CLIPN) {
                CLIPN ring = (CLIPN) notification;
                createInboundCallEvent(ring.getCallerId(), RING.Validity.VALID);
                return;
            }
        } catch (ModemException ex) {
            log.warn("Exception while notification process", ex);
        }
    }

    private void createOutboundEvent(OutboundMessage mess, OutboundMessage.Status status) {
        if (smsStatusListener != null) {
            smsStatusListener.outboundMessageEvent(new OutboundMessageEvent(mess, status));
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
    private TypeOfMemory getPreferedMemoryBySuppored(TypeOfMemory[] arr) {
        List<TypeOfMemory> m1 = Arrays.asList(arr);
        if (m1.contains(TypeOfMemory.ME)) {
            return TypeOfMemory.ME;
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

    /**
     * Gateway status
     */
    public enum Status {

        OPENED, OPENED_INITIALIZED, CLOSED;
    }
}
