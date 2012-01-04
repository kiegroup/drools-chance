/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.informer.write.questionnaire;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Formatter;
import java.util.Iterator;

import org.drools.informer.util.TemplateManager;
import org.drools.io.impl.ByteArrayResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.drools.informer.domain.questionnaire.Application;
import org.drools.informer.domain.questionnaire.Page;
import org.drools.informer.domain.questionnaire.PageElement;
import org.drools.informer.write.questionnaire.helpers.CopyrightWriter;

public class PageTemplate {
	
	private static final Logger logger = LoggerFactory.getLogger(PageTemplate.class);
	
	protected Page pg;
	
	public PageTemplate(Page page) {
		super();
		this.pg = page;
	}
	
	
	/**
	 * Create the page based DRL file
	 * 
	 * @param application
	 * @param directory
	 * @param importDirectory
	 * @param count
	 * @param seperatePageDirectories
	 * @return
	 */
	public boolean generateDRLFile(Application application, String directory, String importDirectory, int count, boolean seperatePageDirectories) {
		String pageNumber = String.valueOf(count);
		if (pageNumber.length() == 1) {
			pageNumber = "page0" + pageNumber;
		}
		else {
			pageNumber = "page" + pageNumber;
		}
		String subDirectory = directory + "/" + pageNumber;
		if (!seperatePageDirectories) {
			subDirectory = directory;
		}
	    String fileName = subDirectory + "/" + pg.getId().replace(' ', '_') + ".drl";
	    //logger.debug("Preparing to write file: " + fileName);
	    try {
	    	File outdir = new File(subDirectory);
	        
	        //Basic directory existence checks
	        if (outdir.exists() && !outdir.isDirectory()) {
	            throw new IOException(subDirectory + " is not a valid directory.");
	        }
	        
	        // create the directory if it doesn't exist.
	        if(!outdir.exists()) {
	            if(!outdir.mkdir()) {
	            	throw new IOException("Unable to create directory: " + subDirectory);
	            }
	        }
	        Formatter fmtFile;
	        fmtFile = new Formatter(new FileOutputStream(fileName));
	        CopyrightWriter.writeCopyright(fmtFile, importDirectory);
	        writeDRLFileContents(application, LoaderTemplateManager.getInstance(), fmtFile, false);
	        fmtFile.close();
		} catch (IOException e) {
			logger.debug(e.getMessage());
			e.printStackTrace();
			return false;
		}
	    logger.debug("The " + fileName + " file has been written");  
	    return true;
	}



    public void compileDRL(TemplateManager tm, Formatter fmtFile, Application application, int count) {

	    writeDRLFileContents(application, tm, fmtFile, true);
	    fmtFile.close();


	}




	
	protected void writeDRLForPageElement(Application application, TemplateManager tm, Formatter fmt, PageElement element) {
		if (element.isAGroupType()) {
			for (Iterator<PageElement> i = element.getChildren().iterator(); i.hasNext();) {
				PageElement child = (PageElement) i.next();
				child.addGroupId(element.getId());
			}
		}
		new PageElementTemplate(element).compileContentsToDRL(application, tm, fmt);
		for (Iterator<PageElement> i = element.getChildren().iterator(); i.hasNext();) {
			PageElement child = (PageElement) i.next();
			writeDRLForPageElement(application, tm, fmt, child);
		}
	}
	
	protected void writeDRLFileContents(Application application, TemplateManager tm, Formatter fmt, boolean append)  {
//        if (! append) {
//	        fmt.format("package %s.%s;\n\n", application.getApplicationClass(), pg.getSheetName().replace(' ', '_').toLowerCase());
//        }
	    	    
//	    fmt.format("import java.util.Calendar;\n");	// TODO only include if rule require it?
//
//	    fmt.format("import org.drools.informer.Group;\n");
//	    fmt.format("import org.drools.informer.InvalidAnswer;\n");
//	    fmt.format("import org.drools.informer.MultipleChoiceQuestion;\n");
//	    fmt.format("import org.drools.informer.MultipleChoiceQuestion.PossibleAnswer;\n");
//	    fmt.format("import org.drools.informer.Note;\n");
//	    fmt.format("import org.drools.informer.Question;\n");
//	    fmt.format("import org.drools.informer.Answer;\n");
//	    fmt.format("import org.drools.informer.Questionnaire;\n");
//	    fmt.format("import %s.*;\n\n", application.getApplicationClass());	// needed for the definitions of the display facts
	    
	    if ((pg.getElements() != null) && (pg.getElements().size() > 0)) {
			writeDRLForPageElement(application, tm, fmt, pg.getElements().get(0));
		}
	    
	}
}
