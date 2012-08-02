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

package org.drools.scorecards.drl;

import java.util.ArrayList;
import java.util.List;

import org.dmg.pmml_4_1.Array;
import org.dmg.pmml_4_1.Attribute;
import org.dmg.pmml_4_1.Characteristic;
import org.dmg.pmml_4_1.Characteristics;
import org.dmg.pmml_4_1.CompoundPredicate;
import org.dmg.pmml_4_1.Output;
import org.dmg.pmml_4_1.OutputField;
import org.dmg.pmml_4_1.PMML;
import org.dmg.pmml_4_1.RESULTFEATURE;
import org.dmg.pmml_4_1.Scorecard;
import org.dmg.pmml_4_1.SimplePredicate;
import org.dmg.pmml_4_1.SimpleSetPredicate;
import org.drools.core.util.StringUtils;
import org.drools.scorecards.parser.xls.XLSKeywords;
import org.drools.scorecards.pmml.PMMLExtensionNames;
import org.drools.scorecards.pmml.PMMLOperators;
import org.drools.scorecards.pmml.ScorecardPMMLUtils;
import org.drools.template.model.Condition;
import org.drools.template.model.Consequence;
import org.drools.template.model.DRLOutput;
import org.drools.template.model.Import;
import org.drools.template.model.Package;
import org.drools.template.model.Rule;

public class ScorecardDRLEmitter {


    public String emitDRL(PMML pmml) {
        List<Rule> ruleList = createRuleList(pmml);
        String pkgName = ScorecardPMMLUtils.getExtensionValue(pmml.getHeader().getExtensions(), PMMLExtensionNames.SCORECARD_PACKAGE);
        org.drools.template.model.Package aPackage = new org.drools.template.model.Package(pkgName);

        DRLOutput drlOutput = new DRLOutput();
        for (Rule rule : ruleList) {
            aPackage.addRule(rule);
        }

        addDeclaredTypes(pmml, aPackage);

        String importsFromDelimitedString = ScorecardPMMLUtils.getExtensionValue(pmml.getHeader().getExtensions(), PMMLExtensionNames.SCORECARD_IMPORTS);
        for (String importStatement : importsFromDelimitedString.split(",")) {
            Import imp = new Import();
            imp.setClassName(importStatement);
            aPackage.addImport(imp);
        }

        addGlobals(pmml, aPackage);
        aPackage.renderDRL(drlOutput);
        String drl = drlOutput.getDRL();
        return drl;
    }

    private void addDeclaredTypes(PMML pmml, Package aPackage) {
        Import defaultScorecardImport = new Import();
        defaultScorecardImport.setClassName("org.drools.scorecards.DroolsScorecard");
        aPackage.addImport(defaultScorecardImport);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\ndeclare DroolsScorecard\nend\n\n");

        for (Object obj : pmml.getAssociationModelsAndBaselineModelsAndClusteringModels()) {
            if (obj instanceof Scorecard) {
                Scorecard scorecard = (Scorecard) obj;
                stringBuilder.append("declare ").append(scorecard.getModelName().replaceAll(" ","")).append(" extends DroolsScorecard\n");
                Characteristics characteristics = getCharacteristicsFromScorecard(scorecard);
                for (org.dmg.pmml_4_1.Characteristic c : characteristics.getCharacteristics()) {
                    String dataType = ScorecardPMMLUtils.getExtensionValue(c.getExtensions(), PMMLExtensionNames.CHARACTERTISTIC_DATATYPE);
                    if (XLSKeywords.DATATYPE_TEXT.equalsIgnoreCase(dataType)) {
                        dataType = "String";
                    } else if (XLSKeywords.DATATYPE_NUMBER.equalsIgnoreCase(dataType)) {
                        dataType = "int";
                    } else if (XLSKeywords.DATATYPE_BOOLEAN.equalsIgnoreCase(dataType)) {
                        dataType = "boolean";
                    }
                    String field = "";
                    Attribute scoreAttribute = c.getAttributes().get(0);
                    if (scoreAttribute.getSimplePredicate() != null) {
                        field = scoreAttribute.getSimplePredicate().getField();
                    } else if (scoreAttribute.getSimpleSetPredicate() != null) {
                        field = scoreAttribute.getSimpleSetPredicate().getField();
                    } else if (scoreAttribute.getCompoundPredicate() != null) {
                        Object predicate = scoreAttribute.getCompoundPredicate().getSimplePredicatesAndCompoundPredicatesAndSimpleSetPredicates().get(0);
                        if (predicate instanceof SimplePredicate){
                            field = ((SimplePredicate)predicate).getField();
                        } else if (predicate instanceof SimpleSetPredicate){
                            field = ((SimpleSetPredicate)predicate).getField();
                        }
                    }
                    stringBuilder.append("\t").append(field).append(" : ").append(dataType).append("\n");
                }
                stringBuilder.append("end\n");
            }
        }
        aPackage.addDeclaredType(stringBuilder.toString());
    }

    private void addGlobals(PMML pmml, org.drools.template.model.Package aPackage) {

    }

    private List<Rule> createRuleList(PMML pmmlDocument) {
        List<Rule> ruleList = new ArrayList<Rule>();
        for (Object obj : pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()) {
            if (obj instanceof Scorecard) {
                Scorecard scorecard = (Scorecard) obj;
                Characteristics characteristics = getCharacteristicsFromScorecard(scorecard);
                for (org.dmg.pmml_4_1.Characteristic c : characteristics.getCharacteristics()) {
                    for (org.dmg.pmml_4_1.Attribute scoreAttribute : c.getAttributes()) {
                        String name = formRuleName(scorecard.getModelName().replaceAll(" ",""), c, scoreAttribute);
                        Rule rule = new Rule(name, 1, 1);
                        String desc = ScorecardPMMLUtils.getExtensionValue(scoreAttribute.getExtensions(), "description");
                        if (desc != null) {
                            rule.setDescription(desc);
                        }
                        populateLHS(rule, pmmlDocument, scorecard, c, scoreAttribute);
                        populateRHS(rule, pmmlDocument, scorecard, c, scoreAttribute);
                        ruleList.add(rule);
                    }
                }
            }
        }
        return ruleList;
    }

    private void populateLHS(Rule rule, PMML pmmlDocument, Scorecard scorecard, Characteristic c, Attribute scoreAttribute) {
        Condition condition = new Condition();
        StringBuilder stringBuilder = new StringBuilder();
        String var = "$sc";

        String objectClass = scorecard.getModelName().replaceAll(" ", "");
        stringBuilder.append(var).append(" : ").append(objectClass).append("(");

        String dataType = ScorecardPMMLUtils.getExtensionValue(c.getExtensions(), PMMLExtensionNames.CHARACTERTISTIC_DATATYPE);
        if (XLSKeywords.DATATYPE_TEXT.equalsIgnoreCase(dataType)) {
            if (scoreAttribute.getSimplePredicate() != null) {
                SimplePredicate predicate = scoreAttribute.getSimplePredicate();
                String operator = predicate.getOperator();
                if (PMMLOperators.EQUAL.equalsIgnoreCase(operator)) {
                    stringBuilder.append(predicate.getField());
                    stringBuilder.append(" == ");
                    stringBuilder.append("\"").append(predicate.getValue()).append("\"");
                } else if (PMMLOperators.NOT_EQUAL.equalsIgnoreCase(operator)) {
                    stringBuilder.append(predicate.getField());
                    stringBuilder.append(" != ");
                    stringBuilder.append("\"").append(predicate.getValue()).append("\"");
                }
            } else if (scoreAttribute.getSimpleSetPredicate() != null) {
                SimpleSetPredicate simpleSetPredicate = scoreAttribute.getSimpleSetPredicate();
                String content = simpleSetPredicate.getArray().getContent();
                content = content.replaceAll(" ", "\",\"");

                stringBuilder.append(simpleSetPredicate.getField()).append(" in ( \"").append(content).append("\" )");
            }
        } else if (XLSKeywords.DATATYPE_BOOLEAN.equalsIgnoreCase(dataType)) {
            if (scoreAttribute.getSimplePredicate() != null) {
                SimplePredicate predicate = scoreAttribute.getSimplePredicate();
                String operator = predicate.getOperator();
                if (PMMLOperators.EQUAL.equalsIgnoreCase(operator)) {
                    stringBuilder.append(predicate.getField());
                    stringBuilder.append(" == ");
                    stringBuilder.append(predicate.getValue().toLowerCase());
                } else if (PMMLOperators.NOT_EQUAL.equalsIgnoreCase(operator)) {
                    stringBuilder.append(predicate.getField());
                    stringBuilder.append(" != ");
                    stringBuilder.append(predicate.getValue().toLowerCase());
                }
            }
        } else if (XLSKeywords.DATATYPE_NUMBER.equalsIgnoreCase(dataType)) {
            if (scoreAttribute.getSimplePredicate() != null) {
                SimplePredicate predicate = scoreAttribute.getSimplePredicate();
                String operator = predicate.getOperator();
                stringBuilder.append(predicate.getField());
                if (PMMLOperators.LESS_THAN.equalsIgnoreCase(operator)) {
                    stringBuilder.append(" < ");
                } else if (PMMLOperators.GREATER_THAN.equalsIgnoreCase(operator)) {
                    stringBuilder.append(" > ");
                } else if (PMMLOperators.NOT_EQUAL.equalsIgnoreCase(operator)) {
                    stringBuilder.append(" <> ");
                } else if (PMMLOperators.EQUAL.equalsIgnoreCase(operator)) {
                    stringBuilder.append(" = ");
                } else if (PMMLOperators.GREATER_OR_EQUAL.equalsIgnoreCase(operator)) {
                    stringBuilder.append(" >= ");
                } else if (PMMLOperators.LESS_OR_EQUAL.equalsIgnoreCase(operator)) {
                    stringBuilder.append(" <= ");
                }
                stringBuilder.append(predicate.getValue());
            } else if (scoreAttribute.getCompoundPredicate() != null) {
                CompoundPredicate predicate = scoreAttribute.getCompoundPredicate();
                String field = null;
                for (Object obj : predicate.getSimplePredicatesAndCompoundPredicatesAndSimpleSetPredicates()) {
                    if (obj instanceof SimplePredicate) {
                        SimplePredicate simplePredicate = (SimplePredicate) obj;
                        String operator = simplePredicate.getOperator();
                        if (field == null) {
                            stringBuilder.append(simplePredicate.getField());
                            field = simplePredicate.getField();
                        } else {
                            stringBuilder.append(" && ");
                        }
                        if (PMMLOperators.LESS_THAN.equalsIgnoreCase(operator)) {
                            stringBuilder.append(" < ");
                        } else if (PMMLOperators.GREATER_THAN.equalsIgnoreCase(operator)) {
                            stringBuilder.append(" > ");
                        } else if (PMMLOperators.NOT_EQUAL.equalsIgnoreCase(operator)) {
                            stringBuilder.append(" <> ");
                        } else if (PMMLOperators.EQUAL.equalsIgnoreCase(operator)) {
                            stringBuilder.append(" = ");
                        } else if (PMMLOperators.GREATER_OR_EQUAL.equalsIgnoreCase(operator)) {
                            stringBuilder.append(" >= ");
                        } else if (PMMLOperators.LESS_OR_EQUAL.equalsIgnoreCase(operator)) {
                            stringBuilder.append(" <= ");
                        }
                        stringBuilder.append(simplePredicate.getValue());
                    }
                }
            }
        }
        stringBuilder.append(")");

        condition.setSnippet(stringBuilder.toString());
        rule.addCondition(condition);
    }

    private String formRuleName(String modelName, Characteristic c, Attribute scoreAttribute) {
        StringBuilder sb = new StringBuilder();
        sb.append(modelName).append("_").append(c.getName()).append("_");
        String dataType = ScorecardPMMLUtils.getDataType(c);
        if (XLSKeywords.DATATYPE_NUMBER.equalsIgnoreCase(dataType)) {
            if (scoreAttribute.getSimplePredicate() != null) {
                sb.append(scoreAttribute.getSimplePredicate().getOperator()).append("_").append(scoreAttribute.getSimplePredicate().getValue());
            } else if (scoreAttribute.getCompoundPredicate() != null) {
                sb.append("between");
                for (Object obj : scoreAttribute.getCompoundPredicate().getSimplePredicatesAndCompoundPredicatesAndSimpleSetPredicates()) {
                    if (obj instanceof SimplePredicate) {
                        sb.append("_").append(((SimplePredicate) obj).getValue());
                    }
                }
            }
        } else if (XLSKeywords.DATATYPE_TEXT.equalsIgnoreCase(dataType) || XLSKeywords.DATATYPE_BOOLEAN.equalsIgnoreCase(dataType)) {
            if (scoreAttribute.getSimplePredicate() != null) {
                sb.append(scoreAttribute.getSimplePredicate().getOperator()).append("_").append(scoreAttribute.getSimplePredicate().getValue());
            } else if (scoreAttribute.getSimpleSetPredicate() != null) {
                SimpleSetPredicate predicate = scoreAttribute.getSimpleSetPredicate();
                Array array = predicate.getArray();
                String text = array.getContent().replace(" ", "_");
                sb.append(predicate.getBooleanOperator()).append("_").append(text);
            }
        }
        return sb.toString();
    }

    private Characteristics getCharacteristicsFromScorecard(Scorecard scorecard) {
        for (Object obj : scorecard.getExtensionsAndCharacteristicsAndMiningSchemas()) {
            if (obj instanceof Characteristics) {
                return (Characteristics) obj;
            }
        }
        return null;
    }

    private void populateRHS(Rule rule, PMML pmmlDocument, Scorecard scorecard, Characteristic c, Attribute scoreAttribute) {
        Consequence consequence = new Consequence();
        StringBuilder stringBuilder = new StringBuilder();
        String var = "$sc";

        String setter = "addPartialScore";
        stringBuilder.append(var).append(".").append(setter).append("( ").append(scoreAttribute.getPartialScore()).append(" );");
        if (scorecard.isUseReasonCodes()){
            String reasonCode = scoreAttribute.getReasonCode();
            if (reasonCode == null || StringUtils.isEmpty(reasonCode)) {
                reasonCode = c.getReasonCode();
            }
            stringBuilder.append("\n\t\t$sc.addReasonCode(\"").append(reasonCode).append("\");");
        }
        consequence.setSnippet(stringBuilder.toString());
        rule.addConsequence(consequence);
    }

    public static String normalize(String str) {
        if (str == null || "null".equalsIgnoreCase(str)) {
            return "";
        }
        return str.replaceAll(",", "-");
    }


}
