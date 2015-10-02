package cz.zerog.jsms4pi.tool;

import java.time.format.DateTimeFormatter;

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
public class PatternTool {

    private PatternTool() {
    }
    
    public static final DateTimeFormatter TIME_STAMP_FORMATTER = DateTimeFormatter.ofPattern("yy/MM/dd,HH:mm:ssx");

    /**
     * Phone number in format +420731810967 or 731810967
     */
    public static final String PHONE_NUMBER = "\\+?\\d+";
    
    public static final String PHONE_TYPE = "145|129";
    
    public static final String TIME_STAMP = "\\d{2}/\\d{2}/\\d{2},\\d{2}:\\d{2}:\\d{2}[\\+\\-]\\d{2}";
    
    public static final String STAT = "REC UNREAD|REC READ|STO UNSENT|STO SENT|ALL";
    
    public static final String WHATEVER = ".*";
    
    public static final String CR_LF = "\\r\\n";
    
    
    /**
     * Positive integer number
     */
    public static final String NUMBER = "\\d+";
    
    /**
     * Type of memory
     * 
     * "BM" broadcast message storage
     * "ME" ME message storage
     * "MT" any of the storages associated with ME
     * "SM" (U)SIM message storage
     * "TA" TA message storage
     * "SR" status report storage 
     */
    public static final String MEMORY = "BM|ME|MT|SM|TA|SR";    

    
    public static String build(String pattern, String... args) {

        StringBuilder sb = new StringBuilder();

        int lastIndex = 0;
        for (String arg : args) {
            int index = pattern.indexOf("{}",lastIndex);
            if (index >= 0) {
                sb.append(pattern.substring(lastIndex, index));
                sb.append(arg);
                lastIndex = index + 2;
            }
        }
        sb.append(pattern.substring(lastIndex, pattern.length()));
        return sb.toString();
    }
    
    
    
}
