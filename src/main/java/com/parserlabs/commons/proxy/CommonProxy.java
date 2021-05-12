package com.parserlabs.commons.proxy;

import java.util.Objects;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@AllArgsConstructor
public class CommonProxy<T, R> {

	private RestTemplate restTemplate;

	public R delete(String url, Class<R> responseType) {
		return invoke(url, null, HttpMethod.DELETE, null, responseType);
	}

	public R delete(String url, HttpHeaders headers, Class<R> responseType) {
		return invoke(url, headers, HttpMethod.DELETE, null, responseType);
	}

	public R get(String url, Class<R> responseType) {
		return invoke(url, null, HttpMethod.GET, null, responseType);
	}

	public R get(String url, HttpHeaders headers, Class<R> responseType) {
		return invoke(url, headers, HttpMethod.GET, null, responseType);
	}

	public R post(String url, HttpHeaders headers, T request, Class<R> responseType) {
		return invoke(url, headers, HttpMethod.POST, request, responseType);
	}

	public R post(String url, T request, Class<R> responseType) {
		return invoke(url, null, HttpMethod.POST, request, responseType);
	}

	public R put(String url, HttpHeaders headers, T request, Class<R> responseType) {
		return invoke(url, headers, HttpMethod.PUT, request, responseType);
	}

	public R put(String url, T request, Class<R> responseType) {
		return invoke(url, null, HttpMethod.PUT, request, responseType);
	}

	public HttpHeaders basicAuthHeader(String username, String password) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(username, password);
		return headers;
	}

	public HttpHeaders authHeader(String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token);
		return headers;
	}

	private R invoke(String url, HttpHeaders headers, HttpMethod method, T request, Class<R> responseType) {
		R response = null;

		headers = Objects.isNull(headers) ? new HttpHeaders() : headers;
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<T> requestEntity = Objects.nonNull(request) ? new HttpEntity<>(request, headers)
				: new HttpEntity<>(headers);

		try {
			ResponseEntity<R> result = restTemplate.exchange(url, method, requestEntity, responseType);
			response = result.getBody();

		} catch (RestClientException exp) {
			log.error("Exception occured whie calling the url {}", url, exp);
			throw exp;
		} catch (Exception exp) {
			log.error("Unknown Exception occured whie calling the url {}", url, exp);
		}
		return response;
	}
}
