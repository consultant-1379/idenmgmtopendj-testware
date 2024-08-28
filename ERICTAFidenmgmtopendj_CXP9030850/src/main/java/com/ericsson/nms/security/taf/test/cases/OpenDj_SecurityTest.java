/*  ------------------------------------------------------------------------------
 *  ******************************************************************************
 *  * COPYRIGHT Ericsson 2016
 *  *
 *  * The copyright to the computer program(s) herein is the property of
 *  * Ericsson Inc. The programs may be used and/or copied only with written
 *  * permission from Ericsson Inc. or in accordance with the terms and
 *  * conditions stipulated in the agreement/contract under which the
 *  * program(s) have been supplied.
 *  ******************************************************************************
 *  ------------------------------------------------------------------------------
 */
package com.ericsson.nms.security.taf.test.cases;

import static org.testng.Assert.*;

import javax.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.annotations.DataDriven;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.Output;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.tools.cli.TimeoutException;
import com.ericsson.nms.security.taf.test.helpers.HostNotFoundException;
import com.ericsson.nms.security.taf.test.operators.GenericOperator;
import com.ericsson.nms.security.taf.test.operators.OpenDJOperator;
import com.ericsson.oss.testware.hostconfigurator.HostConfigurator;
import com.google.inject.Inject;

public class OpenDj_SecurityTest extends TafTestBase {

    Logger logger = LoggerFactory.getLogger(OpenDj_FunctionalTest.class);

    @Inject
    private Provider<GenericOperator> operatorRegistry;

    @BeforeMethod
    public void lele(final Object[] data) {

        if (!OpenDJOperator.isDbNodeAccessible(HostConfigurator.getDb1()) && !OpenDJOperator.isDbNodeAccessible(HostConfigurator.getDb2())) {
            logger.debug("DB nodes not active");
            throw new SkipException("DB nodes not active");
        }
    }

    /**
     * @throws TimeoutException
     * @throws HostNotFoundException
     * @DESCRIPTION Verify openDJ is running by user opendj
     * @PRE Connection to SUT
     * @PRIORITY HIGH
     * @VUsers 1
     */

    @TestId(id = "TORF-9156_Security_1", title = "Verify openDJ is running by user opendj")
    @Test(groups = { "Acceptance" })
    @DataDriven(name = "OpenDJ_User_Test_Data")
    public void verifyRunningUserForOpenDJService(@Input("tests") final String test, @Input("host") final String hostname,
                                                  @Input("command") final String command, @Input("args") final String args,
                                                  @Input("timeout") final int timeout, @Input("exitShell") final String exit,
                                                  @Output("expectedOut") final String output, @Output("expectedExit") final int exitCode)
                                                          throws TimeoutException, HostNotFoundException {
        System.out.println("Test: \"" + test + "\"");
        final GenericOperator cliOperator = operatorRegistry.get();
        cliOperator.initializeShell(hostname);

        cliOperator.writeln(command, args);
        final String stdOut = cliOperator.getStdOut();
        logger.info(stdOut);
        assertTrue(stdOut.contains(output));
        cliOperator.writeln(exit);
        cliOperator.close();
        //cliOperator.expectClose(timeout);
        //assertTrue(cliOperator.isClosed());
        assertEquals(cliOperator.getExitValue(), exitCode);

        cliOperator.disconnect();
        
    }
}
