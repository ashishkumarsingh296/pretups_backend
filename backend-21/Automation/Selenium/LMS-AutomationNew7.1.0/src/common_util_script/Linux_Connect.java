package common_util_script;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class Linux_Connect {

	
		
	
		
	 public static String serverConn(String command) throws IOException  {
		 
		 
		 String values_temp = null;

		 //System.out.println("buildpath is " + com.ussdreadPropertiesFile.ReadPropertiesFile.properties.getProperty("BuildID"));

		   // Create FileInputStream Object  to read the credentials 
	 		 FileInputStream fileInput = new FileInputStream(new File("dataFile.properties"));  
	 	    // Create Properties object  to read the credentials
	 		 Properties prop = new Properties();  
	 	   //load properties file  to read the credentials
	 		 prop.load(fileInput);  
	 	 
		 
		 
		    try {

		      java.util.Properties config = new java.util.Properties();
		      config.put("StrictHostKeyChecking", "no");
		      JSch jsch = new JSch();
		      Session session = jsch.getSession("pretups_mali", "172.16.10.43", 22);
		      session.setPassword("pretups_mali");
		      session.setConfig(config);
		      session.connect();
		      System.out.println("Connected");
		      Channel channel = session.openChannel("exec");
		      
		      System.out.println("Your command is : " + command);
		      ((ChannelExec) channel).setCommand(command);
		      channel.setInputStream(null);
		      ((ChannelExec) channel).setErrStream(System.err);

		      InputStream in = channel.getInputStream();
		      channel.connect();
		      byte[] tmp = new byte[1024];
		      while (true) {
		        while (in.available() > 0) {
		          int i = in.read(tmp, 0, 1024);
		          if (i < 0)
		            break;
		          values_temp = new String(tmp, 0, i);
		          logs.info("values are: "+values_temp);
		          
		          }
		        if (channel.isClosed()) {
		          System.out.println("exit-status: " + channel.getExitStatus());
		          break;
		        }
		        try {
		          Thread.sleep(1000);
		        } catch (Exception ee) {
		        }
		      }
		      channel.disconnect();
		      session.disconnect();
		      System.out.println("done");
		     
		    } catch (Exception e) {
		      System.err.println("Exception is" + e);
		      
		    }
			return values_temp;

		  }

}