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

import java.util.List;

import org.dmg.pmml_4_1.Attribute;
import org.dmg.pmml_4_1.Characteristic;
import org.dmg.pmml_4_1.Characteristics;
import org.dmg.pmml_4_1.PMML;
import org.dmg.pmml_4_1.Scorecard;
import org.drools.scorecards.parser.xls.XLSKeywords;
import org.drools.scorecards.pmml.PMMLExtensionNames;
import org.drools.scorecards.pmml.ScorecardPMMLUtils;
import org.drools.template.model.Condition;
import org.drools.template.model.Package;
import org.drools.template.model.Rule;

public class DeclaredTypesDRLEmitter extends AbstractDRLEmitter{

    protected void addDeclaredTypeContents(StringBuilder stringBuilder, Scorecard scorecard) {
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
            String field = extractFieldFromCharacteristic(c);
            stringBuilder.append("\t").append(field).append(" : ").append(dataType).append("\n");
        }
    }

    @Override
    public void internalEmitDRL(PMML pmml, List<Rule> ruleList, Package aPackage) {
        //ignore
    }

    @Override
    protected void addLHSConditions(Rule rule, PMML pmmlDocument, Scorecard scorecard, Characteristic c, Attribute scoreAttribute) {
        Condition condition = new Condition();
        StringBuilder stringBuilder = new StringBuilder();
        String var = "$sc";

        String objectClass = scorecard.getModelName().replaceAll(" ", "");
        stringBuilder.append(var).append(" : ").append(objectClass);

        createFieldRestriction(c, scoreAttribute, stringBuilder);

        condition.setSnippet(stringBuilder.toString());
        rule.addCondition(condition);
    }

    @Override protected void addAdditionalSummationCondition(Rule calcTotalRule, Scorecard scorecard) {
        //nothing additional
    }
}
