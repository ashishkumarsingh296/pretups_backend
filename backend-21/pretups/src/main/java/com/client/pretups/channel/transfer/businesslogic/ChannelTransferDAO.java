/**
 * @(#)ChannelTransferDAO.java
 *                             Copyright(c) 2005, Bharti Telesoft Ltd.
 *                             All Rights Reserved
 * 
 * 
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Author Date History
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             avinash.kamthan Aug 2, 2005 Initital Creation
 *                             Sandeep Goel Nov 05, 2005
 *                             Modification,Customization
 *                             Sandeep Goel Sep 18, 2006
 *                             Modification,Customization ID REC001
 *                             Sandeep Goel Oct 09, 2006
 *                             Modification,Customization ID RECON001
 *                             Shishupal Singh Mar 27, 2007 Modification in
 *                             loadC2STransferVOList method
 *                             avinash.kamthan July 19, 2011 Changes for Email
 *                             Notification
 *                             Amit Raheja Oct 11,2012 c2s_transfer_items
 *                             removal from db
 *                             Ashutosh Kumar July 22,2015 Network Wallet
 *                             management
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 * 
 */

package com.client.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAOI;
import com.client.pretups.user.businesslogic.ChannelPgpUserVO;

public class ChannelTransferDAO implements ChannelTransferDAOI{
    /**
     * Commons Logging instance.
     */
    private static Log _log = LogFactory.getLog(ChannelTransferDAO.class.getName());
    
	 public ArrayList loadPGPUser(Connection p_con,String p_userID) throws BTSLBaseException
    {
		 final String methodName = "loadPGPUser";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered  p_userID " + p_userID);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList pgpUserList=null;
        StringBuffer strBuff = new StringBuffer(" SELECT USER_ID,IP,PORT,PUBLIC_KEY,PRIVATE_KEY,SECRET_KEY,USER_NAME,PASSWORD,FILEPATH ");
        strBuff.append(" FROM PGP_DETAILS ");
        strBuff.append(" WHERE ");
        strBuff.append(" user_id = ?  ");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        ArrayList FTPUserList = new ArrayList();
        try
        {
            pstmt = p_con.prepareStatement(sqlSelect);
            int m = 0;
            pstmt.setString(++m, p_userID);            
            int i = 0;
            rs = pstmt.executeQuery();
            pgpUserList=new ArrayList();
            ChannelPgpUserVO pgpUserVO = null;
            while (rs.next())
            {
            	pgpUserVO = new ChannelPgpUserVO();
            	pgpUserVO.setUserID(rs.getString("USER_ID"));
            	pgpUserVO.setpgpIp(rs.getString("IP"));
            	pgpUserVO.setpgpPort(rs.getString("PORT"));
            	pgpUserVO.setpgpEncryptKeyFileName(rs.getString("PUBLIC_KEY"));
            	pgpUserVO.setpgpDecryptKeyFileName(rs.getString("PRIVATE_KEY"));
            	pgpUserVO.setPassphrase(rs.getString("SECRET_KEY"));
            	pgpUserVO.setUserName(rs.getString("USER_NAME"));
            	pgpUserVO.setPassword(rs.getString("PASSWORD"));
            	pgpUserVO.setSftpPGPFilePath(rs.getString("FILEPATH"));
            	pgpUserList.add(pgpUserVO);
            }

        }
		catch (SQLException sqe)
        {
            _log.error(methodName, "SQLException : " + sqe);
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadPGPUser]", "", "", "", "SQL Exception:"
                    + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } 
		catch (Exception ex)
        {
            _log.error("", "Exception : " + ex);
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[loadPGPUser]", "", "", "", "Exception:"
                    + ex.getMessage());
            throw new BTSLBaseException(this, "", "error.general.processing");
        } 
		finally
        {
            try{if (rs != null){rs.close();}} catch (Exception e){_log.errorTrace(methodName,e);}
            try{if (pstmt != null){pstmt.close();}} catch (Exception e){_log.errorTrace(methodName,e);}
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting:  FTP user List size =" + pgpUserList.size());
        }
        return pgpUserList;
    } 
}
