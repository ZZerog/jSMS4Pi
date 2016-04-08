package cz.zerog.jsms4pi.exception;

/*
 * #%L
 * jSMS4Pi
 * %%
 * Copyright (C) 2015 - 2016 jSMS4Pi
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

public enum CmsError {

	CME_0(0, "Phone failure"),
	CME_321(321, "Invalid memory index"),
	CME_500(500, "Unknown error");

	private final int number;
	private final String text;

	private CmsError(int number, String text) {
		this.number = number;
		this.text = text;
	}

	/**
	 * @return the number
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	public static CmsError valueOf(int error) {

		for (CmsError cms : CmsError.values()) {
			if (cms.getNumber() == error) {
				return cms;
			}
		}

		throw new IllegalArgumentException("Unknow CMS error number '" + error + "'");
	}
}
