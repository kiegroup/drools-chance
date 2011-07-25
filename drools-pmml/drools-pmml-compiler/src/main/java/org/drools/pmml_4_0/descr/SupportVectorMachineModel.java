
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
 *         &lt;element ref="{http://www.dmg.org/PMML-4_0}MiningSchema"/>
 *         &lt;element ref="{http://www.dmg.org/PMML-4_0}Output" minOccurs="0"/>
 *         &lt;element ref="{http://www.dmg.org/PMML-4_0}ModelStats" minOccurs="0"/>
 *         &lt;element ref="{http://www.dmg.org/PMML-4_0}ModelExplanation" minOccurs="0"/>
 *         &lt;element ref="{http://www.dmg.org/PMML-4_0}Targets" minOccurs="0"/>
 *         &lt;element ref="{http://www.dmg.org/PMML-4_0}LocalTransformations" minOccurs="0"/>
 *         &lt;sequence>
 *           &lt;choice>
 *             &lt;element ref="{http://www.dmg.org/PMML-4_0}LinearKernelType"/>
 *             &lt;element ref="{http://www.dmg.org/PMML-4_0}PolynomialKernelType"/>
 *             &lt;element ref="{http://www.dmg.org/PMML-4_0}RadialBasisKernelType"/>
 *             &lt;element ref="{http://www.dmg.org/PMML-4_0}SigmoidKernelType"/>
 *           &lt;/choice>
 *         &lt;/sequence>
 *         &lt;element ref="{http://www.dmg.org/PMML-4_0}VectorDictionary"/>
 *         &lt;element ref="{http://www.dmg.org/PMML-4_0}SupportVectorMachine" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://www.dmg.org/PMML-4_0}ModelVerification" minOccurs="0"/>
 *         &lt;element ref="{http://www.dmg.org/PMML-4_0}Extension" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="modelName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="functionName" use="required" type="{http://www.dmg.org/PMML-4_0}MINING-FUNCTION" />
 *       &lt;attribute name="algorithmName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="threshold" type="{http://www.dmg.org/PMML-4_0}REAL-NUMBER" default="0" />
 *       &lt;attribute name="svmRepresentation" type="{http://www.dmg.org/PMML-4_0}SVM-REPRESENTATION" default="SupportVectors" />
 *       &lt;attribute name="classificationMethod" type="{http://www.dmg.org/PMML-4_0}SVM-CLASSIFICATION-METHOD" default="OneAgainstAll" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "extensionsAndSupportVectorMachinesAndVectorDictionaries"
})
@XmlRootElement(name = "SupportVectorMachineModel")
public class SupportVectorMachineModel implements Serializable
{

    private final static long serialVersionUID = 145235L;
    @XmlElements({
        @XmlElement(name = "VectorDictionary", required = true, type = VectorDictionary.class),
        @XmlElement(name = "SupportVectorMachine", required = true, type = SupportVectorMachine.class),
        @XmlElement(name = "ModelStats", required = true, type = ModelStats.class),
        @XmlElement(name = "ModelVerification", required = true, type = ModelVerification.class),
        @XmlElement(name = "MiningSchema", required = true, type = MiningSchema.class),
        @XmlElement(name = "Output", required = true, type = Output.class),
        @XmlElement(name = "ModelExplanation", required = true, type = ModelExplanation.class),
        @XmlElement(name = "LinearKernelType", required = true, type = LinearKernelType.class),
        @XmlElement(name = "PolynomialKernelType", required = true, type = PolynomialKernelType.class),
        @XmlElement(name = "Targets", required = true, type = Targets.class),
        @XmlElement(name = "RadialBasisKernelType", required = true, type = RadialBasisKernelType.class),
        @XmlElement(name = "Extension", required = true, type = Extension.class),
        @XmlElement(name = "SigmoidKernelType", required = true, type = SigmoidKernelType.class),
        @XmlElement(name = "LocalTransformations", required = true, type = LocalTransformations.class)
    })
    protected List<Serializable> extensionsAndSupportVectorMachinesAndVectorDictionaries;
    @XmlAttribute
    protected String modelName;
    @XmlAttribute(required = true)
    protected MININGFUNCTION functionName;
    @XmlAttribute
    protected String algorithmName;
    @XmlAttribute
    protected Double threshold;
    @XmlAttribute
    protected SVMREPRESENTATION svmRepresentation;
    @XmlAttribute
    protected SVMCLASSIFICATIONMETHOD classificationMethod;

    /**
     * Gets the value of the extensionsAndSupportVectorMachinesAndVectorDictionaries property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the extensionsAndSupportVectorMachinesAndVectorDictionaries property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExtensionsAndSupportVectorMachinesAndVectorDictionaries().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VectorDictionary }
     * {@link SupportVectorMachine }
     * {@link ModelStats }
     * {@link ModelVerification }
     * {@link MiningSchema }
     * {@link Output }
     * {@link ModelExplanation }
     * {@link LinearKernelType }
     * {@link PolynomialKernelType }
     * {@link Targets }
     * {@link RadialBasisKernelType }
     * {@link Extension }
     * {@link SigmoidKernelType }
     * {@link LocalTransformations }
     * 
     * 
     */
    public List<Serializable> getExtensionsAndSupportVectorMachinesAndVectorDictionaries() {
        if (extensionsAndSupportVectorMachinesAndVectorDictionaries == null) {
            extensionsAndSupportVectorMachinesAndVectorDictionaries = new ArrayList<Serializable>();
        }
        return this.extensionsAndSupportVectorMachinesAndVectorDictionaries;
    }

    /**
     * Gets the value of the modelName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * Sets the value of the modelName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModelName(String value) {
        this.modelName = value;
    }

    /**
     * Gets the value of the functionName property.
     * 
     * @return
     *     possible object is
     *     {@link MININGFUNCTION }
     *     
     */
    public MININGFUNCTION getFunctionName() {
        return functionName;
    }

    /**
     * Sets the value of the functionName property.
     * 
     * @param value
     *     allowed object is
     *     {@link MININGFUNCTION }
     *     
     */
    public void setFunctionName(MININGFUNCTION value) {
        this.functionName = value;
    }

    /**
     * Gets the value of the algorithmName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAlgorithmName() {
        return algorithmName;
    }

    /**
     * Sets the value of the algorithmName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAlgorithmName(String value) {
        this.algorithmName = value;
    }

    /**
     * Gets the value of the threshold property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public double getThreshold() {
        if (threshold == null) {
            return  0.0D;
        } else {
            return threshold;
        }
    }

    /**
     * Sets the value of the threshold property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setThreshold(Double value) {
        this.threshold = value;
    }

    /**
     * Gets the value of the svmRepresentation property.
     * 
     * @return
     *     possible object is
     *     {@link SVMREPRESENTATION }
     *     
     */
    public SVMREPRESENTATION getSvmRepresentation() {
        if (svmRepresentation == null) {
            return SVMREPRESENTATION.SUPPORT_VECTORS;
        } else {
            return svmRepresentation;
        }
    }

    /**
     * Sets the value of the svmRepresentation property.
     * 
     * @param value
     *     allowed object is
     *     {@link SVMREPRESENTATION }
     *     
     */
    public void setSvmRepresentation(SVMREPRESENTATION value) {
        this.svmRepresentation = value;
    }

    /**
     * Gets the value of the classificationMethod property.
     * 
     * @return
     *     possible object is
     *     {@link SVMCLASSIFICATIONMETHOD }
     *     
     */
    public SVMCLASSIFICATIONMETHOD getClassificationMethod() {
        if (classificationMethod == null) {
            return SVMCLASSIFICATIONMETHOD.ONE_AGAINST_ALL;
        } else {
            return classificationMethod;
        }
    }

    /**
     * Sets the value of the classificationMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link SVMCLASSIFICATIONMETHOD }
     *     
     */
    public void setClassificationMethod(SVMCLASSIFICATIONMETHOD value) {
        this.classificationMethod = value;
    }

}
