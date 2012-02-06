package http.org.drools.conyard.owl;


import com.clarkparsia.empire.SupportsRdfId;

import javax.xml.bind.annotation.*;
import java.net.URI;
import java.util.UUID;


@XmlAccessorType(XmlAccessType.PROPERTY)
public abstract class UIdAble implements SupportsRdfId {

    private Key key;

    private String actualType;

    private boolean isReference;

    @XmlTransient
    public RdfKey getRdfId() {
        if ( key == null && getUniversalId() != null ) {
            key = new Key( getUniversalId() );
        }
        return key;
    }

    public void setRdfId( RdfKey theId ) {
        key = new Key( theId.value() );
        setUniversalId( theId.toString() );
    }

    @XmlTransient
    public abstract String getUniversalId();
    public abstract void setUniversalId( String x );

    public String getActualType() {
        return actualType;
    }

    public void setActualType(String actualType) {
        this.actualType = actualType;
    }

    public UIdAble() {
        setUniversalId( "http://" + UUID.randomUUID().toString() );
    }

    public boolean isReference() {
        return isReference;
    }

    public void setReference(boolean reference) {
        isReference = reference;
    }

    protected static class Key implements RdfKey {

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