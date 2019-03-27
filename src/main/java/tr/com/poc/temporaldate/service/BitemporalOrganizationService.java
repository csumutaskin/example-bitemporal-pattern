package tr.com.poc.temporaldate.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.core.service.BaseService;
import tr.com.poc.temporaldate.dao.BitemporalOrganizationDao;
import tr.com.poc.temporaldate.dto.OrganizationDTO;
import tr.com.poc.temporaldate.dto.converter.BitemporalOrganizationDTOConverter;
import tr.com.poc.temporaldate.dto.converter.OrganizationDTOConverter;
import tr.com.poc.temporaldate.model.BitemporalOrganization;
import tr.com.poc.temporaldate.model.Organization;

/**
 * Transactional Service Operations for {@link BitemporalOrganization} entity
 * @author umutaskin
 */
@Service
@Transactional
@Log4j2
public class BitemporalOrganizationService  implements BaseService
{
	@Autowired
	private BitemporalOrganizationDao bitemporalOrganizationDao;

	public Boolean updateOrganization(Serializable id, OrganizationDTO toUpdate, Date effectiveStartDate, Date effectiveEndDate)
	{
		BitemporalOrganization updateEntityByDTO = bitemporalOrganizationDao.saveorUpdateEntityByDTO(id, toUpdate, BitemporalOrganizationDTOConverter.class, effectiveStartDate, effectiveEndDate);		
		if(updateEntityByDTO == null)
		{
			log.info("No BitemporalOrganization with id: {} is detected on db. Thus no update operation will be performed this time using DTO: {}." , id, toUpdate);
			//TODO: throw exception necessary or not...
			return false;
		}
		return true;
	}
	
	public Boolean deleteOrganization(Serializable id)
	{
		boolean entityDeleted = bitemporalOrganizationDao.deleteEntityWithAllVersions(id);
		return Boolean.valueOf(entityDeleted);
	}
	
	public BigDecimal saveOrganization(OrganizationDTO toSave, Date effectiveStartDate, Date effectiveEndDate)
	{
		BitemporalOrganization organizationSaved = bitemporalOrganizationDao.saveorUpdateEntityByDTO(null, toSave, BitemporalOrganizationDTOConverter.class, effectiveStartDate, effectiveEndDate);
		return organizationSaved.getId();		
	}
	
	public List<OrganizationDTO> getAllOrganizations(Date effectiveDate)
	{
		return bitemporalOrganizationDao.getDTOListAtEffectiveDate(BitemporalOrganizationDTOConverter.class, effectiveDate);		
	}
	
	public OrganizationDTO getOrganization(Serializable id, Date effectiveDate) 
	{		
		return bitemporalOrganizationDao.getDTOAtEffectiveDate(id, BitemporalOrganizationDTOConverter.class, effectiveDate);
	}
	
}