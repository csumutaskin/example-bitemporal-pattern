package tr.com.poc.temporaldate.bitemporalexample.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tr.com.poc.temporaldate.core.model.audited.BaseAuditedEntity;

@SuppressWarnings("serial")
@Table(name = "REST_SERVER_LOG")
@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestServerLog extends BaseAuditedEntity
{

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "VURL")
	private String url;

	@Column(name = "VPATH_VARIABLE")
	private String pathvariable;

	@Column(name = "VTRANSACION_NO")
	private String trxn;

	@Column(name = "FREQUEST")
	@Lob
	private byte[] request;

	@Column(name = "FRESPONSE")
	@Lob
	private byte[] response;

	@Column(name = "VRESPONSE_CODE")
	private String responseCode;

}
