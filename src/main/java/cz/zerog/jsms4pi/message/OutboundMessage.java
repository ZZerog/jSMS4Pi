package cz.zerog.jsms4pi.message;

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

/**
 *
 * @author zerog
 */
public class OutboundMessage extends Message {

    private Status status = Status.NOT_SEND;
    
    private int index;
    
    private boolean deliveryReport;
    private boolean validityPeriod;
    
    public OutboundMessage(String text, String destination, boolean deliveryReport) {
        this(text, destination, deliveryReport, false);
    }
    
    public OutboundMessage(String text, String destination, boolean deliveryReport, boolean validityPeriod) {
        super(MessageTypes.OUTBOUND, text, destination);
        this.deliveryReport = deliveryReport;
        this.validityPeriod = validityPeriod;
    }    
    
    public OutboundMessage(String text, String destination) {
        this(text, destination, false, false);
    }
    
    public enum Status {
        NOT_SEND_NO_SIGNAL, //cakem na GSM signal
        NOT_SEND, //sprava nebyla odeslana
        SENDED_NOT_ACK, // odesla v poradku
        SENDED_ACK, //odeslana a potvrzena
        EXPIRED, //vyprsel cas ktery byl stanoven na doruceni
        ;
    }
    
    public void setIndex(int index) {
        this.index = index;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public int getIndex() {
        return index;
    }

    public boolean isDeliveryReport() {
        return deliveryReport;
    }

    public boolean isValidityPeriod() {
        return validityPeriod;
    }
    
    
}
