package org.drools.semantics;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.helpers.DefaultValidationEventHandler;
import javax.xml.validation.Schema;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.fail;

public class XMLSerializationHelper extends XmlAdapter<UIdAble,org.w3._2002._07.owl.Thing> {


    private Map<String,org.w3._2002._07.owl.Thing> cache;
    private UIdAble rootObj;


    public XMLSerializationHelper() {
        cache = new HashMap<String, org.w3._2002._07.owl.Thing>();
    }

    protected XMLSerializationHelper( org.w3._2002._07.owl.Thing root ) {
        cache = new HashMap<String, org.w3._2002._07.owl.Thing>();
        if ( root instanceof UIdAble ) {
            rootObj = (UIdAble) root;
        }
    }

    public static Marshaller createMarshaller( String pack ) {
        return createMarshaller( pack, null );
    }

    public static Marshaller createMarshaller( String pack, Schema schema ) {
        JAXBContext jaxbContext = null;
        try {
            jaxbContext = JAXBContext.newInstance( pack );
            Marshaller marsh = jaxbContext.createMarshaller();

            if ( schema != null ) {
                marsh.setSchema( schema );
//                marsh.setEventHandler( new DefaultValidationEventHandler() {
//                    @Override
//                    public boolean handleEvent( ValidationEvent event ) {
//                        return super.handleEvent( event );
//                    }
//                });
            }
            marsh.setProperty( Marshaller.JAXB_ENCODING, "UTF-8" );
            marsh.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            return marsh;
        } catch ( JAXBException e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
        return null;
    }

    public static Unmarshaller createUnmarshaller( String pack ) {
        return createUnmarshaller( pack, null );
    }

    public static Unmarshaller createUnmarshaller( String pack, Schema schema ) {
        JAXBContext jaxbContext = null;
        try {
            jaxbContext = JAXBContext.newInstance( pack );
            final Unmarshaller unmarsh = jaxbContext.createUnmarshaller();
            if ( schema != null ) {
                unmarsh.setSchema( schema );
//                unmarsh.setEventHandler( new DefaultValidationEventHandler() );
            }

            unmarsh.setListener( new Unmarshaller.Listener() {
                @Override
                public void beforeUnmarshal( Object target, Object parent ) {
                    if ( parent == null && target instanceof org.w3._2002._07.owl.Thing ) {
                        unmarsh.setAdapter( XMLSerializationHelper.class, new XMLSerializationHelper( (org.w3._2002._07.owl.Thing) target ) );
                    }
                }
            } );
            return unmarsh;
        } catch ( JAXBException e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
        return null;
    }



    public static Collection fromXMLResource( String sourceURL, String namespace ) {
        try {
            return fromXMLResource( new URL( sourceURL ), namespace );
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static Collection fromXMLResource( URL source, String namespace ) {
        try {
            Unmarshaller unmarshal = JAXBContext.newInstance( namespace ).createUnmarshaller();
            unmarshal.unmarshal( source.openStream() );
            XMLSerializationHelper loader = unmarshal.getAdapter( XMLSerializationHelper.class );
            return loader.getObjects();
        } catch (JAXBException e) {
            e.printStackTrace();
            return Collections.emptyList();
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static Collection fromXMLStream( InputStream source, String namespace ) {
        try {
            Unmarshaller unmarshal = JAXBContext.newInstance( namespace ).createUnmarshaller();
            unmarshal.unmarshal( source );
            XMLSerializationHelper loader = unmarshal.getAdapter( XMLSerializationHelper.class );
            return loader.getObjects();
        } catch (JAXBException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static Collection fromXMLString( String data, String namespace ) {
        try {
            Unmarshaller unmarshal = JAXBContext.newInstance( namespace ).createUnmarshaller();
            unmarshal.unmarshal( new ByteArrayInputStream( data.getBytes() ) );
            XMLSerializationHelper loader = unmarshal.getAdapter( XMLSerializationHelper.class );
            return loader.getObjects();
        } catch (JAXBException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public Collection getObjects() {
        return cache.values();
    }

    public Object getObject( String key ) {
        return cache.get( key );
    }

    @Override
    public org.w3._2002._07.owl.Thing unmarshal( UIdAble reborn ) throws Exception {
        if ( cache.containsKey( reborn.getDyReference() ) ) {
            return cache.get( reborn.getDyReference() );
        } else {
            if ( reborn.getDyEntryId() != null && reborn.getDyReference() != null ) {
                // This means that the root object has been passed...
                // Unfortunately, the adapter does not get invoked on the root obj, so we need a hack
                rootObj.setDyEntryId( reborn.getDyReference() );
                reborn = rootObj;
            }
            cache.put( reborn.getDyEntryId(), reborn );
            return reborn;
        }
    }

    @Override
    public UIdAble marshal(org.w3._2002._07.owl.Thing src) throws Exception {
        UIdAble x = (UIdAble) src;
        if ( ! cache.containsKey( x.getDyEntryId() ) ) {
            cache.put( x.getDyEntryId(), x );
            return  x;
        } else {
            Class k = x.getClass();
            UIdAble alter = (UIdAble) k.newInstance();
            alter.setDyEntryId( null );
            alter.setDyReference( x.getDyEntryId() );
            return alter;
        }
    }





}
