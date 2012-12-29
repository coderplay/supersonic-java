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

import com.google.common.base.Preconditions;

/**
 * Read-only view of a multi-column 'block' of data. The data is usually owned
 * by one or more {@link Block}s.
 * 
 * @author Min Zhou (coderplay@gmail.com)
 */
public class View {

  private final TupleSchema schema_;
  private Column[] columns_;
  private int row_count_;

  // Views are copyable.

  /**
   * Creates an empty view that can hold data of the specified schema.
   */
  public View(final TupleSchema schema) {
    this.schema_ = schema;
    this.columns_ = new Column[schema.attributeCount()];
    this.row_count_ = 0;
    init();
  }

  /**
   * A convenience copy constructor.
   * 
   * @param other
   */
  public View(final View other) {
    this.schema_ = other.schema();
    this.columns_ = new Column[other.schema().attributeCount()];
    this.row_count_ = 0;
    init();
    resetFrom(other);
  }

  /**
   * A convenience 'subrange' copy constructor.
   */
  public View(final View other, int offset, int row_count) {

    this.schema_ = other.schema();
    this.columns_ = new Column[other.schema().attributeCount()];
    this.row_count_ = 0;
    init();
    resetFromSubRange(other, offset, row_count);
  }

  /**
   * A convenience constructor, wrapping a single column into a view.
   */
  public View(final Column column, int row_count) {
    this.schema_ =
        TupleSchema.singleton(column.attribute().getName(), column.attribute()
            .getType(), column.attribute().getNullability());
    this.columns_ = new Column[1];
    this.row_count_ = row_count;
    init();
    // mutable_column(0)->ResetFrom(column);
  }

  /**
   * Returns the view's schema.
   * @return
   */
  public final TupleSchema schema() {
    return schema_;
  }

  /**
   * Equivalent to schema().attributeCount().
   * @return
   */
  public int columnCount() {
    return schema().attributeCount();
  }

  /**
   * Returns the number of rows in the view.
   * @return the number of rows in the view.
   */
  public int rowCount() {
    return row_count_;
  }

  /**
   * Sets the row count. Unchecked.
   * @param row_count
   */
  public void setRowCount(final int row_count) {
    row_count_ = row_count;
  }

  /**
   * Returns an immutable reference to the specified column.
   * @param column_index
   * @return
   */
  public final Column column(int column_index) {
    Preconditions.checkPositionIndex(column_index, columnCount());
    return columns_[column_index];
  }

  /**
   * Returns a pointer to the mutable (resettable) column.
   * 
   * @param column_index
   * @return
   */
  Column mutableColumn(int column_index) {
    Preconditions.checkPositionIndex(column_index, columnCount());
    return columns_[column_index];
  }

  /**
   * Resets View's columns from another View. Sets the row_count as well.
   * @param other
   */
  void resetFrom(final View other) {
    for (int i = 0; i < schema_.attributeCount(); ++i) {
      mutableColumn(i).resetFrom(other.column(i));
    }
    setRowCount(other.rowCount());
  }

  /**
   * Resets View's columns from a sub-range in an another View. Sets the
   * row_count as well.
   * 
   * @param other
   * @param offset
   * @param row_count
   */
  void resetFromSubRange(final View other, int offset, int row_count) {
    Preconditions.checkPositionIndex(offset, other.rowCount());
    for (int i = 0; i < columnCount(); ++i) {
      mutableColumn(i).resetFromPlusOffset(other.column(i), offset);
    }
    setRowCount(row_count);
  }

  /**
   * Advances the view by the specified offset. Reduces view size accordingly.
   * In debug mode, guards against moving past the range.
   * Note: views can only more forward; the offset is unsigned.
   * 
   * @param offset
   */
  void advance(int offset) {
    Preconditions.checkPositionIndex(offset, rowCount());
    for (int i = 0; i < columnCount(); i++) {
      Column column = mutableColumn(i);
      column.resetFromPlusOffset(column, offset);
    }
    row_count_ -= (row_count_ < offset ? row_count_ : offset);
  }

  private void init() {
    for (int i = 0; i < schema_.attributeCount(); i++) {
      columns_[i].init(schema_.getAttributeAt(i));
    }
  }

};