
package org.drools.pmml_4_0.descr;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TIME-ANCHOR.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TIME-ANCHOR">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="dateTimeMillisecdondsSince[0]"/>
 *     &lt;enumeration value="dateTimeMillisecdondsSince[1960]"/>
 *     &lt;enumeration value="dateTimeMillisecdondsSince[1970]"/>
 *     &lt;enumeration value="dateTimeMillisecdondsSince[1980]"/>
 *     &lt;enumeration value="dateTimeSecdondsSince[0]"/>
 *     &lt;enumeration value="dateTimeSecdondsSince[1960]"/>
 *     &lt;enumeration value="dateTimeSecdondsSince[1970]"/>
 *     &lt;enumeration value="dateTimeSecdondsSince[1980]"/>
 *     &lt;enumeration value="dateDaysSince[0]"/>
 *     &lt;enumeration value="dateDaysSince[1960]"/>
 *     &lt;enumeration value="dateDaysSince[1970]"/>
 *     &lt;enumeration value="dateDaysSince[1980]"/>
 *     &lt;enumeration value="dateMonthsSince[0]"/>
 *     &lt;enumeration value="dateMonthsSince[1960]"/>
 *     &lt;enumeration value="dateMonthsSince[1970]"/>
 *     &lt;enumeration value="dateMonthsSince[1980]"/>
 *     &lt;enumeration value="dateYearsSince[0]"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TIME-ANCHOR")
@XmlEnum
public enum ENUMTIMEANCHOR {

    @XmlEnumValue("dateTimeMillisecdondsSince[0]")
    DATE_TIME_MILLISECDONDS_SINCE_0("dateTimeMillisecdondsSince[0]"),
    @XmlEnumValue("dateTimeMillisecdondsSince[1960]")
    DATE_TIME_MILLISECDONDS_SINCE_1960("dateTimeMillisecdondsSince[1960]"),
    @XmlEnumValue("dateTimeMillisecdondsSince[1970]")
    DATE_TIME_MILLISECDONDS_SINCE_1970("dateTimeMillisecdondsSince[1970]"),
    @XmlEnumValue("dateTimeMillisecdondsSince[1980]")
    DATE_TIME_MILLISECDONDS_SINCE_1980("dateTimeMillisecdondsSince[1980]"),
    @XmlEnumValue("dateTimeSecdondsSince[0]")
    DATE_TIME_SECDONDS_SINCE_0("dateTimeSecdondsSince[0]"),
    @XmlEnumValue("dateTimeSecdondsSince[1960]")
    DATE_TIME_SECDONDS_SINCE_1960("dateTimeSecdondsSince[1960]"),
    @XmlEnumValue("dateTimeSecdondsSince[1970]")
    DATE_TIME_SECDONDS_SINCE_1970("dateTimeSecdondsSince[1970]"),
    @XmlEnumValue("dateTimeSecdondsSince[1980]")
    DATE_TIME_SECDONDS_SINCE_1980("dateTimeSecdondsSince[1980]"),
    @XmlEnumValue("dateDaysSince[0]")
    DATE_DAYS_SINCE_0("dateDaysSince[0]"),
    @XmlEnumValue("dateDaysSince[1960]")
    DATE_DAYS_SINCE_1960("dateDaysSince[1960]"),
    @XmlEnumValue("dateDaysSince[1970]")
    DATE_DAYS_SINCE_1970("dateDaysSince[1970]"),
    @XmlEnumValue("dateDaysSince[1980]")
    DATE_DAYS_SINCE_1980("dateDaysSince[1980]"),
    @XmlEnumValue("dateMonthsSince[0]")
    DATE_MONTHS_SINCE_0("dateMonthsSince[0]"),
    @XmlEnumValue("dateMonthsSince[1960]")
    DATE_MONTHS_SINCE_1960("dateMonthsSince[1960]"),
    @XmlEnumValue("dateMonthsSince[1970]")
    DATE_MONTHS_SINCE_1970("dateMonthsSince[1970]"),
    @XmlEnumValue("dateMonthsSince[1980]")
    DATE_MONTHS_SINCE_1980("dateMonthsSince[1980]"),
    @XmlEnumValue("dateYearsSince[0]")
    DATE_YEARS_SINCE_0("dateYearsSince[0]");
    private final String value;

    ENUMTIMEANCHOR(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ENUMTIMEANCHOR fromValue(String v) {
        for (ENUMTIMEANCHOR c: ENUMTIMEANCHOR.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
