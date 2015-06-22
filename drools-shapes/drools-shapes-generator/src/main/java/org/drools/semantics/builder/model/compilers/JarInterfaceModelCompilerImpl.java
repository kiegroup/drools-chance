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

package org.drools.semantics.builder.model.compilers;


import com.clarkparsia.empire.SupportsRdfId;
import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;
import org.drools.core.factmodel.BuildUtils;
import org.drools.core.factmodel.traits.Trait;
import org.drools.semantics.Thing;
import org.drools.semantics.builder.model.CompiledOntoModel;
import org.drools.semantics.builder.model.Concept;
import org.drools.semantics.builder.model.JarModel;
import org.drools.semantics.builder.model.JarModelImpl;
import org.drools.semantics.builder.model.ModelFactory;
import org.drools.semantics.builder.model.OntoModel;
import org.drools.semantics.builder.model.PropertyRelation;
import org.drools.semantics.utils.NameUtils;
import org.drools.semantics.utils.NamespaceUtils;
import org.kie.api.definition.type.PropertyReactive;
import org.mvel2.asm.AnnotationVisitor;
import org.mvel2.asm.ClassWriter;
import org.mvel2.asm.FieldVisitor;
import org.mvel2.asm.MethodVisitor;
import org.mvel2.asm.Opcodes;
import org.mvel2.asm.Type;
import thewebsemantic.Namespace;
import thewebsemantic.RdfType;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JarInterfaceModelCompilerImpl extends JavaInterfaceModelCompilerImpl implements Opcodes, JarInterfaceModelCompiler {


    public void setModel(OntoModel model) {

        this.model = (CompiledOntoModel) ModelFactory.newModel( ModelFactory.CompileTarget.JAR, model );

    }

    public void compile( Concept con, Object context, Map<String, Object> params ) {

        if ( "Thing".equals( con.getName() ) && NamespaceUtils.compareNamespaces("http://www.w3.org/2002/07/owl", con.getNamespace()) ) {
            return;
        }

       super.compile( con, context, params );
        String name = con.getFullyQualifiedName();

        ((JarModel) getModel()).addCompiledTrait( name, this.compile( name, params ) );

    }


    private JarModelImpl.Holder compile( String trait, Map<String, Object> params ) {
        return compileInterface( trait, params );
    }


    private JarModelImpl.Holder compileInterface( String trait, Map<String, Object> params ) {

        ClassWriter cw = new ClassWriter( ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_FRAMES );
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;

        Boolean standalone = (Boolean) params.get( "standalone" );
        Boolean minimal = (Boolean) params.get( "minimal" );
        Set<Concept> sup = ( (Set<Concept>) params.get( "superConcepts" ) );
        String implInterface = (String) params.get( "implInterface" );
        int N = sup.size()
                + 1
                + ( implInterface != null ? 1 : 0 );
        String[] superTypes = new String[ N ];
        superTypes[0] = Type.getInternalName( SupportsRdfId.class );

        int j = 1;
        if ( implInterface != null ) {
            superTypes[ j++ ] = implInterface.replace( ".", "/" );
        }
        for ( Iterator<Concept> iter = sup.iterator(); iter.hasNext(); ) {
            String iface = iter.next().getFullyQualifiedName();
            if ( ! standalone || ! Thing.class.getName().equals( iface ) ) {
                superTypes[ j ] = iface.replace( ".", "/" );
            }
            j++;
        }


        Map<String, PropertyRelation> props = (Map<String, PropertyRelation>) params.get( "properties" );

        cw.visit(V1_5, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE,
                trait.replace( ".", "/" ),
                null,
                Type.getInternalName( Object.class ),
                superTypes);

        {
            av0 = cw.visitAnnotation( Type.getDescriptor( RdfsClass.class ), true);
            av0.visit( "value", "tns:" + params.get( "name" ) );
            av0.visitEnd();
        }
        {
            av0 = cw.visitAnnotation( Type.getDescriptor( Namespaces.class ), true);
            {
                //TODO : This does not work!! 
                AnnotationVisitor av1 = av0.visitArray( "value" );
                av1.visit( null, "tns" );
                av1.visit( null, params.get( "namespace" ) );
                av1.visitEnd();
            }
            av0.visitEnd();
        }
        {
            av0 = cw.visitAnnotation( Type.getDescriptor( Namespace.class ), true);
            av0.visit( "value", params.get( "namespace" ) );
        }
        {
            av0 = cw.visitAnnotation( Type.getDescriptor( RdfType.class ), true);
            av0.visit( "value", "" + params.get( "name" ) + params.get( "namespace" ) );
        }
        {
            av0 = cw.visitAnnotation( Type.getDescriptor( Trait.class ), true);
        }
        {
            av0 = cw.visitAnnotation( Type.getDescriptor( PropertyReactive.class ), true);
        }

        for ( String propKey : props.keySet() ) {
            PropertyRelation rel = props.get( propKey );
            String propName = rel.getName();
            propName = propName.substring( 0, 1 ).toUpperCase() + propName.substring( 1 );
            String target = rel.getTarget().getFullyQualifiedName();

            boolean isBoolean = target.equalsIgnoreCase( "xsd:boolean" );
            if ( target.startsWith( "xsd:" ) ) {
                target = NameUtils.map( target, rel.getMaxCard() == null || rel.getMaxCard() != 1 );
            } else {
                target = target.replace( ".", "/" );
            }

            String propType = BuildUtils.getTypeDescriptor( target );
            String genericGetType = null;
            String genericSetType = null;
            if ( rel.getMaxCard() == null || rel.getMaxCard() != 1  ) {
                genericGetType = "()Ljava/util/List<" + propType + ">;";
                genericSetType = "(Ljava/util/List<" + propType + ">;)V";
                propType = Type.getDescriptor( List.class );
                isBoolean = false;
            }


            {
                mv = cw.visitMethod( ACC_PUBLIC + ACC_ABSTRACT,
                        NameUtils.getter( rel.getName(), NameUtils.map( rel.getTarget().getName(), true ), rel.getMaxCard(), model.isUseEnhancedNames() ),
                        "()" + propType,
                        genericGetType,
                        null);
                if ( ! rel.isRestricted() && ! rel.isTransient() ) {
                    av0 = mv.visitAnnotation( Type.getDescriptor( RdfProperty.class ), true );
                    av0.visit( "value", "tns:" + propName );
                    av0.visitEnd();
                    if ( rel.getTarget().isPrimitive() ) {
                        av0 = mv.visitAnnotation( Type.getDescriptor( Basic.class ), true );
                        av0.visitEnd();
                    } else {
                        if ( rel.getMaxCard() == null || rel.getMaxCard() > 1 ) {
                            av0 = mv.visitAnnotation( Type.getDescriptor( OneToMany.class ), true );
                        } else {
                            av0 = mv.visitAnnotation( Type.getDescriptor( ManyToOne.class ), true );
                        }
                        {
                            AnnotationVisitor av1 = av0.visitArray( "cascade" );
                            av1.visitEnum( null, Type.getDescriptor( CascadeType.class ), "ALL" );
                            av1.visitEnd();
                        }
                        av0.visitEnd();
                    }
                }

                mv.visitEnd();
            }
            {
                //if ( ! rel.isReadOnly() ) {
                    mv = cw.visitMethod( ACC_PUBLIC + ACC_ABSTRACT,
                            NameUtils.setter( rel.getName(), rel.getMaxCard(), model.isUseEnhancedNames() ),
                            "(" + propType + ")V",
                            genericSetType,
                            null);
                    mv.visitEnd();
                //}
            }
            if ( ! minimal ) {
                {
                    if ( !rel.isRestricted() && !rel.getTarget().isPrimitive() && !rel.isTransient() ) {
                        mv = cw.visitMethod( ACC_PUBLIC + ACC_ABSTRACT,
                                             "add" + NameUtils.compactUpperCase( rel.getName() ),
                                             "(" + Type.getDescriptor( Object.class ) + ")V",
                                             null,
                                             null );
                        {
                            av0 = mv.visitAnnotation( Type.getDescriptor( Transient.class ), true );
                            av0.visitEnd();
                        }
                        mv.visitEnd();

                    }
                }
                {
                    if ( rel.isSimple() || ( ( rel.getMaxCard() == null || rel.getMaxCard() > 1 ) && !rel.isTransient() ) ) {
                        mv = cw.visitMethod( ACC_PUBLIC + ACC_ABSTRACT,
                                             "add" + NameUtils.compactUpperCase( rel.getName() ),
                                             "(L" + NameUtils.map( rel.getTarget().getFullyQualifiedName(), true ).replace( ".", "/" ) + ";)V",
                                             null,
                                             null );
                        {
                            av0 = mv.visitAnnotation( Type.getDescriptor( Transient.class ), true );
                            av0.visitEnd();
                        }
                        mv.visitEnd();
                        mv = cw.visitMethod( ACC_PUBLIC + ACC_ABSTRACT,
                                             "remove" + NameUtils.compactUpperCase( rel.getName() ),
                                             "(" + Type.getDescriptor( Object.class ) + ")V",
                                             null,
                                             null );
                        {
                            av0 = mv.visitAnnotation( Type.getDescriptor( Transient.class ), true );
                            av0.visitEnd();
                        }
                        mv.visitEnd();
                    }
                }
            }
        }


        cw.visitEnd();

        return new JarModelImpl.Holder( cw.toByteArray() );

    }


}
