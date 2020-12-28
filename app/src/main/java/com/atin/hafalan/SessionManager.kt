package com.atin.hafalan

import android.content.Context
import android.content.SharedPreferences

class SessionManager(_context: Context) {
    // Shared Preferences
    private val pref: SharedPreferences
    private val editor: SharedPreferences.Editor
    private val KEY_Help = "help"
    private val KEY_LONG = "longitude"
    private val KEY_LATI = "latitude"
    private val KEY_LONG_TENANT = "longitude_tenant"
    private val KEY_LATI_TENANT = "latitude_tenant"
    private val KEY_BOOKING_CODE = "bookingCode"
    private val KEY_EFORM = "eform"
    private val KEY_CODE_EFORM = "codeEform"
    private val KEY_APIKEY = "apikey"
    private val KEY_IDJT = "idjt"
    private val KEY_TTS = "tts"
    private val KEY_FULL_NAME = "fullName"
    private val KEY_NIK = "NIK"
    private val KEY_PHONE_FORM = "phoneForm"
    private val KEY_REK = "rek"
    private val KEY_NAMA_TENANT = "namaTenant"
    private val KEY_TENANT = "tenant"
    private val KEY_READY = "ready"
    private val KEY_SPEAK = "speak"
    private val KEY_SURAT_KE = "suratKe"

    var isHelp: Boolean?
        get() = pref.getBoolean(KEY_Help, false)
        set(sudah) {
            editor.putBoolean(KEY_Help, sudah!!).apply()
        }
    var longitude: String?
        get() = pref.getString(KEY_LONG, "0.0")
        set(longitude) {
            editor.putString(KEY_LONG, longitude).apply()
        }
    var latitude: String?
        get() = pref.getString(KEY_LATI, "0.0")
        set(latitude) {
            editor.putString(KEY_LATI, latitude).apply()
        }
    var bookingCode: String?
        get() = pref.getString(KEY_BOOKING_CODE, null)
        set(bookingCode) {
            editor.putString(KEY_BOOKING_CODE, bookingCode).apply()
        }
    var eform: String?
        get() = pref.getString(KEY_EFORM, null)
        set(eform) {
            editor.putString(KEY_EFORM, eform).apply()
        }
    var codeEform: String?
        get() = pref.getString(KEY_CODE_EFORM, "")
        set(codeEform) {
            editor.putString(KEY_CODE_EFORM, codeEform).apply()
        }

    fun saveApikey(apikey: String?, ke: Int) {
        editor.putString(KEY_APIKEY + ke, apikey).apply()
    }

    fun getApikey(ke: Int): String? {
        return pref.getString(KEY_APIKEY + ke, null)
    }

    var jumlahApikey: Int
        get() = pref.getInt(KEY_APIKEY, 0)
        set(jumlah) {
            editor.putInt(KEY_APIKEY, jumlah).apply()
        }
    var idJTx: String?
        get() = pref.getString(KEY_IDJT, null)
        set(id) {
            editor.putString(KEY_IDJT, id).apply()
        }
    var tts: String?
        get() = pref.getString(KEY_TTS, null)
        set(tts) {
            editor.putString(KEY_TTS, tts).apply()
        }
    var fullName: String?
        get() = pref.getString(KEY_FULL_NAME, null)
        set(fullName) {
            editor.putString(KEY_FULL_NAME, fullName).apply()
        }
    var nIK: String?
        get() = pref.getString(KEY_NIK, null)
        set(nik) {
            editor.putString(KEY_NIK, nik).apply()
        }
    var phoneForm: String?
        get() = pref.getString(KEY_PHONE_FORM, null)
        set(phone) {
            editor.putString(KEY_PHONE_FORM, phone).apply()
        }
    var rek: String?
        get() = pref.getString(KEY_REK, null)
        set(rek) {
            editor.putString(KEY_REK, rek).apply()
        }

    //    public void setLongitudeTenant(String longitude) {
    //        editor.putString(KEY_LONG_TENANT,longitude).apply();
    //    }
    //
    //    public String getLongitudeTenant(){
    //        return pref.getString(KEY_LONG_TENANT,"0.0");
    //    }
    //
    //    public void setLatitudeTenant(String latitude) {
    //        editor.putString(KEY_LATI_TENANT,latitude).apply();
    //    }
    //
    //    public String getLatitudeTenant(){
    //        return pref.getString(KEY_LATI_TENANT,"0.0");
    //    }
    //    public void setNamaTenant(String namaTenant) {
    //        editor.putString(KEY_NAMA_TENANT,namaTenant).apply();
    //    }
    //
    //    public String getNamaTenant(){
    //        return pref.getString(KEY_NAMA_TENANT,"");
    //    }
    val isCheckin: Boolean
        get() {
            val checkin = pref.getInt(KEY_TENANT + "_size", 0)
            return if (checkin == 0) {
                true
            } else {
                false
            }
        }

    fun resetCheckIn() {
        editor.remove(KEY_TENANT).remove(KEY_NAMA_TENANT).remove(KEY_BOOKING_CODE)
            .remove(KEY_LATI_TENANT)
            .remove(KEY_LONG_TENANT).apply()
        editor.remove(KEY_TENANT + "_size").remove(KEY_NAMA_TENANT + "_size")
            .remove(KEY_BOOKING_CODE + "_size")
            .remove(KEY_LATI_TENANT + "_size").remove(KEY_LONG_TENANT + "_size").apply()
    }

    fun isCheckin2(): Int {
        return pref.getInt(KEY_TENANT + "_size", 0)
    }

    //    public void setTenant(String tenant) {
    //        editor.putString(KEY_TENANT,tenant).apply();
    //    }
    //
    //    public String getTenant(){
    //        return pref.getString(KEY_TENANT,"");
    //    }
    //    public void setIdLayanan(String idLayanan) {
    //        editor.putString(KEY_ID_LAYANAN,idLayanan).apply();
    //    }
    //
    //    public String getIdLayanan(){
    //        return pref.getString(KEY_ID_LAYANAN,"");
    //    }
    //
    //    public void setNoAntri(String noAntri) {
    //        editor.putString(KEY_NO_ANTRIAN,noAntri).apply();
    //    }
    //
    //    public String getNoAntri(){
    //        return pref.getString(KEY_NO_ANTRIAN,"");
    //    }
    //
    //    public void setJam(String jam) {
    //        editor.putString(KEY_JAM,jam).apply();
    //    }
    //
    //    public String getJam(){
    //        return pref.getString(KEY_JAM,"");
    //    }
    fun isReady(): Boolean {
        return pref.getBoolean(KEY_READY, false)
    }

    fun setReady(ready: Boolean?) {
        editor.putBoolean(KEY_READY, ready!!).apply()
    }

    fun isSpeak(): Boolean {
        return pref.getBoolean(KEY_SPEAK, false)
    }

    fun setSpeak(speak: Boolean?) {
        editor.putBoolean(KEY_SPEAK, speak!!).apply()
    }

    fun saveArrayTenant(array: Array<String?>): Boolean {
        editor.putInt(KEY_TENANT + "_size", array.size)
        for (i in array.indices) editor.putString(KEY_TENANT + "_" + i, array[i])
        return editor.commit()
    }

    fun loadArrayTenant(): Array<String?> {
        val size = pref.getInt(KEY_TENANT + "_size", 0)
        val array = arrayOfNulls<String>(size)
        for (i in 0 until size) array[i] = pref.getString(KEY_TENANT + "_" + i, "")
        return array
    }

    fun saveArrayNamaTenant(array: Array<String?>): Boolean {
        editor.putInt(KEY_NAMA_TENANT + "_size", array.size)
        for (i in array.indices) editor.putString(KEY_NAMA_TENANT + "_" + i, array[i])
        return editor.commit()
    }

    fun loadArrayNamaTenant(): Array<String?> {
        val size = pref.getInt(KEY_NAMA_TENANT + "_size", 0)
        val array = arrayOfNulls<String>(size)
        for (i in 0 until size) array[i] = pref.getString(KEY_NAMA_TENANT + "_" + i, "")
        return array
    }

    fun saveArrayLong(array: Array<String?>): Boolean {
        editor.putInt(KEY_LONG_TENANT + "_size", array.size)
        for (i in array.indices) editor.putString(KEY_LONG_TENANT + "_" + i, array[i])
        return editor.commit()
    }

    fun loadArrayLong(): Array<String?> {
        val size = pref.getInt(KEY_LONG_TENANT + "_size", 0)
        val array = arrayOfNulls<String>(size)
        for (i in 0 until size) array[i] = pref.getString(KEY_LONG_TENANT + "_" + i, "0.0")
        return array
    }

    fun saveArrayLat(array: Array<String?>): Boolean {
        editor.putInt(KEY_LATI_TENANT + "_size", array.size)
        for (i in array.indices) editor.putString(KEY_LATI_TENANT + "_" + i, array[i])
        return editor.commit()
    }

    fun loadArrayLat(): Array<String?> {
        val size = pref.getInt(KEY_LATI_TENANT + "_size", 0)
        val array = arrayOfNulls<String>(size)
        for (i in 0 until size) array[i] = pref.getString(KEY_LATI_TENANT + "_" + i, "0.0")
        return array
    }

    fun saveArrayBookCode(array: Array<String?>): Boolean {
        editor.putInt(KEY_BOOKING_CODE + "_size", array.size)
        for (i in array.indices) editor.putString(KEY_BOOKING_CODE + "_" + i, array[i])
        return editor.commit()
    }

    fun loadArrayBookCode(): Array<String?> {
        val size = pref.getInt(KEY_BOOKING_CODE + "_size", 0)
        val array = arrayOfNulls<String>(size)
        for (i in 0 until size) array[i] = pref.getString(KEY_BOOKING_CODE + "_" + i, "")
        return array
    }

    var suratKe: Int
        get() = pref.getInt(KEY_SURAT_KE, -1)
        set(ke) {
            editor.putInt(KEY_SURAT_KE, ke).apply()
        }

    init {
        val PREF_NAME = "data_account"
        val PRIVATE_MODE = 0
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }
}