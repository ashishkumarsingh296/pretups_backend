package com.btsl.pretups.filters;

import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.encoder.Encode;


/**
 * @author tarun.kumar
 *
 */
public class XSSRequestWrapper extends HttpServletRequestWrapper {

	 private static final Log logger = LogFactory.getLog(XSSRequestWrapper.class);

	 public XSSRequestWrapper(HttpServletRequest request) {
	        super(request);
	    }
	 
	   private static Pattern[] patterns = new Pattern[]{
           
           Pattern.compile("(<style>|<script>)((.|[\\r\\n])*?)(</style>|</script>)", Pattern.CASE_INSENSITIVE),
           
           Pattern.compile("(style|href|src)((\\s|[\\r\\n])*?)=(\\s|[\\r\\n])*(\\'|\\\")((.|[\\r\\n])*?)(\\'|\\\")", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
           
           Pattern.compile("(document|attr|element|nodemap|nodelist|text|entity|namemap).(.*?)[\\s|=|)]", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
           
           Pattern.compile("</script>|</style>", Pattern.CASE_INSENSITIVE),
           Pattern.compile("<script(.*?)|<style(.*?)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
           
           Pattern.compile("(eval|expression)\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
           
           Pattern.compile("(javascript|vbscript):", Pattern.CASE_INSENSITIVE),
           
           Pattern.compile("on((.|[\\r\\n])*?)=((.|[\\r\\n])*?)[\\'|\\\"]((.|[\\r\\n])*?)[\\'|\\\"]", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
           
           Pattern.compile("<(meta|html|body|iframe|img|input|link|object|a|div|td|th|span)((.|[\\r\\n])*)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
           
           Pattern.compile("<((.|[\\r\\n])*?)>((.|[\\r\\n])*?)</((.|[\\r\\n])*)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
           
           Pattern.compile("<!\\[CDATA((.|[\\r\\n])*?)\\]>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
           
           Pattern.compile("<((.|[\\r\\n])*)(>|\\\")", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
           Pattern.compile("\\[((.|[\\r\\n])*)(\\]|\\\")", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
           Pattern.compile("<|>|\\[|\\]|&|\"|'|\\(|\\)|\\+", Pattern.MULTILINE | Pattern.DOTALL)
       };

	   @Override
       public String[] getParameterValues(String parameter) {
           try {
               String[] values = super.getParameterValues(parameter);
   
               if (values == null) {
                   return null;
               }
   
               int count = values.length;
               String[] encodedValues = new String[count];
               for (int i = 0; i < count; i++) {
                   encodedValues[i] = filterXSS(values[i]);
               }
               return encodedValues;
           } catch (Exception e) {
        	   if(logger.isDebugEnabled()){
        		   logger.debug(this.getClass().getName() );
        	   }
           }
           return null;
       }
	   @Override
       public String getParameter(String parameter) {
           String value = super.getParameter(parameter);
           return filterXSS(value);
       }

       @Override
       public String getHeader(String name) {
           String value = super.getHeader(name);
           return filterXSS(value);
       }

       private String filterXSS(String value) {
           if (value != null) {
                      	       	   
               value = value.replaceAll("", "");

               
               for (Pattern scriptPattern : patterns){
                   value = scriptPattern.matcher(value).replaceAll("");
               }
               
               
               value = Encode.forHtml(value);
           }
           return value;
       }
}
