package com.zodiactap.mapper.data.db.typeconverter

import androidx.room.TypeConverter
import com.zodiactap.mapper.common.utils.NodeInteractionType

class NodeInteractionTypeSetTypeConverter {
    @TypeConverter
    fun toSet(mask: Int): Set<NodeInteractionType> {
        val interactionTypeSet = mutableSetOf<NodeInteractionType>()

        for (type in NodeInteractionType.entries) {
            if (mask and type.accessibilityActionId == type.accessibilityActionId) {
                interactionTypeSet.add(type)
            }
        }

        return interactionTypeSet
    }

    @TypeConverter
    fun toMask(set: Set<NodeInteractionType>): Int {
        var nodeActionMask = 0

        for (nodeAction in set) {
            nodeActionMask = nodeActionMask or nodeAction.accessibilityActionId
        }

        return nodeActionMask
    }
}
