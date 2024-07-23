package com.selftopup.alarm;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Locale;
import java.util.StringTokenizer;

import com.selftopup.event.EventHandler;
import com.selftopup.event.EventVO;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.gateway.businesslogic.PushMessage;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.Constants;

public class AlarmSender extends Thread {

    private static Log _log = LogFactory.getLog(AlarmSender.class.getName());
    public boolean _running = true;
    private Hashtable _alarmHash = null;
    private EventVO _newEventVO = null;
    private EventVO _oldEventVO = null;
    private String _key = null;
    private long _sleepTime = 0;
    private long _maxHoldTime = 0;
    private String _alerCountMessage = null;
    private String _eventLevel = null;
    private String _eventId = null;
    private String _msisdnString = null;
    Locale _locale = null;
    private String _langCountry = null;
    private String _instanceId = null;

    public AlarmSender() {
        if (_log.isDebugEnabled())
            _log.debug("default contructor", "Entered");
        _alarmHash = new Hashtable();
        refresh();
        if (_log.isDebugEnabled())
            _log.debug("default contructor", "Exiting");
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        new AlarmSender().send(args[0], Integer.parseInt(args[1]), args[2], args[4]);
    }

    /**
     * This method is used to send alarm,
     * 
     * @param p_componentName
     *            - Name of the component
     * @param p_alarmState
     *            - Alarm state
     * @param messageString
     *            - Alarm message
     *            return void
     */
    public void send(String p_componentName, int p_alarmID, String p_alarmState, String messageString) {
        (new AlarmManager(p_componentName, p_alarmID, p_alarmState, messageString)).start();
    }

    /**
	 * 
	 *
	 */
    public void refresh() {
        if (_log.isDebugEnabled())
            _log.debug("refresh", "Entered");
        try {
            try {
                _instanceId = Constants.getProperty("INSTANCE_ID");
                if (_log.isDebugEnabled())
                    _log.debug("refresh", "_instanceId: " + _instanceId);
            } catch (Exception e1) {
                _log.error("refresh", "INSTANCE_ID is not defined in Constant.props, cannot use default");
            }

            try {
                _sleepTime = Long.parseLong(Constants.getProperty("ALARM_SLEEP_TIME"));
                if (_log.isDebugEnabled())
                    _log.debug("refresh", "_sleepTime: " + _sleepTime);
            } catch (Exception e2) {
                _log.error("refresh", "ALARM_SLEEP_TIME is not defined in Constant.props, setting default as 1000");
                _sleepTime = 1000;
            }

            try {
                _alerCountMessage = Constants.getProperty("ALARM_COUNT_MESSAGE");
                if (_log.isDebugEnabled())
                    _log.debug("refresh", "_alerCountMessage: " + _alerCountMessage);
            } catch (Exception e3) {
                _log.error("refresh", "ALARM_COUNT_MESSAGE is not defined in Constant.props, setting default");
                _alerCountMessage = " ( {0} similar case(s) have happened in last {1} seconds )";
            }

            try {
                _maxHoldTime = Long.parseLong(Constants.getProperty("ALARM_MAX_HOLD_TIME"));
                if (_log.isDebugEnabled())
                    _log.debug("refresh", "_maxHoldTime: " + _maxHoldTime);
            } catch (Exception e4) {
                _log.error("refresh", "ALARM_MAX_HOLD_TIME is not defined in Constant.props, setting default as 5000");
                _maxHoldTime = 5000;
            }

            // Added for monitoring PreTUPS by SMS by Vipul on 05/12/07
            try {
                _msisdnString = new String(Constants.getProperty("adminmobile"));
                if (_log.isDebugEnabled())
                    _log.debug("refresh", "_msisdnString: " + _msisdnString);
            } catch (Exception e5) {
                _log.error("refresh", "adminmobile is not defined in Constant.props, cannot use default");
            }

            try {
                _eventLevel = new String(Constants.getProperty("SMS_EVENT_LEVELS"));
                if (_log.isDebugEnabled())
                    _log.debug("refresh", "_eventLevel: " + _eventLevel);
            } catch (Exception e6) {
                _log.error("refresh", "SMS_EVENT_LEVELS is not defined in Constant.props, setting default as 1,2");
                _eventLevel = "1,2";
            }

            try {
                _eventId = new String(Constants.getProperty("SMS_EVENT_IDS"));
                if (_log.isDebugEnabled())
                    _log.debug("refresh", "_eventId: " + _eventId);
            } catch (Exception e7) {
                _log.error("refresh", "SMS_EVENT_IDS is not defined in Constant.props, setting default as 1,2,14");
            }

            try {
                _langCountry = new String(Constants.getProperty("ALARM_DEFAULT_LOCALE"));
                if (_log.isDebugEnabled())
                    _log.debug("refresh", "_langCountry: " + _langCountry);
                // locale from Constant.props is en,us; so splitting and using
                // separately to create locale
                _locale = new Locale(_langCountry.split(",")[0], _langCountry.split(",")[1]);
            } catch (Exception e8) {
                _log.error("refresh", "ALARM_DEFAULT_LOCALE is not defined in Constant.props, setting default as en_us");
                _locale = new Locale("en", "US");
            }

        } catch (Exception e) {
            _log.errorTrace("run:  Exception print stack trace", e);
        }
        if (_log.isDebugEnabled())
            _log.debug("refresh", "Exiting ");
    }

    public void run() {
        if (_log.isDebugEnabled())
            _log.debug("run", "Entered");
        long generatedOn = 0;
        long currentTime = 0;
        int count = 0;
        String message = null;
        long timeDiff = 0;
        while (_running) {
            generatedOn = 0;
            count = 0;
            try {
                timeDiff = 0;
                // if have to think of logic for initilising following parameter
                // during startup and only loading when there is change
                Thread.sleep(_sleepTime);
                try {
                    // retrive alarm from message queue made bu event handler.if
                    // no alarm found just continue after sleep in exception
                    _newEventVO = (EventVO) EventHandler._eventList.remove(0);
                    if (_newEventVO != null) {
                        _key = _newEventVO.getComponentName() + "|" + _newEventVO.getEventID() + "|" + _newEventVO.getProcessName();
                        if (_log.isDebugEnabled())
                            _log.debug("run", "_key: " + _key);
                        // If alarm hash contains this alarm the check for its
                        // valididty for sending to O & M
                        if (_alarmHash.containsKey(_key)) {
                            _oldEventVO = (EventVO) _alarmHash.get(_key);
                            generatedOn = _oldEventVO.getGeneratedOn();
                            currentTime = System.currentTimeMillis();
                            count = _oldEventVO.getAlarmCount();
                            timeDiff = currentTime - generatedOn;
                            if (_log.isDebugEnabled())
                                _log.debug("run", "_key: " + _key + " _alarmHash size: " + _alarmHash.size() + " _newEventVO: " + _newEventVO + " _alarmHash: " + _alarmHash);
                            // check whether max time expired for the alarm
                            if (timeDiff > _maxHoldTime) {
                                // if yes, then initialise the alarm count and
                                // generatedon(or just remove from alarmHash)
                                // and send alarm telling similar cases
                                // number(if >0)
                                _oldEventVO.setAlarmCount(0);
                                _oldEventVO.setGeneratedOn(currentTime);
                                message = _newEventVO.getMessage();
                                // change message only if count is greater than
                                // 1
                                if (count > 0) {
                                    if (_log.isDebugEnabled())
                                        _log.debug("run", "_key: " + _key + " count: " + count);
                                    message = message + MessageFormat.format(_alerCountMessage, new String[] { String.valueOf(count), String.valueOf(timeDiff / 1000) });
                                }
                                // If instance id is not defined, it should not
                                // be appended in the alarm
                                if (!BTSLUtil.isNullString(_instanceId))
                                    message = message + " [Instance:" + _instanceId + "]";

                                send(_newEventVO.getComponentName(), _newEventVO.getEventID(), _newEventVO.getEventStatus(), message);

                                // Added for monitoring PreTUPS by SMS by Vipul
                                // on 05/12/07
                                // //Alert will be sent only if the event id and
                                // msisdn and event level are available in
                                // loaded list from Constants.props. //Alarm
                                // will be sent at admin mobile numbers loaded
                                // above in refresh()
                                // if(!BTSLUtil.isNullString(_msisdnString))
                                // //killed by Vikas 28/03/08 to take decision
                                // of sending alrm on the basis of eventid
                                if (isStringContain(_eventId, String.valueOf(_newEventVO.getEventID())) && !BTSLUtil.isNullString(_msisdnString)) {
                                    // if
                                    // ((BTSLUtil.isStringContain(_eventLevel,String.valueOf(_newEventVO.getLevel())))
                                    // &&
                                    // (BTSLUtil.isStringContain(_eventId,String.valueOf(_newEventVO.getEventID()))))
                                    if ((isStringContain(_eventLevel, String.valueOf(_newEventVO.getLevel())))) {
                                        String[] msisdn = _msisdnString.split(",");
                                        PushMessage pushMessage = null;

                                        for (int i = 0, len = msisdn.length; i < len; i++) {
                                            pushMessage = new PushMessage(msisdn[i], message, null, null, _locale);
                                            pushMessage.pushAlarm();
                                        }// end of for msisdn.length
                                    }// end of if event level
                                }// end of if event id && msisdn
                            } else {
                                // if no, just increase the alarm count without
                                // taking any action
                                _oldEventVO.setAlarmCount(count + 1);
                            }
                        } else {
                            _alarmHash.put(_key, _newEventVO);
                            // send Alarm

                            // If instance id is not defined, it should not be
                            // appended in the Alarm
                            if (!BTSLUtil.isNullString(_instanceId))
                                message = _newEventVO.getMessage() + " [Instance:" + _instanceId + "]";
                            else
                                message = _newEventVO.getMessage();

                            send(_newEventVO.getComponentName(), _newEventVO.getEventID(), _newEventVO.getEventStatus(), message);

                            // Added for monitoring PreTUPS by SMS by Vipul on
                            // 05/12/07
                            // Alert will be sent only if the event id and
                            // msisdn and event level are available in loaded
                            // list from Constants.props.
                            // Alarm will be sent at admin mobile numbers loaded
                            // above
                            // if(!BTSLUtil.isNullString(_msisdnString))
                            // //killed by Vikas 28/03/08 to take decision of
                            // sending alrm on the basis of eventid
                            if (isStringContain(_eventId, String.valueOf(_newEventVO.getEventID())) && !BTSLUtil.isNullString(_msisdnString)) {
                                // if
                                // ((BTSLUtil.isStringContain(_eventLevel,String.valueOf(_newEventVO.getLevel())))
                                // &&
                                // (BTSLUtil.isStringContain(_eventId,String.valueOf(_newEventVO.getEventID()))))
                                if ((isStringContain(_eventLevel, String.valueOf(_newEventVO.getLevel())))) {
                                    String[] msisdn = _msisdnString.split(",");
                                    PushMessage pushMessage = null;
                                    for (int i = 0, len = msisdn.length; i < len; i++) {
                                        pushMessage = new PushMessage(msisdn[i], message, null, null, _locale);
                                        pushMessage.pushAlarm();
                                    }// //end of for msisdn.length
                                }// //end of if event level
                            }// end of if event id && msisdn
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException ax) {
                    continue;
                } catch (Exception ex) {
                    _log.errorTrace("run:  Exception print stack trace:e=", ex);// temporarily
                    continue;
                }
            } catch (Exception e) {
                _log.errorTrace("run:  Exception print stack trace:e=", e);
                try {
                    Thread.sleep(200);
                } catch (Exception ex) {
                    _log.errorTrace("run:  Exception print stack trace", ex);
                }
            }
        }
        if (_log.isDebugEnabled())
            _log.debug("run", "Exiting");
    }

    /**
     * Checks whether coma seprated value present in a string
     * 
     * @param commaStr
     * @param string
     * @return
     */
    private boolean isStringContain(String commaStr, String string) {
        if (_log.isDebugEnabled())
            _log.debug("isStringContain", "Entered commaStr=" + commaStr + "  string=" + string);
        try {
            ArrayList usageStringList = null;
            if (commaStr != null) {
                usageStringList = new ArrayList();
                StringTokenizer strToken = new StringTokenizer(commaStr, ",");
                while (strToken.hasMoreTokens()) {
                    usageStringList.add(strToken.nextElement());
                }
            } else
                return false;
            String tempStr;
            for (int i = 0; i < usageStringList.size(); i++) {
                tempStr = (String) usageStringList.get(i);
                if (_log.isDebugEnabled())
                    _log.debug("isStringContain", "comparing allowed=" + tempStr + "  with=" + string);
                if (string.equals(tempStr.trim())) {
                    return true;
                }
            }
        } catch (Exception e) {
            _log.error("isStringContain", "Exception " + e);
            _log.errorTrace("isStringContain: Exception print stack trace ", e);
        }
        return false;
    }
}
