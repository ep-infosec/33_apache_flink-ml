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

package org.apache.flink.ml.common.broadcast.typeinfo;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeutils.TypeSerializer;

import java.util.Objects;

/**
 * TypeInformation for {@link CacheElement}.
 *
 * @param <T> the record type.
 */
public class CacheElementTypeInfo<T> extends TypeInformation<CacheElement<T>> {

    private final TypeInformation<T> recordTypeInfo;

    public CacheElementTypeInfo(TypeInformation<T> recordTypeInfo) {
        this.recordTypeInfo = recordTypeInfo;
    }

    @Override
    public boolean isBasicType() {
        return false;
    }

    @Override
    public boolean isTupleType() {
        return false;
    }

    @Override
    public int getArity() {
        return 1;
    }

    @Override
    public int getTotalFields() {
        return 1;
    }

    @Override
    public Class<CacheElement<T>> getTypeClass() {
        return (Class) CacheElement.class;
    }

    @Override
    public boolean isKeyType() {
        return false;
    }

    @Override
    public TypeSerializer<CacheElement<T>> createSerializer(ExecutionConfig config) {
        return new CacheElementSerializer<>(recordTypeInfo.createSerializer(config));
    }

    @Override
    public String toString() {
        return "RecordOrWatermark Type";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (null == obj || getClass() != obj.getClass()) {
            return false;
        }

        CacheElementTypeInfo<T> that = (CacheElementTypeInfo<T>) obj;
        return Objects.equals(recordTypeInfo, that.recordTypeInfo);
    }

    @Override
    public int hashCode() {
        return recordTypeInfo != null ? recordTypeInfo.hashCode() : 0;
    }

    @Override
    public boolean canEqual(Object obj) {
        return obj instanceof CacheElementTypeInfo;
    }
}
