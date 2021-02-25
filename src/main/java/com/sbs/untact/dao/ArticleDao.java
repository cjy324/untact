package com.sbs.untact.dao;

import java.util.List;

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
	public Article getArticle(@Param(value = "id") int id);
	public List<Article> getArticles(@Param(value = "searchKeywordType") String searchKeywordType, @Param(value = "searchKeyword") String searchKeyword);
	public void addArticle(@Param(value = "title") String title, @Param(value = "body") String body);
	public void deleteArticle(@Param(value = "id") int id);
	public void modifyArticle(@Param(value = "id") int id, @Param(value = "title") String title, @Param(value = "body") String body);
}
