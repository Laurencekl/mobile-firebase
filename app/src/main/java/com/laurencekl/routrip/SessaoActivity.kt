package com.laurencekl.routrip

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

// Telas que exigem login herdam esta verificação.
open class SessaoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verificarSessao()
    }

    override fun onStart() {
        super.onStart()
        if (!isFinishing) verificarSessao()
    }

    private fun verificarSessao() {
        val firebase = FirebaseApp.initializeApp(this)
        if (firebase == null || FirebaseAuth.getInstance().currentUser == null) {
            abrirLogin()
        }
    }

    protected fun abrirLogin() {
        val tela = Intent(this, LoginActivity::class.java)
        // Limpa o histórico para o botão Voltar não reabrir a área autenticada.
        tela.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(tela)
        finish()
    }
}
