package com.jmr.data.mapper

import com.jmr.domain.model.DropboxFileList as DomainModel
import com.jmr.domain.model.DropboxFileEntry as DomainModelItem
import com.jmr.data.model.DropboxFileList as DataModel
import com.jmr.data.model.FileResponse as DataModelItem

class DropboxFileMapper : Mapper<DomainModel, DataModel> {
    override fun mapToDataModel(type: DomainModel): DataModel {
        val resultList = mutableListOf<DataModelItem>()
        type.entries.forEach {
            resultList.add(
                DataModelItem(
                    it.tag,
                    it.name,
                    it.path_display,
                    it.id,
                    it.size,
                    it.client_modified
                )
            )
        }
        return DataModel(resultList)
    }

    override fun mapToDomainModel(type: DataModel): DomainModel {
        val resultList = mutableListOf<DomainModelItem>()
        type.entries.forEach {
            resultList.add(
                DomainModelItem(
                    it.tag,
                    it.name,
                    it.path_display,
                    it.id,
                    it.size,
                    it.client_modified
                )
            )
        }
        return DomainModel(resultList)
    }
}