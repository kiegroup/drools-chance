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

package org.drools.pmml_4_1;


import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBaseConfiguration;
import org.drools.builder.*;
import org.drools.common.DefaultFactHandle;
import org.drools.common.EventFactHandle;
import org.drools.conf.EventProcessingOption;
import org.drools.definition.type.FactType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.ClassObjectFilter;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResults;

import java.io.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public abstract class DroolsAbstractPMMLTest {


    public static final String PMML = "org.drools.pmml_4_1.descr";
    public static final String BASE_PACK = DroolsAbstractPMMLTest.class.getPackage().getName().replace('.','/');




    public static final String RESOURCE_PATH = BASE_PACK;




    private StatefulKnowledgeSession kSession;
    private KnowledgeBase kbase;
    private static PMML4Compiler compiler = new PMML4Compiler();





    public DroolsAbstractPMMLTest() {
        super();
    }

    protected StatefulKnowledgeSession getModelSession(String pmmlSource, boolean verbose) {
        return getModelSession(new String[] {pmmlSource}, verbose);
    }


    protected StatefulKnowledgeSession getModelSession(String[] pmmlSources, boolean verbose) {

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add(ResourceFactory.newClassPathResource("org/drools/informer/informer-changeset.xml"), ResourceType.CHANGE_SET);



        if (! verbose) {
            for ( String pmmlSource : pmmlSources ) {
                kbuilder.add(ResourceFactory.newClassPathResource(pmmlSource),ResourceType.PMML);
            }
        } else {

            try {
                for ( String pmmlSource : pmmlSources ) {
                    String src = compiler.compile( ResourceFactory.newClassPathResource( pmmlSource ).getInputStream(), null );
                    kbuilder.add( ResourceFactory.newByteArrayResource( src.getBytes() ), ResourceType.DRL );
                    System.out.println(src);
                }
            } catch (IOException e) {
                fail(e.getMessage());
            }
        }


        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if ( errors.size() > 0 ) {
            throw new IllegalArgumentException( "Could not parse knowledge : " + errors.toString() );
        }
        RuleBaseConfiguration conf = new RuleBaseConfiguration();
        conf.setEventProcessingMode( EventProcessingOption.STREAM );
        //conf.setConflictResolver(LifoConflictResolver.getInstance());
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( conf );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        return kbase.newStatefulKnowledgeSession();

    }










    protected StatefulKnowledgeSession getSession(String theory) {
        KnowledgeBase kbase = readKnowledgeBase(new ByteArrayInputStream(theory.getBytes()));
        return kbase != null ? kbase.newStatefulKnowledgeSession() : null;
    }

    protected void refreshKSession() {
        if (getKSession() != null)
            getKSession().dispose();
        setKSession(getKbase().newStatefulKnowledgeSession());
    }








    private static KnowledgeBase readKnowledgeBase(InputStream theory) {
        return readKnowledgeBase(Arrays.asList(theory));
    }

    private static KnowledgeBase readKnowledgeBase(List<InputStream> theory) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        for (InputStream is : theory)
            kbuilder.add(ResourceFactory.newInputStreamResource(is), ResourceType.DRL);
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if (errors.size() > 0) {
            for (KnowledgeBuilderError error: errors) {
                System.err.println(error);
            }
            throw new IllegalArgumentException("Could not parse knowledge.");
        }
        RuleBaseConfiguration conf = new RuleBaseConfiguration();
        conf.setEventProcessingMode(EventProcessingOption.STREAM);
        conf.setAssertBehaviour(RuleBaseConfiguration.AssertBehaviour.EQUALITY);
        //conf.setConflictResolver(LifoConflictResolver.getInstance());
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(conf);
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        return kbase;
    }

    public String reportWMObjects(StatefulKnowledgeSession session) {
        PriorityQueue<String> queue = new PriorityQueue<String>();
        for (FactHandle fh : session.getFactHandles()) {
            Object o;
            if (fh instanceof EventFactHandle) {
                EventFactHandle efh = (EventFactHandle) fh;
                queue.add("\t " + efh.getStartTimestamp() + "\t" + efh.getObject().toString() + "\n");
            } else {
                o = ((DefaultFactHandle) fh).getObject();
                queue.add("\t " + o.toString() + "\n");
            }

        }
        String ans = " ---------------- WM " + session.getObjects().size() + " --------------\n";
        while (! queue.isEmpty())
            ans += queue.poll();
        ans += " ---------------- END WM -----------\n";
        return ans;
    }










    private void dump(String s, OutputStream ostream) {
        // write to outstream
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(ostream, "UTF-8");
            writer.write(s);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (writer != null) {
                    writer.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }










    public StatefulKnowledgeSession getKSession() {
        return kSession;
    }

    public void setKSession(StatefulKnowledgeSession kSession) {
        this.kSession = kSession;
    }

    public KnowledgeBase getKbase() {
        return kbase;
    }

    public void setKbase(KnowledgeBase kbase) {
        this.kbase = kbase;
    }






    protected void checkFirstDataFieldOfTypeStatus(FactType type, boolean valid, boolean missing, String ctx, Object... target) {
        Class<?> klass = type.getFactClass();
        Iterator iter = getKSession().getObjects( new ClassObjectFilter( klass ) ).iterator();
        assertTrue( iter.hasNext() );
        Object obj = iter.next();
        if (ctx == null) {
            while ( type.get( obj, "context" ) != null && iter.hasNext() )
                obj = iter.next();
        } else {
            while ( ( ! ctx.equals( type.get( obj, "context" ) ) ) && iter.hasNext() )
                obj = iter.next();
        }
        assertEquals( target[0], type.get( obj, "value" ) );
        assertEquals( valid, type.get( obj, "valid" ) );
        assertEquals( missing, type.get( obj, "missing" ) );

    }


    protected double queryDoubleField(String target, String modelName) {
        QueryResults results = getKSession().getQueryResults( target, modelName );
        assertEquals( 1, results.size() );

        return (Double) results.iterator().next().get( "result" );
    }


    protected double queryIntegerField(String target, String modelName) {
        QueryResults results = getKSession().getQueryResults(target,modelName);
        assertEquals(1, results.size());

        return (Integer) results.iterator().next().get("result");
    }


    protected String queryStringField(String target, String modelName) {
        QueryResults results = getKSession().getQueryResults(target,modelName);
        assertEquals(1, results.size());

        return (String) results.iterator().next().get("result");
    }


    public Double getDoubleFieldValue( FactType type ) {
        Class<?> klass = type.getFactClass();
        Iterator iter = getKSession().getObjects(new ClassObjectFilter(klass)).iterator();
        Object obj = iter.next();
        return (Double) type.get( obj, "value" );
    }

    public Object getFieldValue( FactType type ) {
        Class<?> klass = type.getFactClass();
        Iterator iter = getKSession().getObjects(new ClassObjectFilter(klass)).iterator();
        Object obj = iter.next();
        return type.get( obj, "value" );
    }



}
