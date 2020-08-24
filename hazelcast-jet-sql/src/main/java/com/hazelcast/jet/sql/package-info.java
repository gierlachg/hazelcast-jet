/*
 * Copyright (c) 2008-2020, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * The Hazelcast Jet SQL backend.
 * <p>
 * Hazelcast can execute SQL statements using either the default SQL
 * backend contained in the Hazelcast IMDG code, or using the Jet SQL
 * backend in this package. The algorithm is simple: we first try the
 * default backend, if it can't execute a particular statement, we try the
 * Jet backend.
 * <p>
 * Hazelcast Jet is able to execute distributed SQL statements over any Jet
 * connector that supports the SQL integration. Currently they are:
 * <ul>
 *     <li>local IMap
 *     <li>Apache Kafka topic
 *     <li>Files
 *     <li>Hadoop
 * </ul>
 * Each connector specifies its own serialization formats and a way of
 * mapping the stored objects to records with column names and SQL types.
 * See the individual connectors for details.
 *
 * <h2>DDL statements</h2>
 *
 * The SQL language works with <em>tables</em> that have a fixed list of
 * columns with data types. To use a remote object as a table, an
 * <em>external mapping</em> must be created first (except for local
 * IMaps, which can be queried without creating the mapping first).
 * <p>
 * The mapping specifies the table name, column list with types and
 * connection and other options.
 *
 * <h3>CREATE EXTERNAL MAPPING statement</h3>
 *
 * <pre>{@code
 * CREATE [OR REPLACE] EXTERNAL MAPPING [IF NOT EXISTS] <mappingName>
 * [
 *     (
 *         <columnName> <columnType> [EXTERNAL NAME <externalName>]
 *         [, ...]
 *     )
 * ]
 * TYPE <connectorType>
 * [
 *     OPTIONS (
 *         <optionName> <optionValue>
 *         [, ...]
 *     )
 * ]
 * }</pre>
 *
 * <ul>
 *
 *     <li>{@code OR REPLACE}: overwrite the mapping if it already exists
 *
 *     <li>{@code IF NOT EXISTS}: do nothing if the external mapping already
 *     exists
 *
 *     <li>{@code mappingName}: an SQL identifier that identifies the mapping
 *     in SQL queries. Currently we don't support schemas.
 *
 *     <li>{@code columnName}: the name of the column
 *
 *     <li>{@code columnType}: the type of the column, see below for supported
 *     types
 *
 *     <li>{@code EXTERNAL NAME <externalName>}: the optional external name. If
 *     omitted a connector-specific rules are used to derive it from the column
 *     name. For example for key-value connectors such as IMap or Kafka, unless
 *     the column name is {@code __key} or {@code this}, it is assumed to be a
 *     field of the value. See the connector specification for details.
 *
 *     <li>{@code TYPE <connectorType>: the identifier of the connector type
 *
 *     <li>{@code OPTIONS}: connector-specific options. For a list of possible
 *     options check out the connector javadoc.
 *
 * </ul>
 *
 * <h3>Auto-resolving the columns</h3>
 *
 * Often the column list can be omitted - in this case Jet will connect to
 * the remote system and try to resolve the columns in a connector-specific
 * way. For example, Jet can read a random message from a Kafka topic and
 * determine the column list from it. If Jet fails to resolve the columns
 * (most commonly if the remote object is empty), the DDL statement will
 * fail.
 *
 * <h3>DROP EXTERNAL MAPPING statement</h3>
 *
 * <pre>{@code
 * DROP EXTERNAL MAPPING [IF EXISTS] <mappingName>
 * }</pre>
 *
 * <ul>
 *
 *     <li>{@code IF EXISTS} if the external mapping doesn't exist, do nothing;
 *     fail otherwise.
 *
 *     <li>{@code <mappingName>} the name of teh mapping
 *
 * </ul>
 *
 * <h3>SHOW EXTERNAL MAPPINGS statement</h3>
 *
 * {@code SHOW EXTERNAL MAPPINGS statement}
 *
 * <pre>{@code
 * SHOW EXTERNAL MAPPINGS
 * }</pre>
 *
 * Lists the mappings created using DDL. Doesn't list the implicit mappings
 * for local IMaps.
 * <p>
 * The result set has 2 columns:
 * <ul>
 *     <li>{@code name} the mapping name
 *
 *     <li>{@code ddl} the text of the DDL command that specifies the mapping.
 *     It will contain also column names, types, external names and options as
 *     they were resolved when the DDL statement was executed.
 * </ul>
 *
 * <h3>Notes</h3>
 *
 * Changes to mappings do not affect any jobs that are already running.
 * Only new jobs are affected.
 *
 * <h2>Custom connectors</h2>
 *
 * Adding SQL mappings is currently not a public API, we plan to define an
 * API in the future.
 */
package com.hazelcast.jet.sql;
