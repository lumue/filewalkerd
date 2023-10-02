package net.lumue.filewalkerd.restapi

import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.lumue.filewalkerd.scanner.ScanService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController


@RestController
@ExperimentalCoroutinesApi
class ScanController(@Autowired val scanService: ScanService) {

    @RequestMapping(
        path = ["/scan"],
        method = [RequestMethod.POST],
        produces = ["application/json; charset=utf-8"]
    )
    fun handlePost(@RequestBody scanRequest: ScanRequest): HttpStatus {
        scanService.startScan(scanRequest.path, scanRequest.fileHandlerId)
        return HttpStatus.OK
    }


}

class ScanRequest(val path: String, val fileHandlerId: String)
