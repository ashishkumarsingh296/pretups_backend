package common_util_script;

import java.util.Iterator;
import java.util.Set;

public class Switchwindow {
	
	private static String homepage1 = Launchdriver.driver.getWindowHandle();

	public static boolean windowhandle () {
		try{
		homepage1 = Launchdriver.driver.getWindowHandle();
		Set<String> windows = Launchdriver.driver.getWindowHandles();
		Iterator iterator =windows.iterator();
		String currentWindowID;
		while(iterator.hasNext()){
			currentWindowID = iterator.next().toString();
			if(!currentWindowID.equals(homepage1));
			Launchdriver.driver.switchTo().window(currentWindowID);
		}
		}catch (Exception e){
		return false;
	}
	return true;
	}
	
	public static boolean windowhandleclose () {
		try{        
			//bringing the control back to the main window
			Launchdriver.driver.switchTo().window(homepage1);
			System.out.println("page switched");
			//Launchdriver.driver.close();

		}catch (Exception e){
		return false;
	}
	return true;
	}
}
