package tr.com.poc.temporaldate.core.model;

import java.io.Serializable;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * A marker interface indicating a Data Transfer Object
 * 
 * @author umutaskin
 */
@JacksonXmlRootElement
public interface BaseDTO extends Serializable  
{}
