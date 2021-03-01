package com.sbs.untact.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.sbs.untact.dto.Article;
import com.sbs.untact.dto.Board;
import com.sbs.untact.dto.ResultData;

/* Mybatis 적용으로 삭제 */
//Mybatis틑 class가 아닌 interface를 인식함

//@Component
//public class ArticleDao {

@Mapper
public interface ArticleDao {
	/* Mybatis 적용으로 기존 내용 삭제 */
	//Mybatis에서 자동으로 만들어 줌
	Article getArticle(@Param("id") int id);
	List<Article> getArticles(@Param("searchKeywordType") String searchKeywordType, @Param("searchKeyword") String searchKeyword);
	void addArticle(Map<String, Object> param);
	void deleteArticle(@Param("id") int id);
	void modifyArticle(@Param("id") int id, @Param("title") String title, @Param("body") String body);
	Article getForPrintArticle(@Param("id") int id);
	List<Article> getForPrintArticles(@Param("boardId") int boardId, @Param("searchKeywordType") String searchKeywordType, @Param("searchKeyword") String searchKeyword, @Param("limitStart") int limitStart,
			@Param("limitTake") int limitTake);
	Board getBoard(@Param("id") int id);
	
	ResultData addReply(Map<String, Object> param);
}
