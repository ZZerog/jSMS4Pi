package cz.zerog.jsms4pi;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import jssc.SerialPortList;

/**
 *
 * @author zerog
 */
public class Tool {

    public static String selectionPort(BufferedReader reader) throws IOException {
        String[] portNames = SerialPortList.getPortNames();

        if (portNames.length == 0) {
            System.out.println("No serial port avaible! Exit.");
            return null;
        }

        if (portNames.length == 1) {
            return portNames[0];
        }

        do {

            System.out.println("Select a serial port:");

            int i = 1;
            for (; i <= portNames.length; i++) {
                System.out.println(i + " - " + portNames[i - 1]);
            }

            System.out.print("Select port name [1-" + (i - 1) + "]: ");

            String line = reader.readLine();

            try {
                int num = Integer.parseInt(line);
                if (num > 0 && num <= portNames.length) {
                    return portNames[num - 1];
                }
            } catch (NumberFormatException e) {
                //nothing
            }

        } while (true);

    }

    public static void enter(BufferedReader reader) throws IOException {
        System.out.print("\nPress enter to continue");
        reader.readLine();
    }

    public static void main(String[] args) {
        String spatter = "CPMS:( *)\\(\\((.*)\\),\\((.*)\\),\\((.*)\\)\\)";
        Pattern pattern = Pattern.compile(spatter);

        String line = "CPMS:   ((a),(b),(c)";
        Matcher matcher = pattern.matcher(line);

        if (matcher.matches()) {
            System.out.println("OK - match");
        } else {
            System.out.println("Not match");

            int pIndex = 1;
            Pattern patt = null;
            
            boolean match = false;
            while (true) {

                while (true) {
                    try {
                        String pat = spatter.substring(0, pIndex);
                        patt = Pattern.compile(pat);
                    } catch (PatternSyntaxException e) {
                        pIndex++;
                        continue;
                    }
                    break;
                }

                for (int i = 1; i <= line.length(); i++) {
                    String newLine = line.substring(0, i);
                    if(patt.matcher(newLine).matches()) {
                        match = true;
                        //System.out.println("pattern: "+patt.pattern());
                        //System.out.println("input: "+newLine);
                        //System.out.println("");
                    }
                }
                
                if(!match) {
                    System.out.println("Error pattern on position = "+pIndex);
                    
                    printt(pIndex, spatter, line);
                    break;
                }
                
                match = false;
                pIndex++;
            }

        }
    }
    
    private static void printt(int i, String patter, String input) {
        StringBuilder sb = new StringBuilder();
        for (int j = 1; j < i; j++) {
            sb.append(" ");            
        }
        sb.append("^");
        System.out.println("Input: "+input);
        System.out.println("Pattern: "+patter);
        System.out.println("         "+sb);
    }

}
