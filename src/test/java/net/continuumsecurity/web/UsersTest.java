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

import net.continuumsecurity.Credentials;
import net.continuumsecurity.User;
import net.continuumsecurity.Users;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class UsersTest {
	Users users;
	User bob;
	User tom;
	User alice;
	User admin;
	
	@Before
	public void setup() {
		bob = new User(new Credentials("username","bob","password","bobp"),"user");
		tom = new User(new Credentials("username","tom","password","tomp"),"user");
		alice = new User(new Credentials("username","alice", "password","alicep"),"user","admin");
		admin = new User(new Credentials("username","admin","password","adminp"),"admin");
		users = new Users();
		users.add(bob);
		users.add(tom);
		users.add(alice);
	}
	
	@Test
	public void testAddUsers() {
		assert users.getDefaultCredentials("user").get("username").equals("bob");
		assert users.getDefaultCredentials("user").get("password").equals("password");
		assert users.getDefaultCredentials("admin").get("username").equals("alice");
	}
	
	@Test
	public void testUsersNotInRole() {
		//for (User user : users.getAllUsersNotInRoles("admin")) {
		//	System.out.println(user.getCredentials().get("username"));
		//}
		assert users.getAllUsersNotInRoles("admin").contains(bob);
		assert users.getAllUsersNotInRoles("admin").contains(tom);
		assertFalse(users.getAllUsersNotInRoles("admin").contains(alice));
		assertFalse(users.getAllUsersNotInRoles("admin").contains(admin));
	}

}

