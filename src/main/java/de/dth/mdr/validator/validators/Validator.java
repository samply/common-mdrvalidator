package de.dth.mdr.validator.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Validator implements ValidatorInterface {

  private String unitOfMeasure;
  private boolean caseSensitive;

  public Validator() {
    caseSensitive = true;
  }

  public boolean isCaseSensitive() {
    return caseSensitive;
  }

  public void setCaseSensitive(boolean caseSensitive) {
    this.caseSensitive = caseSensitive;
  }

  /**
   * Cuts the unit of measure from a given string (and all whitespaces) and returns the pure number.
   * It does not check if that number is actually a number! If no unit of measure is found, just
   * returns the given string
   *
   * @param value a number (unchecked) maybe including the defined unit of measure
   * @return the value without the unit of measure
   */
  protected String cutUnitOfMeasure(String value) {
    // get number before unit of measure
    String unitOfMeasurePatternString = "^\\s*(\\S*)\\s*" + getUnitOfMeasure() + "\\s*$";
    Pattern unitOfMeasurePattern = Pattern.compile(unitOfMeasurePatternString);
    Matcher unitOfMeasureMatcher = unitOfMeasurePattern.matcher(value);

    // if the unit of measure is found, just check the number,
    // if not, we allow that the value is saved without the unit of measure, too
    if (unitOfMeasureMatcher.matches()) {
      value = unitOfMeasureMatcher.group(1);
    }

    return value;
  }

  public String getUnitOfMeasure() {
    return unitOfMeasure;
  }

  public void setUnitOfMeasure(String unitOfMeasure) {
    this.unitOfMeasure = unitOfMeasure;
  }

}
