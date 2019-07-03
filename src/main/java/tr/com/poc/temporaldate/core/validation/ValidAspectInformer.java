package tr.com.poc.temporaldate.core.validation;

/**
 * Thread local service class' path informer for @valid checks
 * 
 * @author UMUT
 *
 */
public class ValidAspectInformer
{
	private static final ValidAspectInformer INSTANCE = new ValidAspectInformer();
	private ThreadLocal<String> path = new ThreadLocal<>();

	private ValidAspectInformer()
	{
	}

	public static ValidAspectInformer getInstance()
	{
		return INSTANCE;
	}

	public String getPath()
	{
		return path.get();
	}

	public void setPath(String pathStr)
	{
		this.path.set(pathStr);
	}

	public void remove()
	{
		path.remove();
	}
}
