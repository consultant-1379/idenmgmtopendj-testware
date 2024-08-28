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
import com.ericsson.nms.security.taf.test.data.UserPasswordPolicyData;
import com.ericsson.nms.security.taf.test.teststep.OpenDJTestSteps;

public class OpenDJTestFlow {

    public static final String VERIFY_USERS_PASSWORD_POLICIES_FLOW = "verifyUsersPasswordPoliciesFlow";
    public static final String VERIFY_USERS_PASSWORD_EXPIRATION_FLOW = "verifyUsersPasswordExpirationFlow";

    @Inject
    private OpenDJTestSteps openDJTestSteps;

    public TestStepFlow verifyUsersPasswordExpirePolicies() {
        return flow(VERIFY_USERS_PASSWORD_POLICIES_FLOW)//
                .addTestStep(annotatedMethod(openDJTestSteps, OpenDJTestSteps.VERIFY_POLICY_SUBENTRY))//
                .withDataSources(dataSource(UserPasswordPolicyData.USER_PASSWORD_POLICY_DATA))//
                .build();
    }

    public TestStepFlow verifyUsersPasswordExpiration() {
        return flow(VERIFY_USERS_PASSWORD_EXPIRATION_FLOW)//
                .addTestStep(annotatedMethod(openDJTestSteps, OpenDJTestSteps.VERIFY_PASSWORD_EXPIRATION_TIME))//
                .withDataSources(dataSource(UserPasswordPolicyData.USER_PASSWORD_EXPIRATION_DATA))//
                .build();
    }

}
