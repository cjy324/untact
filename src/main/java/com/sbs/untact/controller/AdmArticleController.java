package com.sbs.untact.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartRequest;

import com.sbs.untact.dto.Article;
import com.sbs.untact.dto.Board;
import com.sbs.untact.dto.GenFile;
import com.sbs.untact.dto.Member;
import com.sbs.untact.dto.ResultData;
import com.sbs.untact.service.ArticleService;
import com.sbs.untact.service.GenFileService;
import com.sbs.untact.util.Util;

@Controller
public class AdmArticleController extends BaseController{

	@Autowired
	private ArticleService articleService;
	@Autowired
	private GenFileService genFileService;

	@RequestMapping("/adm/article/detail")
	@ResponseBody
	// 스프링부트: 알아서 json형태로 바꿔 출력값을 리턴해준다.
	public ResultData showDetail(Integer id) {
		if (id == null) {
			return new ResultData("F-1", "id를 입력해주세요.");
		}

		Article article = articleService.getForPrintArticle(id);

		if (article == null) {
			return new ResultData("F-2", "존재하지 않는 게시물번호 입니다.");
		}
	
		return new ResultData("S-1", "성공", "article", article);
	}

	@RequestMapping("/adm/article/list")
	//@ResponseBody
	public String showList(HttpServletRequest req, @RequestParam(defaultValue = "1") int boardId, String searchKeywordType, String searchKeyword, @RequestParam(defaultValue = "1") int page) {
		// @RequestParam(defaultValue = "1") int page : page 파라미터의 값이 없으면 디폴트로 1이다.
		
		Board board = articleService.getBoard(boardId);
		
		req.setAttribute("board", board);

		if ( board == null ) {
			return msgAndBack(req, "존재하지 않는 게시판 입니다.");
		}
		
		if (searchKeywordType != null) {
			searchKeywordType = searchKeywordType.trim();
		}

		if (searchKeywordType == null || searchKeywordType.length() == 0) {
			searchKeywordType = "titleAndBody";
		}

		if (searchKeyword != null && searchKeyword.length() == 0) {
			searchKeyword = null;
		}

		if (searchKeyword != null) {
			searchKeyword = searchKeyword.trim();
		}

		if ( searchKeyword == null ) {
			searchKeywordType = null;
		}
		
		int itemsInAPage = 10;
		
		List<Article> articles = articleService.getForPrintArticles(boardId, searchKeywordType, searchKeyword, page, itemsInAPage);
		
		
		// 21.03.06 게시물 리스트에서 첨부 이미지 가져오는 쿼리를 게시물 마다 1번씩 실행하지 않도록 로직 변경함에 따라 필요 없음
		/* 각 article에 달려있는 첨부파일 섬네일 가져오기 시작 */
		/*
		for ( Article article : articles ) {
											//String relTypeCode, int relId, String typeCode, String type2Code, int fileNo
			GenFile genFile = genFileService.getGenFile("article", article.getId(), "common", "attachment", 1);

			if ( genFile != null ) {
				//img의 url을 가져오기
				article.setExtra__thumbImg(genFile.getForPrintUrl());
			}
		}
		*/
		/* 각 article에 달려있는 첨부파일 섬네일 가져오기 끝 */
		
		
		
		req.setAttribute("articles", articles);

		return "adm/article/list";
	}
	
	@RequestMapping("/adm/article/add")
	public String showAdd(@RequestParam Map<String, Object> param, HttpServletRequest req) {
		return "adm/article/add";
	}

	@RequestMapping("/adm/article/doAdd")
	public String doAdd(@RequestParam Map<String, Object> param, HttpServletRequest req, MultipartRequest multipartRequest) {
		//HttpSession session을 HttpServletRequest req로 교체, 인터셉터에서 session 정보를 Request에 담음으로 
		//session을 가져올 필요 없이 req로 값을 받으면 됨
		
		int loginedMemberId = (int)req.getAttribute("loginedMemberId");

		if (param.get("title") == null) {
			return msgAndBack(req, "title을 입력해주세요.");
		}
		if (param.get("body") == null) {
			return msgAndBack(req, "body를 입력해주세요.");
		}

		param.put("memberId", loginedMemberId);
		
		ResultData addArticleRd = articleService.addArticle(param);
		
		// addArticleRd map의 body에서 key값이 id인 것을 가져와라
		int newArticleId = (int) addArticleRd.getBody().get("id");
		
		
		/* 이미 ajax상에서 처리하므로 더이상 필요 없음
		//MultipartRequest : 첨부파일 기능 관련 요청
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap(); //MultipartRequest로 들어온 map 정보를 가져오기
				
		
		//fileMap.keySet() : file__article__0__common__attachment__1
		for (String fileInputName : fileMap.keySet()) {
			//fileInputName : file__article__0__common__attachment__1
			MultipartFile multipartFile = fileMap.get(fileInputName);
			
			if(multipartFile.isEmpty() == false) {
				//저장할 파일관련 정보를 넘김
				genFileService.save(multipartFile, newArticleId);
			}
			
		}
		*/
		return msgAndReplace(req, newArticleId + "번 게시물이 생성되었습니다.", "../article/detail?id=" + newArticleId);
	}

	@RequestMapping("/adm/article/doDelete")
	@ResponseBody
	public ResultData doDelete(Integer id, HttpServletRequest req) {
		// int 기본타입 -> null이 들어갈 수 없음
		// Integer 객체타입 -> null이 들어갈 수 있음
		//int loginedMemberId = (int)req.getAttribute("loginedMemberId");
		Member loginedMember = (Member) req.getAttribute("loginedMember");
		
		if (id == null) {
			return new ResultData("F-1", "id를 입력해주세요.");
		}

		Article article = articleService.getArticle(id);

		if (article == null) {
			return new ResultData("F-1", "해당 게시물은 존재하지 않습니다.");
		}
		
		ResultData actorCanDeleteRd = articleService.getActorCanDeleteRd(article, loginedMember);

		if (actorCanDeleteRd.isFail()) {
			return actorCanDeleteRd;
		}

		return articleService.deleteArticle(id);
	}
	
	@RequestMapping("/adm/article/modify")
	public String showModify(Integer id, HttpServletRequest req) {
		if (id == null) {
			return msgAndBack(req, "id를 입력해주세요.");
		}

		Article article = articleService.getForPrintArticle(id);
		
		if (article == null) {
			return msgAndBack(req, "존재하지 않는 게시물번호 입니다.");
		}

		List<GenFile> genfiles = genFileService.getGenFiles("article", article.getId(), "common", "attachment");

		Map<String, GenFile> filesMap = new HashMap<>();

		for (GenFile genfile : genfiles) {
			filesMap.put(genfile.getFileNo() + "", genfile);
		}

		article.getExtraNotNull().put("file__common__attachment", filesMap);
		req.setAttribute("article", article);

		return "adm/article/modify";
	}
	

	@RequestMapping("/adm/article/doModify")
	@ResponseBody
	public ResultData doModify(@RequestParam Map<String, Object> param, HttpServletRequest req) {
		// int 기본타입 -> null이 들어갈 수 없음
		// Integer 객체타입 -> null이 들어갈 수 있음
		
		//int loginedMemberId = (int)req.getAttribute("loginedMemberId");
		Member loginedMember = (Member) req.getAttribute("loginedMember");
		
		int id = Util.getAsInt(param.get("id"), 0);

		if (id == 0) {
			return new ResultData("F-1", "id를 입력해주세요.");
		}

		if (Util.isEmpty(param.get("title"))) {
			return new ResultData("F-1", "title을 입력해주세요.");
		}
		if (Util.isEmpty(param.get("body"))) {
			return new ResultData("F-1", "body를 입력해주세요.");
		}

		Article article = articleService.getArticle(id);

		if (article == null) {
			return new ResultData("F-1", "해당 게시물은 존재하지 않습니다.", "id", id);
		}

		ResultData actorCanModifyRd = articleService.getActorCanModifyRd(article, loginedMember);

		if (actorCanModifyRd.isFail()) {
			return actorCanModifyRd;
		}
		
		return articleService.modifyArticle(param);
	}
	
	@RequestMapping("/adm/article/doAddReply")
	@ResponseBody
	public ResultData doAddReply(@RequestParam Map<String, Object> param, HttpServletRequest req) {
		int loginedMemberId = (int) req.getAttribute("loginedMemberId");

		if (param.get("body") == null) {
			return new ResultData("F-1", "body를 입력해주세요.");
		}

		if (param.get("articleId") == null) {
			return new ResultData("F-1", "articleId를 입력해주세요.");
		}

		param.put("memberId", loginedMemberId);

		return articleService.addReply(param);
	}
}
