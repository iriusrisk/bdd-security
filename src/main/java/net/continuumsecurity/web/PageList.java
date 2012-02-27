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

import java.util.ArrayList;
import java.util.List;



class PageList {
	List<Page> pages = new ArrayList<Page>();
	
	public void add(Page p) {
		pages.add(p);
	}
	
	public PageList getAllPublic() {
		PageList ret = new PageList();
		for (Page page : pages) {
			if (!page.getClass().isAnnotationPresent(Roles.class)) {
				//System.out.println("Page: "+page.getClass().getName()+" is public")
				ret.add(page);
			}
		}
		return ret;
	}
	
	public PageList getAllProtected() {
		PageList ret = new PageList();
		for (Page page : pages) {
			if (page.getClass().isAnnotationPresent(Roles.class)) {
				ret.add(page);
			}
		}
		return ret;
	}
	
	/*
	 * Get all the pages except those that implement the listed interface
	 */
	public PageList findAllThatDontImplement(Class theInterface) {
		PageList ret = new PageList();
		for (Page page : pages) {
			if (!theInterface.isAssignableFrom(page.getClass())) {
				ret.add(page);
			}
		}
		return ret;
	}
	
	public PageList findAllThatImplement(Class theInterface) {
		PageList ret = new PageList();
		for (Page page : pages) {
			if (theInterface.isAssignableFrom(page.getClass())) {
				ret.add(page);
			}
		}
		return ret;
	}
	
	public Page findFirstThatImplements(Class theInterface) {
		for (Page page : pages) {
			if (theInterface.isAssignableFrom(page.getClass())) {
				return page;
			}
		}
		return null;
	}
	
	public int size() { return pages.size(); }
	
	public Page get(int index) { return pages.get(index); }
	
	public List<Page> getAll() { return pages; }
	
}
