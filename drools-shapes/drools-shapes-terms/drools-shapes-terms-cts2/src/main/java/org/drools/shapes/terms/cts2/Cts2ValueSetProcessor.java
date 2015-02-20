package org.drools.shapes.terms.cts2;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import edu.mayo.cts2.framework.core.client.Cts2RestClient;
import edu.mayo.cts2.framework.model.core.URIAndEntityName;
import edu.mayo.cts2.framework.model.valuesetdefinition.IteratableResolvedValueSet;
import org.drools.drools_shapes.terms.Code;
import org.drools.shapes.terms.Terms;

import java.util.concurrent.ExecutionException;

/**
 * OMG(R) CTS2 REST-Based ValueSetProcessor.
 */
public class Cts2ValueSetProcessor implements Terms {

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

    public boolean isEntityInSet(Code entity, Code target) {
        IteratableResolvedValueSet vs = null;
        try {
            vs = this.valueSetCache.get(target.getCode());
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        if(vs == null){
            return false;
        }

        return this.containsCode(vs, target);
    }

    protected boolean containsCode( IteratableResolvedValueSet valueSet, Code code ) {
        for( URIAndEntityName entry : valueSet.getEntry() ) {
            if( code.getCode().equals( entry.getName() ) && code.getCodeSystem().equals( entry.getNamespace() ) ) {
                return true;
            }
        }

        return false;
    }
}