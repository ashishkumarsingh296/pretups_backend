package com.btsl.pretups.processes;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @author nand.sahu
 * 
 */
public class OnlyExt implements FilenameFilter {
    private String ext;

    /**
     * 
     */
    public OnlyExt(String ext) {
        this.ext = "." + ext;
    }

    public boolean accept(File dir, String filename) {

        return filename.endsWith(ext);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        /*
         * String _filePath="D:\\Test";
         * if(_filePath !=null || _filePath != ""){
         * System.out.println("_filePath = "+_filePath);
         * File f = new File(_filePath);
         * String fileList[] = f.list();
         * for(int i=0; i<fileList.length;i++){
         * System.out.println("fileList["+i+"] = "+fileList[i]);
         * File f1 = new File(_filePath+"\\"+fileList[i]);
         * System.out.println(" Dir Final Path = "+_filePath+"\\"+fileList[i]);
         * if(f1.isDirectory()){
         * FilenameFilter onlyExtFiles = new OnlyExt("csv");
         * String csvFiles[] = f1.list(onlyExtFiles);
         * if(csvFiles.length < 1){
         * onlyExtFiles = new OnlyExt("CSV");
         * csvFiles = f1.list(onlyExtFiles);
         * }
         * for(int j=0; j<csvFiles.length;j++){
         * System.out.println(" Final File Path = "+_filePath+"\\"+fileList[i]+"\\"
         * +csvFiles[j]);
         * File f2 = new File(f1,csvFiles[j]);
         * File backUpPath = new File("D:\\Test\\Backup\\"+fileList[i]);
         * File backUpFile = new File(backUpPath,csvFiles[j]);
         * if(!backUpPath.exists())
         * backUpPath.mkdir();
         * f2.renameTo(backUpFile);
         * }
         * 
         * }
         * }
         * }
         */}

}
