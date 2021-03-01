package com.sbs.untact.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sbs.untact.dto.Member;
import com.sbs.untact.dto.ResultData;
import com.sbs.untact.service.MemberService;

@Controller
public class AdmMemberController {

	@Autowired
	private MemberService memberService;
	
	@RequestMapping("/adm/member/doLogin")
	@ResponseBody
	public ResultData doLogin(String loginId, String loginPw, HttpSession session) {
		//HttpSession session
		//servlet에서와는 달리 스프링에선 session을 바로 요청해서 가져올 수 있다.
		// ex) servlet에서는 requst를 통해 session을 요청하고 다시 HttpSession로 session 값을 가져왔었다.
		
		if (loginId == null) {
			return new ResultData("F-1", "loginId를 입력해주세요.");
		}

		Member existingMember = memberService.getMemberByLoginId(loginId);

		if (existingMember == null) {
			return new ResultData("F-2", "존재하지 않는 로그인아이디 입니다.", "loginId", loginId);
		}

		if (loginPw == null) {
			return new ResultData("F-1", "loginPw를 입력해주세요.");
		}

		if (existingMember.getLoginPw().equals(loginPw) == false) {
			return new ResultData("F-3", "비밀번호가 일치하지 않습니다.");
		}
		
		if ( memberService.isAdmin(existingMember) == false ) {
			return new ResultData("F-4", "관리자만 접근할 수 있는 페이지 입니다.");
		}

		//세션에 로그인 회원 id 등록
		session.setAttribute("loginedMemberId", existingMember.getId());

		return new ResultData("S-1", String.format("%s님 환영합니다.", existingMember.getNickname()));
	}
	
	@RequestMapping("/adm/member/doModify")
	@ResponseBody
	public ResultData doModify(@RequestParam Map<String, Object> param, HttpServletRequest req) {

		if (param.isEmpty()) {
			return new ResultData("F-2", "수정할 회원정보를 입력해주세요.");
		}

		int loginedMemberId = (int) req.getAttribute("loginedMemberId");
		param.put("id", loginedMemberId);

		return memberService.modifyMember(param);
	}
	
}
