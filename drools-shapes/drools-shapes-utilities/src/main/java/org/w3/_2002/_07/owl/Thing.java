package org.w3._2002._07.owl;

import com.clarkparsia.empire.annotation.RdfsClass;
import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import javax.persistence.Basic;


// <http://www.w3.org/2002/07/owl#Thing>

@RdfsClass( value="tns:Thing" )
@Namespaces({ "tns", "http://www.w3.org/2002/07/owl#" })
public interface Thing extends org.drools.semantics.Thing  
                                 
{

    @javax.xml.bind.annotation.XmlID
    public String getDyEntryId();

    public void setDyEntryId( String id );

    public String getDyEntryType();

    public void setDyEntryType(String dyEntryType);

    public boolean isDyReference();

    public void setDyReference(boolean dyReference);


}