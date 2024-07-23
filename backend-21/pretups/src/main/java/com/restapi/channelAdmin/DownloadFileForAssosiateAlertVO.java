package com.restapi.channelAdmin;

import java.util.ArrayList;

import com.btsl.pretups.domain.businesslogic.CategoryVO;

public class DownloadFileForAssosiateAlertVO {
	 	private String _domainCode;
	    private String _domainName;
	    private String _domainType = null;
	    private ArrayList _domainAllList = null;
	    private ArrayList _domainList = null;
	    
	    private ArrayList _categoryList = null;
	    
	    private ArrayList _geographyList = null;
	    
	    private String _geographyCode;
	    private String _geographyName;
	    
	    private CategoryVO _categoryVO = null;
	    
	    private String _categoryName = null;
	    private String _categoryCode = null;
	    
	    /**
	     * @return Returns the domainCode.
	     */
	    public String getDomainCode() {
	        return _domainCode;
	    }

	    /**
	     * @param domainCode
	     *            The domainCode to set.
	     */
	    public void setDomainCode(String domainCode) {
	        _domainCode = domainCode;
	    }
	    
	    /**
	     * @return Returns the domainName.
	     */
	    public String getDomainName() {
	        return _domainName;
	    }

	    /**
	     * @param domainName
	     *            The domainName to set.
	     */
	    public void setDomainName(String domainName) {
	        _domainName = domainName;
	    }
	    
	    /**
	     * @return Returns the domainType.
	     */
	    public String getDomainType() {
	        return _domainType;
	    }

	    /**
	     * @param domainType
	     *            The domainType to set.
	     */
	    public void setDomainType(String domainType) {
	        _domainType = domainType;
	    }
	    
	    /**
	     * @return Returns the domainAllList.
	     */
	    public ArrayList getDomainAllList() {
	        return _domainAllList;
	    }

	    /**
	     * @param domainAllList
	     *            The domainAllList to set.
	     */
	    public void setDomainAllList(ArrayList domainAllList) {
	        _domainAllList = domainAllList;
	    }
	    
	    /**
	     * @return Returns the domainList.
	     */
	    public ArrayList getDomainList() {
	        return _domainList;
	    }

	    /**
	     * @param domainList
	     *            The domainList to set.
	     */
	    public void setDomainList(ArrayList domainList) {
	        _domainList = domainList;
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
	     * @return Returns the geographyList.
	     */
	    public ArrayList getGeographyList() {
	        return _geographyList;
	    }

	    /**
	     * @param geographyList
	     *            The geographyList to set.
	     */
	    public void setGeographyList(ArrayList geographyList) {
	        _geographyList = geographyList;
	    }
	    
	    /**
	     * @return Returns the geographyCode.
	     */
	    public String getGeographyCode() {
	        return _geographyCode;
	    }

	    /**
	     * @param geographyCode
	     *            The geographyCode to set.
	     */
	    public void setGeographyCode(String geographyCode) {
	        _geographyCode = geographyCode;
	    }
	    
	    /**
	     * @return Returns the geographyName.
	     */
	    public String getGeographyName() {
	        return _geographyName;
	    }

	    /**
	     * @param geographyName
	     *            The geographyName to set.
	     */
	    public void setGeographyName(String geographyName) {
	        _geographyName = geographyName;
	    }
	    
	    /**
	     * @return Returns the categoryVO.
	     */
	    public CategoryVO getCategoryVO() {
	        return _categoryVO;
	    }

	    /**
	     * @param categoryVO
	     *            The categoryVO to set.
	     */
	    public void setCategoryVO(CategoryVO categoryVO) {
	        _categoryVO = categoryVO;
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
	    
}
