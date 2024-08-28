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

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;
import javax.inject.Inject;
import javax.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.TestContext;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.nms.security.taf.test.operators.IdentityManagementServicesApiOperator;

public class ProxyUserTestSteps {

    public static final String PROXY_USERS = "proxyUsers";

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyUserTestSteps.class);

    public static final String CREATE_PROXY_USER_TEST_STEP = "createProxyUserTestStep";
    public static final String REMOVE_PROXY_USER_TEST_STEP = "removeProxyUserTestStep";

    @Inject
    private Provider<IdentityManagementServicesApiOperator> provider;

    @Inject
    private TestContext context;

    @TestStep(id = CREATE_PROXY_USER_TEST_STEP)
    public void createProxyUserTestStep() {
        final IdentityManagementServicesApiOperator secservApiOperator = provider.get();
        final String proxyUserDn = secservApiOperator.createProxyUser();
        assertNotNull(proxyUserDn);
        final Set<String> proxyUsers = context.getAttribute(PROXY_USERS);
        proxyUsers.add(proxyUserDn);
        LOGGER.info("proxy user <{}> has been created", proxyUserDn);

    }

    @TestStep(id = REMOVE_PROXY_USER_TEST_STEP)
    public void removeProxyUserTestStep() {
        final Set<String> proxyUsers = context.getAttribute(PROXY_USERS);
        for (final String proxyUserDn : proxyUsers) {
            LOGGER.info("proxy user to remove: <{}>", proxyUserDn);
            final IdentityManagementServicesApiOperator secservApiOperator = provider.get();
            final boolean removed = secservApiOperator.removeProxyUser(proxyUserDn);
            assertTrue(removed);
        }
    }

}
