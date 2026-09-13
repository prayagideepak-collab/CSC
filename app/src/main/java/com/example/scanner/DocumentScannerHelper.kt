package com.example.scanner

import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import android.content.Context

data class ExtractedDocumentMetadata(
    val rawExtractedName: String?,
    val detectedIdNumberMasked: String?,
    val inferredDocType: String
)

class DocumentScannerHelper(private val context: Context) {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    fun scanDocumentImage(
        imageUri: Uri,
        targetChecklistType: String,
        onSuccess: (ExtractedDocumentMetadata) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            val image = InputImage.fromFilePath(context, imageUri)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val fullText = visionText.text
                    val parsedMetadata = parseChecklistFields(fullText, targetChecklistType)
                    onSuccess(parsedMetadata)
                }
                .addOnFailureListener { exception ->
                    onError(exception)
                }
        } catch (e: Exception) {
            onError(e)
        }
    }

    private fun parseChecklistFields(ocrText: String, checklistType: String): ExtractedDocumentMetadata {
        val lines = ocrText.lines().map { it.trim() }.filter { it.isNotBlank() }
        
        var candidateName: String? = null
        var candidateIdNumber: String? = null

        val idRegex = Regex("[A-Z0-9]{8,15}")
        for (line in lines) {
            if (candidateIdNumber == null && idRegex.matches(line) && line.length >= 10) {
                candidateIdNumber = maskSensitiveIdDigits(line)
            }
            if (candidateName == null && line.length > 3 && !line.any { it.isDigit() }) {
                candidateName = line
            }
        }

        return ExtractedDocumentMetadata(
            rawExtractedName = candidateName,
            detectedIdNumberMasked = candidateIdNumber,
            inferredDocType = checklistType.uppercase()
        )
    }

    private fun maskSensitiveIdDigits(rawId: String): String {
        return if (rawId.length > 4) {
            "XXXX-XXXX-${rawId.takeLast(4)}"
        } else {
            "XXXX-XXXX"
        }
    }
}
