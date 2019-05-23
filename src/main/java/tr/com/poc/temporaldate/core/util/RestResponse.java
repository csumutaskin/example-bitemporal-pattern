package tr.com.poc.temporaldate.core.util;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tr.com.poc.temporaldate.core.model.BaseDTO;

@SuppressWarnings("serial")
@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
@AllArgsConstructor
@Getter
@Setter
@ToString
public class RestResponse<T extends BaseDTO> implements Serializable
{	
	private String status;
	private String transactionId;	
	private String hostName;
	private String clientIp;
	private String userName;
	private String errorCode;
	private List<String> errorMessages;	
	private T body;
}
