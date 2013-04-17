package org.drools.semantics;


import com.clarkparsia.empire.SupportsRdfId;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.net.URI;
import java.util.UUID;


@XmlAccessorType(XmlAccessType.PROPERTY)
public abstract class UIdAble implements SupportsRdfId, org.w3._2002._07.owl.Thing {

    private Key key;

//    private String dyEntryType;

//    private boolean dyReference;

    @XmlTransient
    public RdfKey getRdfId() {
        if ( key == null && getDyEntryId() != null ) {
            key = new Key( "http://" + getDyEntryId() );
        }
        return key;
    }

    public void setRdfId( RdfKey theId ) {
        if ( theId != null && theId.value() != null ) {
            key = new Key( theId.value() );
            setDyEntryId( theId.toString().replace( "http://", "" ) );
        }
    }

    @XmlTransient
    public abstract String getDyEntryId();
    public abstract void setDyEntryId( String x );


    public UIdAble() {
        setDyEntryId( "id" + UUID.randomUUID().toString() );
    }

//    @XmlTransient
//    public abstract String getDyEntryType();
//    public abstract void setDyEntryType(String dyEntryType);

    @XmlTransient
    public abstract String getDyReference();
    public abstract void setDyReference(String dyReference);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UIdAble uIdAble = (UIdAble) o;

        if ( getDyReference() != null ) {
            return this.getDyReference().equals( uIdAble.getDyEntryId() ) || this.getDyReference().equals( uIdAble.getDyReference() );
        } else if ( uIdAble.getDyReference() != null ) {
            return uIdAble.getDyReference().equals( this.getDyEntryId() );
        } else {
            return this.getDyEntryId().equals( uIdAble.getDyEntryId() );
        }
    }

    @Override
    public int hashCode() {
        if ( getDyReference() == null && getDyEntryId() != null ) {
            return getDyEntryId().hashCode();
        } else {
            return getDyReference() != null ? getDyReference().hashCode() : System.identityHashCode( this );
        }
    }

    protected static class Key implements RdfKey, Serializable {

        private URI innerKey;

        public Key() {
            innerKey = null;
        }

        public Key( Object val ) {
            innerKey = URI.create( val.toString() );
        }

        public Key( URI val ) {
            innerKey = val;
        }

        public Object value() {
            return innerKey;
        }

        public String toString() {
            return innerKey == null ? null : innerKey.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            if (innerKey != null ? !innerKey.equals(key.innerKey) : key.innerKey != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return innerKey != null ? innerKey.hashCode() : 0;
        }
    }
}