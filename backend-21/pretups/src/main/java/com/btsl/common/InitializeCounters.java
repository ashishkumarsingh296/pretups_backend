package com.btsl.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;

import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.loadcontroller.InstanceLoadVO;
import com.btsl.loadcontroller.LoadControllerDAO;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.UpdateCacheServlet;
import com.btsl.util.Constants;

public class InitializeCounters {
    private static Log log = LogFactory.getLog(UpdateCacheServlet.class.getName());

    public InitializeCounters(String p_consPath, String p_logPath) {
        final String METHOD_NAME = "InitializeCounters";
        try {
            Constants.load(p_consPath);
       //     org.apache.log4j.PropertyConfigurator.configure(p_logPath);
        } catch (Exception e) {
            log.errorTrace(METHOD_NAME, e);
        }

    }

    public static void main(String args[]) {
        // the values that are passed into constructer are later replaced by
        // args[1] and args[2]
        // the args[1] will contain the path of constatnt.props and
        // args[2] will contain the path of logConfig.props
        final String METHOD_NAME = "main";
        InitializeCounters initializeCounters = new InitializeCounters("C:\\eclipse\\workspace\\pretups\\src\\configfiles\\Constants.props", "C:\\eclipse\\workspace\\pretups\\src\\configfiles\\LogConfig.props");
        ArrayList instanceList = initializeCounters.loadInstances();
        InstanceLoadVO instanceLoadVO = new InstanceLoadVO();
        String responseStr = null;
        URL url = null;
        HttpURLConnection _con = null;
        BufferedReader in = null;
        String response = "";
        int i = 0;
        StringBuilder urlBuilder=new StringBuilder("");
        if (instanceList != null && !instanceList.isEmpty()) {
            while (i < instanceList.size() ) {
                instanceLoadVO = (InstanceLoadVO) instanceList.get(i);                
                 if( "SMS".equals(instanceLoadVO.getInstanceType())){        
                    try {
                    	urlBuilder.setLength(0);
                    	urlBuilder.append("http://");
                    	urlBuilder.append(instanceLoadVO.getHostAddress());
                    	urlBuilder.append(":");
                    	urlBuilder.append(instanceLoadVO.getHostPort());
                    	urlBuilder.append("/pretups/monitorserver/initialiseCountersAll.jsp?instanceID=");
                    	urlBuilder.append(instanceLoadVO.getInstanceID());
                        url = new URL(urlBuilder.toString());
                        if (log.isDebugEnabled()) {
                            log.debug("main", "URL:: " + url);
                        }
                        _con = (HttpURLConnection) url.openConnection();
                        _con.setDoInput(true);
                        _con.setDoOutput(true);
                        _con.setRequestMethod("GET");
                        in = new BufferedReader(new InputStreamReader(_con.getInputStream()));
                        while ((responseStr = in.readLine()) != null) {
                            response = response + responseStr;
                        }
                        if (log.isDebugEnabled()) {
                            log.debug("main", "response:: " + response);
                        }
                        if (response.indexOf("success") > 0) {
                            if (log.isDebugEnabled()) {
                                log.debug("main", "response:: Successfully initialize counters for instance id=" + instanceLoadVO.getInstanceID());
                            }
                        } else {
                            if (log.isDebugEnabled()) {
                                log.debug("main", "response:: counters can not be initialized successfully please try later.Intstance id=" + instanceLoadVO.getInstanceID());
                            }
                        }
                    } catch (Exception e) {
                        log.errorTrace(METHOD_NAME, e);
                    } finally {
                        if (in != null) {
                            try {
                                in.close();
                            } catch (Exception e) {
                                log.errorTrace(METHOD_NAME, e);
                            }
                        }
                    }
                }
                i++;
            }
        }

    }

    private ArrayList loadInstances() {
        Connection con = null;
        MComConnectionI mcomCon = null;
        ArrayList list = null;
        final String METHOD_NAME = "loadInstances";
        try {
        	mcomCon = new MComConnection();
        	con=mcomCon.getConnection();
            LoadControllerDAO controllerDAO = new LoadControllerDAO();
            list = controllerDAO.loadInstanceLoadDetails(con);
        } catch (BTSLBaseException be) {
            log.error("loadInstances", "Exception " + be.getMessage());
            log.errorTrace(METHOD_NAME, be);
        }// end of catch
        catch (Exception e) {
            log.error("loadInstances", "Exception " + e.getMessage());
            log.errorTrace(METHOD_NAME, e);	
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "InitializeCounters[loadInstances]", "", "", "", "Exception while loading instances  Getting =" + e.getMessage());
        }// end of catch
        finally {
        	if(mcomCon != null){mcomCon.closeAfterSelect("InitializeCounters#loadInstances");mcomCon=null;}
            if (log.isDebugEnabled()) {
                log.debug("loadInstances", "Exiting list.size " + list.size());
            }
        }// end of finally
        return list;
    }

}
