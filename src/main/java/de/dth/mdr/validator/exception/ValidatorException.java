
package de.dth.mdr.validator.exception;

/**
 * The Class MdrException.
 */
public class ValidatorException extends Exception {

  /**
   * The Constant serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new form exception.
   */
  public ValidatorException() {
    super("MdrException thrown");
  }

  /**
   * Instantiates a new exception with the given message.
   *
   * @param msg the message
   */
  public ValidatorException(String msg) {
    super(msg);
  }

  public ValidatorException(String designation, String definition) throws ValidatorException {
    String msg = "ValidatorException: Designation: " + designation + " Definition: " + definition;
    throw new ValidatorException(msg);
  }

  /**
   * Instantiates a new exception with the given cause.
   *
   * @param e the underlying cause for this exception
   */
  public ValidatorException(Throwable e) {
    super(e);
  }
}
