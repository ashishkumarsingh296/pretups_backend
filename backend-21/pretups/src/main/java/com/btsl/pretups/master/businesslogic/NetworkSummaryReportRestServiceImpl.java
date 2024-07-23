package com.btsl.pretups.master.businesslogic;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import javax.ws.rs.core.Response.ResponseBuilder;

import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.processes.NetworkSummaryReportWriteInXL;
import com.btsl.util.BTSLUtil;
import com.fasterxml.jackson.core.type.TypeReference;
/*
 * This class implements LookupsRestService and provides basic method to load Look and sublookups
 */
public class NetworkSummaryReportRestServiceImpl implements  NetworkSummaryReportRestService {

        public static final Log _log = LogFactory.getLog(NetworkSummaryReportRestServiceImpl.class.getName());
         private static Locale _locale=null;
		 private File file=null;

        @SuppressWarnings("unchecked")
        @Override
        public PretupsResponse<byte[]> downloadMonthly(String requestData) throws IOException, Exception {
                final String METHOD_NAME = "downloadMonthly";
				ResponseBuilder responseBuilder = null;
				PretupsResponse<byte[]> response = new PretupsResponse<byte[]>();
				Map<String, Object> dataMap = (Map<String, Object>) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<Map<String, Object>>() {});
				Map<String, Object> map = (Map<String, Object>) dataMap.get("data");
				if(!map.containsKey("reporttype") ||!map.containsKey("fromDate") || !map.containsKey("toDate") || map.isEmpty()){
					_log.debug(METHOD_NAME, "NULL JSON KEYS"+ map.get("reporttype").toString() + map.get("fromDate").toString() +map.get("toDate").toString());
					response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, null);
					return response;
				}
				if (_log.isDebugEnabled()) {
                        _log.debug(METHOD_NAME, "Entered ");
                        _log.debug(METHOD_NAME, map.get("reporttype").toString());
						_log.debug(METHOD_NAME,  map.get("fromDate").toString());
                        _log.debug(METHOD_NAME,  map.get("toDate").toString());
                        _log.debug(METHOD_NAME,map.get("networkCode"));
                }
			
			InputStream inputStream = null;
			try
			{
					String from = map.get("fromDate").toString();
					String to = map.get("toDate").toString();
					String currentDate = map.get("fromDate").toString();
					if (_log.isDebugEnabled()) {
								_log.debug(METHOD_NAME, "Current date to be passed ",currentDate);
								
						}
					
					ArrayList<TransactionSummaryVO> networkSummaryDataList=new ArrayList<TransactionSummaryVO>();
					ArrayList<String> labelArray;
						String reportType =  map.get("reporttype").toString();
						

								 NetworkSummaryDAO networkSummaryDAO = new NetworkSummaryDAO();
								 networkSummaryDataList = networkSummaryDAO.loadNetworkSummaryDataList(  map.get("reporttype").toString(), map.get("networkCode").toString(), map.get("fromDate").toString(),map.get("toDate").toString(),map.get("fromDate").toString());
								 labelArray = generateLabelData(reportType);

								 NetworkSummaryReportWriteInXL writeContent = new NetworkSummaryReportWriteInXL();
								 String finalFileName = writeContent.write(reportType, labelArray, networkSummaryDataList);
						  if (_log.isDebugEnabled()) {
								_log.debug(METHOD_NAME, "the final file name obtained :",finalFileName);
						}

				
					file = new File(finalFileName);
					
					
					inputStream = new FileInputStream(file);
					try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();)
					{
						byte[] buffer = new byte[1024];
						
						int read = 0;
						while ((read = inputStream.read(buffer, 0, buffer.length)) != -1) {
							byteArrayOutputStream.write(buffer, 0, read);
						}		
						byteArrayOutputStream.flush();		
						
						
						if (_log.isDebugEnabled()) {
									_log.debug(METHOD_NAME, "Entered 22222 ");
							}
			
							response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, byteArrayOutputStream.toByteArray());
							return response;
				}   
			}catch (Exception e) {
							_log.errorTrace(METHOD_NAME, e);
							
								return null;
						} finally {
							
							if(inputStream!=null)
							{
								inputStream.close();
							}
							if(file!=null)
							{
								boolean isDeleted = file.delete();							
								if(isDeleted){
									_log.debug(METHOD_NAME, "File deleted successfully");
								}
							}
								_log.debug(METHOD_NAME, "Exited finally file also deleted 3333333333333333 ");
						}		
					
	}

  private ArrayList<String> generateLabelData(String reportType) {
                ArrayList<String> labelArrayData = new ArrayList<String>();
                  _locale = LocaleMasterCache.getLocaleFromCodeDetails("0");
                        String keyName=null;
                 if (PretupsI.DAILY_FILTER.equals(reportType)) {
                         keyName = BTSLUtil.getMessage(_locale,"nwreport.Date",null);
                         labelArrayData.add(keyName);
                 }
                 else if (PretupsI.MONTHLY_FILTER.equals(reportType)) {
                         keyName = BTSLUtil.getMessage(_locale,"nwreport.Month",null);
                         labelArrayData.add(keyName);
                 }
                 else {
                         keyName = BTSLUtil.getMessage(_locale,"nwreport.Time",null);
                         labelArrayData.add(keyName);
                 }
                 keyName = BTSLUtil.getMessage(_locale,"nwreport.networkcode",null);
                 labelArrayData.add(keyName);
                 keyName = BTSLUtil.getMessage(_locale,"nwreport.gatewaycode",null);
                 labelArrayData.add(keyName);
                 keyName = BTSLUtil.getMessage(_locale,"nwreport.category",null);
                 labelArrayData.add(keyName);
                 keyName = BTSLUtil.getMessage(_locale,"nwreport.service",null);
                 labelArrayData.add(keyName);
                 keyName = BTSLUtil.getMessage(_locale,"nwreport.subService",null);
                 labelArrayData.add(keyName);
                 keyName = BTSLUtil.getMessage(_locale,"nwreport.interfaceID",null);
                 labelArrayData.add(keyName);
                 keyName = BTSLUtil.getMessage(_locale,"nwreport.successfull.recharge",null);
                 labelArrayData.add(keyName);
                 keyName = BTSLUtil.getMessage(_locale,"nwreport.recharge.denom",null);
                 labelArrayData.add(keyName);
                 keyName = BTSLUtil.getMessage(_locale,"nwreport.servicetax",null);
                 labelArrayData.add(keyName);
                 keyName = BTSLUtil.getMessage(_locale,"nwreport.accessFee",null);
                 labelArrayData.add(keyName);
                 keyName = BTSLUtil.getMessage(_locale,"nwreport.talktime.value",null);
                 labelArrayData.add(keyName);
                 keyName = BTSLUtil.getMessage(_locale,"nwreport.totalRecharge.amt",null);
                 labelArrayData.add(keyName);
                 keyName = BTSLUtil.getMessage(_locale,"nwreport.failure.recharge",null);
                 labelArrayData.add(keyName);
                 keyName = BTSLUtil.getMessage(_locale,"nwreport.failure.amt",null);
                 labelArrayData.add(keyName);
                return labelArrayData;
        }

}
