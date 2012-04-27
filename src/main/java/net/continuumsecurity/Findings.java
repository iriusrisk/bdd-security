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
import java.util.List;

class Findings {
	List<Finding> all = new ArrayList<Finding>();
	public final static Findings instance = new Findings();
	
	private Findings() {}
	
	public synchronized void add(Finding finding) {
		all.add(finding);
	}
	
	public String toString() {
		String ret ="";
		for (Finding finding : all) {
			ret = ret + finding.id+" "+finding.url+" "+finding.detail+"\n";
		}
		return ret;
	}
}
