package testcases;

import java.io.IOException;

import org.testng.annotations.Test;

import common_util_script.ExtentReportMultipleClasses;

public class Runlmsputtyscript extends ExtentReportMultipleClasses {

	@Test
	public void script() throws IOException{
		 test = extent.createTest("Script: LMS scripts ", "Script should be run successfully from backend");
		common_util_script.Linux_Connect.serverConn("sh /home/pretups_mali/tomcat7_web/webapps/pretups/WEB-INF/pretups_scripts/c2sMISProcessNew.sh>>c2sMISProcessNew20june.txt");
		common_util_script.Linux_Connect.serverConn("sh /tomcat7_web/webapps/pretups/WEB-INF/pretups_scripts/runLMSReferenceTargetCalculation.sh>>runLMSReferenceTargetCalculation20june.txt");
		//common_util_script.Linux_Connect.serverConn("sh /tomcat7_web/webapps/pretups/WEB-INF/pretups_scripts/runLMSPromotionMessages.sh");
		//common_util_script.Linux_Connect.serverConn("sh /tomcat7_web/webapps/pretups/WEB-INF/pretups_scripts/runLMSForTargetCredit.sh");
		//common_util_script.Linux_Connect.serverConn("sh /tomcat7_web/webapps/pretups/WEB-INF/pretups_scripts/runLMSProintsRedemption.sh");
		//common_util_script.Linux_Connect.serverConn("sh /tomcat7_web/webapps/pretups/WEB-INF/pretups_scripts/LMSProgressiveMessage.sh");
		//common_util_script.Linux_Connect.serverConn("sh /tomcat7_web/webapps/pretups/WEB-INF/pretups_scripts/LMSExpiryPoint.sh");

	}
}
