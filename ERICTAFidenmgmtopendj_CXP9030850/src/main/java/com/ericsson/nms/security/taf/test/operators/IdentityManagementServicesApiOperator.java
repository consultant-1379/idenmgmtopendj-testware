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

import com.ericsson.oss.itpf.security.identitymgmtservices.*;
import com.ericsson.oss.itpf.security.identitymgmtservices.ejb.IdentityManagementServiceBean;

@Singleton
public class IdentityManagementServicesApiOperator {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdentityManagementServicesApiOperator.class);
    private static final String SERVICE_NAME = "identitymgmtservices";

    @Inject
    Provider<SecurityServiceOperator> provider;

    public String createM2MUser(final String userName, final String groupName, final String homeDir, final String validDays) {
        final IdentityManagementService ims = locateIdentityManagementServiceBean();
        final M2MUser user = ims.createM2MUser(userName, groupName, homeDir, Integer.parseInt(validDays));
        return user.getUserName();
    }

    public boolean removeM2MUser(final String userName) {
        final IdentityManagementService ims = locateIdentityManagementServiceBean();
        final boolean deleted = ims.deleteM2MUser(userName);
        return deleted;
    }

    public String createProxyUser() {
        final IdentityManagementService ims = locateIdentityManagementServiceBean();
        final ProxyAgentAccountData proxyAgentAccountData = ims.createProxyAgentAccount();
        LOGGER.info("proxyAgentAccountData: {}", proxyAgentAccountData);
        return proxyAgentAccountData.getUserDN();
    }

    public boolean removeProxyUser(final String userDN) {
        final IdentityManagementService ims = locateIdentityManagementServiceBean();
        final boolean deleted = ims.deleteProxyAgentAccount(userDN);
        return deleted;
    }

    private IdentityManagementService locateIdentityManagementServiceBean() {
        final SecurityServiceOperator secServOperator = provider.get();
        final IdentityManagementService locateBean = secServOperator.locateEjb(IdentityManagementServiceBean.class.getSimpleName(),
                IdentityManagementService.class.getName(), SERVICE_NAME);
        return locateBean;
    }

}
