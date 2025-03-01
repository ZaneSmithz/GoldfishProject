package com.project.goldfish.model

import com.project.goldfish.data.UserDto
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern


data class MomentData(
    val title: String,
    val description: String,
    val formattedTime: LocalDateTime,
    val user: UserDto,
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
