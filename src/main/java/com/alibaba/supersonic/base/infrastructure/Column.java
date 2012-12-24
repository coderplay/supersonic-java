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

import java.util.List;

import com.google.common.base.Preconditions;

/**
 * Immutable column content.
 * Knows its type. Currently, does not know its length. (May be revisited).
 * Exposes read-only pointers to the underlying data and nullability arrays,
 * via data() and is_null() methods. Allows these pointers to be reset,
 * via Reset() and ResetFrom() methods. Provides templatized methods for
 * type-safe access, should the type be known at compile time.
 * 
 * @author Min Zhou (coderplay@gmail.com)
 */
public class Column {

  private Attribute attribute;
  private TypeInfo typeInfo;

  @SuppressWarnings("rawtypes")
  private List data;

  /* A vector to point each element of data is null or not */
  private List<Boolean> isNull;
  
  Column() {
    
  }
  
  // Returns the associated schema attribute.
  final Attribute attribute() {
    checkInitialized();
    return attribute;
  }

  /**
   * Returns the (cached) {@link TypeInfo}
   * @return
   */
  final TypeInfo typeInfo() {
    checkInitialized();
    return typeInfo;
  }

  /*
   * Returns an untyped pointer to the data.
   */
  @SuppressWarnings("rawtypes")
  List data() { return data; }

  /**
   * Returns an untyped pointer to the data, plus the specified index.
   */
  Object get(int index) {
    return data().get(index);
  }

  /**
   * Returns the is_null vector.
   * Returns null for columns that are not nullable. May return null for
   * a column that is nullable according to the schema, but happens to have
   * no NULLs in this particular view.
   * @return
   */
  List<Boolean> isNull() { return isNull; }

  /**
   * A convenience method that returns the is_null vector shifted by the
   * specified offset, or null if the is_null vector == null.
   * @param index
   * @return
   */
  Boolean isNull(int index) {
    return isNull() == null ? null : isNull.get(index);
  }

  /**
   * Updates the column to point to a new place.
   * Ownership of data and is_null stays with the callee.
   * @param data
   * @param isNull
   */
  @SuppressWarnings("rawtypes")
  void reset(List data, List<Boolean> isNull) {
    checkInitialized();
    Preconditions.checkState(typeInfo == null || attribute().isNullable(),  
         "Attempt to use is_null vector for a non-nullable attribute "
        +"'" + attribute().getName() + "'");
    this.data = data;
    this.isNull = isNull;
  }

  /**
   * Updates the column to point to a new place, the same as pointed to by the
   * specified column.
   * @param other
   */
  void resetFrom(final Column other) {
    checkInitialized();
    Preconditions.checkState(typeInfo().type().equals(other.typeInfo().type()),  
        "Type mismatch; trying to reset " +  typeInfo().name() + " from "
        + other.typeInfo().name());
    reset(other.data(), other.isNull());
  }

  /**
   * Updates the column to point to a new place, as pointed to by the specified
   * column, plus the specified offset.
   * @param other
   * @param offset
   */
  void resetFromPlusOffset(final Column other, final int offset) {
    checkInitialized();
    Preconditions.checkState(typeInfo().type().equals(other.typeInfo().type()),
        "Type mismatch; trying to reset " + typeInfo().name() + " from "
            + other.typeInfo().name());
    List d = other.data();
    List<Boolean> n = other.isNull;
    reset(d.subList(offset, d.size()), n.subList(offset, n.size()));
  }

  /**
   * Resets only the is_null vector. If the column is not_nullable, does
   * nothing, if it is nullable, resets is_null to the bit_ptr passed.
   * Used to insert the incoming selection vector as the is_null vector of the
   * resultant view in expressions.
   * @param isNull
   */
  void resetIsNull(List<Boolean> isNull) {
    checkInitialized();
    if (attribute().isNullable()) this.isNull = isNull;
  }

  /**
   * Must be called before use, if the no-arg constructor was used to create.
   * Ownership of the attribute remains with the caller.
   * @param attribute
   */
  void init(final Attribute attribute) {
    Preconditions.checkState(typeInfo == null, "Column already initialized");
    this.attribute = attribute;
    this.typeInfo = TypeInfo.getTypeInfo(attribute.getType());
  }

  /**
   *  Returns the associated schema attribute.
   */
  public void checkInitialized() {
    Preconditions.checkNotNull(typeInfo, "Column not initialized");
  }
}
