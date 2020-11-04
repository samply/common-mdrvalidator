package de.dth.mdr.validator.utils;

import de.dth.mdr.validator.MdrConnection;
import de.samply.common.config.Configuration;
import de.samply.common.config.ObjectFactory;
import de.samply.common.config.mdr.Connection;
import de.samply.common.config.mdr.DataElementGroup;
import de.samply.common.config.mdr.MdrUsage;
import de.samply.common.config.mdr.Validation;
import de.samply.config.util.FileFinderUtil;
import de.samply.config.util.JaxbUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;

public class MdrUsageUtil {

  private final Connection connection;
  private final Validation validation;


  public MdrUsageUtil(MdrUsage mdrUsage) {
    this.connection = mdrUsage != null ? mdrUsage.getConnection() : null;
    this.validation = mdrUsage != null ? mdrUsage.getValidation() : null;
  }

  public boolean getUseMdrValidation() {
    return validation != null && BooleanUtils.isTrue(validation.getUseMdrValidation());
  }

  /**
   * Todo.
   *
   * @return Todo.
   * @throws FileNotFoundException        Todo.
   * @throws JAXBException                Todo.
   * @throws ParserConfigurationException Todo.
   * @throws SAXException                 Todo.
   */
  public MdrConnection getMdrConnection()
      throws FileNotFoundException, JAXBException, ParserConfigurationException, SAXException {
    /*if (!getUseMdrValidation()) {
      return null;
    }*/

    de.samply.common.config.Configuration proxy = null;
    if (connection.getUseProxy()) {
      proxy = getProxyConfiguration();
    }

    return new MdrConnection(connection.getUrlMdr(), null, null, null, null, getNamespaces(), proxy,
        connection.getUseAnonymousMdrConnection());
  }

  private List<String> getNamespaces() {
    return (connection != null && connection.getNamespace() != null) ? Utils
        .convert(connection.getNamespace()) : new ArrayList<>();
  }

  private Configuration getProxyConfiguration()
      throws FileNotFoundException, JAXBException, ParserConfigurationException, SAXException {
    File configFile = FileFinderUtil
        .findFile("proxy.xml", "dth.validator", connection.getPathToProxy().toString());
    return JaxbUtil
        .unmarshall(configFile, JAXBContext.newInstance(ObjectFactory.class), Configuration.class);
  }


  /**
   * Todo.
   *
   * @return Todo.
   */
  public Set<String> getAllNamesDataElementGroups() {
    return validation.getDataElementGroups().stream().map(DataElementGroup::getName)
        .collect(Collectors.toSet());
  }

  /**
   * Todo.
   *
   * @param name Todo.
   * @return Todo.
   */
  public String getUrnDataElementGroup(String name) {
    Optional<DataElementGroup> urn = validation.getDataElementGroups().stream()
        .filter(dataElementGroup -> StringUtils.equals(dataElementGroup.getName(), name))
        .findFirst();

    if (!urn.isPresent()) {
      return null;
    } else {
      return urn.get().getValue();
    }
  }

  /**
   * Todo.
   *
   * @return Todo.
   */
  public String getNamespace() {
    return connection.getNamespace();
  }
}
