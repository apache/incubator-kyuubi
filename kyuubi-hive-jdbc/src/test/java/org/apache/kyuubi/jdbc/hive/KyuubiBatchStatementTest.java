/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.kyuubi.jdbc.hive;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class KyuubiBatchStatementTest {
  @Test
  public void testSplitSemiColon() {
    List<String> expected = Arrays.asList("select 1", "select 2", "select 3");
    String queries = "select 1; select 2; select 3";
    assert KyuubiBatchStatement.splitSemiColon(queries).retainAll(expected);
    queries = "select 1; select 2; select 3;";
    assert KyuubiBatchStatement.splitSemiColon(queries).retainAll(expected);
    queries = "select 1; select 2;;; select 3;";
    assert KyuubiBatchStatement.splitSemiColon(queries).retainAll(expected);
    queries = "select 1 /** ;*/\n--;\n; select 2;;; select 3;";
    assert KyuubiBatchStatement.splitSemiColon(queries).retainAll(expected);
    List<String> expected2 = Arrays.asList("select ';', 1", "select \";\", 2", "select 3");
    String queries2 = "select ';', 1 /** ;*/\n--;\n; select \";\", 2;;; select 3;";
    assert KyuubiBatchStatement.splitSemiColon(queries2).retainAll(expected2);
  }
}
