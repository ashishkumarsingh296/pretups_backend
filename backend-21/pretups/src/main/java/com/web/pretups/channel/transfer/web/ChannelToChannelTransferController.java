package com.web.pretups.channel.transfer.web;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.btsl.pretups.channel.transfer.businesslogic.AutoCompleteUserDetailsResponseVO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonController;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.SqlParameterEncoder;
import com.web.pretups.channel.transfer.service.C2CTransferService;

/**
 * 
 * @author yogesh.keshari
 *
 */
@Controller
public class ChannelToChannelTransferController extends CommonController {
	private static final String PANEL_NO = "PanelNo";
	private static final String CATEGORY_KEY = "categoryList";
	private static final String CATEGORY_KEY_SESSION = "categoryListSession";
	private static final String MODEL_KEY = "c2cTransfer";
	private static final String MSG_WHEN_NO_CAT_SEL4_USER_SEARCH = "Select Category First";
	private static final String NO_DATA_FOUND = "No Data Found";

	@Autowired
	private C2CTransferService c2cTransferService;

	/**
	 * 
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/pretups/ChannelToChannelSearchAction.form", method = RequestMethod.GET)
	public String loadC2CTransferForm(final Model model, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("ChannelToChannelTransferController#loadC2CTransferForm", PretupsI.ENTERED);
		}
		request.getSession().removeAttribute(MODEL_KEY);
		request.getSession().removeAttribute(CATEGORY_KEY);
		request.getSession().removeAttribute(PANEL_NO);
		request.getSession().removeAttribute(CATEGORY_KEY_SESSION);

		authorise(request, response, "C2CTRF001A", false);
		final ChannelUserVO channelUserVO = (ChannelUserVO) this.getUserFormSession(request);
		C2CTransferModel c2cTransferModel = new C2CTransferModel();

		c2cTransferService.loadloggedinUserdetails(c2cTransferModel, model, channelUserVO);

		model.addAttribute(PANEL_NO, "Panel-One");

		if (PretupsI.USER_TRANSFER_OUT_STATUS_SUSPEND.equals(channelUserVO.getOutSuspened())) {
			model.addAttribute("breadcrumb", PretupsRestUtil.getMessageString("pretups.channeltransfer.chnltochnlsearchuser")); 
			model.addAttribute("fail",PretupsRestUtil.getMessageString("pretups.channeltransfer.chnltochnlsearchuser.usernotfound.msg.transferoutsuspend"));
			return "common/commonViewForInOutSuspendedUser";

		}

		List<ListValueVO> categoryList = c2cTransferService.loadCategoryList(channelUserVO);
		// model.addAttribute(MODEL_KEY, channelUserVO);
		model.addAttribute(MODEL_KEY, c2cTransferModel);
		model.addAttribute(CATEGORY_KEY, categoryList);
		request.getSession().setAttribute(CATEGORY_KEY_SESSION, categoryList);
		
		if (log.isDebugEnabled()) {
			log.debug("ChannelToChannelTransferController#loadC2CTransferForm", PretupsI.EXITED);
		}
		return "channeltransfer/channelToChannelSearchUser";

	}

	/**
	 * 
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/pretups/ChannelToChannelSearchActionback.form", method = RequestMethod.GET)
	public String loadC2CTransferFormback(final Model model, HttpServletRequest request) throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("ChannelToChannelTransferController#loadC2CTransferFormback", PretupsI.ENTERED);
		}
		request.getSession().setAttribute(CATEGORY_KEY,request.getSession().getAttribute(CATEGORY_KEY_SESSION));
		//model.addAttribute(MODEL_KEY, request.getSession().getAttribute(MODEL_KEY));
		model.addAttribute(PANEL_NO, request.getSession().getAttribute(PANEL_NO));
		return "channeltransfer/channelToChannelSearchUser";

	}

	/**
	 * 
	 * @param c2cTransferModel
	 * @param bindingResult
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/pretups/submit-c2c-transfer.form", method = RequestMethod.POST)
	public String loadUserProduct(@ModelAttribute("c2cTransferModel") C2CTransferModel c2cTransferModel,
			BindingResult bindingResult, final Model model, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("ChannelToChannelTransferController#loadUserProduct", PretupsI.ENTERED);
		}

		final ChannelUserVO channelUserVO = (ChannelUserVO) getUserFormSession(request);

		final Locale locale = BTSLUtil.getBTSLLocale(request);
		if (c2cTransferService.loadUserProductsdetails(c2cTransferModel, channelUserVO, bindingResult, locale, model,
				request)) {
			model.addAttribute(MODEL_KEY, c2cTransferModel);
			request.getSession().setAttribute(MODEL_KEY, c2cTransferModel);

		} else {
			c2cTransferService.loadloggedinUserdetails(c2cTransferModel, model, channelUserVO);
			final List<ListValueVO> categoryList = c2cTransferService.loadCategoryList(channelUserVO);
			request.getSession().setAttribute(MODEL_KEY, c2cTransferModel);
			request.getSession().setAttribute(CATEGORY_KEY, categoryList);
			return "channeltransfer/channelToChannelSearchUser";
		}
		return "channeltransfer/channelToChannelViewProduct";
	}

	/**
	 * 
	 * @param c2cTransferModel
	 * @param bindingResult
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 * @throws ServletException
	 * @throws NoSuchAlgorithmException
	 */
	@RequestMapping(value = "/pretups/initiate-c2c-transfer.form", method = RequestMethod.POST)
	public String channelProductConfirm(@ModelAttribute("c2cTransferModel") C2CTransferModel c2cTransferModel,
			BindingResult bindingResult, final Model model, HttpServletRequest request, HttpServletResponse response)
			throws BTSLBaseException {
		if (log.isDebugEnabled()) {
			log.debug("ChannelToChannelTransferController#channelProductConfirm", PretupsI.ENTERED);
		}
		final C2CTransferModel theForm = c2cTransferModel;
		final ChannelUserVO sessionUserVO = (ChannelUserVO) getUserFormSession(request);
		C2CTransferModel theFormNew = (C2CTransferModel) request.getSession().getAttribute(MODEL_KEY);

		List<ChannelTransferItemsVO> sessionProdlist = theFormNew.getProductList();
		List<ChannelTransferItemsVO> modelProdlist = c2cTransferModel.getProductList();
		ArrayList<ChannelTransferItemsVO> finalSessionProdlist = new ArrayList<>();
		for (int i = 0, k = sessionProdlist.size(); i < k; i++) {
			ChannelTransferItemsVO sessionChnnlTransfrItemsVO = sessionProdlist.get(i);
			ChannelTransferItemsVO modelChnnlTransfrItemsVO = modelProdlist.get(i);
			if (sessionChnnlTransfrItemsVO.getProductShortCode() == (modelChnnlTransfrItemsVO.getProductShortCode())) {
				sessionChnnlTransfrItemsVO.setRequestedQuantity(modelChnnlTransfrItemsVO.getRequestedQuantity());
				finalSessionProdlist.add(sessionChnnlTransfrItemsVO);
			}
		}
		theFormNew.setProductList(finalSessionProdlist);
		theFormNew.setRefrenceNum(theForm.getRefrenceNum());
		theFormNew.setRemarks(theForm.getRemarks());
		theFormNew.setSmsPin(theForm.getSmsPin());

		if (c2cTransferService.channelproductConfirm(theFormNew, sessionUserVO, model, bindingResult, request)) {
			model.addAttribute(MODEL_KEY, theFormNew);
			request.getSession().setAttribute(MODEL_KEY, theFormNew);

		} else {
			model.addAttribute(MODEL_KEY, theFormNew);
			return "channeltransfer/channelToChannelViewProduct";
		}
		return "channeltransfer/channelToChannelConfirmProduct";
	}

	/**
	 * this method called when confirm button pressed from
	 * channelToChannelConfirmProduct.jsp page
	 * 
	 * @param c2cTransferModel
	 * @param bindingResult
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 * @throws ServletException
	 * @throws NoSuchAlgorithmException
	 */
	@RequestMapping(value = "/pretups/confirm-c2c-transfer.form", method = RequestMethod.POST)
	public String approveTransferOrder(@ModelAttribute("c2cTransferModel") C2CTransferModel c2cTransferModel,
			BindingResult bindingResult, final Model model, HttpServletRequest request, HttpServletResponse response)
			throws BTSLBaseException, NoSuchAlgorithmException, ServletException {
		if (log.isDebugEnabled()) {
			log.debug("ChannelToChannelTransferController#approveTransferOrder", PretupsI.ENTERED);
		}
		
		final ChannelUserVO sessionUser = (ChannelUserVO) getUserFormSession(request);

		C2CTransferModel theFormNew = (C2CTransferModel) request.getSession().getAttribute(MODEL_KEY);
		if (csrfcheck(request, model)) {
			return "common/csrfmessage";
		}
		if (c2cTransferService.approveTransferOrder(theFormNew, sessionUser, model, bindingResult, request)) {
			// request.getSession().setAttribute(MODEL_KEY, c2cTransferModel);
			c2cTransferService.loadloggedinUserdetails(c2cTransferModel, model, sessionUser);

		}
		model.addAttribute(PANEL_NO, request.getSession().getAttribute(PANEL_NO));
		model.addAttribute(MODEL_KEY, c2cTransferModel);
		request.getSession().removeAttribute(MODEL_KEY);
		final List<ListValueVO> categoryList = c2cTransferService.loadCategoryList(sessionUser);
		model.addAttribute(CATEGORY_KEY, categoryList);
		return "channeltransfer/channelToChannelSearchUser";
	}

	/**
	 * loadUserProductBack this method called when back button pressed from
	 * channelToChannelConfirmProduct.jsp page
	 * 
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/pretups/submit-c2c-transfer.form", method = RequestMethod.GET)
	public String loadUserProductBack(final Model model, HttpServletRequest request) throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("ChannelToChannelTransferController#loadUserProduct", PretupsI.ENTERED);
		}
		model.addAttribute(MODEL_KEY, request.getSession().getAttribute(MODEL_KEY));
		return "channeltransfer/channelToChannelViewProduct";
	}

	/**
	 * 
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/pretups/SearchUser.form", method = RequestMethod.GET)
	public @ResponseBody List<Map<String, String>> loaduserList(Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List<Map<String, String>> list = new ArrayList<>();
		String UserName = request.getParameter("query");
		UserName = SqlParameterEncoder.encodeParams(UserName + "%");
		String categorycode = SqlParameterEncoder.encodeParams(request.getParameter("categorycode"));
		final ChannelUserVO channelUserVO = (ChannelUserVO) getUserFormSession(request);
		if (BTSLUtil.isNullString(categorycode)) {
			Map<String, String> map = new HashMap<>();
			map.put("loginId", MSG_WHEN_NO_CAT_SEL4_USER_SEARCH);
			map.put("userId", "");
			list.add(map);
			return list;
		}
		List<AutoCompleteUserDetailsResponseVO> userList = c2cTransferService.loadUserList(categorycode, UserName, channelUserVO);

		Iterator<AutoCompleteUserDetailsResponseVO> itr = userList.iterator();

		while (itr.hasNext()) {
			AutoCompleteUserDetailsResponseVO object = itr.next();
			Map<String, String> map = new HashMap<>();
			String loginId = object.getLoginId() + "(" + object.getUserID() + ")";
			map.put("loginId", loginId);
			map.put("userId", object.getUserID());
			list.add(map);
		}
		if (list.isEmpty()) {
			Map<String, String> map = new HashMap<>();
			map.put("loginId", NO_DATA_FOUND);
			map.put("userId", "");
			list.add(map);

		}

		return list;

	}

}
