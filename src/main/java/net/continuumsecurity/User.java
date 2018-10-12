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

import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class User {
	private Credentials credentials;
	private List<String> roles;
	private Map<String,String> recoverPasswordMap;
 
	public User(Credentials credentials,String... roles) {
		this.roles = (List<String>)Arrays.asList(roles);
		this.credentials = credentials;
	}
	
	public Credentials getCredentials() {
		return credentials;
	}

	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}
	
	public String getDefaultRole() {
		if (roles != null && roles.size() > 0) {
			return roles.get(0);
		}
		return null;
	}

	public List<String> getRoles() {
		return roles;
	}
	
	public String getRolesAsCSV() {
		StringBuilder res = new StringBuilder();
		for (int i=0;i<roles.size();i++) {
			res.append(roles.get(i));
			if (i<roles.size()-1) res.append(",");
		}
		return res.toString();
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public boolean hasRole(String theRole) {
		for (String role : roles) {
			if (role.equalsIgnoreCase(theRole)) return true;
		}
		return false;
	}
	
	public boolean hasRole(List<String> theRoles) {
		for (String role : roles) {
			if (theRoles.contains(role)) return true;
		}
		return false;
	}

	public Map<String, String> getRecoverPasswordMap() {
		return recoverPasswordMap;
	}

	public void setRecoverPasswordMap(Map<String, String> recoverPassword) {
		this.recoverPasswordMap = recoverPassword;
	}
	
	
}
