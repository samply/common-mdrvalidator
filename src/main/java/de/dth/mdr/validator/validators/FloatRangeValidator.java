
package de.dth.mdr.validator.validators;

import de.dth.mdr.validator.exception.ValidatorException;
import de.samply.common.mdrclient.domain.EnumValidationType;
import de.samply.common.mdrclient.domain.Validations;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validator for float numbers between a maximum and a minimum value.
 */
public class FloatRangeValidator extends Validator {

  /**
   * A float range regular expression, as it is known in the MDR.
   */
  static final String FLOAT_RANGE_REGEX = "(?:(.+)<=)?x(?:<=(.+))?";

  @Override
  public final boolean validate(Validations dataElementValidations, final Object value)
      throws ValidatorException {

    if (value == null || value.toString().equals("")) {
      return true;
    }

    boolean retValue = false;

    try {
      if (dataElementValidations.getValidationType().equals(EnumValidationType.FLOATRANGE.name())) {
        // check if it is a valid range
        Pattern pattern = Pattern.compile(FLOAT_RANGE_REGEX);
        Matcher matcher = pattern.matcher(dataElementValidations.getValidationData());

        if (matcher.find()) {
          String min = matcher.group(1);
          String max = matcher.group(2);

          Float minFloat = null;
          if (min != null) {
            minFloat = Float.valueOf(min);
          }
          Float maxFloat = null;
          if (max != null) {
            maxFloat = Float.valueOf(max);
          }

          String checkMe = value.toString();
          if (getUnitOfMeasure() != null) {
            checkMe = cutUnitOfMeasure(checkMe);
          }

          Float floatValue = Float.parseFloat(String.valueOf(checkMe));

          if (minFloat != null && floatValue < minFloat
              || maxFloat != null && floatValue > maxFloat) {
            retValue = false;
          } else {
            retValue = true;
          }
        }
      }
    } catch (NumberFormatException e) {
      retValue = false;
    }

    return retValue;
  }
}
