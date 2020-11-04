package de.dth.mdr.validator;

import de.samply.common.mdrclient.domain.ErrorMessage;
import java.util.List;

public class ValidationError {

  private String mdrKey;
  private String dthKey;
  private String value;
  private int line;
  private List<ErrorMessage> errorMessages;

  /**
   * Todo.
   *
   * @return Todo.
   */
  public String toString() {
    return "DTHKey= '" + dthKey + "'\nValue= "
        + "'" + value + "'\nLine='" + line + "'\nMdrKey='" + mdrKey + "'\nErrorMessage='"
        + (errorMessages == null || errorMessages.isEmpty() ? ""
        : errorMessages.get(0).getDesignation() + " -> " + errorMessages.get(0).getDefinition()
            + "'");
  }


  public String getMdrKey() {
    return mdrKey;
  }

  public void setMdrKey(String mdrKey) {
    this.mdrKey = mdrKey;
  }

  public String getDthKey() {
    return dthKey;
  }

  public void setDthKey(String dthKey) {
    this.dthKey = dthKey;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public List<ErrorMessage> getErrorMessages() {
    return errorMessages;
  }

  public void setErrorMessages(List<ErrorMessage> errorMessages) {
    this.errorMessages = errorMessages;
  }

  public int getLine() {
    return line;
  }

  public void setLine(int line) {
    this.line = line;
  }
}
