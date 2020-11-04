# MDR Validation Project

## General information

This is a lib to validate values against the MDR.

## Usage

First prepare the MDR connection. You need to provide the URL to the MDR to be used, the AUTH userId and keyId, the AUTH URL to be used, the privateKey used there (for signature),
the MDR namespace, and a samply proxyConfiguration

<code>
MdrConnection mdrConnection = new MdrConnection(mdrUrl, authUserId, keyId, authUrl, privateKeyBase64, nameSpace, proxyConfiguration);
</code>

The initiate the Validator and read out the members of the namespace

<code>
MDRValidator val = new MDRValidator(mdrConnection);
val.initMembers();
</code>

Now you can simply validate, by calling 
<code>
val.validate(mdrKey, value)
</code>

Example:
<code>
String mdrKey = "urn:mdr:dataelement:1:1";
        
        if(!val.validate(mdrKey, 900)) {
            for(ErrorMessage error: val.getErrorMessage(mdrKey)) {
                System.out.println(error.getDesignation()+ " : "+error.getDefinition());
            }
        }
        
        assertFalse(val.validate(mdrKey, 900));
        assertTrue(val.validate(mdrKey, 90));
</code>

As you can see you can get the error messages defined in MDR by calling

<code>
val.getErrorMessage(mdrKey)
</code>

## Build

Use maven to build the jar:

```
mvn clean install
```
