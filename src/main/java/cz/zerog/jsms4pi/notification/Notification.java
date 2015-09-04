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

import cz.zerog.jsms4pi.ATResponse;


/**
 *
 * @author zerog
 */
public abstract class Notification implements ATResponse {

    protected final StringBuilder response = new StringBuilder();
    
    public Notification() {
        
    }

    @Override
    public boolean appendResponse(String partOfResponse) {
        if (partOfResponse == null) {
            return false;
        }
        response.append(partOfResponse);
        return isComplete();
    }

    protected boolean isComplete() {
        //TODO
        if (false) {
            parse(response.toString());
        }
        return false;
    }

    protected abstract void parse(String notification);

    @Override
    public String getResponse() {
        return response.toString();
    }

}
