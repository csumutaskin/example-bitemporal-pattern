package tr.com.poc.temporaldate.bitemporalexample.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import tr.com.poc.temporaldate.bitemporalexample.dao.BitemporalOrganizationDao;
import tr.com.poc.temporaldate.bitemporalexample.dao.UserDao;
import tr.com.poc.temporaldate.bitemporalexample.dto.BitemporalOrganizationDTO;
import tr.com.poc.temporaldate.bitemporalexample.model.BitemporalOrganization;
import tr.com.poc.temporaldate.bitemporalexample.model.User;
import tr.com.poc.temporaldate.core.service.BaseService;
import tr.com.poc.temporaldate.core.util.DateUtils;

@Service
@Transactional
public class UserService implements BaseService
{	
	@Autowired
	private BitemporalOrganizationDao orgDao;
	
	@Autowired
	private UserDao userDao;
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public User getAllOrganizations()
	{
		//return userDao.getEntityWithPrimaryId(new Long(5l));
//		String sql = "Select U from User U, BitemporalOrganization O where O.orgId = U.orgId and U.id = 1 and O.effectiveDateStart > '2023-10-10' and O.effectiveDateEnd > '2023-10-10' and "
//				+ "O.name = 'string'";
		
//		String sql = "select U.id from User U join fetch U.organization O where (U.orgId = O.orgId and O.name = 'string')";
//		
//		String sql = "Select U from User U, BitemporalOrganization O where O.orgId = U.orgId and U.id = 1 and O.effectiveDateStart > '2023-10-10' and O.effectiveDateEnd > '2023-10-10' and "
//				+ "O.name = 'string'";
		
		String sql ="Select new User(U,O) from User U, BitemporalOrganization O where O.orgId = U.orgId and O.effectiveDateStart > '2019-10-10'";
		
		
		
		Query createQuery = userDao.getEntityManager().createQuery(sql);
		List<User> resultList = createQuery.getResultList();
		if(!CollectionUtils.isEmpty(resultList))
		{
			BitemporalOrganization createDate = resultList.get(0).getOrganization();
			System.out.println(createDate);
		}
		System.out.println(resultList);
		
		return null;
	}
	
	public List<BitemporalOrganizationDTO> saveUserWithOrganization()
	{
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime end = DateUtils.END_OF_SOFTWARE;
		
		BitemporalOrganization org = new BitemporalOrganization(null, "EPIAS", null, 1d, 1d);
		org.setEffectiveDateEnd(end);
		org.setEffectiveDateStart(now);
		org.setPerspectiveDateStart(now);
		org.setPerspectiveDateEnd(end);
		org.setOrgId((Long)orgDao.getSequenceNextValueOfPidColumn());
		org.setIsDeleted(Boolean.FALSE);

		User umut = new User();
		umut.setName("Umut Askin");
		umut.setOrgId(org.getOrgId());
		umut.setOrganization(org);
		umut.setEffectiveDateStart(now);
		umut.setEffectiveDateEnd(end);
		umut.setPerspectiveDateStart(now);
		umut.setPerspectiveDateEnd(end);
		umut.setIsDeleted(Boolean.FALSE);
		userDao.saveEntityWithPrimaryId(umut);
		
		return null;
	}
	
	
}