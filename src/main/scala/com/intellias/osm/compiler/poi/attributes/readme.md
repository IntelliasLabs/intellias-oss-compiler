OPEN_24_HRS, /** Indicates that the POI is open 24 hours a day. */



/** List of accepted payment methods (credit cards, mobile payment) by a service. */
ACCEPTED_PAYMENT_METHODS,
/** Access point type. */
ACCESS_POINT_TYPE,
/** IATA airport code. */
AIRPORT_CODE,
/** Brand name of the service. */
BRAND_NAME,
/** Details of an electric vehicle charging station. */
EV_CHARGING_DETAILS,
/** Accepted payment methods of an electric vehicle charging station. */
EV_CHARGING_PAYMENT_METHODS,
/** Energy provider of an electric vehicle charging station. */
EV_ENERGY_PROVIDER,
/** Electric vehicle charging is free of charge. */
EV_FREE_CHARGING,
/** Parking is free during electric vehicle charging. */
EV_FREE_PARKING,
/** Availability of an electric vehicle charging station. */
EV_CHARGING_AVAILABILITY,
/**
Flag indicating that a service has opening hours. Use conditions to model the actual opening times.
*/
OPENING_HRS,
/** E-mail address of the POI. */
EMAIL,
/** Phone number of the POI using the international ITU-T standard E.123. */
PHONE_NUMBER,
/**
Star rating of the service, for example, indicating the quality of hotels or restaurants.
*/
STAR_RATING,
/** Fuel or energy types served at the POI. */
FUEL_TYPE,
/** Type of logical access point. */
LOGICAL_ACCESS_POINT_TYPE,
/** Website of the service. */
WEBSITE,
/**
Identifies whether a POI has been associated with a road that represents the POI's true location or with a road nearby. The attribute suggests what kind of guidance information should be provided to a user. If `IN_VICINITY` does not apply to the POI, the application can state that the user has arrived. If `IN_VICINITY` applies to the POI, the application can tell the user that the POI is nearby but that further routing advice is not available.
*/
IN_VICINITY,
/** Airport entrance type. Can be a main entrance, a terminal entrance, or both. */
AIRPORT_ENTRANCE_TYPE,
/** Type of food available at the POI. */
FOOD_TYPE,
/**
Reference to a data source containing multimedia, for example, a video in the local file system or an image on the internet.
*/
MULTIMEDIA,
/** POI has private access. */
PRIVATE_ACCESS,
/**
Service associated with the POI is of national importance. If not added, local importance is assumed.
*/
NATIONAL_IMPORTANCE,
/** Classification of the parking spots available at a parking facility. */
PARKING_FACILITIES_SIZE_CLASS,
/** Exact number of parking spots available at a parking facility. */
PARKING_FACILITIES_SIZE,
/**
Number of currently free parking spots. Indicates the occupancy of the parking facility at a given moment in time.
*/
NUM_FREE_PARKING_SPOTS,
/** Available services at a rest area POI. */
REST_AREA_SERVICE_AVAILABILITY,
/** Infrastructure accessibility aids. */
ACCESSIBILITY_AIDS,
/** Available restaurant facilities. */
RESTAURANT_FACILITIES_AVAILABLE,
/** Traffic service is available for departures, arrivals, or both. */
DEPARTURE_ARRIVAL_SERVICE,
/** Categorizes the cost of a service, for example, the price level of a restaurant. */
PRICE_RANGE,
/** Short description of the POI. */
SHORT_DESCRIPTION,
/** Long description of the POI. */
LONG_DESCRIPTION,
/** Floor number of the POI. */
FLOOR_NUMBER,
/** Number of rooms with en-suite facilities, for example, in hotels. */
NUMBER_OF_ROOMS_EN_SUITE,
/** Building that indicates a place of worship. */
PLACE_OF_WORSHIP_TYPE,
/** Sportive activities. */
AVAILABLE_SPORTIVE_ACTIVITIES,
/** Fee taken at the POI for getting the respective service. */
SERVICE_FEE,
/** Types of services the car dealer offers. */
CAR_DEALER_TYPE,
/**
Indicates that the POI is of major importance. Can be used, for example, for post offices, airports, railway stations, or access points.
*/
MAJOR_IMPORTANCE,
/** Airport service type. */
AIRPORT_SERVICE_AVAILABILITY,
/** Airport is structurally used for military activities. */
AIRPORT_MILITARY,
/** Transit type. */
TRANSIT_TYPE,
/** Details about the access point, for example, the used access method. */
ACCESS_POINT_DETAILS,
/** Specifies whether parking is provided with a park and ride facility. */
PARK_AND_RIDE_FACILITY,
/** Defines if and how AdBlue fuel is available. */
ADBLUE_AVAILABILITY,
/**
Fuel cell electric vehicles are designed to accept hydrogen at different pressures.
*/
HYDROGEN_PRESSURE_AVAILABILITY,
/** URL for checking the availability of the service. */
AVAILABILITY_URL,
/** Relevance of a POI in relation to the distance to another point. */
RELEVANCE_RADIUS,
/** Global source ID of the POI. */
GLOBAL_SOURCE_ID