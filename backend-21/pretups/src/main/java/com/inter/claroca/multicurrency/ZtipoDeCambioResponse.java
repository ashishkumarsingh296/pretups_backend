package com.inter.claroca.multicurrency;

import java.math.BigDecimal;
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
 *         &lt;element name="Ukurs" type="{urn:sap-com:document:sap:rfc:functions}decimal9.5"/>
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
    "ukurs"
})
@XmlRootElement(name = "ZtipoDeCambioResponse")
public class ZtipoDeCambioResponse {

    @XmlElement(name = "Ukurs", required = true)
    protected BigDecimal ukurs;

    /**
     * Gets the value of the ukurs property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getUkurs() {
        return ukurs;
    }

    /**
     * Sets the value of the ukurs property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setUkurs(BigDecimal value) {
        this.ukurs = value;
    }

}
