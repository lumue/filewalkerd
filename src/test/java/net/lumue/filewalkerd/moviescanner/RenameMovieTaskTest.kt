package net.lumue.filewalkerd.moviescanner


import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import net.lumue.filewalkerd.moviescanner.MovieScanners.*
import org.junit.jupiter.api.Test

/**
 * Created by lm on 23.04.16.
 */
@ExperimentalCoroutinesApi
class RenameMovieTaskTest {

    val locations = listOf("/mnt/truenas/media/porn/neu")

    @Test
    fun execute() {
        runBlocking {
            val task1 = RenameMovieTask()
            val executions = mutableListOf<Deferred<Unit>>()
            locations.forEach { l ->
                executions.add(async { task1.execute(l) })
            }
            executions.forEach { e -> e.await() }
        }
    }

}
