/*
 * COPYRIGHT: Mahindra Comviva Technologies Pvt. Ltd.
 *
 * This software is the sole property of Comviva and is protected
 * by copyright law and international treaty provisions. Unauthorized
 * reproduction or redistribution of this program, or any portion of
 * it may result in severe civil and criminal penalties and will be
 * prosecuted to the maximum extent possible under the law.
 * Comviva reserves all rights not expressly granted. You may not
 * reverse engineer, decompile, or disassemble the software, except
 * and only to the extent that such activity is expressly permitted
 * by applicable law notwithstanding this limitation.

 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY
 * KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE. YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY
 * AND THE USE OF THIS SOFTWARE. Comviva SHALL NOT BE LIABLE FOR
 * ANY DAMAGES WHATSOEVER ARISING OUT OF THE USE OF OR INABILITY TO
 * USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.client.pretups.channel.profile.web;

import java.util.ArrayList;

import jakarta.servlet.http.HttpServletRequest;

import org.spring.custom.action.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.spring.custom.action.action.ActionMessage;
import org.spring.custom.action.action.ActionMessages;
import org.apache.struts.validator.ValidatorActionForm;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileDeatilsVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
/**
 * @author manish.doodi
 *
 */
public class OtherCommissionProfileForm extends ValidatorActionForm{

	private String requestType;

	private ArrayList otherCommissionTypeList;
	private ArrayList gradeList;
	private ArrayList categoryList;
	private ArrayList gatewayList;
	private String commissionType;
	private String gradeCode;
	private String categoryCode;
	private String gatewayCode;
	private String commissionTypeValue;
	private String o2cFlag;
	private String c2cFlag;
	private String commissionTypeAsString;
	private String commissionTypeValueAsString;
	private String networkName;
	private String profileName;
	private ArrayList amountTypeList;
	private ArrayList slabsList;
	private ArrayList commissionProfileList;
	private int locationIndex;
	private String selectCommProfileSetID;
	private ArrayList selectCommProfileSetList;
	private boolean modifyAllowed=false;
		
    @Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = null;
        /*when user click on the 
         *  no need to validate the form
         */
       
        if(request.getParameter("back")==null){
             errors = super.validate(mapping,request);
        } 
       
        //validation for the slab Filed on setTypeForOtherCommission.jsp
       
        if(request.getParameter("addCommission")!=null && (errors==null || errors.size()==0))
        { 
            if(errors==null)
                errors = new ActionErrors();
            
            if(c2cFlag == "N" && o2cFlag == "N" )
            {
            	errors.add(ActionMessages.GLOBAL_MESSAGE,new ActionMessage("profile.addothercommissionprofile.error.transactiontypeempty"));
            }
            if(BTSLUtil.isNullString(profileName))
            {
            	errors.add(ActionMessages.GLOBAL_MESSAGE,new ActionMessage("profile.addothercommissionprofile.error.profilenameempty"));
            }

            boolean notExistFlag = true;
            if(slabsList!=null && !(slabsList.isEmpty()))
	        {
            	
                CommissionProfileDeatilsVO slabVO = null;
	            for(int i=0,j=slabsList.size(); i<j; i++)
	            {
	                slabVO = (CommissionProfileDeatilsVO)slabsList.get(i);
	                
	                //set the value in rate type fields according to the Type
	                double startRange = 0;
	                double endRange = 0;
	                double commRate = 0;
	                if(!BTSLUtil.isNullString(slabVO.getStartRangeAsString()))
	                { 
	                	startRange = Double.parseDouble(slabVO.getStartRangeAsString());
	                	if(startRange<=0)
		                {
		                    notExistFlag = false;
		                    errors.add(ActionMessages.GLOBAL_MESSAGE,new ActionMessage("profile.addcommissionprofile.error.fromrangeinvalid",i+1+""));
		                }
	                    
	                }
	                if(!BTSLUtil.isNullString(slabVO.getEndRangeAsString()))
	                { 
	                	endRange = Double.parseDouble(slabVO.getEndRangeAsString());
	                	 if(endRange<=0)
	 	                {
	                		 notExistFlag = false;
	 	                    errors.add(ActionMessages.GLOBAL_MESSAGE,new ActionMessage("profile.addcommissionprofile.error.torangeinvalid",i+1+""));
	 	                }
	                    
	                }   
	                if(!BTSLUtil.isNullString(slabVO.getCommRateAsString()))
	                {
	                    commRate = Double.parseDouble(slabVO.getCommRateAsString());     
	                }
	                if(startRange > endRange)
	                {
	                    errors.add(ActionMessages.GLOBAL_MESSAGE,new ActionMessage("profile.addcommissionprofile.error.invalidendrange",i+1+""));
	                }
	                
	                String value = null;
	                
	                if(startRange>0)
	                {
	                    notExistFlag = false;
	                    //check for commission rate
	                    if(PretupsI.AMOUNT_TYPE_PERCENTAGE.equals(slabVO.getCommType()))
		                {
		                    if(commRate<0 || commRate>100)
		                    {
		                        value = "100";
		                        errors.add(ActionMessages.GLOBAL_MESSAGE,new ActionMessage("profile.addcommissionprofile.error.invalidcommrate",value));
		                    }
		                }
	                    else{
		                    
		                	if(commRate<0)
		                	{
		                		value = "0";
	                            errors.add(ActionMessages.GLOBAL_MESSAGE,new ActionMessage("profile.addothercommissionprofile.error.invalidcommrate",value));
		                	}
	                		
	                    }		              
	                }
  		           
	               //set the values in the actual rates filed exist on the vo
		           try 
		           {
		              slabVO.setStartRange(PretupsBL.getSystemAmount(startRange));
                      slabVO.setEndRange(PretupsBL.getSystemAmount(endRange));
                      
                      if(PretupsI.AMOUNT_TYPE_AMOUNT.equals(slabVO.getCommType()))
                          slabVO.setCommRate(PretupsBL.getSystemAmount(commRate));
                      else
                          slabVO.setCommRate(commRate);
		           } catch (BTSLBaseException e) {}
	            }
	        }
	        
            if(slabsList==null || slabsList.size()==0)
	        {
	            errors.add(ActionMessages.GLOBAL_MESSAGE,new ActionMessage("profile.addcommissionprofile.error.commissionslablistempty"));
	        }
            else if(notExistFlag)
            {
                errors.add(ActionMessages.GLOBAL_MESSAGE,new ActionMessage("profile.addcommissionprofile.error.commissionslablistempty"));
            }
        }
         return errors;
    }  
	/**
	 * @return the modifyAllowed
	 */
	public boolean isModifyAllowed() {
		return modifyAllowed;
	}

	/**
	 * @param modifyAllowed the modifyAllowed to set
	 */
	public void setModifyAllowed(boolean modifyAllowed) {
		this.modifyAllowed = modifyAllowed;
	}

	/**
	 * @return the commissionTypeAsString
	 */
	public String getCommissionTypeAsString() {
		return commissionTypeAsString;
	}

	/**
	 * @param commissionTypeAsString the commissionTypeAsString to set
	 */
	public void setCommissionTypeAsString(String commissionTypeAsString) {
		this.commissionTypeAsString = commissionTypeAsString;
	}

	/**
	 * @return the commissionTypeValueAsString
	 */
	public String getCommissionTypeValueAsString() {
		return commissionTypeValueAsString;
	}

	/**
	 * @param commissionTypeValueAsString the commissionTypeValueAsString to set
	 */
	public void setCommissionTypeValueAsString(String commissionTypeValueAsString) {
		this.commissionTypeValueAsString = commissionTypeValueAsString;
	}
     /**
     * @return Returns the selectCommProfileSetList.
     */
    public ArrayList getSelectCommProfileSetList() {
        return selectCommProfileSetList;
    }

    /**
     * @param selectCommProfileSetList The selectCommProfileSetList to set.
     */
    public void setSelectCommProfileSetList(ArrayList selectCommProfileSetList) {
        this.selectCommProfileSetList = selectCommProfileSetList;
    }

 public int getSelectCommProfileSetListCount()
    {
    	 if(selectCommProfileSetList!=null && !(selectCommProfileSetList.isEmpty()))
    	    {
    	        return selectCommProfileSetList.size();
    	    }
    	    else
    	    {
    	        return 0;
    	    }
    }
    /**
     * @return Returns the selectCommProfileSetID.
     */
    public String getSelectCommProfileSetID() {
        return selectCommProfileSetID;
    }
    /**
     * @param selectCommProfileSetID The selectCommProfileSetID to set.
     */
    public void setSelectCommProfileSetID(String selectCommProfileSetID) {
        if(selectCommProfileSetID!=null)
            this.selectCommProfileSetID = selectCommProfileSetID.trim();
        else
            this.selectCommProfileSetID = selectCommProfileSetID;
    }
    
    public String getRequestType() {
        return requestType;
    }
    /**
     * @param requestType The requestType to set.
     */
    public void setRequestType(String requestType) {
        if(requestType!=null)
           this. requestType = requestType.trim();
        else
           this. requestType = requestType;
    }

	/**
	 * @return the o2cFlag
	 */
	public String getO2cFlag() {
		return o2cFlag;
	}
	/**
	 * @param o2cFlag the o2cFlag to set
	 */
	public void setO2cFlag(String o2cFlag) {
		this.o2cFlag = o2cFlag;
	}
	/**
	 * @return the c2cFlag
	 */
	public String getC2cFlag() {
		return c2cFlag;
	}
	/**
	 * @param c2cFlag the c2cFlag to set
	 */
	public void setC2cFlag(String c2cFlag) {
		this.c2cFlag = c2cFlag;
	}
	/**
	 * @return the locationIndex
	 */
	public int getLocationIndex() {
		return locationIndex;
	}
	/**
	 * @param locationIndex the locationIndex to set
	 */
	public void setLocationIndex(int locationIndex) {
		this.locationIndex = locationIndex;
	}
	/**
	 * @return the profileName
	 */
	public String getProfileName() {
		return profileName;
	}
	/**
	 * @param profileName the profileName to set
	 */
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	/**
	 * @return the networkName
	 */
	public String getNetworkName() {
		return networkName;
	}
	/**
	 * @param networkName the networkName to set
	 */
	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}

	public ArrayList getCommissionProfileList() {
		return commissionProfileList;
	}
	/**
	 * @param commissionProfileList the commissionProfileList to set
	 */
	public void setCommissionProfileList(ArrayList commissionProfileList) {
		this.commissionProfileList = commissionProfileList;
	}
	/**
	 * @return the amountTypeList
	 */
	public ArrayList getAmountTypeList() {
		return amountTypeList;
	}
	/**
	 * @param amountTypeList the amountTypeList to set
	 */
	public void setAmountTypeList(ArrayList amountTypeList) {
		this.amountTypeList = amountTypeList;
	}
	/**
	 * @return the slabsList
	 */
	public ArrayList getSlabsList() {
		return slabsList;
	}
	/**
	 * @param slabsList the slabsList to set
	 */
	public void setSlabsList(ArrayList slabsList) {
		this.slabsList = slabsList;
	}

	/**
	 * @return the commissionTypeValue
	 */
	public String getCommissionTypeValue() {
		return commissionTypeValue;
	}
	/**
	 * @param commissionTypeValue the commissionTypeValue to set
	 */
	public void setCommissionTypeValue(String commissionTypeValue) {
		this.commissionTypeValue = commissionTypeValue;
	}
	/**
	 * @return the gradeCode
	 */
	public String getGradeCode() {
		return gradeCode;
	}
	/**
	 * @param gradeCode the gradeCode to set
	 */
	public void setGradeCode(String gradeCode) {
		this.gradeCode = gradeCode;
	}
	/**
	 * @return the categoryCode
	 */
	public String getCategoryCode() {
		return categoryCode;
	}
	/**
	 * @param categoryCode the categoryCode to set
	 */
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	/**
	 * @return the gatewayCode
	 */
	public String getGatewayCode() {
		return gatewayCode;
	}
	/**
	 * @param gatewayCode the gatewayCode to set
	 */
	public void setGatewayCode(String gatewayCode) {
		this.gatewayCode = gatewayCode;
	}
	/**
	 * @return the commissionType
	 */
	public String getCommissionType() {
		return commissionType;
	}
	/**
	 * @param commissionType the commissionType to set
	 */
	public void setCommissionType(String commissionType) {
		this.commissionType = commissionType;
	}
	/**
	 * @return the otherCommissionTypeList
	 */
	public ArrayList getOtherCommissionTypeList() {
		return otherCommissionTypeList;
	}
	/**
	 * @param otherCommissionTypeList the otherCommissionTypeList to set
	 */
	public void setOtherCommissionTypeList(ArrayList otherCommissionTypeList) {
		this.otherCommissionTypeList = otherCommissionTypeList;
	}
	/**
	 * @return the gradeList
	 */
	public ArrayList getGradeList() {
		return gradeList;
	}
	/**
	 * @param gradeList the gradeList to set
	 */
	public void setGradeList(ArrayList gradeList) {
		this.gradeList = gradeList;
	}
	/**
	 * @return the categoryList
	 */
	public ArrayList getCategoryList() {
		return categoryList;
	}
	/**
	 * @param categoryList the categoryList to set
	 */
	public void setCategoryList(ArrayList categoryList) {
		this.categoryList = categoryList;
	}
	/**
	 * @return the gatewayList
	 */
	public ArrayList getGatewayList() {
		return gatewayList;
	}
	/**
	 * @param gatewayList the gatewayList to set
	 */
	public void setGatewayList(ArrayList gatewayList) {
		this.gatewayList = gatewayList;
	}

	public CommissionProfileDeatilsVO getCommSlabsListIndexed(int i) {
		return (CommissionProfileDeatilsVO)slabsList.get(i);
	}

	public void setCommSlabsListIndexed(int i, CommissionProfileDeatilsVO commissionProfileDeatilsVO) {
		slabsList.set(i,commissionProfileDeatilsVO);
	}
	
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
        if(request.getParameter("addCommission")!=null && !(this.isModifyAllowed()))
        {
            c2cFlag = "N";
            o2cFlag = "N";
        }
        
    }

	public void flush()
	{
		requestType = null;
		otherCommissionTypeList = null;
		gradeList = null;
		categoryList = null;
		gatewayList = null;
		commissionType = null;
		gradeCode = null;
		categoryCode = null;
		gatewayCode = null;
		commissionTypeValue = null;
		o2cFlag = null;
		c2cFlag = null;
		commissionTypeAsString = null;
		commissionTypeValueAsString = null;
		networkName = null;
		profileName = null;
		amountTypeList = null;	 
		commissionProfileList = null;
		locationIndex = -1;
		selectCommProfileSetID = null;
		selectCommProfileSetList = null;		
		CommissionProfileDeatilsVO commissionProfileDeatilsVO = null;		
        int length = Integer.parseInt(Constants.getProperty("ASSIGN_COMMISSION_SLABS_LENGTH"));               
           	slabsList = new ArrayList();
             for(int i=0;i<length;i++)
             {
               commissionProfileDeatilsVO = new CommissionProfileDeatilsVO();
               slabsList.add(commissionProfileDeatilsVO);
             }
	}
	
	public void semiFlush()
	{	
		o2cFlag = null;
		c2cFlag = null;		
		profileName = null;	
		CommissionProfileDeatilsVO commissionProfileDeatilsVO = null;		
        int length = Integer.parseInt(Constants.getProperty("ASSIGN_COMMISSION_SLABS_LENGTH"));               
           	slabsList = new ArrayList();
             for(int i=0;i<length;i++)
             {
               commissionProfileDeatilsVO = new CommissionProfileDeatilsVO();
               slabsList.add(commissionProfileDeatilsVO);
             }
			
		
	}
}
