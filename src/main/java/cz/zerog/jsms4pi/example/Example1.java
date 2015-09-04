package cz.zerog.jsms4pi.example;

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
import cz.zerog.jsms4pi.message.OutboundMessage;
import cz.zerog.jsms4pi.event.CallEvent;
import cz.zerog.jsms4pi.event.InboundCallEventListener;
import cz.zerog.jsms4pi.event.OutboundMessageEvent;
import cz.zerog.jsms4pi.event.OutboundMessageEventListener;
import cz.zerog.jsms4pi.exception.GatewayException;


/**
 *
 * @author zerog
 */
public class Example1 implements OutboundMessageEventListener, InboundCallEventListener {
    

    public static void main(String[] args) throws GatewayException {
        new Example1().start();
    }
    
    public void start() throws GatewayException {

        //AtGateway gateway = new AtGateway("/dev/ttyUSB2");
        ATGateway gateway = new ATGateway("/dev/ttyUSB0");
        
        gateway.setOutboundMessageEventListener(this);
        gateway.setInboundCallListener(this);
        
        gateway.open();
        gateway.init();
        
        //gateway.setGlobalDeliveryReport(true);
        //gateway.setGlobalValidityPeriod(true);
        
        gateway.setSmsServiceAddress("+420603052000"); //CZ T-mobile 
        gateway.sendMessage(new OutboundMessage("Unix time: " + System.currentTimeMillis(), "+420739474009"));

        System.console().readLine();

        gateway.close();
        System.out.println("The END");        
    }


    @Override
    public void outboundMessageEvent(OutboundMessageEvent event) {
        switch(event.getStatus()) {
            case SENDED_ACK:
                System.out.println("Delivery: "+event.getMessage().getDestination());
                break;
            case EXPIRED:
                System.out.println("Time expired. "+event.getMessage().getDestination());
                break;
        }
    }

    @Override
    public void inboundCallEvent(CallEvent event) {
        System.out.println("Detected a call: "+event.getCallerId());
    }
}
