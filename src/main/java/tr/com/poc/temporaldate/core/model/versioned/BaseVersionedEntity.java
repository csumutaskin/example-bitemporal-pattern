package tr.com.poc.temporaldate.core.model.versioned;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

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
	@Column(name = "CREATE_USER")
	@CreatedBy
	private String createUser; 
	
	@Column(name = "IP")
	private String clientIp;

	@Column(name = "MODIFY_USER")
	@LastModifiedBy
	private String modifyUser;
	
	@Column(name = "CREATE_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	@CreatedDate	
	private Date createDate;
	
	@Column(name = "MODIFY_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	@LastModifiedDate	
	private Date modifyDate;
	
	@Column(name = "EFFECTIVE_DATE")
	@Temporal(TemporalType.TIMESTAMP)	
	private Date effectiveDate;
	
	@Version
	private Integer version;
	
	@Column
	private Boolean isLastVersion;
	
	@Column
	private Boolean isFirstVersion; 

}

