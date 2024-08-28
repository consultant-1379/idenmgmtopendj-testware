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

import static com.ericsson.cifwk.taf.datasource.TafDataSources.*;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.*;

import java.util.HashSet;

import javax.inject.Inject;

import com.ericsson.cifwk.taf.scenario.api.TestStepFlowBuilder;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.TestContext;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.data.pool.DataPoolStrategy;
import com.ericsson.cifwk.taf.scenario.TestScenarioRunner;
import com.ericsson.cifwk.taf.scenario.TestStepFlow;
import com.ericsson.cifwk.taf.scenario.api.ScenarioExceptionHandler;
import com.ericsson.oss.testware.enmbase.data.CommonDataSources;
import com.ericsson.oss.testware.enmbase.scenarios.DebugLogger;
import com.ericsson.oss.testware.enmbase.scenarios.FailingSummaryLogger;
import com.ericsson.nms.security.taf.test.data.M2mUser;
import com.ericsson.nms.security.taf.test.data.UserPasswordPolicyData;
import com.ericsson.nms.security.taf.test.flow.M2MUserTestFlow;
import com.ericsson.nms.security.taf.test.flow.OpenDJTestFlow;
import com.ericsson.nms.security.taf.test.flow.ProxyUserTestFlow;
import com.ericsson.nms.security.taf.test.teststep.ProxyUserTestSteps;
import com.ericsson.oss.testware.security.gim.flows.GimCleanupFlows;
import com.ericsson.oss.testware.security.gim.flows.GimCleanupFlows.EnmObjectType;
import com.ericsson.oss.testware.security.gim.flows.UserManagementTestFlows;

public class OpenDJScenario extends TafTestBase {

    private static final String USER_PASSWORD_POLICY_DATA = "data/userPasswordPolicyTestData.csv";
    private static final String USER_PASSWORD_EXPIRATION = "data/userPasswordExpirationTestData.csv";
    private static final String CREATE_USER_DATA = "data/userToCreate.csv";
   // private static final String M2M_USER_DATA = "data/m2mUsers.csv";

    @Inject
    private OpenDJTestFlow openDJTestFlow;

    //@Inject
    //private ProxyUserTestFlow proxyUserTestFlow;

    //@Inject
    //private M2MUserTestFlow m2MUserTestFlow;

    @Inject
    private GimCleanupFlows idmCleanupFlows;

    @Inject
    private UserManagementTestFlows userManagementFlows;

    @Inject
    private TestContext context;

    @BeforeSuite
    public void setUp() {

        //context.setAttribute(ProxyUserTestSteps.PROXY_USERS, new HashSet<String>());
        context.addDataSource(UserPasswordPolicyData.USER_PASSWORD_POLICY_DATA,
                shared(fromCsv(USER_PASSWORD_POLICY_DATA, DataPoolStrategy.STOP_ON_END)));
        context.addDataSource(UserPasswordPolicyData.USER_PASSWORD_EXPIRATION_DATA,
                shared(fromCsv(USER_PASSWORD_EXPIRATION, DataPoolStrategy.STOP_ON_END)));

        context.addDataSource(CommonDataSources.USER_TO_CLEAN_UP, shared(fromCsv(CREATE_USER_DATA, DataPoolStrategy.STOP_ON_END)));
        start(idmCleanupFlows.cleanUp(EnmObjectType.USER));
        //context.addDataSource(M2mUser.M2M_USER_DATA, shared(fromCsv(M2M_USER_DATA, DataPoolStrategy.STOP_ON_END)));
        //start(m2MUserTestFlow.cleanUpM2mUserTestFlow());

        context.addDataSource(CommonDataSources.USERS_TO_CREATE, shared(fromCsv(CREATE_USER_DATA, DataPoolStrategy.STOP_ON_END)));

        //start(proxyUserTestFlow.createProxyUserTestFlow());
        //start(m2MUserTestFlow.createM2mUserTestFlow());
        start_build(userManagementFlows.createUser());

    }

//     @TestId(id = "TORF-21858_Func_2", title = "Verify User's Password Policy")
//     @Test
//     public void verifyUsersHaveCorrectPasswordPolicies() {
// 
//         start(openDJTestFlow.verifyUsersPasswordExpirePolicies());
//         start(openDJTestFlow.verifyUsersPasswordExpiration());
//     }

    @AfterSuite(alwaysRun = true)
    public void tearDown() {
        context.addDataSource(CommonDataSources.USERS_TO_DELETE, shared(fromCsv(CREATE_USER_DATA)));

        //start(proxyUserTestFlow.removeProxyUserTestFlow());
        //(m2MUserTestFlow.removeM2mUserTestFlow());
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
