package tr.com.poc.temporaldate.bitemporalexample.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.NaturalIdCache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tr.com.poc.temporaldate.core.dao.annotation.Pid;
import tr.com.poc.temporaldate.core.model.bitemporal.BaseBitemporalEntity;

@SuppressWarnings("serial")
@Table(name = "BT_ORGANIZATION")
@Entity
@AllArgsConstructor 
@NoArgsConstructor 
@Getter 
@Setter 
@ToString 
@Builder
@NaturalIdCache
public class BitemporalOrganization extends BaseBitemporalEntity
{
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private BigDecimal id;
	
	@Column(name = "NAME")
	private String name;
	
	@Column(name = "SHORT_NAME")
	@Pid
	private String shortName;
	
	@Column(name = "FINE_AMOUNT")
	private double fineAmount;
	
	@Column(name = "EARN_AMOUNT")
	private double earnAmount;
}
