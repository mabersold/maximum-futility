package mabersold.services

import mabersold.dao.MetroDAO

class MetroDataService(private val metroDAO: MetroDAO) {
    suspend fun getMetros() =
        metroDAO.all().map { mabersold.models.api.Metro(it.id, it.name, it.label) }
}