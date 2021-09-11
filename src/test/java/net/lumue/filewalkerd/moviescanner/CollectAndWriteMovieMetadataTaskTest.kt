package net.lumue.filewalkerd.moviescanner


import org.junit.jupiter.api.Test
import kotlinx.coroutines.*
/**
 * Created by lm on 23.04.16.
 */
@ExperimentalCoroutinesApi
class CollectAndWriteMovieMetadataTaskTest {

    val locations=listOf("/mnt/storagebox/neu")// /mnt/vm-mediafiles/video/adult","/mnt/storagebox/video/adult","/mnt/nasbox/media/adult")

    @Test
    fun execute() {
        runBlocking {
            val task1 = CollectAndWriteMovieMetadataTask()
            val executions=mutableListOf<Deferred<Unit>>()
            locations.forEach { l ->
                executions.add(async { task1.execute(l) })
            }
            executions.forEach {e-> e.await()}
        }
    }

}
