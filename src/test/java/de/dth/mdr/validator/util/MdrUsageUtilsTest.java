package de.dth.mdr.validator.util;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import de.dth.mdr.validator.utils.MdrUsageUtil;
import de.samply.common.config.mdr.DataElementGroup;
import de.samply.common.config.mdr.MdrUsage;
import java.util.List;
import org.junit.Test;

public class MdrUsageUtilsTest {

  private static final String[] EXPECTED_NAMES = {
      MdrUsageTestFactory.NAME_BIOBANK,
      MdrUsageTestFactory.NAME_COLLECTION,
      MdrUsageTestFactory.NAME_DONOR,
      MdrUsageTestFactory.NAME_EVENT,
      MdrUsageTestFactory.NAME_SAMPLE,
      MdrUsageTestFactory.NAME_SAMPLE_CONTEXT,
  };

  @Test
  public void test_getAllNamesDataElementGroups() {
    MdrUsage mdrUsage = MdrUsageTestFactory.createMdrUsage();

    MdrUsageUtil mdrUsageUtil = new MdrUsageUtil(mdrUsage);

    List<DataElementGroup> expectedDataElementGroups = mdrUsage.getValidation()
        .getDataElementGroups();
    assertThat("Size of list is wrong.",
        mdrUsageUtil.getAllNamesDataElementGroups().size(), is(expectedDataElementGroups.size()));
    assertThat("Some elements are missing.",
        mdrUsageUtil.getAllNamesDataElementGroups(), hasItems(EXPECTED_NAMES));
  }

  @Test
  public void test_getUrnDataElementGroup_ExistingName() {
    MdrUsage mdrUsage = MdrUsageTestFactory.createMdrUsage();

    MdrUsageUtil mdrUsageUtil = new MdrUsageUtil(mdrUsage);

    assertThat("Wrong value for 'donor' is found.",
        mdrUsageUtil.getUrnDataElementGroup(MdrUsageTestFactory.NAME_DONOR),
        is(MdrUsageTestFactory.URN_DONOR));
  }

  @Test
  public void test_getUrnDataElementGroup_NotExistingName() {
    MdrUsage mdrUsage = MdrUsageTestFactory.createMdrUsage();

    MdrUsageUtil mdrUsageUtil = new MdrUsageUtil(mdrUsage);

    assertThat("Wrong value for 'donor' is found.",
        mdrUsageUtil.getUrnDataElementGroup("abc"), nullValue());
  }

  @Test
  public void test_getUseMdrValidation_NoMdrUsage() {
    MdrUsageUtil mdrUsageUtil = new MdrUsageUtil(null);

    assertThat("When parameter MdrUsage is null, no MDR is used",
        mdrUsageUtil.getUseMdrValidation(), is(false));
  }

  @Test
  public void test_getUseMdrValidation_Validation() {
    MdrUsage mdrUsage = MdrUsageTestFactory.createMdrUsage();
    mdrUsage.setValidation(null);

    MdrUsageUtil mdrUsageUtil = new MdrUsageUtil(mdrUsage);

    assertThat("When Validation inside MdrUsage is null, no MDR is used",
        mdrUsageUtil.getUseMdrValidation(), is(false));
  }

  @Test
  public void test_getUseMdrValidation_OptionUseMdrValidation_false() {
    MdrUsage mdrUsage = MdrUsageTestFactory.createMdrUsage();
    mdrUsage.getValidation().setUseMdrValidation(false);

    MdrUsageUtil mdrUsageUtil = new MdrUsageUtil(mdrUsage);

    assertThat("When option 'useMdrValidation' is not set, no MDR is used",
        mdrUsageUtil.getUseMdrValidation(), is(false));
  }

  @Test
  public void test_getUseMdrValidation_OptionUseMdrValidation_true() {
    MdrUsage mdrUsage = MdrUsageTestFactory.createMdrUsage();
    mdrUsage.getValidation().setUseMdrValidation(true);

    MdrUsageUtil mdrUsageUtil = new MdrUsageUtil(mdrUsage);

    assertThat("When option 'useMdrValidation' is not set, no MDR is used",
        mdrUsageUtil.getUseMdrValidation(), is(true));
  }
}
