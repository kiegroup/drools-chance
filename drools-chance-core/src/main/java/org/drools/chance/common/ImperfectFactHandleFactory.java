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

package org.drools.chance.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.drools.chance.lang.descr.ImperfectTypeFieldDescr;
import org.drools.common.AbstractFactHandleFactory;
import org.drools.common.DefaultFactHandle;
import org.drools.common.EventFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.lang.descr.TypeDeclarationDescr;
import org.drools.lang.descr.TypeFieldDescr;
import org.drools.reteoo.ObjectTypeConf;
import org.drools.rule.TypeDeclaration;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.drools.spi.FactHandleFactory;
import org.mvel2.asm.*;

/**
 * TODO
 *
 * Alternative / extension of the ReteooFactHandleFactory.
 * Generates and caches "customized" handles for specific classes,
 * returns the most appropriate handle for a given object
 */
public  class  ImperfectFactHandleFactory extends AbstractFactHandleFactory implements Opcodes {



	private  Map <Class, Constructor> handleMap=new HashMap <Class , Constructor>();

	public ImperfectFactHandleFactory() {
		super();
	}

	public ImperfectFactHandleFactory(int id, long counter) {
		super(id, counter);
	}





    protected final InternalFactHandle newFactHandle(final int id,
                                                        final Object object,
                                                        final long recency,
                                                        final ObjectTypeConf conf,
                                                        final InternalWorkingMemory workingMemory,
                                                        final WorkingMemoryEntryPoint wmEntryPoint) {
           if ( conf != null && conf.isEvent() ) {
               TypeDeclaration type = conf.getTypeDeclaration();
               long timestamp;
               if ( type.getTimestampExtractor() != null ) {
                   if ( Date.class.isAssignableFrom( type.getTimestampExtractor().getExtractToClass() ) ) {
                       timestamp = ((Date) type.getTimestampExtractor().getValue( workingMemory,
                                                                                  object )).getTime();
                   } else {
                       timestamp = type.getTimestampExtractor().getLongValue( workingMemory,
                                                                              object );
                   }
               } else {
                   timestamp = workingMemory.getTimerService().getCurrentTime();
               }
               long duration = 0;
               if ( type.getDurationExtractor() != null ) {
                   duration = type.getDurationExtractor().getLongValue( workingMemory,
                                                                        object );
               }

               if (this.handleMap.containsKey(object.getClass())) {
            	try {
            		Constructor cx = this.handleMap.get(object.getClass());
            		return (EventFactHandle) cx.newInstance(id,object,recency,timestamp,duration);
            	} catch (InstantiationException ie) {
            		ie.printStackTrace();
            	} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
                }

               return new EventFactHandle( id,
                                           object,
                                           recency,
                                           timestamp,
                                           duration,
                                           wmEntryPoint );
           } else {

               if (this.handleMap.containsKey(object.getClass())) {
            	try {
            		Constructor cx = this.handleMap.get(object.getClass());
            		return (DefaultFactHandle) cx.newInstance(id,object,recency);
            	} catch (InstantiationException ie) {
            		ie.printStackTrace();
            	} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
            }

               return new DefaultFactHandle( id,
                                             object,
                                             recency,
                                             wmEntryPoint );
           }
       }




	public FactHandleFactory newInstance() {
		return new ImperfectFactHandleFactory();
	}


	public Class<?> getFactHandleType() {
		return DefaultFactHandle.class;
	}


	public FactHandleFactory newInstance(int id, long counter) {
		return new ImperfectFactHandleFactory(id, counter);
	}





	public void registerImperfectFactHandle(String name, TypeDeclarationDescr tdd) {
//		byte[] serializedKlass = dump(tdd);
//
//		JavaDialectRuntimeData dialect = (JavaDialectRuntimeData) pkgRegistry.getDialectRuntimeRegistry().getDialectData( "java" );
//
//        dialect.write(
//        		JavaDialectRuntimeData.convertClassToResourcePath( name ),
//                serializedKlass );
//
//
//
//		this.handleMap.put( , )
	}


	//TODO : choose appropriate constructor, if event or not
	protected static byte[] dump (TypeDeclarationDescr td) throws Exception {

		ClassWriter cw = new ClassWriter(0);
		FieldVisitor fv;
		MethodVisitor mv;
		AnnotationVisitor av0;

		boolean isEvent = td.getAnnotation("role").equals("event");

		String superClass = isEvent ? EventFactHandle.class.getName() : DefaultFactHandle.class.getName();

		cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, td.getNamespace()+"/"+td.getTypeName()+"_Handler", null, superClass, null);

        Map mapFiedl=td.getFields();
        Collection<TypeFieldDescr> tfd=td.getFields().values();


        ImperfectTypeFieldDescr[] _fieldImp=tfd.toArray(  new ImperfectTypeFieldDescr[tfd.size()]);


		for(int i=0;i<_fieldImp.length;i++){

			//System.out.println(" field "+_fieldImp[i].getInternalTypeName());

			{

			fv = cw.visitField(ACC_PRIVATE, _fieldImp[i].getName(), "Lstructure/Storico;", "Lstructure/Storico<L"+_fieldImp[i].getInternalTypeName()+";>;", null);
			fv.visitEnd();
			}
		}




		//costruttore ?

		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "structure/Handler", "<init>", "()V");

			for(int i=0;i<_fieldImp.length;i++){
			mv.visitVarInsn(ALOAD, 0);
			mv.visitTypeInsn(NEW, "structure/Storico");
			mv.visitInsn(DUP);
			mv.visitIntInsn(BIPUSH,_fieldImp[i].getHistory());
			mv.visitMethodInsn(INVOKESPECIAL, "structure/Storico", "<init>", "(I)V");
			mv.visitFieldInsn(PUTFIELD, td.getNamespace()+"/"+td.getTypeName()+"_Handler", _fieldImp[i].getName(), "Lstructure/Storico;");
			}
			mv.visitInsn(RETURN);
			mv.visitMaxs(4, 1);
			mv.visitEnd();


			}


		for(int i=0;i<_fieldImp.length;i++){


			{
				mv = cw.visitMethod(ACC_PUBLIC, "get"+_fieldImp[i].getName()+"History", "()Lstructure/Storico;", "()Lstructure/Storico<L"+_fieldImp[i].getInternalTypeName()+";>;", null);
				mv.visitCode();
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, td.getNamespace()+"/"+td.getTypeName()+"_Handler",_fieldImp[i].getName() , "Lstructure/Storico;");
				mv.visitInsn(ARETURN);
				mv.visitMaxs(1, 1);
				mv.visitEnd();
				}


			{
				mv = cw.visitMethod(ACC_PUBLIC, "set"+_fieldImp[i].getName(), "(Ldistribution/IDistribution;)V", "(Ldistribution/IDistribution<L"+_fieldImp[i].getInternalTypeName()+">;)V", null);
				mv.visitCode();
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, td.getNamespace()+"/"+td.getTypeName()+"_Handler", _fieldImp[i].getName(), "Lstructure/Storico;");
				mv.visitVarInsn(ALOAD, 1);
				mv.visitMethodInsn(INVOKEVIRTUAL, "structure/Storico", "setValue", "(Ldistribution/IDistribution;)V");
				mv.visitVarInsn(ALOAD, 0);
				mv.visitMethodInsn(INVOKEVIRTUAL, td.getNamespace()+"/"+td.getTypeName()+"_Handler", "getBean", "()Ljava/lang/Object;");
				mv.visitTypeInsn(CHECKCAST, td.getNamespace()+"/"+td.getTypeName());
				mv.visitVarInsn(ALOAD, 1);
				mv.visitMethodInsn(INVOKEINTERFACE, "distribution/IDistribution", "getBestChoice", "()Ljava/lang/Object;");
				mv.visitTypeInsn(CHECKCAST, _fieldImp[i].getInternalTypeName());
				mv.visitMethodInsn(INVOKEVIRTUAL, td.getNamespace()+"/"+td.getTypeName(), "set"+_fieldImp[i].getName(), "(L"+_fieldImp[i].getInternalTypeName()+";)V");
				mv.visitInsn(RETURN);
				mv.visitMaxs(2, 2);
				mv.visitEnd();
				}


			{

				mv = cw.visitMethod(ACC_PUBLIC, "get"+_fieldImp[i].getName(), "()L"+_fieldImp[i].getInternalTypeName()+";", null, null);
				mv.visitCode();
				mv.visitVarInsn(ALOAD, 0);
				mv.visitMethodInsn(INVOKEVIRTUAL, td.getNamespace()+"/"+td.getTypeName()+"_Handler", "getBean", "()Ljava/lang/Object;");
				mv.visitTypeInsn(CHECKCAST, td.getNamespace()+"/"+td.getTypeName());
				mv.visitMethodInsn(INVOKEVIRTUAL,td.getNamespace()+"/"+td.getTypeName(), "get"+_fieldImp[i].getName(), "()L"+_fieldImp[i].getInternalTypeName()+";");
				mv.visitInsn(ARETURN);
				mv.visitMaxs(1, 1);
				mv.visitEnd();
				}





		}//for
		cw.visitEnd();
		return cw.toByteArray();
	}



}
