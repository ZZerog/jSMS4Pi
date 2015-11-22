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

/**
 * Set text mode or PDU mode
 *
 * @author zerog
 */
public class CMGF extends AAT {

	public static final String NAME = "+CMGF";

	private Mode mode = Mode.TEXT;

	/**
	 * Default type is text mode
	 * 
	 * @param mode
	 */
	public CMGF(Mode mode) {
		super(NAME);
		this.mode = mode;
	}

	@Override
	public String getCommandRequest() {
		return getName() + "=" + mode.getCode() + AAT.CR;
	}

	public Mode getCMGFMode() {
		return mode;
	}

	public enum Mode {

		TEXT(1),
		PDU(0);

		int code;

		private Mode(int type) {
			this.code = type;
		}

		public int getCode() {
			return code;
		}

		public static Mode valueOf(int code) {
			switch (code) {
			case 0:
				return Mode.PDU;
			case 1:
				return Mode.TEXT;
			}
			throw new IllegalStateException("Illegal mode. Accept 0 to 1 including.");
		}
	}
}
