
package com.btsl.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;

public interface ChannelTransferDAOI {

	public ArrayList loadPGPUser(Connection p_con,String p_userID) throws BTSLBaseException;
}
