/*
 * �COPYRIGHT: Mahindra Comviva Technologies Pvt. Ltd.
 * �
 * �This software is the sole property of Comviva and is protected
 * �by copyright law and international treaty provisions. Unauthorized
 * �reproduction or redistribution of this program, or any portion of
 * �it may result in severe civil and criminal penalties and will be
 * �prosecuted to the maximum extent possible under the law.
 * �Comviva reserves all rights not expressly granted. You may not
 * �reverse engineer, decompile, or disassemble the software, except
 * �and only to the extent that such activity is expressly permitted
 * �by applicable law notwithstanding this limitation.
 * �
 * �THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY
 * �KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * �THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * �PARTICULAR PURPOSE. YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY
 * �AND THE USE OF THIS SOFTWARE. Comviva SHALL NOT BE LIABLE FOR
 * �ANY DAMAGES WHATSOEVER ARISING OUT OF THE USE OF OR INABILITY TO
 * �USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED OF THE
 * �POSSIBILITY OF SUCH DAMAGE.
 */
package com.client.pretups.processes.clientprocesses;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.processes.businesslogic.MsisdnUpdateProcessDAO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;

public class MsisdnUpdateProcess {

    private static String _msisdnUpdateFileName = null;
    private static String _msisdnUpdateFilePath = null;
    private static String _msisdnUpdateFileExt = null;
    private static String _msisdnBatchUpdateCount = null;
    private static final Log LOG = LogFactory.getLog(MsisdnUpdateProcess.class.getName());

    private MsisdnUpdateProcess() {

    }

    public static void main(String arg[]) {

        final String methodName = "main()";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, " Entering the main method: ");
        }

        Map<String, String> msisdnMap = null;
        MsisdnUpdateProcessDAO msisdnUpdateDao = null;
        Connection con = null;MComConnectionI mcomCon = null;

        try {
            if (arg.length < 2) {
                LOG.info(methodName, "Usage : MsisdnUpdateProcess [Constants file] [LogConfig file]");
                return;
            }
            final File constantsFile = new File(arg[0]);
            if (!constantsFile.exists()) {
                LOG.info(methodName, "MsisdnUpdateProcess" + " Constants File Not Found .............");
                return;
            }
            final File logconfigFile = new File(arg[1]);
            if (!logconfigFile.exists()) {
                LOG.info(methodName, "MsisdnUpdateProcess" + " Logconfig File Not Found .............");
                return;
            }

            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            loadConstantParameters();
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            msisdnMap = readMSISDNUpdateFile();
            if (msisdnMap.isEmpty() == false) {
                msisdnUpdateDao = new MsisdnUpdateProcessDAO();
                msisdnUpdateDao.updateUserPhonePrimaryMsisdnBatch(con, msisdnMap, Integer.parseInt(_msisdnBatchUpdateCount));
            }

        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "MsisdnUpdateProcess[main]", "", "", "",
                " MsisdnUpdateProcess could not be executed successfully.");
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Error in main() method" + e.getMessage());
            }
        } finally {
			if (mcomCon != null) {
				mcomCon.close("MsisdnUpdateProcess#main");
				mcomCon = null;
			}
            ConfigServlet.destroyProcessCache();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting ");
            }
        }
    }

    private static Map<String, String> readMSISDNUpdateFile() {
        final String methodName = "readMSISDNUpdateFile()";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, " Entering the readMSISDNUpdateFile(): ");
        }
        final Map<String, String> msisdnMap = new LinkedHashMap<String, String>(5);
        try {
            final InputStream in = new FileInputStream(_msisdnUpdateFilePath + _msisdnUpdateFileName + _msisdnUpdateFileExt);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            final StringBuilder out = new StringBuilder();
            String line;
            String parts[] = null;
            while ((line = reader.readLine()) != null) {
                out.append(line + "\n");
                parts = line.split("\\|");
                msisdnMap.put(parts[0], parts[1]);
            }
            reader.close();

        } catch (IOException ex) {
            LOG.errorTrace(methodName, ex);
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, " Error in Reading Files: " + ex.getMessage());
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting ");
        }
        return msisdnMap;
    }

    private static void loadConstantParameters() throws BTSLBaseException {
        final String methodName = "loadConstantParameters()";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, " Entered: ");
        }
        try {
            _msisdnUpdateFileName = Constants.getProperty("ASSO_DISASSO_FILE_NAME");
            if (BTSLUtil.isNullString(_msisdnUpdateFileName)) {
                LOG.info(methodName, " Could not find file label for transaction data in the Constants file.");
            }

            _msisdnUpdateFilePath = Constants.getProperty("ASSO_DISASSO_FILE_PATH");
            if (BTSLUtil.isNullString(_msisdnUpdateFilePath)) {
                LOG.info(methodName, " Could not find file label for master data in the Constants file.");
            }

            _msisdnUpdateFileExt = Constants.getProperty("ASSO_DISASSO_FILE_EXT");
            if (BTSLUtil.isNullString(_msisdnUpdateFileExt)) {
                LOG.info(methodName, " Could not find file name for transaction data in the Constants file.");
            }
            _msisdnBatchUpdateCount = Constants.getProperty("BATCH_UPDATE_COUNT");
            if (BTSLUtil.isNullString(_msisdnBatchUpdateCount)) {
                LOG.info(methodName, " Could not find file name for transaction data in the Constants file.");
            }

        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "MsisdnUpdateProcess[loadConstantParameters]", "", "",
                "", " MsisdnUpdateProcess could not be executed successfully.");
            throw new BTSLBaseException("MsisdnUpdateProcess", "loadConstantParameters()", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }

    }

}
