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
package net.continuumsecurity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Users {
	List<User> users = new ArrayList<User>();
	
	public Users add(Credentials creds,String... roles) {
		users.add(new User(creds,roles));
		return this;
	}
	
	public Users add(String username,String password,String... roles) {
		users.add(new User(new UserPassCredentials(username,password),roles));
		return this;
	}
	
	public String getDefaultRole() {
		assert users.size() > 0;
		if (users.get(0).getRoles().size() > 0) {
			return users.get(0).getRoles().get(0);
		} else { 
			return null;
		}
	}
	
	public Credentials getDefaultCredentials() {
		return getDefaultCredentials(getDefaultRole());
	}
	
	public Credentials getDefaultCredentials(String role) {
		assert users.size() > 0;
		for (User user : users) {
			if (role == null) {
				return user.getCredentials();
			}
			if (user.getRoles().contains(role)) {
				return user.getCredentials();
			}
		}
		throw new RuntimeException("Role: "+role+" not found.");
	}
	
	public List<User> getAllUsersNotInRoles(List<String> roles) {
		List<User> theUsers = new ArrayList<User>();
		for (User user : users) {
			if (!user.hasRole(roles)) theUsers.add(user);
		}
		return theUsers;
	}
	
	public List<User> getAllUsersNotInRoles(String... roles) {
		return getAllUsersNotInRoles(Arrays.asList(roles));
	}
	
	public List<User> getAll() {
		return users;
	}
	
	public User findByCredential(String key, String value) {
		for (User user : users) {
			if ((user.getCredentials().containsKey(key)) && (user.getCredentials().get(key).equals(value))) return user;
		}
		return null;
	}

	public void add(User user) {
		users.add(user);
	}
}
