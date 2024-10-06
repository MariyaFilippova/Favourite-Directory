package com.me.favourite.directory

import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.vfs.VirtualFile

class MarkDirectoryAsFavouriteAction : AnAction(), DumbAware {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val storage = FavouriteDirectoryStorage.getInstance(project)

        val selectedDirectories = getSelectedDirectories(e, false)
        if (selectedDirectories.isEmpty()) return
        selectedDirectories.forEach { storage.markDirectoryAsFavourite(it) }
        updateTree(e)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun update(e: AnActionEvent) {
        val selectedDirectories = getSelectedDirectories(e, false)
        e.presentation.isEnabledAndVisible = selectedDirectories.isNotEmpty()
    }
}

private fun updateTree(e: AnActionEvent) {
    val project = e.project ?: return
    val projectDir = project.guessProjectDir() ?: return
    val module = ProjectFileIndex.getInstance(project).getModuleForFile(projectDir) ?: return
    val model = ModuleRootManager.getInstance(module).modifiableModel
    ApplicationManager.getApplication().runWriteAction(model::commit)
    project.save()
    ProjectView.getInstance(project).refresh()
}

private fun getSelectedDirectories(e: AnActionEvent, shouldBeFavourite: Boolean): List<VirtualFile> {
    val project = e.project ?: return emptyList()
    val storage = FavouriteDirectoryStorage.getInstance(project)

    return e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY)?.let {
        it.filter { it.isDirectory && storage.isDirectoryFavourite(it) == shouldBeFavourite }
    } ?: emptyList()
}

class UnmarkDirectoryAsFavouriteAction : AnAction(), DumbAware {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val storage = FavouriteDirectoryStorage.getInstance(project)

        val selectedDirectories = getSelectedDirectories(e, true)
        if (selectedDirectories.isEmpty()) return
        selectedDirectories.forEach { storage.unmarkDirectoryAsFavourite(it) }
        updateTree(e)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun update(e: AnActionEvent) {
        val selectedDirectories = getSelectedDirectories(e, true)
        e.presentation.isEnabledAndVisible = selectedDirectories.isNotEmpty()
    }
}