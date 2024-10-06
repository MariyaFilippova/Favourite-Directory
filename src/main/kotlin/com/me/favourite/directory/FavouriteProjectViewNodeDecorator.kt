package com.me.favourite.directory

import com.intellij.icons.AllIcons
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.projectView.ProjectViewNodeDecorator
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiDirectory
import com.intellij.ui.JBColor
import com.intellij.util.ImageLoader
import com.intellij.util.ui.JBImageIcon
import java.awt.Color
import javax.swing.Icon

class FavouriteProjectViewNodeDecorator : ProjectViewNodeDecorator, DumbAware {
    override fun decorate(node: ProjectViewNode<*>, data: PresentationData) {
        val value = node.value as? PsiDirectory ?: return
        val storage = FavouriteDirectoryStorage.getInstance(node.project)
        if (!storage.isDirectoryFavourite(value.virtualFile)) return

        if (node is MyPsiDirectoryNode) {
            data.locationString = data.presentableText
            data.presentableText = data.presentableText?.substringAfterLast("/")
        }
        data.background = JBColor(
            Color(237, 235, 251, 128),
            Color(71, 39, 60, 60)
        )
        data.setIcon(createIcon())
    }

    fun createIcon(): Icon {
        val imgURL = FavouriteDirectoryStorage::class.java.getResource("/icons/favourite.svg")
            ?: return AllIcons.General.Information
        val image = ImageLoader.loadFromUrl(imgURL) ?: return AllIcons.General.Information
        return JBImageIcon(ImageLoader.scaleImage(image, 18, 18))
    }
}