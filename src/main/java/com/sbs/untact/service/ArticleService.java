package com.sbs.untact.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sbs.untact.dao.ArticleDao;
import com.sbs.untact.dto.Article;
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

}
