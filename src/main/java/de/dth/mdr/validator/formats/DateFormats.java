
package de.dth.mdr.validator.formats;

import de.dth.mdr.validator.enums.EnumDateFormat;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateFormats {

  /**
   * Get the date format pattern from the MDR date format description that the date/time picker can
   * understand.
   *
   * @param enumDateFormat the date format as known in MDR
   * @return the date format pattern string representation that the date/time picker can understand
   */
  public static String getDatepickerPattern(final EnumDateFormat enumDateFormat) {
    String datePattern = getDatePattern(enumDateFormat);
    return datePattern.toUpperCase();
  }

  /**
   * Get the date format pattern from the MDR date format description.
   *
   * @param enumDateFormat the date format as known in MDR
   * @return the date format pattern string representation
   */
  public static String getDatePattern(final EnumDateFormat enumDateFormat) {
    DateFormat formatter;
    switch (enumDateFormat) {
      case DIN_5008:
        formatter = new SimpleDateFormat("MM.yyyy");
        break;
      case DIN_5008_WITH_DAYS:
        formatter = new SimpleDateFormat("dd.MM.yyyy");
        break;
      case DIN_5008_ONLY_YEAR:
        formatter = new SimpleDateFormat("yyyy");
        break;
      case ISO_8601:
        formatter = new SimpleDateFormat("yyyy-MM");
        break;
      case ISO_8601_WITH_DAYS:
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        break;
      case LOCAL_DATE: // not valid
      case LOCAL_DATE_WITH_DAYS:
      default:
        formatter = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        break;
    }
    return ((SimpleDateFormat) formatter).toPattern();
  }
}
