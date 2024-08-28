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

import javax.inject.Inject;
import javax.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.nms.security.taf.test.data.M2mUser;
import com.ericsson.nms.security.taf.test.operators.IdentityManagementServicesApiOperator;

public class M2MUserTestSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(M2MUserTestSteps.class);

    public static final String CREATE_M2M_USER_TEST_STEP = "createM2MUserTestStep";
    public static final String REMOVE_M2M_USER_TEST_STEP = "removeM2MUserTestStep";
    public static final String CLEAN_UP_M2M_USER_TEST_STEP = "cleanUpM2MUserTestStep";

    @Inject
    private Provider<IdentityManagementServicesApiOperator> provider;

    @TestStep(id = CREATE_M2M_USER_TEST_STEP)
    public void createM2MUserTestStep(@Input(M2mUser.M2M_USER_DATA) final M2mUser m2mUser) {
        LOGGER.info("inputData: {}", m2mUser);
        final IdentityManagementServicesApiOperator secservApiOperator = provider.get();
        final String m2mUserName = secservApiOperator.createM2MUser(m2mUser.getUserName(), m2mUser.getGroupName(), m2mUser.getHomeDir(),
                m2mUser.getValidDays());
        assertNotNull(m2mUserName);
        LOGGER.info("M2M User <{}> has been created", m2mUserName);
    }

    @TestStep(id = REMOVE_M2M_USER_TEST_STEP)
    public void removeM2MUserTestStep(@Input(M2mUser.M2M_USER_DATA) final M2mUser m2mUser) {
        LOGGER.info("inputData: {}", m2mUser);
        final IdentityManagementServicesApiOperator secservApiOperator = provider.get();
        final boolean removed = secservApiOperator.removeM2MUser(m2mUser.getUserName());
        assertTrue(removed);
        LOGGER.info("M2M User <{}> has been removed", m2mUser.getUserName());
    }

    @TestStep(id = CLEAN_UP_M2M_USER_TEST_STEP)
    public void cleanUpM2MUserTestStep(@Input(M2mUser.M2M_USER_DATA) final M2mUser m2mUser) {
        LOGGER.info("Clean up input: {}", m2mUser);
        final IdentityManagementServicesApiOperator secservApiOperator = provider.get();
        secservApiOperator.removeM2MUser(m2mUser.getUserName());
    }

}
