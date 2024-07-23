package com.btsl.pretups.channel.reports.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.web.pretups.channel.reports.web.UsersReportModel;

/**
 * @author tarun.kumar
 *
 */
public class C2STransferRptDAO { 

	  private Log log = LogFactory.getLog(this.getClass().getName());	  
	  private String entered="entered";	
	  private C2STransferRptQry c2STransferRptQry;
	  private C2STransferReportsUserVO c2STransferReportsUserVO; 	  
	  private static final String RECEIVER_BONUS_VALUE= "receiver_bonus_value";
	  private static final String RECEIVER_TRANSFER_VALUE = "receiver_transfer_value";
	  private static final String RECEIVER_ACCESS_FEE = "receiver_access_fee";
	  private static final String RECEIVER_MSISDN = "reciever_msisdn";
	  private static final String TRANSFER_VALUE = "transfer_value";
	  private static final String REQUEST_GATEWAY_TYPE = "request_gateway_type";
	  private static final String SENDER_MSISDN = "sender_msisdn";
	  private static final String SERVICE_NAME = "service_name";
	  private static final String SERVICE_CLASS_NAME = "service_class_name";
	  private static final String SUBSERVICE_NAME = "subservice_name";
	  private static final String TRANSFER_ID= "transfer_id";
	  private static final String TRANSFER_DATE_TIME = "transfer_date_time";
	  private static final String USER_NAME = "user_name";
	/**
	 * Default Constructor
	 */
	public C2STransferRptDAO(){
		c2STransferRptQry = (C2STransferRptQry)ObjectProducer.getObject(QueryConstants.C2S_Transfer_Details_REPORT_QRY, QueryConstants.QUERY_PRODUCER);
    }
	/**
	 * @param con
	 * @param usersReportModel
	 * @return
	 */
	public List<C2STransferReportsUserVO> loadC2sTransferChannelUserReport(	Connection con, UsersReportModel usersReportModel) {
		
		 final String methodName = "loadC2sTransferChannelUserReport";		
		 if (log.isDebugEnabled()) {
            log.debug(methodName,entered);
        }       
        ArrayList<C2STransferReportsUserVO> reportList = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;      
        try {       	 
			 pstmt = c2STransferRptQry.loadC2sTransferChannelUserReport(con,usersReportModel);		 	       				 
			  rs = pstmt.executeQuery();          
	            while(rs.next()){           	    
	            	c2STransferReportsUserVO= new C2STransferReportsUserVO();        	
		         	c2STransferReportsUserVO.setBonus(String.valueOf(rs.getString(RECEIVER_BONUS_VALUE)));
		         	c2STransferReportsUserVO.setCreditAmount(String.valueOf(rs.getString(RECEIVER_TRANSFER_VALUE)));
		         	c2STransferReportsUserVO.setProcessFee(String.valueOf(rs.getString(RECEIVER_ACCESS_FEE)));
		         	c2STransferReportsUserVO.setReceiverMobileNumber(rs.getString(RECEIVER_MSISDN));
		         	c2STransferReportsUserVO.setRequestAmount(String.valueOf(rs.getString(TRANSFER_VALUE)));
		         	c2STransferReportsUserVO.setRequestSource(rs.getString(REQUEST_GATEWAY_TYPE));
		         	c2STransferReportsUserVO.setSenderMobileNumber(rs.getString(SENDER_MSISDN));
		         	c2STransferReportsUserVO.setService(rs.getString(SERVICE_NAME));
		         	c2STransferReportsUserVO.setServiceClass(rs.getString(SERVICE_CLASS_NAME));
		         	c2STransferReportsUserVO.setSubService(rs.getString(SUBSERVICE_NAME));
		         	c2STransferReportsUserVO.setTransactionId(rs.getString(TRANSFER_ID));
		         	c2STransferReportsUserVO.setTransferTime(rs.getString(TRANSFER_DATE_TIME));
		         	c2STransferReportsUserVO.setUserName(rs.getString(USER_NAME));        	                        	                	            
		         	reportList.add(c2STransferReportsUserVO);
             }
             if (log.isDebugEnabled()) {
                 log.debug(methodName,reportList);
             }   
		} catch (SQLException e) {			
			 log.errorTrace(methodName, e);
		}finally {
            try {
                if (rs != null) {
                	rs.close();
                }
            } catch (Exception e) {
            	log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                	pstmt.close();
                }
            } catch (Exception e) {
            	log.errorTrace(methodName, e);
            }
		}
        return reportList;
	}
	
	
/**
 * @param con
 * @param usersReportModel
 * @return
 */
public List<C2STransferReportsUserVO> loadC2sTransferChannelUserStaffReport(Connection con, UsersReportModel usersReportModel) {
		
	final String methodName = "loadC2sTransferChannelUserReport";
	if (log.isDebugEnabled()) {
        log.debug(methodName,entered);
    }   
    ArrayList<C2STransferReportsUserVO> reportList = new ArrayList<>();
    PreparedStatement pstmt = null;
    ResultSet rs = null;      
    try {       	 
		 pstmt = c2STransferRptQry.loadC2sTransferChannelUserStaffReport(con,usersReportModel);			       			
		  rs = pstmt.executeQuery();          
           while(rs.next()){           	    
        	   c2STransferReportsUserVO= new C2STransferReportsUserVO();        	
	         	c2STransferReportsUserVO.setBonus(String.valueOf(rs.getString(RECEIVER_BONUS_VALUE)));
	         	c2STransferReportsUserVO.setCreditAmount(String.valueOf(rs.getString(RECEIVER_TRANSFER_VALUE)));
	         	c2STransferReportsUserVO.setProcessFee(String.valueOf(rs.getString(RECEIVER_ACCESS_FEE)));
	         	c2STransferReportsUserVO.setReceiverMobileNumber(rs.getString(RECEIVER_MSISDN));
	         	c2STransferReportsUserVO.setRequestAmount(String.valueOf(rs.getString(TRANSFER_VALUE)));
	         	c2STransferReportsUserVO.setRequestSource(rs.getString(REQUEST_GATEWAY_TYPE));
	         	c2STransferReportsUserVO.setSenderMobileNumber(rs.getString(SENDER_MSISDN));
	         	c2STransferReportsUserVO.setService(rs.getString(SERVICE_NAME));
	         	c2STransferReportsUserVO.setServiceClass(rs.getString(SERVICE_CLASS_NAME));
	         	c2STransferReportsUserVO.setSubService(rs.getString(SUBSERVICE_NAME));
	         	c2STransferReportsUserVO.setTransactionId(rs.getString(TRANSFER_ID));
	         	c2STransferReportsUserVO.setTransferTime(rs.getString(TRANSFER_DATE_TIME));
	         	c2STransferReportsUserVO.setUserName(rs.getString(USER_NAME));        	                        	                	            
	         	reportList.add(c2STransferReportsUserVO);
         }
         if (log.isDebugEnabled()) {
             log.debug("loadC2sTransferChannelUserStaffReportList",reportList);
         }   
	} catch (SQLException e) {			
		 log.errorTrace(methodName, e);
	}finally {
        try {
            if (rs != null) {
            	rs.close();
            }
        } catch (Exception e) {
        	log.errorTrace(methodName, e);
        }
        try {
            if (pstmt != null) {
            	pstmt.close();
            }
        } catch (Exception e) {
        	log.errorTrace(methodName, e);
        }
	}
    return reportList;
	}
/**
 * @param con
 * @param usersReportModel
 * @return
 */
public List<C2STransferReportsUserVO> loadC2sTransferChannelUserNewReport(Connection con, UsersReportModel usersReportModel) {
	
	 final String methodName = "loadC2sTransferChannelUserNewReport";
		if (log.isDebugEnabled()) {
         log.debug(methodName,entered);
        }   
     ArrayList<C2STransferReportsUserVO> reportList = new ArrayList<>();
     PreparedStatement pstmt = null; 
     ResultSet rs = null;      
     try {       	 
		  pstmt = c2STransferRptQry.loadC2sTransferChannelUserNewReport(con,usersReportModel);		 	       			 
		  rs = pstmt.executeQuery();          
            while(rs.next()){           	    
            	c2STransferReportsUserVO= new C2STransferReportsUserVO();        	
	         	c2STransferReportsUserVO.setBonus(String.valueOf(rs.getString(RECEIVER_BONUS_VALUE)));
	         	c2STransferReportsUserVO.setCreditAmount(String.valueOf(rs.getString(RECEIVER_TRANSFER_VALUE)));
	         	c2STransferReportsUserVO.setProcessFee(String.valueOf(rs.getString(RECEIVER_ACCESS_FEE)));
	         	c2STransferReportsUserVO.setReceiverMobileNumber(rs.getString(RECEIVER_MSISDN));
	         	c2STransferReportsUserVO.setRequestAmount(String.valueOf(rs.getString(TRANSFER_VALUE)));
	         	c2STransferReportsUserVO.setRequestSource(rs.getString(REQUEST_GATEWAY_TYPE));
	         	c2STransferReportsUserVO.setSenderMobileNumber(rs.getString(SENDER_MSISDN));
	         	c2STransferReportsUserVO.setService(rs.getString(SERVICE_NAME));
	         	c2STransferReportsUserVO.setServiceClass(rs.getString(SERVICE_CLASS_NAME));
	         	c2STransferReportsUserVO.setSubService(rs.getString(SUBSERVICE_NAME));
	         	c2STransferReportsUserVO.setTransactionId(rs.getString(TRANSFER_ID));
	         	c2STransferReportsUserVO.setTransferTime(rs.getString(TRANSFER_DATE_TIME));
	         	c2STransferReportsUserVO.setUserName(rs.getString(USER_NAME));        	                        	                	            
	         	reportList.add(c2STransferReportsUserVO);
          }
          if (log.isDebugEnabled()) {
              log.debug("loadC2sTransferChannelUserReportList",reportList);
          }   
		} catch (SQLException e) {			
			 log.errorTrace(methodName, e);
		}finally {
            try {
                if (rs != null) {
                	rs.close();
                }
            } catch (Exception e) {
            	log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                	pstmt.close();
                }
            } catch (Exception e) {
            	log.errorTrace(methodName, e);
            }
		}
     return reportList;
   }
/**
 * @param con
 * @param usersReportModel
 * @return
 */
public List<C2STransferReportsUserVO> loadC2sTransferChannelUserStaffNewReport(	Connection con, UsersReportModel usersReportModel) {	
	
	final String methodName = "loadC2sTransferChannelUserStaffNewReport";
	if (log.isDebugEnabled()) {
     log.debug(methodName,entered);
 }

 ArrayList<C2STransferReportsUserVO> reportList = new ArrayList<>();
 PreparedStatement pstmt = null;
 ResultSet rs = null;      
 try {       	 
	  pstmt = c2STransferRptQry.loadC2sTransferChannelUserStaffNewReport(con,usersReportModel);		 	       		  
	  rs = pstmt.executeQuery();          
        while(rs.next()){           	    
        	c2STransferReportsUserVO= new C2STransferReportsUserVO();        	
         	c2STransferReportsUserVO.setBonus(String.valueOf(rs.getString(RECEIVER_BONUS_VALUE)));
         	c2STransferReportsUserVO.setCreditAmount(String.valueOf(rs.getString(RECEIVER_TRANSFER_VALUE)));
         	c2STransferReportsUserVO.setProcessFee(String.valueOf(rs.getString(RECEIVER_ACCESS_FEE)));
         	c2STransferReportsUserVO.setReceiverMobileNumber(rs.getString(RECEIVER_MSISDN));
         	c2STransferReportsUserVO.setRequestAmount(String.valueOf(rs.getString(TRANSFER_VALUE)));
         	c2STransferReportsUserVO.setRequestSource(rs.getString(REQUEST_GATEWAY_TYPE));
         	c2STransferReportsUserVO.setSenderMobileNumber(rs.getString(SENDER_MSISDN));
         	c2STransferReportsUserVO.setService(rs.getString(SERVICE_NAME));
         	c2STransferReportsUserVO.setServiceClass(rs.getString(SERVICE_CLASS_NAME));
         	c2STransferReportsUserVO.setSubService(rs.getString(SUBSERVICE_NAME));
         	c2STransferReportsUserVO.setTransactionId(rs.getString(TRANSFER_ID));
         	c2STransferReportsUserVO.setTransferTime(rs.getString(TRANSFER_DATE_TIME));
         	c2STransferReportsUserVO.setUserName(rs.getString(USER_NAME));        	                        	                	            
         	reportList.add(c2STransferReportsUserVO);
      }
      if (log.isDebugEnabled()) {
          log.debug("loadC2sTransferChannelUserReportList",reportList);
      }   
	} catch (SQLException e) {			
		 log.errorTrace(methodName, e);
	}finally {
        try {
            if (rs != null) {
            	rs.close();
            }
        } catch (Exception e) {
        	log.errorTrace(methodName, e);
        }
        try {
            if (pstmt != null) {
            	pstmt.close();
            }
        } catch (Exception e) {
        	log.errorTrace(methodName, e);
        }
	}
       return reportList;
   }
/**
 * @param con
 * @param usersReportModel
 * @return
 */
public List<C2STransferReportsUserVO> loadC2sTransferNewReport(Connection con,UsersReportModel usersReportModel) {
	
	   final String methodName = "loadC2sTransferNewReport";
	   if (log.isDebugEnabled()) {
        log.debug(methodName,entered);
       }

 ArrayList<C2STransferReportsUserVO> reportList = new ArrayList<>();
 PreparedStatement pstmt = null;
 ResultSet rs = null;      
 try {       	 
	  pstmt = c2STransferRptQry.loadC2sTransferNewReport(con,usersReportModel);		 	       		  
	  rs = pstmt.executeQuery();          
        while(rs.next()){           	    
        	c2STransferReportsUserVO= new C2STransferReportsUserVO();        	
         	c2STransferReportsUserVO.setBonus(String.valueOf(rs.getString(RECEIVER_BONUS_VALUE)));
         	c2STransferReportsUserVO.setCreditAmount(String.valueOf(rs.getString(RECEIVER_TRANSFER_VALUE)));
         	c2STransferReportsUserVO.setProcessFee(String.valueOf(rs.getString(RECEIVER_ACCESS_FEE)));
         	c2STransferReportsUserVO.setReceiverMobileNumber(rs.getString(RECEIVER_MSISDN));
         	c2STransferReportsUserVO.setRequestAmount(String.valueOf(rs.getString(TRANSFER_VALUE)));
         	c2STransferReportsUserVO.setRequestSource(rs.getString(REQUEST_GATEWAY_TYPE));
         	c2STransferReportsUserVO.setSenderMobileNumber(rs.getString(SENDER_MSISDN));
         	c2STransferReportsUserVO.setService(rs.getString(SERVICE_NAME));
         	c2STransferReportsUserVO.setServiceClass(rs.getString(SERVICE_CLASS_NAME));
         	c2STransferReportsUserVO.setSubService(rs.getString(SUBSERVICE_NAME));
         	c2STransferReportsUserVO.setTransactionId(rs.getString(TRANSFER_ID));
         	c2STransferReportsUserVO.setTransferTime(rs.getString(TRANSFER_DATE_TIME));
         	c2STransferReportsUserVO.setUserName(rs.getString(USER_NAME));        	                        	                	            
         	reportList.add(c2STransferReportsUserVO);
      }
      if (log.isDebugEnabled()) {
          log.debug("loadC2sTransferNewReportList",reportList);
      }   
	} catch (SQLException e) {			
		 log.errorTrace(methodName, e);
	}finally {
        try {
            if (rs != null) {
            	rs.close();
            }
        } catch (Exception e) {
        	log.errorTrace(methodName, e);
        }
        try {
            if (pstmt != null) {
            	pstmt.close();
            }
        } catch (Exception e) {
        	log.errorTrace(methodName, e);
        }
	}
       return reportList;
}
/**
 * @param con
 * @param usersReportModel
 * @return
 */
public List<C2STransferReportsUserVO> loadC2sTransferReport(Connection con,	UsersReportModel usersReportModel) {
	
	final String methodName = "loadC2sTransferReport";
	if (log.isDebugEnabled()) {
     log.debug(methodName,entered);
    }

	 ArrayList<C2STransferReportsUserVO> reportList = new ArrayList<>();
	 PreparedStatement pstmt = null;
	 ResultSet rs = null;      
	 try {       	 
		 pstmt = c2STransferRptQry.loadC2sTransferReport(con,usersReportModel);		 	       			 
		  rs = pstmt.executeQuery();          
           while(rs.next()){           	    
        	   c2STransferReportsUserVO= new C2STransferReportsUserVO();        	
	         	c2STransferReportsUserVO.setBonus(String.valueOf(rs.getString(RECEIVER_BONUS_VALUE)));
	         	c2STransferReportsUserVO.setCreditAmount(String.valueOf(rs.getString(RECEIVER_TRANSFER_VALUE)));
	         	c2STransferReportsUserVO.setProcessFee(String.valueOf(rs.getString(RECEIVER_ACCESS_FEE)));
	         	c2STransferReportsUserVO.setReceiverMobileNumber(rs.getString(RECEIVER_MSISDN));
	         	c2STransferReportsUserVO.setRequestAmount(String.valueOf(rs.getString(TRANSFER_VALUE)));
	         	c2STransferReportsUserVO.setRequestSource(rs.getString(REQUEST_GATEWAY_TYPE));
	         	c2STransferReportsUserVO.setSenderMobileNumber(rs.getString(SENDER_MSISDN));
	         	c2STransferReportsUserVO.setService(rs.getString(SERVICE_NAME));
	         	c2STransferReportsUserVO.setServiceClass(rs.getString(SERVICE_CLASS_NAME));
	         	c2STransferReportsUserVO.setSubService(rs.getString(SUBSERVICE_NAME));
	         	c2STransferReportsUserVO.setTransactionId(rs.getString(TRANSFER_ID));
	         	c2STransferReportsUserVO.setTransferTime(rs.getString(TRANSFER_DATE_TIME));
	         	c2STransferReportsUserVO.setUserName(rs.getString(USER_NAME));        	                        	                	            
	         	reportList.add(c2STransferReportsUserVO);
      }
      if (log.isDebugEnabled()) {
          log.debug("loadC2sTransferReportList",reportList);
      }   
	} catch (SQLException e) {			
		 log.errorTrace(methodName, e);
	}finally {
        try {
            if (rs != null) {
            	rs.close();
            }
        } catch (Exception e) {
        	log.errorTrace(methodName, e);
        }
        try {
            if (pstmt != null) {
            	pstmt.close();
            }
        } catch (Exception e) {
        	log.errorTrace(methodName, e);
        }
	}
       return reportList;
}
/**
 * @param con
 * @param usersReportModel
 * @return
 */
public List<C2STransferReportsUserVO> loadC2sTransferStaffNewReport(Connection con, UsersReportModel usersReportModel) {
	
	final String methodName = "loadC2sTransferStaffNewReport";
	if (log.isDebugEnabled()) {
     log.debug(methodName,entered);
    }
 ArrayList<C2STransferReportsUserVO> reportList = new ArrayList<>();
 PreparedStatement pstmt = null;
 ResultSet rs = null;      
 try {       	 
	  pstmt = c2STransferRptQry.loadC2sTransferStaffNewReport(con,usersReportModel);		 	       		  
	  rs = pstmt.executeQuery();          
        while(rs.next()){           	    
        	c2STransferReportsUserVO= new C2STransferReportsUserVO();        	
         	c2STransferReportsUserVO.setBonus(String.valueOf(rs.getString(RECEIVER_BONUS_VALUE)));
         	c2STransferReportsUserVO.setCreditAmount(String.valueOf(rs.getString(RECEIVER_TRANSFER_VALUE)));
         	c2STransferReportsUserVO.setProcessFee(String.valueOf(rs.getString(RECEIVER_ACCESS_FEE)));
         	c2STransferReportsUserVO.setReceiverMobileNumber(rs.getString(RECEIVER_MSISDN));
         	c2STransferReportsUserVO.setRequestAmount(String.valueOf(rs.getString(TRANSFER_VALUE)));
         	c2STransferReportsUserVO.setRequestSource(rs.getString(REQUEST_GATEWAY_TYPE));
         	c2STransferReportsUserVO.setSenderMobileNumber(rs.getString(SENDER_MSISDN));
         	c2STransferReportsUserVO.setService(rs.getString(SERVICE_NAME));
         	c2STransferReportsUserVO.setServiceClass(rs.getString(SERVICE_CLASS_NAME));
         	c2STransferReportsUserVO.setSubService(rs.getString(SUBSERVICE_NAME));
         	c2STransferReportsUserVO.setTransactionId(rs.getString(TRANSFER_ID));
         	c2STransferReportsUserVO.setTransferTime(rs.getString(TRANSFER_DATE_TIME));
         	c2STransferReportsUserVO.setUserName(rs.getString(USER_NAME));        	                        	                	            
         	reportList.add(c2STransferReportsUserVO);
      }
      if (log.isDebugEnabled()) {
          log.debug("loadC2sTransferStaffNewReportList",reportList);
      }   
	} catch (SQLException e) {			
		 log.errorTrace(methodName, e);
	}finally {
        try {
            if (rs != null) {
            	rs.close();
            }
        } catch (Exception e) {
        	log.errorTrace(methodName, e);
        }
        try {
            if (pstmt != null) {
            	pstmt.close();
            }
        } catch (Exception e) {
        	log.errorTrace(methodName, e);
        }
	}
       return reportList;
  }
/**
 * @param con
 * @param usersReportModel
 * @return
 */
public List<C2STransferReportsUserVO> loadC2sTransferStaffReport(Connection con, UsersReportModel usersReportModel) {
	
	final String methodName = "loadC2sTransferStaffReport";
	if (log.isDebugEnabled()) {
     log.debug(methodName,entered);
    }
 ArrayList<C2STransferReportsUserVO> reportList = new ArrayList<>();
 PreparedStatement pstmt = null;
 ResultSet rs = null;      
 try {       	 
	  pstmt = c2STransferRptQry.loadC2sTransferStaffReport(con,usersReportModel);		 	       		 
	  rs = pstmt.executeQuery();          
        while(rs.next()){           	    
        	c2STransferReportsUserVO= new C2STransferReportsUserVO();        	
         	c2STransferReportsUserVO.setBonus(String.valueOf(rs.getString(RECEIVER_BONUS_VALUE)));
         	c2STransferReportsUserVO.setCreditAmount(String.valueOf(rs.getString(RECEIVER_TRANSFER_VALUE)));
         	c2STransferReportsUserVO.setProcessFee(String.valueOf(rs.getString(RECEIVER_ACCESS_FEE)));
         	c2STransferReportsUserVO.setReceiverMobileNumber(rs.getString(RECEIVER_MSISDN));
         	c2STransferReportsUserVO.setRequestAmount(String.valueOf(rs.getString(TRANSFER_VALUE)));
         	c2STransferReportsUserVO.setRequestSource(rs.getString(REQUEST_GATEWAY_TYPE));
         	c2STransferReportsUserVO.setSenderMobileNumber(rs.getString(SENDER_MSISDN));
         	c2STransferReportsUserVO.setService(rs.getString(SERVICE_NAME));
         	c2STransferReportsUserVO.setServiceClass(rs.getString(SERVICE_CLASS_NAME));
         	c2STransferReportsUserVO.setSubService(rs.getString(SUBSERVICE_NAME));
         	c2STransferReportsUserVO.setTransactionId(rs.getString(TRANSFER_ID));
         	c2STransferReportsUserVO.setTransferTime(rs.getString(TRANSFER_DATE_TIME));
         	c2STransferReportsUserVO.setUserName(rs.getString(USER_NAME));        	                        	                	            
         	reportList.add(c2STransferReportsUserVO);
      }
      if (log.isDebugEnabled()) {
          log.debug("loadC2sTransferStaffReportList",reportList);
      }   
	} catch (SQLException e) {			
		 log.errorTrace(methodName, e);
	}finally {
        try {
            if (rs != null) {
            	rs.close();
            }
        } catch (Exception e) {
        	log.errorTrace(methodName, e);
        }
        try {
            if (pstmt != null) {
            	pstmt.close();
            }
        } catch (Exception e) {
        	log.errorTrace(methodName, e);
        }
	}
       return reportList;
   }
}
