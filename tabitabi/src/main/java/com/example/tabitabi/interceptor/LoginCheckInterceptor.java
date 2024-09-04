package com.example.tabitabi.interceptor;

import java.util.Enumeration;

import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {
	
//	@Override
//	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
//			throws Exception {
//		String requestURI = request.getRequestURI();
//		
//		
//		HttpSession session = request.getSession(false);
//		
//		if(session == null || session.getAttribute("loginMember") == null || session.getAttribute("loginSeller") == null) {
//			log.info("로그인 하지 않은 사용자 요청");
//			
//			Enumeration<String> parameterNames = request.getParameterNames();
//			
//			StringBuffer stringBuffer = new StringBuffer();
//			
//			while(parameterNames.hasMoreElements()) {
//				String parameterName = parameterNames.nextElement();
//				stringBuffer.append(parameterName + "=" + request.getParameter(parameterName) + "%26");
//			}
//			
//			//board/read?id=2&name=홍길동
//			//서블릿 방식 리다이렉트
//			response.sendRedirect("/member/login?redirectURL=" + requestURI + "?" + stringBuffer.toString());
//			return false;
//		}
//		
//		return true;
//	}
	
}
