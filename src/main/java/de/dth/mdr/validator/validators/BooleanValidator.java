
package de.dth.mdr.validator.validators;

import de.dth.mdr.validator.exception.ValidatorException;
import de.samply.common.mdrclient.domain.Validations;

/**
 * Validator for booleans.
 */
public class BooleanValidator extends Validator {

  @Override
  public final boolean validate(Validations dataElementValidations, final Object value)
      throws ValidatorException {
    if (value == null || value.toString().equals("")) {
      return true;
    }

    return ("true".equalsIgnoreCase(value.toString()) || "false"
        .equalsIgnoreCase(value.toString()));
  }
}
