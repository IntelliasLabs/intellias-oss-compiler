package com.intellias.osm.compiler

import com.intellias.osm.common.SourceType
import com.intellias.osm.model.road.Topology

package object topology {
  case object TopologiesTable extends SourceType[Topology]
}
