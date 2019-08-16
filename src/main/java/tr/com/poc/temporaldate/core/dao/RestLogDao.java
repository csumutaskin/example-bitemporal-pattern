package tr.com.poc.temporaldate.core.dao;

import org.springframework.stereotype.Repository;

import tr.com.poc.temporaldate.core.dao.impl.BaseAuditedDaoImpl;
import tr.com.poc.temporaldate.core.model.RestServerLog;

@Repository
public class RestLogDao extends BaseAuditedDaoImpl<RestServerLog>
{
}
