/*
 * $Header$
 * $Revision$
 * $Date$
 * ------------------------------------------------------------------------------------------------------
 *
 * Copyright (c) Cubis Limited. All rights reserved.
 * http://www.cubis.co.uk
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.mule.ra;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.resource.spi.*;
import javax.resource.ResourceException;
import javax.security.auth.Subject;

/**
 * <code>DefaultConnectionManager</code> TODO
 *
 * @author <a href="mailto:ross.mason@cubis.co.uk">Ross Mason</a>
 * @version $Revision$
 */
public class DefaultConnectionManager implements ConnectionManager, ConnectionEventListener {

    private static final Log log = LogFactory.getLog(DefaultConnectionManager.class);

    /**
     * @see javax.resource.spi.ConnectionManager#allocateConnection(javax.resource.spi.ManagedConnectionFactory, javax.resource.spi.ConnectionRequestInfo)
     */
    public Object allocateConnection(ManagedConnectionFactory connectionFactory, ConnectionRequestInfo info) throws ResourceException {
        Subject subject = null;
        ManagedConnection connection = connectionFactory.createManagedConnection(subject, info);
        connection.addConnectionEventListener(this);
        return connection.getConnection(subject, info);
    }

    /**
     * @see javax.resource.spi.ConnectionEventListener#connectionClosed(javax.resource.spi.ConnectionEvent)
     */
    public void connectionClosed(ConnectionEvent event) {
        try {
            ((ManagedConnection) event.getSource()).cleanup();
        }
        catch (ResourceException e) {
            log.warn("Error occured during the cleanup of a managed connection: ", e);
        }
        try {
            ((ManagedConnection) event.getSource()).destroy();
        }
        catch (ResourceException e) {
            log.warn("Error occured during the destruction of a managed connection: ", e);
        }
    }

    /**
     * @see javax.resource.spi.ConnectionEventListener#localTransactionStarted(javax.resource.spi.ConnectionEvent)
     */
    public void localTransactionStarted(ConnectionEvent event) {
    }

    /**
     * @see javax.resource.spi.ConnectionEventListener#localTransactionCommitted(javax.resource.spi.ConnectionEvent)
     */
    public void localTransactionCommitted(ConnectionEvent event) {
    }

    /**
     * @see javax.resource.spi.ConnectionEventListener#localTransactionRolledback(javax.resource.spi.ConnectionEvent)
     */
    public void localTransactionRolledback(ConnectionEvent event) {
    }

    /**
     * @see javax.resource.spi.ConnectionEventListener#connectionErrorOccurred(javax.resource.spi.ConnectionEvent)
     */
    public void connectionErrorOccurred(ConnectionEvent event) {
        log.warn("Managed connection experiened an error: ", event.getException());
        try {
            ((ManagedConnection) event.getSource()).cleanup();
        }
        catch (ResourceException e) {
            log.warn("Error occured during the cleanup of a managed connection: ", e);
        }
        try {
            ((ManagedConnection) event.getSource()).destroy();
        }
        catch (ResourceException e) {
            log.warn("Error occured during the destruction of a managed connection: ", e);
        }
    }

}
