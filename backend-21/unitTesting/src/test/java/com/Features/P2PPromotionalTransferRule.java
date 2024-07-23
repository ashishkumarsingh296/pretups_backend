package com.Features;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.classes.CONSTANT;
import com.classes.Login;
import com.classes.MessagesDAO;
import com.classes.UserAccess;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.entity.P2PPromotionalTrfRuleVO;
import com.pageobjects.channeladminpages.addchanneluser.AddChannelUserDetailsPage;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pageobjects.networkadminpages.homepage.P2PPromotionalTransferRuleSubCategories;
import com.pageobjects.networkadminpages.p2ppromotionaltrfrule.AddP2PPromotionalTrfRulePage1;
import com.pageobjects.networkadminpages.p2ppromotionaltrfrule.AddP2PPromotionalTrfRulePage2;
import com.pageobjects.networkadminpages.p2ppromotionaltrfrule.ModifyP2PPromotionalTrfRulePage;
import com.pageobjects.networkadminpages.p2ppromotionaltrfrule.ViewP2PPromotionalTrfRulePage;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.pageobjects.superadminpages.homepage.SuperAdminHomePage;
import com.pageobjects.superadminpages.preferences.SystemPreferencePage;
import com.pretupsControllers.BTSLUtil;
import com.testscripts.prerequisites.UpdateCache;
import com.utils.Log;
import com.utils.RandomGeneration;

public class P2PPromotionalTransferRule {

	public WebDriver driver;
	String MasterSheetPath;
	NetworkAdminHomePage homePage;
	Login login;
	RandomGeneration RandomGenerator;
	AddP2PPromotionalTrfRulePage1 addP2PPromotionalTransferRule;
	AddP2PPromotionalTrfRulePage2 addP2PPromotionalTransferRulePage2;
	ModifyP2PPromotionalTrfRulePage modifyP2PPromotionalTransferRule;
	ViewP2PPromotionalTrfRulePage viewP2PPromotionalTransferRule;
	P2PPromotionalTransferRuleSubCategories p2ppromoTrfRuleSubCategories;
	String[] result;
	SelectNetworkPage selectNetworkPage;
	Map<String, String> userAccessMap;
	NetworkAdminHomePage networkAdminHomePage;
	String User = PretupsI.USER_LOOKUP;
	String Grade = PretupsI.GRADE_LOOKUP;
	String Geography = PretupsI.GEOGRAPHY_LOOKUP;
	String Category = PretupsI.CATEGORY_LOOKUP;
	String PrePaid = DBHandler.AccessHandler.getLookUpName(PretupsI.PREPAID_LOOKUP, PretupsI.SERVICE_LOOUP);
	String PostPaid = DBHandler.AccessHandler.getLookUpName(PretupsI.POSTPAID_LOOKUP, PretupsI.SERVICE_LOOUP);
	SystemPreferencePage systemPreferencePage;
	SuperAdminHomePage saHomePage;
	UpdateCache updateCache  ;
	
	public P2PPromotionalTransferRule(WebDriver driver) {
		this.driver = driver;
		homePage = new NetworkAdminHomePage(driver);
		login = new Login();
		RandomGenerator = new RandomGeneration();
		addP2PPromotionalTransferRule = new AddP2PPromotionalTrfRulePage1(driver);
		addP2PPromotionalTransferRulePage2 = new AddP2PPromotionalTrfRulePage2(driver);
		modifyP2PPromotionalTransferRule = new ModifyP2PPromotionalTrfRulePage(driver);
		viewP2PPromotionalTransferRule = new ViewP2PPromotionalTrfRulePage(driver);
		selectNetworkPage = new SelectNetworkPage(driver);
		userAccessMap = new HashMap<String, String>();
		updateCache = new UpdateCache();
		networkAdminHomePage = new NetworkAdminHomePage(driver);
		p2ppromoTrfRuleSubCategories = new P2PPromotionalTransferRuleSubCategories(driver);
		systemPreferencePage = new SystemPreferencePage(driver);
		saHomePage = new SuperAdminHomePage(driver);
	}


	public String addP2PPromotionalTransferRule(P2PPromotionalTrfRuleVO p2pPromodata ,String promotionallevel,String Type, String SlabType) {
		String message = null;
		try{
			userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_P2P_PROMOTIONAL_TRANSFER_RULE);
			login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));

			selectNetworkPage.selectNetwork();
			networkAdminHomePage.clickP2PPromotinalTransferRuleLink();
			p2ppromoTrfRuleSubCategories.clickAddP2PPromotionalTransferRule();
			String code = DBHandler.AccessHandler.getCellGroupCode();
			p2pPromodata.setPromotionallevel(promotionallevel);
			p2pPromodata.setType(Type);
			p2pPromodata.setSlabtype(SlabType);
			addP2PPromotionalTransferRule.selectPromotionalLevel(p2pPromodata.getPromotionallevel());
			addP2PPromotionalTransferRule.selecttype(p2pPromodata.getType());
			
			
			
			if(p2pPromodata.getType().equalsIgnoreCase("TIME"))
				addP2PPromotionalTransferRule.selectslabtype(p2pPromodata.getSlabtype());
			
			addP2PPromotionalTransferRule.clicksubmitBtn();
			
			
			if(p2pPromodata.getPromotionallevel().equals("SUB"))
				addP2PPromotionalTransferRulePage2.enterMobileNumber(p2pPromodata.getMobilenumber());

			if(p2pPromodata.getPromotionallevel().equals("CEL"))
				
			
				addP2PPromotionalTransferRulePage2.cellgroupCode(code);

			addP2PPromotionalTransferRulePage2.selectRequestGatewayCode(p2pPromodata.getGatewaycode());
			addP2PPromotionalTransferRulePage2.selectSubscriberType(p2pPromodata.getSubscribertype());
			addP2PPromotionalTransferRulePage2.selectServiceClass(p2pPromodata.getServiceclass());
			addP2PPromotionalTransferRulePage2.selectSubscriberStatus(p2pPromodata.getSubscriberstatus());
			addP2PPromotionalTransferRulePage2.selectSerivceGroup(p2pPromodata.getServicegroup());
			addP2PPromotionalTransferRulePage2.selectSerivceType(p2pPromodata.getServicetype());
			addP2PPromotionalTransferRulePage2.selectSubSerivce(p2pPromodata.getSubservice());
			addP2PPromotionalTransferRulePage2.selectCardGroupSet(p2pPromodata.getCardgroupset());
			addP2PPromotionalTransferRulePage2.enterfromDate(p2pPromodata.getApplicablefromdate());
			addP2PPromotionalTransferRulePage2.entertillDate(p2pPromodata.getApplicabletilldate());

			if(BTSLUtil.isNullString(p2pPromodata.getSlabtype())||p2pPromodata.getSlabtype().equalsIgnoreCase("SINGLE")){
				addP2PPromotionalTransferRulePage2.enterfromTime(p2pPromodata.getApplicablefromtime());
				addP2PPromotionalTransferRulePage2.entertillTime(p2pPromodata.getApplicabletilltime());}

			if(!BTSLUtil.isNullString(p2pPromodata.getSlabtype()) && p2pPromodata.getSlabtype().equalsIgnoreCase("MULTIPLE"))
				addP2PPromotionalTransferRulePage2.enterTimeInMultipleSlab(p2pPromodata.getMultipleslabtime());	

			if(p2pPromodata.isAddbutton())
				addP2PPromotionalTransferRulePage2.clickaddbtn();

			if(p2pPromodata.isBackbutton())
				addP2PPromotionalTransferRulePage2.clickbackbtn();

			if(p2pPromodata.isConfirmbutton())
				addP2PPromotionalTransferRulePage2.clickconfirmbtn();

			if(p2pPromodata.isConfirmcancelbutton())
				addP2PPromotionalTransferRulePage2.clickcancelbtn();

			if(p2pPromodata.isConfirmbackbutton())
				addP2PPromotionalTransferRulePage2.clickconfirmbackbtn();

			message = new AddChannelUserDetailsPage(driver).getActualMessage();
			
			String exp= MessagesDAO.prepareMessageByKey("trfrule.operation.msg.alreadyexist","1");
			
			if( message.equals(exp)){
				
				p2ppromoTrfRuleSubCategories.clickModifyP2PPromotionalTransferRule();
				
				
				modifyP2PPromotionalTransferRule.selectPromotionalLevel(p2pPromodata.getPromotionallevel());
				modifyP2PPromotionalTransferRule.selecttype(p2pPromodata.getType());
				
							
				modifyP2PPromotionalTransferRule.clicksubmitBtn();
				
				if(p2pPromodata.getPromotionallevel().equals("SUB"))
					modifyP2PPromotionalTransferRule.enterMobileNumber(p2pPromodata.getMobilenumber());

				if(p2pPromodata.getPromotionallevel().equals("CEL"))
					modifyP2PPromotionalTransferRule.cellgroupCode(code);
				
				modifyP2PPromotionalTransferRule.clickSubmit2Btn();
				
				
				modifyP2PPromotionalTransferRule.selectTransferRule(p2pPromodata);
				
				modifyP2PPromotionalTransferRule.clickModifyBtn();
				
				modifyP2PPromotionalTransferRule.selectCardGroupSet(p2pPromodata.getCardgroupset());
				modifyP2PPromotionalTransferRule.enterfromDate(p2pPromodata.getApplicablefromdate());
				modifyP2PPromotionalTransferRule.entertillDate(p2pPromodata.getApplicabletilldate());

				if(BTSLUtil.isNullString(p2pPromodata.getSlabtype())||p2pPromodata.getSlabtype().equalsIgnoreCase("SINGLE")){
					modifyP2PPromotionalTransferRule.enterfromTime(p2pPromodata.getApplicablefromtime());
					modifyP2PPromotionalTransferRule.entertillTime(p2pPromodata.getApplicabletilltime());}

				if(!BTSLUtil.isNullString(p2pPromodata.getSlabtype()) && p2pPromodata.getSlabtype().equalsIgnoreCase("MULTIPLE"))
					modifyP2PPromotionalTransferRule.enterTimeInMultipleSlab(p2pPromodata.getMultipleslabtime());	
				modifyP2PPromotionalTransferRule.clickModifySubmitBtn();
				modifyP2PPromotionalTransferRule.clickFinalModifyBtn();

				message = new AddChannelUserDetailsPage(driver).getActualMessage();
	
			}
			else {
				message = new AddChannelUserDetailsPage(driver).getActualMessage();
			}
		}
		catch(Exception e){
			message = new AddChannelUserDetailsPage(driver).getActualMessage();
			Log.writeStackTrace(e);	
		}
		
		return message;
	}
	
	
	
	
	public String addP2PPromotionalTransferRuleTimeRange(P2PPromotionalTrfRuleVO p2pPromodata ,String promotionallevel,String Type, String SlabType) {
		String message = null;
		try{
			userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_P2P_PROMOTIONAL_TRANSFER_RULE);
			login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));

			selectNetworkPage.selectNetwork();
			networkAdminHomePage.clickP2PPromotinalTransferRuleLink();
			p2ppromoTrfRuleSubCategories.clickAddP2PPromotionalTransferRule();
			String code = DBHandler.AccessHandler.getCellGroupCode();
			p2pPromodata.setPromotionallevel(promotionallevel);
			p2pPromodata.setType(Type);
			p2pPromodata.setSlabtype(SlabType);
			addP2PPromotionalTransferRule.selectPromotionalLevel(p2pPromodata.getPromotionallevel());
			addP2PPromotionalTransferRule.selecttype(p2pPromodata.getType());
			
			
			
			if(p2pPromodata.getType().equalsIgnoreCase("TIME"))
				addP2PPromotionalTransferRule.selectslabtype(p2pPromodata.getSlabtype());
			
			addP2PPromotionalTransferRule.clicksubmitBtn();
			
			
			if(p2pPromodata.getPromotionallevel().equals("SUB"))
				addP2PPromotionalTransferRulePage2.enterMobileNumber(p2pPromodata.getMobilenumber());

			if(p2pPromodata.getPromotionallevel().equals("CEL"))
				addP2PPromotionalTransferRulePage2.cellgroupCode(code);

			
			String currDate = networkAdminHomePage.getDate();
			addP2PPromotionalTransferRulePage2.selectRequestGatewayCode(p2pPromodata.getGatewaycode());
			addP2PPromotionalTransferRulePage2.selectSubscriberType(p2pPromodata.getSubscribertype());
			addP2PPromotionalTransferRulePage2.selectServiceClass(p2pPromodata.getServiceclass());
			addP2PPromotionalTransferRulePage2.selectSubscriberStatus(p2pPromodata.getSubscriberstatus());
			addP2PPromotionalTransferRulePage2.selectSerivceGroup(p2pPromodata.getServicegroup());
			addP2PPromotionalTransferRulePage2.selectSerivceType(p2pPromodata.getServicetype());
			addP2PPromotionalTransferRulePage2.selectSubSerivce(p2pPromodata.getSubservice());
			addP2PPromotionalTransferRulePage2.selectCardGroupSet(p2pPromodata.getCardgroupset());
			p2pPromodata.setApplicablefromdate(networkAdminHomePage.addDaysToCurrentDate(currDate, 2));
			addP2PPromotionalTransferRulePage2.enterfromDate(p2pPromodata.getApplicablefromdate());
			addP2PPromotionalTransferRulePage2.entertillDate(p2pPromodata.getApplicabletilldate());

			if(BTSLUtil.isNullString(p2pPromodata.getSlabtype())||p2pPromodata.getSlabtype().equalsIgnoreCase("SINGLE")){
				addP2PPromotionalTransferRulePage2.enterfromTime(p2pPromodata.getApplicablefromtime());
				addP2PPromotionalTransferRulePage2.entertillTime(p2pPromodata.getApplicabletilltime());}

			if(!BTSLUtil.isNullString(p2pPromodata.getSlabtype()) && p2pPromodata.getSlabtype().equalsIgnoreCase("MULTIPLE"))
				addP2PPromotionalTransferRulePage2.enterTimeInMultipleSlab(p2pPromodata.getMultipleslabtime());	

			if(p2pPromodata.isAddbutton())
				addP2PPromotionalTransferRulePage2.clickaddbtn();

			if(p2pPromodata.isBackbutton())
				addP2PPromotionalTransferRulePage2.clickbackbtn();

			if(p2pPromodata.isConfirmbutton())
				addP2PPromotionalTransferRulePage2.clickconfirmbtn();

			if(p2pPromodata.isConfirmcancelbutton())
				addP2PPromotionalTransferRulePage2.clickcancelbtn();

			if(p2pPromodata.isConfirmbackbutton())
				addP2PPromotionalTransferRulePage2.clickconfirmbackbtn();

			message = new AddChannelUserDetailsPage(driver).getActualMessage();
			
			String exp= MessagesDAO.prepareMessageByKey("trfrule.operation.msg.alreadyexist","1");
			
			if( message.equals(exp)){
				
				p2ppromoTrfRuleSubCategories.clickModifyP2PPromotionalTransferRule();
				
				
				modifyP2PPromotionalTransferRule.selectPromotionalLevel(p2pPromodata.getPromotionallevel());
				modifyP2PPromotionalTransferRule.selecttype(p2pPromodata.getType());
				
							
				modifyP2PPromotionalTransferRule.clicksubmitBtn();
				
				if(p2pPromodata.getPromotionallevel().equals("SUB"))
					modifyP2PPromotionalTransferRule.enterMobileNumber(p2pPromodata.getMobilenumber());

				if(p2pPromodata.getPromotionallevel().equals("CEL"))
					modifyP2PPromotionalTransferRule.cellgroupCode(code);
				
				modifyP2PPromotionalTransferRule.clickSubmit2Btn();
				
				
				modifyP2PPromotionalTransferRule.selectTransferRule(p2pPromodata);
				
				modifyP2PPromotionalTransferRule.clickModifyBtn();
				
				modifyP2PPromotionalTransferRule.selectCardGroupSet(p2pPromodata.getCardgroupset());
				modifyP2PPromotionalTransferRule.enterfromDate(p2pPromodata.getApplicablefromdate());
				modifyP2PPromotionalTransferRule.entertillDate(p2pPromodata.getApplicabletilldate());

				if(BTSLUtil.isNullString(p2pPromodata.getSlabtype())||p2pPromodata.getSlabtype().equalsIgnoreCase("SINGLE")){
					modifyP2PPromotionalTransferRule.enterfromTime(p2pPromodata.getApplicablefromtime());
					modifyP2PPromotionalTransferRule.entertillTime(p2pPromodata.getApplicabletilltime());}

				if(!BTSLUtil.isNullString(p2pPromodata.getSlabtype()) && p2pPromodata.getSlabtype().equalsIgnoreCase("MULTIPLE"))
					modifyP2PPromotionalTransferRule.enterTimeInMultipleSlab(p2pPromodata.getMultipleslabtime());	
				modifyP2PPromotionalTransferRule.clickModifySubmitBtn();
				modifyP2PPromotionalTransferRule.clickFinalModifyBtn();

				message = new AddChannelUserDetailsPage(driver).getActualMessage();
	
			}
			else {
				message = new AddChannelUserDetailsPage(driver).getActualMessage();
			}
		}
		catch(Exception e){
			message = new AddChannelUserDetailsPage(driver).getActualMessage();
			Log.writeStackTrace(e);	
		}
		
		return message;
	}

	
	
	public String modifyP2PPromotionalTransferRule(P2PPromotionalTrfRuleVO p2pPromodata ,String promotionallevel,String Type) {
		String message = null;
		try{
			userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_P2P_PROMOTIONAL_TRANSFER_RULE);
			login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));

			selectNetworkPage.selectNetwork();
			networkAdminHomePage.clickP2PPromotinalTransferRuleLink();
			p2ppromoTrfRuleSubCategories.clickModifyP2PPromotionalTransferRule();
			String code = DBHandler.AccessHandler.getCellGroupCode();
			p2pPromodata.setPromotionallevel(promotionallevel);
			p2pPromodata.setType(Type);
			
			modifyP2PPromotionalTransferRule.selectPromotionalLevel(p2pPromodata.getPromotionallevel());
			modifyP2PPromotionalTransferRule.selecttype(p2pPromodata.getType());
			
						
			modifyP2PPromotionalTransferRule.clicksubmitBtn();
			
			if(p2pPromodata.getPromotionallevel().equals("SUB"))
				modifyP2PPromotionalTransferRule.enterMobileNumber(p2pPromodata.getMobilenumber());

			if(p2pPromodata.getPromotionallevel().equals("CEL"))
				modifyP2PPromotionalTransferRule.cellgroupCode(code);
			
			modifyP2PPromotionalTransferRule.clickSubmit2Btn();
			
			
			modifyP2PPromotionalTransferRule.selectTransferRule(p2pPromodata);
			
			modifyP2PPromotionalTransferRule.clickModifyBtn();
			modifyP2PPromotionalTransferRule.selectCardGroupSet(p2pPromodata.getCardgroupset());
			modifyP2PPromotionalTransferRule.enterfromDate(p2pPromodata.getApplicablefromdate());
			modifyP2PPromotionalTransferRule.entertillDate(p2pPromodata.getApplicabletilldate());
			
			

			if(p2pPromodata.getType().equals("DATE")){
				modifyP2PPromotionalTransferRule.enterfromTime(p2pPromodata.getApplicablefromtime());
				modifyP2PPromotionalTransferRule.entertillTime(p2pPromodata.getApplicabletilltime());}

			/*if(p2pPromodata.getType().equals("TIME")){
				modifyP2PPromotionalTransferRule.enterTimeInMultipleSlab(p2pPromodata.getMultipleslabtime());
			}*/
			modifyP2PPromotionalTransferRule.clickModifySubmitBtn();
			modifyP2PPromotionalTransferRule.clickFinalModifyBtn();

			message = new AddChannelUserDetailsPage(driver).getActualMessage();
		}
		
		catch(Exception e){
			message = new AddChannelUserDetailsPage(driver).getActualMessage();
			Log.writeStackTrace(e);	
		}
		return message;
		
	}
	
	
	public String deleteP2PPromotionalTransferRule(P2PPromotionalTrfRuleVO p2pPromodata,String promotionallevel,String Type) {
		String message = null;
		try{
			userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_P2P_PROMOTIONAL_TRANSFER_RULE);
			login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));

			selectNetworkPage.selectNetwork();
			networkAdminHomePage.clickP2PPromotinalTransferRuleLink();
			p2ppromoTrfRuleSubCategories.clickModifyP2PPromotionalTransferRule();
			String code = DBHandler.AccessHandler.getCellGroupCode();
			p2pPromodata.setPromotionallevel(promotionallevel);
			p2pPromodata.setType(Type);
			
			modifyP2PPromotionalTransferRule.selectPromotionalLevel(p2pPromodata.getPromotionallevel());
			modifyP2PPromotionalTransferRule.selecttype(p2pPromodata.getType());
			
						
			modifyP2PPromotionalTransferRule.clicksubmitBtn();
			
			if(p2pPromodata.getPromotionallevel().equals("SUB"))
				modifyP2PPromotionalTransferRule.enterMobileNumber(p2pPromodata.getMobilenumber());

			if(p2pPromodata.getPromotionallevel().equals("CEL"))
				modifyP2PPromotionalTransferRule.cellgroupCode(code);
			
			modifyP2PPromotionalTransferRule.clickSubmit2Btn();
			
			modifyP2PPromotionalTransferRule.selectTransferRule(p2pPromodata);
			
			modifyP2PPromotionalTransferRule.clickModifyBtn();
			
				
			modifyP2PPromotionalTransferRule.clickDeleteBtn();
			driver.switchTo().alert().accept();

			message = new AddChannelUserDetailsPage(driver).getActualMessage();
		}
		
		catch(Exception e){
			message = new AddChannelUserDetailsPage(driver).getActualMessage();
			Log.writeStackTrace(e);	
		}
		return message;
		
	}


	
	
	
	public String viewP2PPromotionalTransferRule(P2PPromotionalTrfRuleVO p2pPromodata,String promotionallevel,String Type) {
		String message = null;
		try{
			userAccessMap = UserAccess.getUserWithAccess(RolesI.ADD_P2P_PROMOTIONAL_TRANSFER_RULE);
			login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));

			selectNetworkPage.selectNetwork();
			networkAdminHomePage.clickP2PPromotinalTransferRuleLink();
			p2ppromoTrfRuleSubCategories.clickViewP2PPromotionalTransferRule();
			
			p2pPromodata.setPromotionallevel(promotionallevel);
			p2pPromodata.setType(Type);
			String code = DBHandler.AccessHandler.getCellGroupCode();
			viewP2PPromotionalTransferRule.selectPromotionalLevel(p2pPromodata.getPromotionallevel());
			viewP2PPromotionalTransferRule.selecttype(p2pPromodata.getType());
			viewP2PPromotionalTransferRule.clicksubmitBtn();
			if(p2pPromodata.getPromotionallevel().equals("SUB"))
				modifyP2PPromotionalTransferRule.enterMobileNumber(p2pPromodata.getMobilenumber());

			if(p2pPromodata.getPromotionallevel().equals("CEL"))
				modifyP2PPromotionalTransferRule.cellgroupCode(code);
			
			viewP2PPromotionalTransferRule.clicksecondsubmitBtn();
			message = viewP2PPromotionalTransferRule.getHeading();

		}
			
	
			catch(Exception e){
				message = new AddChannelUserDetailsPage(driver).getActualMessage();
				Log.writeStackTrace(e);	
			}
			return message;
			
		}
	
	
	
	
	public HashMap<String, String> p2pPromoTransferApplicablePreference(String scenario)
	{
    	HashMap<String, String> initiateMap = new HashMap<String, String>();
    	userAccessMap = UserAccess.getUserWithAccess(RolesI.SYSTEM_PREFERENCE);
    	login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickPreferences();
		systemPreferencePage.selectModule("P2P");
		systemPreferencePage.selectPreferenceType(PretupsI.NETWORK_PREFERENCE_TYPE);
		systemPreferencePage.clickSubmitButton();
		String preferenceCode = DBHandler.AccessHandler.getNamefromSystemPreference(CONSTANT.P2P_PROMO_TRF_APP);
		boolean isPreferenceDisplayed = systemPreferencePage.isPreferenceDisplayed(preferenceCode);
		boolean isModifiedAllowed = systemPreferencePage.isModifiedAllowed(preferenceCode);
		if (scenario.equalsIgnoreCase("systemPreferenceVerification") && isPreferenceDisplayed) {
			initiateMap.put("MessageStatus", "Y");
		}
		else if (scenario.equalsIgnoreCase("isModifyAllowed") && isModifiedAllowed) {
			initiateMap.put("MessageStatus", "Y");
		}
		else if (scenario.equalsIgnoreCase("modifySystemPreference")){
			systemPreferencePage.setValueofSystemPreference(preferenceCode, "true");
			systemPreferencePage.clickModifyBtn();
			systemPreferencePage.clickConfirmBtn();
			updateCache.updateCache();
			String defaultValue = DBHandler.AccessHandler.getSystemPreference(CONSTANT.P2P_PROMO_TRF_APP);
			if(defaultValue.equalsIgnoreCase("true"))
			initiateMap.put("MessageStatus", "Y");
		}
		return initiateMap;
	}
	
	
	
	
	public HashMap<String, String> p2pPromoApplicableOrderPreference(String scenario)
	{
    	HashMap<String, String> initiateMap = new HashMap<String, String>();
    	userAccessMap = UserAccess.getUserWithAccess(RolesI.SYSTEM_PREFERENCE);
    	login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		selectNetworkPage.selectNetwork();
		saHomePage.clickPreferences();
		systemPreferencePage.selectModule("P2P");
		systemPreferencePage.selectPreferenceType(PretupsI.NETWORK_PREFERENCE_TYPE);
		systemPreferencePage.clickSubmitButton();
		String preferenceCode = DBHandler.AccessHandler.getNamefromSystemPreference(CONSTANT.P2P_PRO_TRF_ST_LVL_CODE);
		boolean isPreferenceDisplayed = systemPreferencePage.isPreferenceDisplayed(preferenceCode);
		boolean isModifiedAllowed = systemPreferencePage.isModifiedAllowed(preferenceCode);
		if (scenario.equalsIgnoreCase("systemPreferenceVerification") && isPreferenceDisplayed) {
			initiateMap.put("MessageStatus", "Y");
		}
		else if (scenario.equalsIgnoreCase("isModifyAllowed") && isModifiedAllowed) {
			initiateMap.put("MessageStatus", "Y");
		}
		else if (scenario.equalsIgnoreCase("modifySystemPreference")) {
			systemPreferencePage.setValueofSystemPreference(preferenceCode, "1");
			systemPreferencePage.clickModifyBtn();
			systemPreferencePage.clickConfirmBtn();
			updateCache.updateCache();
			String defaultValue = DBHandler.AccessHandler.getSystemPreference(CONSTANT.P2P_PRO_TRF_ST_LVL_CODE);
			if(defaultValue.equalsIgnoreCase("1"))
			initiateMap.put("MessageStatus", "Y");
		}
		return initiateMap;
	}	
	
	
	
	
	
	
}
