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

/**
 * For results passed by value. The Result class must provide a default
 * constructor (used when a failure is propagated). (If this is not acceptable,
 * consider using FailureOrConstReference). The Exception class must
 * provide a public destructor.
 *
 * To create an instance of this class, call:
 *     FailureOr<Result> foo(Success(value)) or
 *     FailureOr<Result> foo(Failure(...)).
 * 
 * @author Min Zhou (coderplay@gmail.com)
 * 
 */
public class FailureOr<RESULT> extends FailureOrVoid {
  private RESULT result;

  public FailureOr(final FailurePropagator failure) {
    super(failure);
  }

  public FailureOr(final ReferencePropagator<RESULT> result) {
    super();
    this.result = result.result;
  }

  public RESULT get() {
    ensureSuccess();
    return result;
  }
}
