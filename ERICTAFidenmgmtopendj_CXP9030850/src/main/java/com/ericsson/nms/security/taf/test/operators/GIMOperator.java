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
package com.ericsson.nms.security.taf.test.operators;

import javax.inject.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.security.genericidentitymgmtservice.ejb.user.TemporaryAmosUserBean;
import com.ericsson.oss.services.security.genericidentitymgmtservice.user.TemporaryAmosUserBeanRemote;

@Singleton
public class GIMOperator {

    @Inject
    Provider<SecurityServiceOperator> provider;

    private static final Logger LOGGER = LoggerFactory.getLogger(GIMOperator.class);
    private static final String SERVICE_NAME = "generic-identity-mgmt-service";

    public void createTempAmosUser(final String amosUser) {
        final TemporaryAmosUserBeanRemote tempAmosBean = locateTemporaryAmosUserBean();
        final String tempAmosUser;
        tempAmosUser = tempAmosBean.createTemporaryAmosUser(amosUser);
        LOGGER.info("createTempAmosUser: {}" + tempAmosUser);
    }

    private TemporaryAmosUserBeanRemote locateTemporaryAmosUserBean() {
        final SecurityServiceOperator secServOperator = provider.get();
        final TemporaryAmosUserBeanRemote locateBean = secServOperator.locateEjb(TemporaryAmosUserBean.class.getSimpleName(),
                TemporaryAmosUserBeanRemote.class.getName(), SERVICE_NAME);
        return locateBean;
    }

}
