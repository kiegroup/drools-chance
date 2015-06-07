package org.drools.chance.reteoo;


import org.drools.chance.distribution.fuzzy.linguistic.Linguistic;
import org.drools.core.base.FieldFactory;
import org.drools.core.base.ValueType;
import org.drools.core.base.field.ObjectFieldImpl;
import org.drools.core.spi.FieldValue;
import org.drools.core.type.DateFormats;

public class ChanceFieldFactory extends FieldFactory {

    public FieldValue getFieldValue( Object value,
                                     ValueType valueType,
                                     DateFormats dateFormats ) {

        // intercepting linguistic, double-supported fields
        if ( value instanceof Linguistic &&
                ( valueType.equals( ValueType.DOUBLE_TYPE ) || valueType.equals( ValueType.FLOAT_TYPE ) || valueType.equals( ValueType.BOOLEAN_TYPE ) ) ) {
            return new ObjectFieldImpl( value );
        }
        
        return super.getFieldValue( value, valueType, dateFormats );
    }


}
