package com.bbva.resources.filters;

import com.google.cloud.ServiceOptions;
import com.google.cloud.logging.TraceLoggingEnhancer;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class TraceLoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(TraceLoggingFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            String traceHeader = ((HttpServletRequest) request).getHeader("x-cloud-trace-context");
            String trace = null;
            if (traceHeader != null) {
                trace = traceHeader.split("/")[0];
            }
            TraceLoggingEnhancer.setCurrentTraceId(String.format("projects/%s/traces/%s",
                    ServiceOptions.getDefaultProjectId(), trace));
        } catch (Exception ex) {
            logger.warn(ExceptionUtils.getStackTrace(ex));
        }
        chain.doFilter( request, response );
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
