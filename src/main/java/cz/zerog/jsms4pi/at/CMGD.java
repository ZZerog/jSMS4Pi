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
 * Set mode text or pdu
 *
 * @author zerog
 */
public class CMGD extends AAT {
    
    public static final String NAME = "+CMGD";

    private DelMode mode;
    private int index = 0;

    public CMGD(int index) {
        this(index, DelMode.SINGLE_SMS);
    }    
    
    public CMGD(DelMode mode) {
        this(0, mode);
    }
    
    public CMGD(int index, DelMode mode) {
        super(NAME);
        this.index = index;
        this.mode = mode;
    }    

    public enum DelMode {

        SINGLE_SMS(0), 
        ALL_REC_READ(1),
        ALL_REC_READ_STORED_SEND(2),
        ALL_REC_READ_STORED_UNSEND_STORED_SEND(3),
        ALL(4);

        int code;

        private DelMode(int type) {
            this.code = type;
        }

        public int getCode() {
            return code;
        }
        
        public static DelMode valueOf(int code) {
            switch (code) {
                case 0:
                    return DelMode.SINGLE_SMS;
                case 1:
                    return DelMode.ALL_REC_READ;
                case 2:
                    return DelMode.ALL_REC_READ_STORED_SEND;
                case 3:
                    return DelMode.ALL_REC_READ_STORED_UNSEND_STORED_SEND;
                case 4:
                    return DelMode.ALL;                    
            }
            return null;
        }
    }

    @Override
    public String getCommandRequest() {
        return getName()+"="+index+","+mode.getCode() + AAT.CR;
    }
    
    public DelMode getDelMode() {
        return mode;
    }

    public int getIndex() {
        return index;
    }


}
