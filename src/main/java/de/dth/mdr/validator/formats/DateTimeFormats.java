
package de.dth.mdr.validator.formats;

import de.dth.mdr.validator.enums.EnumDateFormat;
import de.dth.mdr.validator.enums.EnumTimeFormat;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

public class DateTimeFormats {

  public static final Logger LOGGER = Logger.getLogger(DateTimeFormats.class.getName());
  /**
   * The date format.
   */
  private EnumDateFormat dateFormat;
  /**
   * The time format.
   */
  private EnumTimeFormat timeFormat;

  /**
   * Create an instance with the given date and time format.
   *
   * @param dateFormat the date format.
   * @param timeFormat the time format.
   */
  public DateTimeFormats(final EnumDateFormat dateFormat, final EnumTimeFormat timeFormat) {
    super();
    this.dateFormat = dateFormat;
    this.timeFormat = timeFormat;
  }

  /**
   * Get the {@link DateTimeFormats} from validation data from the MDR.
   *
   * @param validationData validation data obtained from the MDR
   *                       (e.g. "ISO_8601_WITH_DAYS;LOCAL_TIME_WITH_SECONDS")
   * @return the date and the time formats, separately
   */
  public static DateTimeFormats getDateTimeFormats(final String validationData) {
    DateTimeFormats dateTimeFormats = new DateTimeFormats(EnumDateFormat.ISO_8601_WITH_DAYS,
        EnumTimeFormat.HOURS_24);
    String[] mdrFormats = validationData.split(";");
    if (mdrFormats.length == 2) {
      EnumDateFormat enumDateFormat = EnumDateFormat.valueOfTrimmed(mdrFormats[0]);
      EnumTimeFormat enumTimeFormat = EnumTimeFormat.valueOfTrimmed(mdrFormats[1]);
      dateTimeFormats = new DateTimeFormats(enumDateFormat, enumTimeFormat);
    }
    return dateTimeFormats;
  }

  /**
   * Get the date/time format pattern from the MDR date format description that the date/time picker
   * can understand.
   *
   * @param enumDateFormat the date format as known in MDR
   * @param enumTimeFormat the time format as known in MDR
   * @return the date/time format pattern string representation that the date/time picker can
   *        understand
   */
  public static String getDatetimepickerPattern(final EnumDateFormat enumDateFormat,
      final EnumTimeFormat enumTimeFormat) {
    String datePattern = getDateTimePattern(enumDateFormat, enumTimeFormat);
    // Java gives yyyy-MM-dd HH:mm:ss, picker needs YYYY-MM-DD HH:mm:ss
    datePattern = datePattern.replace("y", "Y");
    datePattern = datePattern.replace("d", "D");
    return datePattern;
  }

  /**
   * Get the date format pattern from the MDR date format description.
   *
   * @param enumDateFormat the date format as known in MDR
   * @param enumTimeFormat the time format as known in the MDR
   * @return the date format pattern string representation
   */
  public static String getDateTimePattern(final EnumDateFormat enumDateFormat,
      final EnumTimeFormat enumTimeFormat) {
    String datePattern = DateFormats.getDatePattern(enumDateFormat);
    String timePattern = TimeFormats.getTimePattern(enumTimeFormat);

    if (!datePattern.isEmpty() && !timePattern.isEmpty()) {
      DateFormat formatter = new SimpleDateFormat(datePattern + " " + timePattern);
      return ((SimpleDateFormat) formatter).toPattern();
    } else {
      LOGGER.warning(
          "Could not get a formatter for " + enumDateFormat.name() + " " + enumTimeFormat.name());
      return "";
    }
  }

  /**
   * Get the date format of this validator.
   *
   * @return the date format
   */
  public final EnumDateFormat getDateFormat() {
    return dateFormat;
  }

  /**
   * Set the date format of this validator.
   *
   * @param dateFormat the date format to set
   */
  public final void setDateFormat(final EnumDateFormat dateFormat) {
    this.dateFormat = dateFormat;
  }

  /**
   * Get the time format of this validator.
   *
   * @return the time format
   */
  public final EnumTimeFormat getTimeFormat() {
    return timeFormat;
  }

  /**
   * Set the time format of this validator.
   *
   * @param timeFormat the time format to set
   */
  public final void setTimeFormat(final EnumTimeFormat timeFormat) {
    this.timeFormat = timeFormat;
  }
}
