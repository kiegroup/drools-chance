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
package org.drools.informer.load.questionnaire;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.drools.informer.domain.questionnaire.Application;
import org.drools.informer.load.spreadsheet.sections.SpreadsheetSection;
import org.drools.informer.load.spreadsheet.sections.SpreadsheetSectionSplitter;
import org.drools.informer.load.spreadsheet.WorkbookData;
import org.drools.informer.write.questionnaire.ApplicationTemplate;

/**
 * The main entry point for processing a Questionnaire based spreadsheet. 
 * 
 * @author Derek Rendall
 */
public class InformerSpreadsheetLoader implements SpreadsheetSectionConstants {
	
	private static final Logger logger = LoggerFactory.getLogger(InformerSpreadsheetLoader.class);
	
	/** useful section heading to avoid processing rest of spreadsheet - can store temp working stuff after this line */
	public static final String SHEET_END ="END";
		
	private WorkbookData wbData;
	private Application application;
	
	private String outputDirectory;
	private String importDirectory;

	private boolean seperatePageDirectories = true;
	
	public InformerSpreadsheetLoader() {
		super();
	}


	/**
	 * Start the process of loading the Questionnaire data 
	 * 
	 * @param filename
	 * 			The path and file name of the spreadsheet file 
	 * @return
	 */
    public byte[] compileFile(String filename) {
		wbData = new WorkbookData();
		this.seperatePageDirectories = seperatePageDirectories;

        try {
            File f = new File(InformerSpreadsheetLoader.class.getClassLoader().getResource(filename).toURI());
            filename = f.getAbsolutePath();
        } catch (URISyntaxException e) {
            logger.error("File not found :" + e);
            return null;
        }

        if (!wbData.loadWorkbook(filename)) {
			logger.debug("Data not loaded from workbook");
			return null;
		}

		return compileData(PAGE_SECTION_HEADINGS);
	}
	
	/**
	 * Load up the data from the spreadsheet and split into sections based on the section headings.
	 * 
	 * Then extract the application and page information.
	 * 
	 * Then create the rule files.
	 * 
	 * @param sectionHeadingNames
	 * @return
	 * 			true if everything went OK
	 */
    protected byte[] compileData(String[] sectionHeadingNames) {
		List<SpreadsheetSection> sections = new SpreadsheetSectionSplitter(sectionHeadingNames).splitIntoSections(wbData);

		application = new ExtractApplication(sections).processApp();
		if (application == null) {
			logger.debug("No Application Object Created");
			return null;
		}

		if (!new ExtractPages(sections, application).processPages()) {
			logger.debug("Page Extraction failed");
			return null;
		}

		application.processTableEntries();

		return compile();
	}


    protected byte[] compile() {
		byte[] processed = new ApplicationTemplate(application).generateDRLAsBytes();
		if (processed == null) {
			logger.debug("Failed to create rule files");
		}
		return processed;
	}
}
