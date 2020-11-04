
package de.dth.mdr.validator.validators;

import de.dth.mdr.validator.exception.ValidatorException;
import de.samply.common.mdrclient.domain.Validations;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validates MDRFaces input fields based on regex validation data from the MDR.
 */
public class MdrRegexValidator extends Validator {

  @Override
  public final boolean validate(Validations dataElementValidations, final Object value)
      throws ValidatorException {

    if (value == null || value.toString().equals("")) {
      return true;
    }

    String regex = dataElementValidations.getValidationData();
    if (!regex.isEmpty()) {
      Pattern pattern;

      if (isCaseSensitive()) {
        pattern = Pattern.compile(regex);
      } else {
        pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
      }

      Matcher matcher = pattern.matcher(value.toString());

      return matcher.matches();
    }

    return true;
  }
}
