package com.sbs.untact.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.sbs.untact.dto.Member;

@Mapper
public interface MemberDao {
	void join(Map<String, Object> param);
	Member getMember(@Param(value = "id") int id);
	Member getMemberByLoginId(@Param(value = "loginId") String loginId);
	void modifyMember(Map<String, Object> param);
}
