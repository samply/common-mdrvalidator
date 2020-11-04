

package de.dth.mdr.validator.enums;

/**
 * Enum for the different validator types.
 */
public enum EnumValidatorType {
  /**
   * No validation is supported or validation do not apply.
   */
  NONE,
  /**
   * Data should be validated as a boolean.
   */
  BOOLEAN,
  /**
   * Data should be validated as an integer.
   */
  INTEGER,
  /**
   * Data should be validated as a float.
   */
  FLOAT,
  /**
   * Data should be an integer in between a minimum and maximum value.
   */
  INTEGERRANGE,
  /**
   * Data should be an float in between a minimum and maximum value.
   */
  FLOATRANGE,
  /**
   * Data should be validated as a date.
   */
  DATE,
  /**
   * Data should be validated as time.
   */
  TIME,
  /**
   * Data should be validated as a date and time value.
   */
  DATETIME,
  /**
   * Data should be validated with a regular expression.
   */
  REGEX,
  /**
   * Data validated per catalogue.
   */
  CATALOG,
  /**
   * Data validated per enum/permissible values.
   */
  ENUMERATED,
  /**
   * Data validated as String (with possible maximum character quantity).
   */
  STRING
}

