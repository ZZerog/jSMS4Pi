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
 * Enter PIN.
 * 
 * 127007 (8.3)
 *
 * @author zerog
 */
public class CPIN extends AAT {

    public static final String NAME = "+CPIN";
    
    private final int pin, newPin;

    public CPIN(int pin, int newPin) {
        super(NAME);
        this.pin = pin;
        this.newPin = newPin;
    }    
    
    public CPIN(int pin) {
        this(pin, -1);
    }

    @Override
    public String getCommandRequest() {
        StringBuilder at = new StringBuilder(getName());
        at.append("=").append(pin);
        if (newPin > 0 ) {            
            at.append(",").append(newPin);
        }   
        at.append(AAT.CR);
        return at.toString();
    }
    
    
}
