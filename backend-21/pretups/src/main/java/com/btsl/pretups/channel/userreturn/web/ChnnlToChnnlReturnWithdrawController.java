package com.btsl.pretups.channel.userreturn.web;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.validator.ValidatorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonController;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.userreturn.service.ChnnlToChnnlReturnWithdrawService;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.SqlParameterEncoder;

/**Akanksha
 * This class provides method for withdraw and return functionality for channel users
 */
@Controller
public class ChnnlToChnnlReturnWithdrawController extends CommonController {

	@Autowired
	private ChnnlToChnnlReturnWithdrawService chnnlReturnWithdrawService;
	private static final String MODEL_KEY = "c2cWithdrawReturn";
	private static final String CATEGORY_KEY = "categoryList";
	private static final String MSG_WHEN_NO_CAT_SEL4_USER_SEARCH="Select Category Name First";
	private static final String PANEL_NAME="formNumber";
	private static final String FIRST_PAGE="userreturn/chnnlTochnnlWithdrawReturnSearchUser";
	
	
	
	/**
	 * Load user's name,category domain for withdraw initiation
	 *
	 * @param request  The HttpServletRequest object
	 * @param response The HttpServletResponse object
	 * @param model The Model object
	 * @return String the path of view also store user type and module in model object
	 * @throws BTSLBaseException 
	 * @throws IOException 
	 * @throws ServletException 
	 * @throws Exception
	 */
	@RequestMapping(value = "/channelToChannelWithdrawSearch/withdraw.form", method = RequestMethod.GET)
	public String userSearch(final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException, ServletException, IOException
			 {

		if (log.isDebugEnabled()) {
			log.debug("ChnnlToChnnlReturnWithdrawController#userSearch", PretupsI.ENTERED);
		}
		
		request.getSession().removeAttribute(MODEL_KEY);
		request.getSession().removeAttribute(CATEGORY_KEY);
		request.getSession().removeAttribute(PANEL_NAME);
		
		authorise(request, response, "C2CWDR001A", false);
		ChannelUserVO channelUserVO =(ChannelUserVO) this.getUserFormSession(request);
		if (PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(channelUserVO.getInSuspend())) {
            if (log.isDebugEnabled()) {
                log.debug("userSearch", "USER IS IN SUSPENDED IN THE SYSTEM");
            }
            model.addAttribute("breadcrumb", PretupsRestUtil.getMessageString("pretups.userreturn.channeltochannelwithdrawsearchuser.breadcrumb"));
            model.addAttribute("fail", PretupsRestUtil.getMessageString("pretups.message.channeltransfer.withdraw.errormsg.userinsuspend"));
            return "common/commonViewForInOutSuspendedUser";
        }
		channelUserVO.setReturnFlag(false);
		model.addAttribute(MODEL_KEY, channelUserVO);
		model.addAttribute(PANEL_NAME, "Panel-One");
		final List<ListValueVO> catgList=chnnlReturnWithdrawService.loadCategory(channelUserVO);
		model.addAttribute(CATEGORY_KEY, catgList);
		if (log.isDebugEnabled()) {
			log.debug("ChnnlToChnnlReturnWithdrawController#userSearch", PretupsI.EXITED);
		}
		channelUserVO.setUserCode("");
		request.getSession().setAttribute("categoryDetailOnBack", catgList);
		return FIRST_PAGE;
	}
	
	
	/**This method is used to view the first page when back button is clicked from the second page(Product details)
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/channelToChannelWithdrawSearch/backFromSecondPage.form", method = RequestMethod.GET)
	public String backFromSecondPage(final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException, ServletException, IOException
			 {
		if (log.isDebugEnabled()) {
			log.debug("ChnnlToChnnlReturnWithdrawController#backFromSecondPage", PretupsI.ENTERED);
		}
		request.getSession().setAttribute("c2cWithdrawReturn", request.getSession().getAttribute(MODEL_KEY));
		request.getSession().setAttribute(CATEGORY_KEY, request.getSession().getAttribute("categoryDetailOnBack"));
		model.addAttribute(PANEL_NAME, request.getSession().getAttribute(PANEL_NAME));
		if (log.isDebugEnabled()) {
			log.debug("ChnnlToChnnlReturnWithdrawController#backFromSecondPage", PretupsI.EXITED);
		}
				return FIRST_PAGE;
			 }
	
	
	/** The searchChannelUsers will populate users from whom the withdraw should be done of C2C withdraw module 
	 * @param request
	 * @param model
	 * @return
	 * @throws BTSLBaseException
	 * @throws IOException
	 */
	@RequestMapping(value = "/channelToChannelWithdrawSearch/search-channel-users.form", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> searchChannelUsers( HttpServletRequest request,final Model model) throws BTSLBaseException, IOException {

		if (log.isDebugEnabled()) {
			log.debug("ChnnlToChnnlReturnWithdrawController#searchChannelUsers", PretupsI.ENTERED);
		}
		List<Map<String, String>> list = new ArrayList<>();
		ChannelUserVO channelUserVO =(ChannelUserVO) this.getUserFormSession(request);
		 String toCategoryCode=SqlParameterEncoder.encodeParams(request.getParameter("toCategoryCode"));
		 String userName=SqlParameterEncoder.encodeParams(request.getParameter("query")+"%");
		 if(BTSLUtil.isNullString(toCategoryCode)){
			 Map<String, String> map = new HashMap<>();
				map.put("loginId", MSG_WHEN_NO_CAT_SEL4_USER_SEARCH);
				map.put("userId", "");
				list.add(map);
				return list;
		 }
		List<ListValueVO> userList=chnnlReturnWithdrawService.loadUserList(channelUserVO, toCategoryCode, userName);
		if (log.isDebugEnabled()) {
			log.debug("ChnnlToChnnlReturnWithdrawController#searchChannelUsers", PretupsI.EXITED);
		}
		Iterator<ListValueVO> itr = userList.iterator();
		
		while (itr.hasNext()) {
			ListValueVO object = itr.next();
			Map<String, String> map = new HashMap<>();
			String loginId = object.getLabel() + "(" + object.getValue() + ")";
			map.put("loginId", loginId);
			map.put("userId", object.getValue());
			list.add(map);
		}
		if(list.isEmpty()){
			Map<String, String> map = new HashMap<>();
			map.put("loginId", "No Data Found");
			map.put("userId", "");
			list.add(map);
		}
		return list;
	}
	
	
	
	/**The loadProductDetails method is used to load the users associated products details for which the withdraw will be done
	 * @param chnnlToChnnlReturnWithdrawModel
	 * @param bindingResult
	 * @param model
	 * @param request
	 * @return
	 * @throws BTSLBaseException
	 * @throws IOException
	 * @throws ValidatorException
	 * @throws SAXException
	 */
	@RequestMapping(value = "/channelToChannelWithdrawSearch/submit-channel-users.form", method = RequestMethod.POST)
	public String  loadProductDetails(@ModelAttribute("c2cWithdrawReturn")  ChnnlToChnnlReturnWithdrawModel chnnlToChnnlReturnWithdrawModel,BindingResult bindingResult,final Model model,
			HttpServletRequest request) throws BTSLBaseException, IOException, ValidatorException, SAXException {

		if (log.isDebugEnabled()) {
			log.debug("ChnnlToChnnlReturnWithdrawController#loadProductDetails", PretupsI.ENTERED);
		}
		 request.getSession().removeAttribute(MODEL_KEY);
		 final ChannelUserVO sessionUser = (ChannelUserVO) getUserFormSession(request);
		 final Locale locale = BTSLUtil.getBTSLLocale(request);
		 if(MSG_WHEN_NO_CAT_SEL4_USER_SEARCH.equalsIgnoreCase(chnnlToChnnlReturnWithdrawModel.getToUserName())){
			 chnnlToChnnlReturnWithdrawModel.setToUserName("");
	    	}
		 if (chnnlReturnWithdrawService.showProductDetails(sessionUser, chnnlToChnnlReturnWithdrawModel, bindingResult,locale,model,request)) {
				model.addAttribute(MODEL_KEY, chnnlToChnnlReturnWithdrawModel);
				request.getSession().setAttribute(MODEL_KEY, chnnlToChnnlReturnWithdrawModel);
			} else {
				chnnlToChnnlReturnWithdrawModel.setReturnFlag(false);
   			    final List<ListValueVO> catgList=chnnlReturnWithdrawService.loadCategory(sessionUser);
   			    if(!BTSLUtil.isNullString(chnnlToChnnlReturnWithdrawModel.getToCategoryCode())){
   			    	chnnlToChnnlReturnWithdrawModel.setFromCategoryCode(chnnlToChnnlReturnWithdrawModel.getToCategoryCode());
   			    }
   			    if(!BTSLUtil.isNullString(chnnlToChnnlReturnWithdrawModel.getToUserName())){
   			    	chnnlToChnnlReturnWithdrawModel.setNameAndId(chnnlToChnnlReturnWithdrawModel.getToUserName());
   			    }
   				request.getSession().setAttribute(MODEL_KEY, chnnlToChnnlReturnWithdrawModel);
   				request.getSession().setAttribute(CATEGORY_KEY, catgList);
				return FIRST_PAGE;
				}
		 if (log.isDebugEnabled()) {
				log.debug("ChnnlToChnnlReturnWithdrawController#loadProductDetails", PretupsI.EXITED);
			}
		return "userreturn/chnnlToChnnlWithdrawViewProduct";
		
	}
	
	
	/**The confirmProductDetails will describe products details along with commission , tax calculations
	 * @param chnnlToChnnlReturnWithdrawModel
	 * @param bindingResult
	 * @param model
	 * @param request
	 * @return
	 * @throws BTSLBaseException
	 * @throws IOException
	 */
	@RequestMapping(value = "/channelToChannelWithdrawSearch/confirm-product-details.form", method = RequestMethod.POST)
	public String  confirmProductDetails(@ModelAttribute("c2cWithdrawReturn")  ChnnlToChnnlReturnWithdrawModel chnnlToChnnlReturnWithdrawModel,BindingResult bindingResult,final Model model,
			HttpServletRequest request) throws BTSLBaseException, IOException {

		if (log.isDebugEnabled()) {
			log.debug("ChnnlToChnnlReturnWithdrawController#confirmProductDetails", PretupsI.ENTERED);
		}
		 final ChannelUserVO sessionUser = (ChannelUserVO) getUserFormSession(request);
		 final Locale locale = BTSLUtil.getBTSLLocale(request);
		 ChnnlToChnnlReturnWithdrawModel sessionModel=( ChnnlToChnnlReturnWithdrawModel)request.getSession().getAttribute(MODEL_KEY);
		 List<ChannelTransferItemsVO> sessionProdlist=sessionModel.getProductList();
		 List<ChannelTransferItemsVO> modelProdlist=chnnlToChnnlReturnWithdrawModel.getProductList();
		 ChannelTransferItemsVO sessionChnnlTransfrItemsVO;
		 ChannelTransferItemsVO modelChnnlTransfrItemsVO;
		 ArrayList<ChannelTransferItemsVO> finalSessionProdlist=new ArrayList<>();
		
		 for (int i = 0, k = sessionProdlist.size(); i < k; i++) {
            	 sessionChnnlTransfrItemsVO = sessionProdlist.get(i);
            	 modelChnnlTransfrItemsVO= modelProdlist.get(i);
                 if (sessionChnnlTransfrItemsVO.getProductShortCode()==(modelChnnlTransfrItemsVO.getProductShortCode())) {
                	 sessionChnnlTransfrItemsVO.setRequestedQuantity(modelChnnlTransfrItemsVO.getRequestedQuantity());
                	 finalSessionProdlist.add(sessionChnnlTransfrItemsVO);
                     }
             }
             sessionModel.setProductList(finalSessionProdlist);
             sessionModel.setSmsPin(chnnlToChnnlReturnWithdrawModel.getSmsPin());
             sessionModel.setRemarks(chnnlToChnnlReturnWithdrawModel.getRemarks());
       
		 
		 if (chnnlReturnWithdrawService.confirmWithdrawUserProducts(sessionUser, sessionModel, bindingResult,locale,model,request)) {
				model.addAttribute(MODEL_KEY, sessionModel);
				request.getSession().setAttribute(MODEL_KEY, sessionModel);
			} else {
				model.addAttribute(MODEL_KEY, sessionModel);
				return "userreturn/chnnlToChnnlWithdrawViewProduct";
				}
		 if (log.isDebugEnabled()) {
				log.debug("ChnnlToChnnlReturnWithdrawController#confirmProductDetails", PretupsI.EXITED);
			}
		return "userreturn/chnnlToChnnlWithdrawConfirmList";
	}
	
	
	
	/**The approveProductDetails is used to confirm and perform the C2C withdraw transaction
	 * @param chnnlToChnnlReturnWithdrawModel
	 * @param bindingResult
	 * @param model
	 * @param request
	 * @return
	 * @throws BTSLBaseException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws ServletException
	 */
	@RequestMapping(value = "/channelToChannelWithdrawSearch/approve-product-details.form", method = RequestMethod.POST)
	public String  approveProductDetails(@ModelAttribute("c2cWithdrawReturn")  ChnnlToChnnlReturnWithdrawModel chnnlToChnnlReturnWithdrawModel,BindingResult bindingResult,final Model model,
			HttpServletRequest request) throws BTSLBaseException, IOException, NoSuchAlgorithmException, ServletException {

		if (log.isDebugEnabled()) {
			log.debug("ChnnlToChnnlReturnWithdrawController#approveProductDetails", PretupsI.ENTERED);
		}
		if(request.getParameter("withdrawBackSecond")!=null){
			return "userreturn/chnnlToChnnlWithdrawViewProduct";
		}
		
		if(csrfcheck(request, model)){
			 return "common/csrfmessage";
		}
		
		 final ChannelUserVO sessionUser = (ChannelUserVO) getUserFormSession(request);
		 final Locale locale = BTSLUtil.getBTSLLocale(request);
		 ChnnlToChnnlReturnWithdrawModel sessionModel=( ChnnlToChnnlReturnWithdrawModel)request.getSession().getAttribute(MODEL_KEY);
		 if (chnnlReturnWithdrawService.approveWithdrawReturn(sessionUser, sessionModel, bindingResult,locale,model)) {
			 sessionUser.setReturnFlag(false);
				model.addAttribute(MODEL_KEY, sessionUser);
				request.getSession().removeAttribute(MODEL_KEY);
				final List<ListValueVO> catgList=chnnlReturnWithdrawService.loadCategory(sessionUser);
				model.addAttribute(CATEGORY_KEY, catgList);
				model.addAttribute(PANEL_NAME, request.getSession().getAttribute(PANEL_NAME));
			} else{
				final List<ListValueVO> catgList=chnnlReturnWithdrawService.loadCategory(sessionUser);
				model.addAttribute(CATEGORY_KEY, catgList);
				model.addAttribute(PANEL_NAME, request.getSession().getAttribute(PANEL_NAME));
			}
		    
			if (log.isDebugEnabled()) {
				log.debug("ChnnlToChnnlReturnWithdrawController#approveProductDetails", PretupsI.EXITED);
			}
		return FIRST_PAGE;
	}
}
