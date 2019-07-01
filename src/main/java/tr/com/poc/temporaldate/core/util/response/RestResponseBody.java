package tr.com.poc.temporaldate.core.util.response;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Setter;
import tr.com.poc.temporaldate.core.model.BaseDTO;

/**
 * Custom rest response body that can contain both single item or list of items
 * 
 * @author umutaskin
 *
 * @param <T> any object that extends BaseDTO
 */
@Setter
//@Getter
@SuppressWarnings({"serial" })
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class RestResponseBody<T extends BaseDTO> implements Serializable
{
	@JsonIgnore
	private T body;

	@JsonIgnore
	private List<T> bodyCollection;
	
	@JsonProperty("content")
	public Object getContent()
	{
		if(body != null)
		{
			return body; 
		}
		return bodyCollection;
	}
	
	public RestResponseBody(T body)
	{
		this.body = body;
		this.bodyCollection = null;
	}
	
	public RestResponseBody(List<T> bodyCollection)
	{
		this.bodyCollection = bodyCollection;
		this.body = null;
	}		
}
