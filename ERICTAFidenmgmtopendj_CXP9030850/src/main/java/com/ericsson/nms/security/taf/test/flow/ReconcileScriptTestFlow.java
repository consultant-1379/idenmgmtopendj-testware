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
import com.ericsson.nms.security.taf.test.teststep.ReconcileScriptTestSteps;

/**
 * Created by etomszu on 5/25/16.
 */
public class ReconcileScriptTestFlow {

    public static final String RECONCILE_SCRIPT_FLOW = "reconcileScriptFlow";

    @Inject
    private ReconcileScriptTestSteps reconcileScriptTestSteps;

    public TestStepFlow reconcileScriptTestStepFlow() {
        final TestStepFlow reconsileScript = flow(RECONCILE_SCRIPT_FLOW)//
                .addTestStep(annotatedMethod(reconcileScriptTestSteps, ReconcileScriptTestSteps.RECONCILE_TEST_STEP)).build();

        return reconsileScript;
    }
}