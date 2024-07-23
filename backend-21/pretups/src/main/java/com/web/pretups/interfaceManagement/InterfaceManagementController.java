package com.web.pretups.interfaceManagement;

import java.util.ArrayList;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonController;
import com.btsl.common.JSONCommonConverter;
import com.btsl.common.ListValueVO;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.interfaces.businesslogic.InterfaceDAO;
import com.btsl.pretups.interfaces.businesslogic.InterfaceNodeDetailsVO;
import com.btsl.pretups.interfaces.businesslogic.InterfaceVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;





@Controller
@Lazy
public class InterfaceManagementController extends CommonController  {
	 private static final Log LOG = LogFactory.getLog(InterfaceManagementController.class.getName());
	 
	 @Autowired
	 private  InterfaceForm interfaceForm;

	 @Autowired
	 InterfaceVO interfaceVO;
	 
	
	 
	 /**
	     * This method is called for the first time when the user clicks on channel
	     * New interface management link.
	     * 
	     * 
	     * @param interfaceForm
	     * @param model
	     * @param request
	     * @return String(jsp path)
	     */
	    @RequestMapping(value = "/NewInterfaceMgmt/first.form", method = RequestMethod.GET)
	    public String loadInterfaceCategory(Map<String, Object> model,HttpServletRequest request, HttpServletResponse response) {
	        final String METHOD_NAME = "loadInterfaceCategory";
	        final String ENTRY_KEY = "Entered";
	        StringBuilder loggerValue= new StringBuilder();
	    	if (LOG.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append(ENTRY_KEY);        	
	        	LOG.debug(METHOD_NAME, loggerValue);        	
	        }
	        ArrayList<ListValueVO> interfaceCategoryList = null;

	        try {
	        	//todo 
	            authorise(request, response, "INTF00A", false);
	        	HttpSession session =request.getSession();
	        	session.setAttribute("formName", "interfaceForm");
	            interfaceCategoryList = new ArrayList<ListValueVO>();
	            
	           // interfaceCategoryList = LookupsCache.loadLookupDropDown(PretupsI.INTERFACE_CATEGORY, true);
	           
	            interfaceForm.flush();
	            //interfaceForm.setInterfaceCategoryList(interfaceCategoryList);
	            
	            interfaceVO.setWebServiceType("INTFMGMT");
	            interfaceVO.setInterfaceCategoryType(PretupsI.INTERFACE_CATEGORY);
	            
				JSONCommonConverter converter= new JSONCommonConverter();
				//String jsonString=converter.convertObjectToString(interfaceVO);
				String output= callClient("selectCategory","INTFMGMT", "LIST",interfaceVO);//removed json string
				if (LOG.isDebugEnabled()) {
		        	loggerValue.setLength(0);
		        	loggerValue.append("Outpurt string is -----");
		        	loggerValue.append(output);
		        	LOG.debug(METHOD_NAME, loggerValue);       	
		        }				
				interfaceVO = (InterfaceVO)converter.convertStringToObjectIn(output, InterfaceVO.class);
				interfaceForm.setInterfaceCategoryList(interfaceVO.getInterfaceCategoryList());
	            model.put("interfaceForm",interfaceForm);
	        }

	        catch (Exception e) {
	        	 LOG.errorTrace(METHOD_NAME, e);
	          //here in case of exception handle Todo
	          return "common/errorSpring";
	         
	        }

	        finally {
	            if (LOG.isDebugEnabled()) {
	            	LOG.debug("loadInterfaceCategory", "Exiting Size=" + interfaceCategoryList);
	            }
	        }
	        return "newinterfaces/selectcatfornewintrfc";
	    }

      @RequestMapping(value ="/NewInterfaceMgmt/loadInterfaceDetails.form", method = RequestMethod.POST )
	    public String loadInterfaceDetails( @ModelAttribute("interfaceForm") InterfaceForm interfaceForm, BindingResult result,  Map<String, Object> model, HttpServletRequest request) {
    	  final String methodName = "loadInterfaceDetails";
    	  final String ENTRY_KEY = "Entered";
	        StringBuilder loggerValue= new StringBuilder();
	    	if (LOG.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append(ENTRY_KEY);        	
	        	LOG.debug(methodName, loggerValue);        	
	        }
	        ArrayList<InterfaceVO> interfaceDetailList = null;
	        interfaceVO.setWebServiceType("INTFMGMT");

	        try {
	            UserVO userVO = this.getUserFormSession(request);
	            
	            interfaceDetailList = new ArrayList<InterfaceVO>();
	            InterfaceForm theForm = (InterfaceForm) interfaceForm;
	            theForm.setCategoryCode(userVO.getCategoryCode());
	           // InterfaceDAO interfaceDAO = new InterfaceDAO();
	            if (log.isDebugEnabled()) {
	                log.debug(methodName, "Category Type " + theForm.getInterfaceCatCode());
	            }
	           
	            interfaceVO.setInterfaceCategoryCode(theForm.getInterfaceCatCode());
	            interfaceVO.setInterfaceCategory(theForm.getCategoryCode());
	            interfaceVO.setNetworkCode(userVO.getNetworkID());
	            
	            JSONCommonConverter converter= new JSONCommonConverter();
				//String jsonString=converter.convertObjectToString(interfaceVO);
				String output= callClient("interfaceListByCategory","INTFMGMT", "LIST",interfaceVO);
				if (LOG.isDebugEnabled()) {
		        	loggerValue.setLength(0);
		        	loggerValue.append("Outpurt string is -----");
		        	loggerValue.append(output);
		        	LOG.debug(methodName, loggerValue);       	
		        }
				 interfaceDetailList  = (ArrayList)converter.convertStringToObject(output, InterfaceVO.class);	            
	            //interfaceDetailList=interfaceManagementBL.loadInterfaceDetails(t;heForm.getInterfaceCatCode(), theForm.getCategoryCode(), userVO.getNetworkID());
	            theForm.setInterfaceDetailsList(interfaceDetailList);
	            theForm.setInterfaceStatusList(LookupsCache.loadLookupDropDown(PretupsI.STATUS_TYPE, true));
	            theForm.setInterfaceCatCode(theForm.getInterfaceCatCode());
	            theForm.setCategoryCode(theForm.getCategoryCode());

	            if (interfaceDetailList != null && interfaceDetailList.size() > 0) {
	                InterfaceVO interfaceData = (InterfaceVO) interfaceDetailList.get(0);
	                theForm.setInterfaceCategory(interfaceData.getInterfaceCategory());
	                theForm.setInterfaceName(interfaceData.getInterfaceName());
	              //  theForm.setModifyFlag("true");
	            } else {
	                // In case No data found in the database
	                ListValueVO listVO = new ListValueVO();
	                listVO = BTSLUtil.getOptionDesc(theForm.getInterfaceCatCode(), theForm.getInterfaceCategoryList());
	                theForm.setInterfaceCategory(listVO.getLabel());
	               // theForm.setModifyFlag("false");
	                
	            }
	            model.put("interfaceForm",interfaceForm);

	        } catch (BTSLBaseException be) {
	            log.error("loadInterfaceDetails", "Exception: " + be.getMessage());
	            log.errorTrace(methodName, be);
	          return super.handleError(this, "loadInterfaceDetails", be, result);
	        }
	          catch (Exception e) {
	            log.error("loadInterfaceDetails", "Exception: " + e.getMessage());
	            log.errorTrace(methodName, e);
	          return super.handleError(this, "loadInterfaceDetails", e, result);
	        } 
	        return "newinterfaces/interfaceDetails";

	    }
     
      
      @RequestMapping(value ="/NewInterfaceMgmt/interfaceDetails.form", method = RequestMethod.POST ,params="Add")
	    public String addInterfaceDetails(@ModelAttribute("interfaceForm") InterfaceForm interfaceForm, BindingResult result,  Map<String, Object> model, HttpServletRequest request) {
    	  final String METHOD_NAME = "addInterfaceDetails";
	        final String ENTRY_KEY = "Entered";
	        StringBuilder loggerValue= new StringBuilder();
	    	if (LOG.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append(ENTRY_KEY);        	
	        	LOG.debug(METHOD_NAME, loggerValue);        	
	        }

	        try {
	        UserVO userVO = this.getUserFormSession(request);
	        interfaceVO.setInterfaceCategoryCode(interfaceForm.getInterfaceCatCode());
            interfaceVO.setInterfaceCategory(interfaceForm.getCategoryCode());
            JSONCommonConverter converter= new JSONCommonConverter();
			String output= callClient("addInterfaceDetails","INTFMGMT", "LIST",interfaceVO);
			interfaceVO=(InterfaceVO)converter.convertStringToObjectIn(output, InterfaceVO.class);
			interfaceForm.setSingleStateTransaction(interfaceVO.getSingleStateTransaction());
			interfaceForm.setLocationIndex(-1);
			interfaceForm.setInterfaceTypeIdList(interfaceVO.getInterfaceCategoryList());
			interfaceForm.setUriReq(interfaceVO.getUriReq());
			interfaceForm.setInterfaceStatusList(LookupsCache.loadLookupDropDown(PretupsI.STATUS_TYPE, true));
			if (LOG.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Outpurt string is -----");
	        	loggerValue.append(output);
	        	LOG.debug(METHOD_NAME, loggerValue);       	
	        }
	        } catch (BTSLBaseException be) {
	            log.error("addInterfaceDetails", "Exception: " + be.getMessage());
	            log.errorTrace(METHOD_NAME, be);
	          return super.handleError(this, "addInterfaceDetails", be, result);
	        }
	          catch (Exception e) {
	            log.error("addInterfaceDetails", "Exception: " + e.getMessage());
	            log.errorTrace(METHOD_NAME, e);
	          return super.handleError(this, "addInterfaceDetails", e, result);
	        }
	        model.put("interfaceForm",interfaceForm);
          
    	  return "newinterfaces/addInterfaceDetails";
      }
      
      @RequestMapping(value ="/NewInterfaceMgmt/associateNodes.form", method = RequestMethod.POST ,params="NoOfNodes")
      public String associateNodes(@RequestParam String NoOfNodes, @ModelAttribute("interfaceForm") InterfaceForm interfaceForm, BindingResult result,  Map<String, Object> model, HttpServletRequest request) {
    	 
    	  final String METHOD_NAME = "associateNodes";
          if (log.isDebugEnabled()) {
              log.debug("associateNodes", "Entered ");
          }

          ArrayList<InterfaceNodeDetailsVO> slabList = null;
          try {
              
        	int nodesSize = 0;
  			String noOfNodes=request.getParameter("noOfNodes");
  			InterfaceDAO interfaceDAO=new InterfaceDAO();
  			interfaceForm.setNodeStatusList(LookupsCache.loadLookupDropDown(PretupsI.STATUS_TYPE,true));
  			if(interfaceForm.getSlabsList()==null || interfaceForm.getSlabsList().isEmpty()){
  				slabList= interfaceDAO.loadNodeDetails(null, interfaceForm.getInterfaceId());
  				int slabListSize = slabList.size();
			    if(slabList!=null && slabList.size()>0)
				{
				String[] arr = new String[slabList.size()];
				  for(int i=0;i< slabListSize;i++)
				  {
					  InterfaceNodeDetailsVO nodeDetail=slabList.get(i);
					  arr[i]=nodeDetail.getNodeName();
				  }
				  interfaceForm.setSelectNodeFlag(arr);
				  interfaceForm.setSlabsList(slabList);
				}
		    }
  			    interfaceForm.setMaxNodes(Integer.parseInt(noOfNodes));
				int maxInterFaceNodes=interfaceForm.getMaxNodes();
				populateSelectedNodeDetails(interfaceForm, interfaceForm.getSlabsList(),true);
			    nodesSize=interfaceForm.getSlabsList().size();
			    interfaceForm.setDisplayDeleteNodes("false");
			    interfaceForm.setDelCount(0);
			    if(nodesSize>maxInterFaceNodes)
			    {
			    	interfaceForm.setDisplayDeleteNodes("true");
			    	interfaceForm.setDelCount(nodesSize-maxInterFaceNodes);
			    	
			    }
			    
			    model.put("interfaceForm",interfaceForm);
  			
  			
          }
          
          catch (BTSLBaseException be) {
	            log.error("associateNodes", "Exception: " + be.getMessage());
	            log.errorTrace(METHOD_NAME, be);
	          return super.handleError(this, "associateNodes", be, result);
	        }
	          catch (Exception e) {
	            log.error("addInterfaceDetails", "Exception: " + e.getMessage());
	            log.errorTrace(METHOD_NAME, e);
	          return super.handleError(this, "associateNodes", e, result);
	        }
    	  
    	  return null;
      }
      
      
      
      private void populateSelectedNodeDetails( InterfaceForm theForm , ArrayList<InterfaceNodeDetailsVO> slabList, boolean isAssociateNode)
  	{
  		final String METHOD_NAME = "populateSelectedNodeDetails";
  		if (log.isDebugEnabled())
  			log.debug(METHOD_NAME, "Entered ");
  		InterfaceNodeDetailsVO interfaceNodeDetailsVO=null;
  		String[] updatedSelectedNodes= null;
  		try{
  		String [] selectedNodes=theForm.getSelectNodeFlag();
  		int length=theForm.getMaxNodes();
  		
  		ArrayList<InterfaceNodeDetailsVO> selectedSlabList=new ArrayList<InterfaceNodeDetailsVO>();
  		
  		//if(request.getParameter("deleteNodes")!=null 
  		int slabListSize = slabList.size();
  		if(slabList!=null && slabList.size()>0)
  		{
  			for(int i=0;i< slabListSize;i++)
  				{
  					 interfaceNodeDetailsVO=slabList.get(i);					
  					 if (BTSLUtil.isNullString(interfaceNodeDetailsVO.getNodeStatus())) {
  						 interfaceNodeDetailsVO.setNodeStatus("Y");
  						}
  					 
  					 if(selectedNodes!=null && selectedNodes.length>0)
  					 {
  						 for(int k=0;k<selectedNodes.length;k++)
  						 {
  							 if(interfaceNodeDetailsVO.getNodeName().equals(selectedNodes[k]) && !BTSLUtil.isNullString(interfaceNodeDetailsVO.getIp()) 
  									 && !BTSLUtil.isNullString(interfaceNodeDetailsVO.getPort()))
  							 {
  								 selectedSlabList.add(interfaceNodeDetailsVO);
  							 }
  						 }
  					 }					
  			}
  		}
  		
  		if(isAssociateNode)
  		{
  				if(slabList!=null && slabList.size()<length && selectedSlabList.size()==slabList.size())
  				{
  					for(int i=slabListSize; i<length;i++)
  					{
  						interfaceNodeDetailsVO = new InterfaceNodeDetailsVO();
  						interfaceNodeDetailsVO.setNodeName("UNIQUE_INTERFACE_NAME_"+i);
  		            	selectedSlabList.add(interfaceNodeDetailsVO);
  					}
  				}
  				else if(slabList==null || slabList.isEmpty()) {
  					for(int k=0; k < length;k++)
  					{
  						interfaceNodeDetailsVO = new InterfaceNodeDetailsVO();
  						interfaceNodeDetailsVO.setNodeName("UNIQUE_INTERFACE_NAME_"+k);
  		            	selectedSlabList.add(interfaceNodeDetailsVO);
  					}
  				}
  				else if(selectedSlabList.size()<length) {
  					int selectedSlabListSize = selectedSlabList.size();
  					for(int k= selectedSlabListSize; k < length;k++)
  					{
  						interfaceNodeDetailsVO = new InterfaceNodeDetailsVO();
  						interfaceNodeDetailsVO.setNodeName("UNIQUE_INTERFACE_NAME_"+k);
  		            	selectedSlabList.add(interfaceNodeDetailsVO);
  					}
  					
  				}
  		}
  		theForm.setSlabsList(selectedSlabList);
  		if(selectedSlabList!=null && selectedSlabList.size()>0){
  			updatedSelectedNodes= new String[selectedSlabList.size()];
  				for(int i=0;i<selectedSlabList.size();i++)
  				{
  					 interfaceNodeDetailsVO=selectedSlabList.get(i);
  					 updatedSelectedNodes[i]=interfaceNodeDetailsVO.getNodeName();
  				}
  		  }
  		theForm.setSelectNodeFlag(updatedSelectedNodes);
  		}
  		catch (Exception e) 
  		{
              log.error(METHOD_NAME, "Exception: " + e.getMessage());
              log.errorTrace(METHOD_NAME, e);
          } finally {
              if (log.isDebugEnabled()) {
                  log.debug(METHOD_NAME, "Exiting");
              }
          }

      }
}
