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


object THandleIdentifier extends ValidatingThriftStructCodec3[THandleIdentifier] with StructBuilderFactory[THandleIdentifier] {
  val NoPassthroughFields: immutable$Map[Short, TFieldBlob] = immutable$Map.empty[Short, TFieldBlob]
  val Struct: TStruct = new TStruct("THandleIdentifier")
  val GuidField: TField = new TField("guid", TType.STRING, 1)
  val GuidFieldManifest: Manifest[_root_.java.nio.ByteBuffer] = implicitly[Manifest[_root_.java.nio.ByteBuffer]]
  val SecretField: TField = new TField("secret", TType.STRING, 2)
  val SecretFieldManifest: Manifest[_root_.java.nio.ByteBuffer] = implicitly[Manifest[_root_.java.nio.ByteBuffer]]

  /**
   * Field information in declaration order.
   */
  lazy val fieldInfos: scala.List[ThriftStructFieldInfo] = scala.List[ThriftStructFieldInfo](
    new ThriftStructFieldInfo(
      GuidField,
      false,
      true,
      GuidFieldManifest,
      _root_.scala.None,
      _root_.scala.None,
      immutable$Map.empty[String, String],
      immutable$Map.empty[String, String],
      None
    ),
    new ThriftStructFieldInfo(
      SecretField,
      false,
      true,
      SecretFieldManifest,
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
    classTag[_root_.java.nio.ByteBuffer].asInstanceOf[ClassTag[_]],
    classTag[_root_.java.nio.ByteBuffer].asInstanceOf[ClassTag[_]]
  )

  private[this] val structFields: Seq[ThriftStructField[THandleIdentifier]] = {
    Seq(
      new ThriftStructField[THandleIdentifier](
        GuidField,
        _root_.scala.Some(GuidFieldManifest),
        classOf[THandleIdentifier]) {
          def getValue[R](struct: THandleIdentifier): R = struct.guid.asInstanceOf[R]
      },
      new ThriftStructField[THandleIdentifier](
        SecretField,
        _root_.scala.Some(SecretFieldManifest),
        classOf[THandleIdentifier]) {
          def getValue[R](struct: THandleIdentifier): R = struct.secret.asInstanceOf[R]
      }
    )
  }

  override lazy val metaData: ThriftStructMetaData[THandleIdentifier] =
    new ThriftStructMetaData(this, structFields, fieldInfos, Seq(), structAnnotations)

  /**
   * Checks that all required fields are non-null.
   */
  def validate(_item: THandleIdentifier): Unit = {
    if (_item.guid == null) throw new TProtocolException("Required field guid cannot be null")
    if (_item.secret == null) throw new TProtocolException("Required field secret cannot be null")
  }

  /**
   * Checks that the struct is a valid as a new instance. If there are any missing required or
   * construction required fields, return a non-empty list.
   */
  def validateNewInstance(item: THandleIdentifier): scala.Seq[com.twitter.scrooge.validation.Issue] = {
    val buf = scala.collection.mutable.ListBuffer.empty[com.twitter.scrooge.validation.Issue]

    if (item.guid == null)
      buf += com.twitter.scrooge.validation.MissingRequiredField(fieldInfos.apply(0))
    buf ++= validateField(item.guid)
    if (item.secret == null)
      buf += com.twitter.scrooge.validation.MissingRequiredField(fieldInfos.apply(1))
    buf ++= validateField(item.secret)
    buf.toList
  }

  def withoutPassthroughFields(original: THandleIdentifier): THandleIdentifier =
    new Immutable(
      guid =
        {
          val field = original.guid
          field
        },
      secret =
        {
          val field = original.secret
          field
        }
    )

  def newBuilder(): StructBuilder[THandleIdentifier] = new THandleIdentifierStructBuilder(_root_.scala.None, fieldTypes)

  override def encode(_item: THandleIdentifier, _oproto: TProtocol): Unit = {
    _item.write(_oproto)
  }


  private[this] def lazyDecode(_iprot: LazyTProtocol): THandleIdentifier = {

    var guid: _root_.java.nio.ByteBuffer = null
    var _got_guid = false
    var secret: _root_.java.nio.ByteBuffer = null
    var _got_secret = false

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
              case TType.STRING =>
    
                guid = readGuidValue(_iprot)
                _got_guid = true
              case _actualType =>
                val _expectedType = TType.STRING
                throw new TProtocolException(
                  "Received wrong type for field 'guid' (expected=%s, actual=%s).".format(
                    ttypeToString(_expectedType),
                    ttypeToString(_actualType)
                  )
                )
            }
          case 2 =>
            _field.`type` match {
              case TType.STRING =>
    
                secret = readSecretValue(_iprot)
                _got_secret = true
              case _actualType =>
                val _expectedType = TType.STRING
                throw new TProtocolException(
                  "Received wrong type for field 'secret' (expected=%s, actual=%s).".format(
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

    if (!_got_guid) throw new TProtocolException("Required field 'guid' was not found in serialized data for struct THandleIdentifier")
    if (!_got_secret) throw new TProtocolException("Required field 'secret' was not found in serialized data for struct THandleIdentifier")
    new LazyImmutable(
      _iprot,
      _iprot.buffer,
      _start_offset,
      _iprot.offset,
      guid,
      secret,
      if (_passthroughFields == null)
        NoPassthroughFields
      else
        _passthroughFields.result()
    )
  }

  override def decode(_iprot: TProtocol): THandleIdentifier =
    _iprot match {
      case i: LazyTProtocol => lazyDecode(i)
      case i => eagerDecode(i)
    }

  private[thrift] def eagerDecode(_iprot: TProtocol): THandleIdentifier = {
    var guid: _root_.java.nio.ByteBuffer = null
    var _got_guid = false
    var secret: _root_.java.nio.ByteBuffer = null
    var _got_secret = false
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
              case TType.STRING =>
                guid = readGuidValue(_iprot)
                _got_guid = true
              case _actualType =>
                val _expectedType = TType.STRING
                throw new TProtocolException(
                  "Received wrong type for field 'guid' (expected=%s, actual=%s).".format(
                    ttypeToString(_expectedType),
                    ttypeToString(_actualType)
                  )
                )
            }
          case 2 =>
            _field.`type` match {
              case TType.STRING =>
                secret = readSecretValue(_iprot)
                _got_secret = true
              case _actualType =>
                val _expectedType = TType.STRING
                throw new TProtocolException(
                  "Received wrong type for field 'secret' (expected=%s, actual=%s).".format(
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

    if (!_got_guid) throw new TProtocolException("Required field 'guid' was not found in serialized data for struct THandleIdentifier")
    if (!_got_secret) throw new TProtocolException("Required field 'secret' was not found in serialized data for struct THandleIdentifier")
    new Immutable(
      guid,
      secret,
      if (_passthroughFields == null)
        NoPassthroughFields
      else
        _passthroughFields.result()
    )
  }

  def apply(
    guid: _root_.java.nio.ByteBuffer,
    secret: _root_.java.nio.ByteBuffer
  ): THandleIdentifier =
    new Immutable(
      guid,
      secret
    )

  def unapply(_item: THandleIdentifier): _root_.scala.Option[_root_.scala.Tuple2[_root_.java.nio.ByteBuffer, _root_.java.nio.ByteBuffer]] = _root_.scala.Some(_item.toTuple)


  @inline private[thrift] def readGuidValue(_iprot: TProtocol): _root_.java.nio.ByteBuffer = {
    _iprot.readBinary()
  }

  @inline private def writeGuidField(guid_item: _root_.java.nio.ByteBuffer, _oprot: TProtocol): Unit = {
    _oprot.writeFieldBegin(GuidField)
    writeGuidValue(guid_item, _oprot)
    _oprot.writeFieldEnd()
  }

  @inline private def writeGuidValue(guid_item: _root_.java.nio.ByteBuffer, _oprot: TProtocol): Unit = {
    _oprot.writeBinary(guid_item)
  }

  @inline private[thrift] def readSecretValue(_iprot: TProtocol): _root_.java.nio.ByteBuffer = {
    _iprot.readBinary()
  }

  @inline private def writeSecretField(secret_item: _root_.java.nio.ByteBuffer, _oprot: TProtocol): Unit = {
    _oprot.writeFieldBegin(SecretField)
    writeSecretValue(secret_item, _oprot)
    _oprot.writeFieldEnd()
  }

  @inline private def writeSecretValue(secret_item: _root_.java.nio.ByteBuffer, _oprot: TProtocol): Unit = {
    _oprot.writeBinary(secret_item)
  }


  object Immutable extends ThriftStructCodec3[THandleIdentifier] {
    override def encode(_item: THandleIdentifier, _oproto: TProtocol): Unit = { _item.write(_oproto) }
    override def decode(_iprot: TProtocol): THandleIdentifier = THandleIdentifier.decode(_iprot)
    override lazy val metaData: ThriftStructMetaData[THandleIdentifier] = THandleIdentifier.metaData
  }

  /**
   * The default read-only implementation of THandleIdentifier.  You typically should not need to
   * directly reference this class; instead, use the THandleIdentifier.apply method to construct
   * new instances.
   */
  class Immutable(
      val guid: _root_.java.nio.ByteBuffer,
      val secret: _root_.java.nio.ByteBuffer,
      override val _passthroughFields: immutable$Map[Short, TFieldBlob])
    extends THandleIdentifier {
    def this(
      guid: _root_.java.nio.ByteBuffer,
      secret: _root_.java.nio.ByteBuffer
    ) = this(
      guid,
      secret,
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
      val guid: _root_.java.nio.ByteBuffer,
      val secret: _root_.java.nio.ByteBuffer,
      override val _passthroughFields: immutable$Map[Short, TFieldBlob])
    extends THandleIdentifier {

    override def write(_oprot: TProtocol): Unit = {
      _oprot match {
        case i: LazyTProtocol => i.writeRaw(_buf, _start_offset, _end_offset - _start_offset)
        case _ => super.write(_oprot)
      }
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
   * This Proxy trait allows you to extend the THandleIdentifier trait with additional state or
   * behavior and implement the read-only methods from THandleIdentifier using an underlying
   * instance.
   */
  trait Proxy extends THandleIdentifier {
    protected def _underlying_THandleIdentifier: THandleIdentifier
    override def guid: _root_.java.nio.ByteBuffer = _underlying_THandleIdentifier.guid
    override def secret: _root_.java.nio.ByteBuffer = _underlying_THandleIdentifier.secret
    override def _passthroughFields: immutable$Map[Short, TFieldBlob] = _underlying_THandleIdentifier._passthroughFields
  }
}

/**
 * Prefer the companion object's [[org.apache.hive.service.rpc.thrift.THandleIdentifier.apply]]
 * for construction if you don't need to specify passthrough fields.
 */
trait THandleIdentifier
  extends ThriftStruct
  with _root_.scala.Product2[_root_.java.nio.ByteBuffer, _root_.java.nio.ByteBuffer]
  with ValidatingThriftStruct[THandleIdentifier]
  with java.io.Serializable
{
  import THandleIdentifier._

  def guid: _root_.java.nio.ByteBuffer
  def secret: _root_.java.nio.ByteBuffer

  def _passthroughFields: immutable$Map[Short, TFieldBlob] = immutable$Map.empty

  def _1: _root_.java.nio.ByteBuffer = guid
  def _2: _root_.java.nio.ByteBuffer = secret

  def toTuple: _root_.scala.Tuple2[_root_.java.nio.ByteBuffer, _root_.java.nio.ByteBuffer] = {
    (
      guid,
      secret
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
              if (guid ne null) {
                writeGuidValue(guid, _oprot)
                _root_.scala.Some(THandleIdentifier.GuidField)
              } else {
                _root_.scala.None
              }
            case 2 =>
              if (secret ne null) {
                writeSecretValue(secret, _oprot)
                _root_.scala.Some(THandleIdentifier.SecretField)
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
  def setField(_blob: TFieldBlob): THandleIdentifier = {
    var guid: _root_.java.nio.ByteBuffer = this.guid
    var secret: _root_.java.nio.ByteBuffer = this.secret
    var _passthroughFields = this._passthroughFields
    _blob.id match {
      case 1 =>
        guid = readGuidValue(_blob.read)
      case 2 =>
        secret = readSecretValue(_blob.read)
      case _ => _passthroughFields += (_blob.id -> _blob)
    }
    new Immutable(
      guid,
      secret,
      _passthroughFields
    )
  }

  /**
   * If the specified field is optional, it is set to None.  Otherwise, if the field is
   * known, it is reverted to its default value; if the field is unknown, it is removed
   * from the passthroughFields map, if present.
   */
  def unsetField(_fieldId: Short): THandleIdentifier = {
    var guid: _root_.java.nio.ByteBuffer = this.guid
    var secret: _root_.java.nio.ByteBuffer = this.secret

    _fieldId match {
      case 1 =>
        guid = null
      case 2 =>
        secret = null
      case _ =>
    }
    new Immutable(
      guid,
      secret,
      _passthroughFields - _fieldId
    )
  }

  /**
   * If the specified field is optional, it is set to None.  Otherwise, if the field is
   * known, it is reverted to its default value; if the field is unknown, it is removed
   * from the passthroughFields map, if present.
   */
  def unsetGuid: THandleIdentifier = unsetField(1)

  def unsetSecret: THandleIdentifier = unsetField(2)


  override def write(_oprot: TProtocol): Unit = {
    THandleIdentifier.validate(this)
    _oprot.writeStructBegin(Struct)
    if (guid ne null) writeGuidField(guid, _oprot)
    if (secret ne null) writeSecretField(secret, _oprot)
    if (_passthroughFields.nonEmpty) {
      _passthroughFields.values.foreach { _.write(_oprot) }
    }
    _oprot.writeFieldStop()
    _oprot.writeStructEnd()
  }

  def copy(
    guid: _root_.java.nio.ByteBuffer = this.guid,
    secret: _root_.java.nio.ByteBuffer = this.secret,
    _passthroughFields: immutable$Map[Short, TFieldBlob] = this._passthroughFields
  ): THandleIdentifier =
    new Immutable(
      guid,
      secret,
      _passthroughFields
    )

  override def canEqual(other: Any): Boolean = other.isInstanceOf[THandleIdentifier]

  private def _equals(x: THandleIdentifier, y: THandleIdentifier): Boolean =
      x.productArity == y.productArity &&
      x.productIterator.sameElements(y.productIterator) &&
      x._passthroughFields == y._passthroughFields

  override def equals(other: Any): Boolean =
    canEqual(other) &&
      _equals(this, other.asInstanceOf[THandleIdentifier])

  override def hashCode: Int = {
    _root_.scala.runtime.ScalaRunTime._hashCode(this)
  }

  override def toString: String = _root_.scala.runtime.ScalaRunTime._toString(this)


  override def productArity: Int = 2

  override def productElement(n: Int): Any = n match {
    case 0 => this.guid
    case 1 => this.secret
    case _ => throw new IndexOutOfBoundsException(n.toString)
  }

  override def productPrefix: String = "THandleIdentifier"

  def _codec: ValidatingThriftStructCodec3[THandleIdentifier] = THandleIdentifier

  def newBuilder(): StructBuilder[THandleIdentifier] = new THandleIdentifierStructBuilder(_root_.scala.Some(this), fieldTypes)
}

private[thrift] class THandleIdentifierStructBuilder(instance: _root_.scala.Option[THandleIdentifier], fieldTypes: IndexedSeq[ClassTag[_]])
    extends StructBuilder[THandleIdentifier](fieldTypes) {

  def build(): THandleIdentifier = instance match {
    case _root_.scala.Some(i) =>
      THandleIdentifier(
        (if (fieldArray(0) == null) i.guid else fieldArray(0)).asInstanceOf[_root_.java.nio.ByteBuffer],
        (if (fieldArray(1) == null) i.secret else fieldArray(1)).asInstanceOf[_root_.java.nio.ByteBuffer]
      )
    case _root_.scala.None =>
      if (fieldArray.contains(null)) throw new InvalidFieldsException(structBuildError("THandleIdentifier"))
      else {
        THandleIdentifier(
          fieldArray(0).asInstanceOf[_root_.java.nio.ByteBuffer],
          fieldArray(1).asInstanceOf[_root_.java.nio.ByteBuffer]
        )
      }
    }
}

