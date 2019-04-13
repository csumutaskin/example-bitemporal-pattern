package tr.com.poc.temporaldate.core.model.temporal;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tr.com.poc.temporaldate.core.model.BaseDTO;

/**
 * An extended Data Transfer Object class that contains temporal date data
 * 
 * @author umut
 */
@SuppressWarnings("serial")
@XmlRootElement
@Getter
@Setter
@NoArgsConstructor 
public class BaseTemporalDTO implements BaseDTO
{
	private Date effectiveDateStart;
	private Date effectiveDateEnd;
}