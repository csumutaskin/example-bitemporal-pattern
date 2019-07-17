package tr.com.poc.temporaldate.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.log4j.Log4j2;

/**
 * Veritabanina dosya yazma/okuma isleri icin kullanilacak utility dir.
 * 
 * @author umutaskin
 *
 */
@Log4j2
public class FileDatabaseUtility
{

	public enum FileType
	{
		REQUEST("Request"), RESPONSE("Response");

		private String type;

		private FileType(String type)
		{
			this.type = type;
		}

		public String getType()
		{
			return this.type;
		}
	}

	private FileDatabaseUtility()
	{
	}

	public static byte[] createZipAndByteFile(String url, FileType type, String content)
	{
		String fileName = createFileNameOfServerSideServiceCalls(url, type);
		return createAndZipFileInMemory(fileName, content).toByteArray();
	}

	/*
	 * Verilen dosya adiyla contentte yazan icerigi memoryde dosya olacak sekilde yaratir, olusan dosyayi zipler ve stream olarak doner
	 * 
	 * @param fileName dosya adi
	 * 
	 * @param content icerik
	 * 
	 * @return olusan zip stream
	 */
	private static ByteArrayOutputStream createAndZipFileInMemory(String fileName, String content)
	{
		if (log.isDebugEnabled())
		{
			log.debug("{} adli rest log dosyasi, {} icerigiyle birlikte memoryde olusturulup ziplenecektir, sonra da veritabanina yazmak icin kullanilacaktir.", fileName, content);
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (StringUtils.isBlank(content))
		{
			return baos;
		}
		try (ZipOutputStream zos = new ZipOutputStream(baos))
		{
			ZipEntry entry = new ZipEntry(fileName);
			zos.putNextEntry(entry);
			zos.write(content.getBytes());
			zos.closeEntry();
		}
		catch (IOException e)
		{
			log.info("{} adli rest istek/cevap log dosyasini olusturup ziplemeye calisirken hata alindi. Detay icin hata loglarina bakiniz...", fileName);
			log.error("{} adli rest istek/cevap log dosyasini olusturup ziplemeye calisirken hata alindi. Hata Detay: {}", fileName, e);
		}
		return baos;
	}

	/**
	 * Request URL den mantikli bir dosyalama adi yapar. "Request.v1.organization.save" ya da "Response.v1.organization.save" seklinde, Servis sahibi biz olan servislerle kullanılmalıdır.
	 * 
	 * @param url
	 *            mevcut dosya ismine donusturulecek olan url
	 * @param type
	 *            mevcut loglanacak dosya tipi, request log mu, response log mu
	 * @return {@link String} olusan dosya adi
	 */
	private static String createFileNameOfServerSideServiceCalls(String url, FileType type)
	{
		StringBuilder toReturn = new StringBuilder(type.getType());
		if (StringUtils.isNotBlank(url))
		{
			toReturn.append(url.replaceAll("/", ".").replaceAll("rest.", "").replaceAll("https", ""));
		}
		return toReturn.toString();
	}

}
