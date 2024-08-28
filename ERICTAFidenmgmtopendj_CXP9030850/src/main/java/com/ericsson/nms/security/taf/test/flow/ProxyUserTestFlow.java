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

import javax.inject.Inject;

import com.ericsson.cifwk.taf.scenario.TestStepFlow;
import com.ericsson.nms.security.taf.test.teststep.ProxyUserTestSteps;

public class ProxyUserTestFlow {

    private static final String REMOVE_PROXY_USER_TEST_FLOW = "removeProxyUserTestFlow";
    private static final String CREATE_PROXY_USER_TEST_FLOW = "createProxyUserTestFlow";

    @Inject
    private ProxyUserTestSteps proxyUserTestSteps;

    public TestStepFlow createProxyUserTestFlow() {
        return flow(CREATE_PROXY_USER_TEST_FLOW)//
                .addTestStep(annotatedMethod(proxyUserTestSteps, ProxyUserTestSteps.CREATE_PROXY_USER_TEST_STEP))//
                .build();
    }

    public TestStepFlow removeProxyUserTestFlow() {
        return flow(REMOVE_PROXY_USER_TEST_FLOW)//
                .addTestStep(annotatedMethod(proxyUserTestSteps, ProxyUserTestSteps.REMOVE_PROXY_USER_TEST_STEP))//
                .build();
    }
}
