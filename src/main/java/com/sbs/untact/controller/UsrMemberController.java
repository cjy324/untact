package com.sbs.untact.controller;

import java.util.Map;

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
public class UsrMemberController {

	@Autowired
	private MemberService memberService;

	@RequestMapping("/usr/member/doJoin")
	@ResponseBody
	public ResultData doJoin(@RequestParam Map<String, Object> param) {

		if (param.get("loginId") == null) {
			return new ResultData("F-1", "loginId를 입력해주세요.");
		}
		
		Member existingMember = memberService.getMemberByLoginId((String) param.get("loginId"));

		if (existingMember != null) {
			return new ResultData("F-2", String.format("%s (은)는 이미 사용중인 로그인아이디 입니다.", param.get("loginId")));
		}
		
		if (param.get("loginPw") == null) {
			return new ResultData("F-1", "loginPw를 입력해주세요.");
		}
		if (param.get("name") == null) {
			return new ResultData("F-1", "이름을 입력해주세요.");
		}
		if (param.get("nickname") == null) {
			return new ResultData("F-1", "nickname을 입력해주세요.");
		}
		if (param.get("cellphoneNo") == null) {
			return new ResultData("F-1", "연락처를 입력해주세요.");
		}
		if (param.get("email") == null) {
			return new ResultData("F-1", "email을 입력해주세요.");
		}

		return memberService.join(param);
	}
	
	@RequestMapping("/usr/member/doLogin")
	@ResponseBody
	public ResultData doLogin(String loginId, String loginPw, HttpSession session) {
		//HttpSession session
		//servlet에서와는 달리 스프링에선 session을 바로 요청해서 가져올 수 있다.
		// ex) servlet에서는 requst를 통해 session을 요청하고 다시 HttpSession로 session 값을 가져왔었다.
		
		//기 로그인 회원 여부 확인
		if (session.getAttribute("loginedMemberId") != null) {
			return new ResultData("F-4", "이미 로그인 상태입니다.");
		}

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

		//세션에 로그인 회원 id 등록
		session.setAttribute("loginedMemberId", existingMember.getId());

		return new ResultData("S-1", String.format("%s님 환영합니다.", existingMember.getNickname()));
	}

	@RequestMapping("/usr/member/doLogout")
	@ResponseBody
	public ResultData doLogout(HttpSession session) {
		if (session.getAttribute("loginedMemberId") == null) {
			return new ResultData("S-2", "이미 로그아웃 상태입니다.");
		}

		session.removeAttribute("loginedMemberId");

		return new ResultData("S-1", "로그아웃 되었습니다.");
	}
	
	@RequestMapping("/usr/member/doModify")
	@ResponseBody
	public ResultData doModify(@RequestParam Map<String, Object> param, HttpSession session) {
		if (session.getAttribute("loginedMemberId") == null) {
			return new ResultData("F-1", "로그인 후 이용해주세요.");
		}

		if (param.isEmpty()) {
			return new ResultData("F-2", "수정할 회원정보를 입력해주세요.");
		}

		int loginedMemberId = (int) session.getAttribute("loginedMemberId");
		param.put("id", loginedMemberId);

		return memberService.modifyMember(param);
	}
	
}
