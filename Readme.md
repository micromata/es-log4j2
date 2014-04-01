elasticsearch for log4j2
========================

Allows you to log via log4j2 into your elasticsearch cluster. You can use the elasticsearch REST-interface
or native binary transport to push your logs into your cluster.

# Quick Start

To use logger, you have to import the library into your project (e.g. via maven):

~~~~~ xml
<dependency>
  <groupId>de.micromata.logging</groupId>
  <artifactId>es-log4j2</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
~~~~~

Next step is to define an elasticsearch NoSQL appender in your log4j2 configuration:

~~~~~ xml
<!-- example configuration to log via REST -->
<NoSql name="databaseAppender">
  <Elasticsearch httpEndpoint="http://localhost:9200" combineStackTrace="true" />
</NoSql>

<!-- example configuration to log via binary transport protocol -->
<NoSql name="databaseAppender">
  <Elasticsearch clusterName="myESCluster" typeName="myIncredibleApplication" />
</NoSql>
~~~~~

List of possible configuration flags:

* __httpEndpoint__: Complete URL (protocol, hostname and port) of your HTTP-Endpoint if you want to log
your data via REST. If you want to log your data via the binary transport protocol just leave this attribute blank.
* __clusterName__ (default: elasticsearch): The name of your cluster. This flag won't have any effect if you're
using the __httpEndpoint__.
* __server__ (default: localhost): The fqdn of your server. This flag won't have any effect if you're using
the __httpEndpoint__.
* __port__ (default: 9300): The port of your cluster. This flag won't have any effect if you're using
the __httpEndpoint__.
* __indexPrefix__ (default: logging): The prefix of the elasticsearch index. Each index-name is built as combination
of a prefix and a date-pattern. E.g. logging-2014-14. With this convention you'll be able to backup or
delete indexes easily.
* __datePattern__ (default: yyyy-ww): The date pattern (postfix) of the elasticsearch index.
(see also __indexPrefix__)
* __typeName__ (default: logs): The name of the type in your search index. You can use a application name for example.
* __combineStackTrace__ (default: false): Indicator if stacktraces should be displayed as combined value. This
flag is useful if you're using tools like kibana to view your logfiles.