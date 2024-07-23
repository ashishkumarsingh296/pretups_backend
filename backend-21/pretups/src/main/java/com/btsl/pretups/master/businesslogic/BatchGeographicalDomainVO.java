package com.btsl.pretups.master.businesslogic;

public class BatchGeographicalDomainVO extends GeographicalDomainVO implements Comparable 
{
    private static final long serialVersionUID = 8028069464657197543L;
	private String _recordNumber;
    private String _batchName;
    private String _action;
    private int _sequenceNumber=-1;

	public String toString()
	{
	    String str  = super.toString();
	    str = str+",_recordNumber="+_recordNumber;
	    str = str+",_batchName="+_batchName;
	    str = str+",_action="+_action;
	    str = str+",_sequenceNumber="+_sequenceNumber;
	    return str;
	}

    /**
     * @return Returns the recordNumber.
     */
    public String getRecordNumber() {
        return _recordNumber;
    }
    /**
     * @param recordNumber The recordNumber to set.
     */
    public void setRecordNumber(String recordNumber) {
        _recordNumber = recordNumber;
    }
    
    /**
     * @return Returns the batchName.
     */
    public String getBatchName() {
        return _batchName;
    }
    /**
     * @param batchName The batchName to set.
     */
    public void setBatchName(String batchName) {
        _batchName = batchName;
    }

    /**
     * @return Returns the _action.
     */
	public String getAction() {
		return _action;
	}
	
	/**
     * @param action The action to set.
     */
	public void setAction(String action) {
		this._action = action;
	}
	
	/**
     * @return Returns the _sequenceNumber.
     */
	public int getSequenceNumber() {
		return _sequenceNumber;
	}
	
	/**
     * @param sequenceNumber The sequenceNumber to set.
     */
	public void setSequenceNumber(int sequenceNumber) {
		this._sequenceNumber = sequenceNumber;
	}

	@Override
	public int compareTo(Object obj) {
		int compareSequence = ((BatchGeographicalDomainVO)obj).getSequenceNumber();
		/* For Ascending order*/
		
        return this._sequenceNumber-compareSequence;

	}
    
}
