package com.restapi.channelAdmin;

/*import org.apache.struts.upload.FormFile;*/

public class DeRegisterSubscriberBatchVO {

	
	//private FormFile _fileName; // this variable take the file form the jsp
    // page.
    private String _fileNameStr; // to store the file name.
    
    
  /*  public FormFile getFileName() {
        return _fileName;
    }
*/
    /**
     * @param name
     *            The _fileName to set.
     */
   /* public void setFileName(FormFile name) {
        _fileName = name;
    }
*/
    /**
     * @return Returns the _fileNameStr.
     */
    public String getFileNameStr() {
        return _fileNameStr;
    }

    /**
     * @param nameStr
     *            The _fileNameStr to set.
     */
    public void setFileNameStr(String nameStr) {
        _fileNameStr = nameStr;
    }
}
