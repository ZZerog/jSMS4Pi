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
 * Set SMS configuration
 * 
 * There are four classes of an SMS message. Classes identify the importance of an SMS message and also the location where it must be stored.

Class 0

This type of SMS message is displayed on the mobile screen without being saved in the message store or on the SIM card; unless explicitly saved by the mobile user.

Class 1

This message is to be stored in the device memory or the SIM card (depending on memory availability).

Class 2

This message class carries SIM card data. The SIM card data must be successfully transferred prior to sending acknowledgment to the service center. An error message is sent to the service center if this transmission is not possible.

Class 3

This message is forwarded from the receiving entity to an external device. The delivery acknowledgment is sent to the service center regardless of whether or not the message was forwarded to the external device.


 *
 * @author zerog
 */
public class CSMP extends AAT {
    
    public static final String NAME = "+CSMP";
    
    /**
    bit 5 true = active delivery report
    */
    public static int DELIVERY_REPORT = 1 << 5;
    
    
    /**
    bit 4 true = active SMS validity period 
     */
    public static int VALIDITY_PERIOD = 1 << 4;
    
    /*
     * Fo is combination of bit options.
    
     * Bit 5 = delivery report
     * Bit 4 = validity period
     */
    private int fo = 49;

    public CSMP() {
        super(NAME);
    }    
    
    public CSMP(int fo) {
        this();
        this.fo = fo | 1 ;
    }

    @Override
    public String getCommandRequest() {
        return getName()+"="+fo+",167,0,0" + AAT.CR; //expirace 24h
        //return getName()+"="+fo+",0,0,0" + AAT.CR; //expirace 5min
    }
}
