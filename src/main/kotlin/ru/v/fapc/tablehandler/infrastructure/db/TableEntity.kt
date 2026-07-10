package ru.v.fapc.tablehandler.infrastructure.db

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "table_handler")
class TableEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(name = "table_data", columnDefinition = "TEXT", nullable = false)
    var tableData: String = "",

    @Column(name = "table_type", nullable = false)
    var tableType: String = "",

    @Column(name = "table_source", nullable = false)
    var tableSource: String = "",
)
