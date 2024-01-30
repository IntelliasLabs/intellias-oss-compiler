package com.intellias.osm.compiler.condition

import ch.poole.openinghoursparser._

object OsmTimeOps {
  implicit class TimeSpanImplicit(ts: TimeSpan) {
    def isEvent: Boolean = ts.getStartEvent != null && ts.getEndEvent != null

    def isTime: Boolean = ts.getStart > -1 && ts.getEnd > -1

    def isMixed: Boolean = (ts.getStart > -1 && ts.getEndEvent != null) || (ts.getStartEvent != null && ts.getEnd > -1)
  }

  implicit class TimeSpanListImpl(tsList: Iterable[TimeSpan]) {
    def isAllEvent: Boolean = tsList.forall(_.isEvent)

    def isAllTime: Boolean = tsList.forall(_.isTime)

    def isAllMixed: Boolean = tsList.forall(_.isMixed)
  }

  object EventUnapply {
    def unapply(event: Event): Option[Event] = Option(event)
  }

  object TimeSpanEventUn {
    def unapply(timeSpan: TimeSpan): Option[(Event, Event)] = {
      if (timeSpan.getStartEvent != null && timeSpan.getEndEvent != null) Option((timeSpan.getStartEvent.getEvent, timeSpan.getEndEvent.getEvent))
      else None
    }
  }

  object TimeSpanTimeUn {
    def unapply(timeSpan: TimeSpan): Option[(Int, Int)] = {
      if (timeSpan.getStart > -1 && timeSpan.getEnd > -1) Option((timeSpan.getStart, timeSpan.getEnd))
      else None
    }
  }
}