<!--ffffffffffffffffffffffffffffffffff-->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@ page import="java.io.File" %>
<%@ page import="java.io.FileReader" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.util.*" %>
<%@ page language="java" import="com.btsl.alm.*" %> 
 

<meta http-equiv="refresh" content="300">
<% 
	
   String data=request.getParameter("fav");
   String cmp=request.getParameter("comp");
   String ala_id=request.getParameter("al_id");
   String lines=request.getParameter("lines");
   String inaccC=request.getParameter("inaccC");
  
  
   if(lines==null)
	lines="10";
   int getlines= Integer.parseInt(lines);
	getlines--; 

	//Date addition
	String datevalue=request.getParameter("datesend");
   	if(datevalue==null)
		datevalue="TODAY";
	int nooflines=0;
	int toprint=0;
       
	//Addition for filename
	String[] s = new String[]
	{ "Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};   		
	int month = 0;
	int date = 0;
	int confileread =0;// whether we have our config file "AlmGUI.conf" or not
	String month_str = "";
	String datestr = "";
   	String filename = "";
	String alarm_flag="ALL";
	Calendar date_now=Calendar.getInstance();
	month=date_now.get(Calendar.MONTH);
        month_str=s[month];
	
        date=date_now.get(Calendar.DAY_OF_MONTH);
	//PATH CHANGE REQUIRED
	/**********************************************************/
	/*****************Change Path Here*************************/
	/**********************************************************/
	String ALARM_PATH="";
	try
        {
		ALARM_PATH = GuiConfHandler.getConfigParam("AlmGuiConf.conf","ALARM_PATH");
		//String ee=Mine.my_str;
		confileread = 1; //we have our config file "AlmGUI.conf"
	}
	catch(Exception e)
	{
		System.out.println("Exception Config file Read:: "+e);
		confileread =0;// we have not our config file "AlmGUI.conf"
	}
	/**********************************************************/
	String filepath=ALARM_PATH+"/"+month_str+"-"+date_now.get(Calendar.YEAR)+"-"+"LOG/";

   if(datevalue.compareTo("TODAY")==0)
   {
	   datestr=Integer.toString(date);
	   if(date<10)
	   {
		datestr="0"+datestr;
	   }
	filename=filepath+"OAM"+month_str+"-"+datestr+"-"+date_now.get(Calendar.YEAR)+".log";
       	System.out.println("Alarm Log File Path:: "+filename);
   }
   
	 else
   {
	   filename=filepath+"OAM"+datevalue+".log";
   }
   System.out.println("Alarm Log File Path:: "+filename);
   String component[] = new String[300];
   String cmp_final = "";
  
  String alarmid[]=new String[300];
  String alarm_id="";
  
   if(data == null)
	  data = "ALL";
   if(cmp == null)
	  cmp = "ALL";
   if(ala_id==null)
        ala_id="ALL";
	try
	{
		String line_comp="";
		String compvalue="";
		
/*****************************file  read where alarms are persent datewise***********************/

		BufferedReader input_comps1 = null;
		FileReader file_comps1 = new FileReader(filename);
	        input_comps1 = new BufferedReader(file_comps1);
	
		while(input_comps1.readLine() != null)
			nooflines++;
		
		if(nooflines > getlines)
			toprint=nooflines-getlines;
			
               BufferedReader input_comps = null;
               FileReader file_comps = new FileReader(filename);
                input_comps = new BufferedReader(file_comps);
		int tlines=0;

  
 
		while( (line_comp = input_comps.readLine()) != null)
		{
			tlines++;
		        if(tlines >= toprint)
                        {
			    int tcount=0;
			    StringTokenizer comptoken = new StringTokenizer(line_comp,"#");
			    String str="bye";
			   while(comptoken.hasMoreTokens())
			   {
			      compvalue = comptoken.nextToken();
			      
			       if(tcount==1)
			       {
			           str=compvalue;
			       }
			       if(tcount == 2) //2Componentcolumn
 			       {
			         int flag_comp=0;
                                 int check=0;
				 /////////////
				
				/* if(ala_id.compareTo("ALL")!=0||cmp.compareTo("ALL")!=0)
				 if((ala_id.compareTo(str)!=0)||(cmp.compareTo(compvalue)!=0))
				 {
				 
				 	flag_comp=1;
				 }*/
		 
				 /////
			          while(component[check]!=null)
				  { 
			               if((component[check].compareTo(compvalue)==0))				       			 {
				       flag_comp=1;
				       }
				       check++;
				  }
		    		  if(flag_comp==0)
				  {
				  
				        if(ala_id.equals("ALL"))
				        component[check]=compvalue;
					else if(ala_id.compareTo(str)==0)
					  component[check]=compvalue;
				  }    
				    
			       }    
			    tcount++;
			   }
		      }
		}
		 input_comps.close();
		 
///////////////////////////////mine/////////////////////////

		BufferedReader input_comps_id = null;
		FileReader file_comps_id = new FileReader(filename);
		input_comps_id = new BufferedReader(file_comps_id);
		int tlines_id=0;
	
		while( (line_comp = input_comps_id.readLine()) != null)
		{
			tlines_id++;
			if(tlines_id >= toprint)
			{
				int tcount=0;
				StringTokenizer comptoken = new StringTokenizer(line_comp,"#");
				String str="bye";
				while(comptoken.hasMoreTokens())
				{
					compvalue = comptoken.nextToken();
       					if(tcount == 1) //1alarm column
					{
						int flag_comp=0;
						int check=0;
						while(alarmid[check] != null)
						{
						if(alarmid[check].compareTo(compvalue) == 0)
							{
								flag_comp = 1;								
							}
							check++;
						}
					         str=comptoken.nextToken();
							 if(flag_comp== 0)
							 {
						        if(cmp.compareTo("ALL")==0)
							alarmid[check] = compvalue;
							else
							{int i=0;
							String cmp_flag="false";
							while(component[i]!=null)
							{
							   
							   if(component[i].compareTo(cmp)==0)
							   {
							        cmp_flag="true";
								break;
							   }
							   i++;
							}
				        	if(cmp.compareTo(str)==0&&cmp_flag.compareTo("true")==0)
							  alarmid[check] = compvalue;
							}
							
							
						}
					}
					tcount++;
				}
			}
		}
		input_comps_id.close();
	}
	catch(java.io.IOException e)
	{
		//return;
		//out.println("Exception"+e);
	}
			int test=0;	
			while(component[test] != null)
			{
				cmp_final= cmp_final+"\""+component[test]+"\""+",";
				test++;
			}
			test=0;
		
			while(alarmid[test]!=null)
			{
			     alarm_id=alarm_id+"\""+alarmid[test]+"\""+",";
			     test++;
			}

				int len = cmp_final.length();
			
				if(len > 0)
				cmp_final= cmp_final.substring(0,len-1);
				int len1 = alarm_id.length();
				//out.println("###############len1"+len1);
				
				if(len1>0)
				{
				alarm_id=alarm_id.substring(0,len1-1);
				}
				else
				{
				cmp_final="";				
				}
				

				
				
			
%>


<script language="JavaScript">



function getSelect()
{
      
	var value= "<%=data%>" ;
	var cmp_value = "<%=cmp%>";
	var alarm_value="<%=ala_id%>";
	var date_value = "<%=datevalue%>";
	var comps = new Array(<%=cmp_final%>);
	var alarms = new Array(<%=alarm_id%>);
	var count=0;
	var myNewOption;
	var getdate=<%=date%>;
	var getmonth="<%=month_str%>";
	var getyear="<%=date_now.get(Calendar.YEAR)%>";
	var getfile;
	var datestringjs;
	while(getdate > 0)
	{
		getdate--;
		if(getdate<10)
			datestringjs="0"+getdate;
		else
			datestringjs=getdate;
		if(getdate != 0)
		{
		getfile=getmonth+"-"+datestringjs+"-"+getyear;
		mNewOp = new Option(getfile,getfile);
		document.forms[0].datesend.options[count+1] = mNewOp;
		}
		count++;
	}

		
    count=0;
	while(comps[count] != null)
	{
		myNewOption = new Option(comps[count],comps[count]);
		document.forms[0].comp.options[count+1] = myNewOption;
		count++;
	}
    count=0;
        while(alarms[count]!=null)
	{
	    myNewOption1 = new Option(alarms[count],alarms[count]);
            document.forms[0].al_id.options[count+1] = myNewOption1;
            count++;
	}
	    
        
	
	document.forms[0].fav.value = value;
	document.forms[0].comp.value = cmp_value;
	document.forms[0].al_id.value = alarm_value;
	document.forms[0].datesend.value = date_value;
}

</script>

<jsp:include page="/monitorserver/common/topband.jsp" flush="false">
<jsp:param name="helpFile" value="/help/oam.jsp" />
	<jsp:param name="showJspHeading" value="N" />
</jsp:include>
<jsp:include page="/monitorserver/common/mtopband.jsp"/>

<form name="form1" action="LmDisplayAlarmLog.jsp" method = "get">

<table width="700" border="0" align="center">
  <tr> 
    <td width="700" colspan="6" class="heading">		
      <% if(data.compareTo("ALL") == 0)
	{
		out.println("<div align=\"center\" class=\"heading\"> <STRONG>Displaying All Alarms </STRONG></div>");
	}

	else if(data.compareTo("MAJOR") == 0)
	{
		out.println("<div align=\"center\" class=\"heading\">Displaying Major Alarms</div>");
	}
	else if(data.compareTo("MINOR") == 0)
	{
		out.println("<div align=\"center\" class=\"heading\">Displaying Minor Alarms</div>");
	}
	else if(data.compareTo("FATAL") == 0)
	{
		out.println("<div align=\"center\" class=\"heading\">Displaying Fatal Alarms</div>");
	}
	%>
       </td>
  </tr>
   
  <tr> 
  <br>
    <td width="175" align="center" class="heading">Criticality: </td>
     <td width="175" align="center" class="heading">Component:</td>
	  <td width = "150" align="center" class="heading">Alarm Id:	</td>
	   <td width="150" align="center" class="heading">Date: </td>
		<td width = "150" align="center" class="heading">Lines:</td>
</tr>
<tr>
 <td width="175" align="center" class="heading">

        <select name="fav"><br>
          <option value="MINOR" >Minor</option>
          <option value="MAJOR">Major</option>
          <option value="FATAL">Fatal</option>
          <option value="ALL">All</option>
        </select>
      
    </td>
        
      <td width="175" align="center" class="heading">
	  
	  <select name="comp" >
        <option value="ALL"	>ALL</option>
      </select>
	
	    </td>
	<td width = "150" align="center" class="heading">	
	 <select name="al_id" >	   <option value=ALL>ALL</option>            </select>
			       
		 </td> 
	  <td width="150" align="center" class="heading">
	  <select name="datesend">
	    <option value="TODAY">Today</option>
	  </select>
	</td> 
	<td width = "150" align="center" class="heading">
	<input type="text" name="lines" value=<%=lines%> size="3" maxlength="3">
	
	</td> 
    
	<td align="center">
	 <input type="submit" value="submit" class="btn" name="submit">	</td>
	
<!-- ********************** ********************** 
	********************* Added by Nitin *********************
-->
	
	<input type="hidden" name="inacc" value=<%=inaccC%> >

 </form>
  </tr>
  <tr> 
    <td width="207" colspan="2">&nbsp;</td>
  </tr>
</table>
<table border="0" cellspacing="1" align="center" cellpadding="1" width="90%" class="topbackbg">
  <tr > 
  
	<td  class="tabhead" align="center" width="7%">Time</td>
	<td  class="tabhead"  align="center" width="5%" >Alarm ID</td>
	<td class="tabhead"   align="center" width="7%">Component</td>
	<td  class="tabhead" align="center" width="10%">Criticality</td>
	<td  class="tabhead" align="center"  width="50%">Remarks</td>
	<td  class="tabhead" align="center" width="20%">Alarm Messages</td>

	    
      
  </tr>
 
  <%	
	String line;
	int globalflag=0;
	Vector todisplayline=new Vector();//*****nitin****//
	try
	{
		BufferedReader input = null;
		FileReader file = new FileReader(filename);
		input = new BufferedReader(file);
		int linecount=0;
		int flag;
		int tlines=0;
		int cmp_index=0;
		String tokenstring;
		//*******nitin**************//
		while( (line = input.readLine()) != null){
		line=line.trim();
		cmp=cmp.trim();
		
		StringTokenizer stTemp=new StringTokenizer(line,"#");
		String fin=null,fin1=null; 
		for(int k=1;k<=3;k++)
		{
			//out.println("stTemp is ="+stTemp.nextToken());
			String str=stTemp.nextToken();
	                if(k==2)
			  fin1=str;
			 fin=str; 			  		
		}
		 
		//out.println("*********************");
		        if((cmp.equals("ALL"))&&(ala_id.equals("ALL")))
			   todisplayline.add(line);
		     /* else if(cmp.equals("ALL"))
			{
			     if((fin1.compareTo(ala_id)==0))
			        todisplayline.add(line);
			}
			else if(ala_id.equals("ALL"))
			{
			     if((fin.compareTo(ala_id)==0))
			        todisplayline.add(line);
			}*/
			else
			if((fin.compareTo(cmp)==0)&&(ala_id.equals("ALL")))
			{
			 todisplayline.add(line);
			  continue;
			}
			else
			if((cmp.equals("ALL"))&&(fin1.compareTo(ala_id)==0))
			{
			      todisplayline.add(line);
			      continue;
			}
			else if((fin.compareTo(cmp)==0)&&(fin1.compareTo(ala_id)==0))
			{
				todisplayline.add(line);
				//out.println("Size of Vector ="+todisplayline.size());
				continue;
			}
			
		}
		
	
		int sizeofvector = todisplayline.size();
		//if(sizeofvector!=0)
		//line =String.valueOf(todisplayline.elementAt(sizeofvector-1));

           
		
			
		while(sizeofvector >= 1)
		{
		// *************nitin**********//	
      			tlines++;
			//if(tlines >= toprint)
			if(tlines <= getlines+1)
			{
			flag=0;
			//***********nitin**************//
			line = String.valueOf(todisplayline.elementAt(sizeofvector-1));
	
			StringTokenizer st = new StringTokenizer(line,"#");
			//************nitin************//
			int fcritical=0;
			int fcomp=0;
			int tcount=0;
			while(st.hasMoreTokens() && flag==0)
			{
				tokenstring=st.nextToken();
				if(tcount==3)
				{
				 if(data.compareTo(tokenstring)==0 || data.compareTo("ALL")==0)
					 fcritical=1;
				}
				if(tcount==2)
				{
				  if(cmp.compareTo(tokenstring)==0 || cmp.compareTo("ALL")==0)
					  fcomp=1;
				}
				tcount++;

				if(fcritical == 1 && fcomp == 1)
				{
					if((linecount%2)==0)
					{
						out.println("<tr class=\"tabcol\" >");
					}
					else
					{
					//out.println("<tr bgcolor=\"#E4E4E4\">");
						out.println("<tr class=\"tabcolhead\"");
					}
	
					flag=1;
					StringTokenizer maintoken = new StringTokenizer(line,"#");
					globalflag=1;
					int tokencount=0;
					while(maintoken.hasMoreTokens())
					{
						String waste = maintoken.nextToken();
						
						try
						{	
						if(tokencount==0)// means value before first #
						// retrieve time from file
						waste=waste.substring(5,14);
						}
						catch(Exception e)
						{
						}
						//tokencount == 1-> Alarm ID
						//tokencount == 2-> Component Name
						//tokencount == 3-> Alarm Criticality
						//tokencount == 4-> Remarks
						//tokencount == 5-> Alarm Message
						//if(tokencount != 1)
						{
							out.println("<b>");
							//out.println("<td class=\"tabcol\">");
							// Sandeep Kumar
							//System.out.println("###############3\n"+waste);
							if (waste.compareTo("FATAL") == 0)
		out.println("<td  height= \"20\" bgcolor=\"#cc0000\">");
		else if (waste.compareTo("MAJOR") ==0)
		out.println("<td height= \"20\" bgcolor=\"#ff9933\"> ");
							else if (waste.compareTo("MINOR") ==0)
								out.println("<td  height= \"20\" bgcolor=\"#336600\"> ");
							else {
								out.println("<td   height= \"20\" class=\"fontoam\"> "); 
					if(tokencount == 3) {
						// retrieve criticality by removing CLR
							waste=waste.substring(4,9);
								}
							}
							out.println(waste);
							//System.out.println("\n"+waste);
							out.println("</font> </td></b>");
						}
						tokencount++;
					}
						linecount++;
						out.println("</tr>");
					}
	
				}
	  			
			
			}
			sizeofvector--;//***nitin****//
			
		 }//***nitin ***//
              
                 input.reset();
		}
		catch(java.io.IOException e)
		{
		
			//System.out.println("Exception"+e);
		}

	out.println("</table>");
	if(globalflag == 0)
        {  

	    out.println("<div align=\"center\"> No Records Retrieved</div><br><br><br>");	
      %> <script>form1.comp.value = "";
               form1.al_id.value ="";</script>
	<%       
       
       }


	
	if (confileread == 0) {
		out.println("<div align=\"center\">Config file not found ");
		out.println("[AlmGuiConf.conf]");
		out.println("</div><br><br><br>");
	}
	
	
	if (confileread == 1) {
		out.println("<div align = \"center\"> <font color=\"#CCCCCC\">");
		out.println("Report Generated on ");
		java.util.Date date1 = new java.util.Date();
		out.println(date1);
		out.println("<br>");
		out.println("Log file contains ["+nooflines+"] records");
		out.println("<br>");
		out.println("Reading Alarm Log File ["+filename+"]");
		out.println(" </div>");
	}
%>


	

<script>
javascript:getSelect()
</script>

<jsp:include page="./monitorserver/common/bottomband.jsp" />


