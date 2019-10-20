package `in`.heis.abibierpass

import android.graphics.Bitmap
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import kotlinx.android.synthetic.main.fragment_bottom_sheet.*


class BottomSheetFragment : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_sheet, container, false)
    }

    companion object {
        const val FRAGMENT_TAG = "bottom_sheet_fragment_tag"
        var sheet_head_txt = "Head"
        var sheet_body_txt = "Body"
        var sheet_qr_enable = true
        var sheet_qr_content = ""
        var sheet_qr_height = 400
        var sheet_qr_width = 400
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sheet_body.text = sheet_body_txt
        sheet_head.text = sheet_head_txt
        sheet_qr.isVisible = sheet_qr_enable

        try {
            val bitmap = encodeAsBitmap(sheet_qr_content)
            sheet_qr.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

    @Throws(WriterException::class)
    fun encodeAsBitmap(str: String): Bitmap? {
        val result: BitMatrix
        try {
            result = MultiFormatWriter().encode(
                str,
                BarcodeFormat.QR_CODE,
                sheet_qr_width,
                sheet_qr_height,
                null
            )
        } catch (iae: IllegalArgumentException) {
            sheet_qr.isVisible = false
            return null
        }
        val width = result.width
        val height = result.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (result.get(x, y)) BLACK else WHITE
            }
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }
}
