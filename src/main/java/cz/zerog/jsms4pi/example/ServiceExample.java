package cz.zerog.jsms4pi.example;

import java.io.IOException;

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

import cz.zerog.jsms4pi.Service;
import cz.zerog.jsms4pi.event.CallEvent;
import cz.zerog.jsms4pi.listener.InboundCallListener;

/**
 *
 * @author zerog
 */
public class ServiceExample implements InboundCallListener {
	public static void main(String[] args) throws IOException {
		new ServiceExample().start();
	}

	public void start() throws IOException {
		Service service = Service.getInstance();
		service.addDefaultGateway("/dev/ttyUSB4", "o2");
		service.addInboundCallListener(this);
		System.out.println("Running");
		System.in.read();
	}

	@Override
	public void inboundCallEvent(String gatewayName, CallEvent callEvent) {
		System.out.println("Detected call " + callEvent.getCallerId() + " by number. Gateway: " + gatewayName);
	}
}
