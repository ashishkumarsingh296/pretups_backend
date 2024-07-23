package com.btsl.pretups.channel.transfer.requesthandler;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.btsl.common.BTSLBaseException;

public interface FilesStorageService {
	 public void init(String fileName) throws BTSLBaseException;
	  public  void writeByteArrayToFile(String filePath, byte[] base64Bytes) throws BTSLBaseException;
	  public Resource load(String filename);
	  public void deleteAll();
	  public Stream<Path> loadAll();
	}
