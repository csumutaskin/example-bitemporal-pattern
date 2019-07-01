package tr.com.poc.temporaldate.bitemporalexample.dao;

import org.springframework.stereotype.Repository;

import tr.com.poc.temporaldate.bitemporalexample.model.RestServerLog;
import tr.com.poc.temporaldate.core.dao.impl.BaseAuditedDaoImpl;

@Repository
public class RestLogDao extends BaseAuditedDaoImpl<RestServerLog>
{
}
