package tr.com.poc.temporaldate.core.util.response;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tr.com.poc.temporaldate.core.model.BaseDTO;

/**
 * A template response for the Rest End points that include necessary information about a particular request
 * 
 * @author umutaskin
 *
 * @param <T> any {@link BaseDTO} object that can be a potential rest response body
 */
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
	private String errorMessage;
	   
    //@XmlAnyElement(lax = true)
	private T body;
}
