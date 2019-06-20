package tr.com.poc.temporaldate.core.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.common.ExceptionConstants;
import tr.com.poc.temporaldate.core.exception.ApplicationException;

/**
 * Random UUID generator utility class
 * 
 * @author umutaskin
 *
 */
@Log4j2
public class RandomGenerator
{
	private Random random;
			
    private RandomGenerator() 
    {
    	try 
		{
			random = SecureRandom.getInstanceStrong();
		} 
		catch (NoSuchAlgorithmException e) 
		{		
			log.error("Error initializing a random object in singleton RandomGenerator class initialization");
			throw new ApplicationException(ExceptionConstants.SERVER_STARTUP_EXCEPTION, e);			
		}
    }

    /**
     * Returns Singleton Random Generator instance
     * @return {@link RandomGenerator}
     */
    public static RandomGenerator getInstance()
    {
    	return RandomGeneratorHolder.INSTANCE;
    }
    
    /**
     * Returns a random UUID instance
     * @return {@link UUID}
     */
    public UUID getRandomUUID()
    {
    	return UUID.randomUUID();
    }
    
    /**
     * Returns a random UUID String
     * @return {@link String} Random UUID 
     */
    public String getRandomUUIDString()
    {
    	return UUID.randomUUID().toString();
    }
    
    /**
     * Returns a random integer 
     * @param maxValue maximum value exclusive (if a 0 or negative number is entered, Integer.MaxValue will be taken as the boundary)
     * @return a random integer value
     */
    public int getRandomInteger(int maxValue)
    {
       	if(maxValue <= 0)
    	{
    		maxValue = Integer.MAX_VALUE;
    	}
       	return random.nextInt(maxValue);
    }
    
    private static class RandomGeneratorHolder 
    {
    	private static final RandomGenerator INSTANCE = new RandomGenerator();
    }
}
