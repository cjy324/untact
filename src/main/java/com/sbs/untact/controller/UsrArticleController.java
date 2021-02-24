package com.sbs.untact.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sbs.untact.dto.Article;
import com.sbs.untact.util.Util;

@Controller
public class UsrArticleController {

	private List<Article> articles;
	private int articleLastId;

	public UsrArticleController() {
		// 멤버변수 초기화
		articleLastId = 0;
		articles = new ArrayList<>();

		//init용 article
		articles.add(new Article(++articleLastId, Util.getNowDateStr(), Util.getNowDateStr(), "제목1", "내용1"));
		articles.add(new Article(++articleLastId, Util.getNowDateStr(), Util.getNowDateStr(), "제목2", "내용2"));
		articles.add(new Article(++articleLastId, Util.getNowDateStr(), Util.getNowDateStr(), "제목3", "내용3"));
		articles.add(new Article(++articleLastId, Util.getNowDateStr(), Util.getNowDateStr(), "제목4", "내용4"));
	}

	@RequestMapping("/usr/article/detail")
	@ResponseBody
	// 스프링부트: 알아서 json형태로 바꿔 출력값을 리턴해준다.
	public Article showDetail(int id) {
		return articles.get(id - 1);
	}

	@RequestMapping("/usr/article/list")
	@ResponseBody
	public List<Article> showList() {
		return articles;
	}

	@RequestMapping("/usr/article/doAdd")
	@ResponseBody
	public Map<String, Object> doAdd(String title, String body) {
		
		String regDate = Util.getNowDateStr();
		String updateDate = Util.getNowDateStr();
		
		articles.add(new Article(++articleLastId, regDate, updateDate, title, body));

		Map<String, Object> rs = new HashMap<>();
		rs.put("resultCode", "S-1");
		rs.put("msg", "성공");
		rs.put("id", articleLastId);

		return rs;
	}

	@RequestMapping("/usr/article/doDelete")
	@ResponseBody
	public Map<String, Object> doDelete(int id) {

		boolean deleteArticleRs = deleteArticle(id);

		Map<String, Object> rs = new HashMap<>();

		if (deleteArticleRs) {
			rs.put("resultCode", "S-1");
			rs.put("msg", "성공");
		} else {
			rs.put("resultCode", "F-1");
			rs.put("msg", "해당 게시물은 존재하지 않습니다.");
		}

		rs.put("id", id);

		return rs;
	}

	@RequestMapping("/usr/article/doModify")
	@ResponseBody
	public Map<String, Object> doModify(int id, String title, String body) {

		String updateDate = Util.getNowDateStr();
		
		Article selArticle = null;

		Map<String, Object> rs = new HashMap<>();

		for (Article article : articles) {
			if (article.getId() == id) {
				selArticle = article;
				break;
			}
		}

		if (selArticle == null) {
			rs.put("resultCode", "F-1");
			rs.put("msg", "해당 게시물은 존재하지 않습니다.");
			rs.put("id", id);
			return rs;
		}

		selArticle.setUpdateDate(updateDate);
		selArticle.setTitle(title);
		selArticle.setBody(body);

		rs.put("resultCode", "S-1");
		rs.put("msg", "성공");

		rs.put("id", id);

		return rs;
	}

	private boolean deleteArticle(int id) {
		for (Article article : articles) {
			if (article.getId() == id) {
				articles.remove(article);
				return true;
			}
		}

		return false;
	}
}
