package org.drools.shapes.terms;


import org.drools.shapes.terms.cts2.Cts2TermsImpl;
import org.drools.shapes.terms.operations.internal.TermsInferenceService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Instantiates a {@link org.drools.shapes.terms.operations.Terms} to be used by the 'inValueSet' Drools operator.
 */
public class TermsInferenceServiceFactory {

    private Map<String,TermsInferenceService> termsServices = new ConcurrentHashMap<String, TermsInferenceService>();
    private TermsInferenceService defaultService;

    private static TermsInferenceServiceFactory instance;

    public static synchronized TermsInferenceServiceFactory instance() {
        if(instance == null) {
            instance = new TermsInferenceServiceFactory();
        }

        // TODO DO This dynamically...
        instance.termsServices.put( Cts2TermsImpl.KIND, new Cts2TermsImpl() );

        instance.defaultService = instance.termsServices.get( Cts2TermsImpl.KIND );

        return instance;
    }

    private TermsInferenceServiceFactory() {
    }

    public TermsInferenceService getValueSetProcessor( String kind ) {
        return  termsServices.get( kind );
    }

    public TermsInferenceService getValueSetProcessor() {
        return defaultService;
    }

}
