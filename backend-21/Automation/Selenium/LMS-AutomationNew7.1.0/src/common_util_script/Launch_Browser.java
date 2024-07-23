package common_util_script;

public class Launch_Browser {
	
	public static boolean launch (String url){
		try {			
			Launchdriver.driver.get(url);
			System.out.println("Browser launched successfully");
			System.out.println("now maximizing window size");
			Launchdriver.driver.manage().window().maximize();
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("URL is not valid");
			return false;
		}
			return true;
	}

}
