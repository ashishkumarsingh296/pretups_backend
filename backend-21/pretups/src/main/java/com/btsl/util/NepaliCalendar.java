package com.btsl.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.btsl.pretups.common.PretupsI;

/**
 * Class has functionlity to convert bikram sambat to Gregorian(AD) date
 *
 * @author bahadur baniya
 */
public class NepaliCalendar {
    static Logger logger = LoggerFactory.getLogger(NepaliCalendar.class);
    String format;
    public static final String DEFAULT_FORMAT = "dd/MM/yy";
    public static String monthNamesShort[] = {"Bai", "Je", "As", "Shra", "Bha", "Ash", "Kar", "Mang", "Pau", "Ma", "Fal", "Chai"};
    private String separator;

    public NepaliCalendar() {
        this(DEFAULT_FORMAT);
    }

    /**
     * @param format
     */
    private NepaliCalendar(String format) {
        this(format, null);
    }

    private NepaliCalendar(String format, String separator) {
        if (format.equals(DEFAULT_FORMAT)) {
            this.format = format;
        } else {
            /*throw new InvalidDateFormat(
                    "Nepali date to Gregorian Date converter only supports "
                            + DEFAULT_FORMAT);*/
        }

    }

    /**
     * converts nepali Bikram Sambat date to Gregorian date
     *
     * @param bsDate
     * @return
     */
    public String convertBsToAd(String bsDate, String dateInFormat) {
        int bsYear = 0, bsMonth = 0, bsDayOfMonth = 0;

        if (separator == null) {
            if (!matchFormat(bsDate)) {
                /*throw new InvalidDateFormat("incorrect date format  " + format
                        + " date provided was " + bsDate);*/
            }
            bsDayOfMonth = Integer.parseInt(bsDate.substring(0, 2));
            bsMonth = Integer.parseInt(bsDate.substring(3, 5));
            bsYear = Integer.parseInt(bsDate.substring(6));
        } else {
            String[] bsDates = bsDate.split(separator);
            bsYear = Integer.parseInt(bsDates[0]);
            bsMonth = Integer.parseInt(bsDates[1]);
            bsDayOfMonth = Integer.parseInt(bsDates[2]);
        }

        int lookupIndex = getLookupIndex(bsYear);
        if (validateBsDate(bsYear, bsMonth, bsDayOfMonth)) {
            return convertBsToAd(bsDate, bsMonth, bsDayOfMonth, lookupIndex, dateInFormat);
        } else {
            throw new IllegalStateException("invalid BS date");
        }

    }

    /**
     * converts Gregorian date to Bikram Sambat date
     *
     * @param adDate
     * @return Bikram Sambat date - String type
     */
    public String convertAdToBs(String adDate, String dateInFormat) throws ParseException {
    	String tempDate = adDate.replace("/", "-");
        String getCurrentYear[] = tempDate.split("-");
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        Date current = df.parse(tempDate);
        Calendar adCurrent = new GregorianCalendar(current.getYear(), current.getMonth(), current.getDate());
        Date start = null;
        int equBs = NepaliCalendarLookup.lookupNepaliYearStart;
        Integer monthDay[] = null;
        int count = 0;
        DateFormat df1 = new SimpleDateFormat("dd-MMM-yyyy");
        for (int i = 0; i < NepaliCalendarLookup.lookup.size(); i++) {
            String getStartYear[] = NepaliCalendarLookup.lookup.get(i)[0].split("-");
            if (getStartYear[2].equals(getCurrentYear[2])) {
                start = df1.parse(NepaliCalendarLookup.lookup.get(i)[0]);
                monthDay = NepaliCalendarLookup.monthDays.get(i);
                equBs += i;
                if (start.getTime() >= current.getTime()) {
                    start = df1.parse(NepaliCalendarLookup.lookup.get(i - 1)[0]);
                    equBs -= 1;
                }
            }
        }
        Calendar adStart = new GregorianCalendar(start.getYear(), start.getMonth(), start.getDate());
        long diff = adCurrent.getTime().getTime() - adStart.getTime().getTime();
        long difference = diff / (1000 * 60 * 60 * 24);
        int nepYear = (int) equBs, nepMonth = 0, nepDay = 1, DaysInMonth;
        while (difference != 0) {
            if (difference >= 0) {
                DaysInMonth = monthDay[nepMonth];
                nepDay++;
                if (nepDay > DaysInMonth) {
                    nepMonth++;
                    nepDay = 1;
                }
                if (nepMonth >= 12) {
                    nepYear++;
                    nepMonth = 0;
                }
                difference--;
            }
        }

        nepMonth += 1;
        return getDateInFormat(dateInFormat, nepYear, nepMonth, nepDay);
    }
    
    public String getDateInFormat(String dateInFormat, int nepYear, int nepMonth, int nepDay) {
		if(PretupsI.DATE_FORMAT_DDMMYYYY.equalsIgnoreCase(dateInFormat)) {
			return BTSLUtil.getTwoDigit(nepDay) + PretupsI.FORWARD_SLASH + BTSLUtil.getTwoDigit(nepMonth) + 
					PretupsI.FORWARD_SLASH + BTSLUtil.getTwoDigit(nepYear);
		} else if(PretupsI.DATE_FORMAT_YYMMDD.equalsIgnoreCase(dateInFormat)) {
			return BTSLUtil.getTwoDigit(nepYear).substring(2) + PretupsI.FORWARD_SLASH + BTSLUtil.getTwoDigit(nepMonth) + 
					PretupsI.FORWARD_SLASH + BTSLUtil.getTwoDigit(nepDay);
		} else if(PretupsI.DATE_FORMAT_DDMMYY.equalsIgnoreCase(dateInFormat)) {
			return BTSLUtil.getTwoDigit(nepDay) + PretupsI.FORWARD_SLASH + BTSLUtil.getTwoDigit(nepMonth) + 
					PretupsI.FORWARD_SLASH + BTSLUtil.getTwoDigit(nepYear).substring(2);
		}
		return BTSLUtil.getTwoDigit(nepYear) + PretupsI.FORWARD_SLASH + BTSLUtil.getTwoDigit(nepMonth) + 
				PretupsI.FORWARD_SLASH + BTSLUtil.getTwoDigit(nepDay);
	}

    /**
     * converts nepali bikram sambat date to Gregorian date
     *
     * @param bsDate
     * @param bsMonth
     * @param bsDayOfMonth
     * @param lookupIndex
     * @return
     */
    private String convertBsToAd(String bsDate, int bsMonth, int bsDayOfMonth,
                               int lookupIndex, String dateInFormat) {
        int numberOfDaysPassed = bsDayOfMonth - 1;// number of days
        // passed
        // since
        // start of year
        // 1 is decreased as year start day has already included
        for (int i = 0; i <= bsMonth - 2; i++) {
            numberOfDaysPassed += NepaliCalendarLookup.monthDays.get(lookupIndex)[i];
        }
        // From look up table we need to find corresponding english date
        // for
        // nepali new year
        // we need to add number of days passed from new year to english
        // date
        // which will find
        // corresponding english date
        // we need what starts
        // where...
        String DATE_FORMAT = "dd-MMM-yyyy";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                DATE_FORMAT);
        sdf.setLenient(false);
        Calendar c1 = Calendar.getInstance();
        try {
            c1.setTime(sdf.parse(NepaliCalendarLookup.lookup.get(lookupIndex)[0]));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c1.add(Calendar.DATE, numberOfDaysPassed);
        java.text.SimpleDateFormat sdf1 = new java.text.SimpleDateFormat(
                dateInFormat);
        sdf1.setLenient(false);
        return sdf1.format(c1.getTime());
        
    }

    /**
     * validates nepali year
     *
     * @param bsYear
     * @param bsMonth
     * @param bsDayOfMonth
     * @return boolean if there is no lookup for provided year , false is
     * returned
     */
    private boolean validateBsDate(int bsYear, int bsMonth, int bsDayOfMonth) {
        /*if (bsYear < NepaliCalendarLookup.lookupNepaliYearStart) {
            throw new CustomerDiedException();
        } else if (bsYear > (NepaliCalendarLookup.lookupNepaliYearStart + NepaliCalendarLookup.monthDays.size() - 1)) {
            throw new CustomerYetToBornException();
        }*/
        if (NepaliCalendarLookup.lookupNepaliYearStart <= bsYear
                && bsYear <= (NepaliCalendarLookup.lookupNepaliYearStart + NepaliCalendarLookup.monthDays.size() - 1)) {
            logger.debug("debug: converter supports  year " + bsYear);
            if (bsMonth >= 1 && bsMonth <= 12) {
                logger.debug("debug: month between 1 and 12");
                int dayOfMonth = NepaliCalendarLookup.monthDays.get(getLookupIndex(bsYear))[bsMonth - 1];
                logger.debug("debug:total days in month " + dayOfMonth);
                if (bsDayOfMonth <= dayOfMonth) {
                    return true;
                } else {
                    logger.warn("invalid day of month " + bsDayOfMonth
                            + " for year " + bsYear + " and month " + bsMonth);
                    /*throw new InvalidBsDayOfMonthException(
                            "invalid day of month " + bsDayOfMonth
                                    + " for year " + bsYear + " and month "
                                    + bsMonth);*/
                }
            }
        }
        return false;
    }

    /**
     * gets array lookup index in lookup datastructure
     *
     * @param bsYear
     * @return
     */
    private int getLookupIndex(int bsYear) {
        logger.debug("lookup index " + (bsYear - NepaliCalendarLookup.lookupNepaliYearStart));
        return bsYear - NepaliCalendarLookup.lookupNepaliYearStart;
    }

    /**
     * confirms whether date format is valid or not. date format should be
     * mm-dd-yyyy
     *
     * @param bsDate
     * @return
     */
    public boolean matchFormat(String bsDate) {
        if (format.equals("dd/MM/yy")) {
            logger.debug("date format wants to test is " + format
                    + " real text is " + bsDate);
            Pattern p = Pattern.compile("\\d{2}\\d{2}\\d{4}");
            return p.matcher(bsDate).matches();
        } else {
            logger.debug("date format is " + format);
            return Pattern.matches("\\d{2}-\\d{2}-\\d{4}", bsDate);
        }
    }
}
