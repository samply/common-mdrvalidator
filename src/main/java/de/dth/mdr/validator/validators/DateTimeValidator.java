
package de.dth.mdr.validator.validators;

import de.dth.mdr.validator.exception.ValidatorException;
import de.dth.mdr.validator.formats.DateTimeFormats;
import de.samply.common.mdrclient.domain.Validations;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validator for date and time inputs.
 */
public class DateTimeValidator extends Validator {

  private static final Logger logger = LoggerFactory.getLogger(DateTimeValidator.class);

  @Override
  public boolean validate(Validations dataElementValidations, final Object value)
      throws ValidatorException {
    // get the enum date and time format from the mdr data element
    DateTimeFormats dateTimeFormats = DateTimeFormats
        .getDateTimeFormats(dataElementValidations.getValidationData());

    if (value == null || value.toString().equals("")) {
      return true;
    }
    // translate it to a pattern
    String dateTimePattern = DateTimeFormats.getDateTimePattern(dateTimeFormats.getDateFormat(),
        dateTimeFormats.getTimeFormat());
    SimpleDateFormat sdf = new SimpleDateFormat(dateTimePattern);
    sdf.setLenient(false);

    try {
      // if not valid, it will throw ParseException
      sdf.parse(value.toString());
      return true;
    } catch (ParseException e) {
      logger.debug(e.getMessage());
    }

    return false;
  }

}
