package org.drools.shapes.terms.generator;

public class Concept {

    private String code;
    private String codeSystem;
    private String name;

    public Concept(String code, String codeSystem, String name) {
        this.code = code;
        this.codeSystem = codeSystem;
        this.name = name;
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
}
