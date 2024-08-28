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

import static com.ericsson.nms.security.taf.test.constant.Constants.OPENDJCONFIGURATION;
import static com.ericsson.nms.security.taf.test.constant.Constants.OPENDJCONNECTION;
import static com.ericsson.nms.security.taf.test.constant.Constants.OPENDJREPLICATIONSTATUS;
import static com.ericsson.nms.security.taf.test.constant.Constants.OPENDJSSLCONFIGURATION;
import static com.ericsson.nms.security.taf.test.constant.Constants.PASSWDPOLICYPROPERTIES;

import org.assertj.core.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.User;
import com.ericsson.cifwk.taf.datasource.DataRecord;
import com.ericsson.cifwk.taf.tools.cli.TafCliToolShell;
import com.ericsson.de.tools.cli.CliIntermediateResult;
import com.ericsson.de.tools.cli.WaitConditions;
import com.ericsson.nms.security.taf.test.helpers.TestwareSettings;
import com.ericsson.nms.security.taf.test.operators.OpenDJOperator;
import com.ericsson.nms.security.taf.test.operators.OpenDjAndDbOperator;
import com.ericsson.oss.testware.remoteexecution.operators.RemoteEnmBuilderImplement;

public class FunctionalTestSteps extends BaseTestSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(FunctionalTestSteps.class);

    public static final String VERIFY_CONNECTION = "verifyConnetion";
    public static final String VERIFY_CHANGE_PASSWD = "verifyChangePasswd";
    public static final String VERIFY_OPENDJSSLCONFIG = "verifyOpendjSSLConfiguration";
    public static final String VERIFY_PASSWORDPOLICYPROPERTIES = "verifyPasswordPolicyProperties";
    public static final String VERIFY_OPENDJCONFIGURATION = "verifyOpenDJConfiguration";
    public static final String VERIFY_REPLICATIONSTATUS = "verifyOpenDJReplicationStatus";
    private static final String OPENDJ_PATH = "OPENDJ_PATH";
    private static final String OPENDJ_BIN_PATH = "OPENDJ_BIN_PATH";
    private static final String SUDO_FORCED = "SUDO_FORCED";
    private static final String PENM_OPENDJ = "/opt/opendj/config";
    private static final String VENM_CENM_OPENDJ = "/ericsson/opendj/opendj/config";
    private static final String PENM_OPENDJ_BIN = "/opt/opendj/bin";
    private static final String VENM_CENM_OPENDJ_BIN = "/ericsson/opendj/opendj/bin";
    private static final String HOST_NAME = "HOSTNAME";

    @TestStep(id = VERIFY_CONNECTION)
    public void verifyConnetion(@Input(OPENDJCONNECTION) final DataRecord dataSource) {
        final OpenDjAndDbOperator openDJOperator = provider.get();
        RemoteEnmBuilderImplement remoteEnmBuilderImplement = null;
        final boolean connection = openDJOperator.setParameter(dataSource);
        if (connection) {
            remoteEnmBuilderImplement = openDJOperator.getRemoteEnmBuilderImplement();
            final String hostUser = !remoteEnmBuilderImplement.isCloudEnvironment() ? remoteEnmBuilderImplement.getUser().getUsername() : "root";
            final Host host = openDJOperator.getHost();
            Assertions.assertThat(openDJOperator.getVerifyConnetion().equals(hostUser))
                    .as(String.format("Failed Login with host %s and user %s", host.getHostname(), hostUser)).isTrue();
        }
        else {
            Assertions.assertThat(false)
                    .as(String.format("Connection fail on host %s or %s",dataSource.getFieldValue("db"), dataSource.getFieldValue("opendj"))).isTrue();
        }
    }

    @TestStep(id = VERIFY_CHANGE_PASSWD)
    public void verifyChangePasswd(@Input(OPENDJCONNECTION) final DataRecord dataSource) {
        final OpenDjAndDbOperator openDJOperator = provider.get();
        final boolean connection = openDJOperator.setParameter(dataSource);
        if(openDJOperator.setParameter(dataSource)) {
        /*
        assertThat(pwdPolicySubentry.contains("ds-pwp-password-expiration-time"))
                .as(userString + " has ds-pwp-password-expiration-time attribute, search result: " + pwdPolicySubentry)
                .isEqualTo(userPasswordPolicyData.getShouldContainPasswordExpirationTimeAttribute());
         */
        } else {
            Assertions.assertThat(false)
                    .as(String.format("Connection fail on host %s or %s",dataSource.getFieldValue("db"), dataSource.getFieldValue("opendj"))).isTrue();
        }
    }

    @TestStep(id = VERIFY_OPENDJSSLCONFIG)
    public void verifyOpendjSSLConfiguration(@Input(OPENDJSSLCONFIGURATION) final DataRecord dataSource) {
        final OpenDjAndDbOperator openDJOperator = provider.get();
        RemoteEnmBuilderImplement remoteEnmBuilderImplement = null;
        final boolean connection = openDJOperator.setParameter(dataSource);
        if (connection) {
            remoteEnmBuilderImplement = openDJOperator.getRemoteEnmBuilderImplement();
            String args = dataSource.getFieldValue("args");
            String expectedOut = dataSource.getFieldValue("expectedOut");
            final String connectToLdap = dataSource.getFieldValue("connectToLdap");
            String command = dataSource.getFieldValue("command");
            final boolean suCommand = Boolean.parseBoolean((String) dataSource.getFieldValue("suCommand"));
            args = updateTestVariables(args);
            expectedOut = updateTestVariables(expectedOut);
            command = updateTestVariables(command);
            if (connectToLdap.equalsIgnoreCase("yes")) {
                args = getLdapsAuthProperty() + args;
            }
            LOGGER.debug("Executing commmand " + command + " with args " + args);
            String cmd = String.format("%s %s", command, args);
            if (isCloudEnv) {
                LOGGER.debug("CMD BEFORE {}", cmd);
                cmd = cmd.replaceAll("`", "\\\\`").replaceAll("\"", "\\\\\"");
                LOGGER.debug("CMD AFTER {}", cmd);
            }
            final TafCliToolShell shell = remoteEnmBuilderImplement.getShellTargetConn();
            final User suUser = remoteEnmBuilderImplement.getUser();
            final String suUserName = suUser.getUsername();
            final String suUserPasswd = suUser.getPassword();
            if (suCommand && isPhysdEnv) {
                CliIntermediateResult resultWl = shell.writeLine("su", WaitConditions.substring("Password"));
                LOGGER.debug("Executed commmand su " + resultWl.getOutput());
                resultWl = shell.writeLine(suUserPasswd, WaitConditions.substring(suUserName));
                LOGGER.debug("Sent password " + resultWl.getOutput());
            }
            String result;
            if (suCommand && isPhysdEnv) {
                CliIntermediateResult resultWl = shell.writeLine(cmd, WaitConditions.substring(expectedOut));
                result = resultWl.getOutput();
            } else {
                result = remoteEnmBuilderImplement.execCommand(cmd);
            }
            LOGGER.debug("Executed commmand " + cmd + "with result " + result);
            if (suCommand && isPhysdEnv) {
                final CliIntermediateResult resultW2 = shell.writeLine("exit");
                LOGGER.debug("Exit " + resultW2.getOutput());
            }
            final boolean find = result.contains(expectedOut);
            Assertions.assertThat(find).as(String.format("Received %s expected %s", result, expectedOut)).isTrue();
        } else {
            Assertions.assertThat(false)
                    .as(String.format("Connection fail on host %s or %s",dataSource.getFieldValue("db"), dataSource.getFieldValue("opendj"))).isTrue();
        }
    }

    @TestStep(id = VERIFY_PASSWORDPOLICYPROPERTIES)
    public void verifyPasswordPolicyProperties(@Input(PASSWDPOLICYPROPERTIES) final DataRecord dataSource) {
        final OpenDjAndDbOperator openDJOperator = provider.get();
        RemoteEnmBuilderImplement remoteEnmBuilderImplement = null;
        final boolean connection = openDJOperator.setParameter(dataSource);
        if (connection) {
            remoteEnmBuilderImplement = openDJOperator.getRemoteEnmBuilderImplement();
            String args = dataSource.getFieldValue("args");
            String command = dataSource.getFieldValue("command");
            String expectedOut = dataSource.getFieldValue("expectedOut");
            String subcommand = dataSource.getFieldValue("subcommand");
            command = updateTestVariables(command);
            args = updateTestVariables(args);
            if ((args.contains("-D")) && (args.contains("-w"))) {
                args = subcommand + " -p " + DataHandler.getAttribute("ldap.adminConnectorPort").toString() + " " + args;
            } else {
                args = subcommand + " " + getLdapAdminProperty() + " " + args;
            }
            LOGGER.debug("Executing commmand " + command + " with args " + args);
            String cmd = String.format("%s %s", command, args);
            if (isCloudEnv) {
                cmd = cmd.replaceAll("`", "\\\\`").replaceAll("\"", "\\\\\"");
            }
            String result = remoteEnmBuilderImplement.execCommand(cmd);
            result = result.replaceAll("\\s+", " ");
            final boolean find = result.contains(expectedOut);
            Assertions.assertThat(find).as(String.format("Received %s expected %s", result, expectedOut)).isTrue();
        } else {
            Assertions.assertThat(false)
                    .as(String.format("Connection fail on host %s or %s",dataSource.getFieldValue("db"), dataSource.getFieldValue("opendj"))).isTrue();
        }
    }

    @TestStep(id = VERIFY_OPENDJCONFIGURATION)
    public void verifyOpenDJConfiguration(@Input(OPENDJCONFIGURATION) final DataRecord dataSource) {
        final OpenDjAndDbOperator openDJOperator = provider.get();
        RemoteEnmBuilderImplement remoteEnmBuilderImplement = null;
        final boolean connection = openDJOperator.setParameter(dataSource);
        if (connection) {
            remoteEnmBuilderImplement = openDJOperator.getRemoteEnmBuilderImplement();

            String args = dataSource.getFieldValue("args");
            String command = dataSource.getFieldValue("command");
            String expectedOut = dataSource.getFieldValue("expectedOut");
            command = updateTestVariables(command);
            args = updateTestVariables(args);
            args = getLdapsAuthProperty() + args;
            LOGGER.debug("Executing commmand " + command + " with args " + args);
            String cmd = String.format("%s %s", command, args);
            if (isCloudEnv) {
                cmd = cmd.replaceAll("`", "\\\\`").replaceAll("\"", "\\\\\"");
            }
            String result = remoteEnmBuilderImplement.execCommand(cmd);
            result = result.replaceAll("\\s+", " ");
            final boolean find = result.contains(expectedOut);
            Assertions.assertThat(find).as(String.format("Received %s expected %s", result, expectedOut)).isTrue();
        } else {
            Assertions.assertThat(false)
                    .as(String.format("Connection fail on host %s or %s",dataSource.getFieldValue("db"), dataSource.getFieldValue("opendj"))).isTrue();
        }
    }

    @TestStep(id = VERIFY_REPLICATIONSTATUS)
    public void verifyOpenDJReplicationStatus(@Input(OPENDJREPLICATIONSTATUS) final DataRecord dataSource) {
        final OpenDjAndDbOperator openDJOperator = provider.get();
        RemoteEnmBuilderImplement remoteEnmBuilderImplement = null;
        final boolean connection = openDJOperator.setParameter(dataSource);
        if (connection) {
            remoteEnmBuilderImplement = openDJOperator.getRemoteEnmBuilderImplement();

            String args = dataSource.getFieldValue("args");
            String command = dataSource.getFieldValue("command");
            String expectedOut = dataSource.getFieldValue("expectedOut");
            String subcommand = dataSource.getFieldValue("subcommand");
            final String opendj = dataSource.getFieldValue("opendj");
            command = updateTestVariables(command);
            args = updateTestVariables(args, opendj, remoteEnmBuilderImplement);
            String authString = getReplicationAuth(opendj, remoteEnmBuilderImplement);
            args = authString + " " + args;
            args = subcommand + " " + args;
            LOGGER.debug("Executing commmand " + command + " with args " + args);
            String cmd = String.format("%s %s", command, args);
            if (isCloudEnv) {
                cmd = cmd.replaceAll("`", "\\\\`").replaceAll("\"", "\\\\\"").replaceAll("\\$", "\\\\\\$");
            }
            String result = remoteEnmBuilderImplement.execCommand(cmd);
            result = result.replaceAll("\\s+", " ");
            final boolean find = result.contains(expectedOut);
            Assertions.assertThat(find).as(String.format("Received %s expected %s", result, expectedOut)).isTrue();
        } else {
            Assertions.assertThat(false)
                    .as(String.format("Connection fail on host %s or %s",dataSource.getFieldValue("db"), dataSource.getFieldValue("opendj"))).isTrue();
        }
    }

    private String updateTestVariables(final String data) {
        if (data == null) {
            return null;
        }
        String result = "";
        if (data.contains(TestwareSettings.ROOT_SUFFIX_VARIABLE)) {
            String rootfixvariable = (String) DataHandler.getAttribute("ldap.rootSuffixVariable").toString();
            rootfixvariable = rootfixvariable.replaceAll("[\\[\\]\" \"]", "");
            result = data.replace(TestwareSettings.ROOT_SUFFIX_VARIABLE, rootfixvariable);
        }
        if (data.contains(SUDO_FORCED)) {
            if (isVirtdEnv) {
                result = result.isEmpty() ? data.replaceAll(SUDO_FORCED, "sudo ") : result.replaceAll(SUDO_FORCED, "sudo ");
            } else {
                result = result.isEmpty() ? data.replaceAll(SUDO_FORCED, "") : result.replaceAll(SUDO_FORCED, "");
            }
        }
        if (data.contains(OPENDJ_PATH)) {
            if (isPhysdEnv) {
                result = result.isEmpty() ? data.replaceAll(OPENDJ_PATH, PENM_OPENDJ) : result.replaceAll(OPENDJ_PATH, PENM_OPENDJ);
            } else {
                result = result.isEmpty() ? data.replaceAll(OPENDJ_PATH, VENM_CENM_OPENDJ) : result.replaceAll(OPENDJ_PATH, VENM_CENM_OPENDJ);
            }
        }
        if (data.contains(OPENDJ_BIN_PATH)) {
            if (isPhysdEnv) {
                result = result.isEmpty() ? data.replaceAll(OPENDJ_BIN_PATH, PENM_OPENDJ_BIN) : result.replaceAll(OPENDJ_BIN_PATH, PENM_OPENDJ_BIN);
            } else {
                result = result.isEmpty() ?
                        data.replaceAll(OPENDJ_BIN_PATH, VENM_CENM_OPENDJ_BIN) :
                        result.replaceAll(OPENDJ_BIN_PATH, VENM_CENM_OPENDJ_BIN);
            }
        }
        return result.isEmpty() ? data : result;
    }

    private String updateTestVariables(final String data, final String host, final RemoteEnmBuilderImplement remoteEnmBuilderImplement) {
        if (data == null) {
            return null;
        }
        String result = data.replaceAll(HOST_NAME, getHostName(host, remoteEnmBuilderImplement));
        return result;
    }

    /**
     * Internal interface to get the authentication information for Opendj LDAPS connector
     */
    private String getLdapsAuthProperty() {
        final String userDN = DataHandler.getAttribute("ldapAuth.userDN").toString();
        final String port = DataHandler.getAttribute("ldapsAuth.port").toString();
        final String pwdValue = DataHandler.getAttribute("ldapAuth.userPassword").toString();
        //final String authProp = "-p " + port + " --useSSL --trustAll " + " -D \"" + userDN + "\" -w " + pwdValue + " ";
        final String authProp = String.format(" -p %s --useSSL --trustAll -D \"%s\" -w %s ", port, userDN, pwdValue);
        return authProp;
    }

    /**
     * Function getLdapAuthProperty Description: API to get LDAP Credentials for dsconfig to test and host properties Input: Host name Return: The
     * options for using cammand "dsconfig"
     */
    private String getLdapsAuthProperty(final String host) {
        String hostName = "opendjhost0";
        if (OpenDJOperator.isDb2(host)) {
            hostName = "opendjhost1";
        }
        final String userDN = DataHandler.getAttribute("ldapAuth.userDN").toString();
        final String userPassword = DataHandler.getAttribute("ldapAuth.userPassword").toString();
        final String authProp = " -h " + hostName + " -p 4444 " + " -D \"" + userDN + "\" -w " + userPassword + " -X -n ";
        return authProp;
    }

    /**
     * Internal interface to get the authentication information for Opendj Admin connector
     */
    private String getLdapAdminProperty() {
        final String userDN = DataHandler.getAttribute("ldapAuth.userDN").toString();
        final String port = DataHandler.getAttribute("ldap.adminConnectorPort").toString();
        final String pwdValue = DataHandler.getAttribute("ldapAuth.userPassword").toString();
        final String authProp = "-p " + port + " -D \"" + userDN + "\" -w " + pwdValue + " ";

        return authProp;
    }

    private String getHostName(final String host, final RemoteEnmBuilderImplement remoteEnmBuilderImplement) {
        String hostName = "";
        if(remoteEnmBuilderImplement.isPhysicalEnvironment()) {
            hostName = "opendjhost0";
            if ("opendj_2".equals(host)) {
                hostName = "opendjhost1";
            }
            return hostName;
        } else if(remoteEnmBuilderImplement.isVirtualEnvironment()) {
            return host.replaceAll("_","-");
        } else if(remoteEnmBuilderImplement.isCloudEnvironment()) {
            hostName = "opendj-0";
            if ("opendj_2".equals(host)) {
                hostName = "opendj-1";
            }
            return hostName;
        }
        return null;
    }


    /**
     * Function: getReplicationAuth Description: API to get the credentials for replication and map the TAF hosts to opendj hosts Input: TAF host name
     * Return: the assembled replication auth
     */
    private String getReplicationAuth(final String host, final RemoteEnmBuilderImplement remoteEnmBuilderImplement) {
        final String hostName = getHostName(host, remoteEnmBuilderImplement);
        final String replicationUid = DataHandler.getAttribute("replication.adminUID").toString();
        final String replicationPasswd = DataHandler.getAttribute("replication.password").toString();
        return " -h " + hostName + " -p 4444 " + "-D \"" + replicationUid + "\" -w " + replicationPasswd + " -X --showReplicas --showChangelogs --showGroups ";
    }

    private String getCommand(final String command) {
        return DataHandler.getAttribute("clicommand." + command).toString();
    }

}
