package com.web.pretups.channel.user.web;

import java.util.ArrayList;

import jakarta.servlet.http.HttpServletRequest;

/*import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.ValidatorActionForm;*/

import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.BTSLUtil;

public class ChangeLocaleModel /*extends ValidatorActionForm */{
    private String _module;
    private String _moduleName;
    private String _msisdn = null;
    private ArrayList _moduleList;
    private ChannelUserVO _channelUserVO;
    private ArrayList _languageList;
    private String _languageCode;
    private String _languageName;
    private String _country;
    private ArrayList _subscriberList;
    private ArrayList _categoryList;
    private String _categoryCode;
    private String _categoryName;
    private ArrayList _userList;
    private int _userListSize;
    private String _userName;
    private String _userID;
    private String _loginUserCatCode;
    private String _loginUserCatName;
    private String _loginUserID;
    private String _loginUserName;
    private ArrayList _userPhoneInfoList;
    private ArrayList _listAfterChangeLang;
    private int _categoryListSize;
    private int _userPhoneInfoListSize;
    private String _userIDForUpdateLang;
    private String _requestType;
    private String _categoryNameForMsisdn;
    private String _userNameForMsisdn;
    private long _time = 0;

    /**
     * Method reset
     * this method is to reset the value of multibox array
     * 
     * @param mapping
     *            ActionMapping
     * @param request
     *            HttpServletRequest
     */
    /*public void reset(ActionMapping mapping, HttpServletRequest request) {
        if (BTSLUtil.isNullString(request.getParameter("btnBack"))) {
            if (this._userPhoneInfoList != null) {
                ChannelUserVO channelUserVO = null;
                for (int i = 0, j = this._userPhoneInfoList.size(); i < j; i++) {
                    channelUserVO = (ChannelUserVO) this._userPhoneInfoList.get(i);
                    channelUserVO.setStatus(PretupsI.NO);
                }
            }
        }
    }*/// end of reset

    /**
     * @return Returns the categoryNameForMsisdn.
     */
    public String getCategoryNameForMsisdn() {
        return _categoryNameForMsisdn;
    }

    /**
     * @param categoryNameForMsisdn
     *            The categoryNameForMsisdn to set.
     */
    public void setCategoryNameForMsisdn(String categoryNameForMsisdn) {
        _categoryNameForMsisdn = categoryNameForMsisdn;
    }

    /**
     * @return Returns the userNameForMsisdn.
     */
    public String getUserNameForMsisdn() {
        return _userNameForMsisdn;
    }

    /**
     * @param userNameForMsisdn
     *            The userNameForMsisdn to set.
     */
    public void setUserNameForMsisdn(String userNameForMsisdn) {
        _userNameForMsisdn = userNameForMsisdn;
    }

    /**
     * @return Returns the requestType.
     */
    public String getRequestType() {
        return _requestType;
    }

    /**
     * @param requestType
     *            The requestType to set.
     */
    public void setRequestType(String requestType) {
        _requestType = requestType;
    }

    /**
     * @return Returns the userIDForUpdateLang.
     */
    public String getUserIDForUpdateLang() {
        return _userIDForUpdateLang;
    }

    /**
     * @param userIDForUpdateLang
     *            The userIDForUpdateLang to set.
     */
    public void setUserIDForUpdateLang(String userIDForUpdateLang) {
        _userIDForUpdateLang = userIDForUpdateLang;
    }

    /**
     * @return Returns the userPhoneInfoListSize.
     */
    public int getUserPhoneInfoListSize() {
        return _userPhoneInfoListSize;
    }

    /**
     * @param userPhoneInfoListSize
     *            The userPhoneInfoListSize to set.
     */
    public void setUserPhoneInfoListSize(int userPhoneInfoListSize) {
        _userPhoneInfoListSize = userPhoneInfoListSize;
    }

    /**
     * @param categoryListSize
     *            The categoryListSize to set.
     */
    public void setCategoryListSize(int categoryListSize) {
        _categoryListSize = categoryListSize;
    }

    /**
     * @return Returns the categoryListSize.
     */
    public int getCategoryListSize() {
        if (this._categoryList != null) {
            return this._categoryList.size();
        }
        return 0;
    }

    public void setUserVOForSelfLangSetIndexed(int i, ChannelUserVO channelUserVO) {
        if (this._userPhoneInfoList != null) {
            this._userPhoneInfoList.set(i, channelUserVO);
        }
    }

    public ChannelUserVO getUserVOForSelfLangSetIndexed(int i) {
        if (this._userPhoneInfoList != null) {
            return (ChannelUserVO) this._userPhoneInfoList.get(i);
        }
        return null;
    }

    public void setUserVOForSelfLangSet(ArrayList<ChannelUserVO> userPhoneInfoList) {
            this._userPhoneInfoList = userPhoneInfoList;
    }

    public ArrayList<ChannelUserVO> getUserVOForSelfLangSet() {
        return _userPhoneInfoList;
    }
    
    public void setUserVOForLangSetIndexed(int i, ChannelUserVO channelUserVO) {
        if (this._userPhoneInfoList != null) {
            this._userPhoneInfoList.set(i, channelUserVO);
        }
    }

    public ChannelUserVO getUserVOForLangSetIndexed(int i) {
        if (this._userPhoneInfoList != null) {
            return (ChannelUserVO) this._userPhoneInfoList.get(i);
        }
        return null;
    }
    
    
    public void setUserVOForLangSet(ArrayList<ChannelUserVO> _userPhoneInfoList) {
            this._userPhoneInfoList = _userPhoneInfoList;
    }

    public ArrayList<ChannelUserVO> getUserVOForLangSet() {
        return _userPhoneInfoList;
    }

    /**
     * @return Returns the listAfterChangeLang.
     */
    public ArrayList getListAfterChangeLang() {
        return _listAfterChangeLang;
    }

    /**
     * @param listAfterChangeLang
     *            The listAfterChangeLang to set.
     */
    public void setListAfterChangeLang(ArrayList listAfterChangeLang) {
        _listAfterChangeLang = listAfterChangeLang;
    }

    /**
     * @return Returns the languageName.
     */
    public String getLanguageName() {
        return _languageName;
    }

    /**
     * @param languageName
     *            The languageName to set.
     */
    public void setLanguageName(String languageName) {
        _languageName = languageName;
    }

    /**
     * @return Returns the userPhoneInfoList.
     */
    public ArrayList<ChannelUserVO> getUserPhoneInfoList() {
        return _userPhoneInfoList;
    }

    /**
     * @param userPhoneInfoList
     *            The userPhoneInfoList to set.
     */
    public void setUserPhoneInfoList(ArrayList<ChannelUserVO> userPhoneInfoList) {
        this._userPhoneInfoList = userPhoneInfoList;
    }

    /**
     * @return Returns the userListSize.
     */
    public int getUserListSize() {
        return _userListSize;
    }

    /**
     * @param userListSize
     *            The userListSize to set.
     */
    public void setUserListSize(int userListSize) {
        _userListSize = userListSize;
    }

    /**
     * @return Returns the userList.
     */
    public ArrayList getUserList() {
        return _userList;
    }

    /**
     * @param userList
     *            The userList to set.
     */
    public void setUserList(ArrayList userList) {
        _userList = userList;
    }

    /**
     * @return Returns the loginUserID.
     */
    public String getLoginUserID() {
        return _loginUserID;
    }

    /**
     * @param loginUserID
     *            The loginUserID to set.
     */
    public void setLoginUserID(String loginUserID) {
        _loginUserID = loginUserID;
    }

    /**
     * @return Returns the loginUserName.
     */
    public String getLoginUserName() {
        return _loginUserName;
    }

    /**
     * @param loginUserName
     *            The loginUserName to set.
     */
    public void setLoginUserName(String loginUserName) {
        _loginUserName = loginUserName;
    }

    /**
     * @return Returns the loginUserCatCode.
     */
    public String getLoginUserCatCode() {
        return _loginUserCatCode;
    }

    /**
     * @param loginUserCatCode
     *            The loginUserCatCode to set.
     */
    public void setLoginUserCatCode(String loginUserCatCode) {
        _loginUserCatCode = loginUserCatCode;
    }

    /**
     * @return Returns the loginUserCatName.
     */
    public String getLoginUserCatName() {
        return _loginUserCatName;
    }

    /**
     * @param loginUserCatName
     *            The loginUserCatName to set.
     */
    public void setLoginUserCatName(String loginUserCatName) {
        _loginUserCatName = loginUserCatName;
    }

    /**
     * @return Returns the userID.
     */
    public String getUserID() {
        return _userID;
    }

    /**
     * @param userID
     *            The userID to set.
     */
    public void setUserID(String userID) {
        _userID = userID;
    }

    /**
     * @return Returns the userName.
     */
    public String getUserName() {
        return _userName;
    }

    /**
     * @param userName
     *            The userName to set.
     */
    public void setUserName(String userName) {
        _userName = userName;
    }

    /**
     * @return Returns the categoryCode.
     */
    public String getCategoryCode() {
        return _categoryCode;
    }

    /**
     * @param categoryCode
     *            The categoryCode to set.
     */
    public void setCategoryCode(String categoryCode) {
        _categoryCode = categoryCode;
    }

    /**
     * @return Returns the categoryList.
     */
    public ArrayList getCategoryList() {
        return _categoryList;
    }

    /**
     * @param categoryList
     *            The categoryList to set.
     */
    public void setCategoryList(ArrayList categoryList) {
        _categoryList = categoryList;
    }

    /**
     * @return Returns the categoryName.
     */
    public String getCategoryName() {
        return _categoryName;
    }

    /**
     * @param categoryName
     *            The categoryName to set.
     */
    public void setCategoryName(String categoryName) {
        _categoryName = categoryName;
    }

    /**
     * To get the value of subscriberList field
     * 
     * @return subscriberList.
     */
    public ArrayList getSubscriberList() {
        return _subscriberList;
    }

    /**
     * To set the value of subscriberList field
     */
    public void setSubscriberList(ArrayList subscriberList) {
        _subscriberList = subscriberList;
    }

    /**
     * To get the value of country field
     * 
     * @return country.
     */
    public String getCountry() {
        return _country;
    }

    /**
     * To set the value of country field
     */
    public void setCountry(String country) {
        _country = country;
    }

    /**
     * To get the value of languageCode field
     * 
     * @return languageCode.
     */
    public String getLanguageCode() {
        return _languageCode;
    }

    /**
     * To set the value of languageCode field
     */
    public void setLanguageCode(String languageCode) {
        _languageCode = languageCode;
    }

    /**
     * To get the value of languageList field
     * 
     * @return languageList.
     */
    public ArrayList getLanguageList() {
        return _languageList;
    }

    /**
     * To set the value of languageList field
     */
    public void setLanguageList(ArrayList languageList) {
        _languageList = languageList;
    }

    /**
     * To get the value of channelUserList field
     * 
     * @return channelUserList.
     */
    public ChannelUserVO getChannelUserVO() {
        return _channelUserVO;
    }

    /**
     * To set the value of channelUserList field
     */
    public void setChannelUserVO(ChannelUserVO channelUserVO) {
        _channelUserVO = channelUserVO;
    }

    /**
     * To get the value of moduleList field
     * 
     * @return moduleList.
     */
    public ArrayList getModuleList() {
        return _moduleList;
    }

    /**
     * To set the value of moduleList field
     */
    public void setModuleList(ArrayList moduleList) {
        _moduleList = moduleList;
    }

    /**
     * To get the value of module field
     * 
     * @return module.
     */
    public String getModule() {
        return _module;
    }

    /**
     * To set the value of module field
     */
    public void setModule(String module) {
        _module = module;
    }

    /**
     * To get the value of moduleName field
     * 
     * @return moduleName.
     */
    public String getModuleName() {
        return _moduleName;
    }

    /**
     * To set the value of moduleName field
     */
    public void setModuleName(String moduleName) {
        _moduleName = moduleName;
    }

    /**
     * To get the value of msisdn field
     * 
     * @return msisdn.
     */
    public String getMsisdn() {
        return _msisdn;
    }

    /**
     * To set the value of msisdn field
     */
    public void setMsisdn(String msisdn) {
        if (msisdn != null) {
            _msisdn = msisdn.trim();
        }
    }

    /**
     * Method validate.
     * This method is used to ignore the validation if the user clicks back
     * button.
     * 
     * @param mapping
     *            ActionMapping
     * @param request
     *            HttpServletRequest
     * @return ActionErrors
     */

    public void flush() {
        _module = null;
        _moduleName = null;
        _msisdn = null;
        _moduleList = null;
        _channelUserVO = null;
        _languageList = null;
        _languageCode = null;
        _country = null;
        _subscriberList = null;
        _categoryCode = null;
        _userName = null;
        _userID = null;
    }

    public void semiFlush() {
        _userID = null;
        _userName = null;
    }

    /**
     * @return Returns the time.
     */
    public long getTime() {
        return _time;
    }

    /**
     * @param time
     *            The time to set.
     */
    public void setTime(long time) {
        _time = time;
    }
}
