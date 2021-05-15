package io.github.lumue.filescanner.cli;

import kotlinx.coroutines.CoroutineScope
import org.junit.Test;

import kotlinx.coroutines.*
import kotlin.system.*
/**
 * Created by lm on 23.04.16.
 */
class CollectAndWriteMovieMetadataTaskTest {
	@Test
	fun execute() {
		runBlocking {
			val task1: CollectAndWriteMovieMetadataTask = CollectAndWriteMovieMetadataTask()
			//val task2: CollectAndWriteMovieMetadataTask = CollectAndWriteMovieMetadataTask()

			val execution1 = async { task1.execute("/mnt/storagebox/neu") }
			//val execution2 = async { task2.execute("/mnt/vm-mediafiles/video/adult") }

			execution1.await()
			//execution2.await()
		}
	}

}