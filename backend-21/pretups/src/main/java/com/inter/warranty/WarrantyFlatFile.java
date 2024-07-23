package com.inter.warranty;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.common.BTSLBaseException;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class WarrantyFlatFile {
    private static Log _log = LogFactory.getLog(WarrantyFlatFile.class.getName());
    List<String> results = new ArrayList<String>();
    FileWriter write = null;

    public boolean createFlatFile(HashMap data) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("createFlatFile", " Entered data:" + data);
        String amount = null;
        String txnid = null;
        String intefaceid = null;
        String categorycode = null;
        String senderMsisdn = null;
        String receieverMsisdn = null;
        String ssnNumber = null;
        String posValue = null;
        String userName = null;
        String blank = "";
        StringBuffer file = new StringBuffer();
        String fieldSep = "|";
        String newLine = "\n";
        String filePath = null;
        String fileName = null;
        File[] files = null;
        boolean flatfile = false;
        {
            Date today = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            sdf.setLenient(false);

            if (sdf.format(today) != null)
                file.append(sdf.format(today) + fieldSep);
            else
                file.append(blank + fieldSep);

            intefaceid = (String) data.get("INTERFACE_ID");
            if (intefaceid != null)
                file.append(intefaceid + fieldSep);
            else
                file.append(blank + fieldSep);

            categorycode = (String) data.get("CATEGORY_CODE");
            if (categorycode != null)
                file.append(categorycode + fieldSep);
            else
                file.append(blank + fieldSep);

            amount = (String) data.get("INTERFACE_AMOUNT");
            if (amount != null)
                file.append(amount + fieldSep);
            else
                file.append(blank + fieldSep);

            txnid = (String) data.get("TRANSACTION_ID");
            if (txnid != null)
                file.append(txnid + fieldSep);
            else
                file.append(blank + fieldSep);

            senderMsisdn = (String) data.get("SENDER_MSISDN");
            if (senderMsisdn != null)
                file.append(senderMsisdn + fieldSep);
            else
                file.append(blank + fieldSep);

            receieverMsisdn = (String) data.get("MSISDN");
            if (receieverMsisdn != null)
                file.append(receieverMsisdn + fieldSep);
            else
                file.append(blank + fieldSep);

            ssnNumber = (String) data.get("SSN");
            if (ssnNumber != null)
                file.append(ssnNumber + fieldSep);
            else
                file.append(blank + fieldSep);

            userName = (String) data.get("USER_NAME");
            if (userName != null)
                file.append(userName + fieldSep);
            else
                file.append(blank + fieldSep);

            posValue = (String) data.get("POS_VALUE");
            if (posValue != null)
                file.append(posValue + fieldSep);
            else
                file.append(blank + fieldSep);

            file.append(blank + newLine);
        }

        try {
            flatfile = false;
            Date date = new Date();
            filePath = Constants.getProperty("FLATFILE_PATH_WARRANTY");
            if (InterfaceUtil.isNullString(filePath)) {
                _log.error("createFlatFile", "File Path for warranty is not defined in the Constant.props");
                throw new BTSLBaseException(this, "createFlatFile", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            fileName = Constants.getProperty("FLATFILENAME") + BTSLUtil.getDateStringFromDate(date, "yyyyMMddHHmmss") + ".txt";
            if (InterfaceUtil.isNullString(fileName)) {
                _log.error("createFlatFile", "File name for warranty is not defined in the Constant.props");
                throw new BTSLBaseException(this, "createFlatFile", InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION);
            }
            try {
                File fileDir = new File(filePath);
                if (!fileDir.isDirectory())
                    fileDir.mkdirs();
                files = new File(filePath).listFiles();
                if (files.length != 0) {
                    for (File file1 : files) {
                        if (file1.isFile()) {
                            results.add(file1.getName());
                        }
                    }
                    Collections.sort(results);
                    String lastFileName = ((String) results.get(results.size() - 1)).split("[.]")[0];
                    if (lastFileName.substring(4, 12).equalsIgnoreCase(String.valueOf(BTSLUtil.getDateStringFromDate(date, "yyyyMMdd")))) {
                        fileName = (String) results.get(results.size() - 1);
                        write = new FileWriter(filePath + "/" + fileName, true);
                    }

                    else
                        write = new FileWriter(filePath + "/" + fileName, false);
                } else {
                    write = new FileWriter(filePath + "/" + fileName, false);
                }
                PrintWriter print = new PrintWriter(write);
                print.print(file);
                print.close();
                write.close();
                flatfile = true;
            } catch (Exception e) {
                e.printStackTrace();
                throw new BTSLBaseException(this, "createFlatFile", "", "Error in file Operation");

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("createFlatFile", "Exititng: flat_file=" + flatfile);
            }
        }
        return flatfile;
    }
}