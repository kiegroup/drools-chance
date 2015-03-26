package org.drools.shapes.terms.generator;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import org.apache.commons.io.IOUtils;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class TerminologyGenerator {

    public void generate(InputStream owl, String packageName, File outputDirectory) {
        OntModel model = this.load(owl);

        Map<String,CodeSystem> codeSystems =
                this.traverse(model);

        new JavaGenerator().generate(codeSystems.values(), packageName, outputDirectory);
    }

    protected Map<String,CodeSystem> traverse(OntModel model) {
        Map<String,CodeSystem> codeSystems = new HashMap<String,CodeSystem>();

        ResultSet results = this.doCallSparql("sparql/getCodeSystemProperties.rdf", model);

        while(results.hasNext()) {
            QuerySolution result = results.next();

            Resource codeSystem = result.get("codeSystem").as(Resource.class);

            String codeSystemName = codeSystem.getLocalName();
            String codeSystemUri = codeSystem.getURI();

            codeSystems.put(codeSystemName,
                    new CodeSystem(codeSystemName, codeSystemUri));
        }

        results = this.doCallSparql("sparql/getConceptProperties.rdf", model);

        while(results.hasNext()) {
            QuerySolution result = results.next();

            String code = result.get("code").as(Literal.class).getValue().toString();
            String codeSystem = result.get("codeSystem").as(Resource.class).getLocalName();
            String name = result.get("name").as(Individual.class).getLocalName();

            if(! codeSystems.containsKey(codeSystem)) {
                codeSystems.put(codeSystem, new CodeSystem());
            }

            codeSystems.get(codeSystem).getConcepts().add(new Concept(code, codeSystem, name));
        }

        return codeSystems;
    }

    private ResultSet doCallSparql(String sparqlFile, OntModel model) {
        String sparql;
        try {
            sparql = IOUtils.toString(new ClassPathResource(sparqlFile).getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Query query = QueryFactory.create(sparql, Syntax.syntaxSPARQL_11);

        // Execute the query and obtain results
        QueryExecution qe = QueryExecutionFactory.create(query, model);

        return qe.execSelect();
    }


    public OntModel load(InputStream owl) {
        OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC );
        try {
            model.read(owl, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        model.prepare();

        return model;
    }

}
