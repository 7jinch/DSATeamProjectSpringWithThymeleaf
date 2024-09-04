package com.example.tabitabi.filter;

import java.io.IOException;

import org.springframework.util.PatternMatchUtils;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginCheckFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		try {
			log.info("LoginCheckFilter 실행");
			String requestURI = httpRequest.getRequestURI();
//			String id = httpRequest.getParameter("id");
//			log.info("LoginCheckFilter id: {}", id);
			
			if(isLoginCheckPath(requestURI)) {
				HttpSession session = httpRequest.getSession(false); //세션없다고 만들지 않도록 false
				
				if(session == null || session.getAttribute("loginMember") == null && session.getAttribute("loginSeller") == null ) {
					//로그인 하지 않은 상태
					log.info("로그인 하지 않은 사용자의 요청");
					httpResponse.sendRedirect("/"); //서블릿에서 리다이렉트 하는 방식
					return;
				}
			}
			chain.doFilter(request, response);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			log.info("LoginCheckFilter 종료");
		}
		
	}
	
	private boolean isLoginCheckPath(String requestURI) {
		//로그인 체크X. 로그인을 하지 않아도 들어갈 수 있는 경로들
		String[] whiteList = {"/", "/*/login", "/*/join"};
		
		return !PatternMatchUtils.simpleMatch(whiteList, requestURI);
	}

}
