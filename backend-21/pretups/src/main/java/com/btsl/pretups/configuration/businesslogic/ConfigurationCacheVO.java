/**
* @(#)ConfigurationCacheVO.java
* Copyright(c) 2005, Bharti Telesoft Ltd.
* All Rights Reserved
* 
* <description>
*-------------------------------------------------------------------------------------------------
* Author                        Date            History
*-------------------------------------------------------------------------------------------------
* Sanjay Kumar Bind1            May 7, 2017     Initital Creation
*-------------------------------------------------------------------------------------------------
*
*/

package com.btsl.pretups.configuration.businesslogic;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * @author Sanjay Kumar Bind1
 *
 */
public class ConfigurationCacheVO implements Serializable {

    private String intidKey;
    private String networkCode;
    private String controlCode;
    private String serviceCode;
    private String interfaceId;
    private String intidValue;
    private String type;
    private String valueType;
    private String value;
    private String instanceId;
	private String minValue=null;
	private String maxValue=null;
	private String maxSize=null;
	private String description=null;
	private String descriptionNew=null;
	private String modifiedAllowed=null;
	private Date createdOn=null;
	private String createdBy=null;
	private String modifiedBy=null;
	private Date modifiedOn;
	private Timestamp modifiedTimeStamp;
	private int noOfOtherPrefOtherThanAll=0;
	
	
	/**
	 * Field lastModifiedTime.
	 * This field is used to check that is the record is modified during the transaction?
	 */
	private long  lastModifiedTime;
	
	private String fixedValue;
	private String fixedValueList;
	private String allowedValues;
	private boolean disableAllow;
	private String allowAction;
	private ArrayList allowedValuesList=null;
	private ArrayList descriptionList=null;
	private ArrayList descriptionListNew=null;
	private String moduleDescription=null;
	private String valueTypeDesc=null;
	public int getAllowedValuesListSize()
	{
		if(allowedValuesList!=null)
			return allowedValuesList.size();
		return 0;
	}
	public int getDescriptionListSize()
	{
		if(descriptionList!=null)
			return descriptionList.size();
		return 0;
	}
	public int getDescriptionListSizeNew()
	{
		if(descriptionListNew!=null)
			return descriptionListNew.size();
		return 0;
	}
	
    public Date getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public String getNetworkCode() {
        return networkCode;
    }

    public void setNetworkCode(String networkCode) {
        this.networkCode = networkCode;
    }

    public String getIntidKey() {
        return intidKey;
    }

    public void setIntidKey(String intidKey) {
        this.intidKey = intidKey;
    }

    public String getIntidValue() {
        return intidValue;
    }

    public void setIntidValue(String intidValue) {
        this.intidValue = intidValue;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getControlCode() {
        return controlCode;
    }

    public void setControlCode(String zoneCode) {
        controlCode = zoneCode;
    }
    
    @Override
    public String toString(){
        
        StringBuilder sbf = new StringBuilder();

        sbf.append("IntidKey="+intidKey);
        sbf.append(",networkCode="+networkCode);
        sbf.append(",zoneCode="+controlCode);
        sbf.append(",serviceCode="+serviceCode);
        sbf.append(",interfaceId="+interfaceId);
        sbf.append(",intidValue="+intidValue);
        sbf.append(",type="+type);
        sbf.append(",valueType="+valueType);
        sbf.append(",value="+value);
        sbf.append(",value="+instanceId);
		sbf.append(",minValue="+minValue);
		sbf.append(",maxValue="+maxValue);
		sbf.append(",maxSize="+maxSize);
		sbf.append(",description="+description);
		sbf.append(",descriptionNew="+descriptionNew);
		sbf.append(",modifiedAllowed="+modifiedAllowed);
		sbf.append(",createdOn="+createdOn);
		sbf.append(",createdBy="+createdBy);
		sbf.append(",modifiedOn="+modifiedOn);
		sbf.append(",modifiedBy="+modifiedBy);
	    return sbf.toString();
    }
    
    public boolean equalsConfigurationCacheVO(ConfigurationCacheVO configurationCacheVO){
        boolean flag = false;
        if(this.getModifiedTimeStamp().equals(configurationCacheVO.getModifiedTimeStamp())){
            flag = true;
        }
        
        return flag;
    }
    
    @Override
    public native int hashCode();
    
    public String getPreferenceLevel(){
        
        StringBuilder sbf = new StringBuilder(100);
        sbf.append(this.getIntidKey());
        if(this.getServiceCode() != null){
          sbf.append(" At Service Level");
        }else
           if(this.getControlCode() != null){
               sbf.append(" At Zone Level");
           }else
               if(this.getNetworkCode() != null){
                   sbf.append(" At Network Level");
               }else{
                   sbf.append(" At System Level");
               }
        
        return sbf.toString();
    }
    
    public String differences(ConfigurationCacheVO pConfigurationCacheVO){
        
        StringBuilder sbf = new StringBuilder(100);
        String startSeperator = Constants.getProperty("cachestartseparator");
        String middleSeperator = Constants.getProperty("cachemiddleseparator");
			
        if(this.getIntidValue()!= null &&  pConfigurationCacheVO.getIntidValue()!= null && !BTSLUtil.compareLocaleString(this.getIntidValue() , pConfigurationCacheVO.getIntidValue())  ){
            sbf.append(startSeperator);
            sbf.append("Name");
            sbf.append(middleSeperator);
            sbf.append(pConfigurationCacheVO.getIntidValue());
            sbf.append(middleSeperator);
            sbf.append(this.getIntidValue());            
        }
        
        if( this.getType() != null && pConfigurationCacheVO.getType() != null && ! this.getType().equals(pConfigurationCacheVO.getType()) ){
            sbf.append(startSeperator);
            sbf.append("Type");
            sbf.append(middleSeperator);
            sbf.append(pConfigurationCacheVO.getType());
            sbf.append(middleSeperator);
            sbf.append(this.getType());
        }
        
        if( this.getValueType() != null &&  pConfigurationCacheVO.getValueType() != null && ! this.getValueType().equals(pConfigurationCacheVO.getValueType()) ){
            sbf.append(startSeperator);
            sbf.append("Value Type");
            sbf.append(middleSeperator);
            sbf.append(pConfigurationCacheVO.getValueType());
            sbf.append(middleSeperator);
            sbf.append(this.getValueType());
        }
        
        if( this.getInstanceId() != null && pConfigurationCacheVO.getInstanceId() != null && ! this.getInstanceId().equals(pConfigurationCacheVO.getInstanceId())){
            sbf.append(startSeperator);
            sbf.append("Value");
            sbf.append(middleSeperator);
            sbf.append(pConfigurationCacheVO.getInstanceId());
            sbf.append(middleSeperator);
            sbf.append(this.getInstanceId());
        }
        
        
        return sbf.toString();        
    }



	@Override
	public native boolean equals(Object obj);
	
	public String logInfo(){
        
        StringBuilder sbf = new StringBuilder(100);
        String startSeperator = Constants.getProperty("cachestartseparator");
        String middleSeperator = Constants.getProperty("cachemiddleseparator");

            sbf.append(startSeperator);
            sbf.append("Name");
            sbf.append(middleSeperator);
            sbf.append(this.getIntidValue());            

            sbf.append(startSeperator);
            sbf.append("Type");
            sbf.append(middleSeperator);
            sbf.append(this.getType());

            sbf.append(startSeperator);
            sbf.append("Value Type");
            sbf.append(middleSeperator);
            sbf.append(this.getValueType());

            sbf.append(startSeperator);
            sbf.append("Value");
            sbf.append(middleSeperator);
            sbf.append(this.getInstanceId());
        
        return sbf.toString();        
    }
    
    public String getInterfaceId() {
        return interfaceId;
    }

    public void setInterfaceId(String interfaceId) {
        this.interfaceId = interfaceId;
    }

	/**This method gives the value of createdBy
	 * @return String
	 */
	public String getCreatedBy()
	{
		return createdBy;
	}

	/**This method is used to set the value of createdBy.
	 * @param createdBy 
	 */
	public void setCreatedBy(String createdBy)
	{
		this.createdBy = createdBy;
	}

	/**This method gives the value of createdOn
	 * @return Date
	 */
	public Date getCreatedOn()
	{
		return createdOn;
	}

	/**This method is used to set the value of createdOn.
	 * @param createdOn 
	 */
	public void setCreatedOn(Date createdOn)
	{
		this.createdOn = createdOn;
	}

	/**This method gives the value of description
	 * @return String
	 */
	public String getDescription()
	{
		return description;
	}

	/**This method is used to set the value of description.
	 * @param description 
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	/**This method gives the value of new description
	 * @return String
	 */
	public String getDescriptionNew()
	{
		return descriptionNew;
	}

	/**This method is used to set the value of description.
	 * @param description 
	 */
	public void setDescriptionNew(String descriptionNew)
	{
		this.descriptionNew = descriptionNew;
	}

	/**This method gives the value of lastModifiedOn
	 * @return long
	 */
	public long getLastModifiedTime()
	{
		return lastModifiedTime;
	}

	/**This method is used to set the value of lastModifiedOn.
	 * @param lastModifiedOn 
	 */
	public void setLastModifiedTime(long lastModifiedOn)
	{
		lastModifiedTime = lastModifiedOn;
	}

	/**This method gives the value of maxSize
	 * @return String
	 */
	public String getMaxSize()
	{
		return maxSize;
	}

	/**This method is used to set the value of maxSize.
	 * @param maxSize 
	 */
	public void setMaxSize(String maxSize)
	{
		this.maxSize = maxSize;
	}

	/**This method gives the value of maxValue
	 * @return String
	 */
	public String getMaxValue()
	{
		return maxValue;
	}

	/**This method is used to set the value of maxValue.
	 * @param maxValue 
	 */
	public void setMaxValue(String maxValue)
	{
		this.maxValue = maxValue;
	}

	/**This method gives the value of minValue
	 * @return String
	 */
	public String getMinValue()
	{
		return minValue;
	}

	/**This method is used to set the value of minValue.
	 * @param minValue 
	 */
	public void setMinValue(String minValue)
	{
		this.minValue = minValue;
	}

	/**This method gives the value of modifiedAllowed
	 * @return String
	 */
	public String getModifiedAllowed()
	{
		return modifiedAllowed;
	}

	/**This method is used to set the value of modifiedAllowed.
	 * @param modifiedAllowed 
	 */
	public void setModifiedAllowed(String modifiedAllowed)
	{
		this.modifiedAllowed = modifiedAllowed;
	}

	/**This method gives the value of modifiedBy
	 * @return String
	 */
	public String getModifiedBy()
	{
		return modifiedBy;
	}

	/**This method is used to set the value of modifiedBy.
	 * @param modifiedBy 
	 */
	public void setModifiedBy(String modifiedBy)
	{
		this.modifiedBy = modifiedBy;
	}

	public Timestamp getModifiedTimeStamp()
	{
		return modifiedTimeStamp;
	}

	public void setModifiedTimeStamp(Timestamp modifiedTimeStamp)
	{
		this.modifiedTimeStamp = modifiedTimeStamp;
	}

	public int getNoOfOtherPrefOtherThanAll() {
		return noOfOtherPrefOtherThanAll;
	}

	public void setNoOfOtherPrefOtherThanAll(int noOfOtherPrefOtherThanAll) {
		this.noOfOtherPrefOtherThanAll = noOfOtherPrefOtherThanAll;
	}

	public String getFixedValue()
	{
		return fixedValue;
	}

	public void setFixedValue(String fixedValue)
	{
		this.fixedValue = fixedValue;
	}

	public String getFixedValueList()
	{
		return fixedValueList;
	}

	public void setFixedValueList(String fixedValueList)
	{
		this.fixedValueList = fixedValueList;
	}

	public String getAllowedValues()
	{
		return allowedValues;
	}

	public void setAllowedValues(String allowedValues)
	{
		this.allowedValues = allowedValues;
	}

	public String getAllowAction()
	{
		return allowAction;
	}

	public void setAllowAction(String allowAction)
	{
		this.allowAction = allowAction;
	}

	public boolean getDisableAllow()
	{
		return disableAllow;
	}

	public void setDisableAllow(boolean disableAllow)
	{
		this.disableAllow = disableAllow;
	}

	public List getAllowedValuesList()
	{
		return allowedValuesList;
	}

	public void setAllowedValuesList(List allowedValuesList)
	{
		this.allowedValuesList = (ArrayList) allowedValuesList;
	}

	public String getModuleDescription()
	{
		return moduleDescription;
	}

	public void setModuleDescription(String moduleDescription)
	{
		this.moduleDescription = moduleDescription;
	}

	public String getValueTypeDesc()
	{
		return valueTypeDesc;
	}

	public void setValueTypeDesc(String valueTypeDesc)
	{
		this.valueTypeDesc = valueTypeDesc;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public List getDescriptionList()
	{
		return descriptionList;
	}

	public void setDescriptionList(List descriptionList)
	{
		this.descriptionList = (ArrayList) descriptionList;
	}
	
	public List getDescriptionListNew()
	{
		return descriptionListNew;
	}

	public void setDescriptionListNew(List descriptionListNew)
	{
		this.descriptionListNew = (ArrayList) descriptionListNew;
	}
}
