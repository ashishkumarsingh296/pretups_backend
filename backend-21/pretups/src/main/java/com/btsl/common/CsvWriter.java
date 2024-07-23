package com.btsl.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.voms.voucher.businesslogic.VomsPrintBatchVO;

/**
 * 
 * @author ayush.abhijeet
 *
 */
public class CsvWriter implements Runnable {
	
	private Log log = LogFactory.getLog(this.getClass().getName());
	private BlockingQueue<String> queue;
	private long noOfRecords;
	private MutableBoolean mutableBoolean;
	private MutableBoolean filewritten;
	private VomsPrintBatchVO vomsPrintBatchVO;
	private String file;
	private String directory;
	private boolean newFormat = true; 
	public CsvWriter(BlockingQueue<String> queue, String directory, String file, MutableBoolean mutableBoolean, MutableBoolean filewritten, VomsPrintBatchVO vomsPrintBatchVO) {
		super();
		this.queue = queue;
		this.noOfRecords = vomsPrintBatchVO.getTotNoOfVOuchers();
		this.mutableBoolean = mutableBoolean;
		this.filewritten = filewritten;
		this.vomsPrintBatchVO = vomsPrintBatchVO;
		this.directory = directory;
		this.file = file;
	}

	private void writeHeading(BufferedWriter writer) throws IOException {
		
		writer.write("Voucher Details");
		writer.write('\n');
		
		writer.write('\n');
		
		writer.write("Total Vouchers,");
		writer.write(Long.toString(vomsPrintBatchVO.getTotNoOfVOuchers()));
		writer.write('\n');
		
		writer.write("From Serial No,");
		writer.write(vomsPrintBatchVO.getStartSerialNo());
		writer.write('\n');
		
		writer.write("To Serial No,");
		writer.write(vomsPrintBatchVO.getEndSerialNo());
		writer.write('\n');
		
		writer.write("Network,");
		writer.write(vomsPrintBatchVO.getNetwork());
		writer.write('\n');
		
		writer.write("Voucher Type,");
		writer.write(vomsPrintBatchVO.getVoucherName());
		writer.write('\n');
		
		writer.write("Voucher Segment,");
		writer.write(vomsPrintBatchVO.getVoucherSegmentDesc());
		writer.write('\n');
		
		writer.write('\n');
		
		writer.write("Pin,");
		writer.write("Serial No,");
		writer.write("Denomination,");
		writer.write("Expiry Date");
		writer.write('\n');
	}
	
	@Override
	public void run() {
		final String methodName="run";
		
		File targetFile = new File(directory + File.separator + file);
		try(FileWriter fileWriter = new FileWriter(targetFile);BufferedWriter writer = new BufferedWriter(fileWriter);) {
			
			if(!targetFile.getParentFile().mkdirs()) // Will create parent directories if not exists
				log.info(methodName, "Directories creation failed"+directory + File.separator + file);
			
			if(!targetFile.createNewFile())
				log.info(methodName, "File creation failed"+directory + File.separator + file);
			
			String str;
			long i = 1;
			if(newFormat) writeHeading(writer);
			else {
				StringBuilder buffer = new StringBuilder();
				buffer.append("Total Vouchers=");
				buffer.append(vomsPrintBatchVO.getTotNoOfVOuchers());
				buffer.append(",");
				buffer.append("From Serial No=");
				buffer.append(vomsPrintBatchVO.getStartSerialNo());
				buffer.append(",");
				buffer.append("To Serial No=");
				buffer.append(vomsPrintBatchVO.getEndSerialNo());
				buffer.append(",");
				buffer.append("Network=");
				buffer.append(vomsPrintBatchVO.getNetwork());
				buffer.append(",");
				buffer.append("Voucher Type=");
				buffer.append(vomsPrintBatchVO.getVoucherName());
				buffer.append(",");
				buffer.append("Voucher Segment=");
				buffer.append(vomsPrintBatchVO.getVoucherSegmentDesc());
				writer.write(buffer.toString());
				writer.write('\n');	
			}
			while (!this.mutableBoolean.getValue() || i <= this.noOfRecords) {
				str = queue.poll(200, TimeUnit.MILLISECONDS);
				if (str == null) {
					Thread.sleep(50);
					continue;
				}
				i++;
				writer.write(str);
				writer.write('\n');
			}
			this.filewritten.setValue(true);
		} catch (InterruptedException | IOException e) {
			log.errorTrace(methodName, e);
		}
		finally{
			log.info(methodName, "inside finally");
		}
	}

}
