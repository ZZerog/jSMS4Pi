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
import cz.zerog.jsms4pi.exception.AtParseException;
import cz.zerog.jsms4pi.exception.GatewayException;
import cz.zerog.jsms4pi.listener.event.InboundMessageEvent;
import cz.zerog.jsms4pi.listener.event.OutboundMessageEvent;
import cz.zerog.jsms4pi.listener.gateway.InboundMessageGatewayListener;
import cz.zerog.jsms4pi.listener.gateway.OutboundMessageGatewayListener;
import cz.zerog.jsms4pi.message.OutboundMessage;

/**
 * Example class. Show how used a Gateway to send and received message.
 *
 * @author zerog
 */
public class SendAndReceiveSMSExample implements OutboundMessageGatewayListener, InboundMessageGatewayListener {

	/**
	 * Create and setup the gateway to the outgoing sending messages
	 * 
	 * @param port
	 *            port where is modem
	 * @param text
	 *            text of message
	 * @param number
	 *            destination number
	 * @param service
	 *            number of SMS service
	 * 
	 * @throws GatewayException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void send(String port, String text, String number, String service)
			throws IOException, GatewayException, InterruptedException {

		// create a default gateway (default parameters)
		Gateway gateway = null;

		try {
			gateway = ATGateway.getDefaultFactory(port);

			// set listeners
			gateway.setOutboundMessageListener(this);
			gateway.setInboundMessageListener(this);

			// open and establish a connection with the modem
			gateway.open();
			gateway.init();

			// set service address to modem
			gateway.setSmsServiceAddress(service);

			// create a new outbound message
			OutboundMessage message = new OutboundMessage(text, number);

			// sending
			gateway.sendMessage(message);
			System.out.println("Message was send");
			System.out.println("Wainting for inbound messages");

		} catch (GatewayException | AtParseException e) {
			System.out.println(e.getMessage());
		} finally {

			// only pause a main thread until a key press
			Tool.pressEnterExit();

			if (gateway != null) {
				gateway.close();
			}
			System.out.println("Bye");
		}
	}

	/**
	 * Is called when a outbound message is delivered or expired time for
	 * delivering
	 */
	@Override
	public void outboundMessageEvent(OutboundMessageEvent event) {
		switch (event.getStatus()) {
		case SENT_ACK:
			System.out.println("The message was delivered to number : " + event.getMessage().getDestination());
			break;
		case EXPIRED:
			System.out.println("Time expired. " + event.getMessage().getDestination());
			break;
		}
	}

	/**
	 * Is called when a new inbound message is delivered
	 */
	@Override
	public void inboundMessageEvent(InboundMessageEvent inboundMessageEvent) {
		System.out.println("Received Message from '" + inboundMessageEvent.getMessage().getSource() + "', text: "
				+ inboundMessageEvent.getMessage().getText());
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
	 * @throws GatewayException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws GatewayException, IOException, InterruptedException {

		Tool.showHelp(args,
				String.format("Parameters:%n -p <port name> name of a serial port%n -h show this information%n"
						+ "-d <number> destination number%n -s <number> service number -t <text> text of message%n%n"
						+ "Interactive mode is activated when starting the program without parameters"));

		String port = Tool.selectionPort(args, SerialModem.getAvailablePorts());
		String number = Tool.destNumber(args);
		String text = Tool.text(args);
		String serviceNum = Tool.serviceNumer(args);

		System.out.println(String.format("%n--- Summary ---"));
		System.out.println("Destination phone number: " + number);
		System.out.println("Message text: " + text);
		System.out.println("Serial port: " + port);
		System.out.println("SMS servise number: " + serviceNum);

		Tool.pressEnter();
		Tool.activeLoggingToFile();

		new SendAndReceiveSMSExample().send(port, text, number, serviceNum);
	}

}
