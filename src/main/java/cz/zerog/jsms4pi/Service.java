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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.zerog.jsms4pi.event.CallEvent;
import cz.zerog.jsms4pi.event.InboundMessageEvent;
import cz.zerog.jsms4pi.event.OutboundMessageEvent;
import cz.zerog.jsms4pi.listener.InboundCallGatewayListener;
import cz.zerog.jsms4pi.listener.InboundCallListener;
import cz.zerog.jsms4pi.listener.InboundMessageGatewayListener;
import cz.zerog.jsms4pi.listener.InboundMessageListener;
import cz.zerog.jsms4pi.listener.OutboundMessageGatewayListener;
import cz.zerog.jsms4pi.listener.OutboundMessageListener;
import cz.zerog.jsms4pi.message.OutboundMessage;

/**
 *
 * @author zerog
 */
public final class Service implements Runnable {

	private static final Service single = new Service();
	private final Logger log = LogManager.getLogger();
	private final Thread serviceThread;

	private final Map<String, Gateway> gateways = new HashMap<String, Gateway>();
	private final List<OutboundMessage> waitingMessage = new ArrayList<OutboundMessage>();

	private List<InboundMessageListener> inboundMessListeners = new ArrayList<>();
	private List<OutboundMessageListener> outboundMessListeners = new ArrayList<>();
	private List<InboundCallListener> inboundCallListeners = new ArrayList<>();

	private int TIMER = 10 * 1000;

	private Service() {
		serviceThread = new Thread(this);
		serviceThread.setName("Servise Thread");
		serviceThread.setDaemon(true);
		serviceThread.start();
	}

	public static Service getInstance() {
		return single;
	}

	public void addDefaultGateway(String portPath, String name) {
		this.addGateway(new ATGateway(portPath), name);
	}

	public void addGateway(Gateway gateway, String name) {
		gateways.put(name, gateway);
		log.info("added gateway '{}' into service", name);
		gateway.setInboundMessageListener(new InboundMessageEventListenerImpl(name));
		gateway.setOutboundMessageListener(new OutboundMessageEventListenerImpl(name));
		gateway.setInboundCallListener(new InboundCallEventListenerImpl(name));

		serviceThread.interrupt();
	}

	public void getGateway(String name) {
		gateways.get(name);
	}

	public void sendMessage(OutboundMessage message) {
		waitingMessage.add(message);
		serviceThread.interrupt();
	}

	public void addInboundMessageListener(InboundMessageListener listener) {
		this.inboundMessListeners.add(listener);
	}

	public void addOutboundMessageListener(OutboundMessageListener listener) {
		this.outboundMessListeners.add(listener);
	}

	public void addInboundCallListener(InboundCallListener listener) {
		this.inboundCallListeners.add(listener);
	}

	private boolean restart(Gateway g) {
		log.info("RESTART");
		try {
			g.close();
			g.open();
			g.init();
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				if (waitingMessage.size() > 0) {
					for (OutboundMessage message : waitingMessage) {
						for (Gateway g : gateways.values()) {
							if (g.isReadyToSend()) {
								g.sendMessage(message);
								waitingMessage.remove(message);
							}
						}
					}
				}

				for (Gateway g : gateways.values()) {
					if (!g.isReadyToSend()) {
						restart(g);
					} else {
						if (!g.isAlive()) {
							restart(g);
						}
					}
				}

				try {
					Thread.sleep(TIMER);
				} catch (InterruptedException ex) {
					// ready
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class InboundCallEventListenerImpl implements InboundCallGatewayListener {

		final private String gatewayName;

		public InboundCallEventListenerImpl(String gatewayName) {
			this.gatewayName = gatewayName;
		}

		@Override
		public void inboundCallEvent(CallEvent callEvent) {
			for (InboundCallListener listener : inboundCallListeners) {
				listener.inboundCallEvent(gatewayName, callEvent);
			}
		}

	}

	private class OutboundMessageEventListenerImpl implements OutboundMessageGatewayListener {

		final private String gatewayName;

		public OutboundMessageEventListenerImpl(String gatewayName) {
			this.gatewayName = gatewayName;
		}

		@Override
		public void outboundMessageEvent(OutboundMessageEvent event) {
			for (OutboundMessageListener listener : outboundMessListeners) {
				listener.outboundMessageEvent(gatewayName, event);
			}

		}

	}

	private class InboundMessageEventListenerImpl implements InboundMessageGatewayListener {

		final private String gatewayName;

		public InboundMessageEventListenerImpl(String gatewayName) {
			this.gatewayName = gatewayName;
		}

		@Override
		public void inboundMessageEvent(InboundMessageEvent inboundMessageEvent) {
			for (InboundMessageListener listener : inboundMessListeners) {
				listener.inboundMessageEvent(gatewayName, inboundMessageEvent);
			}
		}
	}
}
