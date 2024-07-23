package com.btsl.common;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.security.SecureRandom;

import javax.imageio.ImageIO;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet implementation class for Servlet: ImageCaptcha
 * 
 */
public class ImageCaptcha extends jakarta.servlet.http.HttpServlet implements jakarta.servlet.Servlet {
	private static final long serialVersionUID = 1L;

    private static int height = 0;
    private static int width = 0;

    public static final String CAPTCHA_KEY = "captcha_key_name";

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        height = Integer.parseInt(getServletConfig().getInitParameter("height"));
        width = Integer.parseInt(getServletConfig().getInitParameter("width"));
    }

    public ImageCaptcha() {
        super();
    }

    /*
     * (non-Java-doc)
     * 
     * @see jakarta.servlet.http.HttpServlet#doGet(HttpServletRequest request,
     * HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Expire response
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Max-Age", 0);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = image.createGraphics();
        SecureRandom r =  new SecureRandom();
        String token = Long.toString(Math.abs(r.nextLong()), 36);
        String ch = token.substring(0, 6);
        Color c = new Color(0.6662f, 0.4569f, 0.3232f);
        GradientPaint gp = new GradientPaint(30, 30, c, 15, 25, Color.WHITE, true);
        graphics2D.setPaint(gp);
        Font font = new Font("Arial", Font.CENTER_BASELINE, 26);
        graphics2D.setFont(font);
        graphics2D.drawString(ch, 2, 20);
        graphics2D.dispose();

        HttpSession session = request.getSession();
        session.setAttribute(CAPTCHA_KEY, ch);

        OutputStream outputStream = response.getOutputStream();
        ImageIO.write(image, "jpeg", outputStream);
        outputStream.close();

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
    }
}