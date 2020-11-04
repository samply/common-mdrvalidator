package de.dth.mdr.validator.validators;

import de.dth.mdr.validator.exception.ValidatorException;
import de.samply.common.mdrclient.domain.Validations;

/**
 * Validator for String Dataelements. Currently only checks if max length is not exceeded.
 */
public class StringValidator extends Validator {

  @Override
  public boolean validate(Validations dataElementValidations, Object value)
      throws ValidatorException {
    if (value == null) {
      return true;
    }

    if (value.getClass() != String.class) {
      return false;
    }
    int maxChars;
    try {
      maxChars = Integer.parseInt(dataElementValidations.getMaximumCharacterQuantity());
    } catch (NumberFormatException e) {
      throw new ValidatorException(e);
    }
    // If no limit is set, this is stored as max quantity of 0 in the mdr
    return maxChars == 0 || ((String) value).length() <= maxChars;

  }
}
