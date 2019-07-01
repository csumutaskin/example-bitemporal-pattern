package tr.com.poc.temporaldate.core.model.temporal;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tr.com.poc.temporaldate.core.model.BaseDTO;

/**
 * An extended Data Transfer Object class that contains temporal date data
 * 
 * @author umutaskin
 */
@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor 
public class BaseTemporalDTO implements BaseDTO
{
	private Date effectiveDateStart;
	private Date effectiveDateEnd;
}