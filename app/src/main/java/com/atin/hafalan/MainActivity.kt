package com.atin.hafalan

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupPermissions()

        Log.e("Response",""+getListSurat().size)

        if (getListSurat().size != 114){
            NetworkConfig().getService()
                .getSurat()
                .enqueue(object : Callback<Surats> {
                    override fun onFailure(call: Call<Surats>, t: Throwable) {
                        Toast.makeText(this@MainActivity, t.localizedMessage, Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call<Surats>, response: Response<Surats>) {
                        dropTableSurat()
                        for (i in 0 until response.body()?.hasil?.size!!){
                            val surat = response.body()!!.hasil.get(i)
                            Log.e("Surat",""+surat.nomor+surat.nama+surat.ayat+surat.arti)
                            insertSurat(surat.nomor, surat.nama, surat.ayat, surat.arti, false)
                        }
                        Log.e("Response",""+getListSurat().size)
                        toast("Berhasil menambahkan surat")
                    }
                })
        } else {
            val list = getListSurat()
            val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            adapter = SuratAdapter(this, list)
            rv_surat.layoutManager = layoutManager
            rv_surat.adapter = adapter
        }
    }

    private fun getListSurat(): List<SuratContract> {
        var listData: List<SuratContract>? = null
        database.use {
            val result = select(SuratContract.TABLE_SURAT)
            listData = result.parseList(classParser<SuratContract>())
        }
        return listData!!
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
                SuratContract.PLAY to BLOB
            )
        }
    }

    private fun insertSurat(no: String?, nama: String, ayat: String, arti: String, play: Boolean) {
        database.use {
            insert(SuratContract.TABLE_SURAT,
                SuratContract.NO to no,
                SuratContract.NAMA to nama,
                SuratContract.AYAT to ayat,
                SuratContract.ARTI to arti,
                SuratContract.PLAY to false
            )
        }
    }

    fun toast(txt: String){
        Toast.makeText(this,txt,Toast.LENGTH_SHORT).show()
    }

    fun update(){
        val listRefresh = getListSurat()
        adapter = SuratAdapter(this, listRefresh)
        adapter?.notifyDataSetChanged()
        rv_surat.adapter = adapter
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("permission", "Permission to record denied")
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            RECORD_REQUEST_CODE)
    }
}