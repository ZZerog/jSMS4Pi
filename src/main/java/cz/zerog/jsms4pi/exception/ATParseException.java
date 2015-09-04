package cz.zerog.jsms4pi.exception;

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
public class ATParseException extends RuntimeException {
    
    private final static String PARSE= "Cannot parse resut of AT: ";
    
    public ATParseException(String name, String result) {
        super(PARSE+name+". \nParsing string: '"+result+"'");
    }

    public ATParseException(String message, Throwable t) {
        super(message, t);
    }    
    
}