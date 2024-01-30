package com.intellias.osm.ndslive.road

import com.intellias.osm.tools.JsonMapperProvider
import nds.core.types.{Var4ByteDirectedReference, Var4ByteId}
import nds.road.reference.types.RoadReference

trait RoadAttributeValueBuilder extends JsonMapperProvider with Serializable {
  protected def toRef(roadId: NdsRoadId, isPositiveDirection: Option[Boolean]): RoadReference = {
    isPositiveDirection
      .map(isPositive => new RoadReference(true, new Var4ByteDirectedReference(if (isPositive) roadId else -roadId), null))
      .getOrElse(new RoadReference(false, null, new Var4ByteId(roadId)))
  }
}
