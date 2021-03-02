package com.sbs.untact.dto;

import lombok.Data;

@Data
public class Article {
	
	private int id;
	private String regDate;
	private String updateDate;
	private int boardId;
	private int memberId;
	private String title;
	private String body;
	
	private String extra__writer;
	private String extra__boardName;
	private String extra__thumbImg;
	
	public Article(int id, String regDate, String updateDate, String title, String body) {
	
		this.id = id;
		this.regDate = regDate;
		this.updateDate = updateDate;
		this.title = title;
		this.body = body;
		
	}

}
