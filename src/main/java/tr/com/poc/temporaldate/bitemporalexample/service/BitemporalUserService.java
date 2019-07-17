package tr.com.poc.temporaldate.bitemporalexample.service;

import java.io.Serializable;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tr.com.poc.temporaldate.bitemporalexample.dao.BitemporalUserDao;
import tr.com.poc.temporaldate.bitemporalexample.dto.bitemporaluser.BitemporalUserWithOrganizationDTO;
import tr.com.poc.temporaldate.bitemporalexample.dto.bitemporaluser.BitemporalUserWithoutOrganizationDTO;
import tr.com.poc.temporaldate.bitemporalexample.dto.common.BitemporalReadRequestDTO;
import tr.com.poc.temporaldate.bitemporalexample.dto.converter.bitemporaluser.BitemporalUserWithOrganizationDTOConverter;
import tr.com.poc.temporaldate.bitemporalexample.model.BitemporalUser;
import tr.com.poc.temporaldate.core.service.BaseService;

/**
 * Transactional BitemporalUser Service Layer
 * @author umutaskin
 */
@Service
@Transactional
public class BitemporalUserService implements BaseService
{	
	@Autowired
	private BitemporalUserDao userDao;
	
	@Autowired
	private BitemporalUserWithOrganizationDTOConverter withOrganizationConverter;
				
	/**
	 * Gets all Users without their Organization information
	 * @return
	 */
	public List<BitemporalUserWithoutOrganizationDTO> getAllUsersWithoutOrganization(BitemporalReadRequestDTO toRead)
	{
		return userDao.getAllUsersWithoutOrganization(toRead);
	}
	
	/**
	 * Gets all Users with their Organization information
	 * @return
	 */
	public List<BitemporalUserWithOrganizationDTO> getAllUsersWithOrganization(BitemporalReadRequestDTO toRead)
	{
		return userDao.getAllUsersWithOrganization(toRead);
	}
	
	/**
	 * Saves or updates user with its organization information
	 * @param pid
	 * @param toSaveOrUpdate
	 * @return
	 */
	public BitemporalUserWithOrganizationDTO saveOrMergeUserWithOrganization(Serializable pid, BitemporalUserWithOrganizationDTO toSaveOrUpdate)
	{
		BitemporalUser organizationSavedOrUpdated = userDao.saveOrUpdateDTOWithNaturalId(pid, toSaveOrUpdate, BitemporalUserWithOrganizationDTOConverter.class);
		return withOrganizationConverter.convertEntityToDTO(organizationSavedOrUpdated);		
	}
}