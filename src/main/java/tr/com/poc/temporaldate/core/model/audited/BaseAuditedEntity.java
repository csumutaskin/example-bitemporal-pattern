package tr.com.poc.temporaldate.core.model.audited;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import tr.com.poc.temporaldate.core.model.BaseEntity;

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
@EntityListeners(AuditingEntityListener.class)
public class BaseAuditedEntity implements BaseEntity
{

	@Column(name = "CREATE_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	@CreatedDate
	private Date createDate;

	@Column(name = "CREATE_USER")
	@CreatedBy
	private String createUser;

	@Column(name = "IP")
	private String clientIp;

}
