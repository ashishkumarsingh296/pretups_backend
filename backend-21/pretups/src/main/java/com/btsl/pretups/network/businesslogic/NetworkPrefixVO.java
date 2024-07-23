package com.btsl.pretups.network.businesslogic;

import java.io.Serializable;

import com.btsl.common.ListValueVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * @(#)NetworkPrefixVO.java
 *                          Copyright(c) 2005, Bharti Telesoft Ltd.
 *                          All Rights Reserved
 * 
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Author Date History
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Mohit Goel 14/06/2005 Initial Creation
 * 
 *                          This class is used for store the values coming from
 *                          the database
 * 
 */
public class NetworkPrefixVO extends NetworkVO implements Serializable {

    private long _prefixID;
    private String _series;
    private String _operator;
    private String _seriesType;
    private String dbFlag;
    private ListValueVO _listValueVO=null;

	public ListValueVO getListValueVO()
	{
		return _listValueVO;
	}
	public void setListValueVO(ListValueVO listvaluevo)
	{
		_listValueVO=listvaluevo;
		
	}

    public String getOperator() {
        return _operator;
    }

    /**
     * @param operator
     *            The operator to set.
     */
    public void setOperator(String operator) {
        _operator = operator;
    }

    /**
     * @return Returns the prefixID.
     */
    public long getPrefixID() {
        return _prefixID;
    }

    /**
     * @param prefixID
     *            The prefixID to set.
     */
    public void setPrefixId(long prefixID) {
        _prefixID = prefixID;
    }

    /**
     * @return Returns the series.
     */
    public String getSeries() {
        return _series;
    }

    /**
     * @param series
     *            The series to set.
     */
    public void setSeries(String series) {
        _series = series;
    }

    /**
     * @return Returns the seriesType.
     */
    public String getSeriesType() {
        return _seriesType;
    }

    /**
     * @param seriesType
     *            The seriesType to set.
     */
    public void setSeriesType(String seriesType) {
        _seriesType = seriesType;
    }

    public String getDbFlag() {
        return dbFlag;
    }

    /**
     * @param dbFlag
     *            The dbFlag to set.
     */
    public void setDbFlag(String dbFlag) {
        this.dbFlag = dbFlag;
    }

    /**
     * @param prefixID
     *            The prefixID to set.
     */
    public void setPrefixID(long prefixID) {
        _prefixID = prefixID;
    }

    public boolean equalsNetworkPrefixVO(NetworkPrefixVO networkPrefixVO) {
        boolean flag = false;

        if (this.getModifiedTimeStamp().equals(networkPrefixVO.getModifiedTimeStamp())) {
            flag = true;
        }
        return flag;
    }

    @Override
    public native int hashCode();

    public String logInfo() {

        StringBuffer sbf = new StringBuffer(200);

        String startSeperator = Constants.getProperty("startSeperatpr");
        String middleSeperator = Constants.getProperty("middleSeperator");

        sbf.append(startSeperator);
        sbf.append("Network Code");
        sbf.append(middleSeperator);
        sbf.append(this.getNetworkCode());

        sbf.append(startSeperator);
        sbf.append("Series Type");
        sbf.append(middleSeperator);
        sbf.append(this.getSeriesType());

        return sbf.toString();
    }

    
	@Override
	public native boolean equals(Object obj);
	
	public String diffrences(NetworkPrefixVO networkPrefixVO) {

        StringBuffer sbf = new StringBuffer(200);

        String startSeperator = Constants.getProperty("startSeperatpr");
        String middleSeperator = Constants.getProperty("middleSeperator");

        if (!BTSLUtil.isNullString(this.getNetworkCode()) && this.getNetworkCode().equals(networkPrefixVO.getNetworkCode())) {
            sbf.append(startSeperator);
            sbf.append("Network Code");
            sbf.append(middleSeperator);
            sbf.append(networkPrefixVO.getNetworkCode());
            sbf.append(middleSeperator);
            sbf.append(this.getNetworkCode());
        }

        if (!BTSLUtil.isNullString(this.getSeriesType()) && this.getSeriesType().equals(networkPrefixVO.getSeriesType())) {
            sbf.append(startSeperator);
            sbf.append("Series Type");
            sbf.append(middleSeperator);
            sbf.append(networkPrefixVO.getSeriesType());
            sbf.append(middleSeperator);
            sbf.append(this.getSeriesType());
        }

        return sbf.toString();
    }

}
