package com.intellias.osm.compiler.poi.attributes

import com.intellias.osm.compiler.poi.PoiEnvironmentDummy
import com.intellias.osm.compiler.poi.attributes.keys.OsmFuelType
import com.intellias.osm.model.poi.{EnergyType, POI}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class PoiEnergyTypeExtractorTest extends AnyFunSpec with Matchers with PoiEnvironmentDummy {

  it("should parse energy type correctly") {
    val srcPoi: POI = creatDummyPoi(
      Map(
        "fuel:diesel"     -> "yes",
        "fuel:octane_98"  -> "yes",
        "fuel:adblue"     -> "yes",
        "fuel:octane_95"  -> "yes",
        "fuel:octane_900" -> "yes",
        "fuel:ethanol"    -> "yes",
        "socket:type2"    -> "2"
      ))

    val res = PoiEnergyTypeExtractor.decodeFromOsm(srcPoi)

    res should contain theSameElementsAs Set(EnergyType.Diesel,
                                             EnergyType.Gasoline98,
                                             EnergyType.Gasoline95,
                                             EnergyType.Electricity,
                                             EnergyType.Ethanol)

  }
}
