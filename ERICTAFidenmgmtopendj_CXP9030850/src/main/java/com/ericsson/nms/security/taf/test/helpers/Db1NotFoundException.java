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
package com.ericsson.nms.security.taf.test.helpers;

public class Db1NotFoundException extends HostNotFoundException {
    private static final long serialVersionUID = 1L;

    public Db1NotFoundException(final String string) {
        super(string);
    }

    public Db1NotFoundException() {
        super();
    }
}
