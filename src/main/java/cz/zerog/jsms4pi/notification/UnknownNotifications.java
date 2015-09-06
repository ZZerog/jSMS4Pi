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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author zerog
 */
public class UnknownNotifications implements ATResponse {
    
    private final Logger log = LogManager.getLogger();

    private final StringBuilder response = new StringBuilder();
    
    @Override
    public String getResponse() {
        return response.toString();
    }

    @Override
    public boolean appendResponse(String partOfResponse) {
        response.append(partOfResponse);
        if(response.indexOf("\r\n")==0) {
            response.delete(0, 2);
        }
        return hasNextMessage();
    }
    
    public boolean hasNextMessage() {
        return response.indexOf("\r\n")>-1;
    }

    public String getNextMessage() {
        String next = response.substring(0, response.indexOf("\r\n"));
        response.delete(0, response.indexOf("\r\n")+2);
        return next.replaceAll("\r\n", "");
    }

    public boolean isEmpty() {
        return response.length()<=0;
    }
}
