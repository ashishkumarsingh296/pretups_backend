package com.btsl.pretups.xl;
/* BatchUserAssociationExcelRW.java
 * Name                          Date           				Project
 *------------------------------------------------------------------------
 * Ankit Agarwal              	Oct 1, 2015         			Idea2
 *------------------------------------------------------------------------
 * Copyright (c) Mahindra Comviva
 */

/**
 * 
 * @author ankit.agarwal
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.util.MessageResources;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.xl.ExcelFileConstants;

import jxl.Cell;
import jxl.CellView;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFeatures;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class BatchUserAssociationExcelRW {

	private static Log _log = LogFactory.getLog(BatchUserCreationExcelRW.class.getName());
	private static final int COLUMN_MARGE=10;
	private WritableCellFeatures cellFeatures=new WritableCellFeatures();
	private WritableFont times12font = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, true);
	private WritableCellFormat times12format = new WritableCellFormat (times12font);
	private WritableFont times16font = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, true);
	private WritableCellFormat times16format = new WritableCellFormat (times16font);
	private MessageResources p_messages=null;
	private Locale p_locale=null;
	private CellView  lock = new CellView ();
	
   /** @param worksheet2
    * @param col
    * @param row
    * @param p_hashMap
    * @return
    * @throws Exception
    */
   private int writeGradeCode(WritableSheet worksheet2, int col,int row, HashMap p_hashMap) throws Exception
   {
       if(_log.isDebugEnabled()) {
		_log.debug("writeGradeCode"," p_hashMap size="+p_hashMap.size()+" p_locale: "+p_locale+" col="+col+" row="+row);
	}
       final String METHOD_NAME = "writeGradeCode";
       try 
       {
           String keyName = p_messages.getMessage(p_locale,"batchusercreation.mastersheet.gradecodeheading");
           
           Label label = new Label(col,row,keyName, times16format);
           worksheet2.mergeCells(col,row,col+2,row);
           worksheet2.addCell(label);
           row++;
           col=0;
           keyName = p_messages.getMessage(p_locale,"batchusercreation.mastersheet.gradecodeheading.note");
           label = new Label(col,row,keyName);
           worksheet2.mergeCells(col,row,COLUMN_MARGE,row);
           worksheet2.addCell(label);
           row++;
           col=0;

           keyName = p_messages.getMessage(p_locale,"batchusercreation.mastersheet.gradecodetype.gradecode");
           label = new Label(col,row,keyName, times16format);
           worksheet2.addCell(label);
           keyName = p_messages.getMessage(p_locale,"batchusercreation.mastersheet.gradecode.gradename");
           label = new Label(++col,row,keyName, times16format);
           worksheet2.addCell(label);
           row++;
           col=0;
           
           ArrayList outletList = (ArrayList)p_hashMap.get(PretupsI.BATCH_USR_GRADE_LIST);
           GradeVO listValueVO= null;
           if(outletList!=null)
           {
           	for(int i=0,j=outletList.size();i<j;i++)
           	{
           	    col=0;
           	    listValueVO = (GradeVO)outletList.get(i);
           	    label = new Label(col,row,listValueVO.getGradeCode());
           		worksheet2.addCell(label);
           		label = new Label(col+1,row,listValueVO.getGradeName());
           		worksheet2.addCell(label);
           		row++;
           	}
           }
           return row;
       } 
       catch (RowsExceededException e) 
       {
           _log.errorTrace(METHOD_NAME, e);
			_log.error("writeGradeCode"," Exception e: "+e.getMessage());
			throw e;
       } catch (WriteException e) {
           _log.errorTrace(METHOD_NAME, e);
			_log.error("writeGradeCode"," Exception e: "+e.getMessage());
			throw e;
       }
   }
   
   private int writeTransferProfile(WritableSheet worksheet2, int col,int row, HashMap p_hashMap) throws Exception
   {
       if(_log.isDebugEnabled()) {
		_log.debug("writeTransferProfile"," p_hashMap size="+p_hashMap.size()+" p_locale: "+p_locale+" col="+col+" row="+row);
	}
       final String METHOD_NAME = "writeTransferProfile";
       try 
       {
           String keyName = p_messages.getMessage(p_locale,"batchusercreation.mastersheet.transferprofileheading");
           
           Label label = new Label(col,row,keyName, times16format);
           worksheet2.mergeCells(col,row,col+2,row);
           worksheet2.addCell(label);
           row++;
           col=0;
           keyName = p_messages.getMessage(p_locale,"batchusercreation.mastersheet.transferprofileheading.note");
           label = new Label(col,row,keyName);
           worksheet2.mergeCells(col,row,COLUMN_MARGE,row);
           worksheet2.addCell(label);
           row++;
           col=0;

           keyName = p_messages.getMessage(p_locale,"batchusercreation.mastersheet.transferprofiletype.transferprofilecode");
           label = new Label(col,row,keyName, times16format);
           worksheet2.addCell(label);
           keyName = p_messages.getMessage(p_locale,"batchusercreation.mastersheet.transferprofiletype.transferprofilename");
           label = new Label(++col,row,keyName, times16format);
           worksheet2.addCell(label);
           row++;
           col=0;
           
           ArrayList outletList = (ArrayList)p_hashMap.get(PretupsI.BATCH_USR_TRANSFER_CONTROL_PRF_LIST);
           TransferProfileVO profileValueVO= null;
           if(outletList!=null)
           {
           	for(int i=0,j=outletList.size();i<j;i++)
           	{
           	    col=0;
           	    profileValueVO = (TransferProfileVO)outletList.get(i);
           	    label = new Label(col,row,profileValueVO.getProfileId());
           		worksheet2.addCell(label);
           		label = new Label(col+1,row,profileValueVO.getProfileName());
           		worksheet2.addCell(label);
           		row++;
           	}
           }
           return row;
       } 
       catch (RowsExceededException e) 
       {
           _log.errorTrace(METHOD_NAME, e);
			_log.error("writeTransferProfile"," Exception e: "+e.getMessage());
			throw e;
       } catch (WriteException e) {
           _log.errorTrace(METHOD_NAME, e);
			_log.error("writeTransferProfile"," Exception e: "+e.getMessage());
			throw e;
       }
   }

   
   private int writeCommisionProfile(WritableSheet worksheet2, int col,int row, HashMap p_hashMap) throws Exception
   {
       if(_log.isDebugEnabled()) {
		_log.debug("writeCommisionProfile"," p_hashMap size="+p_hashMap.size()+" p_locale: "+p_locale+" col="+col+" row="+row);
	}
       final String METHOD_NAME = "writeCommisionProfile";
       try 
       {
           String keyName = p_messages.getMessage(p_locale,"batchusercreation.mastersheet.commisionprofileheading");
           
           Label label = new Label(col,row,keyName, times16format);
           worksheet2.mergeCells(col,row,col+2,row);
           worksheet2.addCell(label);
           row++;
           col=0;
           keyName = p_messages.getMessage(p_locale,"batchusercreation.mastersheet.commisionprofileheading.note");
           label = new Label(col,row,keyName);
           worksheet2.mergeCells(col,row,COLUMN_MARGE,row);
           worksheet2.addCell(label);
           row++;
           col=0;

           keyName = p_messages.getMessage(p_locale,"batchusercreation.mastersheet.commisionprofiletype.commisionprofilecode");
           label = new Label(col,row,keyName, times16format);
           worksheet2.addCell(label);
           keyName = p_messages.getMessage(p_locale,"batchusercreation.mastersheet.commisionprofiletype.commisionprofilename");
           label = new Label(++col,row,keyName, times16format);
           worksheet2.addCell(label);
           row++;
           col=0;
           
           ArrayList outletList = (ArrayList)p_hashMap.get(PretupsI.BATCH_USR_COMMISION_PRF_LIST);
           CommissionProfileSetVO listValueVO= null;
           if(outletList!=null)
           {
           	for(int i=0,j=outletList.size();i<j;i++)
           	{
           	    col=0;
           	    listValueVO = (CommissionProfileSetVO)outletList.get(i);
           	    label = new Label(col,row,listValueVO.getCommProfileSetId());
           		worksheet2.addCell(label);
           		label = new Label(col+1,row,listValueVO.getCommProfileSetName());
           		worksheet2.addCell(label);
           		row++;
           	}
           }
           
           
           
           return row;
       } 
       catch (RowsExceededException e) 
       {
           _log.errorTrace(METHOD_NAME, e);
			_log.error("writeCommisionProfile"," Exception e: "+e.getMessage());
			throw e;
       } catch (WriteException e) {
           _log.errorTrace(METHOD_NAME, e);
			_log.error("writeCommisionProfile"," Exception e: "+e.getMessage());
			throw e;
       }
   }

   
   private int writeGradeandCommission(WritableSheet worksheet2, int col,int row, HashMap p_hashMap) throws Exception
   {
       if(_log.isDebugEnabled()) {
		_log.debug("writeGradeandCommission"," p_hashMap size="+p_hashMap.size()+" p_locale: "+p_locale+" col="+col+" row="+row);
	}
       final String METHOD_NAME = "writeGradeandCommission";
       try 
       {
           String keyName = p_messages.getMessage(p_locale,"batchusercreation.mastersheet.gradecommisionprofileheading");
           
           Label label = new Label(col,row,keyName, times16format);
           worksheet2.mergeCells(col,row,col+4,row);
           worksheet2.addCell(label);
           row++;
           col=0;
           keyName = p_messages.getMessage(p_locale,"batchusercreation.mastersheet.gradecommisionprofileheading.note");
           label = new Label(col,row,keyName);
           worksheet2.mergeCells(col,row,COLUMN_MARGE,row);
           worksheet2.addCell(label);
           row++;
           col=0;

           keyName = p_messages.getMessage(p_locale,"batchusercreation.mastersheet.gradecodetype.gradecode");
           label = new Label(col,row,keyName, times16format);
           worksheet2.addCell(label);
           keyName = p_messages.getMessage(p_locale,"batchusercreation.mastersheet.commisionprofiletype.commisionprofilecode");
           label = new Label(++col,row,keyName, times16format);
           worksheet2.addCell(label);
           row++;
           col=0;
           
           ArrayList outletList = (ArrayList)p_hashMap.get(PretupsI.BATCH_USR_COMMISION_PRF_LIST);
           CommissionProfileSetVO listValueVO= null;
           if(outletList!=null)
           {
           	for(int i=0,j=outletList.size();i<j;i++)
           	{
           	    col=0;
           	    listValueVO = (CommissionProfileSetVO)outletList.get(i);
           	    label = new Label(col,row,listValueVO.getGradeCode());
           		worksheet2.addCell(label);
           		label = new Label(col+1,row,listValueVO.getCommProfileSetId());
           		worksheet2.addCell(label);
           		row++;
           	}
           }
           
           
           
           return row;
       } 
       catch (RowsExceededException e) 
       {
           _log.errorTrace(METHOD_NAME, e);
			_log.error("writeGradeandCommission"," Exception e: "+e.getMessage());
			throw e;
       } catch (WriteException e) {
           _log.errorTrace(METHOD_NAME, e);
			_log.error("writeGradeandCommission"," Exception e: "+e.getMessage());
			throw e;
       }
   }
   
   
   
   
    
    /**Method writeGeographyListing
     * This method writes the Geography Details containing zone,area,sub area etc. [ N level geographies can exists ] 
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return
     * @throws Exception
     * @author Amit Ruwali
     */
    private int writeGeographyListing(WritableSheet worksheet2, int col,int row, HashMap p_hashMap) throws Exception
    {
        if(_log.isDebugEnabled()) {
			_log.debug("writeGeographyListing"," p_hashMap size="+p_hashMap.size()+" p_locale: "+p_locale+" col="+col+" row="+row);
		}
        final String METHOD_NAME = "writeGeographyListing";
        try 
        {
            String keyName = p_messages.getMessage(p_locale,"batchusercreation.mastersheet.avaliablegeographieslist");
            Label label = new Label(col,row,keyName, times16format);
            worksheet2.mergeCells(col,row,col+2,row);
            worksheet2.addCell(label);
            row++;
            col=0;
            keyName = p_messages.getMessage(p_locale,"batchusercreation.mastersheet.avaliablegeographieslist.note");
            label = new Label(col,row,keyName);
            worksheet2.mergeCells(col,row,COLUMN_MARGE,row);
            worksheet2.addCell(label);
            row++;
            col=0;
            //Logic for generating headings
            ArrayList list = (ArrayList)p_hashMap.get(PretupsI.BATCH_USR_GEOGRAPHY_LIST);
            UserGeographiesVO userGeographiesVO=null;
            ArrayList geoDomainTypeList=new ArrayList();
           
            if(list!=null)
            {
            	for(int i=0,j=list.size();i<j;i++)
            	{
            	    userGeographiesVO =(UserGeographiesVO)list.get(i);
            	    if(!geoDomainTypeList.contains(userGeographiesVO.getGraphDomainType())) {
						geoDomainTypeList.add(userGeographiesVO.getGraphDomainType());
					}
            	}
            }
            //Generate Headings from the ArrayList
            String endTagCode=p_messages.getMessage(p_locale,"code");
            String endTagName=p_messages.getMessage(p_locale,"name");
            
            String geoType=null;
            for(int i=0,j=geoDomainTypeList.size();i<j;i++)
            {
            	geoType=((String)geoDomainTypeList.get(i)).trim();
                keyName=p_messages.getMessage(p_locale, geoType);
                label = new Label(col,row,keyName+" "+ endTagCode+"("+geoType+")", times16format);
                worksheet2.addCell(label);
                label = new Label(++col,row,keyName+" "+ endTagName, times16format);
                worksheet2.addCell(label);
                col++;
            }
            row++;
            col=0;
            int nameOccurance=0;
            int oldseqNo=0;
            int sequence_num=0;
            if(list!=null)
            {
            	sequence_num=((UserGeographiesVO)list.get(0)).getGraphDomainSequenceNumber();
            	for(int i=0,j=list.size();i<j;i++)
            	{
            	    
            	    userGeographiesVO =(UserGeographiesVO)list.get(i);
            	    if(oldseqNo>userGeographiesVO.getGraphDomainSequenceNumber()) {
						nameOccurance-=(oldseqNo-userGeographiesVO.getGraphDomainSequenceNumber());					//for proper formatting of geo. list
					} else if(oldseqNo<userGeographiesVO.getGraphDomainSequenceNumber()) {
						nameOccurance++;
					}
            	    
            	    col=nameOccurance+userGeographiesVO.getGraphDomainSequenceNumber()-sequence_num;
            	  //Change made for batch user creation by channel user 
        	        if(userGeographiesVO.getGraphDomainSequenceNumber()==sequence_num)
            	    {
            	        col=0;
	        	        nameOccurance=0;
            	    }
        	      //End of Change made for batch user creation by channel user 
        	        label = new Label(col,row,userGeographiesVO.getGraphDomainCode());
        	        worksheet2.addCell(label);
        	        label = new Label(col+1,row,userGeographiesVO.getGraphDomainName());
        	        worksheet2.addCell(label);
        	       
        	        oldseqNo=userGeographiesVO.getGraphDomainSequenceNumber();
        	        row++;
        		}
            }
            
            
            return row;
        } 
        catch (RowsExceededException e) 
        {
            _log.errorTrace(METHOD_NAME, e);
			_log.error("writeGeographyListing"," Exception e: "+e.getMessage());
			throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
			_log.error("writeGeographyListing"," Exception e: "+e.getMessage());
			throw e;
        }
    
   
    }
    

  


    
 //added for service list
    /**
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return
     * @throws Exception
     */
    private int writeServiceType(WritableSheet worksheet2,int col,int row, HashMap p_hashMap) throws Exception
    {
        if(_log.isDebugEnabled()) {
			_log.debug("writeServiceType"," p_hashMap size="+p_hashMap.size()+" p_locale: "+p_locale+" col="+col+" row="+row);
		}
        final String METHOD_NAME = "writeServiceType";
        try 
        {
            String keyName = p_messages.getMessage(p_locale,"batchusercreation.mastersheet.service");
            
            Label label = new Label(col,row,keyName, times16format);
            worksheet2.mergeCells(col,row,col+2,row);
            worksheet2.addCell(label);
            row++;
            col=0;
            keyName = p_messages.getMessage(p_locale,"batchusercreation.mastersheet.service.note");
            label = new Label(col,row,keyName);
            worksheet2.mergeCells(col,row,COLUMN_MARGE,row);
            worksheet2.addCell(label);
            row++;
            col=0;

            keyName = p_messages.getMessage(p_locale,"batchusercreation.mastersheet.servicetype");
            label = new Label(col,row,keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale,"batchusercreation.mastersheet.servicename");
            label = new Label(++col,row,keyName, times16format);
            worksheet2.addCell(label);
            row++;
            col=0;
            
            ArrayList list = (ArrayList)p_hashMap.get(PretupsI.BATCH_USR_SERVICE_LIST);
            ListValueVO listValueVO= null;
            if(list!=null)
            {
            	for(int i=0,j=list.size();i<j;i++)
            	{
            	    col=0;
            	    listValueVO = (ListValueVO)list.get(i);
            	    label = new Label(col,row,listValueVO.getValue());
            		worksheet2.addCell(label);
            		label = new Label(col+1,row,listValueVO.getLabel());
            		worksheet2.addCell(label);
            		row++;
            	}
            }
            return row;
        } 
        catch (RowsExceededException e) 
        {
            _log.errorTrace(METHOD_NAME, e);
			_log.error("writeServiceType"," Exception e: "+e.getMessage());
			throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
			_log.error("writeServiceType"," Exception e: "+e.getMessage());
			throw e;
        }
    }
 
/**
 * readMultipleExcelSheet
 * This method will upload the exel sheet in to the database
 * @param String p_excelID
 * @param String p_fileName
 * @param boolean p_readLastSheet
 * @param int p_leftHeaderLinesForEachSheet
 * @param _map
 * @return String[][] strArr
 * @throws Exception
 */
	public String[][] readMultipleExcelSheet(String p_excelID,String p_fileName,boolean p_readLastSheet,int p_leftHeaderLinesForEachSheet,HashMap<String,String> map) throws Exception
	{
		if(_log.isDebugEnabled()) {
			_log.debug("readMultipleExcelSheet"," p_excelID: "+p_excelID+" p_fileName: "+p_fileName+" p_readLastSheet="+p_readLastSheet," p_leftHeaderLinesForEachSheet="+p_leftHeaderLinesForEachSheet);
		}
		  final String METHOD_NAME = "readMultipleExcelSheet";
		String strArr[][] = null;
		int arrRow=p_leftHeaderLinesForEachSheet;
		Workbook workbook = null;
		Sheet excelsheet = null;
		int noOfSheet=0;
		int noOfRows =0;
		int noOfcols =0;
		try
		{
			workbook = Workbook.getWorkbook(new File(p_fileName));
			noOfSheet=workbook.getNumberOfSheets();
			if(!p_readLastSheet) {
				noOfSheet=noOfSheet-1;
			}
			//Total number of rows in the excel sheet
			for(int i=0;i<noOfSheet;i++)
			{
				excelsheet = workbook.getSheet(i);
				noOfRows = noOfRows+(excelsheet.getRows()-p_leftHeaderLinesForEachSheet);
				noOfcols = excelsheet.getColumns();
			}
			//Initialization of string array
			strArr = new String[noOfRows+p_leftHeaderLinesForEachSheet][noOfcols];
			for(int i=0;i<noOfSheet;i++)
			{
				excelsheet = workbook.getSheet(i);
				noOfRows = excelsheet.getRows();
				noOfcols = excelsheet.getColumns();
				
				Cell cell = null;
				String content = null;
				String key=null;
				int[] indexMapArray=new int[noOfcols]; 
				String indexStr=null;
				for(int k=0;k<p_leftHeaderLinesForEachSheet;k++)
				{
					for(int col = 0; col < noOfcols; col++)

					{
						indexStr=null;
						key=ExcelFileConstants.getReadProperty(p_excelID,String.valueOf(col));
						if(key==null) {
							key=String.valueOf(col);
						}
						indexStr=ExcelFileConstants.getReadProperty(p_excelID,String.valueOf(col));
						if(indexStr==null) {
							indexStr=String.valueOf(col);
						}
						indexMapArray[col]=Integer.parseInt(indexStr);
						//strArr[0][indexMapArray[col]] = key;
						strArr[k][indexMapArray[col]] = key;
					}
				}
				for(int row = p_leftHeaderLinesForEachSheet; row < noOfRows; row++)
				{
					map.put(Integer.toString(arrRow+1),excelsheet.getName()+PretupsI.ERROR_LINE+(row+1));
					for(int col = 0; col < noOfcols; col++)
					{
						cell = excelsheet.getCell(col,row);
						content = cell.getContents();
						content=content.replaceAll("\n", " ");
						content=content.replaceAll("\r", " ");
						strArr[arrRow][indexMapArray[col]] = content;
					}
					arrRow++;
				}
			}
			return strArr;
		}
		catch(Exception e)
		{
			_log.errorTrace(METHOD_NAME, e);
			_log.error("readMultipleExcelSheet"," Exception e: "+e.getMessage());
			throw new BTSLBaseException(e);
		}
		finally
		{
			try {
				if (workbook != null) {
					workbook.close();
				}
			} catch (Exception e) {
				_log.errorTrace(METHOD_NAME, e);
			}
			workbook=null;
			excelsheet=null;
			if(_log.isDebugEnabled()) {
				_log.debug("readMultipleExcelSheet"," Exiting strArr: "+strArr);
			}
		}
	}
	
	
}
