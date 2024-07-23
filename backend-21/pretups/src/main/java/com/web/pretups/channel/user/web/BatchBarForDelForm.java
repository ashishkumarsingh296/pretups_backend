package com.web.pretups.channel.user.web;

import java.util.ArrayList;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;

import com.btsl.common.ListValueVO;
import com.btsl.pretups.batch.businesslogic.BatchesVO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.util.BTSLUtil;

/**
 * @author samna.soin
 * 
 */
public class BatchBarForDelForm  {

	private static final long serialVersionUID = 1L;
	//private FormFile _file;
    private String _fileName = null;
    private String _errorFlag = null;
    private ArrayList<ListValueVO> _errorList = null;
    private int _totalRecords = 0;
    private int _noOfRecords = 0;
    private HashMap _umap = new HashMap();
    private String _status = null;
    private HashMap _modified = new HashMap();

    private CategoryVO _categoryVO = new CategoryVO();

    private String _requestType = null;
    private String _batchName = null;
    private String _batchID = null;

    private ArrayList<BatchesVO> _batchList = null;
    private String _createdBy = null;
    private String _createdOn = null;
    private String _selectedIndex = null;

    private ArrayList _batchDetailsList = null;
    private BatchesVO _batchesVO = null;

    private String _pageOffset = null;
    private String _actionType = null;
    private String _processType = null;
    private String pageCode = null;
    private int approvedRecords = 0;
    private int rejectedRecords = 0;
    
    
    
    

    public int getApprovedRecords() {
		return approvedRecords;
	}

	public void setApprovedRecords(int approvedRecords) {
		this.approvedRecords = approvedRecords;
	}

	public int getRejectedRecords() {
		return rejectedRecords;
	}

	public void setRejectedRecords(int rejectedRecords) {
		this.rejectedRecords = rejectedRecords;
	}

	public String getPageCode() {
		return pageCode;
	}

	public void setPageCode(String pageCode) {
		this.pageCode = pageCode;
	}

	private int _bar = 0;

  /*  public FormFile getFile() {
        return _file;
    }

    public void setFile(FormFile _file) {
        this._file = _file;
    }
*/
    public String getFileName() {
        return _fileName;
    }

    public void setFileName(String fileName) {
        _fileName = fileName;
    }

    /**
     * @return Returns the errorFlag.
     */
    public String getErrorFlag() {
        return _errorFlag;
    }

    /**
     * @param errorFlag
     *            The errorFlag to set.
     */
    public void setErrorFlag(String errorFlag) {
        _errorFlag = errorFlag;
    }

    /**
     * @return Returns the errorList.
     */
    public ArrayList<ListValueVO> getErrorList() {
        return _errorList;
    }

    /**
     * @param errorList
     *            The errorList to set.
     */
    public void setErrorList(ArrayList<ListValueVO> errorList) {
        _errorList = errorList;
    }

    public int getTotalRecords() {
        return _totalRecords;
    }

    public void setTotalRecords(int totalrecords) {
        _totalRecords = totalrecords;
    }

    public int getNoOfRecords() {
        return _noOfRecords;
    }

    public void setNoOfRecords(int noofrecords) {
        _noOfRecords = noofrecords;
    }

    public void setUsers(HashMap umap) {
        _umap = umap;
    }

    public HashMap getUsers() {
        return _umap;
    }

    public void setStatus(String status) {
        _status = status;
    }

    public String getStatus() {
        return _status;
    }

    public void setModified(HashMap modified) {
        _modified = modified;
    }

    public HashMap getModified() {
        return _modified;
    }

    public void setCategoryVO(CategoryVO categoryVO) {
        _categoryVO = categoryVO;
    }

    public CategoryVO getCategoryVO() {
        return _categoryVO;
    }

    /**
     * @return Returns the requestType.
     */
    public String getRequestType() {
        if (_requestType != null) {
            return _requestType.trim();
        }
        return _requestType;
    }

    /**
     * @param requestType
     *            The requestType to set.
     */
    public void setRequestType(String requestType) {
        _requestType = requestType;
    }

    public String getBatchName() {
        return _batchName;
    }

    public void setBatchName(String batchName) {
        _batchName = batchName;
    }

    public String getBatchID() {
        return _batchID;
    }

    public void setBatchID(String batchID) {
        _batchID = batchID;
    }

    /**
     * @return Returns the batchList.
     */
    public ArrayList getBatchList() {
        return _batchList;
    }

    /**
     * @param The
     *            batchList to set.
     */
    public void setBatchList(ArrayList batchList) {
        _batchList = batchList;
    }

    /**
     * @return Returns the createdBy.
     */
    public String getCreatedBy() {
        return _createdBy;
    }

    /**
     * @param createdBy
     *            The createdOn to set.
     */
    public void setCreatedBy(String createdBy) {
        _createdBy = createdBy;
    }

    /**
     * @param createdBy
     *            The createdOn to set.
     */
    public void setCreatedOn(String createdOn) {
        _createdOn = createdOn;
    }

    /**
     * @return Returns the createdOn.
     */
    public String getCreatedOn() {
        return _createdOn;
    }

    /**
     * @return Returns the selectedIndex.
     */
    public String getSelectedIndex() {
        return _selectedIndex;
    }

    /**
     * @param selectedIndex
     *            The selectedIndex to set.
     */
    public void setSelectedIndex(String selectedIndex) {
        _selectedIndex = selectedIndex;
    }

    public int getSizeOfBatchList() {
        if (_batchList != null) {
            return _batchList.size();
        } else {
            return 0;
        }
    }

    /**
     * @return Returns the batchDetailsList.
     */
    public ArrayList getBatchDetailsList() {
        return _batchDetailsList;
    }

    /**
     * @param batchDetailsList
     *            The batchDetailsList to set.
     */
    public void setBatchDetailsList(ArrayList batchDetailsList) {
        _batchDetailsList = batchDetailsList;
    }

    /**
     * @return Returns the batchesVO.
     */
    public BatchesVO getBatchesVO() {
        return _batchesVO;
    }

    /**
     * @param batchesVO
     *            The batchesVO to set.
     */
    public void setBatchesVO(BatchesVO batchesVO) {
        _batchesVO = batchesVO;
    }

    /**
     * @return Returns the actionType.
     */
    public String getActionType() {
        return _actionType;
    }

    /**
     * @param actionType
     *            The actionType to set.
     */
    public void setActionType(String actionType) {
        _actionType = actionType;
    }

    /**
     * @return Returns the pageOffset.
     */
    public String getPageOffset() {
        return _pageOffset;
    }

    /**
     * @param pageOffset
     *            The pageOffset to set.
     */
    public void setPageOffset(String pageOffset) {
        _pageOffset = pageOffset;
    }

    /**
     * @return Returns the actionType.
     */
    public String getProcessType() {
        return _processType;
    }

    /**
     * @param actionType
     *            The actionType to set.
     */
    public void setProcessType(String processType) {
        _processType = processType;
    }

    public int getBar() {
        return _bar;
    }

    public void setBar(int bar) {
        _bar = bar;
    }

    public void flush() {
        _fileName = null;
        _errorFlag = null;
        _errorList = null;
        _totalRecords = 0;
        _noOfRecords = 0;
        _umap = null;
        _status = null;
        _modified = null;

        _categoryVO = null;

        _requestType = null;

        _batchName = null;
        _batchID = null;

        _batchList = null;
        _createdBy = null;
        _createdOn = null;
        _selectedIndex = null;

        // _batchDetailsList=null;
        _batchesVO = null;
        _pageOffset = null;
        _actionType = null;

        _processType = null;
        _bar = 0;
    }
}
