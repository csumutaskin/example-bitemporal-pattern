package tr.com.poc.temporaldate.core.configuration;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import tr.com.poc.temporaldate.common.ExceptionConstants;
import tr.com.poc.temporaldate.core.exception.ApplicationException;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ApplicationContextProvider implements ApplicationContextAware
{

	private static ApplicationContext ctx;

	public static ApplicationContext getApplicationContext()
	{
		return ctx;
	}

	@Override
	public void setApplicationContext(ApplicationContext context)
	{
		ctx = context;
	}

	public static Object getBeanFromApplicationContext(Class<?> bean)
	{
		try
		{
			return ctx.getBean(bean);
		}
		catch (BeansException ex)
		{
			log.error("Class : {} , BeansException : {}", bean.getSimpleName(), ex);
			throw new ApplicationException(ExceptionConstants.UNEXPECTED_OBJECT_CREATION_EXCEPTION_THROUGH_REFLECTION, ex);
		}
	}
}
