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
import cz.zerog.jsms4pi.Tool;
import cz.zerog.jsms4pi.event.CallEvent;
import cz.zerog.jsms4pi.event.InboundCallEventListener;
import cz.zerog.jsms4pi.exception.GatewayException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author zerog
 */
public class InboundCallExample implements InboundCallEventListener {

    public static void main(String[] args) throws GatewayException, IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        String port = Tool.selectionPort(reader);

        System.out.println("Summary: ");
        System.out.println("Serial port: " + port);

        Tool.enter(reader);

        new InboundCallExample(port, reader);
    }

    public InboundCallExample(String port, BufferedReader reader) throws GatewayException, IOException {
        ATGateway gateway = new ATGateway(port);

        try {
            gateway.setInboundCallListener(this);

            gateway.open();
            gateway.init();
        } catch (Exception e) {          
            gateway.close();
            throw e;
        }

        System.out.print("Now try call me.  Enter key exits program.");
        reader.readLine();

        gateway.close();
    }

    @Override
    public void inboundCallEvent(CallEvent event) {
        System.out.println("Detected a call: " + event.getCallerId());
    }
}
