package tr.com.poc.temporaldate.core.model.bitemporal;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
@ToString(callSuper=true, includeFieldNames=true)
//@FilterDef (name = "notDeletedTuplesFilter", parameters = @ParamDef(name = "isDeleted", type = "boolean"))
//@Filter(name="notDeletedTuplesFilter", condition="IS_DELETED = :isDeleted")
public class BaseBitemporalEntity implements BaseEntity 
{
	@Column(name = "CREATE_USER")
	@CreatedBy
	private String createUser; 
		
	@Column(name = "CLIENT_IP")
	private String clientIp;
	
	@Column(name = "HOST")
	private String host;

	@Column(name = "MODIFY_USER")
	@LastModifiedBy
	private String modifyUser;
		
	@Column(name = "CREATE_DATE")
	@CreatedDate	
	private LocalDateTime createDate;
		
	@Column(name = "MODIFY_DATE")
	@LastModifiedDate	
	private LocalDateTime modifyDate;
	
	@Column(name = "IS_DELETED")
	private Boolean isDeleted;
	
	@Column(name = "EFFECTIVE_DATE_START")
	private LocalDateTime effectiveDateStart;
	
	@Column(name = "EFFECTIVE_DATE_END")
	private LocalDateTime effectiveDateEnd;
	
	@Column(name = "PERSPECTIVE_DATE_START")
	private LocalDateTime perspectiveDateStart;
	
	@Column(name = "PERSPECTIVE_DATE_END")
	private LocalDateTime perspectiveDateEnd;		
	
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}
}
