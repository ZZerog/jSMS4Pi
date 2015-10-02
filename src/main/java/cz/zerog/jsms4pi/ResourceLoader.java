/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import org.apache.logging.log4j.core.config.plugins.util.ResolverUtil.Test;

public class ResourceLoader {

    public static void main(String args[]) {
        InputStream in = ResourceLoader.class.getResourceAsStream("app.properties");
        Properties config = new Properties();
        try {
            config.load(in);
            System.out.println(config.getProperty("name"));
            System.out.println(config.getProperty("version"));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        URL resourceURL = Test.class.getResource("app.properties");
        Properties appConfig = new Properties();
        try {
            appConfig.load(resourceURL.openStream());
            System.out.println(appConfig.getProperty("name"));
            System.out.println(appConfig.getProperty("version"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
