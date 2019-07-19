package tr.com.poc.temporaldate.core.util.logging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.io.output.TeeOutputStream;

import lombok.extern.log4j.Log4j2;

/**
 * Wraps the {@link HttpServletResponse} in another container using the HttpServletResponseWrapper's capability to clone the response, returns back the response body as input stream
 * 
 * @author umutaskin
 */
@Log4j2
public class RestResponseWrapper extends HttpServletResponseWrapper
{

	TeeServletOutputStream teeStream;

	PrintWriter teeWriter;

	ByteArrayOutputStream bos;

	public RestResponseWrapper(HttpServletResponse response)
	{
		super(response);
	}

	public String getContent()
	{
		return bos.toString();
	}

	@Override
	public PrintWriter getWriter() throws IOException
	{

		if (this.teeWriter == null)
		{
			this.teeWriter = new PrintWriter(new OutputStreamWriter(getOutputStream()));
		}
		return this.teeWriter;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException
	{

		if (teeStream == null)
		{
			bos = new ByteArrayOutputStream();
			teeStream = new TeeServletOutputStream(getResponse().getOutputStream(), bos);
		}
		return teeStream;
	}

	@Override
	public void flushBuffer() throws IOException
	{
		if (teeStream != null)
		{
			teeStream.flush();
		}
		if (this.teeWriter != null)
		{
			this.teeWriter.flush();
		}
	}

	// Mevcut streami split ederek baska bir stream olusturur. Loglarken olusturdugumuz ikinciyi kullaniriz
	public class TeeServletOutputStream extends ServletOutputStream
	{
		private final TeeOutputStream targetStream;

		public TeeServletOutputStream(OutputStream one, OutputStream two)
		{
			log.debug("Initializing data input output stream replicator...");
			targetStream = new TeeOutputStream(one, two);
		}

		@Override
		public void write(int arg0) throws IOException
		{
			this.targetStream.write(arg0);
		}

		@Override
		public void flush() throws IOException
		{
			super.flush();
			this.targetStream.flush();
		}

		@Override
		public void close() throws IOException
		{
			super.close();
			this.targetStream.close();
		}

		@Override
		public boolean isReady()
		{
			return true;
		}

		@Override
		public void setWriteListener(WriteListener arg0)
		{
			throw new UnsupportedOperationException();
		}
	}
}