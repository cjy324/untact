package com.sbs.untact.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.sbs.untact.dto.Article;

/* Mybatis 적용으로 삭제 */
//Mybatis틑 class가 아닌 interface를 인식함

//@Component
//public class ArticleDao {

@Mapper
public interface ArticleDao {
	/* Mybatis 적용으로 기존 내용 삭제 */
	//Mybatis에서 자동으로 만들어 줌
	public Article getArticle(@Param("id") int id);
	public List<Article> getArticles(@Param("searchKeywordType") String searchKeywordType, @Param("searchKeyword") String searchKeyword);
	public void addArticle(Map<String, Object> param);
	public void deleteArticle(@Param("id") int id);
	public void modifyArticle(@Param("id") int id, @Param("title") String title, @Param("body") String body);
	public Article getForPrintArticle(@Param("id") int id);
	public List<Article> getForPrintArticles(@Param("searchKeywordType") String searchKeywordType, @Param("searchKeyword") String searchKeyword, @Param("limitStart") int limitStart,
			@Param("limitTake") int limitTake);
}
