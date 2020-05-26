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


object TI32Column extends ValidatingThriftStructCodec3[TI32Column] with StructBuilderFactory[TI32Column] {
  val NoPassthroughFields: immutable$Map[Short, TFieldBlob] = immutable$Map.empty[Short, TFieldBlob]
  val Struct: TStruct = new TStruct("TI32Column")
  val ValuesField: TField = new TField("values", TType.LIST, 1)
  val ValuesFieldManifest: Manifest[_root_.scala.collection.Seq[Int]] = implicitly[Manifest[_root_.scala.collection.Seq[Int]]]
  val NullsField: TField = new TField("nulls", TType.STRING, 2)
  val NullsFieldManifest: Manifest[_root_.java.nio.ByteBuffer] = implicitly[Manifest[_root_.java.nio.ByteBuffer]]

  /**
   * Field information in declaration order.
   */
  lazy val fieldInfos: scala.List[ThriftStructFieldInfo] = scala.List[ThriftStructFieldInfo](
    new ThriftStructFieldInfo(
      ValuesField,
      false,
      true,
      ValuesFieldManifest,
      _root_.scala.None,
      _root_.scala.Some(implicitly[Manifest[Int]]),
      immutable$Map.empty[String, String],
      immutable$Map.empty[String, String],
      None
    ),
    new ThriftStructFieldInfo(
      NullsField,
      false,
      true,
      NullsFieldManifest,
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
    classTag[_root_.scala.collection.Seq[Int]].asInstanceOf[ClassTag[_]],
    classTag[_root_.java.nio.ByteBuffer].asInstanceOf[ClassTag[_]]
  )

  private[this] val structFields: Seq[ThriftStructField[TI32Column]] = {
    Seq(
      new ThriftStructField[TI32Column](
        ValuesField,
        _root_.scala.Some(ValuesFieldManifest),
        classOf[TI32Column]) {
          def getValue[R](struct: TI32Column): R = struct.values.asInstanceOf[R]
      },
      new ThriftStructField[TI32Column](
        NullsField,
        _root_.scala.Some(NullsFieldManifest),
        classOf[TI32Column]) {
          def getValue[R](struct: TI32Column): R = struct.nulls.asInstanceOf[R]
      }
    )
  }

  override lazy val metaData: ThriftStructMetaData[TI32Column] =
    new ThriftStructMetaData(this, structFields, fieldInfos, Seq(), structAnnotations)

  /**
   * Checks that all required fields are non-null.
   */
  def validate(_item: TI32Column): Unit = {
    if (_item.values == null) throw new TProtocolException("Required field values cannot be null")
    if (_item.nulls == null) throw new TProtocolException("Required field nulls cannot be null")
  }

  /**
   * Checks that the struct is a valid as a new instance. If there are any missing required or
   * construction required fields, return a non-empty list.
   */
  def validateNewInstance(item: TI32Column): scala.Seq[com.twitter.scrooge.validation.Issue] = {
    val buf = scala.collection.mutable.ListBuffer.empty[com.twitter.scrooge.validation.Issue]

    if (item.values == null)
      buf += com.twitter.scrooge.validation.MissingRequiredField(fieldInfos.apply(0))
    buf ++= validateField(item.values)
    if (item.nulls == null)
      buf += com.twitter.scrooge.validation.MissingRequiredField(fieldInfos.apply(1))
    buf ++= validateField(item.nulls)
    buf.toList
  }

  def withoutPassthroughFields(original: TI32Column): TI32Column =
    new Immutable(
      values =
        {
          val field = original.values
          field.map { field =>
            field
          }
        },
      nulls =
        {
          val field = original.nulls
          field
        }
    )

  def newBuilder(): StructBuilder[TI32Column] = new TI32ColumnStructBuilder(_root_.scala.None, fieldTypes)

  override def encode(_item: TI32Column, _oproto: TProtocol): Unit = {
    _item.write(_oproto)
  }


  private[this] def lazyDecode(_iprot: LazyTProtocol): TI32Column = {

    var values: _root_.scala.collection.Seq[Int] = _root_.scala.collection.immutable.Nil
    var _got_values = false
    var nulls: _root_.java.nio.ByteBuffer = null
    var _got_nulls = false

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
              case TType.LIST =>
    
                values = readValuesValue(_iprot)
                _got_values = true
              case _actualType =>
                val _expectedType = TType.LIST
                throw new TProtocolException(
                  "Received wrong type for field 'values' (expected=%s, actual=%s).".format(
                    ttypeToString(_expectedType),
                    ttypeToString(_actualType)
                  )
                )
            }
          case 2 =>
            _field.`type` match {
              case TType.STRING =>
    
                nulls = readNullsValue(_iprot)
                _got_nulls = true
              case _actualType =>
                val _expectedType = TType.STRING
                throw new TProtocolException(
                  "Received wrong type for field 'nulls' (expected=%s, actual=%s).".format(
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

    if (!_got_values) throw new TProtocolException("Required field 'values' was not found in serialized data for struct TI32Column")
    if (!_got_nulls) throw new TProtocolException("Required field 'nulls' was not found in serialized data for struct TI32Column")
    new LazyImmutable(
      _iprot,
      _iprot.buffer,
      _start_offset,
      _iprot.offset,
      values,
      nulls,
      if (_passthroughFields == null)
        NoPassthroughFields
      else
        _passthroughFields.result()
    )
  }

  override def decode(_iprot: TProtocol): TI32Column =
    _iprot match {
      case i: LazyTProtocol => lazyDecode(i)
      case i => eagerDecode(i)
    }

  private[thrift] def eagerDecode(_iprot: TProtocol): TI32Column = {
    var values: _root_.scala.collection.Seq[Int] = _root_.scala.collection.immutable.Nil
    var _got_values = false
    var nulls: _root_.java.nio.ByteBuffer = null
    var _got_nulls = false
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
              case TType.LIST =>
                values = readValuesValue(_iprot)
                _got_values = true
              case _actualType =>
                val _expectedType = TType.LIST
                throw new TProtocolException(
                  "Received wrong type for field 'values' (expected=%s, actual=%s).".format(
                    ttypeToString(_expectedType),
                    ttypeToString(_actualType)
                  )
                )
            }
          case 2 =>
            _field.`type` match {
              case TType.STRING =>
                nulls = readNullsValue(_iprot)
                _got_nulls = true
              case _actualType =>
                val _expectedType = TType.STRING
                throw new TProtocolException(
                  "Received wrong type for field 'nulls' (expected=%s, actual=%s).".format(
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

    if (!_got_values) throw new TProtocolException("Required field 'values' was not found in serialized data for struct TI32Column")
    if (!_got_nulls) throw new TProtocolException("Required field 'nulls' was not found in serialized data for struct TI32Column")
    new Immutable(
      values,
      nulls,
      if (_passthroughFields == null)
        NoPassthroughFields
      else
        _passthroughFields.result()
    )
  }

  def apply(
    values: _root_.scala.collection.Seq[Int] = _root_.scala.collection.immutable.Nil,
    nulls: _root_.java.nio.ByteBuffer
  ): TI32Column =
    new Immutable(
      values,
      nulls
    )

  def unapply(_item: TI32Column): _root_.scala.Option[_root_.scala.Tuple2[_root_.scala.collection.Seq[Int], _root_.java.nio.ByteBuffer]] = _root_.scala.Some(_item.toTuple)


  @inline private[thrift] def readValuesValue(_iprot: TProtocol): _root_.scala.collection.Seq[Int] = {
    val _list = _iprot.readListBegin()
    if (_list.size == 0) {
      _iprot.readListEnd()
      Nil
    } else {
      val _rv = new _root_.scala.collection.mutable.ArrayBuffer[Int](_list.size)
      var _i = 0
      while (_i < _list.size) {
        _rv += {
          _iprot.readI32()
        }
        _i += 1
      }
      _iprot.readListEnd()
      _rv
    }
  }

  @inline private def writeValuesField(values_item: _root_.scala.collection.Seq[Int], _oprot: TProtocol): Unit = {
    _oprot.writeFieldBegin(ValuesField)
    writeValuesValue(values_item, _oprot)
    _oprot.writeFieldEnd()
  }

  @inline private def writeValuesValue(values_item: _root_.scala.collection.Seq[Int], _oprot: TProtocol): Unit = {
    _oprot.writeListBegin(new TList(TType.I32, values_item.size))
    values_item match {
      case _: IndexedSeq[_] =>
        var _i = 0
        val _size = values_item.size
        while (_i < _size) {
          val values_item_element = values_item(_i)
          _oprot.writeI32(values_item_element)
          _i += 1
        }
      case _ =>
        values_item.foreach { values_item_element =>
          _oprot.writeI32(values_item_element)
        }
    }
    _oprot.writeListEnd()
  }

  @inline private[thrift] def readNullsValue(_iprot: TProtocol): _root_.java.nio.ByteBuffer = {
    _iprot.readBinary()
  }

  @inline private def writeNullsField(nulls_item: _root_.java.nio.ByteBuffer, _oprot: TProtocol): Unit = {
    _oprot.writeFieldBegin(NullsField)
    writeNullsValue(nulls_item, _oprot)
    _oprot.writeFieldEnd()
  }

  @inline private def writeNullsValue(nulls_item: _root_.java.nio.ByteBuffer, _oprot: TProtocol): Unit = {
    _oprot.writeBinary(nulls_item)
  }


  object Immutable extends ThriftStructCodec3[TI32Column] {
    override def encode(_item: TI32Column, _oproto: TProtocol): Unit = { _item.write(_oproto) }
    override def decode(_iprot: TProtocol): TI32Column = TI32Column.decode(_iprot)
    override lazy val metaData: ThriftStructMetaData[TI32Column] = TI32Column.metaData
  }

  /**
   * The default read-only implementation of TI32Column.  You typically should not need to
   * directly reference this class; instead, use the TI32Column.apply method to construct
   * new instances.
   */
  class Immutable(
      val values: _root_.scala.collection.Seq[Int],
      val nulls: _root_.java.nio.ByteBuffer,
      override val _passthroughFields: immutable$Map[Short, TFieldBlob])
    extends TI32Column {
    def this(
      values: _root_.scala.collection.Seq[Int] = _root_.scala.collection.immutable.Nil,
      nulls: _root_.java.nio.ByteBuffer
    ) = this(
      values,
      nulls,
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
      val values: _root_.scala.collection.Seq[Int],
      val nulls: _root_.java.nio.ByteBuffer,
      override val _passthroughFields: immutable$Map[Short, TFieldBlob])
    extends TI32Column {

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
   * This Proxy trait allows you to extend the TI32Column trait with additional state or
   * behavior and implement the read-only methods from TI32Column using an underlying
   * instance.
   */
  trait Proxy extends TI32Column {
    protected def _underlying_TI32Column: TI32Column
    override def values: _root_.scala.collection.Seq[Int] = _underlying_TI32Column.values
    override def nulls: _root_.java.nio.ByteBuffer = _underlying_TI32Column.nulls
    override def _passthroughFields: immutable$Map[Short, TFieldBlob] = _underlying_TI32Column._passthroughFields
  }
}

/**
 * Prefer the companion object's [[org.apache.hive.service.rpc.thrift.TI32Column.apply]]
 * for construction if you don't need to specify passthrough fields.
 */
trait TI32Column
  extends ThriftStruct
  with _root_.scala.Product2[_root_.scala.collection.Seq[Int], _root_.java.nio.ByteBuffer]
  with ValidatingThriftStruct[TI32Column]
  with java.io.Serializable
{
  import TI32Column._

  def values: _root_.scala.collection.Seq[Int]
  def nulls: _root_.java.nio.ByteBuffer

  def _passthroughFields: immutable$Map[Short, TFieldBlob] = immutable$Map.empty

  def _1: _root_.scala.collection.Seq[Int] = values
  def _2: _root_.java.nio.ByteBuffer = nulls

  def toTuple: _root_.scala.Tuple2[_root_.scala.collection.Seq[Int], _root_.java.nio.ByteBuffer] = {
    (
      values,
      nulls
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
              if (values ne null) {
                writeValuesValue(values, _oprot)
                _root_.scala.Some(TI32Column.ValuesField)
              } else {
                _root_.scala.None
              }
            case 2 =>
              if (nulls ne null) {
                writeNullsValue(nulls, _oprot)
                _root_.scala.Some(TI32Column.NullsField)
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
  def setField(_blob: TFieldBlob): TI32Column = {
    var values: _root_.scala.collection.Seq[Int] = this.values
    var nulls: _root_.java.nio.ByteBuffer = this.nulls
    var _passthroughFields = this._passthroughFields
    _blob.id match {
      case 1 =>
        values = readValuesValue(_blob.read)
      case 2 =>
        nulls = readNullsValue(_blob.read)
      case _ => _passthroughFields += (_blob.id -> _blob)
    }
    new Immutable(
      values,
      nulls,
      _passthroughFields
    )
  }

  /**
   * If the specified field is optional, it is set to None.  Otherwise, if the field is
   * known, it is reverted to its default value; if the field is unknown, it is removed
   * from the passthroughFields map, if present.
   */
  def unsetField(_fieldId: Short): TI32Column = {
    var values: _root_.scala.collection.Seq[Int] = this.values
    var nulls: _root_.java.nio.ByteBuffer = this.nulls

    _fieldId match {
      case 1 =>
        values = _root_.scala.collection.immutable.Nil
      case 2 =>
        nulls = null
      case _ =>
    }
    new Immutable(
      values,
      nulls,
      _passthroughFields - _fieldId
    )
  }

  /**
   * If the specified field is optional, it is set to None.  Otherwise, if the field is
   * known, it is reverted to its default value; if the field is unknown, it is removed
   * from the passthroughFields map, if present.
   */
  def unsetValues: TI32Column = unsetField(1)

  def unsetNulls: TI32Column = unsetField(2)


  override def write(_oprot: TProtocol): Unit = {
    TI32Column.validate(this)
    _oprot.writeStructBegin(Struct)
    if (values ne null) writeValuesField(values, _oprot)
    if (nulls ne null) writeNullsField(nulls, _oprot)
    if (_passthroughFields.nonEmpty) {
      _passthroughFields.values.foreach { _.write(_oprot) }
    }
    _oprot.writeFieldStop()
    _oprot.writeStructEnd()
  }

  def copy(
    values: _root_.scala.collection.Seq[Int] = this.values,
    nulls: _root_.java.nio.ByteBuffer = this.nulls,
    _passthroughFields: immutable$Map[Short, TFieldBlob] = this._passthroughFields
  ): TI32Column =
    new Immutable(
      values,
      nulls,
      _passthroughFields
    )

  override def canEqual(other: Any): Boolean = other.isInstanceOf[TI32Column]

  private def _equals(x: TI32Column, y: TI32Column): Boolean =
      x.productArity == y.productArity &&
      x.productIterator.sameElements(y.productIterator) &&
      x._passthroughFields == y._passthroughFields

  override def equals(other: Any): Boolean =
    canEqual(other) &&
      _equals(this, other.asInstanceOf[TI32Column])

  override def hashCode: Int = {
    _root_.scala.runtime.ScalaRunTime._hashCode(this)
  }

  override def toString: String = _root_.scala.runtime.ScalaRunTime._toString(this)


  override def productArity: Int = 2

  override def productElement(n: Int): Any = n match {
    case 0 => this.values
    case 1 => this.nulls
    case _ => throw new IndexOutOfBoundsException(n.toString)
  }

  override def productPrefix: String = "TI32Column"

  def _codec: ValidatingThriftStructCodec3[TI32Column] = TI32Column

  def newBuilder(): StructBuilder[TI32Column] = new TI32ColumnStructBuilder(_root_.scala.Some(this), fieldTypes)
}

private[thrift] class TI32ColumnStructBuilder(instance: _root_.scala.Option[TI32Column], fieldTypes: IndexedSeq[ClassTag[_]])
    extends StructBuilder[TI32Column](fieldTypes) {

  def build(): TI32Column = instance match {
    case _root_.scala.Some(i) =>
      TI32Column(
        (if (fieldArray(0) == null) i.values else fieldArray(0)).asInstanceOf[_root_.scala.collection.Seq[Int]],
        (if (fieldArray(1) == null) i.nulls else fieldArray(1)).asInstanceOf[_root_.java.nio.ByteBuffer]
      )
    case _root_.scala.None =>
      if (fieldArray.contains(null)) throw new InvalidFieldsException(structBuildError("TI32Column"))
      else {
        TI32Column(
          fieldArray(0).asInstanceOf[_root_.scala.collection.Seq[Int]],
          fieldArray(1).asInstanceOf[_root_.java.nio.ByteBuffer]
        )
      }
    }
}

