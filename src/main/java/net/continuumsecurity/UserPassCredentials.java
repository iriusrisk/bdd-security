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
package net.continuumsecurity;

public class UserPassCredentials extends Credentials {
	
	public UserPassCredentials() {
		super();
	}
	
	public UserPassCredentials(Credentials creds) {
		super("username",creds.get("username"),"password",creds.get("password"));
	}
	
	public UserPassCredentials(String username,String password) {
		super("username",username,"password",password);
	}

	public String getUsername() {
		return creds.get("username");
	}

	public void setUsername(String username) {
		this.creds.put("username", username);
	}

	public String getPassword() {
		return creds.get("password");
	}

	public void setPassword(String password) {
		this.creds.put("password", password);
	}
	
}
