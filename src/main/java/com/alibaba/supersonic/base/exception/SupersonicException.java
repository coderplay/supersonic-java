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
package com.alibaba.supersonic.base.exception;

import com.alibaba.supersonic.proto.CommonEnums.ReturnCode;
import com.alibaba.supersonic.proto.CommonEnums.SerializedException;

/**
 * Represents an error during data processing.
 * NOTE: this class is NOT intended for subclassing. Exception 'types' are
 * differentiated by their associated error codes.
 * Rationale:
 * Exception type hierarchies are most useful in open-ended APIs, where the
 * users are likely to define new exception types. This is not the case in
 * Supersonic. And, in fact, introducing full-blown exception type hierarchy
 * would cause a number of hurdles. Particularly:
 * (1) To support the few important cases where we copy exceptions, we would
 * need a polymorphic 'Clone' supported by every subclass in the hierarchy. It
 * would be difficult to enforce at compile time, and erroneous omissions
 * would lead to subtle run-time bugs.
 * (2) Determining exception types would require some type of dynamic type
 * checking. RTTI is banned by the C++ style guide.
 * All in all, going with the the simpler solution for now; will revisit should
 * new data show its deficiencies.
 * 
 * @author Min Zhou (coderplay@gmail.com)
 */
public class SupersonicException extends Exception {

  private static final long serialVersionUID = -1287612404320544698L;

  private ReturnCode code;
  SerializedException serial_;

  public SupersonicException(final ReturnCode code, final String message) {
    super(message);
    this.code = code;
  }

  public ReturnCode getReturnCode() {
    return code;
  }

}
