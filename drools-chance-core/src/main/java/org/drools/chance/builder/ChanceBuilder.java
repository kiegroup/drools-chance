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
import org.drools.chance.common.ImperfectField;
import org.drools.factmodel.*;
import org.mvel2.asm.*;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public abstract  class ChanceBuilder extends DefaultBeanClassBuilder {


    protected HashSet<String> tabooMethods;












    protected void buildSynchFields( ClassWriter cw, String wrapperName, String coreName, ClassDefinition def ) {
        {
            MethodVisitor mv = cw.visitMethod(ACC_PRIVATE, "synchFields", "()V", null, null);
            mv.visitCode();

            for ( FieldDefinition fld : def.getFieldsDefinitions() ) {
                if ( fld instanceof ImperfectFieldDefinition ) {
                    ImperfectFieldDefinition ifld = (ImperfectFieldDefinition) fld;
                    if ( isLinguistic( ifld ) ) {

                        FieldDefinition tfld = getSupportField( def, ifld );
                        String getter = BuildUtils.getterName( ifld.getName(), ifld.getTypeName() );
                        String setter = BuildUtils.setterName( ifld.getName(), ifld.getTypeName() );

                        String targetGetter = BuildUtils.getterName( tfld.getName(), tfld.getTypeName() );
                        String targetSetter = BuildUtils.setterName( tfld.getName(), tfld.getTypeName() );

//                        mv.visitVarInsn(ALOAD, 0);
//                        mv.visitFieldInsn( GETFIELD,
//                                           BuildUtils.getInternalType( wrapperName ),
//                                           "core",
//                                           BuildUtils.getTypeDescriptor( coreName ));
//                        mv.visitMethodInsn( INVOKEVIRTUAL,
//                                            BuildUtils.getInternalType( coreName ),
//                                            targetGetter,
//                                            "()" + BuildUtils.getTypeDescriptor( tfld.getTypeName() ) +"");
                        getTargetValue( mv, ifld, wrapperName, coreName );

                        Label l4 = new Label();
                        mv.visitJumpInsn(IFNULL, l4);
//                        mv.visitVarInsn(ALOAD, 0);
//                        mv.visitFieldInsn( GETFIELD,
//                                           BuildUtils.getInternalType( wrapperName ),
//                                           "core",
//                                           BuildUtils.getTypeDescriptor( coreName ));
//                        mv.visitMethodInsn( INVOKEVIRTUAL,
//                                            BuildUtils.getInternalType( coreName ),
//                                            targetGetter,
//                                            "()" + BuildUtils.getTypeDescriptor( tfld.getTypeName() ) +"");

                        getTargetValue( mv, tfld, wrapperName, coreName );

                        mv.visitVarInsn(ASTORE, 1);
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitFieldInsn( GETFIELD,
                                           BuildUtils.getInternalType( wrapperName ),
                                           ifld.getName() + "_Dist",
                                           "Lorg/drools/chance/common/IImperfectField;");
                        mv.visitTypeInsn(CHECKCAST, "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField");
                        mv.visitVarInsn(ALOAD, 1);
                        mv.visitMethodInsn( INVOKEVIRTUAL,
                                            "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField",
                                            "fuzzify",
                                            "(Ljava/lang/Number;)Lorg/drools/chance/distribution/IDistribution;");
                        mv.visitVarInsn(ASTORE, 2);
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitFieldInsn( GETFIELD,
                                           BuildUtils.getInternalType( wrapperName ),
                                           ifld.getName() + "_Dist",
                                           "Lorg/drools/chance/common/IImperfectField;");
                        mv.visitVarInsn(ALOAD, 2);
                        mv.visitInsn(ICONST_0);
                        mv.visitMethodInsn( INVOKEINTERFACE,
                                            "org/drools/chance/common/IImperfectField",
                                            "setValue",
                                            "(Lorg/drools/chance/distribution/IDistribution;Z)V");
                        mv.visitVarInsn(ALOAD, 0);
//                        mv.visitFieldInsn( GETFIELD,
//                                           BuildUtils.getInternalType( wrapperName ),
//                                           "core",
//                                           BuildUtils.getTypeDescriptor( coreName ));
                        prepareSetTargetValue( mv, wrapperName, coreName );
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitFieldInsn( GETFIELD,
                                           BuildUtils.getInternalType( wrapperName ),
                                           ifld.getName() + "_Dist",
                                           "Lorg/drools/chance/common/IImperfectField;");
                        mv.visitMethodInsn( INVOKEINTERFACE,
                                            "org/drools/chance/common/IImperfectField",
                                            "getCrisp",
                                            "()Ljava/lang/Object;");
                        mv.visitTypeInsn(CHECKCAST, BuildUtils.getInternalType( ifld.getTypeName() ));
//                        mv.visitMethodInsn( INVOKEVIRTUAL,
//                                            BuildUtils.getInternalType( coreName ),
//                                            setter,
//                                            "(" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) + ")V");
//                        setTargetValue( mv, coreName, setter, ifld.getTypeName() );
                        setTargetValue( mv, coreName, ifld );
                        mv.visitVarInsn(ALOAD, 0);
//                        mv.visitFieldInsn( GETFIELD,
//                                           BuildUtils.getInternalType( wrapperName ),
//                                           "core",
//                                           BuildUtils.getTypeDescriptor( coreName ));
                        prepareSetTargetValue( mv, wrapperName, coreName );

                        mv.visitVarInsn(ALOAD, 1);
//                        mv.visitMethodInsn( INVOKEVIRTUAL,
//                                            BuildUtils.getInternalType( coreName ),
//                                            targetSetter,
//                                            "(" + BuildUtils.getTypeDescriptor( tfld.getTypeName() ) +")V");

                        setTargetValue( mv, coreName, tfld );

                        Label l5 = new Label();
                        mv.visitJumpInsn(GOTO, l5);
                        mv.visitLabel(l4);
//                        mv.visitVarInsn(ALOAD, 0);
//                        mv.visitFieldInsn( GETFIELD,
//                                           BuildUtils.getInternalType( wrapperName ),
//                                           "core",
//                                           BuildUtils.getTypeDescriptor( coreName ));
//                        mv.visitMethodInsn( INVOKEVIRTUAL,
//                                            BuildUtils.getInternalType( coreName ),
//                                            getter,
//                                            "()" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) + "");

                        getTargetValue( mv, ifld, wrapperName, coreName );

                        Label l6 = new Label();
                        mv.visitJumpInsn(IFNULL, l6);
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitFieldInsn( GETFIELD,
                                           BuildUtils.getInternalType( wrapperName ),
                                           ifld.getName() + "_Dist",
                                           "Lorg/drools/chance/common/IImperfectField;");

//                        mv.visitVarInsn(ALOAD, 0);
//                        mv.visitFieldInsn( GETFIELD,
//                                           BuildUtils.getInternalType( wrapperName ),
//                                           "core",
//                                           BuildUtils.getTypeDescriptor( coreName ));
//                        mv.visitMethodInsn( INVOKEVIRTUAL,
//                                            BuildUtils.getInternalType( coreName ),
//                                            getter,
//                                            "()" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) + "");
                        getTargetValue( mv, ifld, wrapperName, coreName );

                        mv.visitMethodInsn( INVOKEINTERFACE,
                                            "org/drools/chance/common/IImperfectField",
                                            "setValue",
                                            "(Ljava/lang/Object;)V");
                        mv.visitLabel(l6);
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitFieldInsn( GETFIELD,
                                           BuildUtils.getInternalType( wrapperName ),
                                           ifld.getName() + "_Dist",
                                           "Lorg/drools/chance/common/IImperfectField;");
                        mv.visitJumpInsn(IFNULL, l5);
                        mv.visitVarInsn(ALOAD, 0);
//                        mv.visitFieldInsn( GETFIELD,
//                                           BuildUtils.getInternalType( wrapperName ),
//                                           "core",
//                                           BuildUtils.getTypeDescriptor( coreName ));
                        prepareSetTargetValue( mv, wrapperName, coreName );

                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitFieldInsn( GETFIELD,
                                           BuildUtils.getInternalType( wrapperName ),
                                           ifld.getName() + "_Dist",
                                           "Lorg/drools/chance/common/IImperfectField;");
                        mv.visitMethodInsn( INVOKEINTERFACE,
                                            "org/drools/chance/common/IImperfectField",
                                            "getCrisp",
                                            "()Ljava/lang/Object;");
                        mv.visitTypeInsn(  CHECKCAST, BuildUtils.getInternalType( ifld.getTypeName() ));
//                        mv.visitMethodInsn( INVOKEVIRTUAL,
//                                            BuildUtils.getInternalType( coreName ),
//                                            setter,
//                                            "(" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) + ")V");
                       setTargetValue( mv, coreName, ifld );

                        mv.visitVarInsn(ALOAD, 0);
//                        mv.visitFieldInsn( GETFIELD,
//                                           BuildUtils.getInternalType( wrapperName ),
//                                           "core",
//                                            BuildUtils.getTypeDescriptor( coreName ));
                        prepareSetTargetValue( mv, wrapperName, coreName );

                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitFieldInsn( GETFIELD,
                                           BuildUtils.getInternalType( wrapperName ),
                                           ifld.getName() + "_Dist",
                                           "Lorg/drools/chance/common/IImperfectField;");
                        mv.visitTypeInsn(CHECKCAST, "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField");
                        mv.visitMethodInsn( INVOKEVIRTUAL,
                                            "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField",
                                            "defuzzify",
                                            "()Ljava/lang/Number;");
                        mv.visitTypeInsn(CHECKCAST, BuildUtils.getInternalType( tfld.getTypeName() ) );
//                        mv.visitMethodInsn( INVOKEVIRTUAL,
//                                            BuildUtils.getInternalType( coreName ),
//                                            targetSetter,
//                                            "(" + BuildUtils.getTypeDescriptor( tfld.getTypeName() ) +")V");
                        setTargetValue( mv, coreName, tfld );
                        mv.visitLabel(l5);


                    } else {
                        String getter = BuildUtils.getterName( ifld.getName(), ifld.getTypeName() );
                        String setter = BuildUtils.setterName(ifld.getName(), ifld.getTypeName());

                        getTargetValue( mv, ifld, wrapperName, coreName );
                        Label l0 = new Label();
                        mv.visitJumpInsn(IFNULL, l0);
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitFieldInsn(GETFIELD, BuildUtils.getInternalType( wrapperName ), ifld.getName()+"_Dist", "Lorg/drools/chance/common/IImperfectField;");
//                        mv.visitVarInsn(ALOAD, 0);
//                        mv.visitFieldInsn( GETFIELD,
//                                           BuildUtils.getInternalType( wrapperName ),
//                                           "core",
//                                           BuildUtils.getTypeDescriptor( coreName ) );
//                        mv.visitMethodInsn(INVOKEVIRTUAL, BuildUtils.getInternalType( coreName ), getter, "()"+ BuildUtils.getTypeDescriptor( ifld.getTypeName() ) );
                        getTargetValue( mv, ifld, wrapperName, coreName );
                        mv.visitMethodInsn(INVOKEINTERFACE, "org/drools/chance/common/IImperfectField", "setValue", "(Ljava/lang/Object;)V");
                        mv.visitLabel(l0);

                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitFieldInsn(GETFIELD, BuildUtils.getInternalType( wrapperName ), ifld.getName()+"_Dist", "Lorg/drools/chance/common/IImperfectField;");
                        Label l1 = new Label();
                        mv.visitJumpInsn(IFNULL, l1);
                        mv.visitVarInsn(ALOAD, 0);
//                        mv.visitFieldInsn( GETFIELD,
//                                           BuildUtils.getInternalType( wrapperName ),
//                                           "core",
//                                           BuildUtils.getTypeDescriptor( coreName ) );
                        prepareSetTargetValue( mv, wrapperName, coreName );
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitFieldInsn(GETFIELD, BuildUtils.getInternalType( wrapperName ), ifld.getName()+"_Dist", "Lorg/drools/chance/common/IImperfectField;");
                        mv.visitMethodInsn(INVOKEINTERFACE, "org/drools/chance/common/IImperfectField", "getCrisp", "()Ljava/lang/Object;");
                        mv.visitTypeInsn(CHECKCAST, BuildUtils.getInternalType( ifld.getTypeName() ) );
//                        mv.visitMethodInsn(INVOKEVIRTUAL, BuildUtils.getInternalType( coreName ), setter, "(" + BuildUtils.getTypeDescriptor(ifld.getTypeName()) + ")V");
                        setTargetValue( mv, coreName, ifld );
                        mv.visitLabel(l1);

                    }
                }

            }

            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 3);
            mv.visitEnd();

        }

    }

    protected abstract void setTargetValue(MethodVisitor mv, String coreName, FieldDefinition field);

    protected abstract void prepareSetTargetValue(MethodVisitor mv, String wrapperName, String coreName);

//    protected abstract void getTargetValue(MethodVisitor mv, String wrapperName, String typeName, String coreName, String getter);
    protected abstract void getTargetValue( MethodVisitor mv, FieldDefinition field, String wrapperName, String coreName );


    protected void initImperfectFields( ClassDefinition cdef, MethodVisitor mv ) {
        for ( FieldDefinition fld : cdef.getFieldsDefinitions() ) {
                if ( fld instanceof ImperfectFieldDefinition ) {
                    ImperfectFieldDefinition ifld = (ImperfectFieldDefinition) fld;

                    if ( isLinguistic( ifld ) ) {

                        FieldDefinition tfld = getSupportField( cdef, ifld );
                        buildImperfectLinguisticField( mv, cdef.getName(), ifld, tfld );


                    } else {
                        buildImperfectField( mv, cdef.getName(), ifld );

                    }
                }
            }
    }

    
    
    
    
    
    


    protected void buildImperfectFields( ClassWriter cw, ClassDefinition classDef ) {
        for ( FieldDefinition fld : classDef.getFieldsDefinitions() ) {
            if ( fld instanceof ImperfectFieldDefinition ) {
                tabooMethods.add( BuildUtils.setterName(fld.getName(), fld.getTypeName()) );
                tabooMethods.add( BuildUtils.getterName( fld.getName(), fld.getTypeName() ) );

                if ( isLinguistic( ((ImperfectFieldDefinition) fld ) ) ) {
                    FieldDefinition support = getSupportField( classDef, (ImperfectFieldDefinition) fld );
                    tabooMethods.add( BuildUtils.setterName( support.getName(), support.getTypeName() ) );
                    tabooMethods.add( BuildUtils.getterName( support.getName(), support.getTypeName() ) );
                }

                FieldVisitor fv = cw.visitField( ACC_PRIVATE,
                        fld.getName() + "_Dist",
                        "Lorg/drools/chance/common/IImperfectField;",
                        "Lorg/drools/chance/common/IImperfectField<" + BuildUtils.getTypeDescriptor( fld.getTypeName() ) + ">;",
                        null);
                fv.visitEnd();

            }
        }
    }


    
    
    
    
    

    protected void buildImperfectField(MethodVisitor mv, String wrapperName, ImperfectFieldDefinition ifld) {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitTypeInsn( NEW, ifld.getHistory() == 0 ?
                               "org/drools/chance/common/ImperfectField" :
                               "org/drools/chance/common/ImperfectHistoryField" );
        mv.visitInsn(DUP);

        mv.visitLdcInsn( ifld.getImpKind() );
        mv.visitLdcInsn( ifld.getImpType() );
        mv.visitLdcInsn( ifld.getDegreeType() );
        mv.visitLdcInsn( Type.getType( BuildUtils.getTypeDescriptor( ifld.getTypeName() ) ) );
        mv.visitMethodInsn( INVOKESTATIC,
                "org/drools/chance/common/StrategyFactory",
                "buildStrategies",
                "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Class;)Lorg/drools/chance/distribution/IDistributionStrategies;");

        if ( ifld.getHistory() > 0 ) {
            mv.visitLdcInsn( ""+ifld.getHistory() );

            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I");
        }

        if ( ifld.getInitExpr() != null ) {
            mv.visitLdcInsn( ifld.getInitExpr() );
        }

        if ( ifld.getHistory() > 0 ) {
            if ( ifld.getInitExpr() != null ) {
                mv.visitMethodInsn( INVOKESPECIAL,
                    "org/drools/chance/common/ImperfectHistoryField",
                    "<init>",
                    "(Lorg/drools/chance/distribution/IDistributionStrategies;ILjava/lang/String;)V" );
            } else {
                throw  new UnsupportedOperationException( " ImpHistoricalField must have an init expr " );
            }
        } else {
            if ( ifld.getInitExpr() != null ) {
                mv.visitMethodInsn( INVOKESPECIAL,
                    "org/drools/chance/common/ImperfectField",
                    "<init>",
                    "(Lorg/drools/chance/distribution/IDistributionStrategies;Ljava/lang/String;)V");
            } else {
                mv.visitMethodInsn( INVOKESPECIAL,
                    "org/drools/chance/common/ImperfectField",
                    "<init>",
                    "(Lorg/drools/chance/distribution/IDistributionStrategies;)V");
            }

        }
            mv.visitFieldInsn( PUTFIELD,
                BuildUtils.getInternalType( wrapperName ),
                ifld.getName()+ "_Dist",
                "Lorg/drools/chance/common/IImperfectField;");
    }


    protected void buildImperfectLinguisticField(MethodVisitor mv, String wrapperName, ImperfectFieldDefinition ifld, FieldDefinition tfld ) {

        mv.visitVarInsn(ALOAD, 0);
        mv.visitTypeInsn(NEW, "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField");
        mv.visitInsn(DUP);

        mv.visitLdcInsn( ifld.getImpKind() );
        mv.visitLdcInsn( ifld.getImpType() );
        mv.visitLdcInsn( ifld.getDegreeType() );
        mv.visitLdcInsn( Type.getType( BuildUtils.getTypeDescriptor( ifld.getTypeName() ) ) );
        mv.visitMethodInsn( INVOKESTATIC,
                            "org/drools/chance/common/StrategyFactory",
                            "buildStrategies",
                            "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Class;)Lorg/drools/chance/distribution/IDistributionStrategies;");

        mv.visitLdcInsn( "possibility" );
        mv.visitLdcInsn( "linguistic" );
        mv.visitLdcInsn( ifld.getDegreeType() );
        mv.visitLdcInsn( Type.getType( BuildUtils.getTypeDescriptor( tfld.getTypeName() ) ) );
        mv.visitMethodInsn( INVOKESTATIC,
                            "org/drools/chance/common/StrategyFactory",
                            "buildStrategies",
                            "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Class;)Lorg/drools/chance/distribution/IDistributionStrategies;");


        mv.visitLdcInsn( ""+ifld.getHistory() );
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(Ljava/lang/String;)Ljava/lang/Integer;");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I");


        if ( ifld.getInitExpr() == null ) {
            mv.visitInsn( ACONST_NULL );
        } else {
            mv.visitLdcInsn( ifld.getInitExpr() );
        }


        mv.visitMethodInsn( INVOKESPECIAL,
                            "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField",
                            "<init>",
                            "(Lorg/drools/chance/distribution/IDistributionStrategies;Lorg/drools/chance/distribution/IDistributionStrategies;ILjava/lang/String;)V" );

        mv.visitFieldInsn(  PUTFIELD,
                            BuildUtils.getInternalType( wrapperName ),
                            ifld.getName()+"_Dist",
                            "Lorg/drools/chance/common/IImperfectField;");
    }






    protected String buildSignature( Method method ) {
        String sig = "(";
            for ( Class arg : method.getParameterTypes() ) {
                sig += org.drools.factmodel.BuildUtils.getTypeDescriptor(arg.getName());
            }
        sig += ")";
        sig += org.drools.factmodel.BuildUtils.getTypeDescriptor(method.getReturnType().getName());
        return sig;
    }


    protected int getStackSize( Method m ) {
        int stack = 1;
        for ( Class klass : m.getParameterTypes() ) {
            stack += org.drools.factmodel.BuildUtils.sizeOf(klass.getName());
        }
        return stack;
    }
    
    
    
    
    
    
    
    protected FieldDefinition getSupportField(ClassDefinition cdef, ImperfectFieldDefinition ifld) {
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

    
    
    
    



    protected void buildImperfectLinguisticFieldGettersAndSetters(ClassWriter cw, String wrapperName, String coreName, ImperfectFieldDefinition ifld, FieldDefinition tfld) {

        String getter = BuildUtils.getterName( ifld.getName(), ifld.getTypeName() );
        String setter = BuildUtils.setterName( ifld.getName(), ifld.getTypeName() );

        String targetGetter = BuildUtils.getterName( tfld.getName(), tfld.getTypeName() );
        String targetSetter = BuildUtils.setterName( tfld.getName(), tfld.getTypeName() );
        String targetType = tfld.getTypeName();


        // first build the fuzzy field

        {
            MethodVisitor mv = cw.visitMethod( ACC_PUBLIC,
                                               getter,
                                               "()Lorg/drools/chance/distribution/IDistribution;",
                                               "()Lorg/drools/chance/distribution/IDistribution<" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) + ">;",
                                               null);
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitFieldInsn( GETFIELD,
                               BuildUtils.getInternalType( wrapperName ) ,
                               ifld.getName() + "_Dist",
                               "Lorg/drools/chance/common/IImperfectField;");
            mv.visitMethodInsn( INVOKEINTERFACE,
                                "org/drools/chance/common/IImperfectField",
                                "getCurrent",
                                "()Lorg/drools/chance/distribution/IDistribution;");
            mv.visitInsn( ARETURN );
            mv.visitMaxs( 1, 1 );
            mv.visitEnd();
        }
        {
            MethodVisitor mv = cw.visitMethod( ACC_PUBLIC,
                                               getter + "Value",
                                               "()" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ),
                                               null, null);
            mv.visitCode();
//            mv.visitVarInsn(ALOAD, 0);
//            mv.visitFieldInsn( GETFIELD,
//                               BuildUtils.getInternalType( wrapperName ) ,
//                               "core",
//                               BuildUtils.getTypeDescriptor( coreName ));
//            mv.visitMethodInsn( INVOKEVIRTUAL,
//                                BuildUtils.getInternalType( coreName ),
//                                getter,
//                                "()" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) );
            getTargetValue( mv, ifld, wrapperName, coreName );
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            MethodVisitor mv = cw.visitMethod( ACC_PUBLIC,
                                               setter,
                                               "(" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) + ")V",
                                               null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
//            mv.visitFieldInsn( GETFIELD,
//                               BuildUtils.getInternalType( wrapperName ) ,
//                               "core",
//                               BuildUtils.getTypeDescriptor( coreName ));
            prepareSetTargetValue( mv, wrapperName, coreName );
            mv.visitVarInsn(ALOAD, 1);
//            mv.visitMethodInsn( INVOKEVIRTUAL,
//                                BuildUtils.getInternalType( coreName ),
//                                setter,
//                                "(" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) + ")V");
            setTargetValue( mv, coreName, ifld );
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                               BuildUtils.getInternalType( wrapperName ),
                               ifld.getName() + "_Dist",
                               "Lorg/drools/chance/common/IImperfectField;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_0);
            mv.visitMethodInsn(INVOKEINTERFACE,
                               "org/drools/chance/common/IImperfectField",
                               "setValue",
                               "(Ljava/lang/Object;Z)V");
            mv.visitVarInsn(ALOAD, 0);
//            mv.visitFieldInsn( GETFIELD,
//                               BuildUtils.getInternalType( wrapperName ),
//                               "core",
//                               BuildUtils.getTypeDescriptor( coreName ));
            prepareSetTargetValue( mv, wrapperName, coreName );
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                               BuildUtils.getInternalType( wrapperName ) ,
                               ifld.getName() + "_Dist",
                               "Lorg/drools/chance/common/IImperfectField;");
            mv.visitTypeInsn(CHECKCAST, "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField");
            mv.visitMethodInsn( INVOKEVIRTUAL,
                                "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField",
                                "defuzzify",
                                "()Ljava/lang/Number;");
            mv.visitTypeInsn(CHECKCAST, BuildUtils.getInternalType( targetType ));
//            mv.visitMethodInsn( INVOKEVIRTUAL,
//                                BuildUtils.getInternalType( coreName ),
//                                targetSetter,
//                                "(" + BuildUtils.getTypeDescriptor( targetType ) + ")V");
            setTargetValue( mv, coreName, tfld );
            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 2);
            mv.visitEnd();
        }
        {
            MethodVisitor mv = cw.visitMethod( ACC_PUBLIC,
                                               setter.replace( "set", "update" ),
                                               "(" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) + ")V",
                                               null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
//            mv.visitFieldInsn( GETFIELD,
//                               BuildUtils.getInternalType( wrapperName ),
//                               "core",
//                               BuildUtils.getTypeDescriptor( coreName ));
            prepareSetTargetValue( mv, wrapperName, coreName );
            mv.visitVarInsn(ALOAD, 1);
//            mv.visitMethodInsn( INVOKEVIRTUAL,
//                                BuildUtils.getInternalType( coreName ),
//                                setter,
//                                "(" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) + ")V");
            setTargetValue( mv, coreName, ifld );
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                               BuildUtils.getInternalType( wrapperName ),
                               ifld.getName() + "_Dist",
                               "Lorg/drools/chance/common/IImperfectField;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_1);
            mv.visitMethodInsn( INVOKEINTERFACE,
                                "org/drools/chance/common/IImperfectField",
                                "setValue",
                                "(Ljava/lang/Object;Z)V");
            mv.visitVarInsn(ALOAD, 0);
//            mv.visitFieldInsn( GETFIELD,
//                               BuildUtils.getInternalType( wrapperName ),
//                               "core",
//                               BuildUtils.getTypeDescriptor( coreName ));
            prepareSetTargetValue( mv, wrapperName, coreName );
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                               BuildUtils.getInternalType( wrapperName ),
                               ifld.getName() + "_Dist",
                               "Lorg/drools/chance/common/IImperfectField;");
            mv.visitTypeInsn( CHECKCAST, "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField" );
            mv.visitMethodInsn( INVOKEVIRTUAL,
                                "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField",
                                "defuzzify",
                                "()Ljava/lang/Number;");
            mv.visitTypeInsn(CHECKCAST, BuildUtils.getInternalType( targetType ));
//            mv.visitMethodInsn( INVOKEVIRTUAL,
//                                BuildUtils.getInternalType( coreName ),
//                                targetSetter,
//                                "(" + BuildUtils.getTypeDescriptor( targetType ) + ")V");
            setTargetValue( mv, coreName, tfld );
            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 2);
            mv.visitEnd();
        }
        {
            MethodVisitor mv = cw.visitMethod( ACC_PUBLIC,
                                               setter,
                                               "(Lorg/drools/chance/distribution/IDistribution;)V",
                                               "(Lorg/drools/chance/distribution/IDistribution<" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) + ">;)V",
                                               null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                               BuildUtils.getInternalType( wrapperName ) ,
                               ifld.getName() + "_Dist",
                               "Lorg/drools/chance/common/IImperfectField;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_1);
            mv.visitMethodInsn( INVOKEINTERFACE,
                                "org/drools/chance/common/IImperfectField",
                                "setValue",
                                "(Lorg/drools/chance/distribution/IDistribution;Z)V");



            mv.visitVarInsn(ALOAD, 0);
//            mv.visitFieldInsn( GETFIELD,
//                               BuildUtils.getInternalType( wrapperName ),
//                               "core",
//                               BuildUtils.getTypeDescriptor( coreName ));
            prepareSetTargetValue( mv, wrapperName, coreName );
            mv.visitVarInsn( ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                               BuildUtils.getInternalType( wrapperName ),
                               ifld.getName() + "_Dist",
                               "Lorg/drools/chance/common/IImperfectField;");
            mv.visitMethodInsn( INVOKEINTERFACE,
                                "org/drools/chance/common/IImperfectField",
                                "getCrisp",
                                "()Ljava/lang/Object;");
            mv.visitTypeInsn( CHECKCAST, BuildUtils.getInternalType( ifld.getTypeName() ) );
//            mv.visitMethodInsn( INVOKEVIRTUAL,
//                                BuildUtils.getInternalType( coreName ),
//                                setter,
//                                "(" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) + ")V");
            setTargetValue( mv, coreName, ifld );


            mv.visitVarInsn(ALOAD, 0);
//            mv.visitFieldInsn(GETFIELD, BuildUtils.getInternalType( wrapperName ), "core", BuildUtils.getTypeDescriptor( coreName ));
            prepareSetTargetValue( mv, wrapperName, coreName );
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                               BuildUtils.getInternalType( wrapperName ),
                               ifld.getName() + "_Dist",
                               "Lorg/drools/chance/common/IImperfectField;");
            mv.visitTypeInsn(CHECKCAST, "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField");
            mv.visitMethodInsn( INVOKEVIRTUAL,
                                "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField",
                                "defuzzify",
                                "()Ljava/lang/Number;");
            mv.visitTypeInsn(CHECKCAST, BuildUtils.getInternalType( targetType ));
//            mv.visitMethodInsn( INVOKEVIRTUAL,
//                                BuildUtils.getInternalType( coreName ),
//                                targetSetter,
//                                "(" + BuildUtils.getTypeDescriptor( targetType ) + ")V");
            setTargetValue( mv, coreName, tfld );
            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 2);
            mv.visitEnd();
        }
        {
            MethodVisitor mv = cw.visitMethod( ACC_PUBLIC,
                                               setter.replace("set","update"),
                                               "(Lorg/drools/chance/distribution/IDistribution;)V",
                                               "(Lorg/drools/chance/distribution/IDistribution<" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) + ">;)V",
                                               null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                               BuildUtils.getInternalType( wrapperName ),
                               ifld.getName() + "_Dist",
                               "Lorg/drools/chance/common/IImperfectField;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn( INVOKEINTERFACE,
                                "org/drools/chance/common/IImperfectField",
                                "update",
                                "(Lorg/drools/chance/distribution/IDistribution;)V");


            mv.visitVarInsn(ALOAD, 0);
//            mv.visitFieldInsn( GETFIELD,
//                               BuildUtils.getInternalType( wrapperName ),
//                               "core",
//                               BuildUtils.getTypeDescriptor( coreName ));
            prepareSetTargetValue( mv, wrapperName, coreName );
            mv.visitVarInsn( ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                               BuildUtils.getInternalType( wrapperName ),
                               ifld.getName() + "_Dist",
                               "Lorg/drools/chance/common/IImperfectField;");
            mv.visitMethodInsn( INVOKEINTERFACE,
                                "org/drools/chance/common/IImperfectField",
                                "getCrisp",
                                "()Ljava/lang/Object;");
            mv.visitTypeInsn( CHECKCAST, BuildUtils.getInternalType( ifld.getTypeName() ) );
//            mv.visitMethodInsn( INVOKEVIRTUAL,
//                                BuildUtils.getInternalType( coreName ),
//                                setter,
//                                "(" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) + ")V");
            setTargetValue( mv, coreName, ifld );

            mv.visitVarInsn(ALOAD, 0);
//            mv.visitFieldInsn( GETFIELD,
//                               BuildUtils.getInternalType( wrapperName ),
//                               "core",
//                               BuildUtils.getTypeDescriptor( coreName ));
            prepareSetTargetValue( mv, wrapperName, coreName );
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                               BuildUtils.getInternalType( wrapperName ),
                               ifld.getName() + "_Dist",
                               "Lorg/drools/chance/common/IImperfectField;");
            mv.visitTypeInsn(CHECKCAST, "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField");
            mv.visitMethodInsn( INVOKEVIRTUAL,
                                "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField",
                                "defuzzify",
                                "()Ljava/lang/Number;");
            mv.visitTypeInsn( CHECKCAST, BuildUtils.getInternalType( targetType ));
//            mv.visitMethodInsn( INVOKEVIRTUAL,
//                                BuildUtils.getInternalType( coreName ),
//                                targetSetter,
//                                "(" + BuildUtils.getTypeDescriptor( targetType ) + ")V");
            setTargetValue( mv, coreName, tfld );
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }











         // Now build for the target support field

        {
            MethodVisitor mv = cw.visitMethod( ACC_PUBLIC,
                                              targetGetter,
                                              "()" + BuildUtils.getTypeDescriptor( targetType ) + "", null, null);
            mv.visitCode();
//            mv.visitVarInsn(ALOAD, 0);
//            mv.visitFieldInsn( GETFIELD,
//                               BuildUtils.getInternalType( wrapperName ),
//                               "core",
//                               BuildUtils.getTypeDescriptor( coreName ));
//            mv.visitMethodInsn( INVOKEVIRTUAL,
//                                BuildUtils.getInternalType( coreName ),
//                                targetGetter,
//                                "()" + BuildUtils.getTypeDescriptor( targetType ) + "");
            getTargetValue( mv, tfld, wrapperName, coreName );
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            MethodVisitor mv = cw.visitMethod( ACC_PUBLIC,
                                               targetSetter,
                                               "(" + BuildUtils.getTypeDescriptor( targetType ) + ")V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                               BuildUtils.getInternalType( wrapperName ),
                               ifld.getName() + "_Dist",
                               "Lorg/drools/chance/common/IImperfectField;");
            mv.visitTypeInsn( CHECKCAST, "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn( INVOKEVIRTUAL,
                                "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField",
                                "fuzzify",
                                "(Ljava/lang/Number;)Lorg/drools/chance/distribution/IDistribution;");
            mv.visitVarInsn(ASTORE, 2);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                               BuildUtils.getInternalType( wrapperName ),
                               ifld.getName() + "_Dist",
                               "Lorg/drools/chance/common/IImperfectField;");
            mv.visitVarInsn(ALOAD, 2);
            mv.visitInsn(ICONST_0);
            mv.visitMethodInsn( INVOKEINTERFACE,
                                "org/drools/chance/common/IImperfectField",
                                "setValue",
                                "(Lorg/drools/chance/distribution/IDistribution;Z)V");
            mv.visitVarInsn(ALOAD, 0);
//            mv.visitFieldInsn( GETFIELD,
//                               BuildUtils.getInternalType( wrapperName ),
//                               "core",
//                               BuildUtils.getTypeDescriptor( coreName ));
            prepareSetTargetValue( mv, wrapperName, coreName );
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                               BuildUtils.getInternalType( wrapperName ),
                               ifld.getName() + "_Dist",
                               "Lorg/drools/chance/common/IImperfectField;");
            mv.visitMethodInsn( INVOKEINTERFACE,
                                "org/drools/chance/common/IImperfectField",
                                "getCrisp",
                                "()Ljava/lang/Object;");
            mv.visitTypeInsn( CHECKCAST, BuildUtils.getInternalType( ifld.getTypeName() ));
//            mv.visitMethodInsn( INVOKEVIRTUAL,
//                                BuildUtils.getInternalType( coreName ),
//                                setter,
//                                "(" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) + ")V");
            setTargetValue( mv, coreName, ifld );
            mv.visitVarInsn(ALOAD, 0);
//            mv.visitFieldInsn( GETFIELD,
//                               BuildUtils.getInternalType( wrapperName ),
//                               "core",
//                               BuildUtils.getTypeDescriptor( coreName ));
            prepareSetTargetValue( mv, wrapperName, coreName );
            mv.visitVarInsn(ALOAD, 1);
//            mv.visitMethodInsn( INVOKEVIRTUAL,
//                                BuildUtils.getInternalType( coreName ),
//                                targetSetter,
//                                "(" + BuildUtils.getTypeDescriptor( targetType ) + ")V");
            setTargetValue( mv, coreName, tfld );
            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 3);
            mv.visitEnd();
        }
        {
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, targetSetter.replace("set","update"), "(" + BuildUtils.getTypeDescriptor( targetType ) + ")V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                               BuildUtils.getInternalType( wrapperName ),
                               ifld.getName() + "_Dist",
                               "Lorg/drools/chance/common/IImperfectField;");
            mv.visitTypeInsn(CHECKCAST, "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn( INVOKEVIRTUAL,
                                "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField",
                                "fuzzify",
                                "(Ljava/lang/Number;)Lorg/drools/chance/distribution/IDistribution;");
            mv.visitVarInsn(ASTORE, 2);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                               BuildUtils.getInternalType( wrapperName ),
                               ifld.getName() + "_Dist",
                               "Lorg/drools/chance/common/IImperfectField;");
            mv.visitVarInsn(ALOAD, 2);
            mv.visitInsn(ICONST_1);
            mv.visitMethodInsn( INVOKEINTERFACE,
                                "org/drools/chance/common/IImperfectField",
                                "setValue",
                                "(Lorg/drools/chance/distribution/IDistribution;Z)V");
            mv.visitVarInsn(ALOAD, 0);
//            mv.visitFieldInsn( GETFIELD,
//                               BuildUtils.getInternalType( wrapperName ),
//                               "core",
//                               BuildUtils.getTypeDescriptor( coreName ));
            prepareSetTargetValue( mv, wrapperName, coreName );
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                               BuildUtils.getInternalType( wrapperName ),
                               ifld.getName() + "_Dist",
                               "Lorg/drools/chance/common/IImperfectField;");
            mv.visitMethodInsn( INVOKEINTERFACE,
                                "org/drools/chance/common/IImperfectField",
                                "getCrisp",
                                "()Ljava/lang/Object;");
            mv.visitTypeInsn(CHECKCAST, BuildUtils.getInternalType( targetType ));
//            mv.visitMethodInsn( INVOKEVIRTUAL,
//                                BuildUtils.getInternalType( coreName ),
//                                setter,
//                                "(" + BuildUtils.getTypeDescriptor( targetType ) + ")V");
            setTargetValue( mv, coreName, tfld );
            mv.visitVarInsn(ALOAD, 0);
//            mv.visitFieldInsn( GETFIELD,
//                               BuildUtils.getInternalType( wrapperName ),
//                               "core",
//                               BuildUtils.getTypeDescriptor( coreName ));
            prepareSetTargetValue( mv, wrapperName, coreName );
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                               BuildUtils.getInternalType( wrapperName ),
                               ifld.getName() + "_Dist",
                               "Lorg/drools/chance/common/IImperfectField;");
            mv.visitTypeInsn( CHECKCAST, "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField");
            mv.visitMethodInsn( INVOKEVIRTUAL,
                                "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField",
                                "defuzzify",
                                "()Ljava/lang/Number;");
            mv.visitTypeInsn( CHECKCAST, "java/lang/Double");
//            mv.visitMethodInsn( INVOKEVIRTUAL,
//                                BuildUtils.getInternalType( coreName ),
//                                targetSetter,
//                                "(" + BuildUtils.getTypeDescriptor( targetType ) + ")V");
            setTargetValue( mv, coreName, tfld );
            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 3);
            mv.visitEnd();
        }
    }




    protected void buildImperfectFieldGettersAndSetters( ClassWriter cw, String wrapperName, String core, ImperfectFieldDefinition ifld) {
        MethodVisitor mv;
        String getter = BuildUtils.getterName( ifld.getName(), ifld.getTypeName() );
        String setter = BuildUtils.setterName( ifld.getName(), ifld.getTypeName() );

        {
            mv = cw.visitMethod( ACC_PUBLIC,
                    getter,
                    "()Lorg/drools/chance/common/IImperfectField;",
                    "()Lorg/drools/chance/common/IImperfectField<" + BuildUtils.getTypeDescriptor( ifld.getTypeName() )+ ">;", null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                               BuildUtils.getInternalType( wrapperName ),
                               ifld.getName() + "_Dist",
                               "Lorg/drools/chance/common/IImperfectField;");
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }

        {
            mv = cw.visitMethod( ACC_PUBLIC,
                                 getter+"Distr",
                                 "()Lorg/drools/chance/distribution/IDistribution;",
                                 "()Lorg/drools/chance/distribution/IDistribution<" + BuildUtils.getInternalType( ifld.getTypeName() )+ ">;",
                                 null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                               BuildUtils.getInternalType( wrapperName ),
                               ifld.getName() + "_Dist",
                               "Lorg/drools/chance/common/IImperfectField;");
            mv.visitMethodInsn( INVOKEINTERFACE,
                                "org/drools/chance/common/IImperfectField",
                                "getCurrent",
                                "()Lorg/drools/chance/distribution/IDistribution;");
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod( ACC_PUBLIC,
                                 getter+"Value",
                                 "()"+BuildUtils.getTypeDescriptor(ifld.getTypeName()),
                                 null, null);
            mv.visitCode();
            getTargetValue( mv, ifld, wrapperName, core );
//            mv.visitVarInsn(ALOAD, 0);
//            mv.visitFieldInsn( GETFIELD,
//                               BuildUtils.getInternalType( wrapperName ),
//                               "core",
//                               BuildUtils.getTypeDescriptor( core ) );
//            mv.visitMethodInsn( INVOKEVIRTUAL,
//                                BuildUtils.getInternalType( core ),
//                                getter,
//                                "()"+BuildUtils.getTypeDescriptor(ifld.getTypeName()) );
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod( ACC_PUBLIC,
                                setter, "("+BuildUtils.getTypeDescriptor( ifld.getTypeName() ) +")V",
                                null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            prepareSetTargetValue( mv, wrapperName, core );
//            mv.visitFieldInsn( GETFIELD,
//                               BuildUtils.getInternalType( wrapperName ),
//                               "core",
//                               BuildUtils.getTypeDescriptor( core ) );
            mv.visitVarInsn(ALOAD, 1);
            setTargetValue( mv, core, ifld );
//            mv.visitMethodInsn( INVOKEVIRTUAL,
//                                BuildUtils.getInternalType( core ),
//                                setter,
//                                "(" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) + ")V" );
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                               BuildUtils.getInternalType( wrapperName ),
                               ifld.getName()+"_Dist",
                               "Lorg/drools/chance/common/IImperfectField;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_0);
            mv.visitMethodInsn( INVOKEINTERFACE,
                                "org/drools/chance/common/IImperfectField",
                                "setValue",
                                "(Ljava/lang/Object;Z)V");
            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod( ACC_PUBLIC,
                                 setter.replace("set","update"),
                                 "(" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) + ")V",
                                 null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                               BuildUtils.getInternalType( wrapperName ),
                               ifld.getName()+"_Dist",
                               "Lorg/drools/chance/common/IImperfectField;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_1);
            mv.visitMethodInsn( INVOKEINTERFACE,
                                "org/drools/chance/common/IImperfectField",
                                "setValue",
                                "(Ljava/lang/Object;Z)V");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, BuildUtils.getInternalType( wrapperName ), "core", BuildUtils.getTypeDescriptor( core ) );
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                               BuildUtils.getInternalType( wrapperName ),
                               ifld.getName()+"_Dist",
                               "Lorg/drools/chance/common/IImperfectField;");
            mv.visitMethodInsn(INVOKEINTERFACE, "org/drools/chance/common/IImperfectField", "getCrisp", "()Ljava/lang/Object;");
            mv.visitTypeInsn(CHECKCAST, BuildUtils.getInternalType( ifld.getTypeName() ) );
            mv.visitMethodInsn( INVOKEVIRTUAL,
                                BuildUtils.getInternalType( core ),
                                setter,
                                "(" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) + ")V" );
            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod( ACC_PUBLIC,
                                setter,
                                "(Lorg/drools/chance/distribution/IDistribution;)V",
                                "(Lorg/drools/chance/distribution/IDistribution<" +BuildUtils.getTypeDescriptor( ifld.getTypeName() ) +">;)V",
                                null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                               BuildUtils.getInternalType( wrapperName ),
                               ifld.getName()+"_Dist",
                               "Lorg/drools/chance/common/IImperfectField;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_1);
            mv.visitMethodInsn( INVOKEINTERFACE,
                                "org/drools/chance/common/IImperfectField",
                                "setValue",
                                "(Lorg/drools/chance/distribution/IDistribution;Z)V");
            mv.visitVarInsn(ALOAD, 0);
//            mv.visitFieldInsn( GETFIELD,
//                               BuildUtils.getInternalType( wrapperName ),
//                               "core",
//                               BuildUtils.getTypeDescriptor( core ) );
            prepareSetTargetValue( mv, wrapperName, core );
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                               BuildUtils.getInternalType( wrapperName ),
                               ifld.getName() + "_Dist",
                               "Lorg/drools/chance/common/IImperfectField;");
            mv.visitMethodInsn( INVOKEINTERFACE,
                                "org/drools/chance/common/IImperfectField",
                                "getCrisp",
                                "()Ljava/lang/Object;");
            mv.visitTypeInsn(CHECKCAST, BuildUtils.getInternalType( ifld.getTypeName() ) );
//            mv.visitMethodInsn( INVOKEVIRTUAL,
//                                BuildUtils.getInternalType( core ),
//                                setter,
//                                "(" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) + ")V" );
            setTargetValue( mv, core, ifld );
            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod( ACC_PUBLIC,
                                setter.replace("set","update"),
                                "(Lorg/drools/chance/distribution/IDistribution;)V",
                                "(Lorg/drools/chance/distribution/IDistribution<" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) + ">;)V",
                                null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                               BuildUtils.getInternalType( wrapperName ),
                               ifld.getName()+"_Dist",
                               "Lorg/drools/chance/common/IImperfectField;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn( INVOKEINTERFACE,
                                "org/drools/chance/common/IImperfectField",
                                "update",
                                "(Lorg/drools/chance/distribution/IDistribution;)V");
            mv.visitVarInsn(ALOAD, 0);
            prepareSetTargetValue( mv, wrapperName, core );
//            mv.visitFieldInsn( GETFIELD,
//                               BuildUtils.getInternalType( wrapperName ),
//                               "core",
//                               BuildUtils.getTypeDescriptor( core ) );
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                               BuildUtils.getInternalType( wrapperName ),
                               ifld.getName()+"_Dist",
                               "Lorg/drools/chance/common/IImperfectField;");
            mv.visitMethodInsn( INVOKEINTERFACE,
                                "org/drools/chance/common/IImperfectField",
                                "getCrisp",
                                "()Ljava/lang/Object;");
            mv.visitTypeInsn(CHECKCAST, BuildUtils.getInternalType( ifld.getTypeName() ) );
//            mv.visitMethodInsn( INVOKEVIRTUAL,
//                                BuildUtils.getInternalType( core ),
//                                setter,
//                                "(" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) + ")V" );
            setTargetValue( mv, core, ifld );
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }


    }


    
    
}
