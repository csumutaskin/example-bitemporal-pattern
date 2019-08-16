package tr.com.poc.temporaldate.core.util.logging;

import static tr.com.poc.temporaldate.common.CommonConstants.SCAN_PATH_REST_CONTROLLER;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class RestServerLogInit
{
	private RestServerLogInit() {}

	private static HashMap<String, Boolean> textLoggableMethodRequest = new HashMap<>();

	private static HashMap<String, Boolean> textLoggableMethodResponse = new HashMap<>();

	private static HashMap<String, Boolean> dbLoggableMethodRequest = new HashMap<>();

	private static HashMap<String, Boolean> dbLoggableMethodResponse = new HashMap<>();

	private static Set<String> pathVariableMethod = new HashSet<>();

	private static String contextPath;

	public static void init(String contextPathUrl)
	{
		log.debug("Initializing rest server logging utility");
		contextPath = contextPathUrl;
		Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage(SCAN_PATH_REST_CONTROLLER)).setScanners(new MethodAnnotationsScanner(), new TypeAnnotationsScanner(), new SubTypesScanner()));
		findLoggableFromClassAnnotation(reflections);
		findLoggableFromMethodAnnotation(reflections);

	}

	private static void findLoggableFromMethodAnnotation(Reflections reflections)
	{
		Set<Method> methods = reflections.getMethodsAnnotatedWith(RestLoggable.class);
		for (Method method : methods)
		{
			Class<?> declaringClass = method.getDeclaringClass();
			RequestMapping request = declaringClass.getAnnotation(RequestMapping.class);
			if (request == null || request.value().length == 0)
			{
				continue;
			}
			String urlPath = request.value()[0];
			if (!declaringClass.isAnnotationPresent(RestLoggable.class))
			{
				enrichLoggableFromMethod(declaringClass, urlPath, method);
			}
		}
	}

	private static void findLoggableFromClassAnnotation(Reflections reflections)
	{
		Set<Class<?>> restClasses = reflections.getTypesAnnotatedWith(RestLoggable.class);

		for (Class<?> restClass : restClasses)
		{

			RequestMapping request = restClass.getAnnotation(RequestMapping.class);
			if (request == null || request.value().length == 0)
			{
				continue;
			}
			String urlPath = request.value()[0];

			for (Method method : restClass.getMethods())
			{
				enrichLoggableFromMethod(restClass, urlPath, method);
			}
		}
	}

	private static void enrichLoggableFromMethod(Class<?> restClass, String urlPath, Method method)
	{
		String methodPathUrl = getUrlPathFromMethod(method);
		if (methodPathUrl == null)
		{
			return;
		}
		RestLoggable loggable = method.getAnnotation(RestLoggable.class);
		if (null == loggable)
		{
			loggable = restClass.getAnnotation(RestLoggable.class);
		}

		boolean isPathVariable = Arrays.stream(method.getParameters()).anyMatch(p -> p.getAnnotation(PathVariable.class) != null);

		addUrlPath(urlPath + methodPathUrl, isPathVariable, loggable);
	}

	private static void addUrlPath(String path, Boolean isPathVariable, RestLoggable loggable)
	{
		if (loggable.textRequest())
		{
			textLoggableMethodRequest.putIfAbsent(contextPath + path, isPathVariable);
		}

		if (loggable.textResponse())
		{
			textLoggableMethodResponse.putIfAbsent(contextPath + path, isPathVariable);
		}

		if (loggable.dbRequest())
		{
			dbLoggableMethodRequest.putIfAbsent(contextPath + path, isPathVariable);
		}

		if (loggable.dbResponse())
		{
			dbLoggableMethodResponse.putIfAbsent(contextPath + path, isPathVariable);
		}

		if (isPathVariable)
		{
			pathVariableMethod.add(contextPath + path);
		}
	}

	private static String getUrlPathFromMethod(Method currentMethod)
	{

		Boolean isPathVariable = Arrays.stream(currentMethod.getParameters()).anyMatch(p -> p.getAnnotation(PathVariable.class) != null);
		RequestMapping requestMapping = currentMethod.getAnnotation(RequestMapping.class);
		if (requestMapping != null)
		{
			return extractArraysToString(requestMapping.value(), isPathVariable);
		}

		PostMapping postMapping = currentMethod.getAnnotation(PostMapping.class);
		if (postMapping != null)
		{
			return extractArraysToString(postMapping.value(), isPathVariable);
		}

		DeleteMapping deleteMapping = currentMethod.getAnnotation(DeleteMapping.class);
		if (deleteMapping != null)
		{
			return extractArraysToString(deleteMapping.value(), isPathVariable);
		}

		PutMapping putMapping = currentMethod.getAnnotation(PutMapping.class);
		if (putMapping != null)
		{
			return extractArraysToString(putMapping.value(), isPathVariable);
		}

		GetMapping getMapping = currentMethod.getAnnotation(GetMapping.class);
		if (getMapping != null)
		{
			return extractArraysToString(getMapping.value(), isPathVariable);
		}

		return null;
	}

	private static String extractArraysToString(String[] str, Boolean isPathVariable)
	{
		String value = String.join("", str);
		if (isPathVariable)
		{
			Matcher matcher = Pattern.compile("/\\{(.*?)\\}").matcher(value);
			if (matcher.find())
			{
				return matcher.replaceAll("");
			}
		}
		return value;
	}

	public static Boolean isTextRequestLoggable(String uri)
	{
		return textLoggableMethodRequest.entrySet().stream().anyMatch(x -> ((x.getValue() && uri.startsWith(x.getKey())) || (!x.getValue() && x.getKey().equalsIgnoreCase(uri))));
	}

	public static Boolean isTextResponseLoggable(String uri)
	{
		return textLoggableMethodResponse.entrySet().stream().anyMatch(x -> ((x.getValue() && uri.startsWith(x.getKey())) || (!x.getValue() && x.getKey().equalsIgnoreCase(uri))));
	}

	public static Boolean isDBRequestLoggable(String uri)
	{
		return dbLoggableMethodRequest.entrySet().stream().anyMatch(x -> ((x.getValue() && uri.startsWith(x.getKey())) || (!x.getValue() && x.getKey().equalsIgnoreCase(uri))));
	}

	public static Boolean isDBResponseLoggable(String uri)
	{
		return dbLoggableMethodResponse.entrySet().stream().anyMatch(x -> ((x.getValue() && uri.startsWith(x.getKey())) || (!x.getValue() && x.getKey().equalsIgnoreCase(uri))));
	}

	public static String pathVariableBaseUrl(String uri)
	{
		return pathVariableMethod.stream().filter(uri::startsWith).findFirst().orElse(null);
	}
}
