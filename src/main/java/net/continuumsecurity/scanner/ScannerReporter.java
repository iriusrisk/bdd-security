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
package net.continuumsecurity.scanner;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import org.apache.log4j.Logger;

import net.continuumsecurity.restyburp.model.ScanIssueList;
import net.continuumsecurity.restyburp.server.JAXBContextResolver;

public class ScannerReporter {
	static final String path = "target"+System.getProperty("file.separator")+"jbehave"+System.getProperty("file.separator");
	Logger log = Logger.getLogger(ScannerReporter.class);

	
	public void write(String reference,ScanIssueList issueList) {
		try {
			File f = new File(path);
			if (!f.exists()) {
				f.mkdirs();
			}
			JAXBContext jaxbContext = new JAXBContextResolver().getContext(ScanIssueList.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,new Boolean(true));
			marshaller.marshal(issueList, new FileOutputStream(path+reference));
		} catch (PropertyException e) {			
			e.printStackTrace();
		} catch (JAXBException e) {			
			e.printStackTrace();
		} catch (Exception e) {			
			e.printStackTrace();
		}
	}
}
