package org.drools.shapes.semantics.fhir;

import org.apache.commons.codec.binary.Base64;
import org.hl7.fhir.model.ValueSet;
import org.hl7.fhir.model.xml.FhirXmlMarshaller;
import org.springframework.http.*;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3._2005.atom.ContentType;
import org.w3._2005.atom.EntryType;
import org.w3._2005.atom.FeedType;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Client for interacting with a FHIR REST API.
 */
@Component
public class FhirRestClient {

    private RestTemplate restTemplate;

    private String serviceUrl;

    private String username;

    private String password;

    private static FhirXmlMarshaller fhirXmlMarshaller;
    static {
        try {
            fhirXmlMarshaller = new FhirXmlMarshaller();
        } catch (JAXBException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public FhirRestClient() {
        super();
    }

    public FhirRestClient(String serviceUrl, String username, String password) {
        super();
        Assert.notNull(serviceUrl);
        this.serviceUrl = serviceUrl;
        this.username = username;
        this.password = password;

        this.initializeTemplate();
    }

    private void initializeTemplate() {
        Assert.notNull(this.serviceUrl);

        this.restTemplate = new RestTemplate();
        this.restTemplate.setMessageConverters(this.createXmlMessageConverters());
    }

    HttpHeaders createHeaders( final String username, final String password ){
        return new HttpHeaders(){
            {
                String auth = username + ":" + password;
                byte[] encodedAuth = Base64.encodeBase64(
                        auth.getBytes(Charset.forName("US-ASCII")));
                String authHeader = "Basic " + new String( encodedAuth );
                set( "Authorization", authHeader );
            }
        };
    }
    protected List<HttpMessageConverter<?>> createXmlMessageConverters() {
        GenericHttpMessageConverter<?> xmlConverter = new GenericHttpMessageConverter<Object>() {

            @Override
            public boolean canRead(Class<?> clazz, MediaType mediaType) {
                return true;
            }

            @Override
            public boolean canWrite(Class<?> clazz, MediaType mediaType) {
                return true;
            }

            @Override
            public List<MediaType> getSupportedMediaTypes() {
                return Arrays.asList(MediaType.APPLICATION_XML, MediaType.TEXT_XML);
            }

            @Override
            public Object read(Class<? extends Object> clazz,
                               HttpInputMessage inputMessage) throws IOException,
                    HttpMessageNotReadableException {
                try {
                    return fhirXmlMarshaller.unmarshall(inputMessage.getBody());
                } catch (JAXBException e) {
                    throw new IOException(e);
                }
            }

            @Override
            public void write(Object t, MediaType contentType,
                              HttpOutputMessage outputMessage) throws IOException,
                    HttpMessageNotWritableException {
                try {
                    fhirXmlMarshaller.marshall(t, outputMessage.getBody());
                } catch (JAXBException e) {
                    throw new IOException(e);
                }
            }

            @Override
            public boolean canRead(Type type, Class<?> contextClass,
                                   MediaType mediaType) {
                return true;
            }

            @Override
            public Object read(Type type, Class<?> contextClass,
                               HttpInputMessage inputMessage) throws IOException,
                    HttpMessageNotReadableException {
                try {
                    return fhirXmlMarshaller.unmarshall(inputMessage.getBody());
                } catch (JAXBException e) {
                    throw new IOException(e);
                }
            }

        };

        List<HttpMessageConverter<?>> returnList = new ArrayList<HttpMessageConverter<?>>();
        returnList.add(xmlConverter);

        return returnList;
    }


    public ValueSet getValueSet(String id) {
        String url = this.buildUrl("/local/ValueSet/_search?name={id}", id);

        FeedType valueSetFeed = this.restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<ValueSet>(createHeaders(username, password)), FeedType.class).getBody();

        List<ValueSet> valueSets = new ArrayList<ValueSet>();

        for(Object feed : valueSetFeed.getTitleOrUpdatedOrId()) {
            if(feed instanceof EntryType) {
                for(JAXBElement entry : ((EntryType) feed).getTitleOrLinkOrId()) {
                    if(entry.getValue() instanceof ContentType) {
                        for(Object content : ((ContentType) entry.getValue()).getContent()) {
                            if(content instanceof JAXBElement) {
                                valueSets.add((ValueSet) ((JAXBElement) content).getValue());
                            }
                        }
                    }
                }
            }
        }

        if(valueSets.size() > 1) {
            throw new RuntimeException("Value Set with id: " + id + " has an ambiguous name. More than one Value Set returned.");
        }

        if(valueSets.size() == 0) {
            throw new RuntimeException("Value Set with id: " + id + " not found.");
        }

        return valueSets.get(0);
    }

    protected String buildUrl(String path, Object... params) {
        return UriComponentsBuilder.fromHttpUrl(this.serviceUrl).path(path).buildAndExpand(params).toString();
    }


    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}