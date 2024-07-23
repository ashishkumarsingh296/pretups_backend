package com.btsl.pretups.master.service;

import java.io.IOException;
import java.io.InputStream;


/**  
    *Interface which provides base for NetworkSummaryReportServiceImpl class
   *also declares different method for NetworkSummaryReportServiceImpl  functionalities
*/
public interface NetworkSummaryReportService {
	public InputStream download (String report_type , String from_date , String to_date,String networkCode)  throws  IOException, Exception ; 
}


