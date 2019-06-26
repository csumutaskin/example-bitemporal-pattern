package tr.com.poc.temporaldate.core.util.response;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.logging.log4j.ThreadContext;

import lombok.Getter;
import lombok.ToString;
import tr.com.poc.temporaldate.common.Constants;
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
@Getter
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
	   
    @XmlAnyElement(lax = true)
	//private T body;
    private RestResponseBody<T> body;
	
	private RestResponse()
	{}
		
	public static class Builder<T extends BaseDTO>
	{
		private String status;
		private String transactionId;	
		private String hostName;
		private String clientIp;
		private String userName;
		private String errorCode;
		private String errorMessage;
		//private T body;
		private RestResponseBody<T> body;

		public Builder(String status, String transactionId)
		{
			this.status = status;
			this.transactionId = transactionId;
		}
		
		public Builder(String status)
		{
			this.status = status;
			this.transactionId = ThreadContext.get(Constants.MDC_TRANSACTION_NO);
		}
		
		public Builder<T> withHostName(String hostName)
		{
			this.hostName = hostName;
			return this;
		}
		
		public Builder<T> withClientIp(String clientIp)
		{
			this.clientIp = clientIp;
			return this;
		}
		
		public Builder<T> withUserName(String userName)
		{
			this.userName = userName;
			return this;
		}
		
		public Builder<T> withErrorCode(String errorCode)
		{
			this.errorCode = errorCode;
			return this;
		}
		
		public Builder<T> withErrorMessage(String errorMessage)
		{
			this.errorMessage = errorMessage;
			return this;
		}
		
		public Builder<T> withBody(T body)
		{
			//this.body = body;
			this.body = new RestResponseBody<>(body);
			return this;
		}
		
		public Builder<T> withBodyList(List<T> bodyList)
		{
			//this.body = body;
			this.body = new RestResponseBody<>(bodyList);
			return this;
		}
		
		public RestResponse<T> build()
		{
			RestResponse<T> restResponseBuilt = new RestResponse<>();
			restResponseBuilt.status = status;
			restResponseBuilt.transactionId = transactionId;
			restResponseBuilt.hostName = hostName;
			restResponseBuilt.clientIp = clientIp;
			restResponseBuilt.userName = userName;
			restResponseBuilt.errorCode = errorCode;
			restResponseBuilt.errorMessage = errorMessage;
			restResponseBuilt.body = body;
			return restResponseBuilt;
		}
	}
}
