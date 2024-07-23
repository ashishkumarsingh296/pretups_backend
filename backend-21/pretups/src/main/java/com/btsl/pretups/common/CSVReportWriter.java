package com.btsl.pretups.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public class CSVReportWriter implements Runnable {
	
	private Log log = LogFactory.getLog(this.getClass().getName());
	private BlockingQueue<String> queue;
	private String columnHeader;
	private MutableBoolean mutableBoolean;
	private MutableBoolean filewritten;
	private String file;
	private String directory;
	private String reportTopHeaders;
	public CSVReportWriter(BlockingQueue<String> queue, String directory, String file, MutableBoolean mutableBoolean, MutableBoolean filewritten, String columnHeader,String reportTopHeaders) {
		super();
		this.queue = queue;
		this.mutableBoolean = mutableBoolean;
		this.filewritten = filewritten;
		this.directory = directory;
		this.file = file;
		this.columnHeader = columnHeader;
		this.reportTopHeaders=reportTopHeaders;
	}

	@Override
	public void run() {
		final String methodName="run";
		BufferedWriter writer = null;
		try {
			File targetFile = new File(directory + File.separator + file);
			targetFile.getParentFile().mkdirs(); 
			targetFile.createNewFile();
			try(FileWriter fileWriter = new FileWriter(targetFile);)
			{
			
			writer = new BufferedWriter(fileWriter);
			
			String str;
			String reportTopHearder [];
			//String reportDescription;
			//String topDetails;
			if(reportTopHeaders!=null &&!reportTopHeaders.isEmpty()){
				reportTopHearder=reportTopHeaders.split(";");
				for (String a : reportTopHearder) {
					writer.write(a);
					writer.write('\n');
				}
				/*reportDescription=reportTopHearder[0];
				topDetails=reportTopHearder[1];
				writer.write(reportDescription);
				writer.write('\n');
				writer.write(topDetails);*/
				writer.write('\n');
				writer.write('\n');
						}
			writer.write(columnHeader);
			writer.write('\n');
			while (!this.mutableBoolean.getValue() || !queue.isEmpty()) {

				str = queue.poll(200, TimeUnit.MILLISECONDS);
				if (str == null) {
					Thread.sleep(50);
					continue;
				}
				writer.write(str);
				writer.write('\n');
			}

			writer.close();
			this.filewritten.setValue(true);
			fileWriter.close();
		}
		}catch (InterruptedException | IOException e) {
			log.errorTrace(methodName, e);
		}finally{
			try {
				if(writer!=null)
				writer.close();
			} catch (IOException e1) {
				log.errorTrace(methodName, e1);
			}
		}
	}

}
