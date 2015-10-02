package cz.zerog.jsms4pi;

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author zerog
 */
public class ModemInformation {

    private String manufaturer;
    private String model;
    private String imei;

    private List<String> info = new ArrayList();

    public Iterable<String> getNames() {
        
        //IMEI has the highest priority
        info.add(imei);
        info.add(manufaturer+"_"+model);
        info.add(manufaturer);
        
        
        return new Iterable<String>() {
            @Override
            public Iterator<java.lang.String> iterator() {
                return info.iterator();
            }
        };
    }

    public void setManufacturer(String manufaturer) {
        this.manufaturer = manufaturer.replaceAll(" ", "-");
    }

    public void setModelAndCapabilities(String model) {
        this.model = model.replaceAll(" ", "-");
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}
