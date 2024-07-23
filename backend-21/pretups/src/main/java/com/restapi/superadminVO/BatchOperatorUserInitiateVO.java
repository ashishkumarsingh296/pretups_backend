package com.restapi.superadminVO;

import java.util.ArrayList;
import java.util.HashMap;

//import org.apache.struts.upload.FormFile;

import com.btsl.pretups.domain.businesslogic.CategoryVO;

public class BatchOperatorUserInitiateVO {
	
	private static final long serialVersionUID = 1L;
	private String _batchName;
   // private FormFile _file;
    private String _categoryStr;
    private String _categoryCode;
    private String _categoryCodeDesc;
    private String _parentDomainTypeDesc;
    private String _parentDomainDesc;
    private String _parentDomainCode;// this hold the domain code of the logged
    // in user
    private String _grphDomainTypeName;
    private String[] _searchDomainTextArray;
    private String[] _searchDomainCode;
    private HashMap _batchOPTUserMasterMap;
    private ArrayList _errorList;
    private ArrayList _categoryList;
    private ArrayList _associatedGeographicalList;
    private ArrayList _productsList;// store the complete list of the Products
	private ArrayList _networkList;
    private ArrayList _geographicalList;
    private ArrayList _domainSearchList;
    private String _errorFlag;
    private int _totalRecords;
    private String _noOfRecords;
    private CategoryVO _categoryVO;
    
    public int getCategoryListSize() {
        if (this.getCategoryList() != null) {
            return this.getCategoryList().size();
        } else {
            return 0;
        }
    }
    
    public void setBatchName(String batchName) {
        _batchName = batchName;
    }

    public String getBatchName() {
        return _batchName;
    }

  /*  public void setFile(FormFile file) {
        _file = file;
    }

    public FormFile getFile() {
        return _file;
    }
*/
    public void setCategoryStr(String categoryStr) {
        _categoryStr = categoryStr;
    }

    public String getCategoryStr() {
        return _categoryStr;
    }

    public void setBatchOPTUserMasterMap(HashMap batchOPTUserMasterMap) {
        _batchOPTUserMasterMap = batchOPTUserMasterMap;
    }

    public HashMap getBatchOPTUserMasterMap() {
        return _batchOPTUserMasterMap;
    }

    public ArrayList getErrorList() {
        return _errorList;
    }

    public void setErrorList(ArrayList errorList) {
        _errorList = errorList;
    }

    public String getErrorFlag() {
        return _errorFlag;
    }

    public void setErrorFlag(String errorFlag) {
        _errorFlag = errorFlag;
    }

    public int getTotalRecords() {
        return _totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        _totalRecords = totalRecords;
    }

    public String getNoOfRecords() {
        return _noOfRecords;
    }

    public void setNoOfRecords(String noOfRecords) {
        _noOfRecords = noOfRecords;
    }
    
    public ArrayList getCategoryList() {
        return _categoryList;
    }

    public void setCategoryList(ArrayList categoryList) {
        _categoryList = categoryList;
    }
    
    public String getCategoryCode() {
        if (_categoryCode != null) {
            return _categoryCode.trim();
        }
        return _categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        _categoryCode = categoryCode;
    }
    
    public String getCategoryCodeDesc() {
        if (_categoryCodeDesc != null) {
            return _categoryCodeDesc.trim();
        }

        return _categoryCodeDesc;
    }

    public void setCategoryCodeDesc(String categoryCodeDesc) {
        _categoryCodeDesc = categoryCodeDesc;
    }
    
    public ArrayList getAssociatedGeographicalList() {
        return _associatedGeographicalList;
    }

    public void setAssociatedGeographicalList(ArrayList associatedGeographicalList) {
        _associatedGeographicalList = associatedGeographicalList;
    }
    
    public String getParentDomainTypeDesc() {
        if (_parentDomainTypeDesc != null) {
            return _parentDomainTypeDesc.trim();
        }

        return _parentDomainTypeDesc;
    }

    public void setParentDomainTypeDesc(String parentDomainTypeDesc) {
        _parentDomainTypeDesc = parentDomainTypeDesc;
    }
    
    public String getParentDomainCode() {
        if (_parentDomainCode != null) {
            return _parentDomainCode.trim();
        }

        return _parentDomainCode;
    }

    public void setParentDomainCode(String parentDomainCode) {
        _parentDomainCode = parentDomainCode;
    }

    public String getParentDomainDesc() {
        if (_parentDomainDesc != null) {
            return _parentDomainDesc.trim();
        }

        return _parentDomainDesc;
    }

    public void setParentDomainDesc(String parentDomainDesc) {
        _parentDomainDesc = parentDomainDesc;
    }

    public ArrayList getProductsList() {
        return _productsList;
    }

    public void setProductsList(ArrayList productsList) {
        _productsList = productsList;
    }
    
    public CategoryVO getCategoryVO() {
        return _categoryVO;
    }

    public void setCategoryVO(CategoryVO categoryVO) {
        _categoryVO = categoryVO;
    }
    
    public void setNetworkList(ArrayList networkList){
    	_networkList = networkList;
    }
    
    public ArrayList getNetworkList(){
    	return _networkList;
    }
    
    public ArrayList getGeographicalList() {
        return _geographicalList;
    }

    public void setGeographicalList(ArrayList geographicalList) {
        _geographicalList = geographicalList;
    }
    
    public String getGrphDomainTypeName() {
        if (_grphDomainTypeName != null) {
            return _grphDomainTypeName.trim();
        }

        return _grphDomainTypeName;
    }

    public void setGrphDomainTypeName(String grphDomainTypeName) {
        _grphDomainTypeName = grphDomainTypeName;
    }
    
    public ArrayList getDomainSearchList() {
        return _domainSearchList;
    }

    public void setDomainSearchList(ArrayList domainSearchList) {
        _domainSearchList = domainSearchList;
    }
    
    public void setSearchDomainTextArrayCount() {
        _searchDomainTextArray = new String[_domainSearchList.size()];
    }
    
    public String[] getSearchDomainTextArray() {
        return _searchDomainTextArray;
    }

    public void setSearchDomainTextArray(String[] searchDomainTextArray) {
        _searchDomainTextArray = searchDomainTextArray;
    }
    
    public void setSearchDomainCodeCount() {
        _searchDomainCode = new String[_domainSearchList.size()];
    }
    
    public String[] getSearchDomainCode() {
        return _searchDomainCode;
    }
    
    public void setSearchDomainCode(String[] searchDomainCode) {
        _searchDomainCode = searchDomainCode;
    }
}
