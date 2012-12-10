package org.drools.semantics.builder;


import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.io.Resource;
import org.drools.io.impl.BaseResource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.semantics.lang.DLReasonerTemplateManager;
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

        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        for ( Resource res : visitor ) {
            knowledgeBuilder.add( res, ((BaseResource) res).getResourceType() );
        }
        if ( knowledgeBuilder.hasErrors() ) {
            System.err.println( knowledgeBuilder.getErrors().toString() );
            return null;
        }
        KnowledgeBase tabKB = knowledgeBuilder.newKnowledgeBase();

        if (tabKB != null) {
            StatefulKnowledgeSession ksession = tabKB.newStatefulKnowledgeSession();

            StringBuilder out = new StringBuilder();
            ksession.setGlobal( "out", out );
            ksession.setGlobal( "registry", DLReasonerTemplateManager.getTableauRegistry( DLReasonerTemplateManager.DLFamilies.FALC) );

            ksession.fireAllRules();

            ksession.insert( ontologyDescr );
            ksession.fireAllRules();

            String tableauRules = out.toString();

            ksession.dispose();

            return tableauRules;
        }
        return null;
    }
}
