package com.atin.hafalan

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
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
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.atin.hafalan.response.Surats
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_surat.view.*
import kotlinx.android.synthetic.main.layout_persistent_bottom_sheet.*
import kotlinx.android.synthetic.main.player_sheet.*
import org.jetbrains.anko.db.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    var adapter: SuratAdapter? = null
    private val RECORD_REQUEST_CODE = 101
    private lateinit var mediaPlayer: MediaPlayer
    val path = "${Environment.getExternalStorageDirectory()}/Hafalan/"
    var lengthMediaPlayer = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setupPermissions()
        toolbar_title.typeface = ResourcesCompat.getFont(this, R.font.khodijah_free)
        mediaPlayer = MediaPlayer()

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
                            insertSurat(surat.nomor, surat.nama, surat.asma, surat.ayat, surat.arti)
                        }
                        toast("Berhasil menambahkan surat")
                        loadList()
                    }
                })
        } else {
            loadList()
        }

        btnPause_.setOnClickListener {
            lengthMediaPlayer = mediaPlayer.currentPosition
            mediaPlayer.pause()
            loadPlayer(false)
        }

        btnPause.setOnClickListener {
            lengthMediaPlayer = mediaPlayer.currentPosition
            mediaPlayer.pause()
            loadPlayer(false)
        }

        tvName.typeface = ResourcesCompat.getFont(this, R.font.champagne_limousines_bold)
        tvDetail.typeface = ResourcesCompat.getFont(this, R.font.champagne_limousines_bold)

        btnPlay.setOnClickListener {
            putarSurat(false, true)
        }

        btnPlay_.setOnClickListener {
            putarSurat(false, true)
        }

        btnNext.setOnClickListener {
            putarSurat(true, true)
        }

        btnBack.setOnClickListener {
            putarSurat(true, false)
        }

        val suratKe = SessionManager(this).suratKe

        if (suratKe != -1){
            loadPlay(getListSurat()[suratKe])
        }else{
            lvPlayer.visibility = View.GONE
        }
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
                SuratContract.ASMA to TEXT,
                SuratContract.AYAT to TEXT,
                SuratContract.ARTI to TEXT,
                SuratContract.PLAY to BLOB,
                SuratContract.DOWNLOAD to BLOB
            )
        }
    }

    private fun insertSurat(no: String, nama: String, asma: String, ayat: String, arti: String) {
        database.use {
            insert(
                SuratContract.TABLE_SURAT,
                SuratContract.NO to no,
                SuratContract.NAMA to nama,
                SuratContract.ASMA to asma,
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

////        for (q in sessionManager.suratKe until listSurat.size){
//        for (q in  SessionManager(this).suratKe until listSurat.size){
//            val surat = listSurat.get(q)
//            if (surat.play){
//                val nomor = String.format("%03d", surat.no)
//                mediaPlayer = MediaPlayer.create(
//                    this,
//                    Uri.parse("${path}${nomor} - ${surat.nama}.mp3")
//                );
//                mediaPlayer.start()
//                break
//            }
//        }
//
//        SessionManager(this).suratKe == 0
////        sessionManager.suratKe = 0
//        playSurat()
//    }

    fun cekPilihan(): Boolean{ //cek ada surat yg dipilih atau tidak
        val listSurat = getListSurat()
        for (q in 0 until listSurat.size){
            if (listSurat.get(q).play){
                return true
            }
        }
        return false
    }
    
    fun listSuratPilihan(): List<SuratContract> {
        val listSurat = getListSurat()
        var listSuratPilihan = mutableListOf<SuratContract>()
        for (q in 0 until listSurat.size){
            if (listSurat[q].play){
                listSuratPilihan.add(listSurat[q])
            }
        }
        return listSuratPilihan
    }

    fun nextSurat(next: Boolean): SuratContract{
        val listSurat = getListSurat()
        var suratKe = SessionManager(this).suratKe

        if (next) {
            if (suratKe == listSurat.size-1) suratKe = 0
            else suratKe++
        }

        for (q in suratKe until listSurat.size){
            if (listSurat[q].play){
                SessionManager(this).suratKe = q
                return listSurat[q]
            }
        }

        return nextSurat(false)
    }

    fun backSurat(): SuratContract{
        val listSurat = getListSurat()
        var suratKe = SessionManager(this).suratKe

        if (suratKe == 0) suratKe = listSurat.size-1
        else suratKe--

        for (q in suratKe downTo 0){
            if (listSurat[q].play){
                SessionManager(this).suratKe = q
                return listSurat[q]
            }
        }

        SessionManager(this).suratKe = listSurat.size
        return backSurat()
    }

    fun loadPlay(surat: SuratContract){
        tvName.text = "${surat.no} - ${surat.nama}"
        tvDetail.text = "${surat.ayat} - ${surat.arti}"
        tvName_.text = surat.asma
        tvName_2.text = surat.nama
        tvDetail_.text = "(${surat.arti})"
        tvDetail_2.text = "Surat ke ${surat.no} Jumlah Ayat ${surat.ayat}"
    }

    fun loadPlayer(status: Boolean){
        if (status){
            lengthMediaPlayer = 0
            btnPlay_.visibility = View.GONE
            btnPlay.visibility = View.GONE
            btnPause_.visibility = View.VISIBLE
            btnPause.visibility = View.VISIBLE
        }else{
            btnPlay_.visibility = View.VISIBLE
            btnPlay.visibility = View.VISIBLE
            btnPause_.visibility = View.GONE
            btnPause.visibility = View.GONE
        }
    }

    fun playTrack(surat: SuratContract){
        val nomor = String.format("%03d", surat.no.toInt())
        if (mediaPlayer == null){
            mediaPlayer = MediaPlayer.create(
                this,
                Uri.parse("${path}${nomor} - ${surat.nama}.mp3")
            )
//        mediaPlayer.apply {
//            setDataSource(applicationContext,
//                // 1
//                Uri.parse("${path}${nomor} - ${surat.nama}.mp3"))
//        }
            mediaPlayer.setOnCompletionListener {
                putarSurat(true, true)
//                if(cekPilihan()){
//                    surat = nextSurat(true)
//                    loadPlay(surat)
//                }else{
//                    toast("Tidak ada surat yang dipilih")
//                }
            }
//            mp.setOnCompletionListener(OnCompletionListener { performOnEnd() })
            mediaPlayer.start()
        }else{
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.setDataSource(this, Uri.parse("${path}${nomor} - ${surat.nama}.mp3"))
            mediaPlayer.prepare();
            mediaPlayer.start();
        }
    }

    fun resumeTrack(){
        mediaPlayer.seekTo(lengthMediaPlayer)
        mediaPlayer.start()
    }

    fun putarSurat(next: Boolean, typeNext: Boolean){//next = putar surat sekarang atau pindah surat
        // typeNext : NextTrack atau backtrack
        if(cekPilihan()){
            if (lengthMediaPlayer>0){
                resumeTrack()
            }else{
                val surat : SuratContract? = null
                if (typeNext){
                    nextSurat(next)
                }else{
                    backSurat()
                }
                loadPlay(surat!!)
                lvPlayer.visibility = View.VISIBLE
                playTrack(surat)
                loadPlayer(true)
            }
        }else{
            toast("Tidak ada surat yang dipilih")
        }
    }
}