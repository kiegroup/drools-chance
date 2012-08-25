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
import org.dmg.pmml_4_1.Extension;
import org.dmg.pmml_4_1.Output;
import org.dmg.pmml_4_1.OutputField;
import org.dmg.pmml_4_1.PMML;
import org.dmg.pmml_4_1.Scorecard;
import org.drools.scorecards.pmml.PMMLExtensionNames;
import org.drools.scorecards.pmml.ScorecardPMMLUtils;
import org.drools.template.model.Condition;
import org.drools.template.model.Package;
import org.drools.template.model.Rule;

import static junit.framework.Assert.*;

public class ExternalModelDRLEmitter  extends AbstractDRLEmitter {

    @Override
    protected void addDeclaredTypeContents(StringBuilder stringBuilder, Scorecard scorecard) {
        //empty by design
    }

    @Override
    public void internalEmitDRL(PMML pmml, List<Rule> ruleList, Package aPackage) {
        //do nothing for now.
    }

    @Override
    protected void addLHSConditions(Rule rule, PMML pmmlDocument, Scorecard scorecard, Characteristic c, Attribute scoreAttribute) {
        Condition condition = new Condition();
        StringBuilder stringBuilder = new StringBuilder();
        String var = "$sc";

        String objectClass = scorecard.getModelName().replaceAll(" ", "");
        stringBuilder.append(var).append(" : ").append(objectClass).append("()");
        condition.setSnippet(stringBuilder.toString());
        rule.addCondition(condition);

        Extension extension =  ScorecardPMMLUtils.getExtension(c.getExtensions(), PMMLExtensionNames.CHARACTERTISTIC_EXTERNAL_CLASS);
        if ( extension != null ) {
            condition = new Condition();
            stringBuilder = new StringBuilder("$");
            stringBuilder.append(c.getName()).append(" : ").append(extension.getValue());
            createFieldRestriction(c, scoreAttribute, stringBuilder);
            condition.setSnippet(stringBuilder.toString());
            rule.addCondition(condition);
        }
    }

    @Override
    protected void addAdditionalSummationCondition(Rule calcTotalRule, Scorecard scorecard) {
        String externalClassName =  null;
        String fieldName =  null;
        for (Object obj :scorecard.getExtensionsAndCharacteristicsAndMiningSchemas()){
            if ( obj instanceof Output) {
                Output output = (Output)obj;
                final List<OutputField> outputFields = output.getOutputFields();
                final OutputField outputField = outputFields.get(0);
                assertEquals("totalScore", outputField.getName());
                fieldName = outputField.getName();
                externalClassName = ScorecardPMMLUtils.getExtension(outputField.getExtensions(), PMMLExtensionNames.SCORECARD_RESULTANT_SCORE_CLASS).getValue();
                break;
            }
        }
        if ( fieldName != null && externalClassName != null) {
            Condition condition = new Condition();
            StringBuilder stringBuilder = new StringBuilder("$");
            stringBuilder.append(fieldName).append("Var : ").append(externalClassName).append("()");
            condition.setSnippet(stringBuilder.toString());
            calcTotalRule.addCondition(condition);
        }
    }
}
