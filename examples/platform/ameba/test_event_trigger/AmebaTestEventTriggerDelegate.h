/*
 *
 *    Copyright (c) 2023 Project CHIP Authors
 *    All rights reserved.
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

#include <stdint.h>

#include <app/TestEventTriggerDelegate.h>
#include <lib/core/CHIPError.h>
#include <lib/support/CodeUtils.h>
#include <lib/support/Span.h>

/**
 * @brief User handler for handling the test event trigger
 *
 * @note If TestEventTrigger is enabled, it needs to be implemented in the app
 *
 * @param eventTrigger Event trigger to handle
 *
 * @warning *** DO NOT USE FOR STANDARD CLUSTER EVENT TRIGGERS ***
 *
 * @retval true on success
 * @retval false if error happened
 */
bool AmebaHandleGlobalTestEventTrigger(uint64_t eventTrigger);

namespace chip {

class AmebaTestEventTriggerDelegate : public TestEventTriggerDelegate, TestEventTriggerHandler
{
public:
    explicit AmebaTestEventTriggerDelegate(const ByteSpan & enableKey) : mEnableKey(enableKey)
    {
        VerifyOrDie(AddHandler(this) == CHIP_NO_ERROR);
    }

    /**
     * @brief Checks to see if `enableKey` provided matches value chosen by the manufacturer.
     * @param enableKey Buffer of the key to verify.
     * @return True or False.
     */
    bool DoesEnableKeyMatch(const ByteSpan & enableKey) const override;

    CHIP_ERROR HandleEventTrigger(uint64_t eventTrigger) override;

private:
    ByteSpan mEnableKey;
};

} // namespace chip
