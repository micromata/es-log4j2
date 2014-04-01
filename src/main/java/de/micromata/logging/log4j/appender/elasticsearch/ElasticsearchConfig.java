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

/**
 * Configuration bean for connection handling etc.
 *
 * @author Christian Claus (c.claus@micromata.de)
 */
public class ElasticsearchConfig {

  private final String httpEndpoint;
  private final String clusterName;
  private final String indexPrefix;
  private final String typeName;
  private final String server;
  private final int port;
  private final String datePattern;
  private final boolean combineStackTrace;

  public ElasticsearchConfig(String httpEndpoint, String clusterName, String indexPrefix,
                             String typeName, String server, String port,
                             String datePattern, String combineStackTrace) {

    // setting default values
    this.httpEndpoint = defaultValue(httpEndpoint, null);
    this.clusterName = defaultValue(clusterName, "elasticsearch");
    this.indexPrefix = defaultValue(indexPrefix, "logging");
    this.typeName = defaultValue(typeName, "logs");
    this.server = defaultValue(server, "localhost");
    this.datePattern = defaultValue(datePattern, "yyyy-ww");
    this.port = defaultValue(port, 9300);
    this.combineStackTrace = defaultValue(combineStackTrace, false);
  }

  private String defaultValue(String value, String defaultValue) {
    return (value != null && value != "") ? value : defaultValue;
  }

  private Boolean defaultValue(String value, boolean defaultValue) {
    return (value != null && value != "") ? Boolean.parseBoolean(value) : defaultValue;
  }

  private int defaultValue(String value, int defaultValue) {
    return (value != null && value != "") ? Integer.parseInt(value) : defaultValue;
  }

  public boolean isRestful() {
    return httpEndpoint != null;
  }

  public String getHttpEndpoint() {
    return httpEndpoint;
  }

  public String getClusterName() {
    return clusterName;
  }

  public String getIndexPrefix() {
    return indexPrefix;
  }

  public String getTypeName() {
    return typeName;
  }

  public String getServer() {
    return server;
  }

  public int getPort() {
    return port;
  }

  public String getDatePattern() {
    return datePattern;
  }

  public boolean isCombineStackTrace() {
    return combineStackTrace;
  }
}
