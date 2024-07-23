/*
 * Created on Nov 14, 2003
 * 
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.btsl.ota.services.businesslogic;

import java.io.Serializable;

/**
 * @author abhijit.chauhan
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ServiceSetVO implements Serializable {

    protected String _id;
    protected String _name;
    protected String _language1;
    protected String _language2;
    protected String _lang1Lang2;

    /**
	 * 
	 */
    public ServiceSetVO() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @return
     */

    public java.lang.String getId() {
        return _id;
    }

    /**
     * @return
     */
    public java.lang.String getLanguage1() {
        return _language1;
    }

    /**
     * @return
     */
    public java.lang.String getLanguage2() {
        return _language2;
    }

    /**
     * @return
     */
    public java.lang.String getName() {
        return _name;
    }

    /**
     * @param string
     */
    public void setId(java.lang.String string) {
        _id = string;
    }

    /**
     * @param string
     */
    public void setLanguage1(java.lang.String string) {
        _language1 = string;
    }

    /**
     * @param string
     */
    public void setLanguage2(java.lang.String string) {
        _language2 = string;
    }

    /**
     * @param string
     */
    public void setName(java.lang.String string) {
        _name = string;
    }

    /**
     * To get the value of lang1Lang2 field
     * 
     * @return lang1Lang2.
     */
    public String getLang1Lang2() {
        return _language1 + "-" + _language2 + "(" + _language2 + " " + _language1 + ")";
    }
}
