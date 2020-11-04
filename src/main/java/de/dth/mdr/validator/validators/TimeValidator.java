
package de.dth.mdr.validator.validators;

import de.dth.mdr.validator.enums.EnumTimeFormat;
import de.dth.mdr.validator.exception.ValidatorException;
import de.dth.mdr.validator.formats.TimeFormats;
import de.samply.common.mdrclient.domain.Validations;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Validator for time.
 */
public class TimeValidator extends Validator {

  @Override
  public final boolean validate(Validations dataElementValidations, final Object value)
      throws ValidatorException {
    if (value == null || value.toString().equals("")) {
      return true;
    }
    try {
      // get the enum date format from the mdr data element
      EnumTimeFormat enumTimeFormat = EnumTimeFormat
          .valueOfTrimmed(dataElementValidations.getValidationData());
      // translate it to a pattern
      String datePattern = TimeFormats.getTimePattern(enumTimeFormat);
      SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
      sdf.setLenient(false);

      try {
        // if not valid, it will throw ParseException
        sdf.parse(value.toString());
      } catch (ParseException e) {
        return false;
      }
    } catch (NumberFormatException e) {
      return false;
    }

    return true;
  }
}
