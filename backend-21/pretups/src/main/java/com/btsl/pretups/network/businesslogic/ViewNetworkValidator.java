package com.btsl.pretups.network.businesslogic;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.fasterxml.jackson.databind.JsonNode;

public class ViewNetworkValidator {

	public static final Log _log = LogFactory
			.getLog(ViewNetworkValidator.class.getName());
	
	
	public void validateViewNetworkData(JsonNode requestData, PretupsResponse<?> response)
			throws BTSLBaseException {
		
		String methodName = "validateViewNetworkData";
		LogFactory.printLog(methodName, PretupsI.ENTERED, _log);
		String status=requestData.get("status").textValue();
	
		try{
			
			 if(requestData.get("loginId").textValue().isEmpty())
			 {
				 response.setFormError("network.viewNetwork.errors.loginId");
			 }
			
					
				 if(requestData.get("status").textValue().isEmpty())
				 {
					 response.setFormError("network.viewNetwork.errors.status");
						return;
				 }
				 
				 if(!status.equals("Y") && !status.equals("N") && !status.equals("S"))
					{
						response.setFormError("network.viewNetwork.errors.status");
						return;
					}
				
				 if(requestData.get("networkCode").textValue().isEmpty())
				 {
					 response.setFormError("network.viewNetwork.errors.networkCode");
						return;
				 }
				 
				 if(requestData.get("networkCode").textValue().length()>2)
					{
						response.setFormError("network.viewNetwork.errors.networkCode");
						return;
					}
			
			
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			finally {
				LogFactory.printLog(methodName, PretupsI.EXITED, _log);
			}
		
		}
	
	
	
	
	
	
	public void validateShowNetworkData(JsonNode requestData, PretupsResponse<?> response)
			throws BTSLBaseException {
		
		String methodName = "validateShowNetworkData";
		LogFactory.printLog(methodName, PretupsI.ENTERED, _log);
		try{
			
			 if(requestData.get("networkCode").textValue().isEmpty())
			 {
				 response.setFormError("network.viewNetwork.errors.networkCode");
					return;
			 }
			 
			 if(requestData.get("networkCode").textValue().length()>2)
				{
					response.setFormError("network.viewNetwork.errors.networkCode");
					return;
				}
			
		
		
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}

		finally {
			LogFactory.printLog(methodName, PretupsI.EXITED, _log);
		}
	
	}
	
}
