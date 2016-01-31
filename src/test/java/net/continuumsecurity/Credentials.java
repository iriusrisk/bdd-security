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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.continuumsecurity;

import java.util.HashMap;
import java.util.Map;

public class Credentials {
	Map<String,String> creds = new HashMap<String,String>();
	
	public Credentials() {}
	
	public Credentials(String... strings) {
		creds = stringsToMap(strings);
	}
	
	public void add(String... strings) {
		creds.putAll(stringsToMap(strings));
	}
	
	public Map<String,String> stringsToMap(String... strings) {
		Map<String,String> map = new HashMap<String,String>();
		if (strings.length % 2 > 0) throw new RuntimeException("Credentials must be provided in pairs, e.g. 'username','bob'");
		
		for (int it = 0;it <= strings.length / 2;it=it+2) {
			map.put(strings[it], strings[it+1]);
		}
		return map;
	}
	
	public boolean containsKey(String key) {
		for (String credName : creds.keySet()) {
			if (credName.equalsIgnoreCase(key)) return true;
		}
		return false;
	}
	
	public String get(String name) {
		return creds.get(name);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String key : creds.keySet()) {
			sb.append(" ").append(key).append("=").append(creds.get(key)).append("\n");
		}
		return sb.toString();	
	}
}

