
package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.ArrayList;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;

public interface C2SRechargeBLI {

	public void loadPGPUser(Locale locale, ArrayList pgpUserList, ChannelUserVO channelUserVO, String PGP_FILE_PATH, String filePath, String fileName) throws BTSLBaseException;
}
