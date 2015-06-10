package org.drools.shapes.terms.generator;

public class Concept {

    private String code;
    private String uri;
    private String codeSystem;
    private String name;

    public Concept() {}

    public Concept(String code, String codeSystem, String name) {
        this.code = code;
        this.codeSystem = codeSystem;
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri( String uri ) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeSystem() {
        return codeSystem;
    }

    public void setCodeSystem(String codeSystem) {
        this.codeSystem = codeSystem;
    }

    @Override
    public String toString() {
        return "Concept{" +
               "code='" + code + '\'' +
               ", codeSystem='" + codeSystem + '\'' +
               ", name='" + name + '\'' +
               '}';
    }
}
