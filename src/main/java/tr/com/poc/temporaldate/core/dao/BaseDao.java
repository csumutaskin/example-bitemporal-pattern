package tr.com.poc.temporaldate.core.dao;

import tr.com.poc.temporaldate.core.model.temporal.BaseTemporalEntity;

/**
 * Marker interface for Repository Abstraction
 * 
 * @author umutaskin
 *
 * @param <E> any class that extends {@link BaseTemporalEntity}
 */
public interface BaseDao<E extends BaseTemporalEntity> 
{}
