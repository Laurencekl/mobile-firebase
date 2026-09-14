package com.laurencekl.routrip

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : SessaoActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isFinishing) return
        setContentView(R.layout.activity_main)

        val auth = FirebaseAuth.getInstance()
        findViewById<TextView>(R.id.textoUsuario).text = auth.currentUser?.email.orEmpty()
        findViewById<Button>(R.id.botaoSair).setOnClickListener {
            auth.signOut()
            abrirLogin()
        }
    }
}
