package com.btsl.util;


import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;

import java.util.Locale;

public class MessageResources {

    public String getMessage(Locale locale, String key) {

        return RestAPIStringParser.getMessage(locale, key) ;

    }

    public String getMessage(Locale locale, String key, String[] args) {

        return RestAPIStringParser.getMessage(locale, key, args) ;

    }

    public String getMessage(Locale locale, String key, String args) {

        String[] arguments = new String[1];
        arguments[0] = args ;
        return RestAPIStringParser.getMessage(locale, key, arguments) ;

    }

    public String getMessage(Locale locale, String key, String arg1, String arg2) {

        String[] arguments = new String[2];
        arguments[0] = arg1 ;
        arguments[1] = arg2 ;
        return RestAPIStringParser.getMessage(locale, key, arguments) ;

    }

    public String getMessage(String key, String args) {
        Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));


        String[] arguments = new String[1];
        arguments[0] = args ;
        return RestAPIStringParser.getMessage(locale, key, arguments) ;

    }

    public String getMessage(String key, String[] args) {
        Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));

        return RestAPIStringParser.getMessage(locale, key, args) ;

    }

    public String getMessage(String key) {

        Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));

        return RestAPIStringParser.getMessage(locale, key) ;

    }




}
