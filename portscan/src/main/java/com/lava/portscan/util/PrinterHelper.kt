package com.shersoft.portscan.util

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.*
import android.print.pdf.PrintedPdfDocument
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi

class PrinterHelper(val activity: Activity, val jobname: String) {
    fun doPrint() {
        activity?.also { context ->
            // Get a PrintManager instance
            val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
            // Set job name, which will be displayed in the print queue
            val jobName = "$jobname Document"
            // Start a print job, passing in a PrintDocumentAdapter implementation
            // to handle the generation of a print document

            val margins = PrintAttributes.Margins(0, 0, 0, 0)
            val myattribute = PrintAttributes.Builder().setMinMargins(margins).build()
            printManager.print(jobName, MyPrintDocumentAdapter(context), myattribute)
        }
    }

    private var mWebView: WebView? = null

    fun doWebViewPrint() {
        // Create a WebView object specifically for printing
        val webView = WebView(activity)
        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest) =
                false

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onPageFinished(view: WebView, url: String) {
                Log.i("TAG", "page finished loading $url")
                createWebPrintJob(view)
                mWebView = null
            }
        }

        /* Generate an HTML document on the fly:*/
        val htmlDocument = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Title</title>\n" +
                "    <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/paper-css/0.3.0/paper.css\">\n" +
                "\n" +
                "</head>\n" +
                "<style>@page {\n" +
                "    size: A5;\n" +
                "    margin: 0mm;\n" +
                "}\n" +
                "\n" +
                "html {\n" +
                "    background-color: #FFFFFF;\n" +
                "    margin: 0px; /* this affects the margin on the html before sending to printer */\n" +
                "}\n" +
                "\n" +
                "body {\n" +
                "    margin: 0mm; /* margin you want for the content */\n" +
                "}\n" +
                "\n" +
                "\n" +
                "</style>\n" +
                "<!--style=\"margin:0px;width: 148mm;height:210mm;transform: rotate(0deg);-->\n" +
                "<body class=\"A5\">\n" +
                "<tr>\n" +
                "    <td align=\"center\" colspan=\"5\">ESTIMATE</td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "    <td colspan=\"6\">\n" +
                "        <table width=\"100%\">\n" +
                "            <tr>\n" +
                "                <td>EntryNo : 1</td>\n" +
                "                <td align=\"right\">\n" +
                "                <td>Date : 21-10-2021</td>\n" +
                "                </td>\n" +
                "            </tr>\n" +
                "            <tr>\n" +
                "                <td>Customer :</td>\n" +
                "                <td align=\"right\">SAFVAN</td>\n" +
                "            </tr>\n" +
                "            <tr>\n" +
                "                <td>Address :</td>\n" +
                "                <td align=\"right\">PANDIKKA\n" +
                "        </table>\n" +
                "    </td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "    <td colspan=\"6\">\n" +
                "        <hr style=\"background-color: #fff;border-top: 2px dashed #8c8b8b;\">\n" +
                "    </td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "    <table width=\"100%\">\n" +
                "        <thead>\n" +
                "        <tr>\n" +
                "            <td align=center>Sl.no</td>\n" +
                "            <td align=center>Description</td>\n" +
                "            <td align=center>Rate</td>\n" +
                "            <td align=center>Qty</td>\n" +
                "            <td align=center>Total</td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td></td>\n" +
                "            <td align=\"center\"></td>\n" +
                "            <td align=\"center\"></td>\n" +
                "            <td align=\"center\"></td>\n" +
                "            <td align=\"center\"></td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td colspan=\"5\">\n" +
                "                <hr style=\"background-color: #fff;border-top: 2px dashed #8c8b8b;\">\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "        </thead>\n" +
                "        <tbody>\n" +
                "        <tr>\n" +
                "            <td>1</td>\n" +
                "            <td>അടക്കാമണിയൻ</td>\n" +
                "            <td align='right'>25</td>\n" +
                "            <td align='right'>2</td>\n" +
                "            <td align='right'>50.0</td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td>2</td>\n" +
                "            <td>അടലോടകം ഫ്രഷ്</td>\n" +
                "            <td align='right'>50</td>\n" +
                "            <td align='right'>3</td>\n" +
                "            <td align='right'>150.0</td>\n" +
                "        </tr>\n" +
                "        </tbody>\n" +
                "    </table>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "    <td colspan=\"6\">\n" +
                "        <hr style=\"background-color: #fff;border-top: 2px dashed #8c8b8b;\">\n" +
                "    </td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "    <td colspan=\"6\">\n" +
                "        <table width=\"100%\">\n" +
                "            <tr>\n" +
                "                <td>Grand Total :</td>\n" +
                "                <td align=\"right\"></td>\n" +
                "                <td align=\"right\">200.0</td>\n" +
                "            </tr>\n" +
                "        </table>\n" +
                "    </td>\n" +
                "</tr>\n" +
                "</body>\n" +
                "</html>\n";



        webView.loadDataWithBaseURL(null, htmlDocument, "text/HTML", "UTF-8", null)

        // Keep a reference to WebView object until you pass the PrintDocumentAdapter
        // to the PrintManager
        mWebView = webView
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun createWebPrintJob(webView: WebView) {

        // Get a PrintManager instance

        (activity?.getSystemService(Context.PRINT_SERVICE) as? PrintManager)?.let { printManager ->

            val jobName = "$jobname Document"

            // Get a print adapter instance
            val printAdapter = webView.createPrintDocumentAdapter(jobName)

            // Create a print job with name and adapter instance
            printManager.print(
                jobName,
                printAdapter,
                PrintAttributes.Builder().build()
            ).also { printJob ->

                // Save the job object for later status checking
//                printJobs += printJob
            }
        }
    }
}

class MyPrintDocumentAdapter(val context: Activity) : PrintDocumentAdapter() {
    lateinit var pdfDocument: PdfDocument
    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback,
        extras: Bundle?
    ) {
        // Create a new PdfDocument with the requested page attributes
        pdfDocument = PrintedPdfDocument(context, newAttributes)

        // Respond to cancellation request
        if (cancellationSignal?.isCanceled == true) {
            callback.onLayoutCancelled()
            return
        }

        // Compute the expected number of printed pages
        val pages = computePageCount(newAttributes)

        if (pages > 0) {
            // Return print information to print framework
            PrintDocumentInfo.Builder("print_output.pdf")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(pages)
                .build()
                .also { info ->
                    // Content layout reflow is complete
                    callback.onLayoutFinished(info, true)
                }
        } else {
            // Otherwise report an error to the print framework
            callback.onLayoutFailed("Page count calculation failed.")
        }
    }

    private fun computePageCount(printAttributes: PrintAttributes): Int {
        var itemsPerPage = 4 // default item count for portrait mode

        val pageSize = printAttributes.mediaSize
        if (!pageSize?.isPortrait!!) {
            // Six items per page in landscape orientation
            itemsPerPage = 6
        }

        // Determine number of print items
//        val printItemCount: Int = getPrintItemCount()

        return 1
//        return Math.ceil((printItemCount / itemsPerPage.toDouble())).toInt()
    }


    private fun drawPage(page: PdfDocument.Page) {
        page.canvas.apply {

            // units are in points (1/72 of an inch)
            val titleBaseLine = 72f
            val leftMargin = 54f

            val paint = Paint()
            paint.color = Color.BLACK
            paint.textSize = 36f
            drawText("Test Title", leftMargin, titleBaseLine, paint)

            paint.textSize = 11f
            drawText("Test paragraph", leftMargin, titleBaseLine + 25, paint)

            paint.color = Color.BLUE
            drawRect(100f, 100f, 172f, 172f, paint)
        }
    }

    override fun onWrite(
        pageRanges: Array<out PageRange>,
        destination: ParcelFileDescriptor,
        cancellationSignal: CancellationSignal?,
        callback: WriteResultCallback
    ) {
//        // Iterate over each page of the document,
//        // check if it's in the output range.
//        for (i in 0 until totalPages) {
//            // Check to see if this page is in the output range.
//            if (containsPage(pageRanges, i)) {
//                // If so, add it to writtenPagesArray. writtenPagesArray.size()
//                // is used to compute the next output page index.
//                writtenPagesArray.append(writtenPagesArray.size(), i)
//                pdfDocument?.startPage(i)?.also { page ->
//
//                    // check for cancellation
//                    if (cancellationSignal?.isCanceled == true) {
//                        callback.onWriteCancelled()
//                        pdfDocument?.close()
//                        pdfDocument = null
//                        return
//                    }
//
//                    // Draw page content for printing
//                    drawPage(page)
//
//                    // Rendering is complete, so page can be finalized.
//                    pdfDocument?.finishPage(page)
//                }
//            }
//        }
//
//        // Write PDF document to file
//        try {
//            pdfDocument?.writeTo(FileOutputStream(destination.fileDescriptor))
//        } catch (e: IOException) {
//            callback.onWriteFailed(e.toString())
//            return
//        } finally {
//            pdfDocument?.close()
//            pdfDocument = null
//        }
//        val writtenPages = computeWrittenPages()
//        // Signal the print framework the document is complete
//        callback.onWriteFinished(writtenPages)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onFinish() {
        super.onFinish()
    }
}