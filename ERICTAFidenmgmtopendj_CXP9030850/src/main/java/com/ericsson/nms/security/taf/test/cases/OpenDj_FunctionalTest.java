/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.taf.test.cases;

import static com.ericsson.nms.security.taf.test.operators.OpenDjAndDbOperator.*;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import static com.ericsson.cifwk.taf.datasource.TafDataSources.fromTafDataProvider;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.dataDrivenScenario;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.dataSource;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.runner;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.scenario;
import static com.ericsson.nms.security.taf.test.constant.Constants.DB1;
import static com.ericsson.nms.security.taf.test.constant.Constants.DB2;
import static com.ericsson.nms.security.taf.test.constant.Constants.DB3;
import static com.ericsson.nms.security.taf.test.constant.Constants.DB4;
import static com.ericsson.nms.security.taf.test.constant.Constants.OPENDJCONFIGURATION;
import static com.ericsson.nms.security.taf.test.constant.Constants.OPENDJSSLCONFIGURATION;
import static com.ericsson.nms.security.taf.test.constant.Constants.PASSWDPOLICYPROPERTIES;

import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.nms.security.taf.test.operators.OpenDjAndDbOperator;
import com.ericsson.oss.testware.hostconfigurator.HostConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TestContext;
import com.ericsson.cifwk.taf.annotations.TestSuite;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.datasource.DataRecord;
import com.ericsson.cifwk.taf.datasource.TestDataSource;
import com.ericsson.cifwk.taf.scenario.TestScenario;
import com.ericsson.cifwk.taf.scenario.TestScenarioRunner;
import com.ericsson.nms.security.taf.test.flow.FunctionalTestFlow;
import com.ericsson.nms.security.taf.test.flow.UtilityTestFlow;
import com.ericsson.nms.security.taf.test.helpers.Db1NotFoundException;
import com.ericsson.nms.security.taf.test.helpers.HostNotFoundException;
import com.ericsson.nms.security.taf.test.helpers.TestwareSettings;
import com.ericsson.nms.security.taf.test.operators.GlobalPropertiesOperatorImpl;
import com.ericsson.nms.security.taf.test.operators.OpenDJOperator;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

public class OpenDj_FunctionalTest extends ScenarioBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenDj_FunctionalTest.class);
    private String rootSuffixVariable;
    static Host host = null;
    private String FILTER_BY_PARAMETER_DBx = "";
    private String vENM_Opendj2 = "opendj_2";
    private String vENM_Opendj_gethost = "opendj_1";


    private Boolean db2Active = null;

    @javax.inject.Inject
    private TestContext context;

    @Inject
    private OpenDJOperator cliOperator;

    @Inject
    private GlobalPropertiesOperatorImpl globalPropertiesOperator;

    @javax.inject.Inject
    private UtilityTestFlow utilityTestFlow;

    @javax.inject.Inject
    private FunctionalTestFlow functionalTestFlow;

    @BeforeClass
    public void beforeClass() {
        TestDataSource<DataRecord> dataSource = fromTafDataProvider(OPENDJSSLCONFIGURATION);
        context.addDataSource(OPENDJSSLCONFIGURATION, dataSource);
        printDataSource(OPENDJSSLCONFIGURATION, LOGGER);

        dataSource = fromTafDataProvider(PASSWDPOLICYPROPERTIES);
        context.addDataSource(PASSWDPOLICYPROPERTIES, dataSource);
        printDataSource(PASSWDPOLICYPROPERTIES, LOGGER);

        dataSource = fromTafDataProvider(OPENDJCONFIGURATION);
        context.addDataSource(OPENDJCONFIGURATION, dataSource);
        printDataSource(OPENDJCONFIGURATION, LOGGER);

        if ( HostConfigurator.isPhysicalEnvironment() ) {
            //Find DB where opendj running
            final String LdapDatabase1 = getHostLdapOpendj1();
            LOGGER.debug("First Instance for opendj : {}", LdapDatabase1);
            final String LdapDatabase2 = getHostLdapOpendj2();
            LOGGER.debug("Second Instance for opendj : {}", LdapDatabase2);

            //Apply filters according to the DB where opendj running
            if (LdapDatabase1.equals(DB1) && LdapDatabase2.equals(DB1)) {
                FILTER_BY_PARAMETER_DBx = FILTER_BY_PARAMETER_DB234;
                LOGGER.debug("Filter Applied : {} ", FILTER_BY_PARAMETER_DBx);
            }
            if ((LdapDatabase1.equals(DB1) && LdapDatabase2.equals(DB2))||(LdapDatabase1.equals(DB2) && LdapDatabase2.equals(DB1))) {
                FILTER_BY_PARAMETER_DBx = FILTER_BY_PARAMETER_DB34;
                LOGGER.debug("Filter Applied : {} ", FILTER_BY_PARAMETER_DBx);
            }
            if ((LdapDatabase1.equals(DB2) && LdapDatabase2.equals(DB3))||(LdapDatabase1.equals(DB3) && LdapDatabase2.equals(DB2))) {
                FILTER_BY_PARAMETER_DBx = FILTER_BY_PARAMETER_DB14;
                LOGGER.debug("Filter Applied : {} ", FILTER_BY_PARAMETER_DBx);
            }
            if ((LdapDatabase1.equals(DB3) && LdapDatabase2.equals(DB4))||(LdapDatabase1.equals(DB4) && LdapDatabase2.equals(DB3))) {
                FILTER_BY_PARAMETER_DBx = FILTER_BY_PARAMETER_DB12;
                LOGGER.debug("Filter Applied : {} ", FILTER_BY_PARAMETER_DBx);
            }
        }else {
            if ( HostConfigurator.isVirtualEnvironment() ) {
                if ( OpenDjAndDbOperator.checkOpendjSingleInstance()) {
                    LOGGER.debug("Siamo entrati nel primo if vENM");
                    FILTER_BY_PARAMETER_DBx = FILTER_BY_PARAMETER_DB234;
                    LOGGER.debug("Filter Applied : {} ", FILTER_BY_PARAMETER_DBx);
                } else {
                    FILTER_BY_PARAMETER_DBx = FILTER_BY_PARAMETER_DB34;
                    LOGGER.debug("Filter Applied : {} ", FILTER_BY_PARAMETER_DBx);
                }
            } else {
                FILTER_BY_PARAMETER_DBx = FILTER_BY_PARAMETER_DB34;
                LOGGER.debug("Filter Applied : {} ", FILTER_BY_PARAMETER_DBx);
            }
        }

        final TestScenario setUpScenario = scenario("Before Class OpenDj_FunctionalTest")
                .addFlow(utilityTestFlow.getLdapAdminPasswordAndComInfLdapSuffix())
                .build();
        final TestScenarioRunner runner = runner().build();
        runner.start(setUpScenario);

        final String adminConnectorPort = "4444";
        LOGGER.debug("adminConnectorPort: " + adminConnectorPort);
        assertThat("The adminConnectorPort is null.", adminConnectorPort, not(isEmptyOrNullString()));
        DataHandler.setAttribute("ldap.adminConnectorPort", adminConnectorPort);

    }

    @AfterClass
    public void closeShellConnections() {
        final TestScenario setUpScenario = scenario("After Class OpenDj_FunctionalTest")
                .addFlow(functionalTestFlow.closeConnection())
                .build();
        final TestScenarioRunner runner = runner().build();
        runner.start(setUpScenario);
    }

    /**
     * @DESCRIPTION Verify Opendj's SSL configuration
     * @PRE TOR has been installed successfully and Opendj is running
     * @PRIORITY HIGH
     * @VUsers 1
     */
    @Test(enabled = true, groups = { "Functional", "RFA250" })
    @TestSuite
    public void verifyOpendjSSLConfiguration() {
        LOGGER.info("Filter Applyed : {} ", FILTER_BY_PARAMETER_DBx);
        final TestScenario verifyScenario = dataDrivenScenario("Verify Opendj SSL Configuration")
                .addFlow(functionalTestFlow.verifyOpendjSSLConfiguration())
                .withScenarioDataSources(dataSource(OPENDJSSLCONFIGURATION).withFilter(mvelFilter(FILTER_BY_PARAMETER_DBx)).allowEmpty())
//                .withScenarioDataSources(dataSourceRemapped(OPENDJSSLCONFIGURATION, FILTER_BY_PARAMETER_DBx).allowEmpty())
                .build();
        final TestScenarioRunner runner = runner().build();
        runner.start(verifyScenario);
    }

    /**
     * @DESCRIPTION Test cases verifying the properties of OpenDJ password policies
     * @PRE OpenDJ PKG is installed in the tested server and the service is running in this server
     * @PRIORITY HIGH
     * @VUsers 1
     */
    @Test(enabled = true, groups = { "Functional", "RFA250" })
    @TestSuite
    public void verifyPasswordPolicyProperties() {
        LOGGER.info("Filter Applyed : {} ", FILTER_BY_PARAMETER_DBx);
        final TestScenario verifyPassword = dataDrivenScenario("Verify Password Policy Properties")
                .addFlow(functionalTestFlow.verifyPasswordPolicyProperties())
                .withScenarioDataSources(dataSource(PASSWDPOLICYPROPERTIES).withFilter(mvelFilter(FILTER_BY_PARAMETER_DBx)).allowEmpty())
//                .withScenarioDataSources(dataSourceRemapped(PASSWDPOLICYPROPERTIES, FILTER_BY_PARAMETER_DBx).allowEmpty())
                .build();
        final TestScenarioRunner runner = runner().build();
        runner.start(verifyPassword);
    }

    /**
     * @DESCRIPTION Verify Opendj's basic configuration
     * @PRE TOR has been installed successfully and Opendj is running
     * @PRIORITY HIGH
     * @VUsers 1
     */
    @Test(enabled = true, groups = { "Functional", "RFA250" })
    @TestSuite
    public void verifyOpendjConfiguration() {
        LOGGER.info("Filter Applyed : {} ", FILTER_BY_PARAMETER_DBx);
        final TestScenario verifyPassword = dataDrivenScenario("Verify Opendj's basic configuration")
                .addFlow(functionalTestFlow.verifyOpenDJConfiguration())
                .withScenarioDataSources(dataSource(OPENDJCONFIGURATION).withFilter(mvelFilter(FILTER_BY_PARAMETER_DBx)).allowEmpty())
//                .withScenarioDataSources(dataSourceRemapped(OPENDJCONFIGURATION, FILTER_BY_PARAMETER_DBx).allowEmpty())
                .build();
        final TestScenarioRunner runner = runner().build();
        runner.start(verifyPassword);
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

    /**
     * Internal interface to get the authentication information for Opendj LDAPS connector
     */
    private String getLdapsAuthProperty() {
        final String userDN = DataHandler.getAttribute("ldapAuth.userDN").toString();
        final String port = DataHandler.getAttribute("ldapsAuth.port").toString();
        final String pwdValue = DataHandler.getAttribute("ldapAuth.userPassword").toString();
        final String authProp = "-p " + port + " --useSSL --trustAll " + " -D \"" + userDN + "\" -w " + pwdValue + " ";
        return authProp;
    }

    private String updateTestVariables(final String data) {
        if (data == null) {
            return null;
        }
        if (data.contains(TestwareSettings.ROOT_SUFFIX_VARIABLE)) {
            final String result = data.replace(TestwareSettings.ROOT_SUFFIX_VARIABLE, rootSuffixVariable);
            return result;
        } else {
            return data;
        }
    }

    private void handleHostNotFoundException(final HostNotFoundException e) {
        if (e instanceof Db1NotFoundException) {
            LOGGER.error("The DB1 node must be accessible to run OpenDJ test suite.");
            throw new RuntimeException(e);
        }
        LOGGER.warn("##### HOST NOT FOUND - TC OMMITED");
        LOGGER.info(e.getMessage());
    }

    /**
     * <pre>
     * <b>Name</b>: printDataSource            <i>[public]</i>
     * <b>Description</b>: Utility used to print Context DataSource.
     * </pre>
     *
     * @param dataSource - Context Datasource to print
     * @param logger     - Logger to use for DataSource Printing
     */
    void printDataSource(final String dataSource, final Logger logger) {
        final String SQUAREBRACKET_REMOVER = "[\\[\\]]";
        final String TAG_TO_SEARCH_01 = "Data value:";
        final String TAG_TO_REPLACE_01 = "\n\tData value:";
        final String REGEX_PATTERNS = "(?i:password[^,]*)";
        final String PWD_REPLACEMENT = "";
        String SEPARATOR = System.lineSeparator();
        final String dsContent = Iterables.toString(context.dataSource(dataSource));
        final String dsContentPretty = String.format("%n%n -- Print DataSource Content [Context]: <%s> %s%n", dataSource,
                dsContent.replaceAll(SQUAREBRACKET_REMOVER, "").replace(TAG_TO_SEARCH_01, TAG_TO_REPLACE_01)
                        .replaceAll(REGEX_PATTERNS, PWD_REPLACEMENT));
        logger.info(" {} {}", dsContentPretty, SEPARATOR);
    }
}
