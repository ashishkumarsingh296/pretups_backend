package angular.feature;


import angular.classes.LoginRevamp;
import angular.pageobjects.Home.CUHomePage;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.classes.BaseTest;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils._masterVO;
import java.util.HashMap;
import org.openqa.selenium.WebDriver;

public class CUHomeRevamp extends BaseTest {

    public WebDriver driver;
    LoginRevamp login;
    CUHomePage CUHome;

    public CUHomeRevamp(WebDriver driver) {
        this.driver = driver;
        login = new LoginRevamp();
        CUHome = new CUHomePage(driver);
    }


    public void performHomeAddWidgets(String ParentCategory, String FromCategory, String PIN) {
        final String methodName = "performHomeAddWidgets";
        Log.methodEntry(methodName, ParentCategory, FromCategory, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        String graphType;
        CUHome.clickCUHomeHeading();
        
        removeGraphs();// remove graph if already present
        
        CUHome.clickAddWidget();
        int graphCount = CUHome.getNumberOfGraphTypes();
        if(graphCount<1) {
        	currentNode.log(Status.FAIL, "Graph options are not available in drop down list.") ;
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
            return;
        }
        
        CUHome.clickPopupCancelButton();
        CUHome.clickAddWidget();
        
        graphType = CUHome.getAndClickGraphName(1);
        CUHome.clickSaveWidget();
        CUHome.clickSaveButton();
        Boolean widgetPresent = CUHome.checkWidget(graphType);
        removeGraphs();
        
        Boolean widgetPresent1 = CUHome.checkWidget(graphType);
        if (widgetPresent && !widgetPresent1) {
            ExtentI.Markup(ExtentColor.GREEN, "Widget successfully added and is visible on the Home Screen.");
            ExtentI.attachCatalinaLogsForSuccess();
            ExtentI.attachScreenShot();
        } else {
            currentNode.log(Status.FAIL, "Widget is not visible on the Home Screen.") ;
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        Log.methodExit(methodName);
    }



    public void performHomeRemoveAddedWidgets(String ParentCategory, String FromCategory, String PIN) {
        final String methodName = "performHomeRemoveAddedWidgets";
        Log.methodEntry(methodName, ParentCategory, FromCategory, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        String graphType;
        CUHome.clickCUHomeHeading();
        removeGraphs();
        CUHome.clickAddWidget();
        
        int graphCount = CUHome.getNumberOfGraphTypes();
        if(graphCount<1) {
        	currentNode.log(Status.FAIL, "Graph options are not available in drop down list.") ;
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
            return;
        }
        
        CUHome.clickPopupCancelButton();
        CUHome.clickAddWidget();
        
        graphType = CUHome.getAndClickGraphName(1);
        CUHome.clickSaveWidget();
        CUHome.clickSaveButton();
        CUHome.clickSettingButton();
        CUHome.clickEditButton();
        CUHome.clickRemoveButton();
        Boolean widgetPresent = CUHome.checkWidget(graphType);
        removeGraphs();

        if (!widgetPresent) {
            ExtentI.Markup(ExtentColor.GREEN, "Widget successfully removed and is not visible on the Home Screen.");
            ExtentI.attachCatalinaLogsForSuccess();
            ExtentI.attachScreenShot();
        } else {
            currentNode.log(Status.FAIL, "Widget is still visible on the Home Screen.") ;
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        Log.methodExit(methodName);
    }

    public void performHomeCancelAddedWidgets(String ParentCategory, String FromCategory, String PIN) {
        final String methodName = "performHomeCancelAddedWidgets";
        Log.methodEntry(methodName, ParentCategory, FromCategory, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        String graphType;
        CUHome.clickCUHomeHeading();
        removeGraphs();
        CUHome.clickAddWidget();
        
        int graphCount = CUHome.getNumberOfGraphTypes();
        if(graphCount<1) {
        	currentNode.log(Status.FAIL, "Graph options are not available in drop down list.") ;
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
            return;
        }
        
        CUHome.clickPopupCancelButton();
        CUHome.clickAddWidget();
        
        graphType = CUHome.getAndClickGraphName(1);
        CUHome.clickSaveWidget();
        CUHome.clickCancelButton();
        Boolean widgetPresent = CUHome.checkWidget(graphType);
        
        removeGraphs();
        
        if (!widgetPresent) {
            ExtentI.Markup(ExtentColor.GREEN, "Widget successfully removed and is not visible on the Home Screen.");
            ExtentI.attachCatalinaLogsForSuccess();
            ExtentI.attachScreenShot();
        } else {
            currentNode.log(Status.FAIL, "Widget is still visible on the Home Screen.") ;
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        Log.methodExit(methodName);
    }


    public void performHomeEditAddedWidgets(String ParentCategory, String FromCategory, String PIN) {
        final String methodName = "performHomeEditAddedWidgets";
        Log.methodEntry(methodName, ParentCategory, FromCategory, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        String graphType = "C2C Stock In";
        CUHome.clickCUHomeHeading();
        removeGraphs();
        CUHome.clickAddWidget();
        
        int graphCount = CUHome.getNumberOfGraphTypes();
        if(graphCount<2) {
        	currentNode.log(Status.FAIL, "Graph options are not available in drop down list.") ;
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
            return;
        }
        
        CUHome.clickPopupCancelButton();
        CUHome.clickAddWidget();
        
        graphType = CUHome.getAndClickGraphName(1);
        CUHome.clickSaveWidget();
        CUHome.clickSaveButton();
        CUHome.clickSettingButton();
        CUHome.clickEditButton();
        CUHome.clickEditWidgetButton();
        
        graphType = CUHome.getAndClickGraphName(2);
        CUHome.clickSaveWidget();
        CUHome.clickSaveButton();
        Boolean widgetPresent = CUHome.checkWidget(graphType);
        
        removeGraphs();
        
        if (widgetPresent) {
            ExtentI.Markup(ExtentColor.GREEN, "Widget successfully removed and is not visible on the Home Screen.");
            ExtentI.attachCatalinaLogsForSuccess();
            ExtentI.attachScreenShot();
        } else {
            currentNode.log(Status.FAIL, "Widget is still visible on the Home Screen.") ;
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        Log.methodExit(methodName);
    }





    public void performHomeAddMultipleWidgets(String ParentCategory, String FromCategory, String PIN) {
        final String methodName = "performHomeAddMultipleWidgets";
        Log.methodEntry(methodName, ParentCategory, FromCategory, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        String graphType ;
        String graphType1 ;
        CUHome.clickCUHomeHeading();
        removeGraphs();
        CUHome.clickAddWidget();
        
        int graphCount = CUHome.getNumberOfGraphTypes();
        if(graphCount<2) {
        	currentNode.log(Status.FAIL, "Graph options are not available in drop down list.") ;
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
            return;
        }
        
        CUHome.clickPopupCancelButton();
        CUHome.clickAddWidget();
        
        graphType = CUHome.getAndClickGraphName(1);
        CUHome.clickSaveWidget();
        CUHome.clickSaveButton();
        Boolean widgetPresent = CUHome.checkWidget(graphType);
        CUHome.clickAddWidget();
        
        graphType1 = CUHome.getAndClickGraphName(2);
        CUHome.clickSaveWidget();
        CUHome.clickSaveButton();
        Boolean widgetPresent1 = CUHome.checkWidget(graphType1);
        
        removeGraphs();
        
        if (widgetPresent&&widgetPresent1) {
            ExtentI.Markup(ExtentColor.GREEN, "Widget successfully added and is visible on the Home Screen.");
            ExtentI.attachCatalinaLogsForSuccess();
            ExtentI.attachScreenShot();
        } else {
            currentNode.log(Status.FAIL, "Widget is not visible on the Home Screen.") ;
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        Log.methodExit(methodName);
    }




    public void performHomeMoveAddedWidgets(String ParentCategory, String FromCategory, String PIN) {
        final String methodName = "performHomeMoveAddedWidgets";
        Log.methodEntry(methodName, ParentCategory, FromCategory, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        String graphType;
        String graphType1;
        CUHome.clickCUHomeHeading();
        removeGraphs();
        CUHome.clickAddWidget();
        
        int graphCount = CUHome.getNumberOfGraphTypes();
        if(graphCount<2) {
        	currentNode.log(Status.FAIL, "Graph options are not available in drop down list.") ;
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
            return;
        }
        
        CUHome.clickPopupCancelButton();
        CUHome.clickAddWidget();
        
        graphType = CUHome.getAndClickGraphName(1);
        CUHome.clickSaveWidget();
        CUHome.clickSaveButton();
        CUHome.clickAddWidget();
        
        graphType1 = CUHome.getAndClickGraphName(2);
        CUHome.clickSaveWidget();
        CUHome.clickSaveButton();
        CUHome.clickSettingButton();
        CUHome.clickRearrangeButton();
        CUHome.dragandDropWidgets(graphType, graphType1);
        CUHome.clickSaveButton();
        Boolean widgetPresent = CUHome.checkWidget(graphType);
        Boolean widgetPresent1 = CUHome.checkWidget(graphType1);
        
        removeGraphs();
        
        if (widgetPresent&&widgetPresent1) {
            ExtentI.Markup(ExtentColor.GREEN, "Widget successfully added and is visible on the Home Screen.");
            ExtentI.attachCatalinaLogsForSuccess();
            ExtentI.attachScreenShot();
        } else {
            currentNode.log(Status.FAIL, "Widget is not visible on the Home Screen.") ;
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        Log.methodExit(methodName);
    }


    public void performWidgetPersistenceCheck(String ParentCategory, String FromCategory, String PIN) {
        final String methodName = "performHomeAddWidgets";
        Log.methodEntry(methodName, ParentCategory, FromCategory, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        String graphType;
        CUHome.clickCUHomeHeading();
        removeGraphs();
        CUHome.clickAddWidget();
        
        int graphCount = CUHome.getNumberOfGraphTypes();
        if(graphCount<2) {
        	currentNode.log(Status.FAIL, "Graph options are not available in drop down list.") ;
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
            return;
        }
        
        CUHome.clickPopupCancelButton();
        CUHome.clickAddWidget();
        
        
        graphType = CUHome.getAndClickGraphName(1);
        CUHome.clickSaveWidget();
        CUHome.clickSaveButton();
        
        
        CUHome.clickAddWidget();
        CUHome.getAndClickGraphName(2);
        
        CUHome.clickSaveWidget();
        CUHome.clickSaveButton();
        Boolean widgetPresent = CUHome.checkWidget(graphType);
        
        CUHome.clickProfileButton();
        CUHome.clickLogoutButton();
        
        
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);

        CUHome.spinnerWait();
        
        widgetPresent = CUHome.checkWidget(graphType);
        
        removeGraphs();
        
        Boolean widgetPresent1 = CUHome.checkWidget(graphType);
        if (widgetPresent && !widgetPresent1) {
            ExtentI.Markup(ExtentColor.GREEN, "Widget successfully added and is visible on the Home Screen.");
            ExtentI.attachCatalinaLogsForSuccess();
            ExtentI.attachScreenShot();
        } else {
            currentNode.log(Status.FAIL, "Widget is not visible on the Home Screen.") ;
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        Log.methodExit(methodName);
    }
   
    public String getactualDateRange(String date, String format) {
    	final String methodName = "getactualDateRange";
        Log.methodEntry(methodName, date, format);
        
        format = format.split(" - ",-1)[0];
	    String[] fArr = format.split("/", -1);

	    String from = date.split(" - ",-1)[0].trim();
	    String to = date.split(" - ",-1)[1].trim();
	    
	    HashMap<String, String> fMap = new HashMap<>(), tMap = new HashMap<>();
	    
	    for(int i=0; i<fArr.length; i++){
	        if(fArr[i].indexOf('d')!=-1){
	            fMap.put("d", from.split("/",-1)[i].trim() );
	            tMap.put("d", to.split("/",-1)[i].trim() );
	        }
	        else if(fArr[i].indexOf('m')!=-1){
	            fMap.put("m", from.split("/",-1)[i].trim() );
	            tMap.put("m", to.split("/",-1)[i].trim() );
	        }
	        else if(fArr[i].indexOf('y')!=-1){
	            fMap.put("y", from.split("/",-1)[i].trim() );
	            tMap.put("y", to.split("/",-1)[i].trim() );
	        }
	    }
	    
	    String actualDateRange = fMap.get("d")+"/"+fMap.get("m")+" - "+tMap.get("d")+"/"+tMap.get("m");
	   
        Log.methodExit(methodName);
        return actualDateRange;
    }
    
    
    public void removeGraphs() {
    	int countWdgts;
    	boolean widgetPresent = CUHome.areGraphsPresent();
        if(widgetPresent)	countWdgts = CUHome.countWidgets();
        else countWdgts =0;
        
        for(int i=0; i<countWdgts; i++) {
            CUHome.clickSettingButton();
            CUHome.clickEditButton();
            CUHome.clickRemoveButton();
            CUHome.clickSaveButton();
        }
    }

    public void performDateValidationInCountWidget(String ParentCategory, String FromCategory, String PIN) {
        final String methodName = "performDateValidationInValueWidget";
        Log.methodEntry(methodName, ParentCategory, FromCategory, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        String graphType;
        CUHome.clickCUHomeHeading();
        removeGraphs();
        
        CUHome.clickAddWidget();
        
        int graphCount = CUHome.getNumberOfGraphTypes();
        if(graphCount<2) {
        	currentNode.log(Status.FAIL, "Graph options are not available in drop down list.") ;
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
            return;
        }
        
        CUHome.clickPopupCancelButton();
        CUHome.clickAddWidget();
        
        graphType = CUHome.getAndClickGraphName(1);
        CUHome.clickSaveWidget();
        CUHome.clickSaveButton();
        
        
        
        String date = CUHome.getDate();
        String format = CUHome.getDateFormat();
        
        String actualDateRange = getactualDateRange(date, format);
        
        CUHome.spinnerWait();// for spinner in graph widget
        CUHome.clickCountButton();
        String dateRangeInWidget = CUHome.getDateRangeOnWidget();
        
        boolean isDateSame = dateRangeInWidget.equals(actualDateRange);
        
        
        Boolean widgetPresent = CUHome.checkWidget(graphType);
        CUHome.spinnerWait();
             
        removeGraphs();
        Boolean widgetPresent1 = CUHome.checkWidget(graphType);
        if (widgetPresent && !widgetPresent1 && isDateSame) {
            ExtentI.Markup(ExtentColor.GREEN, "Widget date is same as in datepicker");
            ExtentI.attachCatalinaLogsForSuccess();
            ExtentI.attachScreenShot();
        } else {
            currentNode.log(Status.FAIL, "Widget date is not same as in datepicker") ;
            Log.info("datepicker date: "+actualDateRange);
            Log.info("widget date: "+dateRangeInWidget);
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        Log.methodExit(methodName);
    }
    
    public void performDateValidationInValueWidget(String ParentCategory, String FromCategory, String PIN) {
        final String methodName = "performDateValidationInValueWidget";
        Log.methodEntry(methodName, ParentCategory, FromCategory, PIN);
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        login.UserLogin(driver, "ChannelUser", ParentCategory, FromCategory);
        String graphType;
        CUHome.clickCUHomeHeading();
        removeGraphs();
        
        CUHome.clickAddWidget();
        
        int graphCount = CUHome.getNumberOfGraphTypes();
        if(graphCount<1) {
        	currentNode.log(Status.FAIL, "Graph options are not available in drop down list.") ;
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
            return;
        }
        
        CUHome.clickPopupCancelButton();
        CUHome.clickAddWidget();
        
        graphType = CUHome.getAndClickGraphName(1);
        CUHome.clickSaveWidget();
        CUHome.clickSaveButton();
        
        String date = CUHome.getDate();
        String format = CUHome.getDateFormat();
        
        String actualDateRange = getactualDateRange(date, format);
        
        
        CUHome.spinnerWait();// for spinner in graph widget
        String dateRangeInWidget = CUHome.getDateRangeOnWidget();
        
        boolean isDateSame = dateRangeInWidget.equals(actualDateRange);
        
        
        Boolean widgetPresent = CUHome.checkWidget(graphType);
        CUHome.spinnerWait();
                
        removeGraphs();
        Boolean widgetPresent1 = CUHome.checkWidget(graphType);
        if (widgetPresent && !widgetPresent1 && isDateSame) {
            ExtentI.Markup(ExtentColor.GREEN, "Widget date is same as in datepicker");
            ExtentI.attachCatalinaLogsForSuccess();
            ExtentI.attachScreenShot();
        } else {
            currentNode.log(Status.FAIL, "Widget date is not same as in datepicker") ;
            Log.info("datepicker date: "+actualDateRange);
            Log.info("widget date: "+dateRangeInWidget);
            ExtentI.attachCatalinaLogs();
            ExtentI.attachScreenShot();
        }
        Log.methodExit(methodName);
    }




}
