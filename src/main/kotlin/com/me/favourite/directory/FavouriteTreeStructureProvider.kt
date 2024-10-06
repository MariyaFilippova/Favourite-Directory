package com.me.favourite.directory

import com.intellij.ide.projectView.TreeStructureProvider
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.psi.PsiDirectory
import com.intellij.psi.impl.file.PsiDirectoryFactory
import com.me.favourite.directory.FavouriteDirectoryStorage.Companion.logger
import kotlin.io.path.Path

class FavouriteTreeStructureProvider : TreeStructureProvider, DumbAware {
    override fun modify(
        parent: AbstractTreeNode<*>,
        children: Collection<AbstractTreeNode<*>>,
        settings: ViewSettings,
    ): Collection<AbstractTreeNode<*>?> {
        val result = arrayListOf<AbstractTreeNode<*>>()
        val storage = FavouriteDirectoryStorage.getInstance(parent.project)
        result.addAll(children.filterNot {
            it is MyPsiDirectoryNode && it.value.virtualFile.path !in storage.state.directoryPaths
        })
        if (parent.value is Project) {
            val directories = createFavouriteDirectories(parent.project, settings)
            result.addAll(directories)
        }
        return result
    }

    fun createFavouriteDirectories(project: Project?, viewSettings: ViewSettings): List<PsiDirectoryNode> {
        if (project == null) return emptyList()
        val storage = FavouriteDirectoryStorage.getInstance(project)
        val directories = storage.state.directoryPaths.mapNotNull {
            val directory = VfsUtil.findFile(Path(it), true) ?: return@mapNotNull null
            logger.debug("Favourite directory: $it")
            return@mapNotNull PsiDirectoryFactory.getInstance(project).createDirectory(directory)
        }
        return directories.map { MyPsiDirectoryNode(project, it, viewSettings) }
    }
}

class MyPsiDirectoryNode(project: Project, value: PsiDirectory, viewSettings: ViewSettings) :
    PsiDirectoryNode(project, value, viewSettings) {
    override fun getTypeSortWeight(sortByType: Boolean): Int {
        return -1
    }
}