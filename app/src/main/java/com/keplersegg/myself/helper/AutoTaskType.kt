package com.keplersegg.myself.helper

enum class AutoTaskType(val typeId: Int) {
    CallDuration(1), AppUsage(2), WentTo(3);

    companion object {
        fun valueOf(value: Int) = values().find { it.typeId == value }
    }
}