package com.databuilder;
import java.sql.SQLException;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.classes.CONSTANT;
import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.utils.Log;
import com.utils._masterVO;

public class DataBuilder {

	@BeforeClass(alwaysRun = true)
	public void loadMaps() {
		_masterVO.loadProperties();
		_masterVO.loadMasterSheet();

		DBHandler DBDAO = new DBHandler();
		DBDAO.getDatabaseDAO();
	}

	/**
	 * @author krishan.chawla
	 * This test fetches Operator Users from Database & write them in Operator Users Hierarchy Sheet
	 * @throws SQLException 
	 * @throws Exception
	 * Dependencies: Database Connectivity / BuilderLogic.java for Functional Logic.
	 **/
	@Test(description="Fetch Operator User Hierarchy from database & add them to Operator Users Hierarchy Sheet", priority=1)
	public void fetchOperatorUsers() throws SQLException {
		BuilderLogic OperatorHierarchy = new BuilderLogic();
		OperatorHierarchy.WriteOperatorUserstoExcel();
	}
	
	/**
	 * @author krishan.chawla
	 * This test fetches Channel Users from Transfer Matrix & write them in Channel Users Hierarchy Sheet
	 * Dependencies: Transfer Matrix sheet in DataProvider.xlsx / BuilderLogic.java for Functional Logic.
	 **/
	@Test(description="Fetch Channel User Hierarchy from Transfer Matrix & add them to Channel Users Hierarchy Sheet", priority=2)
	public void fetchChannelHierarchy() {
		BuilderLogic ChannelUserHierarchy = new BuilderLogic();
		ChannelUserHierarchy.WriteChannelUsersHierarchy(ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		ChannelUserHierarchy.WriteChannelUsersHierarchy(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET);
	}
	
	@Test(description="Fetch Category Code & Geographical Domain Type using the Category Name provided in Transfer Matrix", priority=3)
	public void getGeographicalDomainTypeAndCategoryCode() {
		BuilderLogic CategoryAndGeographicalDomainType = new BuilderLogic();
		CategoryAndGeographicalDomainType.fetchCategoryCodeAndGeographicalDomain(ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		CategoryAndGeographicalDomainType.fetchCategoryCodeAndGeographicalDomain(ExcelI.EXTGW_CHANNEL_USERS_HIERARCHY_SHEET);
	}
	
	/**
	 * @author ayush.abhijeet
	 * Dependencies: Pending
	 * @throws SQLException 
	 **/
	@Test(description="Fetch Geographical Domain information from database & add them to Geography Domain Types Sheet", priority=4)
	public void fetchGeographicalDomainTypes() throws SQLException {
		BuilderLogic geographicalDomain = new BuilderLogic();
		geographicalDomain.WriteGeographyDomainTypestoExcel();
	}
	
	/**
	 * @author tinky.sharma
	 * Dependencies: Database Connection
	 * @throws SQLException 
	 **/
	@Test(description="Pending", priority=5)
	public void fetchDomainAndCategories() throws SQLException {
		BuilderLogic DomainAndCategories = new BuilderLogic();
		DomainAndCategories.WriteDomainCategoryForChannelUser();
	}
	
	/**
	 * @author krishan.chawla
	 * Dependencies: Database Connection
	 * @throws SQLException 
	 **/
	@Test(description="Read Transfer Matrix Sheet from Data Provider & Write Required Transfer Rule Combinations to Transfer Rule Sheet", priority=6)
	public void generateTransferRuleSheet() {
		BuilderLogic TransferRule = new BuilderLogic();
		TransferRule.writeTransferRuleSheet();
		String trfUserLevelAlllow = DBHandler.AccessHandler.getSystemPreference(CONSTANT.TRF_RULE_USER_LEVEL_ALLOW);
	       if(trfUserLevelAlllow.equalsIgnoreCase("FALSE"))
	    	   TransferRule.writeTransferRuleType();
	       else if(trfUserLevelAlllow.equalsIgnoreCase("TRUE"))
	    	   Log.info("As preference 'TRF_RULE_USER_LEVEL_ALLOW' is TRUE. Hence, not writing the 'trf_rul_type' in 'Transfer_rule_sheet' ");
	}
	
	/**
     * @author ayush.abhijeet
     * 
      **/
     @Test(description="Fetch Access Bearer information from Access Bearer sheet & add them to Transfer Rule Sheet", priority=7)
     public void fetchAccessBearer(){
            BuilderLogic accessBearer = new BuilderLogic();
            Object[][] accessBearerData = accessBearer.fetchAcsessBearer();
            accessBearer.writeAcsessBearer(accessBearerData);
     }
     
	
	/**
	 * @author tinky.sharma
	 * Dependencies: Database Connection
	 * @throws SQLException 
	 **/
	@Test(description="Fetch Details from Database & write C2S Services & Sub Services Sheet", priority=8)
	public void fetchC2SServicesAndSubServices() throws SQLException {
		BuilderLogic ServicesAndsubServices = new BuilderLogic();
		ServicesAndsubServices.WriteC2SServiceAndSubServices();
	}

	/**
	 * @author tinky.sharma
	 * Dependencies: Database Connection
	 * @throws SQLException 
	 **/
	@Test(description="Fetch Details from Database & write P2P Services & Sub Services", priority=9)
	public void fetchP2PServicesAndSubServices() throws SQLException {
		BuilderLogic ServicesAndsubServices = new BuilderLogic();
		ServicesAndsubServices.WriteP2PServiceAndSubServices();
	}
	
	/**
	 * @author tinky.sharma
	 * Dependencies: Database Connection
	 * @throws SQLException 
	 **/
	@Test(description="Fetch Details from Database & write Product Sheet.", priority=10)
	public void fetchProductTypes() throws SQLException {
		BuilderLogic productTypes = new BuilderLogic();
		productTypes.WriteProductType();
	}

	@Test(description="Create Staff User Sheet.", priority=11)
	public void generateStaffUsersheet() throws SQLException {
		BuilderLogic staffsheet = new BuilderLogic();
		staffsheet.WriteStaffUser();
	}
	
	@Test(description="Batch Operator Users Sheet", priority=12)
	public void createSheetForBatchOperators() throws SQLException{
		BuilderLogic OperatorHierarchy = new BuilderLogic();
		OperatorHierarchy.WriteOperatorUserstoExcel("BATCH");
	}

}