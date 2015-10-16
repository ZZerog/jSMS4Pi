package cz.zerog.jsms4pi.at;

import cz.zerog.jsms4pi.exception.AtParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * PIN Status.
 *
 * 127007 (8.3)
 *
 * @author zerog
 */
public class CPINquestion extends AAT {

    private final Pattern pattern = Pattern.compile("\\+CPIN: *(READY|SIM PIN|SIM PUK|PH-SIM PIN|PS-FSIM PIN|PH-FSIM PUK|SIP PIN2|SIM PUK2|PH-NET PIN|PH-NET PUK|PH-NETSUB PIN|PH-NETSUB PUK|PH-SP PIN|PH-SP PUK|PH-CORP PIN|PH-CORP PUK)\\s*");
    
    private PinStatus pinStatus;
    
    public CPINquestion() {
        super(CPIN.NAME, Mode.QUESTION);
    }
    
    @Override
    protected void parseQuestionResult(String response) {
        Matcher matcher = pattern.matcher(response);
        if (!matcher.matches()) {
            throwExceptionInMainThread(new AtParseException(response, pattern));
            return;
        }
        
        pinStatus = PinStatus.getValue(matcher.group(1));        
    }    
    
    public PinStatus getPinStatus() {
        return pinStatus;
    }

    public enum PinStatus {
        


        READY, //MT is not pending for any password 
        SIM_PIN, //MT is waiting SIM PIN to be given
        SIM_PUK, //MT is waiting SIM PUK to be given
        PH_SIM_PIN, //MT is waiting phone-to-SIM card password to be given 
        PH_FSIM_PIN, //MT is waiting phone-to-very first SIM card password to be given 
        PH_FSIM_PUN, //MT is waiting phone-to-very first SIM card unblocking password to be given 

        /*
         MT is waiting SIM PIN2 to be given (this <code> is recommended to be returned only when the
         last executed command resulted in PIN2 authentication failure (i.e. +CME ERROR: 17); if PIN2
         is not entered right after the failure, it is recommended that MT does not block its operation) 
         */
        SIM_PIN2,
        /*
         MT is waiting SIM PUK2 to be given (this <code> is recommended to be returned only when the
         last executed command resulted in PUK2 authentication failure (i.e. +CME ERROR: 18); if
         PUK2 and new PIN2 are not entered right after the failure, it is recommended that MT does not
         block its operation) 
         */
        SIM_PUK2,
        PH_NET_PIN, //MT is waiting network personalization password to be given
        PH_NET_PUK, //MT is waiting network personalization unblocking password to be given 
        PH_NETSUB_PIN, //MT is waiting network subset personalization password to be given
        PH_NETSUB_PUK, //MT is waiting network subset personalization unblocking password to be given 
        PH_SP_PIN, //MT is waiting service provider personalization password to be given
        PH_SP_PUK, //MT is waiting service provider personalization unblocking password to be given 
        PH_CORP_PIN, //MT is waiting corporate personalization password to be given 
        PH_CORP_PUK; //MT is waiting corporate personalization unblocking password to be given
        
        public static PinStatus getValue(String in) {
            switch(in) {
                case "READY" : return READY;
                case "SIM PIN" : return SIM_PIN;
                case "SIM PUK" : return SIM_PUK;
                case "PH-SIM PIN" : return PH_SIM_PIN;
                case "PH-FSIM PIN" : return PH_FSIM_PIN;
                case "PH-FSIM PUN" : return PH_FSIM_PUN;    
                case "SIM PIN2" : return SIM_PIN2;
                case "SIM PUK2" : return SIM_PUK2;
                case "PH-NET PIN" : return PH_NET_PIN;
                case "PH-NET PUK" : return PH_NET_PUK;
                case "PH-NETSUB PIN" : return PH_NETSUB_PIN;
                case "PH-NETSUB PUK" : return PH_NETSUB_PUK;
                case "PH-SP PIN" : return PH_SP_PIN;
                case "PH-SP PUK" : return PH_SP_PUK;
                case "PH-CORP PUK" : return PH_CORP_PUK;
                case "PH-CORP PIN" : return PH_CORP_PIN;
            }
            return null;
        }    
    }
}
