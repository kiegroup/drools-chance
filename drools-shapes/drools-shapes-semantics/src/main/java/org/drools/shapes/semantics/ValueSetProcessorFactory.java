package org.drools.shapes.semantics;


/**
 * Instantiates a {@link ValueSetProcessor} to be used by the 'inValueSet' Drools operator.
 */
public class ValueSetProcessorFactory {

    private ValueSetProcessor valueSetProcessor;

    private static ValueSetProcessorFactory instance;

    public static synchronized ValueSetProcessorFactory instance() {
        if(instance == null) {
            instance = new ValueSetProcessorFactory();
        }

        return instance;
    }

    private ValueSetProcessorFactory() {
        // TODO: Configure implementation based on properties file, etc...
    }

    public ValueSetProcessor getValueSetProcessor() {
        return this.valueSetProcessor;
    }

}
