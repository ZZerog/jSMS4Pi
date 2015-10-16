package cz.zerog.jsms4pi.tool;

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
import cz.zerog.jsms4pi.NullGateway;
import cz.zerog.jsms4pi.SerialModem;
import cz.zerog.jsms4pi.at.AT;
import jssc.SerialPortList;

/**
 *
 * @author zerog
 */
public class ModemSeeker {

    private SerialModem modem;
    private final int[] defaultBoundrate = {300, 1200, 2400, 4800, 9600, 19200, 38400, 57600, 115200, 128000, 256000};
    private static boolean verbose = false;

    public void scan(String ownPort, int[] ownBoudrate) {

        String[] portNames;
        int[] boundrate;

        /*
         set serial port
         */
        if (ownPort == null) {
            portNames = SerialPortList.getPortNames();
        } else {
            portNames = new String[1];
            portNames[0] = ownPort;
        }

        /*
         set boudrate
         */
        if (ownBoudrate == null) {
            boundrate = defaultBoundrate;
        } else {
            boundrate = ownBoudrate;
        }

        if (portNames.length <= 0) {
            System.out.println("No serial ports. Have you right permission?");
            System.exit(0);
        }

        /*
         Main loop
         */
        System.out.println("Scanning start");
        for (int i = 0; i < portNames.length; i++) {
            System.out.println("Port: " + portNames[i]);
            for (int j = 0; j < boundrate.length; j++) {
                try {
                    modem = new SerialModem(new NullGateway(), boundrate[j], 1 * 1000);
                    modem.open(portNames[i]);
                    modem.send(new AT());
                    System.out.println("  - Boundrate: " + boundrate[j]);
                } catch (Exception e) {
                    if (verbose) {
                        System.out.println("Error on " + portNames[i] + ", boundrate: " + boundrate[j] + ". Error: " + e.getMessage());
                    }
                } finally {
                    if (modem != null) {
                        try {
                            modem.close();
                        } catch (Exception ee) {
                            if (verbose) {
                                System.err.println("Cannot close port " + portNames[i] + ". Error: " + ee.getMessage());
                            }
                        }
                    }
                }
            }
        }
        System.out.println("Scanning finish.");
        System.out.println("Found modem on port:");
    }

    public static void main(String[] args) {

        String port = null;
        int[] boudrate = null;

        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                String identifier = args[i];

                switch (identifier) {
                    case "-p":
                        if (i + 1 < args.length) {
                            i++;
                            port = args[i];
                        } else {
                            System.out.println("Wrong count of argument.");
                            printHelp();
                            System.exit(0);
                        }
                        break;
                    case "-b":
                        i++;

                        String[] boundArr = args[i].split(",");
                        boudrate = new int[boundArr.length];

                        for (int ii = 0; ii < boundArr.length; ii++) {
                            try {
                                boudrate[ii] = Integer.parseInt(boundArr[ii].trim());
                            } catch (NumberFormatException e) {
                                System.err.println(boundArr[ii].trim() + " is not valid number. Exit");
                                System.exit(1);
                            }
                        }
                        break;
                    case "-v":
                        i++;
                        verbose = true;
                        break;
                    case "-h":
                    case "-help":
                        printHelp();
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Unknow parametr " + args[i]);
                        printHelp();
                        System.exit(0);
                }
            }
        }

        new ModemSeeker().scan(port, boudrate);
    }

    private static void printHelp() {
        System.out.println("INFO");
    }

}
