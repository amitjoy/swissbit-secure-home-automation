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
import com.whizzosoftware.wzwave.node.MultiChannelEncapsulatingNodeContext;
import com.whizzosoftware.wzwave.node.NodeContext;
import com.whizzosoftware.wzwave.node.ZWaveMultiChannelEndpoint;
import com.whizzosoftware.wzwave.util.ByteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Multi Instance Command Class
 *
 * @author Dan Noguerol
 */
public class MultiInstanceCommandClass extends CommandClass {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final byte ID = 0x60;

    // version 1
    public static final byte MULTI_INSTANCE_GET = 0x04;
    public static final byte MULTI_INSTANCE_REPORT = 0x05;
    public static final byte MULTI_INSTANCE_CMD_ENCAP = 0x06;

    // version 2
    public static final byte MULTI_CHANNEL_END_POINT_GET = 0x07;
    public static final byte MULTI_CHANNEL_END_POINT_REPORT = 0x08;
    public static final byte MULTI_CHANNEL_CAPABILITY_GET = 0x09;
    public static final byte MULTI_CHANNEL_CAPABILITY_REPORT = 0x0A;
    public static final byte MULTI_CHANNEL_END_POINT_FIND = 0x0B;
    public static final byte MULTI_CHANNEL_END_POINT_FIND_REPORT = 0x0C;
    public static final byte MULTI_CHANNEL_CMD_ENCAP = 0x0D;

    public static final byte IDENTICAL_ENDPOINTS = 0x40;

    private Map<Byte, ZWaveMultiChannelEndpoint> endpointMap = new HashMap<Byte, ZWaveMultiChannelEndpoint>();
    private int endpointCount;
    private boolean endpointsIdentical;

    @Override
    public byte getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "COMMAND_CLASS_MULTI_INSTANCE";
    }

    @Override
    public int getMaxSupportedVersion() {
        return 2;
    }

    /**
     * Returns all the endpoints associated with this node.
     *
     * @return a Collection of Endpoint instances
     */
    public Collection<ZWaveMultiChannelEndpoint> getEndpoints() {
        return endpointMap.values();
    }

    /**
     * Returns a specific endpoint.
     *
     * @param number the endpoint number
     *
     * @return an Endpoint instance (or null if not found)
     */
    public ZWaveMultiChannelEndpoint getEndpoint(byte number) {
        return endpointMap.get(number);
    }

    @Override
    public void onApplicationCommand(NodeContext context, byte[] ccb, int startIndex) {
        switch (ccb[startIndex+1]) {
            case MULTI_INSTANCE_REPORT: // v1
                processMultiInstanceReport();
                break;

            case MULTI_CHANNEL_END_POINT_REPORT: // v2
                processMultiChannelEndpointReport(context, ccb, startIndex);
                break;

            case MULTI_CHANNEL_CAPABILITY_REPORT: // v2
                processMultiChannelCapabilityReport(context, ccb, startIndex);
                break;

            case MULTI_CHANNEL_CMD_ENCAP: // v2
                processMultiChannelCommandEncapsulation(context, ccb, startIndex);
                break;

            default:
                logger.warn("Ignoring unsupported command: {}", ByteUtil.createString(ccb[startIndex+1]));
                break;
        }
    }

    @Override
    public int queueStartupMessages(NodeContext context, byte nodeId) {
        int count = 0;
        if (getVersion() == 1) {
            for (CommandClass cc : context.getCommandClasses()) {
                context.sendDataFrame(createMultiInstanceGetv1(nodeId, cc.getId()));
                count++;
            }
        } else {
            context.sendDataFrame(createMultiChannelEndPointGetv2(nodeId));
            count++;
        }
        return count;
    }

    protected void processMultiInstanceReport() {
        // TODO
        logger.debug("Received multi instance report -- not currently supported");
    }

    protected void processMultiChannelEndpointReport(NodeContext context, byte[] ccb, int startIndex) {
        this.endpointCount = ccb[startIndex+3] & 0x3F;
        this.endpointsIdentical = ((ccb[startIndex+2] & IDENTICAL_ENDPOINTS) > 0);

        if (endpointsIdentical) {
            // if the node reports all endpoints are identical, we simply query the first one for its
            // capabilities
            logger.debug(
                "Node {} has {} identical endpoints; querying for endpoint 1 capability",
                ByteUtil.createString(context.getNodeId()),
                endpointCount
            );
            context.sendDataFrame(createMultiChannelCapabilityGetv2(context.getNodeId(), (byte) 0x01));
        } else {
            // if the node reports all endpoints are NOT identical, query each endpoint individually
            logger.debug(
                "Node {} has {} non-identical endpoints; querying each endpoint",
                ByteUtil.createString(context.getNodeId()),
                endpointCount
            );
            for (int i=1; i <= endpointCount; i++) {
                context.sendDataFrame(createMultiChannelCapabilityGetv2(context.getNodeId(), (byte) i));
            }
        }
    }

    protected void processMultiChannelCommandEncapsulation(NodeContext context, byte[] ccb, int startIndex) {
        logger.trace("Got multi channel cmd encap response: src {}, dst {}", ByteUtil.createString(ccb[startIndex+2]), ByteUtil.createString(ccb[startIndex+3]));
        byte num = ccb[startIndex+2];
        byte cmdClass = ccb[startIndex+4];
        ZWaveMultiChannelEndpoint endpoint = endpointMap.get(num);
        MultiChannelEncapsulatingNodeContext context2 = new MultiChannelEncapsulatingNodeContext(endpoint.getNumber(), context);
        CommandClass cc = endpoint.getCommandClass(cmdClass);
        cc.onApplicationCommand(context2, ccb, startIndex+4);
    }

    protected void processMultiChannelCapabilityReport(NodeContext context, byte[] ccb, int startIndex) {
        byte endpoint = ccb[startIndex+2];
        logger.debug("Received multi channel capability report for endpoint {}", endpoint);

        if (endpointsIdentical) {
            // if endpoints are identical, use the same information for all of them
            for (int i=1; i <= endpointCount; i++) {
                createNewEndpoint(context, (byte)i, ccb, startIndex);
            }
        } else {
            // otherwise, create an endpoint for just the number we received
            createNewEndpoint(context, endpoint, ccb, startIndex);
        }
    }

    protected void createNewEndpoint(NodeContext context, byte number, byte[] ccb, int startIndex) {
        if (ccb.length > startIndex + 4) {
            byte genericDeviceClass = ccb[startIndex + 3];
            byte specificDeviceClass = ccb[startIndex + 4];

            ZWaveMultiChannelEndpoint ep = new ZWaveMultiChannelEndpoint(context.getNodeId(), number, genericDeviceClass, specificDeviceClass);
            MultiChannelEncapsulatingNodeContext context2 = new MultiChannelEncapsulatingNodeContext(number, context);

            for (int x = startIndex + 5; x < ccb.length; x++) {
                CommandClass cc = CommandClassFactory.createCommandClass(ccb[x]);

                // assume that the parent node's command class version is the same as the command classes for all endpoints
                CommandClass pcc = context.getCommandClass(ccb[x]);
                if (pcc != null) {
                    cc.setVersion(pcc.getVersion());
                }

                if (cc != null) {
                    ep.addCommandClass(cc.getId(), cc);
                    cc.queueStartupMessages(context2, context.getNodeId());
                } else {
                    logger.warn("Endpoint reported unknown command class: {}", ccb[x]);
                }
            }
            endpointMap.put(number, ep);
        } else {
            logger.error("Didn't receive any command class data for endpoint; ignoring it");
        }
    }

    /**
     * Create a MULTI_INSTANCE_GET command.
     *
     * @param nodeId the target node ID
     * @param commandClass the command class being requested
     *
     * @return a DataFrame instance
     */
    static public DataFrame createMultiInstanceGetv1(byte nodeId, byte commandClass) {
        return createSendDataFrame(
            "MULTI_INSTANCE_GET",
            nodeId,
            new byte[] {
                MultiInstanceCommandClass.ID,
                MULTI_INSTANCE_GET,
                commandClass
            },
            true
        );
    }

    /**
     * Create a MULTI_CHANNEL_END_POINT_GET command.
     *
     * @param nodeId the target node ID
     *
     * @return a DataFrame instance
     */
    static public DataFrame createMultiChannelEndPointGetv2(byte nodeId) {
        return createSendDataFrame(
            "MULTI_CHANNEL_END_POINT_GET",
            nodeId,
            new byte[] {
                MultiInstanceCommandClass.ID,
                MULTI_CHANNEL_END_POINT_GET
            },
            true
        );
    }

    /**
     * Create a MULTI_CHANNEL_CAPABILITY_GET command.
     *
     * @param nodeId the target node ID
     * @param endPoint the endpoint ID
     *
     * @return a DataFrame instance
     */
    static public DataFrame createMultiChannelCapabilityGetv2(byte nodeId, byte endPoint) {
        return createSendDataFrame(
            "MULTI_CHANNEL_CAPABILITY_GET",
            nodeId,
            new byte[] {
                MultiInstanceCommandClass.ID,
                MULTI_CHANNEL_CAPABILITY_GET,
                endPoint
            },
            true
        );
    }

    static public DataFrame createMultiChannelCmdEncapv2(byte sourceEndpoint, byte destEndpoint, DataFrame command, boolean responseExpected) {
        if (command instanceof SendData) {
            SendData sd = (SendData)command;
            byte[] data = sd.getSendData();

            byte[] newData = new byte[data.length + 4];
            newData[0] = MultiInstanceCommandClass.ID;
            newData[1] = MULTI_CHANNEL_CMD_ENCAP;
            newData[2] = sourceEndpoint;
            newData[3] = destEndpoint;
            System.arraycopy(data, 0, newData, 4, data.length);

            return createSendDataFrame(
                "MULTI_CHANNEL_CMD_ENCAP",
                sd.getNodeId(),
                newData,
                responseExpected
            );
        } else {
            return null;
        }
    }
}
