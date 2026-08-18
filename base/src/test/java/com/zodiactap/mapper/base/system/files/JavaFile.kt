package com.zodiactap.mapper.base.system.files

import com.anggrayudi.storage.file.recreateFile
import com.zodiactap.mapper.common.utils.KMResult
import com.zodiactap.mapper.common.utils.Success
import com.zodiactap.mapper.system.files.IFile
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import timber.log.Timber

class JavaFile(val file: File) : IFile {

    override val uri: String
        get() = file.path

    override val path: String
        get() = file.path

    override val name: String
        get() = file.name

    override val extension: String
        get() = file.extension

    override val baseName: String
        get() = file.nameWithoutExtension

    override val isFile: Boolean
        get() = file.isFile

    override val isDirectory: Boolean
        get() = file.isDirectory

    override fun inputStream(): InputStream {
        return file.inputStream()
    }

    override fun outputStream(): OutputStream {
        return file.outputStream()
    }

    override fun listFiles(): List<IFile>? {
        return file.listFiles()?.toList()?.map { JavaFile(it) }
    }

    override fun clear() {
        if (this.isFile) {
            file.recreateFile()
        }
    }

    override fun delete() {
        file.delete()
    }

    override fun exists(): Boolean {
        return file.exists()
    }

    override fun createFile() {
        val directories = this.path.substringBeforeLast('/')

        if (directories != this.path) {
            File(directories).mkdirs()
        }

        file.createNewFile()
    }

    override fun createDirectory() {
        file.mkdirs()
    }

    override suspend fun copyTo(directory: IFile, fileName: String?): KMResult<*> {
        val targetFile = File((directory as JavaFile).file, fileName ?: this.name)

        if (this.isDirectory) {
            file.copyRecursively(targetFile) { file, e ->
                Timber.e(e)
                OnErrorAction.TERMINATE
            }
        } else {
            file.copyTo(targetFile)
        }

        return Success(Unit)
    }
}
