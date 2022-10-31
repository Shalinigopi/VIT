Email Code

package org.vtop.research.postgreSQL.service;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.vtop.research.mongoDB.model.ResearchDCGuidePanel;
import org.vtop.research.mongoDB.model.ResearchDCMemberOrder;
import org.vtop.research.mongoDB.service.ResearchDCMemberOrderService;
import org.vtop.research.mongoDB.service.ResearchDcGuidePanelService;
import org.vtop.research.postgreSQL.model.ResearchDcMemberOrderLogModel;
import org.vtop.research.postgreSQL.model.ResearchDcMemberOrderLogPK;
import org.vtop.research.postgreSQL.repository.ResearchDcMemberOrderLogRepository;
import org.vtop.research.repository.ResearchDcMembersApprovalsRepository;
import org.vtop.research.repository.ResearchDcMembersRepository;
import org.vtop.research.util.ByteArrayDecodedMultipartFile;
import org.vtop.services.util.MailUtility;
import org.vtop.services.util.PDFUtil;

@Service
public class ResearchDcMemberOrderLogService
{
	private static final Logger LOGGER = LogManager.getLogger(ResearchDcMemberOrderLogService.class);

	@Autowired
	private ResearchDcMemberOrderLogRepository researchDcMemberOrderLogRepository;

	@Autowired
	private ResearchDCMemberOrderService researchDCMemberOrderSerivce;

	@Autowired
	private PDFUtil pdfUtil;

	@Autowired
	private ResearchDcMembersApprovalsRepository researchDcMembersApprovalsRepository;

	@Autowired
	private ResearchDcGuidePanelService researchDcGuidePanelService;

	@Autowired
	private ResearchDcGuidePanelLogService researchDcGuidePanelLogService;

	@Autowired
	private MailUtility mailUtility;

	@Autowired
	private Environment environment;
	
	@Autowired 
	private ResearchDcMembersRepository researchDcMembersRepository;

	/**
	 * Method for Getting Members Details
	 * @author Salini Gopi
	 * @param regNo
	 * @param researchDcMemberOrderLogModel
	 * @return
	 */
	public List<Object[]> scholarDetailstoDcAutomation(String regNO) 
	{
		List<Object[]> studData =researchDcMemberOrderLogRepository.scholarDetailstoDcAutomation(regNO);
		return studData;
	}

	/**
	 * Method for Getting ResearchDeanDetails
	 * @author Salini Gopi
	 * @param employeeId
	 * @return
	 */
	public List<Object[]> researchDeanDetails(String employeeId)
	{
		List<Object[]> deanData = researchDcMemberOrderLogRepository.researchDeanDetails(employeeId);
		return deanData;
	}

	/**
	 * Saving Process in DcOrderLog for Members
	 * @author Salini Gopi
	 * @param registerNumber
	 * @param memberId
	 * @param logUserId
	 * @param logIpAddress
	 * @param fileId
	 * @return
	 */
	public String saveLog(String registerNumber, Integer memberId, String logUserId, String logIpAddress,
			String fileId)
	{
		try 
		{
			ResearchDcMemberOrderLogPK researchDcMemberOrderLogPK = new ResearchDcMemberOrderLogPK();
			ResearchDcMemberOrderLogModel researchDcMemberOrderLogModel = new ResearchDcMemberOrderLogModel();
			researchDcMemberOrderLogPK.setStdntslgndtlsRegisterNumber(registerNumber);
			researchDcMemberOrderLogPK.setMemberId(memberId);
			researchDcMemberOrderLogModel.setResearchDcMemberOrderLogPK(researchDcMemberOrderLogPK);
			researchDcMemberOrderLogModel.setFileId(fileId);
			researchDcMemberOrderLogModel.setLogUserId(logUserId);
			researchDcMemberOrderLogModel.setLogIpAddress(logIpAddress);
			researchDcMemberOrderLogModel.setLogTimeStamp(new Date());
			researchDcMemberOrderLogRepository.save(researchDcMemberOrderLogModel);
			return "SUCCESS";
		}
		catch (Exception exception)
		{
			LOGGER.trace("Something Went Wrong in saveing process....: " + exception.toString());
			return "ERROR";
		}
	}
	
	/**
	 * Method for Image
	 * @param ImageName
	 * @return
	 */
	public byte[] extractBytes(String ImageName) 
	{
		byte[] bytes = new byte[] {};
		try
		{
			File file = new File(ImageName);
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];

			for (int readNum; (readNum = fis.read(buf)) != -1;)
			{
				bos.write(buf, 0, readNum);
			}

			bytes = bos.toByteArray();
			fis.close();
		} 
		catch (Exception exception)
		{
			LOGGER.trace("exception...........!!!" + exception.getMessage());
		}
		return bytes;
	}

	/**
	 * Genetate PDF and Mail Trigger in DcAutomation 
	 * @author Salini Gopi
	 * @param regNo
	 * @param memberIdList
	 * @param logUserId
	 * @param logIpAddress
	 * @return
	 */
	public String generateAndSaveResearchDcLetter(String regNo, List<Integer> memberIdList, Integer existMemberIdList, String logUserId, String logIpAddress) 
	{
		List<Object[]> scholarData = new ArrayList<Object[]>();
		Map<String, Object> data = new HashMap<String, Object>();

		String studName = "", studSchool = "", gender = "", degree = "", guideName = "", guideEmpId = "", category = "",
				salutation = "", guideMobileNumber = "", guideEmailId = "", convenorName = "", deanName = "",deanDesignation="",
				message = "", convenorSchool = "", guideSchool = "";
		String appMode = environment.getProperty("app.env");
		String memberEmail = "";

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
		String dcLetterDate = dtf
				.format(Instant.ofEpochMilli(new Date().getTime()).atZone(ZoneId.systemDefault()).toLocalDate());
		String dcLetterNumber = "L02";
		StringBuilder builder = new StringBuilder();

		try 
		{
			scholarData = scholarDetailstoDcAutomation(regNo);

			if (!scholarData.isEmpty())
			{
				studName = scholarData.get(0)[2].toString();
				studSchool = scholarData.get(0)[3].toString();
				guideEmpId = scholarData.get(0)[4].toString();
				guideName = scholarData.get(0)[5].toString();
				guideSchool = scholarData.get(0)[6].toString();
				degree = scholarData.get(0)[7].toString();
				category = scholarData.get(0)[8].toString();
				gender = scholarData.get(0)[9].toString();
				guideMobileNumber = scholarData.get(0)[10].toString();
				guideEmailId = scholarData.get(0)[11].toString();
				salutation = gender.equalsIgnoreCase("MALE") ? "Mr." : "Ms. ";
				
				/* delete existing records */
				if(existMemberIdList != null)
				{
					researchDcGuidePanelService.deleteByRegisterNumberAndOrderType(regNo, 1);//mongoDelete
					researchDcGuidePanelLogService.deleteByResearchDcGuidePanelLogPKStdntslgndtlsRegisterNumberAndResearchDcGuidePanelLogPKOrderType(regNo, 1);//logDelete
					researchDCMemberOrderSerivce.deleteByRegisterNumberAndMemberId(regNo, existMemberIdList);
					researchDcMemberOrderLogRepository.deleteOneMember(regNo, existMemberIdList);
				}
				int count = 0;
				for (Integer memberId : memberIdList) 
				{
					count++;
					data.clear();
					byte[] pdfFile = new byte[] {};

					List<Object[]> memberDetails = researchDcMembersApprovalsRepository.listByDcmembers(regNo); // externalType
					String memberName = memberDetails.stream()
							.filter(e -> Integer.parseInt(e[11].toString()) == memberId).findFirst().get()[1].toString();
					String memberType = memberDetails.stream()
							.filter(e -> Integer.parseInt(e[11].toString()) == memberId).findFirst().get()[0].toString();

					List<Object[]> deanDetails = researchDcMemberOrderLogRepository.researchDeanDetails(regNo);
					deanName = deanDetails.stream().findFirst().get()[0].toString();
					deanDesignation=deanDetails.stream().findFirst().get()[1].toString();
					
					List<Object[]> convenorDetails = researchDcMemberOrderLogRepository.schoolDeanNameDeanDcAutomation(regNo);
					convenorName = convenorDetails.stream().findFirst().get()[0].toString();
					convenorSchool = convenorDetails.stream().findFirst().get()[1].toString();
					
					ResearchDCMemberOrder researchDCMemberOrder = researchDCMemberOrderSerivce.getOne(regNo, memberId);
			
					if (researchDCMemberOrder == null) 
					{
 						data.put("degree", degree);
						data.put("appMode", appMode);
						data.put("regNo", regNo);
						data.put("studName", studName);
						data.put("category", category);
						data.put("studSchool", studSchool);
						data.put("guideName", guideName);
						data.put("guideSchool", guideSchool);
						data.put("guideEmpId", guideEmpId);
						data.put("salutation", salutation);
						data.put("guideMobileNumber", guideMobileNumber);
						data.put("guideEmailId", guideEmailId);
						data.put("memberType", memberType);
						data.put("memberName",  memberName);
						data.put("deanName", deanName);
						data.put("dcLetterNumber", dcLetterNumber);
						data.put("dcLetterDate", dcLetterDate);
						data.put("convenorName",convenorName);
						data.put("convenorSchool", convenorSchool);
						data.put("memberId", memberId);
						data.put("count", count);
						data.put("deanDesignation", deanDesignation);
						
						/* For Committee PDF */
						data.put("memberDetailsApproved",
								researchDcMembersApprovalsRepository.listByDcmembersByStatusApproved(regNo));
						pdfFile = pdfUtil.createPdfByteArray("research/DcAutomation/ExternalMemberOne", data);

						if (count > 0 && count < 3) 
						{
							if(count == 1)
							{
								List<Object[]> members = researchDcMembersApprovalsRepository.listByDcmembersByStatusApproved(regNo); // externalType
								
								for (Object[] objects : members) 
								{
									String externalMemberOneName = objects[1].toString();
									externalMemberOneName = getMemberName(externalMemberOneName);
									builder.append(externalMemberOneName).append(",");
								}
								
								builder.setLength(builder.length()-1);
								data.put("externalMemberNames", builder);
							}
							String pageName = count == 1 ? "research/DcAutomation/DcConstitution" : "research/DcAutomation/DcGuide";//1.DcConstitution,2.Guide
							byte[] pdfFileForGuide = pdfUtil.createPdfByteArray(pageName, data); // Type 1
							researchDcGuidePanelService.save(regNo, count, pdfFileForGuide, logUserId, logIpAddress);// MongoSave
							ResearchDCGuidePanel researchDCGuidePanel = researchDcGuidePanelService.getOne(regNo,count);// MongoGetOne
							researchDcGuidePanelLogService.saveGuidePanelLog(regNo, count, logUserId, logIpAddress,researchDCGuidePanel.getId());//guideLogSave
						}

						researchDCMemberOrderSerivce.save(regNo, memberId, pdfFile, logUserId, logIpAddress);// MongoSave
						researchDCMemberOrder = researchDCMemberOrderSerivce.getOne(regNo, memberId);// MongoGetOne
						saveLog(regNo, memberId, logUserId, logIpAddress, researchDCMemberOrder.getId());// memberLogSave

						if (researchDCMemberOrderSerivce.isExist(regNo))
						{
							researchDCMemberOrder.setLogIpAddress(logIpAddress);
							memberEmail = memberDetails.stream()
										.filter(e -> Integer.parseInt(e[11].toString()) == memberId).findFirst().get()[8].toString();
							data.put("memberEmail", memberEmail);
						}
					}
					sendEmailToMembers(data);
				}

				List<MultipartFile> guide = new ArrayList<>();
				ResearchDCGuidePanel dCGuidePanel = researchDcGuidePanelService.getOne(regNo, 1);
				ResearchDCGuidePanel researchDCGuidePanel = researchDcGuidePanelService.getOne(regNo, 2);
				guide.add(new ByteArrayDecodedMultipartFile(dCGuidePanel.getFileContent(),
						dCGuidePanel.getFileName()));
				guide.add(new ByteArrayDecodedMultipartFile(researchDCGuidePanel.getFileContent(),
						researchDCGuidePanel.getFileName()));

				String sub = "Constitution of Doctoral Advisory Committee for the scholar, " + salutation
						+ studName + " " + regNo + "-Reg";

				String bodyGuide = "<html><body>" + "Dear Prof.  "+ "Dr."+ guideName +"<br>" +System.lineSeparator()+"<br>"
						+"Greetings!" +"<br><br><br>"+System.lineSeparator()
						+ "In respect of your " + degree + "scholar, " + salutation + studName + " (" + regNo + "), I am to inform you"
						+" that a Doctoral Advisory Committee (DAC) is constituted." 
						+ " Please find the list of members in the attachment.<br><br>"
						+ "Role of Doctoral Advisory Committee:<br>" + System.lineSeparator()
						+ "<p style=text-align: justify;>&nbsp;&nbsp; As per"
						+ "the Ph.D. regulations of VIT, a Doctoral Advisory Committee is to be"
						+ "	constituted for each Research scholar to continuously monitor the"
						+ "	progress of the candidate in his/her research work. The DAC consists"
						+ "	of experts in the relevant area both within the Institution and"
						+ "	outside the Institution. The main tasks of the DAC are as follows:</p>"
						+ " The main tasks of the DAC are as follows: "
						+ "<li>To approve the research proposal of the candidate.</li><br>"
						+ "<li>To prescribe the course work required to be undergone by" + "the candidate.</li><br>"
						+ "<li>To conduct comprehensive examination on completion of"
						+ "course work within two years from the date of registration.</li><br>"
						+ "<li>To consider the six-monthly progress reports submitted by"
						+ "the candidate and suggest mid-course corrections wherever needed.</li><br>"
						+ "	<li>To consider and approve the synopsis of the thesis when"
						+ "submitted and recommend a panel of examiners for the evaluation of"
						+ "the thesis.</li><br><br>" + System.lineSeparator()
						+ "This information has also been communicated to the DAC members (both external and internal)."+"<br>"+ "The external members will"
						+"contact you and provide their consent for being a part of the DAC."+"<br>"
						+ "VIT will reimburse"
						+ "	DAC members’ actual expenditure for the travel (limited to train by"
						+ "	two-tier AC sleeper), provide local hospitality during their stay at"
						+ "	Vellore and pay an honorarium of Rs.3000 /- per sitting for the"
						+ "	external DAC member. This is for your kind\n"
						+ "	information."+"<br>"
						+ "Looking forward to your great role in organizing meetings with the DAC members as and when required. <br><br>"
						+ System.lineSeparator() + "Thank you.<br><br>" + System.lineSeparator()
						+ "Yours sincerely<br><br>" + System.lineSeparator() + " " + deanName + "<br>"
						+"Professor &amp; Dean, Academic Research<br>" + System.lineSeparator()
						+ "Vellore Institute of Technology <br>" + System.lineSeparator()
						+ "Vellore 632014, TN, India.<br>" + System.lineSeparator() + "Phone: +91 416 220 2233. <br>"
						+ System.lineSeparator() + "-------------------------------------------<br>"
						+ System.lineSeparator()
						+ "VIT Recognised as Institution of Eminence (IoE) by Government of India Technology<br>"
						+ System.lineSeparator();

				//if (appMode.equalsIgnoreCase("DEV") || appMode.equalsIgnoreCase("TEST"))
				if (!appMode.equalsIgnoreCase("PROD"))
				{
					if(mailUtility.triggerMailResearch(Arrays.asList("salini.g@vit.ac.in"),
							Arrays.asList(
									 "desabandhu.p@vit.ac.in", "salini.g@vit.ac.in"), sub,
							bodyGuide, null, guide, regNo , "RESEARCH")==1)
					{ 
						message = "Success";
					}
				}
				else if (appMode.equalsIgnoreCase("PROD"))
				{
					if(mailUtility.triggerMailResearch(Arrays.asList(guideEmailId),Arrays.asList("ad1.ar@vit.ac.in",
							"dean.ar@vit.ac.in", "aroffice@vit.ac.in"), sub,
							bodyGuide, null, guide, regNo , "RESEARCH")==1)
					{ 
						message = "Success";
					}
				}
			}
			return message;
		}
		catch (NullPointerException nullPointerException)
		{
			LOGGER.trace("exception..." + nullPointerException.getMessage());
		} 
		catch (ParseException parseException) 
		{
			LOGGER.trace("Parse Exception...", parseException);
		} 
		catch (Exception exception) 
		{
			exception.printStackTrace();
			LOGGER.trace("exception..." + exception.getMessage());
		}
		return message;
	}
	
	/**
	 * External type
	 * @author sjt820sdc023
	 * @param regno
	 * @return
	 */
	public List<Object[]> listByDcmembers(String regno)
	{
		return researchDcMembersApprovalsRepository.listByDcmembers(regno);
	}
	
	/**
	 * Description: Mail Trigger for External and Internal members in DcAutomation
	 * @author SaliniGopi
	 * @param inputMap
	 * @return
	 * @throws IOException
	 */
	public Integer sendEmailToMembers(Map<String, Object> inputMap) throws IOException 
	{
		Integer.parseInt(inputMap.get("count").toString());
		String regNo = inputMap.get("regNo").toString();
		Integer memberId = Integer.parseInt(inputMap.get("memberId").toString());
		Integer result=0;

		String member = "VIT will be very happy to reimburse your actual expenditure on travel (limited to train by two-tier AC"
				+ "sleeper), provide local hospitality during your stay at Vellore and pay an honorarium of Rs.3000/- per sitting"
				+ "for the external Doctoral Advisory Committee member.";

		String sub = "Constitution of Doctoral Advisory Committee for the scholar, " + inputMap.get("salutation")
		+ inputMap.get("studName") + " " + inputMap.get("regNo") + "-Reg";

		String bodyMembers = "<html><body> " + "Dear Prof. " + inputMap.get("memberName") + "<br><br>"
				+ System.lineSeparator() + "As per the Ph.D. regulations of VIT, a"
				+ "	Doctoral Advisory Committee is to be constituted for each Research"
				+ "	scholar to continuously monitor the progress of the candidate in"
				+ "	his/her research work. The Doctoral Advisory Committee consists of"
				+ "	experts in the relevant area both within the Institution and outside"
				+ "	the Institution. The main tasks of the Doctoral Advisory Committee" + "	are as follows:<br><br>"
				+ System.lineSeparator() 
				+ "<li>To approve the research proposal of the candidate.</li>\n"
				+ "	<li>To prescribe the course work required to be undergone by\n" + "	the candidate.</li>\n"
				+ "	<li>To conduct comprehensive examination on completion of course work within"
				+ " two years from the date of registration.</li>\n"
				+ "	<li>To consider the six-monthly progress reports submitted by\n"
				+ "	the candidate and suggest mid-course corrections wherever needed.</li>\n"
				+ "	<li>To consider and approve the synopsis of the thesis when\n"
				+ "	submitted and recommend a panel of examiners for the evaluation of\n"
				+ "	the thesis.</li><br><br>" + System.lineSeparator()
				+ "	In this connection, on behalf of the Academic Council of VIT, I invite you to be a member of the Doctoral"
				+ " Advisory Committee in respect of " + inputMap.get("salutation") + inputMap.get("studName")
				+ " who is admitted to the " + inputMap.get("degree") + " programme under the guidance of "
				+ inputMap.get("guideName") + " in the "+inputMap.get("studSchool") + " as an "
				+ inputMap.get("category") + " candidate. The complete constitution of the committee is available in the attachment.<br><br>"
				+ System.lineSeparator()
				+ "I would highly appreciate if you can accept our invitation and share your research expertise and help "
				+ "enhance the quality of research being carried out at VIT. You may please contact the guide (e-mail:"
				+ "" + inputMap.get("guideEmailId") + " , Phone: " + inputMap.get("guideMobileNumber")
				+ ")and provide your consent for acting as a Doctoral Advisory Committee member.<br><br>"
				+ System.lineSeparator();

		if (inputMap.get("memberType").toString().equalsIgnoreCase("E")) 
		{
			bodyMembers += member;
		}

		bodyMembers += "The guide concerned will keep in touch with you for further process.<br><br>"
				+ System.lineSeparator() + "Thank you.<br><br>" + System.lineSeparator() + "Yours sincerely<br>"
				+ System.lineSeparator() + " " + inputMap.get("deanName") + "<br>" + System.lineSeparator()
				+ System.lineSeparator() +"Professor &amp; Dean, Academic Research<br>" + System.lineSeparator()
				+ "Vellore Institute of Technology <br>"+ System.lineSeparator()
				+ "Vellore 632014, TN, India.<br>" + System.lineSeparator()
				+ "Phone: +91 416 220 2233. <br>" + System.lineSeparator()
				+ "-------------------------------------------<br>" + System.lineSeparator()
				+ "VIT Recognised as Institution of Eminence (IoE) by Government of India Technology<br> "
				+ System.lineSeparator() + "</body></html>";

		List<MultipartFile> resource = new ArrayList<>();

		ResearchDCGuidePanel researchDCGuidePanel = researchDcGuidePanelService.getOne(regNo, 1);
		ResearchDCMemberOrder order = researchDCMemberOrderSerivce.getOne(regNo, memberId);

		resource.add(new ByteArrayDecodedMultipartFile(order.getFileContent(), order.getFileName()));
		resource.add(new ByteArrayDecodedMultipartFile(researchDCGuidePanel.getFileContent(),
				researchDCGuidePanel.getFileName()));

		//if (inputMap.get("appMode").toString().equalsIgnoreCase("DEV") || inputMap.get("appMode").toString().equalsIgnoreCase("TEST"))
			
		if (!inputMap.get("appMode").toString().equalsIgnoreCase("PROD"))
		{
			result= mailUtility.triggerMailResearch(Arrays.asList("salini.g@vit.ac.in"),
					Arrays.asList(
							 "desabandhu.p@vit.ac.in",  "salini.g@vit.ac.in"), sub, bodyMembers, null, resource,
					inputMap.get("regNo") + "_" + inputMap.get("memberId") , "RESEARCH");
		}
		else if (inputMap.get("appMode").toString().equalsIgnoreCase("PROD"))
		{
			result= mailUtility.triggerMailResearch(Arrays.asList(inputMap.get("memberEmail").toString()),
					Arrays.asList("ad1.ar@vit.ac.in",  "dean.ar@vit.ac.in", "aroffice@vit.ac.in"), sub, bodyMembers, null, resource,
					inputMap.get("regNo") + "_" + inputMap.get("memberId") , "RESEARCH");
		}
		return result;
	}

	class MultipartInputStreamFileResource extends InputStreamResource 
	{
		private final String filename;

		MultipartInputStreamFileResource(InputStream inputStream, String filename) 
		{
			super(inputStream);
			this.filename = filename;
		}

		@Override
		public String getFilename()
		{
			return this.filename;
		}

		@Override
		public long contentLength() throws IOException 
		{
			return -1; // we do not want to generally read the whole stream into memory ...
		}
	}
	
	public void updateApprovedStatusAPP(String regno, Integer memberId) 
	{
		researchDcMembersRepository.updateApprovedStatusAPP(regno, memberId);
	}

	public void updateApprovedStatusForExistingMembers(String regno,Integer memberId)
	{
		researchDcMembersRepository.updateApprovedStatusForExistingMembers(regno, memberId);
	}

	/**
	 * Description: Static 'Dr' for All in DcAutomation
	 * @author Salini Gopi
	 * @param memberName
	 * @return
	 */
	private String getMemberName(String memberName)
	{
		String result = memberName.startsWith("Dr.") ? memberName.replaceAll("Dr.", "") : 
			(memberName.startsWith("Dr ") ? memberName.replaceAll("Dr ", "") : (memberName.startsWith("Mr.") ? memberName.replaceAll("Mr.", "") : 
				(memberName.startsWith("Mr ") ? memberName.replaceAll("Mr ", "") : (memberName.startsWith("Mrs.") ? memberName.replaceAll("Mrs.", "") : 
					(memberName.startsWith("Mrs ") ? memberName.replaceAll("Mrs ", "") : memberName)))));
		
		return result.trim().toUpperCase();
	}
}













