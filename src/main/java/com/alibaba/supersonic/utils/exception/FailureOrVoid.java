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
import com.google.common.base.Preconditions;

/**
 * Implementation note: to support the syntax shown at the top of the file, we
 * rely on implicit conversion from 'propagator' classes to the FailureOr*
 * classes. That conversion is performed via implicit constructors in the
 * FailureOr* classes. For example, given this statement:
 *
 *   FailureOr<Cursor*> result = Success(new CoolCursor);
 *
 * The compiler will first instantiate and call:
 *
 *   ReferencePropagator<CoolCursor*> Success<CoolCursor*>(CoolCursor& result);
 *
 * And then materialize and invoke the following constructor:
 *
 *   FailureOr<CoolCursor*>::FailureOr(const ReferencePropagator<CoolCursor*>&)
 *
 * (Exception to the C++ Style Guide rule on implicit exceptions granted by
 * csilvers; see http://groups/c-style/browse_thread/thread/5c18ccb21d72923e).
 *
 * For void results. Can be considered a quasi-specialization for
 * FailureOr<void>. The Exception class must provide a public destructor.
 *
 * To create an instance of this class, call:
 *     FailureOrVoid foo(Success()) or
 *     FailureOrVoid foo(Failure(...)).
 *
 * @author Min Zhou (coderplay@gmail.com)
 */
public class FailureOrVoid {

  private SupersonicException exception;

  public FailureOrVoid() {
    this.exception = null;
  }

  public FailureOrVoid(final FailurePropagator failure) {
    this.exception = failure.exception;
  }

  public SupersonicException exception() {
    ensureFailure();
    return exception;
  }

  protected void ensureFailure() {
    Preconditions.checkState(isFailure(),
        "Expected failure, but hasn't found one.");
  }

  protected void ensureSuccess() {
    Preconditions.checkState(isSuccess(),
        "Unexpected failure: " + exception.getMessage());
  }

  public boolean isSuccess() {
    return !isFailure();
  }

  public boolean isFailure() {
    return exception != null;
  }

}
