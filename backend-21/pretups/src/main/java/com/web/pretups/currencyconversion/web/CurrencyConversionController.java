package com.web.pretups.currencyconversion.web;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonController;
import com.btsl.common.PretupsRestUtil;
import com.btsl.pretups.currencyconversion.businesslogic.CurrencyConversionVO;
import com.btsl.user.businesslogic.UserVO;
import com.web.pretups.currencyconversion.service.CurrencyConversionService;
import com.web.pretups.currencyconversion.service.CurrencyConversionServiceImpl;
@Controller
public class CurrencyConversionController extends CommonController {

	//@Autowired
	//private CurrencyConversionService currencyConversionService;
	@RequestMapping(value = "/currconv/currconv.form", method = RequestMethod.GET)
	public String loadCurrencyConversionData(final Model model, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BTSLBaseException {

		if (log.isDebugEnabled()) {
			log.debug("CurrencyConversionController#loadCurrencyConversionData", "Entered ");
		}

		authorise(request, response, "CCN01A", false);	
		CurrencyConversionService currencyConversionService = new CurrencyConversionServiceImpl();
		model.addAttribute("currconv", new CurrencyConversionVO());	
		List<CurrencyConversionVO> currencyConversionDataList = new ArrayList<CurrencyConversionVO>();
		Boolean flag = currencyConversionService.loadDetails(currencyConversionDataList);	
		CurrencyConversionVO co=new CurrencyConversionVO();
		co.setmDataList(currencyConversionDataList);
		model.addAttribute("currencyConversionDataList", co);	
		
		System.out.println(co.getmDataList().get(0).getCountry());
		if (log.isDebugEnabled()) {
			log.debug("CurrencyConversionController#loadCurrencyConversionData", "Exiting");
		}
		return "currencyconversion/currencyConversion";
	}
	@RequestMapping(value = "/currconv/submit-currency-conversion.form", method = RequestMethod.POST)
	public String processCurrencyConversionData(@ModelAttribute("currencyConversion") CurrencyConversionVO currencyVO , BindingResult bindingResult,
			final Model model, HttpServletRequest request) throws BTSLBaseException, NoSuchMessageException, IOException {
		if (log.isDebugEnabled()) {
			log.debug("CurrencyConversionController#processCurrencyConversionData", "Entered ");
		}
		CurrencyConversionVO co=(CurrencyConversionVO)(currencyVO.getmDataList().get(0));
		CurrencyConversionService currencyConversionService = new CurrencyConversionServiceImpl();
		UserVO userVO = this.getUserFormSession(request);		
		if(currencyConversionService.updateCurrencyDetails(currencyVO,userVO,bindingResult))
		{
			model.addAttribute("success", PretupsRestUtil.getMessageString("currencyconversion.update.currency.success"));
			model.addAttribute("currencyConversionDataList", currencyVO);
		}
		else
		{
			model.addAttribute("fail", true);
		}
		
		return "currencyconversion/currencyConversion";
		
	}
}
