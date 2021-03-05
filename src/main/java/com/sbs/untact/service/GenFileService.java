package com.sbs.untact.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import com.google.common.base.Joiner;
import com.sbs.untact.dao.GenFileDao;
import com.sbs.untact.dto.GenFile;
import com.sbs.untact.dto.ResultData;
import com.sbs.untact.util.Util;

@Service
public class GenFileService {
	
	//파일이 저장될 폴더 경로를 가져오는 명령어
	@Value("${custom.genFileDirPath}")
	private String genFileDirPath;
	
	@Autowired
	private GenFileDao genFileDao;

	public ResultData saveMeta(String relTypeCode, int relId, String typeCode, String type2Code, int fileNo,
			String originFileName, String fileExtTypeCode, String fileExtType2Code, String fileExt, int fileSize,
			String fileDir) {

		Map<String, Object> param = Util.mapOf("relTypeCode", relTypeCode, "relId", relId, "typeCode", typeCode,
				"type2Code", type2Code, "fileNo", fileNo, "originFileName", originFileName, "fileExtTypeCode",
				fileExtTypeCode, "fileExtType2Code", fileExtType2Code, "fileExt", fileExt, "fileSize", fileSize,
				"fileDir", fileDir);
		genFileDao.saveMeta(param);

		int id = Util.getAsInt(param.get("id"), 0);
		
		return new ResultData("S-1", "성공하였습니다.", "id", id);
	}

	public ResultData save(MultipartFile multipartFile) {
		String fileInputName = multipartFile.getName();
		//'file__article__0__common__attachment__1'를 "__" 기준으로 쪼갠다.
		//  0       1	  2    3          4      5
		String[] fileInputNameBits = fileInputName.split("__");

		if (fileInputNameBits[0].equals("file") == false) {
			return new ResultData("F-1", "파라미터명이 올바르지 않습니다.");
		}
		
		// getSize() : 파일 사이즈를 가져오는 명령어
		int fileSize = (int) multipartFile.getSize();

		// 파일 사이즈가 0이거나 0보다 작으면
		if (fileSize <= 0) {
			return new ResultData("F-2", "파일이 업로드 되지 않았습니다.");
		}

		String relTypeCode = fileInputNameBits[1];
		int relId = Integer.parseInt(fileInputNameBits[2]);
		String typeCode = fileInputNameBits[3];
		String type2Code = fileInputNameBits[4];
		int fileNo = Integer.parseInt(fileInputNameBits[5]);
		String originFileName = multipartFile.getOriginalFilename();
		String fileExtTypeCode = Util.getFileExtTypeCodeFromFileName(multipartFile.getOriginalFilename());
		String fileExtType2Code = Util.getFileExtType2CodeFromFileName(multipartFile.getOriginalFilename());
		String fileExt = Util.getFileExtFromFileName(multipartFile.getOriginalFilename()).toLowerCase();

		if (fileExt.equals("jpeg")) {
			fileExt = "jpg";
		} else if (fileExt.equals("htm")) {
			fileExt = "html";
		}

		String fileDir = Util.getNowYearMonthDateStr();

		ResultData saveMetaRd = saveMeta(relTypeCode, relId, typeCode, type2Code, fileNo, originFileName,
				fileExtTypeCode, fileExtType2Code, fileExt, fileSize, fileDir);
		int newGenFileId = (int) saveMetaRd.getBody().get("id");

		// 새 파일이 저장될 폴더(io파일) 객체 생성
		String targetDirPath = genFileDirPath + "/" + relTypeCode + "/" + fileDir;
		java.io.File targetDir = new java.io.File(targetDirPath);

		// 새 파일이 저장될 폴더가 존재하지 않는다면 생성
		if (targetDir.exists() == false) {
			targetDir.mkdirs();
		}

		String targetFileName = newGenFileId + "." + fileExt;
		String targetFilePath = targetDirPath + "/" + targetFileName;

		// 파일 생성(업로드된 파일을 지정된 경로로 옮김)
		try {
			multipartFile.transferTo(new File(targetFilePath));
		} catch (IllegalStateException | IOException e) {
			return new ResultData("F-3", "파일저장에 실패하였습니다.");
		}

		return new ResultData("S-1", "파일이 생성되었습니다.", "id", newGenFileId, "fileRealPath", targetFilePath, "fileName",
				targetFileName, "fileInputName", fileInputName);
	}

	public GenFile getGenFile(String relTypeCode, int relId, String typeCode, String type2Code, int fileNo) {
		return genFileDao.getGenFile(relTypeCode, relId, typeCode, type2Code, fileNo);
	}
	
	public ResultData saveFiles(MultipartRequest multipartRequest) {
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		Map<String, ResultData> filesResultData = new HashMap<>();
		List<Integer> genFileIds = new ArrayList<>();

		//fileMap.keySet() : file__article__0__common__attachment__1
		for (String fileInputName : fileMap.keySet()) {
			//fileInputName : file__article__0__common__attachment__1
			MultipartFile multipartFile = fileMap.get(fileInputName);

			if (multipartFile.isEmpty() == false) {
				ResultData fileResultData = save(multipartFile);
				int genFileId = (int) fileResultData.getBody().get("id");
				genFileIds.add(genFileId);

				filesResultData.put(fileInputName, fileResultData);
			}
		}
		
		//genFileIds 리스트를 ","를 기준으로 문장형으로 조인(결합)시켜주는 구아바
		//ex) [1,2,3,4,5]  => 1,2,3,4,5
		String genFileIdsStr = Joiner.on(",").join(genFileIds);

		return new ResultData("S-1", "파일을 업로드하였습니다.", "filesResultData", filesResultData, "genFileIdsStr",
				genFileIdsStr);
	}

	public void changeRelId(int id, int relId) {
		genFileDao.changeRelId(id, relId);
	}

	public void deleteFiles(String relTypeCode, int relId) {
		//게시물에 달려있는 genFile 리스트 불러오기
		List<GenFile> genFiles = genFileDao.getGenFiles(relTypeCode, relId);

		for ( GenFile genFile : genFiles ) {
			deleteFile(genFile);
		}
	}

	private void deleteFile(GenFile genFile) {
		//1. genFile의 저장소 경로를 찾고 저장소에서 삭제(유틸 활용)
		String filePath = genFile.getFilePath(genFileDirPath);
		Util.delteFile(filePath);

		//2. genFile정보를 DB상에서 삭제
		genFileDao.deleteFile(genFile.getId());
	}

	public List<GenFile> getGenFiles(String relTypeCode, int relId, String typeCode, String type2Code) {
		return genFileDao.getGenFiles(relTypeCode, relId, typeCode, type2Code);
	}


}
