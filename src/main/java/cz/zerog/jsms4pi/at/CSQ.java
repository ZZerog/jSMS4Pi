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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Get Signal Quality
 * 
 * @author zerog
 */
public class CSQ extends AAT {
    
    public static final String NAME = "+CSQ";

    private final Pattern pattern = Pattern.compile("CSQ:( *)(\\d{1,2}),");
    private final int mindBm = -113;
    
    public enum Condiconal {
        NO_SIGNAL,
        MARGINAL,
        OK, 
        GOOD, 
        EXCELLENT;
    }

    public CSQ() {
        super(NAME);
    }

    /**
     * Value in native form returned by modem (0 to 31)
     * @return 
     */
    public int getRawValue() {
        Matcher matcher = pattern.matcher(getResponse());
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(2));
        }
        return -1;
    }

    /**
     * Value as RSSI in dBm
     * @return 
     */
    public int getRssi() {
        return mindBm + getRawValue()*2;
    }

    /**
     * Condition text
     * @return 
     */
    public Condiconal getCondition() {
        int raw = getRawValue();
        if(raw < 10) {
            return Condiconal.MARGINAL;
        } 
        if(raw >= 10 && raw < 15) {
            return Condiconal.OK;
        }
        if(raw >= 15 && raw < 20) {
            return Condiconal.GOOD;
        }        
        if(raw >= 20) {
            return Condiconal.EXCELLENT;
        }
        throw new IllegalStateException("Illegal signal value.");
    }
}
