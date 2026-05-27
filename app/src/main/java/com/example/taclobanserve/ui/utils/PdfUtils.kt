package com.example.taclobanserve.ui.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import com.example.taclobanserve.CheckInRequest
import com.example.taclobanserve.TaclobanUser
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

fun exportProfileToPdf(context: Context, user: TaclobanUser, verifiedCheckIns: List<CheckInRequest>) {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
    val page = pdfDocument.startPage(pageInfo)
    val canvas: Canvas = page.canvas
    val paint = Paint()
    val titlePaint = Paint()

    // Background
    canvas.drawColor(Color.WHITE)

    // Header
    titlePaint.color = Color.rgb(244, 81, 30) // themeOrange
    titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    titlePaint.textSize = 24f
    canvas.drawText("TaclobanServe", 40f, 60f, titlePaint)
    
    paint.color = Color.GRAY
    paint.textSize = 12f
    canvas.drawText("Official Service Transcript", 40f, 80f, paint)
    
    // Line separator
    paint.strokeWidth = 2f
    canvas.drawLine(40f, 100f, 555f, 100f, paint)

    // User Info
    paint.color = Color.BLACK
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    paint.textSize = 18f
    canvas.drawText(user.name, 40f, 140f, paint)
    
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    paint.textSize = 12f
    paint.color = Color.DKGRAY
    canvas.drawText("Volunteer ID: ${user.volunteerId}", 40f, 160f, paint)
    canvas.drawText("Profession: ${user.profession}", 40f, 180f, paint)
    canvas.drawText("Member since: ${user.joinedYear}", 40f, 200f, paint)

    // Stats Section
    paint.color = Color.rgb(244, 81, 30)
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    val totalHours = verifiedCheckIns.sumOf { it.earnedHours }
    canvas.drawText("Summary Statistics", 40f, 240f, paint)
    
    paint.color = Color.BLACK
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    canvas.drawText("Total Service Hours: ${String.format("%.1f", totalHours)}", 40f, 265f, paint)
    canvas.drawText("Total Projects Completed: ${verifiedCheckIns.size}", 40f, 285f, paint)

    // Service History Table
    paint.color = Color.rgb(15, 23, 42) // navyDark
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    canvas.drawText("Verified Service History", 40f, 330f, paint)
    
    paint.strokeWidth = 1f
    canvas.drawLine(40f, 340f, 555f, 340f, paint)
    
    var yPos = 365f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    paint.textSize = 10f
    
    if (verifiedCheckIns.isEmpty()) {
        canvas.drawText("No verified records found.", 40f, yPos, paint)
    } else {
        verifiedCheckIns.forEach { req ->
            if (yPos > 780f) return@forEach // Basic overflow protection
            canvas.drawText("${req.timestamp.split(",").first()} - ${req.eventTitle}", 40f, yPos, paint)
            canvas.drawText("${req.earnedHours} hrs", 500f, yPos, paint)
            yPos += 25f
        }
    }

    // Footer
    paint.color = Color.LTGRAY
    paint.textSize = 8f
    val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    canvas.drawText("This is an electronically generated document. Verification ID: BT-${user.volunteerId}-${System.currentTimeMillis() % 10000}", 40f, 810f, paint)
    canvas.drawText("Generated on: $timestamp", 40f, 825f, paint)

    pdfDocument.finishPage(page)

    // Save to device
    val fileName = "Transcript_${user.name.replace(" ", "_")}.pdf"
    val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

    try {
        pdfDocument.writeTo(FileOutputStream(filePath))
        Toast.makeText(context, "PDF saved to Documents folder", Toast.LENGTH_LONG).show()
        println("PDF SAVED TO: ${filePath.absolutePath}")
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Error saving PDF: ${e.message}", Toast.LENGTH_SHORT).show()
    }

    pdfDocument.close()
}