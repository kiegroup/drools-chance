
package org.drools.pmml_4_0.descr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element ref="{http://www.dmg.org/PMML-4_0}Con" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.dmg.org/PMML-4_0}NN-NEURON-ID" />
 *       &lt;attribute name="bias" type="{http://www.dmg.org/PMML-4_0}REAL-NUMBER" />
 *       &lt;attribute name="width" type="{http://www.dmg.org/PMML-4_0}REAL-NUMBER" />
 *       &lt;attribute name="altitude" type="{http://www.dmg.org/PMML-4_0}REAL-NUMBER" />
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
    "cons"
})
@XmlRootElement(name = "Neuron")
public class Neuron
    implements Serializable
{

    private final static long serialVersionUID = 145235L;
    @XmlElement(name = "Extension")
    protected List<Extension> extensions;
    @XmlElement(name = "Con", required = true)
    protected List<Synapse> cons;
    @XmlAttribute(required = true)
    protected String id;
    @XmlAttribute
    protected Double bias;
    @XmlAttribute
    protected Double width;
    @XmlAttribute
    protected Double altitude;

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
     * Gets the value of the cons property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cons property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCons().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Synapse }
     * 
     * 
     */
    public List<Synapse> getCons() {
        if (cons == null) {
            cons = new ArrayList<Synapse>();
        }
        return this.cons;
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
     * Gets the value of the bias property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getBias() {
        return bias;
    }

    /**
     * Sets the value of the bias property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setBias(Double value) {
        this.bias = value;
    }

    /**
     * Gets the value of the width property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getWidth() {
        return width;
    }

    /**
     * Sets the value of the width property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setWidth(Double value) {
        this.width = value;
    }

    /**
     * Gets the value of the altitude property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getAltitude() {
        return altitude;
    }

    /**
     * Sets the value of the altitude property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setAltitude(Double value) {
        this.altitude = value;
    }

}
