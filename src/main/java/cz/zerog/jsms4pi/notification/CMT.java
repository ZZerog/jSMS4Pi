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

/**
 * SMS status notification
 * 
 * @author zerog
 */
public final class CMT implements Notification {

	private final static Pattern pattern = Pattern.compile(build("\\+CMT: *\"({})\",({}),\"({})\"", PHONE_NUMBER, // oa
			WHATEVER, // alpha
			TIME_STAMP)); // scts

	private final String oa; // source phone number
	private final String alpha; // if oa is in phone book
	private final OffsetDateTime scts; //
	private final String data; // text

	private final String response;

	public CMT(Matcher matcher, String response, String text) {
		oa = matcher.group(1);
		alpha = matcher.group(2);
		scts = PatternTool.getOffsetDateTime(matcher.group(3));
		data = text;
		this.response = response + "\r\n" + text;
	}

	public static CMT tryParse(String notification, UnknownNotifications notifications) {
		Matcher matcher = pattern.matcher(notification);
		if (matcher.matches()) {
			return new CMT(matcher, notification, notifications.getNextMessage());
		}
		return null;
	}

	@Override
	public String getResponse() {
		return response;
	}

	public String getOa() {
		return oa;
	}

	public String getAlpha() {
		return alpha;
	}

	public OffsetDateTime getScts() {
		return scts;
	}

	public String getData() {
		return data;
	}
}
