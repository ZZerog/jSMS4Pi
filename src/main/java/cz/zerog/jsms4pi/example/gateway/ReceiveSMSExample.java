package cz.zerog.jsms4pi.example.gateway;

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
import cz.zerog.jsms4pi.ATGateway;
import cz.zerog.jsms4pi.Gateway;
import cz.zerog.jsms4pi.SerialModem;
import cz.zerog.jsms4pi.example.Tool;
import cz.zerog.jsms4pi.exception.GatewayException;
import cz.zerog.jsms4pi.listener.event.InboundMessageEvent;
import cz.zerog.jsms4pi.listener.gateway.InboundMessageGatewayListener;

/**
 * Example class. Show how used a Gateway to received a inbound message.
 * 
 * @author zerog
 */
public class ReceiveSMSExample implements InboundMessageGatewayListener {

	/**
	 * Create and setup the gateway to accepted a inbound messages
	 * 
	 * @param port
	 *            port name
	 * @throws GatewayException
	 * @throws IOException
	 */
	public ReceiveSMSExample(String port) throws GatewayException, IOException {

		// create a default gateway (default parameters)
		Gateway gateway = null;

		try {
			gateway = ATGateway.getDefaultFactory(port);

			// set listeners
			gateway.setInboundMessageListener(this);

			// open and establish a connection with the modem
			gateway.open();
			gateway.init();

			// only pause a main thread until a key press
			Tool.pressEnterExit();

		} finally {
			if (gateway != null) {
				gateway.close();
			}
			System.out.println("Bye");
		}
	}

	/**
	 * Called when a new inbound message is delivered
	 */
	@Override
	public void inboundMessageEvent(InboundMessageEvent inboundMessageEvent) {
		System.out.println("Received Message from '" + inboundMessageEvent.getMessage().getSource() + "', text: "
				+ inboundMessageEvent.getMessage().getText());
	}

	/**
	 * Parse parameters or asking user for information.
	 * 
	 * Parameters: -p name of a serial port -h show this information
	 * 
	 * Interactive mode is activated when starting the program without
	 * parameters.
	 * 
	 * @param args
	 * @throws GatewayException
	 * @throws IOException
	 */
	public static void main(String[] args) throws GatewayException, IOException {

		Tool.showHelp(args,
				String.format("Parameters:%n -p <port name> name of a serial port%n -h show this information%n%n"
						+ "Interactive mode is activated when starting the program without parameters"));

		String port = Tool.selectionPort(args, SerialModem.getAvailablePorts());

		System.out.println(String.format("%n--- Summary ---"));
		System.out.println("Serial port: " + port);

		Tool.pressEnter();

		new ReceiveSMSExample(port);
	}
}
