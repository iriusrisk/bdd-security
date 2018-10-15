package net.continuumsecurity.web;

/**
 * ****************************************************************************
 * BDD-Security, application security testing framework
 * <p/>
 * Copyright (C) `2014 Stephen de Vries`
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see `<http://www.gnu.org/licenses/>`.
 * ****************************************************************************
 */

import net.continuumsecurity.clients.AuthTokenManager;
import org.apache.log4j.Logger;

public abstract class Application {
    public static Logger log = Logger.getLogger(Application.class);

    public void pause(long milliSeconds) {
        try {
            Thread.sleep(milliSeconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public abstract void enableHttpLoggingClient();

    public abstract void enableDefaultClient();
    
    public abstract AuthTokenManager getAuthTokenManager();
}

