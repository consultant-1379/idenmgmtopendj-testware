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
import static com.ericsson.nms.security.taf.test.teststep.BaseTestSteps.CLOSE_CONNECTION;
import static com.ericsson.nms.security.taf.test.teststep.FunctionalTestSteps.VERIFY_CHANGE_PASSWD;
import static com.ericsson.nms.security.taf.test.teststep.FunctionalTestSteps.VERIFY_CONNECTION;
import static com.ericsson.nms.security.taf.test.teststep.FunctionalTestSteps.VERIFY_OPENDJCONFIGURATION;
import static com.ericsson.nms.security.taf.test.teststep.FunctionalTestSteps.VERIFY_OPENDJSSLCONFIG;
import static com.ericsson.nms.security.taf.test.teststep.FunctionalTestSteps.VERIFY_PASSWORDPOLICYPROPERTIES;
import static com.ericsson.nms.security.taf.test.teststep.FunctionalTestSteps.VERIFY_REPLICATIONSTATUS;

import javax.inject.Inject;

import com.ericsson.cifwk.taf.scenario.api.TestStepFlowBuilder;
import com.ericsson.nms.security.taf.test.teststep.FunctionalTestSteps;

public class FunctionalTestFlow {

    @Inject
    private FunctionalTestSteps functionalTestSteps;

    public TestStepFlowBuilder verifyConnection() {
        return flow("Verify Connection")
                .addTestStep(annotatedMethod(functionalTestSteps, VERIFY_CONNECTION));
    }

    public TestStepFlowBuilder changePassword() {
        return flow("Change Password")
                .addTestStep(annotatedMethod(functionalTestSteps, VERIFY_CHANGE_PASSWD));
    }

    public TestStepFlowBuilder verifyOpendjSSLConfiguration() {
        return flow("Verify Opendj SSL Configuration")
                .addTestStep(annotatedMethod(functionalTestSteps, VERIFY_OPENDJSSLCONFIG));
    }

    public TestStepFlowBuilder verifyPasswordPolicyProperties() {
        return flow("Verify passwd Policy properties")
                .addTestStep(annotatedMethod(functionalTestSteps, VERIFY_PASSWORDPOLICYPROPERTIES));
    }

    public TestStepFlowBuilder verifyOpenDJConfiguration() {
        return flow("Verify OpenDJ Basic Configuration")
                .addTestStep(annotatedMethod(functionalTestSteps, VERIFY_OPENDJCONFIGURATION));
    }

    public TestStepFlowBuilder verifyOpenDJReplicationStatus() {
        return flow("Verify Replication Status")
                .addTestStep(annotatedMethod(functionalTestSteps, VERIFY_REPLICATIONSTATUS));
    }

    public TestStepFlowBuilder closeConnection() {
        return flow("Close Connection")
                .addTestStep(annotatedMethod(functionalTestSteps, CLOSE_CONNECTION));
    }


}
