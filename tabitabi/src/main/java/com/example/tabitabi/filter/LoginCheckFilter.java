package com.example.tabitabi.filter;

import java.io.IOException;
import java.io.PrintWriter;

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
			
			if(isLoginCheckPath(requestURI)) {
				HttpSession session = httpRequest.getSession(false); //세션없다고 만들지 않도록 false
				
				if(session == null || session.getAttribute("loginMember") == null && session.getAttribute("loginSeller") == null ) {
					//로그인 하지 않은 상태
					log.info("로그인 하지 않은 사용자의 요청");
					httpResponse.setContentType("text/html; charset=UTF-8");
				    PrintWriter out = httpResponse.getWriter();
				    out.println("<script>alert('로그인이 필요한 페이지입니다. 메인화면으로 이동합니다.'); location.href='/';</script>");
					return;
				}
			}
			chain.doFilter(request, response);
		} catch (Exception e) {
		} finally {
			log.info("LoginCheckFilter 종료");
		}
		
	}
	
	private boolean isLoginCheckPath(String requestURI) {
        // 로그인 체크가 필요 없는 경로들 (화이트리스트)
        String[] whiteList = {
                "/", "/*/join", "/*/login", "/*/logout", "/*/forgotPw", "/*/setPw", 
                "/kudamon/mall", "/products/categorylist", "/products/read",
                "/products/files", "/joinchoice", "/crawling", "/api/statistics",
                "/*.css", "/*.js", "/*.ico", "/error",
                "/*.png", "/*.jpg", "/*.jpeg", "/*.gif", "/*.svg", "/*.mp4", // 정적 파일 형식
                "/src/**", "/uploads/**" // 추가: 정적 리소스 경로들
        };
 		
		return !PatternMatchUtils.simpleMatch(whiteList, requestURI);
	}

}
