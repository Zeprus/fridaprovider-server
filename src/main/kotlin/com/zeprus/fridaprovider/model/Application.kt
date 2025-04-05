package com.zeprus.fridaprovider.model

public class Application(val applicationInfo: ApplicationInfo) {
    val scripts: MutableList<Script> = mutableListOf()

    public fun addScript(script: Script): Application {
        scripts.add(script)
        return this
    }
}