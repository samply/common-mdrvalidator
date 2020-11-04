package de.dth.mdr.validator.exception;

/**
 * The Class MdrException.
 */
public class MdrException extends Exception {

  /**
   * The Constant serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new form exception.
   */
  public MdrException() {
    super("MdrException thrown");
  }

  /**
   * Instantiates a new exception with the given message.
   *
   * @param msg the message
   */
  public MdrException(String msg) {
    super(msg);
  }

  /**
   * Instantiates a new exception with the given cause.
   *
   * @param e the underlying cause for this exception
   */
  public MdrException(Throwable e) {
    super(e);
  }

  public MdrException(String msg, Throwable e) {
    super(msg, e);
  }
}
