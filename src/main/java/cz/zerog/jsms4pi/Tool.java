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
        
        if(portNames.length == 1) {
            return portNames[0];
        }

        do {

            System.out.println("Select a serial port:");

            int i = 1;
            for (; i <= portNames.length; i++) {
                System.out.println(i + " - " + portNames[i - 1]);
            }

            System.out.print("Select port name [1-" + (i-1) + "]: ");

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

}
