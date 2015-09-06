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


import cz.zerog.jsms4pi.ATResponse;
import cz.zerog.jsms4pi.exception.ATParseException;
import java.util.regex.Pattern;

/**
 *
 * @author zerog
 */
public abstract class AAT implements ATResponse {

    public static final char CR = 0x0d;
    public static final int CTRLZ = 0x1A;

    private final Pattern errorPattern = Pattern.compile("CMS:( *)ERROR:( *)(\\d{1,3})");
    
    public static ATParseException getParseException(String name, String result) {
        return new ATParseException(name, result);
    }

    protected final StringBuilder response = new StringBuilder();
    protected Status status = Status.READY;
    private Mode mode;
    private int cmsError = -1;

    private String name;
    
    private AAT() {};

    public AAT(String commandName, Mode mode) {
        this.name = commandName;
        this.mode = mode;
    }

    public AAT(String commandName) {
        this(commandName, Mode.COMMAND);
    }

    public void send() {
        status = Status.WAITING;
    }

    public boolean appendResponse(String partOfResponse) {
        if (partOfResponse == null) {
            return false;
        }
        response.append(partOfResponse);
        return isComplete();
    }

    public enum Mode {

        COMMAND,
        QUESTION,
        SUPPORT;
    }

    public enum Status {
        READY, WAITING, OK, ERROR;
    }

    protected boolean isComplete() {
        if (response.indexOf("OK\r\n") > 0) {

            switch (mode) {
                case COMMAND:
                    parseCommandResult(response.toString());
                    break;
                case QUESTION:
                    parseQuestionResult(response.toString());
                    break;
                case SUPPORT:
                    parseSupportResult(response.toString());
                    break;
            }

            status = Status.OK;
            return true;
        }
        if (response.indexOf("ERROR") > 0) {
            status = Status.ERROR;
            return true;
        }
        return false;
    }

    /**
     * Parse additional OK result.
     * @param response
     */
    protected void parseCommandResult(String response) {
        
    }

    protected void parseQuestionResult(String response) {
        throw new RuntimeException("Not implement");
    }

    protected void parseSupportResult(String response) {
        throw new RuntimeException("Not implement");
    }


    @Override
    public String getResponse() {
        if (status.equals(Status.OK) || status.equals(Status.ERROR)) {
            return response.toString();
        }
        throw new RuntimeException("At has no response yed");
    }

    public Status getStatus() {
        return status;
    }
    
    public boolean isStatus(Status status) {
        return getStatus().equals(status);
    }
    
    public boolean isStatusOK() {
        return isStatus(Status.OK);
    }
    

    public String getRequest() {
        switch (mode) {
            case COMMAND:
                return getCommandRequest();
            case QUESTION:
                return getQuestionRequest();
            case SUPPORT:
                return getSupportRequest();
        }
        return null;
    }

    public String getCommandRequest() {
        return name + AAT.CR;
    }

    protected String getQuestionRequest() {
        return name + "?" + AAT.CR;
    }

    protected String getSupportRequest() {
        return name + "=?" + AAT.CR;
    }

    protected final String getName() {
        return name;
    }

    public Mode getMode() {
        return mode;
    }
    
    public int getCMSErrorCode() {
        return cmsError;
    }
    
    public String getPrefix() {
        return "AT";
    }
}
