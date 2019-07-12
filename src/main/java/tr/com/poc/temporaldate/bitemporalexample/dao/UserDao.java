package tr.com.poc.temporaldate.bitemporalexample.dao;

import org.springframework.stereotype.Repository;

import tr.com.poc.temporaldate.bitemporalexample.model.User;
import tr.com.poc.temporaldate.core.dao.impl.BaseBiTemporalDaoImpl;

@Repository
public class UserDao extends BaseBiTemporalDaoImpl<User> 
{

}
