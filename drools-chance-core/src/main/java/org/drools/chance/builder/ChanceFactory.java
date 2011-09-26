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

import org.drools.KnowledgeBase;
import org.drools.RuntimeDroolsException;
import org.drools.common.AbstractRuleBase;
import org.drools.core.util.asm.ClassFieldInspector;
import org.drools.factmodel.BuildUtils;
import org.drools.factmodel.ClassDefinition;
import org.drools.factmodel.FieldDefinition;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.rule.JavaDialectRuntimeData;
import org.drools.rule.Package;
import org.mvel2.asm.*;
import org.mvel2.asm.Type;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ChanceFactory implements Opcodes {


    private static final String pack = "org.drools.chance";

    private static Map<String, Constructor> factoryCache = new HashMap<String, Constructor>();

    private AbstractRuleBase ruleBase;

    private ChanceWrapperBuilder wrapperBuilder = new ChanceWrapperBuilder();
    private ChanceBuilder builder = new ChanceBeanBuilder();


    public static void reset() {
        factoryCache.clear();
    }


    public ChanceFactory(KnowledgeBase knowledgeBase) {
        ruleBase = (AbstractRuleBase) ((KnowledgeBaseImpl) knowledgeBase).getRuleBase();
    }



    public Object asImperfectFact( Object core ) {

        String key = core.getClass().getName();
        Constructor konst = factoryCache.get( key );
        if ( konst == null ) {
            konst = cacheConstructor( core );
        }

        Object proxy = null;
        try {
            proxy = konst.newInstance( core );
            return proxy;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }





    private Constructor cacheConstructor( Object core ) {
        Class proxyClass = buildProxyClass( core );
        if ( proxyClass == null ) {
            return null;
        }
        try {
            for ( Constructor c : proxyClass.getConstructors() ) {
                System.out.println( c );
            }
            Constructor konst = proxyClass.getConstructor( core.getClass() );
            factoryCache.put( core.getClass().getName(), konst );
            return konst;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }




    private Class buildProxyClass( Object core ) {

        Class coreKlass = core.getClass();
        ImperfectRegistry registry = ImperfectRegistry.getInstance();

        if ( ! registry.isImperfect( coreKlass ) ) {
            throw new UnsupportedOperationException( "Class " + coreKlass + " was expected to be ''Imperfect'', but it is not");
        }
        ClassDefinition cdef = registry.getDefinition( coreKlass );
        return buildClass( cdef );
    }

    private Class buildClass( ClassDefinition cdef ) {

        ImperfectRegistry registry = ImperfectRegistry.getInstance();

        byte[] proxy = null;
        String className = null;

        if ( registry.isWrapper( cdef.getDefinedClass() ) ) {
            className = cdef.getClassName() + "Imperfect";
            proxy = wrapperBuilder.buildClass( cdef );
        } else {
            className = cdef.getClassName();
            try {
                proxy = builder.buildClass( cdef );
            } catch ( Exception e ) {}
        }

        if ( proxy != null ) {
            JavaDialectRuntimeData data = ((JavaDialectRuntimeData) getPackage( pack ).getDialectRuntimeRegistry().
                    getDialectData( "java" ));
            data.write(JavaDialectRuntimeData.convertClassToResourcePath( className ), proxy );
            data.onBeforeExecute();

            try {
                Class wrapperClass = (Class<?>) ruleBase.getRootClassLoader().loadClass( className, true );

                System.err.println( "Const = " + wrapperClass.getConstructors().length);
                System.err.println( "Meth = " + wrapperClass.getMethods().length);

                //TODO
//            bindAccessors(wrapperClass, cdef);
                return wrapperClass;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }




//    private void bindAccessors( Class proxyClass, ClassDefinition cdef ) {
//        int j = 0;
//        for ( FieldDefinition traitField : cdef.getFieldsDefinitions() ) {
//            FieldDefinition field = cdef.getField(traitField.getName());
//            Field staticField;
//            try {
//                staticField = proxyClass.getField(field.getName() + "_reader");
//                staticField.set(null, field.getFieldAccessor().getReadAccessor() );
//
//                staticField = proxyClass.getField(field.getName() + "_writer");
//                staticField.set(null, field.getFieldAccessor().getWriteAccessor() );
//            } catch (NoSuchFieldException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            }
//        }
//    }



    private org.drools.rule.Package getPackage(String pack) {
        Package pkg = ruleBase.getPackage( pack );
        if ( pkg == null ) {
            pkg = new Package( pack );
            JavaDialectRuntimeData data = new JavaDialectRuntimeData();
            pkg.getDialectRuntimeRegistry().setDialectData( "java", data );
            data.onAdd(pkg.getDialectRuntimeRegistry(),
                    ruleBase.getRootClassLoader());
            ruleBase.addPackages( Arrays.asList(pkg) );
        }
        return pkg;

    }



    /**
     * To be used with non-drools classes already having imperfect annotations
     * @param coreKlass
     * @return
     */
    public static ClassDefinition inspect(Class coreKlass) {
        ClassDefinition cdef = new ClassDefinition( coreKlass.getName() );
        cdef.setTraitable(false);
        cdef.setDefinedClass( coreKlass );
        cdef.setInterfaces( toInterfaceNames( coreKlass.getInterfaces() ) );
        cdef.setSuperClass( coreKlass.getSuperclass().getName() );
        try {
            ClassFieldInspector inspector = new ClassFieldInspector( coreKlass );
            for ( String fldName : inspector.getFieldNames().keySet() ) {
                if ( ! inspector.isNonGetter( fldName ) && inspector.getSetterMethods().containsKey( fldName ) ) {
                    try {
                        Field field = coreKlass.getDeclaredField(fldName);
                        if ( field != null ) {
                            Imperfect ia = field.getAnnotation(Imperfect.class);

                            if ( ia == null ) {
                                FieldDefinition fldDef = new FieldDefinition( field.getName(), field.getType().getName() );
                                cdef.addField( fldDef );
                            } else {
                                ImperfectFieldDefinition ifldDef = new ImperfectFieldDefinition( field.getName(), field.getType().getName() );
                                ifldDef.setImpKind( ia.kind() );
                                ifldDef.setImpType( ia.type() );
                                ifldDef.setHistory( ia.history() );
                                ifldDef.setDegreeType( ia.degree() );
                                ifldDef.setSupport( ia.support() );
                                cdef.addField( ifldDef );
                            }

                        }
                    } catch ( NoSuchFieldException nsfe ) {
                        System.err.println("OOOPs " + fldName);
                    }
                }
            }
        } catch ( IOException e) {
            return null;
        }
        return cdef;
    }

    private static String[] toInterfaceNames(Class[] interfaces) {
        String[] ifs = new String[ interfaces.length ];
        for ( int j = 0; j < interfaces.length; j ++ ) {
            ifs[ j ] = interfaces[ j ].getName();
        }
        return ifs;
    }













































}
