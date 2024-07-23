
package com.client.pretups.channel.user.web;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonController;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.pretups.common.PretupsI;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.client.pretups.channel.user.service.GreetingMsgService;
/*
 *  * This class provides method for loading UI for Greeting Msg as well as 
 *   * uploading Greeting Msg File to The Server
 *    */
@Controller
public class GreetingMsgController extends CommonController {



	@Autowired
	private GreetingMsgService greetingMsgService;
	private static final String forward="channeluser/greetingMsg";	
	/**
 * 	 * Load Category , Geography for Channel Users For Greeting Msgs 
 * 	 	 *
 * 	 	 	 * @param request  The HttpServletRequest object
 * 	 	 	 	 * @param response The HttpServletResponse object
 * 	 	 	 	 	 * @param model The Model object
 * 	 	 	 	 	 	 * @return String the path of view also store user type and module in model object
 * 	 	 	 	 	 	 	 * @throws Exception
 * 	 	 	 	 	 	 	 	 */


		@RequestMapping(value = "/channeluser/greetMsg.form", method = RequestMethod.GET)
		public String loadDomain(final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException, ServletException, IOException, Exception

		 {

			final String methodName = "LoadDomain";
        	if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered");

       		}

			UserVO userVO = this.getUserFormSession(request);
			
			authorise(request, response, "CUSERGT01A", false);
			
			
			List<ListValueVO> domainList = greetingMsgService.loadDomain();
			
			domainList.add(0,new ListValueVO(PretupsI.ALL, PretupsI.ALL));
			
			model.addAttribute("domainList" , domainList);
			request.getSession().setAttribute("category", null);
			List<ListValueVO> geographyList = new ArrayList<ListValueVO>();
			
			if (userVO.getGeographicalAreaList() != null) {
			
				for (int i = 0, j = userVO.getGeographicalAreaList().size(); i < j; i++) {
				
					geographyList.add(new ListValueVO(((UserGeographiesVO) userVO.getGeographicalAreaList().get(i)).getGraphDomainName().toString() , ((UserGeographiesVO) userVO.getGeographicalAreaList().get(i)).getGraphDomainCode().toString()));
				
				}
				if(geographyList.size()>1)
					geographyList.add(0,new ListValueVO(PretupsI.ALL, PretupsI.ALL));
				model.addAttribute("geographyList" , geographyList);
				
			}
			else{
				log.debug(methodName, "geographyList is empty , problem with UserVO");
			}
			if (log.isDebugEnabled()) {
                log.debug(methodName, "Exited");

                }

			return forward;

        	}

			
			
		@RequestMapping(value = "/channeluser/load-category.form", method = RequestMethod.POST)
		public @ResponseBody List<ListValueVO> loadCategory(@RequestParam("domain") String domain,  Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException, ServletException, IOException, Exception

		 {
			final String methodName = "LoadCategory";

        	if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered : domain ="+domain);
		}
		List<ListValueVO> categoryList = null;
	
		
		categoryList = greetingMsgService.loadCategory();
		
		if(!domain.equals(PretupsI.ALL)){
				ListValueVO listValueVO = null;
				if (categoryList != null && !categoryList.isEmpty()) {
				for (int i = 0, j = categoryList.size(); i < j; i++) {
					listValueVO = (ListValueVO) categoryList.get(i);
					
					if (!(listValueVO.getValue().split(":")[0].toString()).equals(domain)) {
						categoryList.remove(i);
						i--;
						j--;
					}
				}
			}
			
		}
		
		
		
		request.getSession().setAttribute("category", categoryList);
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting");
		}
		return categoryList;
     }

			
	@RequestMapping(value = "/channeluser/submit-greet-msg.form", method = RequestMethod.POST)
	public String submitGreetMsgForm(@RequestParam("domain") String domain,@RequestParam("category") String category,@RequestParam("geography") String geography,
			Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		String fileName = category.split(":")[1].trim()+"_"+Constants.getProperty("DownloadUserGreetingMsgFileNamePrefix")+BTSLUtil.getFileNameStringFromDate(new Date());
		final String methodName = "downloadUserList";
	
		InputStream inputStream = null;
			if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered with : DOMAIN=" + domain +",CATEGORY="+category+",GEOGRAPHY="+geography);
		}
		
		
		UserVO userVO = this.getUserFormSession(request);
		
		
		
		inputStream = greetingMsgService.downloadUserList(domain,category.split(":")[1].trim(),geography , userVO.getUserID().toString());
		
		if(inputStream == null)
		{
			if (log.isDebugEnabled()) {
			log.debug(methodName, "Input Stream is null");
			}
			model.addAttribute("failDownload" ,PretupsRestUtil.getMessageString("no.data.found"));
			return forward;
		}
		
		String mimeType = null;
		
		if(mimeType==null){
            System.out.println("mimetype is not detectable, will take default");
            mimeType = "application/octet-stream";
        }
		
		
		response.setContentType("text/csv");
		response.setHeader("Content-Disposition", "attachment; filename="+fileName+"."+PretupsI.FILE_CONTENT_TYPE_CSV.toLowerCase()+"");
		
        FileCopyUtils.copy(inputStream, response.getOutputStream());
		
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited with :"+fileName+"."+PretupsI.FILE_CONTENT_TYPE_CSV);
		}
		return forward;

	}

		
		
		
	
	@RequestMapping(value = "/channeluser/upload-greet-msg.form", method = RequestMethod.POST)
	public String uploadGreetMsgForm(@RequestParam("file") MultipartFile file ,Model model,  HttpServletRequest request, HttpServletResponse response) throws Exception {

		final String methodName = "UploadGreetingMsgForm";
	
		BufferedReader br = null;
		String bytesString = "";
		InputStream inputStream = null;
		
		String filePath = Constants.getProperty("GREETING_UPLOAD_DIR_PATH");
		
		String fileExtn = null;
		String fileName = file.getOriginalFilename();
		UserVO userVO = this.getUserFormSession(request);
		
		
		List<ListValueVO> domainList = greetingMsgService.loadDomain();
		domainList.add(0,new ListValueVO(PretupsI.ALL, PretupsI.ALL));
		model.addAttribute("domainList" , domainList);
		request.getSession().setAttribute("category", null);		
		List<ListValueVO> geographyList = new ArrayList<ListValueVO>();
		
		
		
		if (userVO.getGeographicalAreaList() != null) {
			  int userGeographicalAreaLists=userVO.getGeographicalAreaList().size();
				for (int i = 0, j = userGeographicalAreaLists; i < j; i++) {
				
					geographyList.add(new ListValueVO(((UserGeographiesVO) userVO.getGeographicalAreaList().get(i)).getGraphDomainName().toString() , ((UserGeographiesVO) userVO.getGeographicalAreaList().get(i)).getGraphDomainCode().toString()));
				
				}
				if(geographyList.size()>1)
					geographyList.add(0,new ListValueVO(PretupsI.ALL, PretupsI.ALL));
				model.addAttribute("geographyList" , geographyList);
				
		}
		else{
			log.debug(methodName, "geographyList is empty , problem with UserVO");
		}

		
		
		try {
				
				byte[] bytes = file.getBytes();
				inputStream = new ByteArrayInputStream(bytes); 
				bytesString = new String(bytes);
				String line = null;
				br = new BufferedReader(new InputStreamReader(inputStream));
				Boolean isFileContentValid = false;
				while ((line = br.readLine()) != null) {
                	 
                	 	if(line.length()>160)
                	 	{
                	 		int j = 0;
                	 		for(int i =0 ; j<line.length()-160;i++)
							{
								
									isFileContentValid = BTSLUtil.isFileContentValid(line.substring(j , j+160-1));
									j+=160-1;
								
							}
						}else
						{
							isFileContentValid = BTSLUtil.isFileContentValid(line);
				            		
						}
						if (!isFileContentValid) {
                        	
								model.addAttribute("failUpload" ,PretupsRestUtil.getMessageString("file.content.not.valid"));
								return forward;
						
						}
				}

				
				
				
				  	boolean isFileUpload = false;
					try{
						isFileUpload = uploadFileToServer(file , filePath , file.getSize());
					}
					catch(BTSLBaseException be)
					{
						log.error(methodName, "SQLException " + be);
	        			log.errorTrace(methodName, be);
						if (log.isDebugEnabled()) {
							log.debug(methodName, be.toString());
						}
						model.addAttribute("failUpload" ,PretupsRestUtil.getMessageString(be.getMessageKey() , new String[]{Constants.getProperty("GREETMSG_FILE_SIZE")}));
						return forward;
					}
					
				
                 	
					//String result = greetingMsgService.uploadUserList(bytesString , fileName);
			
                 if(isFileUpload)
                	 model.addAttribute("successUpload",PretupsRestUtil.getMessageString("file.upload.successfully")) ;				
                 else
                	 model.addAttribute("failUpload" ,PretupsRestUtil.getMessageString("file.not.uploaded.successfully"));
			
					
			} catch (Exception e) {
				log.error(methodName, "Exception " + e);
    			log.errorTrace(methodName, e);
				log.debug(methodName, e.toString());
				
				model.addAttribute("failUpload" ,PretupsRestUtil.getMessageString("file.not.uploaded.successfully"));
			}
		
		
		
		return forward;

	}



	
	private boolean uploadFileToServer(MultipartFile file , String filePath , Long fileSize) throws BTSLBaseException, IOException
	{
		final String methodName="uploadFileToServer";
		File fileUpload = null;
		FileOutputStream outputStream = null;
	
		if(file==null)
			throw new BTSLBaseException("file.is.empty");
		else if(!BTSLUtil.isValideFileName(file.getOriginalFilename()))
			throw new BTSLBaseException("file.name.not.valid");
		else if(BTSLUtil.isNullString(filePath))
			throw new BTSLBaseException("dir.not.exist");
		else if(fileSize>Long.parseLong(Constants.getProperty("GREETMSG_FILE_SIZE")))
			throw new BTSLBaseException("file.size.exceed");
		else if(!getFileExtn(file.getOriginalFilename()).equals(PretupsI.FILE_CONTENT_TYPE_CSV.toLowerCase()))
			throw new BTSLBaseException("file.content.type.not.valid");
			
	     File fileDir = new File(filePath);
		 if(!fileDir.isDirectory())
			 fileDir.mkdirs();
		 if(!fileDir.exists())
			 throw new BTSLBaseException("dir.not.exist");
		
		 String fileName= file.getOriginalFilename();
		fileUpload = new File(filePath+"/"+fileName);
		if(fileUpload.exists())
			throw new BTSLBaseException("file.already.exist");
		try{
			outputStream = new FileOutputStream(filePath+"/"+fileName);
			byte[] bytes = file.getBytes();
			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(fileUpload));
			stream.write(bytes);
			stream.close();
			
			
		}catch(IOException io)
		{
			log.error(methodName, "SQLException " + io);
		    log.errorTrace(methodName, io);
			if (log.isDebugEnabled()) {
				log.debug("uploadFIletoServer", io.toString());
			}
			throw new BTSLBaseException("problem.file.upload");
		}
		finally{
			if(outputStream!=null)
				outputStream.close();
		}

		return true;
		
		
	}
	
	private String getFileExtn(String fileName) {
		
		String extn = "";
		log.debug("getFIleExtn", "FILENAME :"+fileName);
		
		int i = fileName.lastIndexOf('.');
		if(i>0)
			extn = fileName.substring(i+1);
		
		
		log.debug("getFIleExtn", "FILENAME :"+extn);
		return extn.trim();
	}

	
	
		
	
	
}
