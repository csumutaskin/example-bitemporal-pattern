package tr.com.poc.temporaldate.core.util;

import java.util.Random;
import java.util.UUID;

/**
 * Random UUID generator utility class
 * 
 * @author umutaskin
 *
 */
public class RandomGenerator 
{
    private RandomGenerator() 
    {}

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
    	Random rand = new Random();
    	if(maxValue <= 0)
    	{
    		maxValue = Integer.MAX_VALUE;
    	}
       	return rand.nextInt(maxValue);
    }
    
    /**
     * Returns a random integer in String while padding left with 0s. The string max length can be maxDigitNumber.
     * @param maxValue max exclusive value of the random integer. If 0 or negative Integer.maxvalue will be applied
     * @param maxDigitNumber Maximum length of the String with zeros. If generated integer digit number is more than maxDigitNumber no padding with 0s will be applied to the integer.
     * @return
     */
    public String getRandomIntegerLeftPadWithZeros(int maxValue, int maxDigitNumber)
    {
       	return String.format("%0" + maxDigitNumber + "d", getRandomInteger(maxValue));
    }    
    
    private static class RandomGeneratorHolder 
    {
    	private static RandomGenerator INSTANCE = new RandomGenerator();
    }
}
