package ru.v.fapc.tablehandler.infrastructure.db

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TableRepository : JpaRepository<TableEntity, UUID>
