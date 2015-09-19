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

import cz.zerog.jsms4pi.at.CPMS.TypeOfMemory;
import cz.zerog.jsms4pi.exception.AtParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Preferred Message Storage (Support).
 *
 * CPMS=?
 * 
 * @author zerog
 */
public class CPMSsupport extends AAT {

    private final Pattern pattern = Pattern.compile("\\+CPMS:( *)\\(?\\((.*)\\),\\((.*)\\),\\((.*)\\)\\)?\\s*");

    private TypeOfMemory[] memory1;
    private TypeOfMemory[] memory2;
    private TypeOfMemory[] memory3;

    public CPMSsupport() {
        super(CPMS.NAME, Mode.SUPPORT);
    }

    /**
     *
     * @param response
     */
    @Override
    protected void parseSupportResult(String response) {
        Matcher matcher = pattern.matcher(response);
        if (matcher.matches()) {
            memory1 = getMemoryArr(matcher.group(2));
            memory2 = getMemoryArr(matcher.group(3));
            memory3 = getMemoryArr(matcher.group(4));
        } else {
            throwExceptionInMainThread(new AtParseException(response, pattern));
        }
    }
    
    private TypeOfMemory[] getMemoryArr(String memory) {
        String[] memArr = memory.split(",");
        TypeOfMemory[] tMem = new TypeOfMemory[memArr.length];
        for (int i = 0; i < tMem.length; i++) {
            tMem[i] = TypeOfMemory.valueOf(memArr[i].substring(1, 3));            
        }
        return tMem;
    }
    
    
    public TypeOfMemory[] getMemory1() {
        return memory1;
    }
    
    public TypeOfMemory[] getMemory2() {
        return memory2;
    }
    
    public TypeOfMemory[] getMemory3() {
        return memory3;
    }    
}
