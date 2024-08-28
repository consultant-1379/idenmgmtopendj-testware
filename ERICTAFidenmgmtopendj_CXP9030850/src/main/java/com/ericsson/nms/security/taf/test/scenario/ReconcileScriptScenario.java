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
package com.ericsson.nms.security.taf.test.scenario;

import static com.ericsson.cifwk.taf.scenario.TestScenarios.runner;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.scenario;

import javax.inject.Inject;

import org.testng.annotations.Test;
import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.scenario.TestScenarioRunner;
import com.ericsson.cifwk.taf.scenario.api.ScenarioExceptionHandler;
import com.ericsson.oss.testware.enmbase.scenarios.DebugLogger;
import com.ericsson.oss.testware.enmbase.scenarios.FailingSummaryLogger;
import com.ericsson.nms.security.taf.test.flow.ReconcileScriptTestFlow;

/**
 * Created by etomszu on 5/24/16.
 */
public class ReconcileScriptScenario extends TafTestBase {

    @Inject
    private ReconcileScriptTestFlow reconcileScriptTestFlow;

    @TestId(id = "TORF-113179", title = "Verify that reconcile.sh script is working properly")
    @Test
    public void reconcileTestRunner() {

        final TestScenarioRunner runner = runner().withListener(new FailingSummaryLogger()).withListener(new DebugLogger())
                .withDefaultExceptionHandler(ScenarioExceptionHandler.PROPAGATE).build();

        runner.start(scenario().addFlow(reconcileScriptTestFlow.reconcileScriptTestStepFlow()).build());
    }

}
