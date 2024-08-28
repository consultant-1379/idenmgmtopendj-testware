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
import static com.ericsson.cifwk.taf.scenario.TestScenarios.flow;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.fromTestStepResult;
import static com.ericsson.nms.security.taf.test.teststep.UtilityTestSteps.CLOSE_CONNECTION;
import static com.ericsson.nms.security.taf.test.teststep.UtilityTestSteps.GET_COM_INF_LDAP_ROOT_SUFFIX;
import static com.ericsson.nms.security.taf.test.teststep.UtilityTestSteps.GET_CRIP_LDAP_ADMIN_PASSWORD;
import static com.ericsson.nms.security.taf.test.teststep.UtilityTestSteps.GET_LDAP_ADMIN_PASSWORD;
import static com.ericsson.nms.security.taf.test.teststep.UtilityTestSteps.PARAM;

import javax.inject.Inject;

import com.ericsson.cifwk.taf.scenario.api.TestStepFlowBuilder;
import com.ericsson.nms.security.taf.test.teststep.UtilityTestSteps;

public class UtilityTestFlow {

    @Inject
    private UtilityTestSteps utilityTestSteps;

    public TestStepFlowBuilder getLdapAdminPasswordAndComInfLdapSuffix() {
        return flow("Get LdapAdminPassword")
                .addTestStep(annotatedMethod(utilityTestSteps, GET_CRIP_LDAP_ADMIN_PASSWORD))
                .addTestStep(annotatedMethod(utilityTestSteps, GET_LDAP_ADMIN_PASSWORD).withParameter(PARAM,
                        fromTestStepResult(GET_CRIP_LDAP_ADMIN_PASSWORD)))
                .addTestStep(annotatedMethod(utilityTestSteps, GET_COM_INF_LDAP_ROOT_SUFFIX))
                .addTestStep(annotatedMethod(utilityTestSteps, CLOSE_CONNECTION));
    }


}
