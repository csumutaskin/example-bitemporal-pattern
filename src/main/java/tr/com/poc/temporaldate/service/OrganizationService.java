package tr.com.poc.temporaldate.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tr.com.poc.temporaldate.core.service.BaseService;
import tr.com.poc.temporaldate.dao.OrganizationDao;
import tr.com.poc.temporaldate.dto.OrganizationDTO;
import tr.com.poc.temporaldate.model.Organization;

/**
 * CRUD operations for {@link Organization} entity
 * @author umutaskin
 */
@Service
@Transactional
public class OrganizationService implements BaseService
{
	@Autowired
	private OrganizationDao organizationDao;
	
	public Boolean updateOrganization(Serializable id, OrganizationDTO toUpdate)
	{
		//return organizationDao.updateEntity(id, toUpdate);
		return null;
	}
	
	public Boolean deleteOrganization(Serializable id)
	{
		//return organizationDao.deleteEntity(id);
		return null;
	}
	
	public BigDecimal saveOrganization(OrganizationDTO toSave)
	{
		//return organizationDao.saveEntity(toSave);
		return null;
	}
	
	public List<OrganizationDTO> getAllOrganizations()
	{
		//return organizationDao.getAllEntities();
		return null;
	}
	
	public OrganizationDTO getOrganization(Serializable id) 
	{
		//return organizationDao.getOrganization(id);
		return null;
	}
}
