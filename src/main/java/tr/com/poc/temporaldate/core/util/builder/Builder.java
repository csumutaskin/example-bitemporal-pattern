package tr.com.poc.temporaldate.core.util.builder;

import java.lang.reflect.InvocationTargetException;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import org.apache.commons.lang3.exception.ExceptionUtils;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.common.ExceptionConstants;
import tr.com.poc.temporaldate.core.exception.ApplicationException;

/**
 * A generic builder that can build any type of POJOs in current system.
 * <p>
 * {@link https://howtocodetutorial.wordpress.com/generic-builder-pattern-in-java-8/} 
 * <p>
 * Sample Usage:</br>
 * <code>
 * Builder.build(Sample.class)</br>
 *         .with(s -> s.setId(1))</br>
 *         .with(s -> s.setName("Sample object"))</br>
 *         .with(s -> s.setList(list))</br>
 *         .get()</br>
 * </code>
 * 
 * @param <T> Any type of object that can has related setter methods.
 */
@Log4j2
public class Builder<T> 
{
    private T instance;
    private boolean ifCond = true;
    public Builder(Class<T> clazz)
    {
       try 
       {
           instance = clazz.getDeclaredConstructor().newInstance();
       } 
       catch (InstantiationException | IllegalAccessException|NoSuchMethodException|InvocationTargetException e) 
       {
    	   log.error("Instantiating an object using reflection in generic builder threw an unexpected exception, detail: {}", ExceptionUtils.getStackTrace(e));
           throw new ApplicationException(ExceptionConstants.UNEXPECTED_OBJECT_CREATION_EXCEPTION_THROUGH_REFLECTION, e);
       } 
    }
    
    public Builder<T> with(Consumer<T> setter)
    {
       if(ifCond)
       {
           setter.accept(instance);
       }
       return this;
    }
    
    public T get()
    {
       return instance;
    }
    
    public static <T> Builder<T> build(Class<T> clazz) 
    {
       return new Builder<>(clazz);
    }
    
    public Builder<T> iff(BooleanSupplier condition)
    {
       this.ifCond = condition.getAsBoolean();
       return this;
    }
    
    public Builder<T> endIf()
    {
        this.ifCond = true;
        return this;
    }
 }