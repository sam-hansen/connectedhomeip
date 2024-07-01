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
package matter.controller.cluster.structs

import matter.controller.cluster.*
import matter.tlv.ContextSpecificTag
import matter.tlv.Tag
import matter.tlv.TlvReader
import matter.tlv.TlvWriter

class GroupKeyManagementClusterGroupKeyMapStruct(
  val groupId: UShort,
  val groupKeySetID: UShort,
  val fabricIndex: UByte,
) {
  override fun toString(): String = buildString {
    append("GroupKeyManagementClusterGroupKeyMapStruct {\n")
    append("\tgroupId : $groupId\n")
    append("\tgroupKeySetID : $groupKeySetID\n")
    append("\tfabricIndex : $fabricIndex\n")
    append("}\n")
  }

  fun toTlv(tlvTag: Tag, tlvWriter: TlvWriter) {
    tlvWriter.apply {
      startStructure(tlvTag)
      put(ContextSpecificTag(TAG_GROUP_ID), groupId)
      put(ContextSpecificTag(TAG_GROUP_KEY_SET_I_D), groupKeySetID)
      put(ContextSpecificTag(TAG_FABRIC_INDEX), fabricIndex)
      endStructure()
    }
  }

  companion object {
    private const val TAG_GROUP_ID = 1
    private const val TAG_GROUP_KEY_SET_I_D = 2
    private const val TAG_FABRIC_INDEX = 254

    fun fromTlv(tlvTag: Tag, tlvReader: TlvReader): GroupKeyManagementClusterGroupKeyMapStruct {
      tlvReader.enterStructure(tlvTag)
      val groupId = tlvReader.getUShort(ContextSpecificTag(TAG_GROUP_ID))
      val groupKeySetID = tlvReader.getUShort(ContextSpecificTag(TAG_GROUP_KEY_SET_I_D))
      val fabricIndex = tlvReader.getUByte(ContextSpecificTag(TAG_FABRIC_INDEX))

      tlvReader.exitContainer()

      return GroupKeyManagementClusterGroupKeyMapStruct(groupId, groupKeySetID, fabricIndex)
    }
  }
}
