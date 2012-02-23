package http.org.drools.conyard.owl;


import com.clarkparsia.empire.SupportsRdfId;

import javax.xml.bind.annotation.*;
import java.net.URI;
import java.util.UUID;


@XmlAccessorType(XmlAccessType.PROPERTY)
public abstract class UIdAble implements SupportsRdfId {

    private Key key;

    private String dyEntryType;

    private boolean dyReference;

    @XmlTransient
    public RdfKey getRdfId() {
        if ( key == null && getDyEntryId() != null ) {
            key = new Key( getDyEntryId() );
        }
        return key;
    }

    public void setRdfId( RdfKey theId ) {
        key = new Key( theId.value() );
        setDyEntryId( theId.toString() );
    }

    @XmlTransient
    public abstract String getDyEntryId();
    public abstract void setDyEntryId( String x );


    public UIdAble() {
        setDyEntryId( "http://" + UUID.randomUUID().toString() );
    }

    public String getDyEntryType() {
        return dyEntryType;
    }

    public void setDyEntryType(String dyEntryType) {
        this.dyEntryType = dyEntryType;
    }

    public boolean isDyReference() {
        return dyReference;
    }

    public void setDyReference(boolean dyReference) {
        this.dyReference = dyReference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UIdAble uIdAble = (UIdAble) o;

        if (isDyReference() != uIdAble.isDyReference()) return false;
        
        if ( this.getDyEntryId() != null ) {
            return getDyEntryId().equals( uIdAble.getDyEntryId() );
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = getDyEntryId() != null ? getDyEntryId().hashCode() : 0;
        result = 31 * result + (isDyReference() ? 1 : 0);
        return result;
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