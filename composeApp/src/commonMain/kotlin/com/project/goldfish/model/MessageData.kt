package com.project.goldfish.model

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern

data class MessageData(
    val text: String,
    val formattedTime: LocalDateTime,
    val userId: String,
) {
    companion object {
        @OptIn(FormatStringsInDatetimeFormats::class)
        fun LocalDateTime.formattedTime(): String {
            return this.format(
                LocalDateTime.Format {
                    byUnicodePattern("HH:mm")
                }
            )
        }

        @OptIn(FormatStringsInDatetimeFormats::class)
        fun LocalDateTime.formattedDate(): String {
            return this.format(
                LocalDateTime.Format {
                    byUnicodePattern("EEE, d MMM")
                }
            )
        }
    }
}
