package com.sbs.untact.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;


@Data
public class Member {
	private int id;
	private String regDate;
	private String updateDate;
	private String loginId;
	@JsonIgnore
	//@JsonIgnore: json으로 보이지 않게 할 수 있다.
	private String loginPw;
	@JsonIgnore
	private String authKey;
	private String name;
	private String nickname;
	private String cellphoneNo;
	private String email;
}