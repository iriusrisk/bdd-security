/*******************************************************************************
 *    BDD-Security, application security testing framework
 * 
 * Copyright (C) `2012 Stephen de Vries`
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
package net.continuumsecurity.web;

import java.util.Map;

public class LoginFailedException extends Exception {
	String msg;
	
	public LoginFailedException() {
		super("Login failed.");
	}
	
	public LoginFailedException(Map<String,String> credentials) {
		msg = "Login failed with credentials: \n";
		for (String key : credentials.keySet()) {
			msg = " "+key+"="+credentials.get(key);
		}
	}
	
	public LoginFailedException(String msg) {
		super(msg);
	}
	
	@Override
	public String getMessage() {
		return msg;
	}
}
