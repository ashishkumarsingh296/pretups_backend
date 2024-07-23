package com.web.pretups.currencyconversion.service;

import java.io.IOException;
import java.util.List;

import org.springframework.validation.BindingResult;

import com.btsl.pretups.currencyconversion.businesslogic.CurrencyConversionVO;
import com.btsl.user.businesslogic.UserVO;
/*
 * Interface which provides base for BarredUserServiceImpl class
 * also declares different method for Bar User functionalities
 */
public interface CurrencyConversionService {

	public boolean loadDetails(List<CurrencyConversionVO> currencyConversionDataList) throws  IOException;
	public boolean updateCurrencyDetails(CurrencyConversionVO currencyVO,UserVO userVO, BindingResult bindingResult) throws  IOException;
	
}
