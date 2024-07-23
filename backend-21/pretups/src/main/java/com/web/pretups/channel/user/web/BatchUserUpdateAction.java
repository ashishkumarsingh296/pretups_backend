/*
 * @# BatchUserUpdateAction.java
 * * This class is the controller class of the Update Batch User Module.
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Sanjeew Kumar March 29, 2007 Initial creation*
 * Santanu Mohanty Dec 06, 2007 Modify*
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2007 Bharti Telesoft Ltd.
 */
package com.web.pretups.channel.user.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
/*import org.apache.struts.Globals;
//import org.apache.struts.action.ActionForm;
//import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;*/
import com.btsl.util.MessageResources;

import com.btsl.common.BTSLBaseException;
//import com.btsl.common.BTSLDispatchAction;
import com.btsl.common.BTSLMessages;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;
import com.btsl.pretups.channel.user.businesslogic.BatchUserDAO;
import com.btsl.pretups.common.ExcelFileIDI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.SubLookUpDAO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.processes.TargetBasedCommissionMessages;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.roles.businesslogic.UserRolesVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.pretups.xl.BatchUserCreationExcelRWPOI;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.monitorjbl.xlsx.StreamingReader;
import com.web.pretups.channel.user.businesslogic.BatchUserWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

public class BatchUserUpdateAction  {
    public static final String XLS_PINPASSWARD = "****";


    protected static final Log LOG = LogFactory.getLog(BatchUserUpdateAction.class.getName());



    /**
     * Method deleteUploadedFile.
     * This method is used to delete the uploaded file if any error occurs
     * during file processing
     * 
     * @param p_form
     *            BatchUserForm
     * @return void
     * @throws Exceptionproce
     */

   /* private void deleteUploadedFile(BatchUserForm p_form) throws BTSLBaseException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("deleteUploadedFile", "Entered");
        }
        final String METHOD_NAME = "deleteUploadedFile";
        String fileStr = Constants.getProperty("UPLOADMODIFYBATCHUSERFILEPATH");
        fileStr = fileStr + p_form.getFile();
        final File f = new File(fileStr);
        if (f.exists()) {
            try {
            	boolean isDeleted = f.delete();
                if(isDeleted){
                 LOG.debug(METHOD_NAME, "File deleted successfully");
                }
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
                LOG.error("deleteUploadedFile", "Error in deleting the uploaded file" + f.getName() + " as file validations are failed Exception::" + e);
                throw new BTSLBaseException(this, METHOD_NAME, "Exception in deleting uploaded file as file validations failed");
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("deleteUploadedFile", "Exiting:");
        }
    }
*/
    public String cellValueNull(Cell cell){
    	String cellval;
    	if(cell==null){
    		cellval="";
		}
    	else{
    		cellval = cell.getStringCellValue();
    	}
    	return cellval;
    }
    
    //define a runnable task for processing defined number of records
    private class UpdateRecordsInDB implements Runnable {
    	private Connection innerCon=null;
    	private MComConnectionI innermcomCon = null;
    	private ArrayList<ChannelUserVO> channelUserList = null;
    	private ArrayList fileErrorList = null;
    	private String domCode = null;
    	private MessageResources msgRsc = null;
    	private Locale locale = null;
    	private BatchUserWebDAO batchUserWebDAO = null;
    	private String fileName = null;
    	public UpdateRecordsInDB(ArrayList<ChannelUserVO> list,ArrayList fileErrorList, BatchUserWebDAO batchUserWebDAO, String domCode, MessageResources msgRsc, Locale locale, String fileName) {
    		this.channelUserList = list;
    		this.fileErrorList = fileErrorList;
    		this.domCode = domCode;
    		this.msgRsc = msgRsc;
    		this.locale = locale;
    		this.batchUserWebDAO = batchUserWebDAO;
    		this.fileName = fileName;   		
    	}

    	@Override
		public void run() {
    		final String METHOD_NAME = "run";
    		try { double startTime = System.currentTimeMillis();
    			if(innermcomCon==null){ 
					innermcomCon = new MComConnection();
					innerCon = innermcomCon.getConnection();
    			}
    			ArrayList dbErrorList = batchUserWebDAO.modifyChannelUserList(innerCon, channelUserList, domCode, msgRsc, locale, fileName);
    			innerCon.commit();
    			synchronized (this) {
    				fileErrorList.addAll(dbErrorList);
				}
    			LOG.debug("UpdateRecordsInDB","Hey ASHU thread "+Thread.currentThread().getName()+" processed "+channelUserList.size()+" records, time taken = "+(System.currentTimeMillis()-startTime)+" ms");
    			this.channelUserList.clear();
    			this.channelUserList = null;
            	Runtime runtime = Runtime.getRuntime();
                long memory = runtime.totalMemory() - runtime.freeMemory();
                LOG.debug("run","Used memory in megabytes before gc: " + (memory)/1048576);
                // Run the garbage collector
                runtime.gc();
                // Calculate the used memory
                memory = runtime.totalMemory() - runtime.freeMemory();
                LOG.debug("run","Used memory in megabytes after gc: " + (memory)/1048576);
    		} catch (BTSLBaseException e1) {
    			LOG.error(METHOD_NAME, "BTSLBaseException:e=" + e1);
    			LOG.errorTrace(METHOD_NAME, e1);
    		} catch (SQLException e) {
    			LOG.error(METHOD_NAME, "SQLException:e=" + e);
    			LOG.errorTrace(METHOD_NAME, e);
			} finally {
				if (innermcomCon != null) {
					innermcomCon.close("BatchUserUpdateAction#UpdateRecordsInDB");
					innermcomCon = null;
				}
			}
    	}
}
}
