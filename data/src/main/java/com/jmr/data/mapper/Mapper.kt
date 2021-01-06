package com.jmr.data.mapper

interface Mapper<E, D> {
    fun mapToDataModel(type: E): D
    fun mapToDomainModel(type: D): E
}