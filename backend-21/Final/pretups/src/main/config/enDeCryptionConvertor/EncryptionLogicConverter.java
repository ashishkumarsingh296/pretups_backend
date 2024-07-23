package enDeCryptionConvertor;

/**
* @(#)EncryptionLogicConverter
* Copyright(c) 2008, Bharti Telesoft Int. Public Ltd.
* All Rights Reserved
* This class is an utility Class for Pretups System to generate encryption key.
*-------------------------------------------------------------------------------------------------
* Author				Date			History
*-------------------------------------------------------------------------------------------------
* Sanjeev Sharma	   July 22,2008		Initial Creation
* ------------------------------------------------------------------------------------------------
*/

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.btsl.pretups.common.PretupsI;
import com.btsl.util.AESEncryptionUtil;
import com.btsl.util.AESKeyStore;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.CryptoUtil;
import com.btsl.util.OneWayHashingAlgoUtil;

public class EncryptionLogicConverter {
	static Log _log;
    public static void main(String[] arg) {
	    try
		{
	    	String filePath=getFilePath(EncryptionLogicConverter.class);
			filePath=filePath.substring(0,filePath.lastIndexOf(File.separatorChar) );
			_log = LogFactory.getLog(EncryptionLogicConverter.class.getName());
			
			File constantsFile = new File(filePath+File.separatorChar+"ConfigFile.props");
			_log.debug("@@@@@"+constantsFile.getAbsolutePath());
			if(!constantsFile.exists())
			{
				_log.debug("EncryptionLogicConverter"+" ConfigFile.props File Not Found .............");
				return;
			}
			
			File logconfigFile = new File(filePath+File.separatorChar+"enDeCryptionLogicLog.props");
			_log.debug("@@@@@"+logconfigFile.getAbsolutePath());
			if(!logconfigFile.exists())
			{
				_log.debug("EncryptionLogicConverter"+" Logconfig File Not Found .............");
				return;
			}
			Constants.load(constantsFile.getAbsolutePath());
			org.apache.log4j.PropertyConfigurator.configure(logconfigFile.getAbsolutePath());
			
			if("SHA-2".equals(Constants.getProperty("encryption_level_from")))
			{
				System.out.println("Value of encryption_level_from can not be SHA-2");
				return;
			}
			
			if("AES".equals(Constants.getProperty("encryption_level_from")) || "AES".equals(Constants.getProperty("encryption_level_to")))	
				loadKeyStore();
		
			ConverterEncryptionType();
		}//end of try
		catch(Exception e)
		{
			_log.debug(" Error in Loading Files ...........................: "+e.getMessage());
			e.printStackTrace();
			return;
		}// end of catch
		finally
		{
			_log.debug("main Exiting..... ");
		}
    }
   
    private static void loadKeyStore() throws Exception {

    	AESKeyStore aesKeyStore = new AESKeyStore();
		boolean credentialsLoad=false;
		String filePath=getFilePath(ConfigServlet.class);
		filePath=filePath.substring(0,filePath.lastIndexOf(File.separatorChar) );
		credentialsLoad=aesKeyStore.LoadStoreKeyCredentials(filePath+File.separatorChar+"Credentials.txt");
		if(credentialsLoad == false || AESKeyStore.getKey() == null )
			throw new Exception("Unable to load Encryption keyLoad");
	}

	private static void ConverterEncryptionType() 
    {
		long startTime=System.currentTimeMillis();
		if(_log.isDebugEnabled())_log.debug("main Exiting..... ");
		_log.debug("ConverterEncryptionType Entered for encryption from "+Constants.getProperty("encryption_level_from")+"-->"+Constants.getProperty("encryption_level_to"));
        Connection con=null;
        try{
            Class.forName ("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e1) {
        	_log.debug("ConverterEncryptionType Database Connection Fail");
            e1.printStackTrace();
        }
        _log.debug("@@@@@"+Constants.getProperty("datasource")+"::"+Constants.getProperty("username")+"::"+Constants.getProperty("password"));
        try{
            con = DriverManager.getConnection(Constants.getProperty("datasource"),Constants.getProperty("username"),Constants.getProperty("password"));
            //con = DriverManager.getConnection("jdbc:oracle:thin:@172.16.1.109:1521:PRTP","pretups_ocitz","pretups_ocitz");
        } catch (SQLException e2) {
        	_log.debug("ConverterEncryptionType Database Connection Fail");
            e2.printStackTrace();
        }        
        
        try {
            loadAndUpdateReqMessageGateway(con);
			loadAndUpdateResMessageGateway(con);
			loadAndUpdatePasswords(con);
			loadAndUpdatePins(con);
			if(! "SHA-2".equals(Constants.getProperty("encryption_level_to")))
				loadAndUpdateVomsVouchers(con);
			loadAndUpdateP2PSubscribers(con);
			Thread.sleep(10000);
        } catch (Exception e) {
			e.printStackTrace();
			_log.debug("loadUserPasswords Error::"+e);
		}
		finally {
			try{if (con != null)con.close();}catch (Exception e){}
			_log.debug("Total Time"+(System.currentTimeMillis()-startTime)/60000+"minutes");
		}
    }

    private static void loadAndUpdatePasswords(Connection p_con) {
        long startTime=System.currentTimeMillis();
        _log.debug("loadUserPasswords :: Start selecting & updating users informations with Start Time"+startTime);
        PreparedStatement pstmtSelect = null,pstmtUpdate=null;
        ResultSet rs = null;
        String user_id=null;
        String oldPassword=null;
        String newPassword=null;
       
        StringBuffer strSelectBuff = new StringBuffer("SELECT U.USER_ID,U.PASSWORD");
        strSelectBuff.append(" FROM USERS U WHERE STATUS<>'N' AND ENCRYPTION_DONE<>'Y'"); 
                
        StringBuffer strUpdateBuff = new StringBuffer("UPDATE USERS SET PASSWORD=?,ENCRYPTION_DONE=?");
        strUpdateBuff.append("WHERE USERS.user_id = ?");
        try
        {
            pstmtSelect = p_con.prepareStatement(strSelectBuff.toString());
            pstmtUpdate = p_con.prepareStatement(strUpdateBuff.toString());
            rs = pstmtSelect.executeQuery();
            while (rs.next())
            {	
                int i=0;
                user_id=rs.getString("user_id");
                oldPassword=rs.getString("password");
                newPassword=encryptText(decryptText(oldPassword));
                if(newPassword==null)
                	continue;
                pstmtUpdate.setString(++i,newPassword);
                pstmtUpdate.setString(++i,PretupsI.YES);
                pstmtUpdate.setString(++i,user_id);
	            if(pstmtUpdate.executeUpdate()<=0)
	            {
	                p_con.rollback();
	                throw new Exception();
	            }
                _log.debug("[UID "+user_id+ "]  [OrgPass "+decryptText(oldPassword)+"]  [OldPass  "+oldPassword+"]  [NewPass "+newPassword+"]");
            }
        } catch (SQLException sqe)
        {
            sqe.printStackTrace();
            _log.debug("loadAndUpdatePasswords Error::"+sqe.getMessage());
        } catch (Exception ex)
        {
            ex.printStackTrace();
            _log.debug("loadAndUpdatePasswords Error::"+ex.getMessage());
        } finally
        {
            try{if (rs != null){rs.close();}} catch (Exception e){}
            try{if (pstmtSelect != null){pstmtSelect.close();}} catch (Exception e){e.printStackTrace();}
            try{if (pstmtUpdate != null){pstmtUpdate.close();}} catch (Exception e){e.printStackTrace();}
            try {p_con.commit();} catch (SQLException e1) {e1.printStackTrace();}
            _log.debug("Updated users informations with successfully");
            
        }
    }
    
    private static void loadAndUpdatePins(Connection p_con) {
        long startTime=System.currentTimeMillis();
            _log.debug("loadAndUpdatePins :: Start selecting & updating users Pin ");
        
        PreparedStatement pstmtSelect = null,pstmtUpdate=null;
        ResultSet rs = null;
        String user_phone_id=null;
        String oldPIN=null;
        String newPIN=null;
        
        StringBuffer strSelectPinsBuff = new StringBuffer("SELECT UP.USER_PHONES_ID,UP.SMS_PIN ");
        strSelectPinsBuff.append("FROM USER_PHONES UP, USERS U ");  
        strSelectPinsBuff.append("WHERE U.USER_ID=UP.USER_ID AND U.STATUS<>'N' AND UP.ENCRYPTION_DONE<>'Y'");
        
        StringBuffer strUpdatePinBuff = new StringBuffer("UPDATE USER_PHONES SET SMS_PIN=?,ENCRYPTION_DONE=?");
        strUpdatePinBuff.append("WHERE USER_PHONES_ID =?");
        
        try
        {
            pstmtSelect = p_con.prepareStatement(strSelectPinsBuff.toString());
            pstmtUpdate = p_con.prepareStatement(strUpdatePinBuff.toString());
            rs = pstmtSelect.executeQuery();
           while (rs.next())
            {
                int i=0;
                user_phone_id=rs.getString("USER_PHONES_ID");
                oldPIN=rs.getString("SMS_PIN");
                newPIN=encryptText(decryptText(oldPIN));
                if(newPIN==null)
                	continue;
                pstmtUpdate.setString(++i,newPIN);
                pstmtUpdate.setString(++i,PretupsI.YES);
                pstmtUpdate.setString(++i,user_phone_id);
	            if(pstmtUpdate.executeUpdate()<=0)
	            {
	                p_con.rollback();
	                throw new Exception();
	            }
                    _log.debug(++i +"[UPID "+user_phone_id+ "]  [OrgPin"+decryptText(oldPIN)+"]  [OldPss "+oldPIN+"]  [NewPass "+newPIN+"]");
            }
        } catch (SQLException sqe)
        {
            sqe.printStackTrace();
            _log.debug("loadAndUpdatePins Error::"+sqe.getMessage());
        } catch (Exception ex)
        {
            ex.printStackTrace();
            _log.debug("loadAndUpdatePins Error::"+ex.getMessage());
        } finally
        {
            try{if (rs != null){rs.close();}} catch (Exception e){}
            try{if (pstmtSelect != null){pstmtSelect.close();}} catch (Exception e){e.printStackTrace();}
            try{if (pstmtUpdate != null){pstmtUpdate.close();}} catch (Exception e){e.printStackTrace();}
            try {p_con.commit();} catch (SQLException e1) {e1.printStackTrace();}
            _log.debug("loadAndUpdatePins :: Updated users Pin with successfully ");
        }
    }
    
    private static void loadAndUpdateReqMessageGateway(Connection p_con) {
        
            _log.debug("\n\n loadAndUpdateMessageGateway :: Start selecting & updating REQ MESSAGE GATEWAY informations ");
        PreparedStatement pstmtSelect = null,pstmtUpdate=null;
        ResultSet rs = null;
        String gatewayCode=null;
        String oldPass=null;
        String newPass=null;
        int j=0;
        
        StringBuffer strSelectReqGtwBuff = new StringBuffer("SELECT GATEWAY_CODE,PASSWORD FROM  REQ_MESSAGE_GATEWAY WHERE ENCRYPTION_DONE<>'Y'");
        StringBuffer strUpdateReqGtwBuff = new StringBuffer("UPDATE REQ_MESSAGE_GATEWAY set PASSWORD=? ,ENCRYPTION_DONE=? where GATEWAY_CODE=?");
        
        try
        {
            pstmtSelect = p_con.prepareStatement(strSelectReqGtwBuff.toString());
            pstmtUpdate = p_con.prepareStatement(strUpdateReqGtwBuff.toString());
            rs = pstmtSelect.executeQuery();
            while (rs.next())
            {
                int i=0;
                gatewayCode=rs.getString("GATEWAY_CODE");
                oldPass=rs.getString("PASSWORD");
                newPass=encryptText(decryptText(oldPass));
                if(newPass==null)
                	continue;
                pstmtUpdate.setString(++i,newPass);
                pstmtUpdate.setString(++i,PretupsI.YES);
                pstmtUpdate.setString(++i,gatewayCode);
	            if(pstmtUpdate.executeUpdate()<=0)
	            {
	                p_con.rollback();
	                throw new Exception();
	            }
                	_log.debug("[gatewayCode="+gatewayCode+ "]  [OrgPass="+decryptText(oldPass)+"]  [oldPass="+oldPass+"]  [newPass="+newPass+"]");
            }
          } catch (SQLException sqe)
            {
            sqe.printStackTrace();
            _log.debug("loadAndUpdateReqMessageGateway Error::"+sqe.getMessage());
             } catch (Exception ex)
            {
            ex.printStackTrace();
            _log.debug("loadAndUpdateReqMessageGateway Error::"+ex.getMessage());
            } finally
            {
	            try{if (rs != null){rs.close();}} catch (Exception e){}
	            try{if (pstmtSelect != null){pstmtSelect.close();}} catch (Exception e){e.printStackTrace();}
	            try{if (pstmtUpdate != null){pstmtUpdate.close();}} catch (Exception e){e.printStackTrace();}
	            try {p_con.commit();} catch (SQLException e1) {e1.printStackTrace();}
	            _log.debug("loadAndUpdateMessageGateway :: Exitinf After Updating REQ MESSAGE GATEWAY informations");
            }
    }
    
    private static void loadAndUpdateResMessageGateway(Connection p_con) {
            _log.debug("\n\n loadAndUpdateMessageGateway :: Start selecting & updating RES MESSAGE GATEWAY informations");
        PreparedStatement pstmtSelect = null,pstmtUpdate=null;
        ResultSet rs = null;
        String gatewayCode=null;
        String oldPass=null;
        String newPass=null;
        
        StringBuffer strSelectReqGtwBuff = new StringBuffer("SELECT GATEWAY_CODE,PASSWORD FROM  RES_MESSAGE_GATEWAY WHERE ENCRYPTION_DONE<>'Y'");
        StringBuffer strUpdateReqGtwBuff = new StringBuffer("UPDATE RES_MESSAGE_GATEWAY SET PASSWORD=?,ENCRYPTION_DONE=? WHERE GATEWAY_CODE=?");
        
        try
        {
            pstmtSelect = p_con.prepareStatement(strSelectReqGtwBuff.toString());
            pstmtUpdate = p_con.prepareStatement(strUpdateReqGtwBuff.toString());
            rs = pstmtSelect.executeQuery();
            while(rs.next())
            {
                int i=0;
                gatewayCode=rs.getString("GATEWAY_CODE");
                oldPass=rs.getString("PASSWORD");
                newPass=encryptText(decryptText(oldPass));
                if(newPass==null)
                	continue;
                pstmtUpdate.setString(++i,newPass);
                pstmtUpdate.setString(++i,PretupsI.YES);
                pstmtUpdate.setString(++i,gatewayCode);
                
	            if(pstmtUpdate.executeUpdate()<=0)
	            {
	                p_con.rollback();
	                throw new Exception();
	            }
                    _log.debug("[gatewayCode="+gatewayCode+ "]  [OrgPass="+decryptText(oldPass)+"]  [oldPass="+oldPass+"]  [newPass="+newPass+"]");
            }
        } catch (SQLException sqe)
        {
            sqe.printStackTrace();
            _log.debug("loadAndUpdateResMessageGateway Error::"+sqe.getMessage());
        } catch (Exception ex)
        {
            ex.printStackTrace();
            _log.debug("loadAndUpdateResMessageGateway Error::"+ex.getMessage());
        } finally
        {
            try{if (rs != null){rs.close();}} catch (Exception e){}
            try{if (pstmtSelect != null){pstmtSelect.close();}} catch (Exception e){e.printStackTrace();}
            try{if (pstmtUpdate != null){pstmtUpdate.close();}} catch (Exception e){e.printStackTrace();}
            try {p_con.commit();} catch (SQLException e1) {e1.printStackTrace();}
            _log.debug("loadAndUpdateMessageGateway :: Exited After Updating RES MESSAGE GATEWAY ");
        }
    }
    
    private static void loadAndUpdateP2PSubscribers(Connection p_con) {
            _log.debug("loadAndUpdateP2PSubscribers :: Start selecting & updating P2P users informations ");
        PreparedStatement pstmtSelect = null,pstmtUpdate=null;
        ResultSet rs = null;
        String user_id=null;
        String oldPin=null;
        String newPin=null;
        
        StringBuffer strSelectBuff = new StringBuffer("SELECT USER_ID,PIN FROM P2P_SUBSCRIBERS WHERE ENCRYPTION_DONE<>'Y'");
        StringBuffer strUpdateBuff = new StringBuffer("UPDATE P2P_SUBSCRIBERS SET PIN=?,ENCRYPTION_DONE=? WHERE USER_ID=?");
        try
        {
            pstmtSelect = p_con.prepareStatement(strSelectBuff.toString());
            pstmtUpdate = p_con.prepareStatement(strUpdateBuff.toString());
            rs = pstmtSelect.executeQuery();
            while (rs.next())
            {	
                int i=0;
                user_id=rs.getString("USER_ID");
                oldPin=rs.getString("PIN");
                newPin=encryptText(decryptText(oldPin));
                if(newPin==null)
                	continue;
                pstmtUpdate.setString(++i,newPin);
                pstmtUpdate.setString(++i,PretupsI.YES);
                pstmtUpdate.setString(++i,user_id);
	            if(pstmtUpdate.executeUpdate()<=0)
	            {
	                p_con.rollback();
	                throw new Exception();
	            }
                _log.debug("[UID="+user_id+ "]  [OrgPin "+decryptText(oldPin)+"]  [oldPin  "+oldPin+"]  [newPin "+newPin+"]");
            }
        } catch (SQLException sqe)
        {
            sqe.printStackTrace();
            _log.debug("loadAndUpdateP2PSubscribers Error::"+sqe.getMessage());
        } catch (Exception ex)
        {
            ex.printStackTrace();
            _log.debug("loadAndUpdateP2PSubscribers Error::"+ex.getMessage());
        } finally
        {
            try{if (rs != null){rs.close();}} catch (Exception e){}
            try{if (pstmtSelect != null){pstmtSelect.close();}} catch (Exception e){e.printStackTrace();}
            try{if (pstmtUpdate != null){pstmtUpdate.close();}} catch (Exception e){e.printStackTrace();}
            try {p_con.commit();} catch (SQLException e1) {e1.printStackTrace();}
            _log.debug("Updated P2P users informations with successfully");
        }
    }
    
    private static void loadAndUpdateVomsVouchers(Connection p_con) {
            _log.debug("loadAndUpdateVomsVouchers :: Start selecting & updating VOMS informations ");
        PreparedStatement pstmtSelect = null,pstmtUpdate=null;
        ResultSet rs = null;
        String serialNo=null;
        String oldPin=null;
        String newPin=null;
       
        StringBuffer strSelectBuff = new StringBuffer("SELECT SERIAL_NO,PIN_NO FROM VOMS_VOUCHERS WHERE ENCRYPTION_DONE<>'Y'");
        StringBuffer strUpdateBuff = new StringBuffer("UPDATE VOMS_VOUCHERS SET PIN_NO=?,ENCRYPTION_DONE=? WHERE SERIAL_NO=?");
        try
        {
            pstmtSelect = p_con.prepareStatement(strSelectBuff.toString());
            pstmtUpdate = p_con.prepareStatement(strUpdateBuff.toString());
            rs = pstmtSelect.executeQuery();
            while (rs.next())
            {	
                int i=0;
                serialNo=rs.getString("SERIAL_NO");
                oldPin=rs.getString("PIN_NO");
                newPin=encryptText(decryptText(oldPin));
                if(newPin==null)
                	continue;
                pstmtUpdate.setString(++i,newPin);
                pstmtUpdate.setString(++i,PretupsI.YES);
                pstmtUpdate.setString(++i,serialNo);
	            if(pstmtUpdate.executeUpdate()<=0)
	            {
	                p_con.rollback();
	                throw new Exception();
	            }
                _log.debug("[SerialNo "+serialNo+ "]  [OrgPin "+decryptText(oldPin)+"]  [oldPin  "+oldPin+"]  [newPin "+newPin+"]");
            }
        } catch (SQLException sqe)
        {
            sqe.printStackTrace();
            _log.debug("loadAndUpdateVomsVouchers Error::"+sqe.getMessage());
        } catch (Exception ex)
        {
            ex.printStackTrace();
            _log.debug("loadAndUpdateVomsVouchers Error::"+ex.getMessage());
        } finally
        {
            try{if (rs != null){rs.close();}} catch (Exception e){}
            try{if (pstmtSelect != null){pstmtSelect.close();}} catch (Exception e){e.printStackTrace();}
            try{if (pstmtUpdate != null){pstmtUpdate.close();}} catch (Exception e){e.printStackTrace();}
            try {p_con.commit();} catch (SQLException e1) {e1.printStackTrace();}
            _log.debug("Updated VOMS informations with successfully ");
        }
    }
   
    public static String decryptText(String p_text)
	{
    	String decPass = null;
    	
	    try
		{
			if("AES".equals(Constants.getProperty("encryption_level_from")))
			{
				AESEncryptionUtil bex = new AESEncryptionUtil();
				decPass = bex.DecryptAES(p_text);
			}else if("DES".equals(Constants.getProperty("encryption_level_from")))
				decPass =  new CryptoUtil().decrypt(p_text,Constants.KEY);
			else if("SHA-2".equals(Constants.getProperty("encryption_level_from")))
				decPass = null;
			
			return decPass;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			_log.debug("decryptText Exception e="+e.getMessage());
			return null;
		}
    }// end method
	/**
	 * Encrypts the passed text string using an encryption key
	 * @param password
	 * @return String
	 */
	public	static String encryptText(String p_text)
	{
		String encPass = null;
	    try
	        {
	            if("AES".equals(Constants.getProperty("encryption_level_to")))
	            {
	            	AESEncryptionUtil bex = new AESEncryptionUtil();
	            	encPass = bex.EncryptAES(p_text);
	            }
	            else if("DES".equals(Constants.getProperty("encryption_level_to")))
	            	encPass = new CryptoUtil().encrypt(p_text,Constants.KEY);
	            else if("SHA-2".equals(Constants.getProperty("encryption_level_to")))
	            	encPass = OneWayHashingAlgoUtil.getInstance().encrypt(p_text);
	            
	            return encPass;
	        }
	        catch(Exception e)
	        {
	        	e.printStackTrace();
	        	_log.debug("encryptText Exception e="+e.getMessage());
	        	return null;
	        }
	 }
	
	public static String getFilePath(Class cls)
	{
		if( cls == null )
			return null ;
		String name = cls.getName().replace( '.' , '/' ) ;
		URL loc = cls.getResource( "/" + name + ".class" ) ;
		File f = new File( loc.getFile() ) ;
		// Class file is inside a jar file.
		if( f.getPath().startsWith( "file:" ) ) {
			String s = f.getPath() ;
			int index = s.indexOf( '!' ) ;
			// It confirm it is a jar file
			if( index != -1 ) {
				f = new File( s.substring( 5 ).replace( '!' , File.separatorChar ) ) ;
				return f.getPath() ;
			}
		}
		try {
			f = f.getCanonicalFile() ;
		}catch( IOException ioe ) {
			ioe.printStackTrace() ;
			_log.debug("getFilePath"+ioe.getMessage());
			return null ;
		}
		return f.getPath() ;
	}
}