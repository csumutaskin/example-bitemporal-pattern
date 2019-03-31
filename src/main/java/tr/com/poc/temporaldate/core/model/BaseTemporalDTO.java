package tr.com.poc.temporaldate.core.model;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * An extended Data Transfer Object class that contains temporal date data
 * 
 * @author umut
 */
@SuppressWarnings("serial")
@XmlRootElement
@Getter
@Setter
@NoArgsConstructor 
public class BaseTemporalDTO implements BaseDTO
{}