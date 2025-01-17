// This class was generated by the JAXRPC SI, do not edit.
// Contents subject to change without notice.
// JAX-RPC Standard Implementation (1.1.3, build R1)
// Generated source version: 1.1.3

package com.inter.iat.client;

import com.sun.xml.rpc.server.http.MessageContextProperties;
import com.sun.xml.rpc.streaming.*;
import com.sun.xml.rpc.encoding.*;
import com.sun.xml.rpc.encoding.soap.SOAPConstants;
import com.sun.xml.rpc.encoding.soap.SOAP12Constants;
import com.sun.xml.rpc.encoding.literal.*;
import com.sun.xml.rpc.soap.streaming.*;
import com.sun.xml.rpc.soap.message.*;
import com.sun.xml.rpc.soap.SOAPVersion;
import com.sun.xml.rpc.soap.SOAPEncodingConstants;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;
import javax.xml.namespace.QName;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.lang.reflect.*;
import java.lang.Class;
import com.sun.xml.rpc.client.SenderException;
import com.sun.xml.rpc.client.*;
import com.sun.xml.rpc.client.http.*;
import javax.xml.rpc.handler.*;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.soap.SOAPFaultException;

public class IATHUB_pretups_Stub extends com.sun.xml.rpc.client.StubBase implements com.inter.iat.client.IATHUB_pretups {

    /*
     * public constructor
     */
    public IATHUB_pretups_Stub(HandlerChain handlerChain) {
        super(handlerChain);
        _setProperty(ENDPOINT_ADDRESS_PROPERTY, "http://localhost:7001/IAT_HUB_WS_old/IATHUB_pretups");
    }

    /*
     * implementation of rechargeRequest
     */
    public void rechargeRequest(com.inter.iat.client.RechargeParam rechargeParam, com.inter.iat.client.holders.RechargeResultHolder _return) throws java.rmi.RemoteException {

        if (_return == null) {
            throw new IllegalArgumentException("_return cannot be null");
        }
        try {

            StreamingSenderState _state = _start(_handlerChain);

            InternalSOAPMessage _request = _state.getRequest();
            _request.setOperationCode(rechargeRequest_OPCODE);

            com.inter.iat.client.RechargeRequest _myRechargeRequest = new com.inter.iat.client.RechargeRequest();
            _myRechargeRequest.setRechargeParam(rechargeParam);

            SOAPBlockInfo _bodyBlock = new SOAPBlockInfo(ns1_rechargeRequest_rechargeRequest_QNAME);
            _bodyBlock.setValue(_myRechargeRequest);
            _bodyBlock.setSerializer(ns1_myRechargeRequest_LiteralSerializer);
            _request.setBody(_bodyBlock);

            _state.getMessageContext().setProperty(HttpClientTransport.HTTP_SOAPACTION_PROPERTY, "");

            _send((java.lang.String) _getProperty(ENDPOINT_ADDRESS_PROPERTY), _state);

            com.inter.iat.client.RechargeRequestResponse _result = null;
            java.lang.Object _responseObj = _state.getResponse().getBody().getValue();
            if (_responseObj instanceof SOAPDeserializationState) {
                _result = (com.inter.iat.client.RechargeRequestResponse) ((SOAPDeserializationState) _responseObj).getInstance();
            } else {
                _result = (com.inter.iat.client.RechargeRequestResponse) _responseObj;
            }

            // found : com.btsl.pretups.inter.iat.client.RechargeRequestResponse
            // required: com.btsl.pretups.inter.iat.client.RechargeResult
            // Casting problem

            // Old Value, generated by wscompile
            // _return.value = _result;

            // New Value, changed by comviva team, for type casting

            _return.value = _result.get_return();

        } catch (RemoteException e) {
            // let this one through unchanged
            throw e;
        } catch (JAXRPCException e) {
            throw new RemoteException(e.getMessage(), e);
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RemoteException(e.getMessage(), e);
            }
        }
    }

    /*
     * implementation of checkStatusRequest
     */
    public void checkStatusRequest(com.inter.iat.client.CheckStatusParam checkStatusParam, com.inter.iat.client.holders.CheckStatusResultHolder _return) throws java.rmi.RemoteException {

        if (_return == null) {
            throw new IllegalArgumentException("_return cannot be null");
        }
        try {

            StreamingSenderState _state = _start(_handlerChain);

            InternalSOAPMessage _request = _state.getRequest();
            _request.setOperationCode(checkStatusRequest_OPCODE);

            com.inter.iat.client.CheckStatusRequest _myCheckStatusRequest = new com.inter.iat.client.CheckStatusRequest();
            _myCheckStatusRequest.setCheckStatusParam(checkStatusParam);

            SOAPBlockInfo _bodyBlock = new SOAPBlockInfo(ns1_checkStatusRequest_checkStatusRequest_QNAME);
            _bodyBlock.setValue(_myCheckStatusRequest);
            _bodyBlock.setSerializer(ns1_myCheckStatusRequest_LiteralSerializer);
            _request.setBody(_bodyBlock);

            _state.getMessageContext().setProperty(HttpClientTransport.HTTP_SOAPACTION_PROPERTY, "");

            _send((java.lang.String) _getProperty(ENDPOINT_ADDRESS_PROPERTY), _state);

            com.inter.iat.client.CheckStatusRequestResponse _result = null;
            java.lang.Object _responseObj = _state.getResponse().getBody().getValue();
            if (_responseObj instanceof SOAPDeserializationState) {
                _result = (com.inter.iat.client.CheckStatusRequestResponse) ((SOAPDeserializationState) _responseObj).getInstance();
            } else {
                _result = (com.inter.iat.client.CheckStatusRequestResponse) _responseObj;
            }

            // found :
            // com.btsl.pretups.inter.iat.client.CheckStatusRequestResponse
            // required: com.btsl.pretups.inter.iat.client.CheckStatusResult
            // Casting problem
            _return.value = _result.get_return();

        } catch (RemoteException e) {
            // let this one through unchanged
            throw e;
        } catch (JAXRPCException e) {
            throw new RemoteException(e.getMessage(), e);
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RemoteException(e.getMessage(), e);
            }
        }
    }

    /*
     * implementation of quotationRequest
     */
    public void quotationRequest(com.inter.iat.client.QuotationParam quotationParam, com.inter.iat.client.holders.CheckStatusResultHolder _return) throws java.rmi.RemoteException {

        if (_return == null) {
            throw new IllegalArgumentException("_return cannot be null");
        }
        try {

            StreamingSenderState _state = _start(_handlerChain);

            InternalSOAPMessage _request = _state.getRequest();
            _request.setOperationCode(quotationRequest_OPCODE);

            com.inter.iat.client.QuotationRequest _myQuotationRequest = new com.inter.iat.client.QuotationRequest();
            _myQuotationRequest.setQuotationParam(quotationParam);

            SOAPBlockInfo _bodyBlock = new SOAPBlockInfo(ns1_quotationRequest_quotationRequest_QNAME);
            _bodyBlock.setValue(_myQuotationRequest);
            _bodyBlock.setSerializer(ns1_myQuotationRequest_LiteralSerializer);
            _request.setBody(_bodyBlock);

            _state.getMessageContext().setProperty(HttpClientTransport.HTTP_SOAPACTION_PROPERTY, "");

            _send((java.lang.String) _getProperty(ENDPOINT_ADDRESS_PROPERTY), _state);

            com.inter.iat.client.QuotationRequestResponse _result = null;
            java.lang.Object _responseObj = _state.getResponse().getBody().getValue();
            if (_responseObj instanceof SOAPDeserializationState) {
                _result = (com.inter.iat.client.QuotationRequestResponse) ((SOAPDeserializationState) _responseObj).getInstance();
            } else {
                _result = (com.inter.iat.client.QuotationRequestResponse) _responseObj;
            }

            // found :
            // com.btsl.pretups.inter.iat.client.QuotationRequestResponse
            // required: com.btsl.pretups.inter.iat.client.CheckStatusResult
            // Casting problem
            _return.value = _result.get_return();

        } catch (RemoteException e) {
            // let this one through unchanged
            throw e;
        } catch (JAXRPCException e) {
            throw new RemoteException(e.getMessage(), e);
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RemoteException(e.getMessage(), e);
            }
        }
    }

    /*
     * this method deserializes the request/response structure in the body
     */
    protected void _readFirstBodyElement(XMLReader bodyReader, SOAPDeserializationContext deserializationContext, StreamingSenderState state) throws Exception {
        int opcode = state.getRequest().getOperationCode();
        switch (opcode) {
        case rechargeRequest_OPCODE:
            _deserialize_rechargeRequest(bodyReader, deserializationContext, state);
            break;
        case checkStatusRequest_OPCODE:
            _deserialize_checkStatusRequest(bodyReader, deserializationContext, state);
            break;
        case quotationRequest_OPCODE:
            _deserialize_quotationRequest(bodyReader, deserializationContext, state);
            break;
        default:
            throw new SenderException("sender.response.unrecognizedOperation", java.lang.Integer.toString(opcode));
        }
    }

    /*
     * This method deserializes the body of the rechargeRequest operation.
     */
    private void _deserialize_rechargeRequest(XMLReader bodyReader, SOAPDeserializationContext deserializationContext, StreamingSenderState state) throws Exception {
        java.lang.Object myRechargeRequestResponseObj = ns1_myRechargeRequestResponse_LiteralSerializer.deserialize(ns1_rechargeRequest_rechargeRequestResponse_QNAME, bodyReader, deserializationContext);

        SOAPBlockInfo bodyBlock = new SOAPBlockInfo(ns1_rechargeRequest_rechargeRequestResponse_QNAME);
        bodyBlock.setValue(myRechargeRequestResponseObj);
        state.getResponse().setBody(bodyBlock);
    }

    /*
     * This method deserializes the body of the checkStatusRequest operation.
     */
    private void _deserialize_checkStatusRequest(XMLReader bodyReader, SOAPDeserializationContext deserializationContext, StreamingSenderState state) throws Exception {
        java.lang.Object myCheckStatusRequestResponseObj = ns1_myCheckStatusRequestResponse_LiteralSerializer.deserialize(ns1_checkStatusRequest_checkStatusRequestResponse_QNAME, bodyReader, deserializationContext);

        SOAPBlockInfo bodyBlock = new SOAPBlockInfo(ns1_checkStatusRequest_checkStatusRequestResponse_QNAME);
        bodyBlock.setValue(myCheckStatusRequestResponseObj);
        state.getResponse().setBody(bodyBlock);
    }

    /*
     * This method deserializes the body of the quotationRequest operation.
     */
    private void _deserialize_quotationRequest(XMLReader bodyReader, SOAPDeserializationContext deserializationContext, StreamingSenderState state) throws Exception {
        java.lang.Object myQuotationRequestResponseObj = ns1_myQuotationRequestResponse_LiteralSerializer.deserialize(ns1_quotationRequest_quotationRequestResponse_QNAME, bodyReader, deserializationContext);

        SOAPBlockInfo bodyBlock = new SOAPBlockInfo(ns1_quotationRequest_quotationRequestResponse_QNAME);
        bodyBlock.setValue(myQuotationRequestResponseObj);
        state.getResponse().setBody(bodyBlock);
    }

    protected java.lang.String _getDefaultEnvelopeEncodingStyle() {
        return null;
    }

    public java.lang.String _getImplicitEnvelopeEncodingStyle() {
        return "";
    }

    public java.lang.String _getEncodingStyle() {
        return SOAPNamespaceConstants.ENCODING;
    }

    public void _setEncodingStyle(java.lang.String encodingStyle) {
        throw new UnsupportedOperationException("cannot set encoding style");
    }

    /*
     * This method returns an array containing (prefix, nsURI) pairs.
     */
    protected java.lang.String[] _getNamespaceDeclarations() {
        return myNamespace_declarations;
    }

    /*
     * This method returns an array containing the names of the headers we
     * understand.
     */
    public javax.xml.namespace.QName[] _getUnderstoodHeaders() {
        return understoodHeaderNames;
    }

    protected void _preHandlingHook(StreamingSenderState state) throws Exception {
        super._preHandlingHook(state);
    }

    protected boolean _preRequestSendingHook(StreamingSenderState state) throws Exception {
        boolean bool = false;
        bool = super._preRequestSendingHook(state);
        return bool;
    }

    public void _initialize(InternalTypeMappingRegistry registry) throws Exception {
        super._initialize(registry);
        ns1_myCheckStatusRequestResponse_LiteralSerializer = (CombinedSerializer) registry.getSerializer("", com.inter.iat.client.CheckStatusRequestResponse.class, ns1_checkStatusRequestResponse_TYPE_QNAME);
        ns1_myQuotationRequest_LiteralSerializer = (CombinedSerializer) registry.getSerializer("", com.inter.iat.client.QuotationRequest.class, ns1_quotationRequest_TYPE_QNAME);
        ns1_myCheckStatusRequest_LiteralSerializer = (CombinedSerializer) registry.getSerializer("", com.inter.iat.client.CheckStatusRequest.class, ns1_checkStatusRequest_TYPE_QNAME);
        ns1_myRechargeRequestResponse_LiteralSerializer = (CombinedSerializer) registry.getSerializer("", com.inter.iat.client.RechargeRequestResponse.class, ns1_rechargeRequestResponse_TYPE_QNAME);
        ns1_myRechargeRequest_LiteralSerializer = (CombinedSerializer) registry.getSerializer("", com.inter.iat.client.RechargeRequest.class, ns1_rechargeRequest_TYPE_QNAME);
        ns1_myQuotationRequestResponse_LiteralSerializer = (CombinedSerializer) registry.getSerializer("", com.inter.iat.client.QuotationRequestResponse.class, ns1_quotationRequestResponse_TYPE_QNAME);
    }

    private static final javax.xml.namespace.QName _portName = new QName("http://com/wha/iat/pretups/ws", "IATHUB_pretupsSoapPort");
    private static final int rechargeRequest_OPCODE = 0;
    private static final int checkStatusRequest_OPCODE = 1;
    private static final int quotationRequest_OPCODE = 2;
    private static final javax.xml.namespace.QName ns1_rechargeRequest_rechargeRequest_QNAME = new QName("http://com/wha/iat/pretups/ws", "rechargeRequest");
    private static final javax.xml.namespace.QName ns1_rechargeRequest_TYPE_QNAME = new QName("http://com/wha/iat/pretups/ws", "rechargeRequest");
    private CombinedSerializer ns1_myRechargeRequest_LiteralSerializer;
    private static final javax.xml.namespace.QName ns1_rechargeRequest_rechargeRequestResponse_QNAME = new QName("http://com/wha/iat/pretups/ws", "rechargeRequestResponse");
    private static final javax.xml.namespace.QName ns1_rechargeRequestResponse_TYPE_QNAME = new QName("http://com/wha/iat/pretups/ws", "rechargeRequestResponse");
    private CombinedSerializer ns1_myRechargeRequestResponse_LiteralSerializer;
    private static final javax.xml.namespace.QName ns1_checkStatusRequest_checkStatusRequest_QNAME = new QName("http://com/wha/iat/pretups/ws", "checkStatusRequest");
    private static final javax.xml.namespace.QName ns1_checkStatusRequest_TYPE_QNAME = new QName("http://com/wha/iat/pretups/ws", "checkStatusRequest");
    private CombinedSerializer ns1_myCheckStatusRequest_LiteralSerializer;
    private static final javax.xml.namespace.QName ns1_checkStatusRequest_checkStatusRequestResponse_QNAME = new QName("http://com/wha/iat/pretups/ws", "checkStatusRequestResponse");
    private static final javax.xml.namespace.QName ns1_checkStatusRequestResponse_TYPE_QNAME = new QName("http://com/wha/iat/pretups/ws", "checkStatusRequestResponse");
    private CombinedSerializer ns1_myCheckStatusRequestResponse_LiteralSerializer;
    private static final javax.xml.namespace.QName ns1_quotationRequest_quotationRequest_QNAME = new QName("http://com/wha/iat/pretups/ws", "quotationRequest");
    private static final javax.xml.namespace.QName ns1_quotationRequest_TYPE_QNAME = new QName("http://com/wha/iat/pretups/ws", "quotationRequest");
    private CombinedSerializer ns1_myQuotationRequest_LiteralSerializer;
    private static final javax.xml.namespace.QName ns1_quotationRequest_quotationRequestResponse_QNAME = new QName("http://com/wha/iat/pretups/ws", "quotationRequestResponse");
    private static final javax.xml.namespace.QName ns1_quotationRequestResponse_TYPE_QNAME = new QName("http://com/wha/iat/pretups/ws", "quotationRequestResponse");
    private CombinedSerializer ns1_myQuotationRequestResponse_LiteralSerializer;
    private static final java.lang.String[] myNamespace_declarations = new java.lang.String[] { "ns0", "http://com/wha/iat/pretups/ws" };

    private static final QName[] understoodHeaderNames = new QName[] {};
}
