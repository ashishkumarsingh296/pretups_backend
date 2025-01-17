// This class was generated by the JAXRPC SI, do not edit.
// Contents subject to change without notice.
// JAX-RPC Standard Implementation (1.1.3, build R1)
// Generated source version: 1.1.3

package com.inter.iat.client;

import com.sun.xml.rpc.encoding.*;
import com.sun.xml.rpc.encoding.xsd.XSDConstants;
import com.sun.xml.rpc.encoding.literal.*;
import com.sun.xml.rpc.encoding.literal.DetailFragmentDeserializer;
import com.sun.xml.rpc.encoding.simpletype.*;
import com.sun.xml.rpc.encoding.soap.SOAPConstants;
import com.sun.xml.rpc.encoding.soap.SOAP12Constants;
import com.sun.xml.rpc.streaming.*;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;
import javax.xml.namespace.QName;
import java.util.List;
import java.util.ArrayList;

public class CheckStatusRequest_LiteralSerializer extends LiteralObjectSerializerBase implements Initializable {
    private static final javax.xml.namespace.QName ns3_checkStatusParam_QNAME = new QName("http://com/wha/iat/pretups/ws", "checkStatusParam");
    private static final javax.xml.namespace.QName ns1_CheckStatusParam_TYPE_QNAME = new QName("java:com.wha.iat.pretups.ws", "CheckStatusParam");
    private CombinedSerializer ns1_myCheckStatusParam_LiteralSerializer;

    public CheckStatusRequest_LiteralSerializer(javax.xml.namespace.QName type, java.lang.String encodingStyle) {
        this(type, encodingStyle, false);
    }

    public CheckStatusRequest_LiteralSerializer(javax.xml.namespace.QName type, java.lang.String encodingStyle, boolean encodeType) {
        super(type, true, encodingStyle, encodeType);
    }

    public void initialize(InternalTypeMappingRegistry registry) throws Exception {
        ns1_myCheckStatusParam_LiteralSerializer = (CombinedSerializer) registry.getSerializer("", com.inter.iat.client.CheckStatusParam.class, ns1_CheckStatusParam_TYPE_QNAME);
    }

    public java.lang.Object doDeserialize(XMLReader reader, SOAPDeserializationContext context) throws java.lang.Exception {
        com.inter.iat.client.CheckStatusRequest instance = new com.inter.iat.client.CheckStatusRequest();
        java.lang.Object member = null;
        javax.xml.namespace.QName elementName;
        java.util.List values;
        java.lang.Object value;

        reader.nextElementContent();
        elementName = reader.getName();
        if (reader.getState() == XMLReader.START) {
            if (elementName.equals(ns3_checkStatusParam_QNAME)) {
                member = ns1_myCheckStatusParam_LiteralSerializer.deserialize(ns3_checkStatusParam_QNAME, reader, context);
                if (member == null) {
                    throw new DeserializationException("literal.unexpectedNull");
                }
                instance.setCheckStatusParam((com.inter.iat.client.CheckStatusParam) member);
                reader.nextElementContent();
            } else {
                throw new DeserializationException("literal.unexpectedElementName", new Object[] { ns3_checkStatusParam_QNAME, reader.getName() });
            }
        } else {
            throw new DeserializationException("literal.expectedElementName", reader.getName().toString());
        }

        XMLReaderUtil.verifyReaderState(reader, XMLReader.END);
        return (java.lang.Object) instance;
    }

    public void doSerializeAttributes(java.lang.Object obj, XMLWriter writer, SOAPSerializationContext context) throws java.lang.Exception {
        com.inter.iat.client.CheckStatusRequest instance = (com.inter.iat.client.CheckStatusRequest) obj;

    }

    public void doSerialize(java.lang.Object obj, XMLWriter writer, SOAPSerializationContext context) throws java.lang.Exception {
        com.inter.iat.client.CheckStatusRequest instance = (com.inter.iat.client.CheckStatusRequest) obj;

        if (instance.getCheckStatusParam() == null) {
            throw new SerializationException("literal.unexpectedNull");
        }
        ns1_myCheckStatusParam_LiteralSerializer.serialize(instance.getCheckStatusParam(), ns3_checkStatusParam_QNAME, null, writer, context);
    }
}
