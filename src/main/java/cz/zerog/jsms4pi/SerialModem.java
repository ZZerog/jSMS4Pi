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


import cz.zerog.jsms4pi.at.AAT;
import cz.zerog.jsms4pi.exception.ModemException;
import cz.zerog.jsms4pi.notification.CDSI;
import cz.zerog.jsms4pi.notification.Notification;
import cz.zerog.jsms4pi.notification.RING;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author zerog
 */
public class SerialModem extends Thread implements Modem, SerialPortEventListener {

    // Logger
    private final Logger log = LogManager.getLogger();

    /*
     RS-232 setting
     */
    private String portName;
    private static String defaultPort = "/dev/ttyUSB2";
    protected SerialPort serialPort;
    private final int speed = SerialPort.BAUDRATE_57600;

    /*
     Gateway 
     */
    private Gateway gateway;

    /*
     Object which waiting for part of response
     Notification or AAT
     */
    private ATResponse atResponse = null;

    private final Queue<Notification> notificationQueue = new LinkedBlockingQueue<>();

    private Mode mode = Mode.READY;

    public SerialModem(Gateway gateway) {
        this.gateway = gateway;
        this.setName("SerialNotifyThread");
        this.setDaemon(true);
        this.start();
    }

    @Override
    public void open(String portName) throws ModemException {
        this.portName = portName;
        try {
            close();
            serialPort = new SerialPort(portName);
            serialPort.openPort();
            serialPort.setParams(speed, 8, 1, SerialPort.PARITY_NONE);
            serialPort.setEventsMask(SerialPort.MASK_RXCHAR);
            serialPort.addEventListener(this);
            log.info("Port open '{}'", portName);
        } catch (SerialPortException ex) {
            throw new ModemException(ex);
        }
    }

    @Override
    public void close() throws ModemException {
        if (serialPort != null) {
            try {
                serialPort.closePort();
            } catch (SerialPortException ex) {
                throw new ModemException(ex);
            }
        }
    }

    @Override
    public <T extends AAT> T send(T cmd) throws ModemException {
        //seve and set mode
        atResponse = (ATResponse) cmd;
        mode = Mode.AT;

        cmd.send();
        String request = cmd.getPrefix() + cmd.getRequest();
        log.info("Request: {}", crrt(request));
        try {
            serialPort.writeString(request);
            synchronized (cmd) {
                cmd.wait();
            }
        } catch (InterruptedException ex) {
            log.warn("Interuppted while wait for answer");
        } catch (SerialPortException ex) {
            throw new ModemException(ex);
        }
        atResponse = null;
        mode = Mode.READY;
        return cmd;
    }

    @Override
    public void run() {
        while (true) {
            gateway.notify(notificationQueue.poll());
        }
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        // if received some bytes
        if (event.getEventValue() > 0) {
            try {
                //read it
                String response = serialPort.readString();

                switch (mode) {
                    case AT:
                        if (response == null) {
                            //TODO
                            log.warn("In mode AT is atResponse = null");
                            break;                            
                        }
                        synchronized (atResponse) {
                            if (atResponse.appendResponse(response)) {
                                log.info("Response: [{}]", crrt(atResponse.getResponse()));
                                atResponse.notify();
                            }
                        }

                        break;
                    case READY:

                        /*
                         Create Notification object
                         */
                        if (response.contains("CDSI")) {
                            atResponse = new CDSI();
                        } else if (response.contains("RING")) {
                            atResponse = new RING();
                        } else {
                            break;
                        }

                    case NOTIFY:

                        /*
                         If notifi. exist
                         */
                        if (atResponse == null) {
                            //TODO
                            log.warn("In mode NOTIFY is atResponse = null");
                            break;
                        }
                        mode = Mode.NOTIFY;

                        /*
                         If "response" is complete
                         */
                        if (atResponse.appendResponse(response)) {
                            log.info("Notify: [{}]", crrt(response));
                            notificationQueue.add((Notification) atResponse);
                            mode = Mode.READY;
                        }

                        break;
                }
            } catch (SerialPortException ex) {
                log.warn(ex,ex);
            }
        }
    }

    private String crrt(String input) {
        return input.replaceAll("\r\n", " ");
    }

    private enum Mode {

        READY, AT, NOTIFY;
    }
}
