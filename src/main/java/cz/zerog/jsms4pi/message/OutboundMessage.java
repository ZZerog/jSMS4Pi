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

	private Status status = Status.NOT_SENT;

	private int index;
	private final String destination;

	private boolean deliveryReport;
	private boolean validityPeriod;

	public OutboundMessage(String text, String destination, boolean deliveryReport) {
		this(text, destination, deliveryReport, false);
	}

	public OutboundMessage(String text, String destination, boolean deliveryReport, boolean validityPeriod) {
		super(MessageTypes.OUTBOUND, text);
		this.destination = destination;
		this.deliveryReport = deliveryReport;
		this.validityPeriod = validityPeriod;
	}

	/**
	 * Create new outbound text message.
	 * 
	 * @param text
	 *            text of messsage
	 * @param destination
	 *            destination phone number.
	 */
	public OutboundMessage(String text, String destination) {
		this(text, destination, false, false);
	}

	public enum Status {
		/**
		 * The message wasn't sent. The message is waiting for a GSM signal.
		 */
		NOT_SENT_NO_SIGNAL,

		/**
		 * The default status. The message wasn't sent yet.
		 */
		NOT_SENT,

		/**
		 * The message was sent but without delivery receipt.
		 */
		SENT_NOT_ACK,

		/**
		 * The message was sent with delivery receipt.
		 */
		SENT_ACK,

		/**
		 * The message wasn't delivered because the time for delivery has
		 * expired.
		 */
		EXPIRED;
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

	public String getDestination() {
		return destination;
	}
}
