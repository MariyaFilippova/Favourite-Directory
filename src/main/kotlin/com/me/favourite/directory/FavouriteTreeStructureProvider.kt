package com.me.favourite.directory

import com.intellij.icons.AllIcons
import com.intellij.ide.projectView.NodeSortOrder
import com.intellij.ide.projectView.NodeSortSettings
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.projectView.TreeStructureProvider
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.ProjectViewDirectoryHelper
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.ui.JBColor
import com.intellij.util.ImageLoader
import com.intellij.util.ui.JBImageIcon
import java.awt.Color
import kotlin.io.path.Path

class FavouriteTreeStructureProvider : TreeStructureProvider, DumbAware {
    override fun modify(
        parent: AbstractTreeNode<*>,
        children: Collection<AbstractTreeNode<*>>,
        settings: ViewSettings,
    ): Collection<AbstractTreeNode<*>?> {
        val project = parent.project ?: return children.toList()
        val result = children.filterNot { it is FavouriteDirectoryNode }.toMutableList()
        if (parent.value is Project) {
            result.addAll(createFavouriteDirectories(project, settings))
        }
        return result
    }

    fun createFavouriteDirectories(project: Project?, viewSettings: ViewSettings): List<FavouriteDirectoryNode> {
        if (project == null) return emptyList()
        val storage = FavouriteDirectoryStorage.getInstance(project)
        return storage.state.directoryPaths.mapNotNull {
            val directory = VfsUtil.findFile(Path(it), false) ?: return@mapNotNull null
            FavouriteDirectoryNode(project, directory, viewSettings)
        }
    }
}

class FavouriteDirectoryNode(
    project: Project,
    directory: VirtualFile,
    viewSettings: ViewSettings,
) : ProjectViewNode<VirtualFile>(project, directory, viewSettings) {

    override fun update(presentation: PresentationData) {
        val vFile = value ?: return
        presentation.presentableText = vFile.name
        presentation.locationString = vFile.path
        presentation.setIcon(createFavouriteIcon())
        presentation.background = JBColor(
            Color(237, 235, 251, 128),
            Color(71, 39, 60, 60)
        )
    }

    override fun getChildren(): Collection<AbstractTreeNode<*>> {
        val project = project ?: return emptyList()
        val vFile = value ?: return emptyList()
        val psiDir = PsiManager.getInstance(project).findDirectory(vFile) ?: return emptyList()
        return ProjectViewDirectoryHelper.getInstance(project)
            .getDirectoryChildren(psiDir, settings, true)
    }

    override fun contains(file: VirtualFile): Boolean {
        val vFile = value ?: return false
        return VfsUtil.isAncestor(vFile, file, false)
    }

    override fun getVirtualFile(): VirtualFile? = value

    override fun getSortOrder(settings: NodeSortSettings): NodeSortOrder {
        return NodeSortOrder.PROJECT_ROOT
    }

    private fun createFavouriteIcon(): javax.swing.Icon {
        val imgURL = FavouriteDirectoryStorage::class.java.getResource("/icons/favourite.svg")
            ?: return AllIcons.Nodes.Folder
        val image = ImageLoader.loadFromUrl(imgURL) ?: return AllIcons.Nodes.Folder
        return JBImageIcon(ImageLoader.scaleImage(image, 18, 18))
    }
}
