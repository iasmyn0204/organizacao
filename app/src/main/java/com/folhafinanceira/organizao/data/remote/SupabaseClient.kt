package com.folhafinanceira.organizao.data.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {
    /**
     * URL do projeto Supabase. Exemplo: "https://xyzcompany.supabase.co"
     */
    const val SUPABASE_URL = "https://SUA_URL_DO_SUPABASE.supabase.co"

    /**
     * Chave anônima (anon key / public API key) do projeto Supabase.
     */
    const val SUPABASE_ANON_KEY = "SUA_CHAVE_ANON_DO_SUPABASE"

    val client by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_ANON_KEY
        ) {
            install(Auth)
            install(Postgrest)
        }
    }
}
