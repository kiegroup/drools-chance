package org.drools.shapes.terms.generator;

import java.util.HashSet;
import java.util.Set;

public class CodeSystem {

    private Set<Concept> concepts = new HashSet<Concept>();

    private String codeSystemName;

    private String codeSystemUri;

    public CodeSystem() {
        super();
    }

    public CodeSystem(String codeSystemName, String codeSystemUri) {
        this.codeSystemName = codeSystemName;
        this.codeSystemUri = codeSystemUri;
    }

    public CodeSystem(Set<Concept> concepts, String codeSystemName, String codeSystemUri) {
        this.concepts = concepts;
        this.codeSystemName = codeSystemName;
        this.codeSystemUri = codeSystemUri;
    }

    public Set<Concept> getConcepts() {
        return concepts;
    }

    public void setConcepts(Set<Concept> concepts) {
        this.concepts = concepts;
    }

    public String getCodeSystemName() {
        return codeSystemName;
    }

    public void setCodeSystemName(String codeSystemName) {
        this.codeSystemName = codeSystemName;
    }

    public String getCodeSystemUri() {
        return codeSystemUri;
    }

    public void setCodeSystemUri(String codeSystemUri) {
        this.codeSystemUri = codeSystemUri;
    }

    @Override
    public String toString() {
        return "CodeSystem{ " +
               "Uri = '" + codeSystemUri + "( " + concepts.size() + " )" +
               '}';
    }
}
