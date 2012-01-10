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

import org.drools.RuntimeDroolsException;
import org.drools.chance.common.IImperfectField;
import org.drools.chance.common.ImperfectField;
import org.drools.factmodel.AnnotationDefinition;
import org.drools.factmodel.BuildUtils;
import org.drools.factmodel.ClassDefinition;
import org.drools.factmodel.FieldDefinition;
import org.drools.factmodel.traits.TraitFactory;
import org.drools.factmodel.traits.TraitTripleProxyClassBuilderImpl;
import org.mvel2.asm.*;

import java.lang.reflect.Method;
import java.util.*;


public class ChanceTripleProxyBuilderImpl extends TraitTripleProxyClassBuilderImpl {


    private Map<FieldDefinition,ImperfectFieldDefinition> supportFields;



    public void init( ClassDefinition trait ) {
        super.init( trait );

    }



    protected void buildConstructorCore( ClassWriter cw, MethodVisitor mv, String internalProxy, String internalWrapper, String internalCore, String descrCore, String mixin, Class mixinClass ) {
        super.buildConstructorCore( cw, mv, internalProxy, internalWrapper, internalCore, descrCore, mixin, mixinClass );

        //TODO reenable when ready
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, internalProxy, "synchFields", "()V");

    }


    protected void addSupportField( FieldDefinition supportField, ImperfectFieldDefinition fuzzyField ) {
        if ( supportFields == null ) {
            supportFields = new HashMap<FieldDefinition, ImperfectFieldDefinition>();
        }
        supportFields.put( supportField, fuzzyField );
    }

    protected FieldDefinition getSupportField( ImperfectFieldDefinition fuzzyField ) {
        if ( supportFields == null ) {
            return null;
        }
        for ( FieldDefinition fld : supportFields.keySet() ) {
            if ( fuzzyField.equals( supportFields.get( fld ) ) ) {
                return fld;
            }
        }
        return null;
    }

    protected ImperfectFieldDefinition getFuzzyField( FieldDefinition field ) {
        if ( supportFields == null ) {
            return null;
        }
        return supportFields.get( field );
    }


    protected FieldDefinition findSupportField(ClassDefinition cdef, ImperfectFieldDefinition ifld) {
        String target = ifld.getSupport();
        FieldDefinition tfld = cdef.getField( target );
        if ( target == null || tfld == null ) {
            throw new RuntimeDroolsException( " Fuzzy Linguistic Field " + ifld.getName() + " requires a support field, not found " + target );
        }
        return  tfld;
    }

    protected boolean isLinguistic(ImperfectFieldDefinition ifld) {
        return "fuzzy".equals( ifld.getImpKind() );
    }


    protected void buildProxyAccessors( long mask, ClassWriter cw, String masterName, ClassDefinition core, Map<String,Method> mixinGetSet) {
        for ( FieldDefinition field : getTrait().getFieldsDefinitions() ) {
            if ( field instanceof VirtualFieldDefinition ) continue;
            if ( field instanceof ImperfectFieldDefinition ) {
                ImperfectFieldDefinition ifld = (ImperfectFieldDefinition) field;
                if ( isLinguistic( ifld ) ) {
                    addSupportField( findSupportField( getTrait(), ifld ), ifld );
                }
            }
        }

        super.buildProxyAccessors( mask, cw, masterName, core, mixinGetSet );

    }






    protected void buildProxyAccessor(long mask, ClassWriter cw, String masterName, ClassDefinition core, Map<String, Method> mixinGetSet, FieldDefinition field, boolean isSoftField ) {
        if ( field instanceof VirtualFieldDefinition ) return;
        if ( ! ( field instanceof ImperfectFieldDefinition ) ) {
            super.buildProxyAccessor(mask, cw, masterName, core, mixinGetSet, field, isSoftField );

        } else {
            buildImperfectGetter( cw, field, masterName, core, isSoftField );
            buildImperfectSetter( cw, field, masterName, core, isSoftField );

            if ( ! isSoftField ) {
                FieldVisitor fv;
                {
                    fv = cw.visitField(ACC_PUBLIC + ACC_STATIC, field.getName()+"_reader", "Lorg/drools/spi/InternalReadAccessor;", null, null);
                    fv.visitEnd();
                }
                {
                    fv = cw.visitField(ACC_PUBLIC + ACC_STATIC, field.getName()+"_writer", "Lorg/drools/spi/WriteAccessor;", null, null);
                    fv.visitEnd();
                }

            }
        }
    }




    protected void buildHardSetter( ClassVisitor cw, FieldDefinition field, String masterName, ClassDefinition proxy, ClassDefinition core, String setterName, boolean protect ) {
        if ( supportFields != null && supportFields.containsKey( field ) ) {
            ImperfectFieldDefinition fuzzyField = getFuzzyField( field );

            String fieldName = field.getName();
            String fieldType = field.getTypeName();


            // setX( val ) {
            //  setX( val, true);
            // }
            {
                MethodVisitor mv = cw.visitMethod( protect ? ACC_PROTECTED : ACC_PUBLIC,
                        setterName,
                        "(" + BuildUtils.getTypeDescriptor( fieldType ) + ")V",
                        null,
                        null);
                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitInsn(ICONST_1);
                mv.visitMethodInsn(INVOKESPECIAL,
                        BuildUtils.getInternalType( masterName ),
                        BuildUtils.setterName( fieldName, fieldType ),
                        "(" + BuildUtils.getTypeDescriptor( fieldType )+ "Z)V");

                mv.visitInsn(RETURN);
                mv.visitMaxs(3,2);
                mv.visitEnd();
            }


            // setX( val, boolean )
            {
                MethodVisitor mv = cw.visitMethod( protect ? ACC_PROTECTED : ACC_PUBLIC,
                        setterName,
                        "(" + BuildUtils.getTypeDescriptor( fieldType ) + "Z)V",
                        null,
                        null);
                mv.visitCode();

                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKEVIRTUAL,
                        BuildUtils.getInternalType( masterName ),
                        BuildUtils.getterName( fuzzyField.getName(), fuzzyField.getTypeName() ),
                        "()Lorg/drools/chance/common/IImperfectField;");
                mv.visitTypeInsn( CHECKCAST, "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField" );
                mv.visitVarInsn( ALOAD, 1 );
                mv.visitMethodInsn( INVOKEVIRTUAL,
                        "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField",
                        "fuzzify",
                        "(Ljava/lang/Number;)Lorg/drools/chance/distribution/IDistribution;" );
                mv.visitVarInsn(ASTORE, 3);





                mv.visitVarInsn(ILOAD, 2);
                Label l0 = new Label();
                mv.visitJumpInsn(IFEQ, l0);

                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKEVIRTUAL,
                        BuildUtils.getInternalType( masterName ),
                        BuildUtils.getterName( fuzzyField.getName(), fuzzyField.getTypeName() ),
                        "()Lorg/drools/chance/common/IImperfectField;");
                mv.visitVarInsn(ALOAD, 3);
                mv.visitInsn(ICONST_0);
                mv.visitMethodInsn(INVOKEINTERFACE,
                        "org/drools/chance/common/IImperfectField",
                        "setValue",
                        "(Lorg/drools/chance/distribution/IDistribution;Z)V");

                mv.visitLabel(l0);



                mv.visitVarInsn( ALOAD, 0 );
                mv.visitVarInsn( ALOAD, 0 );
                mv.visitMethodInsn(INVOKEVIRTUAL,
                        BuildUtils.getInternalType( masterName ),
                        BuildUtils.getterName( fuzzyField.getName(), fuzzyField.getTypeName() ),
                        "()Lorg/drools/chance/common/IImperfectField;");
                mv.visitMethodInsn( INVOKEINTERFACE,
                        "org/drools/chance/common/IImperfectField",
                        "getCrisp",
                        "()Ljava/lang/Object;");
                mv.visitTypeInsn( CHECKCAST, BuildUtils.getInternalType( fuzzyField.getTypeName() )  );
                mv.visitMethodInsn( INVOKEVIRTUAL,
                        BuildUtils.getInternalType( masterName ),
                        BuildUtils.setterName( fuzzyField.getName(), fuzzyField.getTypeName() )+"Core",
                        "(" + BuildUtils.getTypeDescriptor( fuzzyField.getTypeName() ) + ")V" );


                TraitFactory.invokeInjector( mv, masterName, getTrait(), core, field, false, 1 );

                mv.visitInsn(RETURN);
//            mv.visitMaxs( 2 + BuildUtils.sizeOf( fieldType ),
//                    1 + BuildUtils.sizeOf( fieldType ) );
                mv.visitMaxs(3,4);
                mv.visitEnd();
            }

        } else {


            super.buildHardSetter( cw, field, masterName, proxy, core, setterName, protect );


        }

    }





    protected void buildSoftSetter( ClassVisitor cw, FieldDefinition field, String proxy, String setterName, boolean protect ) {
        if ( supportFields != null && supportFields.containsKey( field ) ) {
            ImperfectFieldDefinition fuzzyField = getFuzzyField( field );

            String fieldName = field.getName();
            String fieldType = field.getTypeName();


            // setX( val ) {
            //  setX( val, true);
            // }
            {
                MethodVisitor mv = cw.visitMethod( protect ? ACC_PROTECTED : ACC_PUBLIC,
                        setterName,
                        "(" + BuildUtils.getTypeDescriptor( fieldType ) + ")V",
                        null,
                        null);
                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitInsn(ICONST_1);
                mv.visitMethodInsn(INVOKESPECIAL,
                        BuildUtils.getInternalType( proxy ),
                        BuildUtils.setterName( fieldName, fieldType ),
                        "(" + BuildUtils.getTypeDescriptor( fieldType )+ "Z)V");

                mv.visitInsn(RETURN);
                mv.visitMaxs(3,2);
                mv.visitEnd();
            }


            // setX( val, boolean )
            {
                MethodVisitor mv = cw.visitMethod( protect ? ACC_PROTECTED : ACC_PUBLIC,
                        setterName,
                        "(" + BuildUtils.getTypeDescriptor( fieldType ) + "Z)V",
                        null,
                        null);
                mv.visitCode();

                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKEVIRTUAL,
                        BuildUtils.getInternalType( proxy ),
                        BuildUtils.getterName( fuzzyField.getName(), fuzzyField.getTypeName() ),
                        "()Lorg/drools/chance/common/IImperfectField;");
                mv.visitTypeInsn( CHECKCAST, "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField" );
                mv.visitVarInsn( ALOAD, 1 );
                mv.visitMethodInsn( INVOKEVIRTUAL,
                        "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField",
                        "fuzzify",
                        "(Ljava/lang/Number;)Lorg/drools/chance/distribution/IDistribution;" );
                mv.visitVarInsn(ASTORE, 3);





                mv.visitVarInsn(ILOAD, 2);
                Label l0 = new Label();
                mv.visitJumpInsn(IFEQ, l0);

                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKEVIRTUAL,
                        BuildUtils.getInternalType( proxy ),
                        BuildUtils.getterName( fuzzyField.getName(), fuzzyField.getTypeName() ),
                        "()Lorg/drools/chance/common/IImperfectField;");
                mv.visitVarInsn(ALOAD, 3);
                mv.visitInsn(ICONST_0);
                mv.visitMethodInsn(INVOKEINTERFACE,
                        "org/drools/chance/common/IImperfectField",
                        "setValue",
                        "(Lorg/drools/chance/distribution/IDistribution;Z)V");

                mv.visitLabel(l0);



                mv.visitVarInsn( ALOAD, 0 );

                mv.visitVarInsn( ALOAD, 0 );
                mv.visitMethodInsn(INVOKEVIRTUAL,
                        BuildUtils.getInternalType( proxy ),
                        BuildUtils.getterName( fuzzyField.getName(), fuzzyField.getTypeName() ),
                        "()Lorg/drools/chance/common/IImperfectField;");
                mv.visitMethodInsn( INVOKEINTERFACE,
                        "org/drools/chance/common/IImperfectField",
                        "getCrisp",
                        "()Ljava/lang/Object;");
                mv.visitTypeInsn( CHECKCAST, BuildUtils.getInternalType( fuzzyField.getTypeName() )  );
                mv.visitMethodInsn( INVOKEVIRTUAL,
                        BuildUtils.getInternalType( proxy ),
                        BuildUtils.setterName( fuzzyField.getName(), fuzzyField.getTypeName() )+"Core",
                        "(" + BuildUtils.getTypeDescriptor( fuzzyField.getTypeName() ) + ")V" );



//            mv.visitVarInsn( ALOAD, 0 );
//            mv.visitFieldInsn(GETFIELD, BuildUtils.getInternalType( proxy ), "store", "Lorg/drools/core/util/TripleStore;");
//            mv.visitMethodInsn(INVOKEVIRTUAL, BuildUtils.getInternalType( proxy ), "property", "(Ljava/lang/String;Ljava/lang/Object;)Lorg/drools/core/util/TripleImpl;");
//            mv.visitMethodInsn(INVOKEVIRTUAL, "org/drools/core/util/TripleStore", "put", "(Lorg/drools/core/util/Triple;)Z");

                mv.visitInsn(RETURN);

                mv.visitMaxs(5,4);
                mv.visitEnd();
            }

        } else {


            super.buildSoftSetter( cw, field, proxy, setterName, protect );


        }

    }






    protected void buildExtendedMethods(ClassWriter cw, ClassDefinition trait, ClassDefinition core ) {
        buildSynchFields( cw, TraitFactory.getProxyName(trait, core), core.getName(), getTrait() );
    }






    protected void buildSynchFields( ClassWriter cw, String proxyName, String coreName, ClassDefinition def ) {
        {
            MethodVisitor mv = cw.visitMethod(ACC_PRIVATE, "synchFields", "()V", null, null);
            mv.visitCode();

            for ( FieldDefinition fld : def.getFieldsDefinitions() ) {
                if ( fld instanceof VirtualFieldDefinition ) continue;
                if ( fld instanceof ImperfectFieldDefinition ) {
                    ImperfectFieldDefinition ifld = (ImperfectFieldDefinition) fld;
                    if ( isLinguistic( ifld ) ) {

                        ///TODO

//                        FieldDefinition tfld = getSupportField(def, ifld);
//
//                        getTargetValue( mv, ifld, wrapperName, coreName );
//
//                        Label l4 = new Label();
//                        mv.visitJumpInsn(IFNULL, l4);
//
//                        getTargetValue( mv, tfld, wrapperName, coreName );
//
//                        mv.visitVarInsn(ASTORE, 1);
//                        mv.visitVarInsn(ALOAD, 0);
//                        mv.visitFieldInsn( GETFIELD,
//                                BuildUtils.getInternalType( wrapperName ),
//                                ifld.getName() + "_$$Imp",
//                                "Lorg/drools/chance/common/IImperfectField;");
//                        mv.visitTypeInsn(CHECKCAST, "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField");
//                        mv.visitVarInsn(ALOAD, 1);
//                        mv.visitMethodInsn( INVOKEVIRTUAL,
//                                "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField",
//                                "fuzzify",
//                                "(Ljava/lang/Number;)Lorg/drools/chance/distribution/IDistribution;");
//                        mv.visitVarInsn(ASTORE, 2);
//                        mv.visitVarInsn(ALOAD, 0);
//                        mv.visitFieldInsn( GETFIELD,
//                                BuildUtils.getInternalType( wrapperName ),
//                                ifld.getName() + "_$$Imp",
//                                "Lorg/drools/chance/common/IImperfectField;");
//                        mv.visitVarInsn(ALOAD, 2);
//                        mv.visitInsn(ICONST_0);
//                        mv.visitMethodInsn( INVOKEINTERFACE,
//                                "org/drools/chance/common/IImperfectField",
//                                "setValue",
//                                "(Lorg/drools/chance/distribution/IDistribution;Z)V");
//                        mv.visitVarInsn(ALOAD, 0);
//                        prepareSetTargetValue( mv, wrapperName, coreName );
//                        mv.visitVarInsn(ALOAD, 0);
//                        mv.visitFieldInsn( GETFIELD,
//                                BuildUtils.getInternalType( wrapperName ),
//                                ifld.getName() + "_$$Imp",
//                                "Lorg/drools/chance/common/IImperfectField;");
//                        mv.visitMethodInsn( INVOKEINTERFACE,
//                                "org/drools/chance/common/IImperfectField",
//                                "getCrisp",
//                                "()Ljava/lang/Object;");
//                        mv.visitTypeInsn(CHECKCAST, BuildUtils.getInternalType( ifld.getTypeName() ));
//                        setTargetValue( mv, coreName, ifld );
//                        mv.visitVarInsn(ALOAD, 0);
//                        prepareSetTargetValue( mv, wrapperName, coreName );
//
//                        mv.visitVarInsn(ALOAD, 1);
//
//                        setTargetValue( mv, coreName, tfld );
//
//                        Label l5 = new Label();
//                        mv.visitJumpInsn(GOTO, l5);
//                        mv.visitLabel(l4);
//
//                        getTargetValue( mv, ifld, wrapperName, coreName );
//
//                        Label l6 = new Label();
//                        mv.visitJumpInsn(IFNULL, l6);
//                        mv.visitVarInsn(ALOAD, 0);
//                        mv.visitFieldInsn( GETFIELD,
//                                BuildUtils.getInternalType( wrapperName ),
//                                ifld.getName() + "_$$Imp",
//                                "Lorg/drools/chance/common/IImperfectField;");
//
//                        getTargetValue( mv, ifld, wrapperName, coreName );
//
//                        mv.visitMethodInsn( INVOKEINTERFACE,
//                                "org/drools/chance/common/IImperfectField",
//                                "setValue",
//                                "(Ljava/lang/Object;)V");
//                        mv.visitLabel(l6);
//                        mv.visitVarInsn(ALOAD, 0);
//                        mv.visitFieldInsn( GETFIELD,
//                                BuildUtils.getInternalType( wrapperName ),
//                                ifld.getName() + "_$$Imp",
//                                "Lorg/drools/chance/common/IImperfectField;");
//                        mv.visitJumpInsn(IFNULL, l5);
//                        mv.visitVarInsn(ALOAD, 0);
//                        prepareSetTargetValue( mv, wrapperName, coreName );
//
//                        mv.visitVarInsn(ALOAD, 0);
//                        mv.visitFieldInsn( GETFIELD,
//                                BuildUtils.getInternalType( wrapperName ),
//                                ifld.getName() + "_$$Imp",
//                                "Lorg/drools/chance/common/IImperfectField;");
//                        mv.visitMethodInsn( INVOKEINTERFACE,
//                                "org/drools/chance/common/IImperfectField",
//                                "getCrisp",
//                                "()Ljava/lang/Object;");
//                        mv.visitTypeInsn(  CHECKCAST, BuildUtils.getInternalType( ifld.getTypeName() ));
//                        setTargetValue( mv, coreName, ifld );
//
//                        mv.visitVarInsn(ALOAD, 0);
//                        prepareSetTargetValue( mv, wrapperName, coreName );
//
//                        mv.visitVarInsn(ALOAD, 0);
//                        mv.visitFieldInsn( GETFIELD,
//                                BuildUtils.getInternalType( wrapperName ),
//                                ifld.getName() + "_$$Imp",
//                                "Lorg/drools/chance/common/IImperfectField;");
//                        mv.visitTypeInsn(CHECKCAST, "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField");
//                        mv.visitMethodInsn( INVOKEVIRTUAL,
//                                "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField",
//                                "defuzzify",
//                                "()Ljava/lang/Number;");
//                        mv.visitTypeInsn(CHECKCAST, BuildUtils.getInternalType( tfld.getTypeName() ) );
//                        setTargetValue( mv, coreName, tfld );
//                        mv.visitLabel(l5);


                    } else {

                        // core field simple value
                        mv.visitVarInsn( ALOAD, 0 );
                        mv.visitMethodInsn( INVOKEVIRTUAL,
                                BuildUtils.getInternalType( proxyName ),
                                BuildUtils.getterName( ifld.getName(), ifld.getTypeName() )+ "Core",
                                "()" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) );
                        mv.visitVarInsn( ASTORE, 1 );

                        // imp. field value
                        mv.visitVarInsn( ALOAD, 0 );
                        mv.visitMethodInsn( INVOKEVIRTUAL,
                                BuildUtils.getInternalType( proxyName ),
                                BuildUtils.getterName( ifld.getName(), ifld.getTypeName() ),
                                "()Lorg/drools/chance/common/IImperfectField;");
                        mv.visitVarInsn(ASTORE, 2);

                        mv.visitVarInsn(ALOAD, 1);
                        Label l0 = new Label();
                        mv.visitJumpInsn(IFNULL, l0);

                        mv.visitVarInsn( ALOAD, 2 );
                        mv.visitVarInsn( ALOAD, 1 );
                        mv.visitInsn( ICONST_0 );
                        mv.visitMethodInsn( INVOKEINTERFACE,
                                "org/drools/chance/common/IImperfectField",
                                "setValue",
                                "(Ljava/lang/Object;Z)V" );
                        mv.visitLabel(l0);

//                        mv.visitFieldInsn( GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;" );
//                        mv.visitVarInsn( ALOAD, 2 );
//                        mv.visitMethodInsn( INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/Object;)V" );

                        mv.visitVarInsn( ALOAD, 0 );
                        mv.visitVarInsn( ALOAD, 2 );
                        mv.visitMethodInsn( INVOKEINTERFACE,
                                "org/drools/chance/common/IImperfectField",
                                "getCrisp",
                                "()Ljava/lang/Object;" );
                        mv.visitTypeInsn( CHECKCAST, BuildUtils.getInternalType( ifld.getTypeName() ) );
                        mv.visitMethodInsn( INVOKEVIRTUAL,
                                BuildUtils.getInternalType( proxyName ),
                                BuildUtils.setterName( ifld.getName(), ifld.getTypeName())+"Core",
                                "(" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) + ")V" );

                    }
                }

            }

            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 3);
            mv.visitEnd();

        }

    }



















    protected void buildImperfectGetter(ClassVisitor cw, FieldDefinition ifld, String wrapperName, ClassDefinition core, boolean softField) {

        MethodVisitor mv;
        String getter = BuildUtils.getterName( ifld.getName(), ifld.getTypeName() );

        {
            mv = cw.visitMethod( ACC_PUBLIC,
                    getter,
                    "()Lorg/drools/chance/common/IImperfectField;",
                    "()Lorg/drools/chance/common/IImperfectField<" + BuildUtils.getTypeDescriptor( ifld.getTypeName() )+ ">;", null);
            mv.visitCode();

            int stack = getTargetDistField(mv, ifld, wrapperName, core.getName(), softField);

            mv.visitInsn(ARETURN);
            mv.visitMaxs( 1 + stack, 1);
            mv.visitEnd();
        }

        {
            mv = cw.visitMethod( ACC_PUBLIC,
                    getter+"Distr",
                    "()Lorg/drools/chance/distribution/IDistribution;",
                    "()Lorg/drools/chance/distribution/IDistribution<" + BuildUtils.getTypeDescriptor( ifld.getTypeName() )+ ">;",
                    null);
            mv.visitCode();

            int stack = getTargetDistField(mv, ifld, wrapperName, core.getName(), softField);
            mv.visitMethodInsn( INVOKEINTERFACE,
                    "org/drools/chance/common/IImperfectField",
                    "getCurrent",
                    "()Lorg/drools/chance/distribution/IDistribution;");

            mv.visitInsn(ARETURN);
            mv.visitMaxs( 1 + stack, 1);
            mv.visitEnd();
        }

        {
            mv = cw.visitMethod( ACC_PUBLIC,
                    getter+"Value",
                    "()"+BuildUtils.getTypeDescriptor( ifld.getTypeName() ),
                    null, null);
            mv.visitCode();
            int stack = getTargetCrispValue( mv, ifld, wrapperName, core.getName(), softField );
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1 + stack, 1);
            mv.visitEnd();
        }

        if ( softField ) {
            buildSoftGetter( cw,
                    ifld,
                    wrapperName,
                    BuildUtils.getterName( ifld.getName(), ifld.getTypeName() ) + "Core",
                    true);
        } else {
            buildHardGetter(cw,
                    ifld,
                    wrapperName,
                    getTrait(),
                    core,
                    BuildUtils.getterName(ifld.getName(), ifld.getTypeName()) + "Core",
                    true);
        }

    }





    protected void buildImperfectSetter(ClassVisitor cw, FieldDefinition field, String wrapperName, ClassDefinition core, boolean softField) {

        MethodVisitor mv;
        String setter = BuildUtils.setterName( field.getName(), field.getTypeName() );


        FieldDefinition lingTarget = null;
        ImperfectFieldDefinition ifld = (ImperfectFieldDefinition) field;
        if ( isLinguistic( ifld  ) ) {
            lingTarget = getSupportField( ifld );
        }

        {
            mv = cw.visitMethod( ACC_PUBLIC,
                    setter,
                    "(Lorg/drools/chance/common/IImperfectField;)V",
                    "(Lorg/drools/chance/common/IImperfectField<" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) + ">;)V",
                    null);
            int stack = 2;
            mv.visitCode();


            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, BuildUtils.getInternalType( wrapperName ), "store", "Lorg/drools/core/util/TripleStore;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitLdcInsn( ifld.getName() + "_$$Imp" );
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, BuildUtils.getInternalType( wrapperName ), "property", "(Ljava/lang/String;Ljava/lang/Object;)Lorg/drools/core/util/TripleImpl;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/drools/core/util/TripleStore", "put", "(Lorg/drools/core/util/Triple;)Z");
            mv.visitInsn(POP);



            mv.visitVarInsn(ALOAD, 0);
            prepareSetTargetValue( mv, wrapperName, core.getName(), ifld, softField);
            getTargetDistField( mv, ifld, wrapperName, core.getName(), softField );
            mv.visitMethodInsn( INVOKEINTERFACE,
                    "org/drools/chance/common/IImperfectField",
                    "getCrisp",
                    "()Ljava/lang/Object;");

            stack += setTargetValue( mv, wrapperName, core.getName(), ifld, softField );


            if ( isLinguistic( ifld ) ) {
                updateSupportField( mv, ifld, wrapperName, lingTarget );
            }



            mv.visitInsn(RETURN);
            mv.visitMaxs( 2 + stack, 2);
            mv.visitEnd();
        }
//





        {
            mv = cw.visitMethod( ACC_PUBLIC,
                    setter.replace( "set", "update" ),
                    "(Lorg/drools/chance/common/IImperfectField;)V",
                    "(Lorg/drools/chance/common/IImperfectField<" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) + ">;)V",
                    null);
            int stack = 2;
            mv.visitCode();


            getTargetDistField( mv, ifld, wrapperName, core.getName(), softField );
            mv.visitVarInsn(ASTORE, 2);


            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEINTERFACE, "org/drools/chance/common/IImperfectField", "getCurrent", "()Lorg/drools/chance/distribution/IDistribution;");
            mv.visitMethodInsn(INVOKEINTERFACE, "org/drools/chance/common/IImperfectField", "update", "(Lorg/drools/chance/distribution/IDistribution;)V");


            mv.visitVarInsn(ALOAD, 0);
            prepareSetTargetValue( mv, wrapperName, core.getName(), ifld, softField);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn( INVOKEINTERFACE,
                    "org/drools/chance/common/IImperfectField",
                    "getCrisp",
                    "()Ljava/lang/Object;");
            stack += setTargetValue( mv, wrapperName, core.getName(), ifld, softField );


            if ( isLinguistic( ifld ) ) {
                updateSupportField( mv, ifld, wrapperName, lingTarget );
            }



            mv.visitInsn(RETURN);
            mv.visitMaxs( 2 + stack, 3 );
            mv.visitEnd();
        }




        {
            mv = cw.visitMethod( ACC_PUBLIC,
                    setter + "Value",
                    "(" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) + ")V",
                    null, null);
            mv.visitCode();

            int stack = getTargetDistField( mv, ifld, wrapperName, core.getName(), softField );
            mv.visitVarInsn(ASTORE, 2);

            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_0);
            mv.visitMethodInsn( INVOKEINTERFACE,
                    "org/drools/chance/common/IImperfectField",
                    "setValue",
                    "(Ljava/lang/Object;Z)V");

            mv.visitVarInsn(ALOAD, 0);
            prepareSetTargetValue( mv, wrapperName, core.getName(), ifld, softField);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn( INVOKEINTERFACE,
                    "org/drools/chance/common/IImperfectField",
                    "getCrisp",
                    "()Ljava/lang/Object;");

            int stack2 = setTargetValue( mv, wrapperName, core.getName(), ifld, softField );

            if ( isLinguistic( ifld ) ) {
                updateSupportField( mv, ifld, wrapperName, lingTarget );
            }

            mv.visitInsn(RETURN);
            mv.visitMaxs( 2 + stack + stack2, 3);
            mv.visitEnd();
        }




        {
            mv = cw.visitMethod( ACC_PUBLIC,
                    setter.replace("set","update")+"Value",
                    "(" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) + ")V",
                    null, null);
            mv.visitCode();

            getTargetDistField( mv, ifld, wrapperName, core.getName(), softField );
            mv.visitVarInsn(ASTORE, 2);


            mv.visitVarInsn(ALOAD, 2);
//            int stack = getTargetDistField( mv, ifld, wrapperName, core.getName(), softField );
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn( INVOKEINTERFACE,
                    "org/drools/chance/common/IImperfectField",
                    "update",
                    "(Ljava/lang/Object;)V");

            mv.visitVarInsn(ALOAD, 0);
            prepareSetTargetValue( mv, wrapperName, core.getName(), ifld, softField );
//            getTargetDistField( mv, ifld, wrapperName, core.getName(), softField );
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn( INVOKEINTERFACE,
                    "org/drools/chance/common/IImperfectField",
                    "getCrisp",
                    "()Ljava/lang/Object;");

            int stack2 = setTargetValue( mv, wrapperName, core.getName(), ifld, softField );

            if ( isLinguistic( ifld ) ) {
                updateSupportField( mv, ifld, wrapperName, lingTarget );
            }

            mv.visitInsn(RETURN);
            mv.visitMaxs( 3  + stack2, 3);
            mv.visitEnd();
        }






        {
            mv = cw.visitMethod( ACC_PUBLIC,
                    setter+"Distr",
                    "(Lorg/drools/chance/distribution/IDistribution;)V",
                    "(Lorg/drools/chance/distribution/IDistribution<" +BuildUtils.getTypeDescriptor( ifld.getTypeName() ) +">;)V",
                    null);
            mv.visitCode();


            int stack = getTargetDistField( mv, ifld, wrapperName, core.getName(), softField);
            mv.visitVarInsn(ASTORE, 2);


            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_0);
            mv.visitMethodInsn( INVOKEINTERFACE,
                    "org/drools/chance/common/IImperfectField",
                    "setValue",
                    "(Lorg/drools/chance/distribution/IDistribution;Z)V");


            mv.visitVarInsn(ALOAD, 0);
            prepareSetTargetValue(mv, wrapperName, core.getName(), ifld, softField);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn( INVOKEINTERFACE,
                    "org/drools/chance/common/IImperfectField",
                    "getCrisp",
                    "()Ljava/lang/Object;");
            int stack2 = setTargetValue( mv, wrapperName, core.getName(), ifld, softField );

            if ( isLinguistic( ifld ) ) {
                updateSupportField( mv, ifld, wrapperName, lingTarget );
            }

            mv.visitInsn(RETURN);
            mv.visitMaxs(2 + stack + stack2, 3);
            mv.visitEnd();
        }





        {
            mv = cw.visitMethod( ACC_PUBLIC,
                    setter.replace("set","update") + "Distr",
                    "(Lorg/drools/chance/distribution/IDistribution;)V",
                    "(Lorg/drools/chance/distribution/IDistribution<" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) + ">;)V",
                    null);
            mv.visitCode();

            getTargetDistField( mv, ifld, wrapperName, core.getName(), softField );
            mv.visitVarInsn(ASTORE, 2);




            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn( INVOKEINTERFACE,
                    "org/drools/chance/common/IImperfectField",
                    "update",
                    "(Lorg/drools/chance/distribution/IDistribution;)V");



            mv.visitVarInsn(ALOAD, 0);
            prepareSetTargetValue( mv, wrapperName, core.getName(), ifld, softField );
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn( INVOKEINTERFACE,
                    "org/drools/chance/common/IImperfectField",
                    "getCrisp",
                    "()Ljava/lang/Object;");

            int stack2 = setTargetValue( mv, wrapperName, core.getName(), ifld, softField );

            if ( isLinguistic( ifld ) ) {
                updateSupportField( mv, ifld, wrapperName, lingTarget );
            }

            mv.visitInsn(RETURN);
            mv.visitMaxs( stack2 + 3, 3 );
            mv.visitEnd();
        }


        if ( softField ) {
            buildSoftSetter( cw,
                    ifld,
                    wrapperName,
                    BuildUtils.setterName( ifld.getName(), ifld.getTypeName() ) + "Core",
                    true);
        } else {
            buildHardSetter( cw,
                    ifld,
                    wrapperName,
                    getTrait(),
                    core,
                    BuildUtils.setterName( ifld.getName(), ifld.getTypeName() ) + "Core",
                    true);

        }



    }



    private void updateSupportField( MethodVisitor mv, ImperfectFieldDefinition ifld, String proxyName, FieldDefinition target  ) {

        mv.visitVarInsn(ALOAD, 0);

        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn( INVOKEVIRTUAL,
                BuildUtils.getInternalType( proxyName ),
                BuildUtils.getterName( ifld.getName(), ifld.getTypeName() ),
                "()Lorg/drools/chance/common/IImperfectField;" );
        mv.visitTypeInsn( CHECKCAST,
                "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField" );
        mv.visitMethodInsn(INVOKEVIRTUAL,
                "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField",
                "defuzzify",
                "()Ljava/lang/Number;");

        mv.visitTypeInsn(CHECKCAST, "java/lang/Double" );

        mv.visitMethodInsn( INVOKEVIRTUAL,
                "java/lang/Double",
                BuildUtils.numericMorph( target.getTypeName() ),
                "()" + BuildUtils.unBox( target.getTypeName() ) );


        if ( ! BuildUtils.isPrimitive( target.getTypeName() ) ) {
            mv.visitMethodInsn(INVOKESTATIC,
                    BuildUtils.getInternalType( target.getTypeName() ),
                    "valueOf",
                    "(" + BuildUtils.unBox( target.getTypeName() )+ ")" + BuildUtils.getTypeDescriptor( target.getTypeName() ) );

        }

        mv.visitInsn(ICONST_0);
        mv.visitMethodInsn( INVOKEVIRTUAL,
                BuildUtils.getInternalType( proxyName ),
                BuildUtils.setterName( target.getName(), target.getTypeName() ),
                "(" + BuildUtils.getTypeDescriptor( target.getTypeName() ) +  "Z)V");


    }


    protected int setTargetValue(MethodVisitor mv, String wrapperName, String coreName, FieldDefinition field, boolean isSoftField ) {
        if ( isSoftField ) {

            mv.visitMethodInsn(INVOKEVIRTUAL, BuildUtils.getInternalType(wrapperName), "property", "(Ljava/lang/String;Ljava/lang/Object;)Lorg/drools/core/util/TripleImpl;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/drools/core/util/TripleStore", "put", "(Lorg/drools/core/util/Triple;)Z");
            mv.visitInsn(POP);
            return 2;

        } else {
            mv.visitTypeInsn(CHECKCAST, BuildUtils.getInternalType( field.getTypeName() ) );

            mv.visitMethodInsn( INVOKEVIRTUAL,
                    BuildUtils.getInternalType( coreName ),
                    BuildUtils.setterName( field.getName(), field.getTypeName() ),
                    "(" + BuildUtils.getTypeDescriptor( field.getTypeName() ) + ")V" );
            return 0;
        }
    }


    protected void prepareSetTargetValue(MethodVisitor mv, String wrapperName, String coreName, FieldDefinition ifld, boolean softField) {
        if ( softField ) {
            mv.visitFieldInsn(GETFIELD,  BuildUtils.getInternalType( wrapperName ), "store", "Lorg/drools/core/util/TripleStore;");
            mv.visitVarInsn(ALOAD, 0);

            mv.visitLdcInsn( ifld.getName() );
        } else {
            mv.visitMethodInsn( INVOKEVIRTUAL,
                    BuildUtils.getInternalType( wrapperName ),
                    "getCore",
                    "()" + BuildUtils.getTypeDescriptor( coreName ) );
        }

    }





    protected int getTargetDistField( MethodVisitor mv, FieldDefinition field, String wrapperName, String coreName, boolean isSoftField ) {

        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, BuildUtils.getInternalType(wrapperName), "store", "Lorg/drools/core/util/TripleStore;");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitLdcInsn(field.getName() + "_$$Imp");
        //TODO : how can this work??
        mv.visitMethodInsn(INVOKEVIRTUAL, BuildUtils.getInternalType( wrapperName ), "propertyKey", "(Ljava/lang/String;)Lorg/drools/core/util/TripleImpl;");
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/drools/core/util/TripleStore", "get", "(Lorg/drools/core/util/Triple;)Lorg/drools/core/util/Triple;");
        mv.visitMethodInsn(INVOKEINTERFACE, "org/drools/core/util/Triple", "getValue", "()Ljava/lang/Object;");
        mv.visitTypeInsn(CHECKCAST, BuildUtils.getInternalType( IImperfectField.class.getName() ) );
        return 2;

    }


    protected int getTargetCrispValue( MethodVisitor mv, FieldDefinition field, String wrapperName, String coreName, boolean isSoftField ) {
        if ( isSoftField ) {

            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, BuildUtils.getInternalType( wrapperName ), "store", "Lorg/drools/core/util/TripleStore;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitLdcInsn( field.getName() );
            mv.visitMethodInsn(INVOKEVIRTUAL, BuildUtils.getInternalType( wrapperName ), "propertyKey", "(Ljava/lang/String;)Lorg/drools/core/util/TripleImpl;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/drools/core/util/TripleStore", "get", "(Lorg/drools/core/util/Triple;)Lorg/drools/core/util/Triple;");
            mv.visitMethodInsn(INVOKEINTERFACE, "org/drools/core/util/Triple", "getValue", "()Ljava/lang/Object;");
            mv.visitTypeInsn(CHECKCAST, BuildUtils.getInternalType( field.getTypeName() ) );
            return 2;

        } else {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn( INVOKEVIRTUAL,
                    BuildUtils.getInternalType( wrapperName ),
                    "getCore",
                    "()" + BuildUtils.getTypeDescriptor( coreName ) );
            mv.visitMethodInsn( INVOKEVIRTUAL,
                    BuildUtils.getInternalType( coreName ),
                    BuildUtils.getterName( field.getName(), field.getTypeName() ),
                    "()" + BuildUtils.getTypeDescriptor( field.getTypeName() ) );
            return 0;
        }
    }







}


