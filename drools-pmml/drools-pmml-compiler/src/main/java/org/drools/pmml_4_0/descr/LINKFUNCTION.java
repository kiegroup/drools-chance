
package org.drools.pmml_4_0.descr;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LINK-FUNCTION.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="LINK-FUNCTION">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="cloglog"/>
 *     &lt;enumeration value="identity"/>
 *     &lt;enumeration value="log"/>
 *     &lt;enumeration value="logc"/>
 *     &lt;enumeration value="logit"/>
 *     &lt;enumeration value="loglog"/>
 *     &lt;enumeration value="negbin"/>
 *     &lt;enumeration value="oddspower"/>
 *     &lt;enumeration value="power"/>
 *     &lt;enumeration value="probit"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "LINK-FUNCTION")
@XmlEnum
public enum LINKFUNCTION {

    @XmlEnumValue("cloglog")
    CLOGLOG("cloglog"),
    @XmlEnumValue("identity")
    IDENTITY("identity"),
    @XmlEnumValue("log")
    LOG("log"),
    @XmlEnumValue("logc")
    LOGC("logc"),
    @XmlEnumValue("logit")
    LOGIT("logit"),
    @XmlEnumValue("loglog")
    LOGLOG("loglog"),
    @XmlEnumValue("negbin")
    NEGBIN("negbin"),
    @XmlEnumValue("oddspower")
    ODDSPOWER("oddspower"),
    @XmlEnumValue("power")
    POWER("power"),
    @XmlEnumValue("probit")
    PROBIT("probit");
    private final String value;

    LINKFUNCTION(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static LINKFUNCTION fromValue(String v) {
        for (LINKFUNCTION c: LINKFUNCTION.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
