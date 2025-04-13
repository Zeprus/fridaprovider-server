package com.zeprus.fridaprovider.repository

import com.zeprus.fridaprovider.model.Application
import com.zeprus.fridaprovider.model.ApplicationInfo
import com.zeprus.fridaprovider.model.Script
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import java.io.File
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest
class FileSystemApplicationRepositoryTest {
    @Value("\${fridaprovider.repositoryPath}")
    private lateinit var repositoryPath: String

    @Autowired
    lateinit var fsApplicationRepository: FileSystemApplicationRepository
    final val packageName = "com.zeprus.test"
    lateinit var appDir: File

    final val testScript1Name: String = "test_script_1"
    final val testScript1Content: String = "Automated"
    val testScript1FileContent: String = """
            {
              "packageName": "$packageName",
              "name": "$testScript1Name",
              "content": "$testScript1Content"
            }
        """.trimIndent()
    lateinit var testScript1: File

    final val testScript2Name: String = "test_script_2"
    final val testScript2Content: String = "Test"
    val testScript2FileContent: String = """
            {
              "packageName": "$packageName",
              "name": "$testScript2Name",
              "content": "$testScript2Content"
            }
        """.trimIndent()
    lateinit var testScript2: File


    @BeforeEach
    fun init() {
        this.appDir = File(repositoryPath, packageName)
        this.testScript1 = File(appDir, "$testScript1Name.json")
        this.testScript2 = File(appDir, "$testScript2Name.json")

        appDir.mkdirs()
        testScript1.createNewFile()
        testScript1.writeText(testScript1FileContent)
        testScript2.createNewFile()
        testScript2.writeText(testScript2FileContent)

        fsApplicationRepository.refresh()
    }

    @AfterEach
    fun destroy() {
        appDir.deleteRecursively()
    }

    @Test
    fun storeAppTest() {
        val packageName = "com.zeprus.test.store"
        val app = Application(ApplicationInfo(packageName))

        fsApplicationRepository.store(app)
        val storeDirectory = File(repositoryPath, packageName)

        try {
            assertNotNull(fsApplicationRepository.getApplications().find { it.applicationInfo.packageName == packageName }, "Application $packageName not in repository!")
            assert(storeDirectory.isDirectory) { "Directory for $packageName at ${storeDirectory.absolutePath} does not exist or is not a directory!" }
        } finally {
            storeDirectory.deleteRecursively()
        }
    }

    @Test
    fun storeScriptTest() {
        val packageName = "com.zeprus.test.store"
        val name = "store_script"
        val content = "Hello World!"
        val script = Script(packageName, name, content)

        fsApplicationRepository.store(script)
        val storeDirectory = File(repositoryPath, packageName)
        val scriptFile = File(storeDirectory, "$name.json")

        try {
            assertNotNull(fsApplicationRepository.getApplications().find { it.applicationInfo.packageName == packageName }, "Application $packageName not in repository!")
            val repositoryScript = fsApplicationRepository.getScript(ApplicationInfo(packageName), name)
            assertNotNull(repositoryScript, "Script not in repository!")
            assert(repositoryScript.content == content) { "Script repository content mismatch! Repository: ${repositoryScript.content} Expected: $content" }
            assert(storeDirectory.isDirectory) { "Directory for $packageName at ${storeDirectory.absolutePath} does not exist or is not a directory!" }
            assert(scriptFile.exists()) { "Script file at ${scriptFile.absolutePath} does not exist!" }
        } finally {
            storeDirectory.deleteRecursively()
        }
    }

    @Test
    fun getApplicationTest() {
        val applicationInfo = ApplicationInfo(packageName)
        val application = fsApplicationRepository.get(applicationInfo)
        assertNotNull(application, "Application ${applicationInfo.packageName} does not exist in repository!")
    }

    @Test
    fun getWrongApplicationTest() {
        val applicationInfo = ApplicationInfo("error")
        val application = fsApplicationRepository.get(applicationInfo)
        assertNull(application, "Application ${applicationInfo.packageName} should not exist in repository!")
    }

    @Test
    fun deleteApplicationTest() {
        val applicationInfo = ApplicationInfo(packageName)

        fsApplicationRepository.delete(applicationInfo)

        assertNull(fsApplicationRepository.get(applicationInfo), "$packageName is in repository!")
        assert(!appDir.exists()) { "Application directory ${appDir.absolutePath} is not deleted!" }
    }

    @Test
    fun deleteScriptTest() {
        val script = Script(packageName, testScript1Name, testScript1Content)

        fsApplicationRepository.delete(script)

        assertNull(fsApplicationRepository.getScript(ApplicationInfo(script.packageName), script.name), "Script ${script.name} is in repository!")
        assert(!testScript1.exists()) { "File ${testScript1.absolutePath} still exists!" }
    }

    @Test
    fun loadTest() {
        val applications: MutableList<Application> = fsApplicationRepository.load()
        assertTestAppAndScripts(applications)
    }

    @Test
    fun getApplicationsTest() {
        val applications: List<Application> = fsApplicationRepository.getApplications()
        assertTestAppAndScripts(applications)
    }

    @Test
    fun getApplicationInfosTest() {
        assertNotNull(fsApplicationRepository.getApplicationInfos().find { it.packageName == packageName }, "Applicationinfo for $packageName not found!")
    }

    @Test
    fun getApplicationInfoTest() {
        assert(fsApplicationRepository.getApplicationInfo(packageName)?.packageName == packageName) { "ApplicationInfo for $packageName not found or wrong!" }
    }

    @Test
    fun getScriptsTest() {
        val scripts: List<Script> = fsApplicationRepository.getScripts()
        assertTestScripts(scripts)
    }

    @Test
    fun getAppScriptsTest() {
        val scripts: List<Script> = fsApplicationRepository.getScripts(ApplicationInfo(packageName))
        assertTestScripts(scripts)
    }

    @Test
    fun getScriptTest() {
        val script: Script? = fsApplicationRepository.getScript(ApplicationInfo(packageName), testScript1Name)
        assertNotNull(script, "Script $testScript1Name not in repository!")

        assert(script.packageName == packageName) { "testScript1 packagename wrong. Got ${script.packageName}, expected ${packageName}!" }
        assert(script.name == testScript1Name) { "testScript1 name wrong. Got ${script.name}, expected ${testScript1Name}!" }
        assert(script.content == testScript1Content) { "testScript1 content wrong. Got ${script.content}, expected ${testScript1Content}!" }
    }

    fun assertTestAppAndScripts(applications: List<Application>) {
        val app: Application? = applications.find { it.applicationInfo.packageName == packageName }
        assertNotNull(app, "Application $packageName not in repository!")
        assertTestScripts(app.scripts)
    }

    fun assertTestScripts(scripts: List<Script>) {
        val loadedScript1: Script? = scripts.find { it.name == testScript1Name }
        val loadedScript2: Script? = scripts.find { it.name == testScript2Name }

        assertNotNull(loadedScript1, "Script $testScript1Name not found!")
        assertNotNull(loadedScript2, "Script $testScript2Name not found!")

        assert(loadedScript1.packageName == packageName) { "testScript1 packagename wrong. Got ${loadedScript1.packageName}, expected ${packageName}!" }
        assert(loadedScript1.name == testScript1Name) { "testScript1 name wrong. Got ${loadedScript1.name}, expected ${testScript1Name}!" }
        assert(loadedScript1.content == testScript1Content) { "testScript1 content wrong. Got ${loadedScript1.content}, expected ${testScript1Content}!" }

        assert(loadedScript2.packageName == packageName) { "testScript1 packagename wrong. Got ${loadedScript2.packageName}, expected ${packageName}!" }
        assert(loadedScript2.name == testScript2Name) { "testScript1 name wrong. Got ${loadedScript2.name}, expected ${testScript2Name}!" }
        assert(loadedScript2.content == testScript2Content) { "testScript1 content wrong. Got ${loadedScript2.content}, expected ${testScript2Content}!" }
    }
}