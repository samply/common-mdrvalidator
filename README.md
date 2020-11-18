# MDR Validation Project

## General information

This is a lib to validate value against the MDR.

## Usage

First prepare the MDR connection. 
You need to provide:
* the URL to the MDR to be used
* the AUTH userId and keyId
* the AUTH URL to be used
* the privateKey used there (for signature)
* the MDR namespace
* the samply proxyConfiguration

<code>

    MdrConnection mdrConnection = new MdrConnection(mdrUrl, authUserId, keyId, authUrl, privateKeyBase64, nameSpace, proxyConfiguration);
</code>

Then initiate the Validator and read out the members of the namespace

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

 ## License
        
 Copyright 2020 The Samply Development Community
        
 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
        
 http://www.apache.org/licenses/LICENSE-2.0
        
 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 
