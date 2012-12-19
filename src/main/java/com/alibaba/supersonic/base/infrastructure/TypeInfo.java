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

import com.alibaba.supersonic.proto.CommonEnums.DataType;

/**
 * Dynamic type information. Each value from enum Type has a corresponding
 * singleton TypeInfo object, that can be obtained by calling GetTypeInfo(type).
 * @author Min Zhou (coderplay@gmail.com)
 *
 */
public class TypeInfo {
  final DataType type;
  final String name;
  final int size;
  final int log2Size;
  final boolean isNumeric;
  final boolean isInteger;
  final boolean isFloatingPoint;
  final boolean isVariableLength;

  public static TypeInfo getTypeInfo(DataType type) {
    return TypeInfoResolver.getInstance().getTypeInfo(type);
  }
 
  /**
   * Constructor, called by TypeInfoResolver.
   */
  TypeInfo(DataType type, int size, boolean isNumeric, boolean isInteger,
      boolean isFloatingPoint, boolean isVariableLength) {
    this.type = type;
    this.name = type.name();
    this.size = size;
    this.log2Size = -1;
    this.isNumeric = isNumeric;
    this.isInteger = isInteger;
    this.isFloatingPoint = isFloatingPoint;
    this.isVariableLength = isVariableLength;
  }

  /**
   * Returns the enum Type value that this typeinfo corresponds to.
   */
  public DataType type() { return type; }

  /**
   * Returns the String name of this type.
   */
  public final String name() { return name; }

  /**
   * Returns the number of bytes that a value of that type uses in memory.
   * @return
   */
  public int size()  { return size; }

  /**
   * Returns the log2 of the size(), so that offsets can be quickly computed
   * using left shift.
   * @return
   */
  public int log2Size()  { return log2Size; }

  /**
   * Returns if the type represents a variable length type, allocated using
   * an arena and represented by StringPiece.
   * @return
   */
  public boolean isVariableLength() { return isVariableLength; }

  /**
   * Returns true if the type represents a number; false otherwise.
   * @return
   */
  public boolean isNumeric()  { return isNumeric; }

  /**
   * Returns true if the type represents an integer number; false otherwise.
   * @return
   */
  public boolean isInteger()  { return isInteger; }

  /**
   * Returns true if the type represents a floating point number; false
   * otherwise.
   * @return
   */
  public boolean isFloatingPoint()  { return isFloatingPoint; }

}
