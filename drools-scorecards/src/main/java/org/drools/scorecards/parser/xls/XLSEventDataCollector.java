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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.util.CellReference;
import org.dmg.pmml_4_1.Attribute;
import org.dmg.pmml_4_1.Characteristic;
import org.dmg.pmml_4_1.Characteristics;
import org.dmg.pmml_4_1.DATATYPE;
import org.dmg.pmml_4_1.Extension;
import org.dmg.pmml_4_1.FIELDUSAGETYPE;
import org.dmg.pmml_4_1.INVALIDVALUETREATMENTMETHOD;
import org.dmg.pmml_4_1.MiningField;
import org.dmg.pmml_4_1.MiningSchema;
import org.dmg.pmml_4_1.Output;
import org.dmg.pmml_4_1.OutputField;
import org.dmg.pmml_4_1.RESULTFEATURE;
import org.dmg.pmml_4_1.Scorecard;
import org.drools.core.util.StringUtils;
import org.drools.scorecards.EventDataCollector;
import org.drools.scorecards.ScorecardError;
import org.drools.scorecards.parser.ScorecardParseException;
import org.drools.scorecards.pmml.PMMLExtensionNames;
import org.mvel2.MVEL;

public class XLSEventDataCollector implements EventDataCollector {

    private List<DataExpectation> expectations = new ArrayList<DataExpectation>();
    private List<MergedCellRange> cellRangeList;
    //private String worksheetName;
    private Scorecard scorecard;
    private Characteristics characteristics;
    private Characteristic _characteristic; //stateMachine variables
    private Output output;
    private List<ScorecardError> parseErrors;
    private MiningSchema miningSchema;

    public XLSEventDataCollector() {
        parseErrors = new ArrayList<ScorecardError>();
    }

    public Scorecard getScorecard() {
        return scorecard;
    }

    private void fulfillExpectation(int currentRowCtr, int currentColCtr, Object cellValue, Class expectedClass) throws ScorecardParseException {
        List<DataExpectation> dataExpectations = resolveExpectations(currentRowCtr, currentColCtr);
        CellReference cellRef = new CellReference(currentRowCtr, currentColCtr);
        for (DataExpectation dataExpectation : dataExpectations) {
            try {
                if (dataExpectation != null && dataExpectation.object != null) {
                    if ( cellValue == null || StringUtils.isEmpty(cellValue.toString())){
                        if ( dataExpectation.errorMessage != null && !StringUtils.isEmpty(dataExpectation.errorMessage)) {
                            parseErrors.add(new ScorecardError(cellRef.formatAsString(), dataExpectation.errorMessage));
                            return;
                        }
                    }
                    String setter = "set" + Character.toUpperCase(dataExpectation.property.charAt(0)) + dataExpectation.property.substring(1);
                    Method method = getSuitableMethod(expectedClass, dataExpectation, setter);
                    if ( method == null ) {
                        if (cellValue != null && !StringUtils.isEmpty(cellValue.toString())) {
                            parseErrors.add(new ScorecardError(cellRef.formatAsString(), "Unexpected Value! Wrong Datatype?"));
                        }
                        return;
                    }

                    method.invoke(dataExpectation.object, cellValue);
                    if (dataExpectation.object instanceof Extension && ("cellRef".equals(((Extension) dataExpectation.object).getName()))) {
                        ((Extension) dataExpectation.object).setValue(cellRef.formatAsString());
                    }
                    //dataExpectations.remove(dataExpectation);
                }
            } catch (Exception e) {
                throw new ScorecardParseException(e);
            }
        }
    }

    private Method getSuitableMethod(Class expectedClass, DataExpectation dataExpectation, String setter) {
        Method method;
        try {
            method = dataExpectation.object.getClass().getMethod(setter, expectedClass);
            return method;
        } catch (NoSuchMethodException e) {
            try {
                method = dataExpectation.object.getClass().getMethod(setter, String.class);
                return method;
            } catch (NoSuchMethodException e1) {
                return null;
            }
        }
    }

    private void setAdditionalExpectation(int currentRowCtr, int currentColCtr, String stringCellValue) {
        if (XLSKeywords.SCORECARD_NAME.equalsIgnoreCase(stringCellValue)) {
            addExpectation(currentRowCtr, currentColCtr + 1, "modelName", scorecard, "Model Name is missing!");

        } else if (XLSKeywords.SCORECARD_TYPE.equalsIgnoreCase(stringCellValue)) {
//            Extension extension = new Extension();
//            extension.setName("scorecardType");
//            scorecard.getExtensionsAndCharacteristicsAndMiningSchemas().add(extension);
//            addExpectation(currentRowCtr, currentColCtr + 1, "value", extension);
        } else if (XLSKeywords.SCORECARD_OBJECT.equalsIgnoreCase(stringCellValue)) {
            Extension extension = new Extension();
            extension.setName(PMMLExtensionNames.SCORECARD_OBJECT_CLASS);
            scorecard.getExtensionsAndCharacteristicsAndMiningSchemas().add(extension);
            addExpectation(currentRowCtr, currentColCtr + 1, "value", extension, "Rules Object Class Name is missing!");

        } else if (XLSKeywords.SCORECARD_BOUND_VARIABLE.equalsIgnoreCase(stringCellValue)) {
            Extension extension = new Extension();
            extension.setName(PMMLExtensionNames.SCORECARD_BOUND_VAR_NAME);
            scorecard.getExtensionsAndCharacteristicsAndMiningSchemas().add(extension);
            addExpectation(currentRowCtr, currentColCtr + 1, "value", extension, null);

        } else if (XLSKeywords.SCORECARD_BASE_SCORE.equalsIgnoreCase(stringCellValue)) {
            addExpectation(currentRowCtr, currentColCtr + 1, "initialScore", scorecard, null);

        } else if (XLSKeywords.SCORECARD_SCORE_VAR.equalsIgnoreCase(stringCellValue)) {
            OutputField outputField = new OutputField();
            outputField.setDataType(DATATYPE.DOUBLE);
            outputField.setDisplayName("Final Score");
            output.getOutputFields().add(outputField);
            outputField.setFeature(RESULTFEATURE.PREDICTED_VALUE);
            addExpectation(currentRowCtr, currentColCtr + 1, "name", outputField, "Final Score Variable is missing!");

        } else if (XLSKeywords.SCORECARD_IMPORTS.equalsIgnoreCase(stringCellValue)) {
            Extension extension = new Extension();
            extension.setName(PMMLExtensionNames.SCORECARD_IMPORTS);
            scorecard.getExtensionsAndCharacteristicsAndMiningSchemas().add(extension);
            addExpectation(currentRowCtr, currentColCtr + 1, "value", extension, null);

        } else if (XLSKeywords.SCORECARD_PACKAGE.equalsIgnoreCase(stringCellValue)) {
            Extension extension = new Extension();
            extension.setName(PMMLExtensionNames.SCORECARD_PACKAGE);
            scorecard.getExtensionsAndCharacteristicsAndMiningSchemas().add(extension);
            addExpectation(currentRowCtr, currentColCtr + 1, "value", extension, "Scorecard Package is missing");

        } else if (XLSKeywords.SCORECARD_CHARACTERISTIC_NAME.equalsIgnoreCase(stringCellValue)) {
            _characteristic = new Characteristic();
            characteristics.getCharacteristics().add(_characteristic);
            addExpectation(currentRowCtr + 1, currentColCtr, "name", _characteristic, "Characteristic (Property) Display Name is missing.");

            Extension extension = new Extension();
            extension.setName("cellRef");
            addExpectation(currentRowCtr + 1, currentColCtr, "value", extension, null);
            _characteristic.getExtensions().add(extension);

        } else if (XLSKeywords.SCORECARD_CHARACTERISTIC_DATATYPE.equalsIgnoreCase(stringCellValue)) {
            Extension extension = new Extension();
            extension.setName(PMMLExtensionNames.CHARACTERTISTIC_DATATYPE);
            _characteristic.getExtensions().add(extension);
            addExpectation(currentRowCtr + 1, currentColCtr, "value", extension, "Characteristic (Property) Data Type is missing.");

        } else if (XLSKeywords.SCORECARD_CHARACTERISTIC_INITIALSCORE.equalsIgnoreCase(stringCellValue)) {
            addExpectation(currentRowCtr + 1, currentColCtr, "baselineScore", _characteristic, null);

        } else if (XLSKeywords.SCORECARD_CHARACTERISTIC_WEIGHT.equalsIgnoreCase(stringCellValue)) {
//            Extension extension = new Extension();
//            extension.setName("weight");
//            _characteristic.getExtensions().add(extension);
//            addExpectation(currentRowCtr + 1, currentColCtr, "value", extension);

        } else if (XLSKeywords.SCORECARD_CHARACTERISTIC_BIN_ATTRIBUTE.equalsIgnoreCase(stringCellValue)) {
            MergedCellRange cellRange = getMergedRegionForCell(currentRowCtr + 1, currentColCtr);
            if (cellRange != null) {
                for (int r = cellRange.getFirstRow(); r <= cellRange.getLastRow(); r++) {
                    Attribute attribute = new Attribute();
                    _characteristic.getAttributes().add(attribute);
                    addExpectation(r, currentColCtr + 2, "partialScore", attribute, "Characteristic (Property) Partial Score is missing.");

                    Extension extension = new Extension();
                    extension.setName("description");
                    attribute.getExtensions().add(extension);
                    addExpectation(r, currentColCtr + 3, "value", extension, null);

                    extension = new Extension();
                    extension.setName(PMMLExtensionNames.CHARACTERTISTIC_FIELD);
                    attribute.getExtensions().add(extension);
                    addExpectation(currentRowCtr + 1, currentColCtr, "value", extension, "Characteristic (Property) Name is missing.");

                    extension = new Extension();
                    extension.setName("predicateResolver");
                    attribute.getExtensions().add(extension);
                    addExpectation(r, currentColCtr + 1, "value", extension, "Characteristic (Property) Value is missing.");

                    extension = new Extension();
                    extension.setName("cellRef");
                    addExpectation(r, currentColCtr + 1, "value", extension, null);
                    attribute.getExtensions().add(extension);
                }
                MiningField dataField = new MiningField();
                dataField.setInvalidValueTreatment(INVALIDVALUETREATMENTMETHOD.AS_MISSING);
                dataField.setUsageType(FIELDUSAGETYPE.ACTIVE);
                miningSchema.getMiningFields().add(dataField);
                addExpectation(currentRowCtr + 1, currentColCtr, "name", dataField, null);
            }

        } else if (XLSKeywords.SCORECARD_CHARACTERISTIC_BIN_INITIALSCORE.equalsIgnoreCase(stringCellValue)) {
        } else if (XLSKeywords.SCORECARD_CHARACTERISTIC_BIN_LABEL.equalsIgnoreCase(stringCellValue)) {
        } else if (XLSKeywords.SCORECARD_CHARACTERISTIC_BIN_DESC.equalsIgnoreCase(stringCellValue)) {
        }
    }

    private void addExpectation(int row, int column, String property, Object ref, String errorMessage) {
        expectations.add(new DataExpectation(row, column, ref, property, errorMessage));
    }

    private List<DataExpectation> resolveExpectations(int row, int col) {
        List<DataExpectation> dataExpectations = new ArrayList<DataExpectation>();
        for (DataExpectation dataExpectation : expectations) {
            if (dataExpectation.row == row && dataExpectation.col == col) {
                dataExpectations.add(dataExpectation);
            }
        }
        return dataExpectations;
    }

    public void newCell(int currentRowCtr, int currentColCtr, String stringCellValue) throws ScorecardParseException {
        setAdditionalExpectation(currentRowCtr, currentColCtr, stringCellValue);
        fulfillExpectation(currentRowCtr, currentColCtr, stringCellValue, String.class);
    }

    public void newCell(int currentRowCtr, int currentColCtr, double numericCellValue) throws ScorecardParseException {
        fulfillExpectation(currentRowCtr, currentColCtr, numericCellValue, Double.class);
    }

    public void newCell(int currentRowCtr, int currentColCtr, boolean booleanCellValue) throws ScorecardParseException {
        fulfillExpectation(currentRowCtr, currentColCtr, booleanCellValue, boolean.class);
    }

    public void newCell(int currentRowCtr, int currentColCtr, Date dateCellValue) throws ScorecardParseException {
        fulfillExpectation(currentRowCtr, currentColCtr, dateCellValue, Date.class);
    }

    public void sheetComplete() {
        //verify the data
        ExcelScorecardValidator.runAdditionalValidations(scorecard, parseErrors);
    }

    @SuppressWarnings("unused")
    public void newRow(int rowNum) {

    }

    public void sheetStart(String worksheetName) {
        //this.worksheetName = worksheetName;
        expectations.clear();
        cellRangeList = null;
        _characteristic = null;
        scorecard = new Scorecard();
        output = new Output();
        characteristics = new Characteristics();
        miningSchema = new MiningSchema();

        scorecard.getExtensionsAndCharacteristicsAndMiningSchemas().add(miningSchema);
        scorecard.getExtensionsAndCharacteristicsAndMiningSchemas().add(output);
        scorecard.getExtensionsAndCharacteristicsAndMiningSchemas().add(characteristics);

    }

    public void setMergedRegionsInSheet(List<MergedCellRange> cellRangeList) {
        this.cellRangeList = cellRangeList;
    }

    private MergedCellRange getMergedRegionForCell(int rowInd, int colInd) {
        for (MergedCellRange cellRange : cellRangeList) {
            if ((cellRange.getFirstRow() <= rowInd && rowInd <= cellRange.getLastRow() &&
                    cellRange.getFirstCol() <= colInd && colInd <= cellRange.getLastCol())) {
                return cellRange;
            }
        }
        return null;
    }

    public List<ScorecardError> getParseErrors() {
        return parseErrors;
    }

    class DataExpectation {

        int row;
        int col;
        Object object;
        String property;
        String errorMessage;

        DataExpectation(int row, int col, Object object, String property, String errorMessage) {
            this.row = row;
            this.col = col;
            this.object = object;
            this.property = property;
            this.errorMessage = errorMessage;
        }
    }
}
