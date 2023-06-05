/*
 *
 *    Copyright (c) 2023 Project CHIP Authors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
 
package chip.onboardingpayload

// See section 5.1.2. QR Code in the Matter specification
const val kDiscriminatorLongBits = 12
const val kDiscriminatorShortBits = 4
const val kDiscriminatorLongMask = (1 shl kDiscriminatorLongBits) - 1
const val kDiscriminatorShortMask = (1 shl kDiscriminatorShortBits) - 1

const val kVersionFieldLengthInBits              = 3
const val kVendorIDFieldLengthInBits             = 16
const val kProductIDFieldLengthInBits            = 16
const val kCommissioningFlowFieldLengthInBits    = 2
const val kRendezvousInfoFieldLengthInBits       = 8
const val kPayloadDiscriminatorFieldLengthInBits = kDiscriminatorLongBits
const val kSetupPINCodeFieldLengthInBits         = 27
const val kPaddingFieldLengthInBits              = 4
const val kRawVendorTagLengthInBits              = 7

// See section 5.1.3. Manual Pairing Code in the Matter specification
const val kManualSetupDiscriminatorFieldLengthInBits  = kDiscriminatorShortBits
const val kManualSetupChunk1DiscriminatorMsbitsPos    = 0
const val kManualSetupChunk1DiscriminatorMsbitsLength = 2
const val kManualSetupChunk1VidPidPresentBitPos =
    (kManualSetupChunk1DiscriminatorMsbitsPos + kManualSetupChunk1DiscriminatorMsbitsLength)
const val kManualSetupChunk2PINCodeLsbitsPos       = 0
const val kManualSetupChunk2PINCodeLsbitsLength    = 14
const val kManualSetupChunk2DiscriminatorLsbitsPos = (kManualSetupChunk2PINCodeLsbitsPos + kManualSetupChunk2PINCodeLsbitsLength)
const val kManualSetupChunk2DiscriminatorLsbitsLength = 2
const val kManualSetupChunk3PINCodeMsbitsPos          = 0
const val kManualSetupChunk3PINCodeMsbitsLength       = 13

const val kManualSetupShortCodeCharLength  = 10
const val kManualSetupLongCodeCharLength   = 20
const val kManualSetupCodeChunk1CharLength = 1
const val kManualSetupCodeChunk2CharLength = 5
const val kManualSetupCodeChunk3CharLength = 4
const val kManualSetupVendorIdCharLength   = 5
const val kManualSetupProductIdCharLength  = 5

// Spec 5.1.4.2 CHIP-Common Reserved Tags
const val kSerialNumberTag         = 0x00
const val kPBKDFIterationsTag      = 0x01
const val kBPKFSaltTag             = 0x02
const val kNumberOFDevicesTag      = 0x03
const val kCommissioningTimeoutTag = 0x04

const val kSetupPINCodeMaximumValue   = 99999998
const val kSetupPINCodeUndefinedValue = 0

/** Class to hold the data from the scanned QR code or Manual Pairing Code.  */
class OnboardingPayload(
  /** Version info of the OnboardingPayload: version SHALL be 0 */
  var version: Int = 0,
  
  /** The CHIP device vendor ID: Vendor ID SHALL be between 1 and 0xFFF4. */
  var vendorId: Int = 0,
  
  /** The CHIP device product ID: Product ID SHALL BE greater than 0. */
  var productId: Int = 0,
  
  /** Commissioning flow: 0 = standard, 1 = requires user action, 2 = custom */
  var commissioningFlow: Int = 0,
  
  /**
   * The CHIP device supported rendezvous flags: At least one DiscoveryCapability must be included.
   */
  var discoveryCapabilities: Set<DiscoveryCapability> = emptySet(),
  
  /** The CHIP device discriminator: */
  var discriminator: Int = 0,
  
  /**
   * If hasShortDiscriminator is true, the discriminator value contains just the high 4 bits of the
   * full discriminator. For example, if hasShortDiscriminator is true and discriminator is 0xA,
   * then the full discriminator can be anything in the range 0xA00 to 0xAFF.
   */
  var hasShortDiscriminator: Boolean = false,
  
  /**
   * The CHIP device setup PIN code: setupPINCode SHALL be greater than 0. Also invalid setupPINCode
   * is {000000000, 11111111, 22222222, 33333333, 44444444, 55555555, 66666666, 77777777, 88888888,
   * 99999999, 12345678, 87654321}.
   */
  var setupPinCode: Long = 0
) {
  var optionalQRCodeInfo: HashMap<Int, OptionalQRCodeInfo>

  // code="0x0000" is defined as "Matter Standard" in the official CSA alliance manufacturer ID database
  // and is treated as invalid in Matter SDK. 
  enum class VendorId(val value: Int) {
    UNSPECIFIED(0x0000),
    PANASONIC(0x0001),
    SONY(0x0002),
    SAMSUNG(0x0003),
    PHILIPS(0x0004),
    FREESCALE_RF(0x0005),
    OKI_SEMICONDUCTORS(0x0006),
    TEXAS_INSTRUMENTS(0x0007),
    CIRRONET(0x1000),
    CHIPCON(0x1001),
    EMBER(0x1002),
    NTS(0x1003),
    FREESCALE(0x1004),
    IP_COM(0x1005),
    SAN_JUAN_SOFTWARE(0x1006),
    TUV(0x1007),
    INTEGRATION(0x1008),
    BM_SPA(0x1009),
    AWAREPOINT(0x100A),
    SIGNIFY_NETHERLANDS(0x100B),
    LUXOFT(0x100C),
    KORWIN(0x100D),
    ONE_RF_TECHNOLOGY(0x100E),
    SOFTWARE_TECHNOLOGIES_GROUP(0x100F),
    TELEGESIS(0x1010),
    VISONIC(0x1011),
    INSTA(0x1012),
    ATALUM(0x1013),
    ATMEL(0x1014),
    DEVELCO(0x1015),
    HONEYWELL(0x1016),
    RADIOPULSE(0x1017),
    RENESAS(0x1018),
    XANADU_WIRELESS(0x1019),
    NEC_ENGINEERING(0x101A),
    YAMATAKE_CORPORATION(0x101B),
    TENDRIL_NETWORKS(0x101C),
    ASSA_ABLOY(0x101D),
    MAXSTREAM(0x101E),
    NEUROCOM(0x101F),
    INSTITUTE_FOR_INFORMATION_INDUSTRY(0x1020),
    LEGRAND_GROUP(0x1021),
    ICONTROL(0x1022),
    RAYMARINE(0x1023),
    LS_RESEARCH(0x1024),
    ONITY_INC(0x1025),
    MONO_PRODUCTS(0x1026),
    RF_TECHNOLOGIES(0x1027),
    ITRON(0x1028),
    TRITECH(0x1029),
    EMBEDIT_AS(0x102A),
    S3C(0x102B),
    SIEMENS(0x102C),
    MINDTECH(0x102D),
    LG_ELECTRONICS(0x102E),
    MITSUBISHI_ELECTRIC_CORP(0x102F),
    JOHNSON_CONTROLS(0x1030),
    SECURE_METERS_UK_LTD(0x1031),
    KNICK(0x1032),
    VICONICS(0x1033),
    FLEXIPANEL(0x1034),
    PIASIM_CORPORATION_PTE_LTD(0x1035),
    TRANE(0x1036),
    NXP_SEMICONDUCTORS(0x1037),
    LIVING_INDEPENDENTLY_GROUP(0x1038),
    ALERTME_COM(0x1039),
    DAINTREE(0x103A),
    AIJI_SYSTEM(0x103B),
    TELECOM_ITALIA(0x103C),
    MIKROKRETS_AS(0x103D),
    OKI_SEMICONDUCTOR(0x103E),
    NEWPORT_ELECTONICS(0x103F),
    CONTROL_4(0x1040),
    STMICROELECTRONICS(0x1041),
    AD_SOL_NISSIN_CORP(0x1042),
    DCSI(0x1043),
    FRANCE_TELECOM(0x1044),
    MUNET(0x1045),
    AUTANI_CORPORATION(0x1046),
    COLORADO_VNET(0x1047),
    AEROCOMM_INC(0x1048),
    SILICON_LABORATORIES(0x1049),
    INNCOM_INTERNATIONAL_INC(0x104A),
    COOPER_POWER_SYSTEMS(0x104B),
    SYNAPSE(0x104C),
    FISHER_PIERCE_SUNRISE(0x104D),
    CENTRALITE_SYSTEMS_INC(0x104E),
    CRANE_WIRELESS_MONITORING_SOLUTIONS(0x104F),
    MOBILARM_LIMITED(0x1050),
    IMONITOR_RESEARCH_LTD(0x1051),
    BARTECH(0x1052),
    MESHNETICS(0x1053),
    LS_INDUSTRIAL_SYSTEMS(0x1054),
    CASON_ENGINEERING(0x1055),
    WIRELESS_GLUE_NETWORKS(0x1056),
    ELSTER(0x1057),
    SMS_TECNOLOGIA_ELETRONICA(0x1058),
    ONSET_COMPUTER_CORPORATION(0x1059),
    RIGA_DEVELOPMENT(0x105A),
    ENERGATE(0x105B),
    CONMED_LINVATEC(0x105C),
    POWERMAND(0x105D),
    SCHNEIDER_ELECTRIC(0x105E),
    EATON_CORPORATION(0x105F),
    TELULAR_CORPORATION(0x1060),
    DELPHI_MEDICAL_SYSTEMS(0x1061),
    EPISENSOR_LIMITED(0x1062),
    LANDIS_GYR(0x1063),
    KABA_GROUP(0x1064),
    SHURE_INCORPORATED(0x1065),
    COMVERGE_INC(0x1066),
    DBS_LODGING_TECHNOLOGIES(0x1067),
    ENERGY_AWARE_TECHNOLOGY(0x1068),
    HIDALGO_LIMITED(0x1069),
    AIR2APP(0x106A),
    AMX(0x106B),
    EDMI_PTY_LTD(0x106C),
    CYAN_LTD(0x106D),
    SYSTEM_SPA(0x106E),
    TELIT(0x106F),
    KAGA_ELECTRONICS(0x1070),
    ASTREL_GROUP(0x1071),
    CERTICOM(0x1072),
    GRIDPOINT(0x1073),
    PROFILE_SYSTEMS(0x1074),
    COMPACTA_INTERNATIONAL(0x1075),
    FREESTYLE_TECHNOLOGY(0x1076),
    ALEKTRONA(0x1077),
    COMPUTIME(0x1078),
    REMOTE_TECHNOLOGIES_INC(0x1079),
    WAVECOM_SA(0x107A),
    ENERGY_OPTIMIZERS(0x107B),
    GE(0x107C),
    JETLUN(0x107D),
    CIPHER_SYSTEMS(0x107E),
    CORPORATE_SYSTEMS_ENGINEERING(0x107F),
    ECOBEE(0x1080),
    SMK(0x1081),
    MESHWORKS_WIRELESS(0x1082),
    ELLIPS_BV(0x1083),
    SECURE_ELECTRANS(0x1084),
    CEDO(0x1085),
    TOSHIBA(0x1086),
    DIGI_INTERNATIONAL(0x1087),
    UBILogix(0x1088),
    ECHELON(0x1089),
    GREEN_ENERGY_OPTIONS(0x1090),
    SILVER_SPRING_NETWORKS(0x1091),
    BLACK_AND_DECKER(0x1092),
    AZTECH_ASSOCIATES_INC(0x1093),
    A_AND_D_CO_LTD(0x1094),
    RAINFOREST_AUTOMATION(0x1095),
    CARRIER_ELECTRONICS(0x1096),
    SYCHIP_MURATA(0x1097),
    OPENPEAK(0x1098),
    PASSIVE_SYSTEMS(0x1099),
    MMB_RESEARCH(0x109A),
    LEVITON_MANUFACTURING_COMPANY(0x109B),
    KOREA_ELECTRIC_POWER_DATA_NETWORK_CO(0x109C),
    COMCAST(0x109D),
    NEC_ELECTRONICS(0x109E),
    NETVOX(0x109F),
    U_CONTROL(0x10A0),
    EMBEDIA_TECHNOLOGIES_CORP(0x10A1),
    SENSUS(0x10A2),
    SUNRISE_TECHNOLOGIES(0x10A3),
    MEMTECH_CORP(0x10A4),
    FREEBOX(0x10A5),
    M2_LABS_LTD(0x10A6),
    BRITISH_GAS(0x10A7),
    SENTEC_LTD(0x10A8),
    NAVETAS(0x10A9),
    LIGHTSPEED_TECHNOLOGIES(0x10AA),
    OKI_ELECTRIC_INDUSTRY_CO(0x10AB),
    S_I_SISTEMAS_INTELIGENTES_ELETRONICOS(0x10AC),
    DOMETIC(0x10AD),
    ALPS(0x10AE),
    ENERGYHUB(0x10AF),
    KAMSTRUP(0x10B0),
    ECHOSTAR(0x10B1),
    ENERNOC(0x10B2),
    ELTAV(0x10B3),
    BELKIN(0x10B4),
    XSTREAMHD_WIRELESS_VENTURES(0x10B5),
    SATURN_SOUTH_PTY_LTD(0x10B6),
    GREENTRAPOLINE_A_S(0x10B7),
    SMARTSYNCH_INC(0x10B8),
    NYCE_CONTROL_INC(0x10B9),
    ICM_CONTROLS_CORP(0x10BA),
    MILLENNIUM_ELECTRONICS_PTY_LTD(0x10BB),
    MOTOROLA_INC(0x10BC),
    EMERSON_WHITE_RODGERS(0x10BD),
    RADIO_THERMOSTAT_COMPANY_OF_AMERICA(0x10BE),
    OMRON_CORPORATION(0x10BF),
    GIINII_GLOBAL_LIMITED(0x10C0),
    FUJITSU_GENERAL_LIMITED(0x10C1),
    PEEL_TECHNOLOGIES_INC(0x10C2),
    ACCENT_SPA(0x10C3),
    BYTESNAP_DESIGN_LTD(0x10C4),
    NEC_TOKIN_CORPORATION(0x10C5),
    G4S_JUSTICE_SERVICES(0x10C6),
    TRILLIANT_NETWORKS_INC(0x10C7),
    ELECTROLUX_ITALIA_SPA(0x10C8),
    ONZO_LTD(0x10C9),
    ENTEK_SYSTEMS(0x10CA),
    MAINSTREAM_ENGINEERING(0x10CC),
    INDESIT_COMPANY(0x10CD),
    THINKECO_INC(0x10CE),
    D2C_INC(0x10CF),
    QORVO(0x10D0),
    INTERCEL(0x10D1),
    MITSUMI_ELECTRIC_CO_LTD(0x10D3),
    MITSUMI_ELECTRIC_CO_LTD_2(0x10D4),
    ZENTRUM_MIKROELEKTRONIK_DRESDEN_AG(0x10D5),
    NEST_LABS_INC(0x10D6),
    EXEGIN_TECHNOLOGIES_LTD(0x10D7),
    TAKAHATA_PRECISION_CO(0x10D9),
    SUMITOMO_ELECTRIC_NETWORKS_INC(0x10DA),
    GE_ENERGY(0x10DB),
    GE_APPLIANCES(0x10DC),
    RADIOCRAFTS_AS(0x10DD),
    CEIVA(0x10DE),
    TEC_AND_CO_CO_LTD(0x10DF),
    CHAMELEON_TECHNOLOGY_UK_LTD(0x10E0),
    RUWIDO_AUSTRIA_GMBH(0x10E2),
    HUAWEI_TECHNOLOGIES_CO(0x10E3),
    HUAWEI_TECHNOLOGIES_CO_2(0x10E4),
    GREENWAVE_REALITY(0x10E5),
    BGLOBAL_METERING_LTD(0x10E6),
    MINDTECK(0x10E7),
    INGERSOLL_RAND(0x10E8),
    DIUS_COMPUTING_PTY_LTD(0x10E9),
    EMBEDDED_AUTOMATION_INC(0x10EA),
    ABB(0x10EB),
    GENUS_POWER_INFRASTRUCTURES_LIMITED(0x10ED),
    UNIVERSAL_ELECTRONICS_INC(0x10EE),
    UNIVERSAL_ELECTRONICS_INC_2(0x10EF),
    METRUM_TECHNOLOGIES_LLC(0x10F0),
    CISCO(0x10F1),
    UBISYS_TECHNOLOGIES_GMBH(0x10F2),
    CONSERT(0x10F3),
    CRESTRON_ELECTRONICS(0x10F4),
    ENPHASE_ENERGY(0x10F5),
    INVENSYS_CONTROLS(0x10F6),
    MUELLER_SYSTEMS_LLC(0x10F7),
    AAC_TECHNOLOGIES_HOLDING(0x10F8),
    U_NEXT_CO(0x10F9),
    STEELCASE_INC(0x10FA),
    TELEMATICS_WIRELESS(0x10FB),
    SAMIL_POWER_CO(0x10FC),
    PACE_PLC(0x10FD),
    OSBORNE_COINAGE_CO(0x10FE),
    POWERWATCH(0x10FF),
    CANDELED_GMBH(0x1100),
    FLEXGRID_SRL(0x1101),
    HUMAX(0x1102),
    UNIVERSAL_DEVICES(0x1103),
    ADVANCED_ENERGY(0x1104),
    BEGA_GANTENBRINK_LEUCHTEN(0x1105),
    BRUNEL_UNIVERSITY(0x1106),
    PANASONIC_RD_CENTER_SINGAPORE(0x1107),
    ESYSTEMS_RESEARCH(0x1108),
    PANAMAX(0x1109),
    SMARTTHINGS_INC(0x110A),
    EM_LITE_LTD(0x110B),
    OSRAM_SYLVANIA(0x110C),
    SAVE_ENERGY_LTD(0x110D),
    PLANET_INNOVATION_PRODUCTS_PTY_LTD(0x110E),
    AMBIENT_DEVICES_INC(0x110F),
    PROFALUX(0x1110),
    BILLION_ELECTRIC_COMPANY(0x1111),
    EMBERTEC_PTY_LTD(0x1112),
    IT_WATCHDOGS(0x1113),
    RELOC(0x1114),
    INTEL_CORPORATION(0x1115),
    TREND_ELECTRONICS_LIMITED(0x1116),
    MOXA(0x1117),
    QEES(0x1118),
    SAYME_WIRELESS_SENSOR_NETWORKS(0x1119),
    PENTAIR_AQUATIC_SYSTEMS(0x111A),
    ORBIT_IRRIGATION(0x111B),
    CALIFORNIA_EASTERN_LABORATORIES(0x111C),
    IDT_TECHNOLOGY_LIMITED(0x111E),
    PIXELA_CORPORATION(0x111F),
    TIVO_INC(0x1120),
    FIDURE_CORP(0x1121),
    MARVELL_SEMICONDUCTOR_INC(0x1122),
    WASION_GROUP_LIMITED(0x1123),
    JASCO_PRODUCTS_COMPANY(0x1124),
    SHENZHEN_KAIFA_TECHNOLOGY(0x1125),
    NETCOMM_WIRELESS_LIMITED(0x1126),
    DEFINE_INSTRUMENTS_LIMITED(0x1127),
    IN_HOME_DISPLAYS_LTD(0x1128),
    MIELE_AND_CIE_KG(0x1129),
    TELEVES_SA(0x112A),
    LABELEC(0x112B),
    CHINA_ELECTRONICS_STANDARDIZATION_INSTITUTE(0x112C),
    VECTORFORM_LLC(0x112D),
    BUSCH_JAEGER_ELEKTRO(0x112E),
    REDPINE_SIGNALS_INC(0x112F),
    BRIDGES_ELECTRONIC_TECHNOLOGY_PTY_LTD(0x1130),
    SERCOMM(0x1131),
    WSH_GMBH_WIRSINDHELLER(0x1132),
    BOSCH_SECURITY_SYSTEMS_INC(0x1133),
    EZEX_CORPORATION(0x1134),
    DRESDEN_ELEKTRONIK_INGENIEURTECHNIK_GMBH(0x1135),
    MEAZON_SA(0x1136),
    CROW_ELECTRONIC_ENGINEERING_LTD(0x1137),
    HARVARD_ENGINEERING_PLC(0x1138),
    ANDSON_BEIJING_TECHNOLOGY(0x1139),
    ADHOCO_AG(0x113A),
    WAXMAN_CONSUMER_PRODUCTS_GROUP_INC(0x113B),
    OWON_TECHNOLOGY_INC(0x113C),
    HITRON_TECHNOLOGIES_INC(0x113D),
    SCEMTEC_HARD_UND_SOFTWARE(0x113E),
    WEBEE_LLC(0x113F),
    GRID2HOME_INC(0x1140),
    TELINK_MICRO(0x1141),
    JASMINE_SYSTEMS_INC(0x1142),
    BIDGELY(0x1143),
    LUTRON(0x1144),
    IJENKO(0x1145),
    STARFIELD_ELECTRONIC_LTD(0x1146),
    TCP_INC(0x1147),
    ROGERS_COMMUNICATIONS_PARTNERSHIP(0x1148),
    CREE_INC(0x1149),
    ROBERT_BOSCH_LLC(0x114A),
    IBIS_NETWORKS_INC(0x114B),
    QUIRKY_INC(0x114C),
    EFERGY_TECHNOLOGIES_LIMITED(0x114D),
    SMARTLABS_INC(0x114E),
    EVERSPRING_INDUSTRY_CO_LTD(0x114F),
    SWANN_COMMUNICATIONS_PTL_LTD(0x1150),
    SONETER(0x1151),
    SAMSUNG_SDS(0x1152),
    UNIBAND_ELECTRONIC_CORPORATION(0x1153),
    ACCTON_TECHNOLOGY_CORPORATION(0x1154),
    BOSCH_THERMOTECHNIK_GMBH(0x1155),
    WINCOR_NIXDORF_INC(0x1156),
    OHSUNG_ELECTRONICS(0x1157),
    ZEN_WITHIN_INC(0x1158),
    TECH4HOME_LDA(0x1159),
    NANOLEAF(0x115A),
    KEEN_HOME_INC(0x115B),
    POLY_CONTROL_APS(0x115C),
    EASTFIELD_LIGHTING_CO_LTD_SHENZHEN(0x115D),
    IP_DATATEL_INC(0x115E),
    LUMI_UNITED_TECHOLOGY_LTD_SHENZHEN(0x115F),
    SENGLED_CO_LTD(0x1160),
    REMOTE_SOLUTION_CO_LTD(0x1161),
    ABB_GENWAY_XIAMEN_ELECTRICAL_EQUIPMENT_CO(0x1162),
    ZHEJIANG_REXENSE_TECH(0x1163),
    FOREE_TECHNOLOGY(0x1164),
    OPEN_ACCESS_TECHNOLOGY_INTL(0x1165),
    INNR_LIGHTING_BV(0x1166),
    TECHWORLD_INDUSTRIES(0x1167),
    LEEDARSON_LIGHTING_CO_LTD(0x1168),
    ARZEL_ZONING(0x1169),
    HOLLEY_TECHNOLOGY(0x116A),
    BELDON_TECHNOLOGIES(0x116B),
    FLEXTRONICS(0x116C),
    SHENZHEN_MEIAN(0x116D),
    LOWES(0x116E),
    SIGMA_CONNECTIVITY(0x116F),
    WULIAN(0x1171),
    PLUGWISE_BV(0x1172),
    TITAN_PRODUCTS(0x1173),
    ECOSPECTRAL(0x1174),
    D_LINK(0x1175),
    TECHNICOLOR_HOME_USA(0x1176),
    OPPLE_LIGHTING(0x1177),
    WISTRON_NEWEB_CORP(0x1178),
    QMOTION_SHADES(0x1179),
    INSTA_GMBH(0x117A),
    SHANGHAI_VANCOUNT(0x117B),
    IKEA_OF_SWEDEN(0x117C),
    RT_RK(0x117D),
    SHENZHEN_FEIBIT(0x117E),
    EUCONTROLS(0x117F),
    TELKONET(0x1180),
    THERMAL_SOLUTION_RESOURCES(0x1181),
    POMCUBE(0x1182),
    EI_ELECTRONICS(0x1183),
    OPTOGA(0x1184),
    STELPRO(0x1185),
    LYNXUS_TECHNOLOGIES_CORP(0x1186),
    SEMICONDUCTOR_COMPONENTS(0x1187),
    TP_LINK(0x1188),
    LEDVANCE_GMBH(0x1189),
    NORTEK(0x118A),
    IREVO_ASSA_ABBLOY_KOREA(0x118B),
    MIDEA(0x118C),
    ZF_FRIEDRICHSHAFEN(0x118D),
    CHECKIT(0x118E),
    ACLARA(0x118F),
    NOKIA(0x1190),
    GOLDCARD_HIGH_TECH_CO(0x1191),
    GEORGE_WILSON_INDUSTRIES_LTD(0x1192),
    EASY_SAVER_CO_INC(0x1193),
    ZTE_CORPORATION(0x1194),
    ARRIS(0x1195),
    RELIANCE_BIG_TV(0x1196),
    INSIGHT_ENERGY_VENTURES_POWERLEY(0x1197),
    THOMAS_RESEARCH_PRODUCTS(0x1198),
    LI_SENG_TECHNOLOGY(0x1199),
    SYSTEM_LEVEL_SOLUTIONS_INC(0x119A),
    MATRIX_LABS(0x119B),
    SINOPE_TECHNOLOGIES(0x119C),
    JIUZHOU_GREEBLE(0x119D),
    GUANGZHOU_LANVEE_TECH_CO_LTD(0x119E),
    VENSTAR(0x119F),
    SLV(0x1200),
    HALO_SMART_LABS(0x1201),
    SCOUT_SECURITY_INC(0x1202),
    ALIBABA_CHINA_INC(0x1203),
    RESOLUTION_PRODUCTS_INC(0x1204),
    SMARTLOK_INC(0x1205),
    LUX_PRODUCTS_CORP(0x1206),
    VIMAR_SPA(0x1207),
    UNIVERSAL_LIGHTING_TECHNOLOGIES(0x1208),
    ROBERT_BOSCH_GMBH(0x1209),
    ACCENTURE(0x120A),
    HEIMAN_TECHNOLOGY_CO(0x120B),
    SHENZHEN_HOMA_TECHNOLOGY_CO(0x120C),
    VISION_ELECTRONICS_TECHNOLOGY(0x120D),
    LENOVO(0x120E),
    PRESCIENSE_RD(0x120F),
    SHENZHEN_SEASTAR_INTELLIGENCE_CO(0x1210),
    SENSATIVE_AB(0x1211),
    SOLAREDGE(0x1212),
    ZIPATO(0x1213),
    CHINA_FIRE_SECURITY_SENSING_MANUFACTURING(0x1214),
    QUBY_BV(0x1215),
    HANGZHOU_ROOMBANKER_TECHNOLOGY_CO(0x1216),
    AMAZON_LAB126(0x1217),
    PAULMANN_LICHT_GMBH(0x1218),
    SHENZHEN_ORVIBO_ELECTRONICS_CO_LTD(0x1219),
    TCI_TELECOMMUNICATIONS(0x121A),
    MUELLER_LICHT_INTERNATIONAL_INC(0x121B),
    AURORA_LIMITED(0x121C),
    SMARTDCC(0x121D),
    SHANGHAI_UMEINFO_CO_LTD(0x121E),
    CARBONTRACK(0x121F),
    SOMFY(0x1220),
    VIESSMANN_ELEKTRONIK_GMBH(0x1221),
    HILDEBRAND_TECHNOLOGY_LTD(0x1222),
    ONKYO_TECHNOLOGY_CORPORATION(0x1223),
    SHENZHEN_SUNRICHER_TECHNOLOGY_LTD(0x1224),
    XIU_XIU_TECHNOLOGY_CO_LTD(0x1225),
    ZUMTOBEL_GROUP(0x1226),
    SHENZHEN_KAADAS_INTELLIGENT_TECHNOLOGY_CO_LTD(0x1227),
    SHANGHAI_XIAOYAN_TECHNOLOGY_CO_LTD(0x1228),
    CYPRESS_SEMICONDUCTOR(0x1229),
    XAL_GMBH(0x122A),
    INERGY_SYSTEMS_LLC(0x122B),
    ALFRED_KARCHER_GMBH_CO_KG(0x122C),
    ADUROLIGHT_MANUFACTURING(0x122D),
    GROUPE_MULLER(0x122E),
    V_MARK_ENTERPRISES_INC(0x122F),
    LEAD_ENERGY_AG(0x1230),
    ULTIMATE_IOT_HENAN_TECHNOLOGY_LTD(0x1231),
    AXXESS_INDUSTRIES_INC(0x1232),
    THIRD_REALITY_INC(0x1233),
    DSR_CORPORATION(0x1234),    
    GUANGZHOU_VENSI_INTELLIGENT_TECHNOLOGY(0x1235),
    SCHLAGE_LOCK_ALLEGION(0x1236),
    NET2GRID(0x1237),
    AIRAM_ELECTRIC_OY_AB(0x1238),
    IMMAX_WPB_CZ(0x1239),
    ZIV_AUTOMATION(0x123A),
    HANGZHOU_IMAGICTECHNOLOGY(0x123B),
    XIAMEN_LEELEN_TECHNOLOGY(0x123C),
    OVERKIZ_SAS(0x123D),
    FLONIDAN_A_S(0x123E),
    HDL_AUTOMATION(0x123F),
    ARDOMUS_NETWORKS_CORPORATION(0x1240),
    SAMJIN_CO_LTD(0x1241),
    FIREANGEL_SAFETY_TECHNOLOGY(0x1242),
    INDRA_SISTEMAS_SA(0x1243),
    SHENZHEN_JBT_SMART_LIGHTING(0x1244),
    GE_LIGHTING_CURRENT(0x1245),
    DANFOSS_A_S(0x1246),
    NIVISS_PHP_SP_Z_O_O_SP_K(0x1247),
    SHENZHEN_FENGLIYUAN_ENERGY_CONSERVATING_TECHNOLOGY(0x1248),
    NEXELEC(0x1249),
    SICHUAN_BEHOME_PROMINENT_TECHNOLOGY(0x124A),
    FUJIAN_STAR_NET_COMMUNICATION(0x124B),
    TOSHIBA_VISUAL_SOLUTIONS_CORPORATION(0x124C),
    LATCHABLE_INC(0x124D),
    L_S_DEUTSCHLAND_GMBH(0x124E),
    GLEDOPTO_CO_LTD(0x124F),
    THE_HOME_DEPOT(0x1250),
    NEONLITE_DISTRIBUTION_LIMITED(0x1251),
    ARLO_TECHNOLOGIES_INC(0x1252),
    XINGLUO_TECHNOLOGY_CO_LTD(0x1253),
    SIMON_ELECTRIC_CHINA_CO_LTD(0x1254),
    HANGZHOU_GREATSTAR_INDUSTRIAL_CO_LTD(0x1255),
    SEQUENTRIC_ENERGY_SYSTEMS_LLC(0x1256),
    SOLUM_CO_LTD(0x1257),
    EAGLERISE_ELECTRIC_ELECTRONIC_CHINA_CO_LTD(0x1258),
    FANTEM_TECHNOLOGIES_SHENZHEN_CO_LTD(0x1259),
    YUNDING_NETWORK_TECHNOLOGY_BEIJING_CO_LTD(0x125A),
    ATLANTIC_GROUP(0x125B),
    XIAMEN_INTRETECH_INC(0x125C),
    TUYA_GLOBAL_INC(0x125D),
    DNAKE_XIAMEN_INTELLIGENT_TECHNOLOGY(0x125E),
    NIKO_NV(0x125F),
    EMPORIA_ENERGY(0x1260),
    SIKOM_AS(0x1261),
    AXIS_LABS_INC(0x1262),
    CURRENT_PRODUCTS_CORPORATION(0x1263),
    METERSIT_SRL(0x1264),
    HORNBACH_BAUMARKT_AG(0x1265),
    DICEWORLD_S_R_L_A_SOCIO_UNICO(0x1266),
    ARC_TECHNOLOGY_CO_LTD(0x1267),
    HANGZHOU_KONKE_INFORMATION_TECHNOLOGY(0x1268),
    SALTO_SYSTEMS_SL(0x1269),
    SHENZHEN_SHYUGJ_TECHNOLOGY(0x126A),
    BRAYDEN_AUTOMATION_CORPORATION(0x126B),
    ENVIRONEXUS_PTY_LTD(0x126C),
    ELTRA_NV_SA(0x126D),
    XIAOMI_COMMUNICATIONS_CO_LTD(0x126E),
    SHANGHAI_SHUNCOM_ELECTRONIC_TECHNOLOGY(0x126F),
    VOLTALIS_SA(0x1270),
    FEELUX_CO_LTD(0x1271),
    SMARTPLUS_INC(0x1272),
    HALEMEIER_GMBH(0x1273),
    TRUST_INTERNATIONAL_BV(0x1274),
    DUKE_ENERGY_BUSINESS_SERVICES_LLC(0x1275),
    CALIX_INC(0x1276),
    ADEO(0x1277),
    CONNECTED_RESPONSE_LIMITED(0x1278),
    STROYENERGOKOM_LTD(0x1279),
    LUMITECH_LIGHTING_SOLUTION_GMBH(0x127A),
    VERDANT_ENVIRONMENTAL_TECHNOLOGIES(0x127B),
    ALFRED_INTERNATIONAL_INC(0x127C),
    ANSI_LED_LIGHTING_CO_LTD(0x127D),
    MINDTREE_LIMITED(0x127E),
    NORDIC_SEMICONDUCTOR_ASA(0x127F),
    SITERWELL_ELECTRONICS_CO_LIMITED(0x1280),
    BRILONER_LEUCHTEN_GMBH(0x1281),
    SHENZHEN_SEI_TECHNOLOGY_CO_LTD(0x1282),
    COPPER_LABS_INC(0x1283),
    DELTA_DORE(0x1284),
    HAGER_GROUP(0x1285),
    SHENZHEN_COOLKIT_TECHNOLOGY_CO_LTD(0x1286),
    HANGZHOU_SKY_LIGHTING_CO_LTD(0x1287),
    E_ON_SE(0x1288),
    LIDL_STIFTUNG_CO_KG(0x1289),
    SICHUAN_CHANGHONG_NETWORK_TECHNOLOGIES(0x128A),
    NODON(0x128B),
    JIANGXI_INNOTECH_TECHNOLOGY_CO_LTD(0x128C),
    MERCATOR_PTY_LTD(0x128D),
    BEIJING_RUYING_TECH_LIMITED(0x128E),
    EGLO_LEUCHTEN_GMBH(0x128F),
    PIETRO_FIORENTINI_SPA(0x1290),
    ZEHNDER_GROUP_VAUX_ANDIGNY(0x1291),
    BRK_BRANDS_INC(0x1292),
    ASKEY_COMPUTER_CORP(0x1293),
    PASSIVEBOLT_INC(0x1294),
    AVM_AUDIOVISUELLES_MARKETING_UND_COMPUTERSYSTEME_BERLIN(0x1295),
    NINGBO_SUNTECH_LIGHTING_TECHNOLOGY_CO_LTD(0x1296),
    SOCIETE_EN_COMMANDITE_STELLO(0x1297),
    VIVINT_SMART_HOME(0x1298),
    NAMRON_AS(0x1299),
    RADEMACHER_GERATE_ELEKTRONIK_GMBH(0x129A),
    OMO_SYSTEMS_LTD(0x129B),
    SIGLIS_AG(0x129C),
    IMHOTEP_CREATION(0x129D),
    ICASA(0x129E),
    LEVEL_HOME_INC(0x129F),
    TIS_CONTROL_LIMITED(0x1300),
    RADISYS_INDIA_PVT_LTD(0x1301),
    VEEA_INC(0x1302),
    FELL_TECHNOLOGY_AS(0x1303),
    SOWILO_DESIGN_SERVICES_LTD(0x1304),
    LEXI_DEVICES_INC(0x1305),
    LIFI_LABS_INC_DBA_LIFX(0x1306),
    GRUNDFOS_HOLDING_AS(0x1307),
    SOURCING_AND_CREATION(0x1308),
    KRAKEN_TECHNOLOGIES_LTD(0x1309),
    EVE_SYSTEMS(0x130A),
    LITE_ON_TECHNOLOGY_CORPORATION(0x130B),
    FOCALCREST_LIMITED(0x130C),
    BOUFFALO_LAB_NANJING_CO_LTD(0x130D),
    WYZE_LABS_INC(0x130E),
    Z_WAVE_EUROPE_GMBH(0x130F),
    AEOTEC_LIMITED(0x1310),
    NGSTB_COMPANY_LIMITED(0x1311),
    QINGDAO_YEELINK_INFORMATION_TECHNOLOGY_CO_LTD(0x1312),
    E_SMART_HOME_AUTOMATION_SYSTEMS_LIMITED(0x1313),
    FIBAR_GROUP_SA(0x1314),
    PROLITECH_GMBH(0x1315),
    PANKORE_INTEGRATED_CIRCUIT_TECHNOLOGY_CO_LTD(0x1316),
    LOGITECH(0x1317),
    PIARO_INC(0x1318),
    MITSUBISHI_ELECTRIC_US_INC(0x1319),
    RESIDEO_TECHNOLOGIES_INC(0x131A),
    ESPRESSIF_SYSTEMS_SHANGHAI_CO_LTD(0x131B),
    HELLA_SONNEN_UND_WETTERSCHUTZTECHNIK_GMBH(0x131C),
    GEBERIT_INTERNATIONAL_AG(0x131D),
    CAME_SPA(0x131E),
    GUANGZHOU_ELITE_EDUCATION_AND_TECHNOLOGY_CO_LTD(0x131F),
    PHYPLUS_MICROELECTRONICS_LIMITED(0x1320),
    SHENZHEN_SONOFF_TECHNOLOGIES_CO_LTD(0x1321),
    SAFE4_SECURITY_GROUP(0x1322),
    SHANGHAI_MXCHIP_INFORMATION_TECHNOLOGY_CO_LTD(0x1323),
    HDC_I_CONTROLS(0x1324),
    ZUMA_ARRAY_LIMITED(0x1325),
    DECELECT(0x1326),
    MILL_INTERNATIONAL_AS(0x1327),
    HOMEWIZARD_BV(0x1328),
    SHENZHEN_TOPBAND_CO(0x1329),
    PRESSAC_COMMUNICATIONS_LTD(0x132A),
    ORIGIN_WIRELESS_INC(0x132B),
    CONNECTE_AS(0x132C),
    YOKIS(0x132D),
    XIAMEN_YANKON_ENERGETIC_LIGHTING_CO(0x132E),
    YANDEX_LLC(0x132F),
    CRITICAL_SOFTWARE_SA(0x1330),
    NORTEK_CONTROL(0x1331),
    BRIGHTAI(0x1332),
    BECKER_ANTRIEBE_GMBH(0x1333),
    SHENZHEN_TCL_NEW_TECHNOLOGY_COMPANY_LIMITED(0x1334),
    DEXATEK_TECHNOLOGY_LTD(0x1335),
    ELELABS_INTERNATIONAL_LIMITED(0x1336),
    DATEK_WIRELESS_AS(0x1337),
    ALDES(0x1338),
    SAVANT_COMPANY(0x1339),
    ARISTON_THERMO_GROUP(0x133A),
    WAREMA_RENKHOFF_SE(0x133B),
    VTECH_HOLDINGS_LIMITED(0x133C),
    FUTUREHOME_AS(0x133D),
    COGNITIVE_SYSTEMS_CORP(0x133E),
    ASR_MICROELECTRONICS_SHENZHEN_CO(0x133F),
    AIRIOS(0x1340),
    GUANGDONG_OPPO_MOBILE_TELECOMMUNICATIONS_CORP(0x1341),
    BEKEN_CORPORATION(0x1342),
    CORSAIR(0x1343),
    ELTAKO_GMBH(0x1344),
    CHENGDU_MEROSS_TECHNOLOGY_CO(0x1345),
    RAFAEL_MICROELECTRONICS_INC(0x1346),
    AUG_WINKHUAS_GMBH_AND_CO_KG(0x1347),
    QINGDAO_HAIER_TECHNOLOGY_CO(0x1348),
    APPLE_INC(0x1349),
    ROLLEASE_ACME(0x134A),
    NABU_CASA_INC(0x134B),
    SIMON_HOLDING(0x134C),
    KD_NAVIEN(0x134D),
    TADO_GMBH(0x134E),
    MEDIOLA_CONNECTED_LIVING_AG(0x134F),
    POLYNHOME(0x1350),
    HOORII_TECHNOLOGY_CO(0x1351),
    KIMIN_ELECTRONICS_CO(0x1353),
    ZYAX_AB(0x1354),
    BARACODA_SA(0x1355),
    LENNOX_INTERNATIONAL_INC(0x1356),
    TELEDACTICS_INCORPORATED(0x1357),
    TOP_VICTORY_INVESTMENTS_LIMITED(0x1358),
    GOQUAL_INC(0x1359),
    SIEGENIA_AUBI_KG(0x135A),
    VIRTUAL_CONNECTED_CONTROLLING_SYSTEM_SINGAPORE_PTE_LTD(0x135B),
    GIGASET_COMMUNICATIONS_GMBH(0x135C),
    NUKI_HOME_SOLUTIONS_GMBH(0x135D),
    DEVICEBOOK_INC(0x135E),
    CONSUMER_2_INC_RENTLY(0x135F),
    EDISON_LABS_INC_ORRO(0x1360),
    INOVELLI(0x1361),
    DEVERITEC_GMBH(0x1362),
    CHARTER_COMMUNICATIONS(0x1363),
    MONOLITHIC_POWER_SYSTEMS_INC(0x1364),
    NINGBO_DOOYA_MECHANIC_AND_ELECTRONIC_TECHNOLOGY_CO(0x1365),
    SHENZHEN_SDMC_TECHNOLOGY_CO(0x1366),
    HP_INC(0x1367),
    MUI_LAB_INC(0x1368),
    BHTRONICS_SRL(0x1369),
    AKUVOX_XIAMEN_NETWORKS_CO(0x136A),
    GEWISS_SPA(0x1994),
    CLIMAX_TECHNOLOGY_CO(0x2794),
    GOOGLE_LLC(0x6006),
    CONNECTIVITY_STANDARDS_ALLIANCE_1(0xC5A0),
    CONNECTIVITY_STANDARDS_ALLIANCE_2(0xC5A1),
    CONNECTIVITY_STANDARDS_ALLIANCE_3(0xC5A2),
    CONNECTIVITY_STANDARDS_ALLIANCE_4(0xC5A3),
    CONNECTIVITY_STANDARDS_ALLIANCE_5(0xC5A4),
    CONNECTIVITY_STANDARDS_ALLIANCE_6(0xC5A5),
    CONNECTIVITY_STANDARDS_ALLIANCE_7(0xC5A6),
    CONNECTIVITY_STANDARDS_ALLIANCE_8(0xC5A7),
    CONNECTIVITY_STANDARDS_ALLIANCE_9(0xC5A8),
    CONNECTIVITY_STANDARDS_ALLIANCE_10(0xC5A9),
    CONNECTIVITY_STANDARDS_ALLIANCE_11(0xC5AA),
    CONNECTIVITY_STANDARDS_ALLIANCE_12(0xC5AB),
    CONNECTIVITY_STANDARDS_ALLIANCE_13(0xC5AC),
    CONNECTIVITY_STANDARDS_ALLIANCE_14(0xC5AD),
    CONNECTIVITY_STANDARDS_ALLIANCE_15(0xC5AE),
    CONNECTIVITY_STANDARDS_ALLIANCE_16(0xC5AF),     
    TESTVENDOR1(0xFFF1),
    TESTVENDOR2(0xFFF2),
    TESTVENDOR3(0xFFF3),
    TESTVENDOR4(0xFFF4),
  }

  init {
    optionalQRCodeInfo = HashMap()
  }

  constructor(
    version: Int,
    vendorId: Int,
    productId: Int,
    commissioningFlow: Int,
    discoveryCapabilities: Set<DiscoveryCapability>,
    discriminator: Int,
    setupPinCode: Long
  ) : this(
    version,
    vendorId,
    productId,
    commissioningFlow,
    discoveryCapabilities,
    discriminator,
    false,
    setupPinCode
  )

  fun addOptionalQRCodeInfo(info: OptionalQRCodeInfo) {
    optionalQRCodeInfo[info.tag] = info
  }

  fun isValidManualCode(): Boolean {
    if (setupPinCode >= (1 shl kSetupPINCodeFieldLengthInBits)) {
      return false
    }

    return checkPayloadCommonConstraints()
  }

  fun getShortDiscriminatorValue(): Int {
    if (hasShortDiscriminator) {
      return discriminator
    }
    return longToShortValue(discriminator)
  }

  fun getLongDiscriminatorValue(): Int {
    if (hasShortDiscriminator) {
      return shortToLongValue(discriminator)
    }
    return discriminator
  }

  private fun checkPayloadCommonConstraints(): Boolean {
    if (version != 0) {
      return false
    }

    if (!isValidSetupPIN(setupPinCode.toInt())) {
      return false
    }

    if (!isVendorIdValidOperationally(vendorId) && vendorId != VendorId.UNSPECIFIED.value) {
      return false
    }

    if (productId == 0 && vendorId != VendorId.UNSPECIFIED.value) {
      return false
    }

    return true
  }

  private fun isVendorIdValidOperationally(vendorId: Int): Boolean {
    return vendorId != VendorId.UNSPECIFIED.value && vendorId <= VendorId.TESTVENDOR4.value
  }     

  companion object {
    private fun isValidSetupPIN(setupPIN: Int): Boolean {
      return (setupPIN != kSetupPINCodeUndefinedValue && setupPIN <= kSetupPINCodeMaximumValue &&
              setupPIN != 11111111 && setupPIN != 22222222 && setupPIN != 33333333 &&
              setupPIN != 44444444 && setupPIN != 55555555 && setupPIN != 66666666 &&
              setupPIN != 77777777 && setupPIN != 88888888 && setupPIN != 12345678 &&
              setupPIN != 87654321)
    }

    private fun longToShortValue(longValue: Int): Int {
      return (longValue shr (kDiscriminatorLongBits - kDiscriminatorShortBits))
    }

    private fun shortToLongValue(shortValue: Int): Int {
      return (shortValue shl (kDiscriminatorLongBits - kDiscriminatorShortBits))
    }
  }  
}

class UnrecognizedQrCodeException(qrCode: String) :
  Exception(String.format("Invalid QR code string: %s", qrCode), null) {
  companion object {
    private const val serialVersionUID = 1L
  }
}

class InvalidManualPairingCodeFormatException(entryCode: String) :
  Exception(String.format("Invalid format for entry code string: %s", entryCode), null) {
  companion object {
    private const val serialVersionUID = 1L
  }
}

class OnboardingPayloadException(message: String) :
  Exception(String.format("Failed to encode Onboarding payload to buffer: %s", message)) {
  companion object {
    private const val serialVersionUID = 1L
  }
}
