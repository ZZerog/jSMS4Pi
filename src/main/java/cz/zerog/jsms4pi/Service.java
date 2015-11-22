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

import cz.zerog.jsms4pi.exception.GatewayException;
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
import cz.zerog.jsms4pi.listener.gateway.GatewayStatusGatewayListener;
import cz.zerog.jsms4pi.listener.gateway.InboundCallGatewayListener;
import cz.zerog.jsms4pi.listener.gateway.InboundMessageGatewayListener;
import cz.zerog.jsms4pi.listener.gateway.NetworkCellGatewayListener;
import cz.zerog.jsms4pi.listener.gateway.NetworkStatusGatewayListener;
import cz.zerog.jsms4pi.listener.gateway.OutboundMessageGatewayListener;
import cz.zerog.jsms4pi.message.OutboundMessage;

/**
 *
 * @author zerog
 */
public final class Service implements Runnable {

	private static class StaticHolder {
		static final Service INSTANCE = new Service();
	}

	public static Service getInstance() {
		return StaticHolder.INSTANCE;
	}

	private final Logger log = LogManager.getLogger();
	private final Thread serviceThread;

	private final Map<String, Gateway> gateways = new HashMap<String, Gateway>();
	private final List<OutboundMessage> waitingMessage = new ArrayList<OutboundMessage>();

	private List<InboundMessageListener> inboundMessListeners = new ArrayList<>();
	private List<OutboundMessageListener> outboundMessListeners = new ArrayList<>();
	private List<InboundCallListener> inboundCallListeners = new ArrayList<>();
	private List<NetworkStatusListener> networkStatusListeners = new ArrayList<>();
	private List<NetworkCellListener> networkCellListeners = new ArrayList<>();
	private List<GatewayStatusListener> gatewayStatusListeners = new ArrayList<>();

	private int TIMER = 10 * 1000;

	private Service() {

		serviceThread = new Thread(this);
		serviceThread.setName("Servise Thread");
		serviceThread.setDaemon(true);
		serviceThread.start();
	}

	public void addDefaultGateway(String portPath, String gatewayName) {
		this.addGateway(ATGateway.getDefaultFactory(portPath), gatewayName);
	}

	public void addDefaultGateway(String portname, int serialSpeed, int databit, int stopbit, int parity, int atTimeOut,
			String portPath, String gatewayName) {
		this.addGateway(ATGateway.getDefaultFactory(portname, serialSpeed, databit, stopbit, parity, atTimeOut),
				gatewayName);
	}

	public void addGateway(Gateway gateway, String gatewayName) {
		gateways.put(gatewayName, gateway);
		log.info("added gateway '{}' into service", gatewayName);
		gateway.setInboundMessageListener(new InboundMessageEventListenerImpl(gatewayName));
		gateway.setOutboundMessageListener(new OutboundMessageEventListenerImpl(gatewayName));
		gateway.setInboundCallListener(new InboundCallEventListenerImpl(gatewayName));
		gateway.setNetworkStatusListener(new NetworkStatusListenerImpl(gatewayName));
		gateway.setGatewayStatusListener(new GatewayStatusListenerImpl(gatewayName));
		gateway.setNetworkCellListener(new NetworkCellListenerImpl(gatewayName));
		serviceThread.interrupt();
	}

	public void removeGateway(Gateway gateway) {
		try {
			gateways.remove(gateway);
			gateway.close();
		} catch (Exception e) {
			log.warn(e, e);
		}
	}

	public void removeGateway(String gatewayName) {
		removeGateway(gateways.get(gatewayName));
	}

	public void removeAllGateway() {
		for (Gateway g : gateways.values()) {
			removeGateway(g);
		}
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

	public void addGatewayStatusListener(GatewayStatusListener listener) {
		this.gatewayStatusListeners.add(listener);
	}

	public void addNetworkCellListener(NetworkCellListener listener) {
		this.networkCellListeners.add(listener);
	}

	public void addNetworkStatusListener(NetworkStatusListener listener) {
		this.networkStatusListeners.add(listener);
	}

	private boolean restart(Gateway g) {
		log.info("RESTART");
		try {
			g.close();
			g.open();
			g.init();
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				if (waitingMessage.size() > 0) {
					nextMessage: for (OutboundMessage message : waitingMessage) {
						for (Gateway g : gateways.values()) {
							try {
								if (g.isReadyToSend()) {
									g.sendMessage(message);
									waitingMessage.remove(message);
									continue nextMessage;
								}
							} catch (GatewayException e) {
								g.close();
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

				Thread.sleep(TIMER);

			} catch (InterruptedException e) {

			} catch (Exception e) {
				log.warn(e, e);
			}
		}
	}

	/*
	 * Inner Class Section
	 */

	private class InboundCallEventListenerImpl extends Listener implements InboundCallGatewayListener {

		public InboundCallEventListenerImpl(String gatewayName) {
			super(gatewayName);
		}

		@Override
		public void inboundCallEvent(CallEvent callEvent) {
			for (InboundCallListener listener : inboundCallListeners) {
				listener.inboundCallEvent(super.gatewayName, callEvent);
			}
		}

	}

	private class OutboundMessageEventListenerImpl extends Listener implements OutboundMessageGatewayListener {

		public OutboundMessageEventListenerImpl(String gatewayName) {
			super(gatewayName);
		}

		@Override
		public void outboundMessageEvent(OutboundMessageEvent event) {
			for (OutboundMessageListener listener : outboundMessListeners) {
				listener.outboundMessageEvent(super.gatewayName, event);
			}

		}

	}

	private class InboundMessageEventListenerImpl extends Listener implements InboundMessageGatewayListener {

		public InboundMessageEventListenerImpl(String gatewayName) {
			super(gatewayName);
		}

		@Override
		public void inboundMessageEvent(InboundMessageEvent inboundMessageEvent) {
			for (InboundMessageListener listener : inboundMessListeners) {
				listener.inboundMessageEvent(super.gatewayName, inboundMessageEvent);
			}
		}
	}

	private class NetworkStatusListenerImpl extends Listener implements NetworkStatusGatewayListener {

		public NetworkStatusListenerImpl(String gatewayName) {
			super(gatewayName);
		}

		@Override
		public void networkStatusEvent(NetworkStatusEvent networkStatusEvent) {
			for (NetworkStatusListener listener : networkStatusListeners) {
				listener.networkStatusEvent(super.gatewayName, networkStatusEvent);
			}
		}
	}

	private class GatewayStatusListenerImpl extends Listener implements GatewayStatusGatewayListener {

		public GatewayStatusListenerImpl(String gatewayName) {
			super(gatewayName);
		}

		@Override
		public void gatewayStatusEvent(GatewayStatusEvent status) {
			for (GatewayStatusListener listener : gatewayStatusListeners) {
				listener.gatewayStatusEvent(super.gatewayName, status);
			}
		}
	}

	private class NetworkCellListenerImpl extends Listener implements NetworkCellGatewayListener {

		public NetworkCellListenerImpl(String gatewayName) {
			super(gatewayName);
		}

		@Override
		public void networkStatusEvent(NetworkCellEvent event) {
			for (NetworkCellListener listener : networkCellListeners) {
				listener.networkCellEvent(super.gatewayName, event);
			}
		}
	}

	private abstract class Listener {

		final private String gatewayName;

		public Listener(String gatewayName) {
			this.gatewayName = gatewayName;
		}
	}
}
