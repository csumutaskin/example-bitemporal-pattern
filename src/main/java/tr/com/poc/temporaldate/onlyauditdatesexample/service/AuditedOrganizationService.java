package tr.com.poc.temporaldate.onlyauditdatesexample.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.core.exception.BusinessValidationException;
import tr.com.poc.temporaldate.core.service.BaseService;
import tr.com.poc.temporaldate.onlyauditdatesexample.dao.AuditedOrganizationDao;
import tr.com.poc.temporaldate.onlyauditdatesexample.dto.converter.AuditedOrganizationDTOConverter;
import tr.com.poc.temporaldate.onlyauditdatesexample.model.AuditedOrganization;
import tr.com.poc.temporaldate.temporalexample.dto.TemporalOrganizationDTO;

/**
 * Transactional service operations for {@link AuditedOrganization} entity
 * @author umutaskin
 */
@Service
@Transactional
@Log4j2
public class AuditedOrganizationService  implements BaseService
{
	@Autowired
	private AuditedOrganizationDao versionedOrganizationDao;
	
	public Boolean updateVersionedOrganization(Serializable id, TemporalOrganizationDTO toUpdate)
	{
		AuditedOrganization updateEntityByDTO = versionedOrganizationDao.updateEntityByDTO(id, toUpdate, AuditedOrganizationDTOConverter.class);
		if(updateEntityByDTO == null)
		{
			log.info("No versioned organization with id: {} is detected on db. Thus no update operation will be performed this time using DTO: {}." , id, toUpdate);
			return false;
		}
		return true;
	}
	
	public Boolean deleteVersionedOrganization(Serializable id)
	{
		boolean entityDeleted = versionedOrganizationDao.deleteEntity(id);
		return Boolean.valueOf(entityDeleted);
	}
	
	public BigDecimal saveVersionedOrganization(TemporalOrganizationDTO toSave)
	{
		AuditedOrganization versionedOrganizationSaved = versionedOrganizationDao.saveDTOReturnEntity(toSave, AuditedOrganizationDTOConverter.class);
		return versionedOrganizationSaved.getId();
	}
	
	public List<TemporalOrganizationDTO> getAllOrganizations()
	{
		//TODO: Application Exception controlleri
		//thr new ApplicationException("10000")
		new BusinessValidationException("10000").throwFinally();
		new BusinessValidationException("10001").throwFinally();
		return new ArrayList<>();
		//ret versionedOrganizationDao.getDTOList (AuditedOrganizationDTOConverter.class)		
	}
	
	public TemporalOrganizationDTO getOrganization(Serializable id) 
	{
		return versionedOrganizationDao.getDTO(id, AuditedOrganizationDTOConverter.class);
	}
}