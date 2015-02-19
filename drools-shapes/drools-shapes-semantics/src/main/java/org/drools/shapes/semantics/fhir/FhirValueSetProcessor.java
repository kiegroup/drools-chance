package org.drools.shapes.semantics.fhir;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.drools.shapes.semantics.ValueSetProcessor;
import org.hl7.fhir.model.*;
import org.hl7.v3.CD;

import java.lang.String;
import java.util.concurrent.ExecutionException;

public class FhirValueSetProcessor implements ValueSetProcessor {

    private FhirRestClient client;

    private LoadingCache<String, ValueSet> valueSetCache = CacheBuilder.newBuilder()
            .maximumSize(10)
            .build(
                    new CacheLoader<String, ValueSet>() {
                        public ValueSet load(String key) {
                            return client.getValueSet(key);
                        }
                    });

    public FhirValueSetProcessor(String serviceUrl, String username, String password) {
        super();
        this.client = new FhirRestClient(serviceUrl, username, password);
    }


    public boolean inValueSet(String valueSetUri, CD code) {
        if(valueSetUri == null || code == null || code.getCode() == null) {
            return false;
        }

        ValueSet vs;
        try {
            vs = this.valueSetCache.get(valueSetUri);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        if(vs == null){
            return false;
        }

        return this.containsCode(vs, code);
    }

    protected boolean containsCode(ValueSet valueSet, CD code) {
        if(valueSet.getDefine() != null) {
            for(ValueSetConcept concept : valueSet.getDefine().getConcept()) {
                if(code.getCode().equals(concept.getCode().getValue())) {
                    return true;
                }
            }
        }

        if(valueSet.getCompose() != null) {
            for(ValueSetInclude include : valueSet.getCompose().getInclude()) {
                for(Code foundCode : include.getCode()) {
                    if(code.getCode().equals(foundCode.getValue())) {
                        return true;
                    }
                }
            }
        }

        if(valueSet.getExpansion() != null) {
            for(ValueSetContains contains : valueSet.getExpansion().getContains()) {
                if(code.getCode().equals(contains.getCode().getValue())) {
                    return true;
                }
            }
        }

        return false;
    }
}
