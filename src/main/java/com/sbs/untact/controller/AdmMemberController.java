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
import com.sbs.untact.util.Util;

@Controller
public class AdmMemberController {

	@Autowired
	private MemberService memberService;


	@RequestMapping("/adm/member/join")
	public String showJoin() {
		return "adm/member/join";
	}

	@RequestMapping("/adm/member/doJoin")
	@ResponseBody
	public String doJoin(@RequestParam Map<String, Object> param) {
		if (param.get("loginId") == null) {
			return Util.msgAndBack("loginId를 입력해주세요.");
		}

		Member existingMember = memberService.getMemberByLoginId((String) param.get("loginId"));

		if (existingMember != null) {
			return Util.msgAndBack("이미 사용중인 로그인아이디 입니다.");
		}

		if (param.get("loginPw") == null) {
			return Util.msgAndBack("loginPw를 입력해주세요.");
		}

		if (param.get("name") == null) {
			return Util.msgAndBack("name을 입력해주세요.");
		}

		if (param.get("nickname") == null) {
			return Util.msgAndBack("nickname을 입력해주세요.");
		}

		if (param.get("email") == null) {
			return Util.msgAndBack("email을 입력해주세요.");
		}

		if (param.get("cellphoneNo") == null) {
			return Util.msgAndBack("cellphoneNo를 입력해주세요.");
		}

		memberService.join(param);

		String msg = String.format("%s님 환영합니다.", param.get("nickname"));

		String redirectUrl = Util.ifEmpty((String) param.get("redirectUrl"), "../member/login");

		return Util.msgAndReplace(msg, redirectUrl);
	}

	@RequestMapping("/adm/member/login")
	// @ResponseBody: @ResponseBody를 안하면 /WEB-INF/jsp/adm/member/login.jsp를 찾는다.
	public String showLogin() {
		return "adm/member/login";
	}

	@RequestMapping("/adm/member/doLogin")
	@ResponseBody
	public String doLogin(String loginId, String loginPw, String redirectUrl, HttpSession session) {
		// HttpSession session
		// servlet에서와는 달리 스프링에선 session을 바로 요청해서 가져올 수 있다.
		// ex) servlet에서는 requst를 통해 session을 요청하고 다시 HttpSession로 session 값을 가져왔었다.

		if (loginId == null) {
			// return new ResultData("F-1", "loginId를 입력해주세요.");
			return Util.msgAndBack("loginId를 입력해주세요.");
		}

		Member existingMember = memberService.getMemberByLoginId(loginId);

		if (existingMember == null) {
			// return new ResultData("F-2", "존재하지 않는 로그인아이디 입니다.", "loginId", loginId);
			return Util.msgAndBack("존재하지 않는 로그인아이디 입니다.");
		}

		if (loginPw == null) {
			// return new ResultData("F-1", "loginPw를 입력해주세요.");
			return Util.msgAndBack("loginPw를 입력해주세요.");
		}

		if (existingMember.getLoginPw().equals(loginPw) == false) {
			// return new ResultData("F-3", "비밀번호가 일치하지 않습니다.");\
			return Util.msgAndBack("비밀번호가 일치하지 않습니다.");
		}

		if (memberService.isAdmin(existingMember) == false) {
			// return new ResultData("F-4", "관리자만 접근할 수 있는 페이지 입니다.");
			return Util.msgAndBack("관리자만 접근할 수 있는 페이지 입니다.");
		}

		// 세션에 로그인 회원 id 등록
		session.setAttribute("loginedMemberId", existingMember.getId());

		String msg = String.format("%s님 환영합니다.", existingMember.getNickname());

		redirectUrl = Util.ifEmpty(redirectUrl, "../home/main");

		// return new ResultData("S-1", String.format("%s님 환영합니다.",
		// existingMember.getNickname()));
		return Util.msgAndReplace(msg, redirectUrl);
	}

	@RequestMapping("/adm/member/doLogout")
	@ResponseBody
	public String doLogout(HttpSession session) {
		session.removeAttribute("loginedMemberId");

		return Util.msgAndReplace("로그아웃 되었습니다.", "../member/login");
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
