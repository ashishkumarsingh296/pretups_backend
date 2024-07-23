package com.btsl.pretups.common;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;

import nl.captcha.Captcha;
import nl.captcha.backgrounds.GradiatedBackgroundProducer;
import nl.captcha.servlet.CaptchaServletUtil;
import nl.captcha.text.producer.DefaultTextProducer;

public class ImageCaptchaServlet extends HttpServlet {

    private static final String PARAM_HEIGHT = "height";
    private static final String PARAM_WIDTH = "width";

    protected int _width = 200;
    protected int _height = 50;

    private static final char DEFAULT_CHARS[] = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'k', 'm', 'n', 'p', 'r', 'w', 'x', 'y', '2', '3', '4', '5', '6', '7', '8' };

    /**
     * Constructor of the object.
     */
    public ImageCaptchaServlet() {
        super();
    }

    /**
     * Destruction of the servlet. <br>
     */
    public void destroy() {
        super.destroy(); // Just puts "destroy" string in log
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
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // creating Captcha Image to be send on JSP
        Captcha captcha = new Captcha.Builder(_width, _height).addText(new DefaultTextProducer(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CAPTCHA_LENGTH))).intValue(), DEFAULT_CHARS)).addBackground(new GradiatedBackgroundProducer()).gimp().addNoise().addBorder().build();
        request.getSession().setAttribute(Captcha.NAME, captcha);
        // Write Captcha Image on OutPutStream
       // CaptchaServletUtil.writeImage(response, captcha.getImage());

    }

    /**
     * Initialization of the servlet. <br>
     * 
     * @throws ServletException
     *             if an error occurs
     */
    public void init() throws ServletException {
        if (getInitParameter(PARAM_HEIGHT) != null) {
            _height = Integer.valueOf(getInitParameter(PARAM_HEIGHT));
        }

        if (getInitParameter(PARAM_WIDTH) != null) {
            _width = Integer.valueOf(getInitParameter(PARAM_WIDTH));
        }
    }

}
