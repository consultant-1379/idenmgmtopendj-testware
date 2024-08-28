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

import static com.ericsson.cifwk.taf.scenario.TestScenarios.dataSource;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.configuration.TafConfigurationProvider;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.datasource.DataRecord;
import com.ericsson.cifwk.taf.mvel.TafMVELProcessor;
import com.ericsson.cifwk.taf.scenario.api.TafDataSourceDefinitionBuilder;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;

public class ScenarioBase extends TafTestBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScenarioBase.class);


    private static final String CLUSTER_ID = DataHandler.getConfiguration().getProperty("taf.clusterId", "", String.class);
    public static final String FILTER_BY_PARAMETER_DB4 = TafConfigurationProvider.provide().getProperty("vApp.db4.filter", "", String.class);
    public static final String FILTER_BY_PARAMETER_DB34 = TafConfigurationProvider.provide().getProperty("vApp.db34.filter", "", String.class);
    public static final String FILTER_BY_PARAMETER_DB234 = TafConfigurationProvider.provide().getProperty("vApp.db234.filter", "", String.class);
    public static final String FILTER_BY_PARAMETER_DB1234 = TafConfigurationProvider.provide().getProperty("vApp.db1234.filter", "", String.class);
    public static final String FILTER_BY_PARAMETER_DB14 = TafConfigurationProvider.provide().getProperty("vApp.db14.filter", "", String.class);
    public static final String FILTER_BY_PARAMETER_DB12 = TafConfigurationProvider.provide().getProperty("vApp.db12.filter", "", String.class);


    public static Predicate<DataRecord> mvelFilter(final String filterExpression) {
        return new Predicate<DataRecord>() {
            @Override
            public boolean apply(@Nullable final DataRecord dataRecord) {
                LOGGER.debug("Filter by MVel expression: {}", filterExpression);
                if (dataRecord == null) {
                    return false;
                }
                if (filterExpression == null || filterExpression.trim().length() == 0) {
                    LOGGER.trace("No Mvel Expression: all values are Ok");
                    return true;
                }
                final Boolean evalMvel = TafMVELProcessor.eval(filterExpression, dataRecord.getAllFields(), Boolean.class);
                LOGGER.trace("Evaluated mVel expression ({}) result: ", evalMvel);
                return evalMvel;
            }
        };
    }

    private static boolean isVappEnv() {
        if (!Strings.isNullOrEmpty(CLUSTER_ID)) {
            return CLUSTER_ID.equals("239") || CLUSTER_ID.equals("443");
        }
        return false;
    }

        protected static  TafDataSourceDefinitionBuilder<DataRecord> dataSourceRemapped(String name, final String filter) {
        if (!isVappEnv()) {
            return dataSource(name);
        } else {
            return dataSource(name).withFilter(mvelFilter(filter));
        }
    }
}
