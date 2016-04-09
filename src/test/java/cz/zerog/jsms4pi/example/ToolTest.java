package cz.zerog.jsms4pi.example;

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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.contrib.java.lang.system.TextFromStandardInputStream;

public class ToolTest {

	@Rule
	public final ExpectedSystemExit exit = ExpectedSystemExit.none();

	@Rule
	public final SystemOutRule systemOutRule = new SystemOutRule().enableLog().mute();

	@Rule
	public final SystemErrRule systemErrRule = new SystemErrRule().enableLog().mute();

	@Rule
	public final TextFromStandardInputStream systemInMock = TextFromStandardInputStream.emptyStandardInputStream();

	private String getToolText(String textName) throws Exception {
		String value;

		try {
			Field field = Tool.class.getDeclaredField(textName);
			field.setAccessible(true);
			value = (String) field.get(new String());
		} catch (Exception e) {
			throw e;
		}
		return value;
	}

	@Test
	public void testIteractiveSelectionPortNonePort() throws Exception {

		exit.expectSystemExit();

		String ports[] = {};
		Tool.iteractiveSelectionPort(ports);

		assertEquals(getToolText("NO_SERIAL"), systemOutRule.getLog());
	}

	@Test
	public void testIteractiveSelectionPortOnePort() throws Exception {
		String ports[] = { "port1" };
		assertEquals("port1", Tool.iteractiveSelectionPort(ports));
	}

	@Test
	public void testIteractiveSelectionPortMorePorts() throws Exception {
		String ports[] = { "portA", "portB" };

		systemInMock.provideLines("1");

		Tool.iteractiveSelectionPort(ports);

		String expectedText = getToolText("SELECT_PORT_TITLE") + System.lineSeparator() + "1 - portA"
				+ System.lineSeparator() + "2 - portB" + System.lineSeparator()
				+ String.format(getToolText("SELECT_PORT"), 2) + System.lineSeparator() + "1" + System.lineSeparator();

		assertEquals(expectedText, systemOutRule.getLog());
	}

	@Test
	public void testPressEnter() throws Exception {
		Tool.pressEnter();

		assertEquals(String.format(getToolText("PRESS_ENTER") + System.lineSeparator(), getToolText("PROGRAM_START")),
				systemOutRule.getLog());
	}

	@Test
	public void testPressEnterExit() throws Exception {
		Tool.pressEnterExit();

		assertEquals(String.format(getToolText("PRESS_ENTER") + System.lineSeparator(), getToolText("PROGRAM_FINISH")),
				systemOutRule.getLog());
	}

	@Test
	public void testPressEnterTo() throws Exception {
		Tool.pressEnterTo("REASON");

		assertEquals(String.format(getToolText("PRESS_ENTER") + System.lineSeparator(), "REASON"),
				systemOutRule.getLog());
	}

	@Test
	public void testPortOrNullPort() throws Exception {
		String[] ports = { "-p", "pname" };

		assertEquals("pname", Tool.portOrNull(ports));
	}

	@Test
	public void testPortOrNullNull() throws Exception {
		String[] ports = { "-a", "pname", "-b" };
		assertNull(Tool.portOrNull(ports));
	}

	@Test
	public void testPortOrNullNull2() throws Exception {
		String[] ports = { "-a", "pname", "-p" };
		assertNull(Tool.portOrNull(ports));
	}

	@Test
	public void testSelectionPort() throws Exception {
		String[] args = { "-a", "pname", "-p", "port1" };
		String[] ports = { "port1", "port2" };
		assertEquals("port1", Tool.selectionPort(args, ports));
	}

	@Test
	public void testShowHelph() throws Exception {
		String[] args = { "-h", "pname", "-p", "port1" };
		String text = "help...";

		testHelpText(args, text);
	}

	@Test
	public void testShowHelphh() throws Exception {
		String[] args = { "--h", "pname", "-p", "port1" };
		String text = "help...";

		testHelpText(args, text);
	}

	@Test
	public void testShowHelphelp() throws Exception {
		String[] args = { "-a", "pname", "-p", "port1", "--help" };
		String text = "help...";

		testHelpText(args, text);
	}

	@Test
	public void testShowHelphhelp() throws Exception {
		String[] args = { "-h", "pname", "--help", "port1" };
		String text = "help...";

		testHelpText(args, text);
	}

	private void testHelpText(String[] args, String text) throws Exception {
		exit.expectSystemExit();
		Tool.showHelp(args, text);
		assertEquals(text + System.lineSeparator() + String.format(getToolText("POSTFIX")) + System.lineSeparator(),
				systemOutRule.getLog());
	}

	@Test
	public void testShowVersionV() {
		String[] args = { "-h", "-version", "--help", "port1" };
		String version = "asdd21";

		testVersion(args, version);
	}

	@Test
	public void testShowVersionVV() {
		String[] args = { "-h", "--version", "--help", "port1" };
		String version = "asdd21s";

		testVersion(args, version);
	}

	private void testVersion(String[] args, String version) {
		exit.expectSystemExit();
		Tool.showVersion(args, version);
		assertEquals("Version: " + version + System.lineSeparator(), systemOutRule.getLog());
	}

	@Test
	public void testDestNumberStringArrayString() throws Exception {
		String[] args = { "-d", "4321", "--help", "port1" };
		String text = "text";

		assertEquals("4321", Tool.destNumber(args, text));
	}

	@Test
	public void testDestNumberStringArrayString2() throws Exception {
		String[] args = { "-n", "4321", "--help", "-d" };
		String text = "text";

		systemInMock.provideLines("123456");

		assertEquals("123456", Tool.destNumber(args, text));
		assertEquals(text + System.lineSeparator(), systemOutRule.getLog());
	}

	@Test
	public void testDestNumberStringArray() throws Exception {
		String[] args = { "-n", "4321", "--help", "-d" };
		String text = getToolText("DEST_NUMBER");

		systemInMock.provideLines("123456");

		assertEquals("123456", Tool.destNumber(args, text));
		assertEquals(text + System.lineSeparator(), systemOutRule.getLog());
	}

	@Test
	public void testText() throws Exception {
		String[] args = { "-t", "tteexxtt", "--help", "-d" };
		assertEquals("tteexxtt", Tool.text(args));
	}

	@Test
	public void testTextI() throws Exception {
		String[] args = { "-d", "tteexxtt", "--help", "-t" };
		String text = getToolText("TEXT");

		systemInMock.provideLines("123456");

		assertEquals("123456", Tool.text(args));
		assertEquals(text + System.lineSeparator(), systemOutRule.getLog());
	}

	@Test
	public void testServiceNumer() throws Exception {
		String[] args = { "-s", "tteexxtt", "--help", "-t" };
		assertEquals("tteexxtt", Tool.serviceNumer(args));
	}

	@Test
	public void testServiceNumer2() throws Exception {
		String[] args = { "-d", "tteexxtt", "--help", "-s" };
		assertNull(Tool.serviceNumer(args));
	}

	@Test
	public void testSkipCallTrue() {
		String[] args = { "-d", "tteexxtt", "--help", "-s", "--skip-call" };
		assertTrue(Tool.skipCall(args));
	}

	@Test
	public void testSkipCallFalse() {
		String[] args = { "-d", "tteexxtt", "--help", "-s", "-p-call" };
		assertFalse(Tool.skipCall(args));
	}

	@Test
	public void testSkipSmsTeTrue() {
		String[] args = { "-d", "tteexxtt", "--help", "--skip-sms-te", "--skip-call" };
		assertTrue(Tool.skipSmsTe(args));
	}

	@Test
	public void testSkipSmsTeFalse() {
		String[] args = { "-d", "tteexxtt", "--help", "--ski", "--skip-call" };
		assertFalse(Tool.skipSmsTe(args));
	}

	@Test
	public void testSkipSmsT() {
		String[] args = { "-d", "tteexxtt", "--help", "--skip-sms", "--ski", "--skip-call" };
		assertTrue(Tool.skipSms(args));
	}

	@Test
	public void testSkipSmsF() {
		String[] args = { "-d", "tteexxtt", "--help", "--ip-sms", "--ski", "--skip-call" };
		assertFalse(Tool.skipSms(args));
	}

	@Test
	public void testBoudrates() throws Exception {
		String[] args = { "-b", "123", "--help", "--ip-sms", "--ski", "-v" };
		int[] b = new int[1];
		b[0] = 123;
		assertArrayEquals(b, Tool.boudrates(args));
	}

	@Test
	public void testBoudrates2() throws Exception {
		String[] args = { "-b", "123,432,123", "--help", "--ip-sms", "--ski", "-v" };
		int[] b = new int[3];
		b[0] = 123;
		b[1] = 432;
		b[2] = 123;
		assertArrayEquals(b, Tool.boudrates(args));
	}

	@Test
	public void testBoudrates3() throws Exception {
		String[] args = { "-b", "123,foo,123", "--help", "--ip-sms", "--ski", "-v" };
		exit.expectSystemExit();
		Tool.boudrates(args);
		assertEquals("'foo' is not valid number. Exit" + System.lineSeparator(), systemErrRule.getLog());
	}

	@Test
	public void testBoudratesNull() throws Exception {
		String[] args = { "-k", "123", "--help", "--ip-sms", "--ski", "-v" };
		assertNull(Tool.boudrates(args));
	}

	@Test
	public void testVerboseT() {
		String[] args = { "-d", "tteexxtt", "--help", "--ip-sms", "--ski", "-v" };
		assertTrue(Tool.verbose(args));
	}

	@Test
	public void testVerboseF() {
		String[] args = { "-d", "tteexxtt", "--help", "--ip-sms", "--ski", "-" };
		assertFalse(Tool.verbose(args));
	}
}
