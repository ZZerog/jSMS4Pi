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
 * Select a character set.
 * 
 * @author zerog
 */
public class CSCS extends AAT{

    public static final String NAME = "+CSCS";
    
    private String charset;
    
    public CSCS(String charset) {
        super(NAME);
        this.charset = charset;
    }
    
    @Override
    public String getCommandRequest() {
        return getName() + "=\"" + charset + "\"" + AAT.CR;
    }    
    
}
