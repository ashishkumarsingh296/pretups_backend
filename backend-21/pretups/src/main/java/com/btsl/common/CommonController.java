package com.btsl.common;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.rest.client.CommonControllerClient;
import com.btsl.security.csrf.CSRFTokenUtil;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.KeyArgumentVO;

/**
 * 
 * @author ayush.abhijeet
 *
 */
public class CommonController {
	@Autowired
	private ServletContext servletContext;
    public static final Log log = LogFactory.getLog(CommonController.class.getName());

    protected UserVO getUserFormSession(HttpServletRequest request) throws BTSLBaseException {
        UserVO userVO = null;
        // HttpSession session = request.getSession(true);
        HttpSession session = request.getSession(false);
        Object obj = session.getAttribute("user");

        if (obj != null) {
            userVO = (UserVO) obj;
        }
        return userVO;
    }

		protected void handleError(Object classObj,String method,
		        Exception e, Connection p_con ) {
			final String METHOD_NAME = "handleError";
			try
			{
				if (p_con != null) {
					p_con.rollback();
				}
			}
			catch (SQLException e1) {
				log.errorTrace(METHOD_NAME, e1);
			}
			return;
				
		}
		
		protected String  handleError(Object classObj,String method,
		        Exception e,BindingResult result) {
		        
		        String errKey = null;
		        final String METHOD_NAME = "handleError";
		        String forward="common/displayMessagesSpring";
		        if (e instanceof BTSLBaseException) {
		            BTSLBaseException ae = (BTSLBaseException) e;
		            String[] args = null;
		            
			        if (ae.getForwardPath() != null) {
		                forward = ae.getForwardPath();
		            }

		                
		            if ((ae.getMessageMap() != null) && (ae.getMessageMap().size() > 0)) 
					{
		                Map<String,String[]> mp = ae.getMessageMap();
		                Iterator<Entry<String, String[]>> it = mp.entrySet().iterator();
		                

		                while (it.hasNext()) {
		                    Map.Entry<String,String[]> pairs = (Map.Entry<String,String[]>) it.next();
		                 
		                    errKey = (String) pairs.getKey();
		                    args = (String[]) pairs.getValue();

		                    if ((args == null) || (args.length == 0)) {
		                     result.reject(errKey);
		                    } else {
		                     result.reject(errKey, args, "processing error occurred");
		                    	
		                    }
		                }
		            }
		            else
					if(ae.getMessageList() != null && !ae.getMessageList().isEmpty())
					{
						
						KeyArgumentVO keyArgumentVO = null;
						
						for(int i=0 , k = ae.getMessageList().size(); i < k ; i++ )
						{
							 keyArgumentVO = (KeyArgumentVO) ae.getMessageList().get(i);
							 
			                    if ((keyArgumentVO.getArguments() == null) || (keyArgumentVO.getArguments().length == 0)) {
			                    	 result.reject(errKey);
			                    } else {
			                    	 result.reject(errKey, args, "processing error occurred");
			                    }
						}
					}
					else 
					{
						result= handleError(ae,e,result);
					}
		            log.debug("handleError","Exiting");
		        }else{
				log.errorTrace(METHOD_NAME,e);
				 if ((e.getMessage() != null) && !("".equals(e.getMessage()))) {
			            errKey = e.getMessage();
			        } else {
			            errKey = "error.general.processing";
			        }

				    result.reject(errKey);
				    
		        }  
			
		        //this code is execute when an Exception occured
		        EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,classObj.getClass().getName()+"["+method+"]","","","","Exception:"+e.getMessage());
		        log.debug("handleError","Exiting");

		        return forward;
		    }

		
		public BindingResult handleError(BTSLBaseException ae, Exception e, BindingResult result)
		{
			
			String errKey = null;

	        if (ae.getErrorCode() != 0) {
	            int r = ae.getErrorCode();
	            errKey = "error.errorCode." + r + "";
	        } else if ((e.getMessage() != null) &&
	                !("".equals(e.getMessage()))) {
	            errKey = e.getMessage();
	        } else {
	            errKey = "error.general.processing";
	        }

	        if ((ae.getArgs() == null) || (ae.getArgs().length == 0)) {
	        	result.reject(errKey);
	        } else {
	        	result.reject(errKey, ae.getArgs(), "processing error occurred" );
	        }
	        return result;
	    
		}
		
		public String callClient(String webServiceMethod,String p_webServiceType, String methodType,Object obj)
		{
			String resultString="";
			 CommonControllerClient client= new CommonControllerClient();
			 resultString=client.processRequest(methodType,p_webServiceType ,webServiceMethod, obj);		
			return resultString;
		}
		
		protected void authorise(HttpServletRequest request, HttpServletResponse response,String pageCode,boolean clearSession) throws ServletException,IOException,BTSLBaseException
		{
			if (log.isDebugEnabled()) {
				 StringBuilder sb = new StringBuilder();
		    	sb.append("Entered:pageCode=");
		    	sb.append(pageCode);
		    	sb.append(",clearSession=");
		    	sb.append(clearSession);
		    	
				log.debug("authorise",sb.toString());
			}
			RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher("/jsp/common/securityManager.jsp?pageCode="+pageCode+"&fromAction=true&clearSession="+(clearSession?"Y":"N"));
			if (request instanceof MultipartHttpServletRequest) 
			{
			    if (log.isDebugEnabled()) {
					log.debug("authorise", "Entered:typcast the Request into MultipartHttpServletRequest");
				}
	        }
			requestDispatcher.include(request,response);
			if (log.isDebugEnabled()) {
				log.debug("authorise", "Entered:response.isCommitted()="+response.isCommitted());
			}
			BTSLBaseException be=(BTSLBaseException)request.getAttribute("be");
			if(be!=null)
			{
				if (log.isDebugEnabled()) {
					log.debug("authorise", "throwing BTSLBaseException be: "+be);
				}
				throw be;
			}
		}
		protected boolean csrfcheck(HttpServletRequest request,final Model model) throws NoSuchAlgorithmException, ServletException{
		boolean flag = CSRFTokenUtil.isValid(request);
        if (!flag) {
            if (log.isDebugEnabled()) {
                log.debug("CSRF", "ATTACK!");
            }
            model.addAttribute("csrfattack", PretupsRestUtil.getMessageString("security.csrf.attack.message"));
            return true;
        }
        return false;
      }
}
