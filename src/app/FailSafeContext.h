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

/**
 *    @file
 *          A 'Fail Safe Context' SHALL be created on the receiver, to track fail-safe
 *          state information while the fail-safe is armed.
 */

#pragma once

#include <lib/core/CHIPError.h>
#include <lib/core/DataModelTypes.h>
#include <platform/internal/CHIPDeviceLayerInternal.h>
#include <system/SystemClock.h>

namespace chip {
namespace app {

class FailSafeContext
{
public:
    // ===== Members for internal use by other Device Layer components.

    /**
     * @brief
     *  Only a single fail-safe timer is started on the device, if this function is called again
     *  when the fail-safe timer is currently armed, the currently-running fail-safe timer will
     *  first be cancelled, then the fail-safe timer will be re-armed.
     */
    CHIP_ERROR ArmFailSafe(FabricIndex accessingFabricIndex, System::Clock::Seconds16 expiryLengthSeconds);

    /**
     * @brief Cleanly disarm failsafe timer, such as on CommissioningComplete
     */
    void DisarmFailSafe();
    void SetAddNocCommandInvoked(FabricIndex nocFabricIndex)
    {
        mAddNocCommandHasBeenInvoked = true;
        mFabricIndex                 = nocFabricIndex;
    }
    void SetUpdateNocCommandInvoked() { mUpdateNocCommandHasBeenInvoked = true; }
    void SetAddTrustedRootCertInvoked() { mAddTrustedRootCertHasBeenInvoked = true; }
    void SetCsrRequestForUpdateNoc(bool isForUpdateNoc) { mIsCsrRequestForUpdateNoc = isForUpdateNoc; }
    void SetUpdateTermsAndConditionsHasBeenInvoked() { mUpdateTermsAndConditionsHasBeenInvoked = true; }
    void RecordSetVidVerificationStatementHasBeenInvoked() { mSetVidVerificationStatementHasBeenInvoked = true; }
#if CHIP_DEVICE_CONFIG_ENABLE_JOINT_FABRIC
    void SetAddICACHasBeenInvoked() { mAddICACHasBeenInvoked = true; }
#endif

    /**
     * @brief
     *   Schedules a work to cleanup the FailSafe Context asynchronously after various cleanup work
     *   has completed.
     */
    void ScheduleFailSafeCleanup(FabricIndex fabricIndex, bool addNocCommandInvoked, bool updateNocCommandInvoked);

    bool IsFailSafeArmed(FabricIndex accessingFabricIndex) const
    {
        return IsFailSafeArmed() && MatchesFabricIndex(accessingFabricIndex);
    }

    // Returns true if the fail-safe is in a state where commands that require an armed
    // fail-safe can no longer execute, but a new fail-safe can't be armed yet.
    bool IsFailSafeBusy() const { return mFailSafeBusy; }

    bool IsFailSafeArmed() const { return mFailSafeArmed; }

    // True if it is possible to do an initial arming of the failsafe if needed.
    // To be used in places where some action should take place only if the
    // fail-safe could be armed after that action.
    bool IsFailSafeFullyDisarmed() const { return !IsFailSafeArmed() && !IsFailSafeBusy(); }

    bool MatchesFabricIndex(FabricIndex accessingFabricIndex) const
    {
        VerifyOrDie(IsFailSafeArmed());
        return (accessingFabricIndex == mFabricIndex);
    }

    bool NocCommandHasBeenInvoked() const { return mAddNocCommandHasBeenInvoked || mUpdateNocCommandHasBeenInvoked; }
    bool AddNocCommandHasBeenInvoked() const { return mAddNocCommandHasBeenInvoked; }
    bool UpdateNocCommandHasBeenInvoked() const { return mUpdateNocCommandHasBeenInvoked; }
    bool AddTrustedRootCertHasBeenInvoked() const { return mAddTrustedRootCertHasBeenInvoked; }
    bool IsCsrRequestForUpdateNoc() const { return mIsCsrRequestForUpdateNoc; }
    bool UpdateTermsAndConditionsHasBeenInvoked() const { return mUpdateTermsAndConditionsHasBeenInvoked; }
    bool HasSetVidVerificationStatementHasBeenInvoked() const { return mSetVidVerificationStatementHasBeenInvoked; }
#if CHIP_DEVICE_CONFIG_ENABLE_JOINT_FABRIC
    bool AddICACCommandHasBeenInvoked() const { return mAddICACHasBeenInvoked; }
#endif

    FabricIndex GetFabricIndex() const
    {
        VerifyOrDie(IsFailSafeArmed());
        return mFabricIndex;
    }

    // Immediately disarms the timer and schedules a failsafe timer expiry.
    // If the failsafe is not armed, this is a no-op.
    void ForceFailSafeTimerExpiry();

private:
    bool mFailSafeArmed                    = false;
    bool mFailSafeBusy                     = false;
    bool mAddNocCommandHasBeenInvoked      = false;
    bool mUpdateNocCommandHasBeenInvoked   = false;
    bool mAddTrustedRootCertHasBeenInvoked = false;
    // The fact of whether a CSR occurred at all is stored elsewhere.
    bool mIsCsrRequestForUpdateNoc                  = false;
    FabricIndex mFabricIndex                        = kUndefinedFabricIndex;
    bool mUpdateTermsAndConditionsHasBeenInvoked    = false;
    bool mSetVidVerificationStatementHasBeenInvoked = false;
#if CHIP_DEVICE_CONFIG_ENABLE_JOINT_FABRIC
    bool mAddICACHasBeenInvoked = false;
#endif

    /**
     * @brief
     *  The callback function to be called when "fail-safe timer" expires.
     */
    static void HandleArmFailSafeTimer(System::Layer * layer, void * aAppState);

    /**
     * @brief
     *  The callback function to be called when max cumulative time expires.
     */
    static void HandleMaxCumulativeFailSafeTimer(System::Layer * layer, void * aAppState);

    /**
     * @brief
     *  The callback function to be called asynchronously after various cleanup work has completed
     *  to actually disarm the fail-safe.
     */
    static void HandleDisarmFailSafe(intptr_t arg);

    void SetFailSafeArmed(bool armed);

    /**
     * @brief Reset to unarmed basic state
     */
    void ResetState()
    {
        SetFailSafeArmed(false);

        mAddNocCommandHasBeenInvoked               = false;
        mUpdateNocCommandHasBeenInvoked            = false;
        mAddTrustedRootCertHasBeenInvoked          = false;
        mFailSafeBusy                              = false;
        mIsCsrRequestForUpdateNoc                  = false;
        mUpdateTermsAndConditionsHasBeenInvoked    = false;
        mSetVidVerificationStatementHasBeenInvoked = false;
#if CHIP_DEVICE_CONFIG_ENABLE_JOINT_FABRIC
        mAddICACHasBeenInvoked = false;
#endif
    }

    void FailSafeTimerExpired();
};

} // namespace app
} // namespace chip
