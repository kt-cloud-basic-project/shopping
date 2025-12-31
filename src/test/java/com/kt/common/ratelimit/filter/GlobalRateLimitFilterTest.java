package com.kt.common.ratelimit.filter;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
public class GlobalRateLimitFilterTest {

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private FilterChain filterChain;

	private GlobalRateLimitFilter filter;
	private final String TEST_IP = "192.168.0.100";

	@BeforeEach
	void setUp() throws Exception {
		filter = new GlobalRateLimitFilter();
	}

	@Test
	void 정상_요청은_통과() throws Exception {
		// given
		when(request.getRemoteAddr()).thenReturn(TEST_IP);

		// when
		filter.doFilter(request, response, filterChain);

		// then
		verify(filterChain, times(1)).doFilter(request, response);
		verify(response, never()).setStatus(anyInt());
	}

	@Test
	void 초당_100회_이하_요청은_모두_통과() throws Exception {
		// given
		when(request.getRemoteAddr()).thenReturn(TEST_IP);

		// when - 100번 요청
		for (int i = 0; i < 100; i++) {
			filter.doFilter(request, response, filterChain);
		}

		// then
		verify(filterChain, times(100)).doFilter(request, response);
		verify(response, never()).setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
	}

	@Test
	void 초당_100회_초과_요청은_차단() throws Exception {
		// given
		when(request.getRemoteAddr()).thenReturn(TEST_IP);

		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		when(response.getWriter()).thenReturn(writer);

		// when - 100번은 통과
		for (int i = 0; i < 100; i++) {
			filter.doFilter(request, response, filterChain);
		}

		// when - 101번째 요청
		filter.doFilter(request, response, filterChain);

		// then
		verify(filterChain, times(100)).doFilter(request, response);
		verify(response, times(1)).setStatus(HttpStatus.TOO_MANY_REQUESTS.value());

		String responseBody = stringWriter.toString();
		assertThat(responseBody).contains("429 TOO_MANY_REQUESTS");
		assertThat(responseBody).contains("192.168.0.100");
	}

	@Test
	void 서로_다른_IP는_독립적으로_카운트() throws Exception {
		// given
		String test_ip_1 = "192.168.0.100";
		String test_ip_2 = "192.168.0.101";

		// when
		when(request.getRemoteAddr()).thenReturn(test_ip_1);
		for (int i = 0; i < 100; i++) {
			filter.doFilter(request, response, filterChain);
		}

		// when
		when(request.getRemoteAddr()).thenReturn(test_ip_2);
		filter.doFilter(request, response, filterChain);

		// then
		verify(filterChain, times(101)).doFilter(request, response);
		verify(response, never()).setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
	}

	@Test
	void 카운터_리셋() throws Exception {
		// given
		when(request.getRemoteAddr()).thenReturn(TEST_IP);

		// when
		for (int i = 0; i < 100; i++) {
			filter.doFilter(request, response, filterChain);
		}

		Thread.sleep(1100);

		// when - 리셋되어 통과해야 함
		filter.doFilter(request, response, filterChain);

		// then
		verify(filterChain, times(101)).doFilter(request, response);
		verify(response, never()).setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
	}
}
