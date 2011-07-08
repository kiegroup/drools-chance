
package org.drools.pmml_4_0.descr;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MISSING-VALUE-TREATMENT-METHOD.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="MISSING-VALUE-TREATMENT-METHOD">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="asIs"/>
 *     &lt;enumeration value="asMean"/>
 *     &lt;enumeration value="asMode"/>
 *     &lt;enumeration value="asMedian"/>
 *     &lt;enumeration value="asValue"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "MISSING-VALUE-TREATMENT-METHOD")
@XmlEnum
public enum MISSINGVALUETREATMENTMETHOD {

    @XmlEnumValue("asIs")
    AS_IS("asIs"),
    @XmlEnumValue("asMean")
    AS_MEAN("asMean"),
    @XmlEnumValue("asMode")
    AS_MODE("asMode"),
    @XmlEnumValue("asMedian")
    AS_MEDIAN("asMedian"),
    @XmlEnumValue("asValue")
    AS_VALUE("asValue");
    private final String value;

    MISSINGVALUETREATMENTMETHOD(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MISSINGVALUETREATMENTMETHOD fromValue(String v) {
        for (MISSINGVALUETREATMENTMETHOD c: MISSINGVALUETREATMENTMETHOD.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
