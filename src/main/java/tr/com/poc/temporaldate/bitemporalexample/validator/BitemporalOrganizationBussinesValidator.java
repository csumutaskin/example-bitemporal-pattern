package tr.com.poc.temporaldate.bitemporalexample.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tr.com.poc.temporaldate.bitemporalexample.dao.BitemporalOrganizationDao;
import tr.com.poc.temporaldate.bitemporalexample.dto.bitemporalorganization.BitemporalOrganizationSaveOrUpdateRequestDTO;
import tr.com.poc.temporaldate.core.exception.BusinessValidationException;
import tr.com.poc.temporaldate.core.validation.BussinesValidator;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class BitemporalOrganizationBussinesValidator implements BussinesValidator<BitemporalOrganizationSaveOrUpdateRequestDTO>
{
	@Autowired
	private BitemporalOrganizationDao bitemporalOrganizationDao;

	@Override
	public void valid(BitemporalOrganizationSaveOrUpdateRequestDTO dto)
	{
		if (dto.getEffectiveDateStart().compareTo(dto.getEffectiveDateEnd()) > 0)
		{
			new BusinessValidationException("10001").throwFinally();
		}
	}
}
