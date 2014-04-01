/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.micromata.logging.log4j.appender.elasticsearch;

import org.apache.logging.log4j.core.appender.db.nosql.NoSQLObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * The Elasticsearch implemlementation of {@link org.apache.logging.log4j.core.appender.db.nosql.NoSQLObject}
 *
 * @author Christian Claus (c.claus@micromata.de)
 */
public class ElasticsearchObject implements NoSQLObject<Map<String, Object>> {

  private final Map<String, Object> map;
  private boolean combineStackTrace;

  public ElasticsearchObject(boolean combineStackTrace) {
    this.combineStackTrace = combineStackTrace;
    this.map = new HashMap<String, Object>();
  }

  @Override
  public void set(final String field, final Object value) {
    this.map.put(field, value);
  }

  @Override
  public void set(final String field, final NoSQLObject<Map<String, Object>> value) {
    this.map.put(field, value.unwrap());
  }

  @Override
  public void set(final String field, final Object[] values) {
    this.map.put(field, Arrays.asList(values));
  }

  @Override
  public void set(final String field, final NoSQLObject<Map<String, Object>>[] values) {
    StringBuilder result = new StringBuilder();

    // special handling for stacktraces.
    if (combineStackTrace && field != null && field.equals("stackTrace")) {
      boolean firstElement = true;

      for (final NoSQLObject<Map<String, Object>> value : values) {
        Object className = value.unwrap().get("className");
        Object methodName = value.unwrap().get("methodName");
        Object fileName = value.unwrap().get("fileName");
        Object lineNumber = value.unwrap().get("lineNumber");

        if (firstElement) {
          firstElement = false;
        } else {
          result.append("  at ");
        }

        result.append(className)
            .append(".")
            .append(methodName)
            .append("(")
            .append(fileName)
            .append(":")
            .append(lineNumber)
            .append(")\n");
      }
    } else {
      for (final NoSQLObject<Map<String, Object>> value : values) {
        result.append(value.unwrap()).append("\n");
      }
    }

    this.map.put(field, result.toString());
  }

  @Override
  public Map<String, Object> unwrap() {
    return this.map;
  }
}
