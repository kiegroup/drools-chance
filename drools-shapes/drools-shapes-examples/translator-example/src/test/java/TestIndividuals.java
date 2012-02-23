import http.www.semanticweb.org.ontologies._2012._1.rule.merged.owl.IndividualFactory;
import http.www.semanticweb.org.ontologies._2012._1.rule.merged.owl.Pattern;
import http.www.semanticweb.org.ontologies._2012._1.rule.merged.owl.Pattern1Type;
import http.www.semanticweb.org.ontologies._2012._1.rule.merged.owl.Pattern1TypeImpl;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.impl.ClassPathResource;
import org.drools.lang.DrlDumper;
import org.drools.lang.api.PackageDescrBuilder;
import org.drools.lang.api.RuleDescrBuilder;
import org.drools.lang.api.impl.PackageDescrBuilderImpl;
import org.drools.lang.descr.PackageDescr;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;

import java.util.Collection;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

public class TestIndividuals {
    
    
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


    public void deleetme() {
        PackageDescrBuilder rootDescr = PackageDescrBuilderImpl.newPackage();
        RuleDescrBuilder r = rootDescr.newRule();

    }
}
