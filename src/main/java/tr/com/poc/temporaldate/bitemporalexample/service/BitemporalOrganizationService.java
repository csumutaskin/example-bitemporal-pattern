package tr.com.poc.temporaldate.bitemporalexample.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.log4j.Log4j2;

import tr.com.poc.temporaldate.bitemporalexample.dao.BitemporalOrganizationDao;
import tr.com.poc.temporaldate.bitemporalexample.dto.bitemporalorganization.BitemporalOrganizationSaveOrUpdateRequestDTO;
import tr.com.poc.temporaldate.bitemporalexample.dto.bitemporalorganization.BitemporalOrganizationSaveOrUpdateResponseDTO;
import tr.com.poc.temporaldate.bitemporalexample.dto.common.BitemporalReadRequestDTO;
import tr.com.poc.temporaldate.bitemporalexample.dto.converter.bitemporalorganization.BitemporalOrganizationSaveOrUpdateRequestDTOConverter;
import tr.com.poc.temporaldate.bitemporalexample.dto.converter.bitemporalorganization.BitemporalOrganizationSaveOrUpdateResponseDTOConverter;
import tr.com.poc.temporaldate.bitemporalexample.model.BitemporalOrganization;
import tr.com.poc.temporaldate.core.service.BaseService;

/**
 * Transactional Service Operations for {@link BitemporalOrganization} entity
 * 
 * @author umutaskin
 */
@Service
@Transactional
@Log4j2
public class BitemporalOrganizationService implements BaseService
{
	@Autowired
	private BitemporalOrganizationDao bitemporalOrganizationDao;

	@Autowired
	private BitemporalOrganizationSaveOrUpdateResponseDTOConverter converter;
	
	/**
	 * Retrieves all organization data with the given parameter set in {@link BitemporalReadRequestDTO}
	 * @param toRead input parameters for read criteria
	 * @return List of {@link BitemporalOrganizationSaveOrUpdateResponseDTO}
	 */
	public List<BitemporalOrganizationSaveOrUpdateResponseDTO> getAllOrganizations(BitemporalReadRequestDTO toRead)
	{
		List<BitemporalOrganization> entityWithNaturalId = bitemporalOrganizationDao.getEntityWithNaturalIdAtGivenDates(toRead.getPid(), toRead.getAtObserverTime(), toRead.getAtEffectiveTime());		
		return (List<BitemporalOrganizationSaveOrUpdateResponseDTO>)converter.convertEntityCollectionToDTOCollection(entityWithNaturalId);
	}

	/**
	 * Saves or Updates the given {@link BitemporalOrganizationSaveOrUpdateResponseDTO} object
	 * @param id if null, persist operation is done, if non-null update operation is done
	 * @param toSaveOrUpdate object to be persisted or updated
	 * @return {@link BitemporalOrganizationSaveOrUpdateResponseDTO} Saved or Updated object details
	 */
	public BitemporalOrganizationSaveOrUpdateResponseDTO saveOrMergeOrganization(Serializable id, BitemporalOrganizationSaveOrUpdateRequestDTO toSaveOrUpdate)
	{		
		BitemporalOrganization organizationSavedOrUpdated = bitemporalOrganizationDao.saveOrUpdateDTOWithNaturalId(id, toSaveOrUpdate, BitemporalOrganizationSaveOrUpdateRequestDTOConverter.class);
		return converter.convertEntityToDTO(organizationSavedOrUpdated);				
	}	
	
	/**
	 * Removes all the tuples of {@link BitemporalOrganizationSaveOrUpdateResponseDTO} object, that match the given criteria
	 * @param toRead if null, persist operation is done, if non-null update operation is done	 * 
	 * @return {@link Boolean} true if removal is successful
	 */
	public void removeOrganizations(BitemporalReadRequestDTO toRead)
	{		
		if(toRead == null)
		{
			return;
		}
		bitemporalOrganizationDao.removeEntityWithNaturalIdWithinEffectiveAndObserverDates(toRead.getPid(), toRead.getAtObserverTime(), toRead.getAtEffectiveTime());						
	}	
}