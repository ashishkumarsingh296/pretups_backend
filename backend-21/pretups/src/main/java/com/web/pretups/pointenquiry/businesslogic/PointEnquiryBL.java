package com.web.pretups.pointenquiry.businesslogic;

import java.sql.Connection;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.pointenquiry.web.PointEnquiryForm;

@Service
@Lazy
@Scope("session")
public class PointEnquiryBL {
    @Autowired
    private PointEnquiryDAO pointEnquiryDAO;
    @Autowired
    private ChannelUserDAO channelUserDAO;

    private static final Log LOG = LogFactory.getLog(PointEnquiryBL.class.getName());

    public void displayDetailsUsingMsisdn(Connection con, PointEnquiryForm pointEnquiryForm) throws BTSLBaseException {
        final String methodName = "displayDetailsUsingMsisdn";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }

        String msisdn = null;
        PointEnquiryVO pointEnquiryVO = null;
        ArrayList<PointEnquiryVO> mList = null;
        // ChannelUserDAO channelUserDAO = null;//review
        ChannelUserVO channelUserVO = null;
        try {
            pointEnquiryVO = new PointEnquiryVO();
            // channelUserDAO = new ChannelUserDAO();//review
            channelUserVO = new ChannelUserVO();
            msisdn = pointEnquiryForm.getMsisdn();

            // Enquiry using mobile number
            // 1 valid msisdn
            if (!BTSLUtil.isNullString(msisdn)) {
                // check for valid msisdn
                if (!BTSLUtil.isValidMSISDN(msisdn)) {
                    throw new BTSLBaseException("PointEnquiryBL", "userExists", "bonus.point.enquiry.not.valid.MSISDN");
                }
            }
            // msisdn in system
            channelUserVO = channelUserDAO.loadChannelUserDetails(con, msisdn);
            if (channelUserVO == null) {
                throw new BTSLBaseException("PointEnquiryBL", methodName, "bonus.point.enquiry.details.of.user.not.found");
            }
            // 2 no profile associated with msisdn.

            mList = pointEnquiryDAO.loadList(con, msisdn, pointEnquiryVO);
            if (mList.isEmpty()) {
                throw new BTSLBaseException("PointEnquiryBL", methodName, "bonus.point.enquiry.profile.not.associated");
            }
            pointEnquiryVO = null;
            // 3 profile associated but no points.(TODO)
            pointEnquiryVO = pointEnquiryDAO.loadLMSUserDetails(con, msisdn, pointEnquiryVO);
            if (pointEnquiryVO == null) {

                throw new BTSLBaseException("PointEnquiryBL", "userExists", "bonus.point.enquiry.no.user.did.not.made.point");
            }
            // 4 Enquiry for all users except deleted or cancelled.
            else {
                pointEnquiryForm.setDomain(pointEnquiryVO.getDomainName());
                pointEnquiryForm.setCategory(pointEnquiryVO.getCategoryName());
                pointEnquiryForm.setUserName(pointEnquiryVO.getUserName());
                pointEnquiryForm.setMsisdn(pointEnquiryVO.getMsisdn());
                pointEnquiryForm.setAccumulatedPoint(pointEnquiryVO.getAccumulatedPoints());
                pointEnquiryForm.setProfileType(pointEnquiryVO.getProfileType());
                pointEnquiryForm.setStatus(pointEnquiryVO.getStatus());
                pointEnquiryForm.setGeography(pointEnquiryVO.getGeography());
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exited");
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            throw new BTSLBaseException("PointEnquiryBL", methodName, "");
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }

        }

    }

    @SuppressWarnings("unchecked")
    public void setDetailsForUserSearch(PointEnquiryForm pointEnquiryForm, UserVO userVO, Connection con) throws BTSLBaseException {
        final String methodName = "displayDetailsUsingUserSearch";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }

        try {
            // load the domain of the logged in user from the session
            pointEnquiryForm.setMsisdn(null);
            if (TypesI.YES.equals(userVO.getCategoryVO().getDomainAllowed()) && PretupsI.DOMAINS_FIXED.equals(userVO.getCategoryVO().getFixedDomains())) {
                pointEnquiryForm.setDomainList(BTSLUtil.displayDomainList(pointEnquiryDAO.loadDomainList(con, PretupsI.DOMAIN_TYPE_CODE)));
            } else {
                pointEnquiryForm.setDomainList(BTSLUtil.displayDomainList(userVO.getDomainList()));
            }

            pointEnquiryForm.setNetworkName(userVO.getNetworkName());
            pointEnquiryForm.setGeographyList(pointEnquiryDAO.loadGeographyList(con, PretupsI.OPERATOR_TYPE_OPT, userVO.getNetworkID()));
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            throw new BTSLBaseException("PointEnquiryBL", methodName, "");
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exited");
            }
        }
    }

    /**
     * This method is calls the DAO to populate the user list on
     * the basis of geography,domain, and category selected.
     * 
     * @param pointEnquiryForm
     * @param model
     * @param request
     * @return String(jsp path)
     */
    public void displayDetailsUsingUserSearch(Connection con, PointEnquiryForm pointEnquiryForm, UserVO uservo) throws BTSLBaseException {
        final String methodName = "displayDetailsUsingUserSearch";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }

        ArrayList<ListValueVO> userList = null;
        try {

            userList = pointEnquiryDAO.loadCategoryUsersWithinGeoDomainHirearchy(con, pointEnquiryForm.getCategory(), uservo.getNetworkID(), pointEnquiryForm.getGeography(),
                pointEnquiryForm.getDomain());

            if (userList.isEmpty()) {
                throw new BTSLBaseException("PointEnquiryBL", methodName, "bonus.point.enquiry.user.does.not.exists");
            } else {
                pointEnquiryForm.setUserList(userList);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exited");
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
        	LOG.error(methodName, e.getMessage());
            throw new BTSLBaseException("PointEnquiryBL", methodName, "");
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }

        }

    }

    /**
     * This method is calls the DAO to populate category list on
     * selection of domain
     * 
     * @param pointEnquiryForm
     * @param con
     * @param domainCode
     */
    @SuppressWarnings("unchecked")
    public void loadCategory(PointEnquiryForm pointEnquiryForm, Connection con, String domainCode) throws BTSLBaseException {
        final String methodName = "loadCategory";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        try {
            pointEnquiryForm.setCategoryList(BTSLUtil.displayDomainList(pointEnquiryDAO.loadCategoryList(con, domainCode)));

            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exited");
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
        	LOG.error(methodName, e.getMessage());
            throw new BTSLBaseException("PointEnquiryBL", methodName, "");
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }

        }
    }

    /**
     * This method is checks whether the user selected has any profile
     * associated or not.
     * Also this method loads the bonus point details of the user selected.
     * 
     * @author akanksha
     * @param pointEnquiryForm
     * @param con
     */
    public void displayDetailUsingUserID(PointEnquiryForm pointEnquiryForm, Connection con) throws BTSLBaseException {
        final String methodName = "displayDetailUsingUserID";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        ArrayList<PointEnquiryVO> list = null;
        ;
        PointEnquiryVO pointEnquiryVO = null;
        ArrayList<PointEnquiryVO> userList = null;
        try {
            pointEnquiryVO = new PointEnquiryVO();

            if (!PretupsI.ALL.equals(pointEnquiryForm.getUserID())) {
                list = pointEnquiryDAO.checkAssociated(con, pointEnquiryForm.getUserID(), pointEnquiryVO);
                if (list.isEmpty()) {
                    throw new BTSLBaseException("PointEnquiryBL", "userExists", "bonus.point.enquiry.profile.not.associated");
                }
            }

            userList = pointEnquiryDAO.loadLMSDetailsUsingUserID(con, pointEnquiryForm.getUserID(), pointEnquiryVO);

            if (userList.isEmpty()) {
                throw new BTSLBaseException("PointEnquiryBL", "userExists", "bonus.point.enquiry.no.user.did.not.made.point");
            }
            // 4 Enquiry for all users except deleted or cancelled.

            pointEnquiryForm.setAllUsersList(userList);

            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exited");
            }
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }

        }

    }

}
