package com.btsl.common;

/*
 * IDGeneratorDAO.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Abhijit Singh Chauhan 02/03/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
// commented for DB2
import com.btsl.util.Constants;
import com.ibm.icu.util.Calendar;

/**
 * 
 * @author abhijit.chauhan
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class IDGeneratorDAO {
    private static final Log _log = LogFactory.getFactory().getInstance(IDGeneratorDAO.class.getName());

    public IDGeneratorDAO() {
        super();
    }

    public long getNextID(Connection p_con, String p_idType, String p_year, String p_networkID, Date p_currentDate) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_idType:");
        	msg.append(p_idType);
        	msg.append(" p_year:");
        	msg.append(p_year);
        	msg.append(" p_networkID:");
        	msg.append(p_networkID);
        	
        	String message=msg.toString();
            _log.debug("getNextID", message);
        }
        final String METHOD_NAME = "getNextID";
        long seriesNum = 0;
        long last_mod_date_minut = 0;
        long currentDateinMinut = 0;
        String frequency = null;
        Date moddate = null;
        boolean isInitialised = false;
        boolean isRecordFound = false;
        String sqlQuery = null;
        // get the last_no, frequency and last_initialised_date from the
        // database wrt the id_year,id_type and network_code passed to it.
        // the frequency field is used to take deceision about to reset the
        // last_no field.
        // last_initialised_date is used to get the time of last initialisation
        // of the last_on field.
        // DB220120123for update WITH RS
        if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
            sqlQuery = "SELECT last_no,frequency,last_initialised_date,sysdate currentdate FROM ids WHERE id_year=? AND id_type=? AND network_code=? FOR UPDATE OF last_no WITH RS";
        } else {
    	   IDGeneratorQry idGeneratorQuery = (IDGeneratorQry)ObjectProducer.getObject(QueryConstants.ID_GENERATOR_QRY, QueryConstants.QUERY_PRODUCER);
           sqlQuery = idGeneratorQuery.selectFromIDsQry();
        }
        if (_log.isDebugEnabled()) {
            _log.debug("select query:", sqlQuery);
        }
        // date from database for N+1 as well as normal
        Date currentDBDate = null;
        try{
        try(PreparedStatement ps = p_con.prepareStatement(sqlQuery)) {
            
            ps.setString(1, p_year);
            ps.setString(2, p_idType);
            ps.setString(3, p_networkID);
            
            try(ResultSet rs = ps.executeQuery()){
            if (rs.next()) {
                isRecordFound = true;
                seriesNum = rs.getLong(1);
                frequency = rs.getString(2);
                moddate = rs.getTimestamp(3);
                currentDBDate = rs.getTimestamp(4);
            } // end of if rs.next()
            if (!isRecordFound) {
                throw new BTSLBaseException("idgenerator.creation.error.norecord");
            }
            if (moddate != null) {
                Calendar cal = BTSLDateUtil.getInstance();
                cal.setTime(moddate);
                // get the time in minuts. here cal.getTime().getTime() return
                // the milli second
                // So we divide by 1000*60 to convert it into minuts.
                last_mod_date_minut = cal.getTime().getTime() / (1000 * 60);
                currentDateinMinut = currentDBDate.getTime() / (1000 * 60);
                // get the month of the date in the database for
                // last_initialised_date field.
                int initialisedMonth = cal.get(Calendar.MONTH) + 1;
                // get the year of the date.
                int initialisedYear = cal.get(Calendar.YEAR);
                // set the calender for the current date
                cal.setTime(currentDBDate);
                // get the month of current date
                int curMonth = cal.get(Calendar.MONTH) + 1;
                // get the year of current date.
                int curYear = cal.get(Calendar.YEAR);
                // if the frequency is minuts then we have to reset the last_no
                // on minuts basis.
                if (frequency.equals(PretupsI.FREQUENCY_MINUTS)) {
                    if ((currentDateinMinut - last_mod_date_minut) < 1) {
                        seriesNum++;
                    } else {
                        seriesNum = 1;
                        isInitialised = true;
                    }
                }
                // else if frequency is hours then we have to reset the last_no
                // on hourly basis.
                else if (frequency.equals(PretupsI.FREQUENCY_HOUR)) {
                    long lastIntialisationHrs = last_mod_date_minut / 60;
                    long currtimeHrs = currentDateinMinut / 60;
                    if ((currtimeHrs - lastIntialisationHrs) < 1) {
                        seriesNum++;
                    } else {
                        seriesNum = 1;
                        isInitialised = true;
                    }
                }
                // else if frequency is day then we have to reset the last_no on
                // daily basis.
                else if (frequency.equals(PretupsI.FREQUENCY_DAY)) {
                    long lastIntialisationDay = last_mod_date_minut / (60 * 24);
                    long currtimeDay = currentDateinMinut / (60 * 24);
                    if ((currtimeDay - lastIntialisationDay) < 1) {
                        seriesNum++;
                    } else {
                        seriesNum = 1;
                        isInitialised = true;
                    }
                }
                // else if frequency is month then we have to reset the last_no
                // on monthly basis
                // here we first check the year of dates. if the year is same
                // then we check the month field
                else if (frequency.equals(PretupsI.FREQUENCY_MONTH)) {
                    if (curYear == initialisedYear) {
                        if (curMonth - initialisedMonth < 1) {
                            seriesNum++;
                        } else {
                            seriesNum = 1;
                            isInitialised = true;
                        }
                    } else {
                        seriesNum = 1;
                        isInitialised = true;
                    }
                }
                // else if frequency is year then we have to reset the last_no
                // on yearly basis.
                else if (frequency.equals(PretupsI.FREQUENCY_YEAR)) {
                    if (curYear == initialisedYear) {
                        seriesNum++;
                    } else {
                        seriesNum = 1;
                        isInitialised = true;
                    }
                }
                // else frequency is NA so we have not reset the last_no .
                else {
                    seriesNum++;
                }
            } else {
                isInitialised = true;
                seriesNum = 1;
            }
        }
        }
     

            StringBuilder stringBuilder = new StringBuilder("UPDATE ids SET last_no=?");
            // if initialisation is to be performed then we also update the
            // last_initialised_date in the ids table in database.
            if (isInitialised) {
            	stringBuilder.append(", last_initialised_date=? ");
            }
            stringBuilder.append(" WHERE id_year=? AND id_type=? AND network_code=?");
            if (_log.isDebugEnabled()) {
                _log.debug("update query:", stringBuilder);
            }
            String quer=stringBuilder.toString();
            try(PreparedStatement ps = p_con.prepareStatement(quer)){
            
            int i = 1;
            ps.setLong(i, seriesNum);
            if (isInitialised) {
            	i++;
                ps.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(currentDBDate));
            }
            i++;
            ps.setString(i, p_year);
            i++;
            ps.setString(i, p_idType);
            i++;
            ps.setString(i, p_networkID);
            int updateNum = ps.executeUpdate();
            if (updateNum == 0) {
                throw new BTSLBaseException("idgenerator.creation.error.norecord");
            }// end of updateNum==0
        }
        }// end of try
        catch (BTSLBaseException be) {
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IDGeneratorDAO[getNextID]", "", "", "", "BTSL BaseException:" + be.getMessage());
            throw new BTSLBaseException(be);
        } catch (SQLException sqle) {
            _log.error("getNextID", "SQLException:" + sqle);
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IDGeneratorDAO[getNextID]", "", "", "", "SQL Exception:" + sqle.getMessage());
          
            throw new BTSLBaseException(this, "getNextID", "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
            _log.error("getNextID", "Exception:" + e);
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IDGeneratorDAO[getNextID]", "", "", "", "Exception:" + e.getMessage());
            
            throw new BTSLBaseException(this, "getNextID", "error.general.processing",e);
        }// end of catch
        finally {
           if (_log.isDebugEnabled()) {
                _log.debug("getNextID", "Exiting currentDBDate: " + currentDBDate + " seriesNum: " + seriesNum + " currentDateinMinut: " + currentDateinMinut + " last_mod_date_minut: " + last_mod_date_minut + " moddate: " + moddate);
            }
        } // end of finally
        return seriesNum;
    }

    public long getNextID(Connection p_con, String p_idType, String p_year, ChannelTransferVO p_channelTransferVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_idType:");
        	msg.append(p_idType);
        	msg.append(" p_year:");
        	msg.append(p_year);
        	
        	String message=msg.toString();
            _log.debug("getNextID", message);
        }
        final String METHOD_NAME = "getNextID";
        long seriesNum = 0;
        long last_mod_date_minut = 0;
        long currentDateinMinut = 0;
        String frequency = null;
        Date moddate = null;
      
        boolean isInitialised = false;
        boolean isRecordFound = false;
        String sqlQuery = null;
        // get the last_no, frequency and last_initialised_date from the
        // database wrt the id_year,id_type and network_code passed to it.
        // the frequency field is used to take deceision about to reset the
        // last_no field.
        // last_initialised_date is used to get the time of last initialisation
        // of the last_on field.
        // DB220120123for update WITH RS
        if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
            sqlQuery = "SELECT last_no,frequency,last_initialised_date,sysdate currentdate FROM ids WHERE id_year=? AND id_type=? AND network_code=? FOR UPDATE OF last_no with RS";
        } else {
            IDGeneratorQry idGeneratorQuery = (IDGeneratorQry)ObjectProducer.getObject(QueryConstants.ID_GENERATOR_QRY, QueryConstants.QUERY_PRODUCER);
        	sqlQuery = idGeneratorQuery.selectFromIDsQry();
        }
        if (_log.isDebugEnabled()) {
            _log.debug("select query:", sqlQuery);
        }
        // date from database for N+1 as well as normal
        Timestamp currentDBDate = null;
        String networkCode = p_channelTransferVO.getNetworkCode();
        try{
        try(PreparedStatement ps = p_con.prepareStatement(sqlQuery)) {
            
            ps.setString(1, p_year);
            ps.setString(2, p_idType);
            ps.setString(3, networkCode);
           try(ResultSet rs = ps.executeQuery()){
            if (rs.next()) {
                isRecordFound = true;
                seriesNum = rs.getLong(1);
                frequency = rs.getString(2);
                moddate = rs.getTimestamp(3);
                currentDBDate = rs.getTimestamp(4);

            } // end of if rs.next()
            if (!isRecordFound) {
                throw new BTSLBaseException("idgenerator.creation.error.norecord");
            }

            p_channelTransferVO.setDBDateTime(currentDBDate);

            if (moddate != null) {
                Calendar cal = BTSLDateUtil.getInstance();
                cal.setTime(moddate);
                // get the time in minuts. here cal.getTime().getTime() return
                // the milli second
                // So we divide by 1000*60 to convert it into minuts.
                last_mod_date_minut = cal.getTime().getTime() / (1000 * 60);
                currentDateinMinut = currentDBDate.getTime() / (1000 * 60);
                // get the month of the date in the database for
                // last_initialised_date field.
                int initialisedMonth = cal.get(Calendar.MONTH) + 1;
                // get the year of the date.
                int initialisedYear = cal.get(Calendar.YEAR);
                // set the calender for the current date
                cal.setTime(currentDBDate);
                // get the month of current date
                int curMonth = cal.get(Calendar.MONTH) + 1;
                // get the year of current date.
                int curYear = cal.get(Calendar.YEAR);
                // if the frequency is minuts then we have to reset the last_no
                // on minuts basis.
                if (frequency.equals(PretupsI.FREQUENCY_MINUTS)) {
                    if ((currentDateinMinut - last_mod_date_minut) < 1) {
                        seriesNum++;
                    } else {
                        seriesNum = 1;
                        isInitialised = true;
                    }
                }
                // else if frequency is hours then we have to reset the last_no
                // on hourly basis.
                else if (frequency.equals(PretupsI.FREQUENCY_HOUR)) {
                    long lastIntialisationHrs = last_mod_date_minut / 60;
                    long currtimeHrs = currentDateinMinut / 60;
                    if ((currtimeHrs - lastIntialisationHrs) < 1) {
                        seriesNum++;
                    } else {
                        seriesNum = 1;
                        isInitialised = true;
                    }
                }
                // else if frequency is day then we have to reset the last_no on
                // daily basis.
                else if (frequency.equals(PretupsI.FREQUENCY_DAY)) {
                    long lastIntialisationDay = last_mod_date_minut / (60 * 24);
                    long currtimeDay = currentDateinMinut / (60 * 24);
                    if ((currtimeDay - lastIntialisationDay) < 1) {
                        seriesNum++;
                    } else {
                        seriesNum = 1;
                        isInitialised = true;
                    }
                }
                // else if frequency is month then we have to reset the last_no
                // on monthly basis
                // here we first check the year of dates. if the year is same
                // then we check the month field
                else if (frequency.equals(PretupsI.FREQUENCY_MONTH)) {
                    if (curYear == initialisedYear) {
                        if (curMonth - initialisedMonth < 1) {
                            seriesNum++;
                        } else {
                            seriesNum = 1;
                            isInitialised = true;
                        }
                    } else {
                        seriesNum = 1;
                        isInitialised = true;
                    }
                }
                // else if frequency is year then we have to reset the last_no
                // on yearly basis.
                else if (frequency.equals(PretupsI.FREQUENCY_YEAR)) {
                    if (curYear == initialisedYear) {
                        seriesNum++;
                    } else {
                        seriesNum = 1;
                        isInitialised = true;
                    }
                }
                // else frequency is NA so we have not reset the last_no .
                else {
                    seriesNum++;
                }
            } else {
                isInitialised = true;
                seriesNum = 1;
            }
        }
        }


            StringBuffer query = new StringBuffer("UPDATE ids SET last_no=?");
            // if initialisation is to be performed then we also update the
            // last_initialised_date in the ids table in database.
            if (isInitialised) {
                query.append(", last_initialised_date=? ");
            }
            query.append(" WHERE id_year=? AND id_type=? AND network_code=?");
            if (_log.isDebugEnabled()) {
                _log.debug("update query:", query);
            }
            try(PreparedStatement ps = p_con.prepareStatement(query.toString()) ){
            
            int i = 1;
            ps.setLong(i, seriesNum);
            if (isInitialised) {
            	i++;
                ps.setTimestamp(i, currentDBDate);
            }
            i++;
            ps.setString(i, p_year);
            i++;
            ps.setString(i, p_idType);
            i++;
            ps.setString(i, networkCode);
            int updateNum = ps.executeUpdate();
            if (updateNum == 0) {
                throw new BTSLBaseException("idgenerator.creation.error.norecord");
            }// end of updateNum==0
        }
        }// end of try
        catch (BTSLBaseException be) {
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IDGeneratorDAO[getNextID]", "", "", "", "BTSL BaseException:" + be.getMessage());
            throw new BTSLBaseException(be);
        } catch (SQLException sqle) {
            _log.error("getNextID", "SQLException:" + sqle);
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IDGeneratorDAO[getNextID]", "", "", "", "SQL Exception:" + sqle.getMessage());
            
            throw new BTSLBaseException(this, "getNextID", "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
            _log.error("getNextID", "Exception:" + e);
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IDGeneratorDAO[getNextID]", "", "", "", "Exception:" + e.getMessage());
            
            throw new BTSLBaseException(this, "getNextID", "error.general.processing",e);
        }// end of catch
        finally {
        
            if (_log.isDebugEnabled()) {
                _log.debug("getNextID", "Exiting currentDBDate: " + currentDBDate + " seriesNum: " + seriesNum + " currentDateinMinut: " + currentDateinMinut + " last_mod_date_minut: " + last_mod_date_minut + " moddate: " + moddate);
            }
        } // end of finally
        return seriesNum;
    }

    /**
     * @param p_con
     * @param p_idType
     * @param p_year
     * @param p_networkID
     * @param p_currentDate
     * @return
     * @throws BTSLBaseException
     */
    public long getNextBatchID(Connection p_con, String p_idType, String p_year, String p_networkID, Date p_currentDate) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_idType:");
        	msg.append(p_idType);
        	msg.append(" p_year:");
        	msg.append(p_year);
        	msg.append(" p_networkID:");
        	msg.append(p_networkID);
        	
        	String message=msg.toString();
            _log.debug("getNextBatchID", message);
        }
        final String METHOD_NAME = "getNextBatchID";
        long seriesNum = 0;
        String frequency = null;
        Date moddate = null;
        long last_mod_date_minut = 0;
        long currentDateinMinut = 0;
         
        boolean isRecordFound = false;
        String sqlQuery = null;
        // get the last_no, frequency and last_initialised_date from the
        // database wrt the id_year,id_type and network_code passed to it.
        // the frequency field is used to take deceision about to reset the
        // last_no field.
        // last_initialised_date is used to get the time of last initialisation
        // of the last_on field.
        // DB220120123for update WITH RS
        if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
            sqlQuery = "SELECT last_no,frequency,last_initialised_date FROM ids WHERE id_year=? AND id_type=? AND network_code=? FOR UPDATE OF last_no WITH RS";
        } else {
            IDGeneratorQry idGeneratorQuery = (IDGeneratorQry)ObjectProducer.getObject(QueryConstants.ID_GENERATOR_QRY, QueryConstants.QUERY_PRODUCER);
       		 sqlQuery = idGeneratorQuery.selectFromIDsQry();
        }
        if (_log.isDebugEnabled()) {
            _log.debug("getNextBatchID select query:", sqlQuery);
        }
        try(PreparedStatement ps = p_con.prepareStatement(sqlQuery)) {
           
            ps.setString(1, p_year);
            ps.setString(2, p_idType);
            ps.setString(3, p_networkID);
            try(ResultSet rs = ps.executeQuery())
            {
            long currentTime = new Date().getTime();
            if (p_currentDate != null) {
                p_currentDate.setTime(currentTime);
            } else {
                p_currentDate = new Date();
            }
            if (rs.next()) {
                isRecordFound = true;
                seriesNum = rs.getLong(1);
                frequency = rs.getString(2);
                moddate = rs.getTimestamp(3);

            } // end of if rs.next()
            if (!isRecordFound) {
                throw new BTSLBaseException("idgenerator.creation.error.norecord");
            }
            if (moddate != null) {
                Calendar cal = BTSLDateUtil.getInstance();
                cal.setTime(moddate);
                // get the time in minuts. here cal.getTime().getTime() return
                // the milli second
                // So we divide by 1000*60 to convert it into minuts.
                last_mod_date_minut = cal.getTime().getTime() / (1000 * 60);
                currentDateinMinut = p_currentDate.getTime() / (1000 * 60);
                // get the month of the date in the database for
                // last_initialised_date field.
                int initialisedMonth = cal.get(Calendar.MONTH) + 1;
                // get the year of the date.
                int initialisedYear = cal.get(Calendar.YEAR);
                // set the calender for the current date
                cal.setTime(p_currentDate);
                // get the month of current date
                int curMonth = cal.get(Calendar.MONTH) + 1;
                // get the year of current date.
                int curYear = cal.get(Calendar.YEAR);
                // if the frequency is minuts then we have to reset the last_no
                // on minuts basis.
                if (frequency.equals(PretupsI.FREQUENCY_MINUTS)) {
                    if ((currentDateinMinut - last_mod_date_minut) < 1) {
                        seriesNum++;
                    } else {
                        seriesNum = 1;
                    }
                }
                // else if frequency is hours then we have to reset the last_no
                // on hourly basis.
                else if (frequency.equals(PretupsI.FREQUENCY_HOUR)) {
                    long lastIntialisationHrs = last_mod_date_minut / 60;
                    long currtimeHrs = currentDateinMinut / 60;
                    if ((currtimeHrs - lastIntialisationHrs) < 1) {
                        seriesNum++;
                    } else {
                        seriesNum = 1;
                    }
                }
                // else if frequency is day then we have to reset the last_no on
                // daily basis.
                else if (frequency.equals(PretupsI.FREQUENCY_DAY)) {
                    long lastIntialisationDay = last_mod_date_minut / (60 * 24);
                    long currtimeDay = currentDateinMinut / (60 * 24);
                    if ((currtimeDay - lastIntialisationDay) < 1) {
                        seriesNum++;
                    } else {
                        seriesNum = 1;
                    }
                }
                // else if frequency is month then we have to reset the last_no
                // on monthly basis
                // here we first check the year of dates. if the year is same
                // then we check the month field
                else if (frequency.equals(PretupsI.FREQUENCY_MONTH)) {
                    if (curYear == initialisedYear) {
                        if (curMonth - initialisedMonth < 1) {
                            seriesNum++;
                        } else {
                            seriesNum = 1;
                        }
                    } else {
                        seriesNum = 1;
                    }
                }
                // else if frequency is year then we have to reset the last_no
                // on yearly basis.
                else if (frequency.equals(PretupsI.FREQUENCY_YEAR)) {
                    if (curYear == initialisedYear) {
                        seriesNum++;
                    } else {
                        seriesNum = 1;
                    }
                }
                // else frequency is NA so we have not reset the last_no .
                else {
                    seriesNum++;
                }
            } else {
                seriesNum = 1;
            }
        }
        }// end of try
        catch (BTSLBaseException be) {
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IDGeneratorDAO[getNextBatchID]", "", "", "", "BTSL BaseException:" + be.getMessage());
            throw new BTSLBaseException(be);
        } catch (SQLException sqle) {
            _log.error("getNextBatchID", "SQLException:" + sqle);
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IDGeneratorDAO[getNextBatchID]", "", "", "", "SQL Exception:" + sqle.getMessage());
            
            throw new BTSLBaseException(this, "getNextBatchID", "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
            _log.error("getNextID", "Exception:" + e);
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IDGeneratorDAO[getNextBatchID]", "", "", "", "Exception:" + e.getMessage());
           
            throw new BTSLBaseException(this, "getNextBatchID", "error.general.processing",e);
        }// end of catch
        finally {
            
            if (_log.isDebugEnabled()) {
                _log.debug("getNextBatchID", "Exiting p_currentDate: " + p_currentDate + " seriesNum: " + seriesNum + " currentDateinMinut: " + currentDateinMinut + " last_mod_date_minut: " + last_mod_date_minut + " moddate: " + moddate);
            }
        } // end of finally
        return seriesNum;
    }

    /**
     * @param p_con
     * @param p_idType
     * @param p_year
     * @param p_networkID
     * @param p_currentDate
     * @param long seriesNum
     * @return
     * @throws BTSLBaseException
     */
    public int updateNextBatchID(Connection p_con, String p_idType, String p_year, String p_networkID, Date p_currentDate, long seriesNum) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_idType:");
        	msg.append(p_idType);
        	msg.append(" p_year:");
        	msg.append(p_year);
        	msg.append(" p_networkID:");
        	msg.append(p_networkID);
        	
        	String message=msg.toString();
            _log.debug("updateNextBatchID", message);
        }
        final String METHOD_NAME = "updateNextBatchID";
         
        
        int updateNum = 0;
        try {
            StringBuffer query = new StringBuffer("UPDATE ids SET last_no=?");
            query.append(", last_initialised_date=? ");
            query.append(" WHERE id_year=? AND id_type=? AND network_code=?");
            if (_log.isDebugEnabled()) {
                _log.debug("update query:", query);
            }
            try (PreparedStatement ps = p_con.prepareStatement(query.toString()))
            {
            int i = 1;
            ps.setLong(i, seriesNum);
            i++;
            ps.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_currentDate));
            i++;
            ps.setString(i, p_year);
            i++;
            ps.setString(i, p_idType);
            i++;
            ps.setString(i, p_networkID);
            updateNum = ps.executeUpdate();
            if (updateNum == 0) {
                throw new BTSLBaseException("idgenerator.creation.error.norecord");
            }// end of updateNum==0
        }
        }catch (BTSLBaseException be) {
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IDGeneratorDAO[getNextID]", "", "", "", "BTSL BaseException:" + be.getMessage());
            throw new BTSLBaseException(be);
        } catch (SQLException sqle) {
            _log.error("getNextID", "SQLException:" + sqle);
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IDGeneratorDAO[getNextID]", "", "", "", "SQL Exception:" + sqle.getMessage());
            
            throw new BTSLBaseException(this, "getNextID", "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
            _log.error("getNextID", "Exception:" + e);
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IDGeneratorDAO[getNextID]", "", "", "", "Exception:" + e.getMessage());

            throw new BTSLBaseException(this, "getNextID", "error.general.processing",e);
        }// end of catch
        return updateNum;
    }

    /**
     * Populates the specified no of ids from the database
     * 
     * @param p_con
     * @param p_idType
     * @param p_year
     * @param p_networkID
     * @param p_currentDate
     * @param p_noOfIds
     * @throws BTSLBaseException
     */
    public long getNextID(Connection p_con, String p_idType, String p_year, String p_networkID, Date p_currentDate, int p_noOfIds) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_idType:");
        	msg.append(p_idType);
        	msg.append(" p_year:");
        	msg.append(p_year);
        	msg.append(" p_networkID:");
        	msg.append(p_networkID);
        	msg.append(" p_noOfIds:");
        	msg.append(p_noOfIds);
        	msg.append(" p_currentDate:");
        	msg.append(p_currentDate);
        	
        	String message=msg.toString();
            _log.debug("getNextID", message);
        }
        final String METHOD_NAME = "getNextID";
        long seriesNum = 0;
        long last_mod_date_minut = 0;
        long currentDateinMinut = 0;
        String frequency = null;
        Date moddate = null;
        boolean isInitialised = false;
        boolean isRecordFound = false;
        // date from database for N+1 as well as normal
        Timestamp currentDBDate = null;
        String sqlQuery = null;
        // get the last_no, frequency and last_initialised_date from the
        // database wrt the id_year,id_type and network_code passed to it.
        // the frequency field is used to take deceision about to reset the
        // last_no field.
        // last_initialised_date is used to get the time of last initialisation
        // of the last_on field.
        // DB220120123for update WITH RS
        if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
            sqlQuery = "SELECT last_no,frequency,last_initialised_date,sysdate FROM ids WHERE id_year=? AND id_type=? AND network_code=? FOR UPDATE OF last_no WITH RS";
        } else {
            IDGeneratorQry idGeneratorQuery = (IDGeneratorQry)ObjectProducer.getObject(QueryConstants.ID_GENERATOR_QRY, QueryConstants.QUERY_PRODUCER);
        	sqlQuery = idGeneratorQuery.selectFromIDsQry();
        }

        if (_log.isDebugEnabled()) {
            _log.debug("select query:", sqlQuery);
        }
        try{
        try(PreparedStatement ps = p_con.prepareStatement(sqlQuery)) {
            
            ps.setString(1, p_year);
            ps.setString(2, p_idType);
            ps.setString(3, p_networkID);
            try(ResultSet rs = ps.executeQuery())
            {

            if (rs.next()) {
                isRecordFound = true;
                seriesNum = rs.getLong(1);
                frequency = rs.getString(2);
                moddate = rs.getTimestamp(3);
                currentDBDate = rs.getTimestamp(4);

            } // end of if rs.next()
        
            if (!isRecordFound) {
                throw new BTSLBaseException("idgenerator.creation.error.norecord");
            }
            if (moddate != null) {
                Calendar cal = BTSLDateUtil.getInstance();
                cal.setTime(moddate);
                // get the time in minuts. here cal.getTime().getTime() return
                // the milli second
                // So we divide by 1000*60 to convert it into minuts.
                last_mod_date_minut = cal.getTime().getTime() / (1000 * 60);
                currentDateinMinut = currentDBDate.getTime() / (1000 * 60);
                // get the month of the date in the database for
                // last_initialised_date field.
                int initialisedMonth = cal.get(Calendar.MONTH) + 1;
                // get the year of the date.
                int initialisedYear = cal.get(Calendar.YEAR);
                // set the calender for the current date
                cal.setTime(currentDBDate);
                // get the month of current date
                int curMonth = cal.get(Calendar.MONTH) + 1;
                // get the year of current date.
                int curYear = cal.get(Calendar.YEAR);
                // if the frequency is minuts then we have to reset the last_no
                // on minuts basis.
                if (frequency.equals(PretupsI.FREQUENCY_MINUTS)) {
                    if ((currentDateinMinut - last_mod_date_minut) < 1) {
                        seriesNum++;
                    } else {
                        seriesNum = 1;
                        isInitialised = true;
                    }
                    // if required number of ids does not exist, transaction
                    // will be rollback
                    if (seriesNum + p_noOfIds - 1 > 9999) {
                        p_con.rollback();
                        throw new BTSLBaseException("IDGeneratorDAO", "getNextID", PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
                    }
                }
                // else if frequency is hours then we have to reset the last_no
                // on hourly basis.
                else if (frequency.equals(PretupsI.FREQUENCY_HOUR)) {
                    long lastIntialisationHrs = last_mod_date_minut / 60;
                    long currtimeHrs = currentDateinMinut / 60;
                    if ((currtimeHrs - lastIntialisationHrs) < 1) {
                        seriesNum++;
                    } else {
                        seriesNum = 1;
                        isInitialised = true;
                    }
                }
                // else if frequency is day then we have to reset the last_no on
                // daily basis.
                else if (frequency.equals(PretupsI.FREQUENCY_DAY)) {
                    long lastIntialisationDay = last_mod_date_minut / (60 * 24);
                    long currtimeDay = currentDateinMinut / (60 * 24);
                    if ((currtimeDay - lastIntialisationDay) < 1) {
                        seriesNum++;
                    } else {
                        seriesNum = 1;
                        isInitialised = true;
                    }
                }
                // else if frequency is month then we have to reset the last_no
                // on monthly basis
                // here we first check the year of dates. if the year is same
                // then we check the month field
                else if (frequency.equals(PretupsI.FREQUENCY_MONTH)) {
                    if (curYear == initialisedYear) {
                        if (curMonth - initialisedMonth < 1) {
                            seriesNum++;
                        } else {
                            seriesNum = 1;
                            isInitialised = true;
                        }
                    } else {
                        seriesNum = 1;
                        isInitialised = true;
                    }
                }
                // else if frequency is year then we have to reset the last_no
                // on yearly basis.
                else if (frequency.equals(PretupsI.FREQUENCY_YEAR)) {
                    if (curYear == initialisedYear) {
                        seriesNum++;
                    } else {
                        seriesNum = 1;
                        isInitialised = true;
                    }
                }
                // else frequency is NA so we have not reset the last_no .
                else {
                    seriesNum++;
                }
            } else {
                isInitialised = true;
                seriesNum = 1;
            }
            }
        }

            StringBuffer query = new StringBuffer("UPDATE ids SET last_no=?");
            // if initialisation is to be performed then we also update the
            // last_initialised_date in the ids table in database.
            if (isInitialised) {
                query.append(", last_initialised_date=? ");
            }
            query.append(" WHERE id_year=? AND id_type=? AND network_code=?");
            if (_log.isDebugEnabled()) {
                _log.debug("update query:", query);
            }
            try(PreparedStatement ps = p_con.prepareStatement(query.toString())){
            
            int i = 1;
            ps.setLong(i, seriesNum + p_noOfIds - 1);
            if (isInitialised) {
            	i++;
                ps.setTimestamp(i, currentDBDate);
            }
            i++;
            ps.setString(i, p_year);
            i++;
            ps.setString(i, p_idType);
            i++;
            ps.setString(i, p_networkID);
            int updateNum = ps.executeUpdate();
            if (updateNum == 0) {
                throw new BTSLBaseException("idgenerator.creation.error.norecord");
            }// end of updateNum==0
        }
        }// end of try
        catch (BTSLBaseException be) {
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IDGeneratorDAO[getNextID]", "", "", "", "BTSL BaseException:" + be.getMessage());
            throw new BTSLBaseException(be);
        } catch (SQLException sqle) {
            _log.error("getNextID", "SQLException:" + sqle);
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IDGeneratorDAO[getNextID]", "", "", "", "SQL Exception:" + sqle.getMessage());
            
            throw new BTSLBaseException(this, "getNextID", "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
            _log.error("getNextID", "Exception:" + e);
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IDGeneratorDAO[getNextID]", "", "", "", "Exception:" + e.getMessage());
            
            throw new BTSLBaseException(this, "getNextID", "error.general.processing",e);
        }// end of catch
        finally {
            
            if (_log.isDebugEnabled()) {
                _log.debug("getNextID", "Exiting currentDBDate: " + currentDBDate + " fromSeriesNum: " + seriesNum + " toSeriesNum:" + (seriesNum + p_noOfIds - 1) + " currentDateinMinut: " + currentDateinMinut + " last_mod_date_minut: " + last_mod_date_minut + " moddate: " + moddate);
            }
        } // end of finally
        return seriesNum;
    }

    public long getNextID(Connection p_con, String p_idType, String p_year, NetworkStockTxnVO p_networkStockTxnVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
        	StringBuffer msg=new StringBuffer("");
        	msg.append("Entered p_idType:");
        	msg.append(p_idType);
        	msg.append(" p_year:");
        	msg.append(p_year);
        	
        	String message=msg.toString();
            _log.debug("getNextID", message);
        }
        final String METHOD_NAME = "getNextID";
        long seriesNum = 0;
        long last_mod_date_minut = 0;
        long currentDateinMinut = 0;
        String frequency = null;
        Date moddate = null;
        boolean isInitialised = false;
        boolean isRecordFound = false;
        String networkCode = p_networkStockTxnVO.getNetworkCode();
        // get the last_no, frequency and last_initialised_date from the
        // database wrt the id_year,id_type and network_code passed to it.
        // the frequency field is used to take deceision about to reset the
        // last_no field.
        // last_initialised_date is used to get the time of last initialisation
        // of the last_on field.
        IDGeneratorQry idGeneratorQuery = (IDGeneratorQry)ObjectProducer.getObject(QueryConstants.ID_GENERATOR_QRY, QueryConstants.QUERY_PRODUCER);
        String sqlQuery = idGeneratorQuery.selectFromIDsQry();
        if (_log.isDebugEnabled()) {
            _log.debug("select query:", sqlQuery);
        }
        // date from database for N+1 as well as normal
        Timestamp currentDBDate = null;
        try{
        try(PreparedStatement ps = p_con.prepareStatement(sqlQuery)) {
           
            ps.setString(1, p_year);
            ps.setString(2, p_idType);
            ps.setString(3, networkCode);
            
            try(ResultSet rs = ps.executeQuery()){
            if (rs.next()) {
                isRecordFound = true;
                seriesNum = rs.getLong(1);
                frequency = rs.getString(2);
                moddate = rs.getTimestamp(3);
                currentDBDate = rs.getTimestamp(4);

            } // end of if rs.next()
            if (!isRecordFound) {
                throw new BTSLBaseException("idgenerator.creation.error.norecord");
            }
            p_networkStockTxnVO.setDBDateTime(currentDBDate);

            if (moddate != null) {
                Calendar cal = BTSLDateUtil.getInstance();
                cal.setTime(moddate);
                // get the time in minuts. here cal.getTime().getTime() return
                // the milli second
                // So we divide by 1000*60 to convert it into minuts.
                last_mod_date_minut = cal.getTime().getTime() / (1000 * 60);
                currentDateinMinut = currentDBDate.getTime() / (1000 * 60);
                // get the month of the date in the database for
                // last_initialised_date field.
                int initialisedMonth = cal.get(Calendar.MONTH) + 1;
                // get the year of the date.
                int initialisedYear = cal.get(Calendar.YEAR);
                // set the calender for the current date
                cal.setTime(currentDBDate);
                // get the month of current date
                int curMonth = cal.get(Calendar.MONTH) + 1;
                // get the year of current date.
                int curYear = cal.get(Calendar.YEAR);
                // if the frequency is minuts then we have to reset the last_no
                // on minuts basis.
                if (frequency.equals(PretupsI.FREQUENCY_MINUTS)) {
                    if ((currentDateinMinut - last_mod_date_minut) < 1) {
                        seriesNum++;
                    } else {
                        seriesNum = 1;
                        isInitialised = true;
                    }
                }
                // else if frequency is hours then we have to reset the last_no
                // on hourly basis.
                else if (frequency.equals(PretupsI.FREQUENCY_HOUR)) {
                    long lastIntialisationHrs = last_mod_date_minut / 60;
                    long currtimeHrs = currentDateinMinut / 60;
                    if ((currtimeHrs - lastIntialisationHrs) < 1) {
                        seriesNum++;
                    } else {
                        seriesNum = 1;
                        isInitialised = true;
                    }
                }
                // else if frequency is day then we have to reset the last_no on
                // daily basis.
                else if (frequency.equals(PretupsI.FREQUENCY_DAY)) {
                    long lastIntialisationDay = last_mod_date_minut / (60 * 24);
                    long currtimeDay = currentDateinMinut / (60 * 24);
                    if ((currtimeDay - lastIntialisationDay) < 1) {
                        seriesNum++;
                    } else {
                        seriesNum = 1;
                        isInitialised = true;
                    }
                }
                // else if frequency is month then we have to reset the last_no
                // on monthly basis
                // here we first check the year of dates. if the year is same
                // then we check the month field
                else if (frequency.equals(PretupsI.FREQUENCY_MONTH)) {
                    if (curYear == initialisedYear) {
                        if (curMonth - initialisedMonth < 1) {
                            seriesNum++;
                        } else {
                            seriesNum = 1;
                            isInitialised = true;
                        }
                    } else {
                        seriesNum = 1;
                        isInitialised = true;
                    }
                }
                // else if frequency is year then we have to reset the last_no
                // on yearly basis.
                else if (frequency.equals(PretupsI.FREQUENCY_YEAR)) {
                    if (curYear == initialisedYear) {
                        seriesNum++;
                    } else {
                        seriesNum = 1;
                        isInitialised = true;
                    }
                }
                // else frequency is NA so we have not reset the last_no .
                else {
                    seriesNum++;
                }
            } else {
                isInitialised = true;
                seriesNum = 1;
            }
        }
        }

            StringBuffer query = new StringBuffer("UPDATE ids SET last_no=?");
            // if initialisation is to be performed then we also update the
            // last_initialised_date in the ids table in database.
            if (isInitialised) {
                query.append(", last_initialised_date=? ");
            }
            query.append(" WHERE id_year=? AND id_type=? AND network_code=?");
            if (_log.isDebugEnabled()) {
                _log.debug("update query:", query);
            }
            try(PreparedStatement ps = p_con.prepareStatement(query.toString())){
            
            int i = 1;
            ps.setLong(i, seriesNum);
            if (isInitialised) {
            	i++;
                ps.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(currentDBDate));
            }
            i++;
            ps.setString(i, p_year);
            i++;
            ps.setString(i, p_idType);
            i++;
            ps.setString(i, networkCode);
            int updateNum = ps.executeUpdate();
            if (updateNum == 0) {
                throw new BTSLBaseException("idgenerator.creation.error.norecord");
            }// end of updateNum==0
        }
    }// end of try
        catch (BTSLBaseException be) {
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IDGeneratorDAO[getNextID]", "", "", "", "BTSL BaseException:" + be.getMessage());
            throw new BTSLBaseException(be);
        } catch (SQLException sqle) {
            _log.error("getNextID", "SQLException:" + sqle);
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IDGeneratorDAO[getNextID]", "", "", "", "SQL Exception:" + sqle.getMessage());
            
            throw new BTSLBaseException(this, "getNextID", "error.general.sql.processing",sqle);
        }// end of catch
        catch (Exception e) {
            _log.error("getNextID", "Exception:" + e);
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IDGeneratorDAO[getNextID]", "", "", "", "Exception:" + e.getMessage());
            
            throw new BTSLBaseException(this, "getNextID", "error.general.processing",e);
        }// end of catch
        finally {
        
            
            
            if (_log.isDebugEnabled()) {
                _log.debug("getNextID", "Exiting currentDBDate: " + currentDBDate + " seriesNum: " + seriesNum + " currentDateinMinut: " + currentDateinMinut + " last_mod_date_minut: " + last_mod_date_minut + " moddate: " + moddate);
            }
        } // end of finally
        return seriesNum;
    }
}// end of the class IDGenerator
