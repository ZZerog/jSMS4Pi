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

import cz.zerog.jsms4pi.event.InboundMessageEvent;
import cz.zerog.jsms4pi.event.InboundMessageEventListener;
import cz.zerog.jsms4pi.event.InboundMessageGatewayEventListener;
import cz.zerog.jsms4pi.message.OutboundMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author zerog
 */
public final class Service implements Runnable {

    private static final Service single = new Service();
    private final Logger log = LogManager.getLogger();
    private final Thread serviceThread;

    private Service() {
        serviceThread = new Thread(this);
        serviceThread.setName("Servise Thread");
        serviceThread.start();
    }

    private final Map<String, Gateway> gateways = new HashMap();

    public static Service getInstance() {
        return single;
    }

    private InboundMessageGatewayEventListener inMessGatewayEventListener;
    private final List<OutboundMessage> outMess = new ArrayList();

    private int TIMER = 10 * 1000;

    public void addGateway(Gateway gateway, String name) {
        gateways.put(name, gateway);
        log.info("added gateway '{}' into service", name);
        gateway.setInboundMessageListener(new InboundMessageGatewayEventListenerImpl(name));
        serviceThread.interrupt();
    }

    public void getGateway(String name) {
        gateways.get(name);
    }

    public void sendMessage(OutboundMessage message) {
        outMess.add(message);
    }

    public void sendMessage(String gatewayName, OutboundMessage message) {

    }

    public void setInboundMessageGatewayEventListener(InboundMessageGatewayEventListener listener) {
        this.inMessGatewayEventListener = listener;
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
                
                log.info("s start");

                if (outMess.size() > 0) {
                    for (OutboundMessage message : outMess) {
                        for (Gateway g : gateways.values()) {
                            if (g.isReadyToSend()) {
                                g.sendMessage(message);
                                //TODO delete message!
                            }
                        }
                    }
                }

                for (Gateway g : gateways.values()) {
                    if (!g.isReadyToSend()) {
                        log.info("G is not ready");
                        restart(g);
                    } else {
                        if (!g.isAlive()) {
                            log.info("NO Alive");
                            restart(g);
                        }
                    }
                }

                try {
                    Thread.sleep(TIMER);
                } catch (InterruptedException ex) {
                    //ready
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class InboundMessageGatewayEventListenerImpl implements InboundMessageEventListener {

        final private String name;

        public InboundMessageGatewayEventListenerImpl(String gatewayName) {
            this.name = gatewayName;
        }

        @Override
        public void inboundMessageEvent(InboundMessageEvent inboundMessageEvent) {
            System.out.println("buf");
            if (inMessGatewayEventListener != null) {
                inMessGatewayEventListener.inboundMessageEvent(name, inboundMessageEvent);
            }
        }
    }
}
