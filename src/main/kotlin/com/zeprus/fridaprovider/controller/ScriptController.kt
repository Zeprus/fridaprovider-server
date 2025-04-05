package com.zeprus.fridaprovider.controller

import com.zeprus.fridaprovider.model.ApplicationInfo
import com.zeprus.fridaprovider.model.Script
import com.zeprus.fridaprovider.repository.ApplicationRepository
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ScriptController {

    @PostMapping("/applications")
    fun getApplications(): List<ApplicationInfo> {
        return ApplicationRepository.getApplicationInfos()
    }

    @PostMapping("/scripts")
    fun getScripts(@RequestBody applicationInfo: ApplicationInfo): List<Script> {
        return ApplicationRepository.getScripts(applicationInfo)
    }

}