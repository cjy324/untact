package com.sbs.untact.dto;

import java.util.HashMap;
import java.util.Map;

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
	
	private Map<String, Object> extra;

	public Map<String, Object> getExtraNotNull() {
		//만약에 추가 정보가 없으면 새로운 Map 객체 생성 후 리턴
		if ( extra == null ) {
			extra = new HashMap<String, Object>();
		}

		return extra;
	}
	
	public Article(int id, String regDate, String updateDate, String title, String body) {
	
		this.id = id;
		this.regDate = regDate;
		this.updateDate = updateDate;
		this.title = title;
		this.body = body;
		
	}

}
