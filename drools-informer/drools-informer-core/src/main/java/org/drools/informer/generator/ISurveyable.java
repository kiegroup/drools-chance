package org.drools.informer.generator;


import java.io.Serializable;

public interface ISurveyable extends Serializable {


    public String getQuestionnaireId();

    public void enableSurvey();

    public void disableSurvey();

    public boolean isSurveyEnabled();




}
