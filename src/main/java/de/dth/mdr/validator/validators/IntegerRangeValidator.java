
package de.dth.mdr.validator.validators;

import de.dth.mdr.validator.exception.ValidatorException;
import de.samply.common.mdrclient.domain.Validations;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validator for integers between a maximum and a minimum value.
 */
public class IntegerRangeValidator extends Validator {

  @Override
  public final boolean validate(Validations dataElementValidations, final Object value)
      throws ValidatorException {

    if (value == null || value.toString().equals("")) {
      return true;
    }

    try {
      // check if it is a valid range - range definition is the same for floats and integers
      Pattern pattern = Pattern.compile(FloatRangeValidator.FLOAT_RANGE_REGEX);
      Matcher matcher = pattern.matcher(dataElementValidations.getValidationData());

      if (matcher.find()) {
        String min = matcher.group(1);
        String max = matcher.group(2);

        Integer minInteger = null;
        if (min != null) {
          minInteger = Integer.valueOf(min);
        }
        Integer maxInteger = null;
        if (max != null) {
          maxInteger = Integer.valueOf(max);
        }

        String checkMe = value.toString();
        if (getUnitOfMeasure() != null) {
          checkMe = cutUnitOfMeasure(checkMe);
        }

        Integer integerValue = Integer.valueOf(String.valueOf(checkMe));

        if (minInteger != null && integerValue < minInteger || maxInteger != null
            && integerValue > maxInteger) {
          return false;
        }
      }
    } catch (NumberFormatException e) {
      return false;
    }

    return true;
  }
}
