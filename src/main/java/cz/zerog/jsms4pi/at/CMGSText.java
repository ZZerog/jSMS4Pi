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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Set mode text or pdu
 *
 * @author zerog
 */
public class CMGSText extends AAT {

    private final Pattern indexPattern = Pattern.compile("CMGS:( *)(\\d{1,3})");

    private String text;

    private int index = -1;

    /**
     * Default type is text mode
     *
     * @param mode
     */
    public CMGSText(String text) {
        super("");
        this.text = text + ((char) AAT.CTRLZ);
    }

    @Override
    public String getRequest() {
        return text;
    }

    @Override
    public String getPrefix() {
        return "";
    }

    public String getText() {
        return text;
    }

    @Override
    protected void parseCommandResult(String response) {
        Matcher matcher = indexPattern.matcher(response);
        if (matcher.find()) {
            index = Integer.parseInt(matcher.group(2));
        }
    }

    public int getIndex() {
        return index;
    }
}
