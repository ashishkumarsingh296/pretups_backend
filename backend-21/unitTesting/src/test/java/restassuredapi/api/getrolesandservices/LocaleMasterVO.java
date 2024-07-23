package restassuredapi.api.getrolesandservices;

public class LocaleMasterVO {

    private String _language = null;
    private String _country = null;
    private String _name = null;
    private String _language_code = null;
    private String _charset = null;
    private String _encoding = null;
    private String _status = null;
    private String _type = null;
    private String _message = null;
    private String _coding = null;
    private int _sequenceNo = 0;

    /**
     * @return Returns the charset.
     */
    public String getCharset() {
        return _charset;
    }

    /**
     * @param charset
     *            The charset to set.
     */
    public void setCharset(String charset) {
        _charset = charset;
    }

    /**
     * @return Returns the coding.
     */
    public String getCoding() {
        return _coding;
    }

    /**
     * @param coding
     *            The coding to set.
     */
    public void setCoding(String coding) {
        _coding = coding;
    }

    /**
     * @return Returns the country.
     */
    public String getCountry() {
        return _country;
    }

    /**
     * @param country
     *            The country to set.
     */
    public void setCountry(String country) {
        _country = country;
    }

    /**
     * @return Returns the encoding.
     */
    public String getEncoding() {
        return _encoding;
    }

    /**
     * @param encoding
     *            The encoding to set.
     */
    public void setEncoding(String encoding) {
        _encoding = encoding;
    }

    /**
     * @return Returns the language.
     */
    public String getLanguage() {
        return _language;
    }

    /**
     * @param language
     *            The language to set.
     */
    public void setLanguage(String language) {
        _language = language;
    }

    /**
     * @return Returns the language_code.
     */
    public String getLanguage_code() {
        return _language_code;
    }

    /**
     * @param language_code
     *            The language_code to set.
     */
    public void setLanguage_code(String language_code) {
        _language_code = language_code;
    }

    /**
     * @return Returns the message.
     */
    public String getMessage() {
        return _message;
    }

    /**
     * @param message
     *            The message to set.
     */
    public void setMessage(String message) {
        _message = message;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return _name;
    }

    /**
     * @param name
     *            The name to set.
     */
    public void setName(String name) {
        _name = name;
    }

    /**
     * @return Returns the status.
     */
    public String getStatus() {
        return _status;
    }

    /**
     * @param status
     *            The status to set.
     */
    public void setStatus(String status) {
        _status = status;
    }

    /**
     * @return Returns the type.
     */
    public String getType() {
        return _type;
    }

    /**
     * @param type
     *            The type to set.
     */
    public void setType(String type) {
        _type = type;
    }

    /**
     * @return Returns the sequenceNo.
     */
    public int getSequenceNo() {
        return this._sequenceNo;
    }

    /**
     * @param sequenceNo
     *            The sequenceNo to set.
     */
    public void setSequenceNo(int sequenceNo) {
        this._sequenceNo = sequenceNo;
    }

    public String toString() {
        StringBuffer sbf = new StringBuffer(" language=" + _language + ", country=" + _country + ", name=" + _name + ", language_code=" + _language_code + ", charset=" + _charset + ", encoding=" + _encoding + ", status=" + _status + ", type=" + _type + ", message=" + _message + ", coding=" + _coding + ", sequenceNo" + _sequenceNo);
        return sbf.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object arg0) {
        LocaleMasterVO obj = (LocaleMasterVO) arg0;
        final String METHOD_NAME = "compareTo";
        try {
            if (this._sequenceNo > obj._sequenceNo) {
                return 1;
            }
            return -1;
        } catch (Exception e) {
            return 1;
        }
    }
}
