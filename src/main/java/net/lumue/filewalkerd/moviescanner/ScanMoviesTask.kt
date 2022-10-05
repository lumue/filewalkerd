package net.lumue.filewalkerd.moviescanner

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking

@ExperimentalCoroutinesApi
class ScanMoviesTask(
    val path: String
) {

    fun execute() {
        runBlocking {
            RenameMovieTask().execute(path)
        }
    }

}


@Suppress("UNUSED_PARAMETER")
@ExperimentalCoroutinesApi
fun main(args: Array<String>) {
    ScanMoviesTask("/mnt/storagebox/neu").execute()
}
