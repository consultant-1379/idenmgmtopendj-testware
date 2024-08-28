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

import java.util.Map;

import com.ericsson.nms.security.taf.test.helpers.HostNotFoundException;
import org.assertj.core.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.datasource.DataRecord;
import com.ericsson.cifwk.taf.datasource.DataRecordImpl;
import com.ericsson.nms.security.taf.test.operators.OpenDjAndDbOperator;
import com.ericsson.nms.security.taf.test.operators.OpenDJOperator;
import com.ericsson.oss.testware.remoteexecution.operators.RemoteEnmBuilderImplement;
import com.google.common.collect.Maps;

public class UtilityTestSteps extends BaseTestSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(UtilityTestSteps.class);

    public static final String GET_CRIP_LDAP_ADMIN_PASSWORD = "getCripLdapAdminPassword";
    public static final String GET_LDAP_ADMIN_PASSWORD = "getLdapAdminPassword";
    public static final String GET_COM_INF_LDAP_ROOT_SUFFIX = "geComInfLdapSuffix";

    public static final String PARAM = "param";

    private static final String NULLSTRING = "String is null";
    private static final String INVALIDSTRING = "Invalid result: %s";

    private final String PENM_VEM_COMMAND_CRIP = "cat /ericsson/tor/data/global.properties | grep %s | cut -d '=' -f 2- | head -n 1";
    private final String CEM_COMMAND_CRIP = "kubectl describe -n %s cm gpcloud | grep %s | cut -d '=' -f 2- | head -n 1";

    private final String PENM_VEM_COMMAND_CLEAR =
            "echo %s | openssl enc -a -d -aes-128-cbc -salt -kfile " + "'/ericsson/tor/data/idenmgmt/opendj_passkey'";

    private final String CEM_COMMAND_CLEAR =
            "echo %s | openssl enc -a -d -md md5 -aes-128-cbc -salt -kfile " + "'/ericsson/tor/data/idenmgmt/opendj_passkey'";

    @TestStep(id = GET_CRIP_LDAP_ADMIN_PASSWORD)
    public String getCripLdapAdminPassword() {
        final String parameter = "LDAP_ADMIN_PASSWORD";
        final OpenDjAndDbOperator openDJOperator = provider.get();
        openDJOperator.setDefaultParam();
        String command = "";
        if (!isCloudEnv) {
            command = String.format(PENM_VEM_COMMAND_CRIP, parameter);
        } else {
            command = String.format(CEM_COMMAND_CRIP, openDJOperator.getPibHost().getNamespace(), parameter);
        }
        final String result = openDJOperator.getRemoteEnmBuilderImplement().execPibCommandWithOutput(command);
        Assertions.assertThat(result).as(NULLSTRING).isNotEmpty();
        LOGGER.info("LDAP_ADMIN_PASSWORD : " + result);
        return result;
    }

    @TestStep(id = GET_LDAP_ADMIN_PASSWORD)
    public void getLdapAdminPassword(@Input(PARAM) final String param) throws HostNotFoundException {
        final String parameter = param;
        final OpenDjAndDbOperator openDJOperator = provider.get();
        openDJOperator.setDefaultParam();
        final OpenDJOperator openDJOperator1 = new OpenDJOperator();
        final String command = !isCloudEnv ? String.format(PENM_VEM_COMMAND_CLEAR, parameter) : String.format(CEM_COMMAND_CLEAR, parameter);
        if (!isCloudEnv) {
            if (!isPhysdEnv) {
                final String chownString = String.format("sudo chown -R %s /ericsson/tor/data/idenmgmt/opendj_passkey",
                        openDJOperator.getRemoteEnmBuilderImplement().getTargetHost().getDefaultUser().getUsername());
                openDJOperator.getRemoteEnmBuilderImplement().execPibCommandWithOutput(chownString);
            }
            LOGGER.info("pENM environment no chown commad");
        }
        String result = "";
        if (isVirtdEnv) {
            result = openDJOperator.getRemoteEnmBuilderImplement().execPibCommandWithOutput(command);
        }
        if (isPhysdEnv) {
            result = openDJOperator1.getDirectoryManagerPasswordLDAP(parameter);
            LOGGER.info("LDAP_ADMIN_PASSWORD Decripted = " + result);
        }
        if (isCloudEnv) {
            result = openDJOperator.getRemoteEnmBuilderImplement().execCommand(command);
        }
//        if (!isPhysdEnv) {
            final boolean invalidResult = result.contains("\n");
            Assertions.assertThat(invalidResult).as(String.format(INVALIDSTRING, result)).isFalse();
            if (!isCloudEnv) {
                if (!isPhysdEnv){
                final String users = isPhysdEnv ? "root" : "root:jboss";
                openDJOperator.getRemoteEnmBuilderImplement()
                        .execPibCommandWithOutput(String.format("sudo chown -R %s /ericsson/tor/data/idenmgmt/opendj_passkey", users));
            }
        }
            LOGGER.info("D11 : LDAP_ADMIN_PASSWORD : " + result);
            DataHandler.setAttribute("ldapAuth.userPassword", result);
            DataHandler.setAttribute("replication.password", result);
    }

    @TestStep(id = GET_COM_INF_LDAP_ROOT_SUFFIX)
    public void getComInfLdapSuffix() {
        final String parameter = "COM_INF_LDAP_ROOT_SUFFIX";
        final OpenDjAndDbOperator openDJOperator = provider.get();
        openDJOperator.setDefaultParam();
        String command = "";
        if (!isCloudEnv) {
            command = String.format(PENM_VEM_COMMAND_CRIP, parameter);
        } else {
            command = String.format(CEM_COMMAND_CRIP, openDJOperator.getPibHost().getNamespace(), parameter);
        }
        final String result = openDJOperator.getRemoteEnmBuilderImplement().execPibCommandWithResult(command).getOutput();
        Assertions.assertThat(result).as(NULLSTRING).isNotEmpty();
        DataHandler.setAttribute("ldap.rootSuffixVariable", String.format("%s", result));
    }

}
