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

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.appender.db.nosql.NoSQLConnection;
import org.apache.logging.log4j.core.appender.db.nosql.NoSQLObject;
import org.apache.logging.log4j.status.StatusLogger;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.joda.time.DateTime;
import org.elasticsearch.common.joda.time.format.DateTimeFormat;
import org.elasticsearch.common.joda.time.format.DateTimeFormatter;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Elasticsearch implementation of {@link org.apache.logging.log4j.core.appender.db.nosql.NoSQLConnection}.
 *
 * @author Christian Claus (c.claus@micromata.de)
 */
public class ElasticsearchConnection implements NoSQLConnection<Map<String, Object>, ElasticsearchObject> {

  private static final Logger logger = StatusLogger.getLogger();

  private final TransportClient client;
  private final ElasticsearchConfig config;
  private boolean closed = false;

  public ElasticsearchConnection(ElasticsearchConfig config) {
    if (config.isRestful()) {
      client = null;

    } else {
      Settings settings = ImmutableSettings.settingsBuilder()
          .put("cluster.name", config.getClusterName())
          .build();

      client = new TransportClient(settings);
      client.addTransportAddress(new InetSocketTransportAddress(config.getServer(), config.getPort()));
    }

    this.config = config;
  }

  @Override
  public ElasticsearchObject createObject() {
    return new ElasticsearchObject(config.isCombineStackTrace());
  }

  @Override
  public ElasticsearchObject[] createList(int length) {
    return new ElasticsearchObject[length];
  }

  @Override
  public void insertObject(NoSQLObject<Map<String, Object>> object) {
    final String indexName = indexName();

    if (config.isRestful()) {
      String url = String.format("%s/%s/%s", config.getHttpEndpoint(), indexName, config.getTypeName());

      HttpPost httpPost = new HttpPost(url);
      String json = new Gson().toJson(object.unwrap());
      httpPost.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
      HttpClient httpClient = HttpClients.createDefault();

      try {
        HttpResponse execute = httpClient.execute(httpPost);
        if (execute.getStatusLine().getStatusCode() != 201) {
          logger.error("Failed to write log event to Elasticsearch ({}): {}", url, execute);
        }

      } catch (IOException e) {
        logger.error("Failed to write log event to Elasticsearch ({}) due to error: ", e, url);
      }

    } else {
      try {
        IndexResponse indexResponse = client.prepareIndex(indexName, config.getTypeName())
            .setSource(object.unwrap())
            .execute().get();

        if (indexResponse.isCreated() == false) {
          throw new AppenderLoggingException("Failed to write log event to Elasticsearch: " +
              indexResponse.getHeaders());
        }
      } catch (InterruptedException e) {
        logger.error("Failed to write log event to Elasticsearch due to error: ", e);
      } catch (ExecutionException e) {
        logger.error("Failed to write log event to Elasticsearch due to error: ", e);
      }
    }
  }

  @Override
  public void close() {
    if (client != null) {
      client.close();
    }
    closed = true;
  }

  @Override
  public boolean isClosed() {
    return closed;
  }

  private String indexName() {
    DateTimeFormatter formatter = DateTimeFormat.forPattern(config.getDatePattern());
    return config.getIndexPrefix() + "-" + formatter.print(new DateTime());
  }

}
