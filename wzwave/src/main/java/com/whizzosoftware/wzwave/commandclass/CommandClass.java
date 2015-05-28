/*******************************************************************************
 * Copyright (c) 2013 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.wzwave.commandclass;

import com.whizzosoftware.wzwave.frame.DataFrame;
import com.whizzosoftware.wzwave.frame.SendData;
import com.whizzosoftware.wzwave.node.NodeContext;

/**
 * Abstract base class for all Command classes
 *
 * @author Dan Noguerol
 */
abstract public class CommandClass {
    private int version = 1; // always assume version 1 unless told otherwise

    /**
     * Returns the command class version
     *
     * @return the version
     */
    public int getVersion() {
        return version;
    }

    /**
     * Returns the highest supported command class version
     *
     * @return the version
     */
    public int getMaxSupportedVersion() {
        return 1;
    }

    /**
     * Sets the command class version
     *
     * @param version the version
     */
    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * Returns the command class ID
     *
     * @return the ID as a byte
     */
    abstract public byte getId();

    /**
     * Returns the command class name. This is primarily for logging purposes.
     *
     * @return the name
     */
    abstract public String getName();

    /**
     * Callback for an Application Command being received for this command class.
     *
     * @param context the NodeContext for the new data frame
     * @param ccb the command class data
     * @param startIndex the array index from which to start reading command class data
     */
    abstract public void onApplicationCommand(NodeContext context, byte[] ccb, int startIndex);

    /**
     * Give the command class the opportunity to queue up any data frames it
     * needs to send at startup time (e.g. to request it's current state)
     *
     * @param context the NodeContext to use when queueing additional data frames, etc.
     * @param nodeId the destination node ID
     *
     * @return the number of messages queued
     */
    abstract public int queueStartupMessages(NodeContext context, byte nodeId);

    /**
     * Convenience method for creating SendData frames
     *
     * @param name the name for logging purposes
     * @param nodeId the destination node ID
     * @param data the data portion of the SendData frame
     * @param isResponseExpected indicates whether sending this data frame should require a response
     *
     * @return a DataFrame instance
     */
    static protected DataFrame createSendDataFrame(String name, byte nodeId, byte[] data, boolean isResponseExpected) {
        return new SendData(name, nodeId, data, (byte)0x05, isResponseExpected);
    }
}
