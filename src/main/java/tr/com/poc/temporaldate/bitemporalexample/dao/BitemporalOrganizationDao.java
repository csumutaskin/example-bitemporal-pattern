package tr.com.poc.temporaldate.bitemporalexample.dao;

import org.springframework.stereotype.Repository;

import tr.com.poc.temporaldate.bitemporalexample.model.BitemporalOrganization;
import tr.com.poc.temporaldate.core.dao.impl.BaseBiTemporalDaoImpl;

/**
 * Repository Layer for Data Access Operations of {@link BitemporalOrganization} object
 * @author umutaskin
 *
 */
@Repository
public class BitemporalOrganizationDao extends BaseBiTemporalDaoImpl<BitemporalOrganization>
{}
