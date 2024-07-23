package testcases;

import java.io.IOException;

import org.testng.annotations.Test;

import common_util_script.ExtentReportMultipleClasses;

public class Runlmsputtyscript extends ExtentReportMultipleClasses {

	@Test
	public void script() throws IOException{
		 test = extent.createTest("Script: LMS scripts ", "Script should be run successfully from backend");
		//common_util_script.Linux_Connect.serverConn("sh /pretups7_ansible/tomcat8_web/webapps/pretups/WEB-INF/pretups_scripts/c2sMISProcessNew.sh>>c2sMISProcess30_AUG.txt");
		//common_util_script.Linux_Connect.serverConn("sh /pretups7_ansible/tomcat8_web/webapps/pretups/WEB-INF/pretups_scripts/runLMSReferenceTargetCalculation.sh>>runLMSReferenceTargetCalculation_29aug.txt");
		//common_util_script.Linux_Connect.serverConn("sh /pretups7_ansible/tomcat8_web/webapps/pretups/WEB-INF/pretups_scripts/runLMSPromotionMessages.sh");
		//common_util_script.Linux_Connect.serverConn("sh /pretups7_ansible/tomcat8_web/webapps/pretups/WEB-INF/pretups_scripts/runLMSForTargetCredit.sh");
		//common_util_script.Linux_Connect.serverConn("sh /pretups7_ansible/tomcat8_web/webapps/pretups/WEB-INF/pretups_scripts/runLMSProintsRedemption.sh");
		common_util_script.Linux_Connect.serverConn("sh /pretups7_ansible/tomcat8_web/webapps/pretups/WEB-INF/pretups_scripts/LMSProgressiveMessage.sh");
		//common_util_script.Linux_Connect.serverConn("sh /pretups7_ansible/tomcat8_web/webapps/pretups/WEB-INF/pretups_scripts/LMSExpiryPoint.sh");/*out of scope.not in use*/

	}
}
