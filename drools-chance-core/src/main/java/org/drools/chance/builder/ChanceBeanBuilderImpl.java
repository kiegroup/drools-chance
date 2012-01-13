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
import org.drools.chance.degree.DegreeType;
import org.drools.chance.distribution.Distribution;
import org.drools.chance.distribution.ImpKind;
import org.drools.chance.distribution.ImpType;
import org.drools.factmodel.*;
import org.mvel2.asm.*;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class ChanceBeanBuilderImpl extends DefaultBeanClassBuilder {




    public byte[] buildClass( ClassDefinition classDef ) throws IntrospectionException,
            InvocationTargetException,
            ClassNotFoundException,
            IOException,
            NoSuchMethodException,
            NoSuchFieldException,
            InstantiationException,
            IllegalAccessException {

        try {

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


    protected void rewriteImperfectFields(ClassDefinition classDef) {
        Collection<FieldDefinition> originalFields = new ArrayList<FieldDefinition>( classDef.getFieldsDefinitions() );

        for ( FieldDefinition fld : originalFields ) {
            if ( fld.getAnnotations() != null ) {
                for ( AnnotationDefinition ann : fld.getAnnotations() ) {
                    if ( ann.getName().equals( Imperfect.class.getName() ) ) {
                        ImperfectFieldDefinition ifld = ImperfectFieldDefinition.fromField( fld, ann );

                        if ( ImperfectFieldDefinition.isLinguistic( ifld ) ) {
                            for ( FieldDefinition xfld : originalFields ) {
                                if ( xfld.getName().equals( ifld.getSupport() ) ) {
                                    ifld.setSupportFieldDef( xfld );
                                }
                            }
                        }

                        classDef.addField( ifld );
                        break;
                    }
                }
            }
        }
    }




    protected void finalizeCreation( ClassDefinition klass ) {

        Collection<FieldDefinition> originalFields = new HashSet<FieldDefinition>( klass.getFieldsDefinitions() );

        for ( FieldDefinition field : originalFields ) {
            if ( field instanceof ImperfectFieldDefinition ) {
                FieldDefinition fieldDistr = new VirtualFieldDefinition();
                fieldDistr.setName( field.getName() + "Distr" );
                fieldDistr.setTypeName( Distribution.class.getName() );
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





    /*******************************************************************************************************************
     *
     * Constructors
     *
     *******************************************************************************************************************/


    @Override
    protected void buildConstructorWithFields( ClassVisitor cw, ClassDefinition classDef, Collection<FieldDefinition> fieldDefs ) {

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
    protected void buildDefaultConstructor( ClassVisitor cw, ClassDefinition classDef ) {

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


    protected boolean defaultConstructorStart( MethodVisitor mv, ClassDefinition classDef ) {
        // Building default constructor

        mv.visitVarInsn( Opcodes.ALOAD,
                0 );

        String sup;
        try {
            sup = Type.getInternalName(Class.forName(classDef.getSuperClass()));
        } catch (ClassNotFoundException e) {
            sup = BuildUtils.getInternalType( classDef.getSuperClass() );
        }
        mv.visitMethodInsn( Opcodes.INVOKESPECIAL,
                sup,
                "<init>",
                Type.getMethodDescriptor( Type.VOID_TYPE,
                        new Type[]{} ) );

        boolean hasObjects = false;


        if ( classDef.isTraitable() ) {
            initializeDynamicTypeStructures( mv, classDef );
        }

        return hasObjects;
    }



    protected boolean defaultConstructorInitValues( MethodVisitor mv, ClassDefinition classDef ) {

        boolean hasObjects = false;
        for (FieldDefinition field : classDef.getFieldsDefinitions()) {

            if ( field instanceof  VirtualFieldDefinition ) continue;

            Object val;
            if ( field instanceof ImperfectFieldDefinition ) {
                val = field.getInitExpr();
            } else {
                val = BuildUtils.getDefaultValue(field);
            }

            if (val != null) {


                if ( field instanceof ImperfectFieldDefinition ) {
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitFieldInsn(GETFIELD,
                            BuildUtils.getInternalType( classDef.getClassName() ),
                            field.getName()+"_$$Imp",
                            "Lorg/drools/chance/common/ImperfectField;");
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitFieldInsn(GETFIELD,
                            BuildUtils.getInternalType( classDef.getClassName() ),
                            field.getName()+"_$$Imp",
                            "Lorg/drools/chance/common/ImperfectField;");
                    mv.visitMethodInsn( INVOKEINTERFACE,
                            "org/drools/chance/common/ImperfectField",
                            "getStrategies",
                            "()Lorg/drools/chance/distribution/DistributionStrategies;");
                    mv.visitLdcInsn( field.getInitExpr() );
                    mv.visitMethodInsn( INVOKEINTERFACE,
                            "org/drools/chance/distribution/DistributionStrategies",
                            "parse",
                            "(Ljava/lang/String;)Lorg/drools/chance/distribution/Distribution;" );


                } else {

                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                    if ( BuildUtils.isPrimitive( field.getTypeName() )
                            || BuildUtils.isBoxed( field.getTypeName() )
                            || String.class.getName().equals( field.getTypeName() ) ) {
                        mv.visitLdcInsn(val);
                        if ( BuildUtils.isBoxed(field.getTypeName()) ) {
                            mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                                    BuildUtils.getInternalType(field.getTypeName()),
                                    "valueOf",
                                    "("+BuildUtils.unBox(field.getTypeName())+")"+BuildUtils.getTypeDescriptor(field.getTypeName()));
                        }
                    } else {
                        hasObjects = true;
                        String type = BuildUtils.getInternalType( val.getClass().getName() );
                        mv.visitTypeInsn( NEW, type );
                        mv.visitInsn(DUP);
                        mv.visitMethodInsn( INVOKESPECIAL,
                                type,
                                "<init>",
                                "()V");
                    }


                    if (! field.isInherited()) {
                        mv.visitFieldInsn( Opcodes.PUTFIELD,
                                BuildUtils.getInternalType( classDef.getClassName() ),
                                field.getName(),
                                BuildUtils.getTypeDescriptor( field.getTypeName() ) );
                    } else {
                        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                                BuildUtils.getInternalType(classDef.getClassName()),
                                field.getWriteMethod(),
                                Type.getMethodDescriptor(Type.VOID_TYPE,
                                        new Type[]{Type.getType(BuildUtils.getTypeDescriptor(field.getTypeName()))}
                                ));
                    }
                }

            }
        }
        return hasObjects;
    }




    /*******************************************************************************************************************
     *
     * Fields
     *
     *******************************************************************************************************************/

    @Override
    protected void buildFields(ClassWriter cw, ClassDefinition classDef) {

        super.buildFields( cw, classDef );

        buildImperfectFields( cw, classDef );

        buildSynchFieldsMethod(cw, classDef.getName(), classDef.getName(), classDef);
    }


    protected void initImperfectFields( ClassDefinition cdef, MethodVisitor mv ) {
        for ( FieldDefinition fld : cdef.getFieldsDefinitions() ) {
            if ( fld instanceof  VirtualFieldDefinition ) continue;
            if ( fld instanceof ImperfectFieldDefinition ) {
                ImperfectFieldDefinition ifld = (ImperfectFieldDefinition) fld;

                if ( ImperfectFieldDefinition.isLinguistic( ifld ) ) {

                    FieldDefinition tfld = getSupportField( cdef, ifld );
                    initImperfectLinguisticField(mv, cdef.getName(), ifld, tfld);


                } else {
                    initImperfectField(mv, cdef.getName(), ifld);

                }
            }
        }
    }


    protected void buildImperfectFields( ClassWriter cw, ClassDefinition classDef ) {
        for ( FieldDefinition fld : classDef.getFieldsDefinitions() ) {
            if ( fld instanceof  VirtualFieldDefinition ) continue;
            if ( fld instanceof ImperfectFieldDefinition ) {

                FieldVisitor fv = cw.visitField( ACC_PRIVATE,
                        fld.getName() + "_$$Imp",
                        "Lorg/drools/chance/common/ImperfectField;",
                        "Lorg/drools/chance/common/ImperfectField<" + BuildUtils.getTypeDescriptor( fld.getTypeName() ) + ">;",
                        null );
                fv.visitEnd();

            }
        }
    }








    protected void initImperfectField( MethodVisitor mv, String beanName, ImperfectFieldDefinition ifld ) {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitTypeInsn( NEW, ifld.getHistory() == 0 ?
                "org/drools/chance/common/ImperfectFieldImpl" :
                "org/drools/chance/common/ImperfectHistoryField" );
        mv.visitInsn(DUP);

        createImperfectField( mv, ifld.getImpKind().name(), ifld.getImpType().name(), ifld.getDegreeType().name(), ifld.getTypeName() );

        if ( ifld.getHistory() > 0 ) {
            mv.visitLdcInsn( ""+ifld.getHistory() );

            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I");
        }


        if ( ifld.getInitExpr() != null ) {
            mv.visitLdcInsn( ifld.getInitExpr() );
        }

//        if ( ifld.getHistory() > 0 ) {
//            if ( ifld.getInitExpr() != null ) {
//                mv.visitMethodInsn( INVOKESPECIAL,
//                        "org/drools/chance/common/ImperfectHistoryField",
//                        "<init>",
//                        "(Lorg/drools/chance/distribution/DistributionStrategies;ILjava/lang/String;)V" );
//            } else {
//                throw  new UnsupportedOperationException( " ImpHistoricalField must have an init expr " );
//            }
//        } else {
        if ( ifld.getInitExpr() != null ) {
            mv.visitMethodInsn( INVOKESPECIAL,
                    "org/drools/chance/common/ImperfectFieldImpl",
                    "<init>",
                    "(Lorg/drools/chance/distribution/DistributionStrategies;Ljava/lang/String;)V");
        } else {
            mv.visitMethodInsn( INVOKESPECIAL,
                    "org/drools/chance/common/ImperfectFieldImpl",
                    "<init>",
                    "(Lorg/drools/chance/distribution/DistributionStrategies;)V");
        }
//        }

        mv.visitFieldInsn( PUTFIELD,
                BuildUtils.getInternalType( beanName ),
                ifld.getName()+ "_$$Imp",
                "Lorg/drools/chance/common/ImperfectField;");

    }




    protected void initImperfectLinguisticField( MethodVisitor mv, String beanName, ImperfectFieldDefinition ifld, FieldDefinition tfld ) {

        mv.visitVarInsn(ALOAD, 0);
        mv.visitTypeInsn(NEW, "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField");
        mv.visitInsn(DUP);

        createImperfectField(mv, ifld.getImpKind().name(), ifld.getImpType().name(), ifld.getDegreeType().name(), ifld.getTypeName());

        createImperfectField( mv, ImpKind.POSSIBILITY.name(), ImpType.LINGUISTIC.name(), ifld.getDegreeType().name(), tfld.getTypeName() );


        if ( ifld.getInitExpr() == null ) {
            mv.visitInsn( ACONST_NULL );
        } else {
            mv.visitLdcInsn( ifld.getInitExpr() );
        }


        mv.visitMethodInsn( INVOKESPECIAL,
                "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField",
                "<init>",
                "(Lorg/drools/chance/distribution/DistributionStrategies;Lorg/drools/chance/distribution/DistributionStrategies;Ljava/lang/String;)V" );

        mv.visitFieldInsn(  PUTFIELD,
                BuildUtils.getInternalType( beanName ),
                ifld.getName()+"_$$Imp",
                "Lorg/drools/chance/common/ImperfectField;");
    }






    protected void buildSynchFieldsMethod( ClassWriter cw, String proxyName, String coreName, ClassDefinition def ) {
        {
            MethodVisitor mv = cw.visitMethod(ACC_PRIVATE, "synchFields", "()V", null, null);
            mv.visitCode();

            for ( FieldDefinition fld : def.getFieldsDefinitions() ) {
                if ( fld instanceof  VirtualFieldDefinition ) continue;
                if ( fld instanceof ImperfectFieldDefinition ) {
                    ImperfectFieldDefinition ifld = (ImperfectFieldDefinition) fld;
                    if ( ImperfectFieldDefinition.isLinguistic( ifld ) ) {

                        FieldDefinition tfld = getSupportField(def, ifld);

                        synchLinguisticField( mv, fld, tfld, coreName );

                    } else {

                        synchField( mv, ifld, proxyName, coreName );

                    }
                }

            }

            mv.visitInsn(RETURN);
            mv.visitMaxs( 3, 3 );
            mv.visitEnd();

        }

    }




    protected void synchLinguisticField( MethodVisitor mv, FieldDefinition fld, FieldDefinition tfld, String coreName ) {
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD,
                BuildUtils.getInternalType( coreName ),
                tfld.getName(),
                BuildUtils.getTypeDescriptor( tfld.getTypeName() ) );
        Label l0 = new Label();
        mv.visitJumpInsn( IFNULL, l0 );
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD,
                BuildUtils.getInternalType( coreName ),
                fld.getName() +"_$$Imp",
                "Lorg/drools/chance/common/ImperfectField;" );
        mv.visitTypeInsn( CHECKCAST,
                "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField" );
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD,
                BuildUtils.getInternalType( coreName ),
                tfld.getName(),
                BuildUtils.getTypeDescriptor( tfld.getTypeName() ) );
        mv.visitMethodInsn( INVOKEVIRTUAL,
                "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField",
                "fuzzify",
                "(Ljava/lang/Number;)Lorg/drools/chance/distribution/Distribution;" );
        mv.visitVarInsn( ASTORE, 1 );
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD,
                BuildUtils.getInternalType( coreName ),
                fld.getName() +"_$$Imp",
                "Lorg/drools/chance/common/ImperfectField;" );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitInsn( ICONST_0 );
        mv.visitMethodInsn( INVOKEINTERFACE,
                "org/drools/chance/common/ImperfectField",
                "setValue",
                "(Lorg/drools/chance/distribution/Distribution;Z)V" );
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD,
                BuildUtils.getInternalType( coreName ),
                fld.getName() +"_$$Imp",
                "Lorg/drools/chance/common/ImperfectField;" );
        mv.visitMethodInsn( INVOKEINTERFACE,
                "org/drools/chance/common/ImperfectField",
                "getCrisp",
                "()Ljava/lang/Object;" );
        mv.visitTypeInsn( CHECKCAST,
                BuildUtils.getInternalType( fld.getTypeName() ) );
        mv.visitFieldInsn( PUTFIELD,
                BuildUtils.getInternalType( coreName ),
                fld.getName(),
                BuildUtils.getTypeDescriptor( fld.getTypeName() ) );
        Label l1 = new Label();
        mv.visitJumpInsn( GOTO, l1 );
        mv.visitLabel( l0 );
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD,
                BuildUtils.getInternalType( coreName ),
                fld.getName(),
                BuildUtils.getTypeDescriptor( fld.getTypeName() ) );
        Label l2 = new Label();
        mv.visitJumpInsn( IFNULL, l2 );
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD,
                BuildUtils.getInternalType( coreName ),
                fld.getName() +"_$$Imp",
                "Lorg/drools/chance/common/ImperfectField;" );
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD,
                BuildUtils.getInternalType( coreName ),
                fld.getName(),
                BuildUtils.getTypeDescriptor( fld.getTypeName() ) );
        mv.visitMethodInsn( INVOKEINTERFACE,
                "org/drools/chance/common/ImperfectField",
                "setValue",
                "(Ljava/lang/Object;)V" );
        mv.visitLabel( l2 );
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD,
                BuildUtils.getInternalType( coreName ),
                fld.getName() +"_$$Imp",
                "Lorg/drools/chance/common/ImperfectField;" );
        mv.visitJumpInsn( IFNULL, l1 );
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD,
                BuildUtils.getInternalType( coreName ),
                fld.getName() +"_$$Imp",
                "Lorg/drools/chance/common/ImperfectField;" );
        mv.visitMethodInsn( INVOKEINTERFACE,
                "org/drools/chance/common/ImperfectField",
                "getCrisp",
                "()Ljava/lang/Object;" );
        mv.visitTypeInsn( CHECKCAST,
                BuildUtils.getInternalType( fld.getTypeName() ) );
        mv.visitFieldInsn( PUTFIELD,
                BuildUtils.getInternalType( coreName ),
                fld.getName(),
                BuildUtils.getTypeDescriptor( fld.getTypeName() ) );
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD,
                BuildUtils.getInternalType( coreName ),
                fld.getName() +"_$$Imp",
                "Lorg/drools/chance/common/ImperfectField;" );
        mv.visitTypeInsn( CHECKCAST,
                "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField" );

        defuzzify( mv, tfld.getTypeName()  );

        mv.visitFieldInsn( PUTFIELD,
                BuildUtils.getInternalType( coreName ),
                tfld.getName(),
                BuildUtils.getTypeDescriptor( tfld.getTypeName() ) );

        mv.visitLabel( l1 );
    }


    protected void synchField( MethodVisitor mv, FieldDefinition ifld, String proxyName, String coreName ) {
        getTargetValue( mv, ifld, proxyName, coreName );

        Label l0 = new Label();
        mv.visitJumpInsn( IFNULL, l0 );
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD,
                BuildUtils.getInternalType( proxyName ),
                ifld.getName()+"_$$Imp",
                "Lorg/drools/chance/common/ImperfectField;" );
        getTargetValue( mv, ifld, proxyName, coreName );
        mv.visitMethodInsn( INVOKEINTERFACE,
                "org/drools/chance/common/ImperfectField",
                "setValue",
                "(Ljava/lang/Object;)V");
        mv.visitLabel( l0 );

        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD,
                BuildUtils.getInternalType( proxyName ),
                ifld.getName()+"_$$Imp",
                "Lorg/drools/chance/common/ImperfectField;");
        Label l1 = new Label();
        mv.visitJumpInsn( IFNULL, l1 );
        mv.visitVarInsn( ALOAD, 0 );
        prepareSetTargetValue( mv, proxyName, coreName );
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD,
                BuildUtils.getInternalType( proxyName ),
                ifld.getName()+"_$$Imp",
                "Lorg/drools/chance/common/ImperfectField;" );
        mv.visitMethodInsn( INVOKEINTERFACE,
                "org/drools/chance/common/ImperfectField",
                "getCrisp",
                "()Ljava/lang/Object;");
        mv.visitTypeInsn( CHECKCAST,
                BuildUtils.getInternalType( ifld.getTypeName() ) );
        setTargetValue( mv, coreName, ifld );
        mv.visitLabel( l1 );


    }






    /*******************************************************************************************************************
     *
     * Accessors
     *
     *******************************************************************************************************************/




    @Override
    protected void buildGettersAndSetters(ClassWriter cw, ClassDefinition classDef) {
        for ( FieldDefinition fld : classDef.getFieldsDefinitions() ) {
            if ( fld instanceof  VirtualFieldDefinition ) continue;
            if ( ! fld.isInherited() ) {
                if ( fld instanceof ImperfectFieldDefinition ) {
                    ImperfectFieldDefinition ifld = (ImperfectFieldDefinition) fld;

                    if ( ImperfectFieldDefinition.isLinguistic( ifld ) ) {
                        FieldDefinition tfld = getSupportField( classDef, ifld );
                        buildImperfectLinguisticFieldGettersAndSetters(cw, classDef.getName(), classDef.getName(), ifld, tfld);
                    } else {
                        buildImperfectFieldGettersAndSetters( cw, classDef.getName(), classDef.getName(), ifld );
                    }

                } else {
                    if ( ! isSupport( classDef, fld ) ) {
                        this.buildGetMethod( cw,
                                classDef,
                                fld );
                        this.buildSetMethod( cw,
                                classDef,
                                fld );
                    }
                }
            }
        }
    }



    protected void buildImperfectFieldGettersAndSetters( ClassWriter cw, String proxyName, String core, ImperfectFieldDefinition ifld) {
        MethodVisitor mv;
        String getter = BuildUtils.getterName( ifld.getName(), ifld.getTypeName() );
        String setter = BuildUtils.setterName( ifld.getName(), ifld.getTypeName() );

        {
            mv = cw.visitMethod( ACC_PUBLIC,
                    getter,
                    "()Lorg/drools/chance/common/ImperfectField;",
                    "()Lorg/drools/chance/common/ImperfectField<" + BuildUtils.getTypeDescriptor( ifld.getTypeName() )+ ">;", null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                    BuildUtils.getInternalType( proxyName ),
                    ifld.getName() + "_$$Imp",
                    "Lorg/drools/chance/common/ImperfectField;");
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }

        {
            mv = cw.visitMethod( ACC_PUBLIC,
                    getter+"Distr",
                    "()Lorg/drools/chance/distribution/Distribution;",
                    "()Lorg/drools/chance/distribution/Distribution<" + BuildUtils.getTypeDescriptor( ifld.getTypeName() )+ ">;",
                    null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                    BuildUtils.getInternalType( proxyName ),
                    ifld.getName() + "_$$Imp",
                    "Lorg/drools/chance/common/ImperfectField;");
            mv.visitMethodInsn( INVOKEINTERFACE,
                    "org/drools/chance/common/ImperfectField",
                    "getCurrent",
                    "()Lorg/drools/chance/distribution/Distribution;");
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
            getTargetValue( mv, ifld, proxyName, core );
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }


        {
            mv = cw.visitMethod( ACC_PUBLIC,
                    setter,
                    "(Lorg/drools/chance/common/ImperfectField;)V",
                    "(Lorg/drools/chance/common/ImperfectField<" + BuildUtils.getTypeDescriptor( ifld.getTypeName() )+ ">;)V",
                    null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn( PUTFIELD,
                    BuildUtils.getInternalType( proxyName ),
                    ifld.getName()+"_$$Imp",
                    "Lorg/drools/chance/common/ImperfectField;");

            mv.visitVarInsn(ALOAD, 0);

            prepareSetTargetValue( mv, proxyName, core );


            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD,
                    BuildUtils.getInternalType(proxyName),
                    ifld.getName() + "_$$Imp",
                    "Lorg/drools/chance/common/ImperfectField;");
            mv.visitMethodInsn(INVOKEINTERFACE, "org/drools/chance/common/ImperfectField", "getCrisp", "()Ljava/lang/Object;");
            mv.visitTypeInsn(CHECKCAST, BuildUtils.getInternalType(ifld.getTypeName()));

            setTargetValue( mv, core, ifld );


            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 2);
            mv.visitEnd();
        }

        {
            mv = cw.visitMethod( ACC_PUBLIC,
                    setter.replace( "set", "update" ),
                    "(Lorg/drools/chance/common/ImperfectField;)V",
                    "(Lorg/drools/chance/common/ImperfectField<" + BuildUtils.getTypeDescriptor( ifld.getTypeName() )+ ">;)V",
                    null);
            mv.visitCode();


            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                    BuildUtils.getInternalType( proxyName ),
                    ifld.getName()+"_$$Imp",
                    "Lorg/drools/chance/common/ImperfectField;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn( INVOKEINTERFACE,
                    "org/drools/chance/common/ImperfectField",
                    "getCurrent",
                    "()Lorg/drools/chance/distribution/Distribution;");
            mv.visitMethodInsn( INVOKEINTERFACE,
                    "org/drools/chance/common/ImperfectField",
                    "update",
                    "(Lorg/drools/chance/distribution/Distribution;)V");



            mv.visitVarInsn(ALOAD, 0);

            prepareSetTargetValue( mv, proxyName, core );

            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD,
                    BuildUtils.getInternalType(proxyName),
                    ifld.getName() + "_$$Imp",
                    "Lorg/drools/chance/common/ImperfectField;");
            mv.visitMethodInsn(INVOKEINTERFACE, "org/drools/chance/common/ImperfectField", "getCrisp", "()Ljava/lang/Object;");
            mv.visitTypeInsn(CHECKCAST, BuildUtils.getInternalType(ifld.getTypeName()));

            setTargetValue( mv, core, ifld );


            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }


        {
            mv = cw.visitMethod( ACC_PUBLIC,
                    setter + "Value",
                    "("+BuildUtils.getTypeDescriptor( ifld.getTypeName() ) +")V",
                    null, null);
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 0);
            prepareSetTargetValue( mv, proxyName, core );
            mv.visitVarInsn(ALOAD, 1);
            setTargetValue( mv, core, ifld );

            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                    BuildUtils.getInternalType( proxyName ),
                    ifld.getName()+"_$$Imp",
                    "Lorg/drools/chance/common/ImperfectField;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_0);
            mv.visitMethodInsn( INVOKEINTERFACE,
                    "org/drools/chance/common/ImperfectField",
                    "setValue",
                    "(Ljava/lang/Object;Z)V");
            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 2);
            mv.visitEnd();
        }


        {
            mv = cw.visitMethod( ACC_PUBLIC,
                    setter.replace("set","update") + "Value",
                    "(" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) + ")V",
                    null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                    BuildUtils.getInternalType( proxyName ),
                    ifld.getName()+"_$$Imp",
                    "Lorg/drools/chance/common/ImperfectField;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_1);
            mv.visitMethodInsn( INVOKEINTERFACE,
                    "org/drools/chance/common/ImperfectField",
                    "setValue",
                    "(Ljava/lang/Object;Z)V");



            mv.visitVarInsn(ALOAD, 0);
            prepareSetTargetValue( mv, proxyName, core );
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                    BuildUtils.getInternalType( proxyName ),
                    ifld.getName()+"_$$Imp",
                    "Lorg/drools/chance/common/ImperfectField;");
            mv.visitMethodInsn( INVOKEINTERFACE,
                    "org/drools/chance/common/ImperfectField",
                    "getCrisp",
                    "()Ljava/lang/Object;");
            mv.visitTypeInsn(CHECKCAST, BuildUtils.getInternalType( ifld.getTypeName() ) );
            setTargetValue( mv, core, ifld );

            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 2);
            mv.visitEnd();
        }

        {
            mv = cw.visitMethod( ACC_PUBLIC,
                    setter+"Distr",
                    "(Lorg/drools/chance/distribution/Distribution;)V",
                    "(Lorg/drools/chance/distribution/Distribution<" +BuildUtils.getTypeDescriptor( ifld.getTypeName() ) +">;)V",
                    null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                    BuildUtils.getInternalType( proxyName ),
                    ifld.getName()+"_$$Imp",
                    "Lorg/drools/chance/common/ImperfectField;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_0);
            mv.visitMethodInsn( INVOKEINTERFACE,
                    "org/drools/chance/common/ImperfectField",
                    "setValue",
                    "(Lorg/drools/chance/distribution/Distribution;Z)V");


            mv.visitVarInsn(ALOAD, 0);
            prepareSetTargetValue( mv, proxyName, core );
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                    BuildUtils.getInternalType( proxyName ),
                    ifld.getName() + "_$$Imp",
                    "Lorg/drools/chance/common/ImperfectField;");
            mv.visitMethodInsn( INVOKEINTERFACE,
                    "org/drools/chance/common/ImperfectField",
                    "getCrisp",
                    "()Ljava/lang/Object;");
            mv.visitTypeInsn(CHECKCAST, BuildUtils.getInternalType( ifld.getTypeName() ) );
            setTargetValue( mv, core, ifld );
            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 2);
            mv.visitEnd();
        }

        {
            mv = cw.visitMethod( ACC_PUBLIC,
                    setter.replace("set","update")+"Distr",
                    "(Lorg/drools/chance/distribution/Distribution;)V",
                    "(Lorg/drools/chance/distribution/Distribution<" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) + ">;)V",
                    null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                    BuildUtils.getInternalType( proxyName ),
                    ifld.getName()+"_$$Imp",
                    "Lorg/drools/chance/common/ImperfectField;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn( INVOKEINTERFACE,
                    "org/drools/chance/common/ImperfectField",
                    "update",
                    "(Lorg/drools/chance/distribution/Distribution;)V");

            mv.visitVarInsn(ALOAD, 0);
            prepareSetTargetValue( mv, proxyName, core );
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                    BuildUtils.getInternalType( proxyName ),
                    ifld.getName()+"_$$Imp",
                    "Lorg/drools/chance/common/ImperfectField;");
            mv.visitMethodInsn( INVOKEINTERFACE,
                    "org/drools/chance/common/ImperfectField",
                    "getCrisp",
                    "()Ljava/lang/Object;");
            mv.visitTypeInsn(CHECKCAST, BuildUtils.getInternalType( ifld.getTypeName() ) );
            setTargetValue( mv, core, ifld );

            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }

    }


    protected void buildImperfectLinguisticFieldGettersAndSetters(ClassWriter cw, String proxyName, String coreName, ImperfectFieldDefinition ifld, FieldDefinition tfld) {

        String getter = BuildUtils.getterName( ifld.getName(), ifld.getTypeName() );
        String setter = BuildUtils.setterName( ifld.getName(), ifld.getTypeName() );

        String targetGetter = BuildUtils.getterName( tfld.getName(), tfld.getTypeName() );
        String targetSetter = BuildUtils.setterName( tfld.getName(), tfld.getTypeName() );
        String targetType = tfld.getTypeName();


        // first build the fuzzy field

        {
            MethodVisitor mv = cw.visitMethod( ACC_PUBLIC,
                    getter,
                    "()Lorg/drools/chance/common/ImperfectField;",
                    "()Lorg/drools/chance/common/ImperfectField<" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) + ">;",
                    null);
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitFieldInsn( GETFIELD,
                    BuildUtils.getInternalType( proxyName ) ,
                    ifld.getName() + "_$$Imp",
                    "Lorg/drools/chance/common/ImperfectField;");
            mv.visitInsn( ARETURN );
            mv.visitMaxs( 1, 1 );
            mv.visitEnd();
        }



        {
            MethodVisitor mv = cw.visitMethod( ACC_PUBLIC,
                    getter+"Distr",
                    "()Lorg/drools/chance/distribution/Distribution;",
                    "()Lorg/drools/chance/distribution/Distribution<" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) + ">;",
                    null);
            mv.visitCode();
            mv.visitVarInsn( ALOAD, 0 );
            mv.visitFieldInsn( GETFIELD,
                    BuildUtils.getInternalType( proxyName ) ,
                    ifld.getName() + "_$$Imp",
                    "Lorg/drools/chance/common/ImperfectField;");
            mv.visitMethodInsn( INVOKEINTERFACE,
                    "org/drools/chance/common/ImperfectField",
                    "getCurrent",
                    "()Lorg/drools/chance/distribution/Distribution;");
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
            getTargetValue( mv, ifld, proxyName, coreName );
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }




        {
            MethodVisitor mv = cw.visitMethod( ACC_PUBLIC,
                    setter,
                    "(Lorg/drools/chance/common/ImperfectField;)V",
                    "(Lorg/drools/chance/common/ImperfectField<" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) + ">;)V",
                    null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn( PUTFIELD,
                    BuildUtils.getInternalType( proxyName ) ,
                    ifld.getName() + "_$$Imp",
                    "Lorg/drools/chance/common/ImperfectField;");


            mv.visitVarInsn(ALOAD, 0);
            prepareSetTargetValue( mv, proxyName, coreName );
            mv.visitVarInsn( ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                    BuildUtils.getInternalType( proxyName ),
                    ifld.getName() + "_$$Imp",
                    "Lorg/drools/chance/common/ImperfectField;");
            mv.visitMethodInsn( INVOKEINTERFACE,
                    "org/drools/chance/common/ImperfectField",
                    "getCrisp",
                    "()Ljava/lang/Object;");
            mv.visitTypeInsn( CHECKCAST, BuildUtils.getInternalType( ifld.getTypeName() ) );
            setTargetValue( mv, coreName, ifld );


            mv.visitVarInsn(ALOAD, 0);
            prepareSetTargetValue( mv, proxyName, coreName );
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                    BuildUtils.getInternalType( proxyName ),
                    ifld.getName() + "_$$Imp",
                    "Lorg/drools/chance/common/ImperfectField;");
            mv.visitTypeInsn(CHECKCAST, "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField");

            defuzzify( mv, targetType );


            setTargetValue( mv, coreName, tfld );
            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 2);
            mv.visitEnd();
        }


        {
            MethodVisitor mv = cw.visitMethod( ACC_PUBLIC,
                    setter+"Distr",
                    "(Lorg/drools/chance/distribution/Distribution;)V",
                    "(Lorg/drools/chance/distribution/Distribution<" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) + ">;)V",
                    null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                    BuildUtils.getInternalType( proxyName ) ,
                    ifld.getName() + "_$$Imp",
                    "Lorg/drools/chance/common/ImperfectField;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_0);
            mv.visitMethodInsn( INVOKEINTERFACE,
                    "org/drools/chance/common/ImperfectField",
                    "setValue",
                    "(Lorg/drools/chance/distribution/Distribution;Z)V");



            mv.visitVarInsn(ALOAD, 0);
            prepareSetTargetValue( mv, proxyName, coreName );
            mv.visitVarInsn( ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                    BuildUtils.getInternalType( proxyName ),
                    ifld.getName() + "_$$Imp",
                    "Lorg/drools/chance/common/ImperfectField;");
            mv.visitMethodInsn( INVOKEINTERFACE,
                    "org/drools/chance/common/ImperfectField",
                    "getCrisp",
                    "()Ljava/lang/Object;");
            mv.visitTypeInsn( CHECKCAST, BuildUtils.getInternalType( ifld.getTypeName() ) );
            setTargetValue( mv, coreName, ifld );


            mv.visitVarInsn(ALOAD, 0);
            prepareSetTargetValue( mv, proxyName, coreName );
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                    BuildUtils.getInternalType( proxyName ),
                    ifld.getName() + "_$$Imp",
                    "Lorg/drools/chance/common/ImperfectField;");
            mv.visitTypeInsn(CHECKCAST, "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField");

            defuzzify( mv, targetType );

            setTargetValue( mv, coreName, tfld );
            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 2);
            mv.visitEnd();
        }


        {
            MethodVisitor mv = cw.visitMethod( ACC_PUBLIC,
                    setter + "Value",
                    "(" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) + ")V",
                    null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            prepareSetTargetValue( mv, proxyName, coreName );
            mv.visitVarInsn(ALOAD, 1);
            setTargetValue( mv, coreName, ifld );
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                    BuildUtils.getInternalType( proxyName ),
                    ifld.getName() + "_$$Imp",
                    "Lorg/drools/chance/common/ImperfectField;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_0);
            mv.visitMethodInsn(INVOKEINTERFACE,
                    "org/drools/chance/common/ImperfectField",
                    "setValue",
                    "(Ljava/lang/Object;Z)V");

            mv.visitVarInsn(ALOAD, 0);
            prepareSetTargetValue( mv, proxyName, coreName );
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                    BuildUtils.getInternalType( proxyName ) ,
                    ifld.getName() + "_$$Imp",
                    "Lorg/drools/chance/common/ImperfectField;");
            mv.visitTypeInsn(CHECKCAST, "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField");

            defuzzify( mv, targetType );

            setTargetValue( mv, coreName, tfld );
            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 2);
            mv.visitEnd();
        }

        {
            MethodVisitor mv = cw.visitMethod( ACC_PUBLIC,
                    setter + "Core",
                    "(" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) + ")V",
                    null, null);
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 0);
            prepareSetTargetValue( mv, proxyName, coreName );
            mv.visitVarInsn(ALOAD, 1);
            setTargetValue( mv, coreName, ifld );

            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 2);
            mv.visitEnd();
        }






        {
            MethodVisitor mv = cw.visitMethod( ACC_PUBLIC,
                    setter.replace("set","update"),
                    "(Lorg/drools/chance/common/ImperfectField;)V",
                    "(Lorg/drools/chance/common/ImperfectField<" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) + ">;)V",
                    null);

            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                    BuildUtils.getInternalType( proxyName ),
                    ifld.getName() + "_$$Imp",
                    "Lorg/drools/chance/common/ImperfectField;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn( INVOKEINTERFACE,
                    "org/drools/chance/common/ImperfectField",
                    "getCurrent",
                    "()Lorg/drools/chance/distribution/Distribution;");
            mv.visitMethodInsn( INVOKEINTERFACE,
                    "org/drools/chance/common/ImperfectField",
                    "update",
                    "(Lorg/drools/chance/distribution/Distribution;)V");


            mv.visitVarInsn(ALOAD, 0);
            prepareSetTargetValue( mv, proxyName, coreName );
            mv.visitVarInsn( ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                    BuildUtils.getInternalType( proxyName ),
                    ifld.getName() + "_$$Imp",
                    "Lorg/drools/chance/common/ImperfectField;");
            mv.visitMethodInsn( INVOKEINTERFACE,
                    "org/drools/chance/common/ImperfectField",
                    "getCrisp",
                    "()Ljava/lang/Object;");
            mv.visitTypeInsn( CHECKCAST, BuildUtils.getInternalType( ifld.getTypeName() ) );
            setTargetValue( mv, coreName, ifld );

            mv.visitVarInsn(ALOAD, 0);
            prepareSetTargetValue( mv, proxyName, coreName );
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                    BuildUtils.getInternalType( proxyName ),
                    ifld.getName() + "_$$Imp",
                    "Lorg/drools/chance/common/ImperfectField;");
            mv.visitTypeInsn(CHECKCAST, "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField");

            defuzzify( mv, targetType );

            setTargetValue( mv, coreName, tfld );
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }


        {
            MethodVisitor mv = cw.visitMethod( ACC_PUBLIC,
                    setter.replace("set","update") + "Distr",
                    "(Lorg/drools/chance/distribution/Distribution;)V",
                    "(Lorg/drools/chance/distribution/Distribution<" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) + ">;)V",
                    null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                    BuildUtils.getInternalType( proxyName ),
                    ifld.getName() + "_$$Imp",
                    "Lorg/drools/chance/common/ImperfectField;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn( INVOKEINTERFACE,
                    "org/drools/chance/common/ImperfectField",
                    "update",
                    "(Lorg/drools/chance/distribution/Distribution;)V");


            mv.visitVarInsn(ALOAD, 0);
            prepareSetTargetValue( mv, proxyName, coreName );
            mv.visitVarInsn( ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                    BuildUtils.getInternalType( proxyName ),
                    ifld.getName() + "_$$Imp",
                    "Lorg/drools/chance/common/ImperfectField;");
            mv.visitMethodInsn( INVOKEINTERFACE,
                    "org/drools/chance/common/ImperfectField",
                    "getCrisp",
                    "()Ljava/lang/Object;");
            mv.visitTypeInsn( CHECKCAST, BuildUtils.getInternalType( ifld.getTypeName() ) );
            setTargetValue( mv, coreName, ifld );

            mv.visitVarInsn(ALOAD, 0);
            prepareSetTargetValue( mv, proxyName, coreName );
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                    BuildUtils.getInternalType( proxyName ),
                    ifld.getName() + "_$$Imp",
                    "Lorg/drools/chance/common/ImperfectField;");
            mv.visitTypeInsn(CHECKCAST, "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField");

            defuzzify( mv, targetType );

            setTargetValue( mv, coreName, tfld );
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }


        {
            MethodVisitor mv = cw.visitMethod( ACC_PUBLIC,
                    setter.replace( "set", "update" ) + "Value",
                    "(" + BuildUtils.getTypeDescriptor( ifld.getTypeName() ) + ")V",
                    null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            prepareSetTargetValue( mv, proxyName, coreName );
            mv.visitVarInsn(ALOAD, 1);
            setTargetValue( mv, coreName, ifld );
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                    BuildUtils.getInternalType( proxyName ),
                    ifld.getName() + "_$$Imp",
                    "Lorg/drools/chance/common/ImperfectField;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_1);
            mv.visitMethodInsn( INVOKEINTERFACE,
                    "org/drools/chance/common/ImperfectField",
                    "setValue",
                    "(Ljava/lang/Object;Z)V");
            mv.visitVarInsn(ALOAD, 0);
            prepareSetTargetValue( mv, proxyName, coreName );
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                    BuildUtils.getInternalType( proxyName ),
                    ifld.getName() + "_$$Imp",
                    "Lorg/drools/chance/common/ImperfectField;");
            mv.visitTypeInsn( CHECKCAST, "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField" );

            defuzzify( mv, targetType );

            setTargetValue( mv, coreName, tfld );
            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 2);
            mv.visitEnd();
        }







        // Now build for the target support field

        {
            MethodVisitor mv = cw.visitMethod( ACC_PUBLIC,
                    targetGetter,
                    "()" + BuildUtils.getTypeDescriptor( targetType ) + "", null, null);
            mv.visitCode();
            getTargetValue( mv, tfld, proxyName, coreName );
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
                    BuildUtils.getInternalType( proxyName ),
                    ifld.getName() + "_$$Imp",
                    "Lorg/drools/chance/common/ImperfectField;");
            mv.visitTypeInsn( CHECKCAST, "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn( INVOKEVIRTUAL,
                    "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField",
                    "fuzzify",
                    "(Ljava/lang/Number;)Lorg/drools/chance/distribution/Distribution;");
            mv.visitVarInsn(ASTORE, 2);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                    BuildUtils.getInternalType( proxyName ),
                    ifld.getName() + "_$$Imp",
                    "Lorg/drools/chance/common/ImperfectField;");
            mv.visitVarInsn(ALOAD, 2);
            mv.visitInsn(ICONST_0);
            mv.visitMethodInsn( INVOKEINTERFACE,
                    "org/drools/chance/common/ImperfectField",
                    "setValue",
                    "(Lorg/drools/chance/distribution/Distribution;Z)V");
            mv.visitVarInsn(ALOAD, 0);
            prepareSetTargetValue( mv, proxyName, coreName );
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn( GETFIELD,
                    BuildUtils.getInternalType( proxyName ),
                    ifld.getName() + "_$$Imp",
                    "Lorg/drools/chance/common/ImperfectField;");
            mv.visitMethodInsn( INVOKEINTERFACE,
                    "org/drools/chance/common/ImperfectField",
                    "getCrisp",
                    "()Ljava/lang/Object;");
            mv.visitTypeInsn( CHECKCAST, BuildUtils.getInternalType( ifld.getTypeName() ));
            setTargetValue( mv, coreName, ifld );
            mv.visitVarInsn(ALOAD, 0);
            prepareSetTargetValue( mv, proxyName, coreName );
            mv.visitVarInsn(ALOAD, 1);
            setTargetValue( mv, coreName, tfld );
            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 3);
            mv.visitEnd();
        }

        {
            MethodVisitor mv = cw.visitMethod( ACC_PUBLIC,
                    targetSetter + "Core",
                    "(" + BuildUtils.getTypeDescriptor( targetType ) + ")V",
                    null, null);
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 0);
            prepareSetTargetValue( mv, proxyName, coreName );
            mv.visitVarInsn(ALOAD, 1);
            setTargetValue( mv, coreName, tfld );


            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 2);
            mv.visitEnd();
        }
    }




    /*******************************************************************************************************************
     *
     * Utilities
     *
     *******************************************************************************************************************/






    private void defuzzify( MethodVisitor mv, String targetType ) {
        mv.visitMethodInsn( INVOKEVIRTUAL,
                "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField",
                "defuzzify",
                "()Ljava/lang/Number;");

        mv.visitTypeInsn(CHECKCAST, "java/lang/Number" );
        mv.visitMethodInsn( INVOKEVIRTUAL,
                "java/lang/Number",
                BuildUtils.numericMorph( targetType ),
                "()" + BuildUtils.unBox( targetType ) );


        if ( ! BuildUtils.isPrimitive( targetType ) ) {
            mv.visitMethodInsn(INVOKESTATIC,
                    BuildUtils.getInternalType( targetType ),
                    "valueOf",
                    "(" + BuildUtils.unBox( targetType )+ ")" + BuildUtils.getTypeDescriptor( targetType ) );

        }

    }

    private boolean isSupport(ClassDefinition classDef, FieldDefinition field) {
        for ( FieldDefinition fld : classDef.getFieldsDefinitions() ) {
            if ( fld instanceof ImperfectFieldDefinition &&
                    ImperfectFieldDefinition.isLinguistic( fld ) &&
                    ((ImperfectFieldDefinition) fld).getSupportFieldDef().equals( field ) ) {
                return true;
            }
        }
        return false;
    }


    protected FieldDefinition getSupportField(ClassDefinition cdef, ImperfectFieldDefinition ifld) {
        String target = ifld.getSupport();
        FieldDefinition tfld = cdef.getField( target );
        if ( target == null || tfld == null ) {
            throw new RuntimeDroolsException( " Fuzzy Linguistic Field " + ifld.getName() + " requires a support field, not found " + target );
        }
        return  tfld;
    }


    protected void setTargetValue(MethodVisitor mv, String coreName, FieldDefinition field ) {
        mv.visitFieldInsn( PUTFIELD,
                BuildUtils.getInternalType( coreName ),
                field.getName(),
                BuildUtils.getTypeDescriptor( field.getTypeName() ) );
    }


    protected void prepareSetTargetValue(MethodVisitor mv, String proxyName, String coreName ) {

    }

    protected void getTargetValue( MethodVisitor mv, FieldDefinition field, String proxyName, String coreName ) {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn( GETFIELD,
                BuildUtils.getInternalType( coreName ),
                field.getName(),
                BuildUtils.getTypeDescriptor( field.getTypeName() ) );
    }

    private void createImperfectField( MethodVisitor mv, String ikind, String itype, String degree, String targetType ) {
        mv.visitFieldInsn(GETSTATIC,
                "org/drools/chance/distribution/ImpKind",
                ikind,
                "Lorg/drools/chance/distribution/ImpKind;");
        mv.visitFieldInsn(GETSTATIC,
                "org/drools/chance/distribution/ImpType",
                itype,
                "Lorg/drools/chance/distribution/ImpType;");
        mv.visitFieldInsn(GETSTATIC,
                "org/drools/chance/degree/DegreeType",
                degree,
                "Lorg/drools/chance/degree/DegreeType;");
        mv.visitLdcInsn( Type.getType( BuildUtils.getTypeDescriptor( targetType ) ) );
        mv.visitMethodInsn( INVOKESTATIC,
                "org/drools/chance/common/ChanceStrategyFactory",
                "buildStrategies",
                "(Lorg/drools/chance/distribution/ImpKind;" +
                        "Lorg/drools/chance/distribution/ImpType;" +
                        "Lorg/drools/chance/degree/DegreeType;" +
                        "Ljava/lang/Class;)" +
                        "Lorg/drools/chance/distribution/DistributionStrategies;");

    }

}
