package restassuredapi.test;

import java.util.ArrayList;

import org.junit.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.RandomGeneration;
import com.utils._masterVO;

import restassuredapi.api.vouchercardgroupAPI.AddVoucherCardGroupApi;
import restassuredapi.api.vouchercardgroupAPI.ModifyVoucherCardGroupApi;
import restassuredapi.pojo.addvouchercardgrouprequestpojo.AddVoucherCardGroupRequestPojo;
import restassuredapi.pojo.modifyvouchercardgrouprequestpojo.BonusAccList;
import restassuredapi.pojo.modifyvouchercardgrouprequestpojo.CardGroupDetails;
import restassuredapi.pojo.modifyvouchercardgrouprequestpojo.CardGroupList;
import restassuredapi.pojo.modifyvouchercardgrouprequestpojo.Data;
import restassuredapi.pojo.modifyvouchercardgrouprequestpojo.ModifyVoucherCardGroupRequestPojo;
import restassuredapi.pojo.modifyvouchercardgroupresponsepojo.ModifyVoucherCardGroupResponsePojo;
public class ModifyVoucherCardGroup extends BaseTest {
	/*public static WebDriver driver;
	 @BeforeClass(alwaysRun = true)
	    public void setup() {
	        driver = InitializeBrowser.Chrome();
	    }*/
	Data data = new Data();
	ModifyVoucherCardGroupRequestPojo modifyVoucherCardGroupRequestPojo= new ModifyVoucherCardGroupRequestPojo();
	CardGroupDetails cardGroupDetails=new CardGroupDetails();
	//HashMap<String, String> dataMap;
	//P2PCardGroup P2PCardGroup = new P2PCardGroup(driver);
	
	 
	@Test(dataProvider="getCardGroup")
	public void A_01_Test_modifyVoucherCardGroup_Positive(String cardgroupSetName,String cardGroupSetId) throws Exception
	{
		ModifyVoucherCardGroupApi modifyVoucherCardGroupApi = new ModifyVoucherCardGroupApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		modifyVoucherCardGroupApi.setContentType(_masterVO.getProperty("contentType"));
		modifyVoucherCardGroupRequestPojo.setIdentifierType("btnadm");
		modifyVoucherCardGroupRequestPojo.setIdentifierValue("1357");
		cardGroupDetails.setModuleCode("P2P");
		cardGroupDetails.setModifiedBy("SYSTEM");
		cardGroupDetails.setServiceTypeDesc("Voucher Consumption");
		cardGroupDetails.setNetworkCode("NG");
		cardGroupDetails.setCardGroupSetName(cardgroupSetName);
		cardGroupDetails.setCardGroupSetID(cardGroupSetId);
		cardGroupDetails.setSubServiceTypeDescription("CVG");
		//cardGroupDetails.setDefaultCardGroup("N");
	//	cardGroupDetails.setSetTypeName("Normal");
		cardGroupDetails.setApplicableFromDate("21/10/2019");
		cardGroupDetails.setApplicableFromHour("20:00:00");
		cardGroupDetails.setVersion("1");
		//creating cardGroupList
		ArrayList<CardGroupList> cardGroupList =new ArrayList<CardGroupList>();
		int j=1,k=1;
		for(int i=0;i<j;i++)
		{
			CardGroupList cardGroupListobject=new CardGroupList();
			cardGroupListobject.setStartRange("");
			cardGroupListobject.setEndRange("");
			cardGroupListobject.setValidityPeriodTypeDesc("Highest");
			cardGroupListobject.setValidityPeriod("30");
			cardGroupListobject.setGracePeriod("30");
			cardGroupListobject.setSenderTax1Name("");
			cardGroupListobject.setSenderTax1Type("");
			cardGroupListobject.setSenderTax1RateAsString("");
			cardGroupListobject.setSenderTax2Name("");
			cardGroupListobject.setSenderTax2Type("");
			cardGroupListobject.setSenderTax2RateAsString("");
			cardGroupListobject.setReceiverTax1Name("TAX1");
			cardGroupListobject.setReceiverTax1Type("AMT");
			cardGroupListobject.setReceiverTax1RateAsString("1");
			cardGroupListobject.setReceiverTax2Name("TAX2");
			cardGroupListobject.setReceiverTax2Type("AMT");
			cardGroupListobject.setReceiverTax2RateAsString("1");
			cardGroupListobject.setSenderAccessFeeType("");
			cardGroupListobject.setSenderAccessFeeRateAsString("");
			cardGroupListobject.setMinSenderAccessFeeAsString("");
			cardGroupListobject.setMaxSenderAccessFeeAsString("");
			cardGroupListobject.setReceiverAccessFeeType("PCT");
			cardGroupListobject.setReceiverAccessFeeRateAsString("1");
			cardGroupListobject.setMinReceiverAccessFeeAsString("1");
			cardGroupListobject.setMaxReceiverAccessFeeAsString("10");
			cardGroupListobject.setMultipleOf("");
			cardGroupListobject.setBonusValidityValue("");
            cardGroupListobject.setOnline("N");
            cardGroupListobject.setBoth("N");
            cardGroupListobject.setReceiverConvFactor("1");
            cardGroupListobject.setStatus("Y");
            cardGroupListobject.setCosRequired("N");
            cardGroupListobject.setInPromoAsString("0");
            cardGroupListobject.setCardName("VCNDeepa1");
            cardGroupListobject.setCardGroupCode("cardData1");
            cardGroupListobject.setReversalPermitted("");
            cardGroupListobject.setReversalModifiedDate("");
            cardGroupListobject.setVoucherTypeDesc("physical des");
            cardGroupListobject.setVoucherSegmentDesc("National");
            cardGroupListobject.setVoucherDenomination("15");
            cardGroupListobject.setProductName("NEWPROPL15");
            cardGroupListobject.setReceiverTax3Name("");
            cardGroupListobject.setReceiverTax3Type("");
            cardGroupListobject.setReceiverTax3Rate("");
            cardGroupListobject.setReceiverTax4Name("");
            cardGroupListobject.setReceiverTax4Type("");
            cardGroupListobject.setReceiverTax4Rate("");
            ArrayList<BonusAccList> bonusAccList=new  ArrayList<BonusAccList>();
            for(int l=0;l<k;l++)
            {
            	
            	BonusAccList bonusAccListobj=new BonusAccList();
            	bonusAccListobj.setMultFactor("3");
            	bonusAccListobj.setBonusValidity("1");
            	bonusAccListobj.setBonusValue("2");
            	bonusAccListobj.setType("Amt");
            	bonusAccListobj.setBonusName("CVG");
            	bonusAccList.add(bonusAccListobj);
                
            }
            cardGroupListobject.setBonusAccList(bonusAccList);
			
            cardGroupList.add(cardGroupListobject);
		}
		//setting both to the data
		data.setCardGroupDetails(cardGroupDetails);
		data.setCardGroupList(cardGroupList);
		modifyVoucherCardGroupRequestPojo.setData(data);
		modifyVoucherCardGroupApi.addBodyParam(modifyVoucherCardGroupRequestPojo);
		modifyVoucherCardGroupApi.setExpectedStatusCode(200);
		modifyVoucherCardGroupApi.perform();
		ModifyVoucherCardGroupResponsePojo modifyVoucherCardGroupResponsePojo=modifyVoucherCardGroupApi.getAPIResponseAsPOJO(ModifyVoucherCardGroupResponsePojo.class);
	    int statusCode =modifyVoucherCardGroupResponsePojo.getStatusCode();
		Assert.assertEquals(200,statusCode);
	}
	
	@DataProvider(name = "getCardGroup")
	public Object[][] TestDataFeed() {
		Object[][] categoryData = new Object[1][2];
		restassuredapi.pojo.addvouchercardgrouprequestpojo.Data data = new restassuredapi.pojo.addvouchercardgrouprequestpojo.Data();
		AddVoucherCardGroupRequestPojo addVoucherCardGroupRequestPojo=new AddVoucherCardGroupRequestPojo();
		AddVoucherCardGroupApi addVoucherCardGroupApi = new AddVoucherCardGroupApi("http://172.30.24.113:9678/pretups/rest");
		addVoucherCardGroupApi.setContentType("application/json");
		//creating CardGroupDetails 
		RandomGeneration randStr = new RandomGeneration();
		randStr.randomNumeric(5);
		restassuredapi.pojo.addvouchercardgrouprequestpojo.CardGroupDetails cardGroupDetails=new restassuredapi.pojo.addvouchercardgrouprequestpojo.CardGroupDetails();
		modifyVoucherCardGroupRequestPojo.setIdentifierType("btnadm");
		modifyVoucherCardGroupRequestPojo.setIdentifierValue("1357");
		cardGroupDetails.setStatus("Y");
		cardGroupDetails.setCreatedBy("SYSTEM");
		cardGroupDetails.setModuleCode("P2P");
		cardGroupDetails.setModifiedBy("SYSTEM");
		cardGroupDetails.setServiceTypeDesc("Voucher Consumption");
		cardGroupDetails.setNetworkCode("NG");
		String vouchercardgroupname="VCNnewTest"+ randStr.randomNumeric(5);
		cardGroupDetails.setCardGroupSetName(vouchercardgroupname);
		cardGroupDetails.setSubServiceTypeDescription("CVG");
		cardGroupDetails.setDefaultCardGroup("N");
		cardGroupDetails.setSetTypeName("Normal");
		cardGroupDetails.setApplicableFromDate("21/10/2019");
		cardGroupDetails.setApplicableFromHour("20:00:00");
		//creating cardGroupList
		ArrayList<restassuredapi.pojo.addvouchercardgrouprequestpojo.CardGroupList> cardGroupList =new ArrayList<restassuredapi.pojo.addvouchercardgrouprequestpojo.CardGroupList>();
		int j=1,k=1;
		for(int i=0;i<j;i++)
		{
			restassuredapi.pojo.addvouchercardgrouprequestpojo.CardGroupList cardGroupListobject=new restassuredapi.pojo.addvouchercardgrouprequestpojo.CardGroupList();
			cardGroupListobject.setStartRange("");
			cardGroupListobject.setEndRange("");
			cardGroupListobject.setValidityPeriodTypeDesc("Highest");
			cardGroupListobject.setValidityPeriod("30");
			cardGroupListobject.setGracePeriod("30");
			cardGroupListobject.setSenderTax1Name("");
			cardGroupListobject.setSenderTax1Type("");
			cardGroupListobject.setSenderTax1RateAsString("");
			cardGroupListobject.setSenderTax2Name("");
			cardGroupListobject.setSenderTax2Type("");
			cardGroupListobject.setSenderTax2RateAsString("");
			cardGroupListobject.setReceiverTax1Name("TAX1");
			cardGroupListobject.setReceiverTax1Type("AMT");
			cardGroupListobject.setReceiverTax1RateAsString("1");
			cardGroupListobject.setReceiverTax2Name("TAX2");
			cardGroupListobject.setReceiverTax2Type("AMT");
			cardGroupListobject.setReceiverTax2RateAsString("1");
			cardGroupListobject.setSenderAccessFeeType("");
			cardGroupListobject.setSenderAccessFeeRateAsString("");
			cardGroupListobject.setMinSenderAccessFeeAsString("");
			cardGroupListobject.setMaxSenderAccessFeeAsString("");
			cardGroupListobject.setReceiverAccessFeeType("PCT");
			cardGroupListobject.setReceiverAccessFeeRateAsString("1");
			cardGroupListobject.setMinReceiverAccessFeeAsString("1");
			cardGroupListobject.setMaxReceiverAccessFeeAsString("10");
			cardGroupListobject.setMultipleOf("");
			cardGroupListobject.setBonusValidityValue("");
            cardGroupListobject.setOnline("N");
            cardGroupListobject.setBoth("N");
            cardGroupListobject.setReceiverConvFactor("1");
            cardGroupListobject.setStatus("Y");
            cardGroupListobject.setCosRequired("N");
            cardGroupListobject.setInPromoAsString("0");
            cardGroupListobject.setCardName("VCNDeepa1");
            cardGroupListobject.setCardGroupCode("cardData1");
            cardGroupListobject.setReversalPermitted("");
            cardGroupListobject.setReversalModifiedDate("");
            cardGroupListobject.setVoucherTypeDesc("physical des");
            cardGroupListobject.setVoucherSegmentDesc("National");
            cardGroupListobject.setVoucherDenomination("15");
            cardGroupListobject.setProductName("NEWPROPL15");
            cardGroupListobject.setReceiverTax3Name("");
            cardGroupListobject.setReceiverTax3Type("");
            cardGroupListobject.setReceiverTax3Rate("");
            cardGroupListobject.setReceiverTax4Name("");
            cardGroupListobject.setReceiverTax4Type("");
            cardGroupListobject.setReceiverTax4Rate("");
            ArrayList<restassuredapi.pojo.addvouchercardgrouprequestpojo.BonusAccList> bonusAccList=new  ArrayList<restassuredapi.pojo.addvouchercardgrouprequestpojo.BonusAccList>();
            for(int l=0;l<k;l++)
            {
            	
            	restassuredapi.pojo.addvouchercardgrouprequestpojo.BonusAccList bonusAccListobj=new restassuredapi.pojo.addvouchercardgrouprequestpojo.BonusAccList();
            	bonusAccListobj.setMultFactor("3");
            	bonusAccListobj.setBonusValidity("1");
            	bonusAccListobj.setBonusValue("2");
            	bonusAccListobj.setType("Amt");
            	bonusAccListobj.setBonusName("CVG");
            	bonusAccList.add(bonusAccListobj);
                
            }
            cardGroupListobject.setBonusAccList(bonusAccList);
            cardGroupList.add(cardGroupListobject);
			
		}
		//setting both to the data
	    data.setCardGroupDetails(cardGroupDetails);
		data.setCardGroupList(cardGroupList);
		addVoucherCardGroupRequestPojo.setData(data);
		addVoucherCardGroupApi.addBodyParam(addVoucherCardGroupRequestPojo);
		addVoucherCardGroupApi.setExpectedStatusCode(200);
		addVoucherCardGroupApi.perform();
		//AddVoucherCardGroupResponsePojo addVoucherCardGroupResponsePojo=addVoucherCardGroupApi.getAPIResponseAsPOJO(AddVoucherCardGroupResponsePojo.class);
		categoryData[0][0] = vouchercardgroupname;
		System.out.println(categoryData[0][0]);
		categoryData[0][1] = DBHandler.AccessHandler.getCardGroupSetID(vouchercardgroupname);
		System.out.println(categoryData[0][1]);
		return categoryData;
	}
	
}
