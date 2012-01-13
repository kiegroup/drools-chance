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
import org.drools.factmodel.BuildUtils;
import org.drools.factmodel.ClassDefinition;
import org.drools.factmodel.FieldDefinition;
import org.drools.factmodel.traits.TraitRegistry;
import org.drools.factmodel.traits.TraitTriplePropertyWrapperClassBuilderImpl;
import org.mvel2.asm.Label;
import org.mvel2.asm.MethodVisitor;
import org.mvel2.asm.Type;

public class ChanceTriplePropertyWrapperClassBuilderImpl extends TraitTriplePropertyWrapperClassBuilderImpl {



    protected boolean isVirtual( FieldDefinition field ) {
        return field instanceof VirtualFieldDefinition;
    }


    protected boolean isDirectAccess( FieldDefinition field ) {
        return field instanceof DirectAccessFieldDefinition;
    }

    protected boolean mustSkip( FieldDefinition field ) {
        return isVirtual( field ) || isDirectAccess( field );
    }





    protected int initSoftFields( MethodVisitor mv, String wrapperName, ClassDefinition trait, long mask ) {
        int stackSize = super.initSoftFields( mv, wrapperName, trait, mask );

        for ( FieldDefinition field : trait.getFieldsDefinitions() ) {
            if ( isVirtual( field ) ) continue;
            if ( field instanceof ImperfectFieldDefinition ) {
                ImperfectFieldDefinition ifld = (ImperfectFieldDefinition) field;

                if ( ImperfectFieldDefinition.isLinguistic(ifld) ) {

                    FieldDefinition tfld = getSupportField( trait, ifld );
                    initImperfectLinguisticField( mv, wrapperName, ifld, tfld );
                    stackSize += 2;

                } else {
                    
                    initImperfectField( mv, wrapperName, (ImperfectFieldDefinition) field);

                }

            } else if ( field instanceof DirectAccessFieldDefinition ) {

                System.out.println("Should I init core? " + field.getName());
            }
        }
        return stackSize;
    }





    protected FieldDefinition getSupportField( ClassDefinition cdef, ImperfectFieldDefinition ifld ) {
        String target = ifld.getSupport();
        FieldDefinition tfld = cdef.getField( target );
        if ( target == null || tfld == null ) {
            throw new RuntimeDroolsException( " Fuzzy Linguistic Field " + ifld.getName() + " requires a support field, not found " + target );
        }
        return  tfld;
    }




    protected void initImperfectField(MethodVisitor mv, String wrapperName, ImperfectFieldDefinition impField ) {

        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD,
                wrapperName,
                "store",
                "Lorg/drools/core/util/TripleStore;");
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitLdcInsn( impField.getName() +"_$$Imp" );

        mv.visitMethodInsn( INVOKEVIRTUAL,
                wrapperName,
                "propertyKey",
                "(Ljava/lang/Object;)Lorg/drools/core/util/TripleImpl;");
        mv.visitMethodInsn( INVOKEVIRTUAL,
                "org/drools/core/util/TripleStore",
                "contains",
                "(Lorg/drools/core/util/Triple;)Z" );
        Label l0 = new Label();
        mv.visitJumpInsn( IFNE, l0 );



        mv.visitTypeInsn( NEW, "org/drools/chance/common/ImperfectField" );
        mv.visitInsn(DUP);
        mv.visitLdcInsn( impField.getImpKind() );
        mv.visitLdcInsn( impField.getImpType() );
        mv.visitLdcInsn( impField.getDegreeType() );
        mv.visitLdcInsn( Type.getType( BuildUtils.getTypeDescriptor( impField.getTypeName() ) ) );
        mv.visitMethodInsn( INVOKESTATIC,
                "org/drools/chance/common/ChanceStrategyFactory",
                "buildStrategies",
                "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Class;)Lorg/drools/chance/distribution/IDistributionStrategies;" );
        mv.visitMethodInsn( INVOKESPECIAL,
                "org/drools/chance/common/ImperfectField",
                "<init>",
                "(Lorg/drools/chance/distribution/IDistributionStrategies;)V" );
        mv.visitVarInsn(ASTORE, 1);



        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD,
                wrapperName,
                "store",
                "Lorg/drools/core/util/TripleStore;" );
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitLdcInsn( impField.getName() +"_$$Imp" );
        mv.visitVarInsn( ALOAD, 1 );
        mv.visitMethodInsn( INVOKEVIRTUAL,
                wrapperName,
                "property",
                "(Ljava/lang/String;Ljava/lang/Object;)Lorg/drools/core/util/TripleImpl;" );
        mv.visitMethodInsn( INVOKEVIRTUAL,
                "org/drools/core/util/TripleStore",
                "put",
                "(Lorg/drools/core/util/Triple;)Z" );
        mv.visitInsn( POP );

        mv.visitLabel( l0 );

    }







    protected void initImperfectLinguisticField( MethodVisitor mv, String wrapperName, ImperfectFieldDefinition impField, FieldDefinition tfld ) {

        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn( GETFIELD,
                wrapperName,
                "store",
                "Lorg/drools/core/util/TripleStore;");
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitLdcInsn( impField.getName() +"_$$Imp" );

        mv.visitMethodInsn( INVOKEVIRTUAL,
                wrapperName,
                "propertyKey",
                "(Ljava/lang/Object;)Lorg/drools/core/util/TripleImpl;" );
        mv.visitMethodInsn( INVOKEVIRTUAL,
                "org/drools/core/util/TripleStore",
                "contains",
                "(Lorg/drools/core/util/Triple;)Z");
        Label l0 = new Label();
        mv.visitJumpInsn( IFNE, l0 );



        mv.visitVarInsn( ALOAD, 0 );
        mv.visitFieldInsn( GETFIELD,
                wrapperName,
                "store",
                "Lorg/drools/core/util/TripleStore;" );
        mv.visitVarInsn( ALOAD, 0 );
        mv.visitLdcInsn( impField.getName() +"_$$Imp" );



        mv.visitTypeInsn( NEW, "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField" );
        mv.visitInsn( DUP );

        mv.visitLdcInsn( impField.getImpKind() );
        mv.visitLdcInsn( impField.getImpType() );
        mv.visitLdcInsn( impField.getDegreeType() );
        mv.visitLdcInsn( Type.getType( BuildUtils.getTypeDescriptor( impField.getTypeName() ) ) );
        mv.visitMethodInsn( INVOKESTATIC,
                "org/drools/chance/common/ChanceStrategyFactory",
                "buildStrategies",
                "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Class;)Lorg/drools/chance/distribution/IDistributionStrategies;");

        mv.visitLdcInsn( "possibility" );
        mv.visitLdcInsn( "linguistic" );
        mv.visitLdcInsn( impField.getDegreeType() );
        mv.visitLdcInsn( Type.getType( BuildUtils.getTypeDescriptor( tfld.getTypeName() ) ) );
        mv.visitMethodInsn( INVOKESTATIC,
                "org/drools/chance/common/ChanceStrategyFactory",
                "buildStrategies",
                "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Class;)Lorg/drools/chance/distribution/IDistributionStrategies;");


//        mv.visitLdcInsn( ""+impField.getHistory() );
//        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(Ljava/lang/String;)Ljava/lang/Integer;");
//        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I");

        mv.visitInsn( ACONST_NULL );


        mv.visitMethodInsn( INVOKESPECIAL,
                "org/drools/chance/distribution/fuzzy/linguistic/LinguisticImperfectField",
                "<init>",
                "(Lorg/drools/chance/distribution/IDistributionStrategies;Lorg/drools/chance/distribution/IDistributionStrategies;Ljava/lang/String;)V" );

        mv.visitMethodInsn( INVOKEVIRTUAL,
                wrapperName,
                "property",
                "(Ljava/lang/String;Ljava/lang/Object;)Lorg/drools/core/util/TripleImpl;");
        mv.visitMethodInsn( INVOKEVIRTUAL,
                "org/drools/core/util/TripleStore",
                "put",
                "(Lorg/drools/core/util/Triple;)Z" );
        mv.visitInsn( POP );

        mv.visitLabel( l0 );

    }





}
