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

import javax.inject.Inject;
import javax.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.nms.security.taf.test.operators.GIMOperator;
import com.ericsson.oss.testware.enmbase.data.CommonDataSources;
import com.ericsson.oss.testware.enmbase.data.ENMUser;

public class TempAmosTestStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(TempAmosTestStep.class);

    public static final String CREATE_TEMP_AMOS_USER_TEST_STEP = "createTempAmosUserTestStep";

    @Inject
    private Provider<GIMOperator> provider;

    @TestStep(id = CREATE_TEMP_AMOS_USER_TEST_STEP)
    public void createTempAmosUserTestStep(@Input(CommonDataSources.USERS_TO_CREATE) final ENMUser user) {

        LOGGER.info("inputData: {}", user.getUsername());
        final GIMOperator gimOp = provider.get();
        gimOp.createTempAmosUser(user.getUsername());
    }
}
