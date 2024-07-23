package com.restapi.networkadmin.networkPrefix.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ErrorMap;
import com.btsl.common.IDGenerator;
import com.btsl.common.MasterErrorList;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.mcom.common.CommonUtil;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.restapi.networkadmin.networkPrefix.requestVO.SaveNetworkPrefixReqVO;
import com.restapi.networkadmin.networkPrefix.responseVO.NetworkPrefixRespVO;
import com.restapi.networkadmin.networkPrefix.serviceI.NetworkPrefixServiceI;
import com.restapi.networkadmin.service.NetworkPreferenceServiceImpl;
import com.web.pretups.network.businesslogic.NetworkWebDAO;



@Component
public class NetworkPrefixServiceImpl  implements NetworkPrefixServiceI {
	
	public static final Log log = LogFactory.getLog(NetworkPrefixServiceImpl.class.getName());
	
	
	
    private HashMap prepaidSeriesMap = null;
    private HashMap postpaidSeriesMap = null;
    private HashMap otherSeriesMap = null;
    private HashMap portPrepaidSeriesMap = null;
    private HashMap portPostpaidSeriesMap = null;
	

	
	
	
	@Override
	public NetworkPrefixRespVO loadNetworkPrefixDetails(String networkCode,Locale locale) throws BTSLBaseException {
		
		MComConnection mcomCon = null;
		Connection con = null;
		NetworkPrefixRespVO networkPrefixRespVO = new NetworkPrefixRespVO();
		HashMap map = new HashMap();
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
            NetworkWebDAO networkwebDAO = new NetworkWebDAO();
            ArrayList listNetworkPrefixes = networkwebDAO.loadNetworkPrefix(con,networkCode);

            prepaidSeriesMap = new HashMap();
            postpaidSeriesMap = new HashMap();
            otherSeriesMap = new HashMap();
            portPrepaidSeriesMap = new HashMap();
            portPostpaidSeriesMap = new HashMap();
            
            if(listNetworkPrefixes!=null && !listNetworkPrefixes.isEmpty()) {
            
            for (int i = 0, j = listNetworkPrefixes.size(); i < j; i++) {
                NetworkPrefixVO myVO = (NetworkPrefixVO) listNetworkPrefixes.get(i);

                String key = myVO.getOperator() + "_" + myVO.getSeriesType();
                if (map.containsKey(key)) {
                    String ser = (String) map.get(key);
                    if(ser.indexOf(myVO.getSeries())<0) {
	                    ser += "," + myVO.getSeries();
	                    map.put(key, ser);
                    }
                } else {
                    map.put(key, myVO.getSeries());
                }

                if (PretupsI.OPERATOR_TYPE_OPT.equals(myVO.getOperator()) && PretupsI.SERIES_TYPE_PREPAID.equals(myVO.getSeriesType())) {
                    prepaidSeriesMap.put(myVO.getSeries(), myVO);
                } else if (PretupsI.OPERATOR_TYPE_OPT.equals(myVO.getOperator()) && PretupsI.SERIES_TYPE_POSTPAID.equals(myVO.getSeriesType())) {
                    postpaidSeriesMap.put(myVO.getSeries(), myVO);
                } else if (PretupsI.OPERATOR_TYPE_OTH.equals(myVO.getOperator()) && PretupsI.SERIES_TYPE_PREPAID.equals(myVO.getSeriesType())) {
                    otherSeriesMap.put(myVO.getSeries(), myVO);
                }
                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MNP_ALLOWED))).booleanValue()) {
                    if (PretupsI.OPERATOR_TYPE_PORT.equals(myVO.getOperator()) && PretupsI.SERIES_TYPE_PREPAID.equals(myVO.getSeriesType())) {
                        portPrepaidSeriesMap.put(myVO.getSeries(), myVO);
                    } else if (PretupsI.OPERATOR_TYPE_PORT.equals(myVO.getOperator()) && PretupsI.SERIES_TYPE_POSTPAID.equals(myVO.getSeriesType())) {
                        portPostpaidSeriesMap.put(myVO.getSeries(), myVO);
                    }
                }
            }
            
            /*
             * When we fetch data from the DB it returns all the
             * prepaidseries,postpaidseries
             * and otherseries.
             * if operator = OPT and seriesType = PREPAID its an prepaid
             * series
             * if operator = OPT and seriesType = POSTPAID its an postpaid
             * series
             * if operator = OTH and seriesType = PREPAID its an other
             * series
             * if operator = PORT and seriesType = PORTPREPAID its an
             * prepaid series
             * if operator = PORT and seriesType = PORTPOSTPAID its an
             * postpaid series
             * so here first we create an hash map which contains three key
             * and there value
             * like OPT_PREPIAD = 12345,34567 (PREPIAD SERIES)
             * OPT_POSTPIAD = 12345,34567 (POSTPIAD SERIES)
             * OTH_PREPIAD = 12345,34567 (OTHER SERIES)
             * PORT_PREPIAD = 12345,34567 (POETPREPIAD SERIES)
             * PORT_POSTPIAD = 12345,34567 (PORTPOSTPIAD SERIES)
             */
            networkPrefixRespVO.setPrepaidSeries((String) map.get(PretupsI.OPERATOR_TYPE_OPT + "_" + PretupsI.SERIES_TYPE_PREPAID));
            networkPrefixRespVO.setPostpaidSeries((String) map.get(PretupsI.OPERATOR_TYPE_OPT + "_" + PretupsI.SERIES_TYPE_POSTPAID));
            networkPrefixRespVO.setOtherSeries((String) map.get(PretupsI.OPERATOR_TYPE_OTH + "_" + PretupsI.SERIES_TYPE_PREPAID));
            networkPrefixRespVO.setPortSeries((String) map.get(PretupsI.OPERATOR_TYPE_PORT + "_" + PretupsI.SERIES_TYPE_PREPAID));
            networkPrefixRespVO.setMessageCode(PretupsI.SUCCESS);
            networkPrefixRespVO.setMessage(PretupsI.SUCCESS);
            networkPrefixRespVO.setStatus(HttpStatus.SC_OK);
            
            }else {
            	networkPrefixRespVO.setMessageCode(PretupsErrorCodesI.NO_RECORDS_FOUND);
                networkPrefixRespVO.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.NO_RECORDS_FOUND, null));
                networkPrefixRespVO.setStatus(HttpStatus.SC_BAD_REQUEST);
            }
            
            
  	    }catch (SQLException se) {
			throw new BTSLBaseException("NetworkPrefixServiceImpl", "loadNetworkPrefixDetails",
					"Error while executing sql statement", se);
		}
		catch (Exception  e) {
			throw new BTSLBaseException("NetworkPrefixServiceImpl", "loadNetworkPrefixDetails",
					"Error while executing method loadNetworkPrefixDetails ", e);
		}finally {
			if (mcomCon != null) {
				mcomCon.close("");
				mcomCon = null;
			}
			if (con != null)
				try {
					con.close();
				} catch (SQLException se) {
					throw new BTSLBaseException("NetworkPrefixServiceImpl", "loadNetworkPrefixDetails",
							"Error while close connection", se);
				}
			
		}
		

		return networkPrefixRespVO;
	}



	@Override
	public NetworkPrefixRespVO saveNetworkPrefixDetails(String loggedinUserID,SaveNetworkPrefixReqVO saveNetworkPrefixReqVO, Locale locale)
			throws BTSLBaseException {
		final String methodName ="saveNetworkPrefixDetails";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered");
        }
		NetworkPrefixRespVO networkPrefixRespVO = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
        int insertCount = 0;
        UserVO userVO = null;
        String[]  msgParam = new String[1];
        ArrayList<MasterErrorList> listOfErrors = new ArrayList<MasterErrorList>();
        ArrayList seriesList = new ArrayList();
        MasterErrorList errorVO = null;
        try {
        	
        	mcomCon = new MComConnection();
			con = mcomCon.getConnection();
            NetworkWebDAO networkwebDAO = new NetworkWebDAO();
            UserDAO userDAO = new UserDAO();
            NetworkPrefixVO myVO = null;
            
            Date currentDate = new Date();
            userVO=  userDAO.loadUsersDetailsfromLoginID(con, loggedinUserID);
            //NetworkPrefixRespVO networkPRefixrespvo = loadNetworkPrefixDetails(userVO.getNetworkID(),locale);
            //log.debug("load network prefix",networkPRefixrespvo.getMessage() +networkPRefixrespvo.getMessageCode()); 
            validateMandatoryInputs(saveNetworkPrefixReqVO,listOfErrors,con,locale);
            if (listOfErrors != null && !listOfErrors.isEmpty()) {
                // this is used to display the errors(if any) on the first
                // page
             	return returnErrorResponse(listOfErrors);
		    }
            validatePrepaidSeries(saveNetworkPrefixReqVO.getPrepaidSeries(),seriesList,userVO,listOfErrors,con ,locale);
            validatePostpaidSeries(saveNetworkPrefixReqVO.getPostpaidSeries(),seriesList,userVO,listOfErrors,con ,locale);
            validateOtherSeries(saveNetworkPrefixReqVO.getOtherSeries(),seriesList,userVO,listOfErrors,con ,locale);
            validatePortSeries(saveNetworkPrefixReqVO.getPortSeries(),seriesList,userVO,listOfErrors,con ,locale);
            
            if (listOfErrors != null && !listOfErrors.isEmpty()) {
                // this is used to display the errors(if any) on the first
                // page
           	return returnErrorResponse(listOfErrors);
		    }
            networkPrefixRespVO=finalSaveorUpdate(seriesList,userVO,listOfErrors,locale,mcomCon);
            
        }catch (SQLException se) {
			throw new BTSLBaseException(this, methodName,
					"Error while executing sql statement", se);
		}
        catch (BTSLBaseException e) {
			throw e;
		}
		catch (Exception  e) {
			throw new BTSLBaseException(this, methodName,
					"Error while executing method "+methodName , e);
		}finally {
			if (mcomCon != null) {
				mcomCon.close("");
				mcomCon = null;
			}
			if (con != null)
				try {
					con.close();
				} catch (SQLException se) {
					throw new BTSLBaseException(this, methodName,
							"Error while close connection", se);
				}
			
		}	
		
	
		return networkPrefixRespVO;
	}
	
private NetworkPrefixRespVO returnErrorResponse(ArrayList listOfErrors) {
	NetworkPrefixRespVO networkPrefixRespVO = new NetworkPrefixRespVO();
	ErrorMap errorMap = new ErrorMap();
	errorMap.setMasterErrorList(listOfErrors);
	networkPrefixRespVO.setErrorMap(errorMap);
	networkPrefixRespVO.setStatus(HttpStatus.SC_BAD_REQUEST);
	return networkPrefixRespVO;
	
}
	
  private void validateMandatoryInputs(SaveNetworkPrefixReqVO saveNetworkPrefixReqVO,ArrayList listOfErrors,Connection con ,Locale locale) throws BTSLBaseException {
	  final String methodName ="validateMandatoryInputs";
	  String[]  msgParam = new String[1];
		if( BTSLUtil.isNullString(saveNetworkPrefixReqVO.getPrepaidSeries())
			 && 	
			 BTSLUtil.isNullString(saveNetworkPrefixReqVO.getPostpaidSeries())
			 &&
			 BTSLUtil.isNullString(saveNetworkPrefixReqVO.getOtherSeries())
		  	 &&
			 BTSLUtil.isNullString(saveNetworkPrefixReqVO.getPortSeries())
			){
					
					MasterErrorList error = new MasterErrorList();
	          		error.setErrorCode(PretupsI.NTWRK_PREFIX_SERIES_CANNOTBEBALNK);
	          		error.setErrorMsg(
	          				RestAPIStringParser.getMessage(locale, PretupsI.NTWRK_PREFIX_SERIES_CANNOTBEBALNK,null));
	          		listOfErrors.add(error);		
	     	} else {
	     		
	     		/*
                 * If a series defined in Other Series that series can not
                 * be defined for
                 * prepaid and postpaid so here we check the series
                 * duplication
                 */
	      		CommonUtil commonUtil = new CommonUtil();
	      		commonUtil.validateFieldComma(saveNetworkPrefixReqVO.getPrepaidSeries(),PretupsI.NTWRK_PREFIX_PRE_PAID_SERIES);
	     		commonUtil.validateFieldComma(saveNetworkPrefixReqVO.getPostpaidSeries(),PretupsI.NTWRK_PREFIX_POST_PAID_SERIES);
	     		commonUtil.validateFieldComma(saveNetworkPrefixReqVO.getOtherSeries(),PretupsI.NTWRK_PREFIX_OTHER_SERIES);
	     		commonUtil.validateFieldComma(saveNetworkPrefixReqVO.getPortSeries(),PretupsI.NTWRK_PREFIX_PORT_SERIES);
	            String series = null;

                StringTokenizer prepaidSeriesValue = null;
                if (saveNetworkPrefixReqVO.getPrepaidSeries() != null){
                    prepaidSeriesValue = new StringTokenizer(saveNetworkPrefixReqVO.getPrepaidSeries(), ",");
                }
                StringTokenizer postpaidSeriesValue = null;
                if (saveNetworkPrefixReqVO.getPostpaidSeries() != null) {
                    postpaidSeriesValue = new StringTokenizer(saveNetworkPrefixReqVO.getPostpaidSeries(), ",");
                }
                HashMap prepaidSeriesMap = new HashMap();
                /*
                 * if user enter a value like ,,,, this invalid value are
                 * not checked anywhere so here we check this validation
                 */
                if (!BTSLUtil.isNullString(saveNetworkPrefixReqVO.getPrepaidSeries()) && prepaidSeriesValue != null && prepaidSeriesValue.countTokens() == 0) {
					MasterErrorList error = new MasterErrorList();
	          		error.setErrorCode(PretupsI.NTWRK_PREFIX_PREPAID_SERIES_INVALID);
	          		error.setErrorMsg(
	          				RestAPIStringParser.getMessage(locale, PretupsI.NTWRK_PREFIX_PREPAID_SERIES_INVALID,null));
	          		listOfErrors.add(error);		

                }
      
                String[]  msgParam2 = null;
                while (prepaidSeriesValue != null && prepaidSeriesValue.hasMoreTokens()) {
                    series = ((String) prepaidSeriesValue.nextToken()).trim();
                    if(!BTSLUtil.isNumeric(series) ) {
                    	msgParam2 = new String[2];
                    	msgParam2[0]=series;
                    	msgParam2[1]=PretupsI.NTWRK_PREFIX_PRE_PAID_SERIES;
                    	MasterErrorList error = new MasterErrorList();
    	          		error.setErrorCode(PretupsI.NTWRK_PREFIX_NON_NUMERIC_VALUE);
    	          		error.setErrorMsg(
    	          				RestAPIStringParser.getMessage(locale, PretupsI.NTWRK_PREFIX_NON_NUMERIC_VALUE,msgParam2));
    	          		listOfErrors.add(error);
                    }
                    
                    if (!BTSLUtil.isNullString(series)) {
                    	prepaidSeriesMap.put(series, series);
                    }
                }
                
                
                while (postpaidSeriesValue != null && postpaidSeriesValue.hasMoreTokens()) {
                    series = ((String) postpaidSeriesValue.nextToken()).trim();
                    if(!BTSLUtil.isNumeric(series) ) {
                    	msgParam2 = new String[2];
                    	msgParam2[0]=series;
                    	msgParam2[1]=PretupsI.NTWRK_PREFIX_POST_PAID_SERIES;
                    	MasterErrorList error = new MasterErrorList();
    	          		error.setErrorCode(PretupsI.NTWRK_PREFIX_NON_NUMERIC_VALUE);
    	          		error.setErrorMsg(
    	          				RestAPIStringParser.getMessage(locale, PretupsI.NTWRK_PREFIX_NON_NUMERIC_VALUE,msgParam2));
    	          		listOfErrors.add(error);
                    }
                }


                StringTokenizer otherSeriesValue = null;
                if (saveNetworkPrefixReqVO.getOtherSeries() != null) {
                    otherSeriesValue = new StringTokenizer(saveNetworkPrefixReqVO.getOtherSeries(), ",");
                }
                HashMap otherSeriesMap = new HashMap();
                /*
                 * if user enter a value like ,,,, this invalid value not
                 * checked anywhere so here we check this validation
                 */
                if (!BTSLUtil.isNullString(saveNetworkPrefixReqVO.getOtherSeries()) && otherSeriesValue != null && otherSeriesValue.countTokens() == 0) {
                    //errors.add("error", new ActionMessage("network.networkprefix.errors.invalidotherseries"));
                	MasterErrorList error = new MasterErrorList();
	          		error.setErrorCode(PretupsI.NTWRK_PREFIX_PREPAID_SERIES_INVALID);
	          		error.setErrorMsg(
	          				RestAPIStringParser.getMessage(locale, PretupsI.NTWRK_PREFIX_PREPAID_SERIES_INVALID,null));
	          		listOfErrors.add(error);
                }
                
                while (otherSeriesValue != null && otherSeriesValue.hasMoreTokens()) {
                    series = ((String) otherSeriesValue.nextToken()).trim();
                    
                    if(!BTSLUtil.isNumeric(series) ) {
                    	msgParam2 = new String[2];
                    	msgParam2[0]=series;
                    	msgParam2[1]=PretupsI.NTWRK_PREFIX_OTHER_SERIES;
                    	MasterErrorList error = new MasterErrorList();
    	          		error.setErrorCode(PretupsI.NTWRK_PREFIX_NON_NUMERIC_VALUE);
    	          		error.setErrorMsg(
    	          				RestAPIStringParser.getMessage(locale, PretupsI.NTWRK_PREFIX_NON_NUMERIC_VALUE,msgParam2));
    	          		listOfErrors.add(error);
                    }
                    
                    if (!BTSLUtil.isNullString(series)) {
                    	otherSeriesMap.put(series, series);
                    }
                }
                if (saveNetworkPrefixReqVO.getOtherSeries() != null) {
                    otherSeriesValue = new StringTokenizer(saveNetworkPrefixReqVO.getOtherSeries(), ",");
                }

                while (otherSeriesValue != null && otherSeriesValue.hasMoreTokens()) {
                    series = ((String) otherSeriesValue.nextToken()).trim();
                    if (prepaidSeriesMap.containsKey(series)) {
                        //errors.add("error", new ActionMessage("network.networkprefix.errors.otherseriesprepaidduplication", series));
                    	msgParam[0]=series;
                    	MasterErrorList error = new MasterErrorList();
    	          		error.setErrorCode(PretupsI.NTWRK_PREFIX_OTHER_PREPAID_DUPLICATE);
    	          		error.setErrorMsg(
    	          				RestAPIStringParser.getMessage(locale, PretupsI.NTWRK_PREFIX_OTHER_PREPAID_DUPLICATE,msgParam));
    	          		listOfErrors.add(error);
                    	
                    }
                }
                
                
                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MNP_ALLOWED))).booleanValue()) {
                    StringTokenizer portPrepaidSeriesValue = null;
                    if (saveNetworkPrefixReqVO.getPortSeries() != null) {
                        portPrepaidSeriesValue = new StringTokenizer(saveNetworkPrefixReqVO.getPortSeries(), ",");
                    }
                    HashMap portPrepaidSeriesMap = new HashMap();
                    if (!BTSLUtil.isNullString(saveNetworkPrefixReqVO.getPortSeries()) && portPrepaidSeriesValue != null && portPrepaidSeriesValue.countTokens() == 0) {
                    	
                        //errors.add("error", new ActionMessage("network.networkprefix.errors.invalidportprepaidseries"));
                    	MasterErrorList error = new MasterErrorList();
    	          		error.setErrorCode(PretupsI.NTWRK_PREFIX_PORT_PREPAID_INVALID);
    	          		error.setErrorMsg(
    	          				RestAPIStringParser.getMessage(locale, PretupsI.NTWRK_PREFIX_PORT_PREPAID_INVALID,null));
    	          		listOfErrors.add(error);
                    }
                    while (portPrepaidSeriesValue != null && portPrepaidSeriesValue.hasMoreTokens()) {
                        series = ((String) portPrepaidSeriesValue.nextToken()).trim();
                        if(!BTSLUtil.isNumeric(series) ) {
                        	msgParam2 = new String[2];
                        	msgParam2[0]=series;
                        	msgParam2[1]=PretupsI.NTWRK_PREFIX_PORT_SERIES;
                        	MasterErrorList error = new MasterErrorList();
        	          		error.setErrorCode(PretupsI.NTWRK_PREFIX_NON_NUMERIC_VALUE);
        	          		error.setErrorMsg(
        	          				RestAPIStringParser.getMessage(locale, PretupsI.NTWRK_PREFIX_NON_NUMERIC_VALUE,msgParam2));
        	          		listOfErrors.add(error);
                        }
                   
                        if (!BTSLUtil.isNullString(series)  ) {
                        	portPrepaidSeriesMap.put(series, series);
                        }
                    }
                    if (saveNetworkPrefixReqVO.getPortSeries() != null) {
                        portPrepaidSeriesValue = new StringTokenizer(saveNetworkPrefixReqVO.getPortSeries(), ",");
                    }
                    while (portPrepaidSeriesValue != null && portPrepaidSeriesValue.hasMoreTokens()) {
                        series = ((String) portPrepaidSeriesValue.nextToken()).trim();
                        if (prepaidSeriesMap.containsKey(series)) {
                        	msgParam[0]=series;
                        	MasterErrorList error = new MasterErrorList();
        	          		error.setErrorCode(PretupsI.NTWRK_PREFIX_PORT_PREPAID_DUPLICATION);
        	          		error.setErrorMsg(
        	          				RestAPIStringParser.getMessage(locale, PretupsI.NTWRK_PREFIX_PORT_PREPAID_DUPLICATION,msgParam));
        	          		listOfErrors.add(error);
                            
                        }
                        
                        if (otherSeriesMap.containsKey(series)) {
                        	msgParam[0]=series;
                        	MasterErrorList error = new MasterErrorList();
        	          		error.setErrorCode(PretupsI.NTWRK_PREFIX_PORT_OTHER_DUPLICATION);
        	          		error.setErrorMsg(
        	          				RestAPIStringParser.getMessage(locale, PretupsI.NTWRK_PREFIX_PORT_OTHER_DUPLICATION,msgParam));
        	          		listOfErrors.add(error);
                            
                        }
                    }

                }

	     		
	     		
	     		
	     		
	     		
	     		
	     		
	     	}
		
		
		
		
		
		
		
		
		
		
		
		
		
	}
	
	
	
private NetworkPrefixRespVO finalSaveorUpdate(ArrayList seriesList,UserVO userVO,ArrayList listOfErrors,Locale locale,MComConnectionI mcomCon) throws SQLException, BTSLBaseException {
	final String methodName ="finalSaveorUpdateString";
	if (log.isDebugEnabled()) {
		log.debug(methodName, "Entered");
    }
	
	NetworkPrefixVO myVO=null; 
	Date currentDate = new Date();
	int insertCount=0;
	NetworkWebDAO networkwebDAO = new NetworkWebDAO();
	String[]  msgParam = new String[1];
	String[]  logParam = new String[5];
	NetworkPrefixRespVO networkPrefixRespVO= null;
	Connection con = mcomCon.getConnection();

    if (seriesList != null && !seriesList.isEmpty()) {
        insertCount = networkwebDAO.insertNetworkPrefix(con, seriesList);

        if (con != null) {
            if (insertCount > 0) {
            	mcomCon.finalCommit();
                // log the data in adminOperationLog.log
                AdminOperationVO adminOperationVO = new AdminOperationVO();
                adminOperationVO.setSource(PretupsI.LOGGER_NETWORK_PREFIXES);
                adminOperationVO.setDate(currentDate);
                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
                adminOperationVO.setLoginID(userVO.getLoginID());
                adminOperationVO.setUserID(userVO.getUserID());
                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                adminOperationVO.setNetworkCode(userVO.getNetworkID());
                adminOperationVO.setMsisdn(userVO.getMsisdn());
                for (int i = 0, j = seriesList.size(); i < j; i++) {
                    myVO = (NetworkPrefixVO) seriesList.get(i);
                    //adminOperationVO.setInfo("OPERATION (" + myVO.getDbFlag() + ") on network prefix type (" + myVO.getSeriesType() + "), prefix (" + myVO.getSeries() + ") with prefixID (" + myVO.getPrefixID() + ") has been performed with status (" + myVO.getStatus() + ") successfully");
                    logParam[0]=myVO.getDbFlag() ;
                    logParam[1]=myVO.getSeriesType() ;
                    logParam[2]=myVO.getSeries() ;
                    logParam[3]=String.valueOf(myVO.getPrefixID());
                    logParam[4]=myVO.getStatus() ;
                    adminOperationVO.setInfo(RestAPIStringParser.getMessage(locale, PretupsI.NTWRK_PREFIX_PORT_LOG_INFO, logParam));
                    AdminOperationLog.log(adminOperationVO);
                }
                networkPrefixRespVO = new NetworkPrefixRespVO();
                networkPrefixRespVO.setStatus(HttpStatus.SC_OK);
                networkPrefixRespVO.setMessageCode(PretupsI.NTWRK_PREFIX_SAVE_SUCCESS);
                networkPrefixRespVO.setMessage(RestAPIStringParser.getMessage(locale, PretupsI.NTWRK_PREFIX_SAVE_SUCCESS, null));
             } else {
            	mcomCon.finalRollback();
                //throw new BTSLBaseException(this, "save", "network.networkprefix.failedmessage", "DetailView");
                throw new BTSLBaseException(this, methodName,
						PretupsI.NTWRK_PREFIX_NOT_FOUND);
            }
        }
    } else {
        networkPrefixRespVO = new NetworkPrefixRespVO();
        networkPrefixRespVO.setStatus(HttpStatus.SC_OK);
        networkPrefixRespVO.setMessageCode(PretupsI.NTWRK_PREFIX_SAVE_SUCCESS);
        networkPrefixRespVO.setMessage(RestAPIStringParser.getMessage(locale, PretupsI.NTWRK_PREFIX_SAVE_SUCCESS, null));
    }


    return networkPrefixRespVO;

}
	
	
private void  validatePrepaidSeries(String prepaidSeries ,ArrayList seriesList,UserVO userVO,ArrayList listOfErrors,Connection con ,Locale locale) throws SQLException, BTSLBaseException {
	/*
     * Here we prepare a list of Prepaid series
     * First check the prepaidseries with the prepaidMap(load data
     * in that map while load the prefix list)
     * if series exist in the map no need to perform any database
     * change
     * if series not exist in the map add new record in the database
     * if series exist in the map but not in the prepaid series(user
     * delete that series), update
     * the record and perform the soft delete
     */
	
	NetworkPrefixVO myVO=null; 
	Date currentDate = new Date();
	NetworkWebDAO networkwebDAO = new NetworkWebDAO();
	String[]  msgParam = new String[1];
	StringTokenizer prepaid = new StringTokenizer(prepaidSeries, ",");
    while (prepaid.hasMoreElements()) {
        String series = prepaid.nextToken();
	
	if (prepaidSeriesMap != null && prepaidSeriesMap.size() > 0) {
        if (!(prepaidSeriesMap.containsKey(series))) {
            myVO = new NetworkPrefixVO();
            myVO.setNetworkCode(userVO.getNetworkID());
            myVO.setOperator(PretupsI.OPERATOR_TYPE_OPT);
            myVO.setPrefixId(IDGenerator.getNextID(con,PretupsI.NETWORK_PREFIX_ID, TypesI.ALL));
            myVO.setSeries(series);
            myVO.setSeriesType(PretupsI.SERIES_TYPE_PREPAID);
            myVO.setStatus(TypesI.YES);
            myVO.setDbFlag(PretupsI.DB_FLAG_INSERT);
            myVO.setCreatedOn(currentDate);
            myVO.setCreatedBy(userVO.getUserID());
            myVO.setModifiedOn(currentDate);
            myVO.setModifiedBy(userVO.getUserID());

            if (networkwebDAO.isNetworkPrefixExist(con, myVO)) {
            	
            	MasterErrorList error = new MasterErrorList();
            	msgParam[0]=series;
          		error.setErrorCode(PretupsI.NTWRK_PREFIX_PREAPIDSERIES_ALREADYEXIST);
          		error.setErrorMsg(
          				RestAPIStringParser.getMessage(locale, PretupsI.NTWRK_PREFIX_PREAPIDSERIES_ALREADYEXIST, msgParam));
          		listOfErrors.add(error);
            	
                
            }
            seriesList.add(myVO);
        } else {
            prepaidSeriesMap.remove(series);
        }
    } else {
        myVO = new NetworkPrefixVO();
        myVO.setNetworkCode(userVO.getNetworkID());
        myVO.setOperator(PretupsI.OPERATOR_TYPE_OPT);
        myVO.setPrefixId(IDGenerator.getNextID(con,PretupsI.NETWORK_PREFIX_ID, TypesI.ALL));
        myVO.setSeries(series);
        myVO.setSeriesType(PretupsI.SERIES_TYPE_PREPAID);
        myVO.setStatus(TypesI.YES);
        myVO.setDbFlag(PretupsI.DB_FLAG_INSERT);
        myVO.setCreatedOn(currentDate);
        myVO.setCreatedBy(userVO.getUserID());
        myVO.setModifiedOn(currentDate);
        myVO.setModifiedBy(userVO.getUserID());
        if (networkwebDAO.isNetworkPrefixExist(con, myVO)) {
        	MasterErrorList error = new MasterErrorList();
        	msgParam[0]=series;
      		error.setErrorCode(PretupsI.NTWRK_PREFIX_PREAPIDSERIES_ALREADYEXIST);
      		error.setErrorMsg(
      				RestAPIStringParser.getMessage(locale, PretupsI.NTWRK_PREFIX_PREAPIDSERIES_ALREADYEXIST, msgParam));
      		listOfErrors.add(error);
        }
        seriesList.add(myVO);
    }
    }
	
	if (prepaidSeriesMap != null && prepaidSeriesMap.size() > 0) {
        Iterator it = prepaidSeriesMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            myVO = (NetworkPrefixVO) pairs.getValue();
            if (networkwebDAO.isPrefixIDExistINIntMapping(con, myVO.getPrefixID())) {
            	MasterErrorList error = new MasterErrorList();
            	msgParam[0]=myVO.getSeries();
          		error.setErrorCode(PretupsI.NTWRK_PREFIX_PREAPIDSERIES_CANNOTDELETE);
          		error.setErrorMsg(
          				RestAPIStringParser.getMessage(locale, PretupsI.NTWRK_PREFIX_PREAPIDSERIES_CANNOTDELETE, msgParam));
          		listOfErrors.add(error);
            }
            myVO.setDbFlag(PretupsI.DB_FLAG_UPDATE);
            myVO.setStatus(TypesI.NO);
            myVO.setModifiedOn(currentDate);
            myVO.setModifiedBy(userVO.getUserID());
            seriesList.add(myVO);
        }
    }

}


private void  validatePostpaidSeries(String postPaidSeries,ArrayList seriesList,UserVO userVO,ArrayList listOfErrors,Connection con ,Locale locale) throws SQLException, BTSLBaseException {

    StringTokenizer postpaid = null;
    if (postPaidSeries != null){
        postpaid = new StringTokenizer(postPaidSeries, ",");
    }
	NetworkPrefixVO	myVO = null;
	Date currentDate= new Date();
	String[]  msgParam = new String[1];
	NetworkWebDAO networkWebDAO = new NetworkWebDAO();
    while (postpaid != null && postpaid.hasMoreElements()) {
        String series = postpaid.nextToken();
        if (postpaidSeriesMap != null && postpaidSeriesMap.size() > 0) {
            if (!(postpaidSeriesMap.containsKey(series))) {
                myVO = new NetworkPrefixVO();
                myVO.setNetworkCode(userVO.getNetworkID());
                myVO.setOperator(PretupsI.OPERATOR_TYPE_OPT);
                myVO.setPrefixId(IDGenerator.getNextID(con,PretupsI.NETWORK_PREFIX_ID, TypesI.ALL));
                myVO.setSeries(series);
                myVO.setSeriesType(PretupsI.SERIES_TYPE_POSTPAID);
                myVO.setStatus(TypesI.YES);
                myVO.setDbFlag(PretupsI.DB_FLAG_INSERT);
                myVO.setCreatedOn(currentDate);
                myVO.setCreatedBy(userVO.getUserID());
                myVO.setModifiedOn(currentDate);
                myVO.setModifiedBy(userVO.getUserID());

                if (networkWebDAO.isNetworkPrefixExist(con, myVO)) {
                	
                	MasterErrorList error = new MasterErrorList();
                	msgParam[0]=series;
              		error.setErrorCode(PretupsI.NTWRK_PREFIX_POSTAPIDSERIES_ALREADYEXIST);
              		error.setErrorMsg(
              				RestAPIStringParser.getMessage(locale, PretupsI.NTWRK_PREFIX_POSTAPIDSERIES_ALREADYEXIST, msgParam));
              		listOfErrors.add(error);
                }
                seriesList.add(myVO);
            } else {
                postpaidSeriesMap.remove(series);
            }
        } else {
            myVO = new NetworkPrefixVO();
            myVO.setNetworkCode(userVO.getNetworkID());
            myVO.setOperator(PretupsI.OPERATOR_TYPE_OPT);
            myVO.setPrefixId(IDGenerator.getNextID(con,PretupsI.NETWORK_PREFIX_ID, TypesI.ALL));
            myVO.setSeries(series);
            myVO.setSeriesType(PretupsI.SERIES_TYPE_POSTPAID);
            myVO.setStatus(TypesI.YES);
            myVO.setDbFlag(PretupsI.DB_FLAG_INSERT);
            myVO.setCreatedOn(currentDate);
            myVO.setCreatedBy(userVO.getUserID());
            myVO.setModifiedOn(currentDate);
            myVO.setModifiedBy(userVO.getUserID());
            if (networkWebDAO.isNetworkPrefixExist(con, myVO)) {
            	MasterErrorList error = new MasterErrorList();
            	msgParam[0]=series;
          		error.setErrorCode(PretupsI.NTWRK_PREFIX_POSTAPIDSERIES_ALREADYEXIST);
          		error.setErrorMsg(
          				RestAPIStringParser.getMessage(locale, PretupsI.NTWRK_PREFIX_POSTAPIDSERIES_ALREADYEXIST, msgParam));
          		listOfErrors.add(error);
            }
            seriesList.add(myVO);
        }
    }
    if (postpaidSeriesMap != null && postpaidSeriesMap.size() > 0) {
        Iterator it = postpaidSeriesMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            myVO = (NetworkPrefixVO) pairs.getValue();
            if (networkWebDAO.isPrefixIDExistINIntMapping(con, myVO.getPrefixID())) {
                MasterErrorList error = new MasterErrorList();
            	msgParam[0]=myVO.getSeries();
          		error.setErrorCode(PretupsI.NTWRK_PREFIX_POSTAPIDSERIES_CANNOTDELETE);
          		error.setErrorMsg(
          				RestAPIStringParser.getMessage(locale, PretupsI.NTWRK_PREFIX_POSTAPIDSERIES_CANNOTDELETE, msgParam));
          		listOfErrors.add(error);
            }
            myVO.setDbFlag(PretupsI.DB_FLAG_UPDATE);
            myVO.setStatus(TypesI.NO);
            myVO.setModifiedOn(currentDate);
            myVO.setModifiedBy(userVO.getUserID());
            seriesList.add(myVO);
        }
    }

	
}
	

private void  validateOtherSeries(String otherSeries,ArrayList seriesList,UserVO userVO,ArrayList listOfErrors,Connection con ,Locale locale) throws SQLException, BTSLBaseException {
	NetworkPrefixVO	myVO = null;
	Date currentDate= new Date();
	String[]  msgParam = new String[1];
	NetworkWebDAO networkWebDAO = new NetworkWebDAO();

    StringTokenizer other = null;
    if (otherSeries != null){
        other = new StringTokenizer(otherSeries, ",");
    }
    while (other != null && other.hasMoreElements()) {
        String series = other.nextToken();
        if (otherSeriesMap != null && otherSeriesMap.size() > 0) {
            if (!(otherSeriesMap.containsKey(series))) {
                myVO = new NetworkPrefixVO();
                myVO.setNetworkCode(userVO.getNetworkID());
                myVO.setOperator(PretupsI.OPERATOR_TYPE_OTH);
                myVO.setPrefixId(IDGenerator.getNextID(PretupsI.NETWORK_PREFIX_ID, TypesI.ALL));
                myVO.setSeries(series);
                myVO.setSeriesType(PretupsI.SERIES_TYPE_PREPAID);
                myVO.setStatus(TypesI.YES);
                myVO.setDbFlag(PretupsI.DB_FLAG_INSERT);
                myVO.setCreatedOn(currentDate);
                myVO.setCreatedBy(userVO.getUserID());
                myVO.setModifiedOn(currentDate);
                myVO.setModifiedBy(userVO.getUserID());

                if (networkWebDAO.isNetworkPrefixExist(con, myVO)) {
                    MasterErrorList error = new MasterErrorList();
                	msgParam[0]=series;
              		error.setErrorCode(PretupsI.NTWRK_PREFIX_OTHERDSERIES_ALREADY);
              		error.setErrorMsg(
              				RestAPIStringParser.getMessage(locale, PretupsI.NTWRK_PREFIX_OTHERDSERIES_ALREADY, msgParam));
              		listOfErrors.add(error);
                    
                }
                seriesList.add(myVO);
            } else {
                otherSeriesMap.remove(series);
            }
        } else {
            myVO = new NetworkPrefixVO();
            myVO.setNetworkCode(userVO.getNetworkID());
            myVO.setOperator(PretupsI.OPERATOR_TYPE_OTH);
            myVO.setPrefixId(IDGenerator.getNextID(PretupsI.NETWORK_PREFIX_ID, TypesI.ALL));
            myVO.setSeries(series);
            myVO.setSeriesType(PretupsI.SERIES_TYPE_PREPAID);
            myVO.setStatus(TypesI.YES);
            myVO.setDbFlag(PretupsI.DB_FLAG_INSERT);
            myVO.setCreatedOn(currentDate);
            myVO.setCreatedBy(userVO.getUserID());
            myVO.setModifiedOn(currentDate);
            myVO.setModifiedBy(userVO.getUserID());
            if (networkWebDAO.isNetworkPrefixExist(con, myVO)) {
            	MasterErrorList error = new MasterErrorList();
            	msgParam[0]=series;
          		error.setErrorCode(PretupsI.NTWRK_PREFIX_OTHERDSERIES_ALREADY);
          		error.setErrorMsg(
          				RestAPIStringParser.getMessage(locale, PretupsI.NTWRK_PREFIX_OTHERDSERIES_ALREADY, msgParam));
          		listOfErrors.add(error);
            }
            seriesList.add(myVO);
        }
    }
    if (otherSeriesMap != null && otherSeriesMap.size() > 0) {
        Iterator it = otherSeriesMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            myVO = (NetworkPrefixVO) pairs.getValue();
            if (networkWebDAO.isPrefixIDExistINIntMapping(con, myVO.getPrefixID())) {
                MasterErrorList error = new MasterErrorList();
            	msgParam[0]=myVO.getSeries();
          		error.setErrorCode(PretupsI.NTWRK_PREFIX_OTHERDSERIES_CANNOTDELETE);
          		error.setErrorMsg(
          				RestAPIStringParser.getMessage(locale, PretupsI.NTWRK_PREFIX_OTHERDSERIES_CANNOTDELETE, msgParam));
          		listOfErrors.add(error);
            }
            myVO.setDbFlag(PretupsI.DB_FLAG_UPDATE);
            myVO.setStatus(TypesI.NO);
            myVO.setModifiedOn(currentDate);
            myVO.setModifiedBy(userVO.getUserID());
            seriesList.add(myVO);
        }
    }


}


private void  validatePortSeries(String portPrepaidSeries,ArrayList seriesList,UserVO userVO,ArrayList listOfErrors,Connection con ,Locale locale) throws SQLException, BTSLBaseException {

	NetworkPrefixVO	myVO = null;
	Date currentDate= new Date();
	String[]  msgParam = new String[1];
	NetworkWebDAO networkWebDAO = new NetworkWebDAO();

    if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MNP_ALLOWED))).booleanValue()) {
        StringTokenizer portPrepaid = null;
        if (portPrepaidSeries != null) {
            portPrepaid = new StringTokenizer(portPrepaidSeries, ",");
        }
        while (portPrepaid != null && portPrepaid.hasMoreElements()) {
            String series = portPrepaid.nextToken();
            if (portPrepaidSeriesMap != null && portPrepaidSeriesMap.size() > 0) {
                if (!(portPrepaidSeriesMap.containsKey(series))) {
                    myVO = new NetworkPrefixVO();
                    myVO.setNetworkCode(userVO.getNetworkID());
                    myVO.setOperator(PretupsI.OPERATOR_TYPE_PORT);
                    myVO.setPrefixId(IDGenerator.getNextID(PretupsI.NETWORK_PREFIX_ID, TypesI.ALL));
                    myVO.setSeries(series);
                    myVO.setSeriesType(PretupsI.SERIES_TYPE_PREPAID);
                    myVO.setStatus(TypesI.YES);
                    myVO.setDbFlag(PretupsI.DB_FLAG_INSERT);
                    myVO.setCreatedOn(currentDate);
                    myVO.setCreatedBy(userVO.getUserID());
                    myVO.setModifiedOn(currentDate);
                    myVO.setModifiedBy(userVO.getUserID());

                    if (networkWebDAO.isNetworkPrefixExist(con, myVO)) {
                        MasterErrorList error = new MasterErrorList();
                    	msgParam[0]=series;
                  		error.setErrorCode(PretupsI.NTWRK_PREFIX_PORTPREPAID_ALREADYEXIST);
                  		error.setErrorMsg(
                  				RestAPIStringParser.getMessage(locale, PretupsI.NTWRK_PREFIX_PORTPREPAID_ALREADYEXIST, msgParam));
                  		listOfErrors.add(error);
                    }
                    seriesList.add(myVO);
                } else {
                    portPrepaidSeriesMap.remove(series);
                }
            } else {
                myVO = new NetworkPrefixVO();
                myVO.setNetworkCode(userVO.getNetworkID());
                myVO.setOperator(PretupsI.OPERATOR_TYPE_PORT);
                myVO.setPrefixId(IDGenerator.getNextID(con,PretupsI.NETWORK_PREFIX_ID, TypesI.ALL));
                myVO.setSeries(series);
                myVO.setSeriesType(PretupsI.SERIES_TYPE_PREPAID);
                myVO.setStatus(TypesI.YES);
                myVO.setDbFlag(PretupsI.DB_FLAG_INSERT);
                myVO.setCreatedOn(currentDate);
                myVO.setCreatedBy(userVO.getUserID());
                myVO.setModifiedOn(currentDate);
                myVO.setModifiedBy(userVO.getUserID());
                if (networkWebDAO.isNetworkPrefixExist(con, myVO)) {
                	MasterErrorList error = new MasterErrorList();
                	msgParam[0]=series;
              		error.setErrorCode(PretupsI.NTWRK_PREFIX_PORTPREPAID_ALREADYEXIST);
              		error.setErrorMsg(
              				RestAPIStringParser.getMessage(locale, PretupsI.NTWRK_PREFIX_PORTPREPAID_ALREADYEXIST, msgParam));
              		listOfErrors.add(error);
                }
                seriesList.add(myVO);
            }
        }
        if (portPrepaidSeriesMap != null && portPrepaidSeriesMap.size() > 0) {
            Iterator it = portPrepaidSeriesMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                myVO = (NetworkPrefixVO) pairs.getValue();
                if (networkWebDAO.isPrefixIDExistINIntMapping(con, myVO.getPrefixID())) {
                    MasterErrorList error = new MasterErrorList();
                	msgParam[0]=myVO.getSeries();
              		error.setErrorCode(PretupsI.NTWRK_PREFIX_PREAPIDSERIES_CANNOTDELETE);
              		error.setErrorMsg(
              				RestAPIStringParser.getMessage(locale, PretupsI.NTWRK_PREFIX_PREAPIDSERIES_CANNOTDELETE, msgParam));
              		listOfErrors.add(error);
                }
                myVO.setDbFlag(PretupsI.DB_FLAG_UPDATE);
                myVO.setStatus(TypesI.NO);
                myVO.setModifiedOn(currentDate);
                myVO.setModifiedBy(userVO.getUserID());
                seriesList.add(myVO);
            }
        }

    }
}







	public HashMap getPrepaidSeriesMap() {
		return prepaidSeriesMap;
	}





	public void setPrepaidSeriesMap(HashMap prepaidSeriesMap) {
		this.prepaidSeriesMap = prepaidSeriesMap;
	}





	public HashMap getPostpaidSeriesMap() {
		return postpaidSeriesMap;
	}





	public void setPostpaidSeriesMap(HashMap postpaidSeriesMap) {
		this.postpaidSeriesMap = postpaidSeriesMap;
	}





	public HashMap getOtherSeriesMap() {
		return otherSeriesMap;
	}





	public void setOtherSeriesMap(HashMap otherSeriesMap) {
		this.otherSeriesMap = otherSeriesMap;
	}





	public HashMap getPortPrepaidSeriesMap() {
		return portPrepaidSeriesMap;
	}





	public void setPortPrepaidSeriesMap(HashMap portPrepaidSeriesMap) {
		this.portPrepaidSeriesMap = portPrepaidSeriesMap;
	}





	public HashMap getPortPostpaidSeriesMap() {
		return portPostpaidSeriesMap;
	}





	public void setPortPostpaidSeriesMap(HashMap portPostpaidSeriesMap) {
		this.portPostpaidSeriesMap = portPostpaidSeriesMap;
	}





	
	
	

}
