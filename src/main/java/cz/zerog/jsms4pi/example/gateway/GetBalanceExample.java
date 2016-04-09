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
import cz.zerog.jsms4pi.SerialModem;
import cz.zerog.jsms4pi.at.CUSD;
import cz.zerog.jsms4pi.example.Tool;
import cz.zerog.jsms4pi.exception.GatewayException;

/**
 * Get balance for a prepaid card.
 * 
 * TODO: CUSD is not implemented correct.
 *
 * @author zerog
 */
public class GetBalanceExample {

	public GetBalanceExample(String port) throws GatewayException, IOException {

		// create a default gateway (default parameters)
		ATGateway gateway = null;

		try {

			gateway = (ATGateway) ATGateway.getDefaultFactory(port);

			// open gateway
			gateway.open();

			// send CUSD AT command
			System.out.println(gateway.directSendAtCommand(new CUSD(15)).getResponse());

			Tool.pressEnterExit();
		} finally {
			if (gateway != null) {
				// close gateway
				gateway.close();
			}
		}
	}

	public static void main(String[] args) throws Exception, InterruptedException {

		Tool.showHelp(args,
				String.format("Parameters:%n -p <port name> name of a serial port%n -h show this information%n%n"
						+ "Interactive mode is activated when starting the program without parameters"));

		String port = Tool.selectionPort(args, SerialModem.getAvailablePorts());

		System.out.println(String.format("%n--- Summary ---"));
		System.out.println("Serial port: " + port);

		Tool.pressEnter();

		new GetBalanceExample(port);
	}
}
