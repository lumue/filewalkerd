package net.lumue.filewalkerd.moviescanner

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

@ExperimentalCoroutinesApi
class ScanMoviesTask {


    val locations = listOf(
        "/mnt/storagebox/neu",
        "/mnt/vm-mediafiles/video/adult",
        "/mnt/storagebox/video/adult",
        "/mnt/nasbox/media/adult"
    )

    fun execute() {
        runBlocking {
            val task1: CollectAndWriteMovieMetadataTask = CollectAndWriteMovieMetadataTask()
            val executions = mutableListOf<Deferred<Unit>>()
            locations.forEach { l ->
                executions.add(async { task1.execute(l) })
            }
            executions.forEach { e -> e.await() }
        }
    }

    fun main(args: Array<String>) {
        this.execute()
    }

}
