package com.inter.righttel.zte;
/**
 * @(#)ZTEPoolLoaderServlet.java
 * Copyright(c) 2013, Comviva Ltd.
 *  All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 *  Author				     Date			        History
 *-------------------------------------------------------------------------------------------------
 *	Vipan					September 13, 2013		Initial Creation
 * -----------------------------------------------------------------------------------------------
 * This class is responsible to instantiate the PoolManager either at the server start-up or
 * when a corresponding URL is submitted through browser.
 */
import java.io.IOException;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;


public class ZTEINPoolLoaderServlet extends HttpServlet {
	
	private static final long serialVersionUID = 521L;
	
	static Log _logger = LogFactory.getLog(ZTEINPoolLoaderServlet.class.getName());
    private ZTEINPoolManager _poolManager=null;
	private ZTEINHeartBeatController _ZTEHeartController = null;
/**
 * Constructor of the object.
 */
    public ZTEINPoolLoaderServlet() {
        super();
    }
/**
 * Initialization of the servlet.
 * @throws ServletException if an error occur
 */
    public void init() throws ServletException {
    }
/**
 * Initialization of the servlet.
 * @throws ServletException if an error occur
 */
    public void init(ServletConfig config) throws ServletException {
        if(_logger.isDebugEnabled()) _logger.debug("init-Config"," Entered");
        super.init(config);
        initialize();
        if(_logger.isDebugEnabled()) _logger.debug("init-Config"," Exit");
   }
/**
 * The doPost method of the servlet. 
 * This method is called when a form has its tag value method equals to post.
 * @param request the request send by the client to the server
 * @param response the response send by the server to the client
 * @throws ServletException if an error occurred
 * @throws IOException if an error occurred
 */
    public void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        doGet(request,response);
    }
/**
 * The doGet method of the servlet. 
 * This method is called when a form has its tag value method equals to get.
 * This method takes the value of action and interface id,submitted as part of request.
 * Based on the action value,it does the following
 * 1.If action is NULL return to the method.
 * 2.If value of action is RELOAD, this method checks the value of interface id and make a call to destroy and
 * 	initialize method and pass the interface id as method argument.
 * @param request the request send by the client to the server
 * @param response the response send by the server to the client
 * @throws ServletException if an error occurred
 * @throws IOException if an error occurred
 */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if(_logger.isDebugEnabled()) _logger.debug("doGet"," Entered");
		String action=request.getParameter("action");
		String interfaceID=request.getParameter("INTERFACE_ID");
		if(_logger.isDebugEnabled()) _logger.debug("doGet ","action ="+action+" interfaceID ="+interfaceID);
		if(action==null)
			return;
		if(action.equals("RELOAD"))
		{
			if(!BTSLUtil.isNullString(interfaceID))
			{
				//Destroys the pooled objects for the given interface id or ids(if present would comma seprated.) 
				destroy(interfaceID.trim());
				initialize(interfaceID.trim());	
			}
			else
			{
				//Destroys the pooled objects for All the interface id or ids defined in the constant props.
				destroy();
				initialize();
			}
		}//end if
		if(_logger.isDebugEnabled()) _logger.debug("doGet"," Exited");
    }
    public void destroy(String p_interfaceID)
    {
        super.destroy(); 
 		if(_logger.isDebugEnabled()) _logger.debug("destroy Entered ","with p_interfaceID="+p_interfaceID);
 		_poolManager=new ZTEINPoolManager();
 		_poolManager.destroy(p_interfaceID);
		_ZTEHeartController.stopHeartBeat(p_interfaceID);
 		if(_logger.isDebugEnabled()) _logger.debug("destroy"," Exited");
    }
/**
 * Destruction of the servlet.
 */
    public void destroy()
    {
        super.destroy(); // Just puts "destroy" string in log
        //Put your code here
		if(_logger.isDebugEnabled()) _logger.debug("destroy ","Entered");
		String poolIDs =  Constants.getProperty("ZTE_POOL_IN_IDS");
		
		if(_logger.isDebugEnabled()) _logger.debug("destroy ","poolIDs = "+poolIDs);
		if(!BTSLUtil.isNullString(poolIDs))
		{	_poolManager=new ZTEINPoolManager();
		    _poolManager.destroy(poolIDs.trim(),"ALL");
			_ZTEHeartController.stopHeartBeat(poolIDs);
		}
		if(_logger.isDebugEnabled()) _logger.debug("destroy ","Exited");
    }
/**
 *This method is used to instantiate the PoolManager for the interface ids defined in the constant props
 *@return void
 */
    public void initialize()
    {
        if(_logger.isDebugEnabled()) _logger.debug("initialize ","Entered");
        String poolINIDs =  null;
        try
        {
            //_poolManager = new ZTEPoolManager();
        	poolINIDs =  Constants.getProperty("ZTE_POOL_IN_IDS");
	        
	        if(_logger.isDebugEnabled()) _logger.debug("initialize ","poolINIDs = "+poolINIDs);
	        if(!BTSLUtil.isNullString(poolINIDs))
			{
	        	ZTEINPoolManager.initialize(poolINIDs);
				_ZTEHeartController = new ZTEINHeartBeatController(poolINIDs);
			}
        }
        catch(Exception e)
        {
            e.printStackTrace();
            _logger.error("initialize ","Exception e::"+e.getMessage());
      		EventHandler.handle(EventIDI.SYSTEM_ERROR,poolINIDs,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PoolLoader[initialize]","","","","Exception while initialize IN PoolManager Exception:"+e.getMessage());
        }//end of catch-Exception
        finally
        {
            if(_logger.isDebugEnabled()) _logger.debug("initialize ","Exited");
        }//end finally
    }//end of initialize
/**
 * This method is used to instantiate the PoolManager for the interface ids, supplied as argument
 * @param	String p_interfaceId
 */
    public void initialize(String p_interfaceId)
    {
        if(_logger.isDebugEnabled()) _logger.debug("initialize Entered "," ,p_interfaceId::"+p_interfaceId);
        try
        {
            //_poolManager = new ZTEPoolManager();
            if(!BTSLUtil.isNullString(p_interfaceId))
			{
            	ZTEINPoolManager.initialize(p_interfaceId);
				_ZTEHeartController = new ZTEINHeartBeatController(p_interfaceId);
			} 
        }
        catch(Exception e)
        {
            e.printStackTrace();
            _logger.error("initialize "," Exception e::"+e.getMessage());
    		EventHandler.handle(EventIDI.SYSTEM_ERROR,p_interfaceId,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PoolLoader[initialize]","","","","Exception while initialize IN PoolManager Exception:"+e.getMessage());
          }//end of catch-Exception
        finally
        {
             _logger.debug("initialize ","Exited");
        }//end of finally
    }//end of initialize
}
