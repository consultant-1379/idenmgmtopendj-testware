/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.taf.test.cases;

import static com.ericsson.cifwk.taf.datasource.TafDataSources.fromTafDataProvider;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.dataDrivenScenario;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.dataSource;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.flow;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.runner;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.scenario;
import static com.ericsson.nms.security.taf.test.constant.Constants.OPENDJCONNECTION;
import static com.ericsson.nms.security.taf.test.constant.Constants.OPENDJSSLCONFIGURATION;

import javax.inject.Inject;

import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.nms.security.taf.test.operators.OpenDjAndDbOperator;
import org.assertj.core.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.TestContext;
import com.ericsson.cifwk.taf.annotations.TestSuite;
import com.ericsson.cifwk.taf.datasource.DataRecord;
import com.ericsson.cifwk.taf.datasource.TestDataSource;
import com.ericsson.cifwk.taf.scenario.TestScenario;
import com.ericsson.cifwk.taf.scenario.TestScenarioRunner;
import com.ericsson.nms.security.taf.test.flow.FunctionalTestFlow;
import com.ericsson.oss.testware.hostconfigurator.HostConfigurator;
import com.google.common.collect.Iterables;

public class ChangeDefaultPasswordsOnDbNodes extends ScenarioBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeDefaultPasswordsOnDbNodes.class);
    static Host host = null;
    private String FILTER_BY_PARAMETER_DBx = "";
    private String vENM_Opendj2 = "opendj_2";
    private String vENM_Opendj_gethost = "opendj_1";

    @Inject
    private TestContext context;

    @Inject
    private FunctionalTestFlow functionalTestFlow;

    @BeforeClass
    public void setUpBeforeClass() {
        final TestDataSource<DataRecord> dataSource = fromTafDataProvider(OPENDJCONNECTION);
        context.addDataSource(OPENDJCONNECTION, dataSource);
        printDataSource(OPENDJCONNECTION, LOGGER);
        String dbType = "ciao";
        if ( HostConfigurator.isPhysicalEnvironment() ) {
            final Integer dbTotalNumber = OpenDjAndDbOperator.getdbNumber();
            LOGGER.debug("BeforeClass Number of DB: {}", dbTotalNumber);
            if (dbTotalNumber == 3) {
                FILTER_BY_PARAMETER_DBx = FILTER_BY_PARAMETER_DB4;
                LOGGER.debug("Filter Applied : {} ", FILTER_BY_PARAMETER_DBx);
            }
            if (dbTotalNumber == 2) {
                FILTER_BY_PARAMETER_DBx = FILTER_BY_PARAMETER_DB34;
                LOGGER.debug("Filter Applied : {} ", FILTER_BY_PARAMETER_DBx);
            }
            if (dbTotalNumber == 1) {
                FILTER_BY_PARAMETER_DBx = FILTER_BY_PARAMETER_DB234;
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
        final TestScenario setUpScenario = scenario("After Class ChangeDefaultPasswordsOnDbNodes")
                .addFlow(functionalTestFlow.closeConnection())
                .build();
        final TestScenarioRunner runner = runner().build();
        runner.start(setUpScenario);
    }

    @Test(enabled = true, groups = { "Functional", "RFA250" })
    @TestSuite
    public void changePasswordsAndVerifyAccessToDbNodes() {
        LOGGER.info("Filter Applied : {} ", FILTER_BY_PARAMETER_DBx);
        final boolean isPhysical = HostConfigurator.isPhysicalEnvironment();
        final TestScenario verifyScenario = dataDrivenScenario("Verify Connection and change password")
                .addFlow(functionalTestFlow.verifyConnection())
                .addFlow(isPhysical ? functionalTestFlow.changePassword() : flow("Empty"))
                .withScenarioDataSources(dataSource(OPENDJCONNECTION).withFilter(mvelFilter(FILTER_BY_PARAMETER_DBx)).allowEmpty())
                .build();
        final TestScenarioRunner runner = runner().build();
        runner.start(verifyScenario);
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
