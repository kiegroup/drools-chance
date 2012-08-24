/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.scorecards;

import java.io.InputStream;
import java.util.List;

import org.dmg.pmml_4_1.PMML;
import org.drools.scorecards.drl.DeclaredTypesDRLEmitter;
import org.drools.scorecards.drl.ExternalModelDRLEmitter;
import org.drools.scorecards.parser.AbstractScorecardParser;
import org.drools.scorecards.parser.ScorecardParseException;
import org.drools.scorecards.parser.xls.XLSEventDataCollector;
import org.drools.scorecards.parser.xls.XLSScorecardParser;

public class ScorecardCompiler {

    private PMML pmmlDocument = null;
    public static final String DEFAULT_SHEET_NAME = "scorecards";
    private EventDataCollector eventDataCollector;
    private List<ScorecardError> scorecardErrors;

    public ScorecardCompiler() {

    }

    public boolean compileFromExcel(final String classPathResource) {
        return compile(classPathResource, ScorecardFormat.XLS, DEFAULT_SHEET_NAME);
    }

    public boolean compileFromExcel(final String classPathResource, final String worksheetName) {
        return compile(classPathResource, ScorecardFormat.XLS, worksheetName);
    }

    public boolean compileFromExcel(final InputStream stream) {
        return compile(stream, ScorecardFormat.XLS, DEFAULT_SHEET_NAME);
    }

    public boolean compileFromExcel(final InputStream stream, final String worksheetName) {
        return compile(stream, ScorecardFormat.XLS, worksheetName);
    }

    public boolean compile(final String classPathResource, ScorecardFormat format) {
        return compile(classPathResource, format, DEFAULT_SHEET_NAME);
    }

    public boolean compile(final InputStream stream, ScorecardFormat format) {
        return compile(stream, format, DEFAULT_SHEET_NAME);
    }

    public boolean compile(final String classPathResource, ScorecardFormat format, final String worksheetName) {
        InputStream is = getClass().getResourceAsStream(classPathResource);
        return compile(is, format, worksheetName);
    }

    public EventDataCollector getEventDataCollector() {
        return eventDataCollector;
    }

    public PMML getPMMLDocument() {
        return pmmlDocument;
    }

    public boolean compile(final InputStream stream, ScorecardFormat format, final String worksheetName) {
        if (format == ScorecardFormat.XLS) {
            AbstractScorecardParser parser = new XLSScorecardParser();
            try {
                this.eventDataCollector = new XLSEventDataCollector();
                scorecardErrors = parser.parseFile(eventDataCollector, stream, worksheetName);
                if ( scorecardErrors.isEmpty() ) {
                    pmmlDocument = parser.getPMMLDocument();
                    return true;
                }
            } catch (ScorecardParseException e) {
                e.printStackTrace();
            } finally {
                closeStream(stream);
            }
        }
        return false;
    }

    public String getDRL(){
        return  getDRL(DrlType.INTERNAL_DECLARED_TYPES);
    }

    public String getDRL(DrlType drlType){
        if (pmmlDocument != null) {
            if (drlType == DrlType.INTERNAL_DECLARED_TYPES) {
                return new DeclaredTypesDRLEmitter().emitDRL(pmmlDocument);
            } else if (drlType == DrlType.EXTERNAL_OBJECT_MODEL) {
                return new ExternalModelDRLEmitter().emitDRL(pmmlDocument);
            }
        }
        return  null;
    }

    public static String convertToDRL(PMML pmml, DrlType drlType){
        if (pmml != null) {
            if (drlType == DrlType.INTERNAL_DECLARED_TYPES) {
                return new DeclaredTypesDRLEmitter().emitDRL(pmml);
            } else if (drlType == DrlType.EXTERNAL_OBJECT_MODEL) {
                return new ExternalModelDRLEmitter().emitDRL(pmml);
            }
        }
        return  null;
    }

    public List<ScorecardError> getScorecardParseErrors() {
        return scorecardErrors;
    }

    private void closeStream(final InputStream stream) {
        try {
            if ( stream != null ) {
                stream.close();
            }
        } catch (final Exception e) {
            System.err.print("WARNING: Wasn't able to " + "correctly close stream for scorecard. " + e.getMessage());
        }
    }

    public static enum DrlType {
        INTERNAL_DECLARED_TYPES, EXTERNAL_OBJECT_MODEL
    }
}
