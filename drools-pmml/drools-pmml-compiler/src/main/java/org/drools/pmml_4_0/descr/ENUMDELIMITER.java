
package org.drools.pmml_4_0.descr;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DELIMITER.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DELIMITER">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="sameTimeWindow"/>
 *     &lt;enumeration value="acrossTimeWindows"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DELIMITER")
@XmlEnum
public enum ENUMDELIMITER {

    @XmlEnumValue("sameTimeWindow")
    SAME_TIME_WINDOW("sameTimeWindow"),
    @XmlEnumValue("acrossTimeWindows")
    ACROSS_TIME_WINDOWS("acrossTimeWindows");
    private final String value;

    ENUMDELIMITER(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ENUMDELIMITER fromValue(String v) {
        for (ENUMDELIMITER c: ENUMDELIMITER.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
