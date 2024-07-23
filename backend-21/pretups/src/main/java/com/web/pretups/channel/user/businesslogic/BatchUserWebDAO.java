package com.web.pretups.channel.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/*//import org.apache.struts.action.ActionForm;*/
import com.btsl.util.MessageResources;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.EmailSendToUser;
import com.btsl.common.IDGenerator;
import com.btsl.common.IDGeneratorDAO;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.batch.businesslogic.BatchesVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.BatchesLog;
import com.btsl.pretups.logging.ChannelUserLog;
import com.btsl.pretups.master.businesslogic.GeographicalDomainTypeVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.Batchc2sRevEntryVO;
import com.btsl.user.businesslogic.SessionInfoVO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.SqlParameterEncoder;
import com.web.pretups.channel.user.web.BatchBarForDelForm;


public class BatchUserWebDAO {

    /**
     * Field log. This field is used to display the logs for debugging purpose.
     */
    private Log log = LogFactory.getLog(this.getClass().getName());
    
    private BatchUserWebQry batchUserWebQry = (BatchUserWebQry) ObjectProducer.getObject(QueryConstants.BATCH_USER_WEB_QRY, QueryConstants.QUERY_PRODUCER);

    /**
     * Method :loadMasterGeographyList
     * this method load list of Geographics on the basis of geographical code
     * and login user id.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_geographicsCode
     *            java.lang.String
     * @param p_loginUserID
     *            java.lang.String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     * @author Ved Prakash
     */
    public ArrayList loadMasterGeographyList(Connection p_con, String p_geographicsCode, String p_loginUserID) throws BTSLBaseException {
        final String methodName = "loadMasterGeographyList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_geographicsCode=" + p_geographicsCode + " p_loginUserID=" + p_loginUserID);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        String sqlSelect = batchUserWebQry.loadMasterGeographyListQry();
        final ArrayList geographicesList = new ArrayList();
        try {
        	pstmtSelect = p_con.prepareStatement(sqlSelect);
    		int i = 1;
    		pstmtSelect.setString(i, p_geographicsCode);
    		i++;
    		pstmtSelect.setString(i, p_geographicsCode);
    		i++;
    		pstmtSelect.setString(i, p_loginUserID);
            rs = pstmtSelect.executeQuery();
            UserGeographiesVO userGeographiesVO = null;
            while (rs.next()) {
                userGeographiesVO = new UserGeographiesVO();
                userGeographiesVO.setGraphDomainCode(rs.getString("geography_code"));
                userGeographiesVO.setGraphDomainName(rs.getString("geography_name"));
                userGeographiesVO.setGraphDomainType(rs.getString("grph_domain_type"));
                userGeographiesVO.setGraphDomainTypeName(rs.getString("grph_domain_type_name"));
                userGeographiesVO.setGraphDomainSequenceNumber(rs.getInt("sequence_no"));
                geographicesList.add(userGeographiesVO);
            }
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[loadMasterGeographyList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[loadMasterGeographyList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: geographicesList size =" + geographicesList.size());
            }
        }
        return geographicesList;
    }

    /**
     * Method :loadCategoryGeographyTypeList
     * this method load list of Geographices type on the basis of domain code
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_domainCode
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     * @author Ved Prakash
     */
    public ArrayList loadCategoryGeographyTypeList(Connection p_con, String p_domainCode) throws BTSLBaseException {
        final String methodName = "loadCategoryGeographyTypeList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_domainCode=" + p_domainCode);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer(
            " SELECT CAT.category_code, CAT.grph_domain_type, GDT.grph_domain_type_name FROM categories CAT,geographical_domain_types GDT ");
        strBuff.append(" WHERE CAT.domain_code=? AND CAT.status='Y' AND CAT.grph_domain_type=GDT.grph_domain_type ");
        strBuff.append(" ORDER BY CAT.sequence_no  ");
        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList geographicesList = new ArrayList();
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            int i = 1;
            pstmtSelect.setString(i, p_domainCode);
            i++;
            rs = pstmtSelect.executeQuery();
            GeographicalDomainTypeVO geographicalDomainTypeVO = null;
            while (rs.next()) {
                geographicalDomainTypeVO = new GeographicalDomainTypeVO();
                geographicalDomainTypeVO.setCategoryCode(SqlParameterEncoder.encodeParams(rs.getString("category_code")));
                geographicalDomainTypeVO.setGrphDomainType(SqlParameterEncoder.encodeParams(rs.getString("grph_domain_type")));
                geographicalDomainTypeVO.setGrphDomainTypeName(SqlParameterEncoder.encodeParams(rs.getString("grph_domain_type_name")));
                geographicesList.add(geographicalDomainTypeVO);
            }
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[loadCategoryGeographyTypeList]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[loadCategoryGeographyTypeList]", "",
                "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: geographicesList size =" + geographicesList.size());
            }
        }
        return geographicesList;
    }

    /**
     * Method :loadMasterCategoryHierarchyList
     * This method load list of Categories on the basis of domain code and
     * network code.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_domainCode
     *            java.lang.String
     * @param p_networkCode
     *            java.lang.String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     * @author Ved Prakash
     */
    public ArrayList loadMasterCategoryHierarchyList(Connection p_con, String p_domainCode, String p_networkCode) throws BTSLBaseException {
        final String methodName = "loadMasterCategoryHierarchyList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_domainCode=" + p_domainCode + " p_networkCode=" + p_networkCode);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer(" SELECT CTR.from_category  parent_category_code, PCAT.category_name parent_category_name, ");
        strBuff.append(" CTR.to_category child_category_code,  CCAT.category_name child_category_name, ");
        strBuff.append(" PCAT.sequence_no from_sequence_no, CCAT.sequence_no to_sequence_no ");
        strBuff.append(" FROM chnl_transfer_rules CTR, categories PCAT, categories CCAT ");
        strBuff.append(" WHERE  CTR.type = ? AND CTR.parent_association_allowed = ? AND CTR.network_code = ? AND CTR.domain_code = ? ");
        strBuff.append(" AND PCAT.category_code = CTR.from_category AND CCAT.category_code = CTR.to_category ");
        strBuff.append(" ORDER BY PCAT.sequence_no ");
        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList categoryList = new ArrayList();
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            int i = 1;
            pstmtSelect.setString(i, PretupsI.TRANSFER_RULE_TYPE_CHANNEL);
            i++;
            pstmtSelect.setString(i, TypesI.YES);
            i++;
            pstmtSelect.setString(i, p_networkCode);
            i++;
            pstmtSelect.setString(i, p_domainCode);
            i++;
            rs = pstmtSelect.executeQuery();
            ChannelTransferRuleVO channelTransferRuleVO = null;
            while (rs.next()) {
                channelTransferRuleVO = new ChannelTransferRuleVO();
                channelTransferRuleVO.setFromCategory(SqlParameterEncoder.encodeParams(rs.getString("parent_category_code")));
                channelTransferRuleVO.setFromCategoryDes(SqlParameterEncoder.encodeParams(rs.getString("parent_category_name")));
                channelTransferRuleVO.setFromSeqNo(rs.getInt("from_sequence_no"));
                channelTransferRuleVO.setToCategory(SqlParameterEncoder.encodeParams(rs.getString("child_category_code")));
                channelTransferRuleVO.setToCategoryDes(SqlParameterEncoder.encodeParams(rs.getString("child_category_name")));
                categoryList.add(channelTransferRuleVO);
            }
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[loadMasterCategoryHierarchyList]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[loadMasterCategoryHierarchyList]", "",
                "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: categoryList size =" + categoryList.size());
            }
        }
        return categoryList;
    }

    /**
     * Method :loadCategoryList
     * This method load list of Categories on the basis of domain code
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_domainCode
     *            java.lang.String
     * @return HashMap
     * @throws BTSLBaseException
     * @author Ved Prakash
     */
    public HashMap loadCategoryList(Connection p_con, String p_domainCode, String p_userType, String p_categoryCode) throws BTSLBaseException {
        final String methodName = "loadCategoryList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_domainCode=" + p_domainCode + "p_userType=" + p_userType + "p_categoryCode=" + p_categoryCode);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer("SELECT C.sequence_no, C.category_code, C.category_name,C.sms_interface_allowed,C.web_interface_allowed,");
        strBuff.append(" C.services_allowed, C.user_id_prefix, C.low_bal_alert_allow, C.max_txn_msisdn FROM categories C WHERE C.domain_code=?");
        if (!BTSLUtil.isNullString(p_userType) && p_userType.equals(PretupsI.CHANNEL_USER_TYPE)) {
            strBuff.append(" AND C.SEQUENCE_NO >= ( SELECT sequence_no from categories where category_code= '" + p_categoryCode + "')");
        }
        strBuff.append(" ORDER BY sequence_no ");   
        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final HashMap categoryMap = new HashMap();
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_domainCode);
            rs = pstmtSelect.executeQuery();
            CategoryVO categoryVO = null;
            while (rs.next()) {
                categoryVO = new CategoryVO();
                categoryVO.setSequenceNumber(rs.getInt("sequence_no"));
                categoryVO.setCategoryCode(rs.getString("category_code"));
                categoryVO.setCategoryName(rs.getString("category_name"));
                categoryVO.setSmsInterfaceAllowed(rs.getString("sms_interface_allowed"));
                categoryVO.setWebInterfaceAllowed(rs.getString("web_interface_allowed"));
                categoryVO.setUserIdPrefix(rs.getString("user_id_prefix"));
                categoryVO.setServiceAllowed(rs.getString("services_allowed"));
                categoryVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));
                categoryVO.setMaxTxnMsisdn(rs.getString("max_txn_msisdn"));
                categoryVO.setMaxTxnMsisdnInt(rs.getInt("max_txn_msisdn"));
                categoryMap.put(categoryVO.getCategoryCode(), categoryVO);
            }
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[loadCategoryList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[loadCategoryList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: categoryList size =" + categoryMap.size());
            }
        }
        return categoryMap;
    }

    /**
     * Method :loadMasterCommProfileList
     * This method load list of Commission Profile on the basis of domain code .
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_domainCode
     *            java.lang.String
     * @param p_networkCode
     *            TODO
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     * @author Ved Prakash
     */
    public ArrayList loadMasterCommProfileList(Connection p_con, String p_domainCode, String p_networkCode, String p_categoryCode, String p_userType) throws BTSLBaseException {
        final String methodName = "loadMasterCommProfileList";
        if (log.isDebugEnabled()) {
            log.debug(methodName,
                "Entered: p_domainCode=" + p_domainCode + " p_networkCode=" + p_networkCode + "p_userType=" + p_userType + "p_categoryCode=" + p_categoryCode);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer(
            " SELECT CAT.category_code, CPS.comm_profile_set_id, CPS.comm_profile_set_name  FROM categories CAT,commission_profile_set CPS ");
        strBuff.append(" WHERE CAT.domain_code= ? AND CAT.category_code=CPS.category_code AND CAT.status='Y' AND CPS.status!='N' AND CPS.network_code=? ");
        // Changes made for Batch user initiate for channel user
        if (!BTSLUtil.isNullString(p_userType) && !p_userType.equals(PretupsI.OPERATOR_USER_TYPE)) {
            strBuff.append(" AND CAT.sequence_no>(select sequence_no from  categories where category_code=?)");
        }
        // End of Changes made for Batch user initiate for channel user
        strBuff.append(" ORDER BY CAT.sequence_no  ");
        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList commProfileList = new ArrayList();
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            int i = 1;
            pstmtSelect.setString(i, p_domainCode);
            i++;
            pstmtSelect.setString(i, p_networkCode);
            i++;
            // Changes made for Batch user initiate for channel user
            if (!BTSLUtil.isNullString(p_userType) && !p_userType.equals(PretupsI.OPERATOR_USER_TYPE)) {
                pstmtSelect.setString(i, p_categoryCode);
                i++;
            }
            // End of Changes made for Batch user initiate for channel user
            rs = pstmtSelect.executeQuery();
            CommissionProfileSetVO commissionProfileSetVO = null;
            while (rs.next()) {
                commissionProfileSetVO = new CommissionProfileSetVO();
                commissionProfileSetVO.setCategoryCode(SqlParameterEncoder.encodeParams(rs.getString("category_code")));
                commissionProfileSetVO.setCommProfileSetId(SqlParameterEncoder.encodeParams(rs.getString("comm_profile_set_id")));
                commissionProfileSetVO.setCommProfileSetName(SqlParameterEncoder.encodeParams(rs.getString("comm_profile_set_name")));
                commProfileList.add(commissionProfileSetVO);
            }
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[loadMasterCommProfileList]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[loadMasterCommProfileList]", "", "",
                "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: commProfileList size =" + commProfileList.size());
            }
        }
        return commProfileList;
    }

    /**
     * Method :addChannelUserList
     * This method check the data base validation of initiate channel user
     * and after validation insert into channel user related tables.
     * 
     * @param p_con
     *            Connection
     * @param p_userDetailList
     *            ArrayList
     * @param p_domainCode
     *            String
     * @param p_messages
     *            MessageResources
     * @param p_locale
     *            Locale
     * @param p_userVO
     *            TODO
     * @return ArrayList
     * @throws BTSLBaseException
     * @author Ved Prakash
     */
    public ArrayList addChannelUserList(Connection p_con, ArrayList p_userDetailList, String p_domainCode, MessageResources p_messages, Locale p_locale, UserVO p_userVO, String p_fileName,String batchID,boolean insertintoBatches,int total,boolean inbatch) throws BTSLBaseException {
        final String methodName = "addChannelUserList";
        if (log.isDebugEnabled()) {
            log.debug(
                methodName,
                "Entered: p_userDetailList.size()=" + p_userDetailList.size() + " p_domainCode=" + p_domainCode + " p_messages=" + p_messages + " p_locale=" + p_locale + " p_fileName: " + p_fileName);
        }
        Boolean isLoginPasswordAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LOGIN_PASSWORD_ALLOWED);
        Boolean isMNPAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MNP_ALLOWED);
        Boolean isUserVoucherTypeAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED);
        final ArrayList errorList = new ArrayList();
        double startTime = System.currentTimeMillis();
        ListValueVO errorVO = null;
        int commitCounter = 0;
        int index = 0;
        boolean flag = true;
        long phoneID = 0;
        final IDGeneratorDAO idGeneratorDAO = new IDGeneratorDAO();
        NetworkPrefixVO prefixVO = null;
        OperatorUtilI operatorUtil = null;
        // Parent details
       startTime=System.currentTimeMillis();
        final StringBuffer selectPtrUsrDetail = new StringBuffer(
            "SELECT U.user_id,U.parent_id,U.owner_id,U.category_code, C.sequence_no, C.domain_code, C.grph_domain_type,UG.grph_domain_code ,U.login_id, UP.Msisdn ");
        selectPtrUsrDetail.append(" FROM users U, user_phones UP,categories C,user_geographies UG ");
        selectPtrUsrDetail.append(" WHERE U.login_id=? AND UP.msisdn=? AND UP.primary_number='Y' AND U.status NOT IN('N','C') AND UG.user_id=U.user_id ");
        selectPtrUsrDetail.append(" AND U.user_id = UP.user_id AND U.category_code = C.category_code ");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "selectPtrUsrDetail Query =" + selectPtrUsrDetail);
        }
        PreparedStatement pstmtSelectParentUserDetails = null;
        ResultSet rsParentUserDetails = null;

        /**
         * StringBuffer selectPtrUsrDetailL = new StringBuffer(
         * "SELECT U.user_id,U.parent_id,U.owner_id,U.category_code, C.sequence_no, C.domain_code, C.grph_domain_type,UG.grph_domain_code,U.login_id, UP.Msisdn "
         * );
         * selectPtrUsrDetailL.append(
         * " FROM users U, categories C,user_geographies UG , user_phones UP ");
         * selectPtrUsrDetailL.append(
         * " WHERE U.login_id=? AND U.status NOT IN('N','C') AND UG.user_id=U.user_id "
         * );
         * selectPtrUsrDetailL.append(
         * " AND U.category_code = C.category_code AND U.user_id=UP.user_id");
         * if (log.isDebugEnabled()) log.debug("addChannelUserList",
         * "selectPtrUsrDetailL Query ="+selectPtrUsrDetailL);
         * PreparedStatement pstmtSelectParentUserDetailsL = null;
         **/

        final StringBuffer selectPtrUsrDetailP = new StringBuffer(
            "SELECT U.user_id,U.parent_id,U.owner_id,U.category_code, C.sequence_no, C.domain_code, C.grph_domain_type,UG.grph_domain_code,U.login_id , UP.Msisdn");
        selectPtrUsrDetailP.append(" FROM users U, user_phones UP,categories C,user_geographies UG ");
        selectPtrUsrDetailP.append(" WHERE UP.msisdn=? AND UP.primary_number='Y' AND U.status NOT IN('N','C') AND UG.user_id=U.user_id ");
        selectPtrUsrDetailP.append(" AND U.user_id = UP.user_id AND U.category_code = C.category_code ");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "selectPtrUsrDetailP Query =" + selectPtrUsrDetailP);
        }
        PreparedStatement pstmtSelectParentUserDetailsP = null;

        
        PreparedStatement pstmtChildGeographyAllowed = null;
        ResultSet rsChildGeographyAllowed = null;

        // Assign phone validation & mobile number validation unique
        final StringBuffer selectUserPhones = new StringBuffer("SELECT 1 from user_phones UP,users U ");
        selectUserPhones.append(" WHERE UP.msisdn = ? AND UP.user_id = U.user_id  AND U.status <> 'N' AND U.status <> 'C'");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "selectUserPhones Query =" + selectUserPhones);
        }
        PreparedStatement pstmtSelectUserPhones = null;
        ResultSet rsUserPhones = null;

        // Unique check for login id
        final StringBuffer selectLoginID = new StringBuffer("SELECT 1 FROM users U WHERE U.login_id=? ");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "selectLoginID Query =" + selectLoginID);
        }
        PreparedStatement pstmtSelectLoginID = null;
        ResultSet rsSelectLoginID = null;

        // Check for parent hierarchy added by shashank
        final StringBuffer selectParentHierarchy = new StringBuffer("select (1) from CHNL_TRANSFER_RULES ctr, users u ");
        selectParentHierarchy.append(" where u.MSISDN=? and u.CATEGORY_CODE=ctr.FROM_CATEGORY ");
        selectParentHierarchy.append(" and ctr.TO_CATEGORY=? and ctr.PARENT_ASSOCIATION_ALLOWED=? ");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "selectParentHierarchy Query =" + selectParentHierarchy);
        }
        PreparedStatement pstmtSelectParentHierarchy = null;
        ResultSet rsSelectParentHierarchy = null;

        // Geographical domain check for category entered in xls file
        final StringBuffer selectGeography = new StringBuffer("SELECT 1 FROM categories CAT,");
        selectGeography.append("geographical_domain_types GDT, geographical_domains GD ");
        selectGeography.append(" WHERE CAT.domain_code=? AND CAT.grph_domain_type=GDT.grph_domain_type ");
        selectGeography.append(" AND GD.grph_domain_type= GDT.grph_domain_type AND GD.grph_domain_code=? ");
        selectGeography.append(" AND CAT.category_code=?");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "selectGeography Query =" + selectGeography);
        }
        PreparedStatement pstmtselectGeography = null;
        ResultSet rsselectGeography = null;

        // batches insert
        final StringBuffer insertIntoBatches = new StringBuffer(
            "INSERT INTO batches (batch_id, batch_type, batch_size, batch_name, network_code, status, created_by, created_on, modified_by, modified_on,file_name) ");
        insertIntoBatches.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?)");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "insertIntoBatches Query =" + insertIntoBatches);
        }
        PreparedStatement pstmtInsertIntoBatches = null;

        // update batches table
        final StringBuffer updateIntoBatches = new StringBuffer("UPDATE batches SET batch_size=?, status=? WHERE batch_id=? ");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "updateIntoBatches Query =" + updateIntoBatches);
        }
        PreparedStatement pstmtupdateIntoBatches = null;

        final StringBuffer insertInToUser = new StringBuffer("INSERT INTO users (user_id,user_name,network_code, ");
        insertInToUser.append("login_id,password,category_code,parent_id,");
        insertInToUser.append("owner_id,employee_code,status,email,contact_no,msisdn,user_type,");
        insertInToUser.append("created_by,created_on,modified_by,modified_on,address1, ");
        insertInToUser.append("address2,city,state,country,ssn,user_name_prefix, ");
        insertInToUser.append("external_code,short_name,user_code,appointment_date,previous_status,batch_id, creation_type,pswd_reset,contact_person,company,fax,firstname,lastname ,rsaflag,authentication_allowed");// fname,lname,company,fax
        insertInToUser.append(",longitude,latitude,document_type,document_no,payment_type)");
        insertInToUser.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "insertInToUser Query =" + insertInToUser);
        }
        PreparedStatement pstmtInsertInToUser = null;

        final StringBuffer insertInToChnlUser = new StringBuffer("INSERT INTO channel_users (user_id,user_grade,");
        insertInToChnlUser.append("contact_person,transfer_profile_id, comm_profile_set_id,");
        insertInToChnlUser.append("outlet_code,suboutlet_code, ");
        insertInToChnlUser.append("user_profile_id, mcommerce_service_allow, low_bal_alert_allow, mpay_profile_id , trf_rule_type)");
        insertInToChnlUser.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "insertInToChnlUser Query =" + insertInToChnlUser);
        }
        PreparedStatement pstmtInsertInToChnlUser = null;

        final StringBuffer insertInToUserPhones = new StringBuffer("INSERT INTO user_phones (user_phones_id,msisdn,user_id,");
        insertInToUserPhones.append("primary_number,sms_pin,pin_required,phone_profile,phone_language,country,");
        insertInToUserPhones.append("created_by,created_on,modified_by,modified_on,prefix_id,invalid_pin_count,pin_modified_on,pin_reset) VALUES ");
        insertInToUserPhones.append("(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "insertInToUserPhones Query =" + insertInToUserPhones);
        }
        PreparedStatement pstmtInsertInToUserPhones = null;

        final StringBuffer insertInToUserService = new StringBuffer("INSERT INTO user_services (user_id,service_type) values (?,?)");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "insertInToUserService Query =" + insertInToUserService);
        }
        PreparedStatement pstmtInsertInToUserService = null;
        
        //Voucher Type
        final StringBuffer insertInToVoucherTypes = new StringBuffer("INSERT INTO user_vouchertypes (user_id,voucher_type) values (?,?)");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "insertInToVoucherTypes Query =" + insertInToVoucherTypes);
        }
        PreparedStatement pstmtInsertInToVoucherTypes = null;
        
        final StringBuffer insertInToVoucherSegments = new StringBuffer("INSERT INTO USER_VOUCHER_SEGMENTS (user_id,voucher_segment) values (?,?)");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "insertInToVoucherSegments Query =" + insertInToVoucherSegments);
        }
        PreparedStatement pstmtInsertInToVoucherSegments = null;

        final StringBuffer insertInToUserGeographics = new StringBuffer("INSERT INTO user_geographies (user_id,");
        insertInToUserGeographics.append("grph_domain_code)");
        insertInToUserGeographics.append(" values (?,?)");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "insertInToUserGeographics Query =" + insertInToUserGeographics);
        }
        PreparedStatement pstmtInsertInToUserGeographics = null;

        final StringBuffer insertInToUserRole = new StringBuffer("INSERT INTO user_roles (user_id,");
        insertInToUserRole.append("role_code) values (?,?)");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "insertInToUserRole Query =" + insertInToUserRole);
        }
        PreparedStatement pstmtInsertInToUserRole = null;

        // Batch geographies insert
        final StringBuffer insertInToBatchGeography = new StringBuffer("INSERT INTO batch_geographies (batch_id, geography_code) ");
        insertInToBatchGeography.append(" VALUES(?,?)");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "insertInToBatchGeography Query =" + insertInToBatchGeography);
        }
        
        
        final StringBuffer insertChannelUserLoanInfo = new StringBuffer("insert into channel_user_loan_info (");
        insertChannelUserLoanInfo.append("user_id,profile_id, product_code,loan_threhold,loan_amount,created_on , ");
        insertChannelUserLoanInfo.append(" created_by,modified_on ,modified_by) values ");
        insertChannelUserLoanInfo.append(" (?,?,?,?,?,?,?,?,?)");
        
        if (log.isDebugEnabled()) {
            log.debug(methodName, "insertChannelUserLoanInfo Query =" + insertChannelUserLoanInfo);
        }
        
        PreparedStatement pstmtInsertInChannelUserLoanInfo = null;
        
        
        PreparedStatement pstmtInsertInToBatchGeography = null;
        PreparedStatement pstmtselectParentGeography = null;
        ResultSet rsselectParentGeography = null;
        String parentGeography = null;
        // added by shashank
        String parentLoginId = null;
        String parentMsisdn = null;

        final StringBuffer selectExternalCode = new StringBuffer("SELECT 1 FROM users WHERE external_code= ? AND status <> 'N' AND status <> 'C' ");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "selectExternalCode Query =" + selectExternalCode);
        }
        PreparedStatement pstmtSelectExternalCode = null;
        ResultSet rsExternalCode = null;
        final StringBuffer selectPortedMSISDN = new StringBuffer("SELECT 1 FROM ported_msisdn WHERE msisdn=? AND port_type=? ");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "selectPortedMSISDN Query =" + selectPortedMSISDN);
        }
        PreparedStatement pstmtPortedMSISDN = null;
        ResultSet rsPortedMSISDN = null;

        final StringBuffer selectParentDetails = new StringBuffer("SELECT C.category_code, C.web_interface_allowed, C.sms_interface_allowed ");
        selectParentDetails.append(" FROM users U, categories C WHERE U.category_code=C.category_code AND U.user_id=?");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "selectParentDetails Query =" + selectParentDetails);
        }
        PreparedStatement pstmtParentDetails = null;
        ResultSet rsParentDetails = null;

        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[addChannelUserList]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }

        try {
            pstmtSelectParentUserDetails = p_con.prepareStatement(selectPtrUsrDetail.toString());
            // pstmtSelectParentUserDetailsL=p_con.prepareStatement(selectPtrUsrDetailL.toString());
            pstmtSelectParentUserDetailsP = p_con.prepareStatement(selectPtrUsrDetailP.toString());
          //  pstmtChildGeographyAllowed = p_con.prepareStatement(childGeographyAllowed.toString());
            pstmtSelectUserPhones = p_con.prepareStatement(selectUserPhones.toString());
            pstmtSelectLoginID = p_con.prepareStatement(selectLoginID.toString());
            // added by shashank for batch user creation
            pstmtSelectParentHierarchy = p_con.prepareStatement(selectParentHierarchy.toString());
            // end
            pstmtselectGeography = p_con.prepareStatement(selectGeography.toString());
            pstmtInsertIntoBatches = p_con.prepareStatement(insertIntoBatches.toString());
            pstmtInsertInToUser = p_con.prepareStatement(insertInToUser.toString());
            pstmtInsertInToChnlUser = p_con.prepareStatement(insertInToChnlUser.toString());
            pstmtInsertInToUserPhones = p_con.prepareStatement(insertInToUserPhones.toString());
            pstmtInsertInToUserService = p_con.prepareStatement(insertInToUserService.toString());
            pstmtInsertInToVoucherTypes = p_con.prepareStatement(insertInToVoucherTypes.toString());
            pstmtInsertInToVoucherSegments = p_con.prepareStatement(insertInToVoucherSegments.toString());
           
            pstmtInsertInToUserGeographics = p_con.prepareStatement(insertInToUserGeographics.toString());
            pstmtInsertInToUserRole = p_con.prepareStatement(insertInToUserRole.toString());
            pstmtInsertInToBatchGeography = p_con.prepareStatement(insertInToBatchGeography.toString());
            pstmtupdateIntoBatches = p_con.prepareStatement(updateIntoBatches.toString());
            pstmtSelectExternalCode = p_con.prepareStatement(selectExternalCode.toString());
            pstmtPortedMSISDN = p_con.prepareStatement(selectPortedMSISDN.toString());
            pstmtParentDetails = p_con.prepareStatement(selectParentDetails.toString());
            pstmtInsertInChannelUserLoanInfo = p_con.prepareStatement(insertChannelUserLoanInfo.toString());
            ChannelUserVO channelUserVO = null;
            int seqNo = 0;
            int updateCount = 0;
            boolean noupdateofphoneanduserid=false;
            final ArrayList geographyDomainCodeList = new ArrayList();
            log.debug("batchuserWebDaoafter preparedStatement"," time taken = "+(System.currentTimeMillis()-startTime)+" ms");
            startTime=System.currentTimeMillis();
            Collections.sort(p_userDetailList);

            long idCounter = 0;
            int commitNumber = 0;
            int userPaddingLength = 0;
            try {
                commitNumber = Integer.parseInt(Constants.getProperty("BATCH_USER_COMMIT_NUMBER"));
                userPaddingLength = Integer.parseInt(Constants.getProperty("USER_PADDING_LENGTH"));
            } catch (Exception e) {
                commitNumber = 100;
                userPaddingLength = 10;
                log.error(methodName, "Exception:e=" + e);
                log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[addChannelUserList]", "", "", "",
                    "Exception:" + e.getMessage());
            }
            CategoryVO categoryVO = null;
            NetworkPrefixVO networkPrefixVO = null;
            String filterMsisdn = null;
            String webAccAllowed = null;
            String smsAccAllowed = null;
            ArrayList msisdnList = null;
            UserPhoneVO userPhoneVO = null;
            if(!inbatch){
            for (int i = 0, length = p_userDetailList.size(); i < length; i++) {
                seqNo = 0;
                parentGeography = null;
                channelUserVO = (ChannelUserVO) p_userDetailList.get(i);
                categoryVO = channelUserVO.getCategoryVO();
                /*
                 * Validation 1: Pick category code & match its sequence number
                 * with the sequence number
                 * of the parent[ based on parent login id & msisdn supplied in
                 * XLS file ]. If the seq
                 * number of the category=1 then there is no need to supply
                 * parent login id and parent
                 * msisdn.If the parent info is given then error: No parent info
                 * req for top level category.
                 * Also validation is done if the parent corresponding to the
                 * parent's login ID cannot have
                 * the new user as his child based on the category (Added by
                 * Ankur June 2011)
                 */
                
                rsParentUserDetails = null;
                if (PretupsI.YES.equals(categoryVO.getWebInterfaceAllowed()) && PretupsI.YES.equals(categoryVO.getSmsInterfaceAllowed())) {
                    if (!BTSLUtil.isNullString(channelUserVO.getParentLoginID())) {
                        pstmtSelectParentUserDetails.setString(1, channelUserVO.getParentLoginID());
                        pstmtSelectParentUserDetails.setString(2, channelUserVO.getParentMsisdn());
                        rsParentUserDetails = pstmtSelectParentUserDetails.executeQuery();
                    } else {
                        pstmtSelectParentUserDetailsP.setString(1, channelUserVO.getParentMsisdn());
                        rsParentUserDetails = pstmtSelectParentUserDetailsP.executeQuery();
                    }
                }
                /**
                 * else
                 * if(PretupsI.YES.equals(categoryVO.getWebInterfaceAllowed()))
                 * {
                 * if(!BTSLUtil.isNullString(channelUserVO.getParentLoginID()) )
                 * {
                 * pstmtSelectParentUserDetailsL.setString(1,channelUserVO.
                 * getParentLoginID());
                 * rsParentUserDetails=pstmtSelectParentUserDetailsL.
                 * executeQuery();
                 * }
                 * else
                 * {
                 * pstmtSelectParentUserDetailsP.setString(1,channelUserVO.
                 * getParentMsisdn());
                 * rsParentUserDetails=pstmtSelectParentUserDetailsP.
                 * executeQuery();
                 * }
                 * }
                 **/
                else if (PretupsI.YES.equals(categoryVO.getSmsInterfaceAllowed())) {
                    pstmtSelectParentUserDetailsP.setString(1, channelUserVO.getParentMsisdn());

                    rsParentUserDetails = pstmtSelectParentUserDetailsP.executeQuery();
                }
                seqNo = 0;
                if (rsParentUserDetails.next()) {
                    seqNo = rsParentUserDetails.getInt("sequence_no");
                    channelUserVO.setOwnerID(rsParentUserDetails.getString("owner_id"));
                    channelUserVO.setParentID(rsParentUserDetails.getString("user_id"));
                    channelUserVO.setParentLoginID(rsParentUserDetails.getString("login_id"));
                    parentGeography = rsParentUserDetails.getString("grph_domain_code");
                    // added by shashank
                    parentLoginId = rsParentUserDetails.getString("login_id");
                    parentMsisdn = rsParentUserDetails.getString("msisdn");
                    // end
                    
                    pstmtChildGeographyAllowed = batchUserWebQry.addChannelUserListChildGeographyAllowedQry(p_con, channelUserVO.getGeographicalCode(), parentGeography);
                    rsChildGeographyAllowed = pstmtChildGeographyAllowed.executeQuery();
                    if (!rsChildGeographyAllowed.next()) {
                        errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale,
                            "bulkuser.initiatebulkuser.msg.error.parentnotingeography"));
                        errorList.add(errorVO);
                        BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + p_messages.getMessage("bulkuser.initiatebulkuser.msg.error.parentnotingeography"));
                        continue;
                    }
                    if (!BTSLUtil.isNullString(channelUserVO.getParentLoginID())) {
                        if (!parentLoginId.equals(channelUserVO.getParentLoginID())) {
                            errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale,
                                "bulkuser.initiatebulkuser.msg.error.parentnotexist"));
                            errorList.add(errorVO);
                            BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + p_messages.getMessage("bulkuser.initiatebulkuser.msg.error.parentnotexist"));
                            continue;
                        }
                    }
                    if (!BTSLUtil.isNullString(channelUserVO.getParentMsisdn())) {
                        if (!parentMsisdn.equals(channelUserVO.getParentMsisdn())) {
                            errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale,
                                "bulkuser.initiatebulkuser.msg.error.parentnotexist"));
                            errorList.add(errorVO);
                            BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + p_messages.getMessage("bulkuser.initiatebulkuser.msg.error.parentnotexist"));
                            continue;
                        }
                    }

                } else if (channelUserVO.getCategoryVO().getSequenceNumber() > 1) {
                    errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale, "bulkuser.initiatebulkuser.msg.error.parentnotexist"));
                    errorList.add(errorVO);
                    BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + p_messages.getMessage("bulkuser.initiatebulkuser.msg.error.parentnotexist"));
                    continue;
                }
                pstmtSelectParentUserDetails.clearParameters();

                // Parent Seq num=seqNo
                if (channelUserVO.getCategoryVO().getSequenceNumber() == 1 && !BTSLUtil.isNullString(channelUserVO.getParentMsisdn()) && !BTSLUtil.isNullString(channelUserVO
                    .getParentLoginID())) {
                    // errorVO=new
                    // ListValueVO("WARNING",channelUserVO.getRecordNumber(),p_messages.getMessage(p_locale,"bulkuser.initiatebulkuser.msg.error.hierarchysamewarn"));
                    // errorList.add(errorVO);
                    BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Success :=" + p_messages.getMessage("bulkuser.initiatebulkuser.msg.error.hierarchysamewarn"));
                } else if (rsParentUserDetails != null && seqNo > channelUserVO.getCategoryVO().getSequenceNumber()) {
                    errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale, "bulkuser.initiatebulkuser.msg.error.hierarchyerror"));
                    errorList.add(errorVO);
                    BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + p_messages.getMessage("bulkuser.initiatebulkuser.msg.error.hierarchyerror"));
                    continue;
                }

                else if (channelUserVO.getCategoryVO().getSequenceNumber() > 1) {
                    pstmtParentDetails.setString(1, channelUserVO.getParentID());
                    rsParentDetails = pstmtParentDetails.executeQuery();
                    if (rsParentDetails.next()) {
                        webAccAllowed = rsParentDetails.getString("web_interface_allowed");
                        smsAccAllowed = rsParentDetails.getString("sms_interface_allowed");
                    }
                    if (isLoginPasswordAllowed && PretupsI.YES.equals(webAccAllowed) && BTSLUtil.isNullString(channelUserVO.getParentLoginID())) {
                        errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale, "bulkuser.initiatebulkuser.msg.error.parentidmissing"));
                        errorList.add(errorVO);
                        BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + p_messages.getMessage("bulkuser.initiatebulkuser.msg.error.parentidmissing"));
                        continue;
                    }
                    if (PretupsI.YES.equals(smsAccAllowed) && BTSLUtil.isNullString(channelUserVO.getParentMsisdn())) {
                        errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale,
                            "bulkuser.initiatebulkuser.msg.error.parentmsisdnmissing"));
                        errorList.add(errorVO);
                        BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + p_messages.getMessage("bulkuser.initiatebulkuser.msg.error.parentmsisdnmissing"));
                        continue;
                    }
                    pstmtParentDetails.clearParameters();
                    webAccAllowed = "N";
                    smsAccAllowed = "N";

                    // Check for parent hierarchy added by shashank
                    pstmtSelectParentHierarchy.setString(1, channelUserVO.getParentMsisdn());
                    pstmtSelectParentHierarchy.setString(2, channelUserVO.getCategoryCode());
                    pstmtSelectParentHierarchy.setString(3, PretupsI.YES);
                    rsSelectParentHierarchy = pstmtSelectParentHierarchy.executeQuery();
                    if (!rsSelectParentHierarchy.next()) {
                        errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale, "bulkuser.initiatebulkuser.msg.error.hierarchyerror"));
                        errorList.add(errorVO);
                        BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + p_messages.getMessage("bulkuser.initiatebulkuser.msg.error.hierarchyerror"));
                        continue;
                    }
                    // end
                }
               
                // ************Validation 2: Check for login_id uniqueness, if
                // login id already exists mark error.
                pstmtSelectLoginID.setString(1, channelUserVO.getLoginID());
                rsSelectLoginID = pstmtSelectLoginID.executeQuery();
                if (rsSelectLoginID.next()) {
                    errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale, "bulkuser.initiatebulkuser.msg.error.loginiduniqueerr",
                        new String[] { channelUserVO.getLoginID() }));
                    errorList.add(errorVO);
                    BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + p_messages.getMessage("bulkuser.initiatebulkuser.msg.error.loginiduniqueerr",
                        channelUserVO.getLoginID()));
                    continue;
                }
                
                // *************Validation 3: Check for Geography
                // **************************************
                pstmtselectGeography.setString(1, channelUserVO.getDomainID());
                pstmtselectGeography.setString(2, channelUserVO.getGeographicalCode());
                pstmtselectGeography.setString(3, channelUserVO.getCategoryCode());
                rsselectGeography = pstmtselectGeography.executeQuery();
                if (!rsselectGeography.next()) {
                    errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale, "bulkuser.initiatebulkuser.msg.error.geographyerr",
                        new String[] { channelUserVO.getGeographicalCode(), channelUserVO.getCategoryCode() }));
                    errorList.add(errorVO);
                    BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + p_messages.getMessage("bulkuser.initiatebulkuser.msg.error.geographyerr",
                        new String[] { channelUserVO.getGeographicalCode(), channelUserVO.getCategoryCode() }));
                    continue;
                }
                /*
                 * 26/04/07 Code Added for MNP
                 * Preference to check whether MNP is allowed in system or not.
                 * If yes then check whether Number has not been ported out, If
                 * yes then throw error, else continue
                 */
                
                final String[] msisdnInput = channelUserVO.getMultipleMsisdnlist().split(",");
                boolean checkMsisdn = false;
                boolean checkMnpAllowed = false;
                final StringBuffer errorMsisdn = new StringBuffer();
                final StringBuffer errorMnpMsisdn = new StringBuffer();
                for (int k = 0, j = msisdnInput.length; k < j; k++) {
                    if (isMNPAllowed) {
                        // filterMsisdn=channelUserVO.getMsisdn();
                        filterMsisdn = msisdnInput[k].trim();
                        networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(filterMsisdn));
                        if (networkPrefixVO == null) {
                            errorMnpMsisdn.append(msisdnInput[k]);
                            errorMnpMsisdn.append(",");
                            checkMnpAllowed = true;
                        } else if (networkPrefixVO.getOperator().equals(PretupsI.OPERATOR_TYPE_PORT)) {
                            pstmtPortedMSISDN.setString(1, filterMsisdn);
                            pstmtPortedMSISDN.setString(2, PretupsI.PORTED_IN);
                            rsPortedMSISDN = pstmtPortedMSISDN.executeQuery();
                            if (!rsPortedMSISDN.next()) {
                                // errorVO=new
                                // ListValueVO("",channelUserVO.getRecordNumber(),p_messages.getMessage(p_locale,"bulkuser.processuploadedfile.error.nonetworkprefixfound",new
                                // String[]{msisdnInput[k]}));
                                // errorList.add(errorVO);
                                // BatchesLog.log("BULKUSRINITIATE",channelUserVO,null,"Fail :="+p_messages.getMessage("bulkuser.processuploadedfile.error.nonetworkprefixfound",new
                                // String[]{msisdnInput[k]}));
                                // continue;
                                errorMnpMsisdn.append(msisdnInput[k]);
                                errorMnpMsisdn.append(",");
                                checkMnpAllowed = true;
                            }
                        } else {
                            pstmtPortedMSISDN.setString(1, filterMsisdn);
                            pstmtPortedMSISDN.setString(2, PretupsI.PORTED_OUT);
                            rsPortedMSISDN = pstmtPortedMSISDN.executeQuery();
                            if (rsPortedMSISDN.next()) {
                                // errorVO=new
                                // ListValueVO("",channelUserVO.getRecordNumber(),p_messages.getMessage(p_locale,"bulkuser.processuploadedfile.error.nonetworkprefixfound",new
                                // String[]{msisdnInput[k]}));
                                // errorList.add(errorVO);
                                // BatchesLog.log("BULKUSRINITIATE",channelUserVO,null,"Fail :="+p_messages.getMessage("bulkuser.processuploadedfile.error.nonetworkprefixfound",new
                                // String[]{msisdnInput[k]}));
                                // continue;
                                errorMnpMsisdn.append(msisdnInput[k]);
                                errorMnpMsisdn.append(",");
                                checkMnpAllowed = true;
                            }
                        }
                        pstmtPortedMSISDN.clearParameters();
                    }
                    // 26/04/07: MNP Code End
                    // **********Validation 4: Check for the uniqueness of the
                    // msisdn
                    // pstmtSelectUserPhones.setString(1,channelUserVO.getMsisdn());
                    pstmtSelectUserPhones.setString(1, msisdnInput[k].trim());
                    rsUserPhones = pstmtSelectUserPhones.executeQuery();
                    if (rsUserPhones.next()) {
                        // errorVO=new
                        // ListValueVO("",channelUserVO.getRecordNumber(),p_messages.getMessage(p_locale,"bulkuser.initiatebulkuser.msg.error.msisdnalreadyassigned",new
                        // String[]{channelUserVO.getMsisdn()}));
                        // errorList.add(errorVO);
                        // BatchesLog.log("BULKUSRINITIATE",channelUserVO,null,"Fail :="+p_messages.getMessage("bulkuser.initiatebulkuser.msg.error.msisdnalreadyassigned",new
                        // String[]{channelUserVO.getMsisdn()}));
                        // BatchesLog.log("BULKUSRINITIATE",channelUserVO,null,"Fail :="+p_messages.getMessage("bulkuser.initiatebulkuser.msg.error.msisdnalreadyassigned",new
                        // String[]{msisdnInput[k]}));
                        // continue;
                        errorMsisdn.append(msisdnInput[k]);
                        errorMsisdn.append(",");
                        checkMsisdn = true;
                    }
                } // end of for loop
                  // checking status
                if (checkMnpAllowed) {
                    errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale, "bulkuser.processuploadedfile.error.nonetworkprefixfound",
                        new String[] { errorMnpMsisdn.toString().substring(0, errorMnpMsisdn.toString().length() - 1) }));
                    errorList.add(errorVO);
                    BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + p_messages.getMessage("bulkuser.processuploadedfile.error.nonetworkprefixfound",
                        new String[] { errorMnpMsisdn.toString() }));
                    continue;
                } else if (checkMsisdn) {
                    errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), p_messages
                        .getMessage(p_locale, "bulkuser.initiatebulkuser.msg.error.msisdnalreadyassigned", new String[] { errorMsisdn.toString().substring(0,
                            errorMsisdn.toString().length() - 1) }));
                    errorList.add(errorVO);
                    // BatchesLog.log("BULKUSRINITIATE",channelUserVO,null,"Fail :="+p_messages.getMessage("bulkuser.initiatebulkuser.msg.error.msisdnalreadyassigned",new
                    // String[]{channelUserVO.getMsisdn()}));
                    BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + p_messages.getMessage("bulkuser.initiatebulkuser.msg.error.msisdnalreadyassigned",
                        new String[] { errorMsisdn.toString() }));
                    continue;
                }
                
                // **********Validation 5: Check for the uniqueness of the
                // External code (if given)
                if (!BTSLUtil.isNullString(channelUserVO.getExternalCode())) {
                    pstmtSelectExternalCode.setString(1, channelUserVO.getExternalCode().trim());
                    rsExternalCode = pstmtSelectExternalCode.executeQuery();
                    if (rsExternalCode.next()) {
                        errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale,
                            "bulkuser.initiatebulkuser.msg.error.externalcodeuniqueerr", new String[] { channelUserVO.getExternalCode() }));
                        errorList.add(errorVO);
                        BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + p_messages.getMessage("bulkuser.initiatebulkuser.msg.error.externalcodeuniqueerr",
                            channelUserVO.getExternalCode()));
                        continue;
                    }
                }
               
                // ***After all validation insertion starts
                // here********************************************
                // Insert into batches
                if(flag){
                	 // First time make the user_id
                    idCounter = idGeneratorDAO.getNextBatchID(p_con, PretupsI.USERID, PretupsI.ALL, channelUserVO.getNetworkID(), channelUserVO.getCreatedOn());
                    // First time make the phone_id
                    phoneID = idGeneratorDAO.getNextBatchID(p_con, PretupsI.USR_PHONE_ID, PretupsI.ALL, PretupsI.ALL, channelUserVO.getCreatedOn());
                flag = false;
                }
                
                
                if (commitCounter >= commitNumber)// After 100 record commit the
                // records
                {
                    // after 100 records update the last_no of the ids table for
                    // user_id
                    idGeneratorDAO.updateNextBatchID(p_con, PretupsI.USERID, PretupsI.ALL, channelUserVO.getNetworkID(), channelUserVO.getCreatedOn(), idCounter - 1);
                    // after 100 records update the last_no of the ids table for
                    // phone_id
                    idGeneratorDAO.updateNextBatchID(p_con, PretupsI.USR_PHONE_ID, PretupsI.ALL, PretupsI.ALL, channelUserVO.getCreatedOn(), phoneID - 1);
                    p_con.commit();
                    // after 100 records pick the last_no from the ids table for
                    // user_id
                    idCounter = idGeneratorDAO.getNextBatchID(p_con, PretupsI.USERID, PretupsI.ALL, channelUserVO.getNetworkID(), channelUserVO.getCreatedOn());
                    // after 100 records pick the last_no from the ids table for
                    // phone_id
                    phoneID = idGeneratorDAO.getNextBatchID(p_con, PretupsI.USR_PHONE_ID, PretupsI.ALL, PretupsI.ALL, channelUserVO.getCreatedOn());
                    commitCounter = 0;// reset commit counter
                }
                channelUserVO.setUserID(this.generateUserId(channelUserVO.getNetworkID(), channelUserVO.getCategoryVO().getUserIdPrefix(), idCounter, userPaddingLength));

                // Insert into users table.

                pstmtInsertInToUser.clearParameters();
                index = 0;
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getUserID());
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getUserName());
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getNetworkID());
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getLoginID());
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getPassword());
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getCategoryCode());
                if (channelUserVO.getCategoryVO().getSequenceNumber() == 1) {
                    ++index;
                    pstmtInsertInToUser.setString(index, PretupsI.ROOT_PARENT_ID);
                } else {
                    ++index;
                    pstmtInsertInToUser.setString(index, channelUserVO.getParentID());
                }
                if (channelUserVO.getCategoryVO().getSequenceNumber() == 1) {
                    ++index;
                    pstmtInsertInToUser.setString(index, channelUserVO.getUserID());
                } else {
                    ++index;
                    pstmtInsertInToUser.setString(index, channelUserVO.getOwnerID());
                }
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getEmpCode());
                ++index;
                if(((Integer)PreferenceCache.getControlPreference(PreferenceI.USER_APPROVAL_LEVEL,channelUserVO.getNetworkID(),channelUserVO.getCategoryCode())).intValue() >0  ){
    	        	pstmtInsertInToUser.setString(index,PretupsI.USER_STATUS_NEW);//N New
                }
                else
                {
                	if (LookupsCache.getLookupCodeList(PretupsI.ALLOWED_USER_STATUS).contains(PretupsI.USER_STATUS_PREACTIVE)) {
                		pstmtInsertInToUser.setString(index,PretupsI.USER_STATUS_PREACTIVE);// PA
                		// Active
                	} else {
                		pstmtInsertInToUser.setString(index,PretupsI.USER_STATUS_ACTIVE);// A
                		// Active
                	}
                }
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getEmail());
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getContactNo());
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getMsisdn());
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getUserType());
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getCreatedBy());
                ++index;
                pstmtInsertInToUser.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getCreatedOn()));
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getModifiedBy());
                ++index;
                pstmtInsertInToUser.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getModifiedOn()));
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getAddress1());
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getAddress2());
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getCity());
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getState());
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getCountry());
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getSsn());
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getUserNamePrefix());
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getExternalCode());
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getShortName());
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getUserCode());
                if (channelUserVO.getAppointmentDate() != null) {
                    ++index;
                    pstmtInsertInToUser.setDate(index, BTSLUtil.getSQLDateFromUtilDate(channelUserVO.getAppointmentDate()));
                } else {
                    ++index;
                    pstmtInsertInToUser.setDate(index, null);
                }
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getPreviousStatus());
                ++index;
                pstmtInsertInToUser.setString(index, batchID);
                ++index;
                pstmtInsertInToUser.setString(index, PretupsI.BATCH_USR_CREATION_TYPE);
                ++index;
                pstmtInsertInToUser.setString(index, PretupsI.YES);
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getContactPerson());
                // Added by deepika aggarwal
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getCompany());
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getFax());
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getFirstName());
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getLastName());
                // end
                // added by shashank for rsa authentication
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getRsaFlag());
                // added for OTP
                if (BTSLUtil.isNullString(p_userVO.getAuthTypeAllowed())) {
                    ++index;
                    pstmtInsertInToUser.setString(index, PretupsI.NO);
                } else {
                    ++index;
                    pstmtInsertInToUser.setString(index, p_userVO.getAuthTypeAllowed());
                }
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getLongitude());
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getLatitude());
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getDocumentType());
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getDocumentNo());
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getPaymentType());
               
                if (pstmtInsertInToUser.executeUpdate() <= 0) {
                    p_con.rollback();
                    BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=When inserting users table");
                    continue;
                }

                // insert into channel user
                pstmtInsertInToChnlUser.clearParameters();
                index = 0;
                ++index;
                pstmtInsertInToChnlUser.setString(index, channelUserVO.getUserID());
                ++index;
                pstmtInsertInToChnlUser.setString(index, channelUserVO.getUserGrade());
                ++index;
                pstmtInsertInToChnlUser.setString(index, channelUserVO.getContactPerson());
                ++index;
                pstmtInsertInToChnlUser.setString(index, channelUserVO.getTransferProfileID());
                ++index;
                pstmtInsertInToChnlUser.setString(index, channelUserVO.getCommissionProfileSetID());
                ++index;
                pstmtInsertInToChnlUser.setString(index, channelUserVO.getOutletCode());
                ++index;
                pstmtInsertInToChnlUser.setString(index, channelUserVO.getSubOutletCode());
                // for Zebra and Tango by Sanjeew date 09/07/07
                // user_profile_id, mcommerce_service_allow,
                // low_bal_alert_allow, mpay_profile_id
                // pstmtInsertInToChnlUser.setString(++index,channelUserVO.getUserProfileID());
                ++index;
                pstmtInsertInToChnlUser.setString(index, channelUserVO.getUserID());
                ++index;
                pstmtInsertInToChnlUser.setString(index, channelUserVO.getMcommerceServiceAllow());
                ++index;
                pstmtInsertInToChnlUser.setString(index, channelUserVO.getLowBalAlertAllow());
                ++index;
                pstmtInsertInToChnlUser.setString(index, channelUserVO.getMpayProfileID());
                // Added for Transfer Rule by shashank
                ++index;
                pstmtInsertInToChnlUser.setString(index, channelUserVO.getTrannferRuleTypeId());
                // end
                // End Zebra and Tango

                if (pstmtInsertInToChnlUser.executeUpdate() <= 0) {
                    p_con.rollback();
                    BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=When inserting channel_users table");
                    continue;
                }
                
                // insert into user_loan info
                if (SystemPreferences.USERWISE_LOAN_ENABLE) { 
                	if(!BTSLUtil.isNullString(channelUserVO.getLoanProfileId())){
                		Date curdate = new Date();
                		// insert into channel user loan info
                		pstmtInsertInChannelUserLoanInfo.clearParameters();
                		index = 0;
                		pstmtInsertInChannelUserLoanInfo.setString(++index, channelUserVO.getUserID());
                		pstmtInsertInChannelUserLoanInfo.setString(++index, channelUserVO.getLoanProfileId());
                		pstmtInsertInChannelUserLoanInfo.setString(++index, PretupsI.PRODUCT_ETOPUP);
                		pstmtInsertInChannelUserLoanInfo.setString(++index, "0");
                		pstmtInsertInChannelUserLoanInfo.setString(++index, "0");
                		pstmtInsertInChannelUserLoanInfo.setTimestamp(++index, BTSLUtil.getTimestampFromUtilDate(curdate));
                		pstmtInsertInChannelUserLoanInfo.setString(++index, PretupsI.SYSTEM);
                		pstmtInsertInChannelUserLoanInfo.setTimestamp(++index, BTSLUtil.getTimestampFromUtilDate(curdate));
                		pstmtInsertInChannelUserLoanInfo.setString(++index, PretupsI.SYSTEM);
                		if (pstmtInsertInChannelUserLoanInfo.executeUpdate() <= 0) {
                			p_con.rollback();
                			BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=When inserting channel_user_loan_info table");
                			continue;
                		}
                	}
                }
               
                // insert into user_phones
                if (channelUserVO.getCategoryVO().getSmsInterfaceAllowed().equals(PretupsI.YES)) {
                    int phoneinsCount = 0;
                    msisdnList = channelUserVO.getMsisdnList();
                    // for(int k=0,j=msisdnInput.length;k<j;k++){
                    for (int k = 0, msisdnListSize = msisdnList.size(); k < msisdnListSize; k++) {
                        userPhoneVO = (UserPhoneVO) msisdnList.get(k);
                        pstmtInsertInToUserPhones.clearParameters();
                        index = 0;
                        ++index;
                        pstmtInsertInToUserPhones.setString(index, "" + phoneID);
                        phoneID++;
                        // pstmtInsertInToUserPhones.setString(++index,
                        // channelUserVO.getMsisdn());
                        ++index;
                        pstmtInsertInToUserPhones.setString(index, userPhoneVO.getMsisdn().trim());
                        ++index;
                        pstmtInsertInToUserPhones.setString(index, channelUserVO.getUserID());
                        if (channelUserVO.getMsisdn().equalsIgnoreCase(userPhoneVO.getMsisdn().trim())) {
                            ++index;
                            pstmtInsertInToUserPhones.setString(index, PretupsI.YES);
                        } else {
                            ++index;
                            pstmtInsertInToUserPhones.setString(index, PretupsI.NO);
                        }
                        ++index;
                        pstmtInsertInToUserPhones.setString(index, userPhoneVO.getSmsPin());
                        // pstmtInsertInToUserPhones.setString(++index,
                        // channelUserVO.getSmsPin());
                        ++index;
                        pstmtInsertInToUserPhones.setString(index, PretupsI.YES);
                        ++index;
                        pstmtInsertInToUserPhones.setString(index, channelUserVO.getCategoryCode());
                        // pstmtInsertInToUserPhones.setString(++index,
                        // defaultLanguage);
                        ++index;
                        pstmtInsertInToUserPhones.setString(index, userPhoneVO.getPhoneLanguage());
                        // pstmtInsertInToUserPhones.setString(++index,
                        // defaultCountry);
                        ++index;
                        pstmtInsertInToUserPhones.setString(index, userPhoneVO.getCountry());
                        ++index;
                        pstmtInsertInToUserPhones.setString(index, channelUserVO.getCreatedBy());
                        ++index;
                        pstmtInsertInToUserPhones.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getCreatedOn()));
                        ++index;
                        pstmtInsertInToUserPhones.setString(index, channelUserVO.getModifiedBy());
                        ++index;
                        pstmtInsertInToUserPhones.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getModifiedOn()));
                        prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(PretupsBL.getFilteredMSISDN(channelUserVO.getMsisdn())));
                        ++index;
                        pstmtInsertInToUserPhones.setInt(index, Integer.parseInt(Long.toString(prefixVO.getPrefixID())));
                        ++index;
                        pstmtInsertInToUserPhones.setInt(index, 0);
                        ++index;
                        pstmtInsertInToUserPhones.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getCreatedOn()));
                        ++index;
                        pstmtInsertInToUserPhones.setString(index, PretupsI.YES);
                        phoneinsCount++;
                        if (pstmtInsertInToUserPhones.executeUpdate() <= 0) {
                            phoneinsCount--;
                            // p_con.rollback();
                            BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=When inserting users_phones table");
                            // continue;
                        }
                    }
                    if (msisdnInput.length < phoneinsCount) {
                        p_con.rollback();
                        continue;
                    }
                }
                
                if (channelUserVO.getCategoryVO().getServiceAllowed().equals(PretupsI.YES)) {
                    // insert into user_services
                    for (int ii = 0, j = channelUserVO.getServiceList().size(); ii < j; ii++) {
                        pstmtInsertInToUserService.clearParameters();
                        index = 0;
                        ++index;
                        pstmtInsertInToUserService.setString(index, channelUserVO.getUserID());
                        ++index;
                        pstmtInsertInToUserService.setString(index, (String) channelUserVO.getServiceList().get(ii));
                        pstmtInsertInToUserService.executeUpdate();
                    }
                }
                //Voucher Types
                if(isUserVoucherTypeAllowed && channelUserVO.getVoucherList() != null) {
                    // insert into user_vouchertypes
                    for (int ii = 0, j = channelUserVO.getVoucherList().size(); ii < j; ii++) {
                    	pstmtInsertInToVoucherTypes.clearParameters();
                        index = 0;
                        ++index;
                        pstmtInsertInToVoucherTypes.setString(index, channelUserVO.getUserID());
                        ++index;
                        pstmtInsertInToVoucherTypes.setString(index, (String) channelUserVO.getVoucherList().get(ii));
                        pstmtInsertInToVoucherTypes.executeUpdate();
                    }
                }
                if(channelUserVO.getSegmentList() != null) {
                	for (int ii = 0, j = channelUserVO.getSegmentList().size(); ii < j; ii++) {
                    	pstmtInsertInToVoucherSegments.clearParameters();
                        index = 0;
                        ++index;
                        pstmtInsertInToVoucherSegments.setString(index, channelUserVO.getUserID());
                        ++index;
                        pstmtInsertInToVoucherSegments.setString(index, (String) channelUserVO.getSegmentList().get(ii));
                        pstmtInsertInToVoucherSegments.executeUpdate();
                    }                	
                }
                
                log.debug("batchuserWebDaoafter insert into  users services"," time taken = "+(System.currentTimeMillis()-startTime)+" ms");
                startTime=System.currentTimeMillis();
                pstmtInsertInToUserGeographics.clearParameters();
                index = 0;
                ++index;
                pstmtInsertInToUserGeographics.setString(index, channelUserVO.getUserID());
                ++index;
                pstmtInsertInToUserGeographics.setString(index, channelUserVO.getGeographicalCode());
                if (pstmtInsertInToUserGeographics.executeUpdate() <= 0) {
                    p_con.rollback();
                    BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=When inserting users_geographics table");
                    continue;
                } else// make the array list of geography code
                {
                    if (!geographyDomainCodeList.contains(channelUserVO.getGeographicalCode())) {
                        geographyDomainCodeList.add(channelUserVO.getGeographicalCode());
                    }
                }
                
                if (channelUserVO.getCategoryVO().getWebInterfaceAllowed().equals(PretupsI.YES)) {
                    pstmtInsertInToUserRole.clearParameters();
                    index = 0;
                    ++index;
                    pstmtInsertInToUserRole.setString(index, channelUserVO.getUserID());
                    ++index;
                    pstmtInsertInToUserRole.setString(index, channelUserVO.getGroupRoleCode());
                    if (!BTSLUtil.isNullString(channelUserVO.getLoginID())) {
                        if (pstmtInsertInToUserRole.executeUpdate() <= 0) {
                            p_con.rollback();
                            BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=When inserting users_roles table");
                            continue;
                        }
                    }
                }
                
                commitCounter++;
                idCounter++;
                updateCount++;
                ChannelUserLog.log("BULKUSRINITIATE", channelUserVO, p_userVO, true, "Add channel user from bulk");
            }
        }
            if(updateCount==0&&insertintoBatches){
            	updateCount++;
            	channelUserVO = (ChannelUserVO) p_userDetailList.get(0);
            	noupdateofphoneanduserid = true;
            }
            if (updateCount > 0)// if any user information insert then insert
            // into batch geography and update batches
            {
            	if(!noupdateofphoneanduserid){
                idGeneratorDAO.updateNextBatchID(p_con, PretupsI.USERID, PretupsI.ALL, channelUserVO.getNetworkID(), channelUserVO.getCreatedOn(), idCounter - 1);
                idGeneratorDAO.updateNextBatchID(p_con, PretupsI.USR_PHONE_ID, PretupsI.ALL, PretupsI.ALL, channelUserVO.getCreatedOn(), phoneID - 1);
            	}
                if(geographyDomainCodeList.isEmpty()){
                if (!geographyDomainCodeList.contains(channelUserVO.getGeographicalCode())) {
                    geographyDomainCodeList.add(channelUserVO.getGeographicalCode());
                }
                }
                String geoDomainCode = "'";
                for (int i = 0, j = geographyDomainCodeList.size(); i < j; i++) {
                    geoDomainCode = geoDomainCode + geographyDomainCodeList.get(i) + "','";
                }
                geoDomainCode = geoDomainCode.substring(0, geoDomainCode.length() - 2);

                final UserGeographiesVO userGeographiesVO = p_userVO.getGeographicalAreaList().get(0);
                final int geoTypeSeqNo = userGeographiesVO.getGraphDomainSequenceNumber();

                // get the parent geographical domain code listing
                
                pstmtselectParentGeography = batchUserWebQry.addChannelUserListSelectParentGeographyQry(p_con, geoDomainCode, geoTypeSeqNo);
                rsselectParentGeography = pstmtselectParentGeography.executeQuery();
                log.debug("batchuserWebDaoafter insert into  batch geographies"," time taken = "+(System.currentTimeMillis()-startTime)+" ms");
                // insert into batch_geographics table
                if(insertintoBatches){
                while (rsselectParentGeography.next()) {
                    pstmtInsertInToBatchGeography.clearParameters();
                    pstmtInsertInToBatchGeography.setString(1, batchID);
                    pstmtInsertInToBatchGeography.setString(2, rsselectParentGeography.getString("grph_domain_code"));
                    if (pstmtInsertInToBatchGeography.executeUpdate() <= 0) {
                        p_con.rollback();
                        BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=When inserting batch_geographics table");
                        continue;
                    }
                }
                if(!errorList.isEmpty()){
                	total = total - errorList.size();
                }
                pstmtInsertIntoBatches.setString(1, batchID);
                pstmtInsertIntoBatches.setString(2, PretupsI.BULK_USR_BATCH_TYPE);
                pstmtInsertIntoBatches.setInt(3, total);
                pstmtInsertIntoBatches.setString(4, channelUserVO.getBatchName());
                pstmtInsertIntoBatches.setString(5, channelUserVO.getNetworkID());
                pstmtInsertIntoBatches.setString(6, PretupsI.USR_BATCH_STATUS_UNDERPROCESS);
                pstmtInsertIntoBatches.setString(7, channelUserVO.getCreatedBy());
                pstmtInsertIntoBatches.setTimestamp(8, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getCreatedOn()));
                pstmtInsertIntoBatches.setString(9, channelUserVO.getModifiedBy());
                pstmtInsertIntoBatches.setTimestamp(10, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getModifiedOn()));
                pstmtInsertIntoBatches.setString(11, p_fileName);
                if (pstmtInsertIntoBatches.executeUpdate() <= 0) {
                    p_con.rollback();
                    /*BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + p_messages.getMessage("bulkuser.initiatebulkuser.err.batchnotcreated"));
                    throw new BTSLBaseException(this, methodName, "bulkuser.initiatebulkuser.err.batchnotcreated", "selectDomainForInitiate");*/
                }
                insertintoBatches = false;
                errorVO = new ListValueVO("BATCHID", "", batchID);
                errorList.add(errorVO);
                }
                // update batches table for updating updateCount on the batch
                // size
                pstmtupdateIntoBatches.setInt(1, total);
                if(((Integer)PreferenceCache.getControlPreference(PreferenceI.USER_APPROVAL_LEVEL,channelUserVO.getNetworkID(),channelUserVO.getCategoryCode())).intValue() >0  ){
		    	    pstmtupdateIntoBatches.setString(2,PretupsI.USR_BATCH_STATUS_OPEN);
    	        }
	    	    else 
	    	    	pstmtupdateIntoBatches.setString(2,PretupsI.USR_BATCH_STATUS_CLOSE);
                pstmtupdateIntoBatches.setString(3, batchID);
                pstmtupdateIntoBatches.executeUpdate();
                log.debug("batchuserWebDao last main thread into  batches "," time taken = "+(System.currentTimeMillis()-startTime)+" ms");
                p_con.commit();
            } else {
                p_con.rollback();
            }

        } catch (SQLException sqe) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[addChannelUserList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[addChannelUserList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rsUserPhones != null) {
                    rsUserPhones.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsParentUserDetails != null) {
                    rsParentUserDetails.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsSelectLoginID != null) {
                    rsSelectLoginID.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsselectGeography != null) {
                    rsselectGeography.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsselectParentGeography != null) {
                    rsselectParentGeography.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsChildGeographyAllowed != null) {
                    rsChildGeographyAllowed.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsExternalCode != null) {
                    rsExternalCode.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsPortedMSISDN != null) {
                    rsPortedMSISDN.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsParentDetails != null) {
                    rsParentDetails.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsSelectParentHierarchy != null) {
                    rsSelectParentHierarchy.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
        	try {
                if (pstmtInsertInToVoucherTypes != null) {
                	pstmtInsertInToVoucherTypes.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
        	try {
                if (pstmtInsertInToVoucherSegments != null) {
                	pstmtInsertInToVoucherSegments.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectParentUserDetails != null) {
                    pstmtSelectParentUserDetails.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectParentUserDetailsP != null) {
                    pstmtSelectParentUserDetailsP.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectUserPhones != null) {
                    pstmtSelectUserPhones.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectLoginID != null) {
                    pstmtSelectLoginID.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtselectGeography != null) {
                    pstmtselectGeography.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtselectParentGeography != null) {
                    pstmtselectParentGeography.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtChildGeographyAllowed != null) {
                    pstmtChildGeographyAllowed.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }

            try {
                if (pstmtInsertInToUser != null) {
                    pstmtInsertInToUser.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertInToChnlUser != null) {
                    pstmtInsertInToChnlUser.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertInToUserPhones != null) {
                    pstmtInsertInToUserPhones.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertInToUserService != null) {
                    pstmtInsertInToUserService.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertInToUserGeographics != null) {
                    pstmtInsertInToUserGeographics.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertInToUserRole != null) {
                    pstmtInsertInToUserRole.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertInToBatchGeography != null) {
                    pstmtInsertInToBatchGeography.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertIntoBatches != null) {
                    pstmtInsertIntoBatches.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtupdateIntoBatches != null) {
                    pstmtupdateIntoBatches.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectExternalCode != null) {
                    pstmtSelectExternalCode.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtPortedMSISDN != null) {
                    pstmtPortedMSISDN.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtParentDetails != null) {
                    pstmtParentDetails.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            // added by shashank
            try {
                if (pstmtSelectParentHierarchy != null) {
                    pstmtSelectParentHierarchy.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            
            try {
            	if(pstmtInsertInChannelUserLoanInfo != null) {
            		pstmtInsertInChannelUserLoanInfo.close();
            	}
            }catch(Exception e) {
            	log.errorTrace(methodName, e);
            }
            // end

            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: errorList size =" + errorList.size());
            }
        }
        return errorList;
    }

    /**
     * Method generateUserId
     * Method to generate the userId while inserting new record
     * 
     * @param p_networkCode
     *            String
     * @param p_prefix
     *            String
     * @param p_counter
     *            Long
     * @param p_userPaddingLength
     *            TODO
     * @return String
     */
    private String generateUserId(String p_networkCode, String p_prefix, long p_counter, int p_userPaddingLength) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("generateUserId", "Entered p_networkCode=" + p_networkCode + " p_prefix=" + p_prefix + " p_counter=" + p_counter);
        }
        String id = BTSLUtil.padZeroesToLeft(p_counter + "", p_userPaddingLength);
        id = p_networkCode + p_prefix + id;
        if (log.isDebugEnabled()) {
            log.debug("generateUserId", "Exiting id=" + id);
        }
        return id;
    }

    // added by Ashutosh
    public ArrayList loadCommProfileList(Connection p_con, String p_domainCode, String p_networkCode, String p_categoryCode, String p_userType) throws BTSLBaseException {
        final String methodName = "loadCommProfileList";
        if (log.isDebugEnabled()) {
            log.debug(methodName,
                "Entered: p_domainCode=" + p_domainCode + " p_networkCode=" + p_networkCode + "p_userType=" + p_userType + "p_categoryCode=" + p_categoryCode);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
      
        final String sqlSelect =  batchUserWebQry.loadCommProfileListQry(p_domainCode, p_networkCode);
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList commProfileList = new ArrayList();
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            final int i = 1;
     
            rs = pstmtSelect.executeQuery();
            CommissionProfileSetVO commissionProfileSetVO = null;
            while (rs.next()) {
                commissionProfileSetVO = new CommissionProfileSetVO();
                commissionProfileSetVO.setCategoryCode(rs.getString("category_code"));
                commissionProfileSetVO.setCategoryName((rs.getString("category_name")));
                commissionProfileSetVO.setGradeCode((rs.getString("grade_code")));
                if ("ALL".equals(commissionProfileSetVO.getGradeCode())) {
                    commissionProfileSetVO.setGradeName("ALL");
                } else {
                    commissionProfileSetVO.setGradeName((rs.getString("grade_name")));
                }
                commissionProfileSetVO.setGrphDomainCode((rs.getString("GEOGRAPHY_CODE")));
                if ("ALL".equals(commissionProfileSetVO.getGrphDomainCode())) {
                    commissionProfileSetVO.setGrphDomainName("ALL");
                } else {
                    commissionProfileSetVO.setGrphDomainName((rs.getString("GRPH_DOMAIN_NAME")));
                }
                commissionProfileSetVO.setCommProfileSetId(rs.getString("comm_profile_set_id"));
                
                if(rs.getString("status").equalsIgnoreCase(PretupsI.COMMISSION_PROFILE_STATUS_SUSPEND)){
    	        	commissionProfileSetVO.setCommProfileSetName(rs.getString("comm_profile_set_name")+BTSLUtil.NullToString(Constants.getProperty("COMM_PROFILE_SUSPENDED")));
    	        }
    	        else
    	        	commissionProfileSetVO.setCommProfileSetName(rs.getString("comm_profile_set_name"));
               
                commProfileList.add(commissionProfileSetVO);
            }
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[loadCommProfileList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[loadCommProfileList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: commProfileList size =" + commProfileList.size());
            }
        }
        return commProfileList;
    }

    // added by Ashutosh
    public ArrayList loadGeographyList(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadGeographyList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: ");
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer(" select gd.GRPH_DOMAIN_CODE,gd.GRPH_DOMAIN_NAME,gd.GRPH_DOMAIN_TYPE,c.CATEGORY_CODE ");
        strBuff.append("from GEOGRAPHICAL_DOMAINS gd,CATEGORIES c ");
        strBuff.append("where gd.GRPH_DOMAIN_TYPE=c.GRPH_DOMAIN_TYPE ");

        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList geogList = new ArrayList();
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);

            rs = pstmtSelect.executeQuery();
            UserGeographiesVO userGeographiesVO = null;
            while (rs.next()) {
                userGeographiesVO = new UserGeographiesVO();
                userGeographiesVO.setGraphDomainCode(SqlParameterEncoder.encodeParams(rs.getString("GRPH_DOMAIN_CODE")));
                userGeographiesVO.setGraphDomainName(SqlParameterEncoder.encodeParams(rs.getString("GRPH_DOMAIN_NAME")));
                userGeographiesVO.setGraphDomainType(SqlParameterEncoder.encodeParams(rs.getString("GRPH_DOMAIN_TYPE")));
                userGeographiesVO.setCategoryCode(SqlParameterEncoder.encodeParams(rs.getString("CATEGORY_CODE")));
                System.out.println(userGeographiesVO.getGraphDomainCode() + "," + userGeographiesVO.getGraphDomainName() + "," + userGeographiesVO.getGraphDomainType());

                geogList.add(userGeographiesVO);
            }
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[loadGeographyList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[loadGeographyList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: geographicesList size =" + geogList.size());
            }
        }
        return geogList;
    }

    /**
     * Method :addChannelUserAdditionalDetailList
     * This method check the data base validation of add channel user additional
     * detail
     * and after validation insert into user table.
     * 
     * @param p_con
     *            Connection
     * @param p_userDetailList
     *            ArrayList
     * @param p_domainCode
     *            String
     * @param p_messages
     *            MessageResources
     * @param p_locale
     *            Locale
     * @param p_userVO
     *            TODO
     * @return ArrayList
     * @throws BTSLBaseException
     * @author Harsh Dixit
     */
    public ArrayList addChannelUserAdditionalDetailList(Connection p_con, ArrayList p_userDetailList, String p_domainCode, MessageResources p_messages, Locale p_locale, UserVO p_userVO, String p_fileName) throws BTSLBaseException {
        final String methodName = "addChannelUserAdditionalDetailList";
        if (log.isDebugEnabled()) {
            log.debug(
                methodName,
                "Entered: p_userDetailList.size()=" + p_userDetailList.size() + " p_domainCode=" + p_domainCode + " p_messages=" + p_messages + " p_locale=" + p_locale + " p_fileName: " + p_fileName);
        }
        final ArrayList errorList = new ArrayList();
        ListValueVO errorVO = null;
        int commitCounter = 0;
        int index = 0;
        boolean flag = true;
        final long phoneID = 0;
        final IDGeneratorDAO idGeneratorDAO = new IDGeneratorDAO();
        final NetworkPrefixVO prefixVO = null;
        OperatorUtilI operatorUtil = null;
        String batchID = null;

        // batches insert
        final StringBuffer insertIntoBatches = new StringBuffer(
            "INSERT INTO batches (batch_id, batch_type, batch_size, batch_name, network_code, status, created_by, created_on, modified_by, modified_on,file_name) ");
        insertIntoBatches.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?)");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "insertIntoBatches Query =" + insertIntoBatches);
        }
        PreparedStatement pstmtInsertIntoBatches = null;

        // update batches table
        final StringBuffer updateIntoBatches = new StringBuffer("UPDATE batches SET batch_size=?, status=? WHERE batch_id=? ");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "updateIntoBatches Query =" + updateIntoBatches);
        }
        PreparedStatement pstmtupdateIntoBatches = null;

        final StringBuffer updateInToUser = new StringBuffer("UPDATE users SET batch_id=?,info1=?,info2=?,info3=?, ");
        updateInToUser.append("info4=?,info5=?,info6=?,info7=?,info8=?,info9=?,info10=?, ");
        updateInToUser.append("info11=?,info12=?,info13=?,info14=?,info15=?, ");
        updateInToUser.append("modified_by=?,modified_on=? ");
        updateInToUser.append(" where user_name=? AND msisdn=? ");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "updateInToUser Query =" + updateInToUser);
        }
        PreparedStatement pstmtUpdateInToUser = null;

        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[addChannelUserList]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }

        try {
            pstmtInsertIntoBatches = p_con.prepareStatement(insertIntoBatches.toString());
            pstmtUpdateInToUser = p_con.prepareStatement(updateInToUser.toString());
            pstmtupdateIntoBatches = p_con.prepareStatement(updateIntoBatches.toString());
            ChannelUserVO channelUserVO = null;
            int seqNo = 0;
            int updateCount = 0;
            long idCounter = 0;
            int commitNumber = 0;
            int userPaddingLength = 0;
            try {
                commitNumber = Integer.parseInt(Constants.getProperty("BATCH_USER_COMMIT_NUMBER"));
                userPaddingLength = Integer.parseInt(Constants.getProperty("USER_PADDING_LENGTH"));
            } catch (Exception e) {
                commitNumber = 100;
                userPaddingLength = 10;
                log.error(methodName, "Exception:e=" + e);
                log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "BatchUserWebDAO[addChannelUserAdditionalDetailList]", "", "", "", "Exception:" + e.getMessage());
            }
            CategoryVO categoryVO = null;
            final NetworkPrefixVO networkPrefixVO = null;
            final String filterMsisdn = null;
            final ArrayList msisdnList = null;
            final UserPhoneVO userPhoneVO = null;
            for (int i = 0, length = p_userDetailList.size(); i < length; i++) {
                seqNo = 0;
                channelUserVO = (ChannelUserVO) p_userDetailList.get(i);
                categoryVO = channelUserVO.getCategoryVO();
                // ***After all validation insertion starts
                // here********************************************
                // Insert into batches
                if (flag) {
                    // one time entry into batches table
                    batchID = operatorUtil.formatBatchesID(p_userVO.getNetworkID(), PretupsI.BATCH_USRADDET_PREFIX_ID, new Date(), IDGenerator.getNextID(
                        PretupsI.BATCH_USRADDET_ID, BTSLUtil.getFinancialYear(), p_userVO.getNetworkID()));
                    pstmtInsertIntoBatches.setString(1, batchID);
                    pstmtInsertIntoBatches.setString(2, PretupsI.BULK_USRADDET_BATCH_TYPE);
                    pstmtInsertIntoBatches.setInt(3, length);
                    pstmtInsertIntoBatches.setString(4, channelUserVO.getBatchName());
                    pstmtInsertIntoBatches.setString(5, channelUserVO.getNetworkID());
                    pstmtInsertIntoBatches.setString(6, PretupsI.USR_BATCH_STATUS_UNDERPROCESS);
                    pstmtInsertIntoBatches.setString(7, channelUserVO.getCreatedBy());
                    pstmtInsertIntoBatches.setTimestamp(8, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getCreatedOn()));
                    pstmtInsertIntoBatches.setString(9, channelUserVO.getModifiedBy());
                    pstmtInsertIntoBatches.setTimestamp(10, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getModifiedOn()));
                    pstmtInsertIntoBatches.setString(11, p_fileName);
                    if (pstmtInsertIntoBatches.executeUpdate() <= 0) {
                        p_con.rollback();
                        BatchesLog.log("BULKUSRADDDETAIL", channelUserVO, null, "Fail :=" + p_messages.getMessage("bulkuser.initiatebulkuser.err.batchnotcreated"));
                        throw new BTSLBaseException(this, "addChannelUserList", "bulkuser.initiatebulkuser.err.batchnotcreated", "addBatchUserAdditionalDetails");
                    }
                    flag = false;
                }

                // Insert into users table.

                pstmtUpdateInToUser.clearParameters();
                index = 0;
                ++index;
                pstmtUpdateInToUser.setString(index, batchID);
                ++index;
                pstmtUpdateInToUser.setString(index, channelUserVO.getInfo1());
                ++index;
                pstmtUpdateInToUser.setString(index, channelUserVO.getInfo2());
                ++index;
                pstmtUpdateInToUser.setString(index, channelUserVO.getInfo3());
                ++index;
                pstmtUpdateInToUser.setString(index, channelUserVO.getInfo4());
                ++index;
                pstmtUpdateInToUser.setString(index, channelUserVO.getInfo5());
                ++index;
                pstmtUpdateInToUser.setString(index, channelUserVO.getInfo6());
                ++index;
                pstmtUpdateInToUser.setString(index, channelUserVO.getInfo7());
                ++index;
                pstmtUpdateInToUser.setString(index, channelUserVO.getInfo8());
                ++index;
                pstmtUpdateInToUser.setString(index, channelUserVO.getInfo9());
                ++index;
                pstmtUpdateInToUser.setString(index, channelUserVO.getInfo10());
                ++index;
                pstmtUpdateInToUser.setString(index, channelUserVO.getInfo11());
                ++index;
                pstmtUpdateInToUser.setString(index, channelUserVO.getInfo12());
                ++index;
                pstmtUpdateInToUser.setString(index, channelUserVO.getInfo13());
                ++index;
                pstmtUpdateInToUser.setString(index, channelUserVO.getInfo14());
                ++index;
                pstmtUpdateInToUser.setString(index, channelUserVO.getInfo15());
                ++index;
                pstmtUpdateInToUser.setString(index, channelUserVO.getModifiedBy());
                ++index;
                pstmtUpdateInToUser.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getModifiedOn()));
                ++index;
                pstmtUpdateInToUser.setString(index, channelUserVO.getUserName());
                ++index;
                pstmtUpdateInToUser.setString(index, channelUserVO.getMsisdn());

                if (pstmtUpdateInToUser.executeUpdate() <= 0) {
                    p_con.rollback();
                    BatchesLog.log("BULKUSRADDDETAIL", channelUserVO, null, "Fail :=When updating users table");
                    continue;
                }
                commitCounter++;
                idCounter++;
                updateCount++;
                ChannelUserLog.log("BULKUSRADDDETAIL", channelUserVO, p_userVO, true, "Add channel user from bulk");
            }
            if (updateCount > 0)// if any user information insert then insert
            // into batch geography and update batches
            {
                idGeneratorDAO.updateNextBatchID(p_con, PretupsI.USERID, PretupsI.ALL, channelUserVO.getNetworkID(), channelUserVO.getCreatedOn(), idCounter - 1);
                // insert into batch_geographics table
                // update batches table for updating updateCount on the batch
                // size
                pstmtupdateIntoBatches.setInt(1, updateCount);
                pstmtupdateIntoBatches.setString(2, PretupsI.USR_BATCH_STATUS_OPEN);
                pstmtupdateIntoBatches.setString(3, batchID);
                pstmtupdateIntoBatches.executeUpdate();
                p_con.commit();
            } else {
                p_con.rollback();
            }
            errorVO = new ListValueVO("BATCHID", "", batchID);
            errorList.add(errorVO);

        } catch (SQLException sqe) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[addChannelUserAdditionalDetailList]",
                "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "addChannelUserList", "error.general.sql.processing");
        } catch (Exception ex) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[addChannelUserAdditionalDetailList]",
                "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {

            try {
                if (pstmtUpdateInToUser != null) {
                    pstmtUpdateInToUser.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertIntoBatches != null) {
                    pstmtInsertIntoBatches.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtupdateIntoBatches != null) {
                    pstmtupdateIntoBatches.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            // added by shashank
            // end

            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: errorList size =" + errorList.size());
            }
        }
        return errorList;
    }

    /**
     * Method :loadBatchBarDetailList
     * 
     * @author shashank.gaur
     * 
     *         This method load list of users of the selected batch to be
     *         barred.
     * @param p_con
     *            Connection
     * @param p_batchid
     *            String
     * 
     * @return ArrayList
     * @throws BTSLBaseException
     * 
     */

    public ArrayList loadBatchBarDetailList(Connection p_con, String p_batchID, String userStaus, BatchesVO batchesVO) throws BTSLBaseException {
        final String methodName = "loadBatchBarDetailList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_batchID=" + p_batchID);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer();

        strBuff.append("select u.USER_ID, b.BATCH_ID, u.STATUS, u.CATEGORY_CODE,");
        // strBuff.append(" bi.INITIATOR_REMARKS, bi.FIRST_APPROVER_REMARKS,");
        // strBuff.append(" bi.FIRST_APPROVED_BY, bi.FIRST_APPROVED_ON,bi.REJECT_BY,");
        strBuff.append(" u.msisdn, u.user_name, b.approved_records, b.rejected_records");
        strBuff.append(" from users u,batches b");
        strBuff.append(" where b.batch_id=u.barred_deletion_batchid and b.batch_id=? and u.status = ?");

        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList userList = new ArrayList();
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_batchID);
            pstmtSelect.setString(2, userStaus);
            rs = pstmtSelect.executeQuery();
            UserVO userVO = null;
            while (rs.next()) {
                userVO = new UserVO();
                userVO.setBatchID(rs.getString("batch_id"));
                // userVO.setBatchName(rs.getString("batch_name"));
                // userVO.setBatchDetailID(rs.getString("batch_details_id"));
                userVO.setMsisdn(rs.getString("msisdn"));
                userVO.setUserID(rs.getString("user_id"));
                userVO.setUserName(rs.getString("user_name"));
                userVO.setStatus(rs.getString("status"));
                userVO.setCategoryCode(rs.getString("category_code"));
                batchesVO.setActiveRecords(rs.getInt("approved_records"));
                batchesVO.setRejectRecords(rs.getInt("rejected_records"));
                // userVO.setRemarks(rs.getString("initiator_remarks"));
                // userVO.setLevel1Remarks(rs.getString("first_approver_remarks"));
                // userVO.setLevel1ApprovedBy(rs.getString("first_approved_by"));
                // userVO.setLevel1ApprovedOn(rs.getTimestamp("first_approved_on"));
                // if(userVO.getLevel1ApprovedOn()!=null)
                // userVO.setLevel1ApprovedOnStr(BTSLUtil.getDateTimeStringFromDate(userVO.getLevel1ApprovedOn()));
                userList.add(userVO);
            }
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[loadBatchBarListForApproval]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error("loadBatchListForApproval", "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[loadBatchBarListForApproval]", "", "",
                "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: userList size =" + userList.size());
            }
        }

        return userList;
    }

    /**
     * Method :loadBatchBarListForApproval
     * 
     * @author shashank.gaur
     * 
     *         This method load list of batches to be barred.
     * @param p_con
     *            Connection
     * @param p_itemStatus
     *            String
     * @param p_currentLevel
     *            String
     * 
     * @return ArrayList
     * @throws BTSLBaseException
     * 
     */
    public ArrayList loadBatchBarListForApproval(Connection p_con, String p_currentLevel, String p_networkcode) throws BTSLBaseException {
        final String methodName = "loadBatchBarListForApproval";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_currentLevel=" + p_currentLevel, " p_networkcode=" + p_networkcode);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final String sqlSelect = batchUserWebQry.loadBatchBarListForApprovalQry(p_currentLevel);
        
        final ArrayList<BatchesVO> batchList = new ArrayList<BatchesVO>();
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            int i = 1;
            pstmtSelect.setString(i, PretupsI.USR_BATCH_BAR_STATUS_OPEN);
            i++;
            pstmtSelect.setString(i, PretupsI.USR_BATCH_BAR_STATUS_APPROVE1);
            i++;
            pstmtSelect.setString(i, PretupsI.USR_BATCH_BAR_STATUS_REJECT);
            i++;
            pstmtSelect.setString(i, PretupsI.USR_BATCH_BAR_STATUS_CLOSE);
            i++;
            // pstmtSelect.setString(i++,PretupsI.USR_BATCH_BAR_STATUS_OPEN);
            pstmtSelect.setString(i, PretupsI.BATCH_BAR_FOR_DEL_TYPE);
            i++;
            pstmtSelect.setString(i, p_networkcode);
            i++;
            rs = pstmtSelect.executeQuery();
            BatchesVO batchesVO = null;
            while (rs.next()) {
                batchesVO = new BatchesVO();
                batchesVO.setBatchID(rs.getString("batch_id"));
                batchesVO.setBatchName(rs.getString("batch_name"));
                batchesVO.setBatchSize(rs.getLong("batch_size"));
                // batchesVO.setBatchType(rs.getString("batch_type"));
                batchesVO.setCreatedBy(rs.getString("created_by"));
                batchesVO.setCreatedOn(rs.getTimestamp("created_on"));
                if (batchesVO.getCreatedOn() != null) {
                    /**Convert date-timestamp as per locale starts**/
                	batchesVO.setCreatedOnStr(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(batchesVO.getCreatedOn())));
                	/**Convert date-timestamp as per locale ends**/
                }
                batchesVO.setModifiedBy(rs.getString("modified_by"));
                batchesVO.setModifiedOn(rs.getTimestamp("modified_on"));
                if (batchesVO.getModifiedOn() != null) {
                	/**Convert date-timestamp as per locale starts**/
                    batchesVO.setModifiedOnStr(BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(batchesVO.getModifiedOn())));
                    /**Convert date-timestamp as per locale ends**/
                }
                // batchesVO.setActiveRecords(rs.getInt("appr1"));
                // batchesVO.setNewRecords(rs.getInt("open"));
                // batchesVO.setRejectRecords(rs.getInt("rejct"));
                batchList.add(batchesVO);
            }
        } catch (SQLException sqe) {
            log.error("loadBatchListForApproval", "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[loadBatchBarListForApproval]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error("loadBatchListForApproval", "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[loadBatchBarListForApproval]", "", "",
                "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: batchList size =" + batchList.size());
            }
        }
        return batchList;
    }

    /**
     * @author shashank.gaur
     *         Method for create a new batch for barred users
     *         (insert into batches)
     * 
     * @param p_con
     *            java.sql.Connection
     * @param sessionUserVO
     *            (consist of UserVO)
     * @param form
     *            BatchBarForDelForm
     * 
     * @throws BTSLBaseException
     */
    public void addbatchbar(Connection p_con, BatchBarForDelForm form, UserVO sessionUserVO) throws BTSLBaseException {
        int i = 0;
        String batchID = null;
        BatchBarForDelForm theForm = null;
        OperatorUtilI operatorUtil = null;
        PreparedStatement pstmtBar = null;
        final Date currentDate = new Date();
        final String methodName = "addbatchbar";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }

        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);

        try {
            operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[addChannelUserList]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }

        try {
            theForm = (BatchBarForDelForm) form;
            batchID = operatorUtil.formatBatchesID(sessionUserVO.getNetworkID(), PretupsI.BULK_USR_BAR_ID_PREFIX, currentDate, IDGenerator.getNextID(
                PretupsI.BULK_USR_BAR_ID_PREFIX, BTSLUtil.getFinancialYear(), sessionUserVO.getNetworkID()));
            theForm.setBatchID(batchID);
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append("INSERT into batches values (?,?,?,?,?,?,?,?,?,?,?,?,?)");

            pstmtBar = p_con.prepareStatement(strBuff.toString());
            ++i;
            pstmtBar.setString(i, batchID);
            ++i;
            pstmtBar.setString(i, PretupsI.BATCH_BAR_FOR_DEL_TYPE);
            ++i;
            pstmtBar.setInt(i, theForm.getNoOfRecords());
            ++i;
            pstmtBar.setString(i, theForm.getBatchName());
            ++i;
            pstmtBar.setString(i, sessionUserVO.getNetworkID());
            ++i;
            pstmtBar.setString(i, theForm.getStatus());
            ++i;
            pstmtBar.setString(i, sessionUserVO.getUserID());
            ++i;
            pstmtBar.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(currentDate));
            ++i;
            pstmtBar.setString(i, sessionUserVO.getUserID());
            ++i;
            pstmtBar.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(currentDate));
            ++i;
            pstmtBar.setString(i, theForm.getFileName());
            
            ++i;
            pstmtBar.setInt(i, theForm.getApprovedRecords());
            
            ++i;
            pstmtBar.setInt(i, theForm.getRejectedRecords());
            pstmtBar.executeUpdate();

            if (log.isDebugEnabled()) {
                log.debug(methodName, " OtherQuery sqlSelect QUERY :" + strBuff.toString());
            }

        } // end of try

        catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[addbatchbar]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[addbatchbar]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (pstmtBar != null) {
                    pstmtBar.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting");
            }
        } // end of finally
    }

    /**
     * @author lalit
     * @param p_con
     * @param p_associateGradeUserIdMap
     * @param p_messages
     * @param p_locale
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList reAssociateModChannelUser(Connection p_con, HashMap p_associateGradeUserIdMap, MessageResources p_messages, Locale p_locale) throws BTSLBaseException {
        final String methodName = "reAssociateModChannelUser";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_reAssociateCellGroupIdList size = " + p_associateGradeUserIdMap.size());
        }
        final ArrayList errorList = new ArrayList();
        ArrayList associateGradeUserIdList = new ArrayList();
        PreparedStatement pstmt = null;
        GradeVO gradeVO = null;
        ListValueVO errorVO = null;
        int reAssociateCount = 0;
        final java.util.Date date = new java.util.Date();
        if (p_associateGradeUserIdMap.containsKey(PretupsI.BATCH_USER_GRADE_ASSOCIATION_USERGRDASSCID)) {
            associateGradeUserIdList = (ArrayList) p_associateGradeUserIdMap.get(PretupsI.BATCH_USER_GRADE_ASSOCIATION_USERGRDASSCID);
        }
        final StringBuffer modUserGradeQuery = new StringBuffer("UPDATE channel_users SET user_grade = ?  WHERE user_id = ?");
        if (log.isDebugEnabled()) {
            log.debug("reAssociateModChannelUser ", " Modify Status Query  =  " + modUserGradeQuery);
        }
        int associateGradeListSize = associateGradeUserIdList.size();
        try {
            pstmt = p_con.prepareStatement(modUserGradeQuery.toString());
            for (int i = 0; i < associateGradeListSize; i++) {
                gradeVO = (GradeVO) associateGradeUserIdList.get(i);
                pstmt.clearParameters();
                pstmt.setString(1, gradeVO.getGradeCode());
                pstmt.setString(2, gradeVO.getCategoryUserId());
                final int update = pstmt.executeUpdate();
                if (update > 0) {
                    reAssociateCount++;
                } else {
                    errorVO = new ListValueVO("", (new Integer(gradeVO.getRecordNumber())).toString(), p_messages.getMessage(p_locale,
                        "userbulkgradeassociation.upload.dberror.nomatchfound"));
                    errorList.add(errorVO);
                    continue;
                }
            }

        } catch (SQLException sqe) {
            log.error("reAssociateModChannelUser ", "User Id and grade could not re-associated " + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error("reAssociateModChannelUser ", "User Id and grade could not re-associated " + e.getMessage());
            log.errorTrace(methodName, e);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            if (log.isDebugEnabled()) {
                log.debug("reAssociateModChannelUser ", "Exiting " + reAssociateCount);
            }
        }
        return errorList;
    }

    /**
     * @author lalit.mishra
     * @param p_con
     * @param p_AssociateGradeUserIdMap
     * @param p_messages
     * @param p_locale
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList associateModUser(Connection p_con, HashMap p_AssociateGradeUserIdMap, MessageResources p_messages, Locale p_locale) throws BTSLBaseException {
        final String methodName = "associateModUser";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_reAssociateCellGroupIdList size = " + p_AssociateGradeUserIdMap.size());
        }
        final ArrayList errorList = new ArrayList();
        ArrayList associateGradeUserIdList = new ArrayList();
        PreparedStatement pstmt = null;
        GradeVO gradeVO = null;
        ListValueVO errorVO = null;
        int reAssociateCount = 0;
        final java.util.Date date = new java.util.Date();
        if (p_AssociateGradeUserIdMap.containsKey(PretupsI.BATCH_USER_GRADE_ASSOCIATION_USERGRDASSCID)) {
            associateGradeUserIdList = (ArrayList) p_AssociateGradeUserIdMap.get(PretupsI.BATCH_USER_GRADE_ASSOCIATION_USERGRDASSCID);
        }
        final StringBuffer modUserGradeQuery = new StringBuffer("UPDATE users SET modified_by = ?,modified_on = ?  WHERE user_id = ? and owner_id = ? and status <> 'N'");
        if (log.isDebugEnabled()) {
            log.debug("associateModUser ", " Modify Status Query  =  " + modUserGradeQuery);
        }
        int associateUserListSize = associateGradeUserIdList.size();
        try {
            pstmt = p_con.prepareStatement(modUserGradeQuery.toString());
            for (int i = 0; i < associateUserListSize; i++) {
                gradeVO = (GradeVO) associateGradeUserIdList.get(i);
                pstmt.clearParameters();
                pstmt.setString(1, gradeVO.getModifiedBy());
                pstmt.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(date));
                pstmt.setString(3, gradeVO.getCategoryUserId());
                pstmt.setString(4, gradeVO.getOwnerCategoryCode());
                final int update = pstmt.executeUpdate();
                if (update > 0) {
                    reAssociateCount++;
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("error_desc ", " DESC :" + p_messages.getMessage(p_locale, "userbulkgradeassociation.upload.datasheet.currentgradeuserid.invalid"));
                    }
                    errorVO = new ListValueVO("", (new Integer(gradeVO.getRecordNumber())).toString(), p_messages.getMessage(p_locale,
                        "userbulkgradeassociation.upload.datasheet.currentgradeuserid.invalid"));
                    errorList.add(errorVO);
                    continue;
                }
            }

        } catch (SQLException sqe) {
            log.error("associateModUser ", "User Id could not associated " + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error("associateModUser ", "User Id could not associated " + e.getMessage());
            log.errorTrace(methodName, e);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            if (log.isDebugEnabled()) {
                log.debug("associateModUser ", "Exiting " + reAssociateCount);
            }
        }
        return errorList;
    }

    /**
     * Method :modifyChannelUserList
     * This method check the data base validation of initiate channel user
     * and after validation update into channel user related tables.
     * 
     * @param p_con
     *            Connection
     * @param p_userDetailList
     *            ArrayList
     * @param p_domainCode
     *            String
     * @param p_messages
     *            MessageResources
     * @param p_locale
     *            Locale
     * @return ArrayList
     * @throws BTSLBaseException
     * @author sanjeew.kumar
     */
    public ArrayList modifyChannelUserList(Connection p_con, ArrayList p_userDetailList, String p_domainCode, MessageResources p_messages, Locale p_locale, String p_fileName) throws BTSLBaseException {
        final String methodName = "modifyChannelUserList";
        if (log.isDebugEnabled()) {
            log.debug(
                methodName,
                "Entered: p_userDetailList.size()=" + p_userDetailList.size() + " p_domainCode=" + p_domainCode + " p_messages=" + p_messages + " p_locale=" + p_locale + " p_fileName: " + p_fileName);
        }
        Boolean isBatchUserPasswdModifyAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.BATCH_USER_PASSWD_MODIFY_ALLOWED);
        Boolean isLmsAppl = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
        String pinpasEnDeCryptionType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE);
        String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        Boolean isLoginPasswordAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LOGIN_PASSWORD_ALLOWED);
        Boolean isMNPAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MNP_ALLOWED);
        Boolean isUserVoucherTypeAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED);
        Boolean isEmailServiceAllow = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW);
        final ArrayList errorList = new ArrayList();
        ListValueVO errorVO = null;
        int commitCounter = 0;
        int index = 0;

        // Assign phone validation & mobile number validation unique
        final StringBuffer selectUserPhones = new StringBuffer("SELECT 1 from user_phones UP,users U ");
        selectUserPhones.append(" WHERE UP.msisdn = ? AND UP.user_id = U.user_id  AND U.status <> 'N' AND U.status <> 'C' AND U.user_id !=? ");
        // "AND primary_number='Y' ");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "selectUserPhones Query =" + selectUserPhones);
        }
        PreparedStatement pstmtSelectUserPhones = null;
        ResultSet rsUserPhones = null;

        // Unique check for login id
        final StringBuffer selectLoginID = new StringBuffer("SELECT 1 FROM users U WHERE U.login_id=? AND U.user_id !=? ");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "selectLoginID Query =" + selectLoginID);
        }
        PreparedStatement pstmtSelectLoginID = null;
        ResultSet rsSelectLoginID = null;

        final StringBuffer updateInToUser = new StringBuffer("UPDATE users SET user_name=?,user_name_prefix=?,login_id=?, ");

        if (isBatchUserPasswdModifyAllowed) {
            updateInToUser.append("password=?,");
        }

        updateInToUser.append("short_name=?,employee_code=?, ");
        updateInToUser.append("external_code=?,contact_person=?,contact_no=?, ");
        updateInToUser.append("ssn=?,designation=?,address1=?,address2=?,city=?,state=?,country=?,company=?,fax=?,email=?,firstname=?,lastname=?, ");// company,fax,fname,lname
        // added
        // by
        // deepika
        // aggarwal
        updateInToUser.append("modified_on=?, modified_by=?, rsaflag=?,authentication_allowed=?, ");
        updateInToUser.append("msisdn=?,user_code=? ");
        updateInToUser.append(" ,LONGITUDE=?, LATITUDE=?, document_type=?, document_no=?, payment_type=? ");
        updateInToUser.append(" WHERE user_id=?");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "updateInToUser Query =" + updateInToUser);
        }
        PreparedStatement pstmtupdateInToUser = null;

        final StringBuffer selectExternalCode = new StringBuffer("SELECT 1 FROM users WHERE external_code= ? AND user_id !=? AND status <> 'N' AND status <> 'C'");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "selectExternalCode Query =" + selectExternalCode);
        }
        PreparedStatement pstmtSelectExternalCode = null;
        ResultSet rsExternalCode = null;

        final StringBuffer deleteFromUserRoles = new StringBuffer("DELETE FROM user_roles WHERE user_id=?");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "deleteFromUserRoles Query =" + deleteFromUserRoles);
        }
        PreparedStatement pstmtDeleteFromUserRoles = null;

        final StringBuffer insertInUserRoles = new StringBuffer("INSERT INTO user_roles (user_id,role_code)VALUES (?,?) ");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "insertInUserRoles Query =" + insertInUserRoles);
        }
        PreparedStatement pstmtInsertInUserRoles = null;

        final StringBuffer deleteFromUserServices = new StringBuffer("DELETE FROM user_services WHERE user_id=?");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "deleteFromUserServices Query =" + deleteFromUserServices);
        }
        PreparedStatement pstmtDeleteFromUserServices = null;

        final StringBuffer insertInUserServices = new StringBuffer("INSERT INTO user_services(user_id,service_type ) VALUES (?,?)");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "insertInUserServices Query =" + insertInUserServices);
        }
        PreparedStatement pstmtInsertInUserServices = null;

        //Voucher Type
        final StringBuffer deleteFromUserVoucherTypes = new StringBuffer("DELETE FROM user_vouchertypes WHERE user_id=?");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "deleteFromUserVoucherTypes Query =" + deleteFromUserVoucherTypes);
        }
        PreparedStatement pstmtDeleteFromUserVoucherTypes = null;

        final StringBuffer insertInUserVoucherTypes = new StringBuffer("INSERT INTO user_vouchertypes(user_id,voucher_type ) VALUES (?,?)");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "insertInUserVoucherTypes Query =" + insertInUserVoucherTypes);
        }
        PreparedStatement pstmtInsertInUserVoucherTypes = null;
        
        
        
        final StringBuffer deleteFromUserVoucherSegments = new StringBuffer("DELETE FROM USER_VOUCHER_SEGMENTS WHERE user_id=?");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "deleteFromUserVoucherSegments Query =" + deleteFromUserVoucherSegments);
        }
        PreparedStatement pstmtDeleteFromUserVoucherSegments = null;

        final StringBuffer insertInUserVoucherSegments = new StringBuffer("INSERT INTO USER_VOUCHER_SEGMENTS(user_id,voucher_segment ) VALUES (?,?)");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "insertInUserVoucherSegments Query =" + insertInUserVoucherSegments);
        }
        PreparedStatement pstmtInsertInUserVoucherSegments = null;
        
        
        final StringBuffer deleteFromUserGeography = new StringBuffer("DELETE FROM user_geographies WHERE user_id=?");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "deleteFromUserGeography Query =" + deleteFromUserGeography);
        }
        PreparedStatement pstmtDeleteFromUserGeography = null;

        final StringBuffer insertInUserGeography = new StringBuffer("INSERT INTO user_geographies(user_id,grph_domain_code) VALUES (?,?)");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "insertInUserGeography Query =" + insertInUserGeography);
        }
        PreparedStatement pstmtInsertInUserGeography = null;

        final StringBuffer updateInToChanelUsers = new StringBuffer("UPDATE channel_users SET contact_person=?, ");
        updateInToChanelUsers.append("in_suspend=?,out_suspend=?,outlet_code=?,suboutlet_code=?, ");
        // updateInToChanelUsers.append("in_suspend=?,out_suspend=?, ");

        // for Zebra and Tango by Sanjeew date 11/07/07
        updateInToChanelUsers.append("low_bal_alert_allow=? ");
        // End Zebra and Tango
        // added by shashank for trf
        updateInToChanelUsers.append(", trf_rule_type=? ");
        // end
        if (isLmsAppl) {
            updateInToChanelUsers.append(", lms_profile=? ");
            updateInToChanelUsers.append(", lms_profile_updated_on=? ");
        }
        updateInToChanelUsers.append(", COMM_PROFILE_SET_ID = ? ");
        updateInToChanelUsers.append(", TRANSFER_PROFILE_ID = ? ");
        updateInToChanelUsers.append(", USER_GRADE = ? ");
        updateInToChanelUsers.append("WHERE user_id=?");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "updateInToChanelUsers Query =" + updateInToChanelUsers);
        }
        PreparedStatement pstmtupdateInToChanelUsers = null;

        // StringBuffer updateInToUserPhones = new
        // StringBuffer("UPDATE user_phones SET msisdn=?, ");
        final StringBuffer updateInToUserPhones = new StringBuffer("UPDATE user_phones SET primary_number=?, ");
        if (!"SHA".equalsIgnoreCase(pinpasEnDeCryptionType))
        updateInToUserPhones.append("sms_pin=?, ");
       
        updateInToUserPhones.append("modified_by=?,modified_on=?,phone_language=?,country=?  ");// phone_language,country
        // added
        // by
        // deepika
        // aggarwal
        // updateInToUserPhones.append(" WHERE user_id=? AND primary_number='Y' ");
        updateInToUserPhones.append(" WHERE user_id=? AND msisdn=? ");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "updateInToUserPhones Query =" + updateInToUserPhones);
        }
        PreparedStatement pstmtupdateInToUserPhones = null;

        final StringBuffer selectPortedMSISDN = new StringBuffer("SELECT 1 FROM ported_msisdn WHERE msisdn=? AND port_type=? ");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "selectPortedMSISDN Query =" + selectPortedMSISDN);
        }
        PreparedStatement pstmtPortedMSISDN = null;
        ResultSet rsPortedMSISDN = null;

        // added to update SIMTxnID of the MSISDN in user_phones : Zafar Abbas
        final StringBuffer selectPhoneExists = new StringBuffer("SELECT 1 from user_phones UP,users U ");
        selectPhoneExists.append(" WHERE UP.msisdn = ? AND U.user_id = ? AND UP.user_id = U.user_id  ");
        selectPhoneExists.append(" AND U.status <> 'N' AND U.status <> 'C'");
        if (log.isDebugEnabled()) {
            log.debug("updateInsertDeleteUserPhoneList", "Query selectPhoneExistsQuery:" + selectPhoneExists);
        }
        PreparedStatement psmtSelectPhone = null;
        ResultSet rsSelectPhone = null;

        final String updateSIMTxnIDQuery = "UPDATE user_phones SET temp_transfer_id =? WHERE msisdn=? ";
        if (log.isDebugEnabled()) {
            log.debug(" updateTransactionId ", " Query :: " + updateSIMTxnIDQuery);
        }
        PreparedStatement psmtSIMTxnID = null;
        // new addition ended here
        final StringBuffer getOldLoginID = new StringBuffer("SELECT U.user_name,U.login_id,U.external_code, U.creation_type,U.status FROM users U WHERE  U.user_id = ? ");
        if (log.isDebugEnabled()) {
            log.debug(" modifyChannelUserList ", " getOldLoginID Query :: " + getOldLoginID.toString());
        }
        PreparedStatement pstmtSelectOldLoinID = null;
        ResultSet rsOldLoginID = null;

        final StringBuffer insertInUserPhones = new StringBuffer("INSERT INTO user_phones (user_phones_id,msisdn,user_id,");
        insertInUserPhones.append("primary_number,sms_pin,pin_required,");
        insertInUserPhones.append("phone_profile,phone_language,country,pin_modified_on,");
        insertInUserPhones.append("created_by,created_on,modified_by,modified_on, prefix_id) values ");
        insertInUserPhones.append("(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "insertInUserPhones Query =" + insertInUserPhones);
        }
        PreparedStatement pstmtInsertInUserPhones = null;

        final StringBuffer deleteFromUserPhones = new StringBuffer("DELETE FROM user_phones WHERE user_id =? AND msisdn=?");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "deleteFromUserPhones Query =" + deleteFromUserPhones);
        }
        PreparedStatement pstmtDeleteFromUserPhones = null;

        final StringBuffer selectAllUserPhones = new StringBuffer("SELECT user_phones_id,msisdn FROM user_phones WHERE user_id=?");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "selectAllUserPhones Query =" + selectAllUserPhones);
        }
        PreparedStatement pstmtSelectAllFromUserPhones = null;
        ResultSet rsAllUserPhones = null;
        
        
        final StringBuffer updateInToChanelUsersLoanInfo = new StringBuffer("UPDATE channel_user_loan_info SET profile_id=? ");
        updateInToChanelUsersLoanInfo.append(",modified_by=?,modified_on=? ");
        updateInToChanelUsersLoanInfo.append(" where user_id=?");
		
		
		if (log.isDebugEnabled()) {
            log.debug(methodName, "updateInToChanelUsersLoanInfo Query =" + updateInToChanelUsersLoanInfo);
        }
        PreparedStatement pstmtupdateInToChanelUsersLoanInfo = null;
        
        final StringBuffer insertInChanelUsersLoanInfo = new StringBuffer("insert into channel_user_loan_info (");
        insertInChanelUsersLoanInfo.append("user_id,profile_id, product_code,loan_threhold,loan_amount,created_on , ");
        insertInChanelUsersLoanInfo.append(" created_by,modified_on ,modified_by) values ");
        insertInChanelUsersLoanInfo.append(" (?,?,?,?,?,?,?,?,?)");
        
        if (log.isDebugEnabled()) {
            log.debug(methodName, "insertInChanelUsersLoanInfo Query =" + insertInChanelUsersLoanInfo);
        }
        
        PreparedStatement pstmtInsertInChanelUsersLoanInfo = null;
        

        try {
            pstmtSelectOldLoinID = p_con.prepareStatement(getOldLoginID.toString());
            pstmtSelectUserPhones = p_con.prepareStatement(selectUserPhones.toString());
            pstmtSelectLoginID = p_con.prepareStatement(selectLoginID.toString());
            pstmtupdateInToUser = p_con.prepareStatement(updateInToUser.toString());
            pstmtSelectExternalCode = p_con.prepareStatement(selectExternalCode.toString());
            pstmtDeleteFromUserRoles = p_con.prepareStatement(deleteFromUserRoles.toString());
            pstmtInsertInUserRoles = p_con.prepareStatement(insertInUserRoles.toString());
            pstmtDeleteFromUserServices = p_con.prepareStatement(deleteFromUserServices.toString());
            pstmtInsertInUserServices = p_con.prepareStatement(insertInUserServices.toString());
            
            pstmtDeleteFromUserVoucherTypes = p_con.prepareStatement(deleteFromUserVoucherTypes.toString());
            pstmtInsertInUserVoucherTypes = p_con.prepareStatement(insertInUserVoucherTypes.toString());
            pstmtInsertInUserVoucherSegments = p_con.prepareStatement(insertInUserVoucherSegments.toString());
            
            pstmtDeleteFromUserGeography = p_con.prepareStatement(deleteFromUserGeography.toString());
            pstmtInsertInUserGeography = p_con.prepareStatement(insertInUserGeography.toString());
            pstmtupdateInToChanelUsers = p_con.prepareStatement(updateInToChanelUsers.toString());
            pstmtupdateInToUserPhones = p_con.prepareStatement(updateInToUserPhones.toString());
            pstmtPortedMSISDN = p_con.prepareStatement(selectPortedMSISDN.toString());
            psmtSIMTxnID = p_con.prepareStatement(updateSIMTxnIDQuery.toString());
            psmtSelectPhone = p_con.prepareStatement(selectPhoneExists.toString());
            pstmtInsertInUserPhones = p_con.prepareStatement(insertInUserPhones.toString());
            pstmtDeleteFromUserPhones = p_con.prepareStatement(deleteFromUserPhones.toString());
            pstmtSelectAllFromUserPhones = p_con.prepareStatement(selectAllUserPhones.toString());
            
            pstmtupdateInToChanelUsersLoanInfo = p_con.prepareStatement(updateInToChanelUsersLoanInfo.toString());
            pstmtInsertInChanelUsersLoanInfo = p_con.prepareStatement(insertInChanelUsersLoanInfo.toString());
            
            
            ChannelUserVO channelUserVO = null;
            CategoryVO categoryVO = null;
            int commitNumber = 0;
            // Email for pin & password
            BTSLMessages btslPushMessage = null;
            final Locale defaultLocale = new Locale(defaultLanguage, defaultCountry);
            PushMessage pushMessage = null;
            boolean loginIDChange = false;
            String subject = null;
            EmailSendToUser emailSendToUser = null;

            try {
                commitNumber = Integer.parseInt(Constants.getProperty("BATCH_USER_COMMIT_NUMBER"));
            } catch (Exception e) {
                commitNumber = 100;
                log.error(methodName, "Exception:e=" + e);
                log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[addChannelUserList]", "", "", "",
                    "Exception:" + e.getMessage());
            }
            NetworkPrefixVO networkPrefixVO = null;
            String filterMsisdn = null;
            boolean[] phoneExists = null;
            ArrayList msisdnList = null;
            UserPhoneVO userPhoneVO = null;
            int switchCase = 0;
            for (int i = 0, length = p_userDetailList.size(); i < length; i++) {
                categoryVO = null;
                channelUserVO = (ChannelUserVO) p_userDetailList.get(i);
                categoryVO = channelUserVO.getCategoryVO();
                if (PretupsI.YES.equals(categoryVO.getSmsInterfaceAllowed())) {
                    msisdnList = channelUserVO.getMsisdnList();
                    // first get old login id for push message related to login
                    // id
                }
                if (categoryVO.getWebInterfaceAllowed().equals(PretupsI.YES)){
                pstmtSelectOldLoinID.setString(1, channelUserVO.getUserID());
                rsOldLoginID = pstmtSelectOldLoinID.executeQuery();
				//Gaurav
                if(rsOldLoginID.next())
                {
                if(!rsOldLoginID.getString("login_id").equals(channelUserVO.getLoginID()))
                {
                	loginIDChange=true;
                }
                channelUserVO.setStatus(rsOldLoginID.getString("status"));
                }
                }
                if (categoryVO.getWebInterfaceAllowed().equals(PretupsI.YES) && isLoginPasswordAllowed) {
                    // ************Validation 2: Check for login_id uniqueness,
                    // if login id already exists mark error.
                    pstmtSelectLoginID.clearParameters();
                    pstmtSelectLoginID.setString(1, channelUserVO.getLoginID());
                    pstmtSelectLoginID.setString(2, channelUserVO.getUserID());
                    rsSelectLoginID = pstmtSelectLoginID.executeQuery();
                    if (rsSelectLoginID.next()) {
                        errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale, "bulkuser.initiatebulkuser.msg.error.loginiduniqueerr",
                            new String[] { channelUserVO.getLoginID() }));
                        errorList.add(errorVO);
                        BatchesLog.log("BULKUSRUPDATE", channelUserVO, null, "Fail :=" + p_messages.getMessage("bulkuser.initiatebulkuser.msg.error.loginiduniqueerr",
                            channelUserVO.getLoginID()));
                        continue;
                    }
                }
                if (PretupsI.YES.equals(categoryVO.getSmsInterfaceAllowed())) {
                    // **********Validation 4: Check for the uniqueness of the
                    // msisdn
                    phoneExists = new boolean[msisdnList.size()];
                    boolean exitLoop = false;// this flag is used to exist user
                    // loop
                    final StringBuffer invalidMsisdn = new StringBuffer();
                    switchCase = 0;
                    for (int j = 0, msisdnSize = msisdnList.size(); j < msisdnSize; j++) {
                        userPhoneVO = (UserPhoneVO) msisdnList.get(j);
                        pstmtSelectUserPhones.clearParameters();
                        pstmtSelectUserPhones.setString(1, userPhoneVO.getMsisdn());
                        pstmtSelectUserPhones.setString(2, channelUserVO.getUserID());
                        rsUserPhones = pstmtSelectUserPhones.executeQuery();
                        // need to the primary msisdn of the channel user,
                        // primary will be the first msisdn.
                        if (rsUserPhones.next()) {
                            invalidMsisdn.append(userPhoneVO.getMsisdn());
                            invalidMsisdn.append(",");
                            switchCase = 1;
                            BatchesLog.log("BULKUSRUPDATE", channelUserVO, null, "Fail :=" + p_messages.getMessage(
                                "bulkuser.initiatebulkuser.msg.error.msisdnalreadyassigned", new String[] { userPhoneVO.getMsisdn() }));
                            exitLoop = true;
                            continue;
                        }
                        // ***********validation 4.1 : Check for the association
                        // of MSISDN with the user
                        // BY : Zafar Abbas , ON : 14/05/2007
                        psmtSelectPhone.setString(1, userPhoneVO.getMsisdn());
                        psmtSelectPhone.setString(2, channelUserVO.getUserID());
                        rsSelectPhone = psmtSelectPhone.executeQuery();
                        if (rsSelectPhone.next()) {
                            phoneExists[j] = true;
                            userPhoneVO.setOperationType("U");
                        } else {
                            phoneExists[j] = false;
                            userPhoneVO.setOperationType("I");
                        }
                        // new addition ended here
                        /*
                         * 26/04/07 Code Added for MNP
                         * Preference to check whether MNP is allowed in system
                         * or not.
                         * If yes then check whether Number has not been ported
                         * out, If yes then throw error, else continue
                         */
                        if (isMNPAllowed) {
                            filterMsisdn = userPhoneVO.getMsisdn();
                            networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(filterMsisdn));
                            if (networkPrefixVO.getOperator().equals(PretupsI.OPERATOR_TYPE_PORT)) {
                                pstmtPortedMSISDN.setString(1, filterMsisdn);
                                pstmtPortedMSISDN.setString(2, PretupsI.PORTED_IN);
                                rsPortedMSISDN = pstmtPortedMSISDN.executeQuery();
                                if (!rsPortedMSISDN.next()) {
                                    invalidMsisdn.append(userPhoneVO.getMsisdn());
                                    invalidMsisdn.append(",");
                                    BatchesLog.log("BULKUSRUPDATE", channelUserVO, null, "Fail :=" + p_messages.getMessage(
                                        "bulkuser.processuploadedfile.error.nonetworkprefixfound", new String[] { userPhoneVO.getMsisdn() }));
                                    exitLoop = true;
                                    continue;
                                }
                            } else {
                                pstmtPortedMSISDN.setString(1, filterMsisdn);
                                pstmtPortedMSISDN.setString(2, PretupsI.PORTED_OUT);
                                rsPortedMSISDN = pstmtPortedMSISDN.executeQuery();
                                if (rsPortedMSISDN.next()) {
                                    invalidMsisdn.append(userPhoneVO.getMsisdn());
                                    invalidMsisdn.append(",");
                                    BatchesLog.log("BULKUSRUPDATE", channelUserVO, null, "Fail :=" + p_messages.getMessage(
                                        "bulkuser.processuploadedfile.error.nonetworkprefixfound", new String[] { userPhoneVO.getMsisdn() }));
                                    exitLoop = true;
                                    continue;
                                }
                            }
                            pstmtPortedMSISDN.clearParameters();
                        }
                        // 26/04/07: MNP Code End
                    }
                    // if any of the specified msisdn for user, has problem,
                    // this whole user should be skipped.
                    if (exitLoop) {
                        if (switchCase == 1) {
                            errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale,
                                "bulkuser.initiatebulkuser.msg.error.msisdnalreadyassigned", new String[] { invalidMsisdn.toString().substring(0,
                                    invalidMsisdn.toString().length() - 1) }));
                            errorList.add(errorVO);
                        } else {
                            errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale,
                                "bulkuser.processuploadedfile.error.nonetworkprefixfound", new String[] { invalidMsisdn.toString().substring(0,
                                    invalidMsisdn.toString().length() - 1) }));
                            errorList.add(errorVO);
                        }
                        continue;
                    }

                }

                // **********Validation 5: Check for the uniqueness of the
                // External code (if given)
                if (!BTSLUtil.isNullString(channelUserVO.getExternalCode())) {
                    pstmtSelectExternalCode.clearParameters();
                    pstmtSelectExternalCode.setString(1, channelUserVO.getExternalCode());
                    pstmtSelectExternalCode.setString(2, channelUserVO.getUserID());
                    rsExternalCode = pstmtSelectExternalCode.executeQuery();
                    if (rsExternalCode.next()) {
                        errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale,
                            "bulkuser.initiatebulkuser.msg.error.externalcodeuniqueerr", new String[] { channelUserVO.getExternalCode() }));
                        errorList.add(errorVO);
                        BatchesLog.log("BULKUSRUPDATE", channelUserVO, null, "Fail :=" + p_messages.getMessage("bulkuser.initiatebulkuser.msg.error.externalcodeuniqueerr",
                            channelUserVO.getExternalCode()));
                        continue;
                    }
                }
                // ***After all validation insertion starts
                // here********************************************
                // Update into users table.

                pstmtupdateInToUser.clearParameters();
                if (log.isDebugEnabled()) {
                    log.debug(methodName, "updateInToUser Query =" + updateInToUser);
                }
                index = 0;
                // pstmtupdateInToUser.setFormOfUse(++index,
                // OraclePreparedStatement.FORM_NCHAR);
                ++index;
                pstmtupdateInToUser.setString(index, channelUserVO.getUserName());
                ++index;
                pstmtupdateInToUser.setString(index, channelUserVO.getUserNamePrefix());
                ++index;
                pstmtupdateInToUser.setString(index, channelUserVO.getLoginID());
                if (isBatchUserPasswdModifyAllowed) {
                    ++index;
                    pstmtupdateInToUser.setString(index, channelUserVO.getPassword());
                } // pstmtupdateInToUser.setFormOfUse(++index,
                  // OraclePreparedStatement.FORM_NCHAR);
                ++index;
                pstmtupdateInToUser.setString(index, channelUserVO.getShortName());
                ++index;
                pstmtupdateInToUser.setString(index, channelUserVO.getEmpCode());
                ++index;
                pstmtupdateInToUser.setString(index, channelUserVO.getExternalCode());
                // pstmtupdateInToUser.setString(++index,channelUserVO.getInSuspend());
                // pstmtupdateInToUser.setString(++index,channelUserVO.getOutSuspened());
                ++index;
                pstmtupdateInToUser.setString(index, channelUserVO.getContactPerson());
                ++index;
                pstmtupdateInToUser.setString(index, channelUserVO.getContactNo());
                ++index;
                pstmtupdateInToUser.setString(index, channelUserVO.getSsn());
                // pstmtupdateInToUser.setFormOfUse(++index,
                // OraclePreparedStatement.FORM_NCHAR);
                ++index;
                pstmtupdateInToUser.setString(index, channelUserVO.getDesignation());
                // pstmtupdateInToUser.setFormOfUse(++index,
                // OraclePreparedStatement.FORM_NCHAR);
                ++index;
                pstmtupdateInToUser.setString(index, channelUserVO.getAddress1());
                // pstmtupdateInToUser.setFormOfUse(++index,
                // OraclePreparedStatement.FORM_NCHAR);
                ++index;
                pstmtupdateInToUser.setString(index, channelUserVO.getAddress2());
                // pstmtupdateInToUser.setFormOfUse(++index,
                // OraclePreparedStatement.FORM_NCHAR);
                ++index;
                pstmtupdateInToUser.setString(index, channelUserVO.getCity());
                // pstmtupdateInToUser.setFormOfUse(++index,
                // OraclePreparedStatement.FORM_NCHAR);
                ++index;
                pstmtupdateInToUser.setString(index, channelUserVO.getState());
                // pstmtupdateInToUser.setFormOfUse(++index,
                // OraclePreparedStatement.FORM_NCHAR);
                ++index;
                pstmtupdateInToUser.setString(index, channelUserVO.getCountry());
                // added by deepika aggarwal
                // pstmtupdateInToUser.setFormOfUse(++index,
                // OraclePreparedStatement.FORM_NCHAR);
                ++index;
                pstmtupdateInToUser.setString(index, channelUserVO.getCompany());
                // pstmtupdateInToUser.setFormOfUse(++index,
                // OraclePreparedStatement.FORM_NCHAR);
                ++index;
                pstmtupdateInToUser.setString(index, channelUserVO.getFax());
                ++index;
                pstmtupdateInToUser.setString(index, channelUserVO.getEmail());
                ++index;
                pstmtupdateInToUser.setString(index, channelUserVO.getFirstName());
                ++index;
                pstmtupdateInToUser.setString(index, channelUserVO.getLastName());
                // end
                // pstmtupdateInToUser.setString(++index,channelUserVO.getEmail());
                ++index;
                pstmtupdateInToUser.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getModifiedOn()));
                ++index;
                pstmtupdateInToUser.setString(index, channelUserVO.getModifiedBy());
                // added by shashank for rsa authentication
                ++index;
                pstmtupdateInToUser.setString(index, channelUserVO.getRsaFlag());
                // end
                // Added for OTP Authentication
                if (BTSLUtil.isNullString(channelUserVO.getAuthTypeAllowed())) {
                    ++index;
                    pstmtupdateInToUser.setString(index, PretupsI.NO);
                } else {
                    ++index;
                    pstmtupdateInToUser.setString(index, channelUserVO.getAuthTypeAllowed());
                }// pstmtupdateInToUser.setString(++index,
                 // channelUserVO.getAuthTypeAllowed());
                ++index;
                pstmtupdateInToUser.setString(index, channelUserVO.getMsisdn());
                ++index;
                pstmtupdateInToUser.setString(index, channelUserVO.getUserCode());
                // pstmtupdateInToUser.setString(++index,channelUserVO.getContactPerson());
                // Added by Diwakar for updating Longitede & Latitude
                ++index;
                pstmtupdateInToUser.setString(index, channelUserVO.getLongitude());
                ++index;
                pstmtupdateInToUser.setString(index, channelUserVO.getLatitude());
                ++index;
                pstmtupdateInToUser.setString(index, channelUserVO.getDocumentType());
                ++index;
                pstmtupdateInToUser.setString(index, channelUserVO.getDocumentNo());
                ++index;
                pstmtupdateInToUser.setString(index, channelUserVO.getPaymentType());
                ++index;
                pstmtupdateInToUser.setString(index, channelUserVO.getUserID());
                if (pstmtupdateInToUser.executeUpdate() <= 0) {
                    errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale, "bulkuser.initiatebulkuser.msg.error.updateusertable",
                        new String[] { channelUserVO.getExternalCode() }));
                    errorList.add(errorVO);
                    p_con.rollback();
                    BatchesLog.log("BULKUSRUPDATE", channelUserVO, null, "Fail :=When inserting users table");
                    continue;
                }
                if (categoryVO.getWebInterfaceAllowed().equals(PretupsI.YES)) {
                    // Delete into User Roles table
                    pstmtDeleteFromUserRoles.clearParameters();
                    pstmtDeleteFromUserRoles.setString(1, channelUserVO.getUserID());
                    pstmtDeleteFromUserRoles.executeUpdate();
                    if ((channelUserVO.getGroupRoleFlag()).equals(PretupsI.NO)) // Group
                    // role
                    // flag=
                    // N
                    {
                        final String[] rolecodeArray = ((channelUserVO.getGroupRoleCode()).trim()).split(",");
                        for (int k = 0, j = rolecodeArray.length; k < j; k++) {
                            // insert into User Roles table
                            pstmtInsertInUserRoles.clearParameters();
                            pstmtInsertInUserRoles.setString(1, channelUserVO.getUserID());
                            pstmtInsertInUserRoles.setString(2, rolecodeArray[k].trim());
                            if (channelUserVO.getGroupRoleCode().trim().length() != 0) {
                                if (pstmtInsertInUserRoles.executeUpdate() <= 0) {
                                    errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale,
                                        "bulkuser.initiatebulkuser.msg.error.insertuserrolestable", new String[] { channelUserVO.getExternalCode() }));
                                    errorList.add(errorVO);
                                    p_con.rollback();
                                    BatchesLog.log("BULKUSRUPDATE", channelUserVO, null, "Fail :=When inserting User Roles table");
                                    continue;
                                }
                            }
                        }
                    } else // Group role flag= Y
                    {
                        pstmtInsertInUserRoles.clearParameters();
                        pstmtInsertInUserRoles.setString(1, channelUserVO.getUserID());
                        pstmtInsertInUserRoles.setString(2, channelUserVO.getGroupRoleCode());
                        if (pstmtInsertInUserRoles.executeUpdate() <= 0) {
                            errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale,
                                "bulkuser.initiatebulkuser.msg.error.insertuserrolestable", new String[] { channelUserVO.getExternalCode() }));
                            errorList.add(errorVO);
                            p_con.rollback();
                            BatchesLog.log("BULKUSRUPDATE", channelUserVO, null, "Fail :=When inserting User Roles table");
                            continue;
                        }
                    }

                }
                if (categoryVO.getServiceAllowed().equals(PretupsI.YES)) {
                    // Delete into User Service table
                    pstmtDeleteFromUserServices.clearParameters();
                    pstmtDeleteFromUserServices.setString(1, channelUserVO.getUserID());
                    pstmtDeleteFromUserServices.executeUpdate();
                    final String[] serviceArr = channelUserVO.getServiceTypes().split(",");
                    for (int k = 0, j = serviceArr.length; k < j; k++) {
                        // insert into User Service table
                        pstmtInsertInUserServices.clearParameters();
                        pstmtInsertInUserServices.setString(1, channelUserVO.getUserID());
                        pstmtInsertInUserServices.setString(2, serviceArr[k].trim());
                        if (pstmtInsertInUserServices.executeUpdate() <= 0) {
                            errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale,
                                "bulkuser.initiatebulkuser.msg.error.insertuserservicestable", new String[] { channelUserVO.getExternalCode() }));
                            errorList.add(errorVO);
                            p_con.rollback();
                            BatchesLog.log("BULKUSRUPDATE", channelUserVO, null, "Fail :=When inserting User Service table");
                            continue;
                        }
                    }
                }                                
                //Voucher Type
                if(isUserVoucherTypeAllowed && channelUserVO.getVoucherTypes() != null) {
                    // Delete into User Service table
                	pstmtDeleteFromUserVoucherTypes.clearParameters();
                	pstmtDeleteFromUserVoucherTypes.setString(1, channelUserVO.getUserID());
                	pstmtDeleteFromUserVoucherTypes.executeUpdate();
                	
                    final String[] voucherTypeArr = channelUserVO.getVoucherTypes().split(",");
                    for (int k = 0, j = voucherTypeArr.length; k < j; k++) {
                        // insert into User VoucherTypes table
                    	if(!BTSLUtil.isNullString(voucherTypeArr[k])) {
	                    	pstmtInsertInUserVoucherTypes.clearParameters();
	                    	pstmtInsertInUserVoucherTypes.setString(1, channelUserVO.getUserID());
	                    	pstmtInsertInUserVoucherTypes.setString(2, voucherTypeArr[k].trim());
	                        if (pstmtInsertInUserVoucherTypes.executeUpdate() <= 0) {
	                            errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale,
	                                "bulkuser.initiatebulkuser.msg.error.insertuservouchertypetable", new String[] { channelUserVO.getExternalCode() }));
	                            errorList.add(errorVO);
	                            p_con.rollback();
	                            BatchesLog.log("BULKUSRUPDATE", channelUserVO, null, "Fail :=When inserting User VoucherTypes table");
	                            continue;
	                        }
                    	}
                    }
                }
                
                pstmtDeleteFromUserVoucherSegments.clearParameters();
                pstmtDeleteFromUserVoucherSegments.setString(1, channelUserVO.getUserID());
                pstmtDeleteFromUserVoucherSegments.executeUpdate();
            	
                final String[] voucherSegmentArr = channelUserVO.getSegments().split(",");
                for (int k = 0, j = voucherSegmentArr.length; k < j; k++) {
                    // insert into User VoucherTypes table
                	if(!BTSLUtil.isNullString(voucherSegmentArr[k])) {
                    	pstmtInsertInUserVoucherSegments.clearParameters();
                    	pstmtInsertInUserVoucherSegments.setString(1, channelUserVO.getUserID());
                    	pstmtInsertInUserVoucherSegments.setString(2, voucherSegmentArr[k].trim());
                        if (pstmtInsertInUserVoucherSegments.executeUpdate() <= 0) {
                            errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale,
                                "bulkuser.initiatebulkuser.msg.error.insertuservouchersegmenttable", new String[] { channelUserVO.getExternalCode() }));
                            errorList.add(errorVO);
                            p_con.rollback();
                            BatchesLog.log("BULKUSRUPDATE", channelUserVO, null, "Fail :=When inserting USER_VOUCHER_SEGMENTS table");
                            continue;
                        }
                	}
                }
                
                // Delete into User Geography table
                pstmtDeleteFromUserGeography.clearParameters();
                pstmtDeleteFromUserGeography.setString(1, channelUserVO.getUserID());
                pstmtDeleteFromUserGeography.executeUpdate();
                if (channelUserVO.getGeographicalCode().contains(",")) // more
                // than
                // one
                // geography
                // code
                {
                    final String[] geographyArray = (channelUserVO.getGeographicalCode()).split(",");
                    for (int k = 0, j = geographyArray.length; k < j; k++) {
                        // insert into User Geography table
                        pstmtInsertInUserGeography.clearParameters();
                        pstmtInsertInUserGeography.setString(1, channelUserVO.getUserID());
                        pstmtInsertInUserGeography.setString(2, geographyArray[k].trim());
                        if (pstmtInsertInUserGeography.executeUpdate() <= 0) {
                            errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale,
                                "bulkuser.initiatebulkuser.msg.error.insertusergeographytable", new String[] { channelUserVO.getExternalCode() }));
                            errorList.add(errorVO);
                            p_con.rollback();
                            BatchesLog.log("BULKUSRUPDATE", channelUserVO, null, "Fail :=When inserting User Geography table");
                            continue;
                        }
                    }
                } else {
                    // insert into User Geography table
                    pstmtInsertInUserGeography.clearParameters();
                    pstmtInsertInUserGeography.setString(1, channelUserVO.getUserID());
                    pstmtInsertInUserGeography.setString(2, (channelUserVO.getGeographicalCode()));
                    if (pstmtInsertInUserGeography.executeUpdate() <= 0) {
                        errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale,
                            "bulkuser.initiatebulkuser.msg.error.insertusergeographytable", new String[] { channelUserVO.getExternalCode() }));
                        errorList.add(errorVO);
                        p_con.rollback();
                        BatchesLog.log("BULKUSRUPDATE", channelUserVO, null, "Fail :=When inserting User Geography table");
                        continue;
                    }
                }
                // Update into ChanelUsers table

                pstmtupdateInToChanelUsers.clearParameters();
                index = 0;
                ++index;
                pstmtupdateInToChanelUsers.setString(index, channelUserVO.getContactPerson());
                ++index;
                pstmtupdateInToChanelUsers.setString(index, (channelUserVO.getInSuspend()));
                ++index;
                pstmtupdateInToChanelUsers.setString(index, (channelUserVO.getOutSuspened()));
                if (categoryVO.getOutletsAllowed().equals(PretupsI.YES)) {
                    ++index;
                    pstmtupdateInToChanelUsers.setString(index, (channelUserVO.getOutletCode()));
                    ++index;
                    pstmtupdateInToChanelUsers.setString(index, (channelUserVO.getSubOutletCode()));
                } else {
                    ++index;
                    pstmtupdateInToChanelUsers.setString(index, null);
                    ++index;
                    pstmtupdateInToChanelUsers.setString(index, null);
                }

                // for Zebra and Tango by Sanjeew date 11/07/07
                ++index;
                pstmtupdateInToChanelUsers.setString(index, channelUserVO.getLowBalAlertAllow());
                // End Zebra and Tango
                // added by shashank for trf
                ++index;
                pstmtupdateInToChanelUsers.setString(index, channelUserVO.getTrannferRuleTypeId());
                // end
                if (isLmsAppl) {
                    ++index;
                    pstmtupdateInToChanelUsers.setString(index, channelUserVO.getLmsProfile());
                    ++index;
                    pstmtupdateInToChanelUsers.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(new Date()));
                }
                ++index;
                pstmtupdateInToChanelUsers.setString(index, channelUserVO.getCommissionProfileSetID());
                ++index;
                pstmtupdateInToChanelUsers.setString(index, channelUserVO.getTransferProfileID());
                ++index;
                pstmtupdateInToChanelUsers.setString(index, channelUserVO.getUserGrade());
                ++index;
                pstmtupdateInToChanelUsers.setString(index, channelUserVO.getUserID());
                if (pstmtupdateInToChanelUsers.executeUpdate() <= 0) {
                    errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale,
                        "bulkuser.initiatebulkuser.msg.error.updatechaneluserstable", new String[] { channelUserVO.getExternalCode() }));
                    errorList.add(errorVO);
                    p_con.rollback();
                    BatchesLog.log("BULKUSRUPDATE", channelUserVO, null, "Fail :=When inserting ChannelUsers table");
                    continue;
                }
                if (PretupsI.YES.equals(categoryVO.getSmsInterfaceAllowed())) {
                    // Update into UserPhones table
                    boolean existLoop = false;// used to exist the loop if error
                    // in updating or inserting any
                    // record
                    final HashMap msisdnNotToDelete = new HashMap();
                    final IDGeneratorDAO idGeneratorDAO = new IDGeneratorDAO();
                    long phoneID;
                    pstmtSelectAllFromUserPhones.setString(1, channelUserVO.getUserID());
                    rsAllUserPhones = pstmtSelectAllFromUserPhones.executeQuery();
                    phoneID = idGeneratorDAO.getNextBatchID(p_con, PretupsI.USR_PHONE_ID, PretupsI.ALL, PretupsI.ALL, channelUserVO.getCreatedOn());
                    for (int k = 0, msisdnListSize = msisdnList.size(); k < msisdnListSize; k++) {
                        userPhoneVO = (UserPhoneVO) msisdnList.get(k);
                        int recordCount = 0;
                        if ("U".equalsIgnoreCase(userPhoneVO.getOperationType()))// &&
                        // userPhoneVO.isPinModifyFlag())
                        {
                            msisdnNotToDelete.put(userPhoneVO.getMsisdn(), "");
                            pstmtupdateInToUserPhones.clearParameters();
                            index = 0;
                            if (channelUserVO.getMsisdn().equalsIgnoreCase(userPhoneVO.getMsisdn())) {
                                ++index;
                                pstmtupdateInToUserPhones.setString(index, "Y");
                            } else {
                                ++index;
                                pstmtupdateInToUserPhones.setString(index, "N");
                            }
                            if (!"SHA".equalsIgnoreCase(pinpasEnDeCryptionType)) {
                            	++index;
                            	pstmtupdateInToUserPhones.setString(index, (userPhoneVO.getSmsPin()));
                            }
                            ++index;
                            pstmtupdateInToUserPhones.setString(index, (channelUserVO.getModifiedBy()));
                            ++index;
                            pstmtupdateInToUserPhones.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getModifiedOn()));
                            ++index;
                            pstmtupdateInToUserPhones.setString(index, userPhoneVO.getPhoneLanguage());// added
                            // by
                            // deepika
                            ++index; // aggarwal
                            pstmtupdateInToUserPhones.setString(index, userPhoneVO.getCountry());// added
                            // by
                            // deepika
                            ++index; // aggarwal
                            pstmtupdateInToUserPhones.setString(index, channelUserVO.getUserID());
                            ++index;
                            pstmtupdateInToUserPhones.setString(index, userPhoneVO.getMsisdn());
                            recordCount = pstmtupdateInToUserPhones.executeUpdate();
                        }
                        // if the pin is not modified, so no need to change it,
                        // but need to set as procesed
                        /*
                         * else if
                         * ("U".equalsIgnoreCase(userPhoneVO.getOperationType
                         * ())&& !userPhoneVO.isPinModifyFlag())
                         * {
                         * recordCount=1;
                         * msisdnNotToDelete.put(userPhoneVO.getMsisdn(),"");
                         * }
                         */else if ("I".equalsIgnoreCase(userPhoneVO.getOperationType())) {
                            msisdnNotToDelete.put(userPhoneVO.getMsisdn(), "");
                            pstmtInsertInUserPhones.clearParameters();
                            index = 0;
                            ++index;
                            pstmtInsertInUserPhones.setLong(index, phoneID);
                            ++index;
                            pstmtInsertInUserPhones.setString(index, userPhoneVO.getMsisdn());
                            ++index;
                            pstmtInsertInUserPhones.setString(index, channelUserVO.getUserID());
                            if (channelUserVO.getMsisdn().equalsIgnoreCase(userPhoneVO.getMsisdn())) {
                                ++index;
                                pstmtInsertInUserPhones.setString(index, "Y");
                            } else {
                                ++index;
                                pstmtInsertInUserPhones.setString(index, "N");
                            }
                            ++index;
                            pstmtInsertInUserPhones.setString(index, userPhoneVO.getSmsPin());
                            ++index;
                            pstmtInsertInUserPhones.setString(index, PretupsI.YES);
                            ++index;
                            pstmtInsertInUserPhones.setString(index, categoryVO.getCategoryCode());
                            // modified by deepika aggarwal
                            // pstmtInsertInUserPhones.setString(++index,defaultLanguage);
                            ++index;
                            pstmtInsertInUserPhones.setString(index, userPhoneVO.getPhoneLanguage());
                            // pstmtInsertInUserPhones.setString(++index,defaultCountry);
                            ++index;
                            pstmtInsertInUserPhones.setString(index, userPhoneVO.getCountry());
                            // end modified by deepika aggarwal
                            ++index;
                            pstmtInsertInUserPhones.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getModifiedOn()));
                            ++index;
                            pstmtInsertInUserPhones.setString(index, channelUserVO.getModifiedBy());
                            ++index;
                            pstmtInsertInUserPhones.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getModifiedOn()));
                            ++index;
                            pstmtInsertInUserPhones.setString(index, channelUserVO.getModifiedBy());
                            ++index;
                            pstmtInsertInUserPhones.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getModifiedOn()));
                            ++index;
                            pstmtInsertInUserPhones.setString(index, PretupsBL.getMSISDNPrefix((userPhoneVO.getMsisdn())));
                            recordCount = pstmtInsertInUserPhones.executeUpdate();
                            phoneID++;

                        }

                        // Update the temp_transfer_id , if number is new
                        if (log.isDebugEnabled()) {
                            log.debug("updateInsertDeleteUserPhoneList", "phoneExists = " + phoneExists[k]);
                        }
                        if (!phoneExists[k]) {
                            psmtSIMTxnID.clearParameters();
                            psmtSIMTxnID.setString(1, PretupsI.UPD_SIM_TXN_ID);
                            psmtSIMTxnID.setString(2, userPhoneVO.getMsisdn());
                        }
                        if (recordCount <= 0 || (!phoneExists[k] && psmtSIMTxnID.executeUpdate() <= 0)) {
                            errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale,
                                "bulkuser.initiatebulkuser.msg.error.updateuserphonestable", new String[] { channelUserVO.getExternalCode() }));
                            errorList.add(errorVO);
                            p_con.rollback();
                            BatchesLog.log("BULKUSRUPDATE", channelUserVO, null, "Fail :=When inserting UserPhones table");
                            existLoop = true;
                            break;
                        }
                    }

                    // it will skip the current user record as there is problem
                    // in processing
                    if (existLoop) {
                        continue;
                    }

                    while (rsAllUserPhones.next()) {
                        final String msisdn = rsAllUserPhones.getString("msisdn");
                        if (!msisdnNotToDelete.containsKey(msisdn)) {
                            pstmtDeleteFromUserPhones.setString(1, channelUserVO.getUserID());
                            pstmtDeleteFromUserPhones.setString(2, msisdn);
                            if (pstmtDeleteFromUserPhones.executeUpdate() <= 0) {
                                errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale,
                                    "bulkuser.initiatebulkuser.msg.error.deleteuserphonestable", new String[] { channelUserVO.getExternalCode() }));
                                errorList.add(errorVO);
                                p_con.rollback();
                                BatchesLog.log("BULKUSRUPDATE", channelUserVO, null, "Fail :=When deleting UserPhones table");
                                existLoop = true;
                                break;
                            }
                        }
                    }
                    // skip the user if error in deleting any of msisdn
                    if (existLoop) {
                        continue;
                    }
                    if ("I".equalsIgnoreCase(userPhoneVO.getOperationType())) {
                        idGeneratorDAO.updateNextBatchID(p_con, PretupsI.USR_PHONE_ID, PretupsI.ALL, PretupsI.ALL, channelUserVO.getModifiedOn(), phoneID - 1);
                    }
                }
                commitCounter++;
                if (commitCounter > commitNumber) {
                    // records
                    p_con.commit();
                }

                channelUserVO.setSessionInfoVO(new SessionInfoVO());
                // Email for pin & password
                final String tmpMsisdn = channelUserVO.getMsisdn();
                // to push messages to individual user
                for (int k = 0, j = msisdnList.size(); k < j; k++) {
                    userPhoneVO = (UserPhoneVO) msisdnList.get(k);
                    // only pin modify
                    if (userPhoneVO.isPinModifyFlag() && !loginIDChange && !channelUserVO.isPasswordModifyFlag()) {
                        final String[] arrArray = { BTSLUtil.decryptText(userPhoneVO.getShowSmsPin()), "", userPhoneVO.getMsisdn() };
                        btslPushMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_PIN_MODIFY, arrArray);
                    } else if (!userPhoneVO.isPinModifyFlag() && channelUserVO.isPasswordModifyFlag() && !loginIDChange) {
                        // only password change
                        btslPushMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_PWD_MODIFY,
                            new String[] { BTSLUtil.decryptText(channelUserVO.getPassword()), channelUserVO.getLoginID() });
                    }
                    // web password and primary no pin change
                    else if (channelUserVO.isPasswordModifyFlag() && userPhoneVO.isPinModifyFlag() && !loginIDChange) {
                        final String[] arrArray = { BTSLUtil.decryptText(channelUserVO.getPassword()), BTSLUtil.decryptText(userPhoneVO.getShowSmsPin()), channelUserVO
                            .getLoginID(), userPhoneVO.getMsisdn() };
                        btslPushMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_PWD_AND_PIN_MODIFY, arrArray);
                    }
                    // web loginid and web password and primary no pin change
                    else if (channelUserVO.isPasswordModifyFlag() && userPhoneVO.isPinModifyFlag() && loginIDChange) {
                        final String[] arrArray = { channelUserVO.getLoginID(), BTSLUtil.decryptText(channelUserVO.getPassword()), BTSLUtil.decryptText(userPhoneVO
                            .getShowSmsPin()), userPhoneVO.getMsisdn() };
                        btslPushMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_LOGIN_AND_PWD_AND_PIN_MODIFY, arrArray);
                    }
                    // only login id change
                    else if (!channelUserVO.isPasswordModifyFlag() && !userPhoneVO.isPinModifyFlag() && loginIDChange) {
                        final String[] arrArray = { channelUserVO.getLoginID() };
                        btslPushMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_LOGIN_AND_PWD_MODIFY, arrArray);

                    }
                    // only login id and web password change
                    else if (channelUserVO.isPasswordModifyFlag() && loginIDChange) {
                        final String[] arrArray = { channelUserVO.getLoginID(), BTSLUtil.decryptText(channelUserVO.getPassword()) };
                        btslPushMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_LOGIN_MODIFY, arrArray);

                    }
                    // only login id and pin change
                    else if (!channelUserVO.isPasswordModifyFlag() && userPhoneVO.isPinModifyFlag() && loginIDChange) {
                        final String[] arrArray = { channelUserVO.getLoginID(), BTSLUtil.decryptText(userPhoneVO.getShowSmsPin()), userPhoneVO.getMsisdn() };
                        btslPushMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_LOGIN_AND_PIN_MODIFY, arrArray);
                    }
                    if (btslPushMessage != null) {
                        // Changed for hiding PIN and PWD that are written in
                        // MessageSentLog, BY Manisha(01/12/08)
                        // pushMessage=new
                        // PushMessage(userPhoneVO.getMsisdn(),btslPushMessage,"","",defaultLocale,channelUserVO.getNetworkID());
                        pushMessage = new PushMessage(userPhoneVO.getMsisdn(), btslPushMessage, "", "", defaultLocale, channelUserVO.getNetworkID(),
                            "SMS will be delivered shortly thanks");
                        pushMessage.push();

                        // email change
                        if (isEmailServiceAllow && !BTSLUtil.isNullString(channelUserVO.getEmail())) {
                            subject = p_messages.getMessage(p_locale, "subject.channeluser.update.massage", new String[] { channelUserVO.getUserName() });
                            channelUserVO.setMsisdn(userPhoneVO.getMsisdn());
                            emailSendToUser = new EmailSendToUser(subject, btslPushMessage, p_locale, channelUserVO.getNetworkID(), "Email will be delivered shortly",
                                channelUserVO, channelUserVO);
                            emailSendToUser.sendMail();
                            channelUserVO.setMsisdn(tmpMsisdn);
                        }
                    }
                }
                
                
                
                int pstmtupdateInToChanelUsersLoanInfoCount=0;
                if (SystemPreferences.USERWISE_LOAN_ENABLE) { 
                	pstmtupdateInToChanelUsersLoanInfo.clearParameters();
            		index = 0;
            		pstmtupdateInToChanelUsersLoanInfo.setString(++index, channelUserVO.getLoanProfileId());
            		pstmtupdateInToChanelUsersLoanInfo.setString(++index, PretupsI.SYSTEM);
            		pstmtupdateInToChanelUsersLoanInfo.setTimestamp(++index, BTSLUtil.getTimestampFromUtilDate(new Date()));
            		pstmtupdateInToChanelUsersLoanInfo.setString(++index, channelUserVO.getUserID());
            		pstmtupdateInToChanelUsersLoanInfoCount=pstmtupdateInToChanelUsersLoanInfo.executeUpdate() ;
            			
            		
                }
                if(log.isDebugEnabled())
                {
                	 log.debug(methodName, "Entered: pstmtupdateInToChanelUsersLoanInfoCount=" + pstmtupdateInToChanelUsersLoanInfoCount);
                }
                
                
                // insert into user_loan info
                if (pstmtupdateInToChanelUsersLoanInfoCount<=0 && SystemPreferences.USERWISE_LOAN_ENABLE) { 
                	if(log.isDebugEnabled())
                    {
                    	 log.debug(methodName, "Entered: UPdate Failed Going to Insert" + pstmtupdateInToChanelUsersLoanInfoCount);
                    }
                	if(!BTSLUtil.isNullString(channelUserVO.getLoanProfileId())){
                		Date curdate = new Date();
                		// insert into channel user loan info
                		pstmtInsertInChanelUsersLoanInfo.clearParameters();
                		index = 0;
                		pstmtInsertInChanelUsersLoanInfo.setString(++index, channelUserVO.getUserID());
                		pstmtInsertInChanelUsersLoanInfo.setString(++index, channelUserVO.getLoanProfileId());
                		pstmtInsertInChanelUsersLoanInfo.setString(++index, PretupsI.PRODUCT_ETOPUP);
                		pstmtInsertInChanelUsersLoanInfo.setString(++index, "0");
                		pstmtInsertInChanelUsersLoanInfo.setString(++index, "0");
                		pstmtInsertInChanelUsersLoanInfo.setTimestamp(++index, BTSLUtil.getTimestampFromUtilDate(curdate));
                		pstmtInsertInChanelUsersLoanInfo.setString(++index, PretupsI.SYSTEM);
                		pstmtInsertInChanelUsersLoanInfo.setTimestamp(++index, BTSLUtil.getTimestampFromUtilDate(curdate));
                		pstmtInsertInChanelUsersLoanInfo.setString(++index, PretupsI.SYSTEM);
                		if (pstmtInsertInChanelUsersLoanInfo.executeUpdate() <= 0) {
                			p_con.rollback();
                			BatchesLog.log("BATCHUSERMODIFY", channelUserVO, null, "Fail :=When inserting channel_user_loan_info table");
                			continue;
                		}
                	}
                }
                // insert into channel user loan info

                ChannelUserLog.log("BATCHUSERMODIFY", channelUserVO, channelUserVO, true, "Channel user successfully modify");
				btslPushMessage=null;
				loginIDChange=false;
            }

        } catch (SQLException sqe) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[ModifyChannelUserList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[ModifyChannelUserList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rsUserPhones != null) {
                    rsUserPhones.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsSelectLoginID != null) {
                    rsSelectLoginID.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsExternalCode != null) {
                    rsExternalCode.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsPortedMSISDN != null) {
                    rsPortedMSISDN.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectUserPhones != null) {
                    pstmtSelectUserPhones.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectLoginID != null) {
                    pstmtSelectLoginID.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtupdateInToUser != null) {
                    pstmtupdateInToUser.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtupdateInToChanelUsers != null) {
                    pstmtupdateInToChanelUsers.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtupdateInToUserPhones != null) {
                    pstmtupdateInToUserPhones.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertInUserGeography != null) {
                    pstmtInsertInUserGeography.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtDeleteFromUserGeography != null) {
                    pstmtDeleteFromUserGeography.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertInUserServices != null) {
                    pstmtInsertInUserServices.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtDeleteFromUserServices != null) {
                    pstmtDeleteFromUserServices.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertInUserRoles != null) {
                    pstmtInsertInUserRoles.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtDeleteFromUserRoles != null) {
                    pstmtDeleteFromUserRoles.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectExternalCode != null) {
                    pstmtSelectExternalCode.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtPortedMSISDN != null) {
                    pstmtPortedMSISDN.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (psmtSelectPhone != null) {
                    psmtSelectPhone.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsSelectPhone != null) {
                    rsSelectPhone.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (psmtSIMTxnID != null) {
                    psmtSIMTxnID.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsOldLoginID != null) {
                    rsOldLoginID.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectOldLoinID != null) {
                    pstmtSelectOldLoinID.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertInUserPhones != null) {
                    pstmtInsertInUserPhones.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (rsAllUserPhones != null) {
                    rsAllUserPhones.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectAllFromUserPhones != null) {
                    pstmtSelectAllFromUserPhones.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtDeleteFromUserPhones != null) {
                    pstmtDeleteFromUserPhones.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertInUserVoucherSegments != null) {
                	pstmtInsertInUserVoucherSegments.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtDeleteFromUserVoucherTypes != null) {
                	pstmtDeleteFromUserVoucherTypes.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertInUserVoucherTypes != null) {
                	pstmtInsertInUserVoucherTypes.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            
            try {
                if (pstmtInsertInChanelUsersLoanInfo != null) {
                	pstmtInsertInChanelUsersLoanInfo.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmtupdateInToChanelUsersLoanInfo != null) {
                	pstmtupdateInToChanelUsersLoanInfo.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: errorList size =" + errorList.size());
            }
        }
        return errorList;
    }

    /**
     * Method :rejectUserList
     * 
     * @param p_con
     * @param p_userDetails
     * @param p_messages
     * @param p_locale
     * @param p_userVO
     *            TODO
     * @return
     * @throws BTSLBaseException
     * @author Ved Prakash
     */
    public ArrayList rejectUserList(Connection p_con, ArrayList p_userDetails, MessageResources p_messages, Locale p_locale, UserVO p_userVO) throws BTSLBaseException {
        final String methodName = "rejectUserList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: p_userDetails.size()=" + p_userDetails.size() + " p_messages=" + p_messages + " p_locale=" + p_locale + " p_userVO=" + p_userVO
                .toString());
        }
        PreparedStatement pstmtUpdateUsr = null;
        final StringBuffer strBuffUsr = new StringBuffer(" UPDATE users SET level1_approved_by=?, level1_approved_on=?,");
        strBuffUsr.append(" modified_by=?, modified_on=?, status=?, previous_status=?, remarks=?, login_id=? WHERE user_id=?");
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY strBuffUsr=" + strBuffUsr);
        }

        final ArrayList errorList = new ArrayList();
        try {
            pstmtUpdateUsr = p_con.prepareStatement(strBuffUsr.toString());
            ChannelUserVO channelUserVO = null;
            int updateCountUsr = 0;
            int warnings = 0;
            int updateCount = 0;
            ListValueVO errorVO = null;
            for (int i = 0, j = p_userDetails.size(); i < j; i++) {
                try {
                    updateCountUsr = 0;
                    channelUserVO = (ChannelUserVO) p_userDetails.get(i);
                    pstmtUpdateUsr.clearParameters();
                    pstmtUpdateUsr.setString(1, channelUserVO.getLevel1ApprovedBy());
                    pstmtUpdateUsr.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getLevel1ApprovedOn()));
                    pstmtUpdateUsr.setString(3, channelUserVO.getModifiedBy());
                    pstmtUpdateUsr.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getModifiedOn()));
                    pstmtUpdateUsr.setString(5, channelUserVO.getStatus());
                    pstmtUpdateUsr.setString(6, channelUserVO.getPreviousStatus());
                    pstmtUpdateUsr.setString(7, channelUserVO.getRemarks());
                    pstmtUpdateUsr.setString(8, channelUserVO.getLoginID());
                    pstmtUpdateUsr.setString(9, channelUserVO.getUserID());
                    updateCountUsr = pstmtUpdateUsr.executeUpdate();
                    if (updateCountUsr <= 0) {
                        errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale, "bulkuser.processuploadedfile.error.updateusererr"));
                        errorList.add(errorVO);
                        warnings++;
                        BatchesLog.log("BULKUSRREJ", channelUserVO, null, "Fail :=" + p_messages.getMessage("bulkuser.processuploadedfile.error.updateusererr"));
                        continue;
                    }
                    BatchesLog.log("BULKUSRREJ", channelUserVO, null, "Success");
                    ChannelUserLog.log("BULKUSRREJ", channelUserVO, p_userVO, true, "Success Batch user approval");
                    updateCount++;
                    p_con.commit();
                } catch (SQLException sqe) {
                    try {
                        if (p_con != null) {
                            p_con.rollback();
                        }
                    } catch (Exception e) {
                        log.errorTrace(methodName, e);
                    }
                    log.error(methodName, "SQLException : " + sqe);
                    log.errorTrace(methodName, sqe);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[rejectUserList]", "", "", "",
                        "SQL Exception:" + sqe.getMessage());
                    errorVO = new ListValueVO("ERROR", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale, "error.general.sql.processing"));
                    errorList.add(errorVO);
                    BatchesLog.log("BULKUSRREJ", channelUserVO, null, "Fail");
                    ChannelUserLog.log("BULKUSRREJ", channelUserVO, p_userVO, true, p_messages.getMessage("error.general.sql.processing"));
                    warnings++;
                    continue;
                } catch (Exception ex) {
                    try {
                        if (p_con != null) {
                            p_con.rollback();
                        }
                    } catch (Exception e) {
                        log.errorTrace(methodName, e);
                    }
                    log.error(methodName, "Exception : " + ex);
                    log.errorTrace(methodName, ex);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[rejectUserList]", "", "", "",
                        "Exception:" + ex.getMessage());
                    errorVO = new ListValueVO("ERROR", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale, "error.general.processing"));
                    errorList.add(errorVO);
                    BatchesLog.log("BULKUSRREJ", channelUserVO, null, "Fail");
                    ChannelUserLog.log("BULKUSRREJ", channelUserVO, p_userVO, true, p_messages.getMessage("error.general.processing"));
                    warnings++;
                    continue;
                }
            }
            errorVO = new ListValueVO("UPDATECOUNT", String.valueOf(warnings), String.valueOf(updateCount));
            errorList.add(errorVO);

        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[rejectUserList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[rejectUserList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmtUpdateUsr != null) {
                    pstmtUpdateUsr.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: p_userDetails size =" + p_userDetails.size());
            }
        }
        return errorList;
    }
    
    /**
 * Method :associateChannelUserList
 * This method check the data base validation of initiate channel user 
 * and after validation update into channel user related tables. 
 * @param p_con Connection
 * @param p_userDetailList ArrayList
 * @param p_domainCode String
 * @param p_messages MessageResources
 * @param p_locale Locale
 * @return ArrayList
 * @throws BTSLBaseException
 * @author ankit.agarwal
 */
public ArrayList associateChannelUserList(Connection p_con, ArrayList p_userDetailList, String p_domainCode,  MessageResources p_messages, Locale p_locale, String p_fileName)throws BTSLBaseException
{
	final String methodName = "associateChannelUserList";
	if (log.isDebugEnabled())
	    log.debug(methodName, "Entered: p_userDetailList.size()="+p_userDetailList.size()+" p_domainCode="+p_domainCode+" p_messages="+p_messages+" p_locale="+p_locale+" p_fileName: "+p_fileName);
	ArrayList errorList=new ArrayList();
	ListValueVO errorVO=null;
	int commitCounter=0;
	int index=0;
	String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
	String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
	Boolean isChannelUserMNPAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_USER_MNP_ALLOW);
	//Assign phone validation & mobile number validation unique
	StringBuffer selectUserPhones = new StringBuffer("SELECT 1 from user_phones UP,users U ");
	selectUserPhones.append(" WHERE UP.msisdn = ? AND UP.user_id = U.user_id  AND U.status <> 'N' AND U.status <> 'C' AND U.user_id !=? ");
	
	if (log.isDebugEnabled()) 	log.debug(methodName, "selectUserPhones Query ="+selectUserPhones);
	PreparedStatement pstmtSelectUserPhones = null;
	ResultSet rsUserPhones = null;

	StringBuffer getOldLoginID = new StringBuffer("SELECT U.user_name,U.login_id,U.external_code, U.creation_type FROM users U WHERE  U.user_id = ? ");
	if(log.isDebugEnabled())  log.debug(" modifyChannelUserList "," getOldLoginID Query :: "+getOldLoginID.toString());
	PreparedStatement pstmtSelectOldLoinID = null;
	ResultSet rsOldLoginID = null;
	
	
	StringBuffer updateInToChanelUsers = new StringBuffer("UPDATE channel_users SET user_grade=?,transfer_profile_id=?,comm_profile_set_id=? ");
	
	updateInToChanelUsers.append("WHERE user_id=?");
	if (log.isDebugEnabled()) 	log.debug(methodName, "updateInToChanelUsers Query ="+updateInToChanelUsers);
	PreparedStatement pstmtupdateInToChanelUsers = null;
	
	StringBuffer selectPortedMSISDN = new StringBuffer("SELECT 1 FROM ported_msisdn WHERE msisdn=? AND port_type=? ");
	if (log.isDebugEnabled()) 	log.debug(methodName, "selectPortedMSISDN Query ="+selectPortedMSISDN);
	PreparedStatement pstmtPortedMSISDN = null;
	ResultSet rsPortedMSISDN=null;
	
	//added to update SIMTxnID of the MSISDN in user_phones : Zafar Abbas
	StringBuffer selectPhoneExists = new StringBuffer("SELECT 1 from user_phones UP,users U ");
    selectPhoneExists.append(" WHERE UP.msisdn = ? AND U.user_id = ? AND UP.user_id = U.user_id  ");
    selectPhoneExists.append(" AND U.status <> 'N' AND U.status <> 'C'");
    if (log.isDebugEnabled())  log.debug("updateInsertDeleteUserPhoneList", "Query selectPhoneExistsQuery:" + selectPhoneExists);
    PreparedStatement psmtSelectPhone = null;
    ResultSet rsSelectPhone=null;
	
	//added for service list
    StringBuffer deleteFromUserServices = new StringBuffer("DELETE FROM user_services WHERE user_id=?");
	if (log.isDebugEnabled()) 	log.debug(methodName, "deleteFromUserServices Query ="+deleteFromUserServices);
	PreparedStatement pstmtDeleteFromUserServices = null;
	
	StringBuffer insertInUserServices = new StringBuffer("INSERT INTO user_services(user_id,service_type ) VALUES (?,?)");
	if (log.isDebugEnabled()) 	log.debug(methodName, "insertInUserServices Query ="+insertInUserServices);
	PreparedStatement pstmtInsertInUserServices = null;
    
	try
	{
		pstmtSelectOldLoinID=p_con.prepareStatement(getOldLoginID.toString());
		pstmtSelectUserPhones=p_con.prepareStatement(selectUserPhones.toString());
	    pstmtupdateInToChanelUsers=p_con.prepareStatement(updateInToChanelUsers.toString());
	    pstmtPortedMSISDN=p_con.prepareStatement(selectPortedMSISDN.toString());
	    psmtSelectPhone = p_con.prepareStatement(selectPhoneExists.toString());
		//added for service list
	    pstmtDeleteFromUserServices=p_con.prepareStatement(deleteFromUserServices.toString());
	    pstmtInsertInUserServices=p_con.prepareStatement(insertInUserServices.toString());
	    ChannelUserVO channelUserVO=null;
	    CategoryVO categoryVO=null;
	    int commitNumber=0;
	    BTSLMessages btslPushMessage = null;
	    Locale defaultLocale=new Locale(defaultLanguage,defaultCountry);
	    PushMessage pushMessage=null;
	    boolean loginIDChange=false;
	    String subject=null;
	    EmailSendToUser emailSendToUser=null;
	    
	    try 
	    {
            commitNumber=Integer.parseInt(Constants.getProperty("BATCH_USER_COMMIT_NUMBER"));
        } 
	    catch (Exception e) 
	    {
	        commitNumber=100;
            log.error(methodName,"Exception:e="+e);
            log.errorTrace(methodName,e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"BatchUserWebDAO[addChannelUserList]","","","","Exception:"+e.getMessage());
        }
	    NetworkPrefixVO networkPrefixVO=null;
	    String filterMsisdn=null;
	    boolean [] phoneExists=null;
	    ArrayList msisdnList=null;
	    UserPhoneVO userPhoneVO=null;
	    int switchCase=0;
	    for(int i=0, length=p_userDetailList.size(); i<length; i++)
	    {	    	
	        categoryVO=null;
	        channelUserVO=(ChannelUserVO)p_userDetailList.get(i);
	        categoryVO=channelUserVO.getCategoryVO();
	        if(PretupsI.YES.equals(categoryVO.getSmsInterfaceAllowed()))
	            msisdnList=channelUserVO.getMsisdnList();
	        // first get old login id  for push message related to login id
	        
	        pstmtSelectOldLoinID.setString(1,channelUserVO.getUserID());
	        rsOldLoginID=pstmtSelectOldLoinID.executeQuery();
	       
	        if(PretupsI.YES.equals(categoryVO.getSmsInterfaceAllowed()))
            {
    	        //**********Validation : Check for the uniqueness of the msisdn
	        	phoneExists=new boolean [msisdnList.size()];
	            boolean exitLoop=false;//this flag is used to exist user loop
	            StringBuffer invalidMsisdn=new StringBuffer();
	            switchCase=0;
	            for (int j=0,msisdnSize=msisdnList.size();j<msisdnSize;j++)
	            {
	                userPhoneVO=(UserPhoneVO)msisdnList.get(j);
	                pstmtSelectUserPhones.clearParameters();
	    	        pstmtSelectUserPhones.setString(1,userPhoneVO.getMsisdn());
	    	        pstmtSelectUserPhones.setString(2,channelUserVO.getUserID());
	    	        rsUserPhones=pstmtSelectUserPhones.executeQuery();
	    	        //need to the primary msisdn of the channel user, primary will be the first msisdn.
	    	        if(rsUserPhones.next())
	    	        {
	    	            invalidMsisdn.append(userPhoneVO.getMsisdn());
	    	            invalidMsisdn.append(",");
	    	            switchCase=1;
	    	            BatchesLog.log("BULKUSRUPDATE",channelUserVO,null,"Fail :="+p_messages.getMessage("bulkuser.initiatebulkuser.msg.error.msisdnalreadyassigned",new String[]{userPhoneVO.getMsisdn()}));
	    	            exitLoop=true;
	    	            continue;
	    	        }
	    	        //***********validation : Check for the association of MSISDN with the user 
	    	        //BY : Zafar Abbas , ON : 14/05/2007
	    	        psmtSelectPhone.setString(1,userPhoneVO.getMsisdn());
	                psmtSelectPhone.setString(2,channelUserVO.getUserID());
	                rsSelectPhone = psmtSelectPhone.executeQuery();
	                if(rsSelectPhone.next())
	                {
	                    phoneExists[j]=true;
	                    userPhoneVO.setOperationType("U");
	                }
	                else
	                {
	                    phoneExists[j]=false;
	                    userPhoneVO.setOperationType("I");
	                }
	                //new addition ended here	                
	    	        /* 26/04/07 Code Added for MNP
	    			 * Preference to check whether MNP is allowed in system or not.
	    			 * If yes then check whether Number has not been ported out, If yes then throw error, else continue
	    			 */
					 	filterMsisdn=userPhoneVO.getMsisdn();
	    				// Added by Naveen For Channel user MNP.
						ListValueVO listValueVO=null;
						if(isChannelUserMNPAllowed )
						{						 
							 listValueVO=PretupsBL.validateChannelUserForMNP(PretupsBL.getFilteredMSISDN(filterMsisdn));	
							 if(listValueVO!=null)
								{
								 networkPrefixVO=new NetworkPrefixVO();
								 networkPrefixVO.setNetworkCode(listValueVO.getCodeName());
								 networkPrefixVO.setListValueVO(listValueVO);
								}						 
						}
						
						if(listValueVO==null){
							networkPrefixVO = (NetworkPrefixVO)NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(filterMsisdn));
						}
	    				   	    
	    		/*	if(isMNPAllowed)
	    			{
	    				filterMsisdn=userPhoneVO.getMsisdn();
	    				networkPrefixVO= (NetworkPrefixVO)NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(filterMsisdn));
	    				if(networkPrefixVO.getOperator().equals(PretupsI.OPERATOR_TYPE_PORT))
	    				{
	    					pstmtPortedMSISDN.setString(1, filterMsisdn);
	    					pstmtPortedMSISDN.setString(2, PretupsI.PORTED_IN);
	    					rsPortedMSISDN=pstmtPortedMSISDN.executeQuery();
	    					if(!rsPortedMSISDN.next())
	    					{	    						 
	    	    	            invalidMsisdn.append(userPhoneVO.getMsisdn());
	    	    	            invalidMsisdn.append(",");    	    	             
	    	    	            BatchesLog.log("BULKUSRUPDATE",channelUserVO,null,"Fail :="+p_messages.getMessage("bulkuser.processuploadedfile.error.nonetworkprefixfound",new String[]{userPhoneVO.getMsisdn()}));
	    	    	            exitLoop=true;
	    	    	            continue;
	    					}					
	    				}
	    				else
	    				{
	    					pstmtPortedMSISDN.setString(1, filterMsisdn);
	    					pstmtPortedMSISDN.setString(2, PretupsI.PORTED_OUT);
	    					rsPortedMSISDN=pstmtPortedMSISDN.executeQuery();
	    					if(rsPortedMSISDN.next())
	    					{
	    	    	            invalidMsisdn.append(userPhoneVO.getMsisdn());
	    	    	            invalidMsisdn.append(",");
	    	    	            BatchesLog.log("BULKUSRUPDATE",channelUserVO,null,"Fail :="+p_messages.getMessage("bulkuser.processuploadedfile.error.nonetworkprefixfound",new String[]{userPhoneVO.getMsisdn()}));
	    	    	            exitLoop=true;
	    	    	            continue;
	    					}
	    				}
	    				pstmtPortedMSISDN.clearParameters();
	    			}*/
	    			// 26/04/07: MNP Code End
	            }	            
	            //if any of the specified msisdn for user, has problem, this whole user should be skipped.
    	        if (exitLoop)
    	        {
    	            if(switchCase==1){
    	            errorVO=new ListValueVO("",channelUserVO.getRecordNumber(),p_messages.getMessage(p_locale,"bulkuser.initiatebulkuser.msg.error.msisdnalreadyassigned",new String[]{invalidMsisdn.toString().substring(0,invalidMsisdn.toString().length()-1)}));
    	            errorList.add(errorVO);
    	            }else{
    	                errorVO=new ListValueVO("",channelUserVO.getRecordNumber(),p_messages.getMessage(p_locale,"bulkuser.processuploadedfile.error.nonetworkprefixfound",new String[]{invalidMsisdn.toString().substring(0,invalidMsisdn.toString().length()-1)}));
	    	            errorList.add(errorVO);
    	            } 
    	           continue;
    	        }
    	           
            }	        
	        
	       //added for service list
	        if(categoryVO.getServiceAllowed().equals(PretupsI.YES))
			{
				if(BTSLUtil.isNullString(channelUserVO.getServiceTypes())){
	            //Delete into User Service table
	            pstmtDeleteFromUserServices.clearParameters();
	            pstmtDeleteFromUserServices.setString(1, channelUserVO.getUserID());
	            pstmtDeleteFromUserServices.executeUpdate();
				}
				else{
				pstmtDeleteFromUserServices.clearParameters();
	            pstmtDeleteFromUserServices.setString(1, channelUserVO.getUserID());
	            pstmtDeleteFromUserServices.executeUpdate();
	            String[] serviceArr=channelUserVO.getServiceTypes().split(",");
	            for(int k=0,j=serviceArr.length;k<j;k++)
                {
	                //insert into User Service table
	                pstmtInsertInUserServices.clearParameters();
	                pstmtInsertInUserServices.setString(1, channelUserVO.getUserID());
	                pstmtInsertInUserServices.setString(2, serviceArr[k].trim());
	                if(pstmtInsertInUserServices.executeUpdate()<=0)
	    	        {
	                    errorVO=new ListValueVO("",channelUserVO.getRecordNumber(),p_messages.getMessage(p_locale,"bulkuser.initiatebulkuser.msg.error.insertuserservicestable",new String[]{channelUserVO.getExternalCode()}));
	    	            errorList.add(errorVO);
	                    p_con.rollback();
	    	            BatchesLog.log("BULKUSRUPDATE",channelUserVO,null,"Fail :=When inserting User Service table");
	    	            continue;
	    	        }
                }
				}
			}
			
	        //Update into ChanelUsers table
	        pstmtupdateInToChanelUsers.clearParameters();
	        index=0;
	        
	        if(channelUserVO.getUserGrade().contains(",")) //more than one geography code
            {
	            String[] gradeArray=(channelUserVO.getUserGrade()).split(",");
	            for(int k=0,j=gradeArray.length;k<j;k++)
                {
	               
	            	pstmtupdateInToChanelUsers.setString(1, gradeArray[k].trim());
	            	pstmtupdateInToChanelUsers.setString(2, channelUserVO.getTransferProfileID());
	            	pstmtupdateInToChanelUsers.setString(3, channelUserVO.getCommissionProfileSetID());
	            	pstmtupdateInToChanelUsers.setString(4, channelUserVO.getUserID());
	                if(pstmtupdateInToChanelUsers.executeUpdate()<=0)
	    	        {
	                	errorVO=new ListValueVO("",channelUserVO.getRecordNumber(),p_messages.getMessage(p_locale,"bulkuser.initiatebulkuser.msg.error.updatechaneluserstable",new String[]{channelUserVO.getExternalCode()}));
	                	errorList.add(errorVO);
	    	            p_con.rollback();
	    	            BatchesLog.log("BULKUSRUPDATE",channelUserVO,null,"Fail :=When inserting ChannelUsers table");
	    	            continue;
	    	        }
                }
            }
	        else
	        {
	        	
	        	pstmtupdateInToChanelUsers.setString(1, channelUserVO.getUserGrade());
            	pstmtupdateInToChanelUsers.setString(2, channelUserVO.getTransferProfileID());
            	pstmtupdateInToChanelUsers.setString(3, channelUserVO.getCommissionProfileSetID());
	        	pstmtupdateInToChanelUsers.setString(4, (channelUserVO.getUserID()));
                if(pstmtupdateInToChanelUsers.executeUpdate()<=0)
    	        {
                	errorVO=new ListValueVO("",channelUserVO.getRecordNumber(),p_messages.getMessage(p_locale,"bulkuser.initiatebulkuser.msg.error.updatechaneluserstable",new String[]{channelUserVO.getExternalCode()}));
                	errorList.add(errorVO);
    	            p_con.rollback();
    	            BatchesLog.log("BULKUSRUPDATE",channelUserVO,null,"Fail :=When inserting ChannelUsers table");
    	            continue;
    	        } 
	        }
	        
	        
	        
	        commitCounter++;
	        if(commitCounter>commitNumber)//After 100 record commit the records
	            p_con.commit();

	        channelUserVO.setSessionInfoVO(new SessionInfoVO());
	        
	        
	        ChannelUserLog.log("BATCHUSERMODIFY",channelUserVO,channelUserVO,true,"Channel user successfully modify");
	    }

	} 
	catch (SQLException sqe)
	{
	    try{if (p_con != null){p_con.rollback();}} catch (Exception e){log.errorTrace(methodName,e);}
	    log.error(methodName, "SQLException : " + sqe);
	    log.errorTrace(methodName,sqe);
	    EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"BatchUserWebDAO[ModifyChannelUserList]","","","","SQL Exception:"+sqe.getMessage());
	    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	}
	catch (Exception ex)
	{
	    try{if (p_con != null){p_con.rollback();}} catch (Exception e){log.errorTrace(methodName,e);}
	    log.error(methodName, "Exception : " + ex);
	    log.errorTrace(methodName,ex);
	    EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"BatchUserWebDAO[ModifyChannelUserList]","","","","Exception:"+ex.getMessage());
	    throw new BTSLBaseException(this, methodName, "error.general.processing");
	}
	finally
	{
	    try{if (rsUserPhones != null){rsUserPhones.close();}} catch (Exception e){log.errorTrace(methodName,e);}
	    try{if (rsPortedMSISDN!= null){rsPortedMSISDN.close();}} catch (Exception e){log.errorTrace(methodName,e);}
	    try{if (pstmtSelectUserPhones != null){pstmtSelectUserPhones.close();}} catch (Exception e){log.errorTrace(methodName,e);}
	    try{if (pstmtupdateInToChanelUsers != null){pstmtupdateInToChanelUsers.close();}} catch (Exception e){log.errorTrace(methodName,e);}
	    try{if (pstmtPortedMSISDN != null){pstmtPortedMSISDN.close();}} catch (Exception e){log.errorTrace(methodName,e);}
	    try{if (psmtSelectPhone != null){psmtSelectPhone.close();}} catch (Exception e){log.errorTrace(methodName,e);}
        try{if (rsSelectPhone != null){rsSelectPhone.close();}} catch (Exception e){log.errorTrace(methodName,e);}
        try{if (rsOldLoginID != null){rsOldLoginID.close();}} catch (Exception e){log.errorTrace(methodName,e);}   
		try{if (pstmtSelectOldLoinID != null){pstmtSelectOldLoinID.close();}} catch (Exception e){log.errorTrace(methodName,e);}
		try{
			if (pstmtDeleteFromUserServices != null)
			{
				pstmtDeleteFromUserServices.close();
				}
			} catch (Exception e)
		     {
				log.errorTrace(methodName,e);
				}
		try{
			if (pstmtInsertInUserServices != null)
			{
				pstmtInsertInUserServices.close();
				}
			} catch (Exception e)
		     {
				log.errorTrace(methodName,e);
				}
	    if (log.isDebugEnabled())
	        log.debug(methodName, "Exiting: errorList size =" + errorList.size());
	}
	return errorList;
}

/**
 * Method :addChannelUserList
 * This method check the data base validation of initiate channel user
 * and after validation insert into channel user related tables.
 * 
 * @param p_con
 *            Connection
 * @param p_userDetailList
 *            ArrayList
 * @param p_domainCode
 *            String
 * @param p_locale
 *            Locale
 * @param p_userVO
 *            TODO
 * @return ArrayList
 * @throws BTSLBaseException
 */
public ArrayList addChannelUserList(Connection p_con, ArrayList p_userDetailList, String p_domainCode, Locale p_locale, UserVO p_userVO, String p_fileName,String batchID,boolean insertintoBatches,int total,boolean inbatch) throws BTSLBaseException {
    final String methodName = "addChannelUserList";
    if (log.isDebugEnabled()) {
        log.debug(
            methodName,
            "Entered: p_userDetailList.size()=" + p_userDetailList.size() + " p_domainCode=" + p_domainCode + " p_locale=" + p_locale + " p_fileName: " + p_fileName);
    }
    Boolean isLoginPasswordAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LOGIN_PASSWORD_ALLOWED);
    Boolean isMNPAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MNP_ALLOWED);
    Boolean isUserVoucherTypeAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED);
    final ArrayList errorList = new ArrayList();
    double startTime = System.currentTimeMillis();
    ListValueVO errorVO = null;
    int commitCounter = 0;
    int index = 0;
    boolean flag = true;
    long phoneID = 0;
    final IDGeneratorDAO idGeneratorDAO = new IDGeneratorDAO();
    NetworkPrefixVO prefixVO = null;
    OperatorUtilI operatorUtil = null;
    // Parent details
   startTime=System.currentTimeMillis();
    final StringBuffer selectPtrUsrDetail = new StringBuffer(
        "SELECT U.user_id,U.parent_id,U.owner_id,U.category_code, C.sequence_no, C.domain_code, C.grph_domain_type,UG.grph_domain_code ,U.login_id, UP.Msisdn ");
    selectPtrUsrDetail.append(" FROM users U, user_phones UP,categories C,user_geographies UG ");
    selectPtrUsrDetail.append(" WHERE U.login_id=? AND UP.msisdn=? AND UP.primary_number='Y' AND U.status NOT IN('N','C') AND UG.user_id=U.user_id ");
    selectPtrUsrDetail.append(" AND U.user_id = UP.user_id AND U.category_code = C.category_code ");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "selectPtrUsrDetail Query =" + selectPtrUsrDetail);
    }
    PreparedStatement pstmtSelectParentUserDetails = null;
    ResultSet rsParentUserDetails = null;

    /**
     * StringBuffer selectPtrUsrDetailL = new StringBuffer(
     * "SELECT U.user_id,U.parent_id,U.owner_id,U.category_code, C.sequence_no, C.domain_code, C.grph_domain_type,UG.grph_domain_code,U.login_id, UP.Msisdn "
     * );
     * selectPtrUsrDetailL.append(
     * " FROM users U, categories C,user_geographies UG , user_phones UP ");
     * selectPtrUsrDetailL.append(
     * " WHERE U.login_id=? AND U.status NOT IN('N','C') AND UG.user_id=U.user_id "
     * );
     * selectPtrUsrDetailL.append(
     * " AND U.category_code = C.category_code AND U.user_id=UP.user_id");
     * if (log.isDebugEnabled()) log.debug("addChannelUserList",
     * "selectPtrUsrDetailL Query ="+selectPtrUsrDetailL);
     * PreparedStatement pstmtSelectParentUserDetailsL = null;
     **/

    final StringBuffer selectPtrUsrDetailP = new StringBuffer(
        "SELECT U.user_id,U.parent_id,U.owner_id,U.category_code, C.sequence_no, C.domain_code, C.grph_domain_type,UG.grph_domain_code,U.login_id , UP.Msisdn");
    selectPtrUsrDetailP.append(" FROM users U, user_phones UP,categories C,user_geographies UG ");
    selectPtrUsrDetailP.append(" WHERE UP.msisdn=? AND UP.primary_number='Y' AND U.status NOT IN('N','C') AND UG.user_id=U.user_id ");
    selectPtrUsrDetailP.append(" AND U.user_id = UP.user_id AND U.category_code = C.category_code ");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "selectPtrUsrDetailP Query =" + selectPtrUsrDetailP);
    }
    PreparedStatement pstmtSelectParentUserDetailsP = null;

    
    PreparedStatement pstmtChildGeographyAllowed = null;
    ResultSet rsChildGeographyAllowed = null;

    // Assign phone validation & mobile number validation unique
    final StringBuffer selectUserPhones = new StringBuffer("SELECT 1 from user_phones UP,users U ");
    selectUserPhones.append(" WHERE UP.msisdn = ? AND UP.user_id = U.user_id  AND U.status <> 'N' AND U.status <> 'C'");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "selectUserPhones Query =" + selectUserPhones);
    }
    PreparedStatement pstmtSelectUserPhones = null;
    ResultSet rsUserPhones = null;

    // Unique check for login id
    final StringBuffer selectLoginID = new StringBuffer("SELECT 1 FROM users U WHERE U.login_id=? ");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "selectLoginID Query =" + selectLoginID);
    }
    PreparedStatement pstmtSelectLoginID = null;
    ResultSet rsSelectLoginID = null;

    // Check for parent hierarchy added by shashank
    final StringBuffer selectParentHierarchy = new StringBuffer("select (1) from CHNL_TRANSFER_RULES ctr, users u ");
    selectParentHierarchy.append(" where u.MSISDN=? and u.CATEGORY_CODE=ctr.FROM_CATEGORY ");
    selectParentHierarchy.append(" and ctr.TO_CATEGORY=? and ctr.PARENT_ASSOCIATION_ALLOWED=? ");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "selectParentHierarchy Query =" + selectParentHierarchy);
    }
    PreparedStatement pstmtSelectParentHierarchy = null;
    ResultSet rsSelectParentHierarchy = null;

    // Geographical domain check for category entered in xls file
    final StringBuffer selectGeography = new StringBuffer("SELECT 1 FROM categories CAT,");
    selectGeography.append("geographical_domain_types GDT, geographical_domains GD ");
    selectGeography.append(" WHERE CAT.domain_code=? AND CAT.grph_domain_type=GDT.grph_domain_type ");
    selectGeography.append(" AND GD.grph_domain_type= GDT.grph_domain_type AND GD.grph_domain_code=? ");
    selectGeography.append(" AND CAT.category_code=?");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "selectGeography Query =" + selectGeography);
    }
    PreparedStatement pstmtselectGeography = null;
    ResultSet rsselectGeography = null;

    // batches insert
    final StringBuffer insertIntoBatches = new StringBuffer(
        "INSERT INTO batches (batch_id, batch_type, batch_size, batch_name, network_code, status, created_by, created_on, modified_by, modified_on,file_name) ");
    insertIntoBatches.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?)");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "insertIntoBatches Query =" + insertIntoBatches);
    }
    PreparedStatement pstmtInsertIntoBatches = null;

    // update batches table
    final StringBuffer updateIntoBatches = new StringBuffer("UPDATE batches SET batch_size=?, status=? WHERE batch_id=? ");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "updateIntoBatches Query =" + updateIntoBatches);
    }
    PreparedStatement pstmtupdateIntoBatches = null;

    final StringBuffer insertInToUser = new StringBuffer("INSERT INTO users (user_id,user_name,network_code, ");
    insertInToUser.append("login_id,password,category_code,parent_id,");
    insertInToUser.append("owner_id,employee_code,status,email,contact_no,msisdn,user_type,");
    insertInToUser.append("created_by,created_on,modified_by,modified_on,address1, ");
    insertInToUser.append("address2,city,state,country,ssn,user_name_prefix, ");
    insertInToUser.append("external_code,short_name,user_code,appointment_date,previous_status,batch_id, creation_type,pswd_reset,contact_person,company,fax,firstname,lastname ,rsaflag,authentication_allowed");// fname,lname,company,fax
    insertInToUser.append(",longitude,latitude,document_type,document_no,payment_type)");
    insertInToUser.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "insertInToUser Query =" + insertInToUser);
    }
    PreparedStatement pstmtInsertInToUser = null;

    final StringBuffer insertInToChnlUser = new StringBuffer("INSERT INTO channel_users (user_id,user_grade,");
    insertInToChnlUser.append("contact_person,transfer_profile_id, comm_profile_set_id,");
    insertInToChnlUser.append("outlet_code,suboutlet_code, ");
    insertInToChnlUser.append("user_profile_id, mcommerce_service_allow, low_bal_alert_allow, mpay_profile_id , trf_rule_type)");
    insertInToChnlUser.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "insertInToChnlUser Query =" + insertInToChnlUser);
    }
    PreparedStatement pstmtInsertInToChnlUser = null;

    final StringBuffer insertInToUserPhones = new StringBuffer("INSERT INTO user_phones (user_phones_id,msisdn,user_id,");
    insertInToUserPhones.append("primary_number,sms_pin,pin_required,phone_profile,phone_language,country,");
    insertInToUserPhones.append("created_by,created_on,modified_by,modified_on,prefix_id,invalid_pin_count,pin_modified_on,pin_reset) VALUES ");
    insertInToUserPhones.append("(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "insertInToUserPhones Query =" + insertInToUserPhones);
    }
    PreparedStatement pstmtInsertInToUserPhones = null;

    final StringBuffer insertInToUserService = new StringBuffer("INSERT INTO user_services (user_id,service_type) values (?,?)");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "insertInToUserService Query =" + insertInToUserService);
    }
    PreparedStatement pstmtInsertInToUserService = null;
    
    //Voucher Type
    final StringBuffer insertInToVoucherTypes = new StringBuffer("INSERT INTO user_vouchertypes (user_id,voucher_type) values (?,?)");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "insertInToVoucherTypes Query =" + insertInToVoucherTypes);
    }
    PreparedStatement pstmtInsertInToVoucherTypes = null;
    
    final StringBuffer insertInToVoucherSegments = new StringBuffer("INSERT INTO USER_VOUCHER_SEGMENTS (user_id,voucher_segment) values (?,?)");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "insertInToVoucherSegments Query =" + insertInToVoucherSegments);
    }
    PreparedStatement pstmtInsertInToVoucherSegments = null;

    final StringBuffer insertInToUserGeographics = new StringBuffer("INSERT INTO user_geographies (user_id,");
    insertInToUserGeographics.append("grph_domain_code)");
    insertInToUserGeographics.append(" values (?,?)");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "insertInToUserGeographics Query =" + insertInToUserGeographics);
    }
    PreparedStatement pstmtInsertInToUserGeographics = null;

    final StringBuffer insertInToUserRole = new StringBuffer("INSERT INTO user_roles (user_id,");
    insertInToUserRole.append("role_code) values (?,?)");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "insertInToUserRole Query =" + insertInToUserRole);
    }
    PreparedStatement pstmtInsertInToUserRole = null;

    // Batch geographies insert
    final StringBuffer insertInToBatchGeography = new StringBuffer("INSERT INTO batch_geographies (batch_id, geography_code) ");
    insertInToBatchGeography.append(" VALUES(?,?)");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "insertInToBatchGeography Query =" + insertInToBatchGeography);
    }
    PreparedStatement pstmtInsertInToBatchGeography = null;
    PreparedStatement pstmtselectParentGeography = null;
    ResultSet rsselectParentGeography = null;
    String parentGeography = null;
    // added by shashank
    String parentLoginId = null;
    String parentMsisdn = null;

    final StringBuffer selectExternalCode = new StringBuffer("SELECT 1 FROM users WHERE external_code= ? AND status <> 'N' AND status <> 'C' ");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "selectExternalCode Query =" + selectExternalCode);
    }
    PreparedStatement pstmtSelectExternalCode = null;
    ResultSet rsExternalCode = null;
    final StringBuffer selectPortedMSISDN = new StringBuffer("SELECT 1 FROM ported_msisdn WHERE msisdn=? AND port_type=? ");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "selectPortedMSISDN Query =" + selectPortedMSISDN);
    }
    PreparedStatement pstmtPortedMSISDN = null;
    ResultSet rsPortedMSISDN = null;

    final StringBuffer selectParentDetails = new StringBuffer("SELECT C.category_code, C.web_interface_allowed, C.sms_interface_allowed ");
    selectParentDetails.append(" FROM users U, categories C WHERE U.category_code=C.category_code AND U.user_id=?");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "selectParentDetails Query =" + selectParentDetails);
    }
    PreparedStatement pstmtParentDetails = null;
    ResultSet rsParentDetails = null;

    final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
    try {
        operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
    } catch (Exception e) {
        log.errorTrace(methodName, e);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[addChannelUserList]", "", "", "",
            "Exception while loading the class at the call:" + e.getMessage());
    }

    try {
        pstmtSelectParentUserDetails = p_con.prepareStatement(selectPtrUsrDetail.toString());
        // pstmtSelectParentUserDetailsL=p_con.prepareStatement(selectPtrUsrDetailL.toString());
        pstmtSelectParentUserDetailsP = p_con.prepareStatement(selectPtrUsrDetailP.toString());
      //  pstmtChildGeographyAllowed = p_con.prepareStatement(childGeographyAllowed.toString());
        pstmtSelectUserPhones = p_con.prepareStatement(selectUserPhones.toString());
        pstmtSelectLoginID = p_con.prepareStatement(selectLoginID.toString());
        // added by shashank for batch user creation
        pstmtSelectParentHierarchy = p_con.prepareStatement(selectParentHierarchy.toString());
        // end
        pstmtselectGeography = p_con.prepareStatement(selectGeography.toString());
        pstmtInsertIntoBatches = p_con.prepareStatement(insertIntoBatches.toString());
        pstmtInsertInToUser = p_con.prepareStatement(insertInToUser.toString());
        pstmtInsertInToChnlUser = p_con.prepareStatement(insertInToChnlUser.toString());
        pstmtInsertInToUserPhones = p_con.prepareStatement(insertInToUserPhones.toString());
        pstmtInsertInToUserService = p_con.prepareStatement(insertInToUserService.toString());
        pstmtInsertInToVoucherTypes = p_con.prepareStatement(insertInToVoucherTypes.toString());
        pstmtInsertInToVoucherSegments = p_con.prepareStatement(insertInToVoucherSegments.toString());
       
        pstmtInsertInToUserGeographics = p_con.prepareStatement(insertInToUserGeographics.toString());
        pstmtInsertInToUserRole = p_con.prepareStatement(insertInToUserRole.toString());
        pstmtInsertInToBatchGeography = p_con.prepareStatement(insertInToBatchGeography.toString());
        pstmtupdateIntoBatches = p_con.prepareStatement(updateIntoBatches.toString());
        pstmtSelectExternalCode = p_con.prepareStatement(selectExternalCode.toString());
        pstmtPortedMSISDN = p_con.prepareStatement(selectPortedMSISDN.toString());
        pstmtParentDetails = p_con.prepareStatement(selectParentDetails.toString());
        ChannelUserVO channelUserVO = null;
        int seqNo = 0;
        int updateCount = 0;
        boolean noupdateofphoneanduserid=false;
        final ArrayList geographyDomainCodeList = new ArrayList();
        log.debug("batchuserWebDaoafter preparedStatement"," time taken = "+(System.currentTimeMillis()-startTime)+" ms");
        startTime=System.currentTimeMillis();
        Collections.sort(p_userDetailList);

        long idCounter = 0;
        int commitNumber = 0;
        int userPaddingLength = 0;
        try {
            commitNumber = Integer.parseInt(Constants.getProperty("BATCH_USER_COMMIT_NUMBER"));
            userPaddingLength = Integer.parseInt(Constants.getProperty("USER_PADDING_LENGTH"));
        } catch (Exception e) {
            commitNumber = 100;
            userPaddingLength = 10;
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[addChannelUserList]", "", "", "",
                "Exception:" + e.getMessage());
        }
        CategoryVO categoryVO = null;
        NetworkPrefixVO networkPrefixVO = null;
        String filterMsisdn = null;
        String webAccAllowed = null;
        String smsAccAllowed = null;
        ArrayList msisdnList = null;
        UserPhoneVO userPhoneVO = null;
        if(!inbatch){
        for (int i = 0, length = p_userDetailList.size(); i < length; i++) {
            seqNo = 0;
            parentGeography = null;
            channelUserVO = (ChannelUserVO) p_userDetailList.get(i);
            categoryVO = channelUserVO.getCategoryVO();
            /*
             * Validation 1: Pick category code & match its sequence number
             * with the sequence number
             * of the parent[ based on parent login id & msisdn supplied in
             * XLS file ]. If the seq
             * number of the category=1 then there is no need to supply
             * parent login id and parent
             * msisdn.If the parent info is given then error: No parent info
             * req for top level category.
             * Also validation is done if the parent corresponding to the
             * parent's login ID cannot have
             * the new user as his child based on the category (Added by
             * Ankur June 2011)
             */
            
            rsParentUserDetails = null;
            if (PretupsI.YES.equals(categoryVO.getWebInterfaceAllowed()) && PretupsI.YES.equals(categoryVO.getSmsInterfaceAllowed())) {
                if (!BTSLUtil.isNullString(channelUserVO.getParentLoginID())) {
                    pstmtSelectParentUserDetails.setString(1, channelUserVO.getParentLoginID());
                    pstmtSelectParentUserDetails.setString(2, channelUserVO.getParentMsisdn());
                    rsParentUserDetails = pstmtSelectParentUserDetails.executeQuery();
                } else {
                    pstmtSelectParentUserDetailsP.setString(1, channelUserVO.getParentMsisdn());
                    rsParentUserDetails = pstmtSelectParentUserDetailsP.executeQuery();
                }
            }
            /**
             * else
             * if(PretupsI.YES.equals(categoryVO.getWebInterfaceAllowed()))
             * {
             * if(!BTSLUtil.isNullString(channelUserVO.getParentLoginID()) )
             * {
             * pstmtSelectParentUserDetailsL.setString(1,channelUserVO.
             * getParentLoginID());
             * rsParentUserDetails=pstmtSelectParentUserDetailsL.
             * executeQuery();
             * }
             * else
             * {
             * pstmtSelectParentUserDetailsP.setString(1,channelUserVO.
             * getParentMsisdn());
             * rsParentUserDetails=pstmtSelectParentUserDetailsP.
             * executeQuery();
             * }
             * }
             **/
            else if (PretupsI.YES.equals(categoryVO.getSmsInterfaceAllowed())) {
                pstmtSelectParentUserDetailsP.setString(1, channelUserVO.getParentMsisdn());

                rsParentUserDetails = pstmtSelectParentUserDetailsP.executeQuery();
            }
            seqNo = 0;
            if (rsParentUserDetails.next()) {
                seqNo = rsParentUserDetails.getInt("sequence_no");
                channelUserVO.setOwnerID(rsParentUserDetails.getString("owner_id"));
                channelUserVO.setParentID(rsParentUserDetails.getString("user_id"));
                channelUserVO.setParentLoginID(rsParentUserDetails.getString("login_id"));
                parentGeography = rsParentUserDetails.getString("grph_domain_code");
                // added by shashank
                parentLoginId = rsParentUserDetails.getString("login_id");
                parentMsisdn = rsParentUserDetails.getString("msisdn");
                // end
                
                pstmtChildGeographyAllowed = batchUserWebQry.addChannelUserListChildGeographyAllowedQry(p_con, channelUserVO.getGeographicalCode(), parentGeography);
                rsChildGeographyAllowed = pstmtChildGeographyAllowed.executeQuery();
                if (!rsChildGeographyAllowed.next()) {
                    errorVO = new ListValueVO(channelUserVO.getRecordNumber(), channelUserVO.getGeographicalCode(),PretupsErrorCodesI.GEO_NOT_IN_PARENT_HIERARCHY, RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.GEO_NOT_IN_PARENT_HIERARCHY,new String[] {""}));
                    errorList.add(errorVO);
                    BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.GEO_NOT_IN_PARENT_HIERARCHY,new String[] {""}));
                    continue;
                }
                if (!BTSLUtil.isNullString(channelUserVO.getParentLoginID())) {
                    if (!parentLoginId.equals(channelUserVO.getParentLoginID())) {
                    	errorVO = new ListValueVO(channelUserVO.getRecordNumber(), channelUserVO.getParentLoginID(),PretupsErrorCodesI.PARENT_NOT_FOUND, RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.PARENT_NOT_FOUND,new String[] {""}));
                        errorList.add(errorVO);
                        BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.PARENT_NOT_FOUND,new String[] {""}));
                        continue;
                    }
                }
                if (!BTSLUtil.isNullString(channelUserVO.getParentMsisdn())) {
                    if (!parentMsisdn.equals(channelUserVO.getParentMsisdn())) {
                    	errorVO = new ListValueVO(channelUserVO.getRecordNumber(), channelUserVO.getParentLoginID(),PretupsErrorCodesI.PARENT_NOT_FOUND, RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.PARENT_NOT_FOUND,new String[] {""}));
                        errorList.add(errorVO);
                        BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.PARENT_NOT_FOUND,new String[] {""}));
                        continue;
                    }
                }

            } else if (channelUserVO.getCategoryVO().getSequenceNumber() > 1) {
            	errorVO = new ListValueVO(channelUserVO.getRecordNumber(), channelUserVO.getParentLoginID(),PretupsErrorCodesI.PARENT_NOT_FOUND, RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.PARENT_NOT_FOUND,new String[] {""}));
                errorList.add(errorVO);
                BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.PARENT_NOT_FOUND,new String[] {""}));
                continue;
            }
            pstmtSelectParentUserDetails.clearParameters();

            // Parent Seq num=seqNo
            if (channelUserVO.getCategoryVO().getSequenceNumber() == 1 && !BTSLUtil.isNullString(channelUserVO.getParentMsisdn()) && !BTSLUtil.isNullString(channelUserVO
                .getParentLoginID())) {
                // errorVO=new
                // ListValueVO("WARNING",channelUserVO.getRecordNumber(),p_messages.getMessage(p_locale,"bulkuser.initiatebulkuser.msg.error.hierarchysamewarn"));
                // errorList.add(errorVO);
                BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Success :=" + "Parent information should be discarded when the category specified is parent category.");
            } else if (rsParentUserDetails != null && seqNo > channelUserVO.getCategoryVO().getSequenceNumber()) {
                errorVO = new ListValueVO(channelUserVO.getRecordNumber(), Integer.toString(channelUserVO.getCategoryVO().getSequenceNumber()),PretupsErrorCodesI.HIERARCHY_ERROR, RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.HIERARCHY_ERROR,new String[] {""}));
                errorList.add(errorVO);
                BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.HIERARCHY_ERROR,new String[] {""}));
                continue;
            }

            else if (channelUserVO.getCategoryVO().getSequenceNumber() > 1) {
                pstmtParentDetails.setString(1, channelUserVO.getParentID());
                rsParentDetails = pstmtParentDetails.executeQuery();
                if (rsParentDetails.next()) {
                    webAccAllowed = rsParentDetails.getString("web_interface_allowed");
                    smsAccAllowed = rsParentDetails.getString("sms_interface_allowed");
                }
                if (isLoginPasswordAllowed && PretupsI.YES.equals(webAccAllowed) && BTSLUtil.isNullString(channelUserVO.getParentLoginID())) {
                    errorVO = new ListValueVO(channelUserVO.getRecordNumber(), channelUserVO.getParentLoginID(),PretupsErrorCodesI.PROPERTY_MISSING, RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.PROPERTY_MISSING,new String[] {"Parent Login ID"}));
                    errorList.add(errorVO);
                    BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.PROPERTY_MISSING,new String[] {"Parent Login ID"}));
                    continue;
                }
                if (PretupsI.YES.equals(smsAccAllowed) && BTSLUtil.isNullString(channelUserVO.getParentMsisdn())) {
                	errorVO = new ListValueVO(channelUserVO.getRecordNumber(), channelUserVO.getParentLoginID(),PretupsErrorCodesI.PROPERTY_MISSING, RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.PROPERTY_MISSING,new String[] {"Parent MSISDN"}));
                    errorList.add(errorVO);
                    BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.PROPERTY_MISSING,new String[] {"Parent MSISDN"}));
                    continue;
                }
                pstmtParentDetails.clearParameters();
                webAccAllowed = "N";
                smsAccAllowed = "N";

                // Check for parent hierarchy added by shashank
                pstmtSelectParentHierarchy.setString(1, channelUserVO.getParentMsisdn());
                pstmtSelectParentHierarchy.setString(2, channelUserVO.getCategoryCode());
                pstmtSelectParentHierarchy.setString(3, PretupsI.YES);
                rsSelectParentHierarchy = pstmtSelectParentHierarchy.executeQuery();
                if (!rsSelectParentHierarchy.next()) {
                	errorVO = new ListValueVO(channelUserVO.getRecordNumber(), Integer.toString(channelUserVO.getCategoryVO().getSequenceNumber()),PretupsErrorCodesI.HIERARCHY_ERROR, RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.HIERARCHY_ERROR,new String[] {""}));
                    errorList.add(errorVO);
                    BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.HIERARCHY_ERROR,new String[] {""}));
                    continue;
                }
                // end
            }
           
            // ************Validation 2: Check for login_id uniqueness, if
            // login id already exists mark error.
            pstmtSelectLoginID.setString(1, channelUserVO.getLoginID());
            rsSelectLoginID = pstmtSelectLoginID.executeQuery();
            if (rsSelectLoginID.next()) {
                errorVO = new ListValueVO(channelUserVO.getRecordNumber(), channelUserVO.getLoginID(),PretupsErrorCodesI.LOGINID_EXIST_ALREADY, RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.LOGINID_EXIST_ALREADY,new String[] {""}));
                errorList.add(errorVO);
                BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.LOGINID_EXIST_ALREADY,new String[] {""}));
                continue;
            }
            
            // *************Validation 3: Check for Geography
            // **************************************
            pstmtselectGeography.setString(1, channelUserVO.getDomainID());
            pstmtselectGeography.setString(2, channelUserVO.getGeographicalCode());
            pstmtselectGeography.setString(3, channelUserVO.getCategoryCode());
            rsselectGeography = pstmtselectGeography.executeQuery();
            if (!rsselectGeography.next()) {
                errorVO = new ListValueVO(channelUserVO.getRecordNumber(), channelUserVO.getLoginID(),PretupsErrorCodesI.GEO_NOT_VALID_FOR_CATEOGRY, RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.GEO_NOT_VALID_FOR_CATEOGRY,new String[] {channelUserVO.getGeographicalCode(), channelUserVO.getCategoryCode()}));
                errorList.add(errorVO);
                BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.GEO_NOT_VALID_FOR_CATEOGRY,new String[] {channelUserVO.getGeographicalCode(), channelUserVO.getCategoryCode()}));
                continue;
            }
            /*
             * 26/04/07 Code Added for MNP
             * Preference to check whether MNP is allowed in system or not.
             * If yes then check whether Number has not been ported out, If
             * yes then throw error, else continue
             */
            
            final String[] msisdnInput = channelUserVO.getMultipleMsisdnlist().split(",");
            boolean checkMsisdn = false;
            boolean checkMnpAllowed = false;
            final StringBuffer errorMsisdn = new StringBuffer();
            final StringBuffer errorMnpMsisdn = new StringBuffer();
            for (int k = 0, j = msisdnInput.length; k < j; k++) {
                if (isMNPAllowed) {
                    // filterMsisdn=channelUserVO.getMsisdn();
                    filterMsisdn = msisdnInput[k].trim();
                    networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(filterMsisdn));
                    if (networkPrefixVO == null) {
                        errorMnpMsisdn.append(msisdnInput[k]);
                        errorMnpMsisdn.append(",");
                        checkMnpAllowed = true;
                    } else if (networkPrefixVO.getOperator().equals(PretupsI.OPERATOR_TYPE_PORT)) {
                        pstmtPortedMSISDN.setString(1, filterMsisdn);
                        pstmtPortedMSISDN.setString(2, PretupsI.PORTED_IN);
                        rsPortedMSISDN = pstmtPortedMSISDN.executeQuery();
                        if (!rsPortedMSISDN.next()) {
                            // errorVO=new
                            // ListValueVO("",channelUserVO.getRecordNumber(),p_messages.getMessage(p_locale,"bulkuser.processuploadedfile.error.nonetworkprefixfound",new
                            // String[]{msisdnInput[k]}));
                            // errorList.add(errorVO);
                            // BatchesLog.log("BULKUSRINITIATE",channelUserVO,null,"Fail :="+p_messages.getMessage("bulkuser.processuploadedfile.error.nonetworkprefixfound",new
                            // String[]{msisdnInput[k]}));
                            // continue;
                            errorMnpMsisdn.append(msisdnInput[k]);
                            errorMnpMsisdn.append(",");
                            checkMnpAllowed = true;
                        }
                    } else {
                        pstmtPortedMSISDN.setString(1, filterMsisdn);
                        pstmtPortedMSISDN.setString(2, PretupsI.PORTED_OUT);
                        rsPortedMSISDN = pstmtPortedMSISDN.executeQuery();
                        if (rsPortedMSISDN.next()) {
                            // errorVO=new
                            // ListValueVO("",channelUserVO.getRecordNumber(),p_messages.getMessage(p_locale,"bulkuser.processuploadedfile.error.nonetworkprefixfound",new
                            // String[]{msisdnInput[k]}));
                            // errorList.add(errorVO);
                            // BatchesLog.log("BULKUSRINITIATE",channelUserVO,null,"Fail :="+p_messages.getMessage("bulkuser.processuploadedfile.error.nonetworkprefixfound",new
                            // String[]{msisdnInput[k]}));
                            // continue;
                            errorMnpMsisdn.append(msisdnInput[k]);
                            errorMnpMsisdn.append(",");
                            checkMnpAllowed = true;
                        }
                    }
                    pstmtPortedMSISDN.clearParameters();
                }
                // 26/04/07: MNP Code End
                // **********Validation 4: Check for the uniqueness of the
                // msisdn
                // pstmtSelectUserPhones.setString(1,channelUserVO.getMsisdn());
                pstmtSelectUserPhones.setString(1, msisdnInput[k].trim());
                rsUserPhones = pstmtSelectUserPhones.executeQuery();
                if (rsUserPhones.next()) {
                    // errorVO=new
                    // ListValueVO("",channelUserVO.getRecordNumber(),p_messages.getMessage(p_locale,"bulkuser.initiatebulkuser.msg.error.msisdnalreadyassigned",new
                    // String[]{channelUserVO.getMsisdn()}));
                    // errorList.add(errorVO);
                    // BatchesLog.log("BULKUSRINITIATE",channelUserVO,null,"Fail :="+p_messages.getMessage("bulkuser.initiatebulkuser.msg.error.msisdnalreadyassigned",new
                    // String[]{channelUserVO.getMsisdn()}));
                    // BatchesLog.log("BULKUSRINITIATE",channelUserVO,null,"Fail :="+p_messages.getMessage("bulkuser.initiatebulkuser.msg.error.msisdnalreadyassigned",new
                    // String[]{msisdnInput[k]}));
                    // continue;
                    errorMsisdn.append(msisdnInput[k]);
                    errorMsisdn.append(",");
                    checkMsisdn = true;
                }
            } // end of for loop
              // checking status
            if (checkMnpAllowed) {
            	errorVO = new ListValueVO(channelUserVO.getRecordNumber(), errorMnpMsisdn.toString().substring(0, errorMnpMsisdn.toString().length() - 1),PretupsErrorCodesI.NONE_NETWORK_PREFIX_FOUND, RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.NONE_NETWORK_PREFIX_FOUND,new String[] {errorMnpMsisdn.toString().substring(0, errorMnpMsisdn.toString().length() - 1)}));
                errorList.add(errorVO);
                BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.NONE_NETWORK_PREFIX_FOUND,new String[] {errorMnpMsisdn.toString().substring(0, errorMnpMsisdn.toString().length() - 1)}));
                continue;
            } else if (checkMsisdn) {
            	errorVO = new ListValueVO(channelUserVO.getRecordNumber(), errorMsisdn.toString().substring(0, errorMsisdn.toString().length() - 1),PretupsErrorCodesI.MSISDN_EXIST, RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.MSISDN_EXIST,new String[] {errorMsisdn.toString().substring(0, errorMsisdn.toString().length() - 1)}));
                errorList.add(errorVO);
                // BatchesLog.log("BULKUSRINITIATE",channelUserVO,null,"Fail :="+p_messages.getMessage("bulkuser.initiatebulkuser.msg.error.msisdnalreadyassigned",new
                // String[]{channelUserVO.getMsisdn()}));
                BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.MSISDN_EXIST,new String[] {errorMsisdn.toString().substring(0, errorMsisdn.toString().length() - 1)}));
                continue;
            }
            
            // **********Validation 5: Check for the uniqueness of the
            // External code (if given)
            if (!BTSLUtil.isNullString(channelUserVO.getExternalCode())) {
                pstmtSelectExternalCode.setString(1, channelUserVO.getExternalCode().trim());
                rsExternalCode = pstmtSelectExternalCode.executeQuery();
                if (rsExternalCode.next()) {
                    errorVO = new ListValueVO(channelUserVO.getRecordNumber(), channelUserVO.getExternalCode(),PretupsErrorCodesI.EXT_CODE_EXIST, RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.EXT_CODE_EXIST,new String[] {channelUserVO.getExternalCode()}));
                    errorList.add(errorVO);
                    BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.EXT_CODE_EXIST,new String[] {channelUserVO.getExternalCode()}));
                    continue;
                }
            }
           
            // ***After all validation insertion starts
            // here********************************************
            // Insert into batches
            if(flag){
            	 // First time make the user_id
                idCounter = idGeneratorDAO.getNextBatchID(p_con, PretupsI.USERID, PretupsI.ALL, channelUserVO.getNetworkID(), channelUserVO.getCreatedOn());
                // First time make the phone_id
                phoneID = idGeneratorDAO.getNextBatchID(p_con, PretupsI.USR_PHONE_ID, PretupsI.ALL, PretupsI.ALL, channelUserVO.getCreatedOn());
            flag = false;
            }
            
            
            if (commitCounter >= commitNumber)// After 100 record commit the
            // records
            {
                // after 100 records update the last_no of the ids table for
                // user_id
                idGeneratorDAO.updateNextBatchID(p_con, PretupsI.USERID, PretupsI.ALL, channelUserVO.getNetworkID(), channelUserVO.getCreatedOn(), idCounter - 1);
                // after 100 records update the last_no of the ids table for
                // phone_id
                idGeneratorDAO.updateNextBatchID(p_con, PretupsI.USR_PHONE_ID, PretupsI.ALL, PretupsI.ALL, channelUserVO.getCreatedOn(), phoneID - 1);
                p_con.commit();
                // after 100 records pick the last_no from the ids table for
                // user_id
                idCounter = idGeneratorDAO.getNextBatchID(p_con, PretupsI.USERID, PretupsI.ALL, channelUserVO.getNetworkID(), channelUserVO.getCreatedOn());
                // after 100 records pick the last_no from the ids table for
                // phone_id
                phoneID = idGeneratorDAO.getNextBatchID(p_con, PretupsI.USR_PHONE_ID, PretupsI.ALL, PretupsI.ALL, channelUserVO.getCreatedOn());
                commitCounter = 0;// reset commit counter
            }
            channelUserVO.setUserID(this.generateUserId(channelUserVO.getNetworkID(), channelUserVO.getCategoryVO().getUserIdPrefix(), idCounter, userPaddingLength));

            // Insert into users table.

            pstmtInsertInToUser.clearParameters();
            index = 0;
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getUserID());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getUserName());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getNetworkID());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getLoginID());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getPassword());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getCategoryCode());
            if (channelUserVO.getCategoryVO().getSequenceNumber() == 1) {
                ++index;
                pstmtInsertInToUser.setString(index, PretupsI.ROOT_PARENT_ID);
            } else {
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getParentID());
            }
            if (channelUserVO.getCategoryVO().getSequenceNumber() == 1) {
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getUserID());
            } else {
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getOwnerID());
            }
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getEmpCode());
            ++index;
            if(((Integer)PreferenceCache.getControlPreference(PreferenceI.USER_APPROVAL_LEVEL,channelUserVO.getNetworkID(),channelUserVO.getCategoryCode())).intValue() >0  ){
	        	pstmtInsertInToUser.setString(index,PretupsI.USER_STATUS_NEW);//N New
            }
            else
            {
            	if (LookupsCache.getLookupCodeList(PretupsI.ALLOWED_USER_STATUS).contains(PretupsI.USER_STATUS_PREACTIVE)) {
            		pstmtInsertInToUser.setString(index,PretupsI.USER_STATUS_PREACTIVE);// PA
            		// Active
            	} else {
            		pstmtInsertInToUser.setString(index,PretupsI.USER_STATUS_ACTIVE);// A
            		// Active
            	}
            }
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getEmail());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getContactNo());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getMsisdn());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getUserType());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getCreatedBy());
            ++index;
            pstmtInsertInToUser.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getCreatedOn()));
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getModifiedBy());
            ++index;
            pstmtInsertInToUser.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getModifiedOn()));
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getAddress1());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getAddress2());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getCity());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getState());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getCountry());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getSsn());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getUserNamePrefix());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getExternalCode());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getShortName());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getUserCode());
            if (channelUserVO.getAppointmentDate() != null) {
                ++index;
                pstmtInsertInToUser.setDate(index, BTSLUtil.getSQLDateFromUtilDate(channelUserVO.getAppointmentDate()));
            } else {
                ++index;
                pstmtInsertInToUser.setDate(index, null);
            }
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getPreviousStatus());
            ++index;
            pstmtInsertInToUser.setString(index, batchID);
            ++index;
            pstmtInsertInToUser.setString(index, PretupsI.BATCH_USR_CREATION_TYPE);
            ++index;
            pstmtInsertInToUser.setString(index, PretupsI.YES);
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getContactPerson());
            // Added by deepika aggarwal
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getCompany());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getFax());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getFirstName());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getLastName());
            // end
            // added by shashank for rsa authentication
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getRsaFlag());
            // added for OTP
            if (BTSLUtil.isNullString(p_userVO.getAuthTypeAllowed())) {
                ++index;
                pstmtInsertInToUser.setString(index, PretupsI.NO);
            } else {
                ++index;
                pstmtInsertInToUser.setString(index, p_userVO.getAuthTypeAllowed());
            }
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getLongitude());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getLatitude());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getDocumentType());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getDocumentNo());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getPaymentType());
           
            if (pstmtInsertInToUser.executeUpdate() <= 0) {
                p_con.rollback();
                BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=When inserting users table");
                continue;
            }

            // insert into channel user
            pstmtInsertInToChnlUser.clearParameters();
            index = 0;
            ++index;
            pstmtInsertInToChnlUser.setString(index, channelUserVO.getUserID());
            ++index;
            pstmtInsertInToChnlUser.setString(index, channelUserVO.getUserGrade());
            ++index;
            pstmtInsertInToChnlUser.setString(index, channelUserVO.getContactPerson());
            ++index;
            pstmtInsertInToChnlUser.setString(index, channelUserVO.getTransferProfileID());
            ++index;
            pstmtInsertInToChnlUser.setString(index, channelUserVO.getCommissionProfileSetID());
            ++index;
            pstmtInsertInToChnlUser.setString(index, channelUserVO.getOutletCode());
            ++index;
            pstmtInsertInToChnlUser.setString(index, channelUserVO.getSubOutletCode());
            // for Zebra and Tango by Sanjeew date 09/07/07
            // user_profile_id, mcommerce_service_allow,
            // low_bal_alert_allow, mpay_profile_id
            // pstmtInsertInToChnlUser.setString(++index,channelUserVO.getUserProfileID());
            ++index;
            pstmtInsertInToChnlUser.setString(index, channelUserVO.getUserID());
            ++index;
            pstmtInsertInToChnlUser.setString(index, channelUserVO.getMcommerceServiceAllow());
            ++index;
            pstmtInsertInToChnlUser.setString(index, channelUserVO.getLowBalAlertAllow());
            ++index;
            pstmtInsertInToChnlUser.setString(index, channelUserVO.getMpayProfileID());
            // Added for Transfer Rule by shashank
            ++index;
            pstmtInsertInToChnlUser.setString(index, channelUserVO.getTrannferRuleTypeId());
            // end
            // End Zebra and Tango

            if (pstmtInsertInToChnlUser.executeUpdate() <= 0) {
                p_con.rollback();
                BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=When inserting channel_users table");
                continue;
            }
           
            // insert into user_phones
            if (channelUserVO.getCategoryVO().getSmsInterfaceAllowed().equals(PretupsI.YES)) {
                int phoneinsCount = 0;
                msisdnList = channelUserVO.getMsisdnList();
                // for(int k=0,j=msisdnInput.length;k<j;k++){
                for (int k = 0, msisdnListSize = msisdnList.size(); k < msisdnListSize; k++) {
                    userPhoneVO = (UserPhoneVO) msisdnList.get(k);
                    pstmtInsertInToUserPhones.clearParameters();
                    index = 0;
                    ++index;
                    pstmtInsertInToUserPhones.setString(index, "" + phoneID);
                    phoneID++;
                    // pstmtInsertInToUserPhones.setString(++index,
                    // channelUserVO.getMsisdn());
                    ++index;
                    pstmtInsertInToUserPhones.setString(index, userPhoneVO.getMsisdn().trim());
                    ++index;
                    pstmtInsertInToUserPhones.setString(index, channelUserVO.getUserID());
                    if (channelUserVO.getMsisdn().equalsIgnoreCase(userPhoneVO.getMsisdn().trim())) {
                        ++index;
                        pstmtInsertInToUserPhones.setString(index, PretupsI.YES);
                    } else {
                        ++index;
                        pstmtInsertInToUserPhones.setString(index, PretupsI.NO);
                    }
                    ++index;
                    pstmtInsertInToUserPhones.setString(index, userPhoneVO.getSmsPin());
                    // pstmtInsertInToUserPhones.setString(++index,
                    // channelUserVO.getSmsPin());
                    ++index;
                    pstmtInsertInToUserPhones.setString(index, PretupsI.YES);
                    ++index;
                    pstmtInsertInToUserPhones.setString(index, channelUserVO.getCategoryCode());
                    // pstmtInsertInToUserPhones.setString(++index,
                    // defaultLanguage);
                    ++index;
                    pstmtInsertInToUserPhones.setString(index, userPhoneVO.getPhoneLanguage());
                    // pstmtInsertInToUserPhones.setString(++index,
                    // defaultCountry);
                    ++index;
                    pstmtInsertInToUserPhones.setString(index, userPhoneVO.getCountry());
                    ++index;
                    pstmtInsertInToUserPhones.setString(index, channelUserVO.getCreatedBy());
                    ++index;
                    pstmtInsertInToUserPhones.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getCreatedOn()));
                    ++index;
                    pstmtInsertInToUserPhones.setString(index, channelUserVO.getModifiedBy());
                    ++index;
                    pstmtInsertInToUserPhones.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getModifiedOn()));
                    prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(PretupsBL.getFilteredMSISDN(channelUserVO.getMsisdn())));
                    ++index;
                    pstmtInsertInToUserPhones.setInt(index, Integer.parseInt(Long.toString(prefixVO.getPrefixID())));
                    ++index;
                    pstmtInsertInToUserPhones.setInt(index, 0);
                    ++index;
                    pstmtInsertInToUserPhones.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getCreatedOn()));
                    ++index;
                    pstmtInsertInToUserPhones.setString(index, PretupsI.YES);
                    phoneinsCount++;
                    if (pstmtInsertInToUserPhones.executeUpdate() <= 0) {
                        phoneinsCount--;
                        // p_con.rollback();
                        BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=When inserting users_phones table");
                        // continue;
                    }
                }
                if (msisdnInput.length < phoneinsCount) {
                    p_con.rollback();
                    continue;
                }
            }
            
            if (channelUserVO.getCategoryVO().getServiceAllowed().equals(PretupsI.YES)) {
                // insert into user_services
                for (int ii = 0, j = channelUserVO.getServiceList().size(); ii < j; ii++) {
                    pstmtInsertInToUserService.clearParameters();
                    index = 0;
                    ++index;
                    pstmtInsertInToUserService.setString(index, channelUserVO.getUserID());
                    ++index;
                    pstmtInsertInToUserService.setString(index, (String) channelUserVO.getServiceList().get(ii));
                    pstmtInsertInToUserService.executeUpdate();
                }
            }
            //Voucher Types
            if(isUserVoucherTypeAllowed && channelUserVO.getVoucherList() != null) {
                // insert into user_vouchertypes
                for (int ii = 0, j = channelUserVO.getVoucherList().size(); ii < j; ii++) {
                	pstmtInsertInToVoucherTypes.clearParameters();
                    index = 0;
                    ++index;
                    pstmtInsertInToVoucherTypes.setString(index, channelUserVO.getUserID());
                    ++index;
                    pstmtInsertInToVoucherTypes.setString(index, (String) channelUserVO.getVoucherList().get(ii));
                    pstmtInsertInToVoucherTypes.executeUpdate();
                }
            }
            if(channelUserVO.getSegmentList() != null) {
            	for (int ii = 0, j = channelUserVO.getSegmentList().size(); ii < j; ii++) {
                	pstmtInsertInToVoucherSegments.clearParameters();
                    index = 0;
                    ++index;
                    pstmtInsertInToVoucherSegments.setString(index, channelUserVO.getUserID());
                    ++index;
                    pstmtInsertInToVoucherSegments.setString(index, (String) channelUserVO.getSegmentList().get(ii));
                    pstmtInsertInToVoucherSegments.executeUpdate();
                }                	
            }
            
            log.debug("batchuserWebDaoafter insert into  users services"," time taken = "+(System.currentTimeMillis()-startTime)+" ms");
            startTime=System.currentTimeMillis();
            pstmtInsertInToUserGeographics.clearParameters();
            index = 0;
            ++index;
            pstmtInsertInToUserGeographics.setString(index, channelUserVO.getUserID());
            ++index;
            pstmtInsertInToUserGeographics.setString(index, channelUserVO.getGeographicalCode());
            if (pstmtInsertInToUserGeographics.executeUpdate() <= 0) {
                p_con.rollback();
                BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=When inserting users_geographics table");
                continue;
            } else// make the array list of geography code
            {
                if (!geographyDomainCodeList.contains(channelUserVO.getGeographicalCode())) {
                    geographyDomainCodeList.add(channelUserVO.getGeographicalCode());
                }
            }
            
            if (channelUserVO.getCategoryVO().getWebInterfaceAllowed().equals(PretupsI.YES)) {
                pstmtInsertInToUserRole.clearParameters();
                index = 0;
                ++index;
                pstmtInsertInToUserRole.setString(index, channelUserVO.getUserID());
                ++index;
                pstmtInsertInToUserRole.setString(index, channelUserVO.getGroupRoleCode());
                if (!BTSLUtil.isNullString(channelUserVO.getLoginID())) {
                    if (pstmtInsertInToUserRole.executeUpdate() <= 0) {
                        p_con.rollback();
                        BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=When inserting users_roles table");
                        continue;
                    }
                }
            }
            
            commitCounter++;
            idCounter++;
            updateCount++;
            ChannelUserLog.log("BULKUSRINITIATE", channelUserVO, p_userVO, true, "Add channel user from bulk");
        }
    }
        if(updateCount==0&&insertintoBatches){
        	updateCount++;
        	channelUserVO = (ChannelUserVO) p_userDetailList.get(0);
        	noupdateofphoneanduserid = true;
        }
        if (updateCount > 0)// if any user information insert then insert
        // into batch geography and update batches
        {
        	if(!noupdateofphoneanduserid){
            idGeneratorDAO.updateNextBatchID(p_con, PretupsI.USERID, PretupsI.ALL, channelUserVO.getNetworkID(), channelUserVO.getCreatedOn(), idCounter - 1);
            idGeneratorDAO.updateNextBatchID(p_con, PretupsI.USR_PHONE_ID, PretupsI.ALL, PretupsI.ALL, channelUserVO.getCreatedOn(), phoneID - 1);
        	}
            if(geographyDomainCodeList.isEmpty()){
            if (!geographyDomainCodeList.contains(channelUserVO.getGeographicalCode())) {
                geographyDomainCodeList.add(channelUserVO.getGeographicalCode());
            }
            }
            String geoDomainCode = "'";
            for (int i = 0, j = geographyDomainCodeList.size(); i < j; i++) {
                geoDomainCode = geoDomainCode + geographyDomainCodeList.get(i) + "','";
            }
            geoDomainCode = geoDomainCode.substring(0, geoDomainCode.length() - 2);

            final UserGeographiesVO userGeographiesVO = p_userVO.getGeographicalAreaList().get(0);
            final int geoTypeSeqNo = userGeographiesVO.getGraphDomainSequenceNumber();

            // get the parent geographical domain code listing
            
            pstmtselectParentGeography = batchUserWebQry.addChannelUserListSelectParentGeographyQry(p_con, geoDomainCode, geoTypeSeqNo);
            rsselectParentGeography = pstmtselectParentGeography.executeQuery();
            log.debug("batchuserWebDaoafter insert into  batch geographies"," time taken = "+(System.currentTimeMillis()-startTime)+" ms");
            // insert into batch_geographics table
            if(insertintoBatches){
            while (rsselectParentGeography.next()) {
                pstmtInsertInToBatchGeography.clearParameters();
                pstmtInsertInToBatchGeography.setString(1, batchID);
                pstmtInsertInToBatchGeography.setString(2, rsselectParentGeography.getString("grph_domain_code"));
                if (pstmtInsertInToBatchGeography.executeUpdate() <= 0) {
                    p_con.rollback();
                    BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=When inserting batch_geographics table");
                    continue;
                }
            }
            if(!errorList.isEmpty()){
            	total = total - errorList.size();
            }
            pstmtInsertIntoBatches.setString(1, batchID);
            pstmtInsertIntoBatches.setString(2, PretupsI.BULK_USR_BATCH_TYPE);
            pstmtInsertIntoBatches.setInt(3, total);
            pstmtInsertIntoBatches.setString(4, channelUserVO.getBatchName());
            pstmtInsertIntoBatches.setString(5, channelUserVO.getNetworkID());
            pstmtInsertIntoBatches.setString(6, PretupsI.USR_BATCH_STATUS_UNDERPROCESS);
            pstmtInsertIntoBatches.setString(7, channelUserVO.getCreatedBy());
            pstmtInsertIntoBatches.setTimestamp(8, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getCreatedOn()));
            pstmtInsertIntoBatches.setString(9, channelUserVO.getModifiedBy());
            pstmtInsertIntoBatches.setTimestamp(10, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getModifiedOn()));
            pstmtInsertIntoBatches.setString(11, p_fileName);
            if (pstmtInsertIntoBatches.executeUpdate() <= 0) {
                p_con.rollback();
                /*BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + p_messages.getMessage("bulkuser.initiatebulkuser.err.batchnotcreated"));
                throw new BTSLBaseException(this, methodName, "bulkuser.initiatebulkuser.err.batchnotcreated", "selectDomainForInitiate");*/
            }
            insertintoBatches = false;
            errorVO = new ListValueVO("BATCHID", "", batchID);
            errorList.add(errorVO);
            }
            // update batches table for updating updateCount on the batch
            // size
            pstmtupdateIntoBatches.setInt(1, total);
            if(((Integer)PreferenceCache.getControlPreference(PreferenceI.USER_APPROVAL_LEVEL,channelUserVO.getNetworkID(),channelUserVO.getCategoryCode())).intValue() >0  ){
	    	    pstmtupdateIntoBatches.setString(2,PretupsI.USR_BATCH_STATUS_OPEN);
	        }
    	    else 
    	    	pstmtupdateIntoBatches.setString(2,PretupsI.USR_BATCH_STATUS_CLOSE);
            pstmtupdateIntoBatches.setString(3, batchID);
            pstmtupdateIntoBatches.executeUpdate();
            log.debug("batchuserWebDao last main thread into  batches "," time taken = "+(System.currentTimeMillis()-startTime)+" ms");
//            p_con.commit();
        } else {
            p_con.rollback();
        }

    } catch (SQLException sqe) {
        try {
            if (p_con != null) {
                p_con.rollback();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        log.error(methodName, "SQLException : " + sqe);
        log.errorTrace(methodName, sqe);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[addChannelUserList]", "", "", "",
            "SQL Exception:" + sqe.getMessage());
        throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
    } catch (Exception ex) {
        try {
            if (p_con != null) {
                p_con.rollback();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        log.error(methodName, "Exception : " + ex);
        log.errorTrace(methodName, ex);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[addChannelUserList]", "", "", "",
            "Exception:" + ex.getMessage());
        throw new BTSLBaseException(this, methodName, "error.general.processing");
    } finally {
        try {
            if (rsUserPhones != null) {
                rsUserPhones.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (rsParentUserDetails != null) {
                rsParentUserDetails.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (rsSelectLoginID != null) {
                rsSelectLoginID.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (rsselectGeography != null) {
                rsselectGeography.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (rsselectParentGeography != null) {
                rsselectParentGeography.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (rsChildGeographyAllowed != null) {
                rsChildGeographyAllowed.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (rsExternalCode != null) {
                rsExternalCode.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (rsPortedMSISDN != null) {
                rsPortedMSISDN.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (rsParentDetails != null) {
                rsParentDetails.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (rsSelectParentHierarchy != null) {
                rsSelectParentHierarchy.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
    	try {
            if (pstmtInsertInToVoucherTypes != null) {
            	pstmtInsertInToVoucherTypes.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
    	try {
            if (pstmtInsertInToVoucherSegments != null) {
            	pstmtInsertInToVoucherSegments.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtSelectParentUserDetails != null) {
                pstmtSelectParentUserDetails.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtSelectParentUserDetailsP != null) {
                pstmtSelectParentUserDetailsP.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtSelectUserPhones != null) {
                pstmtSelectUserPhones.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtSelectLoginID != null) {
                pstmtSelectLoginID.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtselectGeography != null) {
                pstmtselectGeography.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtselectParentGeography != null) {
                pstmtselectParentGeography.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtChildGeographyAllowed != null) {
                pstmtChildGeographyAllowed.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }

        try {
            if (pstmtInsertInToUser != null) {
                pstmtInsertInToUser.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtInsertInToChnlUser != null) {
                pstmtInsertInToChnlUser.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtInsertInToUserPhones != null) {
                pstmtInsertInToUserPhones.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtInsertInToUserService != null) {
                pstmtInsertInToUserService.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtInsertInToUserGeographics != null) {
                pstmtInsertInToUserGeographics.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtInsertInToUserRole != null) {
                pstmtInsertInToUserRole.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtInsertInToBatchGeography != null) {
                pstmtInsertInToBatchGeography.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtInsertIntoBatches != null) {
                pstmtInsertIntoBatches.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtupdateIntoBatches != null) {
                pstmtupdateIntoBatches.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtSelectExternalCode != null) {
                pstmtSelectExternalCode.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtPortedMSISDN != null) {
                pstmtPortedMSISDN.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtParentDetails != null) {
                pstmtParentDetails.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        // added by shashank
        try {
            if (pstmtSelectParentHierarchy != null) {
                pstmtSelectParentHierarchy.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        // end

        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exiting: errorList size =" + errorList.size());
        }
    }
    return errorList;
}

public ArrayList addChannelUserBulkRestList(Connection p_con, ArrayList p_userDetailList, String p_domainCode,Locale locale, UserVO p_userVO, String p_fileName,String batchID,boolean insertintoBatches,int total,boolean inbatch) throws BTSLBaseException {
    final String methodName = "addChannelUserList";
    if (log.isDebugEnabled()) {
        log.debug(
            methodName,
            "Entered: p_userDetailList.size()=" + p_userDetailList.size() + " p_domainCode=" + p_domainCode  + " p_locale=" + locale + " p_fileName: " + p_fileName);
    }
    Boolean isLoginPasswordAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LOGIN_PASSWORD_ALLOWED);
    Boolean isMNPAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MNP_ALLOWED);
    Boolean isUserVoucherTypeAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED);
    final ArrayList errorList = new ArrayList();
    double startTime = System.currentTimeMillis();
    ListValueVO errorVO = null;
    int commitCounter = 0;
    int index = 0;
    boolean flag = true;
    long phoneID = 0;
    final IDGeneratorDAO idGeneratorDAO = new IDGeneratorDAO();
    NetworkPrefixVO prefixVO = null;
    OperatorUtilI operatorUtil = null;
    // Parent details
   startTime=System.currentTimeMillis();
    final StringBuffer selectPtrUsrDetail = new StringBuffer(
        "SELECT U.user_id,U.parent_id,U.owner_id,U.category_code, C.sequence_no, C.domain_code, C.grph_domain_type,UG.grph_domain_code ,U.login_id, UP.Msisdn ");
    selectPtrUsrDetail.append(" FROM users U, user_phones UP,categories C,user_geographies UG ");
    selectPtrUsrDetail.append(" WHERE U.login_id=? AND UP.msisdn=? AND UP.primary_number='Y' AND U.status NOT IN('N','C') AND UG.user_id=U.user_id ");
    selectPtrUsrDetail.append(" AND U.user_id = UP.user_id AND U.category_code = C.category_code ");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "selectPtrUsrDetail Query =" + selectPtrUsrDetail);
    }
    PreparedStatement pstmtSelectParentUserDetails = null;
    ResultSet rsParentUserDetails = null;

    /**
     * StringBuffer selectPtrUsrDetailL = new StringBuffer(
     * "SELECT U.user_id,U.parent_id,U.owner_id,U.category_code, C.sequence_no, C.domain_code, C.grph_domain_type,UG.grph_domain_code,U.login_id, UP.Msisdn "
     * );
     * selectPtrUsrDetailL.append(
     * " FROM users U, categories C,user_geographies UG , user_phones UP ");
     * selectPtrUsrDetailL.append(
     * " WHERE U.login_id=? AND U.status NOT IN('N','C') AND UG.user_id=U.user_id "
     * );
     * selectPtrUsrDetailL.append(
     * " AND U.category_code = C.category_code AND U.user_id=UP.user_id");
     * if (log.isDebugEnabled()) log.debug("addChannelUserList",
     * "selectPtrUsrDetailL Query ="+selectPtrUsrDetailL);
     * PreparedStatement pstmtSelectParentUserDetailsL = null;
     **/

    final StringBuffer selectPtrUsrDetailP = new StringBuffer(
        "SELECT U.user_id,U.parent_id,U.owner_id,U.category_code, C.sequence_no, C.domain_code, C.grph_domain_type,UG.grph_domain_code,U.login_id , UP.Msisdn");
    selectPtrUsrDetailP.append(" FROM users U, user_phones UP,categories C,user_geographies UG ");
    selectPtrUsrDetailP.append(" WHERE UP.msisdn=? AND UP.primary_number='Y' AND U.status NOT IN('N','C') AND UG.user_id=U.user_id ");
    selectPtrUsrDetailP.append(" AND U.user_id = UP.user_id AND U.category_code = C.category_code ");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "selectPtrUsrDetailP Query =" + selectPtrUsrDetailP);
    }
    PreparedStatement pstmtSelectParentUserDetailsP = null;

    
    PreparedStatement pstmtChildGeographyAllowed = null;
    ResultSet rsChildGeographyAllowed = null;

    // Assign phone validation & mobile number validation unique
    final StringBuffer selectUserPhones = new StringBuffer("SELECT 1 from user_phones UP,users U ");
    selectUserPhones.append(" WHERE UP.msisdn = ? AND UP.user_id = U.user_id  AND U.status <> 'N' AND U.status <> 'C'");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "selectUserPhones Query =" + selectUserPhones);
    }
    PreparedStatement pstmtSelectUserPhones = null;
    ResultSet rsUserPhones = null;

    // Unique check for login id
    final StringBuffer selectLoginID = new StringBuffer("SELECT 1 FROM users U WHERE U.login_id=? ");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "selectLoginID Query =" + selectLoginID);
    }
    PreparedStatement pstmtSelectLoginID = null;
    ResultSet rsSelectLoginID = null;

    // Check for parent hierarchy added by shashank
    final StringBuffer selectParentHierarchy = new StringBuffer("select (1) from CHNL_TRANSFER_RULES ctr, users u ");
    selectParentHierarchy.append(" where u.MSISDN=? and u.CATEGORY_CODE=ctr.FROM_CATEGORY ");
    selectParentHierarchy.append(" and ctr.TO_CATEGORY=? and ctr.PARENT_ASSOCIATION_ALLOWED=? ");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "selectParentHierarchy Query =" + selectParentHierarchy);
    }
    PreparedStatement pstmtSelectParentHierarchy = null;
    ResultSet rsSelectParentHierarchy = null;

    // Geographical domain check for category entered in xls file
    final StringBuffer selectGeography = new StringBuffer("SELECT 1 FROM categories CAT,");
    selectGeography.append("geographical_domain_types GDT, geographical_domains GD ");
    selectGeography.append(" WHERE CAT.domain_code=? AND CAT.grph_domain_type=GDT.grph_domain_type ");
    selectGeography.append(" AND GD.grph_domain_type= GDT.grph_domain_type AND GD.grph_domain_code=? ");
    selectGeography.append(" AND CAT.category_code=?");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "selectGeography Query =" + selectGeography);
    }
    PreparedStatement pstmtselectGeography = null;
    ResultSet rsselectGeography = null;

    // batches insert
    final StringBuffer insertIntoBatches = new StringBuffer(
        "INSERT INTO batches (batch_id, batch_type, batch_size, batch_name, network_code, status, created_by, created_on, modified_by, modified_on,file_name) ");
    insertIntoBatches.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?)");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "insertIntoBatches Query =" + insertIntoBatches);
    }
    PreparedStatement pstmtInsertIntoBatches = null;

    // update batches table
    final StringBuffer updateIntoBatches = new StringBuffer("UPDATE batches SET batch_size=?, status=? WHERE batch_id=? ");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "updateIntoBatches Query =" + updateIntoBatches);
    }
    PreparedStatement pstmtupdateIntoBatches = null;

    final StringBuffer insertInToUser = new StringBuffer("INSERT INTO users (user_id,user_name,network_code, ");
    insertInToUser.append("login_id,password,category_code,parent_id,");
    insertInToUser.append("owner_id,employee_code,status,email,contact_no,msisdn,user_type,");
    insertInToUser.append("created_by,created_on,modified_by,modified_on,address1, ");
    insertInToUser.append("address2,city,state,country,ssn,user_name_prefix, ");
    insertInToUser.append("external_code,short_name,user_code,appointment_date,previous_status,batch_id, creation_type,pswd_reset,contact_person,company,fax,firstname,lastname ,rsaflag,authentication_allowed");// fname,lname,company,fax
    insertInToUser.append(",longitude,latitude,document_type,document_no,payment_type)");
    insertInToUser.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "insertInToUser Query =" + insertInToUser);
    }
    PreparedStatement pstmtInsertInToUser = null;

    final StringBuffer insertInToChnlUser = new StringBuffer("INSERT INTO channel_users (user_id,user_grade,");
    insertInToChnlUser.append("contact_person,transfer_profile_id, comm_profile_set_id,");
    insertInToChnlUser.append("outlet_code,suboutlet_code, ");
    insertInToChnlUser.append("user_profile_id, mcommerce_service_allow, low_bal_alert_allow, mpay_profile_id , trf_rule_type)");
    insertInToChnlUser.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "insertInToChnlUser Query =" + insertInToChnlUser);
    }
    PreparedStatement pstmtInsertInToChnlUser = null;

    final StringBuffer insertInToUserPhones = new StringBuffer("INSERT INTO user_phones (user_phones_id,msisdn,user_id,");
    insertInToUserPhones.append("primary_number,sms_pin,pin_required,phone_profile,phone_language,country,");
    insertInToUserPhones.append("created_by,created_on,modified_by,modified_on,prefix_id,invalid_pin_count,pin_modified_on,pin_reset) VALUES ");
    insertInToUserPhones.append("(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "insertInToUserPhones Query =" + insertInToUserPhones);
    }
    PreparedStatement pstmtInsertInToUserPhones = null;

    final StringBuffer insertInToUserService = new StringBuffer("INSERT INTO user_services (user_id,service_type) values (?,?)");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "insertInToUserService Query =" + insertInToUserService);
    }
    PreparedStatement pstmtInsertInToUserService = null;
    
    //Voucher Type
    final StringBuffer insertInToVoucherTypes = new StringBuffer("INSERT INTO user_vouchertypes (user_id,voucher_type) values (?,?)");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "insertInToVoucherTypes Query =" + insertInToVoucherTypes);
    }
    PreparedStatement pstmtInsertInToVoucherTypes = null;
    
    final StringBuffer insertInToVoucherSegments = new StringBuffer("INSERT INTO USER_VOUCHER_SEGMENTS (user_id,voucher_segment) values (?,?)");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "insertInToVoucherSegments Query =" + insertInToVoucherSegments);
    }
    PreparedStatement pstmtInsertInToVoucherSegments = null;

    final StringBuffer insertInToUserGeographics = new StringBuffer("INSERT INTO user_geographies (user_id,");
    insertInToUserGeographics.append("grph_domain_code)");
    insertInToUserGeographics.append(" values (?,?)");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "insertInToUserGeographics Query =" + insertInToUserGeographics);
    }
    PreparedStatement pstmtInsertInToUserGeographics = null;

    final StringBuffer insertInToUserRole = new StringBuffer("INSERT INTO user_roles (user_id,");
    insertInToUserRole.append("role_code) values (?,?)");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "insertInToUserRole Query =" + insertInToUserRole);
    }
    PreparedStatement pstmtInsertInToUserRole = null;

    // Batch geographies insert
    final StringBuffer insertInToBatchGeography = new StringBuffer("INSERT INTO batch_geographies (batch_id, geography_code) ");
    insertInToBatchGeography.append(" VALUES(?,?)");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "insertInToBatchGeography Query =" + insertInToBatchGeography);
    }
    PreparedStatement pstmtInsertInToBatchGeography = null;
    PreparedStatement pstmtselectParentGeography = null;
    ResultSet rsselectParentGeography = null;
    String parentGeography = null;
    // added by shashank
    String parentLoginId = null;
    String parentMsisdn = null;

    final StringBuffer selectExternalCode = new StringBuffer("SELECT 1 FROM users WHERE external_code= ? AND status <> 'N' AND status <> 'C' ");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "selectExternalCode Query =" + selectExternalCode);
    }
    PreparedStatement pstmtSelectExternalCode = null;
    ResultSet rsExternalCode = null;
    final StringBuffer selectPortedMSISDN = new StringBuffer("SELECT 1 FROM ported_msisdn WHERE msisdn=? AND port_type=? ");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "selectPortedMSISDN Query =" + selectPortedMSISDN);
    }
    PreparedStatement pstmtPortedMSISDN = null;
    ResultSet rsPortedMSISDN = null;

    final StringBuffer selectParentDetails = new StringBuffer("SELECT C.category_code, C.web_interface_allowed, C.sms_interface_allowed ");
    selectParentDetails.append(" FROM users U, categories C WHERE U.category_code=C.category_code AND U.user_id=?");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "selectParentDetails Query =" + selectParentDetails);
    }
    PreparedStatement pstmtParentDetails = null;
    ResultSet rsParentDetails = null;

    final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
    try {
        operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
    } catch (Exception e) {
        log.errorTrace(methodName, e);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[addChannelUserList]", "", "", "",
            "Exception while loading the class at the call:" + e.getMessage());
    }

    try {
        pstmtSelectParentUserDetails = p_con.prepareStatement(selectPtrUsrDetail.toString());
        // pstmtSelectParentUserDetailsL=p_con.prepareStatement(selectPtrUsrDetailL.toString());
        pstmtSelectParentUserDetailsP = p_con.prepareStatement(selectPtrUsrDetailP.toString());
      //  pstmtChildGeographyAllowed = p_con.prepareStatement(childGeographyAllowed.toString());
        pstmtSelectUserPhones = p_con.prepareStatement(selectUserPhones.toString());
        pstmtSelectLoginID = p_con.prepareStatement(selectLoginID.toString());
        // added by shashank for batch user creation
        pstmtSelectParentHierarchy = p_con.prepareStatement(selectParentHierarchy.toString());
        // end
        pstmtselectGeography = p_con.prepareStatement(selectGeography.toString());
        pstmtInsertIntoBatches = p_con.prepareStatement(insertIntoBatches.toString());
        pstmtInsertInToUser = p_con.prepareStatement(insertInToUser.toString());
        pstmtInsertInToChnlUser = p_con.prepareStatement(insertInToChnlUser.toString());
        pstmtInsertInToUserPhones = p_con.prepareStatement(insertInToUserPhones.toString());
        pstmtInsertInToUserService = p_con.prepareStatement(insertInToUserService.toString());
        pstmtInsertInToVoucherTypes = p_con.prepareStatement(insertInToVoucherTypes.toString());
        pstmtInsertInToVoucherSegments = p_con.prepareStatement(insertInToVoucherSegments.toString());
       
        pstmtInsertInToUserGeographics = p_con.prepareStatement(insertInToUserGeographics.toString());
        pstmtInsertInToUserRole = p_con.prepareStatement(insertInToUserRole.toString());
        pstmtInsertInToBatchGeography = p_con.prepareStatement(insertInToBatchGeography.toString());
        pstmtupdateIntoBatches = p_con.prepareStatement(updateIntoBatches.toString());
        pstmtSelectExternalCode = p_con.prepareStatement(selectExternalCode.toString());
        pstmtPortedMSISDN = p_con.prepareStatement(selectPortedMSISDN.toString());
        pstmtParentDetails = p_con.prepareStatement(selectParentDetails.toString());
        ChannelUserVO channelUserVO = null;
        int seqNo = 0;
        int updateCount = 0;
        boolean noupdateofphoneanduserid=false;
        final ArrayList geographyDomainCodeList = new ArrayList();
        log.debug("batchuserWebDaoafter preparedStatement"," time taken = "+(System.currentTimeMillis()-startTime)+" ms");
        startTime=System.currentTimeMillis();
        Collections.sort(p_userDetailList);

        long idCounter = 0;
        int commitNumber = 0;
        int userPaddingLength = 0;
        try {
            commitNumber = Integer.parseInt(Constants.getProperty("BATCH_USER_COMMIT_NUMBER"));
            userPaddingLength = Integer.parseInt(Constants.getProperty("USER_PADDING_LENGTH"));
        } catch (Exception e) {
            commitNumber = 100;
            userPaddingLength = 10;
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[addChannelUserList]", "", "", "",
                "Exception:" + e.getMessage());
        }
        CategoryVO categoryVO = null;
        NetworkPrefixVO networkPrefixVO = null;
        String filterMsisdn = null;
        String webAccAllowed = null;
        String smsAccAllowed = null;
        ArrayList msisdnList = null;
        UserPhoneVO userPhoneVO = null;
        if(!inbatch){
        for (int i = 0, length = p_userDetailList.size(); i < length; i++) {
            seqNo = 0;
            parentGeography = null;
            channelUserVO = (ChannelUserVO) p_userDetailList.get(i);
            categoryVO = channelUserVO.getCategoryVO();
            /*
             * Validation 1: Pick category code & match its sequence number
             * with the sequence number
             * of the parent[ based on parent login id & msisdn supplied in
             * XLS file ]. If the seq
             * number of the category=1 then there is no need to supply
             * parent login id and parent
             * msisdn.If the parent info is given then error: No parent info
             * req for top level category.
             * Also validation is done if the parent corresponding to the
             * parent's login ID cannot have
             * the new user as his child based on the category (Added by
             * Ankur June 2011)
             */
            
            rsParentUserDetails = null;
            if (PretupsI.YES.equals(categoryVO.getWebInterfaceAllowed()) && PretupsI.YES.equals(categoryVO.getSmsInterfaceAllowed())) {
                if (!BTSLUtil.isNullString(channelUserVO.getParentLoginID())) {
                    pstmtSelectParentUserDetails.setString(1, channelUserVO.getParentLoginID());
                    pstmtSelectParentUserDetails.setString(2, channelUserVO.getParentMsisdn());
                    rsParentUserDetails = pstmtSelectParentUserDetails.executeQuery();
                } else {
                    pstmtSelectParentUserDetailsP.setString(1, channelUserVO.getParentMsisdn());
                    rsParentUserDetails = pstmtSelectParentUserDetailsP.executeQuery();
                }
            }
            /**
             * else
             * if(PretupsI.YES.equals(categoryVO.getWebInterfaceAllowed()))
             * {
             * if(!BTSLUtil.isNullString(channelUserVO.getParentLoginID()) )
             * {
             * pstmtSelectParentUserDetailsL.setString(1,channelUserVO.
             * getParentLoginID());
             * rsParentUserDetails=pstmtSelectParentUserDetailsL.
             * executeQuery();
             * }
             * else
             * {
             * pstmtSelectParentUserDetailsP.setString(1,channelUserVO.
             * getParentMsisdn());
             * rsParentUserDetails=pstmtSelectParentUserDetailsP.
             * executeQuery();
             * }
             * }
             **/
            else if (PretupsI.YES.equals(categoryVO.getSmsInterfaceAllowed())) {
                pstmtSelectParentUserDetailsP.setString(1, channelUserVO.getParentMsisdn());

                rsParentUserDetails = pstmtSelectParentUserDetailsP.executeQuery();
            }
            seqNo = 0;
            if (rsParentUserDetails.next()) {
                seqNo = rsParentUserDetails.getInt("sequence_no");
                channelUserVO.setOwnerID(rsParentUserDetails.getString("owner_id"));
                channelUserVO.setParentID(rsParentUserDetails.getString("user_id"));
                channelUserVO.setParentLoginID(rsParentUserDetails.getString("login_id"));
                parentGeography = rsParentUserDetails.getString("grph_domain_code");
                // added by shashank
                parentLoginId = rsParentUserDetails.getString("login_id");
                parentMsisdn = rsParentUserDetails.getString("msisdn");
                // end
                
                pstmtChildGeographyAllowed = batchUserWebQry.addChannelUserListChildGeographyAllowedQry(p_con, channelUserVO.getGeographicalCode(), parentGeography);
                rsChildGeographyAllowed = pstmtChildGeographyAllowed.executeQuery();
                if (!rsChildGeographyAllowed.next()) {
                	String error = RestAPIStringParser.getMessage(locale, "bulkuser.initiatebulkuser.msg.error.parentnotingeography", null);
                    errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), error);
                    errorList.add(errorVO);
                    BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + error);
                    continue;
                }
                if (!BTSLUtil.isNullString(channelUserVO.getParentLoginID())) {
                    if (!parentLoginId.equals(channelUserVO.getParentLoginID())) {
                    	String error = RestAPIStringParser.getMessage(locale, "bulkuser.initiatebulkuser.msg.error.parentnotexist", null);
                        errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), error);
                        errorList.add(errorVO);
                        BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + error);
                        continue;
                    }
                }
                if (!BTSLUtil.isNullString(channelUserVO.getParentMsisdn())) {
                    if (!parentMsisdn.equals(channelUserVO.getParentMsisdn())) {
                    	String error = RestAPIStringParser.getMessage(locale, "bulkuser.initiatebulkuser.msg.error.parentnotexist", null);
                    	errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), error);
                        errorList.add(errorVO);
                        BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + error);
                        continue;
                    }
                }

            } else if (channelUserVO.getCategoryVO().getSequenceNumber() > 1) {
            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.initiatebulkuser.msg.error.parentnotexist", null);
                errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), error);
                errorList.add(errorVO);
                BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + error);
                continue;
            }
            pstmtSelectParentUserDetails.clearParameters();

            // Parent Seq num=seqNo
            if (channelUserVO.getCategoryVO().getSequenceNumber() == 1 && !BTSLUtil.isNullString(channelUserVO.getParentMsisdn()) && !BTSLUtil.isNullString(channelUserVO
                .getParentLoginID())) {
                // errorVO=new
                // ListValueVO("WARNING",channelUserVO.getRecordNumber(),p_messages.getMessage(p_locale,"bulkuser.initiatebulkuser.msg.error.hierarchysamewarn"));
                // errorList.add(errorVO);
            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.initiatebulkuser.msg.error.hierarchysamewarn", null);
                BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Success :=" + error);
            } else if (rsParentUserDetails != null && seqNo > channelUserVO.getCategoryVO().getSequenceNumber()) {
            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.initiatebulkuser.msg.error.hierarchyerror", null);
            	errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), error);
                errorList.add(errorVO);
                BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + error);
                continue;
            }

            else if (channelUserVO.getCategoryVO().getSequenceNumber() > 1) {
                pstmtParentDetails.setString(1, channelUserVO.getParentID());
                rsParentDetails = pstmtParentDetails.executeQuery();
                if (rsParentDetails.next()) {
                    webAccAllowed = rsParentDetails.getString("web_interface_allowed");
                    smsAccAllowed = rsParentDetails.getString("sms_interface_allowed");
                }
                if (isLoginPasswordAllowed && PretupsI.YES.equals(webAccAllowed) && BTSLUtil.isNullString(channelUserVO.getParentLoginID())) {
                	String error = RestAPIStringParser.getMessage(locale, "bulkuser.initiatebulkuser.msg.error.parentidmissing", null);
                	errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), error);
                    errorList.add(errorVO);
                    BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + error);
                    continue;
                }
                if (PretupsI.YES.equals(smsAccAllowed) && BTSLUtil.isNullString(channelUserVO.getParentMsisdn())) {
                	String error = RestAPIStringParser.getMessage(locale, "bulkuser.initiatebulkuser.msg.error.parentmsisdnmissing", null);
                	errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), error);
                    errorList.add(errorVO);
                    BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + error);
                    continue;
                }
                pstmtParentDetails.clearParameters();
                webAccAllowed = "N";
                smsAccAllowed = "N";

                // Check for parent hierarchy added by shashank
                pstmtSelectParentHierarchy.setString(1, channelUserVO.getParentMsisdn());
                pstmtSelectParentHierarchy.setString(2, channelUserVO.getCategoryCode());
                pstmtSelectParentHierarchy.setString(3, PretupsI.YES);
                rsSelectParentHierarchy = pstmtSelectParentHierarchy.executeQuery();
                if (!rsSelectParentHierarchy.next()) {
                	String error = RestAPIStringParser.getMessage(locale, "bulkuser.initiatebulkuser.msg.error.hierarchyerror", null);
                	errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), error);
                    errorList.add(errorVO);
                    BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + error);
                    continue;
                }
                // end
            }
           
            // ************Validation 2: Check for login_id uniqueness, if
            // login id already exists mark error.
            pstmtSelectLoginID.setString(1, channelUserVO.getLoginID());
            rsSelectLoginID = pstmtSelectLoginID.executeQuery();
            if (rsSelectLoginID.next()) {
            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.initiatebulkuser.msg.error.loginiduniqueerr", new String[] {channelUserVO.getLoginID()});
            	errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), error);
                errorList.add(errorVO);
                BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + error);
                continue;
            }
            
            // *************Validation 3: Check for Geography
            // **************************************
            pstmtselectGeography.setString(1, channelUserVO.getDomainID());
            pstmtselectGeography.setString(2, channelUserVO.getGeographicalCode());
            pstmtselectGeography.setString(3, channelUserVO.getCategoryCode());
            rsselectGeography = pstmtselectGeography.executeQuery();
            if (!rsselectGeography.next()) {
            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.initiatebulkuser.msg.error.geographyerr", new String[] { channelUserVO.getGeographicalCode(), channelUserVO.getCategoryCode() });
            	errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), error);
                errorList.add(errorVO);
                BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + error);
                continue;
            }
            /*
             * 26/04/07 Code Added for MNP
             * Preference to check whether MNP is allowed in system or not.
             * If yes then check whether Number has not been ported out, If
             * yes then throw error, else continue
             */
            
            final String[] msisdnInput = channelUserVO.getMultipleMsisdnlist().split(",");
            boolean checkMsisdn = false;
            boolean checkMnpAllowed = false;
            final StringBuffer errorMsisdn = new StringBuffer();
            final StringBuffer errorMnpMsisdn = new StringBuffer();
            for (int k = 0, j = msisdnInput.length; k < j; k++) {
                if (isMNPAllowed) {
                    // filterMsisdn=channelUserVO.getMsisdn();
                    filterMsisdn = msisdnInput[k].trim();
                    networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(filterMsisdn));
                    if (networkPrefixVO == null) {
                        errorMnpMsisdn.append(msisdnInput[k]);
                        errorMnpMsisdn.append(",");
                        checkMnpAllowed = true;
                    } else if (networkPrefixVO.getOperator().equals(PretupsI.OPERATOR_TYPE_PORT)) {
                        pstmtPortedMSISDN.setString(1, filterMsisdn);
                        pstmtPortedMSISDN.setString(2, PretupsI.PORTED_IN);
                        rsPortedMSISDN = pstmtPortedMSISDN.executeQuery();
                        if (!rsPortedMSISDN.next()) {
                            // errorVO=new
                            // ListValueVO("",channelUserVO.getRecordNumber(),p_messages.getMessage(p_locale,"bulkuser.processuploadedfile.error.nonetworkprefixfound",new
                            // String[]{msisdnInput[k]}));
                            // errorList.add(errorVO);
                            // BatchesLog.log("BULKUSRINITIATE",channelUserVO,null,"Fail :="+p_messages.getMessage("bulkuser.processuploadedfile.error.nonetworkprefixfound",new
                            // String[]{msisdnInput[k]}));
                            // continue;
                            errorMnpMsisdn.append(msisdnInput[k]);
                            errorMnpMsisdn.append(",");
                            checkMnpAllowed = true;
                        }
                    } else {
                        pstmtPortedMSISDN.setString(1, filterMsisdn);
                        pstmtPortedMSISDN.setString(2, PretupsI.PORTED_OUT);
                        rsPortedMSISDN = pstmtPortedMSISDN.executeQuery();
                        if (rsPortedMSISDN.next()) {
                            // errorVO=new
                            // ListValueVO("",channelUserVO.getRecordNumber(),p_messages.getMessage(p_locale,"bulkuser.processuploadedfile.error.nonetworkprefixfound",new
                            // String[]{msisdnInput[k]}));
                            // errorList.add(errorVO);
                            // BatchesLog.log("BULKUSRINITIATE",channelUserVO,null,"Fail :="+p_messages.getMessage("bulkuser.processuploadedfile.error.nonetworkprefixfound",new
                            // String[]{msisdnInput[k]}));
                            // continue;
                            errorMnpMsisdn.append(msisdnInput[k]);
                            errorMnpMsisdn.append(",");
                            checkMnpAllowed = true;
                        }
                    }
                    pstmtPortedMSISDN.clearParameters();
                }
                // 26/04/07: MNP Code End
                // **********Validation 4: Check for the uniqueness of the
                // msisdn
                // pstmtSelectUserPhones.setString(1,channelUserVO.getMsisdn());
                pstmtSelectUserPhones.setString(1, msisdnInput[k].trim());
                rsUserPhones = pstmtSelectUserPhones.executeQuery();
                if (rsUserPhones.next()) {
                    // errorVO=new
                    // ListValueVO("",channelUserVO.getRecordNumber(),p_messages.getMessage(p_locale,"bulkuser.initiatebulkuser.msg.error.msisdnalreadyassigned",new
                    // String[]{channelUserVO.getMsisdn()}));
                    // errorList.add(errorVO);
                    // BatchesLog.log("BULKUSRINITIATE",channelUserVO,null,"Fail :="+p_messages.getMessage("bulkuser.initiatebulkuser.msg.error.msisdnalreadyassigned",new
                    // String[]{channelUserVO.getMsisdn()}));
                    // BatchesLog.log("BULKUSRINITIATE",channelUserVO,null,"Fail :="+p_messages.getMessage("bulkuser.initiatebulkuser.msg.error.msisdnalreadyassigned",new
                    // String[]{msisdnInput[k]}));
                    // continue;
                    errorMsisdn.append(msisdnInput[k]);
                    errorMsisdn.append(",");
                    checkMsisdn = true;
                }
            } // end of for loop
              // checking status
            if (checkMnpAllowed) {
            	String error = RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.nonetworkprefixfound",new String[] { errorMnpMsisdn.toString().substring(0, errorMnpMsisdn.toString().length() - 1) }); 
                errorVO = new ListValueVO("", channelUserVO.getRecordNumber(),error);
                errorList.add(errorVO);
                BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + RestAPIStringParser.getMessage(locale, "bulkuser.processuploadedfile.error.nonetworkprefixfound",new String[] { errorMnpMsisdn.toString()}));
                continue;
            } else if (checkMsisdn) {
            	errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), RestAPIStringParser.getMessage(locale, "bulkuser.initiatebulkuser.msg.error.msisdnalreadyassigned", new String[] { errorMsisdn.toString().substring(0,
                        errorMsisdn.toString().length() - 1) }));
                errorList.add(errorVO);
                // BatchesLog.log("BULKUSRINITIATE",channelUserVO,null,"Fail :="+p_messages.getMessage("bulkuser.initiatebulkuser.msg.error.msisdnalreadyassigned",new
                // String[]{channelUserVO.getMsisdn()}));
                BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + RestAPIStringParser.getMessage(locale, "bulkuser.initiatebulkuser.msg.error.msisdnalreadyassigned",
                    new String[] { errorMsisdn.toString() }));
                continue;
            }
            
            // **********Validation 5: Check for the uniqueness of the
            // External code (if given)
            if (!BTSLUtil.isNullString(channelUserVO.getExternalCode())) {
                pstmtSelectExternalCode.setString(1, channelUserVO.getExternalCode().trim());
                rsExternalCode = pstmtSelectExternalCode.executeQuery();
                if (rsExternalCode.next()) {
                	errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), RestAPIStringParser.getMessage(locale, "bulkuser.initiatebulkuser.msg.error.externalcodeuniqueerr", new String[] { channelUserVO.getExternalCode() }));
                    errorList.add(errorVO);
                    BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + RestAPIStringParser.getMessage(locale, "bulkuser.initiatebulkuser.msg.error.externalcodeuniqueerr",
                    		new String[] { channelUserVO.getExternalCode() }));
                    continue;
                }
            }
           
            // ***After all validation insertion starts
            // here********************************************
            // Insert into batches
            if(flag){
            	 // First time make the user_id
                idCounter = idGeneratorDAO.getNextBatchID(p_con, PretupsI.USERID, PretupsI.ALL, channelUserVO.getNetworkID(), channelUserVO.getCreatedOn());
                // First time make the phone_id
                phoneID = idGeneratorDAO.getNextBatchID(p_con, PretupsI.USR_PHONE_ID, PretupsI.ALL, PretupsI.ALL, channelUserVO.getCreatedOn());
            flag = false;
            }
            
            
            if (commitCounter >= commitNumber)// After 100 record commit the
            // records
            {
                // after 100 records update the last_no of the ids table for
                // user_id
                idGeneratorDAO.updateNextBatchID(p_con, PretupsI.USERID, PretupsI.ALL, channelUserVO.getNetworkID(), channelUserVO.getCreatedOn(), idCounter - 1);
                // after 100 records update the last_no of the ids table for
                // phone_id
                idGeneratorDAO.updateNextBatchID(p_con, PretupsI.USR_PHONE_ID, PretupsI.ALL, PretupsI.ALL, channelUserVO.getCreatedOn(), phoneID - 1);
                p_con.commit();
                // after 100 records pick the last_no from the ids table for
                // user_id
                idCounter = idGeneratorDAO.getNextBatchID(p_con, PretupsI.USERID, PretupsI.ALL, channelUserVO.getNetworkID(), channelUserVO.getCreatedOn());
                // after 100 records pick the last_no from the ids table for
                // phone_id
                phoneID = idGeneratorDAO.getNextBatchID(p_con, PretupsI.USR_PHONE_ID, PretupsI.ALL, PretupsI.ALL, channelUserVO.getCreatedOn());
                commitCounter = 0;// reset commit counter
            }
            channelUserVO.setUserID(this.generateUserId(channelUserVO.getNetworkID(), channelUserVO.getCategoryVO().getUserIdPrefix(), idCounter, userPaddingLength));

            // Insert into users table.

            pstmtInsertInToUser.clearParameters();
            index = 0;
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getUserID());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getUserName());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getNetworkID());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getLoginID());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getPassword());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getCategoryCode());
            if (channelUserVO.getCategoryVO().getSequenceNumber() == 1) {
                ++index;
                pstmtInsertInToUser.setString(index, PretupsI.ROOT_PARENT_ID);
            } else {
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getParentID());
            }
            if (channelUserVO.getCategoryVO().getSequenceNumber() == 1) {
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getUserID());
            } else {
                ++index;
                pstmtInsertInToUser.setString(index, channelUserVO.getOwnerID());
            }
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getEmpCode());
            ++index;
            if(((Integer)PreferenceCache.getControlPreference(PreferenceI.USER_APPROVAL_LEVEL,channelUserVO.getNetworkID(),channelUserVO.getCategoryCode())).intValue() >0  ){
	        	pstmtInsertInToUser.setString(index,PretupsI.USER_STATUS_NEW);//N New
            }
            else
            {
            	if (LookupsCache.getLookupCodeList(PretupsI.ALLOWED_USER_STATUS).contains(PretupsI.USER_STATUS_PREACTIVE)) {
            		pstmtInsertInToUser.setString(index,PretupsI.USER_STATUS_PREACTIVE);// PA
            		// Active
            	} else {
            		pstmtInsertInToUser.setString(index,PretupsI.USER_STATUS_ACTIVE);// A
            		// Active
            	}
            }
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getEmail());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getContactNo());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getMsisdn());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getUserType());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getCreatedBy());
            ++index;
            pstmtInsertInToUser.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getCreatedOn()));
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getModifiedBy());
            ++index;
            pstmtInsertInToUser.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getModifiedOn()));
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getAddress1());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getAddress2());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getCity());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getState());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getCountry());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getSsn());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getUserNamePrefix());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getExternalCode());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getShortName());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getUserCode());
            if (channelUserVO.getAppointmentDate() != null) {
                ++index;
                pstmtInsertInToUser.setDate(index, BTSLUtil.getSQLDateFromUtilDate(channelUserVO.getAppointmentDate()));
            } else {
                ++index;
                pstmtInsertInToUser.setDate(index, null);
            }
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getPreviousStatus());
            ++index;
            pstmtInsertInToUser.setString(index, batchID);
            ++index;
            pstmtInsertInToUser.setString(index, PretupsI.BATCH_USR_CREATION_TYPE);
            ++index;
            pstmtInsertInToUser.setString(index, PretupsI.YES);
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getContactPerson());
            // Added by deepika aggarwal
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getCompany());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getFax());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getFirstName());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getLastName());
            // end
            // added by shashank for rsa authentication
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getRsaFlag());
            // added for OTP
            if (BTSLUtil.isNullString(p_userVO.getAuthTypeAllowed())) {
                ++index;
                pstmtInsertInToUser.setString(index, PretupsI.NO);
            } else {
                ++index;
                pstmtInsertInToUser.setString(index, p_userVO.getAuthTypeAllowed());
            }
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getLongitude());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getLatitude());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getDocumentType());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getDocumentNo());
            ++index;
            pstmtInsertInToUser.setString(index, channelUserVO.getPaymentType());
           
            if (pstmtInsertInToUser.executeUpdate() <= 0) {
                p_con.rollback();
                BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=When inserting users table");
                continue;
            }

            // insert into channel user
            pstmtInsertInToChnlUser.clearParameters();
            index = 0;
            ++index;
            pstmtInsertInToChnlUser.setString(index, channelUserVO.getUserID());
            ++index;
            pstmtInsertInToChnlUser.setString(index, channelUserVO.getUserGrade());
            ++index;
            pstmtInsertInToChnlUser.setString(index, channelUserVO.getContactPerson());
            ++index;
            pstmtInsertInToChnlUser.setString(index, channelUserVO.getTransferProfileID());
            ++index;
            pstmtInsertInToChnlUser.setString(index, channelUserVO.getCommissionProfileSetID());
            ++index;
            pstmtInsertInToChnlUser.setString(index, channelUserVO.getOutletCode());
            ++index;
            pstmtInsertInToChnlUser.setString(index, channelUserVO.getSubOutletCode());
            // for Zebra and Tango by Sanjeew date 09/07/07
            // user_profile_id, mcommerce_service_allow,
            // low_bal_alert_allow, mpay_profile_id
            // pstmtInsertInToChnlUser.setString(++index,channelUserVO.getUserProfileID());
            ++index;
            pstmtInsertInToChnlUser.setString(index, channelUserVO.getUserID());
            ++index;
            pstmtInsertInToChnlUser.setString(index, channelUserVO.getMcommerceServiceAllow());
            ++index;
            pstmtInsertInToChnlUser.setString(index, channelUserVO.getLowBalAlertAllow());
            ++index;
            pstmtInsertInToChnlUser.setString(index, channelUserVO.getMpayProfileID());
            // Added for Transfer Rule by shashank
            ++index;
            pstmtInsertInToChnlUser.setString(index, channelUserVO.getTrannferRuleTypeId());
            // end
            // End Zebra and Tango

            if (pstmtInsertInToChnlUser.executeUpdate() <= 0) {
                p_con.rollback();
                BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=When inserting channel_users table");
                continue;
            }
           
            // insert into user_phones
            if (channelUserVO.getCategoryVO().getSmsInterfaceAllowed().equals(PretupsI.YES)) {
                int phoneinsCount = 0;
                msisdnList = channelUserVO.getMsisdnList();
                // for(int k=0,j=msisdnInput.length;k<j;k++){
                for (int k = 0, msisdnListSize = msisdnList.size(); k < msisdnListSize; k++) {
                    userPhoneVO = (UserPhoneVO) msisdnList.get(k);
                    pstmtInsertInToUserPhones.clearParameters();
                    index = 0;
                    ++index;
                    pstmtInsertInToUserPhones.setString(index, "" + phoneID);
                    phoneID++;
                    // pstmtInsertInToUserPhones.setString(++index,
                    // channelUserVO.getMsisdn());
                    ++index;
                    pstmtInsertInToUserPhones.setString(index, userPhoneVO.getMsisdn().trim());
                    ++index;
                    pstmtInsertInToUserPhones.setString(index, channelUserVO.getUserID());
                    if (channelUserVO.getMsisdn().equalsIgnoreCase(userPhoneVO.getMsisdn().trim())) {
                        ++index;
                        pstmtInsertInToUserPhones.setString(index, PretupsI.YES);
                    } else {
                        ++index;
                        pstmtInsertInToUserPhones.setString(index, PretupsI.NO);
                    }
                    ++index;
                    pstmtInsertInToUserPhones.setString(index, userPhoneVO.getSmsPin());
                    // pstmtInsertInToUserPhones.setString(++index,
                    // channelUserVO.getSmsPin());
                    ++index;
                    pstmtInsertInToUserPhones.setString(index, PretupsI.YES);
                    ++index;
                    pstmtInsertInToUserPhones.setString(index, channelUserVO.getCategoryCode());
                    // pstmtInsertInToUserPhones.setString(++index,
                    // defaultLanguage);
                    ++index;
                    pstmtInsertInToUserPhones.setString(index, userPhoneVO.getPhoneLanguage());
                    // pstmtInsertInToUserPhones.setString(++index,
                    // defaultCountry);
                    ++index;
                    pstmtInsertInToUserPhones.setString(index, userPhoneVO.getCountry());
                    ++index;
                    pstmtInsertInToUserPhones.setString(index, channelUserVO.getCreatedBy());
                    ++index;
                    pstmtInsertInToUserPhones.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getCreatedOn()));
                    ++index;
                    pstmtInsertInToUserPhones.setString(index, channelUserVO.getModifiedBy());
                    ++index;
                    pstmtInsertInToUserPhones.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getModifiedOn()));
                    prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(PretupsBL.getFilteredMSISDN(channelUserVO.getMsisdn())));
                    ++index;
                    pstmtInsertInToUserPhones.setInt(index, Integer.parseInt(Long.toString(prefixVO.getPrefixID())));
                    ++index;
                    pstmtInsertInToUserPhones.setInt(index, 0);
                    ++index;
                    pstmtInsertInToUserPhones.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getCreatedOn()));
                    ++index;
                    pstmtInsertInToUserPhones.setString(index, PretupsI.YES);
                    phoneinsCount++;
                    if (pstmtInsertInToUserPhones.executeUpdate() <= 0) {
                        phoneinsCount--;
                        // p_con.rollback();
                        BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=When inserting users_phones table");
                        // continue;
                    }
                }
                if (msisdnInput.length < phoneinsCount) {
                    p_con.rollback();
                    continue;
                }
            }
            
            if (channelUserVO.getCategoryVO().getServiceAllowed().equals(PretupsI.YES)) {
                // insert into user_services
                for (int ii = 0, j = channelUserVO.getServiceList().size(); ii < j; ii++) {
                    pstmtInsertInToUserService.clearParameters();
                    index = 0;
                    ++index;
                    pstmtInsertInToUserService.setString(index, channelUserVO.getUserID());
                    ++index;
                    pstmtInsertInToUserService.setString(index, (String) channelUserVO.getServiceList().get(ii));
                    pstmtInsertInToUserService.executeUpdate();
                }
            }
            //Voucher Types
            if(isUserVoucherTypeAllowed && channelUserVO.getVoucherList() != null) {
                // insert into user_vouchertypes
                for (int ii = 0, j = channelUserVO.getVoucherList().size(); ii < j; ii++) {
                	pstmtInsertInToVoucherTypes.clearParameters();
                    index = 0;
                    ++index;
                    pstmtInsertInToVoucherTypes.setString(index, channelUserVO.getUserID());
                    ++index;
                    pstmtInsertInToVoucherTypes.setString(index, (String) channelUserVO.getVoucherList().get(ii));
                    pstmtInsertInToVoucherTypes.executeUpdate();
                }
            }
            if(channelUserVO.getSegmentList() != null) {
            	for (int ii = 0, j = channelUserVO.getSegmentList().size(); ii < j; ii++) {
                	pstmtInsertInToVoucherSegments.clearParameters();
                    index = 0;
                    ++index;
                    pstmtInsertInToVoucherSegments.setString(index, channelUserVO.getUserID());
                    ++index;
                    pstmtInsertInToVoucherSegments.setString(index, (String) channelUserVO.getSegmentList().get(ii));
                    pstmtInsertInToVoucherSegments.executeUpdate();
                }                	
            }
            
            log.debug("batchuserWebDaoafter insert into  users services"," time taken = "+(System.currentTimeMillis()-startTime)+" ms");
            startTime=System.currentTimeMillis();
            pstmtInsertInToUserGeographics.clearParameters();
            index = 0;
            ++index;
            pstmtInsertInToUserGeographics.setString(index, channelUserVO.getUserID());
            ++index;
            pstmtInsertInToUserGeographics.setString(index, channelUserVO.getGeographicalCode());
            if (pstmtInsertInToUserGeographics.executeUpdate() <= 0) {
                p_con.rollback();
                BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=When inserting users_geographics table");
                continue;
            } else// make the array list of geography code
            {
                if (!geographyDomainCodeList.contains(channelUserVO.getGeographicalCode())) {
                    geographyDomainCodeList.add(channelUserVO.getGeographicalCode());
                }
            }
            
            if (channelUserVO.getCategoryVO().getWebInterfaceAllowed().equals(PretupsI.YES)) {
                pstmtInsertInToUserRole.clearParameters();
                index = 0;
                ++index;
                pstmtInsertInToUserRole.setString(index, channelUserVO.getUserID());
                ++index;
                pstmtInsertInToUserRole.setString(index, channelUserVO.getGroupRoleCode());
                if (!BTSLUtil.isNullString(channelUserVO.getLoginID())) {
                    if (pstmtInsertInToUserRole.executeUpdate() <= 0) {
                        p_con.rollback();
                        BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=When inserting users_roles table");
                        continue;
                    }
                }
            }
            
            commitCounter++;
            idCounter++;
            updateCount++;
            ChannelUserLog.log("BULKUSRINITIATE", channelUserVO, p_userVO, true, "Add channel user from bulk");
        }
    }
        if(updateCount==0&&insertintoBatches){
        	updateCount++;
        	channelUserVO = (ChannelUserVO) p_userDetailList.get(0);
        	noupdateofphoneanduserid = true;
        }
        if (updateCount > 0)// if any user information insert then insert
        // into batch geography and update batches
        {
        	if(!noupdateofphoneanduserid){
            idGeneratorDAO.updateNextBatchID(p_con, PretupsI.USERID, PretupsI.ALL, channelUserVO.getNetworkID(), channelUserVO.getCreatedOn(), idCounter - 1);
            idGeneratorDAO.updateNextBatchID(p_con, PretupsI.USR_PHONE_ID, PretupsI.ALL, PretupsI.ALL, channelUserVO.getCreatedOn(), phoneID - 1);
        	}
            if(geographyDomainCodeList.isEmpty()){
            if (!geographyDomainCodeList.contains(channelUserVO.getGeographicalCode())) {
                geographyDomainCodeList.add(channelUserVO.getGeographicalCode());
            }
            }
            String geoDomainCode = "'";
            for (int i = 0, j = geographyDomainCodeList.size(); i < j; i++) {
                geoDomainCode = geoDomainCode + geographyDomainCodeList.get(i) + "','";
            }
            geoDomainCode = geoDomainCode.substring(0, geoDomainCode.length() - 2);

            final UserGeographiesVO userGeographiesVO = p_userVO.getGeographicalAreaList().get(0);
            final int geoTypeSeqNo = userGeographiesVO.getGraphDomainSequenceNumber();

            // get the parent geographical domain code listing
            
            pstmtselectParentGeography = batchUserWebQry.addChannelUserListSelectParentGeographyQry(p_con, geoDomainCode, geoTypeSeqNo);
            rsselectParentGeography = pstmtselectParentGeography.executeQuery();
            log.debug("batchuserWebDaoafter insert into  batch geographies"," time taken = "+(System.currentTimeMillis()-startTime)+" ms");
            // insert into batch_geographics table
            if(insertintoBatches){
            while (rsselectParentGeography.next()) {
                pstmtInsertInToBatchGeography.clearParameters();
                pstmtInsertInToBatchGeography.setString(1, batchID);
                pstmtInsertInToBatchGeography.setString(2, rsselectParentGeography.getString("grph_domain_code"));
                if (pstmtInsertInToBatchGeography.executeUpdate() <= 0) {
                    p_con.rollback();
                    BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=When inserting batch_geographics table");
                    continue;
                }
            }
            if(!errorList.isEmpty()){
            	total = total - errorList.size();
            }
            pstmtInsertIntoBatches.setString(1, batchID);
            pstmtInsertIntoBatches.setString(2, PretupsI.BULK_USR_BATCH_TYPE);
            pstmtInsertIntoBatches.setInt(3, total);
            pstmtInsertIntoBatches.setString(4, channelUserVO.getBatchName());
            pstmtInsertIntoBatches.setString(5, channelUserVO.getNetworkID());
            pstmtInsertIntoBatches.setString(6, PretupsI.USR_BATCH_STATUS_UNDERPROCESS);
            pstmtInsertIntoBatches.setString(7, channelUserVO.getCreatedBy());
            pstmtInsertIntoBatches.setTimestamp(8, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getCreatedOn()));
            pstmtInsertIntoBatches.setString(9, channelUserVO.getModifiedBy());
            pstmtInsertIntoBatches.setTimestamp(10, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getModifiedOn()));
            pstmtInsertIntoBatches.setString(11, p_fileName);
            if (pstmtInsertIntoBatches.executeUpdate() <= 0) {
                p_con.rollback();
                /*BatchesLog.log("BULKUSRINITIATE", channelUserVO, null, "Fail :=" + p_messages.getMessage("bulkuser.initiatebulkuser.err.batchnotcreated"));
                throw new BTSLBaseException(this, methodName, "bulkuser.initiatebulkuser.err.batchnotcreated", "selectDomainForInitiate");*/
            }
            insertintoBatches = false;
            errorVO = new ListValueVO("BATCHID", "", batchID);
            errorList.add(errorVO);
            }
            // update batches table for updating updateCount on the batch
            // size
            pstmtupdateIntoBatches.setInt(1, total);
            if(((Integer)PreferenceCache.getControlPreference(PreferenceI.USER_APPROVAL_LEVEL,channelUserVO.getNetworkID(),channelUserVO.getCategoryCode())).intValue() >0  ){
	    	    pstmtupdateIntoBatches.setString(2,PretupsI.USR_BATCH_STATUS_OPEN);
	        }
    	    else 
    	    	pstmtupdateIntoBatches.setString(2,PretupsI.USR_BATCH_STATUS_CLOSE);
            pstmtupdateIntoBatches.setString(3, batchID);
            pstmtupdateIntoBatches.executeUpdate();
            log.debug("batchuserWebDao last main thread into  batches "," time taken = "+(System.currentTimeMillis()-startTime)+" ms");
            p_con.commit();
        } else {
            p_con.rollback();
        }

    } catch (SQLException sqe) {
        try {
            if (p_con != null) {
                p_con.rollback();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        log.error(methodName, "SQLException : " + sqe);
        log.errorTrace(methodName, sqe);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[addChannelUserList]", "", "", "",
            "SQL Exception:" + sqe.getMessage());
        throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
    } catch (Exception ex) {
        try {
            if (p_con != null) {
                p_con.rollback();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        log.error(methodName, "Exception : " + ex);
        log.errorTrace(methodName, ex);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[addChannelUserList]", "", "", "",
            "Exception:" + ex.getMessage());
        throw new BTSLBaseException(this, methodName, "error.general.processing");
    } finally {
        try {
            if (rsUserPhones != null) {
                rsUserPhones.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (rsParentUserDetails != null) {
                rsParentUserDetails.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (rsSelectLoginID != null) {
                rsSelectLoginID.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (rsselectGeography != null) {
                rsselectGeography.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (rsselectParentGeography != null) {
                rsselectParentGeography.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (rsChildGeographyAllowed != null) {
                rsChildGeographyAllowed.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (rsExternalCode != null) {
                rsExternalCode.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (rsPortedMSISDN != null) {
                rsPortedMSISDN.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (rsParentDetails != null) {
                rsParentDetails.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (rsSelectParentHierarchy != null) {
                rsSelectParentHierarchy.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
    	try {
            if (pstmtInsertInToVoucherTypes != null) {
            	pstmtInsertInToVoucherTypes.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
    	try {
            if (pstmtInsertInToVoucherSegments != null) {
            	pstmtInsertInToVoucherSegments.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtSelectParentUserDetails != null) {
                pstmtSelectParentUserDetails.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtSelectParentUserDetailsP != null) {
                pstmtSelectParentUserDetailsP.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtSelectUserPhones != null) {
                pstmtSelectUserPhones.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtSelectLoginID != null) {
                pstmtSelectLoginID.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtselectGeography != null) {
                pstmtselectGeography.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtselectParentGeography != null) {
                pstmtselectParentGeography.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtChildGeographyAllowed != null) {
                pstmtChildGeographyAllowed.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }

        try {
            if (pstmtInsertInToUser != null) {
                pstmtInsertInToUser.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtInsertInToChnlUser != null) {
                pstmtInsertInToChnlUser.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtInsertInToUserPhones != null) {
                pstmtInsertInToUserPhones.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtInsertInToUserService != null) {
                pstmtInsertInToUserService.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtInsertInToUserGeographics != null) {
                pstmtInsertInToUserGeographics.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtInsertInToUserRole != null) {
                pstmtInsertInToUserRole.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtInsertInToBatchGeography != null) {
                pstmtInsertInToBatchGeography.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtInsertIntoBatches != null) {
                pstmtInsertIntoBatches.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtupdateIntoBatches != null) {
                pstmtupdateIntoBatches.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtSelectExternalCode != null) {
                pstmtSelectExternalCode.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtPortedMSISDN != null) {
                pstmtPortedMSISDN.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtParentDetails != null) {
                pstmtParentDetails.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        // added by shashank
        try {
            if (pstmtSelectParentHierarchy != null) {
                pstmtSelectParentHierarchy.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        // end

        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exiting: errorList size =" + errorList.size());
        }
    }
    return errorList;
}
public ArrayList modifyBulkChannelUserList(Connection con, ArrayList p_userDetailList, String p_domainCode, Locale locale, String p_fileName) throws BTSLBaseException {

	final String methodName = "modifyBulkChannelUserList";
    if (log.isDebugEnabled()) {
        log.debug(
            methodName,
            "Entered: p_userDetailList.size()=" + p_userDetailList.size() + " p_domainCode=" + p_domainCode + " locale=" + locale + " p_fileName: " + p_fileName);
    }
    Boolean isBatchUserPasswdModifyAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.BATCH_USER_PASSWD_MODIFY_ALLOWED);
    Boolean isLmsAppl = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL);
    String pinpasEnDeCryptionType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE);
    String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
    String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
    Boolean isLoginPasswordAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LOGIN_PASSWORD_ALLOWED);
    Boolean isMNPAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MNP_ALLOWED);
    Boolean isUserVoucherTypeAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED);
    Boolean isEmailServiceAllow = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW);
    final ArrayList errorList = new ArrayList();
    ListValueVO errorVO = null;
    int commitCounter = 0;
    int index = 0;

    // Assign phone validation & mobile number validation unique
    final StringBuffer selectUserPhones = new StringBuffer("SELECT 1 from user_phones UP,users U ");
    selectUserPhones.append(" WHERE UP.msisdn = ? AND UP.user_id = U.user_id  AND U.status <> 'N' AND U.status <> 'C' AND U.user_id !=? ");
    // "AND primary_number='Y' ");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "selectUserPhones Query =" + selectUserPhones);
    }
    PreparedStatement pstmtSelectUserPhones = null;
    ResultSet rsUserPhones = null;

    // Unique check for login id
    final StringBuffer selectLoginID = new StringBuffer("SELECT 1 FROM users U WHERE U.login_id=? AND U.user_id !=? ");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "selectLoginID Query =" + selectLoginID);
    }
    PreparedStatement pstmtSelectLoginID = null;
    ResultSet rsSelectLoginID = null;

    final StringBuffer updateInToUser = new StringBuffer("UPDATE users SET user_name=?,user_name_prefix=?,login_id=?, ");

    if (isBatchUserPasswdModifyAllowed) {
        updateInToUser.append("password=?,");
    }

    updateInToUser.append("short_name=?,employee_code=?, ");
    updateInToUser.append("external_code=?,contact_person=?,contact_no=?, ");
    updateInToUser.append("ssn=?,designation=?,address1=?,address2=?,city=?,state=?,country=?,company=?,fax=?,email=?,firstname=?,lastname=?, ");// company,fax,fname,lname
    // added
    // by
    // deepika
    // aggarwal
    updateInToUser.append("modified_on=?, modified_by=?, rsaflag=?,authentication_allowed=?, ");
    updateInToUser.append("msisdn=?,user_code=? ");
    updateInToUser.append(" ,LONGITUDE=?, LATITUDE=?, document_type=?, document_no=?, payment_type=? ");
    updateInToUser.append(" WHERE user_id=?");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "updateInToUser Query =" + updateInToUser);
    }
    PreparedStatement pstmtupdateInToUser = null;

    final StringBuffer selectExternalCode = new StringBuffer("SELECT 1 FROM users WHERE external_code= ? AND user_id !=? AND status <> 'N' AND status <> 'C'");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "selectExternalCode Query =" + selectExternalCode);
    }
    PreparedStatement pstmtSelectExternalCode = null;
    ResultSet rsExternalCode = null;

    final StringBuffer deleteFromUserRoles = new StringBuffer("DELETE FROM user_roles WHERE user_id=?");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "deleteFromUserRoles Query =" + deleteFromUserRoles);
    }
    PreparedStatement pstmtDeleteFromUserRoles = null;

    final StringBuffer insertInUserRoles = new StringBuffer("INSERT INTO user_roles (user_id,role_code)VALUES (?,?) ");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "insertInUserRoles Query =" + insertInUserRoles);
    }
    PreparedStatement pstmtInsertInUserRoles = null;

    final StringBuffer deleteFromUserServices = new StringBuffer("DELETE FROM user_services WHERE user_id=?");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "deleteFromUserServices Query =" + deleteFromUserServices);
    }
    PreparedStatement pstmtDeleteFromUserServices = null;

    final StringBuffer insertInUserServices = new StringBuffer("INSERT INTO user_services(user_id,service_type ) VALUES (?,?)");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "insertInUserServices Query =" + insertInUserServices);
    }
    PreparedStatement pstmtInsertInUserServices = null;

    //Voucher Type
    final StringBuffer deleteFromUserVoucherTypes = new StringBuffer("DELETE FROM user_vouchertypes WHERE user_id=?");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "deleteFromUserVoucherTypes Query =" + deleteFromUserVoucherTypes);
    }
    PreparedStatement pstmtDeleteFromUserVoucherTypes = null;

    final StringBuffer insertInUserVoucherTypes = new StringBuffer("INSERT INTO user_vouchertypes(user_id,voucher_type ) VALUES (?,?)");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "insertInUserVoucherTypes Query =" + insertInUserVoucherTypes);
    }
    PreparedStatement pstmtInsertInUserVoucherTypes = null;
    
    


    
    final StringBuffer deleteFromUserVoucherSegments = new StringBuffer("DELETE FROM USER_VOUCHER_SEGMENTS WHERE user_id=?");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "deleteFromUserVoucherSegments Query =" + deleteFromUserVoucherSegments);
    }
    PreparedStatement pstmtDeleteFromUserVoucherSegments = null;

    final StringBuffer insertInUserVoucherSegments = new StringBuffer("INSERT INTO USER_VOUCHER_SEGMENTS(user_id,voucher_segment ) VALUES (?,?)");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "insertInUserVoucherSegments Query =" + insertInUserVoucherSegments);
    }
    PreparedStatement pstmtInsertInUserVoucherSegments = null;
    
    
    final StringBuffer deleteFromUserGeography = new StringBuffer("DELETE FROM user_geographies WHERE user_id=?");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "deleteFromUserGeography Query =" + deleteFromUserGeography);
    }
    PreparedStatement pstmtDeleteFromUserGeography = null;

    final StringBuffer insertInUserGeography = new StringBuffer("INSERT INTO user_geographies(user_id,grph_domain_code) VALUES (?,?)");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "insertInUserGeography Query =" + insertInUserGeography);
    }
    PreparedStatement pstmtInsertInUserGeography = null;

    final StringBuffer updateInToChanelUsers = new StringBuffer("UPDATE channel_users SET contact_person=?, ");
    updateInToChanelUsers.append("in_suspend=?,out_suspend=?,outlet_code=?,suboutlet_code=?, ");
    // updateInToChanelUsers.append("in_suspend=?,out_suspend=?, ");

    // for Zebra and Tango by Sanjeew date 11/07/07
    updateInToChanelUsers.append("low_bal_alert_allow=? ");
    // End Zebra and Tango
    // added by shashank for trf
    updateInToChanelUsers.append(", trf_rule_type=? ");
    // end
    if (isLmsAppl) {
        updateInToChanelUsers.append(", lms_profile=? ");
        updateInToChanelUsers.append(", lms_profile_updated_on=? ");
    }
    updateInToChanelUsers.append(", COMM_PROFILE_SET_ID = ? ");
    updateInToChanelUsers.append(", TRANSFER_PROFILE_ID = ? ");
    updateInToChanelUsers.append(", USER_GRADE = ? ");
    updateInToChanelUsers.append("WHERE user_id=?");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "updateInToChanelUsers Query =" + updateInToChanelUsers);
    }
    PreparedStatement pstmtupdateInToChanelUsers = null;

    // StringBuffer updateInToUserPhones = new
    // StringBuffer("UPDATE user_phones SET msisdn=?, ");
    final StringBuffer updateInToUserPhones = new StringBuffer("UPDATE user_phones SET primary_number=?, ");
    if (!"SHA".equalsIgnoreCase(pinpasEnDeCryptionType))
    updateInToUserPhones.append("sms_pin=?, ");
   
    updateInToUserPhones.append("modified_by=?,modified_on=?,phone_language=?,country=?  ");// phone_language,country
    // added
    // by
    // deepika
    // aggarwal
    // updateInToUserPhones.append(" WHERE user_id=? AND primary_number='Y' ");
    updateInToUserPhones.append(" WHERE user_id=? AND msisdn=? ");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "updateInToUserPhones Query =" + updateInToUserPhones);
    }
    PreparedStatement pstmtupdateInToUserPhones = null;

    final StringBuffer selectPortedMSISDN = new StringBuffer("SELECT 1 FROM ported_msisdn WHERE msisdn=? AND port_type=? ");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "selectPortedMSISDN Query =" + selectPortedMSISDN);
    }
    PreparedStatement pstmtPortedMSISDN = null;
    ResultSet rsPortedMSISDN = null;

    // added to update SIMTxnID of the MSISDN in user_phones : Zafar Abbas
    final StringBuffer selectPhoneExists = new StringBuffer("SELECT 1 from user_phones UP,users U ");
    selectPhoneExists.append(" WHERE UP.msisdn = ? AND U.user_id = ? AND UP.user_id = U.user_id  ");
    selectPhoneExists.append(" AND U.status <> 'N' AND U.status <> 'C'");
    if (log.isDebugEnabled()) {
        log.debug("updateInsertDeleteUserPhoneList", "Query selectPhoneExistsQuery:" + selectPhoneExists);
    }
    PreparedStatement psmtSelectPhone = null;
    ResultSet rsSelectPhone = null;

    final String updateSIMTxnIDQuery = "UPDATE user_phones SET temp_transfer_id =? WHERE msisdn=? ";
    if (log.isDebugEnabled()) {
        log.debug(" updateTransactionId ", " Query :: " + updateSIMTxnIDQuery);
    }
    PreparedStatement psmtSIMTxnID = null;
    // new addition ended here
    final StringBuffer getOldLoginID = new StringBuffer("SELECT U.user_name,U.login_id,U.external_code, U.creation_type,U.status FROM users U WHERE  U.user_id = ? ");
    if (log.isDebugEnabled()) {
        log.debug(" modifyChannelUserList ", " getOldLoginID Query :: " + getOldLoginID.toString());
    }
    PreparedStatement pstmtSelectOldLoinID = null;
    ResultSet rsOldLoginID = null;

    final StringBuffer insertInUserPhones = new StringBuffer("INSERT INTO user_phones (user_phones_id,msisdn,user_id,");
    insertInUserPhones.append("primary_number,sms_pin,pin_required,");
    insertInUserPhones.append("phone_profile,phone_language,country,pin_modified_on,");
    insertInUserPhones.append("created_by,created_on,modified_by,modified_on, prefix_id) values ");
    insertInUserPhones.append("(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "insertInUserPhones Query =" + insertInUserPhones);
    }
    PreparedStatement pstmtInsertInUserPhones = null;

    final StringBuffer deleteFromUserPhones = new StringBuffer("DELETE FROM user_phones WHERE user_id =? AND msisdn=?");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "deleteFromUserPhones Query =" + deleteFromUserPhones);
    }
    PreparedStatement pstmtDeleteFromUserPhones = null;

    final StringBuffer selectAllUserPhones = new StringBuffer("SELECT user_phones_id,msisdn FROM user_phones WHERE user_id=?");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "selectAllUserPhones Query =" + selectAllUserPhones);
    }
    PreparedStatement pstmtSelectAllFromUserPhones = null;
    ResultSet rsAllUserPhones = null;

    try {
        pstmtSelectOldLoinID = con.prepareStatement(getOldLoginID.toString());
        pstmtSelectUserPhones = con.prepareStatement(selectUserPhones.toString());
        pstmtSelectLoginID = con.prepareStatement(selectLoginID.toString());
        pstmtupdateInToUser = con.prepareStatement(updateInToUser.toString());
        pstmtSelectExternalCode = con.prepareStatement(selectExternalCode.toString());
        pstmtDeleteFromUserRoles = con.prepareStatement(deleteFromUserRoles.toString());
        pstmtInsertInUserRoles = con.prepareStatement(insertInUserRoles.toString());
        pstmtDeleteFromUserServices = con.prepareStatement(deleteFromUserServices.toString());
        pstmtInsertInUserServices = con.prepareStatement(insertInUserServices.toString());
        
        pstmtDeleteFromUserVoucherTypes = con.prepareStatement(deleteFromUserVoucherTypes.toString());
        pstmtInsertInUserVoucherTypes = con.prepareStatement(insertInUserVoucherTypes.toString());
        pstmtInsertInUserVoucherSegments = con.prepareStatement(insertInUserVoucherSegments.toString());
        pstmtDeleteFromUserVoucherSegments = con.prepareStatement(deleteFromUserVoucherSegments.toString());
        
        pstmtDeleteFromUserGeography = con.prepareStatement(deleteFromUserGeography.toString());
        pstmtInsertInUserGeography = con.prepareStatement(insertInUserGeography.toString());
        pstmtupdateInToChanelUsers = con.prepareStatement(updateInToChanelUsers.toString());
        pstmtupdateInToUserPhones = con.prepareStatement(updateInToUserPhones.toString());
        pstmtPortedMSISDN = con.prepareStatement(selectPortedMSISDN.toString());
        psmtSIMTxnID = con.prepareStatement(updateSIMTxnIDQuery.toString());
        psmtSelectPhone = con.prepareStatement(selectPhoneExists.toString());
        pstmtInsertInUserPhones = con.prepareStatement(insertInUserPhones.toString());
        pstmtDeleteFromUserPhones = con.prepareStatement(deleteFromUserPhones.toString());
        pstmtSelectAllFromUserPhones = con.prepareStatement(selectAllUserPhones.toString());
        ChannelUserVO channelUserVO = null;
        CategoryVO categoryVO = null;
        int commitNumber = 0;
        // Email for pin & password
        BTSLMessages btslPushMessage = null;
        final Locale defaultLocale = new Locale(defaultLanguage, defaultCountry);
        PushMessage pushMessage = null;
        boolean loginIDChange = false;
        String subject = null;
        EmailSendToUser emailSendToUser = null;

        try {
            commitNumber = Integer.parseInt(Constants.getProperty("BATCH_USER_COMMIT_NUMBER"));
        } catch (Exception e) {
            commitNumber = 100;
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[addChannelUserList]", "", "", "",
                "Exception:" + e.getMessage());
        }
        NetworkPrefixVO networkPrefixVO = null;
        String filterMsisdn = null;
        boolean[] phoneExists = null;
        ArrayList msisdnList = null;
        UserPhoneVO userPhoneVO = null;
        int switchCase = 0;
        for (int i = 0, length = p_userDetailList.size(); i < length; i++) {
            categoryVO = null;
            channelUserVO = (ChannelUserVO) p_userDetailList.get(i);
            categoryVO = channelUserVO.getCategoryVO();
            if (PretupsI.YES.equals(categoryVO.getSmsInterfaceAllowed())) {
                msisdnList = channelUserVO.getMsisdnList();
                // first get old login id for push message related to login
                // id
            }
            if (categoryVO.getWebInterfaceAllowed().equals(PretupsI.YES)){
            pstmtSelectOldLoinID.setString(1, channelUserVO.getUserID());
            rsOldLoginID = pstmtSelectOldLoinID.executeQuery();
			//Gaurav
            if(rsOldLoginID.next())
            {
            if(!rsOldLoginID.getString("login_id").equals(channelUserVO.getLoginID()))
            {
            	loginIDChange=true;
            }
            channelUserVO.setStatus(rsOldLoginID.getString("status"));
            }
            }
            if (categoryVO.getWebInterfaceAllowed().equals(PretupsI.YES) && isLoginPasswordAllowed) {
                // ************Validation 2: Check for login_id uniqueness,
                // if login id already exists mark error.
                pstmtSelectLoginID.clearParameters();
                pstmtSelectLoginID.setString(1, channelUserVO.getLoginID());
                pstmtSelectLoginID.setString(2, channelUserVO.getUserID());
                rsSelectLoginID = pstmtSelectLoginID.executeQuery();
                if (rsSelectLoginID.next()) {
                    errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), RestAPIStringParser.getMessage(locale, "bulkuser.initiatebulkuser.msg.error.loginiduniqueerr",
                        new String[] { channelUserVO.getLoginID() }));
                    errorList.add(errorVO);
                    BatchesLog.log("BULKUSRUPDATE", channelUserVO, null, "Fail :=" + RestAPIStringParser.getMessage(locale, "bulkuser.initiatebulkuser.msg.error.loginiduniqueerr",
                    		new String[] { channelUserVO.getLoginID() }));
                    continue;
                }
            }
            if (PretupsI.YES.equals(categoryVO.getSmsInterfaceAllowed())) {
                // **********Validation 4: Check for the uniqueness of the
                // msisdn
                phoneExists = new boolean[msisdnList.size()];
                boolean exitLoop = false;// this flag is used to exist user
                // loop
                final StringBuffer invalidMsisdn = new StringBuffer();
                switchCase = 0;
                for (int j = 0, msisdnSize = msisdnList.size(); j < msisdnSize; j++) {
                    userPhoneVO = (UserPhoneVO) msisdnList.get(j);
                    pstmtSelectUserPhones.clearParameters();
                    pstmtSelectUserPhones.setString(1, userPhoneVO.getMsisdn());
                    pstmtSelectUserPhones.setString(2, channelUserVO.getUserID());
                    rsUserPhones = pstmtSelectUserPhones.executeQuery();
                    // need to the primary msisdn of the channel user,
                    // primary will be the first msisdn.
                    if (rsUserPhones.next()) {
                        invalidMsisdn.append(userPhoneVO.getMsisdn());
                        invalidMsisdn.append(",");
                        switchCase = 1;
                        BatchesLog.log("BULKUSRUPDATE", channelUserVO, null, "Fail :=" + RestAPIStringParser.getMessage(locale,
                            "bulkuser.initiatebulkuser.msg.error.msisdnalreadyassigned", new String[] { userPhoneVO.getMsisdn() }));
                        exitLoop = true;
                        continue;
                    }
                    // ***********validation 4.1 : Check for the association
                    // of MSISDN with the user
                    // BY : Zafar Abbas , ON : 14/05/2007
                    psmtSelectPhone.setString(1, userPhoneVO.getMsisdn());
                    psmtSelectPhone.setString(2, channelUserVO.getUserID());
                    rsSelectPhone = psmtSelectPhone.executeQuery();
                    if (rsSelectPhone.next()) {
                        phoneExists[j] = true;
                        userPhoneVO.setOperationType("U");
                    } else {
                        phoneExists[j] = false;
                        userPhoneVO.setOperationType("I");
                    }
                    // new addition ended here
                    /*
                     * 26/04/07 Code Added for MNP
                     * Preference to check whether MNP is allowed in system
                     * or not.
                     * If yes then check whether Number has not been ported
                     * out, If yes then throw error, else continue
                     */
                    if (isMNPAllowed) {
                        filterMsisdn = userPhoneVO.getMsisdn();
                        networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(filterMsisdn));
                        if (networkPrefixVO.getOperator().equals(PretupsI.OPERATOR_TYPE_PORT)) {
                            pstmtPortedMSISDN.setString(1, filterMsisdn);
                            pstmtPortedMSISDN.setString(2, PretupsI.PORTED_IN);
                            rsPortedMSISDN = pstmtPortedMSISDN.executeQuery();
                            if (!rsPortedMSISDN.next()) {
                                invalidMsisdn.append(userPhoneVO.getMsisdn());
                                invalidMsisdn.append(",");
                                BatchesLog.log("BULKUSRUPDATE", channelUserVO, null, "Fail :=" + RestAPIStringParser.getMessage(locale, 
                                    "bulkuser.processuploadedfile.error.nonetworkprefixfound", new String[] { userPhoneVO.getMsisdn() }));
                                exitLoop = true;
                                continue;
                            }
                        } else {
                            pstmtPortedMSISDN.setString(1, filterMsisdn);
                            pstmtPortedMSISDN.setString(2, PretupsI.PORTED_OUT);
                            rsPortedMSISDN = pstmtPortedMSISDN.executeQuery();
                            if (rsPortedMSISDN.next()) {
                                invalidMsisdn.append(userPhoneVO.getMsisdn());
                                invalidMsisdn.append(",");
                                BatchesLog.log("BULKUSRUPDATE", channelUserVO, null, "Fail :=" + RestAPIStringParser.getMessage(locale, 
                                    "bulkuser.processuploadedfile.error.nonetworkprefixfound", new String[] { userPhoneVO.getMsisdn() }));
                                exitLoop = true;
                                continue;
                            }
                        }
                        pstmtPortedMSISDN.clearParameters();
                    }
                    // 26/04/07: MNP Code End
                }
                // if any of the specified msisdn for user, has problem,
                // this whole user should be skipped.
                if (exitLoop) {
                    if (switchCase == 1) {
                        errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), RestAPIStringParser.getMessage(locale,
                            "bulkuser.initiatebulkuser.msg.error.msisdnalreadyassigned", new String[] { invalidMsisdn.toString().substring(0,
                                invalidMsisdn.toString().length() - 1) }));
                        errorList.add(errorVO);
                    } else {
                        errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), RestAPIStringParser.getMessage(locale,
                            "bulkuser.processuploadedfile.error.nonetworkprefixfound", new String[] { invalidMsisdn.toString().substring(0,
                                invalidMsisdn.toString().length() - 1) }));
                        errorList.add(errorVO);
                    }
                    continue;
                }

            }

            // **********Validation 5: Check for the uniqueness of the
            // External code (if given)
            if (!BTSLUtil.isNullString(channelUserVO.getExternalCode())) {
                pstmtSelectExternalCode.clearParameters();
                pstmtSelectExternalCode.setString(1, channelUserVO.getExternalCode());
                pstmtSelectExternalCode.setString(2, channelUserVO.getUserID());
                rsExternalCode = pstmtSelectExternalCode.executeQuery();
                if (rsExternalCode.next()) {
                    errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), RestAPIStringParser.getMessage(locale,
                        "bulkuser.initiatebulkuser.msg.error.externalcodeuniqueerr", new String[] { channelUserVO.getExternalCode() }));
                    ArrayList tempExtCode = new ArrayList();
                    tempExtCode.add(errorVO.getOtherInfo());
                    tempExtCode.add(errorVO.getOtherInfo2());
                    errorList.add(tempExtCode);
                    BatchesLog.log("BULKUSRUPDATE", channelUserVO, null, "Fail :=" + RestAPIStringParser.getMessage(locale, "bulkuser.initiatebulkuser.msg.error.externalcodeuniqueerr",
                        new String[] {channelUserVO.getExternalCode()}));
                    continue;
                }
            }
            // ***After all validation insertion starts
            // here********************************************
            // Update into users table.

            pstmtupdateInToUser.clearParameters();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "updateInToUser Query =" + updateInToUser);
            }
            index = 0;
            // pstmtupdateInToUser.setFormOfUse(++index,
            // OraclePreparedStatement.FORM_NCHAR);
            ++index;
            pstmtupdateInToUser.setString(index, channelUserVO.getUserName());
            ++index;
            pstmtupdateInToUser.setString(index, channelUserVO.getUserNamePrefix());
            ++index;
            pstmtupdateInToUser.setString(index, channelUserVO.getLoginID());
            if (isBatchUserPasswdModifyAllowed) {
                ++index;
                pstmtupdateInToUser.setString(index, channelUserVO.getPassword());
            } // pstmtupdateInToUser.setFormOfUse(++index,
              // OraclePreparedStatement.FORM_NCHAR);
            ++index;
            pstmtupdateInToUser.setString(index, channelUserVO.getShortName());
            ++index;
            pstmtupdateInToUser.setString(index, channelUserVO.getEmpCode());
            ++index;
            pstmtupdateInToUser.setString(index, channelUserVO.getExternalCode());
            // pstmtupdateInToUser.setString(++index,channelUserVO.getInSuspend());
            // pstmtupdateInToUser.setString(++index,channelUserVO.getOutSuspened());
            ++index;
            pstmtupdateInToUser.setString(index, channelUserVO.getContactPerson());
            ++index;
            pstmtupdateInToUser.setString(index, channelUserVO.getContactNo());
            ++index;
            pstmtupdateInToUser.setString(index, channelUserVO.getSsn());
            // pstmtupdateInToUser.setFormOfUse(++index,
            // OraclePreparedStatement.FORM_NCHAR);
            ++index;
            pstmtupdateInToUser.setString(index, channelUserVO.getDesignation());
            // pstmtupdateInToUser.setFormOfUse(++index,
            // OraclePreparedStatement.FORM_NCHAR);
            ++index;
            pstmtupdateInToUser.setString(index, channelUserVO.getAddress1());
            // pstmtupdateInToUser.setFormOfUse(++index,
            // OraclePreparedStatement.FORM_NCHAR);
            ++index;
            pstmtupdateInToUser.setString(index, channelUserVO.getAddress2());
            // pstmtupdateInToUser.setFormOfUse(++index,
            // OraclePreparedStatement.FORM_NCHAR);
            ++index;
            pstmtupdateInToUser.setString(index, channelUserVO.getCity());
            // pstmtupdateInToUser.setFormOfUse(++index,
            // OraclePreparedStatement.FORM_NCHAR);
            ++index;
            pstmtupdateInToUser.setString(index, channelUserVO.getState());
            // pstmtupdateInToUser.setFormOfUse(++index,
            // OraclePreparedStatement.FORM_NCHAR);
            ++index;
            pstmtupdateInToUser.setString(index, channelUserVO.getCountry());
            // added by deepika aggarwal
            // pstmtupdateInToUser.setFormOfUse(++index,
            // OraclePreparedStatement.FORM_NCHAR);
            ++index;
            pstmtupdateInToUser.setString(index, channelUserVO.getCompany());
            // pstmtupdateInToUser.setFormOfUse(++index,
            // OraclePreparedStatement.FORM_NCHAR);
            ++index;
            pstmtupdateInToUser.setString(index, channelUserVO.getFax());
            ++index;
            pstmtupdateInToUser.setString(index, channelUserVO.getEmail());
            ++index;
            pstmtupdateInToUser.setString(index, channelUserVO.getFirstName());
            ++index;
            pstmtupdateInToUser.setString(index, channelUserVO.getLastName());
            // end
            // pstmtupdateInToUser.setString(++index,channelUserVO.getEmail());
            ++index;
            pstmtupdateInToUser.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getModifiedOn()));
            ++index;
            pstmtupdateInToUser.setString(index, channelUserVO.getModifiedBy());
            // added by shashank for rsa authentication
            ++index;
            pstmtupdateInToUser.setString(index, channelUserVO.getRsaFlag());
            // end
            // Added for OTP Authentication
            if (BTSLUtil.isNullString(channelUserVO.getAuthTypeAllowed())) {
                ++index;
                pstmtupdateInToUser.setString(index, PretupsI.NO);
            } else {
                ++index;
                pstmtupdateInToUser.setString(index, channelUserVO.getAuthTypeAllowed());
            }// pstmtupdateInToUser.setString(++index,
             // channelUserVO.getAuthTypeAllowed());
            ++index;
            pstmtupdateInToUser.setString(index, channelUserVO.getMsisdn());
            ++index;
            pstmtupdateInToUser.setString(index, channelUserVO.getUserCode());
            // pstmtupdateInToUser.setString(++index,channelUserVO.getContactPerson());
            // Added by Diwakar for updating Longitede & Latitude
            ++index;
            pstmtupdateInToUser.setString(index, channelUserVO.getLongitude());
            ++index;
            pstmtupdateInToUser.setString(index, channelUserVO.getLatitude());
            ++index;
            pstmtupdateInToUser.setString(index, channelUserVO.getDocumentType());
            ++index;
            pstmtupdateInToUser.setString(index, channelUserVO.getDocumentNo());
            ++index;
            pstmtupdateInToUser.setString(index, channelUserVO.getPaymentType());
            ++index;
            pstmtupdateInToUser.setString(index, channelUserVO.getUserID());
            if (pstmtupdateInToUser.executeUpdate() <= 0) {
                errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), RestAPIStringParser.getMessage(locale, "bulkuser.initiatebulkuser.msg.error.updateusertable",
                    new String[] { channelUserVO.getExternalCode() }));
                errorList.add(errorVO);
                con.rollback();
                BatchesLog.log("BULKUSRUPDATE", channelUserVO, null, "Fail :=When inserting users table");
                continue;
            }
            if (categoryVO.getWebInterfaceAllowed().equals(PretupsI.YES)) {
                // Delete into User Roles table
                pstmtDeleteFromUserRoles.clearParameters();
                pstmtDeleteFromUserRoles.setString(1, channelUserVO.getUserID());
                pstmtDeleteFromUserRoles.executeUpdate();
                if ((channelUserVO.getGroupRoleFlag()).equals(PretupsI.NO)) // Group
                // role
                // flag=
                // N
                {
                    final String[] rolecodeArray = ((channelUserVO.getGroupRoleCode()).trim()).split(",");
                    for (int k = 0, j = rolecodeArray.length; k < j; k++) {
                        // insert into User Roles table
                        pstmtInsertInUserRoles.clearParameters();
                        pstmtInsertInUserRoles.setString(1, channelUserVO.getUserID());
                        pstmtInsertInUserRoles.setString(2, rolecodeArray[k].trim());
                        if (channelUserVO.getGroupRoleCode().trim().length() != 0) {
                            if (pstmtInsertInUserRoles.executeUpdate() <= 0) {
                                errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), RestAPIStringParser.getMessage(locale,
                                    "bulkuser.initiatebulkuser.msg.error.insertuserrolestable", new String[] { channelUserVO.getExternalCode() }));
                                errorList.add(errorVO);
                                con.rollback();
                                BatchesLog.log("BULKUSRUPDATE", channelUserVO, null, "Fail :=When inserting User Roles table");
                                continue;
                            }
                        }
                    }
                } else // Group role flag= Y
                {
                    pstmtInsertInUserRoles.clearParameters();
                    pstmtInsertInUserRoles.setString(1, channelUserVO.getUserID());
                    pstmtInsertInUserRoles.setString(2, channelUserVO.getGroupRoleCode());
                    if (pstmtInsertInUserRoles.executeUpdate() <= 0) {
                        errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), RestAPIStringParser.getMessage(locale,
                            "bulkuser.initiatebulkuser.msg.error.insertuserrolestable", new String[] { channelUserVO.getExternalCode() }));
                        errorList.add(errorVO);
                        con.rollback();
                        BatchesLog.log("BULKUSRUPDATE", channelUserVO, null, "Fail :=When inserting User Roles table");
                        continue;
                    }
                }

            }
            if (categoryVO.getServiceAllowed().equals(PretupsI.YES)) {
                // Delete into User Service table
                pstmtDeleteFromUserServices.clearParameters();
                pstmtDeleteFromUserServices.setString(1, channelUserVO.getUserID());
                pstmtDeleteFromUserServices.executeUpdate();
                final String[] serviceArr = channelUserVO.getServiceTypes().split(",");
                for (int k = 0, j = serviceArr.length; k < j; k++) {
                    // insert into User Service table
                    pstmtInsertInUserServices.clearParameters();
                    pstmtInsertInUserServices.setString(1, channelUserVO.getUserID());
                    pstmtInsertInUserServices.setString(2, serviceArr[k].trim());
                    if (pstmtInsertInUserServices.executeUpdate() <= 0) {
                        errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), RestAPIStringParser.getMessage(locale,
                            "bulkuser.initiatebulkuser.msg.error.insertuserservicestable", new String[] { channelUserVO.getExternalCode() }));
                        errorList.add(errorVO);
                        con.rollback();
                        BatchesLog.log("BULKUSRUPDATE", channelUserVO, null, "Fail :=When inserting User Service table");
                        continue;
                    }
                }
            }                                
            //Voucher Type
            if(isUserVoucherTypeAllowed && channelUserVO.getVoucherTypes() != null) {
                // Delete into User Service table
            	pstmtDeleteFromUserVoucherTypes.clearParameters();
            	pstmtDeleteFromUserVoucherTypes.setString(1, channelUserVO.getUserID());
            	pstmtDeleteFromUserVoucherTypes.executeUpdate();
            	
                final String[] voucherTypeArr = channelUserVO.getVoucherTypes().split(",");
                for (int k = 0, j = voucherTypeArr.length; k < j; k++) {
                    // insert into User VoucherTypes table
                	if(!BTSLUtil.isNullString(voucherTypeArr[k])) {
                    	pstmtInsertInUserVoucherTypes.clearParameters();
                    	pstmtInsertInUserVoucherTypes.setString(1, channelUserVO.getUserID());
                    	pstmtInsertInUserVoucherTypes.setString(2, voucherTypeArr[k].trim());
                        if (pstmtInsertInUserVoucherTypes.executeUpdate() <= 0) {
                            errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), RestAPIStringParser.getMessage(locale,
                                "bulkuser.initiatebulkuser.msg.error.insertuservouchertypetable", new String[] { channelUserVO.getExternalCode() }));
                            errorList.add(errorVO);
                            con.rollback();
                            BatchesLog.log("BULKUSRUPDATE", channelUserVO, null, "Fail :=When inserting User VoucherTypes table");
                            continue;
                        }
                	}
                }
            }
            
//            pstmtDeleteFromUserVoucherSegments.clearParameters();
//            pstmtDeleteFromUserVoucherSegments.setString(1, channelUserVO.getUserID());
//            pstmtDeleteFromUserVoucherSegments.executeUpdate();
//        	
//            final String[] voucherSegmentArr = channelUserVO.getSegments().split(",");
//            for (int k = 0, j = voucherSegmentArr.length; k < j; k++) {
//                // insert into User VoucherTypes table
//            	if(!BTSLUtil.isNullString(voucherSegmentArr[k])) {
//                	pstmtInsertInUserVoucherSegments.clearParameters();
//                	pstmtInsertInUserVoucherSegments.setString(1, channelUserVO.getUserID());
//                	pstmtInsertInUserVoucherSegments.setString(2, voucherSegmentArr[k].trim());
//                    if (pstmtInsertInUserVoucherSegments.executeUpdate() <= 0) {
//                        errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), RestAPIStringParser.getMessage(locale,
//                            "bulkuser.initiatebulkuser.msg.error.insertuservouchersegmenttable", new String[] { channelUserVO.getExternalCode() }));
//                        errorList.add(errorVO);
//                        con.rollback();
//                        BatchesLog.log("BULKUSRUPDATE", channelUserVO, null, "Fail :=When inserting USER_VOUCHER_SEGMENTS table");
//                        continue;
//                    }
//            	}
//            }
            
            // Delete into User Geography table
            pstmtDeleteFromUserGeography.clearParameters();
            pstmtDeleteFromUserGeography.setString(1, channelUserVO.getUserID());
            pstmtDeleteFromUserGeography.executeUpdate();
            if (channelUserVO.getGeographicalCode().contains(",")) // more
            // than
            // one
            // geography
            // code
            {
                final String[] geographyArray = (channelUserVO.getGeographicalCode()).split(",");
                for (int k = 0, j = geographyArray.length; k < j; k++) {
                    // insert into User Geography table
                    pstmtInsertInUserGeography.clearParameters();
                    pstmtInsertInUserGeography.setString(1, channelUserVO.getUserID());
                    pstmtInsertInUserGeography.setString(2, geographyArray[k].trim());
                    if (pstmtInsertInUserGeography.executeUpdate() <= 0) {
                        errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), RestAPIStringParser.getMessage(locale,
                            "bulkuser.initiatebulkuser.msg.error.insertusergeographytable", new String[] { channelUserVO.getExternalCode() }));
                        errorList.add(errorVO);
                        con.rollback();
                        BatchesLog.log("BULKUSRUPDATE", channelUserVO, null, "Fail :=When inserting User Geography table");
                        continue;
                    }
                }
            } else {
                // insert into User Geography table
                pstmtInsertInUserGeography.clearParameters();
                pstmtInsertInUserGeography.setString(1, channelUserVO.getUserID());
                pstmtInsertInUserGeography.setString(2, (channelUserVO.getGeographicalCode()));
                if (pstmtInsertInUserGeography.executeUpdate() <= 0) {
                    errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), RestAPIStringParser.getMessage(locale,
                        "bulkuser.initiatebulkuser.msg.error.insertusergeographytable", new String[] { channelUserVO.getExternalCode() }));
                    errorList.add(errorVO);
                    con.rollback();
                    BatchesLog.log("BULKUSRUPDATE", channelUserVO, null, "Fail :=When inserting User Geography table");
                    continue;
                }
            }
            // Update into ChanelUsers table

            pstmtupdateInToChanelUsers.clearParameters();
            index = 0;
            ++index;
            pstmtupdateInToChanelUsers.setString(index, channelUserVO.getContactPerson());
            ++index;
            pstmtupdateInToChanelUsers.setString(index, (channelUserVO.getInSuspend()));
            ++index;
            pstmtupdateInToChanelUsers.setString(index, (channelUserVO.getOutSuspened()));
            if (categoryVO.getOutletsAllowed().equals(PretupsI.YES)) {
                ++index;
                pstmtupdateInToChanelUsers.setString(index, (channelUserVO.getOutletCode()));
                ++index;
                pstmtupdateInToChanelUsers.setString(index, (channelUserVO.getSubOutletCode()));
            } else {
                ++index;
                pstmtupdateInToChanelUsers.setString(index, null);
                ++index;
                pstmtupdateInToChanelUsers.setString(index, null);
            }

            // for Zebra and Tango by Sanjeew date 11/07/07
            ++index;
            pstmtupdateInToChanelUsers.setString(index, channelUserVO.getLowBalAlertAllow());
            // End Zebra and Tango
            // added by shashank for trf
            ++index;
            pstmtupdateInToChanelUsers.setString(index, channelUserVO.getTrannferRuleTypeId());
            // end
            if (isLmsAppl) {
                ++index;
                pstmtupdateInToChanelUsers.setString(index, channelUserVO.getLmsProfile());
                ++index;
                pstmtupdateInToChanelUsers.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(new Date()));
            }
            ++index;
            pstmtupdateInToChanelUsers.setString(index, channelUserVO.getCommissionProfileSetID());
            ++index;
            pstmtupdateInToChanelUsers.setString(index, channelUserVO.getTransferProfileID());
            ++index;
            pstmtupdateInToChanelUsers.setString(index, channelUserVO.getUserGrade());
            ++index;
            pstmtupdateInToChanelUsers.setString(index, channelUserVO.getUserID());
            if (pstmtupdateInToChanelUsers.executeUpdate() <= 0) {
                errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), RestAPIStringParser.getMessage(locale,
                    "bulkuser.initiatebulkuser.msg.error.updatechaneluserstable", new String[] { channelUserVO.getExternalCode() }));
                errorList.add(errorVO);
                con.rollback();
                BatchesLog.log("BULKUSRUPDATE", channelUserVO, null, "Fail :=When inserting ChannelUsers table");
                continue;
            }
            if (PretupsI.YES.equals(categoryVO.getSmsInterfaceAllowed())) {
                // Update into UserPhones table
                boolean existLoop = false;// used to exist the loop if error
                // in updating or inserting any
                // record
                final HashMap msisdnNotToDelete = new HashMap();
                final IDGeneratorDAO idGeneratorDAO = new IDGeneratorDAO();
                long phoneID;
                pstmtSelectAllFromUserPhones.setString(1, channelUserVO.getUserID());
                rsAllUserPhones = pstmtSelectAllFromUserPhones.executeQuery();
                phoneID = idGeneratorDAO.getNextBatchID(con, PretupsI.USR_PHONE_ID, PretupsI.ALL, PretupsI.ALL, channelUserVO.getCreatedOn());
                for (int k = 0, msisdnListSize = msisdnList.size(); k < msisdnListSize; k++) {
                    userPhoneVO = (UserPhoneVO) msisdnList.get(k);
                    int recordCount = 0;
                    if ("U".equalsIgnoreCase(userPhoneVO.getOperationType()))// &&
                    // userPhoneVO.isPinModifyFlag())
                    {
                        msisdnNotToDelete.put(userPhoneVO.getMsisdn(), "");
                        pstmtupdateInToUserPhones.clearParameters();
                        index = 0;
                        if (channelUserVO.getMsisdn().equalsIgnoreCase(userPhoneVO.getMsisdn())) {
                            ++index;
                            pstmtupdateInToUserPhones.setString(index, "Y");
                        } else {
                            ++index;
                            pstmtupdateInToUserPhones.setString(index, "N");
                        }
                        if (!"SHA".equalsIgnoreCase(pinpasEnDeCryptionType)) {
                        	++index;
                        	pstmtupdateInToUserPhones.setString(index, (userPhoneVO.getSmsPin()));
                        }
                        ++index;
                        pstmtupdateInToUserPhones.setString(index, (channelUserVO.getModifiedBy()));
                        ++index;
                        pstmtupdateInToUserPhones.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getModifiedOn()));
                        ++index;
                        pstmtupdateInToUserPhones.setString(index, userPhoneVO.getPhoneLanguage());// added
                        // by
                        // deepika
                        ++index; // aggarwal
                        pstmtupdateInToUserPhones.setString(index, userPhoneVO.getCountry());// added
                        // by
                        // deepika
                        ++index; // aggarwal
                        pstmtupdateInToUserPhones.setString(index, channelUserVO.getUserID());
                        ++index;
                        pstmtupdateInToUserPhones.setString(index, userPhoneVO.getMsisdn());
                        recordCount = pstmtupdateInToUserPhones.executeUpdate();
                    }
                    // if the pin is not modified, so no need to change it,
                    // but need to set as procesed
                    /*
                     * else if
                     * ("U".equalsIgnoreCase(userPhoneVO.getOperationType
                     * ())&& !userPhoneVO.isPinModifyFlag())
                     * {
                     * recordCount=1;
                     * msisdnNotToDelete.put(userPhoneVO.getMsisdn(),"");
                     * }
                     */else if ("I".equalsIgnoreCase(userPhoneVO.getOperationType())) {
                        msisdnNotToDelete.put(userPhoneVO.getMsisdn(), "");
                        pstmtInsertInUserPhones.clearParameters();
                        index = 0;
                        ++index;
                        pstmtInsertInUserPhones.setLong(index, phoneID);
                        ++index;
                        pstmtInsertInUserPhones.setString(index, userPhoneVO.getMsisdn());
                        ++index;
                        pstmtInsertInUserPhones.setString(index, channelUserVO.getUserID());
                        if (channelUserVO.getMsisdn().equalsIgnoreCase(userPhoneVO.getMsisdn())) {
                            ++index;
                            pstmtInsertInUserPhones.setString(index, "Y");
                        } else {
                            ++index;
                            pstmtInsertInUserPhones.setString(index, "N");
                        }
                        ++index;
                        pstmtInsertInUserPhones.setString(index, userPhoneVO.getSmsPin());
                        ++index;
                        pstmtInsertInUserPhones.setString(index, PretupsI.YES);
                        ++index;
                        pstmtInsertInUserPhones.setString(index, categoryVO.getCategoryCode());
                        // modified by deepika aggarwal
                        // pstmtInsertInUserPhones.setString(++index,defaultLanguage);
                        ++index;
                        pstmtInsertInUserPhones.setString(index, userPhoneVO.getPhoneLanguage());
                        // pstmtInsertInUserPhones.setString(++index,defaultCountry);
                        ++index;
                        pstmtInsertInUserPhones.setString(index, userPhoneVO.getCountry());
                        // end modified by deepika aggarwal
                        ++index;
                        pstmtInsertInUserPhones.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getModifiedOn()));
                        ++index;
                        pstmtInsertInUserPhones.setString(index, channelUserVO.getModifiedBy());
                        ++index;
                        pstmtInsertInUserPhones.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getModifiedOn()));
                        ++index;
                        pstmtInsertInUserPhones.setString(index, channelUserVO.getModifiedBy());
                        ++index;
                        pstmtInsertInUserPhones.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getModifiedOn()));
                        ++index;
                        pstmtInsertInUserPhones.setString(index, PretupsBL.getMSISDNPrefix((userPhoneVO.getMsisdn())));
                        recordCount = pstmtInsertInUserPhones.executeUpdate();
                        phoneID++;

                    }

                    // Update the temp_transfer_id , if number is new
                    if (log.isDebugEnabled()) {
                        log.debug("updateInsertDeleteUserPhoneList", "phoneExists = " + phoneExists[k]);
                    }
                    if (!phoneExists[k]) {
                        psmtSIMTxnID.clearParameters();
                        psmtSIMTxnID.setString(1, PretupsI.UPD_SIM_TXN_ID);
                        psmtSIMTxnID.setString(2, userPhoneVO.getMsisdn());
                    }
                    if (recordCount <= 0 || (!phoneExists[k] && psmtSIMTxnID.executeUpdate() <= 0)) {
                        errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), RestAPIStringParser.getMessage(locale,
                            "bulkuser.initiatebulkuser.msg.error.updateuserphonestable", new String[] { channelUserVO.getExternalCode() }));
                        errorList.add(errorVO);
                        con.rollback();
                        BatchesLog.log("BULKUSRUPDATE", channelUserVO, null, "Fail :=When inserting UserPhones table");
                        existLoop = true;
                        break;
                    }
                }

                // it will skip the current user record as there is problem
                // in processing
                if (existLoop) {
                    continue;
                }

                while (rsAllUserPhones.next()) {
                    final String msisdn = rsAllUserPhones.getString("msisdn");
                    if (!msisdnNotToDelete.containsKey(msisdn)) {
                        pstmtDeleteFromUserPhones.setString(1, channelUserVO.getUserID());
                        pstmtDeleteFromUserPhones.setString(2, msisdn);
                        if (pstmtDeleteFromUserPhones.executeUpdate() <= 0) {
                            errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), RestAPIStringParser.getMessage(locale,
                                "bulkuser.initiatebulkuser.msg.error.deleteuserphonestable", new String[] { channelUserVO.getExternalCode() }));
                            errorList.add(errorVO);
                            con.rollback();
                            BatchesLog.log("BULKUSRUPDATE", channelUserVO, null, "Fail :=When deleting UserPhones table");
                            existLoop = true;
                            break;
                        }
                    }
                }
                // skip the user if error in deleting any of msisdn
                if (existLoop) {
                    continue;
                }
                if ("I".equalsIgnoreCase(userPhoneVO.getOperationType())) {
                    idGeneratorDAO.updateNextBatchID(con, PretupsI.USR_PHONE_ID, PretupsI.ALL, PretupsI.ALL, channelUserVO.getModifiedOn(), phoneID - 1);
                }
            }
            commitCounter++;
            if (commitCounter > commitNumber) {
                // records
                con.commit();
            }

            channelUserVO.setSessionInfoVO(new SessionInfoVO());
            // Email for pin & password
            final String tmpMsisdn = channelUserVO.getMsisdn();
            // to push messages to individual user
            for (int k = 0, j = msisdnList.size(); k < j; k++) {
                userPhoneVO = (UserPhoneVO) msisdnList.get(k);
                // only pin modify
                if (userPhoneVO.isPinModifyFlag() && !loginIDChange && !channelUserVO.isPasswordModifyFlag()) {
                    final String[] arrArray = { BTSLUtil.decryptText(userPhoneVO.getShowSmsPin()), "", userPhoneVO.getMsisdn() };
                    btslPushMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_PIN_MODIFY, arrArray);
                } else if (!userPhoneVO.isPinModifyFlag() && channelUserVO.isPasswordModifyFlag() && !loginIDChange) {
                    // only password change
                    btslPushMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_PWD_MODIFY,
                        new String[] { BTSLUtil.decryptText(channelUserVO.getPassword()), channelUserVO.getLoginID() });
                }
                // web password and primary no pin change
                else if (channelUserVO.isPasswordModifyFlag() && userPhoneVO.isPinModifyFlag() && !loginIDChange) {
                    final String[] arrArray = { BTSLUtil.decryptText(channelUserVO.getPassword()), BTSLUtil.decryptText(userPhoneVO.getShowSmsPin()), channelUserVO
                        .getLoginID(), userPhoneVO.getMsisdn() };
                    btslPushMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_PWD_AND_PIN_MODIFY, arrArray);
                }
                // web loginid and web password and primary no pin change
                else if (channelUserVO.isPasswordModifyFlag() && userPhoneVO.isPinModifyFlag() && loginIDChange) {
                    final String[] arrArray = { channelUserVO.getLoginID(), BTSLUtil.decryptText(channelUserVO.getPassword()), BTSLUtil.decryptText(userPhoneVO
                        .getShowSmsPin()), userPhoneVO.getMsisdn() };
                    btslPushMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_LOGIN_AND_PWD_AND_PIN_MODIFY, arrArray);
                }
                // only login id change
                else if (!channelUserVO.isPasswordModifyFlag() && !userPhoneVO.isPinModifyFlag() && loginIDChange) {
                    final String[] arrArray = { channelUserVO.getLoginID() };
                    btslPushMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_LOGIN_AND_PWD_MODIFY, arrArray);

                }
                // only login id and web password change
                else if (channelUserVO.isPasswordModifyFlag() && loginIDChange) {
                    final String[] arrArray = { channelUserVO.getLoginID(), BTSLUtil.decryptText(channelUserVO.getPassword()) };
                    btslPushMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_LOGIN_MODIFY, arrArray);

                }
                // only login id and pin change
                else if (!channelUserVO.isPasswordModifyFlag() && userPhoneVO.isPinModifyFlag() && loginIDChange) {
                    final String[] arrArray = { channelUserVO.getLoginID(), BTSLUtil.decryptText(userPhoneVO.getShowSmsPin()), userPhoneVO.getMsisdn() };
                    btslPushMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_LOGIN_AND_PIN_MODIFY, arrArray);
                }
                if (btslPushMessage != null) {
                    // Changed for hiding PIN and PWD that are written in
                    // MessageSentLog, BY Manisha(01/12/08)
                    // pushMessage=new
                    // PushMessage(userPhoneVO.getMsisdn(),btslPushMessage,"","",defaultLocale,channelUserVO.getNetworkID());
                    pushMessage = new PushMessage(userPhoneVO.getMsisdn(), btslPushMessage, "", "", defaultLocale, channelUserVO.getNetworkID(),
                        "SMS will be delivered shortly thanks");
                    pushMessage.push();

                    // email change
                    if (isEmailServiceAllow && !BTSLUtil.isNullString(channelUserVO.getEmail())) {
                        subject = RestAPIStringParser.getMessage(locale, "subject.channeluser.update.massage", new String[] { channelUserVO.getUserName() });
                        channelUserVO.setMsisdn(userPhoneVO.getMsisdn());
                        emailSendToUser = new EmailSendToUser(subject, btslPushMessage, locale, channelUserVO.getNetworkID(), "Email will be delivered shortly",
                            channelUserVO, channelUserVO);
                        emailSendToUser.sendMail();
                        channelUserVO.setMsisdn(tmpMsisdn);
                    }
                }
            }

            ChannelUserLog.log("BATCHUSERMODIFY", channelUserVO, channelUserVO, true, "Channel user successfully modify");
			btslPushMessage=null;
			loginIDChange=false;
        }

    } catch (SQLException sqe) {
        try {
            if (con != null) {
                con.rollback();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        log.error(methodName, "SQLException : " + sqe);
        log.errorTrace(methodName, sqe);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[ModifyChannelUserList]", "", "", "",
            "SQL Exception:" + sqe.getMessage());
        throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
    } catch (Exception ex) {
        try {
            if (con != null) {
                con.rollback();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        log.error(methodName, "Exception : " + ex);
        log.errorTrace(methodName, ex);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[ModifyChannelUserList]", "", "", "",
            "Exception:" + ex.getMessage());
        throw new BTSLBaseException(this, methodName, "error.general.processing");
    } finally {
        try {
            if (rsUserPhones != null) {
                rsUserPhones.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (rsSelectLoginID != null) {
                rsSelectLoginID.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (rsExternalCode != null) {
                rsExternalCode.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (rsPortedMSISDN != null) {
                rsPortedMSISDN.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtSelectUserPhones != null) {
                pstmtSelectUserPhones.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtSelectLoginID != null) {
                pstmtSelectLoginID.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtupdateInToUser != null) {
                pstmtupdateInToUser.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtupdateInToChanelUsers != null) {
                pstmtupdateInToChanelUsers.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtupdateInToUserPhones != null) {
                pstmtupdateInToUserPhones.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtInsertInUserGeography != null) {
                pstmtInsertInUserGeography.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtDeleteFromUserGeography != null) {
                pstmtDeleteFromUserGeography.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtInsertInUserServices != null) {
                pstmtInsertInUserServices.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtDeleteFromUserServices != null) {
                pstmtDeleteFromUserServices.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtInsertInUserRoles != null) {
                pstmtInsertInUserRoles.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtDeleteFromUserRoles != null) {
                pstmtDeleteFromUserRoles.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtSelectExternalCode != null) {
                pstmtSelectExternalCode.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtPortedMSISDN != null) {
                pstmtPortedMSISDN.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (psmtSelectPhone != null) {
                psmtSelectPhone.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (rsSelectPhone != null) {
                rsSelectPhone.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (psmtSIMTxnID != null) {
                psmtSIMTxnID.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (rsOldLoginID != null) {
                rsOldLoginID.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtSelectOldLoinID != null) {
                pstmtSelectOldLoinID.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtInsertInUserPhones != null) {
                pstmtInsertInUserPhones.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (rsAllUserPhones != null) {
                rsAllUserPhones.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtSelectAllFromUserPhones != null) {
                pstmtSelectAllFromUserPhones.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtDeleteFromUserPhones != null) {
                pstmtDeleteFromUserPhones.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtInsertInUserVoucherSegments != null) {
            	pstmtInsertInUserVoucherSegments.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtDeleteFromUserVoucherTypes != null) {
            	pstmtDeleteFromUserVoucherTypes.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        try {
            if (pstmtInsertInUserVoucherTypes != null) {
            	pstmtInsertInUserVoucherTypes.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exiting: errorList size =" + errorList.size());
        }
    }
    return errorList;
	}




/**
 * @author Subesh
 *         Method for create a new batch for barred users
 *         (insert into batches)
 * 
 * @param p_con
 *            java.sql.Connection
 * @param sessionUserVO
 *            (consist of UserVO)
 * @param form
 *            Batchc2sRevEntryVO
 * 
 * @throws BTSLBaseException
 */
public void addbatchRev(Connection p_con, Batchc2sRevEntryVO  batchc2sRevEntryVO , UserVO sessionUserVO) throws BTSLBaseException {
    int i = 0;
    String batchID = null;
    OperatorUtilI operatorUtil = null;
    PreparedStatement pstmtBar = null;
    final Date currentDate = new Date();
    final String methodName = "addbatchRev";
    if (log.isDebugEnabled()) {
        log.debug(methodName, "Entered ");
    }

    final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);

    try {
        operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
    } catch (Exception e) {
        log.errorTrace(methodName, e);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[addChannelUserList]", "", "", "",
            "Exception while loading the class at the call:" + e.getMessage());
    }

    try {
        
        batchID = operatorUtil.formatBatchesID(sessionUserVO.getNetworkID(), PretupsI.BULK_USR_BAR_ID_PREFIX, currentDate, IDGenerator.getNextID(
            PretupsI.BULK_USR_BAR_ID_PREFIX, PretupsI.ALL, sessionUserVO.getNetworkID()));
        
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append("INSERT into batches values (?,?,?,?,?,?,?,?,?,?,?,?,?)");

        pstmtBar = p_con.prepareStatement(strBuff.toString());
        ++i;
        pstmtBar.setString(i, batchID);
        ++i;
        pstmtBar.setString(i, PretupsI.BATCH_FOR_C2S_REV);
        ++i;
        pstmtBar.setInt(i, batchc2sRevEntryVO.getBatchSize());
        ++i;
        pstmtBar.setString(i, batchc2sRevEntryVO.getBatchName());
        ++i;
        pstmtBar.setString(i, sessionUserVO.getNetworkID());
        ++i;
        pstmtBar.setString(i, batchc2sRevEntryVO.getStatus());
        ++i;
        pstmtBar.setString(i, sessionUserVO.getUserID());
        ++i;
        pstmtBar.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(currentDate));
        ++i;
        pstmtBar.setString(i, sessionUserVO.getUserID());
        ++i;
        pstmtBar.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(currentDate));
        ++i;
        pstmtBar.setString(i, batchc2sRevEntryVO.getFileName());
        
        ++i;
        pstmtBar.setInt(i, batchc2sRevEntryVO.getApprovedRecords());
        
        ++i;
        pstmtBar.setInt(i, batchc2sRevEntryVO.getRejectedRecords());
        pstmtBar.executeUpdate();

        if (log.isDebugEnabled()) {
            log.debug(methodName, " OtherQuery sqlSelect QUERY :" + strBuff.toString());
        }

    } // end of try

    catch (SQLException sqle) {
        log.error(methodName, "SQLException: " + sqle.getMessage());
        log.errorTrace(methodName, sqle);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[addbatchbar]", "", "", "",
            "SQL Exception:" + sqle.getMessage());
        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERIC_SERVER_ERROR);
    } // end of catch
    catch (Exception e) {
        log.error(methodName, "Exception: " + e.getMessage());
        log.errorTrace(methodName, e);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[addbatchbar]", "", "", "",
            "Exception:" + e.getMessage());
        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERIC_SERVER_ERROR);
    } // end of catch
    finally {
        try {
            if (pstmtBar != null) {
                pstmtBar.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exiting");
        }
    } // end of finally
}


/**
 * Method :rejectUserListForBatchApproval
 * 
 * @param p_con
 * @param p_userDetails
 * @param p_messages
 * @param p_locale
 * @param p_userVO
 *            TODO
 * @return
 * @throws BTSLBaseException
 * @author Anand Swaraj
 */
public ArrayList rejectUserListForBatchApproval(Connection p_con, ArrayList p_userDetails, Locale p_locale, UserVO p_userVO) throws BTSLBaseException {
    final String methodName = "rejectUserListForBatchApproval";
    if (log.isDebugEnabled()) {
        log.debug(methodName, "Entered: p_userDetails.size()=" + p_userDetails.size()  + " p_locale=" + p_locale + " p_userVO=" + p_userVO
            .toString());
    }
    PreparedStatement pstmtUpdateUsr = null;
    final StringBuffer strBuffUsr = new StringBuffer(" UPDATE users SET level1_approved_by=?, level1_approved_on=?,");
    strBuffUsr.append(" modified_by=?, modified_on=?, status=?, previous_status=?, remarks=?, login_id=? WHERE user_id=?");
    if (log.isDebugEnabled()) {
        log.debug(methodName, "QUERY strBuffUsr=" + strBuffUsr);
    }

    final ArrayList errorList = new ArrayList();
    try {
        pstmtUpdateUsr = p_con.prepareStatement(strBuffUsr.toString());
        ChannelUserVO channelUserVO = null;
        int updateCountUsr = 0;
        int warnings = 0;
        int updateCount = 0;
        ListValueVO errorVO = null;
        for (int i = 0, j = p_userDetails.size(); i < j; i++) {
            try {
                updateCountUsr = 0;
                channelUserVO = (ChannelUserVO) p_userDetails.get(i);
                pstmtUpdateUsr.clearParameters();
                pstmtUpdateUsr.setString(1, channelUserVO.getLevel1ApprovedBy());
                pstmtUpdateUsr.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getLevel1ApprovedOn()));
                pstmtUpdateUsr.setString(3, channelUserVO.getModifiedBy());
                pstmtUpdateUsr.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getModifiedOn()));
                pstmtUpdateUsr.setString(5, channelUserVO.getStatus());
                pstmtUpdateUsr.setString(6, channelUserVO.getPreviousStatus());
                pstmtUpdateUsr.setString(7, channelUserVO.getRemarks());
                pstmtUpdateUsr.setString(8, channelUserVO.getLoginID());
                pstmtUpdateUsr.setString(9, channelUserVO.getUserID());
                updateCountUsr = pstmtUpdateUsr.executeUpdate();
                if (updateCountUsr <= 0) {
                    //errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale, "bulkuser.processuploadedfile.error.updateusererr"));
                	errorVO = new ListValueVO("", channelUserVO.getRecordNumber(), RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.UPDATE_USER_ERROR, null));
                    errorList.add(errorVO);
                    warnings++;
                    BatchesLog.log("BULKUSRREJ", channelUserVO, null, "Fail :=" + RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.UPDATE_USER_ERROR, null));
                    continue;
                }
                BatchesLog.log("BULKUSRREJ", channelUserVO, null, "Success");
                ChannelUserLog.log("BULKUSRREJ", channelUserVO, p_userVO, true, "Success Batch user approval");
                updateCount++;
                p_con.commit();
            } catch (SQLException sqe) {
                try {
                    if (p_con != null) {
                        p_con.rollback();
                    }
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                }
                log.error(methodName, "SQLException : " + sqe);
                log.errorTrace(methodName, sqe);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[rejectUserList]", "", "", "",
                    "SQL Exception:" + sqe.getMessage());
                //errorVO = new ListValueVO("ERROR", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale, "error.general.sql.processing"));
                errorVO = new ListValueVO("ERROR", channelUserVO.getRecordNumber(), RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.TECHNICAL_ERROR, null));
                errorList.add(errorVO);
                BatchesLog.log("BULKUSRREJ", channelUserVO, null, "Fail");
                ChannelUserLog.log("BULKUSRREJ", channelUserVO, p_userVO, true, RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.TECHNICAL_ERROR, null));
                warnings++;
                continue;
            } catch (Exception ex) {
                try {
                    if (p_con != null) {
                        p_con.rollback();
                    }
                } catch (Exception e) {
                    log.errorTrace(methodName, e);
                }
                log.error(methodName, "Exception : " + ex);
                log.errorTrace(methodName, ex);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[rejectUserList]", "", "", "",
                    "Exception:" + ex.getMessage());
                //errorVO = new ListValueVO("ERROR", channelUserVO.getRecordNumber(), p_messages.getMessage(p_locale, "error.general.processing"));
                errorVO = new ListValueVO("ERROR", channelUserVO.getRecordNumber(), RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.TECHNICAL_ERROR, null));
                errorList.add(errorVO);
                BatchesLog.log("BULKUSRREJ", channelUserVO, null, "Fail");
                ChannelUserLog.log("BULKUSRREJ", channelUserVO, p_userVO, true, RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.TECHNICAL_ERROR, null));
                warnings++;
                continue;
            }
        }
        errorVO = new ListValueVO("UPDATECOUNT", String.valueOf(warnings), String.valueOf(updateCount));
        errorList.add(errorVO);

    } catch (SQLException sqe) {
        log.error(methodName, "SQLException : " + sqe);
        log.errorTrace(methodName, sqe);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[rejectUserList]", "", "", "",
            "SQL Exception:" + sqe.getMessage());
        throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
    } catch (Exception ex) {
        log.error(methodName, "Exception : " + ex);
        log.errorTrace(methodName, ex);
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserWebDAO[rejectUserList]", "", "", "",
            "Exception:" + ex.getMessage());
        throw new BTSLBaseException(this, methodName, "error.general.processing");
    } finally {
        try {
            if (pstmtUpdateUsr != null) {
                pstmtUpdateUsr.close();
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exiting: p_userDetails size =" + p_userDetails.size());
        }
    }
    return errorList;
}








    
    
}

