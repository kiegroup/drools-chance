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
package org.drools.informer.write.questionnaire.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Formatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CopyrightWriter {

	private static final Logger logger = LoggerFactory.getLogger(CopyrightWriter.class);
	
	protected static boolean fileExists = true;
	
	/**
	 * Looks for file named "Copyright.drl"
	 * 
	 * Does nothing if no such file can be found.
	 * 
	 * @param fmtFile
	 * @param importDirectory
	 * @throws IOException
	 */
	public static void writeCopyright(Formatter fmtFile, String importDirectory) throws IOException {
		if (fileExists && (importDirectory != null)) {
			String filename = importDirectory + "/Copyright.drl";
	    	File importFile = new File(filename);
	    	if (importFile.exists()) {
	            BufferedReader reader = new BufferedReader(new FileReader(importFile));

	            //... Loop as long as there are input lines.
	            String line = null;
	            while ((line=reader.readLine()) != null) {
	            	fmtFile.format("%s\n", line);
	            }

	            //... Close reader and writer.
	            reader.close();  // Close to unlock.
	    	}
	    	else {
	    		logger.debug("No copyright file found at " + filename);
	    		fileExists = false;
	    	}
		}
		
	}

}
