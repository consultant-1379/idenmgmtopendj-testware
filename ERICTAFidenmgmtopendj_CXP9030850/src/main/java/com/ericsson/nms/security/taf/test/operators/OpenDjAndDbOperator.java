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
package com.ericsson.nms.security.taf.test.operators;

import java.util.*;

import com.ericsson.cifwk.taf.tools.cli.TafCliToolShell;
import com.ericsson.cifwk.taf.tools.cli.TafCliTools;
import com.ericsson.de.tools.cli.CliCommandResult;
import com.ericsson.oss.testware.remoteexecution.operators.NewRemoteEnmBuilderOperator;
import com.ericsson.oss.testware.remoteexecution.operators.PibConnector;
import org.assertj.core.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.User;
import com.ericsson.cifwk.taf.datasource.DataRecord;
import com.ericsson.oss.testware.hostconfigurator.HostConfigurator;
import com.ericsson.oss.testware.remoteexecution.operators.RemoteEnmBuilderImplement;

import javax.inject.Inject;

@Operator
public class OpenDjAndDbOperator {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenDjAndDbOperator.class);
    private static final String DB = "db";
    private static final String OPENDJ = "opendj";
    private static final String PODNAME = "podname";
    private static final String SUDO_COMMAND = "sudo -s";
    private static final String SINGLEINSTANCE = "1";

    private static boolean isPhys = HostConfigurator.isPhysicalEnvironment();

    static Host host = null;
    private static RemoteEnmBuilderImplement remoteEnmBuilderImplement;
    private TafCliToolShell shellTargetConn;
    @Inject
    private static PibConnector pibConnector;
    static String dbName = "";
    static String opendjName = "";

    static String consulGetOpendjInstance = "sudo consul members |grep opendj |wc -l";


    private boolean initOpenDjAndDbOperator(final DataRecord dataSource) {
        setLineToRemove();
        final String dbHost = dataSource.getFieldValue(DB);
        final String opendjHost = dataSource.getFieldValue(OPENDJ);
        final String podName = dataSource.getFieldValue(PODNAME);
        if (!dbName.equals(dbHost) || !opendjName.equals(opendjHost)) {
            closeShell();
            dbName = dbHost;
            opendjName = opendjHost;
            host = isPhys ?
                    HostConfigurator.getHost(dbHost) :
                    HostConfigurator.getHost(opendjHost);
            if (host == null) {
                dbName = "";
                opendjName = "";
                return false;
            }
            Assertions.assertThat(host).as("hostPhisicalOrCloud is null").isNotNull();
            final User user = isPhys ? host.getDefaultUser() : null;
            this.remoteEnmBuilderImplement = new RemoteEnmBuilderImplement(host, podName, user);
            Assertions.assertThat(remoteEnmBuilderImplement).as("RemoteEnmBuilderImplement is null").isNotNull();
            LOGGER.debug("OpenDjCliOperatorImpl Constructor:\n\tIt physical Environment -> {}\n\tHost Name Selected -> {}",
                    this.remoteEnmBuilderImplement.isPhysicalEnvironment(), this.remoteEnmBuilderImplement.getTargetHost());
        }
        return true;
    }

    private void setLineToRemove() {
        DataHandler.getConfiguration().setProperty("kubectl.lines.to.remove",
                Arrays.asList("Defaulted container", "Use 'kubectl describe", "*** WARNING ", "Using ", "ssl-key-pair"));
        LOGGER.debug("LINES TO REMOVE {}",DataHandler.getConfiguration().getProperty("kubectl.lines.to.remove").toString());
    }

    public boolean setParameter(final DataRecord dataSource) {
        return initOpenDjAndDbOperator(dataSource);
    }

    public void closeShell() {
        if (this.remoteEnmBuilderImplement != null) {
            this.remoteEnmBuilderImplement.closeShell();
        }
        this.remoteEnmBuilderImplement = null;
        dbName = "";
        opendjName = "";
    }

    public void setDefaultParam() {
        if (this.remoteEnmBuilderImplement == null) {
            setLineToRemove();
            host = isPhys ?
                    HostConfigurator.getHost("db1") :
                    HostConfigurator.getHost("opendj_1");
            Assertions.assertThat(host).as("hostPhisicalOrCloud is null").isNotNull();
            final User user = isPhys ? host.getDefaultUser() : null;
            this.remoteEnmBuilderImplement = new RemoteEnmBuilderImplement(host, "opendj-0", user);
            Assertions.assertThat(remoteEnmBuilderImplement).as("RemoteEnmBuilderImplement is null").isNotNull();
            LOGGER.debug("OpenDjCliOperatorImpl Constructor:\n\tIt phisical Environment -> {}\n\tHost Name Selected -> {}",
                    this.remoteEnmBuilderImplement.isPhysicalEnvironment(), this.remoteEnmBuilderImplement.getTargetHost());
        }
    }

    public Host getHost() {
        return host;
    }

    public Host getPibHost() {
        return remoteEnmBuilderImplement.getPibHost();
    }

    public RemoteEnmBuilderImplement getRemoteEnmBuilderImplement() {
        return remoteEnmBuilderImplement;
    }

    public String getVerifyConnetion() {
        final String result = remoteEnmBuilderImplement.execCommand("whoami");
        return result;
    }

    //    retun Number of DB into physical environment
    public static Integer getdbNumber() {
        final TafCliToolShell shell = TafCliTools.sshShell(HostConfigurator.getMS()).build();
        final CliCommandResult dbnumberCommand = shell.execute("grep \"db-\" /etc/hosts | grep -v \"cloud\" | wc -l ", 25);
        final Integer dbTotalNumber = Integer.valueOf(dbnumberCommand.getOutput());
        LOGGER.debug("Number of DB on this Environment : {}", dbTotalNumber);
        return dbTotalNumber;
    }

    //    retun DB where running Opendj-0 into physical environment
    public static String getHostLdapOpendj1() {
        final TafCliToolShell shell = TafCliTools.sshShell(HostConfigurator.getMS()).build();
        final CliCommandResult resultOpendj1Command = shell.execute("grep $(grep \"$(/opt/ericsson/enminst/bin/vcs.bsh --groups | grep opendj | grep ONLINE |  tail -2 | awk -F' ' '{print $3}' | tail -1)\"  /etc/hosts |awk '{print $1}' ) /etc/hosts |grep db- | grep -v \"cloud-\" | awk -F' ' '{print $2}'|sed  s+-++g", 35);
        final String stdout1 = resultOpendj1Command.getOutput();
        LOGGER.info("DB where Opendj instance 1 running on: {}", stdout1);
        return stdout1;
    }
    //    retun DB where running Opendj-1 into physical environment
    public static String getHostLdapOpendj2() {
        final TafCliToolShell shell2 = TafCliTools.sshShell(HostConfigurator.getMS()).build();
        final CliCommandResult resultOpendj2Command =
                shell2.execute("grep $(grep \"$(/opt/ericsson/enminst/bin/vcs.bsh --groups | grep opendj | grep ONLINE |  tail -2 | awk -F' ' '{print $3}' | head -1)\"  /etc/hosts |awk '{print $1}' ) /etc/hosts |grep db- | grep -v \"cloud-\" | awk -F' ' '{print $2}'|sed  s+-++g", 35);
        final String stdout2 = resultOpendj2Command.getOutput();
        LOGGER.info("DB where Opendj instance 2 running on: {}", stdout2);
        return stdout2;
    }

    /**
     * This Method return true if you are using a vENM with opendj single Instance
     * for example FFE or extra Small vENM
     * Based on NewRemoteEnmBuilderOperator available from enm-taf-test-library version  1.2.36
     * @return
     */
    public static boolean checkOpendjSingleInstance() {
        LOGGER.info("VENM Environment");
        NewRemoteEnmBuilderOperator remoteEnmBuilder = new NewRemoteEnmBuilderOperator();
        String NewCliCommandResult = String.valueOf(remoteEnmBuilder.execPibCommandWithOutput(consulGetOpendjInstance));
        LOGGER.info("remoteEnmBuilder receive: {}", NewCliCommandResult);
        if ( NewCliCommandResult.equals(SINGLEINSTANCE)) {
            LOGGER.info("System with single instance opendj releved: {}", NewCliCommandResult);
            return true;
        }
        LOGGER.info("System with Double instances of opendj releved: {}", NewCliCommandResult);
        return false;
    }
}
