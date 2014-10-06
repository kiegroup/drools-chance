import org.drools.beliefs.provenance.Provenance;
import org.drools.beliefs.provenance.ProvenanceBeliefSystem;
import org.drools.beliefs.provenance.ProvenanceHelper;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.metadata.Modify;
import org.drools.core.rule.EntryPointId;
import org.jboss.drools.provenance.Assertion;
import org.jboss.drools.provenance.Recognition;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.utils.KieHelper;
import org.w3.ns.prov.Activity;
import org.w3.ns.prov.Entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class ProvenanceTest {


    @Test
    public void testConceptualModelBindingWithTraitDRL() {

        KieServices kieServices = KieServices.Factory.get();
        Resource traitDRL = kieServices.getResources().newClassPathResource( "tiny_declare.drl" );
        Resource ruleDRL = kieServices.getResources().newClassPathResource( "testProvenance.drl" );

        KieHelper kieHelper = validateKieBuilder( traitDRL, ruleDRL );
        List list = new ArrayList();
        KieSession kieSession = kieHelper.build( ProvenanceHelper.getProvenanceEnabledKieBaseConfiguration() ).newKieSession();
        kieSession.setGlobal( "list", list );
        kieSession.fireAllRules();

        Provenance provenance = ProvenanceHelper.getProvenance( kieSession );

        for ( Object o : kieSession.getObjects() ) {
            if ( provenance.hasProvenanceFor( o ) ) {
                Collection<? extends Activity> activity = provenance.describeProvenance( o );
                Collection<? extends Activity> acts = provenance.describeProvenance( o );

                if ( o instanceof Modify ) {
                    assertEquals( 2, acts.size() );
                } else {
                    Activity act = acts.iterator().next();
                    assertEquals( 1, acts.size() );

                    if ( act instanceof Recognition ) {
                        assertEquals( 1, act.getGenerated().size() );
                        Entity rec = act.getGenerated().get( 0 );
                        Entity tgt = rec.getHadPrimarySource().get( 0 );

                        assertEquals( 1, tgt.getDisplaysAs().size() );
                        assertEquals( "Pretty print hello world from IdentifiableEntity_Proxy",
                                      tgt.getDisplaysAs().get( 0 ).getNarrativeText().get( 0 ) );
                    }
                }

            }
        }

        assertEquals( Arrays.asList( "RESULT" ), list );
    }

    private KieHelper validateKieBuilder( Resource... resources ) {
        KieHelper helper = new KieHelper();
        for ( Resource resource : resources ) {
            helper.addResource( resource );
        }

        Results res = helper.verify();
        System.err.println( res.getMessages() );
        assertFalse( helper.verify().hasMessages( Message.Level.ERROR ) );

        return helper;
    }

    @Test
    public void testAssertions() {
        String drl = "package org.drools.provenance; " +
                     "global " + ProvenanceBeliefSystem.class.getName() + " pbs; " +

                     "declare Foo " +
                     "@propertyReactive " +
                     "@Traitable " +
                     "  id : int @key " +
                     "  link : Foo " +
                     "end " +

                     "declare trait Bar " +
                     "@propertyReactive " +
                     "  id : int " +
                     "  val : String " +
                     "end " +

                     "rule Create " +
                     "when " +
                     "  $s : String( this == 'go' ) " +
                     "then " +
                     "  System.out.println( 'Create the foos' ); " +
                     "  insertLogical( new Foo( 1 ), pbs.PROV ); " +
                     "  insertLogical( new Foo( 99 ), pbs.PROV ); " +
                     "end " +

                     "rule Don " +
                     "when " +
                     "  $f : Foo( 1; ) " +
                     "then " +
                     "  System.out.println( 'Don foo 1' ); " +
                     "  don( $f, Bar.class, pbs.PROV ); " +
                     "end " +

                     "rule Set " +
                     "when " +
                     "  $b : Bar() " +
                     "then " +
                     "  System.out.println( 'Set value on Bar' ); " +
                     "  $b.setVal( 'hello' ); " +
                     "end " +

                     "rule Link " +
                     "when " +
                     "  $f : Foo( 1; ) " +
                     "  $g : Foo( 99; ) " +
                     "then " +
                     "  System.out.println( 'Link two foos' ); " +
                     "  $f.setLink( $g ); " +
                     "end " +

                     "";

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );

        KieBase kBase = helper.build();
        KieSession kSession = kBase.newKieSession();

        NamedEntryPoint ep = (NamedEntryPoint) kSession.getEntryPoint( EntryPointId.DEFAULT.getEntryPointId());
        ProvenanceBeliefSystem pbs = ProvenanceHelper.install( ep );
        kSession.setGlobal( "pbs", pbs );

        FactHandle handle = kSession.insert( "go" );
        kSession.fireAllRules();

        kSession.delete( handle );
        kSession.fireAllRules();

        assertEquals( 0, kSession.getObjects().size() );

        Foo f;


    }

    public enum Foo { A,B }
}
