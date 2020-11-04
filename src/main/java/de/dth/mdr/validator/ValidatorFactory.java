
package de.dth.mdr.validator;

import de.dth.mdr.validator.enums.EnumValidatorType;
import de.dth.mdr.validator.validators.BooleanValidator;
import de.dth.mdr.validator.validators.CatalogueValidator;
import de.dth.mdr.validator.validators.DateTimeValidator;
import de.dth.mdr.validator.validators.DateValidator;
import de.dth.mdr.validator.validators.FloatRangeValidator;
import de.dth.mdr.validator.validators.FloatValidator;
import de.dth.mdr.validator.validators.IntegerRangeValidator;
import de.dth.mdr.validator.validators.IntegerValidator;
import de.dth.mdr.validator.validators.MdrRegexValidator;
import de.dth.mdr.validator.validators.PermissibleValueValidator;
import de.dth.mdr.validator.validators.StringValidator;
import de.dth.mdr.validator.validators.TimeValidator;
import de.dth.mdr.validator.validators.Validator;

public class ValidatorFactory {

  /**
   * Factory to give a Validator based on type.
   *
   * @param type          validator type
   * @param unitOfMeasure the unit of measure if there is one
   * @param flatCatalogue the flattened catalogue (only for catalogue validator)
   * @return
   */
  public static Validator getValidator(EnumValidatorType type, String unitOfMeasure,
      FlatCatalogue flatCatalogue) {
    Validator validator = null;

    switch (type) {
      case BOOLEAN:
        validator = new BooleanValidator();
        break;
      case INTEGER:
        validator = new IntegerValidator();
        break;
      case INTEGERRANGE:
        validator = new IntegerRangeValidator();
        break;
      case FLOAT:
        validator = new FloatValidator();
        break;
      case FLOATRANGE:
        validator = new FloatRangeValidator();
        break;
      case DATE:
        validator = new DateValidator();
        break;
      case TIME:
        validator = new TimeValidator();
        break;
      case DATETIME:
        validator = new DateTimeValidator();
        break;
      case REGEX:
        validator = new MdrRegexValidator();
        break;
      case CATALOG:
        validator = new CatalogueValidator();
        break;
      case ENUMERATED:
        validator = new PermissibleValueValidator();
        break;
      case STRING:
        validator = new StringValidator();
        break;
      case NONE:
        validator = null;
        break;
      default:
        break;
    }

    if (validator != null) {
      if (unitOfMeasure != null) {
        validator.setUnitOfMeasure(unitOfMeasure);
      }

      if (validator instanceof CatalogueValidator && flatCatalogue != null) {
        ((CatalogueValidator) validator).setFlatCatalogue(flatCatalogue);
      }
    }

    return validator;
  }

  /**
   * Factory to give a Validator based on type.
   *
   * @param type validator type
   * @return
   */
  public static Validator getValidator(EnumValidatorType type) {
    return getValidator(type, null, null);
  }
}
