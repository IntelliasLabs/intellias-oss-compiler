package com.intellias.osm.model.road

import com.intellias.osm.model.JsonFormatter


trait EnforcementType {}
object EnforcementType extends JsonFormatter[EnforcementType] {
  val values: Seq[EnforcementType] = Seq(
    AverageSpeedZone,
    SpeedEnforcementZone,
    DangerZone,
    MobileSpeedHotspotZone,
    AccidentBlackspotZone,
    RiskZone
  )

  case object AverageSpeedZone       extends EnforcementType
  case object SpeedEnforcementZone   extends EnforcementType
  case object DangerZone             extends EnforcementType
  case object MobileSpeedHotspotZone extends EnforcementType
  case object AccidentBlackspotZone  extends EnforcementType
  case object RiskZone               extends EnforcementType

}
