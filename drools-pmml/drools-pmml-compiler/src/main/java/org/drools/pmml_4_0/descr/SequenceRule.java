
package org.drools.pmml_4_0.descr;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.dmg.org/PMML-4_0}Extension" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.dmg.org/PMML-4_0}AntecedentSequence"/>
 *         &lt;element ref="{http://www.dmg.org/PMML-4_0}Delimiter"/>
 *         &lt;element ref="{http://www.dmg.org/PMML-4_0}Time" minOccurs="0"/>
 *         &lt;element ref="{http://www.dmg.org/PMML-4_0}ConsequentSequence"/>
 *         &lt;element ref="{http://www.dmg.org/PMML-4_0}Time" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.dmg.org/PMML-4_0}ELEMENT-ID" />
 *       &lt;attribute name="numberOfSets" use="required" type="{http://www.dmg.org/PMML-4_0}INT-NUMBER" />
 *       &lt;attribute name="occurrence" use="required" type="{http://www.dmg.org/PMML-4_0}INT-NUMBER" />
 *       &lt;attribute name="support" use="required" type="{http://www.dmg.org/PMML-4_0}REAL-NUMBER" />
 *       &lt;attribute name="confidence" use="required" type="{http://www.dmg.org/PMML-4_0}REAL-NUMBER" />
 *       &lt;attribute name="lift" type="{http://www.dmg.org/PMML-4_0}REAL-NUMBER" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "extensions",
    "antecedentSequence",
    "delimiter",
    "timesAndConsequentSequences"
})
@XmlRootElement(name = "SequenceRule")
public class SequenceRule implements Serializable
{

    private final static long serialVersionUID = 145235L;
    @XmlElement(name = "Extension")
    protected List<Extension> extensions;
    @XmlElement(name = "AntecedentSequence", required = true)
    protected AntecedentSequence antecedentSequence;
    @XmlElement(name = "Delimiter", required = true)
    protected Delimiter delimiter;
    @XmlElements({
        @XmlElement(name = "ConsequentSequence", required = true, type = ConsequentSequence.class),
        @XmlElement(name = "Time", required = true, type = Time.class)
    })
    protected List<Serializable> timesAndConsequentSequences;
    @XmlAttribute(required = true)
    protected String id;
    @XmlAttribute(required = true)
    protected BigInteger numberOfSets;
    @XmlAttribute(required = true)
    protected BigInteger occurrence;
    @XmlAttribute(required = true)
    protected double support;
    @XmlAttribute(required = true)
    protected double confidence;
    @XmlAttribute
    protected Double lift;

    /**
     * Gets the value of the extensions property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the extensions property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExtensions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Extension }
     * 
     * 
     */
    public List<Extension> getExtensions() {
        if (extensions == null) {
            extensions = new ArrayList<Extension>();
        }
        return this.extensions;
    }

    /**
     * Gets the value of the antecedentSequence property.
     * 
     * @return
     *     possible object is
     *     {@link AntecedentSequence }
     *     
     */
    public AntecedentSequence getAntecedentSequence() {
        return antecedentSequence;
    }

    /**
     * Sets the value of the antecedentSequence property.
     * 
     * @param value
     *     allowed object is
     *     {@link AntecedentSequence }
     *     
     */
    public void setAntecedentSequence(AntecedentSequence value) {
        this.antecedentSequence = value;
    }

    /**
     * Gets the value of the delimiter property.
     * 
     * @return
     *     possible object is
     *     {@link Delimiter }
     *     
     */
    public Delimiter getDelimiter() {
        return delimiter;
    }

    /**
     * Sets the value of the delimiter property.
     * 
     * @param value
     *     allowed object is
     *     {@link Delimiter }
     *     
     */
    public void setDelimiter(Delimiter value) {
        this.delimiter = value;
    }

    /**
     * Gets the value of the timesAndConsequentSequences property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the timesAndConsequentSequences property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTimesAndConsequentSequences().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ConsequentSequence }
     * {@link Time }
     * 
     * 
     */
    public List<Serializable> getTimesAndConsequentSequences() {
        if (timesAndConsequentSequences == null) {
            timesAndConsequentSequences = new ArrayList<Serializable>();
        }
        return this.timesAndConsequentSequences;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the numberOfSets property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNumberOfSets() {
        return numberOfSets;
    }

    /**
     * Sets the value of the numberOfSets property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNumberOfSets(BigInteger value) {
        this.numberOfSets = value;
    }

    /**
     * Gets the value of the occurrence property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getOccurrence() {
        return occurrence;
    }

    /**
     * Sets the value of the occurrence property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setOccurrence(BigInteger value) {
        this.occurrence = value;
    }

    /**
     * Gets the value of the support property.
     * 
     */
    public double getSupport() {
        return support;
    }

    /**
     * Sets the value of the support property.
     * 
     */
    public void setSupport(double value) {
        this.support = value;
    }

    /**
     * Gets the value of the confidence property.
     * 
     */
    public double getConfidence() {
        return confidence;
    }

    /**
     * Sets the value of the confidence property.
     * 
     */
    public void setConfidence(double value) {
        this.confidence = value;
    }

    /**
     * Gets the value of the lift property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getLift() {
        return lift;
    }

    /**
     * Sets the value of the lift property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setLift(Double value) {
        this.lift = value;
    }

}
