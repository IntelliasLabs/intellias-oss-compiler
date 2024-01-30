package com.intellias.osm.ndslive.display.order

import com.intellias.osm.compiler.display.conf.DisplayFeatureProperties
import com.intellias.osm.model.display.DisplayFeatureType

trait DrawingOrderProvider {
  def collectDrawingOrders[T](displayProperties: Array[DisplayFeatureProperties],
                              ndsTypeMap: Map[DisplayFeatureType, T]): Map[T, Short] = {
    displayProperties
      .map(displayProps => displayProps.featureTypeMapped -> displayProps.drawingOrder)
      .toMap
      .filterNot {
        case (featureType, _) => featureType == DisplayFeatureType.Unknown
      }
      .flatMap {
        case (featureType, order) => ndsTypeMap.get(featureType).map(ndsFeatureType => ndsFeatureType -> order.toShort)
      }
  }
}
