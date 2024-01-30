package com.intellias.osm.compiler.geometry.udt

import org.apache.spark.sql.catalyst.util.{ArrayData, GenericArrayData}
import org.apache.spark.sql.types.{ArrayType, DataType, DoubleType, SQLUserDefinedType, UserDefinedType}
import org.locationtech.jts.geom.{Coordinate, GeometryFactory, MultiPolygon}

// @SQLUserDefinedType(udt = classOf[MultiPolygonUDT])
class MultiPolygonUDT extends UserDefinedType[MultiPolygon] {
  override def sqlType: DataType = ArrayType(ArrayType(ArrayType(DoubleType))) // MultiPolygon(Polygon(CoordinatesPair()))

  override def serialize(obj: MultiPolygon): Any = {
    try {
      new GenericArrayData(
        Range(0, obj.getNumGeometries)
          .map(obj.getGeometryN)
          .map(_.getCoordinates.map(coordinate => Array(coordinate.x, coordinate.y)).toArray)
          .toArray
      )
    } catch {
      case e @ _ =>
        println(e)
        new GenericArrayData()
    }
  }

  override def deserialize(datum: Any): MultiPolygon = {
    val coordinates = datum.asInstanceOf[ArrayData].toDoubleArray()
      .sliding(2, 2)
      .map { case Array(x, y) => new Coordinate(x, y) }
      .toArray
    val geometryFactory = new GeometryFactory()
    // TODO: implement properly
    geometryFactory.createMultiPolygon(Array(geometryFactory.createPolygon(coordinates)))
  }

  override def userClass: Class[MultiPolygon] = classOf[MultiPolygon]

  override def equals(o: Any): Boolean = {
    o.isInstanceOf[MultiPolygonUDT]
  }
}