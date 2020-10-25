package com.keplersegg.myself.Room.Entity


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Task {

    @PrimaryKey
    var Id: Int = 0
    var Label: String = ""
    var DataType: Int = 0
    var Unit: String = ""
    var AutomationType: Int? = null
    var AutomationVar: String? = null
    var Status: Int = 1

    companion object {

        fun CreateItem(id: Int, label: String, dataType: Int, unit: String,
                       automationType: Int?, automationVar: String?): Task {

            val m = Task()

            m.Id = id
            m.Label = label
            m.DataType = dataType
            m.Unit = unit
            m.AutomationType = automationType
            m.AutomationVar = automationVar
            m.Status = 1

            return m
        }
    }
}
