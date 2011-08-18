/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.informer.generator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class FormRegistry {

    private static Map< String, Constructor<? extends ISurveyable> > registry = new HashMap<String, Constructor<? extends ISurveyable>>();


    public static ISurveyable create( String type, String id ) {
        Constructor<? extends ISurveyable> c = registry.get( type );
        try {
        if ( c != null ) {
            ISurveyable form = c.newInstance();
            if ( form instanceof Surveyable ) {
                ((Surveyable) form).setQuestionnaireId( id );
            }
            return form;
        }
        } catch (InvocationTargetException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InstantiationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }


    public static void register( Class klass ) {
        try {
            if ( ISurveyable.class.isAssignableFrom( klass ) ) {
                registry.put( klass.getName(), klass.getConstructor() );
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
