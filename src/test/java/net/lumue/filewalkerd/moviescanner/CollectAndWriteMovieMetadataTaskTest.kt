package net.lumue.filewalkerd.moviescanner


import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

/**
 * Created by lm on 23.04.16.
 */
@ExperimentalCoroutinesApi
class CollectAndWriteMovieMetadataTaskTest {

    val locations = listOf("/mnt/truenas/media/porn/neu/2")
    //,"/mnt/truenas/media/porn/adult/2023")

    @Test
    fun execute() {
        runBlocking {
            val task1 = CollectAndWriteMovieMetadataTask()
            val executions = mutableListOf<Deferred<Unit>>()
            locations.forEach { l ->
                executions.add(async { task1.execute(l) })
            }
            executions.forEach { e -> e.await() }
        }
    }

}
