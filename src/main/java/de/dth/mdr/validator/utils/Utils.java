
package de.dth.mdr.validator.utils;

import de.samply.common.mdrclient.MdrClient;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.domain.Result;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {

  private static final Logger logger = LoggerFactory.getLogger(Utils.class);
  /**
   * Uppers the first char of a string.
   *
   * @param string the string
   * @return the string
   */

  private static String[] separators = new String[]{",", ";"};

  /**
   * Todo.
   *
   * @param string Todo.
   * @return Todo.
   */
  public static String upperCaseFirstChar(String string) {
    if (string == null || string.length() == 0) {
      return string;
    }

    char[] c = string.toCharArray();
    c[0] = Character.toUpperCase(c[0]);
    string = new String(c);

    return string;
  }

  /**
   * Gets all dataelements from a group and its subgroups.
   *
   * @param mdrClient    a pre-initialized mdr client instance
   * @param languageCode the two-letter language code. e.g. "en" or "de"
   * @param theList      the list to extend with new dataelement ids
   * @param groupKey     the mdr id of the dataelementgroup to add
   * @return the elements from the group and its subgroups
   */
  public static List<String> getElementsFromGroupAndSubgroups(MdrClient mdrClient,
      String languageCode, List<String> theList, String groupKey) {
    try {
      List<Result> resultL = mdrClient.getMembers(groupKey, languageCode);
      for (Result r : resultL) {
        if (r.getType().equalsIgnoreCase("dataelementgroup")) {
          theList = getElementsFromGroupAndSubgroups(mdrClient, languageCode, theList, r.getId());
        } else {
          theList.add(r.getId());
        }
      }
    } catch (MdrConnectionException | ExecutionException e) {
      logger.debug(e.getMessage());
    }

    return theList;
  }

  /**
   * Todo.
   *
   * @param stringToConvert Todo.
   * @return Todo.
   */
  public static List<String> convert(String stringToConvert) {

    List<String> stringList = new ArrayList<>();

    if (stringToConvert != null) {

      stringToConvert = stringToConvert.trim();
      String separator = getSeparator(stringToConvert);
      if (separator == null) {

        stringList.add(stringToConvert);

      } else {

        for (String myNamespace : stringToConvert.split(separator)) {

          stringList.add(myNamespace);

        }
      }


    }

    return stringList;

  }

  private static String getSeparator(String myList) {

    String mySeparator = null;
    for (String separator : separators) {
      if (myList.contains(separator)) {
        mySeparator = separator;
        break;
      }
    }

    return mySeparator;

  }

}
