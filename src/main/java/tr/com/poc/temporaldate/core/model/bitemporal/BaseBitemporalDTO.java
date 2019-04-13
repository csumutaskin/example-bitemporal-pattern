package tr.com.poc.temporaldate.core.model.bitemporal;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tr.com.poc.temporaldate.core.model.BaseDTO;

/**
 * Base DTO for Bitemporal entities
 * "Record Dates" hold perspective time details whereas "effective Dates" mean in which interval those tuples are active.
 * 
 * @author umut
 */
@SuppressWarnings("serial")
@XmlRootElement
@Getter
@Setter
@NoArgsConstructor 
@AllArgsConstructor 
public class BaseBitemporalDTO implements BaseDTO
{
	private Date recordDateStart;
	private Date recordDateEnd;
	private Date effectiveDateStart;
	private Date effectiveDateEnd;
}
