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

package org.drools.chance.builder;

import org.drools.factmodel.ClassDefinition;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public class ImperfectRegistry {

    private static ImperfectRegistry instance;

    private Map<Class,ClassDefinition> wrapperCache;

    private Map<Class,ClassDefinition> beanCache;


    public static ImperfectRegistry getInstance() {
        if ( instance == null ) {
            instance = new ImperfectRegistry();
        }
        return instance;
    }

    protected ImperfectRegistry() {

    }


    public ClassDefinition getDefinition(Class coreKlass) {
        if ( beanCache != null ) {
            if ( beanCache.containsKey( coreKlass ) ) {
                return beanCache.get( coreKlass );
            }
        }
        if ( wrapperCache != null ) {
            if ( wrapperCache.containsKey( coreKlass ) ) {
                return wrapperCache.get( coreKlass );
            }
        }
        return null;
    }




    public void registerImperfectWrapperClass( Class coreKlass ) {
        ClassDefinition def = inspect( coreKlass );
        registerImperfectWrapperClass( def, coreKlass );
    }

    private ClassDefinition inspect(Class coreKlass) {
        return ChanceFactory.inspect(coreKlass);
    }

    public void registerImperfectWrapperClass( ClassDefinition def, Class coreKlass ) {
        if ( wrapperCache == null ) {
            wrapperCache = new HashMap<Class, ClassDefinition>();
        }
        wrapperCache.put( coreKlass, def );
    }





    public void registerImperfectNativeClass( ClassDefinition def, Class coreKlass ) {
        if ( beanCache == null ) {
            beanCache = new HashMap<Class, ClassDefinition>();
        }
        beanCache.put( coreKlass, def );
    }







    public boolean isImperfect( Class coreKlass ) {
        return wrapperCache.containsKey( coreKlass ) || beanCache.containsKey( coreKlass );
    }

    public boolean isWrapper( Class coreKlass ) {
        return wrapperCache.containsKey( coreKlass );
    }

    public boolean isNative( Class coreKlass ) {
        return beanCache.containsKey( coreKlass );
    }




}
