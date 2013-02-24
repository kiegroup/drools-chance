package org.drools.shapes.xsd;

import com.sun.org.apache.xpath.internal.operations.Mod;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Jaxplorer {

    private static Map<Class, Collection<String>> declaredCache = new HashMap<Class, Collection<String>>();

    private Object root;
    private Collection<FactHandle> handles = new LinkedList<FactHandle>();

    public Jaxplorer( Object root ) {
        this.root = root;
    }

    public void deepInsert( StatefulKnowledgeSession kSession ) {
        deepInsert( root, kSession );
    }

    protected void deepInsert( Object o, StatefulKnowledgeSession kSession ) {
        if ( o instanceof JAXBElement ) {
            kSession.insert( o );
            deepInsert( ((JAXBElement) o).getValue(), kSession );
        }
        XmlType xmlType = o.getClass().getAnnotation( XmlType.class );
        if ( xmlType  != null ) {
            handles.add( kSession.insert( o ) );
            for ( String fname : getAllXmlFields(o) ) {
                try {
                    String getterName = "get" + fname.substring( 0, 1 ).toUpperCase() + fname.substring( 1 );
                    Method getter = o.getClass().getMethod( getterName );
                    Object fieldVal = getter.invoke( o );
                    if ( fieldVal instanceof Collection ) {
                        for ( Object x : (Collection) fieldVal ) {
                            deepInsert( x, kSession );
                        }
                    } else {
                        if ( fieldVal != null ) {
                            deepInsert( fieldVal, kSession );
                        }
                    }
                } catch ( IllegalAccessException e ) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void deepRetract( StatefulKnowledgeSession kSession ) {
        for ( FactHandle h : handles ) {
            kSession.retract( h );
        }
    }




    private Collection<String> getAllXmlFields( Object o ) {
        if ( declaredCache.containsKey( o.getClass() ) ) {
            return declaredCache.get( o.getClass() );
        }

        List<String> fieldNames = new LinkedList<String>();

        Class x = o.getClass();
        while ( ! x.equals( Object.class ) ) {

            XmlType xmlType = (XmlType) x.getAnnotation( XmlType.class );
            if ( xmlType != null && xmlType.propOrder().length > 0 && ! xmlType.propOrder()[0].isEmpty() ) {
                fieldNames.addAll( Arrays.asList( xmlType.propOrder() ) );
            }

            x = x.getSuperclass();
        }

//        System.out.println( "Class " + o.getClass() );
//        for ( String s : fieldNames ) {
//            System.out.println( "\t >> " + s );
//        }

        declaredCache.put( o.getClass(), fieldNames );
        return fieldNames;
    }

}
