
package de.dth.mdr.validator.enums;

import org.apache.commons.lang.StringUtils;

/**
 * Date format types, as known from MDR.
 */
public enum EnumDateFormat {
  /**
   * A date defined in the session locale format.
   */
  LOCAL_DATE,
  /**
   * ISO 8601 date.
   */
  ISO_8601,
  /**
   * DIN 5008 date.
   */
  DIN_5008,
  /**
   * A date defined in the session locale format, with days.
   */
  LOCAL_DATE_WITH_DAYS,
  /**
   * ISO 8601 date, with days.
   */
  ISO_8601_WITH_DAYS,
  /**
   * DIN 5008 date, with days.
   */
  DIN_5008_WITH_DAYS,
  /**
   * DIN 5008 date, only with years.
   */
  DIN_5008_ONLY_YEAR;

  public static EnumDateFormat valueOfTrimmed(String format) {
    return valueOf(StringUtils.trim(format));
  }
}
