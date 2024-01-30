package com.intellias.osm.model.display

case class DisplayTile(tileId: Int,
                       buildingFootprints: Seq[DisplayArea],
                       areas: Seq[DisplayArea],
                       lines: Seq[DisplayLine],
                       points: Seq[DisplayPoint]
                      )
