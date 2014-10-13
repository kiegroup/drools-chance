package org.drools.semantics.builder;


import org.drools.semantics.lang.DLReasonerTemplateManager;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.StreamDocumentSource;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.IOException;

public class DLReasonerBuilderImpl implements DLReasonerBuilder {


    private static DLReasonerBuilder instance = new DLReasonerBuilderImpl();

    public static DLReasonerBuilder getInstance() {
        return instance;
    }

    private DLReasonerBuilderImpl() {

    }

    public OWLOntology parseOntology( Resource[] resources ) {
        try {

            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();
            config.setMissingOntologyHeaderStrategy( OWLOntologyLoaderConfiguration.MissingOntologyHeaderStrategy.IMPORT_GRAPH );

            OWLOntology onto = null;
            for ( Resource res : resources ) {
                OWLOntologyDocumentSource source = new StreamDocumentSource( res.getInputStream() );
                onto = manager.loadOntologyFromOntologyDocument( source, config );
            }

            return onto;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }


    public OWLOntology parseOntology(Resource resource) {
        return parseOntology( new Resource[] { resource } );
    }

    public String buildTableauRules( OWLOntology ontologyDescr, Resource[] visitor ) {

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        for ( Resource res : visitor ) {
            kfs.write( res );
        }
        KieBuilder kieBuilder = ks.newKieBuilder( kfs );
        kieBuilder.buildAll();
        KieBase tabKB = ks.newKieContainer( kieBuilder.getKieModule().getReleaseId() ).getKieBase();

        if (tabKB != null) {
            KieSession ksession = tabKB.newKieSession();

            StringBuilder out = new StringBuilder();
            ksession.setGlobal( "out", out );
            ksession.setGlobal( "registry", DLReasonerTemplateManager.getTableauRegistry( DLReasonerTemplateManager.DLFamilies.FALC) );

            ksession.fireAllRules();

            ksession.insert( ontologyDescr );
            ksession.fireAllRules();

            String tableauRules = out.toString();

            System.out.println( tableauRules );

            ksession.dispose();

            return tableauRules;
        }
        return null;
    }
}
