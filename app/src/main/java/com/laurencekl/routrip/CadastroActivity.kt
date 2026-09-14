package com.laurencekl.routrip

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class CadastroActivity : AppCompatActivity() {
    private var auth: FirebaseAuth? = null
    private lateinit var email: EditText
    private lateinit var senha: EditText
    private lateinit var confirmarSenha: EditText
    private lateinit var cadastrar: Button
    private lateinit var voltar: Button
    private lateinit var progresso: ProgressBar
    private lateinit var mensagem: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)
        email = findViewById(R.id.campoEmail)
        senha = findViewById(R.id.campoSenha)
        confirmarSenha = findViewById(R.id.campoConfirmarSenha)
        cadastrar = findViewById(R.id.botaoCadastrar)
        voltar = findViewById(R.id.botaoVoltarLogin)
        progresso = findViewById(R.id.progressoAcesso)
        mensagem = findViewById(R.id.mensagemAcesso)

        if (FirebaseApp.initializeApp(this) != null) {
            auth = FirebaseAuth.getInstance()
        } else {
            mensagem.setText(R.string.acesso_indisponivel)
            cadastrar.isEnabled = false
        }

        cadastrar.setOnClickListener { criarConta() }
        voltar.setOnClickListener { finish() }
    }

    override fun onStart() {
        super.onStart()
        if (auth?.currentUser != null) abrirInicio()
    }

    private fun criarConta() {
        val autenticacao = auth ?: return
        val emailDigitado = email.text.toString().trim()
        val senhaDigitada = senha.text.toString()
        mensagem.text = ""

        if (!Patterns.EMAIL_ADDRESS.matcher(emailDigitado).matches()) {
            email.error = getString(R.string.email_invalido)
            email.requestFocus()
            return
        }
        if (senhaDigitada.length < 6) {
            senha.error = getString(R.string.senha_curta)
            senha.requestFocus()
            return
        }
        if (senhaDigitada != confirmarSenha.text.toString()) {
            confirmarSenha.error = getString(R.string.senhas_diferentes)
            confirmarSenha.requestFocus()
            return
        }

        carregando(true)
        // O Firebase cria a conta e já autentica o novo usuário.
        autenticacao.createUserWithEmailAndPassword(emailDigitado, senhaDigitada)
            .addOnCompleteListener(this) { tarefa ->
                carregando(false)
                if (tarefa.isSuccessful) {
                    senha.text.clear()
                    confirmarSenha.text.clear()
                    Toast.makeText(this, R.string.conta_criada, Toast.LENGTH_SHORT).show()
                    abrirInicio()
                } else {
                    val texto = when (tarefa.exception) {
                        is FirebaseNetworkException -> R.string.erro_conexao
                        is FirebaseTooManyRequestsException -> R.string.muitas_tentativas
                        is FirebaseAuthWeakPasswordException -> R.string.senha_fraca
                        else -> R.string.erro_cadastro
                    }
                    mensagem.setText(texto)
                }
            }
    }

    private fun carregando(aguardando: Boolean) {
        cadastrar.isEnabled = !aguardando
        voltar.isEnabled = !aguardando
        email.isEnabled = !aguardando
        senha.isEnabled = !aguardando
        confirmarSenha.isEnabled = !aguardando
        progresso.visibility = if (aguardando) View.VISIBLE else View.GONE
    }

    private fun abrirInicio() {
        val tela = Intent(this, MainActivity::class.java)
        tela.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(tela)
        finish()
    }
}
