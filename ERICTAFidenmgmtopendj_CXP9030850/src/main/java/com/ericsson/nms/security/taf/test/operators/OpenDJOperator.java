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

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.User;
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.cifwk.taf.tools.cli.TafCliToolShell;
import com.ericsson.cifwk.taf.tools.cli.TafCliTools;
import com.ericsson.cifwk.taf.tools.cli.handlers.impl.RemoteObjectHandler;
import com.ericsson.cifwk.taf.utils.FileFinder;
import com.ericsson.de.tools.cli.CliCommandResult;
import com.ericsson.de.tools.cli.CliIntermediateResult;
import com.ericsson.nms.security.taf.test.helpers.Db1NotFoundException;
import com.ericsson.nms.security.taf.test.helpers.HostNotFoundException;
import com.ericsson.oss.testware.hostconfigurator.HostConfigurator;
import com.google.inject.Singleton;

@Singleton
public class OpenDJOperator implements GenericOperator {

    @Inject
    private GlobalPropertiesOperatorImpl globalPropertiesOperatorImpl;

    private TafCliToolShell cmdHelper;
    private TafCliToolShell activeShell;
    private CliCommandResult activeResult;
    private static final Logger logger = LoggerFactory.getLogger(OpenDJOperator.class);

    private static final String PASSWORD = "12shroot";
    private static final User USER_LITP_ADMIN = new User("litp-admin", PASSWORD, UserType.ADMIN);
    private static final User USER_ROOT = new User("root", PASSWORD, UserType.ADMIN);
    //DB IP address is a key
    private static HashMap<String, Boolean> accessibleDbNodes = new HashMap<String, Boolean>();
    private static HashMap<String, TafCliToolShell> shellConnections = new HashMap<String, TafCliToolShell>();

    private String ldapAdminPassword;

    @Override
    public String getCommand(final String command) {
        return DataHandler.getAttribute(cliCommandPropertyPrefix + command).toString();
    }

    /**
     * Works properly ONLY for connections with db nodes!
     *
     * @throws HostNotFoundException
     */
    @Override
    public void initializeShell(final String hostname) throws HostNotFoundException {
        final Host host = getHostFromHostname(hostname);

        if (!isDbNodeAccessible(host)) {
            final String generalExceptionMessage = "The " + host.getHostname() + " node [ip=" + host.getIp() + "] is unavailable from the MS.";
            if (isDb1(hostname)) {
                throw new Db1NotFoundException(
                        generalExceptionMessage + " This could be caused by an invalid configuration or faulty data returned by HostConfigurator.");
            } else {
                throw new HostNotFoundException(generalExceptionMessage + " Is it Vapp? It contains only the db1 node.");
            }
        }

        if (shellConnections.get(host.getIp()) != null){
            try {
                CliCommandResult result = shellConnections.get(host.getIp()).execute("hostname",30);
                final int exitCode = result.getExitCode();
                logger.debug("The hostname command returns: {} with the exit code: {}", hostname, exitCode);

                if (exitCode == 0) {
                    activeShell = shellConnections.get(host.getIp());
                    return;
                }
            } catch (final RuntimeException e) {
                logger.warn("Unable to execute hostname on the {}. Recovering...", "[" + host.getHostname() + ", " + host.getIp() + "]");
                activeShell = initializeShell(host);
                shellConnections.put(host.getIp(), activeShell);
            }
        }
        activeShell = initializeShell(host);
        shellConnections.put(host.getIp(), activeShell);
    }


    private TafCliToolShell initializeShell(final Host host) {
    	cmdHelper = TafCliTools.sshShell(HostConfigurator.getMS()).build();
    	cmdHelper.hopper().hop(host, USER_LITP_ADMIN);
    	cmdHelper.switchUser(USER_ROOT.getUsername(), PASSWORD);
        return cmdHelper;
    }


    @Override
    public void writeln(final String command, final String args) {
        final String cmd = getCommand(command);
        //logger.trace("Writing " + cmd + " " + args + " to standard input");
        logger.debug("Executing commmand " + cmd + " with args " + args);
        activeResult = activeShell.execute(cmd + " " + args);//old shell.writeln
    }

    @Override
    public void writeln(String command) {
        try {
            command = getCommand(command);
        } catch (final NullPointerException e) { //FIXME
            logger.debug("The command is not mapped in the cliCommands.properties file.");
        }

        logger.info("Executing commmand " + command);
        CliIntermediateResult intResult = activeShell.writeLine(command);

        if (command.equals("exit")) {
            // we want to properly exit from all nested shells
            logger.debug("stdout: " + intResult.getOutput());
            intResult= activeShell.writeLine(command);
            logger.debug("stdout: " + intResult.getOutput());
            intResult = activeShell.writeLine(command);
            logger.debug("stdout: " + intResult.getOutput());
        }
    }

    @Override
    public int getExitValue() {
        final int exitValue = activeResult.getExitCode();
        logger.debug("Getting exit value from shell, exit value is :" + exitValue);
        return exitValue;
    }

   @Override
    public void close(){
        activeShell.close();
    }

    @Override
    public String checkForNullError(String error) {
        if (error == null) {
            error = "";
            return error;
        }
        return error;
    }

    @Override
    public String getStdOut() {
        final String result = activeResult.getOutput();
        logger.debug("Standard out: " + result);
        return result;
    }

    @Override
    public void disconnect() {
        logger.info("Disconnecting from shell");

        if (activeShell != null) {
        	activeShell.close();
        }
    }

    @Deprecated
    @Override
    public void sendFileRemotely(final String hostname, final String fileName, final String fileServerLocation) throws FileNotFoundException {
        final RemoteObjectHandler remote = new RemoteObjectHandler(getHostFromHostname(hostname));
        final List<String> fileLocation = FileFinder.findFile(fileName);
        final String remoteFileLocation = fileServerLocation; //unix address
        remote.copyLocalFileToRemote(fileLocation.get(0), remoteFileLocation);
        logger.debug("Copying " + fileName + " to " + remoteFileLocation + " on remote host");
    }

    @Deprecated
    @Override
    public void sendFilesRemotely(final String hostname, final String filesList, final String fileServerLocation) throws FileNotFoundException {

        final String[] fileNames = filesList.split(":");
        for (int i = 0; i < fileNames.length; i++) {
            sendFileRemotely(hostname, fileNames[i], fileServerLocation);
        }

    }

    @Deprecated
    @Override
    public void deleteRemoteFile(final String hostname, final String fileName, final String fileServerLocation) throws FileNotFoundException {
        final RemoteObjectHandler remoteFileHandler = new RemoteObjectHandler(getHostFromHostname(hostname));
        final String remoteFileLocation = fileServerLocation;
        remoteFileHandler.deleteRemoteFile(remoteFileLocation + fileName);
        logger.debug("deleting " + fileName + " at location " + remoteFileLocation + " on remote host");
    }

    @Deprecated
    @Override
    public void copyFileFromRemote(final String hostname, final String localFileLocation, final String remoteFileLocation)
            throws FileNotFoundException {
        final RemoteObjectHandler remote = new RemoteObjectHandler(getHostFromHostname(hostname));
        logger.debug("Copying " + remoteFileLocation + " to " + localFileLocation + " on the local host");
        remote.copyRemoteFileToLocal(remoteFileLocation, localFileLocation);
    }

    @Deprecated
    @Override
    public void scriptInput(final String message) {
        logger.debug("Writing " + message + " to standard in");
        activeShell.writeLine(message);
    }

    public static boolean isDb1(final String hostname) { return hostname.contains("sc1") || hostname.contains("db1"); }
    public static boolean isDb2(final String hostname) {
        return hostname.contains("sc2") || hostname.contains("db2");
    }

    private Host getHostFromHostname(final String hostname) throws IllegalArgumentException {
        Host host;
        if (isDb1(hostname)) {
            host = HostConfigurator.getDb1();
        } else if (isDb2(hostname)) {
            host = HostConfigurator.getDb2();
        } else {
            throw new IllegalArgumentException("Illegal value of the hostname: " + hostname + ".");
        }
        logger.debug("The hostname returned by getHostFromHostname(): hostname - " + host.getHostname() + " ip - " + host.getIp());
        return host;
    }

        public static Boolean isDbNodeAccessible(final Host host) {

        if (host == null) {
            return false;
        }

        //we don't want to check it multiple times
        if (accessibleDbNodes.containsKey(host.getIp())) {
            return accessibleDbNodes.get(host.getIp());
        }

        logger.info("Connect to the MS");

        final TafCliToolShell shell = TafCliTools.sshShell(HostConfigurator.getMS()).build();

        logger.info("Check if is possible to ping the DB node " + host.getIp());
        CliCommandResult result = shell.execute("ping -c 2 " + host.getIp(),30);

        final String cmdOutput = result.getOutput();
        logger.debug("cmdOutput: " + cmdOutput);
        final int cmdExitValue = result.getExitCode();
        shell.writeLine("exit");
        shell.close();

        // ping command returns:
        // code 0 = success, 1 = no reply and 2 = other error
        accessibleDbNodes.put(host.getIp(), cmdExitValue == 0);

        return (cmdExitValue == 0);
    }

    public String ldapsearch(final String filter, final String attributes) {
        final String stdout = (getHelper().execute(String.format(Commands.LDAPSEARCH, getDirectoryManagerPassword(), getBaseDn(), filter, attributes))).getOutput();
        return stdout;
    }

    private TafCliToolShell getHelper() {

        if (cmdHelper == null) {
            cmdHelper = TafCliTools.sshShell(HostConfigurator.getMS()).build();
            cmdHelper.hopper().hop(HostConfigurator.getDb1(), USER_LITP_ADMIN);
            cmdHelper.switchUser(USER_ROOT.getUsername(), PASSWORD);
        }
        return cmdHelper;
    }

    private String getBaseDn() {
        return globalPropertiesOperatorImpl.getProperty("COM_INF_LDAP_ROOT_SUFFIX");
    }

    public String getDirectoryManagerPassword() {
        if (ldapAdminPassword == null) {
            ldapAdminPassword = executeAndRetrieveOutput(getHelper(),
                    String.format(Commands.DECRYPT_LDAP_ADMIN_PASSWORD, globalPropertiesOperatorImpl.getProperty("LDAP_ADMIN_PASSWORD")));
        }
        return ldapAdminPassword;
    }

    public String getDirectoryManagerPasswordLDAP(final String GET_CRIP_LDAP_ADMIN_PASSWORD1) {
        if (ldapAdminPassword == null) {
            ldapAdminPassword = executeAndRetrieveOutput(getHelper(),
                    String.format(Commands.DECRYPT_LDAP_ADMIN_PASSWORD, GET_CRIP_LDAP_ADMIN_PASSWORD1));
        }
        return ldapAdminPassword;
    }

    private String executeAndRetrieveOutput(final TafCliToolShell cmdHelper, final String command) {
        final String commandOutput = (cmdHelper.execute(command)).getOutput();

        final String[] lines = commandOutput.split(System.getProperty("line.separator"));
        for (final String line : lines) {
            if (line.contains(Commands.GET_PREFIX.replaceAll("\\\\", ""))) {
                return line.split("\\s")[1];
            }
        }
        return commandOutput;
    }

    static class Commands {
        static final String GET_PREFIX = "some_random_prefix";
        static final String DECRYPT_LDAP_ADMIN_PASSWORD = "echo " + GET_PREFIX
                + " `echo %s | openssl enc -a -d -aes-128-cbc -salt -kfile '/ericsson/tor/data/idenmgmt/opendj_passkey'`";
        static final String LDAPSEARCH = "/opt/opendj/bin/ldapsearch -p 1636 --useSSL --trustAll " + "-D \"cn=directory manager\" -w %s -b %s %s %s";
    }


    public static boolean isClusterIdPresent() {
        if (System.getProperty("taf.clusterId") != null) {
            return true;
        }
        return false;
    }

    public static User getUserByName(final Host host, final String name) {
        for (final User user : host.getUsers()) {
            if (name.equals(user.getUsername())) {
                return user;
            }
        }
        return new User(name, DEFAULT_PASSWORD, UserType.ADMIN);
    }

}
