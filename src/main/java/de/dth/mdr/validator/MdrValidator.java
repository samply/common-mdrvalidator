package de.dth.mdr.validator;

import de.dth.mdr.validator.enums.EnumValidatorType;
import de.dth.mdr.validator.exception.MdrException;
import de.dth.mdr.validator.exception.ValidatorException;
import de.dth.mdr.validator.utils.Utils;
import de.dth.mdr.validator.validators.CatalogueValidator;
import de.dth.mdr.validator.validators.Validator;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.MdrInvalidResponseException;
import de.samply.common.mdrclient.domain.Catalogue;
import de.samply.common.mdrclient.domain.Code;
import de.samply.common.mdrclient.domain.Designation;
import de.samply.common.mdrclient.domain.ErrorMessage;
import de.samply.common.mdrclient.domain.Result;
import de.samply.common.mdrclient.domain.Slot;
import de.samply.common.mdrclient.domain.Validations;
import de.samply.string.util.StringUtil;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A MDR validator for DTH. TODO: Support for limited list of MDRkeys.
 */
public class MdrValidator implements Closeable {

  /**
   * List of ambiguous catalogues with which we cannot transform designations to codes.
   */
  private static List<String> ambiguousCatalogues;
  private final Logger logger = LoggerFactory.getLogger(MdrValidator.class);
  private final String caseSensitive = "caseSensitive";
  /**
   * Info if the last value was validated by catalogue designation instead of code.
   */
  private Boolean lastValidatedWasCatalogueDesignation = false;
  /**
   * Matrix that contains the MDR Validations entries per MDRKey.
   */
  private HashMap<String, Validations> validationMatrix;
  /**
   * Matrix that contains the MDR keys of the DataElementGroups as a String and their members.
   */
  private HashMap<String, Set<String>> groupMatrix;
  /**
   * Matrix that contains the FlatCatalogue entry per MDRKey.
   */
  private HashMap<String, FlatCatalogue> flatCatalogueMatrix;
  /**
   * Stores a Slot entry -> MdrKey Matrix to quickly find the dataelement belonging to a slot.
   */
  private HashMap<String, HashMap<String, MdrKey>> slotMdrKeyMatrix;
  /**
   * The MDR Connection.
   */
  private MdrConnection mdrConnection;
  private List<MdrKey> baseKeys = new ArrayList<>();

  public MdrValidator(MdrConnection mdrConnection, String... dataelementgroups)
      throws MdrConnectionException, ExecutionException, MdrException, MdrInvalidResponseException {
    this(mdrConnection, false, dataelementgroups);

  }

  /**
   * Todo.
   *
   * @param mdrConnection       Todo.
   * @param preLoadDataelements Todo.
   * @param dataelementgroups   Todo.
   * @throws MdrConnectionException      Todo.
   * @throws ExecutionException          Todo.
   * @throws MdrException                Todo.
   * @throws MdrInvalidResponseException Todo.
   */
  public MdrValidator(MdrConnection mdrConnection, boolean preLoadDataelements,
      String... dataelementgroups)
      throws MdrConnectionException, ExecutionException, MdrException, MdrInvalidResponseException {
    this.mdrConnection = mdrConnection;
    validationMatrix = new HashMap<>();
    groupMatrix = new HashMap<>();
    flatCatalogueMatrix = new HashMap<>();
    slotMdrKeyMatrix = new HashMap<>();
    List<String> groupAndSubgroupMembers = new ArrayList<>();

    if (dataelementgroups.length == 0) {
      initMembers(preLoadDataelements);
    } else {
      for (String groupKey : dataelementgroups) {
        groupAndSubgroupMembers = Utils
            .getElementsFromGroupAndSubgroups(mdrConnection.getMdrClient(), "en",
                groupAndSubgroupMembers, groupKey);
        for (String key : groupAndSubgroupMembers) {
          MdrKey baseKey = new MdrKey(key, mdrConnection);
          baseKeys.add(baseKey);
          logger.info("Reading base key " + key);
          initMember(baseKey, preLoadDataelements);
        }
      }
    }

  }

  /**
   * Todo.
   *
   * @return Todo.
   */
  public static List<String> getAmbiguousCatalogues() {
    if (ambiguousCatalogues == null) {
      ambiguousCatalogues = new ArrayList<>();
    }
    return ambiguousCatalogues;
  }

  /**
   * The root elements of the validator.
   */
  public List<MdrKey> getBaseKeys() {
    return baseKeys;
  }

  /**
   * Initializes one mdrKey without preloading the dataelement.
   *
   * @param mdrKey the key to initialize
   * @throws ExecutionException          Todo.
   * @throws MdrConnectionException      Todo.
   * @throws MdrException                Todo.
   * @throws MdrInvalidResponseException v
   */
  private void initMember(MdrKey mdrKey)
      throws MdrConnectionException, ExecutionException, MdrInvalidResponseException, MdrException {
    initMember(mdrKey, false);
  }

  /**
   * Initializes one mdrKey.
   *
   * @param mdrKey             the key to initialize
   * @param preLoadDataelement should the whole dataelement be preloaded instead of loading
   *                           validations and slots separately
   * @throws ExecutionException          Todo.
   * @throws MdrConnectionException      Todo.
   * @throws MdrException                Todo.
   * @throws MdrInvalidResponseException Todo.
   */
  private void initMember(MdrKey mdrKey, boolean preLoadDataelement)
      throws MdrConnectionException, ExecutionException, MdrInvalidResponseException, MdrException {
    if (mdrKey.isRecord()) {
      List<Result> members = mdrConnection.getMdrClient().getRecordMembers(mdrKey.toString(),
          Locale.getDefault().getLanguage(), mdrConnection.getAccessTokenToken(),
          mdrConnection.getAuthUserId());

      for (Result result : members) {
        //Going recursively through the child keys
        initMember(new MdrKey(result.getId(), mdrConnection), preLoadDataelement);
      }

      //TODO: Disabled for now, because "find mdrkey by slotname" would return the record in most
      // cases. We need to clean up the MDR record slot names or build a real functionality to
      // support validation "as record"
      //readSlots(mdrKey);
      return;
    }

    if (mdrKey.isGroup()) {
      List<Result> members = mdrConnection.getMdrClient().getMembers(mdrKey.toString(),
          Locale.getDefault().getLanguage(), mdrConnection.getAccessTokenToken(),
          mdrConnection.getAuthUserId());

      Set<String> children = new HashSet<>();
      //Going recursively through the child keys
      for (Result result : members) {
        MdrKey child = new MdrKey(result.getId(), mdrConnection);
        children.add(child.toString());
        initMember(child, preLoadDataelement);
      }
      groupMatrix.put(mdrKey.toString(), children);
      readSlots(mdrKey);
      return;
    }

    if (preLoadDataelement) {
      mdrKey.loadDataElement();
    }

    validationMatrix.put(mdrKey.toString(), mdrKey.getValidations());

    if ("CATALOG".equalsIgnoreCase(mdrKey.getValidations().getValueDomainType())) {
      flatCatalogueMatrix.put(mdrKey.toString(), getFlattenedCatalogue(mdrKey.getCatalogue()));
    }
    readSlots(mdrKey);
  }

  /**
   * Loads the members of the given namespace.
   *
   * @throws ExecutionException          Todo.
   * @throws MdrConnectionException      Todo.
   * @throws MdrInvalidResponseException Todo.
   * @throws MdrException                Todo.
   */
  private void initMembers()
      throws ExecutionException, MdrConnectionException, MdrInvalidResponseException, MdrException {
    initMembers(false);
  }

  /**
   * Loads the members of the given namespace.
   *
   * @param preLoadDataelements should the whole dataelements be preloaded instead of loading
   *                            validations and slots separately
   * @throws ExecutionException          Todo.
   * @throws MdrConnectionException      Todo.
   * @throws MdrInvalidResponseException Todo.
   * @throws MdrException                Todo.
   */
  private void initMembers(boolean preLoadDataelements)
      throws ExecutionException, MdrConnectionException, MdrInvalidResponseException, MdrException {
    // get all mdrkeys from our namespaces
    List<Result> namespaceMembers = new ArrayList<>();
    for (String namespace : mdrConnection.getNamespaces()) {
      namespaceMembers.addAll(mdrConnection.getMdrClient().getNamespaceMembers(
          Locale.getDefault().getLanguage(),
          mdrConnection.getAccessTokenToken(),
          mdrConnection.getAuthUserId(),
          namespace));
    }

    for (Result member : namespaceMembers) {
      MdrKey mdrKey = new MdrKey(member.getId(), mdrConnection);
      initMember(mdrKey, preLoadDataelements);
    }
  }

  /**
   * Reads out the slots of a mdrkey and puts them into our slotMdrKeyMatrix.
   *
   * @param mdrKey Todo.
   * @throws MdrConnectionException      Todo.
   * @throws ExecutionException          Todo.
   * @throws MdrInvalidResponseException Todo.
   */
  private void readSlots(MdrKey mdrKey)
      throws MdrConnectionException, ExecutionException, MdrInvalidResponseException {
    for (Slot slot : mdrKey.getSlots()) {
      if (slotMdrKeyMatrix.containsKey(slot.getSlotName())) {
        slotMdrKeyMatrix.get(slot.getSlotName()).put(slot.getSlotValue(), mdrKey);
      } else {
        HashMap<String, MdrKey> temp = new HashMap<>();
        temp.put(slot.getSlotValue(), mdrKey);
        slotMdrKeyMatrix.put(slot.getSlotName(), temp);
      }
    }
  }

  /**
   * Validates the value against what is defined for its mdrkey.
   *
   * @param mdrKey Todo.
   * @param value  Todo.
   * @return Todo.
   * @throws ValidatorException Todo.
   */
  public boolean validate(String mdrKey, Object value) throws ValidatorException {
    // reset catalog info
    lastValidatedWasCatalogueDesignation = false;

    if (!validationMatrix.containsKey(mdrKey)) {
      throw new ValidatorException("The key " + mdrKey + " does not exist in our namespace.");
    }

    String validatorType = validationMatrix.get(mdrKey).getValueDomainType();
    if ("described".equalsIgnoreCase(validatorType)) {
      validatorType = validationMatrix.get(mdrKey).getValidationType();
      if (validatorType.equalsIgnoreCase("NONE") && validationMatrix.get(mdrKey).getDatatype()
          .equalsIgnoreCase("STRING")) {
        validatorType = "STRING";
      }
    }
    validatorType = validatorType.toUpperCase();

    // no validator type, so this element is valid for anything
    if ("NONE".equalsIgnoreCase(validatorType)) {
      return true;
    }

    try {
      EnumValidatorType type = EnumValidatorType.valueOf(validatorType);

      Validator validator = ValidatorFactory
          .getValidator(type, validationMatrix.get(mdrKey).getUnitOfMeasure(),
              flatCatalogueMatrix.get(mdrKey));
      if (validator == null) {
        throw new ValidatorException(
            "No validator found for the key " + mdrKey + " of type " + type.name() + ".");
      }

      MdrKey key = new MdrKey(mdrKey, mdrConnection);
      String caseSensitiveString = key.getSingleSlotValue(caseSensitive);
      if (caseSensitiveString != null && !Boolean.parseBoolean(caseSensitiveString.trim())) {
        validator.setCaseSensitive(false);
      }

      // validate!
      boolean validated = validator.validate(validationMatrix.get(mdrKey), value);

      // if a catalogue was validated, note if it was done by designation instead of code
      if (validated && validator instanceof CatalogueValidator) {
        lastValidatedWasCatalogueDesignation = ((CatalogueValidator) validator)
            .getWasValidatedDesignation();
      }

      // just for debugging purposes
      if (!validated) {
        if (validator instanceof CatalogueValidator) {
          logger.debug("CatalogueValidator says no to '" + value + "'");
          logger.debug("Catalogue accepted codes are : " + StringUtil
              .join(((CatalogueValidator) validator).getFlatCatalogue().getValidCodes(), ","));
        }
      }

      return validated;
    } catch (IllegalArgumentException e) {
      throw new ValidatorException(
          "The validation type " + validatorType + " does not exist for mdr element " + mdrKey
              + ".");
    } catch (MdrException | ExecutionException | MdrInvalidResponseException
        | MdrConnectionException e) {
      throw new ValidatorException(e);
    }
  }

  /**
   * Returns the list of error messages defined in MDR for a mdrKey.
   *
   * @param mdrKey Todo.
   * @return List of ErrorMessage
   */
  public List<ErrorMessage> getErrorMessage(String mdrKey) {
    if (validationMatrix.get(mdrKey).getErrorMessages() != null
        && !validationMatrix.get(mdrKey).getErrorMessages().isEmpty()) {
      return validationMatrix.get(mdrKey).getErrorMessages();
    }
    return null;
  }

  /**
   * Validates if a certain DataElementGroup exists.
   *
   * @param groupKey The URN of the DataElementGroup
   * @return true if the DataElementGroup with the given groupKey exists
   */
  public boolean checkGroupExists(String groupKey) {
    return groupMatrix.keySet().contains(groupKey);
  }

  /**
   * Validates if a DataElement belongs to a certain DataElementGroup.
   *
   * @param groupKey The URN of the DataElementGroup
   * @param mdrKey   The URN of the DataElement
   * @return true if the DataElement is member of the given Group
   */
  public boolean checkGroup(String groupKey, String mdrKey) {
    return groupMatrix.get(groupKey).contains(mdrKey);
  }

  /**
   * Returns the MdrKey of a DTH BioInfName Slot entry.
   *
   * @param slotValue the DTH BioInfName Slot value
   * @return Todo.
   */
  public MdrKey getMdrKeyOfSlot(String slotName, String slotValue) {
    if (slotMdrKeyMatrix.get(slotName) != null) {
      return slotMdrKeyMatrix.get(slotName).get(slotValue);
    } else {
      return null;
    }
  }

  /**
   * Runs through a catalogue and flattens it out flatCatalogues field is used as cache.
   *
   * @param catalogue Todo.
   * @return the flattened catalogue
   */
  public FlatCatalogue getFlattenedCatalogue(Catalogue catalogue) {

    List<String> validCodes = new ArrayList<>();
    HashMap<String, Code> validDesignationToCodes = new HashMap<>();

    for (Code code : catalogue.getCodes()) {
      if (code.getIsValid()) {
        validCodes.add(code.getCode());
        for (Designation designation : code.getDesignations()) {
          if (validDesignationToCodes.get(designation.getDesignation()) != null) {
            // some other code already has this designation, check if the code is the same

            if (!validDesignationToCodes.get(designation.getDesignation()).getCode()
                .equals(code.getCode())) {
              // this is a designation that belongs to another code already so we cannot really
              // decide which code it will belong to for the transformation
              getAmbiguousCatalogues().add(catalogue.getRoot().getIdentification().getUrn());
              logger.warn("Ambiguous code for designation " + designation.getDesignation()
                  + " in catalogue " + catalogue.getRoot().getIdentification().getUrn());
            }
          }
          validDesignationToCodes.put(designation.getDesignation(), code);

          if (validDesignationToCodes.get(designation.getDefinition()) != null) {
            // some other code already has this definition, check if the code is the same

            if (!validDesignationToCodes.get(designation.getDefinition()).getCode()
                .equals(code.getCode())) {
              // this is a designation that belongs to another code already so we cannot really
              // decide which code it will belong to for the transformation
              getAmbiguousCatalogues().add(catalogue.getRoot().getIdentification().getUrn());
              logger.warn("Ambiguous code for definition " + designation
                  .getDefinition() + " in catalogue " + catalogue
                  .getRoot()
                  .getIdentification()
                  .getUrn());
            }
          }
          validDesignationToCodes.put(designation.getDefinition(), code);
        }
      }
    }

    return new FlatCatalogue(validCodes, validDesignationToCodes);
  }

  /**
   * Returns the flatCatalogueMatrix. Used to find the code for a designation entry.
   *
   * @return Todo.
   */
  public HashMap<String, FlatCatalogue> getFlatCatalogueMatrix() {
    return flatCatalogueMatrix;
  }

  /**
   * Information if the last validation was done by a catalogue designation instead of code.
   *
   * @return Todo.
   */
  public Boolean getLastValidatedWasCatalogueDesignation() {
    return lastValidatedWasCatalogueDesignation;
  }

  /**
   * Todo.
   */
  public void close() {
    mdrConnection.close();
  }
}

