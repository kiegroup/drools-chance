package org.drools.informer.util;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnnotationHelper {

    public static List getFields( Class klass ) {
        List fields = new ArrayList();
        while ( ! klass.equals( Object.class ) ) {
            fields.addAll( Arrays.asList(klass.getDeclaredFields()) );
            klass = klass.getSuperclass();
        }
        return fields;
    }
}
