package net.lumue.filewalkerd.cli;

import kotlinx.coroutines.CoroutineScope
import org.junit.jupiter.api.Test
import kotlinx.coroutines.*
import kotlin.system.*
/**
 * Created by lm on 23.04.16.
 */
class CollectAndWriteMovieMetadataTaskTest {

	val locations=listOf("/mnt/storagebox/neu","/mnt/vm-mediafiles/video/adult","/mnt/storagebox/video/adult","/mnt/nasbox/media/adult")

	@Test
	fun execute() {
		runBlocking {
			val task1: CollectAndWriteMovieMetadataTask = CollectAndWriteMovieMetadataTask()
			val executions=mutableListOf<Deferred<Unit>>()
			locations.forEach { l ->
				executions.add(async { task1.execute(l) })
			}
			executions.forEach {e-> e.await()}
		}
	}

}
