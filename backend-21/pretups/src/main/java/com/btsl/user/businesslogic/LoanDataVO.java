package com.btsl.user.businesslogic;

import java.util.ArrayList;
import java.util.Date;

import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;

public class LoanDataVO {
	
	 public String getFileName() {
		return _fileName;
	}
	public void setFileName(String _fileName) {
		this._fileName = _fileName;
	}
	public String getFilePath() {
		return _filePath;
	}
	public void setFilePath(String _filePath) {
		this._filePath = _filePath;
	}
	public int getNoOfRecordsInFile() {
		return _noOfRecordsInFile;
	}
	public void setNoOfRecordsInFile(int _noOfRecordsInFile) {
		this._noOfRecordsInFile = _noOfRecordsInFile;
	}
	public int getMaxNoOfRecordsAllowed() {
		return _maxNoOfRecordsAllowed;
	}
	public void setMaxNoOfRecordsAllowed(int _maxNoOfRecordsAllowed) {
		this._maxNoOfRecordsAllowed = _maxNoOfRecordsAllowed;
	}
	public String getProductID() {
		return _productID;
	}
	public void setProductID(String _productID) {
		this._productID = _productID;
	}
	public ChannelUserVO getChannelUserVO() {
		return _channelUserVO;
	}
	public void setChannelUserVO(ChannelUserVO _channelUserVO) {
		this._channelUserVO = _channelUserVO;
	}
	public Date getCurrentDate() {
		return _currentDate;
	}
	public void setCurrentDate(Date _currentDate) {
		this._currentDate = _currentDate;
	}
	public int getActualNoOfRecords() {
		return _actualNoOfRecords;
	}
	public void setActualNoOfRecords(int _actualNoOfRecords) {
		this._actualNoOfRecords = _actualNoOfRecords;
	}
	public String getProcessType() {
		return _processType;
	}
	public void setProcessType(String _processType) {
		this._processType = _processType;
	}
	public ArrayList getErrorArrayList() {
		return _errorArrayList;
	}
	public void setErrorArrayList(ArrayList _errorArrayList) {
		this._errorArrayList = _errorArrayList;
	}
	public String getRunningFromCron() {
		return _runningFromCron;
	}
	public void setRunningFromCron(String _runningFromCron) {
		this._runningFromCron = _runningFromCron;
	}

	public String getNetwrkID() {
		return netwrkID;
	}
	public void setNetwrkID(String netwrkID) {
		this.netwrkID = netwrkID;
	}
   
	public ArrayList getLoanArrayList() {
	        return _loanArrayList;
	}

	public void setLoanArrayList(ArrayList loanArrayList) {
	        _loanArrayList = loanArrayList;
	}
		private String _fileName = null;
	    private String _filePath = null;
	    private int _noOfRecordsInFile = 0;
	    private int _maxNoOfRecordsAllowed = 0;
	    private String _productID = null;
	    private ChannelUserVO _channelUserVO = null;
	    private Date _currentDate = null;
	    private int _actualNoOfRecords = 0;
	    private ArrayList _loanArrayList = null;
	    public static String _MANUALPROCESSTYPE = "MANUAL";
	    public static String _AUTOPROCESSTYPE = "AUTO";
	    private String _processType = _MANUALPROCESSTYPE; // Dont set if process
	                                                      // type is manual else set
	                                                      // AUTO
	    private ArrayList _errorArrayList = null; // Added to save information about
	                                              // errors in voucher file
	    private String _runningFromCron = null;
	    private String netwrkID=null;
	    private String retailerUserID=null;
	    private String loanAmount=null;
	    private String loanThreshold=null;
	    private String productCode=null;
	    
	    
	    @Override
		public String toString() {
	    	
	    	 StringBuffer sbf = new StringBuffer();
		        sbf.append("LoanDataVO [_fileName= ");
		        sbf.append( _fileName );
		        sbf.append( ", _filePath=" );
		        sbf.append( _filePath );
		        sbf.append( ", _noOfRecordsInFile=");
		        sbf.append( _noOfRecordsInFile );
		        sbf.append( ", _maxNoOfRecordsAllowed=" );
		        sbf.append( _maxNoOfRecordsAllowed );
		        sbf.append( ", _productID=");
		        sbf.append( _productID );
		        sbf.append( ", _channelUserVO=" );
		        sbf.append( _channelUserVO );
		        sbf.append( ", _currentDate=" );
		        sbf.append( _currentDate);
		        sbf.append( ", _actualNoOfRecords=" );
		        sbf.append( _actualNoOfRecords );
		        sbf.append( ", _loanArrayList=" );
		        sbf.append( _loanArrayList);
		        sbf.append( ", _processType=" );
		        sbf.append( _processType );
		        sbf.append( ", _errorArrayList=" );
		        sbf.append( _errorArrayList );
		        sbf.append( ", _runningFromCron=");
		        sbf.append( _runningFromCron );
					sbf.append( ", netwrkID=" );
					sbf.append( netwrkID );
					sbf.append( ", retailerUserID=" );
					sbf.append( retailerUserID);
					sbf.append( ", loanAmount=" );
					sbf.append( loanAmount );
					sbf.append( ", loanThreshold=" );
					sbf.append( loanThreshold );
					sbf.append( ", productCode=" );
					sbf.append( productCode);
					sbf.append( "]");
					
					return sbf.toString();
		}
		public String getRetailerUserID() {
			return retailerUserID;
		}
		public void setRetailerUserID(String retailerUserID) {
			this.retailerUserID = retailerUserID;
		}
		public String getLoanAmount() {
			return loanAmount;
		}
		public void setLoanAmount(String loanAmount) {
			this.loanAmount = loanAmount;
		}
		public String getLoanThreshold() {
			return loanThreshold;
		}
		public void setLoanThreshold(String loanThreshold) {
			this.loanThreshold = loanThreshold;
		}
		
		public String getProductCode() {
				return productCode;
		}


		public void setProductCode(String productCode) {
				this.productCode = productCode;
			}
	    
}
