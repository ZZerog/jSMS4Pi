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

import cz.zerog.jsms4pi.event.InboundMessageEvent;
import cz.zerog.jsms4pi.exception.GatewayException;
import cz.zerog.jsms4pi.listener.InboundCallGatewayListener;
import cz.zerog.jsms4pi.listener.InboundMessageGatewayListener;
import cz.zerog.jsms4pi.listener.OutboundMessageGatewayListener;
import cz.zerog.jsms4pi.message.InboundMessage;
import cz.zerog.jsms4pi.message.OutboundMessage;
import cz.zerog.jsms4pi.notification.Notification;

/**
 *
 * @author zerog
 */
public class NullGateway extends Thread implements Gateway {

    private final Logger log = LogManager.getLogger();

    InboundMessageGatewayListener listener;
    String source;
    int timeout;

    public NullGateway(String source, int timeout) {
        this.timeout = timeout;
        this.source = source;
        this.start();
    }

    public NullGateway() {

    }

    @Override
    public void open() throws Exception {
        log.info("open");
    }

    @Override
    public void close() throws Exception {
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
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void setInboundMessageListener(InboundMessageGatewayListener listener) {
        System.out.println("setIn");
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            sleep(timeout * 1000);
            System.out.println("go");
            listener.inboundMessageEvent(new InboundMessageEvent(new InboundMessage("hoj", source)));
        } catch (InterruptedException ex) {

        }
    }

    @Override
    public boolean isReadyToSend() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendMessage(OutboundMessage message) throws GatewayException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

	@Override
	public void setOutboundMessageListener(OutboundMessageGatewayListener listener) {
		// TODO Auto-generated method stub
		
	}

}
