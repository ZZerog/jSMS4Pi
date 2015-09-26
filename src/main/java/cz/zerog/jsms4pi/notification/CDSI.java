package cz.zerog.jsms4pi.notification;

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


import cz.zerog.jsms4pi.tool.TypeOfMemory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SMS status notification
 * 
 * @author zerog
 */
public final class CDSI implements Notification {

    private final static Pattern pattern = Pattern.compile("\\+CDSI:( *)\"([A-Z]{2})\",(\\d+)");

    private final TypeOfMemory memory;
    private final int index;
    private final String response;
    
    public CDSI(Matcher matcher, String response) {
        memory = TypeOfMemory.valueOf(matcher.group(2));
        index = Integer.parseInt(matcher.group(3));
        this.response = response;
    }

    public TypeOfMemory getMemoryType() {
        return memory;
    }

    public int getSMSIndex() {
        return index;
    }

    public static CDSI tryParse(String notification) {
        Matcher matcher = pattern.matcher(notification);
        if (matcher.matches()) {
            return new CDSI(matcher, notification);
        }
        return null;
    }

    @Override
    public String getResponse() {
        return response;
    }
}
