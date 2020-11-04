package de.dth.mdr.validator.util;

import de.samply.common.config.mdr.Connection;
import de.samply.common.config.mdr.DataElementGroup;
import de.samply.common.config.mdr.MdrUsage;
import de.samply.common.config.mdr.Validation;
import java.nio.file.Path;

class MdrUsageTestFactory {

  static final String NAME_BIOBANK = "biobank";
  static final String NAME_COLLECTION = "collection";
  static final String NAME_SAMPLE = "sample";
  static final String NAME_SAMPLE_CONTEXT = "sampleContext";
  static final String NAME_DONOR = "donor";
  static final String NAME_EVENT = "event";
  static final String URN_DONOR = "urn:gba:dataelementgroup:2:13";
  private static final boolean USE_MDR_VALIDATION = true;
  private static final boolean USE_ANONYMOUS_MDR_CONNECTION = true;
  private static final boolean USE_PROXY = false;
  private static final Path PATH_TO_PROXY = null;
  private static final String MDR_URL = "http://mdr.germanbiobanknode.de" + "/v3/api/mdr";
  private static final String MDR_NAMESPACE = "gba";
  private static final String URN_BIOBANK = "urn:gba:dataelementgroup:4:13";
  private static final String URN_COLLECTION = "urn:gba:dataelementgroup:5:10";
  private static final String URN_SAMPLE = "urn:gba:dataelementgroup:3:9";
  private static final String URN_SAMPLE_CONTEXT = "urn:gba:dataelementgroup:7:8";
  private static final String URN_EVENT = "urn:gba:dataelementgroup:6:7";

  static MdrUsage createMdrUsage() {
    MdrUsage mdrUsage = new MdrUsage();

    Connection connection = new Connection();

    connection.setUseAnonymousMdrConnection(USE_ANONYMOUS_MDR_CONNECTION);
    connection.setUseProxy(USE_PROXY);
    connection.setPathToProxy(PATH_TO_PROXY);
    connection.setUrlMdr(MDR_URL);
    connection.setNamespace(MDR_NAMESPACE);

    mdrUsage.setConnection(connection);

    Validation validation = new Validation();

    validation.setUseMdrValidation(USE_MDR_VALIDATION);
    validation.getDataElementGroups().add(createDataElementGroup(NAME_BIOBANK, URN_BIOBANK));
    validation.getDataElementGroups().add(createDataElementGroup(NAME_COLLECTION, URN_COLLECTION));
    validation.getDataElementGroups().add(createDataElementGroup(NAME_DONOR, URN_DONOR));
    validation.getDataElementGroups().add(createDataElementGroup(NAME_EVENT, URN_EVENT));
    validation.getDataElementGroups().add(createDataElementGroup(NAME_SAMPLE, URN_SAMPLE));
    validation.getDataElementGroups()
        .add(createDataElementGroup(NAME_SAMPLE_CONTEXT, URN_SAMPLE_CONTEXT));

    mdrUsage.setValidation(validation);

    return mdrUsage;
  }

  private static DataElementGroup createDataElementGroup(String name, String value) {
    DataElementGroup dataElementGroup = new DataElementGroup();

    dataElementGroup.setName(name);
    dataElementGroup.setValue(value);

    return dataElementGroup;
  }
}
