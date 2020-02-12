package ru.netology.backend.model.dto

import org.hibernate.validator.constraints.URL
import ru.netology.backend.config.YouTubeIDPatternString
import ru.netology.backend.model.Location
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Pattern

data class PostRqDto(
    @field:NotEmpty
    val createdUser: String = "",

    @field:NotEmpty
    val content: String = "",

    val address: String = "",
    val location: Location? = null,

    @field:Pattern(regexp = YouTubeIDPatternString)
    val youtubeId: String? = null,

    @field:URL
    val commercialContent: String? = null
)
