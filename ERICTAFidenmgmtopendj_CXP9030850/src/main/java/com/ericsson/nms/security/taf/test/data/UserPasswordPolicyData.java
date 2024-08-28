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
package com.ericsson.nms.security.taf.test.data;

import com.ericsson.cifwk.taf.datasource.DataRecord;

public interface UserPasswordPolicyData extends DataRecord {

    String USER_PASSWORD_POLICY_DATA = "UserPasswordPolicyData";

    String USER_PASSWORD_EXPIRATION_DATA = "UserPasswordExpirationData";

    String getExaminingString();

    String getUser();

    Boolean getShouldContainPasswordExpirationTimeAttribute();

}
