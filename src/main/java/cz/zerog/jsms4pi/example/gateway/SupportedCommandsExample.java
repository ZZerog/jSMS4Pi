package cz.zerog.jsms4pi.example.gateway;

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
import cz.zerog.jsms4pi.at.CLAC;
import cz.zerog.jsms4pi.example.Tool;
import cz.zerog.jsms4pi.exception.GatewayException;

/**
 * Example class. Print all AT commands the modem supports.
 * 
 * @author zerog
 */
public class SupportedCommandsExample {

	public SupportedCommandsExample(String port) throws GatewayException {

		// create a default gateway (default parameters)
		Gateway gateway = null;

		try {

			gateway = ATGateway.getDefaultFactory(port);

			// open connection
			gateway.open();

			// send AT command 'CLAC'
			// result is a array of supported commands
			for (String cmd : gateway.directSendAtCommand(new CLAC()).getSupportedCommandList()) {
				System.out.println(cmd);
			}

		} finally {
			if (gateway != null) {
				// close connection
				gateway.close();
			}
		}
	}

	/**
	 * Parse parameters or asking user for information.
	 * 
	 * Parameters: -p <port name> name of a serial port -h show this information
	 * 
	 * Interactive mode is activated when starting the program without
	 * parameters.
	 * 
	 * @param args
	 * @throws Exception
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws Exception, InterruptedException {

		Tool.showHelp(args,
				String.format("Parameters:%n -p <port name> name of a serial port%n -h show this information%n%n"
						+ "Interactive mode is activated when starting the program without parameters"));

		String port = Tool.selectionPort(args, SerialModem.getAvailablePorts());
		System.out.println(String.format("%n--- Summary ---"));
		System.out.println("Serial port: " + port);

		Tool.pressEnter();

		Tool.activeLoggingToFile();

		new SupportedCommandsExample(port);
	}
}
