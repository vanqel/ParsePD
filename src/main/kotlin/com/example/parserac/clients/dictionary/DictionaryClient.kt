package com.example.parserac.clients.dictionary

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.io.File
import java.io.FileOutputStream

@Service
class DictionaryClient(
    @Qualifier("WebClientNSI") val webClient: WebClient,
)  {

    fun compareInfo(
        page: Int,
    ): Mono<Unit> {
        return webClient.get()
            .uri { u ->
                u.path("/${page}.pdf")
                    .build()
            }.accept(MediaType.APPLICATION_PDF)
            .retrieve()
            .toEntity(ByteArray::class.java)
            .publishOn(Schedulers.boundedElastic())
            .map {
                if (it.statusCode.is2xxSuccessful && it.body != null) {
                    println(it.body!!.size)
                    Mono.fromCallable { saveFilesToFolder(page, it.body!!) }.publishOn(Schedulers.boundedElastic()).subscribe()
                }
            }
            .doOnError { error ->
                println("Ошибка при загрузке или сохранении файла: ${error.message}")
            }.retry(5)
    }

    fun saveFilesToFolder(index: Int, file: ByteArray) {
        val folder = File("pdf")
        if (!folder.exists()) {
            folder.mkdirs()
        }

        val outputFile = File(folder, "$index.pdf")
        FileOutputStream(outputFile).use { outputStream ->
            outputStream.write(file)
        }
        println("Файл сохранен: ${outputFile.name}")
    }
}
