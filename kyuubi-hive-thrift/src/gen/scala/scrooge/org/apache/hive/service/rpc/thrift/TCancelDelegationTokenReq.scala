/**
 * Generated by Scrooge
 *   version: 19.12.0
 *   rev: dfdb68cf6b9c501dbbe3ae644504bf403ad76bfa
 *   built at: 20191212-171820
 */
package org.apache.hive.service.rpc.thrift

import com.twitter.io.Buf
import com.twitter.scrooge.{
  InvalidFieldsException,
  LazyTProtocol,
  StructBuilder,
  StructBuilderFactory,
  TFieldBlob,
  ThriftStruct,
  ThriftStructCodec3,
  ThriftStructField,
  ThriftStructFieldInfo,
  ThriftStructMetaData,
  ValidatingThriftStruct,
  ValidatingThriftStructCodec3
}
import org.apache.thrift.protocol._
import org.apache.thrift.transport.TMemoryBuffer
import scala.collection.immutable.{Map => immutable$Map}
import scala.collection.mutable.Builder
import scala.reflect.{ClassTag, classTag}


object TCancelDelegationTokenReq extends ValidatingThriftStructCodec3[TCancelDelegationTokenReq] with StructBuilderFactory[TCancelDelegationTokenReq] {
  val NoPassthroughFields: immutable$Map[Short, TFieldBlob] = immutable$Map.empty[Short, TFieldBlob]
  val Struct: TStruct = new TStruct("TCancelDelegationTokenReq")
  val SessionHandleField: TField = new TField("sessionHandle", TType.STRUCT, 1)
  val SessionHandleFieldManifest: Manifest[org.apache.hive.service.rpc.thrift.TSessionHandle] = implicitly[Manifest[org.apache.hive.service.rpc.thrift.TSessionHandle]]
  val DelegationTokenField: TField = new TField("delegationToken", TType.STRING, 2)
  val DelegationTokenFieldManifest: Manifest[String] = implicitly[Manifest[String]]

  /**
   * Field information in declaration order.
   */
  lazy val fieldInfos: scala.List[ThriftStructFieldInfo] = scala.List[ThriftStructFieldInfo](
    new ThriftStructFieldInfo(
      SessionHandleField,
      false,
      true,
      SessionHandleFieldManifest,
      _root_.scala.None,
      _root_.scala.None,
      immutable$Map.empty[String, String],
      immutable$Map.empty[String, String],
      None
    ),
    new ThriftStructFieldInfo(
      DelegationTokenField,
      false,
      true,
      DelegationTokenFieldManifest,
      _root_.scala.None,
      _root_.scala.None,
      immutable$Map.empty[String, String],
      immutable$Map.empty[String, String],
      None
    )
  )

  lazy val structAnnotations: immutable$Map[String, String] =
    immutable$Map.empty[String, String]

  private val fieldTypes: IndexedSeq[ClassTag[_]] = IndexedSeq(
    classTag[org.apache.hive.service.rpc.thrift.TSessionHandle].asInstanceOf[ClassTag[_]],
    classTag[String].asInstanceOf[ClassTag[_]]
  )

  private[this] val structFields: Seq[ThriftStructField[TCancelDelegationTokenReq]] = {
    Seq(
      new ThriftStructField[TCancelDelegationTokenReq](
        SessionHandleField,
        _root_.scala.Some(SessionHandleFieldManifest),
        classOf[TCancelDelegationTokenReq]) {
          def getValue[R](struct: TCancelDelegationTokenReq): R = struct.sessionHandle.asInstanceOf[R]
      },
      new ThriftStructField[TCancelDelegationTokenReq](
        DelegationTokenField,
        _root_.scala.Some(DelegationTokenFieldManifest),
        classOf[TCancelDelegationTokenReq]) {
          def getValue[R](struct: TCancelDelegationTokenReq): R = struct.delegationToken.asInstanceOf[R]
      }
    )
  }

  override lazy val metaData: ThriftStructMetaData[TCancelDelegationTokenReq] =
    new ThriftStructMetaData(this, structFields, fieldInfos, Seq(), structAnnotations)

  /**
   * Checks that all required fields are non-null.
   */
  def validate(_item: TCancelDelegationTokenReq): Unit = {
    if (_item.sessionHandle == null) throw new TProtocolException("Required field sessionHandle cannot be null")
    if (_item.delegationToken == null) throw new TProtocolException("Required field delegationToken cannot be null")
  }

  /**
   * Checks that the struct is a valid as a new instance. If there are any missing required or
   * construction required fields, return a non-empty list.
   */
  def validateNewInstance(item: TCancelDelegationTokenReq): scala.Seq[com.twitter.scrooge.validation.Issue] = {
    val buf = scala.collection.mutable.ListBuffer.empty[com.twitter.scrooge.validation.Issue]

    if (item.sessionHandle == null)
      buf += com.twitter.scrooge.validation.MissingRequiredField(fieldInfos.apply(0))
    buf ++= validateField(item.sessionHandle)
    if (item.delegationToken == null)
      buf += com.twitter.scrooge.validation.MissingRequiredField(fieldInfos.apply(1))
    buf ++= validateField(item.delegationToken)
    buf.toList
  }

  def withoutPassthroughFields(original: TCancelDelegationTokenReq): TCancelDelegationTokenReq =
    new Immutable(
      sessionHandle =
        {
          val field = original.sessionHandle
          org.apache.hive.service.rpc.thrift.TSessionHandle.withoutPassthroughFields(field)
        },
      delegationToken =
        {
          val field = original.delegationToken
          field
        }
    )

  def newBuilder(): StructBuilder[TCancelDelegationTokenReq] = new TCancelDelegationTokenReqStructBuilder(_root_.scala.None, fieldTypes)

  override def encode(_item: TCancelDelegationTokenReq, _oproto: TProtocol): Unit = {
    _item.write(_oproto)
  }


  private[this] def lazyDecode(_iprot: LazyTProtocol): TCancelDelegationTokenReq = {

    var sessionHandle: org.apache.hive.service.rpc.thrift.TSessionHandle = null
    var _got_sessionHandle = false
    var delegationTokenOffset: Int = -1
    var _got_delegationToken = false

    var _passthroughFields: Builder[(Short, TFieldBlob), immutable$Map[Short, TFieldBlob]] = null
    var _done = false
    val _start_offset = _iprot.offset

    _iprot.readStructBegin()
    while (!_done) {
      val _field = _iprot.readFieldBegin()
      if (_field.`type` == TType.STOP) {
        _done = true
      } else {
        _field.id match {
          case 1 =>
            _field.`type` match {
              case TType.STRUCT =>
    
                sessionHandle = readSessionHandleValue(_iprot)
                _got_sessionHandle = true
              case _actualType =>
                val _expectedType = TType.STRUCT
                throw new TProtocolException(
                  "Received wrong type for field 'sessionHandle' (expected=%s, actual=%s).".format(
                    ttypeToString(_expectedType),
                    ttypeToString(_actualType)
                  )
                )
            }
          case 2 =>
            _field.`type` match {
              case TType.STRING =>
                delegationTokenOffset = _iprot.offsetSkipString
    
                _got_delegationToken = true
              case _actualType =>
                val _expectedType = TType.STRING
                throw new TProtocolException(
                  "Received wrong type for field 'delegationToken' (expected=%s, actual=%s).".format(
                    ttypeToString(_expectedType),
                    ttypeToString(_actualType)
                  )
                )
            }
          case _ =>
            if (_passthroughFields == null)
              _passthroughFields = immutable$Map.newBuilder[Short, TFieldBlob]
            _passthroughFields += (_field.id -> TFieldBlob.read(_field, _iprot))
        }
        _iprot.readFieldEnd()
      }
    }
    _iprot.readStructEnd()

    if (!_got_sessionHandle) throw new TProtocolException("Required field 'sessionHandle' was not found in serialized data for struct TCancelDelegationTokenReq")
    if (!_got_delegationToken) throw new TProtocolException("Required field 'delegationToken' was not found in serialized data for struct TCancelDelegationTokenReq")
    new LazyImmutable(
      _iprot,
      _iprot.buffer,
      _start_offset,
      _iprot.offset,
      sessionHandle,
      delegationTokenOffset,
      if (_passthroughFields == null)
        NoPassthroughFields
      else
        _passthroughFields.result()
    )
  }

  override def decode(_iprot: TProtocol): TCancelDelegationTokenReq =
    _iprot match {
      case i: LazyTProtocol => lazyDecode(i)
      case i => eagerDecode(i)
    }

  private[thrift] def eagerDecode(_iprot: TProtocol): TCancelDelegationTokenReq = {
    var sessionHandle: org.apache.hive.service.rpc.thrift.TSessionHandle = null
    var _got_sessionHandle = false
    var delegationToken: String = null
    var _got_delegationToken = false
    var _passthroughFields: Builder[(Short, TFieldBlob), immutable$Map[Short, TFieldBlob]] = null
    var _done = false

    _iprot.readStructBegin()
    while (!_done) {
      val _field = _iprot.readFieldBegin()
      if (_field.`type` == TType.STOP) {
        _done = true
      } else {
        _field.id match {
          case 1 =>
            _field.`type` match {
              case TType.STRUCT =>
                sessionHandle = readSessionHandleValue(_iprot)
                _got_sessionHandle = true
              case _actualType =>
                val _expectedType = TType.STRUCT
                throw new TProtocolException(
                  "Received wrong type for field 'sessionHandle' (expected=%s, actual=%s).".format(
                    ttypeToString(_expectedType),
                    ttypeToString(_actualType)
                  )
                )
            }
          case 2 =>
            _field.`type` match {
              case TType.STRING =>
                delegationToken = readDelegationTokenValue(_iprot)
                _got_delegationToken = true
              case _actualType =>
                val _expectedType = TType.STRING
                throw new TProtocolException(
                  "Received wrong type for field 'delegationToken' (expected=%s, actual=%s).".format(
                    ttypeToString(_expectedType),
                    ttypeToString(_actualType)
                  )
                )
            }
          case _ =>
            if (_passthroughFields == null)
              _passthroughFields = immutable$Map.newBuilder[Short, TFieldBlob]
            _passthroughFields += (_field.id -> TFieldBlob.read(_field, _iprot))
        }
        _iprot.readFieldEnd()
      }
    }
    _iprot.readStructEnd()

    if (!_got_sessionHandle) throw new TProtocolException("Required field 'sessionHandle' was not found in serialized data for struct TCancelDelegationTokenReq")
    if (!_got_delegationToken) throw new TProtocolException("Required field 'delegationToken' was not found in serialized data for struct TCancelDelegationTokenReq")
    new Immutable(
      sessionHandle,
      delegationToken,
      if (_passthroughFields == null)
        NoPassthroughFields
      else
        _passthroughFields.result()
    )
  }

  def apply(
    sessionHandle: org.apache.hive.service.rpc.thrift.TSessionHandle,
    delegationToken: String
  ): TCancelDelegationTokenReq =
    new Immutable(
      sessionHandle,
      delegationToken
    )

  def unapply(_item: TCancelDelegationTokenReq): _root_.scala.Option[_root_.scala.Tuple2[org.apache.hive.service.rpc.thrift.TSessionHandle, String]] = _root_.scala.Some(_item.toTuple)


  @inline private[thrift] def readSessionHandleValue(_iprot: TProtocol): org.apache.hive.service.rpc.thrift.TSessionHandle = {
    org.apache.hive.service.rpc.thrift.TSessionHandle.decode(_iprot)
  }

  @inline private def writeSessionHandleField(sessionHandle_item: org.apache.hive.service.rpc.thrift.TSessionHandle, _oprot: TProtocol): Unit = {
    _oprot.writeFieldBegin(SessionHandleField)
    writeSessionHandleValue(sessionHandle_item, _oprot)
    _oprot.writeFieldEnd()
  }

  @inline private def writeSessionHandleValue(sessionHandle_item: org.apache.hive.service.rpc.thrift.TSessionHandle, _oprot: TProtocol): Unit = {
    sessionHandle_item.write(_oprot)
  }

  @inline private[thrift] def readDelegationTokenValue(_iprot: TProtocol): String = {
    _iprot.readString()
  }

  @inline private def writeDelegationTokenField(delegationToken_item: String, _oprot: TProtocol): Unit = {
    _oprot.writeFieldBegin(DelegationTokenField)
    writeDelegationTokenValue(delegationToken_item, _oprot)
    _oprot.writeFieldEnd()
  }

  @inline private def writeDelegationTokenValue(delegationToken_item: String, _oprot: TProtocol): Unit = {
    _oprot.writeString(delegationToken_item)
  }


  object Immutable extends ThriftStructCodec3[TCancelDelegationTokenReq] {
    override def encode(_item: TCancelDelegationTokenReq, _oproto: TProtocol): Unit = { _item.write(_oproto) }
    override def decode(_iprot: TProtocol): TCancelDelegationTokenReq = TCancelDelegationTokenReq.decode(_iprot)
    override lazy val metaData: ThriftStructMetaData[TCancelDelegationTokenReq] = TCancelDelegationTokenReq.metaData
  }

  /**
   * The default read-only implementation of TCancelDelegationTokenReq.  You typically should not need to
   * directly reference this class; instead, use the TCancelDelegationTokenReq.apply method to construct
   * new instances.
   */
  class Immutable(
      val sessionHandle: org.apache.hive.service.rpc.thrift.TSessionHandle,
      val delegationToken: String,
      override val _passthroughFields: immutable$Map[Short, TFieldBlob])
    extends TCancelDelegationTokenReq {
    def this(
      sessionHandle: org.apache.hive.service.rpc.thrift.TSessionHandle,
      delegationToken: String
    ) = this(
      sessionHandle,
      delegationToken,
      immutable$Map.empty[Short, TFieldBlob]
    )
  }

  /**
   * This is another Immutable, this however keeps strings as lazy values that are lazily decoded from the backing
   * array byte on read.
   */
  private[this] class LazyImmutable(
      _proto: LazyTProtocol,
      _buf: Array[Byte],
      _start_offset: Int,
      _end_offset: Int,
      val sessionHandle: org.apache.hive.service.rpc.thrift.TSessionHandle,
      delegationTokenOffset: Int,
      override val _passthroughFields: immutable$Map[Short, TFieldBlob])
    extends TCancelDelegationTokenReq {

    override def write(_oprot: TProtocol): Unit = {
      _oprot match {
        case i: LazyTProtocol => i.writeRaw(_buf, _start_offset, _end_offset - _start_offset)
        case _ => super.write(_oprot)
      }
    }

    lazy val delegationToken: String =
      if (delegationTokenOffset == -1)
        null
      else {
        _proto.decodeString(_buf, delegationTokenOffset)
      }

    /**
     * Override the super hash code to make it a lazy val rather than def.
     *
     * Calculating the hash code can be expensive, caching it where possible
     * can provide significant performance wins. (Key in a hash map for instance)
     * Usually not safe since the normal constructor will accept a mutable map or
     * set as an arg
     * Here however we control how the class is generated from serialized data.
     * With the class private and the contract that we throw away our mutable references
     * having the hash code lazy here is safe.
     */
    override lazy val hashCode = super.hashCode
  }

  /**
   * This Proxy trait allows you to extend the TCancelDelegationTokenReq trait with additional state or
   * behavior and implement the read-only methods from TCancelDelegationTokenReq using an underlying
   * instance.
   */
  trait Proxy extends TCancelDelegationTokenReq {
    protected def _underlying_TCancelDelegationTokenReq: TCancelDelegationTokenReq
    override def sessionHandle: org.apache.hive.service.rpc.thrift.TSessionHandle = _underlying_TCancelDelegationTokenReq.sessionHandle
    override def delegationToken: String = _underlying_TCancelDelegationTokenReq.delegationToken
    override def _passthroughFields: immutable$Map[Short, TFieldBlob] = _underlying_TCancelDelegationTokenReq._passthroughFields
  }
}

/**
 * Prefer the companion object's [[org.apache.hive.service.rpc.thrift.TCancelDelegationTokenReq.apply]]
 * for construction if you don't need to specify passthrough fields.
 */
trait TCancelDelegationTokenReq
  extends ThriftStruct
  with _root_.scala.Product2[org.apache.hive.service.rpc.thrift.TSessionHandle, String]
  with ValidatingThriftStruct[TCancelDelegationTokenReq]
  with java.io.Serializable
{
  import TCancelDelegationTokenReq._

  def sessionHandle: org.apache.hive.service.rpc.thrift.TSessionHandle
  def delegationToken: String

  def _passthroughFields: immutable$Map[Short, TFieldBlob] = immutable$Map.empty

  def _1: org.apache.hive.service.rpc.thrift.TSessionHandle = sessionHandle
  def _2: String = delegationToken

  def toTuple: _root_.scala.Tuple2[org.apache.hive.service.rpc.thrift.TSessionHandle, String] = {
    (
      sessionHandle,
      delegationToken
    )
  }


  /**
   * Gets a field value encoded as a binary blob using TCompactProtocol.  If the specified field
   * is present in the passthrough map, that value is returned.  Otherwise, if the specified field
   * is known and not optional and set to None, then the field is serialized and returned.
   */
  def getFieldBlob(_fieldId: Short): _root_.scala.Option[TFieldBlob] = {
    lazy val _buff = new TMemoryBuffer(32)
    lazy val _oprot = new TCompactProtocol(_buff)
    _passthroughFields.get(_fieldId) match {
      case blob: _root_.scala.Some[TFieldBlob] => blob
      case _root_.scala.None => {
        val _fieldOpt: _root_.scala.Option[TField] =
          _fieldId match {
            case 1 =>
              if (sessionHandle ne null) {
                writeSessionHandleValue(sessionHandle, _oprot)
                _root_.scala.Some(TCancelDelegationTokenReq.SessionHandleField)
              } else {
                _root_.scala.None
              }
            case 2 =>
              if (delegationToken ne null) {
                writeDelegationTokenValue(delegationToken, _oprot)
                _root_.scala.Some(TCancelDelegationTokenReq.DelegationTokenField)
              } else {
                _root_.scala.None
              }
            case _ => _root_.scala.None
          }
        _fieldOpt match {
          case _root_.scala.Some(_field) =>
            _root_.scala.Some(TFieldBlob(_field, Buf.ByteArray.Owned(_buff.getArray())))
          case _root_.scala.None =>
            _root_.scala.None
        }
      }
    }
  }

  /**
   * Collects TCompactProtocol-encoded field values according to `getFieldBlob` into a map.
   */
  def getFieldBlobs(ids: TraversableOnce[Short]): immutable$Map[Short, TFieldBlob] =
    (ids flatMap { id => getFieldBlob(id) map { id -> _ } }).toMap

  /**
   * Sets a field using a TCompactProtocol-encoded binary blob.  If the field is a known
   * field, the blob is decoded and the field is set to the decoded value.  If the field
   * is unknown and passthrough fields are enabled, then the blob will be stored in
   * _passthroughFields.
   */
  def setField(_blob: TFieldBlob): TCancelDelegationTokenReq = {
    var sessionHandle: org.apache.hive.service.rpc.thrift.TSessionHandle = this.sessionHandle
    var delegationToken: String = this.delegationToken
    var _passthroughFields = this._passthroughFields
    _blob.id match {
      case 1 =>
        sessionHandle = readSessionHandleValue(_blob.read)
      case 2 =>
        delegationToken = readDelegationTokenValue(_blob.read)
      case _ => _passthroughFields += (_blob.id -> _blob)
    }
    new Immutable(
      sessionHandle,
      delegationToken,
      _passthroughFields
    )
  }

  /**
   * If the specified field is optional, it is set to None.  Otherwise, if the field is
   * known, it is reverted to its default value; if the field is unknown, it is removed
   * from the passthroughFields map, if present.
   */
  def unsetField(_fieldId: Short): TCancelDelegationTokenReq = {
    var sessionHandle: org.apache.hive.service.rpc.thrift.TSessionHandle = this.sessionHandle
    var delegationToken: String = this.delegationToken

    _fieldId match {
      case 1 =>
        sessionHandle = null
      case 2 =>
        delegationToken = null
      case _ =>
    }
    new Immutable(
      sessionHandle,
      delegationToken,
      _passthroughFields - _fieldId
    )
  }

  /**
   * If the specified field is optional, it is set to None.  Otherwise, if the field is
   * known, it is reverted to its default value; if the field is unknown, it is removed
   * from the passthroughFields map, if present.
   */
  def unsetSessionHandle: TCancelDelegationTokenReq = unsetField(1)

  def unsetDelegationToken: TCancelDelegationTokenReq = unsetField(2)


  override def write(_oprot: TProtocol): Unit = {
    TCancelDelegationTokenReq.validate(this)
    _oprot.writeStructBegin(Struct)
    if (sessionHandle ne null) writeSessionHandleField(sessionHandle, _oprot)
    if (delegationToken ne null) writeDelegationTokenField(delegationToken, _oprot)
    if (_passthroughFields.nonEmpty) {
      _passthroughFields.values.foreach { _.write(_oprot) }
    }
    _oprot.writeFieldStop()
    _oprot.writeStructEnd()
  }

  def copy(
    sessionHandle: org.apache.hive.service.rpc.thrift.TSessionHandle = this.sessionHandle,
    delegationToken: String = this.delegationToken,
    _passthroughFields: immutable$Map[Short, TFieldBlob] = this._passthroughFields
  ): TCancelDelegationTokenReq =
    new Immutable(
      sessionHandle,
      delegationToken,
      _passthroughFields
    )

  override def canEqual(other: Any): Boolean = other.isInstanceOf[TCancelDelegationTokenReq]

  private def _equals(x: TCancelDelegationTokenReq, y: TCancelDelegationTokenReq): Boolean =
      x.productArity == y.productArity &&
      x.productIterator.sameElements(y.productIterator) &&
      x._passthroughFields == y._passthroughFields

  override def equals(other: Any): Boolean =
    canEqual(other) &&
      _equals(this, other.asInstanceOf[TCancelDelegationTokenReq])

  override def hashCode: Int = {
    _root_.scala.runtime.ScalaRunTime._hashCode(this)
  }

  override def toString: String = _root_.scala.runtime.ScalaRunTime._toString(this)


  override def productArity: Int = 2

  override def productElement(n: Int): Any = n match {
    case 0 => this.sessionHandle
    case 1 => this.delegationToken
    case _ => throw new IndexOutOfBoundsException(n.toString)
  }

  override def productPrefix: String = "TCancelDelegationTokenReq"

  def _codec: ValidatingThriftStructCodec3[TCancelDelegationTokenReq] = TCancelDelegationTokenReq

  def newBuilder(): StructBuilder[TCancelDelegationTokenReq] = new TCancelDelegationTokenReqStructBuilder(_root_.scala.Some(this), fieldTypes)
}

private[thrift] class TCancelDelegationTokenReqStructBuilder(instance: _root_.scala.Option[TCancelDelegationTokenReq], fieldTypes: IndexedSeq[ClassTag[_]])
    extends StructBuilder[TCancelDelegationTokenReq](fieldTypes) {

  def build(): TCancelDelegationTokenReq = instance match {
    case _root_.scala.Some(i) =>
      TCancelDelegationTokenReq(
        (if (fieldArray(0) == null) i.sessionHandle else fieldArray(0)).asInstanceOf[org.apache.hive.service.rpc.thrift.TSessionHandle],
        (if (fieldArray(1) == null) i.delegationToken else fieldArray(1)).asInstanceOf[String]
      )
    case _root_.scala.None =>
      if (fieldArray.contains(null)) throw new InvalidFieldsException(structBuildError("TCancelDelegationTokenReq"))
      else {
        TCancelDelegationTokenReq(
          fieldArray(0).asInstanceOf[org.apache.hive.service.rpc.thrift.TSessionHandle],
          fieldArray(1).asInstanceOf[String]
        )
      }
    }
}

