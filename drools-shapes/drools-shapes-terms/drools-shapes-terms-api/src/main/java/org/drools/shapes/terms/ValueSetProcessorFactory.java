package org.drools.shapes.terms;


import org.drools.drools_shapes.terms.Code;
import org.w3._2004._02.skos.core.Concept;

/**
 * Instantiates a {@link Terms} to be used by the 'inValueSet' Drools operator.
 */
public class ValueSetProcessorFactory {

    private Terms valueSetProcessor;

    private static ValueSetProcessorFactory instance;

    public static synchronized ValueSetProcessorFactory instance() {
        if(instance == null) {
            instance = new ValueSetProcessorFactory();
        }
        return instance;
    }

    private ValueSetProcessorFactory() {
        Concept con;
        Code c;

        // TODO: Configure implementation based on properties file, etc...
    }

    public Terms getValueSetProcessor() {
        return this.valueSetProcessor;
    }

}
