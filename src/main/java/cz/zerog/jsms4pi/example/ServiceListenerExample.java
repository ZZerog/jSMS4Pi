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
import cz.zerog.jsms4pi.listener.GatewayStatusListener;
import cz.zerog.jsms4pi.listener.InboundCallListener;
import cz.zerog.jsms4pi.listener.InboundMessageListener;
import cz.zerog.jsms4pi.listener.NetworkCellListener;
import cz.zerog.jsms4pi.listener.NetworkStatusListener;
import cz.zerog.jsms4pi.listener.OutboundMessageListener;
import cz.zerog.jsms4pi.listener.event.CallEvent;
import cz.zerog.jsms4pi.listener.event.GatewayStatusEvent;
import cz.zerog.jsms4pi.listener.event.InboundMessageEvent;
import cz.zerog.jsms4pi.listener.event.NetworkCellEvent;
import cz.zerog.jsms4pi.listener.event.NetworkStatusEvent;
import cz.zerog.jsms4pi.listener.event.OutboundMessageEvent;

/**
 * This example demonstrates how use a Service and its listeners.
 *
 * @author zerog
 */
public class ServiceListenerExample implements InboundCallListener, NetworkStatusListener, GatewayStatusListener,
		InboundMessageListener, OutboundMessageListener, NetworkCellListener {

	private final static String VERSION = "ServiceListenerExample 1.0";

	public ServiceListenerExample(String port) throws IOException {

		// get service
		Service service = Service.getInstance();

		// register all listeners
		service.addInboundCallListener(this);
		service.addGatewayStatusListener(this);
		service.addInboundMessageListener(this);
		service.addOutboundMessageListener(this);
		service.addNetworkCellListener(this);
		service.addNetworkStatusListener(this);

		// add a gateway into service, named 'fooGateway'
		service.addDefaultGateway(port, "fooGateway");

		// only pause a main thread until a key press
		Tool.pressEnterExit();

		// remove all gateways from service
		service.removeAllGateway();
	}

	/**
	 * Is called when a inbound call is detected
	 */
	@Override
	public void inboundCallEvent(String gatewayName, CallEvent callEvent) {
		System.out
				.println("Detected a call '" + callEvent.getCallerId() + "' by number. Gateway: '" + gatewayName + "'");
	}

	/**
	 * Is called when a inbound call is detected. Print a source number and
	 * gateway.
	 */
	@Override
	public void networkStatusEvent(String gatewayName, NetworkStatusEvent networkStatusEvent) {
		System.out.println("Network Status changed to state '" + networkStatusEvent.getNetworkStatus() + "'. Gateway: '"
				+ gatewayName + "'");
	}

	/**
	 * Is called when a gateway status changed
	 */
	@Override
	public void gatewayStatusEvent(String gatewayName, GatewayStatusEvent status) {
		System.out.println("Gateway '" + gatewayName + "' changed status to '" + status.getStatus() + "'");
	}

	/**
	 * Is called when a gateway is registered into new network cell
	 */
	@Override
	public void networkCellEvent(String gatewayName, NetworkCellEvent networkCellEvent) {
		System.out.println("The gateway '" + gatewayName + "' is registered into a new network cell");
		System.out.println("Location Area Code: " + networkCellEvent.getLocationAreaCodeHex());
		System.out.println("Cell ID: " + networkCellEvent.getCellIDHex());
	}

	/**
	 * Is called when a outbound message is delivered or expired time for
	 * delivering, etc
	 */
	@Override
	public void outboundMessageEvent(String gatewayName, OutboundMessageEvent event) {
		switch (event.getStatus()) {
		case SENT_ACK:
			System.out.println("The message was delivered to number: '" + event.getMessage().getDestination() + "'");
			break;
		case EXPIRED:
			System.out.println("The message isn't delivered. Time for delivering expired. Dest. num.: '"
					+ event.getMessage().getDestination() + "'");
			break;
		case NOT_SENT:
			System.out.println("The message isn't sent yet");
			break;
		case NOT_SENT_NO_SIGNAL:
			System.out.println("The message is a waiting for a GSM signal");
			break;
		case SENT_NOT_ACK:
			System.out.println("The message was sent");
			break;
		default:
			break;
		}
	}

	/**
	 * Is called when a new inbound message is delivered
	 */
	@Override
	public void inboundMessageEvent(String gatewayName, InboundMessageEvent inboundMessageEvent) {
		System.out.println("Received a message from '" + inboundMessageEvent.getMessage().getSource() + "'");
		System.out.println(", text: " + inboundMessageEvent.getMessage().getText() + "'");
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
				String.format("ServiceListenerExample demonstrates how to use the Service class and Listeners.%n%n"
						+ "Arguments:%n -p <port name>		Name of a serial port%n "
						+ "-h 			Print Help (this message) and exit%n"
						+ " -version 		Print version information and exit%n"
						+ "%n%nInteractive mode is activated when starting the program without parameters"));

		Tool.showVersion(args, VERSION);

		String port = Tool.selectionPort(args);

		System.out.println(String.format("%n--- Summary ---"));
		System.out.println("Serial port: " + port);

		Tool.pressEnter();

		new ServiceListenerExample(port);
	}
}
