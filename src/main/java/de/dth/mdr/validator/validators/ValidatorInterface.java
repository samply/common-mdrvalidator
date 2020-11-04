package de.dth.mdr.validator.validators;

import de.dth.mdr.validator.exception.ValidatorException;
import de.samply.common.mdrclient.domain.Validations;

public interface ValidatorInterface {

  boolean validate(Validations dataElementValidations, Object value) throws ValidatorException;

}
