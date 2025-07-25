/*
 *
 *    Copyright (c) 2022 Project CHIP Authors
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
#pragma once

#include <credentials/CHIPCert.h>
#include <credentials/DeviceAttestationCredsProvider.h>
#include <lib/core/CHIPError.h>
#include <platform/CommissionableDataProvider.h>
#include <platform/internal/GenericDeviceInstanceInfoProvider.h>

#if CONFIG_CHIP_OTA_FACTORY_DATA_PROCESSOR
#include <lib/support/DefaultStorageKeyAllocator.h>
#endif

#include <platform/nxp/common/factory_data/legacy/FactoryDataDriver.h>

#include <vector>

#include CHIP_PLATFORM_CONFIG_INCLUDE

namespace chip {
namespace DeviceLayer {

#define CHIP_FACTORY_DATA_ERROR(e)                                                                                                 \
    CHIP_GENERIC_ERROR(ChipError::Range::kLastRange, ((uint8_t) ChipError::Range::kLastRange << 2) | e)

#define CHIP_FACTORY_DATA_SHA_CHECK CHIP_FACTORY_DATA_ERROR(0x01)
#define CHIP_FACTORY_DATA_HEADER_READ CHIP_FACTORY_DATA_ERROR(0x02)
#define CHIP_FACTORY_DATA_HASH_ID CHIP_FACTORY_DATA_ERROR(0x03)
#define CHIP_FACTORY_DATA_PDM_RESTORE CHIP_FACTORY_DATA_ERROR(0x04)
#define CHIP_FACTORY_DATA_NULL CHIP_FACTORY_DATA_ERROR(0x05)
#define CHIP_FACTORY_DATA_FLASH_ERASE CHIP_FACTORY_DATA_ERROR(0x06)
#define CHIP_FACTORY_DATA_FLASH_PROGRAM CHIP_FACTORY_DATA_ERROR(0x07)
#define CHIP_FACTORY_DATA_INTERNAL_FLASH_READ CHIP_FACTORY_DATA_ERROR(0x08)
#define CHIP_FACTORY_DATA_PDM_SAVE_RECORD CHIP_FACTORY_DATA_ERROR(0x09)
#define CHIP_FACTORY_DATA_PDM_READ_RECORD CHIP_FACTORY_DATA_ERROR(0x0A)
#define CHIP_FACTORY_DATA_RESTORE_MECHANISM CHIP_FACTORY_DATA_ERROR(0x0B)

// Forward declaration to define the getter for factory data provider impl instance
class FactoryDataProviderImpl;

/**
 * @brief This class provides Commissionable data, Device Attestation Credentials,
 *        and Device Instance Info.
 */

class FactoryDataProvider : public DeviceInstanceInfoProvider,
                            public CommissionableDataProvider,
                            public Credentials::DeviceAttestationCredentialsProvider
{
public:
    struct Header
    {
        uint32_t hashId;
        uint32_t size;
        uint8_t hash[4];
    };
    enum KeyType
    {
        kHwKey  = 0U,
        kSftKey = 1U,
    };

    struct FactoryDataConfig
    {
        uint32_t start;
        uint32_t size;
        uint32_t payload;
    };

    enum EncryptionMode
    {
        encrypt_none = 0U,
        encrypt_ecb  = 1U,
        encrypt_cbc  = 2U
    };

    enum AESKeySize
    {
        aes_128 = 128u,
        aes_256 = 256U
    };

    // Default factory data IDs
    enum FactoryDataId
    {
        kVerifierId = 1,
        kSaltId,
        kIcId,
        kDacPrivateKeyId,
        kDacCertificateId,
        kPaiCertificateId,
        kDiscriminatorId,
        kSetupPasscodeId,
        kVidId,
        kPidId,
        kCertDeclarationId,
        kVendorNameId,
        kProductNameId,
        kSerialNumberId,
        kManufacturingDateId,
        kHardwareVersionId,
        kHardwareVersionStrId,
        kUniqueId,
        kPartNumber,
        kProductURL,
        kProductLabel,
        kProductFinish,
        kProductPrimaryColor,
        kEl2GoBlob,
        kEl2GoDacKeyId,
        kEl2GoDacCertId,

        kMaxId
    };

    static uint32_t kFactoryDataMaxSize;
    static constexpr uint32_t kLengthOffset = 1;
    static constexpr uint32_t kValueOffset  = 3;
    static constexpr uint32_t kHashLen      = 4;
    static constexpr size_t kHashId         = 0xCE47BA5E;

    virtual ~FactoryDataProvider();

    virtual CHIP_ERROR Init() = 0;
    virtual CHIP_ERROR SignWithDacKey(const ByteSpan & messageToSign, MutableByteSpan & outSignBuffer);
    virtual CHIP_ERROR Validate();

    virtual CHIP_ERROR SearchForId(uint8_t searchedType, uint8_t * pBuf, size_t bufLength, uint16_t & length,
                                   uint32_t * offset = nullptr);
    virtual CHIP_ERROR SetAesKey(const uint8_t * keyAes, AESKeySize keySize);
    virtual CHIP_ERROR SetEncryptionMode(EncryptionMode mode);
    virtual CHIP_ERROR EncryptFactoryData(uint8_t * FactoryDataBuff) { return CHIP_NO_ERROR; };
    virtual CHIP_ERROR DecryptFactoryData(uint8_t * FactoryDataBuff) { return CHIP_NO_ERROR; };
    virtual CHIP_ERROR SetKeyType(KeyType type) { return CHIP_NO_ERROR; };
    virtual CHIP_ERROR SetCbcInitialVector(const uint8_t * iv, uint16_t ivSize);

#if CONFIG_CHIP_OTA_FACTORY_DATA_PROCESSOR
    using RestoreMechanism = CHIP_ERROR (*)(void);

    CHIP_ERROR ValidateWithRestore();
    void RegisterRestoreMechanism(RestoreMechanism mechanism);

    virtual CHIP_ERROR PreResetCheck();
    virtual CHIP_ERROR PostResetCheck();

    StorageKeyName GetTargetVersionKey() { return StorageKeyName::FromConst("nxp/tgt-sw-ver"); }

    CHIP_ERROR GetTargetVersion(uint32_t & version);
    CHIP_ERROR SaveTargetVersion(uint32_t & version);
#endif

    // ===== Members functions that implement the CommissionableDataProvider
    CHIP_ERROR GetSetupDiscriminator(uint16_t & setupDiscriminator) override;
    CHIP_ERROR SetSetupDiscriminator(uint16_t setupDiscriminator) override;
    CHIP_ERROR GetSpake2pIterationCount(uint32_t & iterationCount) override;
    CHIP_ERROR GetSpake2pSalt(MutableByteSpan & saltBuf) override;
    CHIP_ERROR GetSpake2pVerifier(MutableByteSpan & verifierBuf, size_t & verifierLen) override;
    CHIP_ERROR GetSetupPasscode(uint32_t & setupPasscode) override;
    CHIP_ERROR SetSetupPasscode(uint32_t setupPasscode) override;

    // ===== Members functions that implement the DeviceAttestationCredentialsProvider
    CHIP_ERROR GetCertificationDeclaration(MutableByteSpan & outBuffer) override;
    CHIP_ERROR GetFirmwareInformation(MutableByteSpan & out_firmware_info_buffer) override;
    CHIP_ERROR GetDeviceAttestationCert(MutableByteSpan & outBuffer) override;
    CHIP_ERROR GetProductAttestationIntermediateCert(MutableByteSpan & outBuffer) override;
    CHIP_ERROR SignWithDeviceAttestationKey(const ByteSpan & messageToSign, MutableByteSpan & outSignBuffer) override;

    // ===== Members functions that implement the GenericDeviceInstanceInfoProvider
    CHIP_ERROR GetVendorName(char * buf, size_t bufSize) override;
    CHIP_ERROR GetVendorId(uint16_t & vendorId) override;
    CHIP_ERROR GetProductName(char * buf, size_t bufSize) override;
    CHIP_ERROR GetProductId(uint16_t & productId) override;
    CHIP_ERROR GetPartNumber(char * buf, size_t bufSize) override;
    CHIP_ERROR GetProductURL(char * buf, size_t bufSize) override;
    CHIP_ERROR GetProductLabel(char * buf, size_t bufSize) override;
    CHIP_ERROR GetHardwareVersionString(char * buf, size_t bufSize) override;
    CHIP_ERROR GetSerialNumber(char * buf, size_t bufSize) override;
    CHIP_ERROR GetManufacturingDate(uint16_t & year, uint8_t & month, uint8_t & day) override;
    CHIP_ERROR GetHardwareVersion(uint16_t & hardwareVersion) override;
    CHIP_ERROR GetRotatingDeviceIdUniqueId(MutableByteSpan & uniqueIdSpan) override;
    CHIP_ERROR GetProductFinish(app::Clusters::BasicInformation::ProductFinishEnum * finish) override;
    CHIP_ERROR GetProductPrimaryColor(app::Clusters::BasicInformation::ColorEnum * primaryColor) override;
    CHIP_ERROR GetSoftwareVersionString(char * buf, size_t bufSize) override;

protected:
    // Use when factory data are encrypted using aes key
    const uint8_t * pAesKey = nullptr;
    // AES key size in bit
    AESKeySize pAESKeySize;
    EncryptionMode encryptMode       = encrypt_ecb;
    const uint8_t * cbcInitialVector = nullptr;

    Header mHeader;
    FactoryDataConfig mConfig;
#if CONFIG_CHIP_OTA_FACTORY_DATA_PROCESSOR
    std::vector<RestoreMechanism> mRestoreMechanisms;
    FactoryDataDriver * mFactoryDataDriver = nullptr;
#endif
};

extern FactoryDataProvider & FactoryDataPrvdImpl();

} // namespace DeviceLayer
} // namespace chip
