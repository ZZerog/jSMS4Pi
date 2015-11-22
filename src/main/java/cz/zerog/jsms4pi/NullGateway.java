package cz.zerog.jsms4pi;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
public class NullGateway extends Thread implements Gateway {

	private final Logger log = LogManager.getLogger();

	public NullGateway() {

	}

	@Override
	public void open() throws GatewayException {
		log.info("open");
	}

	@Override
	public void close() throws GatewayException {
		log.info("close");
	}

	@Override
	public boolean init() throws GatewayException {
		log.info("init");
		return true;
	}

	@Override
	public String getPortName() {
		log.info("get port name");
		return "";
	}

	@Override
	public void notify(Notification notifi) {
		log.info("notify");
	}

	@Override
	public void setInboundCallListener(InboundCallGatewayListener callListener) {

	}

	@Override
	public void setInboundMessageListener(InboundMessageGatewayListener listener) {
		System.out.println("setIn");

	}

	@Override
	public boolean isReadyToSend() {
		return false;
	}

	@Override
	public void sendMessage(OutboundMessage message) throws GatewayException {

	}

	@Override
	public void setOutboundMessageListener(OutboundMessageGatewayListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNetworkStatusListener(NetworkStatusGatewayListener networkStatusListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGatewayStatusListener(GatewayStatusGatewayListener gatewayListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNetworkCellListener(NetworkCellGatewayListener cellListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends AAT> T directSendAtCommand(T cmd) throws GatewayException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isServiceAddressSet() throws GatewayException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setSmsServiceAddress(String readLine) throws GatewayException {
		// TODO Auto-generated method stub

	}

}
