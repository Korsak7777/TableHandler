package ru.v.fapc.tablehandler.infrastructure.db

import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringExtension
import ru.v.fapc.tablehandler.SoftAssertionsExtension
import ru.v.fapc.tablehandler.domain.TableAggregate
import ru.v.fapc.tablehandler.domain.dto.SaveCommandDto
import ru.v.fapc.tablehandler.infrastructure.BaseDbIntegrationTest

@ExtendWith(SpringExtension::class, SoftAssertionsExtension::class)
class TablePersisterReadIntegrationTest : BaseDbIntegrationTest() {

    @Autowired
    private lateinit var tablePersister: TablePersister

    @Test
    fun `findById should return aggregate with correct fields`(softly: SoftAssertions) {
        // Given
        val dto = SaveCommandDto(type = "report", source = "api", table = "H1;H2\nV1;V2")
        val aggregate = TableAggregate.create(dto).getOrThrow()
        val saved = tablePersister.writeTable(aggregate)

        // When
        val result = tablePersister.findById(saved.id!!)

        // Then
        softly.assertThat(result).describedAs("Found aggregate").isNotNull()
        softly.assertThat(result?.id).describedAs("id").isEqualTo(saved.id)
        softly.assertThat(result?.tableType?.value).describedAs("tableType").isEqualTo("report")
        softly.assertThat(result?.tableSource?.value).describedAs("tableSource").isEqualTo("api")
        softly.assertThat(result?.table?.table).describedAs("table rows").hasSize(2)
    }

    @Test
    fun `findById should return null when table does not exist`(softly: SoftAssertions) {
        // Given
        val nonExistentId = java.util.UUID.randomUUID()

        // When
        val result = tablePersister.findById(nonExistentId)

        // Then
        softly.assertThat(result).describedAs("Result for non-existent id").isNull()
    }
}
