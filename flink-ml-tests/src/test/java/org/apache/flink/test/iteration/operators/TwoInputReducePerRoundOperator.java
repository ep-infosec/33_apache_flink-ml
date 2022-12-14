/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.test.iteration.operators;

import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeutils.base.IntSerializer;
import org.apache.flink.iteration.operator.OperatorStateUtils;
import org.apache.flink.runtime.state.StateInitializationContext;
import org.apache.flink.runtime.state.StateSnapshotContext;
import org.apache.flink.streaming.api.operators.AbstractStreamOperator;
import org.apache.flink.streaming.api.operators.TwoInputStreamOperator;
import org.apache.flink.streaming.runtime.streamrecord.StreamRecord;
import org.apache.flink.util.OutputTag;

import java.util.Collections;

/**
 * An operator that reduce the received numbers and emit the result into the output, and also emit
 * the received numbers to the next operator.
 */
public class TwoInputReducePerRoundOperator extends AbstractStreamOperator<Integer>
        implements TwoInputStreamOperator<Integer, Integer, Integer> {

    public static final OutputTag<OutputRecord<Integer>> OUTPUT_TAG =
            new OutputTag<OutputRecord<Integer>>("output") {};

    private int round;

    private int sum;

    private ListState<Integer> roundState;

    private ListState<Integer> sumState;

    @Override
    public void initializeState(StateInitializationContext context) throws Exception {
        super.initializeState(context);
        roundState =
                context.getOperatorStateStore()
                        .getListState(new ListStateDescriptor<>("round", IntSerializer.INSTANCE));
        OperatorStateUtils.getUniqueElement(roundState, "round").ifPresent(r -> round = r);

        sumState =
                context.getOperatorStateStore()
                        .getListState(new ListStateDescriptor<>("sum", IntSerializer.INSTANCE));
        OperatorStateUtils.getUniqueElement(sumState, "sum").ifPresent(s -> sum = s);
    }

    @Override
    public void snapshotState(StateSnapshotContext context) throws Exception {
        super.snapshotState(context);
        roundState.update(Collections.singletonList(round));
        sumState.update(Collections.singletonList(sum));
    }

    @Override
    public void processElement1(StreamRecord<Integer> element) throws Exception {
        round = element.getValue();
        output.collect(new StreamRecord<>(element.getValue() + 1));
    }

    @Override
    public void processElement2(StreamRecord<Integer> element) throws Exception {
        sum += element.getValue();
    }

    @Override
    public void finish() throws Exception {
        output.collect(
                OUTPUT_TAG,
                new StreamRecord<>(new OutputRecord<>(OutputRecord.Event.TERMINATED, round, sum)));
    }
}
