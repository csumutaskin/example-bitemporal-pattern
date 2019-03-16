package tr.com.poc.temporaldate.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.core.service.BaseService;
import tr.com.poc.temporaldate.dao.OrganizationDao;
import tr.com.poc.temporaldate.dto.OrganizationDTO;
import tr.com.poc.temporaldate.dto.converter.OrganizationDTOConverter;
import tr.com.poc.temporaldate.model.Organization;

/**
 * CRUD operations for {@link Organization} entity
 * @author umutaskin
 */
@Service
@Transactional
@Log4j2
public class OrganizationService implements BaseService
{
	@Autowired
	private OrganizationDao organizationDao;
	
	public Boolean updateOrganization(Serializable id, OrganizationDTO toUpdate)
	{
		Organization updateEntityByDTO = organizationDao.updateEntityByDTO(id, toUpdate, OrganizationDTOConverter.class);
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
		organizationDao.deleteEntity(id);
		//return organizationDao.deleteEntity(id);
		return null;
	}
	
	public BigDecimal saveOrganization(OrganizationDTO toSave)
	{
		Organization organizationSaved = organizationDao.saveDTOReturnEntity(toSave, OrganizationDTOConverter.class);
		return organizationSaved.getId();
		
	}
	
	public List<OrganizationDTO> getAllOrganizations()
	{
		return organizationDao.getDTOList(OrganizationDTOConverter.class);		
	}
	
	public OrganizationDTO getOrganization(Serializable id) 
	{
		//return organizationDao.getOrganization(id);
		return null;
	}
}
