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
 * Configuration of indications
 *
 * @author zerog
 */
public class CNMI extends AAT {
    
    public static final String NAME = "+CNMI";
    
    private Mode mode;
    private Mt mt;
    private Bm bm;
    private Ds ds;
    private Bfr bfr;

    public CNMI(Mode mode, Mt mt, Bm bm, Ds ds, Bfr bfr) {
        super(NAME);
        this.mode=mode;
        this.mt=mt;
        this.bm=bm;
        this.ds=ds;
        this.bfr=bfr;
    }    
    
    public CNMI(Mode mode, Mt mt, Bm bm, Ds ds) {
        this(mode, mt, bm, ds, null);
    }
    
    public CNMI(Mode mode, Mt mt, Bm bm) {
        this(mode, mt, bm, null, null);
    }    
    
    public CNMI(Mode mode, Mt mt) {
        this(mode, mt, null, null, null);
    }        
    
    public CNMI(Mode mode) {
        this(mode, null, null, null, null);
    }            
    
    public enum Mode {
        
        ONLY_TEL_0(0), // Buffer unsolicited result codes in the TA
        _1(1), //Discard indication and reject new received message unsolicited result codes 
        _2(2), //Buffer unsolicited result codes  and flush them to the TE
        _3(3), //Forward unsolicited result codes directly to the TE
        ;
        
        int index;
        
        private Mode(int index) {
            this.index = index;
        }
        
        public int getIndex() {
            return index;
        }
    }
    
    public enum Mt {
        
        NO_NITIFI_0(0), 
        NOTIFI_1(1), 
        DIRECT_NOTIFI_BESIDES_CLASS2_2(2), 
        NITIFI_ONLY_CLASS3_3(3), 
        ;
        
        int index;
        
        private Mt(int index) {
            this.index = index;
        }
        
        public int getIndex() {
            return index;
        }        
    }    
    
    public enum Bm {        
        NO_CBM_NOTIFI_0(0), 
        CBM_NOTIFI_1(1), 
        DIRECT_NOTIFI_CMB_2(2), 
        DIRECT_NOTIFI_CLASS3_3(3), 
        ;
        
        int index;
        
        private Bm(int index) {
            this.index = index;
        }
        
        public int getIndex() {
            return index;
        }        
    }      
    
    public enum Ds {        
        NO_STATUS_REPORT_0(0), 
        STATUS_REPORT_NOTIFI_1(1), 
        STATUS_REPORT_NOTIFI_IF_STORED_2(2), 
        ;
        
        int index;
        
        private Ds(int index) {
            this.index = index;
        }
        
        public int getIndex() {
            return index;
        }        
    }    
    
    public enum Bfr {        
        BUFFER_FLUSHED_0(0), 
        BUFFER_CLEARED_1(1), 
        ;
        
        int index;
        
        private Bfr(int index) {
            this.index = index;
        }
        
        public int getIndex() {
            return index;
        }        
    }    

    @Override
    public String getRequest() {
        StringBuilder at = new StringBuilder();
        at.append(getName());
        at.append("=");        
        at.append(mode.getIndex());
        if(mt==null) {
            at.append(AAT.CR);
            return at.toString();
        }
        at.append(",").append(mt.getIndex());
        if(bm==null) {
            at.append(AAT.CR);
            return at.toString();
        }    
        at.append(",").append(bm.getIndex());
        if(ds==null) {
            at.append(AAT.CR);
            return at.toString();
        }
        at.append(",").append(ds.getIndex());
        if(bfr==null) {
            at.append(AAT.CR);
            return at.toString();
        }
        at.append(",").append(bfr.getIndex()).append(AAT.CR);        
        return at.toString();
    }   
}
