package tr.com.poc.temporaldate.bitemporalexample.model;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.JoinFormula;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tr.com.poc.temporaldate.core.model.bitemporal.BaseBitemporalEntity;

@SuppressWarnings("serial")
@Table(name = "USER")
@Entity
//@AllArgsConstructor 
@NoArgsConstructor 
@Getter 
@Setter 
//@Builder
@ToString(callSuper=true, includeFieldNames=true)
@Where(clause = "IS_DELETED = 'FALSE'" )
@SQLDelete(sql = "UPDATE USER SET IS_DELETED = 'TRUE' WHERE id = ?", check = ResultCheckStyle.COUNT)
public class User extends BaseBitemporalEntity
{
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name = "NAME")
	private String name;
	
	@Column(name = "ORG_ID")
	private Long orgId;
	
//	@Basic(fetch = FetchType.LAZY)
//	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional=false)
//	@JoinFormula(
//	value="(SELECT B.id FROM BT_ORGANIZATION B WHERE now() > B.PERSPECTIVE_DATE_START AND now() < B.PERSPECTIVE_DATE_END AND now() > B.EFFECTIVE_DATE_START AND now() < B.EFFECTIVE_DATE_END AND B.ORG_ID = ORG_ID)",
//	referencedColumnName="id")	
//	private BitemporalOrganization organization;	
	
	@Transient
	private BitemporalOrganization organization;

	public User(User user, BitemporalOrganization organization) {
		super();
		this.id = user.getId();
		this.name = user.getName();
		this.orgId = user.getOrgId();
		this.organization = organization;
	}
	
	
	
}
