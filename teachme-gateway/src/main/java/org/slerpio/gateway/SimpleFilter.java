package org.slerpio.gateway;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

//@Component
public class SimpleFilter extends ZuulFilter {

	private static Logger log = LoggerFactory.getLogger(SimpleFilter.class);

	@Override
	public String filterType() {
		return "pre";
	}

	@Override
	public int filterOrder() {
		return 1;
	}

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() {
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();

		log.info(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));
		String requestURI = RequestContext.getCurrentContext().getRequest().getRequestURI();
		log.info(String.format("%s request forward %s", requestURI));
		return null;
	}

}