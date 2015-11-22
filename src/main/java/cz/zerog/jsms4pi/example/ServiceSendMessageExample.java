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
import cz.zerog.jsms4pi.message.OutboundMessage;

/**
 * This example demonstrates how use a Service and send text message.
 *
 * @author zerog
 */
public class ServiceSendMessageExample {

	public ServiceSendMessageExample(String port, String destination, String text) throws IOException {

		// create new text message
		OutboundMessage message = new OutboundMessage(text, destination);

		// get service
		Service service = Service.getInstance();

		// add a gateway into service, named 'fooGateway'
		service.addDefaultGateway(port, "fooGateway");

		service.sendMessage(message);

		// only pause a main thread until a key press
		Tool.pressEnterExit();

		// remove all gateways from service
		service.removeAllGateway();
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
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		Tool.showHelp(args,
				String.format("Parameters:%n -p <port name> name of a serial port%n -h show this information%n%n"
						+ "Interactive mode is activated when starting the program without parameters"));

		String port = Tool.selectionPort(args);
		String destination = Tool.destNumber(args);
		String text = Tool.text(args);

		System.out.println(String.format("%n--- Summary ---"));
		System.out.println("Serial port: " + port);
		System.out.println("Destination number: " + destination);
		System.out.println("Message text: " + text);

		Tool.pressEnter();

		new ServiceSendMessageExample(port, destination, text);
	}
}
