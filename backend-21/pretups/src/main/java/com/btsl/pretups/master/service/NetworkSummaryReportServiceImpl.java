package com.btsl.pretups.master.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestClient;
import com.btsl.common.PretupsRestUtil;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;
import com.fasterxml.jackson.core.type.TypeReference;

/** 
 * This class implements NetworkSummaryReportService and define method for used for the NetworkSummaryReport generation
*/
@Service("networkSummaryReportService")
public class NetworkSummaryReportServiceImpl implements NetworkSummaryReportService {

	public static final Log _log = LogFactory.getLog(NetworkSummaryReportServiceImpl.class.getName());
	@Autowired
	private PretupsRestClient client;
	
	/**
	 * DownLoad modules for Network Summary Report
	 * 
	 * @return InputStream with the data returned from Db fetching
	 * @throws IOException
	  * @throws Exception
	 *         
	 */

	
	@SuppressWarnings("unchecked")
    @Override
    public InputStream download (String report_type , String from_date , String to_date,String networkCode) throws IOException, Exception  {
                final String methodName = "download";
				 InputStream inputStream = null;
				 String responseString = null;
                if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Entered ");
						_log.debug(methodName,BTSLUtil.logForgingReqParam(report_type));
						_log.debug(methodName,BTSLUtil.logForgingReqParam(from_date));
						_log.debug(methodName,BTSLUtil.logForgingReqParam(to_date));
						_log.debug(methodName,networkCode);
                }
                Map<String, Object> data = new HashMap<String, Object>();
                data.put("reporttype", report_type);
				data.put("fromDate" , from_date);
				data.put("toDate" ,to_date);
				data.put("networkCode" ,networkCode);
                Map<String, Object> object = new HashMap<String, Object>();
                object.put("data", data);
                try{
					//PretupsRestClient client = new PretupsRestClient();
					responseString =  client.postJSONRequest(object, PretupsI.NET_SUMM_DOWNLOAD);
					PretupsResponse<byte[]> response = (PretupsResponse<byte[]>) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<byte[]>>() {}); 

					byte[] bytes = (byte[]) response.getDataObject(); 
                    inputStream = new ByteArrayInputStream(bytes);


					
                 }
				catch(Exception e)
				{
					if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "NetworkSummaryReportServiceImpl Error : "+e);
						
					}

					_log.errorTrace(methodName, e);
                
				}finally{
					try{
						if(inputStream !=null){
							inputStream.close();
						}
					}catch(Exception e){
						_log.errorTrace(methodName, e);
					}
				}
                if (_log.isDebugEnabled()) {
							_log.debug(methodName, PretupsI.NET_SUMM_DOWNLOAD);
					   _log.debug(methodName, "Exiting");
                }
		
                return inputStream;
        }
                
			
        }






