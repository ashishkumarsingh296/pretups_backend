/*
 * Created on Aug 16, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package simulator.loadtest.report;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.voms.util.VomsDecryputil;

/**
 * @author temp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GenerateLoadReport 
{
	 private static Log _log = LogFactory.getLog(GenerateLoadReport.class.getName());
	private static HashMap map = new HashMap();
	private static Date startTime;
	private static Date endTime;
	private static long totalRequestReceived;
	static Object[]  accInfoList={"0","126"};
	static Object[] adjustList={"0","123"};
	static Object[] refillList={"0","1","2"};
	
	private static void processFile(String fileName) throws Exception	
	{
		final String methodName = "processFile";
		BufferedReader bufferedReader = null;
		System.out.println("File Reading Start and Processing data one by one.........");
		File file = new File(fileName);
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
			String str = "";
			
			while( (str= bufferedReader.readLine()) != null)
			{
				LoadRequestVO loadRequestVO = populateVO(str);
				populateData(loadRequestVO);
			}
		} catch (Exception e) 
		{			

			_log.errorTrace(methodName,e);
			throw e;
		}
		finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {

					_log.errorTrace(methodName,e);
				}
			}
			System.out.println("Process File exiting Data Populated");
			;
		}
		
	}
	

	private static LoadRequestVO populateVO(String str)
	{
		LoadRequestVO loadRequestVO = new LoadRequestVO();
		int index = str.indexOf("Total Time :");
		int eindex = str.indexOf("]",index);
		String totalTime = str.substring(index+"Total Time :".length(),eindex);

		
		index = str.indexOf("Request Type :",eindex);
		eindex = str.indexOf("]",index);
		String requestType = str.substring(index+"Request Type :".length(),eindex);
		
		index = str.indexOf("Response Status :",eindex);
		eindex = str.indexOf("]",index);
		String requestStatus = str.substring(index+("Response Status :").length(),eindex);
		
		index = str.indexOf("Node Name :",eindex);
		eindex = str.indexOf("]",index);
		String nodeName = str.substring(index+("Node Name :").length(),eindex);
		
		loadRequestVO.setTimeDiff(Long.parseLong(totalTime));
		loadRequestVO.setRequestType(requestType);
		loadRequestVO.setRequestStatus(requestStatus);
		loadRequestVO.setNodeName(nodeName);
		
		return loadRequestVO;
		
	}
	
	private static void populateData(LoadRequestVO loadRequestVO)
	{
		   NodeReportVO nodeReportVO = null;
		   
		   if(map.containsKey(loadRequestVO.getNodeName()))
		   {
		   	nodeReportVO = (NodeReportVO)map.get(loadRequestVO.getNodeName());			   		
		   }
		   else
		   {
		   		nodeReportVO = new NodeReportVO();
		   		nodeReportVO.setNodeIP(loadRequestVO.getNodeName());
		   		map.put(loadRequestVO.getNodeName(),nodeReportVO);		   			   		
		   }
		   
		   if("VALIDATE".equals(loadRequestVO.getRequestType()))
		   {
		   		nodeReportVO.incrementAccountInfoTotalRequest();
	            if(Arrays.asList(accInfoList).contains(loadRequestVO.getRequestStatus()))
	            {
	            	nodeReportVO.incrementAccountInfoSuccessfulRequest();
	            }
	            if(loadRequestVO.getTimeDiff() <= nodeReportVO.getAccountInfoMinTime())
	            {
	            	nodeReportVO.setAccountInfoMinTime(loadRequestVO.getTimeDiff());
	            }
	            
	            if(loadRequestVO.getTimeDiff() >= nodeReportVO.getAccountInfoMaxTime() )
	            {
	            	nodeReportVO.setAccountInfoMaxTime(loadRequestVO.getTimeDiff());
	            }
	            nodeReportVO.setAccountInfoTotTime( nodeReportVO.getAccountInfoTotTime() + loadRequestVO.getTimeDiff() );
	            nodeReportVO.setAccountInfoAvgTime( (nodeReportVO.getAccountInfoTotTime()/nodeReportVO.getAccountInfoTotalRequest()) );
		   }
		   else
		   if("CREDIT".equals(loadRequestVO.getRequestType()))
		   {
		   		nodeReportVO.incrementRefillTotalRequest();
	            if(Arrays.asList(refillList).contains(loadRequestVO.getRequestStatus()))
	            {
	            	nodeReportVO.incrementRefillSuccessfulRequest();
	            }

	            if(loadRequestVO.getTimeDiff() <= nodeReportVO.getRefillInfoMinTime())
	            {
	            	nodeReportVO.setRefillInfoMinTime(loadRequestVO.getTimeDiff());
	            }
	            
	            if(loadRequestVO.getTimeDiff() >= nodeReportVO.getRefillInfoMaxTime() )
	            {
	            	nodeReportVO.setRefillInfoMaxTime(loadRequestVO.getTimeDiff());
	            }
	            
	            nodeReportVO.setRefillTotTime( nodeReportVO.getRefillTotTime() + loadRequestVO.getTimeDiff() );
	            nodeReportVO.setRefillInfoAvgTime( ( nodeReportVO.getRefillTotTime()/nodeReportVO.getRefillTotalRequest()) );
		   	
		   }
		   else
		   if("CREDIT_ADJUST".equals(loadRequestVO.getRequestType()) || "DEBIT_ADJUST".equals(loadRequestVO.getRequestType()) )
		   {
		   		nodeReportVO.incrementUpdateTotalRequest();
	            if(Arrays.asList(adjustList).contains(loadRequestVO.getRequestStatus()))
	            {
	            	nodeReportVO.incrementUpdateSuccessfulRequest();
	            }

	            if(loadRequestVO.getTimeDiff() <= nodeReportVO.getUpdateInfoMinTime())
	            {
	            	nodeReportVO.setUpdateInfoMinTime(loadRequestVO.getTimeDiff());
	            }

	            if(loadRequestVO.getTimeDiff() >= nodeReportVO.getUpdateInfoMaxTime() )
	            {
	            	nodeReportVO.setUpdateInfoMaxTime(loadRequestVO.getTimeDiff());
	            }
	            
	            nodeReportVO.setUpdateTotTime( nodeReportVO.getUpdateTotTime() + loadRequestVO.getTimeDiff() );
	            nodeReportVO.setUpdateInfoAvgTime( ( nodeReportVO.getUpdateTotTime()/nodeReportVO.getUpdateTotalRequest()) );
		   	
		   }
	}
	
	
	private static void showData()
	{
		Iterator iterator =  map.keySet().iterator();
		
		NodeReportVO mainNode = new NodeReportVO();
		mainNode.setNodeIP("Main details");
		long mainAccInfoAvgTime=0;
		long mainRefillInfoAvgTime=0;
		long mainUpdateInfoAvgTime=0;
		while(iterator.hasNext())
		{
			NodeReportVO nodeReportVO = (NodeReportVO) map.get(iterator.next());			
			System.out.println(nodeReportVO.toString());
			
			mainNode.setAccountInfoTotalRequest( mainNode.getAccountInfoTotalRequest() + nodeReportVO.getAccountInfoTotalRequest() );
			mainNode.setAccountInfoSuccessfulRequest( mainNode.getAccountInfoSuccessfulRequest() + nodeReportVO.getAccountInfoSuccessfulRequest() );
			
            if((nodeReportVO.getAccountInfoMinTime()>0)&&(nodeReportVO.getAccountInfoMinTime() <= mainNode.getAccountInfoMinTime()))
            {
            	mainNode.setAccountInfoMinTime(nodeReportVO.getAccountInfoMinTime());
            }

            if(nodeReportVO.getAccountInfoMaxTime() >= mainNode.getAccountInfoMaxTime() )
            {
            	mainNode.setAccountInfoMaxTime(nodeReportVO.getAccountInfoMaxTime());
            }
			
			
			mainNode.setUpdateTotalRequest( mainNode.getUpdateTotalRequest() + nodeReportVO.getUpdateTotalRequest() );
			mainNode.setUpdateSuccessfulRequest( mainNode.getUpdateSuccessfulRequest() + nodeReportVO.getUpdateSuccessfulRequest() );
			
            if((nodeReportVO.getUpdateInfoMinTime()>0)&&(nodeReportVO.getUpdateInfoMinTime() <= mainNode.getUpdateInfoMinTime()))
            {
            	mainNode.setUpdateInfoMinTime(nodeReportVO.getUpdateInfoMinTime());
            }

            if(nodeReportVO.getUpdateInfoMaxTime() >= mainNode.getUpdateInfoMaxTime() )
            {
            	mainNode.setUpdateInfoMaxTime(nodeReportVO.getUpdateInfoMaxTime());
            }
			

			mainNode.setRefillTotalRequest( mainNode.getRefillTotalRequest() + nodeReportVO.getRefillTotalRequest() );
			mainNode.setRefillSuccessfulRequest( mainNode.getRefillSuccessfulRequest() + nodeReportVO.getRefillSuccessfulRequest() );
			
            if((nodeReportVO.getRefillInfoMinTime() >0)&&(nodeReportVO.getRefillInfoMinTime() <= mainNode.getRefillInfoMinTime()))
            {
            	mainNode.setRefillInfoMinTime(nodeReportVO.getRefillInfoMinTime());
            }
            
            if(nodeReportVO.getRefillInfoMaxTime() >= mainNode.getRefillInfoMaxTime() )
            {
            	mainNode.setRefillInfoMaxTime(nodeReportVO.getRefillInfoMaxTime());
            }
            mainAccInfoAvgTime=mainAccInfoAvgTime+ nodeReportVO.getAccountInfoAvgTime();
            mainRefillInfoAvgTime=mainRefillInfoAvgTime+nodeReportVO.getRefillInfoAvgTime();
            mainUpdateInfoAvgTime=mainUpdateInfoAvgTime+ nodeReportVO.getUpdateInfoAvgTime();
           
		}

		mainNode.setAccountInfoAvgTime(mainAccInfoAvgTime/map.size());
		mainNode.setRefillInfoAvgTime(mainRefillInfoAvgTime/map.size());
		mainNode.setUpdateInfoAvgTime(mainUpdateInfoAvgTime/map.size());
		System.out.println("\n\n****Main Details****\n\n");
		System.out.println(mainNode.toString());
		
	}
	
	
	public static void main(String[] args) 
	{
		final String methodName = "main";
		System.out.println("File Path "+args[0]);
	
		try
		{
			processFile(args[0]);
		}catch(Exception ex)
		{

			_log.errorTrace(methodName,ex);
		}
		finally
		{
			System.out.println("\n\n\n*************** DATA Start From Here *********\n\n");
			showData();			
			System.out.println("\n\n\n*************** DATA ENDS From Here *********\n\n");
			System.out.println("Main Exit");
		}
		
	}
	
}
