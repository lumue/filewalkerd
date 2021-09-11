package net.lumue.filewalkerd.moviescanner

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

@ExperimentalCoroutinesApi
class ScanMoviesTask {




    fun execute(l:String) {
        runBlocking {
            CollectAndWriteMovieMetadataTask().execute(l)
        }
    }


}


@Suppress("UNUSED_PARAMETER")
@ExperimentalCoroutinesApi
fun main(args: Array<String>) {
    ScanMoviesTask().execute("/mnt/storagebox/neu")
}
