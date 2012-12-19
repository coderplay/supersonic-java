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
import java.util.Map;

import com.alibaba.supersonic.proto.CommonEnums.DataType;
import com.alibaba.supersonic.proto.CommonEnums.Nullability;
import com.google.common.base.Preconditions;

/**
 * Represents the schema for a tuple, which is an ordered list of named and
 * typed values (e.g. a single row in a table/cursor). The tuple schema lists
 * types and names of tuple elements, called 'attributes'.
 * 
 * The schema is often used in objects passed by value, e.g. comparators used
 * for table sorting; therefore, it needs to support copy construction and
 * assignment.
 * 
 * @author Min Zhou (coderplay@gmail.com)
 */
public class TupleSchema {
	private List<Attribute> attributes;
	private Map<String, Integer> attributeNames;

	/*
	 * Creates and returns a schema with a single attribute.
	 */
	public static TupleSchema singleton(final String name, final DataType type,
			final Nullability nullability) {
		TupleSchema result = new TupleSchema();
		Attribute attribute = new Attribute(name, type, nullability);
		Preconditions.checkState(result.addAttribute(attribute));
		return result;
	}
	  
	/**
	 * Creates a new, empty schema (with no attributes).
	 */
	public TupleSchema() {
	};

	/**
	 * A copy constructor.
	 * 
	 * @param other the source schema
	 */
	public TupleSchema(final TupleSchema other) {
		this.attributes = other.attributes;
		this.attributeNames = other.attributeNames;
	}

	/**
	 * Returns the number of attributes.
	 * @return the number of attributes.
	 */
	public int attributeCount() {
		return attributes.size();
	}
	
	/**
	 * Returns the attribute at the specified position.
	 * @param position the specified position.
	 * @return the attribute at the specified position.
	 */
	public Attribute getAttributeAt(final int position) {
		Preconditions.checkPositionIndex(position, attributes.size());
		return attributes.get(position);
	}
	
	/**
	 * Adds an attribute to the schema if it isn't already defined. The added
	 * attribute occupies the last position (positions of all existing
	 * attributes do not change). Returns true iff name has been successfully
	 * added.
	 */
	public boolean addAttribute(final Attribute attribute) {
		if(attributeNames.containsKey(attribute.getName())) {
			return false;
		}
		
		attributeNames.put(attribute.getName(), attributes.size());
		attributes.add(attribute);
		return true;
	}
	
	
	/**
	 * Looks up an attribute with the specified name. O(log2(attribute count)).
	 * The attribute must exist in the schema.
	 */
	public Attribute lookupAttribute(final String attributeName) {
		int position = lookupAttributePosition(attributeName);
		Preconditions.checkPositionIndex(position, attributes.size());
		return attributes.get(position);
	}
	
	/**
	 * Looks up and returns the position of an attribute with the specified
	 * name, or -1 if not found in the schema. O(log2(attribute count)).
	 */
	public int lookupAttributePosition(final String attributeName) {
		Integer i = attributeNames.get(attributeName);
		return (i == null) ? -1 : i;
	}
	
	/**
	 * Checks whether two schemas are equal, i.e. they have attributes of the
	 * same types and (if check_names is set to true) names on the same
	 * positions.
	 */
	public static boolean equals(final TupleSchema a, final TupleSchema b,
			boolean checkNames) {
		if (a.attributeCount() != b.attributeCount()) {
			return false;
		}
		for (int i = 0; i < a.attributeCount(); i++) {
			final Attribute aAttr = a.getAttributeAt(i);
			final Attribute bAttr = b.getAttributeAt(i);
			if (aAttr.getType() != bAttr.getType()
					|| aAttr.isNullable() != bAttr.isNullable()
					|| (checkNames && (aAttr.getName() != bAttr.getName()))) {
				return false;
			}
		}
		return true;
	}
	
  /**
   * Returns true if two schemas can be merged (they do not contain attributes
   * with the same name).
   */
  public static boolean canMerge(final TupleSchema a, final TupleSchema b) {
    for (int i = 0; i < a.attributeCount(); ++i) {
      if (b.lookupAttributePosition(a.getAttributeAt(i).getName()) >= 0) {
        return false;
      }
    }
    for (int i = 0; i < b.attributeCount(); i++) {
      if (a.lookupAttributePosition(b.getAttributeAt(i).getName()) >= 0) {
        return false;
      }
    }
    return true;
  }
	
  /**
   * TODO(user): Remove this method and replace its uses with TryMerge. Merges
   * two TupleSchema into a new TupleSchema object. It is safe to call this
   * method only if CanMerge for the two schemas returns true.
   * 
   * @param a
   * @param b
   * @return
   */
  public TupleSchema merge(final TupleSchema a, final TupleSchema b) {
//    FailureOr<TupleSchema> result = TryMerge(a, b);
//    CHECK(result.is_success())
//        << "TupleSchema::Merge failed, "
//        << result.exception().PrintStackTrace();
//    return result.get();
    return null;
  }
  
  /**
   * Equality tester for CHECKs and tests.
   * TODO(user): clarify relationship to AreEqual (above).
   * @param other
   * @return
   */
  public boolean equalByType(final TupleSchema other) {
    if (attributeCount() != other.attributeCount())
      return false;
    for (int i = 0; i < attributeCount(); ++i) {
      if (getAttributeAt(i).getType() != other.getAttributeAt(i).getType())
        return false;
    }
    return true;
  }
  
  /**
   * Convenience function that returns a schema specification formatted
   * in the human-readable representation:
   * <name>: <type> [, <name>: <type>]*
   * For example: "a: STRING, b: INT32"
   * Note: for logging only. Prone to ambiguities in corner cases, e.g.
   * attribute names with non-ASCII characters, or ':'.
   */
  public String getHumanReadableSpecification() {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < attributeCount(); ++i) {
      if (i > 0) result.append(", ");
      result.append(getAttributeAt(i).getName());
      result.append(": ");
      result.append(TypeInfo.getTypeInfo(getAttributeAt(i).getType()).name());
      if (!getAttributeAt(i).isNullable()) result.append(" NOT NULL");
    }
    return result.toString();
  }

}
