
package de.dth.mdr.validator;

import de.dth.mdr.validator.exception.MdrException;
import de.dth.mdr.validator.utils.Utils;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.MdrInvalidResponseException;
import de.samply.common.mdrclient.domain.Catalogue;
import de.samply.common.mdrclient.domain.DataElement;
import de.samply.common.mdrclient.domain.Definition;
import de.samply.common.mdrclient.domain.EnumElementType;
import de.samply.common.mdrclient.domain.Label;
import de.samply.common.mdrclient.domain.Result;
import de.samply.common.mdrclient.domain.Slot;
import de.samply.common.mdrclient.domain.Validations;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MDR Key model.
 */
public class MdrKey implements Serializable, Comparable<MdrKey> {

  /**
   * The Constant serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Regular expression for valid URNs.
   */
  private static final String URN_PATTERN
      = "urn:([a-zA-Z_0-9-]*):([a-zA-Z_0-9-]*):([a-zA-Z_0-9]*):([a-zA-Z_0-9]*)";

  /**
   * namespace of the mdr key.
   */
  private String namespace;

  /**
   * type of the mdr key as string.
   */
  private EnumElementType type;

  /**
   * the mdr key id.
   */
  private String id;

  /**
   * the mdr key version.
   */
  private String version;

  /**
   * the purified representation of the mdr key.
   */
  private String purified = null;

  /**
   * a mdr client to requst data from the MDR.
   */
  private transient MdrConnection mdrConnection = null;

  /**
   * the data element itself.
   */
  private DataElement dataElement;

  /**
   * Labels of this mdr key.
   */
  private List<Label> label = null;

  /**
   * Members of this mdr record.
   */
  private List<Result> members = null;

  /**
   * Definition of this mdr key.
   */
  private Definition definition = null;

  /**
   * The slots of this dataelement.
   */
  private List<Slot> slots = null;

  /**
   * Slots as hashmap.
   */
  private HashMap<String, List<String>> slotMap;

  /**
   * The validation entries.
   */
  private Validations validations = null;

  /**
   * Instantiates a new mdr key.
   */
  public MdrKey() {
    namespace = "";
    type = EnumElementType.DATAELEMENT;
    id = "";
    version = "";
  }

  /**
   * Instantiates a new mdr key.
   *
   * @param mdrKey        the mdr key
   * @param mdrConnection the mdr connection
   */
  public MdrKey(String mdrKey, MdrConnection mdrConnection) {
    parseString(mdrKey);
    this.mdrConnection = mdrConnection;
  }

  /**
   * Gets the members of a mdr record or a mdr group.
   *
   * @return set of MdrKey
   * @throws MdrConnectionException the mdr connection exception
   * @throws ExecutionException     the execution exception
   * @throws MdrException           the MDR exception
   */
  public Set<MdrKey> getMembersSet()
      throws MdrConnectionException, ExecutionException, MdrException {
    Set<MdrKey> membersSet = new HashSet<>();

    for (Result member : getMembers()) {
      MdrKey memberKey = new MdrKey(member.getId(), mdrConnection);
      membersSet.add(memberKey);
    }

    return membersSet;
  }

  /**
   * Gets the members of specific type from a mdr record or a mdr group.
   *
   * @param type the type of the members
   * @return set of MdrKeys with a specific type
   * @throws MdrConnectionException the mdr connection exception
   * @throws ExecutionException     the execution exception
   * @throws MdrException           the MDR exception
   */
  public Set<MdrKey> getMembersSet(EnumElementType type)
      throws MdrConnectionException, ExecutionException, MdrException {
    Set<MdrKey> membersSet = new HashSet<>();

    for (Result member : getMembers()) {
      if (member.getType().equals(type.name())) {
        MdrKey memberKey = new MdrKey(member.getId(), mdrConnection);
        membersSet.add(memberKey);
      }
    }

    return membersSet;
  }

  /**
   * Gets a list of record or group members.
   *
   * @return list of mdrclient.domain.Result
   * @throws MdrConnectionException the mdr connection exception
   * @throws ExecutionException     the execution exception
   * @throws MdrException           the MDR exception
   */
  public List<Result> getMembers() throws MdrConnectionException, ExecutionException, MdrException {
    if (members == null) {
      if (isRecord()) {
        members = mdrConnection.getMdrClient().getRecordMembers(toString(), "en",
            mdrConnection.getAccessTokenToken(), mdrConnection.getAuthUserId());
      } else if (isGroup()) {
        members = mdrConnection.getMdrClient().getMembers(toString(), "en",
            mdrConnection.getAccessTokenToken(), mdrConnection.getAuthUserId());
      } else {
        throw new MdrException("The MDRkey " + toString()
            + " is neither a record nor a group and thus cannot have members.");
      }
    }
    return members;
  }

  /**
   * Gets a list of mdr key labels.
   *
   * @return the label
   * @throws MdrConnectionException the mdr connection exception
   * @throws ExecutionException     the execution exception
   * @throws MdrException           the MDR exception
   */
  public List<Label> getLabel() throws MdrConnectionException, ExecutionException, MdrException {
    if (label == null) {
      if (isRecord()) {
        label = mdrConnection.getMdrClient().getRecordLabel(toString(), "en",
            mdrConnection.getAccessTokenToken(), mdrConnection.getAuthUserId());
      } else if (isGroup()) {
        label = mdrConnection.getMdrClient().getDataElementGroupLabel(toString(), "en",
            mdrConnection.getAccessTokenToken(), mdrConnection.getAuthUserId());
      } else {
        throw new MdrException(
            "The MDRkey " + toString()
                + " is neither a record or a group and thus cannot have record labels.");
      }
    }

    return label;
  }

  /**
   * Gets the definition.
   *
   * @return the definition
   * @throws MdrConnectionException      the mdr connection exception
   * @throws MdrInvalidResponseException the mdr invalid response exception
   * @throws ExecutionException          the execution exception
   */
  public Definition getDefinition()
      throws MdrConnectionException, MdrInvalidResponseException, ExecutionException {
    if (definition == null) {
      definition = mdrConnection.getMdrClient().getDataElementDefinition(toString(), "en",
          mdrConnection.getAccessTokenToken(), mdrConnection.getAuthUserId());
    }
    return definition;
  }

  /**
   * Gets this mdrkey's first definition as string.
   *
   * @return the mdr definition
   * @throws MdrConnectionException      the mdr connection exception
   * @throws ExecutionException          the execution exception
   * @throws MdrException                the OSSE exception
   * @throws MdrInvalidResponseException the mdr invalid response exception
   */
  public String getMdrDefinition()
      throws MdrConnectionException, ExecutionException, MdrException, MdrInvalidResponseException {
    if (isRecord() || isGroup()) {
      return getLabel().get(0).getDefinition();
    } else {
      return getDefinition().getDesignations().get(0).getDefinition();
    }
  }

  /**
   * gets this mdrkey's first designation as string.
   *
   * @return the mdr designation
   * @throws MdrConnectionException      the mdr connection exception
   * @throws ExecutionException          the execution exception
   * @throws MdrException                the OSSE exception
   * @throws MdrInvalidResponseException the mdr invalid response exception
   */
  public String getMdrDesignation()
      throws MdrConnectionException, ExecutionException, MdrException, MdrInvalidResponseException {
    if (isRecord() || isGroup()) {
      return getLabel().get(0).getDesignation();
    } else {
      return getDefinition().getDesignations().get(0).getDesignation();
    }
  }

  /**
   * gets this mdrkey's first designation as string.
   *
   * @return the mdr label
   * @throws MdrConnectionException      the mdr connection exception
   * @throws ExecutionException          the execution exception
   * @throws MdrException                the OSSE exception
   * @throws MdrInvalidResponseException the mdr invalid response exception
   */
  public String getMdrLabel()
      throws MdrConnectionException, ExecutionException, MdrException, MdrInvalidResponseException {
    return getMdrDesignation();
  }

  /**
   * Parses a string representation of a mdr key.
   *
   * @param mdrKey the mdr key
   * @return boolean if this is a real mdr key
   */
  public Boolean parseString(String mdrKey) {
    Pattern pattern = Pattern.compile(URN_PATTERN);
    Matcher matcher = pattern.matcher(mdrKey);

    if (!matcher.find()) {
      return false;
    }

    try {
      type = EnumElementType.valueOf(matcher.group(2).toUpperCase());
    } catch (IllegalArgumentException e) {
      return false;
    }

    namespace = matcher.group(1);
    id = matcher.group(3);
    version = matcher.group(4);

    return true;
  }

  /**
   * Returns a string representation of this mdr key.
   *
   * @return the string
   */
  @Override
  public String toString() {
    return "urn:" + namespace + ":" + type.toString().toLowerCase() + ":" + id + ":" + version;
  }

  /**
   * Returns a version of the MDRkey in which dots are replaced with one underscore and colons are
   * replaced with double underscores colons and dots are illegal characters in maps and JSF params
   * so we need to replace them in a way that our backend saver can recognize and re-replace with
   * the original character.
   *
   * @return the purified mdrkey as string
   */
  public String purify() {
    if (purified == null) {
      String mdrID = toString();
      mdrID = mdrID.replace(":", "__");
      mdrID = mdrID.replace(".", "_");
      purified = mdrID;
    }

    return purified;
  }

  /**
   * Returns the MDR key as we need it for the import/export XMLs.
   *
   * @return the string
   */
  public String forXml() {
    return Utils.upperCaseFirstChar(type.toString()) + "_" + id + "_" + version;
  }

  /**
   * gets the namespace part of this mdrkey.
   *
   * @return the string
   */
  public String namespaceForXml() {
    return "urn:" + namespace;
  }

  /**
   * checks if a mdrkey represents a record.
   *
   * @return the boolean
   */
  public Boolean isRecord() {
    return getType() == EnumElementType.RECORD;
  }

  /**
   * Load the whole Dataelement in order to reduce (costly) calls to the mdr.
   */
  public void loadDataElement()
      throws ExecutionException, MdrConnectionException, MdrInvalidResponseException {
    if (dataElement == null) {
      dataElement = mdrConnection.getMdrClient()
          .getDataElement(toString(), Locale.getDefault().getLanguage(),
              mdrConnection.getAccessTokenToken(), mdrConnection.getAuthUserId());
    }
  }

  /**
   * Returns the Validations of this dataelement.
   *
   * @return the Validations
   * @throws MdrConnectionException      the mdr connection exception
   * @throws MdrInvalidResponseException the mdr invalid response exception
   * @throws ExecutionException          the execution exception
   * @throws MdrException                the OSSE exception
   */
  public Validations getValidations()
      throws MdrConnectionException, MdrInvalidResponseException, ExecutionException, MdrException {
    if (isRecord()) {
      throw new MdrException(
          "The MDRkey " + toString() + " is a record and thus cannot have validators.");
    }

    if (validations == null) {
      if (dataElement == null) {
        validations = mdrConnection.getMdrClient()
            .getDataElementValidations(toString(), Locale.getDefault().getLanguage(),
                mdrConnection.getAccessTokenToken(), mdrConnection.getAuthUserId());
      } else {
        validations = dataElement.getValidation();
      }
    }

    return validations;
  }

  /**
   * Todo.
   *
   * @return Todo.
   * @throws ExecutionException          Todo.
   * @throws MdrConnectionException      Todo.
   * @throws MdrInvalidResponseException Todo.
   */
  public Catalogue getCatalogue()
      throws ExecutionException, MdrConnectionException, MdrInvalidResponseException {
    return mdrConnection.getMdrClient()
        .getDataElementCatalogue(toString(), Locale.getDefault().getLanguage(),
            mdrConnection.getAccessTokenToken(), mdrConnection.getAuthUserId());
  }

  /**
   * Retuns the slots of this dataelement.
   *
   * @return the slots
   * @throws MdrConnectionException      Todo.
   * @throws MdrInvalidResponseException Todo.
   * @throws ExecutionException          Todo.
   */
  public List<Slot> getSlots()
      throws MdrConnectionException, MdrInvalidResponseException, ExecutionException {
    if (slots == null) {
      if (isRecord()) {
        slots = mdrConnection.getMdrClient()
            .getRecordSlots(toString(), mdrConnection.getAccessTokenToken(),
                mdrConnection.getAuthUserId());
      } else if (isGroup()) {
        slots = mdrConnection.getMdrClient()
            .getDataElementGroupSlots(toString(), mdrConnection.getAccessTokenToken(),
                mdrConnection.getAuthUserId());
      } else {
        if (dataElement == null) {
          slots = mdrConnection.getMdrClient()
              .getDataElementSlots(toString(), mdrConnection.getAccessTokenToken(),
                  mdrConnection.getAuthUserId());
        } else {
          slots = dataElement.getSlots();
        }
      }
      slotMap = new HashMap<>();
      for (Slot slot : slots) {
        // TODO a better solution for trailing white spaces; question is, is that trailing at times
        //  maybe
        // intended?
        String slotKey = slot.getSlotName().trim();
        String value = slot.getSlotValue();
        if (slotMap.containsKey(slotKey)) {
          slotMap.get(slotKey).add(value);
        } else {
          ArrayList<String> values = new ArrayList<>();
          values.add(value);
          slotMap.put(slotKey, values);
        }
      }
    }

    return slots;
  }

  /**
   * Gets the slot value of a key, with lazyloading.
   *
   * @param slotKey the key
   * @return the value
   * @throws MdrConnectionException      Todo.
   * @throws ExecutionException          Todo.
   * @throws MdrInvalidResponseException Todo.
   * @throws MdrException                Todo.
   */
  @Deprecated
  public String getSlotValueLazyLoading(String slotKey) throws MdrConnectionException,
      ExecutionException, MdrInvalidResponseException, MdrException {
    if (slotMap == null) {
      getSlots();
    }

    return getSlotValue(slotKey);

  }

  /**
   * Gets the slot values of a key.
   *
   * @param slotKey the key
   * @return the values or null if there is no value for this slot
   */
  public List<String> getSlotValues(String slotKey)
      throws MdrConnectionException, ExecutionException, MdrInvalidResponseException, MdrException {
    if (slotMap == null) {
      getSlots();
    }
    return slotMap.get(slotKey);
  }

  /**
   * Gets the only slot value of a key.
   *
   * @param slotKey the key
   * @return the value or null if there is no value for this slot
   * @throws MdrException When there are more then one value for this slot.
   */
  public String getSingleSlotValue(String slotKey)
      throws MdrConnectionException, ExecutionException, MdrInvalidResponseException, MdrException {
    if (slotMap == null) {
      getSlots();
    }

    List<String> values = slotMap.get(slotKey);
    if (values == null || values.size() == 0) {
      return null;
    } else if (values.size() > 1) {
      throw new MdrException("There is more then one value for this slot name");
    }

    return values.get(0);

  }

  /**
   * Gets the slot value of a key.
   *
   * @param slotKey the key
   * @return the value or null if there is no value for this slot
   */
  @Deprecated
  public String getSlotValue(String slotKey) {
    List<String> values = slotMap.get(slotKey);
    if (values == null || values.size() == 0) {
      return null;
    }
    return values.get(values.size() - 1);
  }

  /**
   * Gets the namespace.
   *
   * @return the namespace
   */
  public String getNamespace() {
    return namespace;
  }

  /**
   * Sets the namespace.
   *
   * @param namespace the namespace to set
   */
  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  /**
   * Gets the type.
   *
   * @return the type
   */
  public EnumElementType getType() {
    return type;
  }

  /**
   * Sets the type.
   *
   * @param type the type to set
   */
  public void setType(EnumElementType type) {
    this.type = type;
  }

  /**
   * Gets the id.
   *
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the id.
   *
   * @param id the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the version.
   *
   * @return the version
   */
  public String getVersion() {
    return version;
  }

  /**
   * Sets the version.
   *
   * @param version the version to set
   */
  public void setVersion(String version) {
    this.version = version;
  }

  @Override
  public int hashCode() {
    return this.toString().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }

    return this.toString().equals(obj.toString());
  }

  /**
   * Checks if the mdrKey represents a dataelementgroup.
   *
   * @return true if it is a group.
   */
  public boolean isGroup() {
    return EnumElementType.DATAELEMENTGROUP.equals(this.type);
  }

  @Override
  public int compareTo(MdrKey o) {
    return this.toString().compareTo(o.toString());
  }

  public String getKeyWithoutVersion() {
    return "urn:" + namespace + ":" + type.toString().toLowerCase() + ":" + id;
  }
}
