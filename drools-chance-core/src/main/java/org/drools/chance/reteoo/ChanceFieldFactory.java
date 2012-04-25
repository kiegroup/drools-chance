package org.drools.chance.reteoo;


import org.drools.base.FieldFactory;
import org.drools.base.ValueType;
import org.drools.base.field.ObjectFieldImpl;
import org.drools.chance.distribution.fuzzy.linguistic.Linguistic;
import org.drools.spi.FieldValue;
import org.drools.type.DateFormats;

public class ChanceFieldFactory extends FieldFactory {

    public FieldValue getFieldValue( Object value,
                                     ValueType valueType,
                                     DateFormats dateFormats) {

        // intercepting linguistic, double-supported fields
        if ( value instanceof Linguistic && valueType.equals( ValueType.DOUBLE_TYPE ) ) {
            return new ObjectFieldImpl( value );
        }
        
        return super.getFieldValue( value, valueType, dateFormats );
    }


}
