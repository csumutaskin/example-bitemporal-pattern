package tr.com.poc.temporaldate.temporalexample.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.core.service.BaseService;
import tr.com.poc.temporaldate.temporalexample.dao.TemporalOrganizationDao;
import tr.com.poc.temporaldate.temporalexample.dto.TemporalOrganizationDTO;
import tr.com.poc.temporaldate.temporalexample.dto.converter.TemporalOrganizationDTOConverter;
import tr.com.poc.temporaldate.temporalexample.model.TemporalOrganization;

/**
 * Transactional Service operations for {@link TemporalOrganization} entity
 * @author umutaskin
 */
@Service
@Transactional
@Log4j2
public class TemporalOrganizationService implements BaseService
{
	@Autowired
	private TemporalOrganizationDao organizationDao;
	
	public Boolean updateOrganization(Serializable id, TemporalOrganizationDTO toUpdate)
	{
		TemporalOrganization updateEntityByDTO = organizationDao.updateEntityByDTO(id, toUpdate, TemporalOrganizationDTOConverter.class);
		if(updateEntityByDTO == null)
		{
			log.info("No organization with id: {} is detected on db. Thus no update operation will be performed this time using DTO: {}." , id, toUpdate);
			//TODO: throw exception necessary or not...
			return false;
		}
		return true;
	}
	
	public Boolean deleteOrganization(Serializable id)
	{
		boolean entityDeleted = organizationDao.deleteEntity(id);
		return Boolean.valueOf(entityDeleted);
	}
	
	public BigDecimal saveOrganization(TemporalOrganizationDTO toSave)
	{
		TemporalOrganization organizationSaved = organizationDao.saveDTOReturnEntity(toSave, TemporalOrganizationDTOConverter.class);
		return organizationSaved.getId();		
	}
	
	public List<TemporalOrganizationDTO> getAllOrganizations()
	{
		return organizationDao.getDTOList(TemporalOrganizationDTOConverter.class);		
	}
	
	public TemporalOrganizationDTO getOrganization(Serializable id) 
	{		
		return organizationDao.getDTO(id, TemporalOrganizationDTOConverter.class);
	}
}
