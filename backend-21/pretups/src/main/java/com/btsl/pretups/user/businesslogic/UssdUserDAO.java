package com.btsl.pretups.user.businesslogic;

/**
 * @(#)UssdUserDAO.java
 *                      Copyright(c) 2010, Comviva Technologies Ltd.
 *                      All Rights Reserved
 * 
 *                      --------------------------------------------------------
 *                      -----------------------------------------
 *                      Author Date History
 *                      --------------------------------------------------------
 *                      -----------------------------------------
 *                      Ashish Kumar Todia 24Sept10 Creation.
 *                      --------------------------------------------------------
 *                      -----------------------------------------
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.SearchCriteria;
import com.btsl.util.SearchCriteria.BooleanOperator;
import com.btsl.util.SearchCriteria.Operator;
import com.btsl.util.SearchCriteria.ValueType;

public class UssdUserDAO {
    private Log log = LogFactory.getLog(this.getClass().getName());
    private UssdUserQry ussdUserQry = (UssdUserQry)ObjectProducer.getObject(QueryConstants.USSD_USER_QRY, QueryConstants.QUERY_PRODUCER);
    
    private String exception = " Exception: ";
    private String sqlException = "SQL Exception: ";
    private String query = " Query: ";
    private String errorGeneralProcessing =  "error.general.processing";
    private String errorGeneralSqlProcessing = "error.general.sql.processing";

    /**
     * Method loadUsersDetails.
     * This method is used to load all the information used to display Operator
     * User view
     * 
     * @param con
     *            Connection
     * @param msisdn
     *            String
     * @return ChannelUserVO
     * @throws BTSLBaseException
     */
    public UserVO loadUsersDetails(Connection con, String msisdn) throws BTSLBaseException {
    	 final String methodName = "loadUsersDetails";
    	if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_msisdn= " + msisdn);
        }

       
        ResultSet rs = null;
        UserVO userVO = null;
       
        final String sqlSelect = ussdUserQry.loadUsersDetailsQry();
        
        try(PreparedStatement pstmtSelect = con.prepareStatement(sqlSelect);) {
            
            pstmtSelect.setString(1, msisdn);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                userVO = new ChannelUserVO();
                userVO.setUserID(rs.getString("usr_user_id"));
                userVO.setUserName(rs.getString("usr_user_name"));
                userVO.setNetworkID(rs.getString("network_code"));
                userVO.setLoginID(rs.getString("login_id"));
                userVO.setPassword(rs.getString("password1"));
                userVO.setCategoryCode(rs.getString("usr_category_code"));
                userVO.setParentID(rs.getString("parent_id"));
                userVO.setOwnerID(rs.getString("owner_id"));
                userVO.setAllowedIps(rs.getString("allowed_ip"));
                userVO.setAllowedDays(rs.getString("allowed_days"));
                userVO.setFromTime(rs.getString("from_time"));
                userVO.setToTime(rs.getString("to_time"));
                userVO.setEmpCode(rs.getString("employee_code"));
                userVO.setStatus(rs.getString("usr_status"));
                userVO.setEmail(rs.getString("email"));
                userVO.setPasswordModifiedOn(rs.getTimestamp("pswd_modified_on"));
                userVO.setContactNo(rs.getString("contact_no"));
                userVO.setDesignation(rs.getString("designation"));
                userVO.setDivisionCode(rs.getString("division"));
                userVO.setDepartmentCode(rs.getString("department"));
                userVO.setMsisdn(rs.getString("usr_msisdn"));
                userVO.setUserType(rs.getString("user_type"));
                userVO.setCreatedBy(rs.getString("created_by"));
                userVO.setCreatedOn(rs.getTimestamp("created_on"));
                userVO.setModifiedBy(rs.getString("modified_by"));
                userVO.setModifiedOn(rs.getTimestamp("modified_on"));
                userVO.setLastModified(rs.getTimestamp("modified_on").getTime());
                userVO.setAddress1(rs.getString("address1"));
                userVO.setAddress2(rs.getString("address2"));
                userVO.setCity(rs.getString("city"));
                userVO.setState(rs.getString("state"));
                userVO.setCountry(rs.getString("country"));
                userVO.setSsn(rs.getString("ssn"));
                userVO.setUserNamePrefix(rs.getString("user_name_prefix"));
                userVO.setExternalCode(rs.getString("external_code"));
                userVO.setShortName(rs.getString("short_name"));
                userVO.setLevel1ApprovedBy(rs.getString("level1_approved_by"));
                userVO.setLevel1ApprovedOn(rs.getTimestamp("level1_approved_on"));
                userVO.setLevel2ApprovedBy(rs.getString("level2_approved_by"));
                userVO.setLevel2ApprovedOn(rs.getTimestamp("level2_approved_on"));
                userVO.setUserCode(rs.getString("user_code"));
                userVO.setAppointmentDate(rs.getTimestamp("appointment_date"));

                final CategoryVO categoryVO = new CategoryVO();
                categoryVO.setCategoryCode(rs.getString("usr_cat_category_code"));
                categoryVO.setCategoryName(rs.getString("category_name"));
                categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
                categoryVO.setSequenceNumber(rs.getInt("sequence_no"));
                categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));
                categoryVO.setMultipleGrphDomains(rs.getString("multiple_grph_domains"));
                categoryVO.setWebInterfaceAllowed(rs.getString("web_interface_allowed"));
                categoryVO.setSmsInterfaceAllowed(rs.getString("sms_interface_allowed"));
                categoryVO.setFixedRoles(rs.getString("fixed_roles"));
                categoryVO.setCategoryStatus(rs.getString("usr_cat_status"));
                categoryVO.setMultipleLoginAllowed(rs.getString("multiple_login_allowed"));

                userVO.setCategoryVO(categoryVO);

                userVO.setParentName(rs.getString("parent_name"));
                userVO.setParentMsisdn(rs.getString("parent_msisdn"));
                userVO.setParentCategoryName(rs.getString("parent_cat"));

                userVO.setOwnerName(rs.getString("owner_name"));
                userVO.setOwnerMsisdn(rs.getString("owner_msisdn"));
                userVO.setOwnerCategoryName(rs.getString("owner_cat"));

            }
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersDetails]", "", "", "",
                            sqlException + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserDAO[loadUsersDetails]", "", "", "",
                            exception + ex.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralProcessing);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: userVO=" + userVO);
            }
        }
        return userVO;
    }

    /**
     * Method loadCategoryDetails.
     * This method is used to load category details according to domain code
     * from Categories Table
     * 
     * @param con
     *            Connection
     * @param p_categoryCode
     *            String
     * @return categoryList ArrayList
     * @throws BTSLBaseException
     */
    public boolean checkValidUserCreation(Connection con, String fromCategoryCode, String tocategoryCode, String networkCode) throws BTSLBaseException {
    	 final String methodName = "checkValidUserCreation";
    	if (log.isDebugEnabled()) {
            log.debug(methodName,
                            "Entered p_fromCategoryCode=" + fromCategoryCode + "p_tocategoryCode" + tocategoryCode + " p_networkCode " + networkCode);
        }
       
        boolean isValid = false;
        ResultSet rs = null;
        final StringBuilder strBuff = new StringBuilder(" SELECT TRANSFER_RULE_ID FROM CHNL_TRANSFER_RULES  WHERE  NETWORK_CODE=? ");
        strBuff.append("AND status=? AND FROM_CATEGORY=? ");
        strBuff.append("AND TO_CATEGORY=? AND PARENT_ASSOCIATION_ALLOWED=?");

        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, query + sqlSelect);
        }

        try(PreparedStatement pstmtSelect = con.prepareStatement(sqlSelect);) {
            
            pstmtSelect.setString(1, networkCode);
            pstmtSelect.setString(2, PretupsI.YES);
            pstmtSelect.setString(3, fromCategoryCode);
            pstmtSelect.setString(4, tocategoryCode);
            pstmtSelect.setString(5, PretupsI.YES);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                isValid = true;
            }
        }

        catch (SQLException sqe) {
            log.error(methodName, sqlException + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryDAO[loadCategoryDetailsUsingCategoryCode]",
                            "", "", "", sqlException + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
        } catch (Exception e) {
            log.error(methodName, exception + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryDAO[loadCategoryDetailsUsingCategoryCode]",
                            "", "", "", exception + e.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralProcessing);

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting Status: " + isValid);
            }
        }

        return isValid;
    }

    /**
     * To Check category is in same the domain of the parent user who is
     * creating and
     * user does not belong to root category.
     * 
     * @param con
     * @param categoryCode
     * @param domainCode
     * @return
     * @throws BTSLBaseException
     */
    public boolean checkCategoryCode(Connection con, String categoryCode, String domainCode) throws BTSLBaseException {
    	  final String methodName = "checkCategoryCode";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_categoryCode= " + categoryCode + "p_domainCode" + domainCode);
        }
      
        boolean isValid = false;
        ResultSet rs = null;
        final StringBuilder strBuff = new StringBuilder(" SELECT 1 FROM  CATEGORIES WHERE  CATEGORY_CODE=? ");
        strBuff.append(" AND STATUS=?  AND DOMAIN_CODE=? AND SEQUENCE_NO <>'1' ");

        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, query + sqlSelect);
        }

        try (PreparedStatement pstmtSelect = con.prepareStatement(sqlSelect);){
           
            pstmtSelect.setString(1, categoryCode);
            pstmtSelect.setString(2, PretupsI.YES);
            pstmtSelect.setString(3, domainCode);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                isValid = true;
            }
        } catch (SQLException sqe) {
            log.error(methodName, sqlException + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryDAO[loadCategoryDetailsUsingCategoryCode]",
                            "", "", "", sqlException + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
        } catch (Exception e) {
            log.error(methodName, exception + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryDAO[loadCategoryDetailsUsingCategoryCode]",
                            "", "", "", exception + e.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralProcessing);

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting Status: " + isValid);
            }
        }
        return isValid;
    }

    /**
     * Method to load default roles.
     * 
     * @param con
     * @param categoryCode
     * @return
     * @throws BTSLBaseException
     */
    public String loadDefaultRole(Connection con, String categoryCode) throws BTSLBaseException {
    	 final String methodName = "loadDefaultRole";
    	if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_categoryCode= " + categoryCode);
        }
       
        String defaultRole = null;
        ResultSet rs = null;
        final StringBuilder strBuff = new StringBuilder(" SELECT r.role_code ");
        strBuff.append(" FROM CATEGORY_ROLES cr,ROLES r ,CATEGORIES c,DOMAINS d ");
        strBuff.append(" WHERE r.status != 'N' AND cr.category_code = ? ");
        strBuff.append(" AND cr.category_code=c.category_code  AND c.domain_code=d.domain_code ");
        strBuff.append(" AND d.domain_type_code=r.domain_type  AND r.group_role =? AND cr.role_code = r.role_code");
        strBuff.append(" AND  r.IS_DEFAULT=? ORDER BY r.group_name,role_name");

        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, query + sqlSelect);
        }
        int count = 0;
        try(PreparedStatement pstmtSelect = con.prepareStatement(sqlSelect);) {
            
            pstmtSelect.setString(1, categoryCode);
            pstmtSelect.setString(2, PretupsI.YES);
            pstmtSelect.setString(3, PretupsI.YES);

            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                defaultRole = rs.getString("ROLE_CODE");
                count++;
            }
            if (count != 1) {
                defaultRole = null;
            }
        } catch (SQLException sqe) {
            log.error(methodName, sqlException + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryDAO[loadCategoryDetailsUsingCategoryCode]",
                            "", "", "", sqlException + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
        } catch (Exception e) {
            log.error(methodName, exception + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryDAO[loadCategoryDetailsUsingCategoryCode]",
                            "", "", "", exception + e.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralProcessing);

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);

            }
           
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting defaultRole: " + defaultRole);
            }
        }
        return defaultRole;
    }

    /**
     * Method to load default geography.
     * 
     * @param con
     * @param parentMsisdn
     * @param childCatgCode
     * @return
     * @throws BTSLBaseException
     */
    public String loadDefaultGeography(Connection con, String parentMsisdn, String childCatgCode) throws BTSLBaseException {
    	  final String methodName = "loadDefaultGeography";
    	if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_parentMsisdn= " + parentMsisdn + "p_childCatgCode " + childCatgCode);
        }

      
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
 
 

        int count = 0;
        String defaultGeog = null;
        try {
          
        	pstmtSelect = ussdUserQry.loadDefaultGeographyQry(con, parentMsisdn, childCatgCode);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                defaultGeog = rs.getString("GRPH_DOMAIN_CODE");
                count++;
            }
            if (count != 1) // only default geography code should be returned.
            {
                defaultGeog = null;
            }
        } catch (SQLException sqe) {
            log.error(methodName, sqlException + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryDAO[loadCategoryDetailsUsingCategoryCode]",
                            "", "", "", sqlException + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
        } catch (Exception e) {
            log.error(methodName, exception + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryDAO[loadCategoryDetailsUsingCategoryCode]",
                            "", "", "", exception + e.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralProcessing);

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting defaultGeog: " + defaultGeog);
            }
        }
        return defaultGeog;
    }

    /**
     * Method to load default grade category based.
     * 
     * @return
     * @throws BTSLBaseException
     */
    public String loadDefaultGrade(Connection con, String categoryCode) throws BTSLBaseException {
    	final String methodName = "loadDefaultGrade";
    	if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_categoryCode= " + categoryCode);
        }
        
        String defaultGrade = null;

        ResultSet rs = null;
        final StringBuilder strBuff = new StringBuilder(" SELECT GRADE_CODE FROM  CHANNEL_GRADES WHERE  CATEGORY_CODE=? ");
        strBuff.append(" AND STATUS=? AND IS_DEFAULT_GRADE=? ");

        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, query + sqlSelect);
        }
        int count = 0;
        try(PreparedStatement pstmtSelect = con.prepareStatement(sqlSelect);) {
           
            pstmtSelect.setString(1, categoryCode);
            pstmtSelect.setString(2, PretupsI.YES);
            pstmtSelect.setString(3, PretupsI.YES);

            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                defaultGrade = rs.getString("GRADE_CODE");
                count++;
            }
            if (count != 1) {
                defaultGrade = null;
            }
        } catch (SQLException sqe) {
            log.error(methodName, sqlException + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryDAO[loadCategoryDetailsUsingCategoryCode]",
                            "", "", "", sqlException + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
        } catch (Exception e) {
            log.error(methodName, exception + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryDAO[loadCategoryDetailsUsingCategoryCode]",
                            "", "", "", exception + e.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralProcessing);

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting defaultGrade: " + defaultGrade);
            }
        }
        return defaultGrade;
    }

    /**
     * Methof to load default commission profile.
     * 
     * @param con
     * @param categoryCode
     * @return
     * @throws BTSLBaseException
     */
    public String loadDefaultCommissionProfile(Connection con, String categoryCode) throws BTSLBaseException {
    	 final String methodName = "loadDefaultCommissionProfile";
    	if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_categoryCode= " + categoryCode);
        }
       
        String defaultCommission = null;

        ResultSet rs = null;
  

        final StringBuilder strBuff = new StringBuilder("SELECT COMM_PROFILE_SET_ID FROM COMMISSION_PROFILE_SET WHERE CATEGORY_CODE=? ");
        strBuff.append(" AND status=? AND IS_DEFAULT=? ");
        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, query + sqlSelect);
        }
        int count = 0;
        try(PreparedStatement pstmtSelect = con.prepareStatement(sqlSelect);) {
           
            pstmtSelect.setString(1, categoryCode);
            pstmtSelect.setString(2, PretupsI.YES);
            pstmtSelect.setString(3, PretupsI.YES);

            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                defaultCommission = rs.getString("COMM_PROFILE_SET_ID");
                count++;
            }
            if (count != 1) {
                defaultCommission = null;
            }
        } catch (SQLException sqe) {
            log.error(methodName, sqlException + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryDAO[loadCategoryDetailsUsingCategoryCode]",
                            "", "", "", sqlException + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
        } catch (Exception e) {
            log.error(methodName, exception + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryDAO[loadCategoryDetailsUsingCategoryCode]",
                            "", "", "", exception + e.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralProcessing);

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
           
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting defaultCommission: " + defaultCommission);
            }
        }
        return defaultCommission;
    }

    /**
     * Method to load default TransferProfile.
     * 
     * @param con
     * @param categoryCode
     * @return
     * @throws BTSLBaseException
     */
    public String loadDetaultTransferProfile(Connection con, String categoryCode) throws BTSLBaseException {
    	 final String methodName = "loadDetaultTransferProfile";
    	if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_categoryCode= " + categoryCode);
        }
       
        String defaultTransferProfile = null;
        ResultSet rs = null;
        StringBuilder strBuff = null;
        
		String tcpMicroServiceOn = Constants.getProperty("TCP.MICROSERVICE.ON");
		boolean tcpOn = false;
		Set<String> uniqueTransProfileId = new HashSet();

		java.util.List<HashMap<String, String>> resultSet = null;
		if (tcpMicroServiceOn != null && tcpMicroServiceOn.equalsIgnoreCase("Y")) {
			tcpOn = true;
		}

		if (tcpOn) {

			SearchCriteria searchCriteria = new SearchCriteria("CATEGORY_CODE", Operator.EQUALS, categoryCode,
					ValueType.STRING,null).addCriteria(new SearchCriteria("status", Operator.EQUALS, PretupsI.YES,
					ValueType.STRING,null), BooleanOperator.AND)
					.addCriteria(new SearchCriteria("IS_DEFAULT", Operator.EQUALS, PretupsI.YES,
							ValueType.STRING,null), BooleanOperator.AND);
			
			
			
			resultSet = BTSLUtil.invokeService("TRANSFER_PROFILE", new HashSet<String>(Arrays.asList("profile_id","status")), searchCriteria);
        	
        	///(new HashSet<String>(Arrays.asList("profile_id","status")), searchCriteria);
        	
        	
		} else {

			strBuff = new StringBuilder(" SELECT PROFILE_ID FROM  TRANSFER_PROFILE WHERE CATEGORY_CODE=? ");
			strBuff.append(" AND status=? AND IS_DEFAULT=? ");
		}
        
        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug("loadDefaultCommissionProfile", query + sqlSelect);
        }
        int count = 0;
        
        if(!tcpOn) {
        try (PreparedStatement pstmtSelect =  con.prepareStatement(sqlSelect); ){
            pstmtSelect.setString(1, categoryCode);
            pstmtSelect.setString(2, PretupsI.YES);
            pstmtSelect.setString(3, PretupsI.YES);

            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                defaultTransferProfile = rs.getString("PROFILE_ID");
                count++;
            }
            if (count != 1) {
                defaultTransferProfile = null;
            }
        } catch (SQLException sqe) {
            log.error(methodName, sqlException + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryDAO[loadCategoryDetailsUsingCategoryCode]",
                            "", "", "", sqlException + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
        } catch (Exception e) {
            log.error(methodName, exception + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryDAO[loadCategoryDetailsUsingCategoryCode]",
                            "", "", "", exception + e.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralProcessing);

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
        
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting defaultTransferProfile: " + defaultTransferProfile);
            }
        }
        }else {
        	
        	defaultTransferProfile = resultSet.get(0).get("profileId") ;
        }
        
        return defaultTransferProfile;
    }

    /**
     * This method checks the existence of the external code associated to the
     * user.
     * 
     * @param con
     * @param extcode
     * @return
     * @throws BTSLBaseException
     */
    public boolean isExternalCodeExist(Connection con, String extcode) throws BTSLBaseException {
        
    	 final String methodName = "isExternalCodeExist";
    	if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_extcode= " + extcode);
        }
       
        boolean isExist = false;
        ResultSet rs = null;
        final StringBuilder strBuff = new StringBuilder(" SELECT  1 FROM ");
        strBuff.append(" USERS WHERE status NOT IN ('C','N')  AND EXTERNAL_CODE=? ");
        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, query + sqlSelect);
        }
        try(PreparedStatement pstmtSelect = con.prepareStatement(sqlSelect);) {
           
            pstmtSelect.setString(1, extcode);

            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                isExist = true;
            }
        } catch (SQLException sqe) {
            log.error(methodName, sqlException + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryDAO[loadCategoryDetailsUsingCategoryCode]",
                            "", "", "", sqlException + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralSqlProcessing);
        } catch (Exception e) {
            log.error(methodName, exception + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryDAO[loadCategoryDetailsUsingCategoryCode]",
                            "", "", "", exception + e.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralProcessing);

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
           
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting isExist: " + isExist);
            }
        }
        return isExist;
    }
}
