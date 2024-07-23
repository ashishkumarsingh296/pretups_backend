package com.btsl.pretups.channel.transfer.requesthandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.util.Constants;
import com.txn.pretups.channel.transfer.businesslogic.C2STransferTxnDAO;

@Service
public class FilesStorageServiceImpl implements FilesStorageService{
	
	private static Log log = LogFactory.getLog(FilesStorageServiceImpl.class
			.getName());
	
	private String dirPath;
	private Path pathloc ;
	
	@Override
	public void init(String fileName) throws BTSLBaseException {
		try {
			dirPath = Constants.getProperty("UploadFileForUnRegChnlUserPath");
		pathloc=Paths.get(dirPath+fileName);
		
		File directory = new File(dirPath);
		 if (!directory.exists()) {
			 directory.mkdirs();
		 }
	    
		File filepath = new File(dirPath+fileName);
	    if (!filepath.exists()){
	        filepath.createNewFile();
	        // If you require it to make the entire directory path including parents,
	        // use directory.mkdirs(); here instead.
	    }
		
		} catch(IOException ie) {
			throw new BTSLBaseException("PretupsUIReportsController", "passbookSearch",
					PretupsErrorCodesI.CCE_ERROR_FROMDATE_GREATER_THAN_CURRENTDATE, 0, null);
		}
	}

	
	public void save(MultipartFile file,String fileName) {
	    try {
	    	init(fileName);
	        Files.copy(file.getInputStream(), pathloc,StandardCopyOption.REPLACE_EXISTING);
	      } catch (Exception e) {
	        throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
	      }

		
	}
	
	
	/**
	 * Method writeByteArrayToFile write decode data at specified path
	 * 
	 * @param filePath
	 * @param base64Bytes
	 * @throws BTSLBaseException
	 */

	public  void writeByteArrayToFile(String filePath, byte[] base64Bytes) throws BTSLBaseException {
		try {
			log.debug("writeByteArrayToFile: ", filePath);
			log.debug("writeByteArrayToFile: ", base64Bytes);
			FileUtils.writeByteArrayToFile(new File(filePath), base64Bytes);
		} catch (Exception e) {
			log.debug("writeByteArrayToFile: ", e.getMessage());
			log.error("writeByteArrayToFile", "Exceptin:e=" + e);
			log.errorTrace("writeByteArrayToFile", e);
			throw new BTSLBaseException("FilesStorageServiceImpl", "init",
					PretupsErrorCodesI.FILE_WRITE_ERROR, 0, null);

		}
	}

	


	@Override
	public Resource load(String filename) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Stream<Path> loadAll() {
		// TODO Auto-generated method stub
		return null;
	}

}
