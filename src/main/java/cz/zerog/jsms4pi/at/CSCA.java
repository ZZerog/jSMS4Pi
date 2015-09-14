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
 * Set Service Center Address
 *
 * @author zerog
 */
public class CSCA extends AAT {

    public static final String NAME = "+CSCA";
    private NumberType type = NumberType.INTERNACIONAL;
    private String address;

    /**
     * Default type is internacional format.
     * @param serviceAddress 
     */
    public CSCA(String serviceAddress) {
        super(NAME);
        this.address = serviceAddress;
    }

    public CSCA(String serviceAddress, NumberType type) {
        this(serviceAddress);
        this.type = type;
    }
    
    public CSCA(CSCAquestion cscaQ) {
        this(cscaQ.getAddress(), cscaQ.getType());
    }    

    public enum NumberType {

        UNKNOWN(129), INTERNACIONAL(145);

        int code;

        private NumberType(int type) {
            this.code = type;
        }

        public int getCode() {
            return code;
        }
        
        public static NumberType valueOf(int code) {
            switch (code) {
                case 129:
                    return NumberType.UNKNOWN;
                case 145:
                    return NumberType.INTERNACIONAL;
            }
            throw new IllegalArgumentException("Illegal number type code. Accept code 129 and 145.");
        }
    }
    
    @Override
    public String getCommandRequest() {
        return getName()+"=\""+address+"\","+type.getCode() + AAT.CR;
    }


    public String getAddress() {
        return address;
    }

    public NumberType getType() {
        return type;
    }
}
