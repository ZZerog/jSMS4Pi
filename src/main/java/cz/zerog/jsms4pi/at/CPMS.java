package cz.zerog.jsms4pi.at;

import cz.zerog.jsms4pi.tool.TypeOfMemory;

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
 * Set memory policy
 *
 * @author zerog
 */
public class CPMS extends AAT {
    
    public static final String NAME = "+CPMS";

    /*    
     Request fields
     */
    /*
     memory that will by used when reading or deleting
     +CMGR, +CMGL, +CMGD
     */
    private TypeOfMemory reqMem1;

    /*
     memory that will by used when sending or writing
     +CMSS, +CMGW
     */
    private TypeOfMemory reqMem2;

    /*
     Storing newly received SMS.
     */
    private TypeOfMemory reqMem3;

    /**
     * Preferred Message Storage
     *
     * @param mode
     * @param mem1 memory that will by used when reading or deleting (+CMGR,
     * +CMGL, +CMGD)
     * @param mem2 memory that will by used when sending or writing (+CMSS,
     * +CMGW)
     * @param mem3 storing newly received SMS
     */
    public CPMS(TypeOfMemory mem1, TypeOfMemory mem2, TypeOfMemory mem3) {
        super(NAME, Mode.COMMAND);
        this.reqMem1 = mem1;
        this.reqMem2 = mem2;
        this.reqMem3 = mem3;
    }

    /**
     * @param mem1 memory that will by used when reading or deleting (+CMGR,
     * +CMGL, +CMGD)
     */
    public CPMS(TypeOfMemory mem1) {
        this(mem1, null, null);
    }

    public CPMS(TypeOfMemory mem1, TypeOfMemory mem2) {
        this(mem1, mem2, null);
    }



    @Override
    public String getCommandRequest() {
        StringBuilder at = new StringBuilder(getName() + "=\"" + reqMem1 + "\"");
        if (reqMem2 == null) {
            at.append(AAT.CR);
            return at.toString();
        }
        at.append(",\"").append(reqMem2).append("\"");
        if (reqMem3 == null) {
            at.append(AAT.CR);
            return at.toString();
        }
        at.append(",\"").append(reqMem3).append("\"").append(AAT.CR);
        
        return at.toString();
    }
    
    
}
