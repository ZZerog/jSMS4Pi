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
import cz.zerog.jsms4pi.exception.GatewayException;
import cz.zerog.jsms4pi.listener.InboundCallGatewayListener;
import cz.zerog.jsms4pi.listener.InboundMessageGatewayListener;

/**
 *
 * @author zerog
 */
public class ReceiveSMSExample implements  InboundCallGatewayListener, InboundMessageGatewayListener {

    public static void main(String[] args) throws GatewayException, IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        String port = null;

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

        System.out.println("\n\n\nSummary: ");
        System.out.println("Serial port: " + port);


        Tool.enter(reader);

        new ReceiveSMSExample().run(port, reader);
    }

    private static void printHelp() {
        //TODO 
        System.out.println("Info");
    }

    public void run(String port, BufferedReader reader) throws GatewayException, IOException {

        ATGateway gateway = new ATGateway(port);

        gateway.setInboundCallListener(this);
        gateway.setInboundMessageListener(this);

        gateway.open();
        gateway.init();

        System.out.println("Enter to exit");
        System.console().readLine();

        gateway.close();
        System.out.println("Bye");
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
