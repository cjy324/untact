package com.sbs.untact.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartRequest;

import com.sbs.untact.dto.GenFile;
import com.sbs.untact.dto.ResultData;
import com.sbs.untact.exceptions.GenFileNotFoundException;
import com.sbs.untact.service.GenFileService;

@Controller
public class CommonGenFileController extends BaseController {
	@Value("${custom.genFileDirPath}")
	private String genFileDirPath;
	
	@Autowired
	private GenFileService genFileService;
	
	@RequestMapping("/common/genFile/doUpload")
	@ResponseBody
	public ResultData doUpload(@RequestParam Map<String, Object> param, MultipartRequest multipartRequest) {
		return genFileService.saveFiles(param, multipartRequest);
	}
	
	@GetMapping("/common/genFile/doDownload")
	public ResponseEntity<Resource> downloadFile(int id, HttpServletRequest request) throws IOException {
		GenFile genFile = genFileService.getGenFile(id);
		String filePath = genFile.getFilePath(genFileDirPath);
		Path path = Paths.get(filePath);

		Resource resource = new InputStreamResource(new FileInputStream(filePath));

		// Try to determine file's content type
		String contentType = request.getServletContext().getMimeType(new File(filePath).getAbsolutePath());

		// Fallback to the default content type if type could not be determined
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity
				.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + genFile.getOriginFileName() + "\"")
				.contentType(MediaType.parseMediaType(contentType))
				.body(resource);
	}
	
	//첨부파일 있는지 여부 확인하는 로직
	//@PathVariable을 사용하면 {relTypeCode}이런식으로 가져오는 것이 가능하다
	@GetMapping("/common/genFile/file/{relTypeCode}/{relId}/{typeCode}/{type2Code}/{fileNo}")
	public ResponseEntity<Resource> showFile(HttpServletRequest request, @PathVariable String relTypeCode,
			@PathVariable int relId, @PathVariable String typeCode, @PathVariable String type2Code,
			@PathVariable int fileNo) throws FileNotFoundException {
		GenFile genFile = genFileService.getGenFile(relTypeCode, relId, typeCode, type2Code, fileNo);

		if (genFile == null) {
			//첨부파일이 없을 경우(error:404) GenFileNotFoundException이라는 클래스에게 넘김
			// 'genFile not found'라는 메시지가 포함된 페이지가 나옴
			throw new GenFileNotFoundException();
		}

		String filePath = genFile.getFilePath(genFileDirPath);
		Resource resource = new InputStreamResource(new FileInputStream(filePath));

		// Try to determine file's content type
		String contentType = request.getServletContext().getMimeType(new File(filePath).getAbsolutePath());

		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(resource);
	}
	
}