package net.lumue.filewalkerd.moviescanner

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.runBlocking

@ExperimentalCoroutinesApi
class ScanMoviesTask(
    val path: String
) :Runnable{

    override fun run() {
        runBlocking {
            MovieScanners.RenameMovieTask().execute(path)
        }
    }

}



@ExperimentalCoroutinesApi
fun main(args: Array<String>) {
    ScanMoviesTask(args.get(0)).run()
}
