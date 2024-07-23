package com.btsl.xl;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

/**
 * @(#)ExcelStyle.java Copyright(c) 2006, Bharti Telesoft Ltd. All Rights
 *                     Reserved
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     Author Date History
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     Ved Prakash Sharma 21/09/2006 Initial Creation
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     This method use for the font style of xls files.
 */
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;

/**
 * @author ved.sharma TODO To change the template for this generated type
 *         comment go to Window - Preferences - Java - Code Style - Code
 *         Templates
 */
public class ExcelStyle {
    private static Log _log = LogFactory.getLog(ExcelStyle.class.getName());

    /**
	 * ensures no instantiation
	 */
	private ExcelStyle(){
		
	}
	
    /**
     * @return
     * @throws BTSLBaseException
     */
    public static WritableCellFormat getHeadingFont() throws BTSLBaseException {
        // if(_log.isDebugEnabled()) _log.debug("getHeadingFont", " Entered ");
        WritableCellFormat headingHeader = null;
        final String METHOD_NAME = "getHeadingFont";
        try {
            WritableFont timesfont3 = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, false);
            timesfont3.setColour(Colour.BLACK);
            headingHeader = new WritableCellFormat(timesfont3);
            headingHeader.setBackground(Colour.LIGHT_ORANGE);
            headingHeader.setAlignment(Alignment.CENTRE);
            headingHeader.setBorder(Border.ALL, BorderLineStyle.THIN);
            headingHeader.setVerticalAlignment(VerticalAlignment.BOTTOM);
        } catch (Exception e) {
            _log.error("getHeadingFont", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ExcelStyle", "getHeadingFont", "Exception=" + e.getMessage());
        }
        // if(_log.isDebugEnabled()) _log.debug("getHeadingFont"," Exit ");
        return headingHeader;
    }

    /**
     * @return
     * @throws BTSLBaseException
     */
    public static WritableCellFormat getTopHeadingFont() throws BTSLBaseException {
        // if(_log.isDebugEnabled()) _log.debug("getTopHeadingFont",
        // " Entered ");
        WritableCellFormat headingHeader = null;
        final String METHOD_NAME = "getTopHeadingFont";
        try {
            WritableFont timesfont3 = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, false);
            timesfont3.setColour(Colour.WHITE);
            headingHeader = new WritableCellFormat(timesfont3);
            headingHeader.setBackground(Colour.DARK_BLUE);
            headingHeader.setAlignment(Alignment.CENTRE);
            headingHeader.setBorder(Border.ALL, BorderLineStyle.THIN);
            headingHeader.setVerticalAlignment(VerticalAlignment.BOTTOM);
        } catch (Exception e) {
            _log.error("getTopHeadingFont", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ExcelStyle", "getTopHeadingFont", "Exception=" + e.getMessage());
        }
        // if(_log.isDebugEnabled()) _log.debug("getTopHeadingFont"," Exit ");
        return headingHeader;
    }

    /**
     * @return
     * @throws BTSLBaseException
     */
    public static WritableCellFormat getSecondTopHeadingFont() throws BTSLBaseException {
        // if(_log.isDebugEnabled())
        // _log.debug("getSecondTopHeadingFont"," Entered ");
        WritableCellFormat headingHeader = null;
        final String METHOD_NAME = "getSecondTopHeadingFont";
        try {
            WritableFont timesfont3 = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, false);
            timesfont3.setColour(Colour.WHITE);
            headingHeader = new WritableCellFormat(timesfont3);
            headingHeader.setBackground(Colour.ORANGE);
            headingHeader.setBorder(Border.ALL, BorderLineStyle.THIN);
            headingHeader.setVerticalAlignment(VerticalAlignment.BOTTOM);
        } catch (Exception e) {
            _log.error("getSecondTopHeadingFont", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ExcelStyle", "getSecondTopHeadingFont", "Exception=" + e.getMessage());
        }
        // if(_log.isDebugEnabled())
        // _log.debug("getSecondTopHeadingFont"," Exit ");
        return headingHeader;
    }

    /**
     * @return
     * @throws BTSLBaseException
     */
    public static WritableCellFormat getSecondTopHeadingFont2() throws BTSLBaseException {
        // if(_log.isDebugEnabled())
        // _log.debug("getSecondTopHeadingFont"," Entered ");
        WritableCellFormat headingHeader = null;
        final String METHOD_NAME = "getSecondTopHeadingFont2";
        try {
            WritableFont timesfont3 = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, false);
            timesfont3.setColour(Colour.WHITE);
            headingHeader = new WritableCellFormat(timesfont3);
            headingHeader.setBackground(Colour.ORANGE);
            headingHeader.setBorder(Border.ALL, BorderLineStyle.THIN);
            headingHeader.setVerticalAlignment(VerticalAlignment.BOTTOM);
            headingHeader.setAlignment(Alignment.CENTRE);
        } catch (Exception e) {
            _log.error("getSecondTopHeadingFont2", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ExcelStyle", "getSecondTopHeadingFont2", "Exception=" + e.getMessage());
        }
        // if(_log.isDebugEnabled())
        // _log.debug("getSecondTopHeadingFont"," Exit ");
        return headingHeader;
    }

    public static WritableCellFormat getThirdTopHeadingFont() throws BTSLBaseException {
        // if(_log.isDebugEnabled())
        // _log.debug("getSecondTopHeadingFont"," Entered ");
        WritableCellFormat headingHeader = null;
        final String METHOD_NAME = "getThirdTopHeadingFont";
        try {
            WritableFont timesfont3 = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, false);
            timesfont3.setColour(Colour.WHITE);
            headingHeader = new WritableCellFormat(timesfont3);
            headingHeader.setBackground(Colour.ORANGE);
            headingHeader.setBorder(Border.ALL, BorderLineStyle.THIN);
            headingHeader.setVerticalAlignment(VerticalAlignment.BOTTOM);
        } catch (Exception e) {
            _log.error("getThirdTopHeadingFont", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ExcelStyle", "getThirdTopHeadingFont", "Exception=" + e.getMessage());
        }
        // if(_log.isDebugEnabled())
        // _log.debug("getSecondTopHeadingFont"," Exit ");
        return headingHeader;
    }

    /**
     * @return
     * @throws BTSLBaseException
     */
    public static WritableCellFormat getDataStyle() throws BTSLBaseException {
        // if(_log.isDebugEnabled()) _log.debug("getDataStyle", " Entered ");
        WritableCellFormat headingHeader = null;
        final String METHOD_NAME = "getDataStyle";
        try {
            WritableFont timesfont3 = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false);
            headingHeader = new WritableCellFormat(timesfont3);
            headingHeader.setVerticalAlignment(VerticalAlignment.BOTTOM);
            headingHeader.setBorder(Border.ALL, BorderLineStyle.THIN);
        } catch (Exception e) {
            _log.error("getDataStyle", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ExcelStyle", "getDataStyle", "Exception=" + e.getMessage());
        }
        // if(_log.isDebugEnabled()) _log.debug("getDataStyle"," Exit ");
        return headingHeader;
    }

    /**
     * @return
     * @throws BTSLBaseException
     */
    public static WritableCellFormat getSummaryHeadingFont() throws BTSLBaseException {
        // if(_log.isDebugEnabled())
        // _log.debug("getSummaryHeadingFont"," Entered ");
        WritableCellFormat headingHeader = null;
        final String METHOD_NAME = "getSummaryHeadingFont";
        try {
            WritableFont timesfont3 = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, false);
            timesfont3.setColour(Colour.BLACK);
            headingHeader = new WritableCellFormat(timesfont3);
            headingHeader.setBackground(Colour.GREY_25_PERCENT);
            headingHeader.setBorder(Border.ALL, BorderLineStyle.THIN);
            headingHeader.setVerticalAlignment(VerticalAlignment.BOTTOM);
            headingHeader.setAlignment(Alignment.CENTRE);
        } catch (Exception e) {
            _log.error("getSummaryHeadingFont", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ExcelStyle", "getSummaryHeadingFont", "Exception=" + e.getMessage());
        }
        // if(_log.isDebugEnabled())
        // _log.debug("getSummaryHeadingFont"," Exit ");
        return headingHeader;
    }

    /**
     * @return
     * @throws BTSLBaseException
     */
    public static WritableCellFormat getTotalSummaryFont() throws BTSLBaseException {
        // if(_log.isDebugEnabled())
        // _log.debug("getTotalSummaryFont"," Entered ");
        WritableCellFormat headingHeader = null;
        final String METHOD_NAME = "getTotalSummaryFont";
        try {
            WritableFont timesfont3 = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, false);
            timesfont3.setColour(Colour.BLACK);
            headingHeader = new WritableCellFormat(timesfont3);
            headingHeader.setBackground(Colour.GREY_25_PERCENT);
            headingHeader.setBorder(Border.ALL, BorderLineStyle.THIN);
            headingHeader.setVerticalAlignment(VerticalAlignment.BOTTOM);
            // headingHeader.setAlignment(Alignment.CENTRE);
        } catch (Exception e) {
            _log.error("getTotalSummaryFont", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ExcelStyle", "getTotalSummaryFont", "Exception=" + e.getMessage());
        }
        // if(_log.isDebugEnabled()) _log.debug("getTotalSummaryFont"," Exit ");
        return headingHeader;
    }

    /**
     * @return
     * @throws BTSLBaseException
     */
    public static WritableCellFormat getSummaryHeadingFont1() throws BTSLBaseException {
        // if(_log.isDebugEnabled())
        // _log.debug("getSummaryHeadingFont1"," Entered ");
        WritableCellFormat headingHeader = null;
        final String METHOD_NAME = "getSummaryHeadingFont1";
        try {
            WritableFont timesfont3 = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, false);
            timesfont3.setColour(Colour.BLACK);
            headingHeader = new WritableCellFormat(timesfont3);
            headingHeader.setBackground(Colour.ICE_BLUE);
            headingHeader.setBorder(Border.ALL, BorderLineStyle.THIN);
            headingHeader.setVerticalAlignment(VerticalAlignment.BOTTOM);
            headingHeader.setAlignment(Alignment.CENTRE);

        } catch (Exception e) {
            _log.error("getSummaryHeadingFont1", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ExcelStyle", "getSummaryHeadingFont1", "Exception=" + e.getMessage());
        }
        // if(_log.isDebugEnabled())
        // _log.debug("getSummaryHeadingFont1"," Exit ");
        return headingHeader;
    }

    /**
     * @return
     * @throws BTSLBaseException
     */
    public static WritableCellFormat getDateHeadingFont() throws BTSLBaseException {
        // if(_log.isDebugEnabled())
        // _log.debug("getDateHeadingFont"," Entered ");
        WritableCellFormat headingHeader = null;
        final String METHOD_NAME = "getDateHeadingFont";
        try {
            WritableFont timesfont3 = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, false);
            timesfont3.setColour(Colour.WHITE);
            headingHeader = new WritableCellFormat(timesfont3);
            headingHeader.setBackground(Colour.CORAL);
            headingHeader.setAlignment(Alignment.CENTRE);
            headingHeader.setBorder(Border.ALL, BorderLineStyle.THIN);
            headingHeader.setVerticalAlignment(VerticalAlignment.BOTTOM);
        } catch (Exception e) {
            _log.error("getDateHeadingFont", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ExcelStyle", "getDateHeadingFont", "Exception=" + e.getMessage());
        }
        // if(_log.isDebugEnabled()) _log.debug("getDateHeadingFont"," Exit ");
        return headingHeader;
    }

    /**
     * @return
     * @throws BTSLBaseException
     */
    public static WritableCellFormat getServiceTypeFont() throws BTSLBaseException {
        // if(_log.isDebugEnabled())
        // _log.debug("getServiceTypeFont"," Entered ");
        WritableCellFormat headingHeader = null;
        final String METHOD_NAME = "getServiceTypeFont";
        try {
            WritableFont timesfont3 = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, false);
            timesfont3.setColour(Colour.WHITE);
            headingHeader = new WritableCellFormat(timesfont3);
            headingHeader.setBackground(Colour.CORAL);
            headingHeader.setAlignment(Alignment.CENTRE);
            headingHeader.setBorder(Border.ALL, BorderLineStyle.THIN);
            headingHeader.setVerticalAlignment(VerticalAlignment.BOTTOM);
        } catch (Exception e) {
            _log.error("getServiceTypeFont", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ExcelStyle", "getServiceTypeFont", "Exception=" + e.getMessage());
        }
        // if(_log.isDebugEnabled()) _log.debug("getDateHeadingFont"," Exit ");
        return headingHeader;
    }
}