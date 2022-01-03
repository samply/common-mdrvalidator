
package de.dth.mdr.validator;

import de.samply.auth.client.jwt.KeyLoader;
import de.samply.auth.rest.AccessTokenDto;
import de.samply.auth.rest.AccessTokenRequestDto;
import de.samply.auth.rest.KeyIdentificationDto;
import de.samply.auth.rest.SignRequestDto;
import de.samply.common.http.HttpConnector;
import de.samply.common.mdrclient.MdrClient;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.codec.binary.Base64;
import org.glassfish.jersey.client.ClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a Connector to the MDR.
 **/
public class MdrConnection {

  private final Logger logger = LoggerFactory.getLogger(MdrConnection.class);

  private MdrClient mdrClient;
  private AccessTokenDto accessToken;

  private String authUserId;
  private String keyId;
  private String authUrl;
  private String privateKeyBase64;
  private List<String> namespaces;
  private HttpConnector httpConnector;


  /**
   * Establishes connection to the MDR.
   *
   * @param mdrUrl           URL to MDR REST
   * @param authUserId       The userID to log in with
   * @param keyId            The keyId to use
   * @param authUrl          URL to AUTH REST
   * @param privateKeyBase64 The private key (base64 encoded)
   * @param namespace        The MDR namespace to use
   * @param anonymous        Access MDR anonymous way
   * @param httpConnector    The httpConnector43 to use
   */
  public MdrConnection(String mdrUrl, String authUserId, String keyId, String authUrl,
      String privateKeyBase64, String namespace, boolean anonymous, HttpConnector httpConnector) {
    initialize(mdrUrl, authUserId, keyId, authUrl, privateKeyBase64,
        new ArrayList<>(Collections.singletonList(namespace)), null, anonymous, httpConnector);
  }

  /**
   * Establishes connection to the MDR.
   *
   * @param mdrUrl           URL to MDR REST
   * @param authUserId       The userID to log in with
   * @param keyId            The keyId to use
   * @param authUrl          URL to AUTH REST
   * @param privateKeyBase64 The private key (base64 encoded)
   * @param namespaces       The MDR namespaces to use
   * @param anonymous        Access MDR anonymous way
   * @param httpConnector    The httpConnector43 to use
   */
  public MdrConnection(String mdrUrl, String authUserId, String keyId, String authUrl,
      String privateKeyBase64, List<String> namespaces, boolean anonymous,
      HttpConnector httpConnector) {
    initialize(mdrUrl, authUserId, keyId, authUrl, privateKeyBase64, namespaces, null, anonymous,
        httpConnector);
  }

  /**
   * Establishes connection to the MDR.
   *
   * @param mdrUrl           URL to MDR REST
   * @param authUserId       The userID to log in with
   * @param keyId            The keyId to use
   * @param authUrl          URL to AUTH REST
   * @param privateKeyBase64 The private key (base64 encoded)
   * @param namespace        The MDR namespace to use
   * @param proxy            Proxy configuration (as de.samply.common.config.Configuration)
   * @param anonymous        Access MDR anonymous way
   */
  public MdrConnection(String mdrUrl, String authUserId, String keyId, String authUrl,
      String privateKeyBase64, String namespace, de.samply.common.config.Configuration proxy,
      boolean anonymous) {
    initialize(mdrUrl, authUserId, keyId, authUrl, privateKeyBase64,
        new ArrayList<>(Collections.singletonList(namespace)), proxy, anonymous, null);

  }

  /**
   * Establishes connection to the MDR.
   *
   * @param mdrUrl           URL to MDR REST
   * @param authUserId       The userID to log in with
   * @param keyId            The keyId to use
   * @param authUrl          URL to AUTH REST
   * @param privateKeyBase64 The private key (base64 encoded)
   * @param namespaces       The MDR namespaces to use
   * @param proxy            Proxy configuration (as de.samply.common.config.Configuration)
   * @param anonymous        Access MDR anonymous way
   */
  public MdrConnection(String mdrUrl, String authUserId, String keyId, String authUrl,
      String privateKeyBase64, List<String> namespaces, de.samply.common.config.Configuration proxy,
      boolean anonymous) {
    initialize(mdrUrl, authUserId, keyId, authUrl, privateKeyBase64, namespaces, proxy, anonymous,
        null);

  }

  /**
   * Establishes connection to the MDR.
   *
   * @param mdrUrl           URL to MDR REST
   * @param authUserId       The userID to log in with
   * @param keyId            The keyId to use
   * @param authUrl          URL to AUTH REST
   * @param privateKeyBase64 The private key (base64 encoded)
   * @param namespace        The MDR namespace to use
   */
  public MdrConnection(String mdrUrl, String authUserId, String keyId, String authUrl,
      String privateKeyBase64, String namespace) {
    initialize(mdrUrl, authUserId, keyId, authUrl, privateKeyBase64,
        new ArrayList<>(Collections.singletonList(namespace)), null, false, null);
  }

  /**
   * Establishes connection to the MDR.
   *
   * @param mdrUrl           URL to MDR REST
   * @param authUserId       The userID to log in with
   * @param keyId            The keyId to use
   * @param authUrl          URL to AUTH REST
   * @param privateKeyBase64 The private key (base64 encoded)
   * @param namespaces       The MDR namespaces to use
   */
  public MdrConnection(String mdrUrl, String authUserId, String keyId, String authUrl,
      String privateKeyBase64, List<String> namespaces) {
    initialize(mdrUrl, authUserId, keyId, authUrl, privateKeyBase64, namespaces, null, false, null);
  }

  private void initialize(String mdrUrl, String authUserId, String keyId, String authUrl,
      String privateKeyBase64, List<String> namespaces, de.samply.common.config.Configuration proxy,
      boolean anonymous, HttpConnector httpConnector43) {

    this.authUrl = authUrl;
    this.authUserId = authUserId;
    this.keyId = keyId;
    this.privateKeyBase64 = privateKeyBase64;
    this.namespaces = namespaces;

    try {
      if (httpConnector43 != null) {
        httpConnector = httpConnector43;
      } else if (proxy != null) {
        httpConnector = new HttpConnector(proxy);
      } else {
        httpConnector = new HttpConnector(new HashMap<String, String>());
      }

      Client client = httpConnector.getClient(httpConnector.getHttpClientForHttps());
      mdrClient = new MdrClient(mdrUrl, client);

      if (!anonymous) {
        accessToken = getAccessToken(client);
      }
    } catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
      e.printStackTrace();
    }

  }

  /**
   * Closes the connections.
   */
  public void close() {
    try {
      httpConnector.closeClients();
    } catch (IOException e) {
      logger.error("Cannot close http connection", e);
    }
  }

  /**
   * Gets an AccessTokenDto accessToken from Samply.AUTH component, used for communication with
   * other components
   *
   * @param client A Jersey client
   * @return AccessTokenDto
   * @throws NoSuchAlgorithmException the no such algorithm exception
   * @throws InvalidKeyException      the invalid key exception
   * @throws SignatureException       the signature exception
   */
  public AccessTokenDto getAccessToken(Client client)
      throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
    if (client == null) {
      return null;
    }

    KeyIdentificationDto identification = new KeyIdentificationDto();

    String keyId = this.keyId;
    if (keyId == null || "".equalsIgnoreCase(keyId)) {
      return null;
    }

    identification.setKeyId(Integer.parseInt(keyId));
    ClientResponse response = client.target(authUrl + "/oauth2/signRequest")
        .request(MediaType.APPLICATION_JSON)
        .post(Entity.entity(identification,MediaType.APPLICATION_JSON),ClientResponse.class);
    if (response.getStatus() != 200) {
      logger.debug(
          "Auth.getAccessToken returned " + response.getStatus() + " on signRequest bailing out!");
      return null;
    }
    SignRequestDto signRequest = response.readEntity(SignRequestDto.class);

    /*
     * Sign the code and encode to base64
     */
    Signature sig = Signature.getInstance(signRequest.getAlgorithm());
    PrivateKey privkey = KeyLoader.loadPrivateKey(privateKeyBase64);
    sig.initSign(privkey);
    sig.update(signRequest.getCode().getBytes());
    String signature = Base64.encodeBase64String(sig.sign());

    AccessTokenRequestDto accessRequest = new AccessTokenRequestDto();
    accessRequest.setCode(signRequest.getCode());
    accessRequest.setSignature(signature);

    response = client.target(authUrl + "/oauth2/access_token").request(MediaType.APPLICATION_JSON)
        .post(Entity.entity(accessRequest,MediaType.APPLICATION_JSON),ClientResponse.class);

    if (response.getStatus() != 200) {
      logger.debug("Auth.getAccessToken returned " + response.getStatus() + " bailing out!");
      return null;
    }

    return response.readEntity(AccessTokenDto.class);
  }

  /**
   * Gets the access token.
   *
   * @return the access token
   */
  public AccessTokenDto getAccessToken() {
    return accessToken;
  }

  /**
   * Gets the mdr client.
   *
   * @return the used mdr client
   */
  public MdrClient getMdrClient() {
    return mdrClient;
  }

  /**
   * Return AccessToken.
   *
   * @return accessToken
   */
  public String getAccessTokenToken() {
    if (accessToken == null) {
      return null;
    }

    return accessToken.getAccessToken();
  }

  /**
   * Gets the AuthUserId.
   *
   * @return the AuthUserId
   */
  public String getAuthUserId() {
    return authUserId;
  }

  /**
   * Gets the mdr namespaces.
   *
   * @return the list of mdr namespaces
   */
  public List<String> getNamespaces() {
    return namespaces;
  }
}
