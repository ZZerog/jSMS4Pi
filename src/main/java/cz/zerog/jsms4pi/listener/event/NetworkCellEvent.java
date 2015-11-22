package cz.zerog.jsms4pi.listener.event;

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

public class NetworkCellEvent {

	final private int locationAreaCode;
	final private int cellID;

	public NetworkCellEvent(int locationAreaCode, int cellID) {
		this.locationAreaCode = locationAreaCode;
		this.cellID = cellID;
	}

	/**
	 * @return the locationAreaCode
	 */
	public int getLocationAreaCode() {
		return locationAreaCode;
	}

	/**
	 * @return the cellID
	 */
	public int getCellID() {
		return cellID;
	}

	/**
	 * @return the locationAreaCode as hex string
	 */
	public String getLocationAreaCodeHex() {
		return Integer.toHexString(locationAreaCode);
	}

	/**
	 * @return the cellID as hex string
	 */
	public String getCellIDHex() {
		return Integer.toHexString(cellID);
	}

}
