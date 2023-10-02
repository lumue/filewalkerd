package net.lumue.filewalkerd.scanner

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.springframework.stereotype.Component

@ExperimentalCoroutinesApi
@Component
class ScannerRegistry(
    val fileHandlingTasks: Map<String,PathScanner>
) {




    fun byName(name:String) : PathScanner {
        return fileHandlingTasks[name]!!
    }


}