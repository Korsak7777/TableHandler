package ru.v.fapc.tablehandler.application

import com.example.model.ResultTableDto
import com.example.model.SaveCommand
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SaveTableUseCase {
    fun saveTable(saveCommand: SaveCommand) =
        saveCommand.let {
        //            1. TODO достать таблицы из БД.
        //            2. TODO просуммировать таблицы
        //            3. TODO вернуть результат
            ResultTableDto(UUID.randomUUID())
        }
}