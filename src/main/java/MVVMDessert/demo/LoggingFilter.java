package MVVMDessert.demo;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.logging.log4j.LogManager;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class LoggingFilter implements Filter {

	private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(LoggingFilter.class);

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		StatusCaptureWrapper responseWrapper = new StatusCaptureWrapper((HttpServletResponse) res);

		long start = System.currentTimeMillis();
		String traceId = UUID.randomUUID().toString();
		MDC.put("traceId", traceId);

		try {
			chain.doFilter(req, responseWrapper);
		} catch (Exception e) {
			MDC.put("status", String.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
			logger.error("Unhandled error in request", e);
			throw e;
		} finally {
			long duration = System.currentTimeMillis() - start;
			int status = responseWrapper.getStatus();
			logger.info("Path: {}, Method: {}, Status: {}, Duration: {}ms", request.getRequestURI(),
					request.getMethod(), status, duration);
			MDC.put("status", String.valueOf(status));
			MDC.clear();
		}
	}

	// 這個類別負責記錄 status
	private static class StatusCaptureWrapper extends HttpServletResponseWrapper {
		private int httpStatus = SC_OK;

		public StatusCaptureWrapper(HttpServletResponse response) {
			super(response);
		}

		@Override
		public void setStatus(int sc) {
			super.setStatus(sc);
			this.httpStatus = sc;
		}

		@Override
		public void sendError(int sc) throws IOException {
			super.sendError(sc);
			this.httpStatus = sc;
		}

		@Override
		public void sendError(int sc, String msg) throws IOException {
			super.sendError(sc, msg);
			this.httpStatus = sc;
		}

		@Override
		public void setStatus(int sc, String sm) {
			super.setStatus(sc, sm);
			this.httpStatus = sc;
		}

		public int getStatus() {
			return this.httpStatus;
		}
	}
}
