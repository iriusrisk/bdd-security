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
package net.continuumsecurity.behaviour;

import net.continuumsecurity.Credentials;

public interface ILogin {

    /*
        Assume that the browser is open on the login page, and perform the login.
        A UserPassCredentials class exists for simple single auth authentication.
     */
	void login(Credentials credentials);

    /*
        Describe how to navigate from the base URL to the login page.
     */
	void openLoginPage();

    /*
        Determine whether the user is currently logged in or not.
        This should involve first making a request for a resource and then determining whether the
        user is logged in based on the response.
        To improve robustness, the call to the resource should be possible from any location
        in the application.
     */
	boolean isLoggedIn();
}
