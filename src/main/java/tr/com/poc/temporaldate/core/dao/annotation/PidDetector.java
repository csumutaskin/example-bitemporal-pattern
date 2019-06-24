package tr.com.poc.temporaldate.core.dao.annotation;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.reflections.Reflections;

import com.google.common.collect.Sets;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.common.Constants;

/**
 * A startup detection utility that detects @Pid 
 * (custom annotation used to indicate natural id of any bitemporal entity) 
 * on any entities and prepares a @pid usage map of these entities denoted as key value pairs
 * 
 * @author umutaskin
 *
 */
@Log4j2
public class PidDetector 
{
	private PidDetector()
	{}
	
	/**
	 * Creates a map where keys are entities with @pid usage, values are where these key entities are used as attributes.
	 * This is necessary to auto update entities along with associations from perspective view.
	 */
	public static void detectPidAnnotations()
	{
		log.info("Scanning @Pid annotation in all Entities");
		Reflections reflections = new Reflections(Constants.SCAN_PATH_JPA_ENTITIES);
		Set<Class<?>> entityClasses = reflections.getTypesAnnotatedWith(Entity.class);
		Set<Class<?>> pidEntityClasses = detectPidAnnotatedClassesAtColumnFields(entityClasses);
		Set<Class<?>> notPidEntityClasses = Sets.difference(entityClasses, pidEntityClasses);
		Map<Class<?>, Set<Class<?>>> pidEntityMap = getPidEntityMap(createEntityColumnMap(entityClasses),pidEntityClasses);
		log.info("@Entity classes in projects: {}", entityClasses.toString());
		log.info("@Pid annotated classes among entity classes: {}", pidEntityClasses.toString());
		log.info("Not @Pid annotated classes among entity classes: {}", notPidEntityClasses.toString());		
		log.info("In case of auto update with @pid entity, other entities that should also be updated as key value pair: {}",  pidEntityMap.keySet().stream().map(key -> key + "=" + pidEntityMap.get(key)).collect(Collectors.joining(", ", "{", "}")));
	}
	
	
	
	/* Detects all entity classes that has @Pid annotation in one of their '@Column'ed fields */
	public static Set<Class<?>> detectPidAnnotatedClassesAtColumnFields(Set<Class<?>> entityClasses)
	{
		Set<Class<?>> pidEntityClasses = new HashSet<>();
		for(Class<?> clazz : entityClasses)			
		{
			Field[] fields = clazz.getDeclaredFields();
			for(Field field : fields)
			{
				if(field.isAnnotationPresent(Pid.class) && field.isAnnotationPresent(Column.class))
				{					
					pidEntityClasses.add(clazz);
					break;
				}
			}
		}
		return pidEntityClasses;
	}
	
	public static Map<Class<?>, Set<Class<?>>> createEntityColumnMap(Set<Class<?>> entityClasses)
	{
		Map<Class<?>, Set<Class<?>>> entityWithValues = new HashMap<>();
		for(Class<?> clazz : entityClasses)			
		{
			Field[] fields = clazz.getDeclaredFields();
			Set<Class<?>> fieldsOfClass = new HashSet<>();
			for(Field field : fields)
			{
				if(field.isAnnotationPresent(Column.class))
				{
					fieldsOfClass.add(field.getType());
				}
			}
			entityWithValues.put(clazz, fieldsOfClass);
		}
		return entityWithValues;
	}
	
	/**
	 * Returns the following key value map:
	 * Key: Any entity that has a @Column And @Pid annotated column
	 * Value: Which other entities hold the entity in key (of this map) as one of its attributes 
	 * <p>
	 * <b>e.g.:</b> If An Organization Entity contains a @Pid column, and if a User entity has an Organization attribute as one of its columns (also known as foreign key constraint) then one of the key value pairs in this map is:
	 *       </br>
	 *       User -> {Organization, ....} and etc
	 * 
	 */
	public static Map<Class<?>, Set<Class<?>>> getPidEntityMap(Map<Class<?>, Set<Class<?>>> entityColumnMap, Set<Class<?>> pidContainedEntitySet)
	{
		Map<Class<?>, Set<Class<?>>> pidMap = new HashMap<>();
		Set<Class<?>> keySet = entityColumnMap.keySet();
		for(Class<?> key : keySet)
		{
			pidMap.put(key, entityColumnMap.get(key).stream().filter(c -> pidContainedEntitySet.contains(c)).collect(Collectors.toSet()));
		}
		return convertKeyToValueViceVersa(pidMap);			
	}
	
	/* Converts key to values and values to keys */
	private static Map<Class<?>, Set<Class<?>>> convertKeyToValueViceVersa(Map<Class<?>, Set<Class<?>>> pidMap)
	{
		Map<Class<?>, Set<Class<?>>> reverseMap = new HashMap<>();
		Set<Class<?>> keySet = pidMap.keySet();
		for(Class<?> key : keySet)
		{
			Set<Class<?>> valueSet = pidMap.get(key);
			for(Class<?> clazz : valueSet)
			{
				Set<Class<?>> reverseMapValues = reverseMap.get(clazz);
				if(reverseMapValues == null)
				{
					reverseMapValues = new HashSet<>();
				}
				reverseMapValues.add(key);
				reverseMap.put(clazz, reverseMapValues);
			}
		}
		return reverseMap;
	}
}
