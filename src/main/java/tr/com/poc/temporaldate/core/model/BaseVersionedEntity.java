package tr.com.poc.temporaldate.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@NoArgsConstructor 
@AllArgsConstructor 
@Getter 
@Setter
@MappedSuperclass
public abstract class BaseVersionedEntity extends BaseTemporalEntity
{	
	@Version
	@Column(name = "VERSION")
	@Temporal(TemporalType.TIMESTAMP)
	private Date finalDate;
}
