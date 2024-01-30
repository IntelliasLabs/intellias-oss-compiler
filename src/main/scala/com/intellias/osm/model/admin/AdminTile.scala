package com.intellias.osm.model.admin

case class AdminTile(
    tileId: Long,
    adminPlaces: Seq[AdminTileItem]
)
