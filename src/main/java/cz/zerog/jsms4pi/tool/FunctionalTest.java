package cz.zerog.jsms4pi.tool;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.zerog.jsms4pi.Configurator;
import cz.zerog.jsms4pi.Gateway;
import cz.zerog.jsms4pi.Modem;
import cz.zerog.jsms4pi.ModemInformation;
import cz.zerog.jsms4pi.NullGateway;
import cz.zerog.jsms4pi.SerialModem;
import cz.zerog.jsms4pi.at.AT;
import cz.zerog.jsms4pi.at.ATE0;
import cz.zerog.jsms4pi.at.ATZ;
import cz.zerog.jsms4pi.at.CGMM;
import cz.zerog.jsms4pi.at.CGSN;
import cz.zerog.jsms4pi.at.CLIP;
import cz.zerog.jsms4pi.at.CMGD;
import cz.zerog.jsms4pi.at.CMGF;
import cz.zerog.jsms4pi.at.CMGR;
import cz.zerog.jsms4pi.at.CMGS;
import cz.zerog.jsms4pi.at.CMGSText;
import cz.zerog.jsms4pi.at.CNMI;
import cz.zerog.jsms4pi.at.CPMS;
import cz.zerog.jsms4pi.at.CPMSsupport;
import cz.zerog.jsms4pi.at.CSCA;
import cz.zerog.jsms4pi.at.CSMP;
import cz.zerog.jsms4pi.at.GMI;
import cz.zerog.jsms4pi.example.Tool;
import cz.zerog.jsms4pi.exception.AtParseException;
import cz.zerog.jsms4pi.exception.GatewayException;
import cz.zerog.jsms4pi.notification.CDS;
import cz.zerog.jsms4pi.notification.CDSI;
import cz.zerog.jsms4pi.notification.CLIPN;
import cz.zerog.jsms4pi.notification.CMT;
import cz.zerog.jsms4pi.notification.CMTI;
import cz.zerog.jsms4pi.notification.Notification;
import cz.zerog.jsms4pi.notification.RING;

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
 *
 * @author zerog
 */
public class FunctionalTest extends NullGateway {

	private final Logger log = LogManager.getLogger();

	private final static String VERSION = "1.0";

	private Modem modem;

	private ModemInformation modemInfo;

	private String messageText = "jSMS4Pi, a simple Java library for sending SMS";

	private Thread dotter;

	private volatile boolean oneCall = false;

	// info
	private boolean testInfo = false;

	// call
	private boolean testInboundCall = false;

	// sms default
	private boolean testSendDefault = false;
	private boolean testReceivedDefault = false;
	private boolean testReceivedStatusDefault = false;

	// sms into TE
	private boolean testSendTE = false;
	private boolean testReceivedTE = false;
	private boolean testReceivedStatusTE = false;

	/**
	 * Constructor, main part of the program
	 * 
	 * @param port
	 * @param destination
	 * @param service
	 */
	public FunctionalTest(String port, String destination, String service, boolean skipCall, boolean skipSmsIntoTE,
			boolean skipSmsDefault) {

		/*
		 * Storage
		 */
		try {
			supportedStorage(port);
			Thread.sleep(500);
		} catch (InterruptedException e) {
			log.error(e, e);
		} catch (GatewayException e) {
			log.error(e, e);
			System.out.println("Port: " + e.getPortName() + ", Error: " + e.getErrorMessage());
		}

		/*
		 * Modem Info
		 */
		try {
			modemInfo = getModemInfo(port);
			testInfo = modemInfo != null;
			Thread.sleep(500);
		} catch (InterruptedException e) {
			log.error(e, e);
			testInfo = false;
		} catch (GatewayException e) {
			log.error(e, e);
			testInfo = false;
			System.out.println("Port: " + e.getPortName() + ", Error: " + e.getErrorMessage());
		}

		/*
		 * Inbound call
		 */
		if (!skipCall) {
			try {
				testInboundCall = inboundCall(port);
				Thread.sleep(500);
			} catch (IOException | InterruptedException e) {
				log.error(e, e);
				testInboundCall = false;
			} catch (GatewayException e) {
				log.error(e, e);
				testInboundCall = false;
				System.out.println("Port: " + e.getPortName() + ", Error: " + e.getErrorMessage());
			}
		}

		/*
		 * Send, Received and Delivery message default way
		 */
		if (!skipSmsDefault) {
			try {

				System.out.print("Send/Received SMS (default method)");
				log.info("-- SMS default --");

				int timeout = 30;// s
				dotter = new Thread(new Dotter(timeout));
				dotter.start();

				if (sendMessageDefault(port, destination, service)) {
					testSendDefault = true;

					synchronized (this) {
						try {
							wait(timeout * 1000);
						} catch (InterruptedException e) {
							System.out.println("Interupted");
						}
					}
				} else {
					testSendDefault = false;
				}

				dotter.interrupt();
				Thread.sleep(500);
			} catch (IOException | InterruptedException e) {
				log.error(e, e);
			} catch (GatewayException e) {
				log.error(e, e);
				System.out.println("Port: " + e.getPortName() + ", Error: " + e.getErrorMessage());
			} finally {
				dotter.interrupt();

				try {
					modem.close();
				} catch (GatewayException e) {
					log.error(e, e);
				}
			}
		}

		/*
		 * Send, Received and Delivery message directly to TE
		 */
		if (!skipSmsIntoTE) {
			try {

				System.out.print("Send/Received SMS (second method)");
				log.info("-- SMS second --");

				int timeout = 30;// s
				dotter = new Thread(new Dotter(timeout));
				dotter.start();

				if (sendMessageIntoTE(port, destination, service)) {
					testSendTE = true;

					synchronized (this) {
						try {
							wait(timeout * 1000);
						} catch (InterruptedException e) {
							System.out.println("Interupted");
						}
					}

				} else {
					testSendTE = false;
				}

			} catch (IOException e) {
				log.error(e, e);
			} catch (GatewayException e) {
				log.error(e, e);
				System.out.println("Port: " + e.getPortName() + ", Error: " + e.getErrorMessage());
			} finally {
				dotter.interrupt();

				try {
					modem.close();
				} catch (GatewayException e) {
					log.error(e, e);
				}
			}
		}

		/*
		 * Result
		 */
		System.out.println(String.format("Result:%n"));
		printResult("Geting an information about manufacturer of the modem and imei", testInfo);

		if (!skipCall) {
			printResult("Inbound call detection", testInboundCall);
		}
		if (!skipSmsDefault) {
			printResult("Sending test text message (default method)", testSendDefault);
			printResult("Detecting inbound text message (default method)", testReceivedDefault);
			printResult("Testing delivery status detection (default method)", testReceivedStatusDefault);
		}
		if (!skipSmsIntoTE) {
			printResult("Sending test text message (second method)", testSendTE);
			printResult("Detecting inbound text message (second method)", testReceivedTE);
			printResult("Testing delivery status detection (second method)", testReceivedStatusTE);
		}

		if (testSendTE && testReceivedTE && testReceivedStatusTE) {

			Map<String, String> settings = new HashMap<String, String>();

			// message directly to TE
			settings.put(Configurator.CNMI_MODE, "1");
			settings.put(Configurator.CNMI_MT, "2");
			settings.put(Configurator.CNMI_BM, "0");
			settings.put(Configurator.CNMI_DS, "1");

			if (Configurator.generateFile(modemInfo, settings)) {
				System.out.println(String
						.format("%nConfiguration file to send/received SMS through second method was generated."));
				System.out.println("File name: " + modemInfo.getManugaturerAndModem() + ".preperties");
			}
		}

		System.out.println("\n\nTest finished");
	}

	/**
	 * Notification process
	 */
	@Override
	public void notify(Notification notification) {
		// System.out.println("notification: " +
		// notification.getClass().getName());

		try {
			/*
			 * Incoming call
			 */
			if (notification instanceof RING) {
				RING ring = (RING) notification;
				printCallInfo(ring.getCallerId());
				return;
			}
			/*
			 * Incoming call
			 */
			if (notification instanceof CLIPN) {
				CLIPN ring = (CLIPN) notification;
				printCallInfo(ring.getCallerId());
				return;
			}
			/*
			 * Delivery status default
			 */
			if (notification instanceof CDSI) {

				boolean fail = false;

				try {
					CDSI cdsi = (CDSI) notification;

					if (!modem.send(new AT()).isStatusOK()) {
						fail = true;
					}
					// change to memory from CDSI notification
					if (fail || !modem.send(new CPMS(cdsi.getMemoryType())).isStatusOK()) {
						fail = true;
					}
					// read status repord
					if (fail || !modem.send(new CMGR(CMGR.Mode.SMS_STATUS_REPORT, cdsi.getSMSIndex())).isStatusOK()) {
						fail = true;
					}
					// delete status repord
					if (fail || !modem.send(new CMGD(cdsi.getSMSIndex())).isStatusOK()) {
						fail = true;
					}
					// change back to main memory
					if (fail || !modem.send(new CPMS(TypeOfMemory.SM)).isStatusOK()) {
						fail = true;
					}

				} catch (Exception e) {
					log.warn(e, e);
					fail = true;
				} finally {

					// stop prints dots
					try {
						dotter.interrupt();
						Thread.sleep(200);
					} catch (InterruptedException e) {
					}

					synchronized (this) {
						notifyAll();
					}

					testReceivedStatusDefault = !fail;
				}
				return;
			}

			/*
			 * Delivery status into TE
			 */
			if (notification instanceof CDS) {
				CDS cds = (CDS) notification;

				// stop prints dots
				try {
					dotter.interrupt();
					Thread.sleep(200);
				} catch (InterruptedException e) {
				}

				synchronized (this) {
					notifyAll();
				}

				testReceivedStatusTE = true;

				return;
			}

			/*
			 * Incoming SMS Default
			 */
			if (notification instanceof CMTI) {

				boolean fail = false;

				CMTI cmti = (CMTI) notification;
				CMGR cmgr = new CMGR(CMGR.Mode.SMS_DELIVERY, cmti.getSMSIndex());

				try {

					// change to memory from CMTI notification
					if (!modem.send(new CPMS(cmti.getMemoryType())).isStatusOK()) {
						fail = true;
					}
					// read sms

					if (fail || !modem.send(cmgr).isStatusOK()) {
						fail = true;
					}
					// delete sms
					if (fail || !modem.send(new CMGD(cmti.getSMSIndex())).isStatusOK()) {
						fail = true;
					}
					// change back to main memory
					if (fail || !modem.send(new CPMS(TypeOfMemory.SM)).isStatusOK()) {
						fail = true;
					}
				} catch (Exception e) {
					log.warn(e, e);
					fail = true;
				} finally {

					testReceivedDefault = !fail && messageText.equals(cmgr.getText());

					if (messageText.equals(cmgr.getText())) {
						return;
					}

					System.out.println("A malformed or unexpected inbound message ");
					System.out.println("Text: '" + cmgr.getText() + "', Source tel. number: '" + cmgr.getOa() + "'");
				}
				return;
			}

			/*
			 * Incoming SMS into TE
			 */
			if (notification instanceof CMT) {
				CMT cmt = (CMT) notification;

				if (testReceivedTE = messageText.equals(cmt.getData())) {
					return;
				}

				System.out.println("A malformed or unexception inbound message");
				System.out.println("Text: '" + cmt.getData() + "', Source tel. number: '" + cmt.getOa() + "'");

				return;
			}

		} catch (GatewayException | AtParseException e) {
			log.error(e, e);
		}
	}

	/*
	 * Private methods
	 */

	private boolean inboundCall(String port) throws GatewayException, IOException {

		log.info("-- Inbound Call --");

		try {
			if (!modemInit(port, this)) {
				return false;
			}
			// show caller ID when RING notify
			if (!modem.send(new CLIP(true)).isStatusOK()) {
				return false;
			}

			System.out.print("Now call the modem.  After dial tone hang up.");

			int timer = 30;// s
			dotter = new Thread(new Dotter(timer));
			dotter.start();

			synchronized (this) {
				try {
					wait(timer * 1000);
				} catch (InterruptedException e) {
					System.out.println("Interupted");
				}
			}

			if (oneCall) {
				System.out.println("Is this information correct? [y/n]");
				char c = (char) System.in.read();
				if (c == 'y' || c == 'Y') {
					return true;
				}
			}

			return false;
		} finally {
			dotter.interrupt();
			modem.close();
		}
	}

	private void supportedStorage(String port) throws GatewayException {

		log.info("-- Supported Storage --");

		try {
			modemInit(port, new NullGateway());
			CPMSsupport cpmss = new CPMSsupport();
			if (modem.send(cpmss).isStatusOK()) {
				TypeOfMemory[] mem1 = cpmss.getMemory1();
				TypeOfMemory[] mem2 = cpmss.getMemory2();
				TypeOfMemory[] mem3 = cpmss.getMemory3();

				for (int i = 0; i < mem1.length; i++) {
					log.info("Memory1: '{}'", mem1[i]);
				}

				for (int i = 0; i < mem2.length; i++) {
					log.info("Memory2: '{}'", mem2[i]);
				}

				for (int i = 0; i < mem3.length; i++) {
					log.info("Memory3: '{}'", mem3[i]);
				}
			}

		} finally {
			modem.close();
		}

	}

	public ModemInformation getModemInfo(String port) throws GatewayException {

		log.info("-- Modem Info --");

		try {

			// Manufacturer name
			if (!modemInit(port, new NullGateway())) {
				return null;
			}

			ModemInformation info = new ModemInformation();

			GMI gmi = new GMI();
			if (!modem.send(gmi).isStatusOK()) {
				return null;
			}
			info.setManufacturer(gmi.getManufaturer());

			// Model description
			CGMM cgmm = new CGMM();
			if (!modem.send(cgmm).isStatusOK()) {
				return null;
			}
			info.setModelAndCapabilities(cgmm.getModel());

			// IMEI
			CGSN cgsn = new CGSN();
			if (!modem.send(cgsn).isStatusOK()) {
				return null;
			}
			info.setImei(cgsn.getIMEI());

			return info;

		} finally {
			modem.close();
		}
	}

	/**
	 * Send message and delivery save into modem storage
	 * 
	 * @param port
	 * @param modemNumber
	 * @param service
	 * @throws GatewayException
	 * @throws IOException
	 */
	private boolean sendMessageDefault(String port, String modemNumber, String service)
			throws GatewayException, IOException {

		// save delivery into memory
		return send(port, modemNumber, service, new CNMI(CNMI.Mode._2, CNMI.Mt.NOTIFI_1, CNMI.Bm.NO_CBM_NOTIFI_0,
				CNMI.Ds.STATUS_REPORT_NOTIFI_IF_STORED_2));
	}

	/**
	 * Send message and notify into TE
	 * 
	 * 
	 * CNMI.mode=1 CNMI.mt=2 CNMI.bm=0 CNMI.ds=1
	 * 
	 * @param port
	 * @param modemNumber
	 * @param service
	 * @throws GatewayException
	 * @throws IOException
	 */
	private boolean sendMessageIntoTE(String port, String modemNumber, String service)
			throws GatewayException, IOException {

		// save delivery into memory
		return send(port, modemNumber, service, new CNMI(CNMI.Mode._1, CNMI.Mt.DIRECT_NOTIFI_BESIDES_CLASS2_2,
				CNMI.Bm.NO_CBM_NOTIFI_0, CNMI.Ds.STATUS_REPORT_NOTIFI_1));
	}

	private boolean send(String port, String modemNumber, String service, CNMI cnmi)
			throws GatewayException, IOException {

		if (!modemInit(port, this)) {
			return false;
		}

		if (service == null) {
			service = "1234";
		}

		// set text mode
		if (!modem.send(new CMGF(CMGF.Mode.TEXT)).isStatusOK()) {
			return false;
		}
		// use delivery report and time out expiration
		if (!modem.send(new CSMP(CSMP.DELIVERY_REPORT | CSMP.VALIDITY_PERIOD)).isStatusOK()) {
			return false;
		}
		// set stores
		if (!modem.send(new CPMS(TypeOfMemory.ME, TypeOfMemory.ME, TypeOfMemory.ME)).isStatusOK()) {
			return false;
		}
		// notifications
		if (!modem.send(cnmi).isStatusOK()) {
			return false;
		}
		// set sms service
		if (!modem.send(new CSCA(service)).isStatusOK()) {
			return false;
		}
		// destination number
		if (!modem.send(new CMGS(modemNumber)).isStatusOK()) {
			return false;
		}
		// text
		if (!modem.send(new CMGSText(messageText)).isStatusOK()) {
			return false;
		}
		return true;
	}

	private boolean modemInit(String port, Gateway gateway) throws GatewayException {

		modem = new SerialModem(port);
		modem.setGatewayListener(gateway);
		modem.open();

		if (!modem.send(new AT()).isStatusOK()) {
			return false;
		}
		if (!modem.send(new ATZ()).isStatusOK()) {
			return false;
		}
		if (!modem.send(new ATE0()).isStatusOK()) {
			return false;
		}
		return true;
	}

	private void printCallInfo(String tel) throws GatewayException {

		if (oneCall) {
			return;
		}

		try {
			dotter.interrupt();
			Thread.sleep(200);
		} catch (InterruptedException e) {
		}

		oneCall = true;
		System.out.println("Detected an inbound call. Caller ID: " + tel);

		synchronized (this) {
			notifyAll();
		}
	}

	private void printResult(String text, boolean result) {
		System.out.println(" " + text);
		if (result) {
			System.out.println("   - OK");
		} else {
			System.out.println("   - FAIL");
		}
	}

	/**
	 * Main
	 * 
	 * @param args
	 * @throws IOException
	 * @throws GatewayException
	 */
	public static void main(String[] args) throws IOException, GatewayException {
		System.out.println(String.format("The program will test if the modem supports required functions%n"
				+ "   - manufacturing of information%n" + "   - inbound calls detections%n"
				+ "   - inbound / outbound text messages%n"
				+ "%nIf the library is missing configuration file for the modem, it will generate it.%n%n" +

		"Warning: This test will try to send two text messages to itself to test inbount / outbound messages.%n%n ---%n"));

		Tool.showHelp(args,
				String.format("Arguments:%n   -p <port name> 	Name of a serial port%n"
						+ "   -d <number> 		Modem phone number%n"
						+ "   -s <number> 		Number of Message Service Center (optionally)%n"
						+ "   --skip-call 		Skip a inbound call test (optionally)%n"
						+ "   --skip-sms 		Skip send/received sms and delivery report through default method (optionally)%n"
						+ "   --skip-sms-te 	Skip send/received sms and delivery report through second method (optionally)%n"
						+ "   -h 			Print Help (this message) and exit%n"
						+ "   -version 		Print version information and exit%n" +

		"%nInteractive mode is activated when program is started without parameters"));

		Tool.showVersion(args, VERSION);

		String port = Tool.selectionPort(args);
		String dest = Tool.destNumber(args, "Write the modem phone number: ");
		String service = Tool.serviceNumer(args);

		boolean skipCall = Tool.skipCall(args);
		boolean skipSmsIntoTE = Tool.skipSmsTe(args);
		boolean skipSmsDefault = Tool.skipSms(args);

		System.out.println(String.format("%n--- Summary ---"));
		System.out.println("Serial port: " + port);
		System.out.println("Modem phone number: " + dest);

		if (service != null) {
			System.out.println("Number of Message Service Center: " + service);
		}

		System.out.println("");

		if (skipCall) {
			System.out.println("Skipping inbound call test ");
		}
		if (skipSmsIntoTE) {
			System.out.println("Skipping send/received SMS through second method (direct into TE)");
		}
		if (skipSmsDefault) {
			System.out.println("Skipping send/received SMS default");
		}

		Tool.pressEnter();

		Tool.activeLoggingToFile();
		new FunctionalTest(port, dest, service, skipCall, skipSmsIntoTE, skipSmsDefault);
	}

	/**
	 * Printing dot until interrupt or time out.
	 * 
	 * @author zerog
	 *
	 */
	private class Dotter implements Runnable {

		private final int second;

		public Dotter(int second) {
			this.second = second;
		}

		@Override
		public void run() {
			System.out.print(String.format(" [waiting, max " + second + "s]%n"));
			int i = 0;

			try {
				while (!interrupted() && i <= second) {
					System.out.print(" .");
					Thread.sleep(1000);
					i++;
				}
			} catch (InterruptedException e) {
				log.info("countdown interrupted");
				// interrupted
			}
			System.out.print(String.format("+%n%n"));
		}
	}
}
