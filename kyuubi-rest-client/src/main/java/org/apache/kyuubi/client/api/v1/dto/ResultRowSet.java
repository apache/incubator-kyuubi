/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.kyuubi.client.api.v1.dto;

import java.util.List;

public class ResultRowSet {
  private List<Row> rows;
  private int rowCount;

  public ResultRowSet() {}

  public ResultRowSet(List<Row> rows, int rowCount) {
    this.rows = rows;
    this.rowCount = rowCount;
  }

  public List<Row> getRows() {
    return rows;
  }

  public void setRows(List<Row> rows) {
    this.rows = rows;
  }

  public int getRowCount() {
    return rowCount;
  }

  public void setRowCount(int rowCount) {
    this.rowCount = rowCount;
  }
}
