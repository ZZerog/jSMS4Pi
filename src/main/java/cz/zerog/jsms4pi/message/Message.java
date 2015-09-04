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
public class Message {

    private String source;
    private final String destination;
    private final String text;
    
    private MessageTypes type;

    private int textLength = 0;

    /**
     * Enumeration representing the different types of messages.
     */
    public enum MessageTypes {

        /**
         * Inbound message.
         */
        INBOUND,
        /**
         * Outbound message.
         */
        OUTBOUND
    }
    
    public Message(MessageTypes type, String text, String destination) {
        this.type = type;
        this.text = text;
        this.destination = destination;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getText() {
        return text;
    }

    public MessageTypes getType() {
        return type;
    }

    public int getTextLength() {
        return textLength;
    }
    
    
}
