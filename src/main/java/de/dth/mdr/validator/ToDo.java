package de.dth.mdr.validator;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import de.dth.mdr.validator.enums.EnumDateFormat;
import de.dth.mdr.validator.enums.EnumTimeFormat;
import de.dth.mdr.validator.exception.MdrException;
import de.dth.mdr.validator.formats.DateTimeFormats;
import de.samply.auth.client.jwt.KeyLoader;
import de.samply.auth.rest.AccessTokenDto;
import de.samply.auth.rest.AccessTokenRequestDto;
import de.samply.auth.rest.KeyIdentificationDto;
import de.samply.auth.rest.SignRequestDto;
import de.samply.common.http.HttpConnector;
import de.samply.common.mdrclient.MdrClient;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.MdrInvalidResponseException;
import de.samply.common.mdrclient.domain.EnumValidationType;
import de.samply.common.mdrclient.domain.ErrorMessage;
import de.samply.common.mdrclient.domain.PermissibleValue;
import de.samply.common.mdrclient.domain.Validations;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.configuration.Configuration;


public class ToDo {

  /**
   * special range regexp used in mdr.
   */
  public static final String FLOAT_RANGE_REGEX = "(?:(.+)<=)?x(?:<=(.+))?";

  /**
   * Todo.
   */
  public static void main() {
    String userAuthId = null;
    String mdrId = null;

    HttpConnector httpConnector = null;
    AccessTokenDto accessToken = null;

    String urlMdr = "";
    Configuration configuration = null;

    try {
      httpConnector = new HttpConnector(new HashMap<>());

      Client client = httpConnector.getClient(httpConnector
          .getHttpClientForHTTPS());
      accessToken = getAccessToken(client, configuration);
      client.destroy();
    } catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
      e.printStackTrace();
    }

    Client mdrJerseyClient = httpConnector.getJerseyClient(urlMdr);
    MdrClient mdrClient = new MdrClient(urlMdr, mdrJerseyClient);

    try {
      Validations dataElementValidations = mdrClient.getDataElementValidations(mdrId,
          Locale.getDefault().getLanguage(), accessToken.getAccessToken(), userAuthId);

      for (ErrorMessage moo : dataElementValidations.getErrorMessages()) {
        System.out.println(moo.getDesignation() + " ... " + moo.getDefinition());
      }

    } catch (MdrConnectionException | MdrInvalidResponseException | ExecutionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      System.out.println("Could not get the data elements validation for mdrId " + mdrId);
    }
  }

  // moo

  /**
   * Gets an AccessTokenDto accessToken from Samply.AUTH component, used for communication with
   * other components
   *
   * @param client        A Jersey client
   * @param configuration the configuration
   * @return AccessTokenDto
   * @throws NoSuchAlgorithmException the no such algorithm exception
   * @throws InvalidKeyException      the invalid key exception
   * @throws SignatureException       the signature exception
   */
  public static AccessTokenDto getAccessToken(Client client, Configuration configuration)
      throws NoSuchAlgorithmException, InvalidKeyException,
      SignatureException {
    if (client == null) {
      return null;
    }

    KeyIdentificationDto identification = new KeyIdentificationDto();

    String keyId = configuration
        .getString("auth.keyid");
    if (keyId == null || "".equalsIgnoreCase(keyId)) {
      return null;
    }

    identification.setKeyId(Integer.parseInt(keyId));
    String authUrl = configuration
        .getString("auth.rest");
    ClientResponse response = client
        .resource(authUrl + "/oauth2/signRequest")
        .accept("application/json").type("application/json")
        .post(ClientResponse.class, identification);
    if (response.getStatus() != 200) {
      System.out.println(
          "Auth.getAccessToken returned " + response.getStatus()
              + " on signRequest bailing out!");
      return null;
    }
    SignRequestDto signRequest = response.getEntity(SignRequestDto.class);

    /**
     * Sign the code and encode to base64.
     */
    Signature sig = Signature.getInstance(signRequest.getAlgorithm());
    PrivateKey privkey = KeyLoader.loadPrivateKey(configuration
        .getString("auth.my.privkey"));
    sig.initSign(privkey);
    sig.update(signRequest.getCode().getBytes());
    String signature = Base64.encodeBase64String(sig.sign());

    AccessTokenRequestDto accessRequest = new AccessTokenRequestDto();
    accessRequest.setCode(signRequest.getCode());
    accessRequest.setSignature(signature);

    response = client.resource(authUrl + "/oauth2/access_token")
        .accept("application/json").type("application/json")
        .post(ClientResponse.class, accessRequest);

    if (response.getStatus() != 200) {
      System.out.println(
          "Auth.getAccessToken returned " + response.getStatus()
              + " bailing out!");
      return null;
    }

    return response.getEntity(AccessTokenDto.class);
  }

  /**
   * Checks if a value is an integer, and if it's between a min and max if given.
   *
   * @param value The value to check
   * @param min   min value|null
   * @param max   max value|null
   * @return boolean
   */
  public static Boolean validInteger(String value, String min, String max) {
    try {
      Integer floatValue = Integer.valueOf(String.valueOf(value));

      if (min == null) {
        return true;
      }

      Integer minFloat = Integer.valueOf(min);
      Integer maxFloat = Integer.valueOf(max);

      if (floatValue >= minFloat && floatValue <= maxFloat) {
        return true;
      }
    } catch (NumberFormatException e) {
      return false;
    }

    return false;
  }

  /**
   * Checks if a value is a float, and if it's between a min and max if given.
   *
   * @param value The value to check
   * @param min   min value|null
   * @param max   max value|null
   * @return boolean
   */
  public static Boolean validFloat(String value, String min, String max) {
    try {
      Float floatValue = Float.valueOf(String.valueOf(value));

      if (min == null) {
        return true;
      }

      Integer minFloat = Integer.valueOf(min);
      Integer maxFloat = Integer.valueOf(max);

      if (floatValue >= minFloat && floatValue <= maxFloat) {
        return true;
      }
    } catch (NumberFormatException e) {
      return false;
    }

    return false;
  }

  /**
   * Checks if a value is a boolean.
   *
   * @param value the value
   * @return the boolean
   */
  public static Boolean validBoolean(String value) {
    return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false");

  }

  /**
   * Check if validations include permissible values.
   *
   * @param dataElementValidations the data element validations
   * @return true if {@link Validations} include permissible values, false otherwise
   */
  private boolean hasPermissibleValues(final Validations dataElementValidations) {
    return dataElementValidations == null || dataElementValidations.getPermissibleValues() != null
        && dataElementValidations.getPermissibleValues().size() > 0;
  }

  private HashMap<String, Object> prepareValidationMap(MdrKey mdrKey) throws MdrConnectionException,
      MdrInvalidResponseException, ExecutionException, MdrException {
    HashMap<String, Object> temp = new HashMap<>();

    Validations dataElementValidations = mdrKey.getValidations();
    String validationType = dataElementValidations.getValidationType();
    temp.put("type", validationType);

    if (dataElementValidations.getErrorMessages() != null && !dataElementValidations
        .getErrorMessages().isEmpty()) {
      temp.put("error", dataElementValidations.getErrorMessages().get(0).getDefinition());
    } else {
      temp.put("error", "Some error");
    }

    if (hasPermissibleValues(dataElementValidations)) {
      for (PermissibleValue permissibleValue : dataElementValidations.getPermissibleValues()) {
        temp.put("permissibleValue", permissibleValue.getValue());
      }
      temp.put("type", "permissibleValues");
    } else {
      if (EnumValidationType.INTEGERRANGE.name().equalsIgnoreCase(validationType)) {
        Pattern pattern = Pattern.compile(FLOAT_RANGE_REGEX);
        Matcher matcher = pattern.matcher(dataElementValidations.getValidationData());

        if (matcher.find()) {
          String min = matcher.group(1);
          String max = matcher.group(2);

          temp.put("min", min == null ? "" : min);
          temp.put("max", max == null ? "" : max);
        }
      } else {
        if (EnumValidationType.FLOATRANGE.name().equalsIgnoreCase(validationType)) {
          Pattern pattern = Pattern.compile(FLOAT_RANGE_REGEX);
          Matcher matcher = pattern.matcher(dataElementValidations.getValidationData());

          if (matcher.find()) {
            String min = matcher.group(1);
            String max = matcher.group(2);

            temp.put("min", min == null ? "" : min);
            temp.put("max", max == null ? "" : max);
          }
        } else {
          if (EnumValidationType.REGEX.name().equalsIgnoreCase(validationType)) {
            String regex = dataElementValidations.getValidationData();
            temp.put("regexp", regex);
          } else {
            if (EnumValidationType.DATE.name().equalsIgnoreCase(validationType)) {
              EnumDateFormat enumDateFormat = EnumDateFormat.valueOfTrimmed(dataElementValidations
                  .getValidationData());
              temp.put("enumDateFormat", enumDateFormat.name());
            } else if (EnumValidationType.TIME.name().equalsIgnoreCase(validationType)) {
              EnumTimeFormat enumTimeFormat = EnumTimeFormat.valueOfTrimmed(dataElementValidations
                  .getValidationData());
              temp.put("enumTimeFormat", enumTimeFormat.name());
            } else if (EnumValidationType.DATETIME.name().equalsIgnoreCase(validationType)) {
              DateTimeFormats dateTimeFormats = DateTimeFormats
                  .getDateTimeFormats(dataElementValidations
                      .getValidationData());

              temp.put("enumDateFormat", dateTimeFormats.getDateFormat().name());
              temp.put("enumTimeFormat", dateTimeFormats.getTimeFormat().name());
            }
          }
        }
      }
    }
    return temp;
  }

}
