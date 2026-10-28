package com.nexauren.paperya.ui

fun uiText(isPortuguese: Boolean, portuguese: String, english: String): String =
    if (isPortuguese) portuguese else english
