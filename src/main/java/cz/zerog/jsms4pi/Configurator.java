package cz.zerog.jsms4pi;

/*
 * #%L
 /*
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
import cz.zerog.jsms4pi.at.CNMI;
import cz.zerog.jsms4pi.tool.TypeOfMemory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author zerog
 */
public class Configurator {

    private final Logger log = LogManager.getLogger();

    private final Properties defaultProperties;
    private Properties currentProperties;

    public Configurator() {
        defaultProperties = getPropeties("modem/default.properties");
        if (defaultProperties == null) {
            log.error("Cannot load default properties!");
        }
    }

    void selectModem(ModemInformation modemInfo) {
        for (String pathName : modemInfo.getNames()) {
            Properties prop = getPropeties(pathName+".properties");
            log.info("Try find setting in file '{}' ", pathName);
            if (prop != null) {
                log.info("Used '{}' setting", pathName);
                currentProperties = prop;
                return;
            }
        }
        log.info("Used default setting");
        currentProperties = defaultProperties;
    }
    
    
    public void printAll() {
        System.out.println(" == Modem setting properties ==");
        for(String key : currentProperties.stringPropertyNames()) {
            System.out.println(key+" = "+currentProperties.getProperty(key));
        }
        System.out.println("====");
    }

    /**
     *
     *
     * @return
     */
    TypeOfMemory getMemory1RW() {
        return TypeOfMemory.valueOf(currentProperties.getProperty("memory.rw"));
    }

    /**
     *
     * @return
     */
    TypeOfMemory getMemory2Storage() {
        return TypeOfMemory.valueOf(currentProperties.getProperty("memory.storage"));
    }

    /**
     *
     * @return
     */
    TypeOfMemory getMemory3Rec() {
        return TypeOfMemory.valueOf(currentProperties.getProperty("memory.received"));
    }

    /**
     *
     * @return
     */
    CNMI.Mode getCNMIMode() {
        return CNMI.Mode.valueOf(Integer.parseInt(currentProperties.getProperty("CNMI.mode")));
    }

    CNMI.Mt getCNMIMt() {
        return CNMI.Mt.valueOf(Integer.parseInt(currentProperties.getProperty("CNMI.mt")));
    }

    CNMI.Bm getCNMIBm() {
        return CNMI.Bm.valueOf(Integer.parseInt(currentProperties.getProperty("CNMI.bm")));
    }

    CNMI.Ds getCNMIDs() {
        return CNMI.Ds.valueOf(Integer.parseInt(currentProperties.getProperty("CNMI.ds")));
    }

    private Properties getPropeties(String path) {
        Properties p = null;
        try {
            InputStream is = Configurator.class.getClassLoader().getResourceAsStream(path);
            if (is == null) {
                return null;
            }

            p = new Properties(defaultProperties);
            p.load(is);

        } catch (IOException ex) {
            log.warn(ex, ex);
        }
        return p;
    }
}
