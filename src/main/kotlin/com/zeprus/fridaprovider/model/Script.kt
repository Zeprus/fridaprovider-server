package com.zeprus.fridaprovider.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
class Script(val packageName: String, val name: String, val content: String) {
    companion object {
        fun fromJson(json: String): Script {
            return Json.decodeFromString(json)
        }

        fun fromFile(file: File): Script {
            return fromJson(file.readText())
        }
    }
}