package tr.com.poc.temporaldate.core.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.bitemporalexample.service.RestLogService;
import tr.com.poc.temporaldate.core.configuration.ApplicationContextProvider;
import tr.com.poc.temporaldate.core.util.logging.RestRequestWrapper;
import tr.com.poc.temporaldate.core.util.logging.RestResponseWrapper;
import tr.com.poc.temporaldate.core.util.logging.RestServerLogInit;

/**
 * For database and text logging of requests and responses if necessary
 *
 * @author umutaskin
 */

/**
 * TODO: Tüm sistemin loglama açıp kapatma olayını parametre de tutmak gerekir mi ?
 */
@Log4j2
public class RequestResponseLoggingFilter implements Filter
{

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
	{

		RestLogService restLogService = (RestLogService) ApplicationContextProvider.getBeanFromApplicationContext(RestLogService.class);
		String uri = ((HttpServletRequest) request).getRequestURI();
		RestServerLogDto dto = new RestServerLogDto();
		dto.setUri(uri);
		RestRequestWrapper wrappedRequest = null;
		RestResponseWrapper wrappedResponse = null;

		try
		{
			Boolean requestTxtLoggable = RestServerLogInit.isTextRequestLoggable(uri);
			Boolean responseTxtLoggable = RestServerLogInit.isTextResponseLoggable(uri);
			Boolean requestDBLoggable = RestServerLogInit.isDBRequestLoggable(uri);
			Boolean responseDBLoggable = RestServerLogInit.isDBResponseLoggable(uri);
			String baseUrl = RestServerLogInit.pathVariableBaseUrl(uri);
			if (StringUtils.isNotBlank(baseUrl))
			{
				dto.setUri(baseUrl);
				dto.setPathVarieable(uri.replaceAll(baseUrl, ""));
			}

			boolean shouldWrapRequest = false;
			boolean shouldWrapResponse = false;
			if (requestTxtLoggable || requestDBLoggable)
			{
				wrappedRequest = new RestRequestWrapper((HttpServletRequest) request);
				dto.setRequestBody(wrappedRequest.getBody());
				shouldWrapRequest = true;
			}

			if (responseTxtLoggable || responseDBLoggable)
			{
				wrappedResponse = new RestResponseWrapper((HttpServletResponse) response);
				shouldWrapResponse = true;
			}
			/*
			 * TODO:Json olarak gelen request bilgisi sıkıştırılmamış olduğundan dolayı loga yazıdırılırken birden
			 *      fazla satıra yazabilmektedir.Bu da graylog gibi log toplayan sistemler için yanlış olabilmektedir.
			 */
			if (requestTxtLoggable)
			{
				log.info("REQUEST: {}", dto.getRequestBody());
			}

			if (shouldWrapRequest)
			{
				if (shouldWrapResponse)
					chain.doFilter(wrappedRequest, wrappedResponse);
				else
					chain.doFilter(wrappedRequest, response);
			}
			else
			{
				if (shouldWrapResponse)
					chain.doFilter(request, wrappedResponse);
				else
					chain.doFilter(request, response);
			}

			if (wrappedResponse != null)
			{
				dto.setResponseBody(wrappedResponse.getContent());
				dto.setResponseCode(wrappedResponse.getStatus());
			}
			else
			{
				dto.setResponseCode(((HttpServletResponse) response).getStatus());
			}

			/**
			 * TODO:Json olarak gelen request bilgisi sıkıştırılmamış olduğundan dolayı loga yazıdırılırken birden
			 *
			 * TODO:fazla satıra yazabilmektedir.Bu da graylog gibi log toplayan sistemler için yanlış olabilmektedir.
			 */
			if (responseTxtLoggable)
			{
				log.info("RESPONSE: {}", dto.getRequestBody());
			}

			if (responseDBLoggable || requestDBLoggable)
			{
				restLogService.prepareRequestResponseFileInMemoryAndSave(dto);
			}

		}
		catch (Exception ex)
		{
			// TODO:Buradaki metini ingilizce ile değiştir.
			log.error("Gelen istek loglama filtresinden gecerken hata alindi. İstek loglanmamış olabilir. İstek URL: {}. Hata Detayi: {}. ", uri, ex);
		}

	}

	@Getter
	@Setter
	public class RestServerLogDto
	{

		private String uri;

		private String pathVarieable = "";

		private String requestBody = "";

		private String responseBody = "";

		private Integer responseCode;

	}
}
