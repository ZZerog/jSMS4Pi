package cz.zerog.jsms4pi.at;

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
import static cz.zerog.jsms4pi.tool.PatternTool.NUMBER;
import static cz.zerog.jsms4pi.tool.PatternTool.WHATEVER;
import static cz.zerog.jsms4pi.tool.PatternTool.build;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.zerog.jsms4pi.exception.AtParseException;

/**
 * Get Operator Name
 *
 * @author zerog
 */
public class COPSquestion extends AAT {

	private int mode;
	private int format;
	private String oper;
	private int act;

	private static String NAME = "COPS";

	private final Pattern pattern = Pattern
			.compile(build("\\+COPS: *({}),({}),\"({})\",({})\\s*", NUMBER, NUMBER, WHATEVER, NUMBER));

	public COPSquestion() {
		super(NAME, Mode.QUESTION);
	}

	@Override
	protected void parseQuestionResult(String response) {
		Matcher matcher = pattern.matcher(response);
		if (!matcher.matches()) {
			throw new AtParseException(response, pattern);
		}
		mode = Integer.parseInt(matcher.group(1));
		format = Integer.parseInt(matcher.group(2));
		oper = matcher.group(3);
		act = Integer.parseInt(matcher.group(4));

	}

	public int getMod() {
		return mode;
	}

	public int getFormat() {
		return format;
	}

	public String getOperatorName() {
		return oper;
	}

	public int getAct() {
		return act;
	}
}
