package tr.com.poc.temporaldate.core.util.logging;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.springframework.util.CollectionUtils;

/**
 * Log4j2 and Log4j jar name locator for custom log layout
 * Usage:
 * 
 * in log property, define this pattern as %jarLocator when defining the log layout.
 * 
 * @author umutaskin
 *
 */
@Plugin(name = "CustomAppender", category = "Converter")
@ConverterKeys({ "jarLocator" })
public class Log4jCustomJarNameExtractor extends LogEventPatternConverter 
{
	protected Log4jCustomJarNameExtractor(String name, String style) 
	{
		super(name, style);
	}

	public static Log4jCustomJarNameExtractor newInstance(String[] options) 
	{
		return new Log4jCustomJarNameExtractor("jarLocator", Thread.currentThread().getName());
	}

	@Override
	public void format(LogEvent event, StringBuilder toAppendTo) 
	{
        toAppendTo.append(getJarContainer(event.getLoggerName()));
	}
	
	//extracts jar name from classname if possible...
	private String getJarContainer(String className) 
	{		
        try 
        {
            ClassLoader loader = Log4jCustomJarNameExtractor.class.getClassLoader();
            String name = className.replace('.', '/').concat(".class");
            URL url = loader.getResource(name);
            String fullJarPath = url.getPath();
            if(StringUtils.isNotBlank(fullJarPath))
            {         
            	List<String> toReturn = Arrays.asList(fullJarPath.split("/")).stream().filter(s -> s.contains(".jar")).map(s-> s.substring(0, s.indexOf(".jar"))).limit(1).collect(Collectors.toList());
            	if(!CollectionUtils.isEmpty(toReturn))
            	{
            		String toReturnSubstr = toReturn.get(0);
            		if(StringUtils.isNotBlank(toReturnSubstr) && toReturnSubstr.length() > 28)
            		{
            			toReturnSubstr = toReturnSubstr.substring(0, 27);
            		}
            		return "(" + toReturnSubstr + ")";
            	}
            }           
            return "";
        } 
        catch (Exception e) 
        {
            return "";
        }
    }
}
