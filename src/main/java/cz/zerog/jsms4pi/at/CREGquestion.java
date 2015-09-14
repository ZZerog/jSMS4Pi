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
 * Return registration status
 * 
 * @author zerog
 */
public class CREGquestion extends AAT {

    public static String NAME = "+CREG";
    
    private final Pattern pattern = Pattern.compile("\\+CREG: *(\\d{1}),(\\d{1,2})");

    private int n;
    private NetworkStatus status;

            
    public enum NetworkStatus {
        
        NOT_REGISTERED(0),
        REGISTERED(1),
        SEARCHING(2),
        DENIED(3),
        UNKNOWN(4),
        ROAMING(5),
        SMS_ONLY(6),
        SMS_ONLY_ROAMING(7),
        EMERGENCY_ONLY(8),
        CSFB(9),
        CSFB_REAMING(10),
        ;
        
        private int code;
        
        
        
        private NetworkStatus(int code) {
            this.code = code;
        }
        
        public int getCode() {
            return code;
        }
        
        public static NetworkStatus valueOf(int code) {
            switch(code) {
                case 0 :
                    return NOT_REGISTERED;
                case 1 :
                    return REGISTERED;
                case 2 :
                    return SEARCHING;
                case 3 :
                    return DENIED;
                case 4 :
                    return UNKNOWN;
                case 5 :
                    return ROAMING;
                case 6 :
                    return SMS_ONLY;
                case 7 :
                    return SMS_ONLY_ROAMING;
                case 8 :
                    return EMERGENCY_ONLY;
                case 9 :
                    return CSFB;
                case 10 :
                    return CSFB_REAMING;                    
            }
            throw new IllegalArgumentException("Illegal network status code. Accept 0 to 10 including.");
        }
        
        
    }

    
    public CREGquestion() {
        super(NAME, Mode.QUESTION);
    }

    /**
     *
     */
    @Override    
    protected void parseQuestionResult(String response) {
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            n = Integer.valueOf(matcher.group(1));
            status = NetworkStatus.valueOf(Integer.parseInt(matcher.group(2)));
        }
    }

    public int getN() {
        return n;
    }

    public NetworkStatus getNetworkStatus() {
        return status;
    }
    
    public int getNumberOfStatus() {
        return status.getCode();
    }
    
    public boolean useSMS() {
        return status.getCode()==1 || status.getCode()==5 || status.getCode()==6 || status.getCode()==7; 
    }

}
