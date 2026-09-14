package com.laurencekl.routrip

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private var auth: FirebaseAuth? = null
    private lateinit var email: EditText
    private lateinit var senha: EditText
    private lateinit var entrar: Button
    private lateinit var progresso: ProgressBar
    private lateinit var mensagem: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        email = findViewById(R.id.campoEmail)
        senha = findViewById(R.id.campoSenha)
        entrar = findViewById(R.id.botaoEntrar)
        progresso = findViewById(R.id.progressoAcesso)
        mensagem = findViewById(R.id.mensagemAcesso)

        if (FirebaseApp.initializeApp(this) != null) {
            auth = FirebaseAuth.getInstance()
        } else {
            mensagem.setText(R.string.acesso_indisponivel)
            entrar.isEnabled = false
        }

        entrar.setOnClickListener { fazerLogin() }
    }

    override fun onStart() {
        super.onStart()
        if (auth?.currentUser != null) abrirInicio()
    }

    private fun fazerLogin() {
        val autenticacao = auth ?: return
        val emailDigitado = email.text.toString().trim()
        val senhaDigitada = senha.text.toString()
        mensagem.text = ""

        if (!Patterns.EMAIL_ADDRESS.matcher(emailDigitado).matches()) {
            email.error = getString(R.string.email_invalido)
            email.requestFocus()
            return
        }
        if (senhaDigitada.isEmpty()) {
            senha.error = getString(R.string.senha_obrigatoria)
            senha.requestFocus()
            return
        }

        carregando(true)
        autenticacao.signInWithEmailAndPassword(emailDigitado, senhaDigitada)
            .addOnCompleteListener(this) { tarefa ->
                carregando(false)
                if (tarefa.isSuccessful) {
                    senha.text.clear()
                    abrirInicio()
                } else {
                    val texto = when (tarefa.exception) {
                        is FirebaseNetworkException -> R.string.erro_conexao
                        is FirebaseTooManyRequestsException -> R.string.muitas_tentativas
                        else -> R.string.login_invalido
                    }
                    mensagem.setText(texto)
                }
            }
    }

    private fun carregando(aguardando: Boolean) {
        entrar.isEnabled = !aguardando
        email.isEnabled = !aguardando
        senha.isEnabled = !aguardando
        progresso.visibility = if (aguardando) View.VISIBLE else View.GONE
    }

    private fun abrirInicio() {
        val tela = Intent(this, MainActivity::class.java)
        tela.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(tela)
        finish()
    }
}
