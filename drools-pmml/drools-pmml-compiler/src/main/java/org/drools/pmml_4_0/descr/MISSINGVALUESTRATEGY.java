
package org.drools.pmml_4_0.descr;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MISSING-VALUE-STRATEGY.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="MISSING-VALUE-STRATEGY">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="lastPrediction"/>
 *     &lt;enumeration value="nullPrediction"/>
 *     &lt;enumeration value="defaultChild"/>
 *     &lt;enumeration value="weightedConfidence"/>
 *     &lt;enumeration value="aggregateNodes"/>
 *     &lt;enumeration value="none"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "MISSING-VALUE-STRATEGY")
@XmlEnum
public enum MISSINGVALUESTRATEGY {

    @XmlEnumValue("lastPrediction")
    LAST_PREDICTION("lastPrediction"),
    @XmlEnumValue("nullPrediction")
    NULL_PREDICTION("nullPrediction"),
    @XmlEnumValue("defaultChild")
    DEFAULT_CHILD("defaultChild"),
    @XmlEnumValue("weightedConfidence")
    WEIGHTED_CONFIDENCE("weightedConfidence"),
    @XmlEnumValue("aggregateNodes")
    AGGREGATE_NODES("aggregateNodes"),
    @XmlEnumValue("none")
    NONE("none");
    private final String value;

    MISSINGVALUESTRATEGY(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MISSINGVALUESTRATEGY fromValue(String v) {
        for (MISSINGVALUESTRATEGY c: MISSINGVALUESTRATEGY.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
