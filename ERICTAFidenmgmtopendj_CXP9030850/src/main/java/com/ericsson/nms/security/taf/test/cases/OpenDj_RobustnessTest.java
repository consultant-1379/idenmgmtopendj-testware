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

import static com.ericsson.cifwk.taf.datasource.TafDataSources.fromTafDataProvider;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.*;
import static com.ericsson.nms.security.taf.test.constant.Constants.*;
import static com.ericsson.nms.security.taf.test.operators.OpenDjAndDbOperator.getHostLdapOpendj1;
import static com.ericsson.nms.security.taf.test.operators.OpenDjAndDbOperator.getHostLdapOpendj2;

import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.nms.security.taf.test.operators.OpenDjAndDbOperator;
import com.ericsson.oss.testware.hostconfigurator.HostConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.TestContext;
import com.ericsson.cifwk.taf.annotations.TestSuite;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.datasource.DataRecord;
import com.ericsson.cifwk.taf.datasource.TestDataSource;
import com.ericsson.cifwk.taf.scenario.TestScenario;
import com.ericsson.cifwk.taf.scenario.TestScenarioRunner;
import com.ericsson.nms.security.taf.test.flow.FunctionalTestFlow;
import com.ericsson.nms.security.taf.test.flow.UtilityTestFlow;
import com.ericsson.nms.security.taf.test.operators.OpenDJOperator;
import com.google.common.collect.Iterables;

public class OpenDj_RobustnessTest extends ScenarioBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenDj_RobustnessTest.class);
    static Host host = null;
    private String FILTER_BY_PARAMETER_DBx = "";
    private String vENM_Opendj2 = "opendj_2";
    private String vENM_Opendj_gethost = "opendj_1";

    @javax.inject.Inject
    private TestContext context;

    @javax.inject.Inject
    private UtilityTestFlow utilityTestFlow;

    @javax.inject.Inject
    private FunctionalTestFlow functionalTestFlow;

    @BeforeClass
    public void beforeClass() {
        TestDataSource<DataRecord> dataSource = fromTafDataProvider(OPENDJREPLICATIONSTATUS);
        context.addDataSource(OPENDJREPLICATIONSTATUS, dataSource);
        printDataSource(OPENDJREPLICATIONSTATUS, LOGGER);

        if ( HostConfigurator.isPhysicalEnvironment() ) {
            //Applica i filtri in funzione dei db che ospitano gli opendj
            final String LdapDatabase1 = getHostLdapOpendj1();
            LOGGER.info("First Instance for opendj : {}", LdapDatabase1);
            final String LdapDatabase2 = getHostLdapOpendj2();
            LOGGER.info("Second Instance for opendj : {}", LdapDatabase2);

            //EDAVIGA Add modification filter
            if (LdapDatabase1.equals(DB1) && LdapDatabase2.equals(DB1)) {
                FILTER_BY_PARAMETER_DBx = FILTER_BY_PARAMETER_DB1234;
                LOGGER.info("Filter Applied : {} ", FILTER_BY_PARAMETER_DBx);
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
    }

    @AfterClass
    public void closeShellConnections() {
        final TestScenario setUpScenario = scenario("After Class OpenDj_RobustnessTest")
                .addFlow(functionalTestFlow.closeConnection())
                .build();
        final TestScenarioRunner runner = runner().build();
        runner.start(setUpScenario);
    }

    /**
     * @DESCRIPTION Test case to check replication status
     * @PRE The opendj service is running with replication configuration
     * @PRIORITY HIGH
     */
    @Test(enabled = true, groups = { "Functional", "RFA250" })
    @TestSuite
    public void checkReplicationStatus() {
        if ( FILTER_BY_PARAMETER_DBx.equals(FILTER_BY_PARAMETER_DB234) ) {
            LOGGER.info("Opendj Single Instance : Skip Replication Test ");
        } else {
            LOGGER.info("Filter Applied : {} ", FILTER_BY_PARAMETER_DBx);
            final TestScenario verifyScenario = dataDrivenScenario("Verify Replication Status")
                    .addFlow(functionalTestFlow.verifyOpenDJReplicationStatus())
                    .withScenarioDataSources(dataSource(OPENDJREPLICATIONSTATUS).withFilter(mvelFilter(FILTER_BY_PARAMETER_DBx)).allowEmpty())
//                .withScenarioDataSources(dataSourceRemapped(OPENDJREPLICATIONSTATUS, FILTER_BY_PARAMETER_DBx).allowEmpty())
                    .build();
            final TestScenarioRunner runner = runner().build();
            runner.start(verifyScenario);
        }
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
