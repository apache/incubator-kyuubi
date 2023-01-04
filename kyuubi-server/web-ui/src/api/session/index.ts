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

import request from '@/utils/request'
import { ISessionSearch, IOperationSearch } from './types'

export function getAllSessions(params: ISessionSearch) {
  return request({
    url: 'api/v1/sessions/listSessionInfo',
    method: 'get',
    params
  })
}

export function getSession(sessionId: string) {
  return request({
    url: `api/v1/sessions/${sessionId}`,
    method: 'get'
  })
}

export function deleteSession(sessionId: string) {
  return request({
    url: `api/v1/sessions/${sessionId}`,
    method: 'delete'
  })
}

export function getSqlDetails(sessionId: string) {
  return request({
    url: `api/v1/sessions/${sessionId}/sqlDetails`,
    method: 'post'
  })
}

export function getAllOperations(params: IOperationSearch) {
  return request({
    url: 'api/v1/operations/listOperation',
    method: 'get',
    params
  })
}

export function cancelOperation(
  operationId: string,
  data: {
    action: 'CANCEL' | 'CLOSE'
  }
) {
  return request({
    url: `api/v1/operations/${operationId}`,
    method: 'put',
    data
  })
}

export function getOperationLog(operationId: string) {
  return request({
    url: `api/v1/operations/${operationId}/log`,
    method: 'get'
  })
}
