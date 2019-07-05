package tr.com.poc.temporaldate.bitemporalexample.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.bitemporalexample.dao.BitemporalOrganizationDao;
import tr.com.poc.temporaldate.bitemporalexample.dto.BitemporalOrganizationDTO;
import tr.com.poc.temporaldate.bitemporalexample.dto.converter.BitemporalOrganizationDTOConverter;
import tr.com.poc.temporaldate.bitemporalexample.model.BitemporalOrganization;
import tr.com.poc.temporaldate.common.ExceptionConstants;
import tr.com.poc.temporaldate.core.exception.ApplicationException;
import tr.com.poc.temporaldate.core.service.BaseService;

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
	
	@Autowired
	private BitemporalOrganizationDTOConverter converter;

	public Boolean updateOrganization(Serializable id, BitemporalOrganizationDTO toUpdate)
	{
		BitemporalOrganization updateEntityByDTO = bitemporalOrganizationDao.saveorUpdateEntityByDTO(id, toUpdate, BitemporalOrganizationDTOConverter.class);		
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
	
	public BitemporalOrganizationDTO saveOrMergeOrganization(Serializable id, BitemporalOrganizationDTO toSave)
	{		
		BitemporalOrganization organizationSaved = bitemporalOrganizationDao.saveorUpdateEntityByDTO(id, toSave, BitemporalOrganizationDTOConverter.class);
		BitemporalOrganizationDTO convertEntityToDTO = converter.convertEntityToDTO(organizationSaved);
		return convertEntityToDTO;		
	}
	
	//TODO Fix...
	public List<BitemporalOrganizationDTO> getAllOrganizations(LocalDateTime perspectiveTime, LocalDateTime effectiveTime)
	{
		if(perspectiveTime == null)
		{
			perspectiveTime = LocalDateTime.now();
		}
		List<BitemporalOrganization> entityWithNaturalId = bitemporalOrganizationDao.getEntityWithNaturalIdAtGivenDates(12, perspectiveTime, effectiveTime);
		return (List<BitemporalOrganizationDTO>)converter.convertEntityCollectionToDTOCollection(entityWithNaturalId);
	}
	
//	public BitemporalOrganizationDTO getOrganization(Serializable id, Date effectiveDate) 
//	{		
//		return bitemporalOrganizationDao.getDTOAtEffectiveDate(id, BitemporalOrganizationDTOConverter.class, effectiveDate);
//	}
	
}