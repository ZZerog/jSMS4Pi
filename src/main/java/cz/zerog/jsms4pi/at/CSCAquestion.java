package cz.zerog.jsms4pi.at;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import cz.zerog.jsms4pi.at.CSCA.NumberType;
import cz.zerog.jsms4pi.exception.AtParseException;

/**
 * Reading Service Center Address
 *
 * @author zerog
 */
public class CSCAquestion extends AAT {

	private NumberType type;
	private String address = "";

	private final Pattern pattern = Pattern.compile("\\+CSCA:( *)\"(.*)\",(145|129|)\\s*");

	public CSCAquestion() {
		super(CSCA.NAME, Mode.QUESTION);
	}

	@Override
	protected void parseQuestionResult(String response) {
		Matcher matcher = pattern.matcher(response);
		if (!matcher.matches()) {
			throw new AtParseException(response, pattern);
		}
		address = matcher.group(2);
		String sType = matcher.group(3);
		if (!sType.trim().equals("")) {
			type = NumberType.valueOf(Integer.parseInt(sType));
		}

	}

	public String getAddress() {
		return address;
	}

	public NumberType getType() {
		return type;
	}
}
