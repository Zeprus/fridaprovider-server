package com.zeprus.fridaprovider.repository

import com.zeprus.fridaprovider.model.ApplicationInfo
import com.zeprus.fridaprovider.model.Script

public interface ApplicationRepository<Application> {
    public fun store(application: Application)
    public fun store(script: Script)
    public fun get(applicationInfo: ApplicationInfo): Application?
    public fun delete(applicationInfo: ApplicationInfo)
    public fun load(): List<Application>
    public fun getApplicationInfos(): List<ApplicationInfo>
    public fun getScripts(applicationInfo: ApplicationInfo): List<Script>
}