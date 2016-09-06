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

import static cz.zerog.jsms4pi.tool.PatternTool.*;

import java.time.OffsetDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.zerog.jsms4pi.tool.PatternTool;
import cz.zerog.jsms4pi.tool.PhoneType;
import cz.zerog.jsms4pi.tool.SPStatus;

/**
 * SMS status notification
 * 
 * @author zerog
 */
public final class CDS implements Notification {

	private final static Pattern pattern = Pattern
			.compile(build("\\+CDS: *({}),({}),\"({})\",({}),\"({})\",\"({})\",({})", NUMBER, // fo
					NUMBER, // mr
					PHONE_NUMBER, // ra
					PHONE_TYPE, // tora
					TIME_STAMP, // scts
					TIME_STAMP, // dt
					NUMBER)); // st (status)

	private final int fo;
	private final int mr;
	private final String ra;
	private final PhoneType tora;
	private final OffsetDateTime scts, dt;
	private final SPStatus status;

	private final String response;

	public CDS(Matcher matcher, String response) {
		fo = Integer.valueOf(matcher.group(1));
		mr = Integer.valueOf(matcher.group(2));
		ra = matcher.group(3);
		tora = PhoneType.valueOf(Integer.parseInt(matcher.group(4)));
		scts = PatternTool.getOffsetDateTime(matcher.group(5));
		dt = PatternTool.getOffsetDateTime(matcher.group(6));
		status = SPStatus.valueOf(Integer.parseInt(matcher.group(7)));
		this.response = response;
	}

	public static CDS tryParse(String notification, UnknownNotifications notifications) {
		Matcher matcher = pattern.matcher(notification);

		if (matcher.matches()) {
			return new CDS(matcher, notification);
		}
		// throw new AtParseException(notification, pattern);
		return null;
	}

	public static Pattern getPattern() {
		return pattern;
	}

	public int getFo() {
		return fo;
	}

	public int getMr() {
		return mr;
	}

	public String getRa() {
		return ra;
	}

	public PhoneType getTora() {
		return tora;
	}

	public OffsetDateTime getScts() {
		return scts;
	}

	public OffsetDateTime getDt() {
		return dt;
	}

	public SPStatus getStatus() {
		return status;
	}

	@Override
	public String getResponse() {
		return response;
	}
}
