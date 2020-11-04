
package de.dth.mdr.validator.validators;

import de.dth.mdr.validator.exception.ValidatorException;
import de.samply.common.mdrclient.domain.Validations;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validator for email addresses.
 */
public class EmailValidator extends Validator {

  /**
   * Regular expression that defines an email.
   */
  static final String EMAIL_REGEX = "^\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}\\b$";

  /**
   * Pattern from the email regular expression.
   */
  private static Pattern pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);

  @Override
  public final boolean validate(Validations dataElementValidations, final Object value)
      throws ValidatorException {

    if (value == null || value.toString().equals("")) {
      return true;
    }

    Matcher matcher = pattern.matcher(value.toString());

    return matcher.matches();

  }
}
