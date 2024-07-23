/**
 * @(#)ChannelUserDAO.java
 *                         Copyright(c) 2005, Bharti Telesoft Ltd.
 *                         All Rights Reserved
 * 
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Gurjeet Bedi 5/08/2005 Initial Creation
 *                         Sandeep Goel 22/07/2006 Modification
 *                         Sandeep Goel 05/08/2006 Modification ID USD001updateChannelUserInfo
 *                         Ankit Zindal 20/11/2006 ChangeID=LOCALEMASTER
 *                         Harpreet Kaur 03/10/2011 Modification
 *                         Chhaya Sikheria 02/11/2011 Modification
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 */

package com.client.pretups.user.businesslogic; 

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.struts.upload.FormFile;
import org.springframework.stereotype.Repository;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * 
 */

@Repository
public class ChannelUserDAO {

	/**
	 * Field LOG.
	 */
	private static final Log LOG = LogFactory.getLog(ChannelUserDAO.class.getName());	
	
	public int addPgpChannelUser(Connection p_con,
            ChannelPgpUserVO p_channelPgpUserVO) throws BTSLBaseException {
		final String methodName = "addPgpChannelUser";
        PreparedStatement psmtInsert = null;
        int insertCount = 0;
        System.out.println("p_channelPgpUserVO$$$ "
                + p_channelPgpUserVO.getuserId()
                + p_channelPgpUserVO.getpgpIp()
                + p_channelPgpUserVO.getpgpPort());
        if (LOG.isDebugEnabled()) {
            LOG.debug("addPgpChannelUser", "Entered: p_channelPgpUserVO= "
                    + p_channelPgpUserVO);
        }
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff
                    .append("INSERT INTO PGP_DETAILS (user_id,ip,port,public_key,private_key,secret_key,user_name,password,filepath) ");
            strBuff.append(" VALUES (?,?,?,?,?,?,?,?,?)");
            String insertQuery = strBuff.toString();
            System.out.println("insertQuery in addPgpChannelUser "
                    + insertQuery);
            if (LOG.isDebugEnabled()) {
                LOG.debug("addChannelUser", "Query sqlInsert:" + insertQuery);
            }
            int rCount = 1;
            psmtInsert = p_con.prepareStatement(insertQuery);
            psmtInsert.setString(rCount++, p_channelPgpUserVO.getuserId());
            psmtInsert.setString(rCount++, p_channelPgpUserVO.getpgpIp());
            psmtInsert.setString(rCount++, p_channelPgpUserVO.getpgpPort());
            psmtInsert.setString(rCount++, p_channelPgpUserVO.getpgpEncryptKeyFileName());
            psmtInsert.setString(rCount++, p_channelPgpUserVO.getpgpDecryptKeyFileName());
            psmtInsert.setString(rCount++, p_channelPgpUserVO.getPassphrase());
            psmtInsert.setString(rCount++, p_channelPgpUserVO.getpgpUserName());
            psmtInsert.setString(rCount++, p_channelPgpUserVO.getpgpPassword());
            psmtInsert.setString(rCount++, p_channelPgpUserVO.getSftpPGPFilePath());
            insertCount = psmtInsert.executeUpdate();
        } // end of try
        catch (SQLException sqle) {
            LOG.error("addPgpChannelUser", "SQLException: "
                    + sqle.getMessage());
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
                    EventStatusI.RAISED, EventLevelI.FATAL,
                    "ChannelPgpUserDAO[addChannelUser]", "", "", "",
                    "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "addPgpChannelUser",
                    "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            LOG.error("addChannelUser", "Exception: " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
                    EventStatusI.RAISED, EventLevelI.FATAL,
                    "ChannelPgpUserDAO[addPgpChannelUser]", "", "", "",
                    "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "addPgpChannelUser",
                    "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtInsert != null) {
                    psmtInsert.close();
                }
            } catch (Exception e) {
            }
            if (LOG.isDebugEnabled())
                LOG.debug("addPgpChannelUser", "Exiting: insertCount="
                        + insertCount);
        } // end of finally
        return insertCount;
    }
    public ChannelPgpUserVO loadPgpChannelUserDetails(Connection p_con,
            String p_userid) throws BTSLBaseException {
    	final String methodName = "loadPgpChannelUserDetails";
        if (LOG.isDebugEnabled())
            LOG.debug("loadPgpChannelUserDetails", "Entered p_msisdn:"
                    + p_userid);
        PreparedStatement pstmtSelect = null;
        ChannelPgpUserVO channelPgpUserVO = null;
        System.out.println("%%%%%%%%%%%%%p_userid%%%%%%%%%%%%%" + p_userid);
        ResultSet rs = null;
        try {
            StringBuffer selectQueryBuff = new StringBuffer(
                    " select IP,PORT,PUBLIC_KEY,PRIVATE_KEY,USER_NAME,FILEPATH,SECRET_KEY,PASSWORD FROM PGP_DETAILS ");
            selectQueryBuff.append(" WHERE USER_ID=? ");
            String selectQuery = selectQueryBuff.toString();
            System.out.println("selectQuery^^^ " + selectQuery);
            if (LOG.isDebugEnabled())
                LOG.debug("loadPgpChannelUserDetails", "select query:"
                        + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_userid);
            if (LOG.isDebugEnabled())
                LOG.debug("loadPgpChannelUserDetails", "Before result:"
                        + p_userid);
            rs = pstmtSelect.executeQuery();
            if (LOG.isDebugEnabled())
                LOG.debug("loadPgpChannelUserDetails", "After result:"
                        + p_userid);
            if (rs.next()) {
                channelPgpUserVO = new ChannelPgpUserVO();
                channelPgpUserVO.setpgpIp(rs.getString("IP"));
                channelPgpUserVO.setpgpPort(rs.getString("PORT"));
                channelPgpUserVO.setSftpPGPFilePath(rs.getString("FILEPATH"));
                channelPgpUserVO.setPassphrase(rs.getString("SECRET_KEY"));
                channelPgpUserVO.setpgpEncryptKeyFileName(rs.getString("PUBLIC_KEY"));
                channelPgpUserVO.setpgpDecryptKeyFileName(rs.getString("PRIVATE_KEY"));
                channelPgpUserVO.setpgpUserName(rs.getString("USER_NAME"));
                channelPgpUserVO.setpgpPassword(rs.getString("PASSWORD"));
            }
            return channelPgpUserVO;
        }// end of try
        catch (SQLException sqle) {
            LOG.error("loadChannelUserDetails", "SQLException "
                    + sqle.getMessage());
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
                    EventStatusI.RAISED, EventLevelI.FATAL,
                    "ChannelChannelUserDAO[loadChannelUserDetails]", "", "",
                    "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loadPgpChannelUserDetails",
                    "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error("loadChannelUserDetails", "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
                    EventStatusI.RAISED, EventLevelI.FATAL,
                    "ChannelChannelUserDAO[loadPgpChannelUserDetails]", "", "",
                    "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadPgpChannelUserDetails",
                    "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            if (LOG.isDebugEnabled())
                LOG.debug("loadPgpChannelUserDetails",
                        "Exiting channelPgpUserVO:" + "");
        }// end of finally
    }
    public int updatePgpChannelUser(Connection p_con,
            ChannelPgpUserVO p_channelPgpUserVO) throws BTSLBaseException {
    	final String methodName = "updatePgpChannelUser";
        PreparedStatement psmtUpdate = null;
        int updateCount = 0;
        System.out.println("p_channelPgpUserVO$$$ "
                + p_channelPgpUserVO.getuserId() + " ## "
                + p_channelPgpUserVO.getpgpIp() + " ## "
                + p_channelPgpUserVO.getpgpPort() + " ## "
                + p_channelPgpUserVO.getpgpEncryptKeyFileName() + " ## "
                + p_channelPgpUserVO.getpgpDecryptKeyFileName() + " ## "
                + p_channelPgpUserVO.getPassphrase() + " ## "
                + p_channelPgpUserVO.getpgpUserName() + " ## "
                + p_channelPgpUserVO.getpgpPassword());
        if (LOG.isDebugEnabled()) {
            LOG.debug("updatePgpChannelUser", "Entered: p_channelPgpUserVO= "
                    + p_channelPgpUserVO);
        }
        try {
            StringBuffer strBuff = new StringBuffer();
            if(BTSLUtil.isNullString(p_channelPgpUserVO.getpgpEncryptKeyFileName()) && BTSLUtil.isNullString(p_channelPgpUserVO.getpgpDecryptKeyFileName()))
            strBuff.append("UPDATE PGP_DETAILS SET ip=?,port=?, filepath=?,secret_key=?,user_name=?,password=?  ");
            else if(BTSLUtil.isNullString(p_channelPgpUserVO.getpgpEncryptKeyFileName()))
            strBuff.append("UPDATE PGP_DETAILS SET ip=?,port=?, filepath=?,secret_key=?,private_key=?,user_name=?,password=?  ");
            else if(BTSLUtil.isNullString(p_channelPgpUserVO.getpgpDecryptKeyFileName()))
            strBuff.append("UPDATE PGP_DETAILS SET ip=?,port=?, filepath=?,secret_key=?,public_key =?,user_name=?,password=?  ");
            else
            strBuff.append("UPDATE PGP_DETAILS SET ip=?,port=?, filepath=?,secret_key=?,public_key=?,private_key=?,user_name=?,password=?  ");
            strBuff.append(" WHERE user_id=?");
            String updateQuery = strBuff.toString();           
            if (LOG.isDebugEnabled()) {LOG.debug("updatePgpChannelUser", "Query sqlUpdate:"+ updateQuery);
            }
            int rCount = 1;
            psmtUpdate = p_con.prepareStatement(updateQuery);
            psmtUpdate.clearParameters();
            if(BTSLUtil.isNullString(p_channelPgpUserVO.getpgpEncryptKeyFileName()) && BTSLUtil.isNullString(p_channelPgpUserVO.getpgpDecryptKeyFileName()))
            {
            	psmtUpdate.setString(rCount++, p_channelPgpUserVO.getpgpIp());
                psmtUpdate.setString(rCount++, p_channelPgpUserVO.getpgpPort());
                psmtUpdate.setString(rCount++, p_channelPgpUserVO.getSftpPGPFilePath());
                psmtUpdate.setString(rCount++, p_channelPgpUserVO.getPassphrase());
                psmtUpdate.setString(rCount++, p_channelPgpUserVO.getpgpUserName());
                psmtUpdate.setString(rCount++, p_channelPgpUserVO.getpgpPassword());
                psmtUpdate.setString(rCount++, p_channelPgpUserVO.getuserId());            	
            }
            else if(BTSLUtil.isNullString(p_channelPgpUserVO.getpgpEncryptKeyFileName()))
            {
            	psmtUpdate.setString(rCount++, p_channelPgpUserVO.getpgpIp());
                psmtUpdate.setString(rCount++, p_channelPgpUserVO.getpgpPort());
                psmtUpdate.setString(rCount++, p_channelPgpUserVO.getSftpPGPFilePath());
                psmtUpdate.setString(rCount++, p_channelPgpUserVO.getPassphrase());                
                psmtUpdate.setString(rCount++, p_channelPgpUserVO.getpgpDecryptKeyFileName());
                psmtUpdate.setString(rCount++, p_channelPgpUserVO.getpgpUserName());
                psmtUpdate.setString(rCount++, p_channelPgpUserVO.getpgpPassword());
                psmtUpdate.setString(rCount++, p_channelPgpUserVO.getuserId());            	
            }
            else if(BTSLUtil.isNullString(p_channelPgpUserVO.getpgpDecryptKeyFileName()))
            {
            	psmtUpdate.setString(rCount++, p_channelPgpUserVO.getpgpIp());
                psmtUpdate.setString(rCount++, p_channelPgpUserVO.getpgpPort());
                psmtUpdate.setString(rCount++, p_channelPgpUserVO.getSftpPGPFilePath());
                psmtUpdate.setString(rCount++, p_channelPgpUserVO.getPassphrase());
                psmtUpdate.setString(rCount++, p_channelPgpUserVO.getpgpEncryptKeyFileName());
                psmtUpdate.setString(rCount++, p_channelPgpUserVO.getpgpUserName());
                psmtUpdate.setString(rCount++, p_channelPgpUserVO.getpgpPassword());
                psmtUpdate.setString(rCount++, p_channelPgpUserVO.getuserId());            	
            }
            else
            {
            	psmtUpdate.setString(rCount++, p_channelPgpUserVO.getpgpIp());
                psmtUpdate.setString(rCount++, p_channelPgpUserVO.getpgpPort());
                psmtUpdate.setString(rCount++, p_channelPgpUserVO.getSftpPGPFilePath());
                psmtUpdate.setString(rCount++, p_channelPgpUserVO.getPassphrase());
                psmtUpdate.setString(rCount++, p_channelPgpUserVO.getpgpEncryptKeyFileName());
                psmtUpdate.setString(rCount++, p_channelPgpUserVO.getpgpDecryptKeyFileName());
                psmtUpdate.setString(rCount++, p_channelPgpUserVO.getpgpUserName());
                psmtUpdate.setString(rCount++, p_channelPgpUserVO.getpgpPassword());
                psmtUpdate.setString(rCount++, p_channelPgpUserVO.getuserId());            	
            }
            updateCount = psmtUpdate.executeUpdate();
        } // end of try
        catch (SQLException sqle) {
            LOG.error("updatePgpChannelUser", "SQLException: "
                    + sqle.getMessage());
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
                    EventStatusI.RAISED, EventLevelI.FATAL,
                    "ChannelPgpUserDAO[updateChannelUser]", "", "", "",
                    "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "updatePgpChannelUser",
                    "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            LOG.error("updateChannelUser", "Exception: " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
                    EventStatusI.RAISED, EventLevelI.FATAL,
                    "ChannelPgpUserDAO[updatePgpChannelUser]", "", "", "",
                    "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updatePgpChannelUser",
                    "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtUpdate != null) {
                    psmtUpdate.close();
                }
            } catch (Exception e) {
            }
            if (LOG.isDebugEnabled())
                LOG.debug("updatePgpChannelUser", "Exiting: updateCount="
                        + updateCount);
        } // end of finally
        return updateCount;
    }
    public static void delete(File file) throws java.io.IOException {
        if (file.isDirectory()) {
            if (file.list().length == 0) {
                file.delete();
                System.out.println("Directory is deleted : "
                        + file.getAbsolutePath());
            } else {
                String files[] = file.list();
                for (String temp : files) {
                    File fileDelete = new File(file, temp);
                    delete(fileDelete);
                }
                if (file.list().length == 0) {
                    file.delete();
                    System.out.println("Directory is deleted : "
                            + file.getAbsolutePath());
                }
            }
        } else {
            file.delete();
            System.out.println("File is deleted : " + file.getAbsolutePath());
        }
    }
    public String createEncryptPgpFile(FormFile encryptionFileName,
            String userid) {
    	final String methodName = "createEncryptPgpFile";
        try {
            String filePath = Constants.getProperty("PGP_FILE_PATH") + userid + "/" + "ENCRYPTION" + "/";
            String filePath1 = Constants.getProperty("PGP_FILE_PATH") + userid + "/" + "PGPENCRYPTED" + "/";
            System.out.println("createEncryptPgpFile :: "+filePath);
            File folder = new File(filePath);
            File folder1=new File(filePath1);
            if (folder.exists()) {
                try {
                    delete(folder);
                } catch (IOException e) {
                    LOG.errorTrace(methodName, e);
                    System.exit(0);
                }
            }
            if (!folder.exists()) {            	
                folder.mkdirs();                
            }
            if (folder1.exists()) {
                try {
                    delete(folder1);
                } catch (IOException e) {
                    LOG.errorTrace(methodName, e);
                    System.exit(0);
                }
            }
            if (!folder1.exists()) {            	
                folder1.mkdirs();                
            }
            FormFile encryptfile = encryptionFileName;
            String encryptFileName = encryptfile.getFileName();
            System.out.println("filePath : " + filePath+"dfgdf"+encryptFileName);
            if (!("").equals(encryptFileName)) {
                System.out.println("Server path:" + filePath);
                File newEncryptFile = new File(filePath, encryptFileName);
                if (!newEncryptFile.exists()) {
                    FileOutputStream fos = new FileOutputStream(newEncryptFile);
                    fos.write(encryptfile.getFileData());
                    fos.flush();
                    fos.close();
                }
            }
        } catch (IOException ex) {
        }
        return "1";
    }
    public String createDecryptPgpFile(FormFile encryptionFileName,
            String userid) {
    	final String methodName = "createDecryptPgpFile";
        try {
            String filePath = Constants.getProperty("PGP_FILE_PATH") + userid + "/" + "DECRYPTION" + "/";
            File folder = new File(filePath);
            if (folder.exists()) {
                try {
                    delete(folder);
                } catch (IOException e) {
                    LOG.errorTrace(methodName, e);
                    System.exit(0);
                }
            }
            if (!folder.exists()) {
                folder.mkdirs();
            }
            FormFile encryptfile = encryptionFileName;
            String encryptFileName = encryptfile.getFileName();
            System.out.println("filePath : " + filePath);
            if (!("").equals(encryptFileName)) {
                System.out.println("Server path:" + filePath);
                File newEncryptFile = new File(filePath, encryptFileName);
                if (!newEncryptFile.exists()) {
                    FileOutputStream fos = new FileOutputStream(newEncryptFile);
                    fos.write(encryptfile.getFileData());
                    fos.flush();
                    fos.close();
                }
            }
        } catch (IOException ex) {
        }
        return "1";
    }
    public FormFile searchPgpFile(String userid) {
    	final String methodName = "searchPgpFile";
        FormFile encryptfile = null;
        try {
        	String filePath = Constants.getProperty("PGP_FILE_PATH") + userid + "/";
            File dir = new File(filePath);
            String[] children = dir.list();
            if (children == null) {
                System.out.println("does not exist or is not a directory");
            } else {
                for (int i = 0; i < children.length; i++) {
                    String filename = children[i];
                    System.out.println(filename);
                }
            }
        } catch (Exception ex) {
        	LOG.errorTrace(methodName, ex);
        }
        return encryptfile;
    }
    public int deletePgpChannelUser(Connection p_con, String p_userId)
            throws BTSLBaseException {
    	final String methodName = "deletePgpChannelUser";
        PreparedStatement psmtDelete = null;
        int deleteCount = 0;
        if (LOG.isDebugEnabled()) {
            LOG
                    .debug("deletePgpChannelUser", "Entered: p_userId= "
                            + p_userId);
        }
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append("DELETE FROM PGP_DETAILS ");
            strBuff.append(" WHERE user_id=?");
            String deleteQuery = strBuff.toString();
            System.out.println("deleteQuery in deletePgpChannelUser "
                    + deleteQuery);
            if (LOG.isDebugEnabled()) {
                LOG.debug("updatePgpChannelUser", "Query sqlUpdate:"
                        + deleteQuery);
            }
            int rCount = 1;
            psmtDelete = p_con.prepareStatement(deleteQuery);
            psmtDelete.clearParameters();
            psmtDelete.setString(rCount++, p_userId);
            deleteCount = psmtDelete.executeUpdate();
        } // end of try
        catch (SQLException sqle) {
            LOG.error("deletePgpChannelUser", "SQLException: "
                    + sqle.getMessage());
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
                    EventStatusI.RAISED, EventLevelI.FATAL,
                    "ChannelPgpUserDAO[deletePgpChannelUser]", "", "", "",
                    "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "deletePgpChannelUser",
                    "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            LOG.error("deletePgpChannelUser", "Exception: " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
                    EventStatusI.RAISED, EventLevelI.FATAL,
                    "ChannelPgpUserDAO[deletePgpChannelUser]", "", "", "",
                    "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "deletePgpChannelUser",
                    "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtDelete != null) {
                    psmtDelete.close();
                }
            } catch (Exception e) {
            }
            if (LOG.isDebugEnabled())
                LOG.debug("updatePgpChannelUser", "Exiting: updateCount="
                        + deleteCount);
        } // end of finally
        return deleteCount;
    }
    public int checkPgpUsers(Connection p_con, String p_userid)
            throws BTSLBaseException {
    	final String methodName = "checkPgpUsers";
        if (LOG.isDebugEnabled())
            LOG.debug("checkPgpUsers", "Entered p_msisdn:" + p_userid);
        int count = 0;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        try {
            StringBuffer selectQueryBuff = new StringBuffer(
                    " select count(*) FROM PGP_DETAILS ");
            selectQueryBuff.append(" WHERE USER_ID=? ");
            String selectQuery = selectQueryBuff.toString();
            System.out.println("selectQuery^^^ " + selectQuery);
            if (LOG.isDebugEnabled())
                LOG.debug("checkPgpUsers", "select query:" + selectQuery);
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_userid);
            if (LOG.isDebugEnabled())
                LOG.debug("checkPgpUsers", "Before result:" + p_userid);
            rs = pstmtSelect.executeQuery();
            if (LOG.isDebugEnabled())
                LOG.debug("checkPgpUsers", "After result:" + p_userid);
            if (rs.next()) {
                count = rs.getInt(1);
            }
            return count;
        }// end of try
        catch (SQLException sqle) {
            LOG.error("checkPgpUsers", "SQLException " + sqle.getMessage());
            LOG.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
                    EventStatusI.RAISED, EventLevelI.FATAL,
                    "ChannelUserDAO[checkPgpUsers]", "", "", "",
                    "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "checkPgpUsers",
                    "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            LOG.error("checkPgpUsers", "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
                    EventStatusI.RAISED, EventLevelI.FATAL,
                    "ChannelUserDAO[checkPgpUsers]", "", "", "", "Exception:"
                            + e.getMessage());
            throw new BTSLBaseException(this, "checkPgpUsers",
                    "error.general.processing");
        }// end of catch
        finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception e) {
            }
            if (LOG.isDebugEnabled())
                LOG.debug("checkPgpUsers", "Exiting ChannelUserDAO:" + "");
        }// end of finally
    }
}
