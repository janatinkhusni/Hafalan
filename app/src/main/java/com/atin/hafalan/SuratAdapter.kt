package com.atin.hafalan

import android.content.Context
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_surat.view.*
import okhttp3.ResponseBody
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.update
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*

class SuratAdapter(val context: Context) : RecyclerView.Adapter<SuratAdapter.Holder>(){

    lateinit var itemview: View
    var list = getListSurat()
    val path = "${Environment.getExternalStorageDirectory()}/Hafalan"

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): Holder {
        itemview = LayoutInflater.from(context).inflate(R.layout.item_surat, p0, false)
        return Holder(itemview)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val view = holder.view
        val surat = list[position]

        view.btnLoading.visibility = View.GONE
        view.tvName.text = "${surat.no} - ${surat.nama}"
        view.tvDetail.text = "${surat.ayat} - ${surat.arti}"

        view.btnDownload.setOnClickListener {
            view.btnDownload.visibility = View.GONE
            view.btnLoading.visibility = View.VISIBLE
            download(surat.no, surat.nama, view, surat)
        }

//        if (surat.download){
//            if (!sd.exists()){
//                view.btnDownload.visibility = View.GONE
//                view.btnLoading.visibility = View.VISIBLE
//                download(surat.no, surat.nama)
//            }
//        }

        loadStatus(view, surat)
    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view)

    fun download(no: String, nama: String, view: View, surat: SuratContract) {
        val nomor = String.format("%03d", no.toInt())
        Log.e("nomor", "" + nomor)
        ApiMain().services.downloadFile("mishary-rashid-alafasy-${nomor}-muslimcentral.com.mp3")
            ?.enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    notifyDataSetChanged()
                }

                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Log.e("download", "server contacted and has file")
                        val writtenToDisk: Boolean = writeResponseBodyToDisk(response.body()!!, "${no} - ${nama}.mp3")
                        Log.e("download", "file download was a success? $writtenToDisk")
                        if (writtenToDisk) {
                            loadStatus(view, surat)
                            updateDownload(no)
                        }
                    } else {
                        Log.e("download", "server contact failed")
                    }
                }
            })
    }

    private fun writeResponseBodyToDisk(body: ResponseBody, nama :String): Boolean {
        return try {
            // todo change the file location/name according to your needs
            val futureStudioIconFile = File("${Environment.getExternalStorageDirectory()}/Hafalan/${nama}")
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                var fileSizeDownloaded: Long = 0
                inputStream = body.byteStream()
                outputStream = FileOutputStream(futureStudioIconFile)
                while (true) {
                    val read: Int = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                }
                outputStream.flush()
                true
            } catch (e: IOException) {
                Log.e("download", "catch2 ${e.message}")
                false
            } finally {
                if (inputStream != null) {
                    inputStream.close()
                }
                if (outputStream != null) {
                    outputStream.close()
                }
            }
        } catch (e: IOException) {
            Log.e("download", "catch ${e.message}")
            false
        }
    }

    private fun updateDownload(no: String) {
        context.database.use {
            update(SuratContract.TABLE_SURAT, SuratContract.DOWNLOAD to false)
                .where(
                    "${SuratContract.NO} = {${SuratContract.NO}}",
                    SuratContract.NO to no
                )
                .exec()
        }
    }

    fun updateList(){
        list = getListSurat()
        notifyDataSetChanged()
    }

    fun getListSurat(): List<SuratContract> {
        var listData: List<SuratContract>? = null
        context.database.use {
            val result = select(SuratContract.TABLE_SURAT)
            listData = result.parseList(classParser())
        }
        return listData!!
    }

    fun loadStatus(view: View, surat: SuratContract){ //download atau sedang progress
        val sd_main = File(path)
        if (!sd_main.exists()) //cek folder
            sd_main.mkdir() //buat folder
        val sd = File(sd_main,"${surat.no} - ${surat.nama}.mp3")

        view.checkbox.isEnabled = sd.exists()
        Log.e("cek_file", ""+sd.exists())

        if (sd.exists()) { //jika file ada
            view.btnDownload.visibility = View.GONE
            view.btnLoading.visibility = View.GONE
            view.checkbox.isChecked = surat.play
        }else{//tidak ada file
            view.btnDownload.visibility = View.VISIBLE
            view.checkbox.isChecked = false
        }
    }
}