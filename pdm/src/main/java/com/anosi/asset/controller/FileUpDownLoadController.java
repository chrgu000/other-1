package com.anosi.asset.controller;

import com.alibaba.fastjson.JSONObject;
import com.anosi.asset.bean.FileMetaDataBean;
import com.anosi.asset.exception.CustomRunTimeException;
import com.anosi.asset.i18n.I18nComponent;
import com.anosi.asset.model.mongo.FileMetaData;
import com.anosi.asset.model.mongo.Oplog;
import com.anosi.asset.service.FileMetaDataService;
import com.anosi.asset.service.TechnologyDocumentService;
import com.anosi.asset.util.FileConvertUtil;
import com.anosi.asset.util.FileConvertUtil.Suffix;
import com.anosi.asset.util.StringUtil;
import com.google.common.collect.ImmutableMap;
import com.querydsl.core.types.Predicate;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/***
 * 这个类包含了文件上传、下载、预览功能
 *
 * @author jinyao
 *
 */
@RestController
public class FileUpDownLoadController extends BaseController<FileMetaData> {

	private static final Logger logger = LoggerFactory.getLogger(FileUpDownLoadController.class);

	@Autowired
	private FileMetaDataService fileMetaDataService;
	@Autowired
	private I18nComponent i18nComponent;
	@Autowired
	private TechnologyDocumentService technologyDocumentService;

	/***
	 * 查看某个文件的元数据页面
	 *
	 * @param objectId
	 * @return
	 */
	@RequestMapping(value = "/fileMetaData/{objectId}/view", method = RequestMethod.GET)
	public ModelAndView toViewFileMetaData(@PathVariable String objectId) {
		logger.debug("view FileMetaData");
		return new ModelAndView("fileMetaData/viewFileMetaData", "objectId", objectId);
	}

	/***
	 * 查询某条具体的元数据
	 *
	 * @param predicate
	 * @param showAttributes
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/fileMetaData/management/data/one", method = RequestMethod.GET)
	public JSONObject findFileMetaDataManageDataOne(@QuerydslPredicate(root = FileMetaData.class) Predicate predicate,
			@RequestParam(value = "showAttributes", required = false) String showAttributes) throws Exception {
		logger.info("find messageInfo one");
		return jsonUtil.parseAttributesToJson(StringUtil.splitAttributes(showAttributes),
				fileMetaDataService.findOne(predicate));
	}

	/***
	 * 上传文件
	 *
	 * @param multipartFiles
	 * @param identification
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/fileUpload/multipartFiles/{identification}", method = RequestMethod.POST)
	public JSONObject fileUpload(@RequestParam("file_upload") MultipartFile[] multipartFiles,
								 @PathVariable String identification) throws Exception {
		logger.info("file upload");
		logger.debug("identification:{}", identification);
		JSONObject jsonObject = new JSONObject();
		if (multipartFiles != null && multipartFiles.length > 0) {
			String fileNames = "";
			List<FileMetaDataBean> fileMetaDataBeans = new ArrayList<>();
			for (MultipartFile multipartFile : multipartFiles) {
				logger.debug("is uploading");
				fileNames += StringUtils.isBlank(fileNames) ? multipartFile.getOriginalFilename() : (", " + multipartFile.getOriginalFilename());
				FileMetaDataBean fileMetaDataBean = new FileMetaDataBean(identification,
						multipartFile.getOriginalFilename(), multipartFile.getInputStream(), multipartFile.getSize());
				fileMetaDataBeans.add(fileMetaDataBean);
			}
			try {
				this.fileMetaDataService.saveFile(fileMetaDataBeans);
			} catch (Exception e) {
				throw new CustomRunTimeException("upload fail");
			}
			saveLog("文件", Oplog.Operation.UPLOAD, fileNames);
			jsonObject.put("result", "upload success");
			jsonObject.put("identification", identification);
		} else {
			jsonObject.put("result", "file is null");
		}
		logger.info(jsonObject.toString());
		return jsonObject;
	}

	/***
	 * 根据文件组来获取可下载文件的列表
	 *
	 * @param identification
	 * @throws Exception
	 */
	@RequestMapping(value = "/fileDownload/list/group/{identification}", method = RequestMethod.GET)
	public Page<FileMetaData> fileDownloadListGroup(@PathVariable String identification,
                                                    @PageableDefault(sort = {
					"uploadTime" }, direction = Sort.Direction.DESC, page = 0, value = 20) Pageable pageable)
			throws Exception {
		logger.info("to view file list");
		logger.debug("identification:{}", identification);
		logger.debug("page:{},size{},sort{}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
		return fileMetaDataService.findByIdentification(identification, pageable);
	}

	/***
	 * 根据文件组来获取可下载文件的列表.
	 *
	 * @param showType
	 *            返回数据的形式,可选参数:grid,remote
	 * @throws Exception
	 */
	@RequestMapping(value = "/fileDownload/list/{showType}", method = RequestMethod.GET)
	public JSONObject fileDownloadListGrid(@QuerydslPredicate(root = FileMetaData.class) Predicate predicate,
			@PathVariable ShowType showType,
			@PageableDefault(sort = {
					"uploadTime" }, direction = Sort.Direction.DESC, page = 0, value = 20) Pageable pageable,
			@RequestParam(value = "showAttributes", required = false) String showAttributes,
			@RequestParam(value = "rowId", required = false, defaultValue = "id") String rowId) throws Exception {
		logger.info("to view file list");
		logger.debug("page:{},size{},sort{}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
		return parseToJson(fileMetaDataService.findAll(predicate, pageable), rowId, showAttributes, showType);
	}

	/****
	 * 下载文件
	 *
	 * @param
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/fileDownload/{objectId}", method = RequestMethod.GET)
	public JSONObject fileDownload(@PathVariable BigInteger objectId, HttpServletResponse response) throws Exception {
		DoResponseOut doResponseOut = (is, os) -> {
			// 获取文件流
			try (BufferedInputStream bis = new BufferedInputStream(is);
					OutputStream bos = new BufferedOutputStream(response.getOutputStream());) {
				IOUtils.copy(bis, bos);
			}
		};
		return fileResponse(objectId, fileMetaDataService.findByObjectId(objectId), response, doResponseOut);
	}

	/***
	 * 文件预览
	 *
	 * @param objectId
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/filePreview/{objectId}", method = RequestMethod.GET)
	public JSONObject filePreview(@PathVariable BigInteger objectId, HttpServletResponse response) throws Exception {
		FileMetaData fileMetaData = fileMetaDataService.findByObjectId(objectId);
		BigInteger preview = fileMetaData.getPreview();
		FileMetaData previewMetaData = null;
		if (preview != null) {
			previewMetaData = fileMetaDataService.findByObjectId(preview);
		}
		// 实现接口
		DoResponseOut doResponseOut = (is, os) -> {
			String fileName = "";
			if (preview != null) {
				is = fileMetaDataService.getFileByObjectId(preview);
				fileName = fileMetaDataService.findByObjectId(preview).getFileName();
			} else {
				fileName = fileMetaData.getFileName();
			}
			try {
				FileConvertUtil.convert(is,
						Suffix.valueOf(fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase()), os,
						Suffix.PDF);
			} catch (IllegalArgumentException e) {
				throw new CustomRunTimeException(
						MessageFormat.format(i18nComponent.getMessage("exception.unSupportSuffix"),
								fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase()));
			}
		};

		return fileResponse(objectId, previewMetaData == null ? fileMetaData : previewMetaData, response,
				doResponseOut);
	}

	/***
	 * 删除上传的文件
	 *
	 * @param objectId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/deleteUploadFile/{objectId}", method = RequestMethod.POST)
	public JSONObject deleteUploadFile(@PathVariable BigInteger objectId) throws Exception {
		fileMetaDataService.deleteFile(fileMetaDataService.findByObjectId(objectId));
		return new JSONObject(ImmutableMap.of("result", "success"));
	}
	/***
	 * 删除上传的文件
	 *
	 * @param fileId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/deleteTechnologyDocument", method = RequestMethod.POST)
	public JSONObject deleteTechnologyDocument(@RequestParam BigInteger fileId,@RequestParam String id) throws Exception {
		logger.info("删除技术文档, 文件id{}, 文档id{}", fileId, id);
		try {
			fileMetaDataService.deleteFile(fileMetaDataService.findByObjectId(fileId));
		} catch (NullPointerException n) {
			logger.info("文件已被删掉");
		}
		technologyDocumentService.delete(id);
		return new JSONObject(ImmutableMap.of("result", "success"));
	}

	/***
	 * 文件流返回给浏览器
	 *
	 * @param objectId
	 * @param fileMetaData
	 * @param response
	 * @param doResponseOut
	 *            对response.outPutStream的具体操作
	 * @return
	 * @throws Exception
	 */
	private JSONObject fileResponse(BigInteger objectId, FileMetaData fileMetaData, HttpServletResponse response,
                                    DoResponseOut doResponseOut) throws Exception {
		JSONObject jsonObject = new JSONObject();

		if (fileMetaData != null) {
			logger.debug("fileMetaData:{}", fileMetaData.toString());
			// 设置http请求头
			response.setHeader("Content-disposition",
					"attachment;filename=" + new String(fileMetaData.getFileName().getBytes("gbk"), "iso-8859-1"));
			response.setContentType("application/force-download;charset=utf-8");
			response.setHeader("Content_length", String.valueOf(fileMetaData.getFileSize()));

			doResponseOut.excute(fileMetaDataService.getFileByObjectId(objectId), response.getOutputStream());

			jsonObject.put("result", "success");
		} else {
			logger.info("fileMetaData is null");
			jsonObject.put("result",
					MessageFormat.format(i18nComponent.getMessage("exception.fileNotFound"), objectId));
		}
		logger.info(jsonObject.toString());
		return jsonObject;
	}

	/***
	 * 内部方法接口,用来执行completeTask中的具体操作
	 *
	 * @author jinyao
	 *
	 */
	@FunctionalInterface
	private interface DoResponseOut {
		public void excute(InputStream is, OutputStream os) throws Exception;
	}


	/**
	* 根据id删除上传的文件
	* @since  2018/6/19 18:07
	* @author 倪文骅
	* @param id  行id(多个拼接或单个id)
	* @return com.alibaba.fastjson.JSONObject
	*/
	@RequestMapping(value = "/deleteUploadFile/deleteById", method = RequestMethod.POST)
	public JSONObject deleteUploadFile(@RequestParam(value = "id") String id)  {
		String[] ids = id.split("-");
		for(String objectId : ids){
			fileMetaDataService.deleteFile(fileMetaDataService.findByObjectId(new BigInteger(objectId)));
		}
		return new JSONObject(ImmutableMap.of("result", "success"));
	}
}
