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

package org.apache.kyuubi.util

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.{Instant, LocalDate, LocalDateTime, ZoneId}
import java.time.chrono.IsoChronology
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.{Date, Locale}

class DateTimeFormatter {
  private lazy val dateFormatter = {
    createDateTimeFormatterBuilder().appendPattern("yyyy-MM-dd")
      .toFormatter(Locale.US)
      .withChronology(IsoChronology.INSTANCE)
  }

  private lazy val simpleDateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US)

  private lazy val timestampFormatter = {
    createDateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss")
      .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
      .toFormatter(Locale.US)
      .withChronology(IsoChronology.INSTANCE)
  }

  private lazy val simpleTimestampFormatter = {
    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
  }

  private def createDateTimeFormatterBuilder(): DateTimeFormatterBuilder = {
    new DateTimeFormatterBuilder().parseCaseInsensitive()
  }

  def formatDate(d: Date): String = {
    simpleDateFormatter.format(d)
  }

  def formatLocalDate(ld: LocalDate): String = {
    dateFormatter.format(ld)
  }

  def formatLocalDateTime(ldt: LocalDateTime): String = {
    timestampFormatter.format(ldt)
  }

  def formatInstant(i: Instant, timeZone: Option[ZoneId] = None): String = {
    timeZone.map(timestampFormatter.withZone(_).format(i))
      .getOrElse(timestampFormatter.format(i))
  }

  def formatTimestamp(t: Timestamp): String = {
    simpleTimestampFormatter.format(t)
  }
}
