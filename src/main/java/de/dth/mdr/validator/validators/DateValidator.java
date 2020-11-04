
package de.dth.mdr.validator.validators;

import de.dth.mdr.validator.enums.EnumDateFormat;
import de.dth.mdr.validator.exception.ValidatorException;
import de.dth.mdr.validator.formats.DateFormats;
import de.samply.common.mdrclient.domain.EnumValidationType;
import de.samply.common.mdrclient.domain.Validations;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Validator for date inputs.
 */
public class DateValidator extends Validator {

  @Override
  public final boolean validate(Validations dataElementValidations, final Object value)
      throws ValidatorException {
    if (value == null || value.toString().equals("")) {
      return true;
    }

    if (dataElementValidations.getValidationType().equals(EnumValidationType.DATE.name())) {

      // get the enum date format from the mdr data element
      EnumDateFormat enumDateFormat = EnumDateFormat
          .valueOfTrimmed(dataElementValidations.getValidationData());
      // translate it to a pattern
      String datePattern = DateFormats.getDatePattern(enumDateFormat);
      SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
      sdf.setLenient(false);

      try {
        sdf.parse(value.toString());
        return true;
      } catch (ParseException e) {
        // not valid
      }
    }

    return false;
  }

}
