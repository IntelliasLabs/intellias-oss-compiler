





### RoadForm
* ANY (TODO: not mapped) - Special value to be used if applicable to any road form.
* NORMAL - Normal road
  * any other roads.
* DUAL_CARRIAGEWAY (mapped) - Road that is part of a dual carriageway. 
  * dual_carriageway=yes  [osm wiki](https://taginfo.openstreetmap.org/keys/dual_carriageway#values)
  * highway=(motorway|trunk|primary)
* SLIP_ROAD(mapped) - Slip road of a crossing at same grade. Also known as slip lane. [osm wiki](https://wiki.openstreetmap.org/wiki/Highway_link) 
  * highway=(trunk_link|primary_link|secondary_link|tertiary_link)
  * TODO: it's quite hard to check if it real slip road or just specific road on intersection.
* RAMP(TODO: mapped - all motorway link as ramp for now) - Road connecting two roads at different grade or entrance and exits between controlled-access and non-controlled-access network.
  * highway=motorway_link
* INTERCHANGE(TODO: not mapped) - Road linking one controlled-access road with another controlled-access road at a different grade.
  * not mapped, the idea is to check if motorway_link is connected to motorway (it could be through motorway_link) from both sides then map it as it. if not then as ramp.
* ROUNDABOUT(mapped) - Roundabout
  * junction=roundabout [osm wiki](https://wiki.openstreetmap.org/wiki/Tag:junction%3Droundabout)
  * highway=mini_roundabout
* ROUNDABOUT_INTERIOR(TODO: not mapped) - Interior part of a roundabout.
* SQUARE (TODO: not maped) - Square
* PEDESTRIAN_WAY(mapped) - Pedestrian way.
  * highway=footway
  * highway=cycleway && foot=designated
  * highway=path && foot=designated
  * highway=steps
  * TODO: [Different countries have their own rules as to whether foot access is allowed by default on cycleways](https://wiki.openstreetmap.org/wiki/Tag:highway=cycleway?uselang=en)
* SPECIAL_TRAFFIC_FIGURE(TODO: not mapped) - Road that is part of a special traffic figure relating to a construct with similar exit behavior to a roundabout. Special traffic figures can look like a closed circular, an elongated, or even a rectangular construct of roads that are not perceived as a roundabout.
* PARALLEL_ROAD(TODO: not mapped) - Parallel road to a normal or dual carriageway road.
  * not sure if OSM hase such info.
* SERVICE_ROAD(mapped) - Service access road. Also known as frontage road.
  * highway=* && (frontage_road=yes || side_road=yes) [osm wiki](https://wiki.openstreetmap.org/wiki/Frontage_road)

  



### RoadCharacter
* PARKING (mapped) - Parking facility or a road that is part of a parking area.
  * highway=service && service=parking_aisle
* COVERED (mapped) - Road that is covered by an overhead structure. This may indicate bad GPS reception or decreased light conditions.
  * covered=* && !(covered=no)
* FERRY (mapped)- Ferry connection. Not a real road
  * route=ferry
* TUNNEL(mapped) - Road that is in a tunnel
  * tunnel
* BRIDGE(mapped) - Road that is on a bridge.
  * bridge
* MOTORWAY(mapped) - Road that is part of a motorway.
  * highway=(motorway|motorway_link)
* RACE_TRACK (done) - Road that is part of a race track.
  * highway=raceway [osm wiki](https://wiki.openstreetmap.org/wiki/Tag:highway%3Draceway)
* PEDESTRIAN_ZONE (mapped) - Road within a pedestrian zone
  * highway=pedestrian
* TRACKS_ON_ROAD (done) - Road that has a shared road surface with tram tracks.
  * railway=tram
* BICYCLE_PATH (mapped) - Bicycle path or bicycles highway. A bicycle path is only designed for bicycles.
  A bicycle path is not to be confused with a cyclist road as defined in traffic zone, which is an area where vehicles are allowed but cyclists have priority.
  * bicycle_road=yes [wiki](https://wiki.openstreetmap.org/wiki/Key%3Abicycle_road)
  * cyclestreet=yes [wiki](https://wiki.openstreetmap.org/wiki/Key:cyclestreet)
  * highway=cycleway
  * highway=path && bicycle=designated
* TAXI_ROAD (done)- Road that is only used by taxis.
  * taxi=designated
  * but coverage is very low https://wiki.openstreetmap.org/wiki/Key:taxi
* BUS_ROAD (mapped)- Road that is only used by buses.
  * highway=busway || (highway=service && bus=designated) [osm wiki](https://wiki.openstreetmap.org/wiki/Tag:highway%3Dbusway) [in the end of page Deprecated: highway=service for busways](https://wiki.openstreetmap.org/wiki/Tag:highway%3Dservice)
  * way[highway][access=no][bus=yes]({{bbox}}); TODO: add it.
* HORSE_WAY (mapped)- Road that is only used by horses.
  * highway=bridleway [wiki](https://wiki.openstreetmap.org/wiki/Tag:highway%3Dbridleway)
* EMERGENCY_ROAD (mapped)- Road that is used only in emergency cases or by emergency vehicles.
  * service=emergency_access [osm wiki](https://wiki.openstreetmap.org/wiki/Tag:service%3Demergency_access)
* TOLL_ROAD(mapped) - A toll road. All lanes are subject to toll.
  * highway=* && toll=yes [osm wiki](https://wiki.openstreetmap.org/wiki/Key:toll)
* MULTI_DIGITIZED (mapped) - Multi-digitized road, that is, a real-world road that is digitized as multiple separate roads.
  For example, this is done if the two sides of the road are separated by a physical divider.
  The road character type is also used in complex intersections.
  * highway=(motorway|trunk|primary) && oneway=yes
* PHYSICALLY_SEPARATED (mapped) - Road that is physically separated from other roads. No cross traffic is to be expected from other roads.
  If a road only has one travel direction, also no incoming traffic is to be expected.
  * highway=(trunk|motorway)
* SERVICE_AREA(TODO: mapped partially) - Road that is part of a service area or special purpose property, such as a golf course. Includes all entries and exits of these facilities.
  * highway=service [osm wiki](https://wiki.openstreetmap.org/wiki/Tag:highway%3Dservice)
* EXPRESSWAY (TODO: mapped partially)- Limit access road that is not a motorway. [??? need to review after changes in multi-digitized]
  * expressway=yes !(highway=(motorway_link|motorway))
  * [info](https://wiki.openstreetmap.org/wiki/Key:expressway)  [talk about expressway](https://wiki.openstreetmap.org/wiki/Talk:Key:expressway#Cleanup_Request)
* EXPRESS_ROAD (mapped) -All lanes of the road are express lanes. An express road is a road with a limited number of entrance and exit points.
  It is usually part of a motorway. Express roads are physically separated from normal roads.
  * highway=motorway || highway=trunk
* CONTROLLED_ACCESS(mapped) - Road with controlled access, that is, an intersection-free road.
  * highway=(motorway|motorway_link)[isMotorway] || (highway && motorroad=yes)
* TRUCK_ESCAPE_RAMP(mapped) - Road that is part of a truck escape ramp
  * highway=escape
* HAS_SHOULDER_LANE(mapped) - Road with a shoulder lane.
  * shoulder=(yes|left|right|both)    [osm wiki](https://wiki.openstreetmap.org/wiki/Key:shoulder)
* OVERPASS (mapped) - Road on an overpass. An overpass is defined as a structure that bridges over another road without significant change of grade, that is, the z-level does not change and the road does not lead up or down.
  * layer > 0
* UNDERPASS (TODO: some road have value layer<0, but most doesn't) - Road on an underpass. An underpass is defined as a structure that passes under another road with significant change of grade. That is, the z-level does change whereas the road that it passes under keeps its grade.
  * layer < 0
  * some roads have layer=-1, JTS calculations is needed here.
* INSIDE_CITY_LIMITS (TODO: mapped partially, admin hierarchy is needed.) - Road that is inside a city limit.
  This information is derived from city entry or exit signs at city limits in contrast to urban which is only used in built-up areas.
  The value is also used for motorways that are located within city limits.
  * highway=(living_street|residential|tertiary)
  * additional to highway types we can add following tags as an indication of city limits roads 
````    way[maxspeed~".*:urban"]({{bbox}});
        way[maxspeed~"(50|30|20|15)"]({{bbox}}); -> this walue can be grabbed from admin rules.
        way[highway~"(living_street|residential|tertiary|service)"]({{bbox}});
````

* URBAN (TODO: partially mapped, for the rest the admin hierarchy is needed)- Road that is part of an urban or built-up area.
  * highway=(living_street|residential|tertiary) [osm wiki](https://wiki.openstreetmap.org/wiki/Uk:Tag:highway=tertiary?uselang=uk)
  * TODO:  some info how to identify if road is urban can we found here [osm wiki](https://wiki.openstreetmap.org/wiki/Default_speed_limits) section "Road types to tag filters"
  * way[maxspeed~".*:urban"]({{bbox}});
  *  way[highway~"(service)"]({{bbox}}); ???
  *  way["source:maxspeed"~".*urban"]({{bbox}});
  *  way["maxspeed:type"~".*urban"]({{bbox}});
  *  way["zone:maxspeed"~".*urban"]({{bbox}});
  *  way["zone:traffic"~".*urban"]({{bbox}});
  *  way["maxspeed"~".*urban"]({{bbox}});
  *  way["HFCS"~".*Urban.*"]({{bbox}});

* COMPLEX_INTERSECTION (TODO: not mapped) - Road that is part of a complex intersection. 
    A complex intersection is composed of multiple roads that are handled as one real-world intersection. 
    In between two complex intersections it is important that at least one road does not have the complex intersection road character assigned. 
    This way, an application can determine all roads that belong to a complex intersection by following the topology up to the first road that does not have the complex intersection road character assigned.
* IS_ELEVATED_ROAD (TODO: not sure if this info is present in OSM)- Road that is elevated above other nearby roads. An elevated road is usually long and spans several blocks or runs throughout an entire downtown area.
* STATION_PLAZA(TODO: not mapped) - Widened area before and after a station. In this area, lanes are not delimited.,


### PavementType
* ASPHALT - Asphalt pavement.
  * asphalt
* SANDY - Sandy pavement.
  * sand
* GRAVEL - Gravel pavement, such as crushed stones
  * gravel
  * fine_gravel
  * compacted
  * pebblestone
  * chipseal
* COBBLESTONE - Cobblestone pavement.
  * cobblestone
  * sett
  * paving_stones
  * paving_stones:lanes
  * grass_paver
  * unhewn_cobblestone
  * paving_stones:30
  * bricks
  * brick
  * cobblestone:flattened
* CONCRETE - Concrete pavement
  * concrete
  * cement
* PAVED - The road or lane is paved in some way, for example, with concrete or asphalt.
  * paved
  * concrete:plates
* POOR_CONDITION - The road or lane is paved, but in a poor condition, for example, with large holes.
  * 
* OTHER - The road has any other type of pavement.
  * wood
  * metal
  * tartan
  * artificial_turf
  * concrete:lanes
  * rock
  * stone
  * woodchips
  * acrylic
  * hard
  * plastic
  * metal_grid
  * rubber
  * rocks
  * decoturf
* UNKNOWN - Pavement type is unknown.
  * yes
* UNPAVED - The road or lane is not paved, for example, it consists of sand or gravel.
  * unpaved
  * ground
  * dirt
  * grass
  * earth
  * clay
  * mud
  * dirt/sand
  * soil
  * trail
  * ground;grass
  * grass;ground
  * gravel;ground
  * boardwalk