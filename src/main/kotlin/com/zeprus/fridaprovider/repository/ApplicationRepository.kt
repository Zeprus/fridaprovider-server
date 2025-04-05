package com.zeprus.fridaprovider.repository

import com.zeprus.fridaprovider.model.Application
import com.zeprus.fridaprovider.model.ApplicationInfo
import com.zeprus.fridaprovider.model.Script

class ApplicationRepository {
    companion object {
        private val applications: List<Application> = mutableListOf(
            Application(ApplicationInfo("com.zeprus.fridaprovider")).addScript(Script("test", "testScript()")).addScript(Script("test2", "testScript2()")),
            Application(ApplicationInfo("com.zeprus.test"))
        )

        fun getApplicationInfos(): MutableList<ApplicationInfo> {
            val applicationInfoList = mutableListOf<ApplicationInfo>()
            for (application in applications) {
                applicationInfoList.add(application.applicationInfo)
            }
            return applicationInfoList
        }

        fun getScripts(applicationInfo: ApplicationInfo): List<Script> {
            val application: Application? = getApplication(applicationInfo)
            val scripts = mutableListOf<Script>()
            if (application != null) {
                scripts.addAll(application.scripts)
            }
            return scripts
        }

        fun getApplication(applicationInfo: ApplicationInfo): Application? {
            for (application in applications) {
                if (application.applicationInfo == applicationInfo) {
                    return application
                }
            }
            return null
        }
    }
}