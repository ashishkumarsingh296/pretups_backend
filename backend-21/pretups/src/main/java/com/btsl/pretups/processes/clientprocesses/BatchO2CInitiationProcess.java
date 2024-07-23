/*
 * COPYRIGHT: Mahindra Comviva Technologies Pvt. Ltd.
 *
 * This software is the sole property of Comviva and is protected
 * by copyright law and international treaty provisions. Unauthorized
 * reproduction or redistribution of this program, or any portion of
 * it may result in severe civil and criminal penalties and will be
 * prosecuted to the maximum extent possible under the law.
 * Comviva reserves all rights not expressly granted. You may not
 * reverse engineer, decompile, or disassemble the software, except
 * and only to the extent that such activity is expressly permitted
 * by applicable law notwithstanding this limitation.
 *
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY
 * KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE. YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY
 * AND THE USE OF THIS SOFTWARE. Comviva SHALL NOT BE LIABLE FOR
 * ANY DAMAGES WHATSOEVER ARISING OUT OF THE USE OF OR INABILITY TO
 * USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.btsl.pretups.processes.clientprocesses;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.FileUtil;
import com.btsl.util.OracleUtil;
/**
 * @author anjali.agarwal
 *
 */
public class BatchO2CInitiationProcess {
        private static Log logger = LogFactory.getLog(BatchO2CInitiationProcess.class.getName());
        private static String paymentType = "";
    private static long counter = 1;
    private static ArrayList<String> statusList=new ArrayList<String>();
        private static Properties batchO2CProperties = new Properties();

        /**
         * to ensure no class instantiation 
         */
        private BatchO2CInitiationProcess(){
        	
        }

        public static void main(String[] args){
        final String methodName = "BatchO2CInitiationProcess[main()]";
        try
        {
            if(args.length<2 || args.length>3 )
            {
            	if(logger.isDebugEnabled())
            		logger.debug(methodName, "Usage : BatchO2C [Constants file] [LogConfig file] [Y/N]");
                return;
            }
            //load constants.props
            File constantsFile = new File(args[0]);
            if(!constantsFile.exists() )
            {
            	if(logger.isDebugEnabled())
            		logger.debug(methodName, " BatchO2C "+" Constants File Not Found .............");
                logger.error("BatchO2C[main]", "Constants file not found on location: "+constantsFile.toString() );
                        return;
            }
            //load log config file
            File logFile = new File(args[1]);
            if(!logFile.exists())
            {
            	if(logger.isDebugEnabled())
            		logger.debug(methodName, "BatchO2C"+" Logconfig File Not Found .............");
                logger.error("BatchO2C[main]", "Logconfig File not found on location: "+logFile.toString());
                return;
            }


            File batOprtnConfigFile = new File(args[2]);
                        if(!batOprtnConfigFile.exists())
                        {
                        	if(logger.isDebugEnabled())
                        		logger.debug(methodName, "BatchO2C"+" BatchO2CINIConfig.props File Not Found .............");
                                return;
                        }
                         ConfigServlet.loadProcessCache(constantsFile.toString(),logFile.toString());
                         batchO2CProperties.load(new FileInputStream(batOprtnConfigFile));

        }//end of try block
        catch(Exception e)
        {
            if (logger.isDebugEnabled())
                                logger.debug("main"," Error in Loading Files ...........................: "+e.getMessage());
            logger.errorTrace(methodName,e);
                        ConfigServlet.destroyProcessCache();
                        return;
        }//end of catch block
        try
        {
            process();
        }//end of try block
        catch(BTSLBaseException be)
        {
            logger.error("main", "BTSLBaseException : " + be.getMessage());
            logger.errorTrace(methodName,be);
                        return;
        }//end of catch block
        catch(Exception e)
        {
            if (logger.isDebugEnabled())
                   logger.debug("main"," "+e.getMessage());
            logger.errorTrace(methodName,e);
                        return;
        }//end of catch block
        finally
        {
            if (logger.isDebugEnabled())
                    logger.info("main","Exiting");
            try
            {
            	Thread.sleep(5000);
            }
            catch(Exception e)
            {
            	logger.errorTrace(methodName,e);
            }
            ConfigServlet.destroyProcessCache();
        }//end of finally
    }

    /**
         * This method checks the process is under process/complete for the process id
         * specified in process_status table
         * @return void
         * @throws BTSLBaseException
         */
    private static void process() throws BTSLBaseException{
        final String methodName = "process";
        if (logger.isDebugEnabled())
                        logger.info(methodName,"Entered ");
        String processId = null;
        ProcessBL processBL = null;
        Connection con = null;
        ProcessStatusVO processStatusVO = null;
        Date currentDate = null;
        Date processedUpto = null;
        String finalDirectoryPath = null;
        int updateCount = 0;                            //check process details are updated or not
        try
        {
            processId = ProcessI.BATCH_O2C_INITIATION;
            con = OracleUtil.getSingleConnection();
            processBL = new ProcessBL();
            processStatusVO = processBL.checkProcessUnderProcess(con,processId);
            if(processStatusVO.isStatusOkBool()){
                //method call to find maximum date till which process has been executed
                processedUpto=processStatusVO.getExecutedUpto();
                        if (processedUpto != null){
                                currentDate = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(new Date()));
                        processedUpto = currentDate;
                            String dir = Constants.getProperty("UploadBatchO2CInitiationFilePath");
                            File directory = new File(dir);
                            File[] directoryListing = directory.listFiles();
                            boolean isFileProcessed = false;
                                 if (directoryListing != null && directoryListing.length > 0) {
                                         finalDirectoryPath=createDirectory();
                                for (File child : directoryListing) {
                                        isFileProcessed = processFile(child, dir, finalDirectoryPath);
                                }
                            } else {
                            	logger.debug(methodName, "No Files found in directory structure");
                            }
                            if(isFileProcessed) {
                                processStatusVO.setExecutedUpto(processedUpto);
                                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BatchO2CInitiation[process]","","",""," Batch O2C Initiation has been executed successfully.");
                                        if(logger.isDebugEnabled())
                                                logger.debug("process", "message sent successfully");
                            }
                        }else
                                throw new BTSLBaseException(methodName, PretupsErrorCodesI.BATCH_O2C_INITITATION_EXECUTED_UPTO_DATE_NOT_FOUND);
            }else
                throw new BTSLBaseException(methodName, PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
        }catch(BTSLBaseException be){
                logger.error(methodName, "BTSLBaseException : " + be.getMessage());
                logger.errorTrace(methodName, be);
                throw be;
        }catch(Exception e){
            logger.error(methodName, "Exception : " + e.getMessage());
            logger.errorTrace(methodName, e);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "BatchO2CInitiation[process]","","",""," Batch O2C Initiation process could not be executed successfully.");
                    throw new BTSLBaseException("BatchO2CInitiation", methodName, PretupsErrorCodesI.ERROR_IN_DAILY_ALERT);
        }finally{
            try{
                if (processStatusVO.isStatusOkBool()){
                        processStatusVO.setStartDate(currentDate);
                        processStatusVO.setExecutedOn(currentDate);
                        processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                                updateCount=(new ProcessStatusDAO()).updateProcessDetail(con,processStatusVO);
                                if(updateCount>0) {
                                        con.commit();
                                }
                    }
                }catch(Exception ex){
                        if(logger.isDebugEnabled())
                        	logger.debug(methodName, "Exception in closing connection ");
                                        logger.errorTrace(methodName, ex);
                    }
           
            try{
            		con.close();
            		}catch(SQLException e1)
            {
                logger.errorTrace(methodName, e1);
            }
            if(logger.isDebugEnabled())
            	logger.debug(methodName, "Exiting..... ");
        }
    }

    /*
     * this method is used to read the data from file and create the O2C request through it
     * @param file File
     * @param dirPath String
     * @param finalDirectory String
     * throws BTSLBaseException
     * returns boolean value
     */

    public static boolean processFile(File file, String dirPath, String finalDirectroyPath) throws BTSLBaseException{
        BufferedReader br = null;
        String formattedDate = null;
        String methodName = "processFile";
        String ts = null;
        String oldTs = null;
        String trfCategory=null;
        String netwrkCode=null;
        String delimeter=null;
        String fileFormat=null;
        int dataStartRow;
        String extCode=null;
        String remarks=null;
        String productCode=null;
        String paymentInstnumber=null;
        String extRefnumber=null;
        String url=null;
        try {
                String sCurrentLine;
                Date currDate = new Date();              
                        ArrayList<RequestVO> recordList=new ArrayList<RequestVO>();
                RequestVO requestVO1 = null;
                        trfCategory = batchO2CProperties.getProperty("TRFCATEGORY");
                        url = batchO2CProperties.getProperty("URL");
                        netwrkCode = batchO2CProperties.getProperty("NTWRKCODE");
                        delimeter = batchO2CProperties.getProperty("DELIMETER");                        
                        fileFormat = batchO2CProperties.getProperty("FILE_FORMAT");
                        dataStartRow=Integer.parseInt(batchO2CProperties.getProperty("DATA_START_ROW"));
                        
                        extCode="";
                        remarks="";
                        String[] fileFormatArray=null;
                        HashMap<String,Integer> index = new HashMap();
                        if(!BTSLUtil.isNullString(fileFormat)){
                        	fileFormatArray=fileFormat.split("[,]");                        	
                        }
                        for(int count=0;count<fileFormatArray.length;count++){
                        	index.put(fileFormatArray[count],count);
                         }
                        
                        br = new BufferedReader(new FileReader(file));
                        int lineNum=0;
                        while ((sCurrentLine = br.readLine()) != null) {
                        	lineNum++;
                        	if(lineNum < dataStartRow)
                        		continue;
                        	if(logger.isDebugEnabled())
                        		logger.debug(methodName, sCurrentLine);
                        	RequestVO requestVO = new RequestVO();
                        	Map<String, String> elementMap = new HashMap<String, String>();
                        	String msisdn=sCurrentLine.split(delimeter)[index.get("MSISDN")];
                        	requestVO.setMsisdn(msisdn.replaceAll("[^0-9]", ""));
                        	productCode=batchO2CProperties.getProperty("PRE_PROD_CODE");
                        	requestVO.setExternalTransactionDate(formattedDate);
                        	if(Arrays.asList(fileFormatArray).contains("PRODUCT_CODE")){
                        		if(sCurrentLine.split(delimeter)[index.get("PRODUCT_CODE")].equals("0.00")||sCurrentLine.split(delimeter)[index.get("PRODUCT_CODE")].equals("0")){
                        			productCode = batchO2CProperties.getProperty("PRE_PROD_CODE");
                        		}
                        		else if(sCurrentLine.split(delimeter)[index.get("PRODUCT_CODE")].equals("1.00")||sCurrentLine.split(delimeter)[index.get("PRODUCT_CODE")].equals("1")){
                        			productCode = batchO2CProperties.getProperty("POST_PROD_CODE");
                        		}
                        	}                        	
                        	elementMap.put("PRODUCT_CODE", productCode);
                        	elementMap.put("QTY", sCurrentLine.split(delimeter)[index.get("AMOUNT")]);
                        	
                        	if(Arrays.asList(fileFormatArray).contains("EXT_TXN_DATE")){
                        		try     {
                        			currDate = BTSLUtil.getDateFromDateString(sCurrentLine.split(delimeter)[index.get("EXT_TXN_DATE")]);
                        			formattedDate = BTSLUtil.getDateTimeStringFromDate(currDate, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_DATE_FORMAT)));
                        			oldTs = (((BTSLUtil.getDateTimeStringFromDate(currDate)).replace("/","")).replace(":","")).replace(" ","");
                        		}
                        		catch(ParseException pe){
                        			logger.debug(methodName, pe);
                        		}
                        	}
                        	else{
                        		 currDate = new Date();
                        		 try     {
                                     formattedDate = BTSLUtil.getDateTimeStringFromDate(currDate, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_DATE_FORMAT)));
                                     oldTs = (((BTSLUtil.getDateTimeStringFromDate(currDate)).replace("/","")).replace(":","")).replace(" ","");
                             }
                             catch(ParseException pe){
                                     logger.debug(methodName, pe);
                             }
                        	}
                        	
                        	requestVO.setExternalTransactionDate(formattedDate);
                        	elementMap.put("PAYMENT_DATE", formattedDate);
                        	
                        	if(Arrays.asList(fileFormatArray).contains("EXT_TXN_NUMBER")){
                        		requestVO.setExternalTransactionNum(sCurrentLine.split(delimeter)[index.get("EXT_TXN_NUMBER")]);                        		
                        	}
                        	if(Arrays.asList(fileFormatArray).contains("PAYMENT_TYPE")){
                        		paymentType=sCurrentLine.split(delimeter)[index.get("PAYMENT_TYPE")];
                        	}
                        	elementMap.put("PAYMENT_TYPE", paymentType);
                        	paymentInstnumber="";
                        	if(Arrays.asList(fileFormatArray).contains("PAYMENT_INSTNUMBER")){
                        	    paymentInstnumber=sCurrentLine.split(delimeter)[index.get("PAYMENT_INSTNUMBER")];                              
                            }
                        	elementMap.put("PAYMENT_INSTNUMBER", paymentInstnumber);
                        	extCode="";
                        	if(Arrays.asList(fileFormatArray).contains("EXT_CODE")){
                        		extCode=sCurrentLine.split(delimeter)[index.get("EXT_CODE")];                        		
                        	}
                        	elementMap.put("EXT_CODE", extCode);
                        	remarks="";
                        	if(Arrays.asList(fileFormatArray).contains("REMARKS")){
                        		remarks=sCurrentLine.split(delimeter)[index.get("REMARKS")];                        		
                        	}
                        	elementMap.put("REMARKS", remarks);
                        	extRefnumber="";
                        	if(Arrays.asList(fileFormatArray).contains("REF_NUMBER")){
                        	    extRefnumber=sCurrentLine.split(delimeter)[index.get("REF_NUMBER")];                               
                            }
                            elementMap.put("REF_NUMBER", extRefnumber);
                        	
                        	                        	
                        	requestVO.setRequestMap((HashMap<String, String>)elementMap);
                        	recordList.add(requestVO);

                        }

                        
                        String httpURLPrefix = "http://";
                        URL url1 = null;
                        PrintWriter out = null;
                        BufferedReader in = null;
                        String responseStr = null;
                        String requestMessage = null;
                        HttpURLConnection urlConnection = null;
                        int recordListsSize=recordList.size();
                        for(int recordIndex=0; recordIndex<recordListsSize; recordIndex++){
                                try     {
                                        ts = (((BTSLUtil.getDateTimeStringFromDate(new Date())).replace("/","")).replace(":","")).replace(" ","");
                                }
                                catch(ParseException pe){
                                	logger.error(methodName, pe.getMessage());
                                }
                                if(!oldTs.equals(ts)){
                                        counter = 1;
                                }
                                requestVO1 = recordList.get(recordIndex);
                                if(!Arrays.asList(fileFormatArray).contains("EXT_TXN_NUMBER")){
                                    requestVO1.setExternalTransactionNum(ts + counter);                            		
                            	}
                    
                                StringBuilder urlToSend = new StringBuilder();
                                requestMessage = "<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XMLCommand1.0//EN\"\"xml/command.dtd\"><COMMAND><TYPE>O2CINTREQ</TYPE><EXTNWCODE>" + netwrkCode + "</EXTNWCODE>"
                                                + "<MSISDN>" + requestVO1.getMsisdn() + "</MSISDN><PIN>0000</PIN><EXTCODE>"+requestVO1.getRequestMap().get("EXT_CODE")+"</EXTCODE><EXTTXNNUMBER>" + requestVO1.getExternalTransactionNum() + "</EXTTXNNUMBER>"
                                                + "<EXTTXNDATE>" + requestVO1.getExternalTransactionDate() + "</EXTTXNDATE><PRODUCTS><PRODUCTCODE>" + requestVO1.getRequestMap().get("PRODUCT_CODE") + "</PRODUCTCODE>"
                                                + "<QTY>"+requestVO1.getRequestMap().get("QTY")+"</QTY></PRODUCTS><TRFCATEGORY>" + trfCategory + "</TRFCATEGORY><REFNUMBER>"+requestVO1.getRequestMap().get("REF_NUMBER")+"</REFNUMBER><PAYMENTDETAILS>"
                                                + "<PAYMENTTYPE>" + requestVO1.getRequestMap().get("PAYMENT_TYPE") + "</PAYMENTTYPE><PAYMENTINSTNUMBER>"+requestVO1.getRequestMap().get("PAYMENT_INSTNUMBER")+"</PAYMENTINSTNUMBER><PAYMENTDATE>" + requestVO1.getRequestMap().get("PAYMENT_DATE") + "</PAYMENTDATE></PAYMENTDETAILS><REMARKS>"+requestVO1.getRequestMap().get("REMARKS")+"</REMARKS></COMMAND>";
                                urlToSend = urlToSend.append(httpURLPrefix + url + batchO2CProperties.getProperty("C2S_RECEIVER"));
                                urlToSend = urlToSend.append("?REQUEST_GATEWAY_CODE=" + batchO2CProperties.getProperty("REQUEST_GATEWAY_CODE") + "&REQUEST_GATEWAY_TYPE=" + batchO2CProperties.getProperty("REQUEST_GATEWAY_TYPE"));
                                urlToSend = urlToSend.append("&SERVICE_PORT=" + batchO2CProperties.getProperty("SERVICE_PORT") + "&LOGIN=" + batchO2CProperties.getProperty("LOGIN")) ;
                                urlToSend = urlToSend.append("&PASSWORD=" + batchO2CProperties.getProperty("PASSWORD") + "&SOURCE_TYPE=" + batchO2CProperties.getProperty("SOURCE_TYPE")) ;
                                url1 = new URL(urlToSend.toString());
                                urlConnection = (HttpURLConnection)url1.openConnection();
                                urlConnection.setConnectTimeout(10000);
                                urlConnection.setReadTimeout(10000);
                                urlConnection.setDoOutput(true);
                                urlConnection.setDoInput(true);
                                urlConnection.addRequestProperty("Content-Type", "text/xml");
                                urlConnection.setRequestMethod("POST");
                                StringBuilder buffer = new StringBuilder();
                                String respStr = "";
                                try{
                                        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream())),true);
                                        if(logger.isDebugEnabled())
                                        	logger.debug(methodName, "Request sent   =" + requestMessage);
                                        out.println(requestMessage);
                                        out.flush();
                                        in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                                        while ((respStr = in.readLine()) != null){
                                                buffer.append(respStr);
                                        }
                                }catch(Exception e){
                                        logger.errorTrace(methodName, e);
                                        logger.error(methodName, "Exception in reading or writing  e:" + e.getMessage());
                                }//end of catch-Exception
                                finally{
                                       FileUtil.closeQuietly(out);
                                       FileUtil.closeQuietly(in);
                                        oldTs = ts;
                                        counter++;
                                }//end of finally
                                responseStr = buffer.toString();
                                parseResponse(requestVO1.getMsisdn(),responseStr);
                                if(logger.isDebugEnabled())
                                	logger.debug(methodName,"Response Received   =" + responseStr);
                              

                        }
                } catch (IOException e) {
                	logger.errorTrace(methodName, e);
                } finally {
                        writeToFile(finalDirectroyPath,statusList,file.getName());
                        moveFilesToFinalDirectory(dirPath, finalDirectroyPath+"/",file.getName());
                        FileUtil.closeQuietly(br);
                }
        return true;
    }

/*
 * this method parse the response
 * @param msisdn String
 * @param response String
 * returns void
 */
    public static void parseResponse(String msisdn,String response){
        String txnStatus;
        String txnId ;
        String txnNumber;
        String finalResponse ;

        txnStatus = response.substring(response.indexOf("<TXNSTATUS>")+"<TXNSTATUS>".length(), response.indexOf("</TXNSTATUS>"));
        txnId = response.substring(response.indexOf("<TXNID>")+"<TXNID>".length(), response.indexOf("</TXNID>"));
        txnNumber = response.substring(response.indexOf("<EXTTXNNUMBER>")+"<EXTTXNNUMBER>".length(), response.indexOf("</EXTTXNNUMBER>"));
        if(txnId == null)
                txnId = "";
        finalResponse = msisdn+"|"+txnId +"|" + txnStatus + "|" + txnNumber;
        statusList.add(finalResponse);
    }

    public static void writeToFile(String finalDirectoryPath, List<String> statusList, String fileName){
        if (logger.isDebugEnabled())
                        logger.debug("writeToFile","Entered with FinalDirectoryPath ::"+finalDirectoryPath+"statusList size"+statusList.size()+" File Name :: "+fileName);
        String sucFileName = null;
        String failFileName = null;
        String message = null;
        sucFileName = finalDirectoryPath+"/"+fileName.split("[.]")[0]+"_Success."+batchO2CProperties.getProperty("FILE_EXT_SUCCESS");
        failFileName = finalDirectoryPath+"/"+fileName.split("[.]")[0]+"_Fail."+batchO2CProperties.getProperty("FILE_EXT_FAIL");
        try(PrintWriter sucWriter = new PrintWriter(sucFileName, "UTF-8");PrintWriter failWriter = new PrintWriter(failFileName, "UTF-8")){
                String status = null;
                Locale locale=new Locale("en","US");
                int statusListSizes=statusList.size();
                for(int statusCount=0; statusCount<statusListSizes; statusCount++){
                        status = (statusList.get(statusCount)).split("[|]")[2];
                        if("200".equalsIgnoreCase(status)){
                                message = (statusList.get(statusCount)).split("[|]")[0]+"|"+(statusList.get(statusCount)).split("[|]")[1]+"|O2C Direct Transfer is successfull";
                                sucWriter.println(message);
                        }
                        else{
                                message = (statusList.get(statusCount)).split("[|]")[0]+"|"+BTSLUtil.getMessage(locale,(statusList.get(statusCount)).split("[|]")[2],null)+"|";
                                failWriter.println(message);
                        }
                }
                statusList.clear();
        }catch(Exception e){
                logger.errorTrace("writeToFile", e);
        }
    }

    /**
         * This method will copy all the created files to another location.
         * the process will generate files in a particular directroy. if the process thats has to read files strarts before copletion of the file generation,
         * errors will occur. so a different directory is created and files are moved to that final directory.
         * @param p_oldDirectoryPath String
         * @param p_finalDirectoryPath String
         * @param fileName String
         * @throws BTSLBaseException
         * @return void
         */
        private static void moveFilesToFinalDirectory(String oldDirectoryPath,String finalDirectoryPath,String fileName) throws BTSLBaseException{
                final String methodName = "moveFilesToFinalDirectory";
                if (logger.isDebugEnabled())
                        logger.debug(methodName," Entered: p_oldDirectoryPath="+oldDirectoryPath+" p_finalDirectoryPath="+finalDirectoryPath+"fileName ="+fileName);

                String oldFileName=null;
                String newFileName=null;
                File oldFile=null;
                File newFile=null;
                File parentDir = new File(finalDirectoryPath);
                if(!parentDir.exists())
                        parentDir.mkdirs();
                //child directory name includes a file name and being processed date, month and year
                File oldDir = new File(oldDirectoryPath);
                File newDir = new File(finalDirectoryPath);
                if(!newDir.exists())
                        newDir.mkdirs();
                if(logger.isDebugEnabled())
                    logger.debug(methodName, " newDirName=" + finalDirectoryPath);
                try{
                        oldFileName = oldDirectoryPath+fileName;
                        oldFile = new File(oldFileName);
                        newFileName = oldFileName.replace(oldDirectoryPath, finalDirectoryPath);
                        newFile = new File(newFileName);
                        if(oldFile != null){
                                oldFile.renameTo(newFile);
                                if (logger.isDebugEnabled())
                                        logger.debug(methodName," File " + oldFileName + " is moved to " + newFileName);
                        }else{
                                if (logger.isDebugEnabled()){
                                        logger.debug(methodName," File" + oldFileName + " is null");
                                }
                        }
                    logger.debug(methodName," File "+oldFileName+" is moved to "+newFileName);
                }
                catch(Exception e){
                        logger.error(methodName, "Exception " + e.getMessage());
                logger.errorTrace(methodName,e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DWHFileCreation[moveFilesToFinalDirectory]","","","","Exception:"+e.getMessage());
                    throw new BTSLBaseException("BatchO2CInitiation","deleteAllFiles",PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
                }finally{
                if(oldFile != null) 
                	oldFile = null;
                if(newFile != null) 
                    newFile = null;
                if(parentDir != null) 
                	parentDir = null;
                if(newDir != null) 
                	newDir = null;
                if(oldDir != null) 
                	oldDir = null;
            if (logger.isDebugEnabled())
                logger.debug(methodName, "Exiting.. ");
        } // end of finally
        }

        public static String createDirectory(){
        	String methodName="createDirectory";
                String dir = Constants.getProperty("FinalUploadBatchO2CInitiationFilePath");
                String dirName = null;
                String completeFinalDirPath = null;
                try{
                        dirName = (((BTSLUtil.getDateTimeStringFromDate(new Date())).replace("/","")).replace(":","")).replace(" ","");
                        completeFinalDirPath = dir + dirName;
                        File file = new File(completeFinalDirPath);
                        if (!file.exists()) {
                                if (file.mkdir()) {
                                	logger.debug(methodName,"Directory is created!");
                                } else {
                                	logger.debug(methodName,"Failed to create directory!");
                                }
                        }
                }catch(ParseException e){
                       logger.error(methodName, "Exception: "+e.getMessage());
                }finally{
                         if (logger.isDebugEnabled())
                                 logger.debug(methodName, "Exiting.. finalDirectoryName :: " + completeFinalDirPath);   
                }
                return completeFinalDirPath;
        }
}