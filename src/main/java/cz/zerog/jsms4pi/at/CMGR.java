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
import cz.zerog.jsms4pi.exception.AtParseException;
import static cz.zerog.jsms4pi.tool.PatternTool.*;
import cz.zerog.jsms4pi.tool.SPStatus;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Read an SMS Message or Status
 *
 * CPMS?
 *
 * @author zerog
 */
public class CMGR extends AAT {

    private final Pattern patternStatusReport = Pattern.compile(build("\\+CMGR: *({}),({}),({}),\"({})\",({}),\"({})\",\"({})\",(\\d+)\\s*",
                                                            WHATEVER,     //stat (some modems return empty)
                                                            NUMBER,       //fo
                                                            NUMBER,       //mr  
                                                            PHONE_NUMBER, //ra
                                                            PHONE_TYPE,   //tora
                                                            TIME_STAMP,   //scts
                                                            TIME_STAMP,   //dt
                                                            NUMBER));     //st
    private final Pattern patternSmsDelivery = Pattern.compile(build("\\+CMGR: *\"({})\",\"({})\",({}),\"({})\"{}({})\\s*",
                                                            STAT,         //stat
                                                            PHONE_NUMBER, //oa
                                                            WHATEVER,     //alpha
                                                            TIME_STAMP,   //scts
                                                            CR_LF,
                                                            WHATEVER      //data
                                                            ));

    

    public static final String NAME = "+CMGR";

    private final Mode mode;

    private final int index;

    private String stat;
    private int fo = -1;
    private int mr = -1;
    private String ra;
    private int tora = -1;
    private LocalDateTime scts;
    private LocalDateTime dt;
    private SPStatus sp;
    private String text;

    /*
     delivery
     */
    //originating address (source address)
    private String oa;
    //oa from phonebook (alphanumeric string)
    private String alpha;

    public CMGR(Mode mode, int index) {
        super(NAME);
        this.mode = mode;
        this.index = index;
    }

    @Override
    public String getRequest() {
        return getName() + "=" + index + AAT.CR;
    }

    /**
     *
     * @param response
     */
    @Override
    protected void parseCommandResult(String response) {
        switch (mode) {
            case SMS_STATUS_REPORT:
                Matcher matcher = patternStatusReport.matcher(response);
                if (!matcher.matches()) {
                    throwExceptionInMainThread(new AtParseException(response, patternStatusReport));
                    return;
                }
                stat = matcher.group(1);
                fo = Integer.parseInt(matcher.group(2));
                mr = Integer.parseInt(matcher.group(3));
                ra = matcher.group(4);
                tora = Integer.parseInt(matcher.group(5));
                scts = LocalDateTime.parse(matcher.group(6), TIME_STAMP_FORMATTER);
                dt = LocalDateTime.parse(matcher.group(7), TIME_STAMP_FORMATTER);
                sp = SPStatus.valueOf(Integer.parseInt(matcher.group(8)));
                break;
            case SMS_DELIVERY:
                matcher = patternSmsDelivery.matcher(response);
                if (!matcher.matches()) {
                    throwExceptionInMainThread(new AtParseException(response, patternSmsDelivery));
                    return;
                }
                stat = matcher.group(1);
                oa = matcher.group(2);
                alpha = matcher.group(3);
                scts = LocalDateTime.parse(matcher.group(4), TIME_STAMP_FORMATTER);
                text = matcher.group(5);
                break;
        }
    }

    public Pattern getPattern() {
        switch (mode) {
            case SMS_DELIVERY:
                return patternSmsDelivery;
            case SMS_STATUS_REPORT:
                return patternStatusReport;
        }
        return null;
    }

    public int getIndex() {
        return index;
    }

    public String getStat() {
        return stat;
    }

    public int getFo() {
        return fo;
    }

    public int getMr() {
        return mr;
    }

    public String getRa() {
        return ra;
    }

    public int getTora() {
        return tora;
    }

    public LocalDateTime getScts() {
        return scts;
    }

    public LocalDateTime getDt() {
        return dt;
    }

    public SPStatus getSp() {
        return sp;
    }

    public String getOa() {
        return oa;
    }

    public String getAlpha() {
        return alpha;
    }    
    
    public String getText() {
        return text;
    }        

    public enum Mode {

        SMS_STATUS_REPORT,
        SMS_DELIVERY;
    }
}
