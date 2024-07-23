<%@ page import="java.io.File" %>
<%@	page import="java.io.FileReader" %>
<%@	page import="java.io.BufferedReader" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.io.FileWriter" %>
<%@	page import="java.util.*" %>
<%@ page language="java" import="com.btsl.alm.*" %>

<%
String inacc = request.getParameter("inacc");
%>
<meta http-equiv="refresh" content="10">
<%
System.out.println("Inside PendingAlarm Window...");
String[] s = new String[]
{ "Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};   		
int month;
int date;

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
System.out.println(TEMP_PATH);
String sTempLogFile = TEMP_PATH+"temp.log";
String sTempDelFile = TEMP_PATH+"tmplog.tmp";
System.out.println(sTempLogFile);

String PASSEDVAR=request.getParameter("role");//"S";
String totalcount=(String) session.getAttribute("count");
System.out.println("PendingAlarm: Role: "+PASSEDVAR+" , Count: "+totalcount);
int nooflines=0;
if(totalcount == null)
	totalcount="0";
int counttotal = Integer.parseInt(totalcount);
System.out.println("\n\n&&&&&&&&&&&&&&&&&&"+counttotal);
int val=0;

String month_str;
String datestr;

String filename;
Calendar date_now=Calendar.getInstance();
month=date_now.get(Calendar.MONTH);
//System.out.println("\n\n"+month);

month_str=s[month];

//PATH CHANGE REQUIRED
/**********************************************************/
/*****************Change Path Here*************************/
/**********************************************************/
String ALARM_PATH="";
        try
        {
                ALARM_PATH = GuiConfHandler.getConfigParam("AlmGuiConf.conf","ALARM_PATH");
        }
        catch(Exception e)
        {
                System.out.println("Exception Config file Read:: "+e);
        }

/**********************************************************/


String filepath=ALARM_PATH+month_str+"-"+date_now.get(Calendar.YEAR)+"-"+"LOG/";
//String filepath="C:\\tomcat4\\webapps\\ROOT\\";

date=date_now.get(Calendar.DAY_OF_MONTH);
datestr=Integer.toString(date);
int tempnolines=0;


if(date<10)
{
	datestr="0"+datestr;
}
filename=filepath+"OAM"+month_str+"-"+datestr+"-"+date_now.get(Calendar.YEAR)+".log";
System.out.println("PendingAlarm: File: "+filename);


String lineread ;

try
{
	BufferedReader input_c = null;
	FileReader file_c = new FileReader(filename);
	input_c = new BufferedReader(file_c);
	while(input_c.readLine() != null)
		nooflines++;
   // System.out.println(nooflines);
	if(counttotal == 0 && nooflines > 500)
		counttotal=nooflines-500;

	
	File testcreated=null;
	testcreated = new File(sTempLogFile);
	if(testcreated.exists() == false)
	{
		PrintWriter t = new PrintWriter(new FileWriter(sTempLogFile,true));
		t.close();
	}

/*	testcreated = null;
	testcreated = new File(sTempDelFile);
	  if(testcreated.exists() == false)
        {
                PrintWriter t = new PrintWriter(new FileWriter(sTempLogFile,true));
                t.close();
        }
	
*/


	//Get the current number of lines from the raised alarms file
	BufferedReader input_comps1 = null;
	FileReader file_comps1 = new FileReader(sTempLogFile);
	input_comps1 = new BufferedReader(file_comps1);
	while(input_comps1.readLine() != null)
		tempnolines++;
	System.out.println("########33\n"+tempnolines);
	input_comps1.close();
	file_comps1.close();

	//Delete the extra rows
	if(tempnolines > 100)
	{
	    //Delete that particular line from the uncleared log file
		BufferedReader input_tmco=null;
		String tmlineco;
		FileReader tmco = new 	FileReader(sTempLogFile);
		input_tmco = new BufferedReader(tmco);
		int newlcountco=0;
		File datafileco = null;
		datafileco = new File(sTempDelFile);
		PrintWriter dataoutco = new PrintWriter(new FileWriter(datafileco));

		int threshold=tempnolines-100;
		while( (tmlineco = input_tmco.readLine()) != null)
		{
			System.out.println("$$$$$$$$$$$$$$$$\n"+tmlineco);
			newlcountco++;
			if(newlcountco > threshold)
				dataoutco.println(tmlineco);
		}
		dataoutco.close();
		input_tmco.close();
		tmco.close();
										
		BufferedReader cpy_backco = null;
		String cpy_lineco;
		FileReader cpy_tmco = new 	FileReader(sTempDelFile);
		cpy_backco=new BufferedReader(cpy_tmco);
		File cpyfileco=null;
		cpyfileco = new File(sTempLogFile);
		PrintWriter cpyoutco = new PrintWriter(new FileWriter(cpyfileco));
		
		while( (cpy_lineco = cpy_backco.readLine()) != null)
		{
			cpyoutco.println(cpy_lineco);
		}
		cpyoutco.close();
		cpy_backco.close();
		cpy_tmco.close();
	}



	BufferedReader read = null;
	FileReader fname = new FileReader(filename);
	
	read = new BufferedReader(fname);
	System.out.println("%%%%%%%%%%%%%%%%\n"+filename);
	int flag;
	String tokstr;
	
System.out.println("\n7777777777777777"+val);
	while( (lineread = read.readLine()) != null)
	{
		 System.out.println("\n"+val);
		// System.out.println("55555\n"+counttotal);
		if(val < counttotal && counttotal > 0)
		{
			val++;
		}
		else
		{
			val++;
			System.out.println("@@@@@@@@@@\n"+val);
			String concat="";
			int tokenc=0;
			String status="";
			int goflag=1;
			int goflag1=0;
			int noclrflag=0;
			StringTokenizer getcleared= new StringTokenizer(lineread,"#");
			while(getcleared.hasMoreTokens())
			{
					tokstr=getcleared.nextToken();
				    //System.out.println(tokstr);
					if(tokenc==1)
						concat = concat+tokstr;
					//System.out.println("caonact======\n"+concat);
					if(tokenc==2)
					    concat = concat+tokstr;
					System.out.println("caonact======\n"+concat);
					if(tokenc==3)
					{
					  String sub1=tokstr.substring(0,1);
					  if(sub1.compareTo("C")==0)
					  {
						//concat = concat+tokstr.substring(4);/***************************Manoj************************/
						status="CLEARED";
					  }
				          else
					  {		 
						concat = concat+tokstr;
					        status="RAISED";	
					  }
					}
					/*if(tokenc==4)
						status=tokstr;*/
					
					BufferedReader input_tmp=null;
					String templine;
					FileReader tmp = new FileReader(sTempLogFile);
					input_tmp = new BufferedReader(tmp);
					int sublcount=0;
					
					while( (templine = input_tmp.readLine()) != null)
					{
							noclrflag=1;
							sublcount++;
							StringTokenizer sttmp = new StringTokenizer(templine,"#");
							int temptcount=0;
							String tmpstr="";
							String tmptoken="";
							String statstr="";
							//System.out.println("Cleared\n");
							while(sttmp.hasMoreTokens())
							{
								tmptoken=sttmp.nextToken();
							   System.out.println("tokem\n"+tmptoken);
							   if(temptcount==2)
								tmpstr=tmpstr+tmptoken;
								System.out.println("tokenn=======\n"+tmpstr);
								if(temptcount==3)
									tmpstr=tmpstr+tmptoken;
							    System.out.println("tokennnn===========\n"+tmpstr);
								if(temptcount==4)
								{
									//tmpstr=tmpstr+tmptoken;/************************Manoj**************************/
									statstr=tmptoken;
					                System.out.println("connnnn==========\n"+concat);
					                System.out.println("connn========\n"+tmpstr);
									if(tmpstr.compareTo(concat)==0)
									{
										if(status.compareTo("CLEARED")==0)//----got CLR delete that line from templogfile
										{
										//Delete that particular line from the uncleared log file
										BufferedReader input_tm=null;
										String tmline;
										FileReader tm = new 	FileReader(sTempLogFile);
										input_tm = new BufferedReader(tm);
										int newlcount=0;
										File datafile = null;
										datafile = new File(sTempDelFile);
										PrintWriter dataout = new PrintWriter(new FileWriter(datafile));
		
										while( (tmline = input_tm.readLine()) != null)
										{
											newlcount++;
											if(newlcount != sublcount)
												dataout.println(tmline);
										}
										dataout.close();
										input_tm.close();
										tm.close();
										
										BufferedReader cpy_back = null;
										String cpy_line;
										FileReader cpy_tm = new 	FileReader(sTempDelFile);
										cpy_back=new BufferedReader(cpy_tm);
										File cpyfile=null;
										cpyfile = new File(sTempLogFile);
										PrintWriter cpyout = new PrintWriter(new FileWriter(cpyfile));
		
										while( (cpy_line = cpy_back.readLine()) != null)
										{
											cpyout.println(cpy_line);
										}
										cpyout.close();
										cpy_back.close();
										cpy_tm.close();
										goflag1=1;
										}
									
										else //if(status.compareTo("RAISED")==0)
											goflag=0;
									}
									else
									{
										if(status.compareTo("CLEARED")==0)
										{
											goflag1=1;
										}
									}
								
								}
								temptcount++;
							}
						}
						input_tmp.close();
						tmp.close();
			
			tokenc++;
		}
		if(noclrflag==1 || (status.compareTo("RAISED")==0))
		{
		if(goflag==1 && goflag1==0)
		{
			if(lineread != "")
			{
			PrintWriter dout = new PrintWriter(new FileWriter(sTempLogFile,true));
			int dateb;
			int monthb;
			int yearb;
			dateb=date_now.get(Calendar.DATE);
			monthb=date_now.get(Calendar.MONTH);
			monthb++;
			yearb=date_now.get(Calendar.YEAR);
			String today=dateb+"-"+monthb+"-"+yearb;
			dout.println(today+"#"+lineread);
			dout.close();
			}
		}
		}

		}
	}
	
	read.close();
	fname.close();
	System.out.println("Count :: "+val);
	String szCountString = Integer.toString(val);
	session.setAttribute("count",szCountString);
}

catch(java.io.IOException e)
{
			//return;
			//out.println("Exception"+e);
}


%>
<script type="text/javascript" language="JavaScript">
function chkall()
{
        with(filterform)
        {
                var Count = elements.length;
                //alert(Count.value);
            var elem;
            for (i = 0 ; i <= Count.value ; i++)
            {
                 elem = elements[i];
                 if (elem.type == "checkbox" && elem!=document.getElementById("checkall"))
                 {
//                       alert(i);
                          if(elem.checked == false)
                      elem.checked = true;
                          else
                      elem.checked = false;

                 }

            }
        }
}
var sURL 

function doLoad()
{
	sURL= unescape(window.location.pathname);
    // the timeout value should be the same as in the "refresh" meta-tag
	//sURL=sURL+"?role=<%=PASSEDVAR%>&count=<%=val%>#scroll";
        window.location.replace( sURL+"?role=<%=PASSEDVAR%>&count=<%=val%>#scroll" );
    setTimeout( "refresh()", 10*1000 );

}

function refresh()
{
 //sURL= unescape(window.location.pathname);

	//document.out.println("_");
        //window.location.replace( sURL+"?role=<%=PASSEDVAR%>&count=<%=val%>#scroll" );
//alert(sURL);	

window.location.reload( true );

}

</script>

<jsp:include page="/monitorserver/common/topband.jsp" flush="false">
<jsp:param name="helpFile" value="/help/oam.jsp" />
	<jsp:param name="showJspHeading" value="N" />
</jsp:include>
<jsp:include page="/monitorserver/common/mtopband.jsp"/>


<form name="filterform" method="post" action="LmDeleteAlarm.jsp?inacc=<%=inacc%>&role=<%=PASSEDVAR%>">
<div align="center" class="heading"><b>Displaying Pending Alarms</b> </div><br>
<table border="0" cellspacing="1" align="center" cellpadding="1" width="90%" class="topbackbg">
  <tr > 
    <th class="tabhead"><input type=checkbox name="checkall" onclick="chkall()"></input></th>
	<th width="40" class="tabhead">Date	</th>
	<th class="tabhead">Time</th>
	<th class="tabhead"> Component    </th>
	<th class="tabhead"> Criticality   </th>
	<th class="tabhead"> Alarm Messages </th>

  </tr>
  <%	
	String line ;
	int globalflag=0;
	int linecount=0;
	String disabled="";
	if(PASSEDVAR.compareTo("S")!=0)
	{	
		disabled="DISABLED";
	}
	try
	{
		BufferedReader input = null;
		FileReader file = new FileReader(sTempLogFile);
		input = new BufferedReader(file);
		
		int flag;

		int type=0	;
		
		String tokenstring;
		String tmpstring;
		while( (line = input.readLine()) != null)
		{
			flag=0;
			linecount++;
			StringTokenizer getcolor= new StringTokenizer(line,"#");
			int tcount=0;	
			int temptcount=0;
			while(getcolor.hasMoreTokens())
			{
				tmpstring=getcolor.nextToken();
				System.out.println(tmpstring);
				if(temptcount==4)
				{
				if(tmpstring.compareTo("MINOR")==0)
						type=0;
				else if(tmpstring.compareTo("MAJOR")==0)
						type=1;
					else if(tmpstring.compareTo("FATAL")==0)
						type=2;
				}
				temptcount++;
				
			}

			StringTokenizer st = new StringTokenizer(line,"#");

			while(st.hasMoreTokens())
			{
				tokenstring=st.nextToken();

				if(tcount==0)
				{
					if(type==0)
					{
						out.println("<tr bgcolor=\"#009966\">");
	out.println("<td><input TYPE=\"checkbox\" NAME="+linecount+" "+disabled+" VALUE="+linecount+">");
						out.println("</input></td>");

					}
					else if(type == 1)
					{
						out.println("<tr bgcolor=\"Yellow\">");
out.println("<td><input TYPE=\"checkbox\" NAME="+linecount+" "+disabled+" VALUE="+linecount+">");						out.println("</input></td>");

					}
					else if(type == 2)
					{
						out.println("<tr bgcolor=\"#FF3300\">");
						out.println("<td><input TYPE=\"checkbox\" NAME="+linecount+" "+disabled+" VALUE="+linecount+">");
						out.println("</input></td>");

					}

					out.println("<b>"); 
					out.println("<td>  "); 
					out.println(tokenstring); 
					//System.out.println("\n"+tokenstring); 
					out.println(" </td></b>");
					globalflag=1;
				}
				 /*  try
                                   {
                                      if(tcount==1)
                                      //    tokenstring=tokenstring.substring(5,14);
					 out.println("<b>");
           out.println("<td> <font size=\"-6\" face=\"Verdana, Arial, Helvetica, sans-serif\">");
                                        out.println(tokenstring);
					//System.out.println("\n"+tokenstring); 
                                        out.println("</font> </td></b>");

                                    }
                                    catch(Exception e)
                                    {
                                    }*/


				if(tcount==1||tcount==3||tcount==4)
				{
					try{
					if(tcount==1)
                                        tokenstring=tokenstring.substring(5,14);}
					catch(Exception e)
					{}

					out.println("<b>"); 
					out.println("<td>  "); 
					out.println("\n"+tokenstring); 
					//System.out.println(tokenstring); 
					out.println("</td></b>");
				}
					
						
				if(tcount==6)
				{
					out.println("<b>"); 
					out.println("<td>  "); 
					out.println(tokenstring); 
					//System.out.println("\n\n"+tokenstring); 
					out.println(" </td></b>");
							out.println("</tr>");
				}
		
				tcount++;
			}

	
		}
	}
		catch(java.io.IOException e)
		{
			//return;
			//out.println("Exception"+e);
		}

	out.println("</table>");
	out.println("<input type=\"hidden\" name=\"length\" value="+linecount+"></input>");
	if(globalflag == 0)
	out.println("<div align=\"center\"> No Uncleared Alarms</font></div><br><br><br>");
	else
	out.println("<br><br><div align=\"center\"><input type=\"submit\" class=\"btn\" value=\"Clear Alarm\""+" "+disabled+"></input></div></form>");
%>

<input type="hidden" name="scroll" ></input>
<br><br><div align="center"> <font color="#CCCCCC">This page Auto-Refreshes every 10 Seconds 
</font></div>


  <jsp:include page="/monitorserver/common/bottomband.jsp"/>



