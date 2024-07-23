
package com.inter.claroca.multicurrency;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="Fcurr" type="{urn:sap-com:document:sap:rfc:functions}cuky5"/>
 *         &lt;element name="Gdatu" type="{urn:sap-com:document:sap:rfc:functions}date10" minOccurs="0"/>
 *         &lt;element name="Kurst" type="{urn:sap-com:document:sap:rfc:functions}char4" minOccurs="0"/>
 *         &lt;element name="Tcurr" type="{urn:sap-com:document:sap:rfc:functions}cuky5"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "fcurr",
    "gdatu",
    "kurst",
    "tcurr"
})
@XmlRootElement(name = "ZtipoDeCambio")
public class ZtipoDeCambio {

    @XmlElement(name = "Fcurr", required = true)
    protected String fcurr;
    @XmlElement(name = "Gdatu")
    protected String gdatu;
    @XmlElement(name = "Kurst")
    protected String kurst;
    @XmlElement(name = "Tcurr", required = true)
    protected String tcurr;

    /**
     * Gets the value of the fcurr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFcurr() {
        return fcurr;
    }

    /**
     * Sets the value of the fcurr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFcurr(String value) {
        this.fcurr = value;
    }

    /**
     * Gets the value of the gdatu property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGdatu() {
        return gdatu;
    }

    /**
     * Sets the value of the gdatu property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGdatu(String value) {
        this.gdatu = value;
    }

    /**
     * Gets the value of the kurst property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKurst() {
        return kurst;
    }

    /**
     * Sets the value of the kurst property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKurst(String value) {
        this.kurst = value;
    }

    /**
     * Gets the value of the tcurr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTcurr() {
        return tcurr;
    }

    /**
     * Sets the value of the tcurr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTcurr(String value) {
        this.tcurr = value;
    }

}
