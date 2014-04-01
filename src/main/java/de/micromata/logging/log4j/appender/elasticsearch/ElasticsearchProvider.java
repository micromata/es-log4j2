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

import org.apache.logging.log4j.core.appender.db.nosql.NoSQLProvider;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

/**
 * Elasticsearch implementation of {@link org.apache.logging.log4j.core.appender.db.nosql.NoSQLProvider}
 *
 * @author Christian Claus (c.claus@micromata.de)
 */
@Plugin(name = "Elasticsearch", category = "Core", printObject = true)
public class ElasticsearchProvider implements NoSQLProvider<ElasticsearchConnection> {

  private ElasticsearchConfig config;

  public ElasticsearchProvider(ElasticsearchConfig config) {
    this.config = config;
  }

  @Override
  public ElasticsearchConnection getConnection() {
    return new ElasticsearchConnection(config);
  }

  @PluginFactory
  public static ElasticsearchProvider createNoSQLProvider(
      @PluginAttribute("httpEndpoint") String httpEndpoint,
      @PluginAttribute("clusterName") String clusterName,
      @PluginAttribute("indexPrefix") String indexPrefix,
      @PluginAttribute("typeName") String typeName,
      @PluginAttribute("server") String server,
      @PluginAttribute("port") String port,
      @PluginAttribute("datePattern") String datePattern,
      @PluginAttribute("combineStackTrace") String combineStackTrace) {

    ElasticsearchConfig config = new ElasticsearchConfig(httpEndpoint, clusterName, indexPrefix, typeName, server, port, datePattern, combineStackTrace);

    return new ElasticsearchProvider(config);
  }
}
