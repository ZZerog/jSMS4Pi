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


import cz.zerog.jsms4pi.at.CPMS.TypeOfMemory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author zerog
 */
public final class CDSI extends Notification {

    private final Pattern pattern = Pattern.compile("\\+CDSI:( *)\"([A-Z]{2})\",(\\d+)");

    private TypeOfMemory memory;
    private int index;

    public TypeOfMemory getMemoryType() {
        return memory;
    }

    public int getSMSIndex() {
        return index;
    }

    @Override
    protected void parse(String notification) {
        Matcher matcher = pattern.matcher(notification);
        if (matcher.find()) {
            memory = TypeOfMemory.valueOf(matcher.group(2));
            index = Integer.parseInt(matcher.group(3));
        }
    }
}
