package com.sbs.untact.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

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
	private List<Article> articles;
	private int articleLastId;

	public ArticleService() {
		// 멤버변수 초기화
		articleLastId = 0;
		articles = new ArrayList<>();

		// init용 article
		articles.add(new Article(++articleLastId, Util.getNowDateStr(), Util.getNowDateStr(), "제목1", "내용1"));
		articles.add(new Article(++articleLastId, Util.getNowDateStr(), Util.getNowDateStr(), "제목2", "내용2"));
		articles.add(new Article(++articleLastId, Util.getNowDateStr(), Util.getNowDateStr(), "제목3", "내용3"));
		articles.add(new Article(++articleLastId, Util.getNowDateStr(), Util.getNowDateStr(), "제목4", "내용4"));
	}

	public Article getArticle(int id) {
		for (Article article : articles) {
			if (article.getId() == id) {
				return article;
			}
		}

		return null;
	}

	public List<Article> getArticles(String searchKeywordType, String searchKeyword) {
		if (searchKeyword == null) {
			return articles;
		}

		List<Article> filtered = new ArrayList<>();

		for (Article article : articles) {
			boolean contains = false;

			if (searchKeywordType.equals("title")) {
				contains = article.getTitle().contains(searchKeyword);
			} else if (searchKeywordType.equals("body")) {
				contains = article.getBody().contains(searchKeyword);
			} else {
				contains = article.getTitle().contains(searchKeyword);

				if (contains == false) {
					contains = article.getBody().contains(searchKeyword);
				}
			}

			if (contains) {
				filtered.add(article);
			}
		}

		return filtered;
	}

	public ResultData add(String title, String body) {
		int id = ++articleLastId;
		String regDate = Util.getNowDateStr();
		String updateDate = regDate;

		articles.add(new Article(id, regDate, updateDate, title, body));

		return new ResultData("S-1", "성공하였습니다.", "id", id);
	}

	public ResultData deleteArticle(int id) {
		for (Article article : articles) {
			if (article.getId() == id) {
				articles.remove(article);
				return new ResultData("S-1", "삭제하였습니다.", "id", id);
			}
		}

		return new ResultData("F-1", "해당 게시물은 존재하지 않습니다.", "id", id);
	}

	public ResultData modify(int id, String title, String body) {
		Article article = getArticle(id);

		article.setTitle(title);
		article.setBody(body);
		article.setUpdateDate(Util.getNowDateStr());

		return new ResultData("S-1", "게시물을 수정하였습니다.", "id", id);
	}

}
