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


import cz.zerog.jsms4pi.tool.TypeOfMemory;
import cz.zerog.jsms4pi.exception.AtParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Preferred Message Storage (Question).
 * CPMS?
 *
 * @author zerog
 */
public class CPMSquestion extends AAT {

    private final Pattern pattern = Pattern.compile("CPMS:( *)\"([A-Z]{2})\",(\\d+),(\\d+),\"([A-Z]{2})\",(\\d+),(\\d+),\"([A-Z]{2})\",(\\d+),(\\d+)\\s*");

    private TypeOfMemory memory1;
    private TypeOfMemory memory2;
    private TypeOfMemory memory3;

    private int used1;
    private int used2;
    private int used3;

    private int max1;
    private int max2;
    private int max3;

    public CPMSquestion() {
        super(CPMS.NAME, Mode.QUESTION);
    }

    /**
     *
     * @param response
     */
    @Override
    protected void parseQuestionResult(String response) {
        Matcher matcher = pattern.matcher(response);
        if (!matcher.matches()) {
            throwExceptionInMainThread(new AtParseException(response, pattern));
            return;
        }
        
        memory1 = TypeOfMemory.valueOf(matcher.group(2));
        used1 = Integer.parseInt(matcher.group(3));
        max1 = Integer.parseInt(matcher.group(4));

        memory2 = TypeOfMemory.valueOf(matcher.group(5));
        used2 = Integer.parseInt(matcher.group(6));
        max2 = Integer.parseInt(matcher.group(7));

        memory3 = TypeOfMemory.valueOf(matcher.group(8));
        used3 = Integer.parseInt(matcher.group(9));
        max3 = Integer.parseInt(matcher.group(10));
    }

    public TypeOfMemory getMemory1Type() {
        return memory1;
    }

    public TypeOfMemory getMemory2Type() {
        return memory2;
    }

    public TypeOfMemory getMemory3Type() {
        return memory3;
    }

    public int getUsedInMemory1() {
        return used1;
    }

    public int getUsedInMemory2() {
        return used2;
    }

    public int getUsedInMemory3() {
        return used3;
    }

    public int getMaxInMemory1() {
        return max1;
    }

    public int getMaxInMemory2() {
        return max2;
    }

    public int getMaxInMemory3() {
        return max3;
    }
}
