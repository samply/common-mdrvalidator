
package de.dth.mdr.validator.validators;

import de.dth.mdr.validator.FlatCatalogue;
import de.dth.mdr.validator.exception.ValidatorException;
import de.samply.common.mdrclient.domain.Validations;

/**
 * Validator for a catalogue. It checks if the value is a valid code. If not, it will also check if
 * the value is a valid designation.
 */
public class CatalogueValidator extends Validator {

  private FlatCatalogue flatCatalogue;

  /**
   * Info if or not a value was validated by designation instead of code.
   */
  private Boolean wasValidatedDesignation = false;

  @Override
  public final boolean validate(Validations dataElementValidations, final Object value)
      throws ValidatorException {
    wasValidatedDesignation = false;

    if (value == null || value.toString().equals("")) {
      return true;
    }

    if (isCaseSensitive()) {
      return validateCaseSensitive(value);
    } else {
      return validateIgnoreCase(value);
    }

  }

  private boolean validateIgnoreCase(final Object value) {
    // check code
    for (String validCode : flatCatalogue.getValidCodes()) {
      // case sensitive
      if (validCode.equalsIgnoreCase(value.toString())) {
        return true;
      }
    }

    // check designation
    for (String validDesignation : flatCatalogue.getValidDesignationToCodes().keySet()) {
      // case sensitive
      if (validDesignation.equalsIgnoreCase(value.toString())) {
        return true;
      }
    }

    return false;
  }

  private boolean validateCaseSensitive(final Object value) {
    // check code
    if (flatCatalogue.getValidCodes().contains(value.toString())) {
      return true;
    }

    // check designation
    if (flatCatalogue.getValidDesignationToCodes().containsKey(value.toString())) {
      wasValidatedDesignation = true;
      return true;
    }

    return false;
  }

  public FlatCatalogue getFlatCatalogue() {
    return flatCatalogue;
  }

  public void setFlatCatalogue(FlatCatalogue flatCatalogue) {
    this.flatCatalogue = flatCatalogue;
  }

  public Boolean getWasValidatedDesignation() {
    return wasValidatedDesignation;
  }
}
