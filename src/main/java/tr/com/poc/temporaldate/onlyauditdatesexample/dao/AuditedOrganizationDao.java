package tr.com.poc.temporaldate.onlyauditdatesexample.dao;

import org.springframework.stereotype.Repository;

import tr.com.poc.temporaldate.core.dao.impl.BaseVersionedDaoImpl;
import tr.com.poc.temporaldate.onlyauditdatesexample.model.AuditedOrganization;

@Repository
public class AuditedOrganizationDao extends BaseVersionedDaoImpl<AuditedOrganization>
{
	
}
