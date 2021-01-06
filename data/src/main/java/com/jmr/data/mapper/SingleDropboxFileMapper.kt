package com.jmr.data.mapper

import com.jmr.domain.model.DropboxFileEntry as DomainModelItem
import com.jmr.data.model.FileResponse as DataModelItem

class SingleDropboxFileMapper : Mapper<DomainModelItem, DataModelItem> {
    override fun mapToDataModel(type: DomainModelItem): DataModelItem {
        return DataModelItem(
            type.tag,
            type.name,
            type.path_display,
            type.id,
            type.size,
            type.client_modified
        )
    }

    override fun mapToDomainModel(type: DataModelItem): DomainModelItem {
        return DomainModelItem(
            type.tag,
            type.name,
            type.path_display,
            type.id,
            type.size,
            type.client_modified
        )
    }
}