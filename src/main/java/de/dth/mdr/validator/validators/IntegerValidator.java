
package de.dth.mdr.validator.validators;

import de.dth.mdr.validator.exception.ValidatorException;
import de.samply.common.mdrclient.domain.Validations;

/**
 * Validator for integers.
 */
public class IntegerValidator extends Validator {

  @Override
  public final boolean validate(Validations dataElementValidations, final Object value)
      throws ValidatorException {
    if (value == null || value.toString().equals("")) {
      return true;
    }

    String checkMe = value.toString();
    if (getUnitOfMeasure() != null) {
      checkMe = cutUnitOfMeasure(checkMe);
    }

    try {
      Integer.parseInt(checkMe);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }
}
