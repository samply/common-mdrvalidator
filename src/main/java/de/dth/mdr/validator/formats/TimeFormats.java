
package de.dth.mdr.validator.formats;

import de.dth.mdr.validator.enums.EnumTimeFormat;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class TimeFormats {

  /**
   * Get the date format pattern from the MDR date format description that the timepicker can
   * understand.
   *
   * @param enumTimeFormat the date format as known in MDR
   * @return the date format pattern string representation that the timepicker understands
   */
  public static String getTimepickerPattern(final EnumTimeFormat enumTimeFormat) {
    String datePattern = getTimePattern(enumTimeFormat);
    return datePattern.toUpperCase();
  }

  /**
   * Get the date format pattern from the MDR date format description.
   *
   * @param enumTimeFormat the date format as known in MDR
   * @return the date format pattern string representation
   */
  public static String getTimePattern(final EnumTimeFormat enumTimeFormat) {
    DateFormat formatter;
    switch (enumTimeFormat) {
      case HOURS_24:
        formatter = new SimpleDateFormat("HH:mm");
        break;
      case HOURS_24_WITH_SECONDS:
        formatter = new SimpleDateFormat("HH:mm:ss");
        break;
      case HOURS_12:
        formatter = new SimpleDateFormat("h:mm a");
        break;
      case HOURS_12_WITH_SECONDS:
        formatter = new SimpleDateFormat("h:mm:ss a");
        break;
      case LOCAL_TIME:
        formatter = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
        break;
      case LOCAL_TIME_WITH_SECONDS:
        formatter = DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.getDefault());
        break;
      default:
        formatter = new SimpleDateFormat("HH:mm");
        break;
    }
    return ((SimpleDateFormat) formatter).toPattern();
  }
}
