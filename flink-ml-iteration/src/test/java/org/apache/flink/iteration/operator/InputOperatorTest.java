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

package org.apache.flink.iteration.operator;

import org.apache.flink.iteration.IterationRecord;
import org.apache.flink.streaming.runtime.streamrecord.StreamRecord;
import org.apache.flink.streaming.util.OneInputStreamOperatorTestHarness;
import org.apache.flink.streaming.util.TestHarnessUtil;
import org.apache.flink.util.TestLogger;

import org.junit.Test;

import java.util.concurrent.ConcurrentLinkedQueue;

/** Tests the {@link InputOperator}. */
public class InputOperatorTest extends TestLogger {

    @Test
    public void testWrapRecord() throws Exception {
        OneInputStreamOperatorTestHarness<Integer, IterationRecord<Integer>> testHarness =
                new OneInputStreamOperatorTestHarness<>(new InputOperator<>());
        testHarness.open();

        ConcurrentLinkedQueue<Object> expectedOutput = new ConcurrentLinkedQueue<>();
        for (int i = 0; i < 5; ++i) {
            testHarness.processElement(i, 1000);
            expectedOutput.add(new StreamRecord<>(IterationRecord.newRecord(i, 0), 1000));
        }

        TestHarnessUtil.assertOutputEquals(
                "Output was not correct", expectedOutput, testHarness.getOutput());
    }
}
