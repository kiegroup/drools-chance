package org.drools.shapes.semantics.cts2;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import edu.mayo.cts2.framework.core.client.Cts2RestClient;
import edu.mayo.cts2.framework.model.core.URIAndEntityName;
import edu.mayo.cts2.framework.model.valuesetdefinition.IteratableResolvedValueSet;
import org.drools.shapes.semantics.ValueSetProcessor;
import org.hl7.v3.CD;

import java.util.concurrent.ExecutionException;

/**
 * OMG(R) CTS2 REST-Based ValueSetProcessor.
 */
//Disabled
//@Component
public class Cts2ValueSetProcessor implements ValueSetProcessor {

    private final static String BASE_URL = "http://some/server/cts2/valuesetdefinitionbyuri/resolution?uri={uri}";

    private LoadingCache<String, IteratableResolvedValueSet> valueSetCache = CacheBuilder.newBuilder()
            .maximumSize(10)
            .build(
                    new CacheLoader<String, IteratableResolvedValueSet>() {
                        public IteratableResolvedValueSet load(String key) {
                            try {
                                return client.getCts2Resource(BASE_URL, null, null, IteratableResolvedValueSet.class, key);
                            } catch (Exception e) {
                                return null;
                            }
                        }
                    });


    private Cts2RestClient client = Cts2RestClient.instance();

    public boolean inValueSet(String valueSetUri, CD code) {
        IteratableResolvedValueSet vs = null;
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

    protected boolean containsCode(IteratableResolvedValueSet valueSet, CD code) {
        for(URIAndEntityName entry : valueSet.getEntry()) {
            if(code.getCode().equals(entry.getName())) {
                return true;
            }
        }

        return false;
    }
}