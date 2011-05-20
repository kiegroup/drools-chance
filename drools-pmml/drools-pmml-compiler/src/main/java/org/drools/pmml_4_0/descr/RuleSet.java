//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.12.10 at 02:27:43 AM CET 
//


package org.drools.pmml_4_0.descr;

import java.io.Serializable;
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
 *         &lt;element ref="{http://www.dmg.org/PMML-4_0}RuleSelectionMethod" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://www.dmg.org/PMML-4_0}ScoreDistribution" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;group ref="{http://www.dmg.org/PMML-4_0}Rule" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="recordCount" type="{http://www.dmg.org/PMML-4_0}NUMBER" />
 *       &lt;attribute name="nbCorrect" type="{http://www.dmg.org/PMML-4_0}NUMBER" />
 *       &lt;attribute name="defaultScore" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="defaultConfidence" type="{http://www.dmg.org/PMML-4_0}NUMBER" />
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
    "ruleSelectionMethods",
    "scoreDistributions",
    "simpleRulesAndCompoundRules"
})
@XmlRootElement(name = "RuleSet")
public class RuleSet implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "Extension")
    protected List<Extension> extensions;
    @XmlElement(name = "RuleSelectionMethod", required = true)
    protected List<RuleSelectionMethod> ruleSelectionMethods;
    @XmlElement(name = "ScoreDistribution")
    protected List<ScoreDistribution> scoreDistributions;
    @XmlElements({
        @XmlElement(name = "SimpleRule", type = SimpleRule.class),
        @XmlElement(name = "CompoundRule", type = CompoundRule.class)
    })
    protected List<Serializable> simpleRulesAndCompoundRules;
    @XmlAttribute
    protected Double recordCount;
    @XmlAttribute
    protected Double nbCorrect;
    @XmlAttribute
    protected String defaultScore;
    @XmlAttribute
    protected Double defaultConfidence;

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
     * Gets the value of the ruleSelectionMethods property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ruleSelectionMethods property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRuleSelectionMethods().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RuleSelectionMethod }
     * 
     * 
     */
    public List<RuleSelectionMethod> getRuleSelectionMethods() {
        if (ruleSelectionMethods == null) {
            ruleSelectionMethods = new ArrayList<RuleSelectionMethod>();
        }
        return this.ruleSelectionMethods;
    }

    /**
     * Gets the value of the scoreDistributions property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the scoreDistributions property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getScoreDistributions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ScoreDistribution }
     * 
     * 
     */
    public List<ScoreDistribution> getScoreDistributions() {
        if (scoreDistributions == null) {
            scoreDistributions = new ArrayList<ScoreDistribution>();
        }
        return this.scoreDistributions;
    }

    /**
     * Gets the value of the simpleRulesAndCompoundRules property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the simpleRulesAndCompoundRules property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSimpleRulesAndCompoundRules().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SimpleRule }
     * {@link CompoundRule }
     * 
     * 
     */
    public List<Serializable> getSimpleRulesAndCompoundRules() {
        if (simpleRulesAndCompoundRules == null) {
            simpleRulesAndCompoundRules = new ArrayList<Serializable>();
        }
        return this.simpleRulesAndCompoundRules;
    }

    /**
     * Gets the value of the recordCount property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getRecordCount() {
        return recordCount;
    }

    /**
     * Sets the value of the recordCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setRecordCount(Double value) {
        this.recordCount = value;
    }

    /**
     * Gets the value of the nbCorrect property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getNbCorrect() {
        return nbCorrect;
    }

    /**
     * Sets the value of the nbCorrect property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setNbCorrect(Double value) {
        this.nbCorrect = value;
    }

    /**
     * Gets the value of the defaultScore property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultScore() {
        return defaultScore;
    }

    /**
     * Sets the value of the defaultScore property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultScore(String value) {
        this.defaultScore = value;
    }

    /**
     * Gets the value of the defaultConfidence property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getDefaultConfidence() {
        return defaultConfidence;
    }

    /**
     * Sets the value of the defaultConfidence property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setDefaultConfidence(Double value) {
        this.defaultConfidence = value;
    }

}