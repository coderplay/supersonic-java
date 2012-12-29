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
package com.alibaba.supersonic.utils.exception;

import com.alibaba.supersonic.base.exception.SupersonicException;

/**
 * Propagators to support 'exception-like' error propagation. Allow to attach
 * exception information to any object passed by value. Intended themselves to
 * be passed by value.
 *
 * Usage pattern:
 *
 * class Data { ... };
 *
 * FailureOr<Data> GetSomeData() {
 *   if (error detected) {
 *     return Failure(new Exception(...));  // Abrupt completion.
 *   } else {
 *     Data data = ...;
 *     return Success(data);                // Normal completion.
 *   }
 * }
 *
 * FailureOr<Data> result = GetSomeData();
 * if (result.is_failure()) {            // Catch.
 *   HandleException(result.exception());
 * } else {
 *   HandleResult(result.get());
 * }
 *
 * Propagation:
 * FailureOr<ManipulatedData> ManipulateSomeData() {
 *  FailureOr<Data> data = GetSomeData();  // some function that can fail.
 *  if (data.is_failure()) {
 *    return Failure(data.release_exception());
 *  }
 *  ...
 *
 * @author Min Zhou (coderplay@gmail.com)
 * 
 */
public class FailureOrs {

  public static <RESULT> FailureOr<RESULT> failure(SupersonicException exception) {
    return new FailureOr<RESULT>(new FailurePropagator(exception));
  }

  public static <RESULT> FailureOr<RESULT> success(RESULT result) {
    return new FailureOr<RESULT>(new ReferencePropagator<RESULT>(result));
  }

  public static FailureOrVoid voidFailure(SupersonicException exception) {
    return new FailureOrVoid(new FailurePropagator(exception));
  }

  /**
   * Creates an indication of success, with no result. Can be implicitly converted
   * to {@link FailureOrVoid}.
   * @return
   */
  public static FailureOrVoid voidSuccess() {
    return new FailureOrVoid();
  }

}
