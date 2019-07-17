package tr.com.poc.temporaldate.core.validation;

import tr.com.poc.temporaldate.core.model.BaseDTO;

public interface BussinesValidator<E extends BaseDTO>
{
	void valid(E dto);
}
