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

import java.util.List;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.cifwk.taf.handlers.AsRmiHandler;
import com.ericsson.oss.testware.hostconfigurator.HostConfigurator;

@Singleton
public class SecurityServiceOperator {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityServiceOperator.class);
    private static Host secServInternal;
    private static AsRmiHandler asRmiHandler;

    <T> T locateEjb(final String beanSimpleName, final String interfaceName, final String serviceName) {

        LOGGER.info("Trying to connect to secServInternal = secserv_1 service group");
        secServInternal = getSecServInternal("secserv_1");

        if (secServInternal == null) {
            LOGGER.info("Trying to connect to secServInternal = security_1 service group");
            secServInternal = getSecServInternal("security_1");
        }

        if (secServInternal == null) {
            throw new IllegalStateException("Failed to locate service");
        }

        T locateBean = locateBean(beanSimpleName, interfaceName, serviceName);
        locateBean = locateBean(beanSimpleName, interfaceName, serviceName);
        return locateBean;
    }

    @SuppressWarnings("unchecked")
    private <T> T locateBean(final String beanSimpleName, final String interfaceName, final String serviceName) {

        final String patternJNDI = serviceName + "-ear-%1$s/" + serviceName + "-ejb-%1$s/%2$s!%3$s";
        LOGGER.info("patternJNDI: {}", patternJNDI);
        try {

            asRmiHandler = new AsRmiHandler(secServInternal);
            final List<String> versionList = asRmiHandler.getServiceVersion(serviceName);

            final int firstElement = 0;
            final String version = versionList.get(firstElement);
            LOGGER.info("Found {} version: {}", serviceName, version);
            final String jndi = String.format(patternJNDI, version, beanSimpleName, interfaceName);
            LOGGER.info("JNDI:  {}", jndi);

            return (T) asRmiHandler.getServiceViaJndiLookup(jndi);

        } catch (final Exception error) {
            LOGGER.warn("Failed to locate {} EJB on {}!", interfaceName, secServInternal.getHostname());
            secServInternal = null;
            return (T) secServInternal;
        }
    }

    private Host getSecServInternal(final String name) {
        LOGGER.info("start getSecServInternal = " + name);
        if (secServInternal == null) {
            LOGGER.info("1st getSecServInternal step  check hostname = " + name);
                      try {
                          Host host = HostConfigurator.getHost(name);

                          if (host != null) {
                              LOGGER.debug("hostName = " + host.getHostname());
                              LOGGER.debug("host_user = " + host.getUser());
                              LOGGER.debug("host_password = " + host.getPass());
                              LOGGER.debug("host_ip= " + host.getIp());
                              secServInternal = HostConfigurator.useInternal(HostConfigurator.getHost(name));
                              LOGGER.debug("1st getSecServInternal step  add user");
                              secServInternal.addUser("guest", "guestp", UserType.OPER);
                              LOGGER.debug("complete getSecServInternal = " + secServInternal);
                          }
                                          }
                       catch ( final Exception error)
                        {LOGGER.info("exception error on host"+error.getMessage());
                        }
           /* secServInternal = HostConfigurator.useInternal(HostConfigurator.getHost(name));
            secServInternal.addUser("guest", "guestp", UserType.OPER);*/
        }

        return secServInternal;
    }

    public void closeEjbConnection() {

        asRmiHandler.close();
        asRmiHandler = null;
    }
}
