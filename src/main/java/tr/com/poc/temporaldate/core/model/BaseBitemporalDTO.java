package tr.com.poc.temporaldate.core.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@XmlRootElement
@Getter
@Setter
@NoArgsConstructor 
@AllArgsConstructor 
public class BaseBitemporalDTO implements BaseDTO
{
	private Date effectiveDateStart;
	private Date effectiveDateEnd;
}
