package tr.com.poc.temporaldate.core.model.versioned;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tr.com.poc.temporaldate.core.model.temporal.BaseTemporalEntity;

/**
 * For versioned (indicated by effective date) entities 
 * 
 * @author umut
 */
@SuppressWarnings("serial")
@NoArgsConstructor 
@AllArgsConstructor 
@Getter 
@Setter
@MappedSuperclass
public abstract class BaseVersionedEntity extends BaseTemporalEntity
{		
	@Column(name = "EFFECTIVE_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date effectiveDate;	
}
