package tr.com.poc.temporaldate.core.aspect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;

import lombok.extern.log4j.Log4j2;

@Aspect
@Order
@Log4j2
public class AspectDebugLogger
{

	public static final String NO_PARAM_LABEL = "()";
	public static final String VOID_LABEL = "(void)";

	@Around("execution(public * tr.com.poc.temporaldate.bitemporalexample..*.*(..))")
	public Object logMethods(ProceedingJoinPoint pjp) throws Throwable
	{
		long start = System.currentTimeMillis();
		MethodSignature signature = (MethodSignature) pjp.getSignature();
		log.debug("{}.{}({})", signature.getDeclaringType()::getSimpleName, signature.getMethod()::getName, () -> getInputLogWRTLogDetailedInputAnnotation(pjp));
		Object returnValue = pjp.proceed();
		long totalCost = TimeUnit.SECONDS.convert(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
		log.debug("{}.{}({}) > Cost: {}", signature.getDeclaringType()::getSimpleName, signature.getMethod()::getName, () -> getOutputLogWRTLogDetailedOutputAnnotation(returnValue), () -> totalCost);
		return returnValue;
	}

	/*
	 * Herhangi bir methodun girdi parametre setini methodun ya da sinifinin basinda LogDetail annotationu bulunup bulunmamasina gore stringlestirir ve doner. Eger methodun basinda detay log annotation u yoksa (...)si donecektir.
	 */
	private String getInputLogWRTLogDetailedInputAnnotation(ProceedingJoinPoint pjp)
	{
		List<String> args = stringifyMethodInputsInList(pjp.getArgs());
		return args.isEmpty() ? NO_PARAM_LABEL : "(" + args.toString() + ")";
	}

	/*
	 * Herhangi bir methodun return objesini methodun ya da sinifinin basinda LogDetail annotationu bulunup bulunmamasina gore stringlestirir ve doner. Eger methodun basinda detay log annotation u yoksa (...)si donecektir.
	 */
	private String getOutputLogWRTLogDetailedOutputAnnotation(Object returnValue)
	{
		return returnValue == null ? VOID_LABEL : "(" + returnValue.toString() + ")";
	}

	// Object arrayi olarak verilen objeleri String ciktisi olarak birbirine append eder
	private List<String> stringifyMethodInputsInList(Object[] args)
	{
		ArrayList<String> toReturn = new ArrayList<>();
		for (Object argElements : args)
		{
			if (argElements instanceof Object[])
			{
				toReturn.add(Arrays.toString((Object[]) argElements));
			}
			else
			{
				toReturn.add(String.valueOf(argElements));
			}
		}
		return toReturn;
	}

}
