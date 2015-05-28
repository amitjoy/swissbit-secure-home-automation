/*******************************************************************************
 * Copyright (c) 2013 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.whizzosoftware.wzwave.frame.transaction;

import com.whizzosoftware.wzwave.frame.ACK;
import com.whizzosoftware.wzwave.frame.DataFrame;
import com.whizzosoftware.wzwave.frame.DataFrameType;
import com.whizzosoftware.wzwave.frame.Frame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A RequestResponseRequestTransaction is a transaction that is considered complete when:
 *
 * 1. An ACK is received
 * 2. A response is received with retVal == 0x01
 * 3. An request is received
 *
 * @author Dan Noguerol
 */
public class RequestResponseRequestTransaction extends AbstractDataFrameTransaction {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final int STATE_REQUEST_SENT = 1;
    private static final int STATE_ACK_RECEIVED = 2;
    private static final int STATE_RESPONSE_RECEIVED = 3;
    private static final int STATE_REQUEST_RECEIVED = 4;
    private static final int STATE_FAILED = 5;

    private DataFrame finalFrame;
    private int state;

    public RequestResponseRequestTransaction(DataFrame startFrame) {
        super(startFrame);
        this.state = STATE_REQUEST_SENT;
    }

    @Override
    public boolean addFrame(Frame bs) {
        switch (state) {

            case STATE_REQUEST_SENT:
                if (bs instanceof ACK) {
                    logger.trace("Received ACK as expected");
                    state = STATE_ACK_RECEIVED;
                    return true;
                } else {
                    setError("Received unexpected frame for STATE_REQUEST_SENT: " + bs);
                }
                break;

            case STATE_ACK_RECEIVED:
                if (bs instanceof DataFrame) {
                    DataFrame response = (DataFrame)bs;
                    if (response.getType() == DataFrameType.RESPONSE) {
                        if (wasSendSuccessful(response)) {
                            logger.trace("{} sent successfully", getStartFrame().getClass().getName());
                            state = STATE_RESPONSE_RECEIVED;
                            return true;
                        } else {
                            setError(getStartFrame().getClass().getName() + " not sent successfully");
                            state = STATE_FAILED;
                            return true;
                        }
                    } else {
                        setError("Received frame but doesn't appear to be a response: " + bs);
                        state = STATE_FAILED;
                    }
                } else {
                    setError("Received unexpected frame for STATE_ACK_RECEIVED");
                    state = STATE_FAILED;
                }
                break;

            case STATE_RESPONSE_RECEIVED:
                if (bs instanceof DataFrame) {
                    if (((DataFrame)bs).getType() == DataFrameType.REQUEST) {
                        logger.trace("Response received for {}", getStartFrame().getClass().getName());
                        state = STATE_REQUEST_RECEIVED;
                        finalFrame = (DataFrame)bs;
                        return true;
                    } else {
                        setError("Received data frame but doesn't appear to be a request: " + bs);
                        state = STATE_FAILED;
                    }
                } else {
                    setError("Received unexpected frame for STATE_RETVAL_RECEIVED");
                    state = STATE_FAILED;
                }
                break;

        }

        return false;
    }

    @Override
    public boolean isComplete() {
        return (state == STATE_REQUEST_RECEIVED || state == STATE_FAILED);
    }

    @Override
    public DataFrame getFinalFrame() {
        return finalFrame;
    }

    protected boolean wasSendSuccessful(DataFrame dataFrame) {
        return true;
    }
}
