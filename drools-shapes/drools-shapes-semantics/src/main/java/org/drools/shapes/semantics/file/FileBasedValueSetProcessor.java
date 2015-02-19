package org.drools.shapes.semantics.file;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.drools.shapes.semantics.ValueSetProcessor;
import org.hl7.v3.CD;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;


@Component
@SuppressWarnings("unused")
public class FileBasedValueSetProcessor implements ValueSetProcessor {

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

    @Override
    public boolean inValueSet(String valueSetUri, CD code) {
    	if(code == null || StringUtils.isEmpty(code.getCode())) {
    		return false;
    	}
    	
        Set<String> valueSet;
        try {
            valueSet = valueSetCache.get(valueSetUri);
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
