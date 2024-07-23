package com.btsl.pretups.channel.transfer.util.clientutils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/*//import org.apache.struts.action.ActionForm;
//import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;*/

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.CryptoUtil;
import com.btsl.voms.voucher.businesslogic.VomsVoucherDAO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;
import com.client.pretups.user.businesslogic.ChannelPgpUserVO;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.web.pretups.channel.transfer.web.ChannelTransferEnquiryForm;

public class FileWriterBCA implements FileWriterI{

	private static Log _log = LogFactory.getLog(FileWriter.class.getName());




	private UserVO getUserFormSession(HttpServletRequest request) throws BTSLBaseException {
		UserVO userVO = null;
		// HttpSession session = request.getSession(true);
		HttpSession session = request.getSession(false);
		Object obj = session.getAttribute("user");

		if (obj != null) {
			userVO = (UserVO) obj;
		}
		// add this condition after getting the userVO from request, if null.
		if (obj == null || userVO == null) {
			throw new BTSLBaseException("common.topband.message.sessionexpired", "unAuthorisedAccessF");
		}
		return userVO;

	}

	private String getPaddedString(String p_arg,int p_length){
		String methodName = "getPaddedString";
		_log.debug(methodName, "Entered :: "+p_arg);
		String refinedArg=null;
		String paddedString = null;
		if(!BTSLUtil.isNullString(p_arg)){
			refinedArg = p_arg.replaceAll("\\.", "");
			refinedArg = refinedArg.replaceAll("-", "");
		}
		else
		{
			p_arg = "";
		}

		paddedString = BTSLUtil.padZeroesToLeft(refinedArg, p_length);
		_log.debug(methodName, "paddedString :: "+paddedString);
		return(paddedString);
	}
	/**
	 * Method to :
	 * 1) Create encrypted voucher file with BCA encryption technique for Master as well as Normal Serial Numbers
	 * 2) Checking if user is registered for FTP service
	 * 3) Performing the FTP of Voucher File to the user to whom O2C has been done
	 * @param con
	 * @param action
	 * @param channelTransferVO
	 * @param channelUserVO
	 * @throws BTSLBaseException
	 * @throws IOException
	 * @throws ParseException
	 */
	public String writeFileProcess(Connection con, String action, ChannelTransferVO channelTransferVO, ChannelUserVO channelUserVO) throws BTSLBaseException, IOException, ParseException {

		final String methodName = "writeFileProcess BCA 2";

		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered");
		}
		PrintWriter out = null;
		File encryptedVoucherFile = null;
		ArrayList voucherList = null;
		MComConnectionI mcomCon = null;
		VomsVoucherVO voucherVO = null;
		Channel channel = null;
   		ChannelSftp channelSftp = null;
   		Session session = null;
   		String status = null;
		try{
			
			String filePath = "";
				filePath = Constants.getProperty("FTP_FILE_PATH");
			final String fileName = Constants.getProperty("VoucherDownloadFilePrefix") + BTSLUtil.getFileNameStringFromDate(new Date()) + "."+Constants.getProperty("VoucherDownloadFileExt");
			try {
				encryptedVoucherFile = new File(filePath + fileName);
				if (!(encryptedVoucherFile.getParentFile()).isDirectory()) {
					(encryptedVoucherFile.getParentFile()).mkdirs();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
				_log.error(methodName, "Exception" + e.getMessage());
				throw new BTSLBaseException(this, methodName, "downloadfile.error.dirnotcreated");
			}

			try {
				out = new PrintWriter(new BufferedWriter(new java.io.FileWriter(encryptedVoucherFile)));
	
	
				VomsVoucherDAO vomsDAO = new VomsVoucherDAO();
				voucherList = new ArrayList();
				vomsDAO.loadVouchersListForTxnID(con,channelTransferVO.getTransferID(),voucherList);
				voucherVO = (VomsVoucherVO)voucherList.get(0);
				/*
				 * Batch Number,Start Serial Number,Total Record,Creation Date,Expiration Date,Denomination,Active Period,Purchase Order Number,Product Part Code,Primary Product Code,Secondary Product Code
				 */
	
				HashMap packageVoucherMapping = null;
				long serialNumberChecksum = 0;
				String encKey = Constants.getProperty("VOUCHER_ENC_DEC_KEY");
				DESedeDecryption enc = new DESedeDecryption(encKey);
				if(!BTSLUtil.isNullObject(voucherVO.getBundleId()) && voucherVO.getBundleId()>0){
					packageVoucherMapping = new HashMap();
					for(int i = 0, j = voucherList.size(); i < j; i++)
					{
						voucherVO = ((VomsVoucherVO)voucherList.get(i));
						serialNumberChecksum = serialNumberChecksum + Long.parseLong(voucherVO.getSerialNo());
						if(packageVoucherMapping.containsKey(voucherVO.getMasterSerialNo())){
							String tDetails = ","+voucherVO.getSerialNo()+","+new CryptoUtil().decrypt(voucherVO.getPinNo(), Constants.KEY);
							packageVoucherMapping.put(voucherVO.getMasterSerialNo(),packageVoucherMapping.get(voucherVO.getMasterSerialNo())+tDetails);
						}
						else{
							String tDetails = ","+voucherVO.getSerialNo()+","+new CryptoUtil().decrypt(voucherVO.getPinNo(), Constants.KEY);
							packageVoucherMapping.put(voucherVO.getMasterSerialNo(),voucherVO.getMasterSerialNo()+tDetails);
						}
					}
	
					out.print(getPaddedString(String.valueOf(Math.round(Double.parseDouble(PretupsBL.getDisplayAmount(voucherVO.getMRP())))),8));
					out.print(getPaddedString(voucherVO.get_batch_no(),8));
					out.print(getPaddedString(String.valueOf(serialNumberChecksum),17));
					out.println(getPaddedString(String.valueOf(voucherList.size()),5));
	
					Iterator itr = packageVoucherMapping.entrySet().iterator();
					String encrypted = "";
					int size = packageVoucherMapping.size();
					int sizeCounter = 0;
					while(itr.hasNext())
					{
						++sizeCounter;
						encrypted = "";
						Map.Entry entry = (Entry) itr.next();
						System.out.println(entry.getValue());
						encrypted = enc.encrypt((String)entry.getValue());
						if(sizeCounter == size)
							out.print(encrypted);
						else
							out.println(encrypted);
					}
				}
				else{				
					String encrypted = "";
					String tDetails = "";
					StringBuffer sbf = new StringBuffer();
					
					for(int i = 0, j = voucherList.size(); i < j; i++)
					{
						voucherVO = ((VomsVoucherVO)voucherList.get(i));
						serialNumberChecksum = serialNumberChecksum + Long.parseLong(voucherVO.getSerialNo());
						if(i == 0)
							tDetails = voucherVO.getSerialNo()+","+new CryptoUtil().decrypt(voucherVO.getPinNo(), Constants.KEY);
						else
							tDetails = ","+voucherVO.getSerialNo()+","+new CryptoUtil().decrypt(voucherVO.getPinNo(), Constants.KEY);
						encrypted = enc.encrypt(tDetails);
	
						if(i == (j-1))
							sbf.append(encrypted);
						else
							sbf.append(encrypted+"\n");					
					}
					
					out.print(getPaddedString(String.valueOf(Math.round(Double.parseDouble(PretupsBL.getDisplayAmount(voucherVO.getMRP())))),8));
					out.print(getPaddedString(voucherVO.get_batch_no(),8));
					out.print(getPaddedString(String.valueOf(serialNumberChecksum),17));
					out.println(getPaddedString(String.valueOf(voucherList.size()),5));
					out.print(sbf.toString());
				}
				
				out.close();
	
			}catch(Exception e) {
				_log.error(methodName, "Exception:e=" + e);
				_log.errorTrace(methodName, e);
				status = "file_generation_failed";
				throw new BTSLBaseException("file_generation_failed");
			}
	   			com.client.pretups.channel.transfer.businesslogic.ChannelTransferDAO pgpUSERDAO=new com.client.pretups.channel.transfer.businesslogic.ChannelTransferDAO();
	   			ArrayList pgpUserList=pgpUSERDAO.loadPGPUser(con,channelUserVO.getUserID());				
	   			if(pgpUserList.size()<1)
	   			{
	   				throw new BTSLBaseException(this,"ftpFile","ftp.error.usernotexistormultipleentry","notificationBulkDownloadVoucher");
	   			}

	   			ChannelPgpUserVO pgpUserVO=new ChannelPgpUserVO();
	   			pgpUserVO=(ChannelPgpUserVO)pgpUserList.get(0);		
	   			String SFTPHOST = pgpUserVO.getpgpIp();
	   			int SFTPPORT = Integer.parseInt(pgpUserVO.getpgpPort());
	   			String SFTPUSER = pgpUserVO.getUserName();
				String SFTPPASS = BTSLUtil.decrypt3DesAesText(pgpUserVO.getPassword());
	   			String SFTPWORKINGDIR = pgpUserVO.getSftpPGPFilePath();	
	   			try {
	   				String FILETOTRANSFER = encryptedVoucherFile.getAbsolutePath();
	   				JSch jsch = new JSch();
	   				session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
	   				session.setPassword(SFTPPASS);
	   				java.util.Properties config = new java.util.Properties();
	   				config.put("StrictHostKeyChecking", "no");
	   				session.setConfig(config);
	   				session.connect();
	   				channel = session.openChannel("sftp");
	   				channel.connect();
	   				channelSftp = (ChannelSftp) channel;		
	   				channelSftp.chmod(Integer.parseInt("766",8), SFTPWORKINGDIR);				
	   				channelSftp.cd(SFTPWORKINGDIR);
	   				File f = new File(FILETOTRANSFER);
	   				channelSftp.put(new FileInputStream(f), f.getName());
	   			} catch (Exception ex) {
	   				if (_log.isDebugEnabled()) 
	   					_log.debug("ftpFile","BTSLBaseExceptin:e="+ex);
	   				throw new BTSLBaseException("sftp.failed");
	   			}	
		}
		catch (BTSLBaseException e) {
			_log.error(methodName, "BTSLBaseException:e=" + e);
			_log.errorTrace(methodName, e);
			throw e;
		} catch (ParseException e) {
			_log.error(methodName, "ParseException:e=" + e);
			_log.errorTrace(methodName, e);
			throw e;
		}
		catch (Exception e) {
			_log.error(methodName, "Exception:e=" + e);
			_log.errorTrace(methodName, e);
		}
		finally {
			if(session != null)
				session.disconnect();
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			
		}
		return status;
	
	}
}
