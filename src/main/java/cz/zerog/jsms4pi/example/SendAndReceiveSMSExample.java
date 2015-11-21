package cz.zerog.jsms4pi.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
import cz.zerog.jsms4pi.ATGateway;
import cz.zerog.jsms4pi.event.CallEvent;
import cz.zerog.jsms4pi.event.InboundMessageEvent;
import cz.zerog.jsms4pi.event.OutboundMessageEvent;
import cz.zerog.jsms4pi.exception.GatewayException;
import cz.zerog.jsms4pi.listener.InboundCallGatewayListener;
import cz.zerog.jsms4pi.listener.InboundMessageGatewayListener;
import cz.zerog.jsms4pi.listener.OutboundMessageGatewayListener;
import cz.zerog.jsms4pi.message.OutboundMessage;

/**
 *
 * @author zerog
 */
public class SendAndReceiveSMSExample implements OutboundMessageGatewayListener, InboundCallGatewayListener, InboundMessageGatewayListener {

    public static void main(String[] args) throws GatewayException, IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        String port = null;
        String number = null;
        String text = null;
        String servise = null;

        if (args.length > 0) {

            if (args.length % 2 != 0 && args.length > 1) {
                System.out.println("Wrong count of argument.");
                printHelp();
                System.exit(0);
            }

            for (int i = 0; i < args.length; i++) {
                String identifier = args[i];

                switch (identifier) {
                    case "-p":
                        i++;
                        port = args[i];
                        break;
                    case "-d":
                        i++;
                        number = args[i];
                        break;
                    case "-m":
                        i++;
                        text = args[i];
                        break;
                    case "-s":
                        i++;
                        servise = args[i];
                        break;
                    case "-h":
                    case "-help":
                        printHelp();
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Unknow parametr " + args[i]);
                        printHelp();
                        System.exit(0);
                }
            }
        }

        if (port == null) {
            port = Tool.selectionPort(reader);
        }
        
        if(port == null) {
            System.exit(0);
        }

        if (number == null) {
            System.out.println("Write destination phone number: ");
            number = reader.readLine();
        }

        if (text == null) {
            System.out.println("Write text of message: ");
            text = reader.readLine();
        }

        System.out.println("\n\n\nSummary: ");
        System.out.println("Destination phone number: " + number);
        System.out.println("Message text: " + text);
        System.out.println("Serial port: " + port);
        if(servise!=null) {
            System.out.println("SMS servise number: "+servise);
        }

        Tool.enter(reader);

        OutboundMessage message = new OutboundMessage(text, number);

        new SendAndReceiveSMSExample().send(port, message, reader, servise);
    }

    private static void printHelp() {
        //TODO 
        System.out.println("Info");
    }

    public void send(String port, OutboundMessage message, BufferedReader reader, String servise) throws GatewayException, IOException {

        ATGateway gateway = new ATGateway(port);

        gateway.setOutboundMessageListener(this);
        gateway.setInboundCallListener(this);
        gateway.setInboundMessageListener(this);

        gateway.open();
        gateway.init();

        if (servise == null) {
            if (!gateway.isServiceAddressSet()) {
                System.out.println("Can you write phone number of SMS Service Address: ");
                gateway.setSmsServiceAddress(reader.readLine());
            }
        } else {
            gateway.setSmsServiceAddress(servise);
        }

        gateway.sendMessage(message);

        System.out.println("Enter to exit");
        System.console().readLine();

        gateway.close();
        System.out.println("Bye");
    }

    @Override
    public void outboundMessageEvent(OutboundMessageEvent event) {
        switch (event.getStatus()) {
            case SENDED_ACK:
                System.out.println("Delivery: " + event.getMessage().getDestination());
                break;
            case EXPIRED:
                System.out.println("Time expired. " + event.getMessage().getDestination());
                break;
        }
    }

    @Override
    public void inboundCallEvent(CallEvent event) {
        System.out.println("Detected a call: " + event.getCallerId());
    }

    @Override
    public void inboundMessageEvent(InboundMessageEvent inboundMessageEvent) {
        System.out.println("Received Message from '"+inboundMessageEvent.getMessage().getSource()+"', text: "+inboundMessageEvent.getMessage().getText());
    }
}
