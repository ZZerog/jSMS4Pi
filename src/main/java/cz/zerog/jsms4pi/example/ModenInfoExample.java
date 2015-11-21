package cz.zerog.jsms4pi.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

import cz.zerog.jsms4pi.ATGateway;
import cz.zerog.jsms4pi.exception.GatewayException;

/**
 *
 * @author zerog
 */
public class ModenInfoExample {

	private static void printHelp() {
		System.out.println("Show info");
	}

	private ModenInfoExample(String port) throws GatewayException {

		ATGateway gateway = new ATGateway(port);
		gateway.open();
		gateway.printModemInfo();
		gateway.close();

	}

	public static void main(String[] args) throws IOException, GatewayException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		String port = null;

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

		if (port == null) {
			port = Tool.selectionPort(reader);
		}

		if (port == null) {
			System.exit(0);
		}

		System.out.println("Summary: ");
		System.out.println("Serial port: " + port);

		Tool.enter(reader);

		new ModenInfoExample(port);
	}
}
