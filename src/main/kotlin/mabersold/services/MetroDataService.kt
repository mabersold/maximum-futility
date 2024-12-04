package mabersold.services

import mabersold.dao.MetroDAO
import mabersold.models.api.Metro

class MetroDataService(private val metroDAO: MetroDAO) {
    suspend fun getMetros() =
        metroDAO.all().map { mabersold.models.api.Metro(it.id, it.name, it.label) }

    suspend fun create(name: String, label: String): Metro? {
        return metroDAO.create(name, label)?.let {
            Metro(it.id, it.name, it.label)
        }
    }
}