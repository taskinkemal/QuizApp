package com.keplersegg.myself.helper

import com.google.gson.GsonBuilder
import com.keplersegg.myself.interfaces.IHttpProvider
import com.keplersegg.myself.Room.Entity.Entry
import com.keplersegg.myself.Room.Entity.Goal
import com.keplersegg.myself.Room.Entity.Task
import com.keplersegg.myself.models.UploadEntryResponse
import com.keplersegg.myself.models.UploadGoalResponse
import com.keplersegg.myself.models.User
import org.json.JSONObject

object ServiceMethods {

    fun uploadEntry(provider: IHttpProvider, entry: Entry) : UploadEntryResponse? {

        val jsonParams = JSONObject()
        jsonParams.put("Day", entry.Day)
        jsonParams.put("TaskId", entry.TaskId)
        jsonParams.put("Value", entry.Value)
        //jsonParams.put("ModificationDate", entry.ModificationDate)

        val result = HttpClient.send(provider, "entries", "post", jsonParams)

        if (result == null)
        {
            return null
        }

        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()

        return gson.fromJson<UploadEntryResponse>(result.toString(), UploadEntryResponse::class.java)
    }

    fun uploadTask(provider: IHttpProvider, task: Task): Int {

        val jsonParams = JSONObject()
        if (task.Id > 0)
            jsonParams.put("Id", task.Id)
        jsonParams.put("Label", task.Label)
        jsonParams.put("DataType", task.DataType)
        jsonParams.put("Unit", task.Unit)
        jsonParams.put("AutomationType", task.AutomationType)
        jsonParams.put("AutomationVar", task.AutomationVar)
        jsonParams.put("Status", task.Status)

        val result = HttpClient.send(provider, "tasks", "post", jsonParams)

        if (result != null && result.has("Value")) {

            return result.getInt("Value")
        }
        else {
            return -1
        }
    }

    fun deleteTask(provider: IHttpProvider, taskId: Int) {

        HttpClient.send(provider, "tasks/" + taskId, "delete", null)
    }

    fun getTasksFromService(provider: IHttpProvider): List<Task>? {

        val result = HttpClient.send(Array<Task>::class, provider, "tasks", "get", null)

        if (result.hasError()) return null

        return result.value!!.toList()
    }

    fun getEntriesFromService(provider: IHttpProvider): List<Entry>? {

        val end = Utils.getToday()
        val start = end - 5

        val result = HttpClient.send(Array<Entry>::class, provider, "entries?start=" + start + "&end=" + end, "get", null)

        if (result.hasError()) return null

        return result.value!!.toList()
    }

    fun getUser(provider: IHttpProvider): User? {

        return HttpClient.send(User::class, provider, "users", "get", null).value
    }

    fun deleteGoal(provider: IHttpProvider, goalId: Int) {

        HttpClient.send(provider, "goals/" + goalId, "delete", null)
    }

    fun uploadGoal(provider: IHttpProvider, goal: Goal): UploadGoalResponse? {

        val jsonParams = JSONObject()
        if (goal.Id > 0)
            jsonParams.put("Id", goal.Id)
        jsonParams.put("TaskId", goal.TaskId)
        jsonParams.put("MinMax", goal.MinMax)
        jsonParams.put("Target", goal.Target)
        jsonParams.put("StartDay", goal.StartDay)
        jsonParams.put("EndDay", goal.EndDay)
        jsonParams.put("AchievementStatus", 0)

        val result = HttpClient.send(provider, "goals", "post", jsonParams)

        if (result == null)
        {
            return null
        }

        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()

        return gson.fromJson<UploadGoalResponse>(result.toString(), UploadGoalResponse::class.java)
    }

    fun getGoalsFromService(provider: IHttpProvider): List<Goal>? {

        val result = HttpClient.send(Array<Goal>::class, provider, "goals", "get", null)

        if (result.hasError()) return null

        return result.value!!.toList()
    }
}