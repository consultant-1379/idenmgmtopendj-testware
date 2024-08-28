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

import static com.ericsson.cifwk.taf.datasource.TafDataSources.fromCsv;
import static com.ericsson.cifwk.taf.datasource.TafDataSources.shared;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.runner;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.scenario;

import javax.inject.Inject;

import com.ericsson.cifwk.taf.scenario.api.TestStepFlowBuilder;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.TestContext;
import com.ericsson.cifwk.taf.data.pool.DataPoolStrategy;
import com.ericsson.cifwk.taf.scenario.TestScenarioRunner;
import com.ericsson.cifwk.taf.scenario.TestStepFlow;
import com.ericsson.cifwk.taf.scenario.api.ScenarioExceptionHandler;
import com.ericsson.oss.testware.enmbase.data.CommonDataSources;
import com.ericsson.oss.testware.enmbase.scenarios.DebugLogger;
import com.ericsson.oss.testware.enmbase.scenarios.FailingSummaryLogger;
import com.ericsson.nms.security.taf.test.flow.TempAmosTestFlow;
import com.ericsson.oss.testware.security.gim.flows.GimCleanupFlows;
import com.ericsson.oss.testware.security.gim.flows.GimCleanupFlows.EnmObjectType;
import com.ericsson.oss.testware.security.gim.flows.UserManagementTestFlows;

public class TempAmosScenario extends TafTestBase {

    private static final String CREATE_USER_DATA = "data/amosUserToCreate.csv";

    @Inject
    private TempAmosTestFlow tempAmosTestFlow;

    @Inject
    private GimCleanupFlows idmCleanupFlows;

    @Inject
    private UserManagementTestFlows userManagementFlows;

    @Inject
    private TestContext context;

    @BeforeSuite
    public void setUp() {

        context.addDataSource(CommonDataSources.USER_TO_CLEAN_UP, shared(fromCsv(CREATE_USER_DATA, DataPoolStrategy.STOP_ON_END)));
        start(idmCleanupFlows.cleanUp(EnmObjectType.USER));

        context.addDataSource(CommonDataSources.USERS_TO_CREATE, shared(fromCsv(CREATE_USER_DATA, DataPoolStrategy.STOP_ON_END)));
        start_build(userManagementFlows.createUser());
    }

    @Test
    public void createTempAmosUser() {

        start(tempAmosTestFlow.createTempAmosUser());
    }

    @AfterSuite(alwaysRun = true)
    public void tearDown() {

        context.addDataSource(CommonDataSources.USERS_TO_DELETE, shared(fromCsv(CREATE_USER_DATA)));
        start_build(userManagementFlows.deleteUser());
    }

    private void start(final TestStepFlow flow) {
        final TestScenarioRunner runner = runner().withListener(new FailingSummaryLogger()).withListener(new DebugLogger())
                .withDefaultExceptionHandler(ScenarioExceptionHandler.PROPAGATE).build();

        runner.start(scenario().addFlow(flow).build());
    }

    private void start_build(final TestStepFlowBuilder flow) {
        final TestScenarioRunner runner = runner().withListener(new FailingSummaryLogger()).withListener(new DebugLogger())
                .withDefaultExceptionHandler(ScenarioExceptionHandler.PROPAGATE).build();

        runner.start(scenario().addFlow(flow).build());
    }
}
