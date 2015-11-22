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
import cz.zerog.jsms4pi.example.Tool;
import cz.zerog.jsms4pi.exception.GatewayException;

/**
 * Example class. Show how print information about modem.
 *
 * @author zerog
 */
public class InfoExample {

	/**
	 * Create the gateway and show modem information
	 * 
	 * @param port
	 *            port name
	 * @throws GatewayException
	 */
	private InfoExample(String port) throws GatewayException {

		ATGateway gateway = null;

		try {
			// create a default gateway (default parameters)
			gateway = (ATGateway) ATGateway.getDefaultFactory(port);

			// open connection
			gateway.open();
			gateway.init();

			// print model number
			gateway.printModemInfo();

		} finally {
			if (gateway != null) {
				// close connection to gateway
				gateway.close();
			}
		}

	}

	/**
	 * Parse parameters or asking user for information.
	 * 
	 * Parameters: -p name of a serial port -h show this information -d
	 * destination number -t text of message -s service number
	 * 
	 * Interactive mode is activated when starting the program without
	 * parameters.
	 * 
	 * @param args
	 * @throws IOException
	 * @throws GatewayException
	 */
	public static void main(String[] args) throws IOException, GatewayException {
		Tool.showHelp(args,
				String.format("Parameters:%n -p <port name> name of a serial port%n -h show this information%n%n"
						+ "Interactive mode is activated when starting the program without parameters"));

		String port = Tool.selectionPort(args);

		System.out.println(String.format("%n--- Summary ---"));
		System.out.println("Serial port: " + port);

		Tool.pressEnter();

		new InfoExample(port);
	}
}
