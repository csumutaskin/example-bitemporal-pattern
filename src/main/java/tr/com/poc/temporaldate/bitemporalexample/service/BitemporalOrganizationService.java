package tr.com.poc.temporaldate.bitemporalexample.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tr.com.poc.temporaldate.bitemporalexample.dao.BitemporalOrganizationDao;
import tr.com.poc.temporaldate.bitemporalexample.dto.BitemporalOrganizationDTO;
import tr.com.poc.temporaldate.bitemporalexample.dto.BitemporalOrganizationReadRequestDTO;
import tr.com.poc.temporaldate.bitemporalexample.dto.converter.BitemporalOrganizationDTOConverter;
import tr.com.poc.temporaldate.bitemporalexample.model.BitemporalOrganization;
import tr.com.poc.temporaldate.core.service.BaseService;

/**
 * Transactional Service Operations for {@link BitemporalOrganization} entity
 * 
 * @author umutaskin
 */
@Service
@Transactional
public class BitemporalOrganizationService  implements BaseService
{
	@Autowired
	private BitemporalOrganizationDao bitemporalOrganizationDao;
	
	@Autowired
	private BitemporalOrganizationDTOConverter converter;
	
	/**
	 * Retrieves all organization data with the given parameter set in {@link BitemporalOrganizationReadRequestDTO}
	 * @param toRead input parameters for read criteria
	 * @return List of {@link BitemporalOrganizationDTO}
	 */
	public List<BitemporalOrganizationDTO> getAllOrganizations(BitemporalOrganizationReadRequestDTO toRead)
	{
		List<BitemporalOrganization> entityWithNaturalId = bitemporalOrganizationDao.getEntityWithNaturalIdAtGivenDates(toRead.getOrgId(), toRead.getAtPerspectiveTime(), toRead.getAtEffectiveTime());		
		return (List<BitemporalOrganizationDTO>)converter.convertEntityCollectionToDTOCollection(entityWithNaturalId);
	}
	
	/**
	 * Saves or Updates the given {@link BitemporalOrganizationDTO} object
	 * @param id if null, persist operation is done, if non-null update operation is done
	 * @param toSaveOrUpdate object to be persisted or updated
	 * @return {@link BitemporalOrganizationDTO} Saved or Updated object details
	 */
	public BitemporalOrganizationDTO saveOrMergeOrganization(Serializable id, BitemporalOrganizationDTO toSaveOrUpdate)
	{		
		BitemporalOrganization organizationSavedOrUpdated = bitemporalOrganizationDao.saveOrUpdateDTOWithNaturalId(id, toSaveOrUpdate, BitemporalOrganizationDTOConverter.class);
		return converter.convertEntityToDTO(organizationSavedOrUpdated);				
	}	
	
	/**
	 * Removes all the tuples of {@link BitemporalOrganizationDTO} object, that match the given criteria
	 * @param toRead if null, persist operation is done, if non-null update operation is done	 * 
	 * @return {@link Boolean} true if removal is successful
	 */
	public void removeOrganizations(BitemporalOrganizationReadRequestDTO toRead)
	{		
		if(toRead == null)
		{
			return;
		}
		bitemporalOrganizationDao.removeEntities(toRead.getOrgId(), toRead.getAtPerspectiveTime(), toRead.getAtEffectiveTime());						
	}	
}