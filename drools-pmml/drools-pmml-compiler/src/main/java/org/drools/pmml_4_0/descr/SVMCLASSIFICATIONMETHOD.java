
package org.drools.pmml_4_0.descr;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SVM-CLASSIFICATION-METHOD.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="SVM-CLASSIFICATION-METHOD">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="OneAgainstAll"/>
 *     &lt;enumeration value="OneAgainstOne"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "SVM-CLASSIFICATION-METHOD")
@XmlEnum
public enum SVMCLASSIFICATIONMETHOD {

    @XmlEnumValue("OneAgainstAll")
    ONE_AGAINST_ALL("OneAgainstAll"),
    @XmlEnumValue("OneAgainstOne")
    ONE_AGAINST_ONE("OneAgainstOne");
    private final String value;

    SVMCLASSIFICATIONMETHOD(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SVMCLASSIFICATIONMETHOD fromValue(String v) {
        for (SVMCLASSIFICATIONMETHOD c: SVMCLASSIFICATIONMETHOD.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
