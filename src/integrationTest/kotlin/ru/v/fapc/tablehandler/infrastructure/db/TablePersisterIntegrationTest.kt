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
class TablePersisterIntegrationTest: BaseDbIntegrationTest() {

    @Autowired
    private lateinit var tablePersister: TablePersister

    @Test
    fun `writeTable should save new aggregate and return it with generated id`(softly: SoftAssertions) {
        val dto = SaveCommandDto(type = "report", source = "api", table = "H1;H2\nV1;V2")
        val aggregate = TableAggregate.create(dto).getOrThrow()

        val result = tablePersister.writeTable(aggregate)

        softly.assertThat(result.id).describedAs("Returned id").isNotNull()
        softly.assertThat(result.tableType.value).describedAs("tableType").isEqualTo("report")
        softly.assertThat(result.tableSource.value).describedAs("tableSource").isEqualTo("api")
    }

    @Test
    fun `writeTable should update existing aggregate when id is present`(softly: SoftAssertions) {
        val dto = SaveCommandDto(type = "report", source = "api", table = "H1;H2\nV1;V2")
        val aggregate = TableAggregate.create(dto).getOrThrow()
        val saved = tablePersister.writeTable(aggregate)

        val updatedDto = SaveCommandDto(type = "summary", source = "manual", table = "A;B\nC;D")
        val updatedAggregate = TableAggregate.create(updatedDto).getOrThrow().withId(saved.id!!)

        val result = tablePersister.writeTable(updatedAggregate)

        softly.assertThat(result.id).describedAs("Updated id").isEqualTo(saved.id)
        softly.assertThat(result.tableType.value).describedAs("Updated tableType").isEqualTo("summary")
        softly.assertThat(result.tableSource.value).describedAs("Updated tableSource").isEqualTo("manual")
    }

    @Test
    fun `writeTable should persist table data correctly`(softly: SoftAssertions) {
        val dto = SaveCommandDto(type = "data", source = "import", table = "Col1;Col2\nRow1;Row2\nRow3;Row4")
        val aggregate = TableAggregate.create(dto).getOrThrow()

        val result = tablePersister.writeTable(aggregate)

        softly.assertThat(result.id).isNotNull()
        softly.assertThat(result.table.table).hasSize(3)
        softly.assertThat(result.table.table[0]).containsExactly("Col1", "Col2")
    }
}
