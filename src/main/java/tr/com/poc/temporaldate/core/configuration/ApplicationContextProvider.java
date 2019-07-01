package tr.com.poc.temporaldate.core.configuration;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ApplicationContextProvider implements ApplicationContextAware
{

	private static ApplicationContext ctx;

	public static ApplicationContext getApplicationContext()
	{
		return ctx;
	}

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException
	{
		ctx = context;
	}

	public static Object getBeanFromApplicationContext(Class<?> bean)
	{
		return ctx.getBean(bean);
	}
}
