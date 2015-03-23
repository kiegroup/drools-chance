package org.drools.shapes.terms.file;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.drools.core.io.impl.ClassPathResource;
import org.drools.drools_shapes.terms.ConceptDescriptor;
import org.drools.shapes.terms.operations.Terms;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;


@SuppressWarnings("unused")
public class FileTermsImpl implements Terms {

    public static final String KIND = "file";

    private final String NAMESPACE = "http://mayo.edu/sprint/";

    private LoadingCache<String, Set<String>> valueSetCache = CacheBuilder.newBuilder()
            .maximumSize(10)
            .build(
                    new CacheLoader<String, Set<String>>() {
                        
						public Set<String> load(String key) throws FileNotFoundException {

                            String fileName = getLocalPart(key);

                            List<String> lines = null;
                            try {
                                lines = IOUtils.readLines(
                                        new ClassPathResource("/valuesets/" + fileName + ".csv").getInputStream());
                            } catch (FileNotFoundException e) {
                            	throw e;
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            Set<String> returnSet = new HashSet<String>();

                            for(String line : lines) {
                                String[] tokens = StringUtils.split(line, ",");
                                String code = tokens[0].trim();

                                //TODO: we need to handle the codeSystem/URI somehow
                                String codeSystem = tokens[1].trim();

                                returnSet.add(code);
                            }

                            return returnSet;

                        }
                    });

    public boolean isEntityInSet(ConceptDescriptor code, ConceptDescriptor target) {
    	if(code == null || StringUtils.isEmpty(code.getCode())) {
    		return false;
    	}
    	
        Set<String> valueSet;
        try {
            valueSet = valueSetCache.get(target.getUri().toString());
        } catch (ExecutionException e) {
        	if(e.getCause() instanceof FileNotFoundException) {
        		return false;
        	}
            throw new RuntimeException(e.getCause());
        }
        
        if(valueSet.contains(code.getCode())) {
            return true;
        }

        return false;
    }

    private String getLocalPart(String uri) {
        return StringUtils.substringAfter(uri, NAMESPACE);
    }
}
