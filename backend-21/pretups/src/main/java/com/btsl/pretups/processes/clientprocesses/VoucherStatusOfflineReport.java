package com.btsl.pretups.processes.clientprocesses;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.util.VOMSVoucherDAO;
import com.btsl.pretups.inter.util.VOMSVoucherVO;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.util.ConfigServlet;
import com.btsl.util.OracleUtil;

public class VoucherStatusOfflineReport
{
	private static Log LOG = LogFactory.getLog(VoucherStatusOfflineReport.class.getName());

	private static ProcessStatusVO _processStatusVO;
	private static ProcessBL _processBL=null;
	private static Properties _voucherproperties = new Properties();        
	private static String _voucherOfflineReportPath=null;
	private static String _fileEXT=".csv";
	private static String _filePrefix="voucher";
	private static String _fileDataDelimeter=",";
	public static String  processId=null;


	public static void main(String arg[])
	{
		final String METHOD_NAME = "main";
		InputStream inputstream = null;
		try
		{
			if(arg.length!=3)
			{
				if(arg.length!=2)
				{
					System.out.println("Usage : VoucherStatusOfflineReport [Constants file] [LogConfig file] [csvConfigFile file]");
					return;
				}
			}
			File constantsFile = new File(arg[0]);
			if(!constantsFile.exists())
			{
				System.out.println("VoucherStatusOfflineReport"+" Constants File Not Found .............");
				return;
			}
			File logconfigFile = new File(arg[1]);
			if(!logconfigFile.exists())
			{
				System.out.println("VoucherStatusOfflineReport"+" Logconfig File Not Found .............");
				return;
			}

			File voucherConfigFile = new File(arg[2]);
			if(!voucherConfigFile.exists())
			{
				System.out.println("VoucherStatusOfflineReport"+" voucherConfigFile.props File Not Found .............");
				return;
			}
			ConfigServlet.loadProcessCache(constantsFile.toString(),logconfigFile.toString());
			inputstream = new FileInputStream(voucherConfigFile);
			_voucherproperties.load(inputstream);
		}
		catch(Exception e)
		{
			if (LOG.isDebugEnabled())
				LOG.debug(METHOD_NAME," Error in Loading Files ...........................: "+e.getMessage());
			LOG.errorTrace(METHOD_NAME,e);
			ConfigServlet.destroyProcessCache();
			return;
		}
		try
		{
			process();
		}
		catch(BTSLBaseException be)
		{
			LOG.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
			LOG.errorTrace(METHOD_NAME,be);
		}
		finally
		{
			if (inputstream !=null) {
				try {
					inputstream.close();
				} catch (IOException e) {
					LOG.error(METHOD_NAME, "I/O Exception occured");
				}
			}
			ConfigServlet.destroyProcessCache();
			if(LOG.isDebugEnabled())LOG.debug(METHOD_NAME, "Exiting..... ");
		}
	}

	private static void process() throws BTSLBaseException
	{
		final String METHOD_NAME = "process";
		if(LOG.isDebugEnabled())LOG.debug(METHOD_NAME, "Entered");
		Date processedUpto=null;
		Connection con= null;
		boolean statusOk=false;

		try
		{
			LOG.debug("process","Memory at startup: Total:"+Runtime.getRuntime().totalMemory()/1049576+" Free:"+Runtime.getRuntime().freeMemory()/1049576);

			loadConstantParameters();

			con = OracleUtil.getSingleConnection();
			if(con==null)
			{
				if (LOG.isDebugEnabled())
					LOG.debug("process"," DATABASE Connection is NULL ");
				EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VoucherStatusOfflineReport[process]","","","","DATABASE Connection is NULL");
				return;
			}
			processId=ProcessI.VCHR_STATUS_OFFLN_RPT;
			_processBL=new ProcessBL();
			_processStatusVO=_processBL.checkProcessUnderProcess(con,processId);
			statusOk=_processStatusVO.isStatusOkBool();
			if (statusOk)
			{
				con.commit();
				processedUpto=_processStatusVO.getExecutedUpto();
				if (processedUpto!=null)
				{
					processFile(con);
				}
				else
					throw new BTSLBaseException("VoucherStatusOfflineReport","process",PretupsErrorCodesI.DWH_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND);
			}
		}
		catch(BTSLBaseException be)
		{
			LOG.error("process", "BTSLBaseException : " + be.getMessage());
			LOG.errorTrace(METHOD_NAME,be);
			throw be;
		}
		catch(Exception e)
		{
			LOG.error("process", "Exception : " + e.getMessage());
			LOG.errorTrace(METHOD_NAME,e);
			EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"VoucherStatusOfflineReport[process]","","",""," Exception ="+e.getMessage());
			throw new BTSLBaseException("VoucherStatusOfflineReport","process",PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
		}
		finally
		{
			processId=ProcessI.VCHR_STATUS_OFFLN_RPT;
			if (statusOk)
			{
				try
				{
					if (markProcessStatusAsComplete(con,processId)==1)
						try{con.commit();} catch(Exception e){LOG.errorTrace(METHOD_NAME,e);}
					else
						try{con.rollback();} catch(Exception e){LOG.errorTrace(METHOD_NAME,e);}
				}
				catch(Exception e)
				{
					LOG.errorTrace(METHOD_NAME,e);
				}
				try
				{
					if(con!=null)
						con.close();
				}
				catch(Exception ex)
				{
					LOG.errorTrace(METHOD_NAME,ex);
					if(LOG.isDebugEnabled())LOG.debug("process", "Exception closing connection ");
				}
			}
			LOG.debug("process","Memory at end: Total:"+Runtime.getRuntime().totalMemory()/1049576+" Free:"+Runtime.getRuntime().freeMemory()/1049576);
			if(LOG.isDebugEnabled())LOG.debug(METHOD_NAME, "Exiting..... ");
		}
	}

	private static void loadConstantParameters() throws BTSLBaseException
	{
		final String METHOD_NAME = "loadConstantParameters";
		if (LOG.isDebugEnabled())
			LOG.debug(METHOD_NAME," Entered");
		try
		{
			initialize();
			LOG.debug(METHOD_NAME," Required information successfuly loaded from voucherproperties.properties...............: ");
		}
		catch(BTSLBaseException be)
		{
			LOG.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VoucherStatusOfflineReport[loadConstantParameters]","","","","Message:"+be.getMessage());
			LOG.errorTrace(METHOD_NAME,be);
			throw be;
		}
		catch(Exception e)
		{
			LOG.error(METHOD_NAME, "Exception : " + e.getMessage());
			LOG.errorTrace(METHOD_NAME,e);
			BTSLMessages btslMessage=new BTSLMessages(PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VoucherStatusOfflineReport[loadConstantParameters]","","","","Message:"+btslMessage);
			throw new BTSLBaseException("VoucherStatusOfflineReport",METHOD_NAME,PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
		}
		finally {
			if(LOG.isDebugEnabled())LOG.debug(METHOD_NAME, "Exiting");
		}

	}

	private static int markProcessStatusAsComplete(Connection p_con,String p_processId) throws BTSLBaseException
	{
		final String METHOD_NAME = "markProcessStatusAsComplete";
		if (LOG.isDebugEnabled())
			LOG.debug("markProcessStatusAsComplete"," Entered:  p_processId:"+p_processId);
		int updateCount=0;
		Date currentDate=new Date();
		ProcessStatusDAO processStatusDAO=new ProcessStatusDAO();
		_processStatusVO.setProcessID(p_processId);
		_processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
		_processStatusVO.setStartDate(currentDate);
		try
		{
			updateCount =processStatusDAO.updateProcessDetail(p_con,_processStatusVO);
		}
		catch(Exception e)
		{
			LOG.errorTrace(METHOD_NAME,e);
			LOG.error("markProcessStatusAsComplete", "Exception= " + e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VoucherStatusOfflineReport[markProcessStatusAsComplete]","","","","Exception:"+e.getMessage());
			throw new BTSLBaseException("VoucherStatusOfflineReport","markProcessStatusAsComplete",PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
		}
		finally
		{
			if (LOG.isDebugEnabled())
				LOG.debug("markProcessStatusAsComplete", "Exiting: updateCount=" + updateCount);
		}
		return updateCount;

	}

	public static void initialize() throws BTSLBaseException
	{
		final String METHOD_NAME = "initialize";
		if(LOG.isDebugEnabled()) LOG.debug("initialize","Entered ");                
		try
		{
			_voucherOfflineReportPath = _voucherproperties.getProperty("FINAL_FILE_PATH");
			_fileEXT = _voucherproperties.getProperty("FILE_EXTENSION");
			_filePrefix = _voucherproperties.getProperty("FILE_PREFIX");
			_fileDataDelimeter = _voucherproperties.getProperty("FILE_DATA_SEPARATOR");
		}
		catch(Exception e)
		{
			LOG.errorTrace(METHOD_NAME,e);
			LOG.error("initialize","Exception e::"+e.getMessage());
			throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS3_NODE_INITIALIZATION);
		}
		finally
		{
			if(LOG.isDebugEnabled()) LOG.debug(METHOD_NAME,"Exiting");
		}
	}


	private static void processFile(Connection p_con){
		final String METHOD_NAME = "processFile";

		if(LOG.isDebugEnabled())LOG.debug(METHOD_NAME, "Entered");

		String line = null;
		ArrayList<String> voucherDumpData = new ArrayList<String>();
		try {
			VOMSVoucherVO voucherVO = null;
			VOMSVoucherDAO vomsDAO=new VOMSVoucherDAO();

			fetchData(p_con,voucherDumpData);	

			writeVoucherFile(voucherDumpData);                  
		}               
		catch (Exception e) {
			LOG.errorTrace(METHOD_NAME,e);
		}
		finally {
			if(LOG.isDebugEnabled())LOG.debug(METHOD_NAME, "Exiting");
		}
	}

	private static void writeVoucherFile(ArrayList p_data){
		final String METHOD_NAME = "writeVoucherFile";

		if(LOG.isDebugEnabled())LOG.debug(METHOD_NAME, "Entered p_data.size = "+p_data.size());

		BufferedWriter bw = null;
		FileWriter fw = null;
		SimpleDateFormat sdf=null;

		try {                        
			sdf = new SimpleDateFormat("ddMMyyyyHHmmss");  
			String strDate= sdf.format(new Date());  
			fw = new FileWriter(_voucherOfflineReportPath+File.separator+_filePrefix+strDate+_fileEXT);
			bw = new BufferedWriter(fw);
			bw.append(new Date()+"\n");
			bw.append(_voucherproperties.getProperty("mrp")+_fileDataDelimeter);
			bw.append(_voucherproperties.getProperty("pname")+_fileDataDelimeter);
			bw.append(_voucherproperties.getProperty("no_ge")+_fileDataDelimeter);
			bw.append(_voucherproperties.getProperty("value_ge")+_fileDataDelimeter);
			bw.append(_voucherproperties.getProperty("no_en")+_fileDataDelimeter);
			bw.append(_voucherproperties.getProperty("value_en")+_fileDataDelimeter);
			bw.append(_voucherproperties.getProperty("no_cu")+_fileDataDelimeter);
			bw.append(_voucherproperties.getProperty("value_cu")+_fileDataDelimeter);
			bw.append(_voucherproperties.getProperty("no_s")+_fileDataDelimeter);
			bw.append(_voucherproperties.getProperty("value_s")+_fileDataDelimeter);
			bw.append(_voucherproperties.getProperty("no_oh")+_fileDataDelimeter);
			bw.append(_voucherproperties.getProperty("value_oh")+_fileDataDelimeter);
			bw.append(_voucherproperties.getProperty("no_st")+_fileDataDelimeter);
			bw.append(_voucherproperties.getProperty("value_st")+_fileDataDelimeter);
			bw.append(_voucherproperties.getProperty("no_ex")+_fileDataDelimeter);
			bw.append(_voucherproperties.getProperty("value_ex")+"\n");			
			for(int count = 0;count<p_data.size();count++){
				bw.append(p_data.get(count).toString()+"\n");	
			}			
			if(bw!=null)
				bw.close();
			if(fw!=null)
				fw.close();                        

		} catch (IOException e) {
			LOG.errorTrace(METHOD_NAME, e);
		} 
		finally {
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			} catch (IOException ex) {
				LOG.errorTrace(METHOD_NAME,ex);
			}
			if(LOG.isDebugEnabled())LOG.debug(METHOD_NAME, "Exiting");
		}
	}


	private static void fetchData(Connection p_con,ArrayList<String> p_data){
		final String methodName = "fetchData";
		if (LOG.isDebugEnabled())
			LOG.debug(methodName, "Entered");

		PreparedStatement pstmt = null;
		ResultSet rs = null;		

		StringBuffer sqlSelect = new StringBuffer(" select vp.mrp/100 mrp,vp.PRODUCT_NAME pname, ");
		sqlSelect.append(" sum (case when vv.CURRENT_STATUS='GE' then 1 else 0 end) no_ge, ");
		sqlSelect.append(" sum (case when vv.CURRENT_STATUS='GE' then 1*(vp.mrp)/100 else 0 end) value_ge, ");
		sqlSelect.append(" sum (case when vv.CURRENT_STATUS='EN' then 1 else 0 end)  no_en, ");
		sqlSelect.append(" sum (case when vv.CURRENT_STATUS='EN' then 1*(vp.mrp)/100 else 0 end) value_en, ");
		sqlSelect.append(" sum (case when vv.CURRENT_STATUS='CU' then 1 else 0 end)  no_cu, ");
		sqlSelect.append(" sum (case when vv.CURRENT_STATUS='CU' then 1*(vp.mrp)/100 else 0 end) value_cu, ");
		sqlSelect.append(" sum (case when vv.CURRENT_STATUS='S' then 1 else 0 end)  no_s, ");
		sqlSelect.append(" sum (case when vv.CURRENT_STATUS='S' then 1*(vp.mrp)/100 else 0 end) value_s, ");
		sqlSelect.append(" sum (case when vv.CURRENT_STATUS='OH' then 1 else 0 end)  no_oh, ");
		sqlSelect.append(" sum (case when vv.CURRENT_STATUS='OH' then 1*(vp.mrp)/100 else 0 end) value_oh, ");
		sqlSelect.append(" sum (case when vv.CURRENT_STATUS='ST' then 1 else 0 end)  no_st, ");
		sqlSelect.append(" sum (case when vv.CURRENT_STATUS='ST' then 1*(vp.mrp)/100 else 0 end) value_st, ");
		sqlSelect.append(" sum (case when vv.CURRENT_STATUS='EX' then 1 else 0 end)  no_ex, ");
		sqlSelect.append(" sum (case when vv.CURRENT_STATUS='EX' then 1*(vp.mrp)/100 else 0 end) value_ex ");
		sqlSelect.append(" from voms_products vp,voms_vouchers vv ");
		sqlSelect.append(" WHERE vv.product_id = vp.product_id ");
		sqlSelect.append(" group by vv.product_id,vp.mrp,vp.PRODUCT_NAME ");

		if (LOG.isDebugEnabled())
			LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect.toString());
		try
		{
			pstmt = p_con.prepareStatement(sqlSelect.toString());		   
			rs = pstmt.executeQuery();
			String tempRecord;
			while (rs.next())
			{	
				tempRecord = rs.getString("mrp")+_fileDataDelimeter+rs.getString("pname")+_fileDataDelimeter+
						rs.getString("no_ge")+_fileDataDelimeter+rs.getString("value_ge")+_fileDataDelimeter+
						rs.getString("no_en")+_fileDataDelimeter+rs.getString("value_en")+_fileDataDelimeter+
						rs.getString("no_cu")+_fileDataDelimeter+rs.getString("value_cu")+_fileDataDelimeter+
						rs.getString("no_s")+_fileDataDelimeter+rs.getString("value_s")+_fileDataDelimeter+
						rs.getString("no_oh")+_fileDataDelimeter+rs.getString("value_oh")+_fileDataDelimeter+
						rs.getString("no_st")+_fileDataDelimeter+rs.getString("value_st")+_fileDataDelimeter+
						rs.getString("no_ex")+_fileDataDelimeter+rs.getString("value_ex");
				p_data.add(tempRecord);
			}
		}
		catch (SQLException sqe)
		{
			LOG.error(methodName, "SQLException : " + sqe);
			LOG.errorTrace(methodName,sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VomsCategoryDAO[fetchData]","","","","SQL Exception:"+sqe.getMessage());			
		}
		catch (Exception ex)
		{
			LOG.error(methodName, "Exception : " + ex);
			LOG.errorTrace(methodName,ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VomsCategoryDAO[fetchData]","","","","Exception:"+ex.getMessage());			
		}
		finally
		{
			try{if (rs != null){rs.close();}} catch (Exception e){LOG.errorTrace(methodName,e);}
			try{if (pstmt != null){pstmt.close();}} catch (Exception e){LOG.errorTrace(methodName,e);}
			if (LOG.isDebugEnabled())
				LOG.debug(methodName, "Exiting: p_data.size=" + p_data.size());
		}
	}
}


