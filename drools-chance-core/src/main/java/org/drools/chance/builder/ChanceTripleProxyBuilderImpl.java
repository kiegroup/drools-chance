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
import org.drools.factmodel.traits.TraitRegistry;
import org.drools.factmodel.traits.TraitTripleProxyClassBuilderImpl;
import org.mvel2.asm.*;

import java.lang.reflect.Method;
import java.util.*;


public class ChanceTripleProxyBuilderImpl extends TraitTripleProxyClassBuilderImpl {



    protected void buildConstructorCore( ClassWriter cw, MethodVisitor mv, String internalProxy, String internalWrapper, String internalCore, String descrCore, String mixin, Class mixinClass ) {
        super.buildConstructorCore( cw, mv, internalProxy, internalWrapper, internalCore, descrCore, mixin, mixinClass );

        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, internalProxy, "synchFields", "()V");

    }




    protected void buildProxyAccessor(long mask, ClassWriter cw, String masterName, ClassDefinition core, Map<String, Method> mixinGetSet, FieldDefinition field, boolean isSoftField ) {
        if ( field instanceof VirtualFieldDefinition ) return;
        if ( ! ( field instanceof ImperfectFieldDefinition ) ) {

            if ( field instanceof DirectAccessFieldDefinition ) {
                DirectAccessFieldDefinition dfld = (DirectAccessFieldDefinition) field;
                int j = 0;
                for ( FieldDefinition x : getTrait().getFieldsDefinitions() ) {
                    if ( x.equals( dfld.getTarget() ) ) {
                        break;
                    }
                    j++;
                }
                buildDirectProxyAccessor( mask, cw, masterName, core, mixinGetSet, dfld, TraitRegistry.isSoftField( dfld.getTarget(), j, mask) );
            } else if ( ! isSupport( field ) ) {
                super.buildProxyAccessor( mask, cw, masterName, core, mixinGetSet, field, isSoftField );
            }

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

            if ( ImperfectFieldDefinition.isLinguistic( field ) ) {
                ImperfectFieldDefinition ifld = (ImperfectFieldDefinition) field;
                int j = 0;
                for ( FieldDefinition testField : getTrait().getFieldsDefinitions() ) {
                    if ( testField.equals( ifld.getSupportFieldDef() ) ) {
                        break;
                    } else {
                        j++;
                    }
                }
                buildSupportFieldAccessors( mask, cw, masterName, core, mixinGetSet,
                        ifld.getSupportFieldDef(),
                        TraitRegistry.isSoftField( ifld, j , mask ) );
            }
        }
    }



    private void buildSupportFieldAccessors( long mask, ClassWriter cw, String masterName, ClassDefinition core, Map<String, Method> mixinGetSet, FieldDefinition supportField, boolean isSoftField ) {

        ImperfectFieldDefinition lingField = findSupportingField( getTrait(), supportField );
        MethodVisitor mv;

        {
            mv = cw.visitMethod( ACC_PUBLIC,
                    BuildUtils.getterName( supportField.getName(), supportField.getTypeName() ),
                    "()" + BuildUtils.getTypeDescriptor( supportField.getTypeName() ),
                    null,
                    null);
            mv.visitCode();


            mv.visitVarInsn( ALOAD, 0 );
            mv.visitMethodInsn( INVOKEVIRTUAL,
                    BuildUtils.getInternalType( masterName ),
                    BuildUtils.getterName( supportField.getName(), supportField.getTypeName() ) + "Core",
                    "()" + BuildUtils.getTypeDescriptor( supportField.getTypeName() ) );
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }


        {
            mv = cw.visitMethod( ACC_PUBLIC,
                    BuildUtils.setterName( supportField.getName(), supportField.getTypeName() ),
                    "(" + BuildUtils.getTypeDescriptor( supportField.getTypeName() )+ ")V",
                    null,
                    null);
            mv.visitCode();

            mv.visitVarInsn( ALOAD, 0 );
            mv.visitMethodInsn( INVOKEVIRTUAL,
                    BuildUtils.getInternalType( masterName ),
                    BuildUtils.getterName( lingField.getName(), lingField.getTypeName() ),
                    "()Lorg/drools/chance/common/IImperfectField;" );
            mv.visitTypeInsn( CHECKCAST,
                    "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField" );
            mv.visitVarInsn( ASTORE, 2 );

            mv.visitVarInsn( ALOAD, 2 );
            mv.visitVarInsn( ALOAD, 2 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitMethodInsn( INVOKEVIRTUAL,
                    "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField",
                    "fuzzify",
                    "(Ljava/lang/Number;)Lorg/drools/chance/distribution/IDistribution;" );
            mv.visitInsn( ICONST_0 );
            mv.visitMethodInsn( INVOKEVIRTUAL,
                    "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField",
                    "setValue",
                    "(Lorg/drools/chance/distribution/IDistribution;Z)V" );

            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 2 );
            mv.visitMethodInsn( INVOKEVIRTUAL,
                    "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField",
                    "getCrisp",
                    "()Lorg/drools/chance/distribution/fuzzy/linguistic/ILinguistic;" );
            mv.visitTypeInsn( CHECKCAST,
                    BuildUtils.getInternalType( lingField.getTypeName() ) );
            mv.visitMethodInsn( INVOKEVIRTUAL,
                    BuildUtils.getInternalType( masterName ),
                    BuildUtils.setterName( lingField.getName(), lingField.getTypeName() ) + "Core",
                    "(" + BuildUtils.getTypeDescriptor(lingField.getTypeName()) + ")V" );
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitVarInsn( ALOAD, 1 );
            mv.visitMethodInsn( INVOKEVIRTUAL,
                    BuildUtils.getInternalType( masterName ),
                    BuildUtils.setterName(supportField.getName(), supportField.getTypeName()) + "Core",
                    "(" + BuildUtils.getTypeDescriptor( supportField.getTypeName() )+ ")V" );

            mv.visitInsn( RETURN );
            mv.visitMaxs( 3, 3 );
            mv.visitEnd();
        }

        if ( ! isSoftField ) {
            FieldVisitor fv;
            {
                fv = cw.visitField(ACC_PUBLIC + ACC_STATIC, supportField.getName()+"_reader", "Lorg/drools/spi/InternalReadAccessor;", null, null);
                fv.visitEnd();
            }
            {
                fv = cw.visitField(ACC_PUBLIC + ACC_STATIC, supportField.getName()+"_writer", "Lorg/drools/spi/WriteAccessor;", null, null);
                fv.visitEnd();
            }

        }

    }


    protected void buildDirectProxyAccessor( long mask, ClassWriter cw, String masterName, ClassDefinition core, Map<String,Method> mixinGetSet, DirectAccessFieldDefinition field, boolean isSoftField ) {
        FieldVisitor fv;
        String getterName = BuildUtils.getterName( field.getName(), field.getTypeName() );
        String setterName = BuildUtils.setterName( field.getName(), field.getTypeName() );


        if ( isSoftField ) {
            if ( ! mixinGetSet.containsKey( BuildUtils.getterName( field.getName(), field.getTypeName() ) ) ) {
                buildSoftGetter( cw, field.getTarget(), masterName, getterName, false );
                buildSoftSetter( cw, field.getTarget(), masterName, setterName, false );
            } else {
                //
            }

        } else {
            {
                fv = cw.visitField(ACC_PUBLIC + ACC_STATIC, field.getName()+"_reader", "Lorg/drools/spi/InternalReadAccessor;", null, null);
                fv.visitEnd();
            }
            {
                fv = cw.visitField(ACC_PUBLIC + ACC_STATIC, field.getName()+"_writer", "Lorg/drools/spi/WriteAccessor;", null, null);
                fv.visitEnd();
            }

            buildHardGetter( cw, field.getTarget(), masterName, getTrait(), core, getterName, false );
            buildHardSetter( cw, field.getTarget(), masterName, getTrait(), core, setterName, false );

        }
    }






    /*******************************************************************************************************************
     *
     * Synch
     *
     *******************************************************************************************************************/





    protected void buildExtendedMethods(ClassWriter cw, ClassDefinition trait, ClassDefinition core ) {
        buildSynchFields( cw, TraitFactory.getProxyName(trait, core), core.getName(), getTrait() );
    }



    protected void buildSynchFields( ClassWriter cw, String proxyName, String coreName, ClassDefinition def ) {
        {
            MethodVisitor mv = cw.visitMethod(ACC_PRIVATE, "synchFields", "()V", null, null);
            mv.visitCode();

            boolean hasLinguistic = false;
            for ( FieldDefinition fld : def.getFieldsDefinitions() ) {

                if ( fld instanceof VirtualFieldDefinition ) continue;

                if ( fld instanceof ImperfectFieldDefinition ) {
                    ImperfectFieldDefinition ifld = (ImperfectFieldDefinition) fld;
                    if ( ImperfectFieldDefinition.isLinguistic(ifld) ) {

                        FieldDefinition tfld = ifld.getSupportFieldDef();
                        System.out.println("Synch ling " + ifld.getName());
                        synchLinguisticField(mv, ifld, tfld, proxyName, coreName);

                        hasLinguistic = true;



                    } else {
                        System.out.println("Synch " + ifld.getName());
                        synchField(mv, ifld, proxyName);
                    }
                }

            }

            mv.visitInsn( RETURN );
            mv.visitMaxs( 3, hasLinguistic ? 4 : 3 );
            mv.visitEnd();

        }

    }



    private void synchField( MethodVisitor mv, ImperfectFieldDefinition ifld, String proxyName ) {
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


    protected void synchLinguisticField( MethodVisitor mv, FieldDefinition fld, FieldDefinition tfld, String proxyName, String coreName ) {
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitMethodInsn( INVOKEVIRTUAL,
                BuildUtils.getInternalType( proxyName ),
                BuildUtils.getterName( fld.getName(), fld.getTypeName() ),
                "()Lorg/drools/chance/common/IImperfectField;");
        mv.visitTypeInsn( CHECKCAST, "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField" );
        mv.visitVarInsn( ASTORE, 1 );
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitMethodInsn( INVOKEVIRTUAL,
                BuildUtils.getInternalType( proxyName ),
                BuildUtils.getterName( tfld.getName(), tfld.getTypeName() ),
                "()" + BuildUtils.getTypeDescriptor( tfld.getTypeName() ) );
        mv.visitVarInsn( ASTORE, 2 );
        mv.visitVarInsn( ALOAD, 2 );
        Label l0 = new Label();
        mv.visitJumpInsn( IFNULL, l0 );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitVarInsn( ALOAD, 2 );
        mv.visitMethodInsn( INVOKEVIRTUAL,
                "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField",
                "fuzzify",
                "(Ljava/lang/Number;)Lorg/drools/chance/distribution/IDistribution;");
        mv.visitVarInsn( ASTORE, 3 );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitVarInsn( ALOAD, 3 );
        mv.visitInsn( ICONST_0 );
        mv.visitMethodInsn( INVOKEVIRTUAL,
                "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField",
                "setValue",
                "(Lorg/drools/chance/distribution/IDistribution;Z)V" );
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitMethodInsn( INVOKEVIRTUAL,
                "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField",
                "getCrisp",
                "()Lorg/drools/chance/distribution/fuzzy/linguistic/ILinguistic;" );
        mv.visitTypeInsn( CHECKCAST,
                BuildUtils.getInternalType( fld.getTypeName() ) );
        mv.visitMethodInsn( INVOKEVIRTUAL,
                BuildUtils.getInternalType( proxyName ),
                BuildUtils.setterName( fld.getName(), fld.getTypeName() ) + "Core",
                "(" + BuildUtils.getTypeDescriptor( fld.getTypeName() ) + ")V" );
        Label l1 = new Label();
        mv.visitJumpInsn( GOTO, l1 );
        mv.visitLabel( l0 );
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitMethodInsn( INVOKEVIRTUAL,
                BuildUtils.getInternalType( proxyName ),
                BuildUtils.getterName( fld.getName(), fld.getTypeName() ) + "Value",
                "()" + BuildUtils.getTypeDescriptor( fld.getTypeName() ) + "" );
        mv.visitVarInsn( ASTORE, 3 );
        mv.visitVarInsn( ALOAD, 3 );
        Label l2 = new Label();
        mv.visitJumpInsn( IFNULL, l2 );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitVarInsn( ALOAD, 3 );
        mv.visitMethodInsn( INVOKEVIRTUAL,
                "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField",
                "setValue",
                "(Lorg/drools/chance/distribution/fuzzy/linguistic/ILinguistic;)V" );
        mv.visitLabel( l2 );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitJumpInsn( IFNULL, l1 );
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitMethodInsn( INVOKEVIRTUAL,
                "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField",
                "getCrisp",
                "()Lorg/drools/chance/distribution/fuzzy/linguistic/ILinguistic;" );
        mv.visitTypeInsn( CHECKCAST,
                BuildUtils.getInternalType( fld.getTypeName() ) );
        mv.visitMethodInsn( INVOKEVIRTUAL,
                BuildUtils.getInternalType( proxyName ),
                BuildUtils.setterName( fld.getName(), fld.getTypeName() ) + "Core",
                "(" + BuildUtils.getTypeDescriptor( fld.getTypeName() ) + ")V" );
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitVarInsn( ALOAD, 1 );

        defuzzifyOnTargetField( mv, proxyName, tfld );

        mv.visitLabel( l1 );
    }





    /*******************************************************************************************************************
     *
     * Accessors
     *
     *******************************************************************************************************************/




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
        if ( ImperfectFieldDefinition.isLinguistic(ifld) ) {
            lingTarget = ifld.getSupportFieldDef();
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


            if ( ImperfectFieldDefinition.isLinguistic(ifld) ) {
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


            if ( ImperfectFieldDefinition.isLinguistic(ifld) ) {
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

            if ( ImperfectFieldDefinition.isLinguistic(ifld) ) {
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

            if ( ImperfectFieldDefinition.isLinguistic(ifld) ) {
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

            if ( ImperfectFieldDefinition.isLinguistic(ifld) ) {
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

            if ( ImperfectFieldDefinition.isLinguistic(ifld) ) {
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




    /*******************************************************************************************************************
     *
     * Utilities
     *
     *******************************************************************************************************************/




    protected ImperfectFieldDefinition findSupportingField( ClassDefinition cdef, FieldDefinition ifld ) {
        for ( FieldDefinition fld : getTrait().getFieldsDefinitions() ) {
            if ( fld instanceof ImperfectFieldDefinition &&
                    ImperfectFieldDefinition.isLinguistic( fld ) &&
                    ((ImperfectFieldDefinition) fld).getSupportFieldDef().equals( ifld ) ) {
                return (ImperfectFieldDefinition) fld;
            }
        }
        return null;
    }


    private boolean isSupport( FieldDefinition field ) {
        return findSupportingField( getTrait(), field) != null;
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

        defuzzifyOnTargetField( mv, proxyName, target );

    }


    protected void defuzzifyOnTargetField(  MethodVisitor mv, String proxyName, FieldDefinition target ) {
        mv.visitMethodInsn(INVOKEVIRTUAL,
                "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField",
                "defuzzify",
                "()Ljava/lang/Number;");

        mv.visitTypeInsn(CHECKCAST, "java/lang/Number" );

        mv.visitMethodInsn( INVOKEVIRTUAL,
                "java/lang/Number",
                BuildUtils.numericMorph( target.getTypeName() ),
                "()" + BuildUtils.unBox( target.getTypeName() ) );


        if ( ! BuildUtils.isPrimitive( target.getTypeName() ) ) {
            mv.visitMethodInsn(INVOKESTATIC,
                    BuildUtils.getInternalType( target.getTypeName() ),
                    "valueOf",
                    "(" + BuildUtils.unBox( target.getTypeName() )+ ")" + BuildUtils.getTypeDescriptor( target.getTypeName() ) );

        }

        mv.visitMethodInsn( INVOKEVIRTUAL,
                BuildUtils.getInternalType( proxyName ),
                BuildUtils.setterName( target.getName(), target.getTypeName() ) + "Core",
                "(" + BuildUtils.getTypeDescriptor( target.getTypeName() ) +  ")V");
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


