package cz.zerog.jsms4pi.tool;

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
 * The TP-Status field indicates the status of a previously submitted SMS-SUBMIT and certain SMS COMMANDS for
 * which a Status -Report has been requested. 
 * 
 * @author zerog
 */
public enum SPStatus {

    /**
     * 
     */
    RECEIVED(0),
    NOT_CONFIRMED(1),
    REPLACED(2),
    
    /*
     Temporary error, SC still trying to transfer SM
     */
    CONGESTION(32),
    BUSY(33),
    NO_RESPONSE(34),
    REJECTED(35),
    NO_QOS(36),
    SME_ERROR(37),
    
    /*
    Permanent error, SC is not making any more transfer attempts
    */
    REMOTE_ERROR(64),
    DESTINATION_ERROR(65),
    CONNECTION_REJECTED(66),
    NOT_OBTAINABLE(67),
    NO_QOS_ERROR(68),
    NO_INTERWORKING(69),
    EXPIRED(70),
    DELETED_BY_SME(71),
    DELETED_BY_ADMIN(72),
    MESSAGE_NO_EXIST(73),
    CONGESTION_ERROR(96),
    SME_BUSY(97),
    NO_RESPONSE_ERROR(98),
    SEVICE_REJECTED(99),
    NO_QOS_ERROR2(100),
    ERROR_IN_SME(101),

    UNDEFINED_ERROR(1000);

    private final int index;

    private SPStatus(int index) {
        this.index = index;
    }
    
    public static SPStatus valueOf(int index) {
        switch(index) {
            case 0: return RECEIVED;
            case 1: return NOT_CONFIRMED;
            case 2: return REPLACED;
                
            case 32: return CONGESTION;
            case 33: return BUSY;
            case 34: return NO_RESPONSE;
            case 35: return REJECTED;
            case 36: return NO_QOS;
            case 37: return SME_ERROR;
                
            case 64: return REMOTE_ERROR;
            case 65: return DESTINATION_ERROR;
            case 66: return CONNECTION_REJECTED;
            case 67: return NOT_OBTAINABLE;
            case 68: return NO_QOS_ERROR;
            case 69: return NO_INTERWORKING;
            case 70: return EXPIRED;
            case 71: return DELETED_BY_SME;
            case 72: return DELETED_BY_ADMIN;
            case 73: return MESSAGE_NO_EXIST;
            case 96: return CONGESTION_ERROR;
            case 97: return SME_BUSY;
            case 98: return NO_RESPONSE_ERROR;
            case 99: return SEVICE_REJECTED;
            case 100: return NO_QOS_ERROR2;
            case 101: return ERROR_IN_SME;
                
            default: return UNDEFINED_ERROR;
        }
    }
    
    
    public int getIndex() {
        return index;
    }
}
