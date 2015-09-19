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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    private final Pattern pattern = Pattern.compile("\\+CMGR:( *[a-zA-Z]*),(\\d+),(\\d+),\\\"(\\+?\\d+)\\\",(\\d*),\\\"(\\d{2}/\\d{2}/\\d{2},\\d{2}:\\d{2}:\\d{2}\\+?-?\\d{2})\\\",\\\"(\\d{2}/\\d{2}/\\d{2},\\d{2}:\\d{2}:\\d{2}\\+?-?\\d{2})\\\",(\\d+)\\s*");

    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("yy/MM/dd,HH:mm:ssx");

    public static final String NAME = "+CMGR";

    private final int index;

    private String stat;
    private int fo = -1;
    private int mr = -1;
    private String ra;
    private int tora = -1;
    private LocalDateTime scts;
    private LocalDateTime dt;
    private int code = -1;

    public CMGR(int index) {
        super(NAME);
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
        Matcher matcher = pattern.matcher(response);
        if (!matcher.matches()) {
            throwExceptionInMainThread(new AtParseException(response, pattern));
            return;
        }
        stat = matcher.group(1);
        fo = Integer.parseInt(matcher.group(2));
        mr = Integer.parseInt(matcher.group(3));
        ra = matcher.group(4);
        tora = Integer.parseInt(matcher.group(5));
        scts = LocalDateTime.parse(matcher.group(6), timeFormat);
        dt = LocalDateTime.parse(matcher.group(7), timeFormat);
        code = Integer.parseInt(matcher.group(8));
    }

    public Pattern getPattern() {
        return pattern;
    }

    public DateTimeFormatter getTimeFormat() {
        return timeFormat;
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

    public int getCode() {
        return code;
    }
}
