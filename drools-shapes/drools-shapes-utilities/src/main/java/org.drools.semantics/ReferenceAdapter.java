package org.drools.semantics;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ReferenceAdapter extends XmlAdapter<UIdAble,Thing> {


    private Map<String,UIdAble> cache = new HashMap<String, UIdAble>();

    public ReferenceAdapter() {
        cache = new HashMap<String, UIdAble>();
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
            ReferenceAdapter loader = unmarshal.getAdapter( ReferenceAdapter.class );
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
            ReferenceAdapter loader = unmarshal.getAdapter( ReferenceAdapter.class );
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
            ReferenceAdapter loader = unmarshal.getAdapter( ReferenceAdapter.class );
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
    public Thing unmarshal(UIdAble v) throws Exception {
        if ( cache.containsKey( v.getDyEntryId() ) ) {
            return (Thing) ( cache.get( v.getDyEntryId() ) );
        } else {
            UIdAble reborn = v;
            String baseType = v.getClass().getPackage().getName() + "." + v.getDyEntryType();
            String actualClass = baseType + "Impl";
            if ( ! v.getClass().getName().equals( actualClass ) ) {

                Class k = Class.forName( actualClass );
                throw new UnsupportedOperationException( "Used to call a shadow constructor..." );
//                if ( k != null ) {
//                    String desiredClass = baseType + "$$Shadow";
//                    Class shadowInterface = Class.forName(desiredClass );
//                    if ( shadowInterface != null ) {
//                        Constructor con = k.getConstructor( shadowInterface );
//                        reborn = (UIdAble) con.newInstance( v );
//                    }
//                }
            }

            cache.put( reborn.getDyEntryId(), reborn );
            return (Thing) reborn;
        }
    }

    @Override
    public UIdAble marshal(Thing v) throws Exception {

        if ( v instanceof UIdAble) {
            UIdAble x = (UIdAble) v;
            
            x.setDyEntryType( x.getClass().getSimpleName().substring(0, x.getClass().getSimpleName().lastIndexOf("Impl")));
            
            if ( ! cache.containsKey(x.getDyEntryId()) ) {
                cache.put( x.getDyEntryId(), x );
                return  x;
            } else {
                Class k = x.getClass();
                UIdAble alter = (UIdAble) k.newInstance();
                alter.setDyEntryId( x.getDyEntryId() );
                alter.setDyEntryType( x.getDyEntryType() );
                alter.setDyReference( true );
                return alter;
            }
        }
        return null;
    }





}
