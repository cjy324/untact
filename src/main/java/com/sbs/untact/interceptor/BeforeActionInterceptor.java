package com.sbs.untact.interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.sbs.untact.dto.Member;
import com.sbs.untact.service.MemberService;
import com.sbs.untact.util.Util;

@Component("beforeActionInterceptor") // 컴포넌트 이름 설정
public class BeforeActionInterceptor implements HandlerInterceptor {
	@Autowired
	private MemberService memberService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		/* requestUrl 관련 로직 시작 */
		// 기타 유용한 정보를 request에 담는다.
		Map<String, Object> param = Util.getParamMap(request);
		String paramJson = Util.toJsonStr(param);

		String requestUrl = request.getRequestURI();
		String queryString = request.getQueryString();

		if (queryString != null && queryString.length() > 0) {
			requestUrl += "?" + queryString;
		}

		String encodedRequestUrl = Util.getUrlEncoded(requestUrl);

		request.setAttribute("requestUrl", requestUrl);
		request.setAttribute("encodedRequestUrl", encodedRequestUrl);

		request.setAttribute("afterLoginUrl", requestUrl);
		request.setAttribute("encodedAfterLoginUrl", encodedRequestUrl);

		request.setAttribute("paramMap", param);
		request.setAttribute("paramJson", paramJson);
		/* requestUrl 관련 로직 끝 */

		// HttpSession session = request.getSession();
		int loginedMemberId = 0;
		Member loginedMember = null;

		String authKey = request.getParameter("authKey");

		// 파라미터로 authKey가 들어왔으면
		if (authKey != null && authKey.length() > 0) {
			// authKey정보를 통해 해당 회원 검색하기
			loginedMember = memberService.getMemberByAuthKey(authKey);

			// authKey가 일치하는 회원이 없다면
			if (loginedMember == null) {
				// 인증되지 않은 회원
				request.setAttribute("authKeyStatus", "invalid");
			} else {
				// authKey가 일치한다면 인증된 회원
				request.setAttribute("authKeyStatus", "valid");
				// 인증된 회원의 id를 저장(=세션 만료와 상관없이 저장되는 정보)
				loginedMemberId = loginedMember.getId();
			}
		}
		// 파라미터로 authKey가 들어오지 않았다면
		else {
			// session 가져오기
			HttpSession session = request.getSession();
			request.setAttribute("authKeyStatus", "none");

			// session에 로그인 정보 저장(=세션이 만료되면 초기화되는 정보)
			if (session.getAttribute("loginedMemberId") != null) {
				loginedMemberId = (int) session.getAttribute("loginedMemberId");
				loginedMember = memberService.getMember(loginedMemberId);
			}
		}

		// 로그인 여부에 관련된 정보를 request에 담는다.
		boolean isLogined = false;
		boolean isAdmin = false;
		// int loginedMemberId = 0;
		// Member loginedMember = null;

		if (loginedMember != null) {
			// loginedMemberId = (int) session.getAttribute("loginedMemberId");
			isLogined = true;
			// loginedMember = memberService.getMember(loginedMemberId);
			isAdmin = memberService.isAdmin(loginedMemberId);
		}

		request.setAttribute("loginedMemberId", loginedMemberId);
		request.setAttribute("isLogined", isLogined);
		request.setAttribute("isAdmin", isAdmin);
		request.setAttribute("loginedMember", loginedMember);

		return HandlerInterceptor.super.preHandle(request, response, handler);
	}
}