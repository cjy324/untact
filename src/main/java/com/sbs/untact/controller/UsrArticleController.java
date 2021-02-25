package com.sbs.untact.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sbs.untact.dto.Article;
import com.sbs.untact.dto.ResultData;
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
	public ResultData doAdd(String title, String body) {
		
		String regDate = Util.getNowDateStr();
		String updateDate = Util.getNowDateStr();
		
		articles.add(new Article(++articleLastId, regDate, updateDate, title, body));

		return new ResultData("S-1", "성공하였습니다.", "id", articleLastId);
	}

	@RequestMapping("/usr/article/doDelete")
	@ResponseBody
	public ResultData doDelete(int id) {

		boolean deleteArticleRs = deleteArticle(id);

		if (deleteArticleRs == false) {
			return new ResultData("F-1", "해당 게시물은 존재하지 않습니다.");
		}
		
		return new ResultData("S-1", id + "번 게시물을 삭제하였습니다.");
	}

	@RequestMapping("/usr/article/doModify")
	@ResponseBody
	public ResultData doModify(int id, String title, String body) {

		String updateDate = Util.getNowDateStr();
		
		Article selArticle = null;

		for (Article article : articles) {
			if (article.getId() == id) {
				selArticle = article;
				break;
			}
		}

		if (selArticle == null) {
			return new ResultData("F-1", "해당 게시물은 존재하지 않습니다.", "id", id);
		}

		selArticle.setUpdateDate(updateDate);
		selArticle.setTitle(title);
		selArticle.setBody(body);

		return new ResultData("S-1", "성공", "id", id);
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
