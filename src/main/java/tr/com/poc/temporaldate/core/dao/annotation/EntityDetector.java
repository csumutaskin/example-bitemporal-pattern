//package tr.com.poc.temporaldate.core.dao.annotation;
//
//import java.lang.reflect.Field;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//
//import javax.persistence.Entity;
//
//import org.reflections.Reflections;
//
//import lombok.extern.log4j.Log4j2;
//import tr.com.poc.temporaldate.common.Constants;
//
///**
// * A startup detection utility that detects associations of entities between each other
// * @author umutaskin
// */
//@Log4j2
//public class EntityDetector 
//{
//	private EntityDetector(){}
//	
//	private static Map<Class<?>, Set<Class<?>>> entityAssociationMap;
//	
//	/**
//	 * Returns entity association map
//	 * @return
//	 */
//	public static Map<Class<?>, Set<Class<?>>> getEntityAssociationMap() 
//	{
//		return entityAssociationMap;
//	}
//	
//	/**
//	 * Detects entity associations
//	 */
//	public static void detectEntityAssociations()
//	{
//		log.info("Scanning entity annotations among all Entities");
//		Reflections reflections = new Reflections(Constants.SCAN_PATH_JPA_ENTITIES);
//		Set<Class<?>> entityClasses = reflections.getTypesAnnotatedWith(Entity.class);
//		entityAssociationMap = detectEntityAssociationMap(entityClasses);
//	}
//	
//	/**
//	 * Detects all entity classes that has @Pid annotation in one of their '@Column'ed fields 
//	 * @author umutaskin 
//	 */
//	//TODO: cyclic check..
//	public static Map<Class<?>, Set<Class<?>>> detectEntityAssociationMap(Set<Class<?>> entityClasses)
//	{
//		Map<Class<?>, Set<Class<?>>> entityAssociationMap = new ConcurrentHashMap<>();
//		for(Class<?> clazz : entityClasses)			
//		{
//			Field[] fields = clazz.getDeclaredFields();
//			for(Field field : fields)
//			{
//				Class<?> fieldClass = field.getDeclaringClass();
//				if(entityClasses.contains(fieldClass))
//				{					
//					Set<Class<?>> setOfCurrentEntity = entityAssociationMap.get(clazz);
//					if(setOfCurrentEntity == null)
//					{
//						setOfCurrentEntity = new HashSet<>();
//					}
//					setOfCurrentEntity.add(fieldClass);
//					entityAssociationMap.put(clazz, setOfCurrentEntity);					
//				}
//			}
//		}
//		return entityAssociationMap;
//	}	
//}
