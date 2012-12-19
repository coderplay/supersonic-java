/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.supersonic.base.infrastructure;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.supersonic.proto.CommonEnums.DataType;
import com.google.common.base.Preconditions;

/**
 * Helper class to instantiate TypeInfo singletons.
 * 
 * @author Min Zhou (coderplay@gmail.com)
 */
class TypeInfoResolver {
  private static final TypeInfoResolver instance = new TypeInfoResolver();

  final Map<DataType, TypeInfo> map = new HashMap<DataType, TypeInfo>();
  
  static TypeInfoResolver getInstance() {
    return instance;
  }

  TypeInfoResolver() {
    map.put(DataType.INT32, new TypeInfo(DataType.INT32, Integer.SIZE, true,
        true, false, false));
    map.put(DataType.INT64, new TypeInfo(DataType.INT64, Long.SIZE, true, true,
        false, false));
    map.put(DataType.UINT32, new TypeInfo(DataType.UINT32, Integer.SIZE, true,
        true, false, false));
    map.put(DataType.UINT64, new TypeInfo(DataType.UINT64, Long.SIZE, true,
        true, false, false));
    map.put(DataType.FLOAT, new TypeInfo(DataType.FLOAT, Float.SIZE, true,
        false, true, false));
    map.put(DataType.DOUBLE, new TypeInfo(DataType.DOUBLE, Double.SIZE, true,
        false, true, false));
    // FIXME: do assure the size of boolean
    map.put(DataType.BOOL, new TypeInfo(DataType.BOOL, 1, false, false, false,
        false));
    map.put(DataType.STRING, new TypeInfo(DataType.STRING, 32, false, false,
        false, true));
    map.put(DataType.DATETIME, new TypeInfo(DataType.DATETIME, Long.SIZE,
        false, false, false, false));
    map.put(DataType.DATE, new TypeInfo(DataType.DATE, Integer.SIZE, false,
        false, false, false));
    map.put(DataType.BINARY, new TypeInfo(DataType.BINARY, 32, false, false,
        false, true));
    map.put(DataType.DATA_TYPE, new TypeInfo(DataType.DATA_TYPE, 32, false, false,
        false, false));
  }

  public TypeInfo getTypeInfo(DataType type) {
    final TypeInfo type_info = map.get(type);
    Preconditions.checkNotNull(type_info);
    return type_info;
  }

}
