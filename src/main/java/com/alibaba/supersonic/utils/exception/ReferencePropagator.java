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
 * Create an indication of success, with the specified result. Can be
 * implicitly converted to FailureOr<>, FailureOrReference<>,
 * and FailureOrOwned<>, as long as there is a valid type conversion from the
 * parameter type to the wrapped result type of the FailureOr* class.
 * Normally, you don't need to specify the Result type; the type inference does
 * its magic. A notable exception is a Success(NULL), which doesn't work because
 * NULL is just zero, and you can't cast int to a pointer. In this case, provide
 * the pointer type as the Result type.
 * 
 * @author Min Zhou (coderplay@gmail.com)
 * 
 */
public class ReferencePropagator<RESULT> {
  public RESULT result;

  public ReferencePropagator(RESULT result) {
    this.result = result;
  }
}
