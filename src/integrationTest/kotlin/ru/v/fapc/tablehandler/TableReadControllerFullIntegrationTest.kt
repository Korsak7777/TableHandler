package ru.v.fapc.tablehandler

import com.example.model.TableDto
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.web.client.RestTemplate
import ru.v.fapc.tablehandler.domain.TableAggregate
import ru.v.fapc.tablehandler.domain.dto.SaveCommandDto
import ru.v.fapc.tablehandler.infrastructure.BaseDbIntegrationTest
import ru.v.fapc.tablehandler.infrastructure.db.TablePersister
import java.net.URI

@ExtendWith(SoftAssertionsExtension::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class TableReadControllerFullIntegrationTest : BaseDbIntegrationTest() {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var tablePersister: TablePersister

    private val restTemplate = RestTemplate()

    private fun url(path: String) = URI.create("http://localhost:$port$path")

    @Test
    fun `readTable should return 200 OK with TableDto when table exists`(softly: SoftAssertions) {
        // Given - save a table via persister to get the real id
        val dto = SaveCommandDto(type = "report", source = "api", table = "Header1;Header2\nValue1;Value2")
        val aggregate = TableAggregate.create(dto).getOrThrow()
        val saved = tablePersister.writeTable(aggregate)

        // When - read by id
        val readResponse = restTemplate.getForEntity(
            url("/table/${saved.id}"), TableDto::class.java
        )

        // Then
        softly.assertThat(readResponse.statusCode)
            .describedAs("HTTP status").isEqualTo(HttpStatus.OK)
        softly.assertThat(readResponse.body)
            .describedAs("Response body").isNotNull()
        softly.assertThat(readResponse.body?.id)
            .describedAs("Table id").isEqualTo(saved.id)
        softly.assertThat(readResponse.body?.tableType)
            .describedAs("Table type").isEqualTo("report")
        softly.assertThat(readResponse.body?.tableSource)
            .describedAs("Table source").isEqualTo("api")
        softly.assertThat(readResponse.body?.table)
            .describedAs("Table data").isEqualTo("Header1;Header2\nValue1;Value2")
    }
}
