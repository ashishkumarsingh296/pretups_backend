package com.web.pretups.pointenquiry.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
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
import com.btsl.util.BTSLUtil;
import com.web.pretups.programcategory.businesslogic.ProgramCategoryDAO;

@Repository
@Lazy
@Scope("session")
public class PointEnquiryDAO {

    private static Log log = LogFactory.getLog(ProgramCategoryDAO.class.getName());

    public PointEnquiryVO loadLMSUserDetails(Connection con, String p_msisdn, PointEnquiryVO pointEnquiryVO) throws BTSLBaseException {
        final String methodName = "loadLMSUserDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_msisdn:" + p_msisdn);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        try {
            final StringBuilder selectQueryBuff = new StringBuilder(" SELECT u.user_id,u.user_name, u.network_code,u.login_id,u.parent_id, u.owner_id, u.msisdn,");
            selectQueryBuff.append(" u.employee_code, u.status , u.user_type, u.external_code, u.user_code, d.domain_name,cat.category_code, ");
            selectQueryBuff.append("  cat.category_name,  ub.product_code,ub.ACCUMULATED_POINTS,ub.profile_type,gd.GRPH_DOMAIN_NAME ");
            selectQueryBuff.append("  FROM users u,categories cat,user_phones uphones,bonus ub, domains d, user_geographies ug, geographical_domains gd ");
            selectQueryBuff.append(" WHERE uphones.msisdn=?  AND uphones.user_id=u.user_id and u.status not in(?,?)  ");
            selectQueryBuff.append("and u.user_id=ub.user_id_or_msisdn and u.category_code=cat.category_code and ");
            selectQueryBuff.append(" cat.domain_code=d.domain_code and u.user_id=ug.USER_ID and ug.GRPH_DOMAIN_CODE=gd.GRPH_DOMAIN_CODE ");
            final String selectQuery = selectQueryBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "select query:" + selectQuery);
            }

            pstmtSelect = con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_msisdn);
            pstmtSelect.setString(2, PretupsI.USER_STATUS_DELETED);
            pstmtSelect.setString(3, PretupsI.USER_STATUS_CANCELED);

            rs = pstmtSelect.executeQuery();

            if (rs.next()) {
            	PointEnquiryVO pointEnqVO = new PointEnquiryVO();

            	pointEnqVO.setUserID(rs.getString("user_id"));
            	pointEnqVO.setUserName(rs.getString("user_name"));
                pointEnqVO.setNetworkID(rs.getString("network_code"));
                pointEnqVO.setLoginID(rs.getString("login_id"));
                pointEnqVO.setParentID(rs.getString("parent_id"));
                pointEnqVO.setOwnerID(rs.getString("owner_id"));
                pointEnqVO.setMsisdn(rs.getString("msisdn"));
                pointEnqVO.setEmpCode(rs.getString("employee_code"));
                pointEnqVO.setStatus(rs.getString("status"));
                pointEnqVO.setUserType(rs.getString("user_type"));
                pointEnqVO.setExternalCode(rs.getString("external_code"));
                pointEnqVO.setUserCode(rs.getString("user_code"));
                pointEnqVO.setCategoryCode(rs.getString("category_code"));
                pointEnqVO.setCategoryName(rs.getString("category_name"));
                pointEnqVO.setDomainName(rs.getString("domain_name"));
                pointEnqVO.setAccumulatedPoints(rs.getString("ACCUMULATED_POINTS"));
                pointEnqVO.setProfileType(rs.getString("profile_type"));
                pointEnqVO.setGeography(rs.getString("GRPH_DOMAIN_NAME"));

            }
            return pointEnquiryVO;
        } catch (SQLException sqle) {
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[loadLMSUserDetails]", "",
                "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[loadLMSUserDetails]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadChannelUserDetails", "error.general.processing");
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
                log.debug(methodName, "Exiting loyaltyPointsRedemptionVO: " + pointEnquiryVO);
            }
        }
    }

    public ArrayList<PointEnquiryVO> loadList(Connection con, String msisdn, PointEnquiryVO pointEnquiryVO) throws BTSLBaseException {
        final String methodName = "loadList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered msisdn " + msisdn);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<PointEnquiryVO> list = null;
        final StringBuilder strBuilder = new StringBuilder(" select ch.LMS_PROFILE ");
        strBuilder.append(" from channel_users ch,user_phones ph");
        strBuilder.append(" where ph.msisdn=? and ph.user_id=ch.user_id ");

        final String sqlSelect = strBuilder.toString();

        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        list = new ArrayList<PointEnquiryVO>();
        try {
            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setString(1, msisdn);
            rs = pstmt.executeQuery();

            while (rs.next()) {

                if (!BTSLUtil.isNullString(rs.getString("LMS_PROFILE"))) {
                    pointEnquiryVO.setLmsProfile(rs.getString("LMS_PROFILE"));
                    list.add(pointEnquiryVO);
                }

            }
        } catch (SQLException sqe) {
            if (log.isErrorEnabled()) {
                log.error(methodName, "SQLException : " + sqe);
            }
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PointEnquiryDAO[loadList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PointEnquiryDAO[loadList]", "", "", "",
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
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: List size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method for loading Geography List by domainCode.
     * This method returns the arraylist which consist of ListValue VO's
     * 
     * Used in(UserAction, ChannelUserAction)
     * 
     * @author mohit.goel
     * @param con
     *            java.sql.Connection
     * @param p_domainCode
     *            String
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList<ListValueVO> loadGeographyList(Connection con, String domainCode, String networkCode) throws BTSLBaseException {
        final String methodName = "loadGeographyList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered domainCode" + domainCode);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(" select distinct  ge.GRPH_DOMAIN_NAME, ge.GRPH_DOMAIN_TYPE,ge.GRPH_DOMAIN_CODE  ");
        strBuilder.append(" from categories ca, geographical_domains ge  ");
        strBuilder.append("where ge.GRPH_DOMAIN_TYPE = ca.GRPH_DOMAIN_TYPE and ge.STATUS=? and ca.DOMAIN_CODE <>? and ge.network_code=? ");
        final String sqlSelect = strBuilder.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList<ListValueVO> list = new ArrayList<ListValueVO>();
        ListValueVO vo = null;
        try {
            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.STATUS_ACTIVE);
            pstmt.setString(2, domainCode);
            pstmt.setString(3, networkCode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                vo = new ListValueVO(rs.getString("GRPH_DOMAIN_NAME"), rs.getString("GRPH_DOMAIN_CODE"));
                list.add(vo);
            }
            if (!list.isEmpty()) {
                vo = new ListValueVO(PretupsI.ALL, PretupsI.ALL);
                list.add(vo);
            }

        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[loadDomainList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[loadDomainList]", "", "", "",
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
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: domainList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method for loading category List by domainCode.
     * This method returns the arraylist which consist of ListValue VO's
     * 
     * @author akanksha
     * @param con
     *            java.sql.Connection
     * @param p_domainCode
     *            String
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList<ListValueVO> loadCategoryList(Connection con, String domainCode) throws BTSLBaseException {
        final String methodName = "loadCategoryList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered domainCode " + domainCode);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int i = 0;
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("  SELECT c.category_code,c.category_name,c.display_allowed FROM categories c,geographical_domain_types gdt WHERE c.status <>? AND ");
        if (!(PretupsI.ALL).equals(domainCode)) {
            strBuilder.append(" c.domain_code =? AND ");
        }

        strBuilder.append(" gdt.grph_domain_type = c.grph_domain_type  ");
        final String sqlSelect = strBuilder.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList<ListValueVO> list = new ArrayList<ListValueVO>();
        try {
            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setString(++i, PretupsI.USER_STATUS_DELETED);
            if (!(PretupsI.ALL).equals(domainCode)) {
                pstmt.setString(++i, domainCode);
            }

            rs = pstmt.executeQuery();
            ListValueVO vo = null;
            while (rs.next()) {
                vo = new ListValueVO(rs.getString("category_name"), rs.getString("category_code"));
                vo.setStatusType(rs.getString("display_allowed"));
                list.add(vo);
            }

            if (!list.isEmpty()) {
                vo = new ListValueVO(PretupsI.ALL, PretupsI.ALL);
                vo.setStatusType(PretupsI.YES);
                list.add(vo);
            }
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[loadDomainList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[loadDomainList]", "", "", "",
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
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: domainList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method loadCategoryUsersWithinGeoDomainHirearchy. This method the loads
     * the user list with userID and UserName, for the search screen .
     * 
     * @param con
     *            Connection
     * @param p_categoryCode
     *            String
     * @param p_networkCode
     *            String
     * @param p_geographicalDomainCode
     *            String
     * @param ploginUserID
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList<ListValueVO> loadCategoryUsersWithinGeoDomainHirearchy(Connection con, String p_categoryCode, String p_networkCode, String p_geographicalDomainCode, String p_domain) throws BTSLBaseException {

        final String METHOD_NAME = "loadCategoryUsersWithinGeoDomainHirearchy";
        if (log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered  Category Code : ");
        	msg.append(p_categoryCode);
        	msg.append(", Network Code = ");
        	msg.append(p_networkCode);
        	msg.append(", p_geographicalDomainCode = ");
        	msg.append(p_geographicalDomainCode);
        	msg.append(", p_domain = ");
        	msg.append(p_domain);
        	
        	String message=msg.toString();
            log.debug(METHOD_NAME, message);
        }
        int i = 0;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuilder strBuilder = new StringBuilder();
        String geographicalDomainCode = p_geographicalDomainCode.replaceAll(",", "");
        
               final ArrayList<ListValueVO> arrayList = new ArrayList<ListValueVO>();
        try {

        	PointEnquiryQry pointenquiryQry=(PointEnquiryQry)ObjectProducer.getObject(QueryConstants.POINT_ENQUIRY_QRY, QueryConstants.QUERY_PRODUCER);
            pstmt = pointenquiryQry.loadCategoryUsersWithinGeoDomainHirearchy(con, p_networkCode, p_domain, p_categoryCode, geographicalDomainCode);

            
            rs = pstmt.executeQuery();
            while (rs.next()) {
                arrayList.add(new ListValueVO(rs.getString("user_name"), rs.getString("user_id")));
            }
            if (!arrayList.isEmpty()) {
                arrayList.add(new ListValueVO(PretupsI.ALL, PretupsI.ALL));
            }

        } catch (SQLException sqe) {
            log.error(METHOD_NAME, "SQLException : " + sqe);
            log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelUserDAO[loadCategoryUsersWithinGeoDomainHirearchy]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(METHOD_NAME, "Exception : " + ex);
            log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ChannelUserDAO[loadCategoryUsersWithinGeoDomainHirearchy]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }

            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting:  arrayList Size =" + arrayList.size());
            }
        }
        return arrayList;
    }

    /**
     * Method loadLMSDetailsUsingUserID. This method the loads
     * the details of the LMS user who has made some bonus points .
     * 
     * @param con
     *            Connection
     * @param userId
     *            String
     * @param pointEnquiryVO
     *            PointEnquiryVO
     * 
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList<PointEnquiryVO> loadLMSDetailsUsingUserID(Connection con, String userId, PointEnquiryVO pointEnquiryVO) throws BTSLBaseException {

        final String methodName = "loadLMSDetailsUsingUserID";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered userId:" + userId);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ArrayList<PointEnquiryVO> userlist = null;
        try {
            int i = 0;
            userlist = new ArrayList<PointEnquiryVO>();
            final StringBuilder selectQueryBuff = new StringBuilder(" SELECT u.user_id,u.user_name, u.network_code,u.login_id,u.parent_id, u.owner_id, u.msisdn,");
            selectQueryBuff.append(" u.employee_code, u.status , u.user_type, u.external_code, u.user_code, d.domain_name,cat.category_code, ");
            selectQueryBuff.append("  cat.category_name,  ub.product_code,ub.ACCUMULATED_POINTS,ub.profile_type,gd.GRPH_DOMAIN_NAME ");
            selectQueryBuff.append("  FROM users u,categories cat,user_phones uphones,bonus ub, domains d, user_geographies ug, geographical_domains gd WHERE ");
            if (!(PretupsI.ALL).equals(userId)) {
                selectQueryBuff.append(" u.user_id=? AND");
            }
            selectQueryBuff.append(" uphones.user_id=u.user_id and u.status not in(?,?)  ");
            selectQueryBuff.append("and u.user_id=ub.user_id_or_msisdn and u.category_code=cat.category_code and ");
            selectQueryBuff.append(" cat.domain_code=d.domain_code and u.user_id=ug.USER_ID and ug.GRPH_DOMAIN_CODE=gd.GRPH_DOMAIN_CODE ");
            final String selectQuery = selectQueryBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "select query:" + selectQuery);
            }

            pstmtSelect = con.prepareStatement(selectQuery);
            if (!(PretupsI.ALL).equals(userId)) {
                pstmtSelect.setString(++i, userId);
            }
            pstmtSelect.setString(++i, PretupsI.USER_STATUS_DELETED);
            pstmtSelect.setString(++i, PretupsI.USER_STATUS_CANCELED);
            rs = pstmtSelect.executeQuery();

            while (rs.next()) {
            	PointEnquiryVO pointEnqVO = new PointEnquiryVO();

            	pointEnqVO.setUserID(rs.getString("user_id"));
            	pointEnqVO.setUserName(rs.getString("user_name"));
            	pointEnqVO.setNetworkID(rs.getString("network_code"));
            	pointEnqVO.setLoginID(rs.getString("login_id"));
            	pointEnqVO.setParentID(rs.getString("parent_id"));
            	pointEnqVO.setOwnerID(rs.getString("owner_id"));
            	pointEnqVO.setMsisdn(rs.getString("msisdn"));
            	pointEnqVO.setEmpCode(rs.getString("employee_code"));
            	pointEnqVO.setStatus(rs.getString("status"));
            	pointEnqVO.setUserType(rs.getString("user_type"));
            	pointEnqVO.setExternalCode(rs.getString("external_code"));
                pointEnqVO.setUserCode(rs.getString("user_code"));
                pointEnqVO.setCategoryCode(rs.getString("category_code"));
                pointEnqVO.setCategoryName(rs.getString("category_name"));
                pointEnqVO.setDomainName(rs.getString("domain_name"));
                pointEnqVO.setAccumulatedPoints(rs.getString("ACCUMULATED_POINTS"));
                pointEnqVO.setProfileType(rs.getString("profile_type"));
                pointEnqVO.setGeography(rs.getString("GRPH_DOMAIN_NAME"));
                userlist.add(pointEnqVO);
            }
            return userlist;
        } catch (SQLException sqle) {
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[loadLMSUserDetails]", "",
                "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[loadLMSUserDetails]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadChannelUserDetails", "error.general.processing");
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
                log.debug(methodName, "Exiting loyaltyPointsRedemptionVO: " + pointEnquiryVO);
            }
        }
    }

    /**
     * This method checks whether any profile is associated with the user
     * selected
     * selection of domain
     * 
     * @author akanksha
     * @param userid
     * @param con
     * @param pointEnquiryVO
     * @return list
     */
    public ArrayList<PointEnquiryVO> checkAssociated(Connection con, String userid, PointEnquiryVO pointEnquiryVO) throws BTSLBaseException {
        final String methodName = "checkAssociated";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered userid " + userid);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<PointEnquiryVO> list = null;
        final StringBuilder strBuilder = new StringBuilder(" select LMS_PROFILE from channel_users where user_id=? ");
        final String sqlSelect = strBuilder.toString();

        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }

        list = new ArrayList<PointEnquiryVO>();
        try {
            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setString(1, userid);
            rs = pstmt.executeQuery();

            while (rs.next()) {

                if (!BTSLUtil.isNullString(rs.getString("LMS_PROFILE"))) {
                    pointEnquiryVO.setLmsProfile(rs.getString("LMS_PROFILE"));
                    list.add(pointEnquiryVO);
                }

            }
        } catch (SQLException sqe) {
            if (log.isErrorEnabled()) {
                log.error(methodName, "SQLException : " + sqe);
            }
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PointEnquiryDAO[loadList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PointEnquiryDAO[loadList]", "", "", "",
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
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: List size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method for loading Domain List by domainCode.
     * This method returns the arraylist which consist of ListValue VO's
     * 
     * Used in(UserAction, ChannelUserAction)
     * 
     * @author akanksha
     * @param con
     *            java.sql.Connection
     * @param p_domainCode
     *            String
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList<ListValueVO> loadDomainList(Connection con, String p_domainCode) throws BTSLBaseException {
        final String methodName = "loadDomainList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered  p_domainCode=" + p_domainCode);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("SELECT D.domain_code,D.domain_name,DT.restricted_msisdn,DT.display_allowed ");
        strBuilder.append("FROM domains D,domain_types DT WHERE D.status <> 'N' AND D.domain_type_code =DT.domain_type_code ");
        strBuilder.append("AND DT.domain_type_code <> ? ");
        strBuilder.append("ORDER BY domain_name");
        final String sqlSelect = strBuilder.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList<ListValueVO> list = new ArrayList<ListValueVO>();
        try {
            ListValueVO vo = null;
            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_domainCode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                vo = new ListValueVO(rs.getString("domain_name"), rs.getString("domain_code"));
                vo.setType(rs.getString("restricted_msisdn"));
                vo.setStatusType(rs.getString("display_allowed"));
                list.add(vo);
            }
            if (!list.isEmpty()) {
                vo = new ListValueVO(PretupsI.ALL, PretupsI.ALL);
                vo.setStatusType(PretupsI.YES);
                list.add(vo);
            }

        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[loadDomainList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DomainDAO[loadDomainList]", "", "", "",
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
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: domainList size=" + list.size());
            }
        }
        return list;
    }
}
