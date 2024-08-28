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
package com.ericsson.nms.security.taf.test.flow;

import static com.ericsson.cifwk.taf.scenario.TestScenarios.annotatedMethod;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.dataSource;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.flow;

import javax.inject.Inject;

import com.ericsson.cifwk.taf.scenario.TestStepFlow;
import com.ericsson.nms.security.taf.test.teststep.TempAmosTestStep;
import com.ericsson.oss.testware.enmbase.data.CommonDataSources;

public class TempAmosTestFlow {

    public static final String CREATE_TEMP_AMOS_USER_TEST_FLOW = "createTempAmosUserTestFlow";

    @Inject
    private TempAmosTestStep tempAmosTestStep;

    public TestStepFlow createTempAmosUser() {
        return flow(CREATE_TEMP_AMOS_USER_TEST_FLOW)//
                .addTestStep(annotatedMethod(tempAmosTestStep, TempAmosTestStep.CREATE_TEMP_AMOS_USER_TEST_STEP))//
                .withDataSources(dataSource(CommonDataSources.USERS_TO_CREATE))//
                .build();
    }
}
