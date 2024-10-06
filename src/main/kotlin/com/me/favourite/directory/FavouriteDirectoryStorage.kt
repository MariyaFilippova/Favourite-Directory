package com.me.favourite.directory

import com.intellij.openapi.components.*
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.me.favourite.directory.FavouriteDirectoryStorage.FavouriteDirectoryState

@Service(Service.Level.PROJECT)
@State(name = "FavouriteDirectoryStorage", storages = [Storage("directories.xml")])
class FavouriteDirectoryStorage : SimplePersistentStateComponent<FavouriteDirectoryState>(FavouriteDirectoryState()) {
    companion object {
        fun getInstance(project: Project) = project.service<FavouriteDirectoryStorage>()
        val logger = Logger.getInstance(FavouriteDirectoryStorage::class.java)
    }

    class FavouriteDirectoryState : BaseState() {
        var directoryPaths by list<String>()

        fun changeDirectoryState() {
            incrementModificationCount()
        }
    }

    fun isDirectoryFavourite(directory: VirtualFile) = state.directoryPaths.contains(directory.path)

    fun markDirectoryAsFavourite(directory: VirtualFile) {
        state.directoryPaths.add(directory.path)
        logger.info("Marked directory ${directory.path} as favourite")
        state.changeDirectoryState()
    }

    fun unmarkDirectoryAsFavourite(directory: VirtualFile) {
        state.directoryPaths.remove(directory.path)
        logger.info("Unmarked directory ${directory.path} as favourite")
        state.changeDirectoryState()
    }
}