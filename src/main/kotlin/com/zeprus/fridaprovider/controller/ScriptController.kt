package com.zeprus.fridaprovider.controller

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
    private lateinit var fsApplicationRepository: FileSystemApplicationRepository

    @PostMapping("/applications")
    fun getApplications(): List<ApplicationInfo> {
        return fsApplicationRepository.getApplicationInfos()
    }

    @PostMapping("/scripts", produces = ["application/json"])
    fun badHeader(): ResponseEntity<Any> {
        val body = "{\"error\" : \"Missing header 'Action=(get:save)'.\"}"
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
    }

    @PostMapping("/scripts", headers = ["Action=get"], produces = ["application/json"])
    fun getScripts(@RequestBody applicationInfo: ApplicationInfo): List<Script> {
        return fsApplicationRepository.getScripts(applicationInfo)
    }

    @PostMapping("/scripts", headers = ["Action=save"])
    fun saveScript(@RequestBody script: Script): ResponseEntity<Any> {
        fsApplicationRepository.store(script)
        return ResponseEntity.status(HttpStatus.OK).body("OK")
    }

}