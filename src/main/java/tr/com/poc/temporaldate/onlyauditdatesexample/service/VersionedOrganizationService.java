package tr.com.poc.temporaldate.onlyauditdatesexample.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.core.service.BaseService;
import tr.com.poc.temporaldate.onlyauditdatesexample.dao.VersionedOrganizationDao;
import tr.com.poc.temporaldate.onlyauditdatesexample.dto.converter.VersionedOrganizationDTOConverter;
import tr.com.poc.temporaldate.onlyauditdatesexample.model.VersionedOrganization;
import tr.com.poc.temporaldate.temporalexample.dto.OrganizationDTO;

/**
 * Transactional service operations for {@link VersionedOrganization} entity
 * @author umutaskin
 */
@Service
@Transactional
@Log4j2
public class VersionedOrganizationService  implements BaseService
{
	@Autowired
	private VersionedOrganizationDao versionedOrganizationDao;
	
	public Boolean updateVersionedOrganization(Serializable id, OrganizationDTO toUpdate)
	{
		VersionedOrganization updateEntityByDTO = versionedOrganizationDao.updateEntityByDTO(id, toUpdate, VersionedOrganizationDTOConverter.class);
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
	
	public BigDecimal saveVersionedOrganization(OrganizationDTO toSave)
	{
		VersionedOrganization versionedOrganizationSaved = versionedOrganizationDao.saveDTOReturnEntity(toSave, VersionedOrganizationDTOConverter.class);
		return versionedOrganizationSaved.getId();
	}
	
	public List<OrganizationDTO> getAllOrganizations()
	{
		return versionedOrganizationDao.getDTOList(VersionedOrganizationDTOConverter.class);		
	}
	
	public OrganizationDTO getOrganization(Serializable id) 
	{
		return versionedOrganizationDao.getDTO(id, VersionedOrganizationDTOConverter.class);
	}
}