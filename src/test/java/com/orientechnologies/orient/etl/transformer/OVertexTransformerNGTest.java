/*
 *
 *  * Copyright 2010-2014 Orient Technologies LTD (info(at)orientechnologies.com)
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package com.orientechnologies.orient.etl.transformer;

import com.orientechnologies.orient.etl.OETLProcessor;
import com.orientechnologies.orient.etl.extractor.ETLBaseTest;
import com.tinkerpop.blueprints.Parameter;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests ETL Vertex Transformer.
 *
 * @author Gregor Frey
 */
public class OVertexTransformerNGTest extends ETLBaseTest {

  OrientGraph graph;

  @Test
  public void testCreateVertex() {
    OETLProcessor proc = getProcessor(
            "{source: { content: { value: 'name,\nGregor' } }, extractor : { row: {} },"
            + " transformers: [{csv: {}}, {vertex: {class:'Person', skipDuplicates:false}},"
            + "], loader: { orientdb: { dbURL: 'memory:EdgeTransformerTest', dbType:'graph', useLightweightEdges:false } } }")
            .execute();

    assertEquals(graph.countVertices("Person"), 1);
  }

  @Test
  public void testErrorOnDuplicateVertex() {
    OETLProcessor proc = getProcessor(
            "{source: { content: { value: 'name,\nGregor\nGregor\nHans' } }, extractor : { row: {} },"
            + " transformers: [{csv: {}}, {vertex: {class:'Person', skipDuplicates:false}},"
            + "], loader: { orientdb: { dbURL: 'memory:EdgeTransformerTest', dbType:'graph', useLightweightEdges:false } } }")
            .execute();

    assertEquals(graph.countVertices("Person"), 1);
  }

  @Test
  public void testSkipDuplicateVertex() {
    OETLProcessor proc = getProcessor(
            "{source: { content: { value: 'name,\nGregor\nGregor\nHans' } }, extractor : { row: {} },"
            + " transformers: [{csv: {}}, {vertex: {class:'Person', skipDuplicates:true}},"
            + "], loader: { orientdb: { dbURL: 'memory:EdgeTransformerTest', dbType:'graph', useLightweightEdges:false } } }")
            .execute();

    assertEquals(graph.countVertices("Person"), 2);
  }

  @BeforeMethod
  @Override
  public void setUp() {
    graph = new OrientGraph("memory:EdgeTransformerTest");
    graph.setUseLightweightEdges(false);

    graph.createVertexType("Person");
    graph.createKeyIndex("name", Vertex.class,
            new Parameter<String, String>("type", "UNIQUE"),
            new Parameter<String, String>("class", "Person"));
    graph.commit();
  }

  @Override
  @AfterMethod
  public void tearDown() {
    graph.drop();
  }
}
