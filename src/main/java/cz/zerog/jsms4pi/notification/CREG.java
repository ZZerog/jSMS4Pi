package cz.zerog.jsms4pi.notification;

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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.zerog.jsms4pi.at.CREGquestion.NetworkStatus;

/**
 *
 * @author zerog
 */
public final class CREG implements Notification {

	// RING +CLIP: "+420739474009",145,,,,0
	//
	private final static Pattern pattern = Pattern.compile("\\+CREG: *(\\d+), *([0-9a-zA-Z]*), *([0-9a-zA-Z]*)");

	private final NetworkStatus networkStatus;
	private final int lac;
	private final int ci;

	private final String response;

	private CREG(Matcher matcher, String response) {
		networkStatus = NetworkStatus.valueOf(Integer.parseInt(matcher.group(1)));
		lac = Integer.parseInt(matcher.group(2), 16);
		ci = Integer.parseInt(matcher.group(3), 16);
		this.response = response;
	}

	@Override
	public String getResponse() {
		return response;
	}

	public static CREG tryParse(String notification) {
		Matcher matcher = pattern.matcher(notification);
		if (matcher.matches()) {
			return new CREG(matcher, notification);
		}
		return null;
	}

	public NetworkStatus getNetworkStatus() {
		return networkStatus;
	}

	public int getLac() {
		return lac;
	}

	public int getCi() {
		return ci;
	}

	public boolean useSMS() {
		return networkStatus.getCode() == 1 || networkStatus.getCode() == 5 || networkStatus.getCode() == 6
				|| networkStatus.getCode() == 7;
	}
}
