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

package org.drools.informer.generator;


public abstract class Surveyable implements ISurveyable {

    public abstract String getQuestionnaireId();

    public abstract void setQuestionnaireId( String id );


    protected boolean surveyEnabled = true;

    protected boolean surveyLocked = false;

    protected boolean stateful = false;


    public void enableSurvey() {
        surveyEnabled = false;
    }

    public void disableSurvey() {
        surveyEnabled = false;
    }

    public boolean isSurveyEnabled() {
        return surveyEnabled;
    }

    public void setSurveyEnabled(boolean surveyEnabled) {
        this.surveyEnabled = surveyEnabled;
    }

    public boolean isStateful() {
        return stateful;
    }

    public void setStateful(boolean stateful) {
        this.stateful = stateful;
    }

    public boolean isSurveyLocked() {
        return surveyLocked;
    }

    public void setSurveyLocked(boolean surveyLocked) {
        this.surveyLocked = surveyLocked;
    }
}
