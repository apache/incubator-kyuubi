// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: test_case.proto

package org.apache.kyuubi.grpc.proto;

public interface TestAddRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:kyuubi.grpc.TestAddRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>string user_id = 1;</code>
   * @return The userId.
   */
  String getUserId();
  /**
   * <code>string user_id = 1;</code>
   * @return The bytes for userId.
   */
  com.google.protobuf.ByteString
      getUserIdBytes();

  /**
   * <code>string session_id = 2;</code>
   * @return The sessionId.
   */
  String getSessionId();
  /**
   * <code>string session_id = 2;</code>
   * @return The bytes for sessionId.
   */
  com.google.protobuf.ByteString
      getSessionIdBytes();

  /**
   * <code>string operation_id = 3;</code>
   * @return The operationId.
   */
  String getOperationId();
  /**
   * <code>string operation_id = 3;</code>
   * @return The bytes for operationId.
   */
  com.google.protobuf.ByteString
      getOperationIdBytes();

  /**
   * <code>int64 first_num = 4;</code>
   * @return The firstNum.
   */
  long getFirstNum();

  /**
   * <code>int64 second_num = 5;</code>
   * @return The secondNum.
   */
  long getSecondNum();
}
