package org.drools.pmml_4_0;


public interface PMML4Field {

    public String getContext();

    public boolean isValid();

    public boolean isMissing();

    public String getName();
}
