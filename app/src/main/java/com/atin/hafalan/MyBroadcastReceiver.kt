package com.atin.hafalan

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class MyBroadcastReceiver: BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        val extra = intent?.getStringExtra("My Favorite Color")

        //Toast.makeText(context,intent?.action,Toast.LENGTH_SHORT).show()
        Toast.makeText(context,extra,Toast.LENGTH_SHORT).show()
        if (context != null) {
            if (intent != null) {
                context.sendBroadcast(
                    Intent("TRACKS_TRACKS")
                        .putExtra("actionname", intent.action)
                )
            }
        }
    }
}