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
 * Active caller ID while RING
 *
 * @author zerog
 */
public class CLIP extends AAT {

    public static final String NAME = "+CLIP";

    private final boolean active;

    public CLIP(boolean active) {
        super(NAME);
        this.active = active;
    }

    @Override
    public String getCommandRequest() {
        return getName() + "=" + (active ? "1" : "0") + AAT.CR;
    }
    
    public boolean isActive() {
        return active;
    }

}
