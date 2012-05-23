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


import org.drools.factmodel.BuildUtils;
import org.drools.semantics.builder.DLUtils;
import org.drools.semantics.builder.model.*;
import org.mvel2.asm.*;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class JarInterfaceModelCompilerImpl extends JavaInterfaceModelCompilerImpl implements Opcodes, JarInterfaceModelCompiler {


    public void setModel(OntoModel model) {
        this.model = (CompiledOntoModel) ModelFactory.newModel( ModelFactory.CompileTarget.JAR, model );
    }

    public void compile( Concept con, Object context, Map<String, Object> params ) {

//        if ( ! con.isResolved() ) {
        super.compile( con, context, params );
        String name = con.getName().substring( con.getName().lastIndexOf( "." ) + 1 );

        ((JarModel) getModel()).addCompiledTrait( name, this.compile( name, params ) );

        ((JarModel) getModel()).addCompiledTrait( name + "$$Shadow", this.compile( name + "$$Shadow", params ) );
//     }
    }


    private JarModelImpl.Holder compile( String trait, Map<String, Object> params ) {
        boolean isShadow = trait.endsWith( "$$Shadow" );
        if ( isShadow ) {
            return compileShadow( trait, params );
        } else {
            return compileInterface( trait, params );
        }
    }

    private JarModelImpl.Holder compileInterface( String trait, Map<String, Object> params ) {

        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;


        String pack = ((String)params.get("package")).replace(".","/") + "/";

        Set<Concept> sup = ((Set<Concept>) params.get( "superConcepts" ) );
        String implInterface = (String) params.get( "implInterface" );
        int N = sup.size()
                + 1
                + ( implInterface != null ? 1 : 0 );
        String[] superTypes = new String[ N ];
        superTypes[0] = "com/clarkparsia/empire/SupportsRdfId";

        int j = 1;
        if ( implInterface != null ) {
            superTypes[ j++ ] = implInterface;
        }
        for ( Iterator<Concept> iter = sup.iterator(); iter.hasNext(); ) {
            superTypes[ j++ ] = pack + iter.next().getName();
        }


        Map<String, PropertyRelation> props = (Map<String, PropertyRelation>) params.get( "properties" );

        cw.visit(V1_5, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE,
                pack +  trait,
                null,
                "java/lang/Object",
                superTypes);

        {
            av0 = cw.visitAnnotation("Lcom/clarkparsia/empire/annotation/RdfsClass;", true);
            av0.visit("value", "tns:" + trait );
            av0.visitEnd();
        }
        {
            av0 = cw.visitAnnotation("Lcom/clarkparsia/empire/annotation/Namespaces;", true);
            {
                AnnotationVisitor av1 = av0.visitArray("value");
                av1.visit(null, "tns");
                av1.visit(null, "http://" + pack + "#");
                av1.visitEnd();
            }
            av0.visitEnd();
        }


        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "getDyEntryId", "()Ljava/lang/String;", null, null);
            {
                av0 = mv.visitAnnotation("Ljavax/xml/bind/annotation/XmlID;", true);
                av0.visitEnd();
            }
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "setDyEntryId", "(Ljava/lang/String;)V", null, null);
            mv.visitEnd();
        }



        for ( String propKey : props.keySet() ) {
            PropertyRelation rel = props.get( propKey );
            String propName = rel.getName();
            propName = propName.substring(0,1).toUpperCase() + propName.substring(1);
            String target = rel.getTarget().getName();

            boolean isBoolean = target.equalsIgnoreCase("xsd:boolean");
            if ( target.startsWith("xsd:") ) {
                target = DLUtils.map( target, rel.getMaxCard() == null || rel.getMaxCard() != 1 );
            } else {
                target = pack + target;
            }

            String propType = BuildUtils.getTypeDescriptor( target );
            String genericGetType = null;
            String genericSetType = null;
            if ( rel.getMaxCard() == null || rel.getMaxCard() != 1  ) {
                genericGetType = "()Ljava/util/List<" + propType + ">;";
                genericSetType = "(Ljava/util/List<" + propType + ">;)V";
                propType = "Ljava/util/List;";
                isBoolean = false;
            }


            {
                mv = cw.visitMethod( ACC_PUBLIC + ACC_ABSTRACT,
                        DLUtils.getter( rel.getName(), rel.getTarget().getName(), rel.getMaxCard() ),
                        "()" + propType,
                        genericGetType,
                        null);
                if ( ! rel.isRestricted() && ! rel.isTransient() ) {
                    av0 = mv.visitAnnotation("Lcom/clarkparsia/empire/annotation/RdfProperty;", true);
                    av0.visit("value", "tns:hasCollectionPoint");
                    av0.visitEnd();
                    if ( rel.isSimple() ) {
                        av0 = mv.visitAnnotation("Ljavax/persistence/Basic;", true);
                        av0.visitEnd();
                    } else {
                        av0 = mv.visitAnnotation("Ljavax/persistence/OneToMany;", true);
                        {
                            AnnotationVisitor av1 = av0.visitArray("cascade");
                            av1.visitEnum(null, "Ljavax/persistence/CascadeType;", "PERSIST");
                            av1.visitEnd();
                        }
                        av0.visitEnd();
                    }
                }

                mv.visitEnd();
            }
            {
                if ( ! rel.isReadOnly() ) {
                    mv = cw.visitMethod( ACC_PUBLIC + ACC_ABSTRACT,
                            DLUtils.setter( rel.getName() ),
                            "(" + propType + ")V",
                            genericSetType,
                            null);
                    mv.visitEnd();
                }
            }
            {
                if ( ! rel.isRestricted() && ! rel.getTarget().isPrimitive() && ! rel.isTransient() ) {
                    mv = cw.visitMethod( ACC_PUBLIC + ACC_ABSTRACT,
                            "add" + DLUtils.compactUpperCase( rel.getName() ),
                            "(Ljava/lang/Object;)V",
                            null,
                            null);
                    {
                        av0 = mv.visitAnnotation("Ljavax/persistence/Transient;", true);
                        av0.visitEnd();
                    }
                    mv.visitEnd();

                }
            }
            {
                if ( rel.isSimple() || ( ( rel.getMaxCard() == null || rel.getMaxCard() > 1 ) && ! rel.isTransient() ) ) {
                    mv = cw.visitMethod( ACC_PUBLIC + ACC_ABSTRACT,
                            "add" + DLUtils.compactUpperCase( rel.getName() ),
                            "(" + BuildUtils.getTypeDescriptor( ( rel.getTarget().isPrimitive() ? "" : pack )
                                    + DLUtils.map( rel.getTarget().getName(), true ) ) + ")V",
                            null,
                            null );
                    {
                        av0 = mv.visitAnnotation("Ljavax/persistence/Transient;", true);
                        av0.visitEnd();
                    }
                    mv.visitEnd();
                    mv = cw.visitMethod( ACC_PUBLIC + ACC_ABSTRACT,
                            "remove" + DLUtils.compactUpperCase( rel.getName() ),
                            "(Ljava/lang/Object;)V",
                            null,
                            null );
                    {
                        av0 = mv.visitAnnotation("Ljavax/persistence/Transient;", true);
                        av0.visitEnd();
                    }
                    mv.visitEnd();
                }
            }

        }


        cw.visitEnd();

        return new JarModelImpl.Holder( cw.toByteArray() );

    }





    private JarModelImpl.Holder compileShadow( String trait, Map<String, Object> params ) {

        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;


        String pack = ((String)params.get("package")).replace(".","/") + "/";

        Set<Concept> sup =  ((Set<Concept>) params.get("subConcepts"));

        String[] superTypes = new String[ sup.size() +1 ];
        superTypes[0] = "com/clarkparsia/empire/SupportsRdfId";
        int j = 1;
        for ( Iterator<Concept> iter = sup.iterator(); iter.hasNext(); ) {
            superTypes[j++] = pack + iter.next().getName() + "$$Shadow";
        }


        Map<String, PropertyRelation> props =  (Map<String, PropertyRelation>) params.get( "shadowProperties" );




        cw.visit(V1_5, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE,
                pack +  trait,
                null,
                "java/lang/Object",
                superTypes);

        {
            av0 = cw.visitAnnotation("Lcom/clarkparsia/empire/annotation/RdfsClass;", true);
            av0.visit("value", "tns:" + params.get( "name" ) );
            av0.visitEnd();
        }
        {
            av0 = cw.visitAnnotation("Lcom/clarkparsia/empire/annotation/Namespaces;", true);
            {
                AnnotationVisitor av1 = av0.visitArray("value");
                av1.visit(null, "tns");
                av1.visit(null, "http://" + pack + "#");
                av1.visitEnd();
            }
            av0.visitEnd();
        }

        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "getDyEntryId", "()Ljava/lang/String;", null, null);
            {
                av0 = mv.visitAnnotation("Ljavax/xml/bind/annotation/XmlID;", true);
                av0.visitEnd();
            }
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "setDyEntryId", "(Ljava/lang/String;)V", null, null);
            mv.visitEnd();
        }




        for ( String propKey : props.keySet() ) {
            PropertyRelation rel = props.get( propKey );
            String propName = rel.getName();
            propName = propName.substring(0,1).toUpperCase() + propName.substring(1);
            //String target =  pack + props.get( rel ).getName();
            String target = rel.getTarget().getName();
            boolean isBoolean = target.equalsIgnoreCase("xsd:boolean");
            if ( target.startsWith("xsd:") ) {
                target = DLUtils.map( target, rel.getMaxCard() == null || rel.getMaxCard() != 1 ).replace(".","/");
            } else {
                target = pack + target;
            }

            String propType = BuildUtils.getTypeDescriptor( target );
            String genericGetType = null;
            String genericSetType = null;
            if ( rel.getMaxCard() == null || rel.getMaxCard() != 1  ) {
                genericGetType = "()Ljava/util/List<" + propType + ">;";
                genericSetType = "(Ljava/util/List<" + propType + ">;)V";
                propType = "Ljava/util/List;";
                isBoolean = false;
            }


            if ( ! rel.isRestricted() && ! rel.isTransient() ) {
                mv = cw.visitMethod( ACC_PUBLIC + ACC_ABSTRACT,
                        DLUtils.getter( rel.getName(), rel.getTarget().getName(), rel.getMaxCard() ),
                        "()" + propType,
                        genericGetType,
                        null);
                if ( ! rel.isRestricted() && ! rel.isTransient() ) {
                    av0 = mv.visitAnnotation("Lcom/clarkparsia/empire/annotation/RdfProperty;", true);
                    av0.visit("value", "tns:hasCollectionPoint");
                    av0.visitEnd();
                    if ( rel.isSimple() ) {
                        av0 = mv.visitAnnotation("Ljavax/persistence/Basic;", true);
                        av0.visitEnd();
                    } else {
                        av0 = mv.visitAnnotation("Ljavax/persistence/OneToMany;", true);
                        {
                            AnnotationVisitor av1 = av0.visitArray("cascade");
                            av1.visitEnum(null, "Ljavax/persistence/CascadeType;", "PERSIST");
                            av1.visitEnd();
                        }
                        av0.visitEnd();
                    }

                    mv = cw.visitMethod( ACC_PUBLIC + ACC_ABSTRACT,
                            DLUtils.setter( rel.getName() ),
                            "(" + propType + ")V",
                            genericSetType,
                            null);
                    mv.visitEnd();
                }

                mv.visitEnd();
            }

        }

        cw.visitEnd();

        return new JarModelImpl.Holder( cw.toByteArray() );

    }




//    Set<Concept> sup = ((Set<Concept>) params.get("superConcepts"));
//            String[] superTypes = new String[ sup.size() + 1];
//            superTypes[0] = "com/clarkparsia/empire/SupportsRdfId";
//            int j = 1;
//            for ( Iterator<Concept> iter = sup.iterator(); iter.hasNext(); ) {
//                superTypes[j++] = pack + iter.next().getName();
//            }
//
//            Map<String, PropertyRelation> props = (Map<String, PropertyRelation>) params.get( "properties" );
//
//            cw.visit(V1_5, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE,
//                    pack +  params.get("name"),
//                    null,
//                    "java/lang/Object",
//                    superTypes);
//
//                for ( String propKey : props.keySet() ) {
//                    PropertyRelation rel = props.get( propKey );
//                    String propName = rel.getName();
//                        propName = propName.substring(0,1).toUpperCase() + propName.substring(1);
//                    //String target =  pack + props.get( rel ).getName();
//                    String target = rel.getTarget().getName();
//                    boolean isBoolean = target.equalsIgnoreCase("xsd:boolean");
//                        if ( target.startsWith("xsd:") ) {
//                            target = DLUtils.map( target, rel.getMaxCard() == null || rel.getMaxCard() != 1 ).replace(".","/");
//                        } else {
//                            target = pack + target;
//                        }
//
//                    String propType = BuildUtils.getTypeDescriptor( target );
//                    String genericGetType = null;
//                    String genericSetType = null;
//                    if ( rel.getMaxCard() == null || rel.getMaxCard() != 1  ) {
//                        genericGetType = "()Ljava/util/List<" + propType + ">;";
//                        genericSetType = "(Ljava/util/List<" + propType + ">;)V";
//                        propType = "Ljava/util/List;";
//                        isBoolean = false;
//                    }
//
//
//                    String getPrefix = isBoolean ? "is" : "get";
//                    mv = cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, getPrefix + propName, "()" + propType , genericGetType, null);
//                    mv.visitEnd();
//
//                    mv = cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "set" + propName, "(" + propType + ")V", genericSetType, null);
//                    mv.visitEnd();
//
//                    System.out.println("*** Just compiled a property " + getPrefix + "\t\n" + propName + " \t\n " + propType + "\t\n" + genericGetType + "\t\n" + genericSetType + "\t\n" +(pack +  params.get("name"))+"\t\n");
//                }
//
//            cw.visitEnd();
//
//            return new JarModelImpl.Holder( cw.toByteArray() );








}
