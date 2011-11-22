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

import org.drools.factmodel.BuildUtils;
import org.drools.factmodel.ClassDefinition;
import org.drools.factmodel.FieldDefinition;
import org.mvel2.asm.ClassWriter;
import org.mvel2.asm.FieldVisitor;
import org.mvel2.asm.MethodVisitor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;


@Deprecated
public class ChanceWrapperBuilder extends ChanceBuilder {



    public byte[] buildClass( ClassDefinition cdef ) {
        String wrapperName = cdef.getClassName() + "Imperfect";
        System.out.println( wrapperName );
        System.out.println( cdef );


        String coreName = cdef.getClassName();
        Class coreKlazz = cdef.getDefinedClass();

        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;

        tabooMethods = new HashSet<String>();


        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER,
                 BuildUtils.getInternalType(wrapperName),
                 null,
                 BuildUtils.getInternalType(coreName),
                 null);

        {
            fv = cw.visitField(ACC_PRIVATE, "core", BuildUtils.getTypeDescriptor(coreName), null, null);
            fv.visitEnd();
        }

        buildImperfectFields( cw, cdef );


        {
            boolean hasFuzzy = false;
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "("+ BuildUtils.getTypeDescriptor(coreName) +")V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, BuildUtils.getInternalType( coreName ) , "<init>", "()V");


            for ( FieldDefinition fld : cdef.getFieldsDefinitions() ) {
                if ( fld instanceof ImperfectFieldDefinition ) {
                    ImperfectFieldDefinition ifld = (ImperfectFieldDefinition) fld;

                    if ( isLinguistic( ifld ) ) {

                        FieldDefinition tfld = getSupportField( cdef, ifld );
                        buildImperfectLinguisticField(mv, wrapperName, ifld, tfld );

                        buildImperfectLinguisticFieldGettersAndSetters(cw, wrapperName, coreName, ifld, tfld);

                        hasFuzzy = true;
                    } else {
                        buildImperfectField( mv, wrapperName, ifld );

                        buildImperfectFieldGettersAndSetters( cw, wrapperName, coreName, ifld );
                    }
                }
            }

            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn( PUTFIELD,
                               BuildUtils.getInternalType(wrapperName),
                               "core",
                               BuildUtils.getTypeDescriptor(coreName) );
            mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKESPECIAL, BuildUtils.getInternalType(wrapperName), "synchFields", "()V");

            mv.visitInsn(RETURN);
            mv.visitMaxs( hasFuzzy ? 9 : 7,
                          2);
            mv.visitEnd();
        }


        Method[] ms = coreKlazz.getMethods();
        for ( Method method : ms ) {
            if ( Modifier.isFinal(method.getModifiers()) ) {
                continue;
            }

            if ( tabooMethods.contains( method.getName() ) ) {
                System.err.println( "Skipped method " + method );
                continue;
            }

            String signature = buildSignature( method );
            {
            mv = cw.visitMethod( ACC_PUBLIC,
                                 method.getName(),
                                 signature,
                                 null,
                                 null );
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitFieldInsn( GETFIELD, BuildUtils.getInternalType(wrapperName), "core", BuildUtils.getTypeDescriptor(coreName) );
            int j = 1;
            for ( Class arg : method.getParameterTypes() ) {
                mv.visitVarInsn( BuildUtils.varType(arg.getName()), j++ );
            }
            mv.visitMethodInsn( INVOKEVIRTUAL,
                                BuildUtils.getInternalType(coreName),
                                method.getName(),
                                signature );

            mv.visitInsn( BuildUtils.returnType(method.getReturnType().getName()) );
            int stack = getStackSize( method ) ;
            mv.visitMaxs(stack, stack);
            mv.visitEnd();
            }
        }


        buildSynchFields( cw, wrapperName, coreName, cdef );


        cw.visitEnd();

        return cw.toByteArray();
    }


      protected void getTargetValue( MethodVisitor mv, FieldDefinition field, String wrapperName, String coreName ) {
//    protected void getTargetValue(MethodVisitor mv, String wrapperName, String typeName, String coreName, String getter) {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn( GETFIELD,
                BuildUtils.getInternalType( wrapperName ),
                "core",
                BuildUtils.getTypeDescriptor( coreName ));
        mv.visitMethodInsn( INVOKEVIRTUAL,
                BuildUtils.getInternalType( coreName ),
                BuildUtils.getterName( field.getName(), field.getTypeName() ),
                "()" + BuildUtils.getTypeDescriptor( field.getTypeName() ) + "");
    }

    protected  void prepareSetTargetValue(MethodVisitor mv, String wrapperName, String coreName) {
        mv.visitFieldInsn( GETFIELD,
                BuildUtils.getInternalType( wrapperName ),
                "core",
                BuildUtils.getTypeDescriptor( coreName ) );
    }

    protected  void setTargetValue( MethodVisitor mv, String coreName, FieldDefinition field ) {
        mv.visitMethodInsn( INVOKEVIRTUAL,
                BuildUtils.getInternalType( coreName ),
                BuildUtils.setterName( field.getName(), field.getTypeName() ), "" +
                "(" + BuildUtils.getTypeDescriptor( field.getTypeName() ) + ")V" );
    }














}
