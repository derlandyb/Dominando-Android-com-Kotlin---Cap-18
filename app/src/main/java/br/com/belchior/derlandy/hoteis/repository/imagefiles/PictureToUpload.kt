package br.com.belchior.derlandy.hoteis.repository.imagefiles

import okhttp3.MediaType
import java.io.File

data class PictureToUpload(
    val imageFile: File,
    val fileType: MediaType
)