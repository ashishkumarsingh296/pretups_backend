package com.restapi.superadmin;

import java.sql.Connection;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.util.BTSLUtil;
import com.restapi.superadmin.requestVO.AddAgentRequestVO;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import com.web.pretups.domain.businesslogic.DomainWebDAO;
import com.web.pretups.master.businesslogic.GeographicalDomainWebDAO;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AddAgentValidator {
	
	
 public AddAgentValidator(){
		
	}
	
  public boolean checkbusinessValidation(Connection con,AddAgentRequestVO addAgentReqVO) throws BTSLBaseException {
	  final String methodName ="validate";
		DomainWebDAO domainWebDAO = new DomainWebDAO();
		CategoryWebDAO categoryWebDAO  = new CategoryWebDAO(); 
	   if(addAgentReqVO!=null) {
		   if(BTSLUtil.isNullString(addAgentReqVO.getDomainCodeofCategory())){
			    throw new BTSLBaseException(CategoryManagementController.class.getName(), methodName,
						PretupsErrorCodesI.INVALID_DOMAIN_CODE);
		   }
		   
//		   if(domainWebDAO.isExistsChannelDomainCodeForAdd(con,addAgentReqVO.getDomainCodeofCategory())){
//			    throw new BTSLBaseException(CategoryManagementController.class.getName(), methodName,
//						PretupsErrorCodesI.INVALID_DOMAIN_CODE);
//		   }
//		   
//		   if(BTSLUtil.isNullString(addAgentReqVO.getDomainName()) ){
//			    throw new BTSLBaseException(CategoryManagementController.class.getName(), methodName,
//						PretupsErrorCodesI.DOMAIN_NAME_EMPTY);
//		   }
//		  
//		   if(domainWebDAO.isExistsChannelDomainNameForAdd(con,addAgentReqVO.getDomainName()) ){
//			    throw new BTSLBaseException(this, CategoryManagementController.class.getName(), "domain.addchannelcategory.error.domainname.alreadyexists", "addagent");
//		   }
		   
		   String userIdPrefix[] = { addAgentReqVO.getUserIDPrefix() };
           if (categoryWebDAO.isExistsUserIdPrefixForAdd(con, addAgentReqVO.getUserIDPrefix())) {
               throw new BTSLBaseException(this, "checkbusinessValidation", "domain.savecategorydetails.error.useridprefix.alreadyexists", userIdPrefix);
               
           }
           
           validateAgentDetails(con,addAgentReqVO);
           
	
	  
  }
	return false;


  }	   
  
  
  private boolean validateAgentDetails(Connection con,AddAgentRequestVO addAgentReqVO) throws BTSLBaseException {
	   CategoryDAO categoryDAO = new CategoryDAO();
	   CategoryWebDAO categoryWebDAO  = new CategoryWebDAO(); 
	   GeographicalDomainWebDAO geographicalDomainWebDAO  = new GeographicalDomainWebDAO();
	   
	   CategoryVO categoryVO = categoryDAO.loadCategoryDetailsByCategoryCode(con, addAgentReqVO.getAgentCategoryCode());
	   if (!BTSLUtil.isNullorEmpty(categoryVO)) {
		   throw new BTSLBaseException(this, CategoryManagementController.class.getName(), "domain.savecategorydetails.error.categorycode.alreadyexists", "addAgent");
	   }
	   if (categoryWebDAO.isExistsCategoryNameForAdd(con, addAgentReqVO.getAgentCategoryName())) {
			throw new BTSLBaseException(this, CategoryManagementController.class.getName(), "domain.savedomain.error.categoryname.alreadyexists", "addAgent");
	   }
	   
	 if(!geographicalDomainWebDAO.isGeographyDomainTypeValid(con,addAgentReqVO.getGeoDomainType())){
			throw new BTSLBaseException(this, CategoryManagementController.class.getName(), "master.createbatchgeographicaldomains.error.geodomaintypinvalid", "addAgent");
	 }
	   
	  return false;
  }
  
  
  
  
  
  
  
  
  
}
