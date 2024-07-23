package com.client.pretups.processes.clientprocesses;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.currencyconversion.businesslogic.CurrencyConversionDAO;
import com.btsl.pretups.currencyconversion.businesslogic.CurrencyConversionVO;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.inter.moneyconversion.stub.Os_tipo_cambioBindingStub;
import com.inter.moneyconversion.stub.Os_tipo_cambioServiceLocator;
import com.inter.moneyconversion.stub.Tipo_cambio_request;
import com.inter.moneyconversion.stub.Tipo_cambio_response;

/**
 * This class is used to update currency conversion table by hitting a SAP web service.
 * @author 
 * @since 26/09/2017
 *
 */

public class MoneyConversionProcess {
    private static final Log LOG = LogFactory.getLog(MoneyConversionProcess.class.getName());
    private static Os_tipo_cambioBindingStub stub=null;
    
    /**
	 * Main method for class
	 * 
     * @param arg String []
     * @throws  java.sql.SQLException
     */
    public static void main(String [] arg) {
        final String methodName = "main";
        try {
            if (arg.length < 2) {
                LogFactory.printLog(methodName, "Usage : MoneyConversionProcess [Constants file] [LogConfig file]", LOG);
                return;
            }
            final File constantsFile = new File(arg[0]);
            if (!constantsFile.exists()) {
                LogFactory.printLog(methodName, " Constants File Not Found .............", LOG);
                return;
            }
            final File logconfigFile = new File(arg[1]);
            if (!logconfigFile.exists()) {
                LogFactory.printLog(methodName, " Logconfig File Not Found .............", LOG);
                return;
            }

            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());

        } catch (Exception e) {
            LogFactory.printLog(methodName, " Error in Loading Files ...........................: " + e.getMessage(), LOG);
            LOG.errorTrace(methodName, e);
            ConfigServlet.destroyProcessCache();
            return;
        }
        try {
            process();
        } catch (Exception e) {
            LogFactory.printLog(methodName, " " + e.getMessage(), LOG);
            LOG.errorTrace(methodName, e);
        } finally {
            LogFactory.printLog(methodName, "Exiting", LOG);
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            ConfigServlet.destroyProcessCache();
        }
    }
    
    private static void process() throws BTSLBaseException {

        Connection con = null;
        List currencyList = null;
        List <CurrencyConversionVO> finalCurrencyList = new ArrayList<CurrencyConversionVO>();
        CurrencyConversionVO currencyConversionVO = null;
        String conversionFactor = null;
		
        final String methodName = "process";
        try {
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                LogFactory.printLog(methodName, " DATABASE Connection is NULL ", LOG);
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                		"MoneyConversionProcess[process]", "", "", "", "DATABASE Connection is NULL");
                return;
            }

            currencyList = new CurrencyConversionDAO().loadCurrencyConversionDetailsList(con);
            for (int i = 0, size = currencyList.size(); i < size; i++) {
                currencyConversionVO = (CurrencyConversionVO) currencyList.get(i);
                conversionFactor = getConversionFactor(currencyConversionVO.getSourceCurrencyCode(),currencyConversionVO.getTargetCurrencyCode());
                currencyConversionVO.setConversion(Double.parseDouble(conversionFactor));
                finalCurrencyList.add(currencyConversionVO);
            }
            new CurrencyConversionDAO().updateCurrencyConversionRate(con, finalCurrencyList);
        } catch (BTSLBaseException be) {
            LOG.error(methodName, "BTSLBaseException : " + be.getMessage());
            LOG.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            LOG.error(methodName, "Exception : " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "MoneyConversionProcess[process]", "", "", "",
                " MoneyConversionProcess process could not be executed successfully.");
        } finally {
            OracleUtil.closeQuietly(con);
            LogFactory.printLog(methodName, "Exiting..... ", LOG);
        }
    
    }
    
    private static String getConversionFactor(String sourceCurrencyCode, String targetCurrencyCode){
        final String methodName = "getConversionFactor";
        LogFactory.printLog(methodName, "Entering MoneyConversionProcess", LOG);
        String timeOut = null;
        String endpoint = null;
        String exchangeRate = null;
	
        try {
            Os_tipo_cambioServiceLocator osTipoCambioServiceLocator = new Os_tipo_cambioServiceLocator();
        	
            try{
                endpoint=Constants.getProperty("MONEY_CONV_WEB_SERVICE_URL");
                timeOut=Constants.getProperty("MONEY_CONV_TIMEOUT");
            }catch(Exception e){
                LogFactory.printLog(methodName, "Could not find entries in Constants.props", LOG);
            }         
            
            try{
                stub=(Os_tipo_cambioBindingStub)osTipoCambioServiceLocator.getHTTP_Port(new java.net.URL(endpoint));	
                stub.setTimeout(Integer.valueOf(timeOut));
            }catch(Exception e){
                LogFactory.printLog(methodName, "Unable to get Client Stub", LOG);
            } 
            
            Tipo_cambio_request request = new Tipo_cambio_request(sourceCurrencyCode, targetCurrencyCode);
            long startTime =System.currentTimeMillis();
            Tipo_cambio_response response = stub.os_tipo_cambio(request);    
            long endTime =System.currentTimeMillis();
            long timeTaken=endTime-startTime;
            LogFactory.printLog(methodName, "Time Taken for response by Money Conversion Service is "+timeTaken, LOG);
		
            if(response!=null){
                String returncode=response.getRETURN_CODE();
                if("00".equalsIgnoreCase(returncode)){
                    LogFactory.printLog(methodName, "Success response received from Money Conversion Service. Conversion Factor received is "
                +response.getEXCHANGE_RATE(), LOG);
				
                    exchangeRate = response.getEXCHANGE_RATE();	
				}else{
                    LogFactory.printLog(methodName, "Failure response received from Money Conversion Service. Return Code received is "+returncode, LOG);
				}	
			}else{
                LogFactory.printLog(methodName, "No response received from Money Conversion Service", LOG);
			}
		
            LogFactory.printLog(methodName, "Exiting MoneyConversionProcess", LOG);
	    } catch (Exception ex) {
            LogFactory.printLog(methodName, "Exception while retrieving response from Money Conversion Service", LOG);
            LOG.error(methodName, "Exception e:" + ex.getMessage());
            LOG.errorTrace(methodName, ex);
	    }
        return exchangeRate;
	}
}
