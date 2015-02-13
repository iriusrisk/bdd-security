/*******************************************************************************
 *    BDD-Security, application security testing framework
 *
 * Copyright (C) `2014 Stephen de Vries`
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see `<http://www.gnu.org/licenses/>`.
 ******************************************************************************/
package net.continuumsecurity.jbehave;

import groovy.ui.Console;
import net.continuumsecurity.Config;
import net.continuumsecurity.web.Application;
import net.continuumsecurity.web.drivers.ProxyFactory;
import net.continuumsecurity.web.drivers.DriverFactory;
import org.apache.log4j.PropertyConfigurator;


public class WConsole {
    Application app;

    public WConsole() {
        PropertyConfigurator.configure("log4j.properties");

    }

    public void run() {
        app = Config.createApp();
        app.enableDefaultClient();
        Console console = new Console();
        console.setVariable("app",app);
        console.setVariable("proxyDriver",DriverFactory.getDriver(Config.getProxyDriver()));
        console.setVariable("proxy", ProxyFactory.getLoggingProxy());
        console.setVariable("scanner", ProxyFactory.getScanningProxy());
        console.run();
    }

    public static void main(String... args) {
        WConsole c = new WConsole();
        c.run();
    }
}
