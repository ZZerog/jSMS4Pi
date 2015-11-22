package cz.zerog.jsms4pi;

/*
 * #%L
 * jSMS4Pi
 * %%
 * Copyright (C) 2015 - 2016 jSMS4Pi
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

import cz.zerog.jsms4pi.at.AAT;
import cz.zerog.jsms4pi.exception.GatewayException;
import cz.zerog.jsms4pi.listener.gateway.GatewayStatusGatewayListener;
import cz.zerog.jsms4pi.listener.gateway.InboundCallGatewayListener;
import cz.zerog.jsms4pi.listener.gateway.InboundMessageGatewayListener;
import cz.zerog.jsms4pi.listener.gateway.NetworkCellGatewayListener;
import cz.zerog.jsms4pi.listener.gateway.NetworkStatusGatewayListener;
import cz.zerog.jsms4pi.listener.gateway.OutboundMessageGatewayListener;
import cz.zerog.jsms4pi.message.OutboundMessage;
import cz.zerog.jsms4pi.notification.Notification;

/**
 *
 * @author zerog
 */
public interface Gateway {

	public void open() throws GatewayException;

	public void close() throws GatewayException;

	public boolean init() throws GatewayException;
	//
	// public void setGlobalDeliveryReport(boolean report);
	//
	// public void setGlobalValidityPeriod(boolean period);

	public String getPortName();

	void notify(Notification notifi);

	public void setOutboundMessageListener(OutboundMessageGatewayListener listener);

	public void setInboundCallListener(InboundCallGatewayListener callListener);

	public void setInboundMessageListener(InboundMessageGatewayListener listener);

	public void setNetworkStatusListener(NetworkStatusGatewayListener networkStatusListener);

	public void setGatewayStatusListener(GatewayStatusGatewayListener gatewayListener);

	public void setNetworkCellListener(NetworkCellGatewayListener cellListener);

	public boolean isReadyToSend();

	public void sendMessage(OutboundMessage message) throws GatewayException;

	public boolean isAlive();

	public <T extends AAT> T directSendAtCommand(T cmd) throws GatewayException;

	public boolean isServiceAddressSet() throws GatewayException;

	public void setSmsServiceAddress(String readLine) throws GatewayException;

	/**
	 * Gateway status
	 */
	public enum Status {

		/*
		 * Serial port is closed
		 */
		CLOSED(0),
		/*
		 * Serial port is open
		 */
		OPENED(1),
		/*
		 * No PIN or accepted PIN
		 */
		SIM_OK(2),
		/*
		 * Modem is initialized
		 */
		INITIALIZED(3),
		/*
		 * Network is acceptable
		 */
		NETWORK_OK(4),
		/*
		 * Working, sending message
		 */
		BUSY(5),
		/*
		 * Ready for usage
		 */
		READY(6);

		private final int code;

		private Status(int code) {
			this.code = code;
		}

		public int getStatusCode() {
			return code;
		}
	}
}
