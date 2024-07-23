package com.web.pretups.user.web;

import java.util.ArrayList;
import java.util.Iterator;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
//import org.apache.struts.action.ActionForm;
//import org.apache.struts.action.ActionForward;
//import org.apache.struts.action.ActionMapping;

import com.btsl.common.BTSLBaseException;
//import com.btsl.common.BTSLDispatchAction;
import com.btsl.common.IDGenerator;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.web.user.web.UserForm;


/**
 * @(#)ChannelUserAction.java
 *                            Copyright(c) 2005, Bharti Telesoft Ltd.
 *                            All Rights Reserved
 * 
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Author Date History
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Mohit Goel 27/07/2005 Initial Creation
 *                            Santanu Mohanty 5/12/07 modified(Password
 *                            management)
 *                            Shashank Gaur 29/03/13 modified(Barred For
 *                            Deletion)
 * 
 *                            Change 1 for file ApplabsBugReport_ChannelAdmin
 *                            &CCE_PreTUPS5 0.xls.Bug Fixed 9.Fixed on 27/10/06
 *                            by Siddhartha
 *                            This class is used to control the logic flow for
 *                            Channel User Insertion/Updation
 * 
 *                            JSP selectChannelCategory.jsp,addChannelUser.jsp
 */
public class ChannelUserAction  {



    protected static final Log LOG = LogFactory.getLog(ChannelUserAction.class.getName());


    /*
     * This method called from the showParentSearch Screen of the same class
     * basically prepare the search list in edit/view mode
     */
    private void prepareSearchList(UserForm theForm, UserVO channelUserSessionVO, String[] categoryID) {
        /*
         * This is the case when we need to load the userId of the user
         * 
         * 
         * Ind case
         * 1)Suppose BCU add Retailer
         * this time we need to load the list of all distributors
         * 2)Anoter search of the user is depend on the Category
         * of the user
         * 
         * IInd case
         * 1)Suppose Dist add Retailer
         * this time no need to search for distributors
         * 2)Another search of the user is depend on the category
         * of the user
         * 
         * here prepare a list of search
         */

        final ArrayList list = new ArrayList();
        CategoryVO categoryVO = null;

        /*
         * Note: Use the CategoryList to prepare the search list(If operator
         * user)
         * Use OrigCategoryList (if channel user)
         */
        if (PretupsI.OPERATOR_TYPE_OPT.equals(channelUserSessionVO.getDomainID())) {
            /*
             * Note: Not use the OrigCategoryList Use categoryList to prepare
             * the search list
             * becs OrigCategoryList contains all categories not the filtered
             * categories of a particular domain
             */
            for (int i = 0, j = theForm.getCategoryList().size(); i < j; i++) {
                categoryVO = (CategoryVO) theForm.getCategoryList().get(i);
                if (categoryVO.getSequenceNumber() == 1) {
                    list.add(categoryVO);
                }

                if (categoryVO.getSequenceNumber() != 1 && categoryID[0].equals(categoryVO.getCategoryCode())) {
                    list.add(categoryVO);
                }
            }
        } else {
            // explicitly add the session user into the list
            list.add(channelUserSessionVO.getCategoryVO());
            for (int i = 0, j = theForm.getOrigCategoryList().size(); i < j; i++) {
                categoryVO = (CategoryVO) theForm.getOrigCategoryList().get(i);
                if (categoryVO.getSequenceNumber() != 1 && categoryID[0].equals(categoryVO.getCategoryCode())) {
                    list.add(categoryVO);
                }
            }

        }
        theForm.setSearchList(list);
        // allocate memory
        theForm.setSearchTextArraySize();
        theForm.setSearchUserIdSize();
        theForm.setDistributorSearchFlagSize();

        if (!PretupsI.OPERATOR_TYPE_OPT.equals(channelUserSessionVO.getDomainID())) {
            theForm.setOwnerID(channelUserSessionVO.getOwnerID());
            theForm.setDistributorSearchFlagIndexed(0, "true");
            theForm.setSearchTextArrayIndexed(0, channelUserSessionVO.getOwnerName());
            theForm.setSearchUserIdIndexed(0, channelUserSessionVO.getUserID());
        }
    }


    /**
     * This method set the dropdwon descriptions
     * 
     * @param
     * @return void
     */
    private void setDropDownValue(UserForm theForm) {
    	boolean ptupsMobqutyMergd = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD);
        // load the Description of the corresponding selected dropdown value
        if (theForm.getCommissionProfileList() != null && theForm.getCommissionProfileList().size() > 0) {
            final CommissionProfileSetVO vo = BTSLUtil.getOptionDescForCommProfile(theForm.getCommissionProfileSetId(), theForm.getCommissionProfileList());

            theForm.setCommissionProfileSetIdDesc(vo.getCommProfileSetName());
        }

        GradeVO gradeVO = null;
        if (theForm.getUserGradeList() != null) {
            for (int i = 0, j = theForm.getUserGradeList().size(); i < j; i++) {
                gradeVO = (GradeVO) theForm.getUserGradeList().get(i);
                if (gradeVO.getGradeCode().equals(theForm.getUserGradeId())) {
                    theForm.setUserGradeIdDesc(gradeVO.getGradeName());
                    break;
                }
            }
        }
        if (theForm.getLmsProfileList() != null && theForm.getLmsProfileList().size() > 0) {
            final ListValueVO listValueVO = BTSLUtil.getOptionDesc(theForm.getLmsProfileId(), theForm.getLmsProfileList());
            theForm.setLmsProfileListIdDesc(listValueVO.getLabel());
        }
        
        
        if (theForm.getLoanProfileList() != null && theForm.getLoanProfileList().size() > 0) {
            final ListValueVO listValueVO = BTSLUtil.getOptionDesc(theForm.getLoanProfileId(), theForm.getLoanProfileList());
            theForm.setLoanProfileIdDesc(listValueVO.getLabel());
        }
        
        
        if (theForm.getTrannferProfileList() != null && theForm.getTrannferProfileList().size() > 0) {
            final ListValueVO vo = BTSLUtil.getOptionDesc(theForm.getTrannferProfileId(), theForm.getTrannferProfileList());
            theForm.setTrannferProfileIdDesc(vo.getLabel());
        }
        
        
        // added for user level transfer rule
        final boolean isTrfRuleTypeAllow = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW, theForm.getNetworkCode(), theForm
                        .getCategoryCode())).booleanValue();
        if (isTrfRuleTypeAllow && theForm.getTrannferRuleTypeList() != null && theForm.getTrannferRuleTypeList().size() > 0) {
            final ListValueVO vo = BTSLUtil.getOptionDesc(theForm.getTrannferRuleTypeId(), theForm.getTrannferRuleTypeList());
            theForm.setTrannferRuleTypeIdDesc(vo.getLabel());
        }
        if (theForm.getUserNamePrefixList() != null && theForm.getUserNamePrefixList().size() > 0) {
            final ListValueVO vo = BTSLUtil.getOptionDesc(theForm.getUserNamePrefixCode(), theForm.getUserNamePrefixList());
            theForm.setUserNamePrefixDesc(vo.getLabel());
        }
        // added by deepika aggarwal
        if (theForm.getUserLanguageList() != null && theForm.getUserLanguageList().size() > 0) {
            final ListValueVO vo1 = BTSLUtil.getOptionDesc(theForm.getUserLanguage(), theForm.getUserLanguageList());
            theForm.setUserLanguageDesc(vo1.getLabel());
        }
        if (theForm.getDocumentTypeList() != null && theForm.getDocumentTypeList().size() > 0) {
        	final ListValueVO vo2 = BTSLUtil.getOptionDesc(theForm.getDocumentType(), theForm.getDocumentTypeList());
        	theForm.setDocumentTypeDesc(vo2.getLabel());
        }
        if (theForm.getPaymentTypeList() != null && theForm.getPaymentTypeList().size() > 0) {
        	final ListValueVO vo2 = BTSLUtil.getOptionDesc(theForm.getPaymentType(), theForm.getPaymentTypeList());
        	theForm.setPaymentTypeDesc(vo2.getLabel());
        }
        if (ptupsMobqutyMergd) {
            if (theForm.getMpayProfileList() != null && theForm.getMpayProfileList().size() > 0) {
                if (!BTSLUtil.isNullString(theForm.getMpayProfileIDWithGrad()) && theForm.getMpayProfileIDWithGrad().contains(":")) {
                    theForm.setMpayProfileID((theForm.getMpayProfileIDWithGrad()).split(":")[1]);
                } else {
                    theForm.setMpayProfileID("");
                }

                if (!BTSLUtil.isNullString(theForm.getMpayProfileID()) && !BTSLUtil.isNullString(theForm.getUserGradeId())) {
                    theForm.setMpayProfileIDWithGrad(theForm.getUserGradeId() + ":" + theForm.getMpayProfileID());
                }

                theForm.setMpayProfileDesc((BTSLUtil.getOptionDesc(theForm.getMpayProfileIDWithGrad(), theForm.getMpayProfileList())).getLabel());
            }
        }
        // end Zebra and Tango
    }










    /**
     * Method to generate the userId while inserting new record
     * 
     * @param p_networkCode
     *            String
     * @param p_prefix
     *            String
     * @return String
     */
    public String generateUserId(String p_networkCode, String p_prefix) throws Exception {
        final String methodName = "generateUserId";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_networkCode=" + p_networkCode + " p_prefix=" + p_prefix);
        }
        final int length = Integer.parseInt(Constants.getProperty("USER_PADDING_LENGTH"));
        String id = BTSLUtil.padZeroesToLeft((IDGenerator.getNextID(TypesI.USERID, TypesI.ALL, p_networkCode)) + "", length);

        // id =
        // p_networkCode+Constants.getProperty("SEPARATOR_FORWARD_SLASH")+p_prefix+id;
        id = p_networkCode + p_prefix + id;
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting id=" + id);
        }
        return id;
    }




    /******************** Code For User Approval ****************************/





    /**
     * Method loadOwnerUserStatsInfo
     * This method load value of the status and the operator used with the
     * status comparison in the DAO method for
     * the OWNER user.
     * 
     * @param form
     * @param p_status
     * @param p_statusUsed
     * @throws Exception
     *             void
     * @author sandeep.goel
     */
    private void loadOwnerUserStatsInfo(UserForm form, StringBuffer p_status, StringBuffer p_statusUsed) throws Exception {
        final UserForm theForm = (UserForm) form;
        final String methodName = "loadOwnerUserStatsInfo";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered theForm.getRequestType()=" + theForm.getRequestType() + ", p_status=" + p_status + ",p_statusUsed=" + p_statusUsed);
        }

        try {
            // in add mode load the active users
            if ("add".equals(theForm.getRequestType())) {
                p_status.append(PretupsBL.userStatusActive());
                p_statusUsed.append(PretupsI.STATUS_IN);
            } else if ("associate".equals(theForm.getRequestType()))// load
            // active
            // and
            // suspended
            // users
            {
                p_status.append(PretupsBL.userStatusIn() + ",'" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "'");
                p_statusUsed.append(PretupsI.STATUS_IN);
            } else if ("delete".equals(theForm.getRequestType()))// load non
            // deleted and
            // canceled
            {
                p_status.append(PretupsBL.userStatusNotIn() + ",'" + PretupsI.USER_STATUS_DELETE_REQUEST + "','" + PretupsI.USER_STATUS_NEW + "','" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "'");
                p_statusUsed.append(PretupsI.STATUS_NOTIN);
            } else if ("suspend".equals(theForm.getRequestType())) {
                p_status.append(PretupsBL.userStatusNotIn() + ",'" + PretupsI.USER_STATUS_SUSPEND + "','" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "','" + PretupsI.USER_STATUS_NEW + "','" + PretupsI.USER_STATUS_DELETE_REQUEST + "'");
                p_statusUsed.append(PretupsI.STATUS_NOTIN);
            }

            else if ("viewuserbalance".equals(theForm.getRequestType()) || "viewusercounters".equals(theForm.getRequestType())) {
                p_status.append(PretupsBL.userStatusIn() + ",'" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "'");
                p_statusUsed.append(PretupsI.STATUS_IN);
            }
            // add the time of pop up of user list show all those uaers who have
            // status NEW
            else if ("view".equals(theForm.getRequestType())) {
                p_status.append(PretupsBL.userStatusNotIn());
                p_statusUsed.append(PretupsI.STATUS_NOTIN);
            } else if ("associateOther".equals(theForm.getRequestType()))// load
            // active
            // and
            // suspended
            // users
            {
                p_status.append(PretupsBL.userStatusIn() + ",'" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "'");
                p_statusUsed.append(PretupsI.STATUS_IN);
            } else if ("barred".equals(theForm.getRequestType())) {
                p_status.append(PretupsBL.userStatusNotIn() + ",'" + PretupsI.USER_STATUS_DELETE_REQUEST + "','" + PretupsI.USER_STATUS_NEW + "','" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "','" + PretupsI.USER_STATUS_BAR_FOR_DEL_REQUEST + "','" + PretupsI.USER_STATUS_BARRED + "','" + PretupsI.USER_STATUS_BAR_FOR_DEL_APPROVE + "'");
                p_statusUsed.append(PretupsI.STATUS_NOTIN);
            }
            // end
            else// in edit/view/changerole/viewuserbalances/viewusercounters
                // load all users except Deleted and canceled
            {
                p_status.append(PretupsBL.userStatusNotIn());
                p_statusUsed.append(PretupsI.STATUS_NOTIN);
            }

        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            throw new BTSLBaseException(e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting p_status=" + p_status + ",p_statusUsed=" + p_statusUsed);
        }
    }

    /**
     * Method loadChannelUserStatsInfo
     * This method load value of the status and the operator used with the
     * status comparison in the DAO method for
     * the CHANNEL user.
     * 
     * @param form
     * @param p_status
     * @param p_statusUsed
     * @throws Exception
     *             void
     * @author sandeep.goel
     */
    private void loadChannelUserStatsInfo(UserForm form, StringBuffer p_status, StringBuffer p_statusUsed) throws Exception {
        final UserForm theForm = (UserForm) form;
        final String methodName = "loadChannelUserStatsInfo";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered theForm.getRequestType()=" + theForm.getRequestType() + ", p_status=" + p_status + ",p_statusUsed=" + p_statusUsed);
        }

        try {
            // in add mode load the active users
            if ("add".equals(theForm.getRequestType())) {
                p_status.append(PretupsBL.userStatusActive());
                p_statusUsed.append(PretupsI.STATUS_IN);
            } else if ("associate".equals(theForm.getRequestType()))// load
            // active
            // and
            // suspended
            // users
            {
                p_status.append(PretupsBL.userStatusIn());
                p_statusUsed.append(PretupsI.STATUS_IN);
            } else if ("delete".equals(theForm.getRequestType()))// load non
            // deleted and
            // canceled
            {
                p_status.append(PretupsBL.userStatusNotIn() + ",'" + PretupsI.USER_STATUS_DELETE_REQUEST + "','" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "'");
                p_statusUsed.append(PretupsI.STATUS_NOTIN);
            } else if ("suspend".equals(theForm.getRequestType())) {
                p_status.append(PretupsBL.userStatusNotIn() + ",'" + PretupsI.USER_STATUS_SUSPEND + "','" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "','" + PretupsI.USER_STATUS_DELETE_REQUEST + "'");
                p_statusUsed.append(PretupsI.STATUS_NOTIN);
            }

            else if ("associateOther".equals(theForm.getRequestType()))// load
            // active
            // and
            // suspended
            // users
            {
                p_status.append(PretupsBL.userStatusIn());
                p_statusUsed.append(PretupsI.STATUS_IN);
            }
            // Added for bar for del by shashank
            else if ("barred".equals(theForm.getRequestType())) {
                p_status.append(PretupsBL.userStatusNotIn() + ",'" + PretupsI.USER_STATUS_DELETE_REQUEST + "','" + PretupsI.USER_STATUS_NEW + "','" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "','" + PretupsI.USER_STATUS_BAR_FOR_DEL_REQUEST + "','" + PretupsI.USER_STATUS_BARRED + "','" + PretupsI.USER_STATUS_BAR_FOR_DEL_APPROVE + "'");
                p_statusUsed.append(PretupsI.STATUS_NOTIN);
            }
            // end
            else// in edit/view/changerole/viewuserbalances/viewusercounters
                // load all users except Deleted and canceled
            {
                p_status.append(PretupsBL.userStatusNotIn());
                p_statusUsed.append(PretupsI.STATUS_NOTIN);
            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            throw new BTSLBaseException(e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting p_status=" + p_status + ",p_statusUsed=" + p_statusUsed);
        }
    }

    /**
     * Method isExistDomain
     * 
     * @param p_domainList
     *            ArrayList
     * @param p_channelUserVO
     *            ChannelUserVO
     * @return boolean
     * @throws Exception
     * @author ved.sharma
     */
    private boolean isExistDomain(ArrayList p_domainList, ChannelUserVO p_channelUserVO) throws Exception {
        final String methodName = "isExistDomain";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered p_domainList.size()=" + p_domainList.size() + ", p_channelUserVO=" + p_channelUserVO);
        }
        if (p_domainList == null || p_domainList.isEmpty()) {
            return true;
        }
        boolean isDomainExist = false;
        try {
            ListValueVO listValueVO = null;
            for (int i = 0, j = p_domainList.size(); i < j; i++) {
                listValueVO = (ListValueVO) p_domainList.get(i);
                if (listValueVO.getValue().equals(p_channelUserVO.getCategoryVO().getDomainCodeforCategory())) {
                    isDomainExist = true;
                    break;
                }
            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            throw new BTSLBaseException(e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting isDomainExist=" + isDomainExist);
        }
        return isDomainExist;
    }

    /**
     * @param theForm
     */
    private void clearOnback(UserForm theForm) {
        if (!BTSLUtil.isNullString(theForm.getSearchCriteria()) && "M".equals(theForm.getSearchCriteria())) {
            theForm.setSearchLoginId(null);
            theForm.setExternalCode(theForm.getExternalCode());
            if (theForm.getSelectDomainList() != null && theForm.getSelectDomainList().size() > 1) {
                theForm.setDomainCode(null);
            }
            if (theForm.getCategoryList() != null && theForm.getCategoryList().size() > 1) {
                theForm.setChannelCategoryCode(null);
            }
            if (theForm.getAssociatedGeographicalList() != null && theForm.getAssociatedGeographicalList().size() > 1) {
                theForm.setParentDomainCode(null);
            }
        } else if (!BTSLUtil.isNullString(theForm.getSearchCriteria()) && "L".equals(theForm.getSearchCriteria())) {
            theForm.setSearchMsisdn(null);
            theForm.setExternalCode(theForm.getExternalCode());
            if (theForm.getSelectDomainList() != null && theForm.getSelectDomainList().size() > 1) {
                theForm.setDomainCode(null);
            }
            if (theForm.getCategoryList() != null && theForm.getCategoryList().size() > 1) {
                theForm.setChannelCategoryCode(null);
            }
            if (theForm.getAssociatedGeographicalList() != null && theForm.getAssociatedGeographicalList().size() > 1) {
                theForm.setParentDomainCode(null);
            }
        } else if (!BTSLUtil.isNullString(theForm.getSearchCriteria()) && "D".equals(theForm.getSearchCriteria())) {
            theForm.setSearchMsisdn(null);
            theForm.setSearchLoginId(null);
            theForm.setExternalCode(theForm.getExternalCode());// uncommented by akanksha for claro
            // bug fixing
        } else if (!BTSLUtil.isNullString(theForm.getSearchCriteria()) && "E".equals(theForm.getSearchCriteria())) {
            theForm.setSearchMsisdn(null);
            theForm.setSearchLoginId(null);
            if (theForm.getSelectDomainList() != null && theForm.getSelectDomainList().size() > 1) {
                theForm.setDomainCode(null);
            }
            if (theForm.getCategoryList() != null && theForm.getCategoryList().size() > 1) {
                theForm.setChannelCategoryCode(null);
            }
            if (theForm.getAssociatedGeographicalList() != null && theForm.getAssociatedGeographicalList().size() > 1) {
                theForm.setParentDomainCode(null);
            }
        }
    }







    /**
     * Method constructVofromForm
     * This method is to construct VO from the FORMBEAN
     * 
     * @param request
     *            Akanksha
     * @param p_theForm
     * @param p_channelTransferVO
     * @param p_curDate
     * @throws BTSLBaseException
     */
  /*  private void constructVofromForm(HttpServletRequest request, ChnnlToChnnlReturnWithdrawForm p_theForm, ChannelTransferVO p_channelTransferVO, Date p_curDate) throws BTSLBaseException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("constructVofromForm", "Entered TheForm: " + p_theForm + " ChannelTransferVO: " + p_channelTransferVO + " CurDate " + p_curDate);
        }

        final ChannelUserVO channelUserVO = (ChannelUserVO) getUserFormSession(request);

        p_channelTransferVO.setNetworkCode(channelUserVO.getNetworkID());
        p_channelTransferVO.setNetworkCodeFor(channelUserVO.getNetworkID());
        p_channelTransferVO.setCategoryCode(p_theForm.getFromCategoryCode());
        p_channelTransferVO.setSenderGradeCode(p_theForm.getFromGradeCode());
        p_channelTransferVO.setReceiverGradeCode(p_theForm.getToGradeCode());
        p_channelTransferVO.setDomainCode(p_theForm.getDomainCode());
        p_channelTransferVO.setFromUserID(p_theForm.getFromUserID());
        p_channelTransferVO.setFromUserName(p_theForm.getFromUserName());
        p_channelTransferVO.setToUserID(p_theForm.getToUserID());
        p_channelTransferVO.setToUserName(p_theForm.getToUserName());
        p_channelTransferVO.setTransferDate(p_curDate);
        p_channelTransferVO.setGraphicalDomainCode(p_theForm.getFromGeoDomain());
        p_channelTransferVO.setCommProfileSetId(p_theForm.getFromCommissionProfileID());
        p_channelTransferVO.setCommProfileVersion(p_theForm.getFromCommissionProfileVersion());
        p_channelTransferVO.setChannelRemarks(p_theForm.getRemarks());
        p_channelTransferVO.setCreatedOn(p_curDate);
        p_channelTransferVO.setCreatedBy(channelUserVO.getActiveUserID());
        p_channelTransferVO.setModifiedOn(p_curDate);
        p_channelTransferVO.setModifiedBy(channelUserVO.getActiveUserID());
        p_channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
        p_channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN);
        p_channelTransferVO.setTransferInitatedBy(channelUserVO.getUserID());
        p_channelTransferVO.setSenderTxnProfile(p_theForm.getFromTxnProfile());
        p_channelTransferVO.setReceiverTxnProfile(p_theForm.getToTxnProfile());
        p_channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_WEB);
        p_channelTransferVO.setReceiverCategoryCode(p_theForm.getToCategoryCode());
        p_channelTransferVO.setTransferCategory(p_theForm.getTransferCategory());
        p_channelTransferVO.setRequestedQuantity(PretupsBL.getSystemAmount(p_theForm.getTotalReqQty()));
        p_channelTransferVO.setTransferMRP(PretupsBL.getSystemAmount(p_theForm.getTransferMRP()));
        p_channelTransferVO.setPayableAmount(PretupsBL.getSystemAmount(p_theForm.getPayableAmount()));
        p_channelTransferVO.setNetPayableAmount(PretupsBL.getSystemAmount(p_theForm.getNetPayableAmount()));
        p_channelTransferVO.setTotalTax1(PretupsBL.getSystemAmount(p_theForm.getTotalTax1()));
        p_channelTransferVO.setTotalTax2(PretupsBL.getSystemAmount(p_theForm.getTotalTax2()));
        p_channelTransferVO.setTotalTax3(PretupsBL.getSystemAmount(p_theForm.getTotalTax3()));
        p_channelTransferVO.setType(PretupsI.CHANNEL_TYPE_C2C);
        if (p_theForm.getOutsideHierarchyFlag()) {
            p_channelTransferVO.setControlTransfer(PretupsI.NO);
        } else {
            p_channelTransferVO.setControlTransfer(PretupsI.YES);
        }
        if (p_theForm.getReturnFlag()) {
            p_channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN);
        } else {
            p_channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW);
        }
        if (channelUserVO.getSessionInfoVO().getMessageGatewayVO() != null) {
            p_channelTransferVO.setRequestGatewayCode(channelUserVO.getSessionInfoVO().getMessageGatewayVO().getGatewayCode());
            p_channelTransferVO.setRequestGatewayType(channelUserVO.getSessionInfoVO().getMessageGatewayVO().getGatewayType());
        }
        // adding the some additional information for sender/reciever
        p_channelTransferVO.setReceiverGgraphicalDomainCode(p_theForm.getToGeoDomain());
        p_channelTransferVO.setReceiverDomainCode(p_theForm.getToDomainCode());
        p_channelTransferVO.setToUserCode(PretupsBL.getFilteredMSISDN(p_theForm.getToMSISDN()));
        p_channelTransferVO.setFromUserCode(PretupsBL.getFilteredMSISDN(p_theForm.getFromMSISDN()));
        if (p_theForm.getIsReturnFlag()) {
            p_channelTransferVO.setToChannelUserStatus(p_theForm.getFromChannelUserStatus());
            p_channelTransferVO.setFromChannelUserStatus(p_theForm.getToChannelUserStatus());
        } else {
            p_channelTransferVO.setToChannelUserStatus(p_theForm.getToChannelUserStatus());
            p_channelTransferVO.setFromChannelUserStatus(p_theForm.getFromChannelUserStatus());
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("constructVofromForm", "Exited TheForm: " + p_theForm + " ChannelTransferVO: " + p_channelTransferVO + " CurDate " + p_curDate);
        }
    }
*/
    // for updating the batch AssociateDeassociateUserBatch
    

    
	

	
	
	/**
	 * method convertTo2dArrayHeader
	 * This method is used to convert ArrayList to 2D String array for header information
	 * @param p_fileArr
	 * @param p_form
	 * @return String[][]
	 */
	private String[][] convertTo2dArrayHeader(String [][]p_fileArr,UserForm p_form)
	{
		final String methodName = "convertTo2dArrayHeader";
	    if (LOG.isDebugEnabled())
			LOG.debug(methodName, "Entered p_fileArr="+p_fileArr.length+" p_form="+p_form);
	    try
	    {
		    int rows=1;
		    int cols=0;
			    p_fileArr[rows][cols]=p_form.getUserId();
			    cols=cols+1;
			    p_fileArr[rows][cols]=p_form.getUserName();
			    cols=cols+1;
			    p_fileArr[rows][cols]=p_form.getMsisdn();
			    cols=cols+1;
			    p_fileArr[rows][cols]=p_form.getUserCode();
			    cols=cols+1;
		    	p_fileArr[rows][cols]=p_form.getWebLoginID();
		    	cols=cols+1;
		    	p_fileArr[rows][cols]=p_form.getUserType();
		    	cols=cols+1;
		    	p_fileArr[rows][cols]=p_form.getNetworkName();
		    	cols=cols+1;
		    	p_fileArr[rows][cols]=p_form.getChannelCategoryDesc();
		    	cols=cols+1;
		    
	    }catch(Exception e){
	        LOG.debug(methodName, "Exception"+e.getMessage());
	        LOG.errorTrace(methodName, e);
		   EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserAction[convertTo2dArrayHeader]", "", "", "", "Exception:" + e.getMessage());
	    }
	    if (LOG.isDebugEnabled())
			LOG.debug(methodName, "Exit p_fileArr="+p_fileArr);
		return p_fileArr;
	}
	/**
	 * Method convertTo2dArray.
	 * This method is used to convert ArrayList to 2D String array
	 * @param p_fileArr String[][]
	 * @param ArrayList p_balList
	 * @param int p_rows
	 * @return p_fileArr String[][]
	 */
	private String[][] convertTo2dArray(String [][]p_fileArr,ArrayList p_balList,int p_rows,UserForm p_form)
	{
		String methodName= "convertTo2dArray";
	    if (LOG.isDebugEnabled())
			LOG.debug(methodName, "Entered p_fileArr="+p_fileArr.length+" p_balList.size()="+p_balList.size()+" p_rows"+p_rows);
	    try{
		    Iterator iterator=p_balList.iterator();
		    int rows=0;
		    int cols;
		    UserBalancesVO balVO=null;
			while (iterator.hasNext())
			{
				balVO = (UserBalancesVO)iterator.next();
			    rows++;
			    cols=0;
			    p_fileArr[rows][cols]=balVO.getProductName();
			    cols=cols+1;
			    p_fileArr[rows][cols]=balVO.getProductCode();
			    cols=cols+1;
			    p_fileArr[rows][cols]=balVO.getProductShortCode();
			    cols=cols+1;
			    p_fileArr[rows][cols]=balVO.getBalanceStr();
			    cols=cols+1;
			    if(p_form.getAgentBalanceListSize()>0){
			    	p_fileArr[rows][cols]=balVO.getAgentBalanceStr();
			    	cols=cols+1;
			    }
			}
	    }catch(Exception e){
	        LOG.debug(methodName, "Exception"+e.getMessage());
	        LOG.errorTrace(methodName, e);
		    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, 
		    		"ChannelUserAction[convertTo2dArray]", "", "", "", "Exception:" + e.getMessage());
	    }
	    if (LOG.isDebugEnabled())
			LOG.debug(methodName, "Exit p_fileArr="+p_fileArr);
		
		return p_fileArr;
	}
	/**
	 * method convertTo2dThresholdArray
	 * This method is used to convert ArrayList to 2D String array for header information
	 * @param p_fileArr
	 * @param p_form
	 * @return String[][]
	 */
	private String[][] convertTo2dThresholdArrayHeader(String [][]p_fileArr,UserForm p_form)
	{
		String methodName= "convertTo2dThresholdArray";
	    if(LOG.isDebugEnabled())
			LOG.debug(methodName, "Entered p_fileArr="+p_fileArr.length+" p_form="+p_form);
	    try{
		    int rows=1;
		    int cols=0;
			    p_fileArr[rows][cols]=p_form.getNetworkName();
			    cols=cols+1;
			    p_fileArr[rows][cols]=p_form.getChannelCategoryDesc();
			    cols=cols+1;
			    p_fileArr[rows][cols]=p_form.getTransferProfileVO().getProfileName();
			    cols=cols+1;
			    p_fileArr[rows][cols]=p_form.getTransferProfileVO().getShortName();
			    cols=cols+1;
		    	p_fileArr[rows][cols]=p_form.getTransferProfileVO().getDescription();
		    	cols=cols+1;
		    	p_fileArr[rows][cols]=p_form.getTransferProfileVO().getStatus();
		    	cols=cols+1;
		    	p_fileArr[rows][cols]=p_form.getUserName();
		    	cols=cols+1;
		    
	    }catch(Exception e){
	        LOG.debug(methodName, "Exception"+e.getMessage());
	        LOG.errorTrace(methodName, e);
		    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, 
		    		"ChannelUserAction[convertTo2dThresholdArrayHeader]", "", "", "", "Exception:" + e.getMessage());
	    }
	    if (LOG.isDebugEnabled())
			LOG.debug(methodName, "Exit p_fileArr="+p_fileArr);
		return p_fileArr;
	}

	 
	public void setDatesToDisplayInForm(UserForm form) {
    	UserForm thisForm = (UserForm) form;
    	thisForm.setAppointmentDate(BTSLDateUtil.getSystemLocaleDate(thisForm.getAppointmentDate()));
    }
	
	//for updating the batch AssociateDeassociateUserBatch


	
}
