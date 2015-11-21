package cz.zerog.jsms4pi.exception;

import jssc.SerialPortException;

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
public class GatewayException extends Exception {

	public static final String SERVISE_READ_ERR = "Cannot read message servis number";
	public static final String RESPONSE_EXPIRED = "Response timeout expired";
	public static final String GATEWAY_CLOSED = "Gateway is closed";
	public static final String GATEWAY_NOT_READY = "Gateway is not ready";

	private String port;

	public GatewayException(SerialPortException cause) {
		super(cause);
		this.port = cause.getPortName();
	}

	public GatewayException(String message, String port) {
		super(message + ". Port: " + port);
		this.port = port;
	}

	public String getPortName() {
		return port;
	}
}
