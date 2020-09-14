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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*


class SuratAdapter(val context: Context, val list: List<SuratContract>)
    : RecyclerView.Adapter<SuratAdapter.ViewHolder>(){

    lateinit var itemview: View

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        itemview = LayoutInflater.from(context).inflate(R.layout.item_surat, p0, false)
        return ViewHolder(itemview)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.bind(list[p1])
    }

    class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        fun bind(suratContract: SuratContract) {
            val sd_main = File("${Environment.getExternalStorageDirectory().toString()}/Hafalan")
            if (!sd_main.exists()) //cek folder
                sd_main.mkdir() //buat folder

            val sd = File("${suratContract.no} - ${suratContract.nama}.mp3")

            itemView.checkbox.isEnabled = sd.exists()
            if (sd.exists()) { //jika file ada
                itemView.btnDownload.visibility = View.GONE
                itemView.btnPlay.visibility = View.VISIBLE
            }else{//tidak ada file
                itemView.btnDownload.visibility = View.VISIBLE
                itemView.btnPlay.visibility = View.GONE
            }

            itemView.tvName.text = "${suratContract.no} - ${suratContract.nama}"
            itemView.tvDetail.text = "${suratContract.ayat} - ${suratContract.arti}"

            itemView.btnDownload.setOnClickListener {
                download(suratContract.no, suratContract.nama)
            }
        }

        fun download(no: String, nama: String) {
            val nomor = String.format("%03d", no.toInt())
            Log.e("nomor", "" + nomor)
            ApiMain().services.downloadFile("mishary-rashid-alafasy-${nomor}-muslimcentral.com.mp3")
                ?.enqueue(object : Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                    }

                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            Log.e("download", "server contacted and has file")
                            val writtenToDisk: Boolean = writeResponseBodyToDisk(response.body()!!, "${no} - ${nama}.mp3")
                            Log.e("download", "file download was a success? $writtenToDisk")
//                            if (writtenToDisk)
                        } else {
                            Log.e("download", "server contact failed")
                        }
                    }
                })
        }

        private fun writeResponseBodyToDisk(body: ResponseBody, nama :String): Boolean {
            return try {
                // todo change the file location/name according to your needs
                val futureStudioIconFile =
                    File("${Environment.getExternalStorageDirectory()}/Hafalan/${nama}")
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
    }
}