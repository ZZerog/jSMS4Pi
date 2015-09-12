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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author zerog
 */
public final class RING implements Notification {

    //RING  +CLIP: "+420739474009",145,,,,0
    private final static Pattern pattern = Pattern.compile("\\+CLIP: *\"(\\+?\\d+)\",\\d+,.*,.*,.*,(\\d+)");
    

    private final Validity validity;
    private final String callerId;
    private final String response;

    private RING(Matcher matcher, String response) {
        callerId = matcher.group(1);
        validity = Validity.valueOf(Integer.parseInt(matcher.group(2)));
        this.response = response;
    }

    @Override
    public String getResponse() {
        return response;
    }

    public enum Validity {

        VALID(0),
        WITHHELD_ORIGINATOR(1),
        INTERNETWORKING_PROBLEMS(2),
        PAYPHONE(3);

        private final int code;

        private Validity(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static Validity valueOf(int code) {
            switch (code) {
                case 0:
                    return VALID;
                case 1:
                    return WITHHELD_ORIGINATOR;
                case 2:
                    return INTERNETWORKING_PROBLEMS;
                case 3:
                    return PAYPHONE;
            }
            throw new RuntimeException("Cannot translate code '" + code + "' to Validity. Validity accept 0 to 3");
        }
    }

    public static RING tryParse(String notification) {
        Matcher matcher = pattern.matcher(notification);
        if (matcher.matches()) {
            return new RING(matcher,notification);
        } 
        return null;
    }

    public Validity getValidity() {
        return validity;
    }

    public String getCallerId() {
        return callerId;
    }
}
