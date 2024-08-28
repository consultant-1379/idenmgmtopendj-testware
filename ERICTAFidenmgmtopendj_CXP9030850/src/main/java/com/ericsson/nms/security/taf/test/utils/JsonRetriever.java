/*  ------------------------------------------------------------------------------
 *  ******************************************************************************
 *  * COPYRIGHT Ericsson 2016
 *  *
 *  * The copyright to the computer program(s) herein is the property of
 *  * Ericsson Inc. The programs may be used and/or copied only with written
 *  * permission from Ericsson Inc. or in accordance with the terms and
 *  * conditions stipulated in the agreement/contract under which the
 *  * program(s) have been supplied.
 *  ******************************************************************************
 *  ------------------------------------------------------------------------------
 */
package com.ericsson.nms.security.taf.test.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class JsonRetriever {

    private String jsonString;
    private String[] nestedKeys;

    public JsonRetriever() {
    }

    public JsonRetriever(final String jsonString, final String[] keys) {
        this.jsonString = jsonString;
        nestedKeys = keys;
    }

    public void setJsonString(final String str) {
        this.jsonString = str;
    }

    public void setNestedKeys(final String[] keys) {
        this.nestedKeys = keys;
    }

    public String getValue(final String lastKey) {
        String value = null;
        final JSONParser parser = new JSONParser();
        try {
            final Object obj = parser.parse(jsonString);
            JSONObject jsonObj = (JSONObject) obj;
            if (nestedKeys != null) {
                for (final String key : nestedKeys) {
                    jsonObj = (JSONObject) jsonObj.get(key);
                }
            }
            value = (String) jsonObj.get(lastKey);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return value;
    }
}
