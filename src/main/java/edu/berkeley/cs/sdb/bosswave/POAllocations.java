package edu.berkeley.cs.sdb.bosswave;

public class POAllocations {
    /*
     * Double (1.0.2.0/32): Double
     * This payload is an 8 byte long IEEE 754 double floating point value
     * encoded in little endian. This should only be used if the semantic
     * meaning is obvious in the context, otherwise a PID with a more specific
     * semantic meaning should be used.
     */
    public static final int PONumDouble = 16777728;
    public static final String PODFMaskDouble = "1.0.2.0/32";
    public static final byte[] PODFDouble = {1, 0, 2, 0};
    public static final int POMaskDouble = 32;

    /*
     * BWMessage (1.0.1.1/32): Packed Bosswave Message
     * This object contains an entire signed and encoded bosswave message
     */
    public static final int PONumBWMessage = 16777473;
    public static final String PODFMaskBWMessage = "1.0.1.1/32";
    public static final byte[] PODFBWMessage = {1, 0, 1, 1};
    public static final int POMaskBWMessage = 32;

    /*
     * SpawnpointSvcHb (2.0.2.2/32): SpawnPoint Service Heartbeat
     * A heartbeat from spawnpoint about a currently running service. It is a
     * msgpack dictionary that contains the keys "SpawnpointURI", "Name",
     * "Time", "MemAlloc", and "CpuShares".
     */
    public static final int PONumSpawnpointSvcHb = 33554946;
    public static final String PODFMaskSpawnpointSvcHb = "2.0.2.2/32";
    public static final byte[] PODFSpawnpointSvcHb = {2, 0, 2, 2};
    public static final int POMaskSpawnpointSvcHb = 32;

    /*
     * Wavelet (1.0.6.1/32): Wavelet binary
     * This object contains a BOSSWAVE Wavelet
     */
    public static final int PONumWavelet = 16778753;
    public static final String PODFMaskWavelet = "1.0.6.1/32";
    public static final byte[] PODFWavelet = {1, 0, 6, 1};
    public static final int POMaskWavelet = 32;

    /*
     * SpawnpointHeartbeat (2.0.2.1/32): SpawnPoint heartbeat
     * A heartbeat message from spawnpoint. It is a msgpack dictionary that
     * contains the keys "Alias", "Time", "TotalMem", "TotalCpuShares",
     * "AvailableMem", and "AvailableCpuShares".
     */
    public static final int PONumSpawnpointHeartbeat = 33554945;
    public static final String PODFMaskSpawnpointHeartbeat = "2.0.2.1/32";
    public static final byte[] PODFSpawnpointHeartbeat = {2, 0, 2, 1};
    public static final int POMaskSpawnpointHeartbeat = 32;

    /*
     * ROPermissionDChain (0.0.0.18/32): Permission DChain
     * A permission dchain
     */
    public static final int PONumROPermissionDChain = 18;
    public static final String PODFMaskROPermissionDChain = "0.0.0.18/32";
    public static final byte[] PODFROPermissionDChain = {0, 0, 0, 18};
    public static final int POMaskROPermissionDChain = 32;

    /*
     * GilesTimeseriesResponse (2.0.8.4/32): Giles Timeseries Response
     * A dictionary containing timeseries results for a query. Has 2 keys: -
     * Nonce: the uint32 number corresponding to the query nonce that generated
     * this timeseries response - Data: list of GilesTimeseries (2.0.8.5)
     * objects - Stats: list of GilesStatistics (2.0.8.6) objects
     */
    public static final int PONumGilesTimeseriesResponse = 33556484;
    public static final String PODFMaskGilesTimeseriesResponse = "2.0.8.4/32";
    public static final byte[] PODFGilesTimeseriesResponse = {2, 0, 8, 4};
    public static final int POMaskGilesTimeseriesResponse = 32;

    /*
     * ROEntityWKey (0.0.0.50/32): Entity with signing key
     * An entity with signing key
     */
    public static final int PONumROEntityWKey = 50;
    public static final String PODFMaskROEntityWKey = "0.0.0.50/32";
    public static final byte[] PODFROEntityWKey = {0, 0, 0, 50};
    public static final int POMaskROEntityWKey = 32;

    /*
     * ROAccessDOT (0.0.0.32/32): Access DOT
     * An access DOT
     */
    public static final int PONumROAccessDOT = 32;
    public static final String PODFMaskROAccessDOT = "0.0.0.32/32";
    public static final byte[] PODFROAccessDOT = {0, 0, 0, 32};
    public static final int POMaskROAccessDOT = 32;

    /*
     * ROOriginVK (0.0.0.49/32): Origin verifying key
     * The origin VK of a message that does not contain a PAC
     */
    public static final int PONumROOriginVK = 49;
    public static final String PODFMaskROOriginVK = "0.0.0.49/32";
    public static final byte[] PODFROOriginVK = {0, 0, 0, 49};
    public static final int POMaskROOriginVK = 32;

    /*
     * GilesKeyValueMetadata (2.0.8.3/32): Giles Key Value Metadata
     * A dictionary containing metadata results for a single stream. Has 2 keys:
     * - UUID: string identifying the stream - Metadata: a map of keys->values
     * of metadata
     */
    public static final int PONumGilesKeyValueMetadata = 33556483;
    public static final String PODFMaskGilesKeyValueMetadata = "2.0.8.3/32";
    public static final byte[] PODFGilesKeyValueMetadata = {2, 0, 8, 3};
    public static final int POMaskGilesKeyValueMetadata = 32;

    /*
     * Binary (0.0.0.0/4): Binary protocols
     * This is a superclass for classes that are generally unreadable in their
     * plain form and require translation.
     */
    public static final int PONumBinary = 0;
    public static final String PODFMaskBinary = "0.0.0.0/4";
    public static final byte[] PODFBinary = {0, 0, 0, 0};
    public static final int POMaskBinary = 4;

    /*
     * FMDIntentString (64.0.1.1/32): FMD Intent String
     * A plain string used as an intent for the follow-me display service.
     */
    public static final int PONumFMDIntentString = 1073742081;
    public static final String PODFMaskFMDIntentString = "64.0.1.1/32";
    public static final byte[] PODFFMDIntentString = {64, 0, 1, 1};
    public static final int POMaskFMDIntentString = 32;

    /*
     * MsgPack (2.0.0.0/8): MsgPack
     * This class is for schemas that are represented in MsgPack
     */
    public static final int PONumMsgPack = 33554432;
    public static final String PODFMaskMsgPack = "2.0.0.0/8";
    public static final byte[] PODFMsgPack = {2, 0, 0, 0};
    public static final int POMaskMsgPack = 8;

    /*
     * ROAccessDChain (0.0.0.2/32): Access DChain
     * An access dchain
     */
    public static final int PONumROAccessDChain = 2;
    public static final String PODFMaskROAccessDChain = "0.0.0.2/32";
    public static final byte[] PODFROAccessDChain = {0, 0, 0, 2};
    public static final int POMaskROAccessDChain = 32;

    /*
     * HamiltonBase (2.0.4.0/24): Hamilton Messages
     * This is the base class for messages used with the Hamilton motes. The
     * only key guaranteed is "#" that contains a uint16 representation of the
     * serial of the mote the message is destined for or originated from.
     */
    public static final int PONumHamiltonBase = 33555456;
    public static final String PODFMaskHamiltonBase = "2.0.4.0/24";
    public static final byte[] PODFHamiltonBase = {2, 0, 4, 0};
    public static final int POMaskHamiltonBase = 24;

    /*
     * YAML (67.0.0.0/8): YAML
     * This class is for schemas that are represented in YAML
     */
    public static final int PONumYAML = 1124073472;
    public static final String PODFMaskYAML = "67.0.0.0/8";
    public static final byte[] PODFYAML = {67, 0, 0, 0};
    public static final int POMaskYAML = 8;

    /*
     * RORevocation (0.0.0.80/32): Revocation
     * A revocation for an Entity or a DOT
     */
    public static final int PONumRORevocation = 80;
    public static final String PODFMaskRORevocation = "0.0.0.80/32";
    public static final byte[] PODFRORevocation = {0, 0, 0, 80};
    public static final int POMaskRORevocation = 32;

    /*
     * JSON (65.0.0.0/8): JSON
     * This class is for schemas that are represented in JSON
     */
    public static final int PONumJSON = 1090519040;
    public static final String PODFMaskJSON = "65.0.0.0/8";
    public static final byte[] PODFJSON = {65, 0, 0, 0};
    public static final int POMaskJSON = 8;

    /*
     * InterfaceDescriptor (2.0.6.1/32): InterfaceDescriptor
     * This object is used to describe an interface. It contains "uri",
     * "iface","svc","namespace" "prefix" and "metadata" keys.
     */
    public static final int PONumInterfaceDescriptor = 33555969;
    public static final String PODFMaskInterfaceDescriptor = "2.0.6.1/32";
    public static final byte[] PODFInterfaceDescriptor = {2, 0, 6, 1};
    public static final int POMaskInterfaceDescriptor = 32;

    /*
     * GilesKeyValueQuery (2.0.8.1/32): Giles Key Value Query
     * Expresses a query to a Giles instance. Expects 2 keys: - Query: A Giles
     * query string following syntax at
     * https://gtfierro.github.io/giles2/interface/#querylang - Nonce: a unique
     * uint32 number for identifying the results of this query
     */
    public static final int PONumGilesKeyValueQuery = 33556481;
    public static final String PODFMaskGilesKeyValueQuery = "2.0.8.1/32";
    public static final byte[] PODFGilesKeyValueQuery = {2, 0, 8, 1};
    public static final int POMaskGilesKeyValueQuery = 32;

    /*
     * GilesMetadataResponse (2.0.8.2/32): Giles Metadata Response
     * Dictionary containing metadata results for a query. Has 2 keys: - Nonce:
     * the uint32 number corresponding to the query nonce that generated this
     * metadata response - Data: list of GilesKeyValueMetadata (2.0.8.3) objects
     */
    public static final int PONumGilesMetadataResponse = 33556482;
    public static final String PODFMaskGilesMetadataResponse = "2.0.8.2/32";
    public static final byte[] PODFGilesMetadataResponse = {2, 0, 8, 2};
    public static final int POMaskGilesMetadataResponse = 32;

    /*
     * ROEntity (0.0.0.48/32): Entity
     * An entity
     */
    public static final int PONumROEntity = 48;
    public static final String PODFMaskROEntity = "0.0.0.48/32";
    public static final byte[] PODFROEntity = {0, 0, 0, 48};
    public static final int POMaskROEntity = 32;

    /*
     * HSBLightMessage (2.0.5.1/32): HSBLight Message
     * This object may contain "hue", "saturation", "brightness" fields with a
     * float from 0 to 1. It may also contain an "state" key with a boolean.
     * Omitting fields leaves them at their previous state.
     */
    public static final int PONumHSBLightMessage = 33555713;
    public static final String PODFMaskHSBLightMessage = "2.0.5.1/32";
    public static final byte[] PODFHSBLightMessage = {2, 0, 5, 1};
    public static final int POMaskHSBLightMessage = 32;

    /*
     * SMetadata (2.0.3.1/32): Simple Metadata entry
     * This contains a simple "val" string and "ts" int64 metadata entry. The
     * key is determined by the URI. Other information MAY be present in the
     * msgpacked object. The timestamp is used for merging metadata entries.
     */
    public static final int PONumSMetadata = 33555201;
    public static final String PODFMaskSMetadata = "2.0.3.1/32";
    public static final byte[] PODFSMetadata = {2, 0, 3, 1};
    public static final int POMaskSMetadata = 32;

    /*
     * LogDict (2.0.1.0/24): LogDict
     * This class is for log messages encoded in msgpack
     */
    public static final int PONumLogDict = 33554688;
    public static final String PODFMaskLogDict = "2.0.1.0/24";
    public static final byte[] PODFLogDict = {2, 0, 1, 0};
    public static final int POMaskLogDict = 24;

    /*
     * ROPermissionDOT (0.0.0.33/32): Permission DOT
     * A permission DOT
     */
    public static final int PONumROPermissionDOT = 33;
    public static final String PODFMaskROPermissionDOT = "0.0.0.33/32";
    public static final byte[] PODFROPermissionDOT = {0, 0, 0, 33};
    public static final int POMaskROPermissionDOT = 32;

    /*
     * BWRoutingObject (0.0.0.0/24): Bosswave Routing Object
     * This class and schema block is reserved for bosswave routing objects
     * represented using the full PID.
     */
    public static final int PONumBWRoutingObject = 0;
    public static final String PODFMaskBWRoutingObject = "0.0.0.0/24";
    public static final byte[] PODFBWRoutingObject = {0, 0, 0, 0};
    public static final int POMaskBWRoutingObject = 24;

    /*
     * BW2Chat_ChatMessage (2.0.7.2/32): BW2Chat_ChatMessage
     * A textual message to be sent to all members of a chatroom. This is a
     * dictionary with three keys: 'Room', the name of the room to publish to
     * (this is actually implicit in the publishing), 'From', the alias you are
     * using for the chatroom, and 'Message', the actual string to be displayed
     * to all users in the room.
     */
    public static final int PONumBW2Chat_ChatMessage = 33556226;
    public static final String PODFMaskBW2Chat_ChatMessage = "2.0.7.2/32";
    public static final byte[] PODFBW2Chat_ChatMessage = {2, 0, 7, 2};
    public static final int POMaskBW2Chat_ChatMessage = 32;

    /*
     * BW2Chat_CreateRoomMessage (2.0.7.1/32): BW2Chat_CreateRoomMessage
     * A dictionary with a single key "Name" indicating the room to be created.
     * This will likely be deprecated.
     */
    public static final int PONumBW2Chat_CreateRoomMessage = 33556225;
    public static final String PODFMaskBW2Chat_CreateRoomMessage = "2.0.7.1/32";
    public static final byte[] PODFBW2Chat_CreateRoomMessage = {2, 0, 7, 1};
    public static final int POMaskBW2Chat_CreateRoomMessage = 32;

    /*
     * CapnP (3.0.0.0/8): Captain Proto
     * This class is for captain proto interfaces. Schemas below this should
     * include the key "schema" with a url to their .capnp file
     */
    public static final int PONumCapnP = 50331648;
    public static final String PODFMaskCapnP = "3.0.0.0/8";
    public static final byte[] PODFCapnP = {3, 0, 0, 0};
    public static final int POMaskCapnP = 8;

    /*
     * ROAccessDChainHash (0.0.0.1/32): Access DChain hash
     * An access dchain hash
     */
    public static final int PONumROAccessDChainHash = 1;
    public static final String PODFMaskROAccessDChainHash = "0.0.0.1/32";
    public static final byte[] PODFROAccessDChainHash = {0, 0, 0, 1};
    public static final int POMaskROAccessDChainHash = 32;

    /*
     * ROPermissionDChainHash (0.0.0.17/32): Permission DChain hash
     * A permission dchain hash
     */
    public static final int PONumROPermissionDChainHash = 17;
    public static final String PODFMaskROPermissionDChainHash = "0.0.0.17/32";
    public static final byte[] PODFROPermissionDChainHash = {0, 0, 0, 17};
    public static final int POMaskROPermissionDChainHash = 32;

    /*
     * AccountBalance (64.0.1.2/32): Account balance
     * A comma seperated representation of an account and its balance as
     * addr,decimal,human_readable. For example 0x49b1d037c33fdaad75d2532cd373fb
     * 5db87cc94c,57203431159181996982272,57203.4311 Ether  . Be careful in that
     * the decimal representation will frequently be bigger than an int64.
     */
    public static final int PONumAccountBalance = 1073742082;
    public static final String PODFMaskAccountBalance = "64.0.1.2/32";
    public static final byte[] PODFAccountBalance = {64, 0, 1, 2};
    public static final int POMaskAccountBalance = 32;

    /*
     * SpawnpointConfig (67.0.2.0/32): SpawnPoint config
     * A configuration file for SpawnPoint (github.com/immesys/spawnpoint)
     */
    public static final int PONumSpawnpointConfig = 1124073984;
    public static final String PODFMaskSpawnpointConfig = "67.0.2.0/32";
    public static final byte[] PODFSpawnpointConfig = {67, 0, 2, 0};
    public static final int POMaskSpawnpointConfig = 32;

    /*
     * BW2Chat_LeaveRoom (2.0.7.4/32): BW2Chat_LeaveRoom
     * Notify users in the chatroom that you have left. Dictionary with a single
     * key "Alias" that has a value of your nickname
     */
    public static final int PONumBW2Chat_LeaveRoom = 33556228;
    public static final String PODFMaskBW2Chat_LeaveRoom = "2.0.7.4/32";
    public static final byte[] PODFBW2Chat_LeaveRoom = {2, 0, 7, 4};
    public static final int POMaskBW2Chat_LeaveRoom = 32;

    /*
     * GilesTimeseries (2.0.8.5/32): Giles Timeseries
     * A dictionary containing timeseries results for a single stream. has 3
     * keys: - UUID: string identifying the stream - Times: list of uint64
     * timestamps - Values: list of float64 values Times and Values will line
     * up, e.g. index i of Times corresponds to index i of values
     */
    public static final int PONumGilesTimeseries = 33556485;
    public static final String PODFMaskGilesTimeseries = "2.0.8.5/32";
    public static final byte[] PODFGilesTimeseries = {2, 0, 8, 5};
    public static final int POMaskGilesTimeseries = 32;

    /*
     * GilesArchiveRequest (2.0.8.0/32): Giles Archive Request
     * A MsgPack dictionary with the following keys: - URI (optional): the URI
     * to subscribe to for data - PO (required): which PO object type to extract
     * from messages on the URI - UUID (optional): the UUID to use, else it is
     * consistently autogenerated. - Value (required): ObjectBuilder expression
     * for how to extract the value - Time (optional): ObjectBuilder expression
     * for how to extract any timestamp - TimeParse (optional): How to parse
     * that timestamp - MetadataURI (optional): a base URI to scan for metadata
     * (expands to uri/!meta/+) - MetadataBlock (optional): URI containing a
     * key-value structure of metadata - MetadataExpr (optional): ObjectBuilder
     * expression to search for a key-value structure in the current message for
     * metadata ObjectBuilder expressions are documented at:
     * https://github.com/gtfierro/giles2/tree/master/objectbuilder
     */
    public static final int PONumGilesArchiveRequest = 33556480;
    public static final String PODFMaskGilesArchiveRequest = "2.0.8.0/32";
    public static final byte[] PODFGilesArchiveRequest = {2, 0, 8, 0};
    public static final int POMaskGilesArchiveRequest = 32;

    /*
     * ROExpiry (0.0.0.64/32): Expiry
     * Sets an expiry for the message
     */
    public static final int PONumROExpiry = 64;
    public static final String PODFMaskROExpiry = "0.0.0.64/32";
    public static final byte[] PODFROExpiry = {0, 0, 0, 64};
    public static final int POMaskROExpiry = 32;

    /*
     * Giles_Messages (2.0.8.0/24): Giles Messages
     * Messages for communicating with a Giles archiver
     */
    public static final int PONumGiles_Messages = 33556480;
    public static final String PODFMaskGiles_Messages = "2.0.8.0/24";
    public static final byte[] PODFGiles_Messages = {2, 0, 8, 0};
    public static final int POMaskGiles_Messages = 24;

    /*
     * BinaryActuation (1.0.1.0/32): Binary actuation
     * This payload object is one byte long, 0x00 for off, 0x01 for on.
     */
    public static final int PONumBinaryActuation = 16777472;
    public static final String PODFMaskBinaryActuation = "1.0.1.0/32";
    public static final byte[] PODFBinaryActuation = {1, 0, 1, 0};
    public static final int POMaskBinaryActuation = 32;

    /*
     * RODRVK (0.0.0.51/32): Designated router verifying key
     * a 32 byte designated router verifying key
     */
    public static final int PONumRODRVK = 51;
    public static final String PODFMaskRODRVK = "0.0.0.51/32";
    public static final byte[] PODFRODRVK = {0, 0, 0, 51};
    public static final int POMaskRODRVK = 32;

    /*
     * BW2ChatMessages (2.0.7.0/24): BW2ChatMessages
     * These are MsgPack dictionaries sent for the BW2Chat program
     * (https://github.com/gtfierro/bw2chat)
     */
    public static final int PONumBW2ChatMessages = 33556224;
    public static final String PODFMaskBW2ChatMessages = "2.0.7.0/24";
    public static final byte[] PODFBW2ChatMessages = {2, 0, 7, 0};
    public static final int POMaskBW2ChatMessages = 24;

    /*
     * Blob (1.0.0.0/8): Blob
     * This is a class for schemas that do not use a public encoding format. In
     * general it should be avoided. Schemas below this should include the key
     * "readme" with a url to a description of the schema that is sufficiently
     * detailed to allow for a developer to reverse engineer the protocol if
     * required.
     */
    public static final int PONumBlob = 16777216;
    public static final String PODFMaskBlob = "1.0.0.0/8";
    public static final byte[] PODFBlob = {1, 0, 0, 0};
    public static final int POMaskBlob = 8;

    /*
     * SpawnpointLog (2.0.2.0/32): Spawnpoint stdout
     * This contains stdout data from a spawnpoint container. It is a msgpacked
     * dictionary that contains a "service" key, a "time" key (unix nano
     * timestamp) and a "contents" key and a "spalias" key.
     */
    public static final int PONumSpawnpointLog = 33554944;
    public static final String PODFMaskSpawnpointLog = "2.0.2.0/32";
    public static final byte[] PODFSpawnpointLog = {2, 0, 2, 0};
    public static final int POMaskSpawnpointLog = 32;

    /*
     * BW2Chat_JoinRoom (2.0.7.3/32): BW2Chat_JoinRoom
     * Notify users in the chatroom that you have joined. Dictionary with a
     * single key "Alias" that has a value of your nickname
     */
    public static final int PONumBW2Chat_JoinRoom = 33556227;
    public static final String PODFMaskBW2Chat_JoinRoom = "2.0.7.3/32";
    public static final byte[] PODFBW2Chat_JoinRoom = {2, 0, 7, 3};
    public static final int POMaskBW2Chat_JoinRoom = 32;

    /*
     * XML (66.0.0.0/8): XML
     * This class is for schemas that are represented in XML
     */
    public static final int PONumXML = 1107296256;
    public static final String PODFMaskXML = "66.0.0.0/8";
    public static final byte[] PODFXML = {66, 0, 0, 0};
    public static final int POMaskXML = 8;

    /*
     * HamiltonTelemetry (2.0.4.64/26): Hamilton Telemetry
     * This object contains a "#" field for the serial number, as well as
     * possibly containing an "A" field with a list of X, Y, and Z accelerometer
     * values. A "T" field containing the temperature as an integer in degrees C
     * multiplied by 10000, and an "L" field containing the illumination in Lux.
     */
    public static final int PONumHamiltonTelemetry = 33555520;
    public static final String PODFMaskHamiltonTelemetry = "2.0.4.64/26";
    public static final byte[] PODFHamiltonTelemetry = {2, 0, 4, 64};
    public static final int POMaskHamiltonTelemetry = 26;

    /*
     * Text (64.0.0.0/4): Human readable text
     * This is a superclass for classes that are moderately understandable if
     * they are read directly in their binary form. Generally these are
     * protocols that were designed specifically to be human readable.
     */
    public static final int PONumText = 1073741824;
    public static final String PODFMaskText = "64.0.0.0/4";
    public static final byte[] PODFText = {64, 0, 0, 0};
    public static final int POMaskText = 4;

    /*
     * GilesStatistics (2.0.8.6/32): Giles Statistics
     * A dictionary containing timeseries results for a single stream. has 3
     * keys: - UUID: string identifying the stream - Times: list of uint64
     * timestamps - Count: list of uint64 values - Min: list of float64 values -
     * Mean: list of float64 values - Max: list of float64 values All fields
     * will line up, e.g. index i of Times corresponds to index i of Count
     */
    public static final int PONumGilesStatistics = 33556486;
    public static final String PODFMaskGilesStatistics = "2.0.8.6/32";
    public static final byte[] PODFGilesStatistics = {2, 0, 8, 6};
    public static final int POMaskGilesStatistics = 32;

    /*
     * TSTaggedMP (2.0.3.0/24): TSTaggedMP
     * This superclass describes "ts"->int64 tagged msgpack objects. The
     * timestamp is used for merging entries and determining which is later and
     * should be the final value.
     */
    public static final int PONumTSTaggedMP = 33555200;
    public static final String PODFMaskTSTaggedMP = "2.0.3.0/24";
    public static final byte[] PODFTSTaggedMP = {2, 0, 3, 0};
    public static final int POMaskTSTaggedMP = 24;

    /*
     * String (64.0.1.0/32): String
     * A plain string with no rigid semantic meaning. This can be thought of as
     * a print statement. Anything that has semantic meaning like a process log
     * should use a different schema.
     */
    public static final int PONumString = 1073742080;
    public static final String PODFMaskString = "64.0.1.0/32";
    public static final byte[] PODFString = {64, 0, 1, 0};
    public static final int POMaskString = 32;

    /*
     * GilesQueryError (2.0.8.9/32): Giles Query Error
     * A dictionary containing an error returned by a query. Has 3 keys: -
     * Query: the string query that was sent - Nonce: the nonce in the query
     * request - Error: string of the returned error
     */
    public static final int PONumGilesQueryError = 33556489;
    public static final String PODFMaskGilesQueryError = "2.0.8.9/32";
    public static final byte[] PODFGilesQueryError = {2, 0, 8, 9};
    public static final int POMaskGilesQueryError = 32;

}