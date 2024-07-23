package common_util_script;
import java.util.concurrent.TimeUnit;

public class Time_Wait {

			public static boolean timewait (long time) {
			try{
			Launchdriver.driver.manage().timeouts().implicitlyWait(time, TimeUnit.SECONDS);
			
			} catch (Exception e){
				return false ;
						}
			return true;
		}
	}

