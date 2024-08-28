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
package com.ericsson.nms.security.taf.test.helpers;

import javax.inject.Singleton;

@Singleton
public class TestwareSettings {

    public static final String ROOT_SUFFIX_VARIABLE = "COM_INF_LDAP_ROOT_SUFFIX";
    public static final String OPENDJ_ADMIN_PASSWORD_VARIABLE = "OPENDJ_ADMIN_PASSWORD";
    public static final String OPENDJ_DUMMY_ADMIN_PASSWORD_VARIABLE = "OPENDJ_DUMMY_ADMIN_PASSWORD";

    public static final String CHANGE_ADMIN_PWD_CMD = "change.pwd";
    public static final String OPENDJ_DECRYPT_CMD = "decrypt";
    public static final String CHANGE_DIR_CMD = "change.dir";
    public static final String LIST_DIR_CMD = "list.dir";
    public static final String GREP_CMD = "grep";
    public static final String DELETE_FILE_CMD = "delete";
    public static final String DECODE_JSON_CMD = "decodeJson";
    public static final String CMD_PREFIX = "clicommand.";

    public static final String VECTOR_PATTERN = "\"\\\"iv\\\" :\"";
    public static final String PWD_PATTERN = "\"\\\"data\\\" :\"";
    public static final String VECTOR_KEY = "iv";
    public static final String PWD_KEY = "data";
    public static final String[] REGEXES = { "^([-._a-zA-Z0-9])*$", "^(?=.*[A-Z])(?=.*[0-9]).{8,}$" };

    public static final String KEYSTORE_TYPE_PROPERTY = "openidm.keystore.type";
    public static final String CRYPTO_ALIAS_PROPERTY = "openidm.config.crypto.alias";
    public static final String PASSWORD_PROPERTY = "openidm.keystore.password";

    public static final String GLOBAL_PROPERTY_FILE = "/ericsson/tor/data/global.properties";
    public static final String ICF_LDAP_JSON_FILE = "/opt/openidm/conf/provisioner.openicf-ldap.json";
    public static final String SCRIPTS_DIR_REMOTE_TARGET = "/tmp";
    public static final String LOCAL_FILE_LOCATION = "/tmp/keystore.jceks";
    public static final String REMOTE_FILE_LOCATION = "/opt/openidm/security/keystore.jceks";
    public static final String BOOT_PROPERTIES_FILE = "/opt/openidm/conf/boot/boot.properties";

    public static final String FIRST_NODE = "sc1";
    public static final String SECOND_NODE = "sc2";

    public static final int CLOSE_SHELL_TIMEOUT = 5000;
    public static final int OPENIDM_RESTART_TIMEOUT = 150;
    public static final int THREAD_SLEEP_TIME_MILISEC = 5;

    public static final String UPLOAD_FILES_LOG_MSG = "Uploading test script files";
    public static final String DELETE_FILES_LOG_MSG = "Deleting test scripts files";
    public static final String DECRYPT_PWD_SCRIPT_FILENAME = "decryptPwd.sh";

}
