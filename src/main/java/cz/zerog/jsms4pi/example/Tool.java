package cz.zerog.jsms4pi.example;

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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

/**
 * Tool Class Library
 * 
 * @author zerog
 */
public final class Tool {

	/*
	 * Texts constants
	 */
	private static String NO_SERIAL = "No serial port available or you do not have a permission";
	private static String SELECT_PORT_TITLE = "Select a serial port:";
	private static String SELECT_PORT = "Select a port name [1-%d]: ";
	private static String PROGRAM_START = "the program start";
	private static String PROGRAM_FINISH = "finish";
	private static String PRESS_ENTER = "%nPress enter to %s";
	private static String DEST_NUMBER = "Write the destination phone number: ";
	private static String TEXT = "Write a text of the message: ";
	private static String POSTFIX = "%nThis program is a part of jSMS4Pi java library. For more see http://jsms4pi.com%nAutor: Václav Burda%n";

	private Tool() {
	}

	public static String iteractiveSelectionPort(String[] portNames) throws IOException {

		if (portNames.length == 0) {
			System.out.println(NO_SERIAL);
			System.exit(1);
		}

		if (portNames.length == 1) {
			return portNames[0];
		}

		do {
			System.out.println(SELECT_PORT_TITLE);

			int i = 1;
			for (; i <= portNames.length; i++) {
				System.out.println(i + " - " + portNames[i - 1]);
			}

			System.out.println(String.format(SELECT_PORT, i - 1));

			String line = getSystemIn().readLine();

			System.out.println(line);

			try {
				int num = Integer.parseInt(line);
				if (num > 0 && num <= portNames.length) {
					return portNames[num - 1];
				}
			} catch (NumberFormatException e) {
				// nothing
			}

		} while (true);
	}

	public static void pressEnter() throws IOException {
		pressEnterTo(PROGRAM_START);
	}

	public static void pressEnterExit() throws IOException {
		pressEnterTo(PROGRAM_FINISH);
	}

	public static void pressEnterTo(String reason) throws IOException {
		System.out.println(String.format(PRESS_ENTER, reason));
		getSystemIn().readLine();
	}

	public static String portOrNull(String[] args) {
		return existParameterWithValue(args, "-p");
	}

	public static String selectionPort(String[] args, String[] portNames) throws IOException {
		String port = existParameterWithValue(args, "-p");

		if (port == null) {
			port = iteractiveSelectionPort(portNames);
		}
		return port;
	}

	public static void showHelp(String[] args, String helpText) {
		for (String arg : args) {
			if (arg.equals("-h") || arg.equals("--h") || arg.equals("-help") || arg.equals("--help")) {
				System.out.println(helpText);
				System.out.println(String.format(POSTFIX));
				System.exit(0);
				break;
			}
		}
	}

	public static void showVersion(String[] args, String version) {
		for (String arg : args) {
			if (arg.equals("-version") || arg.equals("--version")) {
				System.out.println("Version: " + version);
				System.exit(0);
				break;
			}
		}
	}

	public static String destNumber(String[] args, String text) throws IOException {
		String number = existParameterWithValue(args, "-d");

		if (number == null) {
			System.out.println(text);
			number = getSystemIn().readLine();
		}

		return number;
	}

	public static String destNumber(String[] args) throws IOException {
		return destNumber(args, DEST_NUMBER);
	}

	public static String text(String[] args) throws IOException {
		String t = existParameterWithValue(args, "-t");

		if (t == null) {
			System.out.println(TEXT);
			t = getSystemIn().readLine();
		}

		return t;
	}

	public static String serviceNumer(String[] args) throws IOException {
		return existParameterWithValue(args, "-s");
	}

	public static boolean skipCall(String[] args) {
		return existParameter(args, "--skip-call");
	}

	public static boolean skipSmsTe(String[] args) {
		return existParameter(args, "--skip-sms-te");
	}

	public static boolean skipSms(String[] args) {
		return existParameter(args, "--skip-sms");
	}

	public static int[] boudrates(String[] args) throws IOException {
		String s = existParameterWithValue(args, "-b");

		if (s != null) {
			String[] boundArr = s.split(",");
			int[] boudrate = new int[boundArr.length];

			for (int ii = 0; ii < boundArr.length; ii++) {
				try {
					boudrate[ii] = Integer.parseInt(boundArr[ii].trim());
				} catch (NumberFormatException e) {
					System.err.println("'" + boundArr[ii].trim() + "' is not valid number. Exit");
					System.exit(1);
				}
			}
			return boudrate;
		}

		return null;
	}

	public static boolean verbose(String[] args) {
		return existParameter(args, "-v");
	}

	public static LoggerContext activeLoggingToFile() {
		ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();

		builder.setStatusLevel(Level.ERROR);
		builder.setConfigurationName("BuilderTest");

		builder.add(builder.newFilter("ThresholdFilter", Filter.Result.ACCEPT, Filter.Result.NEUTRAL)
				.addAttribute("level", Level.DEBUG));

		AppenderComponentBuilder appenderBuilder = builder.newAppender("file", "file");
		appenderBuilder.addAttribute("fileName", "jSMS4Pi.log");

		appenderBuilder.add(builder.newLayout("PatternLayout").addAttribute("pattern", "%d %t %-5p %c{2} - %m%n"));
		builder.add(appenderBuilder);

		builder.add(builder.newRootLogger(Level.INFO).add(builder.newAppenderRef("file")));

		return Configurator.initialize(builder.build());
	}

	private static String existParameterWithValue(String[] args, String param) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals(param) && i + 1 < args.length) {
				return args[i + 1];
			}
		}
		return null;
	}

	private static boolean existParameter(String[] args, String param) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals(param)) {
				return true;
			}
		}
		return false;
	}

	private static BufferedReader getSystemIn() {
		return new BufferedReader(new InputStreamReader(System.in));
	}
}
