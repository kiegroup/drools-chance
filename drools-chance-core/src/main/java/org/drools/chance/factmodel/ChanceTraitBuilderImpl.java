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

package org.drools.chance.factmodel;

import org.drools.chance.common.ImperfectField;
import org.drools.chance.distribution.Distribution;
import org.drools.factmodel.AnnotationDefinition;
import org.drools.factmodel.BuildUtils;
import org.drools.factmodel.ClassDefinition;
import org.drools.factmodel.FieldDefinition;
import org.drools.factmodel.traits.TraitClassBuilderImpl;
import org.mvel2.asm.ClassWriter;
import org.mvel2.asm.MethodVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class ChanceTraitBuilderImpl extends TraitClassBuilderImpl {



    public void init(ClassDefinition trait) {
        rewriteImperfectFields( trait );
        super.init(trait);
    }



    protected void rewriteImperfectFields(ClassDefinition classDef) {
        Collection<FieldDefinition> originalFields = new ArrayList<FieldDefinition>( classDef.getFieldsDefinitions() );

        for ( FieldDefinition fld : originalFields ) {
            if ( ! ( fld instanceof ImperfectFieldDefinition) && fld.getAnnotations() != null ) {
                for ( AnnotationDefinition ann : fld.getAnnotations() ) {
                    if ( ann.getName().equals( Imperfect.class.getName() ) ) {
                        ImperfectFieldDefinition ifld = ImperfectFieldDefinition.fromField( fld, ann );
                        ifld.setClassDefinition( classDef );
                        classDef.addField( ifld );

                        if ( ImperfectFieldDefinition.isLinguistic( ifld ) ) {
                            for ( FieldDefinition xfld : originalFields ) {
                                if ( xfld.getName().equals( ifld.getSupport() ) ) {
                                    ifld.setSupportFieldDef( xfld );
                                }
                            }
                        }

                        break;
                    }
                }
            }

        }
    }

    protected void buildSetter(ClassWriter cw, FieldDefinition field, String name, String type, String generic) {

        if ( field instanceof ImperfectFieldDefinition ) {
            super.buildSetter( cw, null, name, ImperfectField.class.getName(), type );

            if ( ImperfectFieldDefinition.isLinguistic( field ) ) {
                ImperfectFieldDefinition ifld = (ImperfectFieldDefinition) field;
                super.buildSetter( cw, null, ifld.getSupport()+"Core", ifld.getSupportFieldDef().getTypeName(), null );
            }
        } else {
            super.buildSetter( cw, null, name, type, null );
        }

        super.buildSetter( cw, null, name+"Distr", Distribution.class.getName(), type );
        super.buildSetter( cw, null, name+"Value", type, null );
//        super.buildSetter( cw, field, name+"Core", type, null );

        buildUpdater( cw, field, name+"Distr", Distribution.class.getName(), type );
        buildUpdater( cw, field, name+"Value", type, null );

    }


    protected void buildGetter(ClassWriter cw, FieldDefinition field, String name, String type, String generic) {

        if ( field instanceof ImperfectFieldDefinition ) {
            super.buildGetter(cw, null, name, ImperfectField.class.getName(), type);

            if ( ImperfectFieldDefinition.isLinguistic( field ) ) {
                ImperfectFieldDefinition ifld = (ImperfectFieldDefinition) field;
                super.buildGetter( cw, null, ifld.getSupport()+"Core", ifld.getSupportFieldDef().getTypeName(), null );
            }
        } else {
            super.buildGetter(cw, null, name, type, null);
        }

        super.buildGetter( cw, null, name+"Distr", Distribution.class.getName(), type );
        super.buildGetter( cw, null, name+"Value", type, null );
//        super.buildGetter( cw, field, name+"Core", type, null );


    }

    protected void buildUpdater(ClassWriter cw, FieldDefinition field, String name, String type, String generic) {

        MethodVisitor mv = cw.visitMethod( ACC_PUBLIC + ACC_ABSTRACT,
                "update" + name,
                "(" + BuildUtils.getTypeDescriptor( type ) + ")V",
                generic == null ? null :
                        "(" + BuildUtils.getTypeDescriptor( type ).replace( ";", "<" + BuildUtils.getTypeDescriptor( generic ) + ">;") + ")V",
                null );
        mv.visitEnd();

    }



    protected void finalizeCreation(ClassDefinition trait) {

        Collection<FieldDefinition> originalFields = new HashSet<FieldDefinition>( trait.getFieldsDefinitions() );

        for ( FieldDefinition field : originalFields ) {
            if ( field instanceof ImperfectFieldDefinition ) {

                ImperfectFieldDefinition ifld = (ImperfectFieldDefinition) field;

                FieldDefinition fieldDistr = new VirtualFieldDefinition();
                fieldDistr.setName( field.getName() + "Distr" );
                fieldDistr.setTypeName( Distribution.class.getName() );
                fieldDistr.setInherited( field.isInherited() );

                trait.addField( fieldDistr );

                FieldDefinition fieldValue = new VirtualFieldDefinition();
                fieldValue.setName( field.getName() + "Value" );
                fieldValue.setTypeName( field.getTypeName() );
                fieldValue.setInherited( field.isInherited() );

                trait.addField(fieldValue);

                if ( ImperfectFieldDefinition.isLinguistic( ifld ) ) {
                    FieldDefinition support = null;
                    for ( FieldDefinition x : originalFields ) {
                        if ( x.getName().equals( ifld.getSupport() ) ) {
                            support = x;
                        }
                    }
                    if ( support != null ) {
                        FieldDefinition fieldCore = new DirectAccessFieldDefinition( support );
                        fieldCore.setName( support.getName() + "Core" );
                        fieldCore.setTypeName( support.getTypeName() );
                        fieldCore.setInherited( support.isInherited() );

                        trait.addField(fieldCore);
                    } else {
                        throw new IllegalStateException("Could not find the support field " + ifld.getSupport() + " for the Linguistic Imperfect field " + field.getName() );
                    }
                }
            }
        }

    }



}
