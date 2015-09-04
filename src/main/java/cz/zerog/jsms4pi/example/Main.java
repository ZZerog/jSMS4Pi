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
public class Main implements OutboundMessageEventListener, InboundCallEventListener {
    
    /*
    TODO LIST
    
     - programek co automaticky najde modemy pripojene na USB porty
     - vyresit zmenu konfigurace na zaklade modemu (zda li to vubec bude nutne), ale asi jo, protoze 
        modemy co mam doted neumeji telefonovat a nektere neumi ani informovat o prozvoneni
     - otestovat co modem umi .. odeslat a prijmout sam sobe sms, vyzvat na prozvoneni, vyzvat na zavolani
     - implementovat prijimani SMS
     - vyresit globalni nastaveni v gateway
     - zatim nejde nastavit validity period
     - zatim nejde nastavit delivery report
     - pridate sendQueue, ktera posle sms hned (neblokuje)
     - otestovat chybove stavy modemu ... co se stane kdyz modem vytahnu, je mozne aby modem neodpovidal (throw exception)
     - zjisteni ceny (zbytek kreditu)
     - pokud neco nejde sparserovat, reagovat na to nejakou normani vyjimkou 
     - dat doporadku trosku tridy
    
    
    */
    public void start() throws GatewayException {

        System.out.println("AHHHOJ");
        //AtGateway gateway = new AtGateway("/dev/ttyUSB2");
        ATGateway gateway = new ATGateway("/dev/ttyUSB0");
        
        gateway.setOutboundMessageEventListener(this);
        gateway.setInboundCallListener(this);
        
        gateway.open();
        gateway.init();
        
        //gateway.setGlobalDeliveryReport(true);
        //gateway.setGlobalValidityPeriod(true);
        
        gateway.setSmsServiceAddress("+420603052000");
        //gateway.sendSms(new OutboundMessage("Unix time: " + System.currentTimeMillis(), "+17327685861")); //REWI
        gateway.sendMessage(new OutboundMessage("Unix time: " + System.currentTimeMillis(), "+420739474009"));

        System.console().readLine();

        gateway.close();
        System.out.println("END of Program");        
    }

    public static void main(String[] args) throws GatewayException {
        new Main().start();
        
    }

    @Override
    public void outboundMessageEvent(OutboundMessageEvent event) {
        switch(event.getStatus()) {
            case SENDED_ACK:
                System.out.println("SMSka do cisla "+event.getMessage().getDestination()+" dosla!");
                break;
            case EXPIRED:
                System.out.println("Nedosla, cislo "+event.getMessage().getDestination()+" nema aktivni telefon");
                break;
        }
    }

    @Override
    public void inboundCallEvent(CallEvent event) {
        System.out.println("Volame me cislo "+event.getCallerId());
    }
}
