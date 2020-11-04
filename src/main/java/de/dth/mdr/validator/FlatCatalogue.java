

package de.dth.mdr.validator;

import de.samply.common.mdrclient.domain.Code;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Flattened version of a catalog. It contains a list of valid codes and a map to transform a valid
 * designation into its code
 */
public class FlatCatalogue {

  private Collection<String> validCodes;
  private HashMap<String, Code> validDesignationToCodes;

  public FlatCatalogue() {
    validCodes = new HashSet<>();
    validDesignationToCodes = new HashMap<>();
  }

  public FlatCatalogue(Collection<String> validCodes,
      HashMap<String, Code> validDesignationToCodes) {
    this.validCodes = new HashSet<>(validCodes);
    this.validDesignationToCodes = validDesignationToCodes;
  }

  public Collection<String> getValidCodes() {
    return validCodes;
  }

  public void setValidCodes(Collection<String> validCodes) {
    this.validCodes = new HashSet<>(validCodes);
  }

  public HashMap<String, Code> getValidDesignationToCodes() {
    return validDesignationToCodes;
  }

  public void setValidDesignationToCodes(HashMap<String, Code> validDesignationToCodes) {
    this.validDesignationToCodes = validDesignationToCodes;
  }
}
