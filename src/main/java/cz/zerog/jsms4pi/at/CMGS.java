/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 * Send SMS Command in Text Mode
 * 
 * @author zerog
 */
public class CMGS extends AAT {

	public static String NAME = "+CMGS";

	private final String destination;

	public CMGS(String destinationNumber) {
		super(NAME);
		this.destination = destinationNumber;
	}

	@Override
	public String getRequest() {
		return getName() + "=\"" + destination + "\"" + AAT.CR;
	}

	@Override
	protected boolean isComplete() {
		if (response.indexOf(">") >= 0) {
			status = Status.OK;
			return true;
		}
		return false;
	}

}
