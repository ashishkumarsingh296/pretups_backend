<HTML>
<%@ page import="java.util.Vector"%>
<%@ page import="java.io.File" %>
<%@	page import="java.io.FileReader" %>
<%@	page import="java.io.BufferedReader" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.io.FileWriter" %>
<%@ page import="java.io.InputStreamReader" %>
<%@	page import="java.util.*" %>
<%@ page language="java" import="com.btsl.alm.*" %>


<jsp:include page="/monitorserver/common/topband.jsp" flush="false">
<jsp:param name="helpFile" value="/help/oam.jsp" />
	<jsp:param name="showJspHeading" value="N" />
</jsp:include>
<jsp:include page="/monitorserver/common/mtopband.jsp"/>

<% 
String inacc = request.getParameter("inacc");

//PATH CHANGE REQUIRED
/**********************************************************/
/*****************Change Path Here*************************/
/**********************************************************/
String TEMP_PATH="";
        try
        {
                TEMP_PATH = GuiConfHandler.getConfigParam("AlmGuiConf.conf","TEMP_PATH");
        }
        catch(Exception e)
        {
                System.out.println("Exception Config file Read:: "+e);
        }

/**********************************************************/


String sTempLogFile = TEMP_PATH+"temp.log";
String sTempDelFile = TEMP_PATH+"tmplog.tmp";

	int i;
	String role=request.getParameter("role");
	String c = (String) session.getAttribute("count");
	String len=request.getParameter("length");
	int lenint=Integer.parseInt(len);
	String val;
	Vector checked = new Vector();
	for(i=0;i<lenint;i++)
	{
		String param=request.getParameter(String.valueOf(i+1));
		System.out.println("\n"+param);
		if(param != null)
		{
			checked.addElement(new String(param));
		}
	
	}
	

	BufferedReader input_tm=null;
	String tmline;
	FileReader tm = new FileReader(sTempLogFile);
	input_tm = new BufferedReader(tm);
	int newlcount=0;
	File datafile = null;
	datafile = new File(sTempDelFile);
	PrintWriter dataout = new PrintWriter(new FileWriter(datafile));
	while( (tmline = input_tm.readLine()) != null)
	{
		newlcount++;
		String linec=Integer.toString(newlcount);
		System.out.println(linec);
		if(checked.contains(new String(linec))==false)
		{
			dataout.println(tmline);
		}
		else
		{
			String tokenstring="";
			StringTokenizer addtolog=new StringTokenizer(tmline,"#");
			String Component="";
			String Subtype="";
			String Criticality="";
			int mytcount=0;
			while(addtolog.hasMoreTokens())
			{
				mytcount++;
				tokenstring=addtolog.nextToken();
				if(mytcount != 1 && mytcount != 2)
				{
					if(mytcount == 5)
					{
						Criticality=tokenstring;
					}
					else if(mytcount == 4)
					{
						Component=tokenstring;	
					}
					else if(mytcount == 3)
					{
						Subtype=tokenstring;
					}
				}
			}
			Runtime clralrm = Runtime.getRuntime();

//PATH CHANGE REQUIRED
/**********************************************************/
/*****************Change Path Here*************************/
/**********************************************************/
	String sPath="";
	try
        {
                sPath = GuiConfHandler.getConfigParam("AlmGuiConf.conf","SEND_ALARM_PATH");
        }
        catch(Exception e)
        {
                System.out.println("Exception Config file Read:: "+e);
        }
/**********************************************************/

	String[] cmd = {sPath,Component,Criticality,Subtype};
			Process p = clralrm.exec(cmd);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String strAlarm = ""; 
			//out.println("Here's output");
			String s = null;
 			String str="0";//default size of alarm log file
			while ((s = stdInput.readLine()) != null)
			{
    			   out.println("Error: "+s);
      			   strAlarm += s+"\n";
			}
			stdInput.close();
 
			p.waitFor();
			int nret = p.exitValue();
			//out.println("Exit Status: "+nret);
		}	
	}
	dataout.close();
									
	BufferedReader cpy_back = null;
	String cpy_line;
	FileReader cpy_tm = new FileReader(sTempDelFile);
	cpy_back=new BufferedReader(cpy_tm);
	File cpyfile=null;
	cpyfile = new File(sTempLogFile);
	PrintWriter cpyout = new PrintWriter(new FileWriter(cpyfile));
	while( (cpy_line = cpy_back.readLine()) != null)
	{
		cpyout.println(cpy_line);
	}
	cpyout.close();

//String myPage = "openalarmc.jsp?count="+String.valueOf(c-2)+"&role="+role;
%>
<div align="center" class="heading"> 
Deletion Successful !! Click 
<a href="LmPendingAlarm.jsp?role=<%=role%>&inacc=<%=inacc%>">here</a> to go back</b></font></div>

