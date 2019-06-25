package tr.com.poc.temporaldate.core.model.bitemporal;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tr.com.poc.temporaldate.core.model.BaseEntity;

/**
 * Super class for objects that have bitemporal data structure   
 * 
 * @author umutaskin
 */
@SuppressWarnings("serial")
@NoArgsConstructor 
@AllArgsConstructor 
@Getter 
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseBitemporalEntity implements BaseEntity 
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
	private Date createDate;//this is the record date
		
	@Column(name = "MODIFY_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	@LastModifiedDate	
	private Date modifyDate;
	
	@Column(name = "IS_DELETED")
	private Boolean isDeleted;
	
	@Column(name = "EFFECTIVE_DATE_START")
	@Temporal(TemporalType.TIMESTAMP)	
	private Date effectiveDateStart;
	
	@Column(name = "EFFECTIVE_DATE_END")
	@Temporal(TemporalType.TIMESTAMP)	
	private Date effectiveDateEnd;
	
	@Column(name = "RECORD_DATE_START")
	@Temporal(TemporalType.TIMESTAMP)	
	private Date recordDateStart;
	
	@Column(name = "RECORD_DATE_END")
	@Temporal(TemporalType.TIMESTAMP)	
	private Date recordDateEnd;
	
	@Version
	private int version;
}
