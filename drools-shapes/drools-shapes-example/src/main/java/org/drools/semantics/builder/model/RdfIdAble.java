package org.drools.semantics.builder.model;


import com.clarkparsia.empire.SupportsRdfId;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.net.URI;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RdfIdAble", propOrder = {
        "key"
})
public class RdfIdAble implements SupportsRdfId {

    @XmlElement(required = true, type = Key.class)
    private Key key;

    public RdfKey getRdfId() {
        return key;
    }

    public void setRdfId( RdfKey theId ) {
        key = new Key( theId.value() );
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