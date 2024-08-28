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
package com.ericsson.nms.security.taf.test.teststep;

import javax.inject.Inject;
import javax.inject.Provider;

import com.ericsson.cifwk.taf.TestContext;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.nms.security.taf.test.operators.OpenDjAndDbOperator;
import com.ericsson.oss.testware.hostconfigurator.HostConfigurator;

public class BaseTestSteps {

    public static final String CLOSE_CONNECTION = "closeConnection";

    protected static boolean isPhysdEnv = HostConfigurator.isPhysicalEnvironment();
    protected static boolean isVirtdEnv = HostConfigurator.isVirtualEnvironment();
    protected static boolean isCloudEnv = HostConfigurator.isCloudEnvironment();

    @Inject
    protected Provider<OpenDjAndDbOperator> provider;

    @Inject
    protected TestContext context;

    @TestStep(id = CLOSE_CONNECTION)
    public void closeConnection() {
        final OpenDjAndDbOperator openDJOperator = provider.get();
        openDJOperator.closeShell();
    }
}
