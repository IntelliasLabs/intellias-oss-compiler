package com.intellias.osm

import com.intellias.osm.compiler.display.DisplayWorkflow
import com.intellias.osm.compiler.poi.PoiWorkflow
import com.intellias.osm.compiler.road.RoadWorkflow
import com.intellias.osm.ndslive.display.NdsDisplayWorkflow
import com.intellias.osm.ndslive.name.NdsNameWorkflow
import com.intellias.osm.ndslive.poi.NdsPoiWorkflow
import com.intellias.osm.ndslive.road.NdsRoadWorkflow

object Main extends SparkLocalRunner {

  def main(args: Array[String]): Unit = {
    try {
      RoadWorkflow(args.headOption).run()
      NdsRoadWorkflow(args.headOption).run()
      PoiWorkflow(args.headOption).run()
      NdsPoiWorkflow(args.headOption).run()
      DisplayWorkflow(args.headOption).run()
      NdsDisplayWorkflow(args.headOption).run()
      NdsNameWorkflow(args.headOption).run()
    } finally {
      spark.stop()
    }
  }
}
