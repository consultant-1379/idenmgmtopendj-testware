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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator {
    private Matcher matcher;
    private Pattern pattern;

    public boolean validate(final String str, final String[] regexes) {

        for (final String regex : regexes) {
            pattern = Pattern.compile(regex);
            matcher = pattern.matcher(str);
            if (!matcher.matches()) {
                return false;
            }
        }
        return true;
    }
}
