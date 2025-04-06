package com.zeprus.fridaprovider.controller

import com.zeprus.fridaprovider.model.Application
import com.zeprus.fridaprovider.model.ApplicationInfo
import com.zeprus.fridaprovider.model.Script
import com.zeprus.fridaprovider.repository.FileSystemApplicationRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ScriptController {
    @Autowired
    private lateinit var applicationRepository: FileSystemApplicationRepository

    @PostMapping("/applications")
    fun badApplicationsHeader(): ResponseEntity<Any> {
        val body = "{\"error\" : \"Missing header 'Action=(all:get:save:delete)'.\"}"
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
    }

    @PostMapping("/applications", headers = ["Action=all"], produces = ["application/json"])
    fun getApplications(): List<ApplicationInfo> {
        return applicationRepository.getApplicationInfos()
    }

    @PostMapping("/applications", headers = ["Action=get"], produces = ["application/json"])
    fun getApplications(@RequestBody packageName: String): ApplicationInfo? {
        return applicationRepository.getApplicationInfo(packageName)
    }

    @PostMapping("/applications", headers = ["Action=save"])
    fun saveApplications(@RequestBody applicationInfos: List<ApplicationInfo>): ResponseEntity<Any> {
        for (applicationInfo in applicationInfos) {
            if (applicationRepository.getApplicationInfo(applicationInfo.packageName) == null) {
                applicationRepository.store(Application(applicationInfo))
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body("OK")
    }

    @PostMapping("/applications", headers = ["Action=delete"])
    fun deleteApplications(@RequestBody applicationInfos: List<ApplicationInfo>): ResponseEntity<Any> {
        for (applicationInfo in applicationInfos) {
            applicationRepository.delete(applicationInfo)
        }
        return ResponseEntity.status(HttpStatus.OK).body("OK")
    }

    @PostMapping("/scripts", produces = ["application/json"])
    fun badScriptsHeader(): ResponseEntity<Any> {
        val body = "{\"error\" : \"Missing header 'Action=(all:get:save:delete)'.\"}"
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
    }

    @PostMapping("/scripts", headers = ["Action=all"], produces = ["application/json"])
    fun getScripts(): List<Script> {
        return applicationRepository.getScripts()
    }

    @PostMapping("/scripts", headers = ["Action=get"], produces = ["application/json"])
    fun getScripts(@RequestBody applicationInfo: ApplicationInfo): List<Script> {
        return applicationRepository.getScripts(applicationInfo)
    }

    @PostMapping("/scripts", headers = ["Action=save"])
    fun saveScript(@RequestBody scripts: List<Script>): ResponseEntity<Any> {
        for (script in scripts) {
            applicationRepository.store(script)
        }
        return ResponseEntity.status(HttpStatus.OK).body("OK")
    }

    @PostMapping("/scripts", headers = ["Action=delete"])
    fun deleteScript(@RequestBody scripts: List<Script>): ResponseEntity<Any> {
        for (script in scripts) {
            applicationRepository.delete(script)
        }
        return ResponseEntity.status(HttpStatus.OK).body("OK")
    }

}