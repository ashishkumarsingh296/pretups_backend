// This class was generated by the JAXRPC SI, do not edit.
// Contents subject to change without notice.
// JAX-RPC Standard Implementation (1.1.3, build R1)
// Generated source version: 1.1.3

package com.inter.safaricom.safaricomstub;

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

public class Mediator_transferAmount_ResponseStruct_Name_LiteralSerializer extends LiteralObjectSerializerBase implements Initializable  {
    private static final javax.xml.namespace.QName ns1_Response_QNAME = new QName("", "Response");
    private static final javax.xml.namespace.QName ns3_Response_TYPE_QNAME = new QName("https://med1-svr:8443/Mediator/", "Response");
    private CombinedSerializer ns3_myResponse_Name_LiteralSerializer;
    
    public Mediator_transferAmount_ResponseStruct_Name_LiteralSerializer(javax.xml.namespace.QName type, java.lang.String encodingStyle) {
        this(type, encodingStyle, false);
    }
    
    public Mediator_transferAmount_ResponseStruct_Name_LiteralSerializer(javax.xml.namespace.QName type, java.lang.String encodingStyle, boolean encodeType) {
        super(type, true, encodingStyle, encodeType);
    }
    
    public void initialize(InternalTypeMappingRegistry registry) throws Exception {
        ns3_myResponse_Name_LiteralSerializer = (CombinedSerializer)registry.getSerializer("", com.inter.safaricom.safaricomstub.Response.class, ns3_Response_TYPE_QNAME);
    }
    
    public java.lang.Object doDeserialize(XMLReader reader,
        SOAPDeserializationContext context) throws java.lang.Exception {
        com.inter.safaricom.safaricomstub.Mediator_transferAmount_ResponseStruct instance = new com.inter.safaricom.safaricomstub.Mediator_transferAmount_ResponseStruct();
        java.lang.Object member=null;
        javax.xml.namespace.QName elementName;
        java.util.List values;
        java.lang.Object value;
        
        reader.nextElementContent();
        elementName = reader.getName();
        if (reader.getState() == XMLReader.START) {
            if (elementName.equals(ns1_Response_QNAME)) {
                member = ns3_myResponse_Name_LiteralSerializer.deserialize(ns1_Response_QNAME, reader, context);
                if (member == null) {
                    throw new DeserializationException("literal.unexpectedNull");
                }
                instance.setResponse((com.inter.safaricom.safaricomstub.Response)member);
                reader.nextElementContent();
            } else {
                throw new DeserializationException("literal.unexpectedElementName", new Object[] { ns1_Response_QNAME, reader.getName() });
            }
        }
        else {
            throw new DeserializationException("literal.expectedElementName", reader.getName().toString());
        }
        
        XMLReaderUtil.verifyReaderState(reader, XMLReader.END);
        return (java.lang.Object)instance;
    }
    
    public void doSerializeAttributes(java.lang.Object obj, XMLWriter writer, SOAPSerializationContext context) throws java.lang.Exception {
        com.inter.safaricom.safaricomstub.Mediator_transferAmount_ResponseStruct instance = (com.inter.safaricom.safaricomstub.Mediator_transferAmount_ResponseStruct)obj;
        
    }
    public void doSerialize(java.lang.Object obj, XMLWriter writer, SOAPSerializationContext context) throws java.lang.Exception {
        com.inter.safaricom.safaricomstub.Mediator_transferAmount_ResponseStruct instance = (com.inter.safaricom.safaricomstub.Mediator_transferAmount_ResponseStruct)obj;
        
        if (instance.getResponse() == null) {
            throw new SerializationException("literal.unexpectedNull");
        }
        ns3_myResponse_Name_LiteralSerializer.serialize(instance.getResponse(), ns1_Response_QNAME, null, writer, context);
    }
}