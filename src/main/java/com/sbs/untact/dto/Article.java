package com.sbs.untact.dto;

import lombok.Data;

@Data
public class Article {
	
	private int id;
	private String regDate;
	private String updateDate;
	private int memberId;
	private String title;
	private String body;
	
	
	public Article(int id, String regDate, String updateDate, String title, String body) {
	
		this.id = id;
		this.regDate = regDate;
		this.updateDate = updateDate;
		this.title = title;
		this.body = body;
		
	}

}
