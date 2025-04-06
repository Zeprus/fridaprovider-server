package com.zeprus.fridaprovider.repository

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.zeprus.fridaprovider.model.Application
import com.zeprus.fridaprovider.model.ApplicationInfo
import com.zeprus.fridaprovider.model.Script
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import java.io.File
import java.io.FileOutputStream

@Repository
class FileSystemApplicationRepository : ApplicationRepository<Application> {
    //TODO: Refresh (Filewatcher? Event?)

    @Value("\${fridaprovider.repositoryPath}")
    private lateinit var repositoryPath: String
    private lateinit var directory: File
    private var applications: MutableList<Application> = mutableListOf()

    @PostConstruct
    fun init() {
        directory = File(repositoryPath)
        applications = load()
    }

    override fun getApplications(): List<Application> {
        return applications
    }

    override fun getApplicationInfos(): MutableList<ApplicationInfo> {
        val applicationInfoList = mutableListOf<ApplicationInfo>()
        for (application in applications) {
            applicationInfoList.add(application.applicationInfo)
        }
        return applicationInfoList
    }

    override fun getApplicationInfo(packageName: String): ApplicationInfo? {
        for (application in applications) {
            if (application.applicationInfo.packageName == packageName) {
                return application.applicationInfo
            }
        }
        return null
    }

    override fun getScripts(): List<Script> {
        val scripts = mutableListOf<Script>()
        for (application in applications) {
            for (script in application.scripts) {
                scripts.add(script)
            }
        }
        return scripts
    }

    override fun getScripts(applicationInfo: ApplicationInfo): List<Script> {
        val application: Application? = get(applicationInfo)
        val scripts = mutableListOf<Script>()
        if (application != null) {
            scripts.addAll(application.scripts)
        }
        return scripts
    }

    override fun getScript(applicationInfo: ApplicationInfo, scriptName: String): Script? {
        return get(applicationInfo)?.scripts?.find { it.name == scriptName }
    }

    override fun store(application: Application) {
        val appDir = File(directory, application.applicationInfo.packageName)
        if (!appDir.exists()) {
            appDir.mkdir()
        }
        for (script: Script in application.scripts) {
            val scriptFile = File(directory, "${script.name}.json")
            val mapper = jacksonObjectMapper()
            mapper.writeValue(FileOutputStream(scriptFile), script)
        }
        applications.add(application)
    }

    override fun store(script: Script) {
        val appDir = File(directory, script.packageName)
        if (!appDir.exists()) {
            appDir.mkdir()
        }
        val scriptFile = File(appDir, "${script.name}.json")
        val mapper = jacksonObjectMapper()
        mapper.writeValue(FileOutputStream(scriptFile), script)
    }

    override fun get(applicationInfo: ApplicationInfo): Application? {
        for (application in applications) {
            if (application.applicationInfo.packageName == applicationInfo.packageName) {
                return application
            }
        }
        return null
    }

    override fun delete(applicationInfo: ApplicationInfo) {
        val appDir = File(directory, applicationInfo.packageName)
        if (appDir.exists()) {
            appDir.deleteRecursively()
        }
        applications.removeIf { it.applicationInfo.packageName == applicationInfo.packageName }
    }

    override fun delete(script: Script) {
        val appDir = File(directory, script.packageName)
        val scriptFile = File(appDir, "${script.name}.json")
        if(scriptFile.exists()) {
            scriptFile.delete()
        }
        applications.find { it.applicationInfo.packageName == script.packageName }?.scripts?.removeIf { it.name == script.name }
    }

    final override fun load(): MutableList<Application> {
        for (appDir in directory.listFiles()!!) {
            if (appDir.isDirectory) {
                val applicationInfo = ApplicationInfo(appDir.name)
                val application = Application(applicationInfo)
                if (get(applicationInfo) == null) {
                    applications.add(application)
                }
                for (file in appDir.listFiles()!!) {
                    if (!file.isDirectory && getScript(applicationInfo, file.nameWithoutExtension) == null) {
                        val script: Script = Script.fromFile(file)
                        application.addScript(script)
                    }
                }
            }
        }

        return applications
    }
}