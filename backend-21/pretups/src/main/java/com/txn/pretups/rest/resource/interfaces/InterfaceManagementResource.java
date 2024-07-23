package com.txn.pretups.rest.resource.interfaces;


import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.businesslogic.interfaceManagement.InterfaceManagementBL;
import com.btsl.pretups.interfaces.businesslogic.InterfaceVO;



@Path("/interfaces")
@Component
public class InterfaceManagementResource {
	
	@POST
	@Path("/selectCategory")
	@Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML}) 
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	public Response getInterfaceCatList(InterfaceVO obj) throws Exception
	{
		InterfaceManagementBL interfaceBl=new InterfaceManagementBL();
		obj=interfaceBl.getIntCatListOnLookUp(obj);
		return Response.status(200).entity(obj).build();
        
	}
	
	
	
	@POST
	@Path("/interfaceListByCategory")
	@Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML}) 
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	public Response getInterfaceListByCategory(InterfaceVO obj) throws Exception
	{
		InterfaceManagementBL interfaceBl=new InterfaceManagementBL();
		ArrayList<InterfaceVO> interfaceList=null;
		try{
		interfaceList=interfaceBl.loadInterfaceDetails(obj.getInterfaceCategoryCode(), obj.getInterfaceCategory(), obj.getNetworkCode());
		}
		catch(BTSLBaseException e) {
			Response.status(206).entity("General Processing Exception").build();
		}
		return Response.status(200).entity(interfaceList).build();
        
	}
	
	@POST
	@Path("/addInterfaceDetails")
	@Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML}) 
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	public Response getAddInterfaceDetails(InterfaceVO p_inputVO) throws Exception
	{
		InterfaceManagementBL interfaceBl=new InterfaceManagementBL();
		InterfaceVO returnObj=null;
		try{
			returnObj=interfaceBl.addInterfaceDetails(p_inputVO);
	    }
		catch(BTSLBaseException e) {
			Response.status(206).entity("General Processing Exception").build();
		}
		 return Response.status(200).entity(returnObj).build();
		
	}

}
