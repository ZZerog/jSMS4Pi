package cz.zerog.jsms4pi.exception;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import cz.zerog.jsms4pi.at.AAT;

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
public class AtParseException extends IllegalArgumentException {

	private final static String desc = "Cannot parse resut of AT. Problematic section in the pattern near index ";
	private final static String descP = "Pattern :  ";
	private final static String descR = "Response : ";

	private final String response;
	private final String pattern;

	public AtParseException(String response, Pattern pattern) {
		this.response = response;
		this.pattern = pattern.pattern();
	}

	@Override
	public String getMessage() {
		int index = calculatePatterIndex();
		StringBuilder sb = new StringBuilder();
		sb.append(desc);
		sb.append(index);
		sb.append(System.lineSeparator());
		sb.append(descR);
		sb.append('[');
		sb.append(AAT.crrt(response));
		sb.append(']');
		sb.append(System.lineSeparator());
		sb.append(descP);
		sb.append(pattern);
		sb.append(System.lineSeparator());
		for (int i = 1; i < index + descP.length(); i++) {
			sb.append(' ');
		}
		sb.append('^');

		return sb.toString();
	}

	private int calculatePatterIndex() {
		int pIndex = 1;

		Pattern patt = null;

		boolean match = false;
		while (true) {

			while (true) {
				try {
					if (pIndex > pattern.length()) {
						return pattern.length();
					}
					String pat = pattern.substring(0, pIndex);
					patt = Pattern.compile(pat);
				} catch (PatternSyntaxException e) {
					pIndex++;
					continue;
				}
				break;
			}

			for (int i = 1; i < response.length(); i++) {
				String newLine = response.substring(0, i);
				if (patt.matcher(newLine).matches()) {
					match = true;
				}
			}

			if (!match) {
				return pIndex;
			}

			match = false;
			pIndex++;
		}
	}

	public String getResponse() {
		return response;
	}

	public String getPattern() {
		return pattern;
	}

}
