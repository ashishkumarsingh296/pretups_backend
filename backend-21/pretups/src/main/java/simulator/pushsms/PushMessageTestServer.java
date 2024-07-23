package simulator.pushsms;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PushMessageTestServer extends HttpServlet
{
    
    private static String ACCOUNT_INFO = null;

    public PushMessageTestServer()
    {
    }
    @Override
    public void init(ServletConfig conf)
        throws ServletException
    {
        try
        {
            ACCOUNT_INFO = "running";
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        doPost(request, response);
    }
    @Override
    public void destroy()
    {
        super.destroy();
    }
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String responseStr = null;
        try
        {
            responseStr = ACCOUNT_INFO;
            out.print(responseStr);
            out.flush();
            if(out != null)
                out.close();
            //System.out.println((new StringBuilder("Shishupal responseStr==============")).append(responseStr).toString());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

}