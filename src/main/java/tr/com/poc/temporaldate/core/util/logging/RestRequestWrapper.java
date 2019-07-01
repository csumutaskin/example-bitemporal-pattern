package tr.com.poc.temporaldate.core.util.logging;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import lombok.extern.log4j.Log4j2;

/**
 * Wraps the {@link HttpServletRequest} in another container using the HttpServletRequestWrapper's capability to clone the request, returns back the request body as input stream
 *
 * @author umutaskin
 */
@Log4j2
public class RestRequestWrapper extends HttpServletRequestWrapper
{
	private final String body;

	public RestRequestWrapper(HttpServletRequest request) throws IOException
	{
		super(request);
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;
		try
		{
			InputStream inputStream = request.getInputStream();
			if (inputStream != null)
			{
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				char[] charBuffer = new char[1024];
				int bytesRead = -1;
				while ((bytesRead = bufferedReader.read(charBuffer)) > 0)
				{
					stringBuilder.append(charBuffer, 0, bytesRead);
				}
			}
			else
			{
				stringBuilder.append("");
			}
		}
		catch (Exception ex)
		{
			log.info("Streamden İsteğin bodysini okurken beklenmedik hata alındı. URI: {} ye yapılan istek loglanamayacaktır. Hatanın tekrar etmemesi için benzer istekle sistemi inceleyin. Detaylar hata logundadır.", request.getRequestURI());
			log.error("Streamden İsteğin bodysini okurken beklenmedik hata alındı. URI: {} ye yapılan istek loglanamayacaktır. Hatanın tekrar etmemesi için benzer istekle sistemi inceleyin. Hata Detayı: {}", request.getRequestURI(), ex);
		}
		finally
		{
			if (bufferedReader != null)
			{
				try
				{
					bufferedReader.close();
				}
				catch (Exception ex)
				{
					log.info("Streamden İsteğin bodysini okurken beklenmedik hata alındı. URI: {} ye yapılan istek loglanamayacaktır. Hatanın tekrar etmemesi için benzer istekle sistemi inceleyin. Detaylar hata logundadır.", request.getRequestURI());
					log.error("Streamden İsteğin bodysini okurken beklenmedik hata alındı. URI: {} ye yapılan istek loglanamayacaktır. Hatanın tekrar etmemesi için benzer istekle sistemi inceleyin. Hata Detayı: {}", request.getRequestURI(), ex);
				}
			}
		}
		body = stringBuilder.toString();
	}

	@Override
	public ServletInputStream getInputStream() throws IOException
	{
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
		return new ReadableServletInputStream(byteArrayInputStream);
	}

	@Override
	public BufferedReader getReader() throws IOException
	{
		return new BufferedReader(new InputStreamReader(this.getInputStream()));
	}

	public String getBody()
	{
		return this.body;
	}

	/* Http Servlet Request streami okumaya yarar */
	private static class ReadableServletInputStream extends ServletInputStream
	{
		private ByteArrayInputStream buffer;

		public ReadableServletInputStream(ByteArrayInputStream byteArrayInputStream)
		{
			this.buffer = byteArrayInputStream;
		}

		@Override
		public int read() throws IOException
		{
			return buffer.read();
		}

		@Override
		public boolean isFinished()
		{
			return buffer.available() == 0;
		}

		@Override
		public boolean isReady()
		{
			return true;
		}

		@Override
		public void setReadListener(ReadListener listener)
		{
			throw new UnsupportedOperationException();
		}
	}
}