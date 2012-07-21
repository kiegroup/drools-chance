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

package org.drools.scorecards.parser.xls;

import java.util.List;

import org.dmg.pmml_4_1.Characteristic;
import org.dmg.pmml_4_1.Characteristics;
import org.dmg.pmml_4_1.Scorecard;
import org.drools.core.util.StringUtils;
import org.drools.scorecards.ScorecardError;
import org.drools.scorecards.pmml.PMMLExtensionNames;
import org.drools.scorecards.pmml.ScorecardPMMLUtils;

public class ExcelScorecardValidator {

    private Scorecard scorecard;
    private List<ScorecardError> parseErrors;

    private ExcelScorecardValidator(Scorecard scorecard, List<ScorecardError> parseErrors) {
        //to ensure this used as a pure Util class only.
        this.scorecard = scorecard;
        this.parseErrors = parseErrors;
    }

    public static void runAdditionalValidations(Scorecard scorecard, List<ScorecardError> parseErrors) {
        ExcelScorecardValidator validator = new ExcelScorecardValidator(scorecard, parseErrors);
        validator.checkForInvalidDataTypes();
        validator.checkForMissingAttributes();
    }

    private void checkForInvalidDataTypes() {
        for (Object obj :scorecard.getExtensionsAndCharacteristicsAndMiningSchemas()){
            if (obj instanceof Characteristics){
                Characteristics characteristics = (Characteristics)obj;
                for (Characteristic characteristic : characteristics.getCharacteristics()){
                    String dataType = ScorecardPMMLUtils.getExtensionValue(characteristic.getExtensions(), PMMLExtensionNames.CHARACTERTISTIC_DATATYPE);
                    String newCellRef = createDataTypeCellRef(ScorecardPMMLUtils.getExtensionValue(characteristic.getExtensions(), "cellRef"));
                    if ( dataType == null || StringUtils.isEmpty(dataType)) {
                        parseErrors.add(new ScorecardError(newCellRef, "Missing Data Type!"));
                    }  else  if ( !"Text".equalsIgnoreCase(dataType) && !"Number".equalsIgnoreCase(dataType)){
                        parseErrors.add(new ScorecardError(newCellRef, "Invalid Data Type!"));
                    }
                }
            }
        }
    }

    private void checkForMissingAttributes() {
        for (Object obj :scorecard.getExtensionsAndCharacteristicsAndMiningSchemas()){
            if (obj instanceof Characteristics){
                Characteristics characteristics = (Characteristics)obj;
                for (Characteristic characteristic : characteristics.getCharacteristics()){
                    String newCellRef = ScorecardPMMLUtils.getExtensionValue(characteristic.getExtensions(), "cellRef");
                    if ( characteristic.getAttributes().size() == 0 ) {
                        parseErrors.add(new ScorecardError(newCellRef, "Missing Attribute Bins for Characteristic '"+characteristic.getName()+"'."));
                    }
                }
            }
        }
    }

    private String createDataTypeCellRef(String cellRef) {
        int col = ((int)(cellRef.charAt(1)))+1;
        return "$"+((char)col)+cellRef.substring(cellRef.indexOf('$',1));
    }
}
