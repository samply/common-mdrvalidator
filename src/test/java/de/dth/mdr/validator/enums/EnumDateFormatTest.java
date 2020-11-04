package de.dth.mdr.validator.enums;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class EnumDateFormatTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();
  private String VALID_ENUM_NAME = EnumDateFormat.DIN_5008.name();

  @Test
  public void test_valueOfTrimmed_preciseString() {
    assertThat(
        "The string represents the name of the enum constant and thus the enum should be found.",
        EnumDateFormat.valueOfTrimmed(VALID_ENUM_NAME), is(EnumDateFormat.DIN_5008));
  }

  @Test
  public void test_valueOfTrimmed_stringWithBlanks() {
    assertThat(
        "The trimmed string represents the name of the enum constant and thus the enum should be found.",
        EnumDateFormat.valueOfTrimmed(" " + VALID_ENUM_NAME + " "), is(EnumDateFormat.DIN_5008));
  }

  @Test
  public void test_valueOfTrimmed_invalidString() {
    thrown.expect(IllegalArgumentException.class);
    assertThat(
        "The string does not represent the name of an enum constant and thus no enum should be found.",
        EnumDateFormat.valueOfTrimmed(VALID_ENUM_NAME + "_XYZ"), not(is(EnumDateFormat.DIN_5008)));
  }
}
