package com.example.perfilapp.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import java.io.ByteArrayOutputStream

/** Funciones para preparar la imagen antes de enviarla al backend (todo en memoria). */
object ImagenUtils {

    private const val LADO_MAXIMO = 800

    /** Lee la imagen elegida de la galería, la reduce y corrige su orientación. */
    fun uriABitmap(context: Context, uri: Uri): Bitmap? {
        val resolver = context.contentResolver

        // 1. Leer solo el tamaño para no cargar una imagen enorme en memoria
        val opciones = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opciones) }

        var muestra = 1
        while (maxOf(opciones.outWidth, opciones.outHeight) / (muestra * 2) >= LADO_MAXIMO) {
            muestra *= 2
        }

        // 2. Cargar la imagen ya reducida
        val bitmap = resolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = muestra })
        } ?: return null

        // 3. Girar la imagen si la foto fue tomada de lado
        val grados = resolver.openInputStream(uri)?.use {
            when (ExifInterface(it).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }
        } ?: 0f

        if (grados == 0f) return bitmap
        val matriz = Matrix().apply { postRotate(grados) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matriz, true)
    }

    /** Convierte el Bitmap a bytes JPEG para enviarlo al backend. */
    fun bitmapAJpeg(bitmap: Bitmap): ByteArray {
        val reducido = reducir(bitmap)
        val salida = ByteArrayOutputStream()
        reducido.compress(Bitmap.CompressFormat.JPEG, 85, salida)
        return salida.toByteArray()
    }

    private fun reducir(bitmap: Bitmap): Bitmap {
        val mayor = maxOf(bitmap.width, bitmap.height)
        if (mayor <= LADO_MAXIMO) return bitmap
        val escala = LADO_MAXIMO.toFloat() / mayor
        return Bitmap.createScaledBitmap(
            bitmap,
            (bitmap.width * escala).toInt(),
            (bitmap.height * escala).toInt(),
            true
        )
    }
}
