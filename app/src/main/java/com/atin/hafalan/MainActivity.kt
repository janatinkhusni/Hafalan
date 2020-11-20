package com.atin.hafalan

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.atin.hafalan.response.Surats
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.db.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    var adapter: SuratAdapter? = null
    private val RECORD_REQUEST_CODE = 101
    private lateinit var mediaPlayer: MediaPlayer
    private var pause:Boolean = false
    val path = "${Environment.getExternalStorageDirectory()}/Hafalan/"
//    var sessionManager = SessionManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setupPermissions()
//        sessionManager = SessionManager(this)

        if (getListSurat().size != 114){
            NetworkConfig().getService()
                .getSurat()
                .enqueue(object : Callback<Surats> {
                    override fun onFailure(call: Call<Surats>, t: Throwable) {
                        Toast.makeText(this@MainActivity, t.localizedMessage, Toast.LENGTH_SHORT)
                            .show()
                    }

                    override fun onResponse(call: Call<Surats>, response: Response<Surats>) {
                        dropTableSurat()
                        for (i in 0 until response.body()?.hasil?.size!!) {
                            val surat = response.body()!!.hasil.get(i)
                            Log.e("Surat", "" + surat.nomor + surat.nama + surat.ayat + surat.arti)
                            insertSurat(surat.nomor, surat.nama, surat.ayat, surat.arti)
                        }
                        toast("Berhasil menambahkan surat")
                        loadList()
                    }
                })
        } else {
            loadList()
        }

        btnPlay.setOnClickListener {
            btnPlay.visibility = View.GONE
            btnPause.visibility = View.VISIBLE

            if(pause){
                mediaPlayer.seekTo(mediaPlayer.currentPosition)
                mediaPlayer.start()
                pause = false
            }else{
                playSurat()
            }
        }

        btnPause.setOnClickListener {
            pause = true
            mediaPlayer.pause()
            btnPlay.visibility = View.VISIBLE
            btnPause.visibility = View.GONE
        }

        btnStop.setOnClickListener {
            pause = false
            mediaPlayer.stop()
            btnPlay.visibility = View.VISIBLE
            btnPause.visibility = View.GONE
        }

        btnNext.setOnClickListener {
//            sessionManager.suratKe++
            SessionManager(this).suratKe++
            playSurat()
        }

        btnPrevious.setOnClickListener {
//            sessionManager.suratKe--
            SessionManager(this).suratKe--
            playSurat()
        }

//        mediaPlayer.setOnCompletionListener {
////            sessionManager.suratKe++
//            SessionManager(this).suratKe++
//            playSurat()
//        }
    }

    private fun dropTableSurat(){
        database.use {
            dropTable(SuratContract.TABLE_SURAT)
        }

        database.use {
            createTable(
                SuratContract.TABLE_SURAT,
                true,
                SuratContract.ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                SuratContract.NO to TEXT,
                SuratContract.NAMA to TEXT,
                SuratContract.AYAT to TEXT,
                SuratContract.ARTI to TEXT,
                SuratContract.PLAY to BLOB,
                SuratContract.DOWNLOAD to BLOB
            )
        }
    }

    private fun insertSurat(no: String, nama: String, ayat: String, arti: String) {
        database.use {
            insert(
                SuratContract.TABLE_SURAT,
                SuratContract.NO to no,
                SuratContract.NAMA to nama,
                SuratContract.AYAT to ayat,
                SuratContract.ARTI to arti,
                SuratContract.PLAY to false,
                SuratContract.DOWNLOAD to false
            )
        }
    }

    private fun updatePlay(play: Boolean) {
        database.use {
            update(SuratContract.TABLE_SURAT, SuratContract.PLAY to play)
                .where(
                    "${SuratContract.PLAY} = {${SuratContract.PLAY}}",
                    SuratContract.PLAY to !play
                )
                .exec()
        }
        adapter?.updateList()
    }

    private fun updateDownload() {
        database.use {
            update(SuratContract.TABLE_SURAT, SuratContract.DOWNLOAD to true)
                .where(
                    "${SuratContract.DOWNLOAD} = {${SuratContract.DOWNLOAD}}",
                    SuratContract.DOWNLOAD to false
                )
                .exec()
        }
        adapter?.updateList()
    }

    fun toast(txt: String){
        Toast.makeText(this, txt, Toast.LENGTH_SHORT).show()
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("permission", "Permission to record denied")
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            RECORD_REQUEST_CODE
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id = item.getItemId()

        if (id == R.id.action_download) {
            updateDownload()
            return true
        } else if (id == R.id.action_check) {
            updatePlay(true)
            return true
        } else if (id == R.id.action_uncheck) {
            updatePlay(false)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun loadList(){
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = SuratAdapter(this)
        rv_surat.layoutManager = layoutManager
        rv_surat.adapter = adapter
    }

    fun getListSurat(): List<SuratContract> {
        var listData: List<SuratContract>? = null
        database.use {
            val result = select(SuratContract.TABLE_SURAT)
            listData = result.parseList(classParser())
        }
        return listData!!
    }

    fun playSurat(){
        val listSurat = getListSurat()
//        for (q in sessionManager.suratKe until listSurat.size){
        for (q in  SessionManager(this).suratKe until listSurat.size){
            val surat = listSurat.get(q)
            if (surat.play){
                val nomor = String.format("%03d", surat.no)
                mediaPlayer = MediaPlayer.create(this, Uri.parse("${path}${nomor} - ${surat.nama}.mp3"));
                mediaPlayer.start()
                break
            }
        }

        SessionManager(this).suratKe == 0
//        sessionManager.suratKe = 0
        playSurat()
    }
}