package net.lumue.filewalkerd.scanner.moviescanner

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.runBlocking
import net.lumue.filewalkerd.scanner.Scanners

@ExperimentalCoroutinesApi
class ScanMoviesTask(
    val path: String
) :Runnable{

    override fun run() {
        runBlocking {
            Scanners.RenameMovieTask().execute(path)
        }
    }

}



@ExperimentalCoroutinesApi
fun main(args: Array<String>) {
    ScanMoviesTask(args.get(0)).run()
}
