import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.impl.ClassPathResource;
import org.drools.lang.DrlDumper;
import org.drools.lang.descr.PackageDescr;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;
import org.semanticweb.ontologies._2012._1.rule_example.Pattern1Type;
import org.semanticweb.ontologies._2012._1.rule_example.Pattern1TypeImpl;
import org.semanticweb.ontologies._2012._1.rule_merged.*;

import java.util.Collection;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

public class IndividualsTest {
    
    
    @Test
    public void testIndividuals() {

        Pattern1Type p1 = new Pattern1TypeImpl();
        Object x = p1.getHasFunctorType();
        Object y = ((Pattern1TypeImpl)p1).getHasFunctorTypeInferred();

        Collection c = IndividualFactory.getIndividuals();
        assertEquals( 21, c.size() );

        StatefulKnowledgeSession kSession = createSession();

        PackageDescr pack = new PackageDescr();
        kSession.setGlobal( "pack", pack );

        
        for ( Object o : c ) {
            kSession.insert( o );
        }                       
        kSession.fireAllRules();



        String drl = new DrlDumper().dump( pack );
        System.err.println( drl );
    }

    private StatefulKnowledgeSession createSession() {
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kBuilder.add( new ClassPathResource( "translators/onto2drl.drl" ), ResourceType.DRL );

        if ( kBuilder.hasErrors() ) {
            fail( kBuilder.getErrors().toString() );
        }

        KnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
        kBase.addKnowledgePackages( kBuilder.getKnowledgePackages() );

        return kBase.newStatefulKnowledgeSession();
    }

}
