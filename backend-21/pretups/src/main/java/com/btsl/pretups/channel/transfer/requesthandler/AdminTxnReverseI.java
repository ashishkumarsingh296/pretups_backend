package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.btsl.pretups.channel.transfer.businesslogic.C2CTxnsForReversalRequestVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2CTxnsForReversalResponseVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnectionI;

@Service
public interface AdminTxnReverseI {

	ArrayList<ChannelTransferVO> getC2CTxnsForReversal(Connection con, C2CTxnsForReversalRequestVO c2cTxnsForReversalRequestVO,
			HttpServletResponse responseSwag, ChannelUserVO sessionUser) throws BTSLBaseException, SQLException;


	C2CTxnsForReversalResponseVO performC2CTxnReversal(MComConnectionI mcomCon, Connection con, HttpServletResponse responseSwag,
			ChannelUserVO sessionUser, String txnId, String nwCode, String nwCodeFor, String remarks)
			throws BTSLBaseException;


}
