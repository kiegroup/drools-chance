
package org.drools.pmml_4_0.descr;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for INVALID-VALUE-TREATMENT-METHOD.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="INVALID-VALUE-TREATMENT-METHOD">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="returnInvalid"/>
 *     &lt;enumeration value="asIs"/>
 *     &lt;enumeration value="asMissing"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "INVALID-VALUE-TREATMENT-METHOD")
@XmlEnum
public enum INVALIDVALUETREATMENTMETHOD {

    @XmlEnumValue("returnInvalid")
    RETURN_INVALID("returnInvalid"),
    @XmlEnumValue("asIs")
    AS_IS("asIs"),
    @XmlEnumValue("asMissing")
    AS_MISSING("asMissing");
    private final String value;

    INVALIDVALUETREATMENTMETHOD(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static INVALIDVALUETREATMENTMETHOD fromValue(String v) {
        for (INVALIDVALUETREATMENTMETHOD c: INVALIDVALUETREATMENTMETHOD.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
