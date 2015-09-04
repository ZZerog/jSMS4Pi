package cz.zerog.jsms4pi.event;

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

import cz.zerog.jsms4pi.notification.RING;



/**
 *
 * @author zerog
 */
public class CallEvent {
    
    private final String callerId; 
    private final RING.Validity validity;

    public CallEvent(String callerId, RING.Validity validity) {
        this.callerId = callerId;
        this.validity = validity;
    }

    public String getCallerId() {
        return callerId;
    }

    public RING.Validity getValidity() {
        return validity;
    }  
    
}
