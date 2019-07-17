package tr.com.poc.temporaldate.bitemporalexample.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import tr.com.poc.temporaldate.bitemporalexample.dto.bitemporaluser.BitemporalUserWithOrganizationDTO;
import tr.com.poc.temporaldate.bitemporalexample.dto.bitemporaluser.BitemporalUserWithoutOrganizationDTO;
import tr.com.poc.temporaldate.bitemporalexample.dto.common.BitemporalReadRequestDTO;
import tr.com.poc.temporaldate.bitemporalexample.model.BitemporalUser;
import tr.com.poc.temporaldate.core.dao.annotation.PidDetail;
import tr.com.poc.temporaldate.core.dao.impl.BaseBiTemporalDaoImpl;
import tr.com.poc.temporaldate.core.dao.impl.OperationType;

/**
 * Data Access Repository Layer for {@link BitemporalUser} entity objects
 * @author umutaskin
 */
@Repository
@SuppressWarnings("unchecked")
public class BitemporalUserDao extends BaseBiTemporalDaoImpl<BitemporalUser> 
{
	/**
	 * Returns user list without its organization data
	 * @return
	 */
	public List<BitemporalUserWithoutOrganizationDTO> getAllUsersWithoutOrganization(BitemporalReadRequestDTO toRead)
	{
		PidDetail pidOfUserEntity =  getPidInfoOfCurrentEntity(OperationType.READ);
		
		boolean effectiveDateExists = (toRead !=null) && (toRead.getAtEffectiveTime() != null);
		boolean perspectiveDateExists = (toRead !=null) && (toRead.getAtPerspectiveTime() != null);
		boolean pidExists = (toRead !=null) && (toRead.getPid() != null);
				
		StringBuilder sql = new StringBuilder("Select U.userName, U.name from BitemporalUser U where 1=1 ");
		if(effectiveDateExists)
		{
			sql.append("and U.effectiveDateStart <= :effectiveDate and U.effectiveDateEnd > :effectiveDate");
		}
		if(perspectiveDateExists)
		{
			sql.append(" and U.perspectiveDateStart <= :perspectiveDate and U.perspectiveDateEnd > :perspectiveDate");
		}
		if(pidExists)
		{
			sql.append(" and ").append(pidOfUserEntity.getName() + " = :pid");
		}
		Query createQuery = getEntityManager().createQuery(sql.toString());
		if(effectiveDateExists)
		{
			createQuery.setParameter("effectiveDate", toRead.getAtEffectiveTime());
		}
		if(perspectiveDateExists)
		{
			createQuery.setParameter("perspectiveDate", toRead.getAtPerspectiveTime());
		}
		if(pidExists)
		{
			createQuery.setParameter("pid", toRead.getPid());
		}
		List<BitemporalUserWithoutOrganizationDTO> toReturn = new ArrayList<>();
		List<Object[]> resultList = createQuery.getResultList();
		if(CollectionUtils.isEmpty(resultList))
		{
			return toReturn;
		}	
		return resultList.stream().map(o -> new BitemporalUserWithoutOrganizationDTO(String.valueOf(o[0]), String.valueOf(o[1]))).collect(Collectors.toList());		
	}
	
	/**
	 * Returns user list with its organization data
	 * @return
	 */
	public List<BitemporalUserWithOrganizationDTO> getAllUsersWithOrganization(BitemporalReadRequestDTO toRead)
	{
		PidDetail pidOfUserEntity =  getPidInfoOfCurrentEntity(OperationType.READ);
		
		boolean effectiveDateExists = (toRead !=null) && (toRead.getAtEffectiveTime() != null);
		boolean perspectiveDateExists = (toRead !=null) && (toRead.getAtPerspectiveTime() != null);
		boolean pidExists = (toRead !=null) && (toRead.getPid() != null);
				
		StringBuilder sql = new StringBuilder("Select U.userName, U.name, U.organization.name, U.organization.fineAmount, U.organization.earnAmount from BitemporalUser U where 1=1 ");
		if(effectiveDateExists)
		{
			sql.append("and U.effectiveDateStart <= :effectiveDate and U.effectiveDateEnd > :effectiveDate");
		}
		if(perspectiveDateExists)
		{
			sql.append(" and U.perspectiveDateStart <= :perspectiveDate and U.perspectiveDateEnd > :perspectiveDate");
		}
		if(pidExists)
		{
			sql.append(" and ").append(pidOfUserEntity.getName() + " = :pid");
		}
		Query createQuery = getEntityManager().createQuery(sql.toString());
		if(effectiveDateExists)
		{
			createQuery.setParameter("effectiveDate", toRead.getAtEffectiveTime());
		}
		if(perspectiveDateExists)
		{
			createQuery.setParameter("perspectiveDate", toRead.getAtPerspectiveTime());
		}
		if(pidExists)
		{
			createQuery.setParameter("pid", toRead.getPid());
		}
		List<BitemporalUserWithOrganizationDTO> toReturn = new ArrayList<>();
		List<Object[]> resultList = createQuery.getResultList();
		if(CollectionUtils.isEmpty(resultList))
		{			
			return toReturn;
		}	
		return resultList.stream().map(o -> new BitemporalUserWithOrganizationDTO(String.valueOf(o[0]), String.valueOf(o[1]), String.valueOf(o[2]), Double.valueOf(String.valueOf(o[3])) , Double.valueOf(String.valueOf(o[4])))).collect(Collectors.toList());		
	}
}
