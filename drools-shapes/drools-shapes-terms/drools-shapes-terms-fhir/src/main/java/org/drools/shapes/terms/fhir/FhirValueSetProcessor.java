package org.drools.shapes.terms.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.dstu.resource.ValueSet;
import ca.uhn.fhir.model.primitive.CodeDt;
import ca.uhn.fhir.rest.client.IGenericClient;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.drools.drools_shapes.terms.Code;
import org.drools.shapes.terms.Terms;

import java.lang.String;
import java.util.concurrent.ExecutionException;

public class FhirValueSetProcessor implements Terms {


    IGenericClient client;

    private LoadingCache<String, ValueSet> valueSetCache = CacheBuilder.newBuilder()
            .maximumSize(10)
            .build(
                    new CacheLoader<String, ValueSet>() {
                        public ValueSet load(String key) {
                            return getValueSet( key );
                        }
                    });

    private ValueSet getValueSet( String key ) {
        Bundle b = client.search().forResource( ValueSet.class ).where( ValueSet.IDENTIFIER.exactly().code( key ) ).execute();
        return (ValueSet) b.getEntries().iterator().next().getResource();
    }

    public FhirValueSetProcessor(String serviceUrl, String username, String password) {
        super();
        client = new FhirContext().newRestfulGenericClient( serviceUrl );
    }


    public boolean isEntityInSet(Code code, Code target) {
        if(target == null || code == null || code.getCode() == null) {
            return false;
        }

        ValueSet vs;
        try {
            vs = this.valueSetCache.get(target.getUri().toString());
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        if(vs == null){
            return false;
        }

        return this.containsCode(vs, code);
    }

    protected boolean containsCode(ValueSet valueSet, Code code) {
        if(valueSet.getDefine() != null) {
            for(ValueSet.DefineConcept concept : valueSet.getDefine().getConcept()) {
                if(code.getCode().equals(concept.getCode().getValue())) {
                    return true;
                }
            }
        }

        if(valueSet.getCompose() != null) {
            for(ValueSet.ComposeInclude include : valueSet.getCompose().getInclude()) {
                for(CodeDt foundCode : include.getCode()) {
                    if(code.getCode().equals(foundCode.getValue())) {
                        return true;
                    }
                }
            }
        }

        if(valueSet.getExpansion() != null) {
            for(ValueSet.ExpansionContains contains : valueSet.getExpansion().getContains()) {
                if ( code.getCode().equals( contains.getCode().getValue() ) ) {
                    return true;
                }
            }
        }

        return false;
    }
}
