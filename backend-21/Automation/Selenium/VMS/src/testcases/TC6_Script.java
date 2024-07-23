package testcases;

import org.testng.annotations.Test;

public class TC6_Script {

	@Test(priority = 9)
	public static void run_script () throws Exception{
	
		System.out.println("My first testcase");
    	System.out.println("Running the Voucher Generator script on the linux server");
    	common_util_script.Linux_Connect.serverConn("pwd");
    	Thread.sleep(10000);
    	System.out.println("Script is executed successfully");
	}
}