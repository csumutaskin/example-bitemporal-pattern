package tr.com.poc.temporaldate.core.model.versioned;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tr.com.poc.temporaldate.core.model.BaseDTO;

/**
 * Super class for objects that only carry version (effective Date) information 
 * Subclasses of this class do not have an interval which is indicated by effective date begin and effective date end
 * only effective date is used and -can be considered as version- 
 * e.g. daily tuples can be versioned by dates whose hour part is truncated on effective date column.  
 * 
 * @author umut
 */
@SuppressWarnings("serial")
@XmlRootElement
@Getter
@Setter
@NoArgsConstructor 
public class BaseVersionedDTO implements BaseDTO
{
	private Date effectiveDate;
}
