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

import static org.testng.Assert.assertEquals;

import javax.inject.Inject;
import javax.inject.Provider;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.nms.security.taf.test.operators.ReconcileScriptOperator;
import com.ericsson.nms.security.taf.test.operators.ReconcileScriptOperatorImpl;

/**
 * Created by etomszu on 5/25/16.
 */
public class ReconcileScriptTestSteps extends TafTestBase {

    public static final String RECONCILE_TEST_STEP = "ReconcileTestStep";

    @Inject
    private Provider<ReconcileScriptOperatorImpl> provider;

    @TestStep(id = RECONCILE_TEST_STEP)
    public void reconsileScript() {

        final ReconcileScriptOperator reconcileScriptOperator = provider.get();
        final int result = reconcileScriptOperator.executeReconsileScript();
        assertEquals(result, 0);
    }
}
