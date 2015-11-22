package cz.zerog.jsms4pi.tool;

import java.io.IOException;

import cz.zerog.jsms4pi.NullGateway;

/*
 * #%L
 * jSMS4Pi
 * %%
 * Copyright (C) 2015 - 2016 jSMS4Pi
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

import cz.zerog.jsms4pi.SerialModem;
import cz.zerog.jsms4pi.at.AT;
import cz.zerog.jsms4pi.example.Tool;
import jssc.SerialPortList;

/**
 * A ModemScanner class test all serial ports with different speed and listens
 * if a modem answer.
 *
 * @author zerog
 */
public class ModemScanner {

	private final static int[] defaultBoundrate = { 9600, 19200, 38400, 57600, 115200, 128000, 256000, 921600 };
	public static String ln = System.getProperty("line.separator");
	private static boolean verbose = false;

	public ModemScanner(String port, int[] baudrate) {

		String[] portNames;

		if (port == null) {
			portNames = SerialPortList.getPortNames();
		} else {
			portNames = new String[1];
			portNames[0] = port;
		}

		if (portNames.length <= 0) {
			System.out.println("No serial ports. Have you right permission?");
			System.exit(0);
		}

		/*
		 * Main loop
		 */
		System.out.println("Scanning start");

		SerialModem modem = null;

		for (int i = 0; i < portNames.length; i++) {
			System.out.println("Found port: " + portNames[i]);

			boolean found = false;

			for (int j = 0; j < baudrate.length; j++) {
				try {
					modem = new SerialModem(portNames[i], baudrate[j], 1 * 1000);
					modem.setGatewayListener(new NullGateway());
					modem.open();
					if (modem.send(new AT()).isStatusOK()) {
						System.out.println("   - modem response - boundrate: " + baudrate[j]);
						found = true;
					}
				} catch (Exception e) {
					if (verbose) {
						System.out.println("Error on " + portNames[i] + ", boundrate: " + baudrate[j] + ". Error: "
								+ e.getMessage());
					}
				} finally {
					if (modem != null) {
						try {
							modem.close();
						} catch (Exception ee) {
							if (verbose) {
								System.err.println("Cannot close port " + portNames[i] + ". Error: ");
								ee.printStackTrace();
							}
						}
					}
				}
			}

			if (!found) {
				System.out.println("   - without an answer");
			}

		}
		System.out.println("Scanning finish.");
	}

	public static void main(String[] args) throws IOException {

		System.out.println(ln + "A Modem Scanner is simple programe for search modems connected into serial port");

		Tool.showHelp(args,
				String.format("Parameters:%n -p <port name> used to testing specifig serial port%n "
						+ "-b <baudrate_1,baudrate_2> set specifig serial speeds%n -v verbose mode, show all errors%n "
						+ "-h show this information%n%n"));

		String port = Tool.portOrNull(args);
		int baudrate[] = Tool.boudrates(args);
		verbose = Tool.verbose(args);

		if (baudrate == null) {
			baudrate = defaultBoundrate;
		}

		System.out.println(ln + "--- Summary ---");
		if (port != null) {
			System.out.println("Selected single port: " + port);
		}

		StringBuilder sb = new StringBuilder("Testing baunrate: {");
		for (int i = 0; i < baudrate.length; i++) {
			sb.append(baudrate[i] + ", ");
		}
		sb.delete(sb.length() - 2, sb.length());
		sb.append("}");

		System.out.println(sb.toString());
		Tool.pressEnter();

		new ModemScanner(port, baudrate);
	}
}
