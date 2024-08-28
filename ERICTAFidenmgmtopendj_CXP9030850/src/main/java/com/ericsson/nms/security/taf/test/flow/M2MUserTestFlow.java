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
import com.ericsson.nms.security.taf.test.data.M2mUser;
import com.ericsson.nms.security.taf.test.teststep.M2MUserTestSteps;

public class M2MUserTestFlow {

    private static final String REMOVE_M2M_USER_TEST_FLOW = "removeM2mUserTestFlow";
    private static final String CREATE_M2M_USER_TEST_FLOW = "createM2mUserTestFlow";
    private static final String CLEAN_UP_M2M_USER_TEST_FLOW = "cleanUpM2mUserTestFlow";

    @Inject
    private M2MUserTestSteps m2mUserTestSteps;

    public TestStepFlow createM2mUserTestFlow() {
        return flow(CREATE_M2M_USER_TEST_FLOW)//
                .addTestStep(annotatedMethod(m2mUserTestSteps, M2MUserTestSteps.CREATE_M2M_USER_TEST_STEP))//
                .withDataSources(dataSource(M2mUser.M2M_USER_DATA))//
                .build();
    }

    public TestStepFlow removeM2mUserTestFlow() {
        return flow(REMOVE_M2M_USER_TEST_FLOW)//
                .addTestStep(annotatedMethod(m2mUserTestSteps, M2MUserTestSteps.REMOVE_M2M_USER_TEST_STEP))//
                .withDataSources(dataSource(M2mUser.M2M_USER_DATA))//
                .build();
    }

    public TestStepFlow cleanUpM2mUserTestFlow() {
        return flow(CLEAN_UP_M2M_USER_TEST_FLOW)//
                .addTestStep(annotatedMethod(m2mUserTestSteps, M2MUserTestSteps.CLEAN_UP_M2M_USER_TEST_STEP))//
                .withDataSources(dataSource(M2mUser.M2M_USER_DATA))//
                .build();
    }
}
