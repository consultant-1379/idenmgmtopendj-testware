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
package com.ericsson.nms.security.taf.test.operators;

import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.tools.cli.TafCliToolShell;
import com.ericsson.cifwk.taf.tools.cli.TafCliTools;
import com.ericsson.de.tools.cli.CliCommandResult;
import com.ericsson.oss.testware.hostconfigurator.HostConfigurator;

/**
 * Created by etomszu on 6/7/16.
 */
public class ReconcileScriptOperatorImpl implements ReconcileScriptOperator {

    public static String reconcileScriptAddress = "/opt/ericsson/opendj/reconcile.sh";

    @Override
    public int executeReconsileScript() {

        final Host host = HostConfigurator.getMS();
        final TafCliToolShell shell = TafCliTools.sshShell(host).build();
        CliCommandResult result = shell.execute(reconcileScriptAddress);
        final int commandExitValue = result.getExitCode();
        shell.close();
        return commandExitValue;
    }
}
