package simulator.decryptutility;

/**
 * @author satakshi.gaur
 * This file is used to decrypt any encrypted String
 * If this file is to be used from shell script then return of main method should be void and there should be print statement uncommented
 * If this file is to be used from Jmeter then return type should be String and there should be return statement uncommented.
 *
 *
 *
 */
public class BTSLUtil {

  
    /**
     * 
     */
    public BTSLUtil() {
        super();
    }

   
    /**
     * @param text
     * @return
     */
    public static String decryptText(String text) {
        try {
        	return new CryptoUtil().decrypt(text, Constants.KEY);
        } catch (Exception e) {
            return null;
        }
    }// end method

  
    /**
     * @param text
     * @return
     */
    public static String encryptText(String text) {
        try {
        	return new CryptoUtil().encrypt(text, Constants.KEY);
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * @param args
     * @return
     */
    public static String main(String args[]) {
        String dec = null;
        try {
        	//uncomment following line and set return type void to use this class via shell script.
        	//	System.out.println( new CryptoUtil().decrypt(args[0], Constants.KEY)); 
        	//uncomment following line and set return type String to use this class via Jmeter.
        	dec =  new CryptoUtil().decrypt(args[0], Constants.KEY);

        } catch (Exception e) {
        }
      //uncomment following line  to use this class via Jmeter.
		return dec;
    }

}