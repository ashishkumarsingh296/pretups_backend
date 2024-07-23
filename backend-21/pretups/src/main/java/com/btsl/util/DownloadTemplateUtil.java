/*
 * Created on February 14, 2013 by Vibhu
 * 
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.btsl.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;


/**
 * @author Administrator
 * 
 *To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class DownloadTemplateUtil extends HttpServlet {


	private static final Log _log = LogFactory
			.getLog(DownloadTemplateUtil.class.getName());

    /**
     * 
     */

    /**
     * Initialization of the servlet. <br>
     * 
     * @throws ServletException
     *             if an error occure
     */
    @Override
    public void init() throws ServletException {
        // Put your code here
    }

    /**
     * The doGet method of the servlet. <br>
     * 
     * This method is called when a form has its tag value method equals to get.
     * 
     * @param request
     *            the request send by the client to the server
     * @param response
     *            the response send by the server to the client
     * @throws ServletException
     *             if an error occurred
     * @throws IOException
     *             if an error occurred
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        final String METHOD_NAME = "doGet";
        final HttpSession sess = request.getSession(false);
        final Object obj = sess.getAttribute("user");
        final String context = request.getContextPath();
        final String url = context + "/logout.do?method=unAuthorisedAccess";
        String notDeletefileName = "";
        if (obj != null) {
            System.out.println("OK");
        } else {
            response.sendRedirect(url);
        }
        final String fileName = request.getParameter("fileName");
        String filePath = request.getParameter("filePath");
        final int check = filePath.indexOf("/");
        if (check < 0) {
            try {
                filePath = BTSLUtil.decrypt3DesAesText(filePath);
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
        }

        notDeletefileName = Constants.getProperty("DOWNLOAD_FILE_NAME_WITHOUTDELETE");

        // read the file name.
        final File f = new File(filePath + fileName);
        final String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        final int fLen = BTSLUtil.parseLongToInt(f.length());
        if("xlsx".equals(ext)) {
        	response.setContentLength(fLen); 
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        }
        else if("docx".equals(ext)){ 
        	response.setContentLength(fLen); 
        	response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        }else if("doc".equals(ext)){
        	response.setContentLength(fLen);
        	response.setContentType("application/msword");
        }else{
        	response.setContentType("application/" + ext);
        	}
        response.setHeader("Pragma", "public");
        response.setHeader("Cache-Control", "max-age=30");
        response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
        // get the file name
        final String name = f.getName().substring(f.getName().lastIndexOf("/") + 1, f.getName().length());
        //ASHU changes start here
        final InputStream in = new FileInputStream(f);
        final ServletOutputStream outs = response.getOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        try{ 
        	 while ((len = in.read(buffer)) != -1) {
             	outs.write(buffer, 0, len);
             }
        }catch (IOException ioe) {
            _log.errorTrace(METHOD_NAME, ioe);
        } finally {
            outs.flush();
            outs.close();
            in.close();
            if (!BTSLUtil.isNullString(notDeletefileName) && BTSLUtil.isStringContain(notDeletefileName, fileName)) {
                // Do nothing
            } else {
                try {
                	boolean isDeleted = f.delete();
                    if(isDeleted){
                     _log.debug(METHOD_NAME, "File deleted successfully");
                    }
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
            }
        }
    

    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * Destruction of the servlet. <br>
     */
    @Override
    public void destroy() {
        super.destroy(); // Just puts "destroy" string in log
        // Put your code here
    }

}
