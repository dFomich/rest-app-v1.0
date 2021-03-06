package com.epam.cleaningProject.filter;

import java.io.IOException;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

/**
 * The {@code EncodingFilter} class is an implementation of {@code Filter}
 * interface. Sets character encoding UTF-8 to each request and response
 * objects.
 */
@WebFilter(urlPatterns = { "/*" }, initParams = {
		@WebInitParam(name = "encoding", value = "UTF-8", description = "Encoding Param") }, dispatcherTypes = {
				DispatcherType.FORWARD, DispatcherType.REQUEST })
public class EncodingFilter implements Filter {
	private final static String ENCODING = "encoding";
	private String code;

	public void init(FilterConfig fConfig) throws ServletException {
		code = fConfig.getInitParameter(ENCODING);
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String codeRequest = request.getCharacterEncoding();
		if (code != null && !code.equalsIgnoreCase(codeRequest)) {
			request.setCharacterEncoding(code);
			response.setCharacterEncoding(code);
		}
		chain.doFilter(request, response);
	}

	public void destroy() {
		code = null;
	}
}