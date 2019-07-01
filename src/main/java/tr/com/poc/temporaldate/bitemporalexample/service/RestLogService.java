package tr.com.poc.temporaldate.bitemporalexample.service;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import tr.com.poc.temporaldate.bitemporalexample.dao.RestLogDao;
import tr.com.poc.temporaldate.bitemporalexample.model.RestServerLog;
import tr.com.poc.temporaldate.common.Constants;
import tr.com.poc.temporaldate.common.util.FileDatabaseUtility;
import tr.com.poc.temporaldate.core.filter.RequestResponseLoggingFilter;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class RestLogService
{

	@Autowired
	private RestLogDao restLogDao;

	/**
	 * Mevcut girdilere gore veritabaninda request ve response iceriklerini loglar
	 *
	 */
	public void prepareRequestResponseFileInMemoryAndSave(RequestResponseLoggingFilter.RestServerLogDto dto)
	{
		RestServerLog restLog = new RestServerLog(null, dto.getUri(), dto.getPathVarieable(), ThreadContext.get(Constants.MDC_TRANSACTION_NO), FileDatabaseUtility.createZipAndByteFile(dto.getUri(), FileDatabaseUtility.FileType.REQUEST, dto.getRequestBody()), FileDatabaseUtility.createZipAndByteFile(dto.getUri(), FileDatabaseUtility.FileType.RESPONSE, dto.getResponseBody()),
				String.valueOf(dto.getResponseCode()));
		restLogDao.saveEntity(restLog);
	}
}
