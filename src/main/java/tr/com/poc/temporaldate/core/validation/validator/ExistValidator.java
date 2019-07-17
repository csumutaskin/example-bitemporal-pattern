package tr.com.poc.temporaldate.core.validation.validator;

import java.util.Arrays;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import tr.com.poc.temporaldate.core.validation.ValidAspectInformer;

/**
 * Base BussinesValidator class for annotation type validations. Validation checker classes should extend this base class as a base utility helper functionality class.
 * 
 * @author TTKASKIN
 *
 */
//@formatter:off
public class ExistValidator 
{
	/**
	 * Checks whether the argument given contains the current request mapping.
	 * @param restList List of rest services written in "/pathObject/pathFunction" format
	 * @return true if the current rest is in "to be validated" list, false otherwise.
	 */
	protected boolean isCurrentToBeValidated(String [] methods)
	{
		if(methods != null && methods.length > 0)
		{
			String currentPath = ValidAspectInformer.getInstance().getPath();
			for(String eachValidationRange:Arrays.asList(methods))
			{
			    	//current path null ise @Valid'le daha karsilasilmadan @Constraintlerle karsilasilmis demektir.. o yuzden @ valid olmadigindan constraintlere takilmasin, gecsin  
				if(StringUtils.isNotBlank(currentPath) && (currentPath.equals(eachValidationRange) || currentPath.endsWith("."+eachValidationRange) || CollectionUtils.isEmpty(Arrays.asList(methods)))) // a package prefixed class.method should exactly match or .class.method should match (Ambiguity of class names is user responsibility)
				{
					return true;
				}
			}
			return false;
		}
		else//pass method check since array is null
		{
		    String currentPath = ValidAspectInformer.getInstance().getPath();
		    if(StringUtils.isNotBlank(currentPath))//@Validle karsilasilmis valide olsun
		    {
			return true;
		    }
		    return false;
		}
		
	}
}
