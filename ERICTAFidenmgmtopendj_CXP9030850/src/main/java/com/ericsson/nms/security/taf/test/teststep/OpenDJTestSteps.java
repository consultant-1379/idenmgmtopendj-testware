/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.taf.test.teststep;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import javax.inject.Inject;
import javax.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.TestContext;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.nms.security.taf.test.data.UserPasswordPolicyData;
import com.ericsson.nms.security.taf.test.operators.OpenDJOperator;

public class OpenDJTestSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenDJTestSteps.class);

    public static final String VERIFY_POLICY_SUBENTRY = "verifyPolicySubentry";
    public static final String VERIFY_PASSWORD_EXPIRATION_TIME = "verifyPasswordExpirationTime";

    @Inject
    private Provider<OpenDJOperator> provider;
    @Inject
    private TestContext context;

    @TestStep(id = VERIFY_POLICY_SUBENTRY)
    public void verifyPolicy(@Input(UserPasswordPolicyData.USER_PASSWORD_POLICY_DATA) final UserPasswordPolicyData userPasswordPolicyData) {
        LOGGER.info("inputData: {}", userPasswordPolicyData);
        final OpenDJOperator openDJOperator = provider.get();
        String userString = userPasswordPolicyData.getUser();
        if (userString.contains("ProxyAccount*")) {
            userString = returnProxyAccountUserName();
        }
        final String pwdPolicySubentry = openDJOperator.ldapsearch(userString, "pwdPolicySubentry");
        assertThat(pwdPolicySubentry.contains(userPasswordPolicyData.getExaminingString()));
    }

    @TestStep(id = VERIFY_PASSWORD_EXPIRATION_TIME)
    public void verifyPasswordExpirationTime(
            @Input(UserPasswordPolicyData.USER_PASSWORD_EXPIRATION_DATA) final UserPasswordPolicyData userPasswordPolicyData) {
        LOGGER.info("inputData: {}", userPasswordPolicyData);
        final OpenDJOperator openDJOperator = provider.get();
        String userString = userPasswordPolicyData.getUser();
        if (userString.contains("ProxyAccount*")) {
            userString = returnProxyAccountUserName();
        }
        final String pwdPolicySubentry = openDJOperator.ldapsearch(userString, "ds-pwp-password-expiration-time");
        assertThat(pwdPolicySubentry.contains("ds-pwp-password-expiration-time"))
                .as(userString + " has ds-pwp-password-expiration-time attribute, search result: " + pwdPolicySubentry)
                .isEqualTo(userPasswordPolicyData.getShouldContainPasswordExpirationTimeAttribute());
    }

    String returnProxyAccountUserName() {
        final HashSet<String> proxyUserSet = context.getAttribute(ProxyUserTestSteps.PROXY_USERS);
        String split[] = proxyUserSet.toString().split(",");
        String proxyUserString = split[0].substring(1);  // to cut the first bracket
        LOGGER.info("ProxyUserString = " + proxyUserString);
        return proxyUserString;
    }

}
