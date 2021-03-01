package com.sbs.untact.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sbs.untact.dao.ArticleDao;
import com.sbs.untact.dto.Article;
import com.sbs.untact.dto.Board;
import com.sbs.untact.dto.ResultData;
import com.sbs.untact.util.Util;

//일반 자바에서는 다른 곳에서 ArticleService를 사용하려면
//ArticleService articleService = new ArticleService();
//이런식으로 새로운 객체를 만들어 연결해 주었어야하지만
//스프링부트에서는 @Component를 달아주면  = new ArticleService();를 생략해도 된다.
//대신 이 객체를 연결하려는 장소에서 @Autowired를 달아주어야 한다.
//@Autowired는 이정표 같은 개념
//또한, 이 객체가 service라면 @Service만 달아주고 @Component를 생략한다.
//@Component
@Service
public class ArticleService {

	@Autowired
	private ArticleDao articleDao;
	@Autowired
	private MemberService memberService;

	public Article getArticle(int id) {
		return articleDao.getArticle(id);
	}

	public List<Article> getArticles(String searchKeywordType, String searchKeyword) {
		return articleDao.getArticles(searchKeywordType, searchKeyword);
	}

	public ResultData addArticle(Map<String, Object> param) {
		articleDao.addArticle(param);

		int id = Util.getAsInt(param.get("id"), 0);
		
		return new ResultData("S-1", "성공하였습니다.", "id", id);
	}

	public ResultData deleteArticle(int id) {
		articleDao.deleteArticle(id);

		return new ResultData("S-1", "삭제하였습니다.", "id", id);
	}

	public ResultData modifyArticle(int id, String title, String body) {
		articleDao.modifyArticle(id, title, body);

		return new ResultData("S-1", "게시물을 수정하였습니다.", "id", id);
	}

	public ResultData getActorCanModifyRd(Article article, int actorId) {
		//1. 작성인 본인인 경우
		if (article.getMemberId() == actorId) {
			return new ResultData("S-1", "가능합니다.");
		}
		//2. 관리자인 경우
		if (memberService.isAdmin(actorId)) {
			return new ResultData("S-2", "가능합니다.");
		}
		//3. 작성인, 관리자 둘다 아닌 경우
		return new ResultData("F-1", "권한이 없습니다.");
	}

	public ResultData getActorCanDeleteRd(Article article, int actorId) {
		return getActorCanModifyRd(article, actorId);
	}

	public Article getForPrintArticle(Integer id) {
		return articleDao.getForPrintArticle(id);
	}

	public List<Article> getForPrintArticles(int boardId, String searchKeywordType, String searchKeyword, int page, int itemsInAPage) {
	
			int limitStart = (page - 1) * itemsInAPage;
			int limitTake = itemsInAPage;

			return articleDao.getForPrintArticles(boardId, searchKeywordType, searchKeyword, limitStart, limitTake);
	}

	public Board getBoard(int id) {
		return articleDao.getBoard(id);
	}

	public ResultData addReply(Map<String, Object> param) {
		articleDao.addReply(param);

		int id = Util.getAsInt(param.get("id"), 0);

		return new ResultData("S-1", "성공하였습니다.", "id", id);
	}

}
