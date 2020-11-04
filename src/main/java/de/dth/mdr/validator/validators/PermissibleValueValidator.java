
package de.dth.mdr.validator.validators;

import de.dth.mdr.validator.exception.ValidatorException;
import de.samply.common.mdrclient.domain.PermissibleValue;
import de.samply.common.mdrclient.domain.Validations;

/**
 * Validator for permissible values.
 */
public class PermissibleValueValidator extends Validator {

  @Override
  public final boolean validate(Validations dataElementValidations, final Object value)
      throws ValidatorException {

    if (value == null || value.toString().equals("")) {
      return true;
    }

    // no permissible values. should not happen, but if so, the element is always valid
    if (dataElementValidations.getPermissibleValues().size() <= 0) {
      return true;
    }

    if (isCaseSensitive()) {
      return validateCaseSensitive(dataElementValidations, value);
    } else {
      return validateIgnoreCase(dataElementValidations, value);
    }
  }

  private boolean validateCaseSensitive(Validations dataElementValidations, final Object value) {
    for (PermissibleValue permissibleValue : dataElementValidations.getPermissibleValues()) {
      if (permissibleValue.getValue().equals(value.toString())) {
        return true;
      }
    }

    return false;
  }

  private boolean validateIgnoreCase(Validations dataElementValidations, final Object value) {
    for (PermissibleValue permissibleValue : dataElementValidations.getPermissibleValues()) {
      if (permissibleValue.getValue().equalsIgnoreCase(value.toString())) {
        return true;
      }
    }

    return false;
  }
}
