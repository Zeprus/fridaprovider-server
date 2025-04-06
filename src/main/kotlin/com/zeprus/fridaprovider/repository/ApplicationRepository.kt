package com.zeprus.fridaprovider.repository

import com.zeprus.fridaprovider.model.ApplicationInfo
import com.zeprus.fridaprovider.model.Script

public interface ApplicationRepository<Application> {
    public fun store(application: Application)
    public fun store(script: Script)
    public fun get(applicationInfo: ApplicationInfo): Application?
    public fun delete(applicationInfo: ApplicationInfo)
    public fun delete(script: Script)
    public fun load(): MutableList<Application>
    public fun getApplications(): List<Application>
    public fun getApplicationInfos(): List<ApplicationInfo>
    public fun getApplicationInfo(packageName: String): ApplicationInfo?
    public fun getScripts(): List<Script>
    public fun getScripts(applicationInfo: ApplicationInfo): List<Script>
    public fun getScript(applicationInfo: ApplicationInfo, scriptName: String): Script?
}