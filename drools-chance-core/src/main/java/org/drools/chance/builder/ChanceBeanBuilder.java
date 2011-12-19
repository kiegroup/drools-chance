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

import org.drools.chance.common.ImperfectField;
import org.drools.chance.distribution.IDistribution;
import org.drools.factmodel.AnnotationDefinition;
import org.drools.factmodel.BuildUtils;
import org.drools.factmodel.ClassDefinition;
import org.drools.factmodel.FieldDefinition;
import org.mvel2.asm.*;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class ChanceBeanBuilder extends ChanceBuilder {




    public byte[] buildClass( ClassDefinition classDef ) throws IntrospectionException,
            InvocationTargetException,
            ClassNotFoundException,
            IOException,
            NoSuchMethodException,
            NoSuchFieldException,
            InstantiationException,
            IllegalAccessException {

        try {
            tabooMethods = new HashSet<String>();

            if ( classDef.getFieldsDefinitions().size() > 0 ) {
                rewriteImperfectFields( classDef );
            }


            byte[] code = super.buildClass(classDef);

            finalizeCreation( classDef );

            return code;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }




    protected void finalizeCreation(ClassDefinition klass) {

        Collection<FieldDefinition> originalFields = new HashSet<FieldDefinition>( klass.getFieldsDefinitions() );

        for ( FieldDefinition field : originalFields ) {
            if ( field instanceof ImperfectFieldDefinition ) {
                FieldDefinition fieldDistr = new VirtualFieldDefinition();
                    fieldDistr.setName( field.getName() + "Distr" );
                    fieldDistr.setTypeName( IDistribution.class.getName() );
                    fieldDistr.setInherited( field.isInherited() );

                klass.addField(fieldDistr);


                FieldDefinition fieldValue = new VirtualFieldDefinition();
                    fieldValue.setName( field.getName() + "Value" );
                    fieldValue.setTypeName( field.getTypeName() );
                    fieldValue.setInherited( field.isInherited() );

                klass.addField(fieldValue);
            }
        }



    }



    protected void rewriteImperfectFields(ClassDefinition classDef) {
        Collection<FieldDefinition> originalFields = new ArrayList<FieldDefinition>( classDef.getFieldsDefinitions() );

        for ( FieldDefinition fld : originalFields ) {
            if ( fld.getAnnotations() != null ) {
                for ( AnnotationDefinition ann : fld.getAnnotations() ) {
                    if ( ann.getName().equals( Imperfect.class.getName() ) ) {
                        ImperfectFieldDefinition ifld = ImperfectFieldDefinition.fromField( fld, ann );
                        classDef.addField( ifld );
                        break;
                    }
                }
            }
        }
    }


    @Override
    protected void buildFields(ClassWriter cw, ClassDefinition classDef) {

        super.buildFields( cw, classDef );

        buildImperfectFields( cw, classDef );

        buildSynchFields( cw, classDef.getName(), classDef.getName(), classDef );
    }


    @Override
    protected void buildConstructorWithFields(ClassVisitor cw, ClassDefinition classDef, Collection<FieldDefinition> fieldDefs) {

        Type[] params = new Type[fieldDefs.size()];
        int index = 0;
        for ( FieldDefinition field : fieldDefs ) {
            if ( field instanceof  VirtualFieldDefinition ) continue;
            params[index++] = Type.getType( BuildUtils.getTypeDescriptor(field.getTypeName()) );
        }

        MethodVisitor mv = cw.visitMethod( Opcodes.ACC_PUBLIC,
                "<init>",
                Type.getMethodDescriptor( Type.VOID_TYPE,
                        params ),
                null,
                null );
        mv.visitCode();
        Label l0 = null;
        if ( this.debug ) {
            l0 = new Label();
            mv.visitLabel( l0 );
        }

        fieldConstructorStart( mv, classDef, fieldDefs );
        initImperfectFields( classDef, mv );

        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, BuildUtils.getInternalType( classDef.getName() ), "synchFields", "()V");



        mv.visitInsn( Opcodes.RETURN );
        Label l1 = null;
        if ( this.debug ) {
            l1 = new Label();
            mv.visitLabel( l1 );
            mv.visitLocalVariable( "this",
                    BuildUtils.getTypeDescriptor( classDef.getClassName() ),
                    null,
                    l0,
                    l1,
                    0 );
            for ( FieldDefinition field : classDef.getFieldsDefinitions() ) {
                if ( field instanceof  VirtualFieldDefinition ) continue;

                Label l11 = new Label();
                mv.visitLabel( l11 );
                mv.visitLocalVariable( field.getName(),
                        BuildUtils.getTypeDescriptor( field.getTypeName() ),
                        null,
                        l0,
                        l1,
                        0 );
            }
        }
        mv.visitMaxs( 0,
                0 );
        mv.visitEnd();



    }

    @Override
    protected void buildDefaultConstructor(ClassVisitor cw, ClassDefinition classDef) {

        MethodVisitor mv = cw.visitMethod( Opcodes.ACC_PUBLIC,
                "<init>",
                Type.getMethodDescriptor( Type.VOID_TYPE,
                        new Type[]{} ),
                null,
                null );
        mv.visitCode();

        Label l0 = null;
            if ( this.debug ) {
                l0 = new Label();
                mv.visitLabel( l0 );
            }

        boolean hasObjects = defaultConstructorStart( mv, classDef );

        initImperfectFields( classDef, mv );

        defaultConstructorInitValues( mv, classDef );


        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, BuildUtils.getInternalType( classDef.getName() ), "synchFields", "()V");

        mv.visitInsn(Opcodes.RETURN);
        Label l1 = null;
        if ( this.debug ) {
            l1 = new Label();
            mv.visitLabel( l1 );
            mv.visitLocalVariable( "this",
                    BuildUtils.getTypeDescriptor( classDef.getClassName() ),
                    null,
                    l0,
                    l1,
                    0 );
        }
        mv.visitMaxs( hasObjects ? 3 : 0,
                hasObjects ? 1 : 0 );
        mv.visitEnd();


    }


    @Override
    protected void buildGettersAndSetters(ClassWriter cw, ClassDefinition classDef) {
        for ( FieldDefinition fld : classDef.getFieldsDefinitions() ) {
            if ( fld instanceof  VirtualFieldDefinition ) continue;
            if ( ! fld.isInherited() ) {
                if ( fld instanceof ImperfectFieldDefinition ) {
                    ImperfectFieldDefinition ifld = (ImperfectFieldDefinition) fld;

                    if ( isLinguistic( ifld ) ) {
                        FieldDefinition tfld = getSupportField( classDef, ifld );
                        buildImperfectLinguisticFieldGettersAndSetters(cw, classDef.getName(), classDef.getName(), ifld, tfld);
                    } else {
                        buildImperfectFieldGettersAndSetters( cw, classDef.getName(), classDef.getName(), ifld );
                    }

                } else {
                    if ( ! tabooMethods.contains( BuildUtils.getterName( fld.getName(), fld.getTypeName() ) ) ) {
                        this.buildGetMethod( cw,
                            classDef,
                            fld );
                    }
                    if ( ! tabooMethods.contains( BuildUtils.setterName( fld.getName(), fld.getTypeName() ) ) ) {
                        this.buildSetMethod( cw,
                             classDef,
                                fld );
                    }
                }
            }
        }
    }

    protected void setTargetValue(MethodVisitor mv, String coreName, FieldDefinition field ) {
        mv.visitFieldInsn( PUTFIELD,
                BuildUtils.getInternalType( coreName ),
                field.getName(),
                BuildUtils.getTypeDescriptor( field.getTypeName() ) );
    }


    protected void prepareSetTargetValue(MethodVisitor mv, String wrapperName, String coreName) {

    }

    protected void getTargetValue( MethodVisitor mv, FieldDefinition field, String wrapperName, String coreName ) {
//    protected void getTargetValue( MethodVisitor mv, String wrapperName, String typeName, String coreName, String getter ) {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn( GETFIELD,
                BuildUtils.getInternalType( coreName ),
                field.getName(),
                BuildUtils.getTypeDescriptor( field.getTypeName() ) );
    }

}
